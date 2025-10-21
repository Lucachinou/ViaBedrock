/*
 * This file is part of ViaBedrock - https://github.com/RaphiMC/ViaBedrock
 * Copyright (C) 2023-2025 RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.raphimc.viabedrock.api.http;

import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.raphimc.viabedrock.ViaBedrock;
import net.raphimc.viabedrock.api.model.resourcepack.ResourcePack;
import net.raphimc.viabedrock.protocol.rewriter.ResourcePackRewriter;
import net.raphimc.viabedrock.protocol.storage.ResourcePacksStorage;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class ResourcePackHttpServer {

    private final InetSocketAddress bindAddress;
    private final ChannelFuture channelFuture;
    private final Map<UUID, UserConnection> connections = new HashMap<>();

    public ResourcePackHttpServer(final InetSocketAddress bindAddress) {
        this.bindAddress = bindAddress;
        this.channelFuture = new ServerBootstrap()
                .group(new NioEventLoopGroup(0))
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        channel.pipeline().addLast("http_codec", new HttpServerCodec());
                        channel.pipeline().addLast("chunked_writer", new ChunkedWriteHandler());
                        channel.pipeline().addLast("http_handler", new SimpleChannelInboundHandler<>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
                                if (msg instanceof HttpRequest request) {
                                    if (!request.method().equals(HttpMethod.GET)) {
                                        ctx.close();
                                        return;
                                    }

                                    final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
                                    if (!queryStringDecoder.parameters().containsKey("token")) {
                                        ctx.close();
                                        return;
                                    }
                                    final UUID uuid = UUID.fromString(queryStringDecoder.parameters().get("token").get(0));
                                    final UserConnection user = ResourcePackHttpServer.this.connections.get(uuid);
                                    if (user == null) {
                                        ctx.close();
                                        return;
                                    }

                                    final ResourcePacksStorage resourcePacksStorage = user.get(ResourcePacksStorage.class);
                                    while (!resourcePacksStorage.hasFinishedLoading()) {
                                        Thread.sleep(100);
                                    }

                                    try {
                                        final long start = System.currentTimeMillis();
                                        final ResourcePack.Content javaContent = ResourcePackRewriter.bedrockToJava(resourcePacksStorage);
                                        final byte[] data = javaContent.toZip();
                                        ViaBedrock.getPlatform().getLogger().log(Level.INFO, "Converted packs in " + (System.currentTimeMillis() - start) + "ms");

                                        final DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                                        response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
                                        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
                                        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, data.length);
                                        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                                        ctx.write(response);
                                        ctx.writeAndFlush(new HttpChunkedInput(new ChunkedStream(new ByteArrayInputStream(data), 65535))).addListener(ChannelFutureListener.CLOSE);
                                    } catch (Throwable e) {
                                        ViaBedrock.getPlatform().getLogger().log(Level.SEVERE, "Failed to convert resource packs", e);
                                        ctx.close();
                                    }
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                ctx.close();
                            }
                        });
                    }
                })
                .bind(bindAddress)
                .syncUninterruptibly();
    }

    public void addConnection(final UUID uuid, final UserConnection connection) {
        synchronized (this.connections) {
            this.connections.put(uuid, connection);
        }

        connection.getChannel().closeFuture().addListener(future -> {
            synchronized (this.connections) {
                this.connections.remove(uuid);
            }
        });
    }

    public void stop() {
        if (this.channelFuture != null) {
            this.channelFuture.channel().close();
        }
    }

    public String getUrl() {
        final String overrideUrl = ViaBedrock.getConfig().getResourcePackUrl();
        if (!overrideUrl.isEmpty()) {
            return overrideUrl;
        } else {
            final InetSocketAddress bindAddress = (InetSocketAddress) this.channelFuture.channel().localAddress();
            return "http://" + this.bindAddress.getHostString() + ":" + bindAddress.getPort() + "/";
        }
    }

    public Channel getChannel() {
        return this.channelFuture.channel();
    }

}
