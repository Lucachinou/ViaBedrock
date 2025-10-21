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
package net.raphimc.viabedrock.protocol.types.model;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import net.raphimc.viabedrock.protocol.model.SkinData;
import net.raphimc.viabedrock.protocol.types.BedrockTypes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SkinType extends Type<SkinData> {

    public SkinType() {
        super(SkinData.class);
    }

    @Override
    public SkinData read(ByteBuf buffer) {
        final String skinId = BedrockTypes.STRING.read(buffer);
        final String playFabId = BedrockTypes.STRING.read(buffer);
        final String skinResourcePatch = BedrockTypes.STRING.read(buffer);
        final BufferedImage skinData = BedrockTypes.IMAGE.read(buffer);

        final int animationCount = buffer.readIntLE();
        final List<SkinData.AnimationData> animations = new ArrayList<>(animationCount);
        for (int i = 0; i < animationCount; i++) {
            final BufferedImage image = BedrockTypes.IMAGE.read(buffer);
            final int type = buffer.readIntLE();
            final float frames = buffer.readFloatLE();
            final int expression = buffer.readIntLE();
            animations.add(new SkinData.AnimationData(image, type, frames, expression));
        }

        final BufferedImage capeData = BedrockTypes.IMAGE.read(buffer);
        final String geometryData = BedrockTypes.STRING.read(buffer);
        final String geometryDataEngineVersion = BedrockTypes.STRING.read(buffer);
        final String animationData = BedrockTypes.STRING.read(buffer);
        final String capeId = BedrockTypes.STRING.read(buffer);
        final String fullSkinId = BedrockTypes.STRING.read(buffer);
        final String armSize = BedrockTypes.STRING.read(buffer);
        final String skinColor = BedrockTypes.STRING.read(buffer);

        final int piecesLength = buffer.readIntLE();
        final List<SkinData.PersonaPieceData> personaPieces = new ArrayList<>(piecesLength);
        for (int i = 0; i < piecesLength; i++) {
            final String id = BedrockTypes.STRING.read(buffer);
            final String type = BedrockTypes.STRING.read(buffer);
            final String packId = BedrockTypes.STRING.read(buffer);
            final boolean defaultPiece = buffer.readBoolean();
            final String productId = BedrockTypes.STRING.read(buffer);
            personaPieces.add(new SkinData.PersonaPieceData(id, type, packId, defaultPiece, productId));
        }

        final int tintsLength = buffer.readIntLE();
        final List<SkinData.PersonaPieceTintData> tintColors = new ArrayList<>(tintsLength);
        for (int i = 0; i < tintsLength; i++) {
            final String type = BedrockTypes.STRING.read(buffer);
            final List<String> colors = new ArrayList<>();
            final int colorsLength = buffer.readIntLE();
            for (int i2 = 0; i2 < colorsLength; i2++) {
                colors.add(BedrockTypes.STRING.read(buffer));
            }
            tintColors.add(new SkinData.PersonaPieceTintData(type, colors));
        }

        final boolean premium = buffer.readBoolean();
        final boolean persona = buffer.readBoolean();
        final boolean capeOnClassic = buffer.readBoolean();
        final boolean primaryUser = buffer.readBoolean();
        final boolean overridingPlayerAppearance = buffer.readBoolean();

        return new SkinData(skinId, playFabId, skinResourcePatch, skinData, animations, capeData, geometryData, geometryDataEngineVersion, animationData, premium, persona, capeOnClassic, primaryUser, capeId, fullSkinId, armSize, skinColor, personaPieces, tintColors, overridingPlayerAppearance);
    }

    @Override
    public void write(ByteBuf buffer, SkinData value) {
        BedrockTypes.STRING.write(buffer, value.skinId());
        BedrockTypes.STRING.write(buffer, value.playFabId());
        BedrockTypes.STRING.write(buffer, value.skinResourcePatch());
        BedrockTypes.IMAGE.write(buffer, value.skinData());

        buffer.writeIntLE(value.animations().size());
        for (SkinData.AnimationData animation : value.animations()) {
            BedrockTypes.IMAGE.write(buffer, animation.image());
            buffer.writeIntLE(animation.type());
            buffer.writeFloatLE(animation.frames());
            buffer.writeIntLE(animation.expression());
        }

        BedrockTypes.IMAGE.write(buffer, value.capeData());
        BedrockTypes.STRING.write(buffer, value.geometryData());
        BedrockTypes.STRING.write(buffer, value.geometryDataEngineVersion());
        BedrockTypes.STRING.write(buffer, value.animationData());
        BedrockTypes.STRING.write(buffer, value.capeId());
        BedrockTypes.STRING.write(buffer, value.fullSkinId());
        BedrockTypes.STRING.write(buffer, value.armSize());
        BedrockTypes.STRING.write(buffer, value.skinColor());

        buffer.writeIntLE(value.personaPieces().size());
        for (SkinData.PersonaPieceData piece : value.personaPieces()) {
            BedrockTypes.STRING.write(buffer, piece.id());
            BedrockTypes.STRING.write(buffer, piece.type());
            BedrockTypes.STRING.write(buffer, piece.packId());
            buffer.writeBoolean(piece.defaultPiece());
            BedrockTypes.STRING.write(buffer, piece.productId());
        }

        buffer.writeIntLE(value.tintColors().size());
        for (SkinData.PersonaPieceTintData tint : value.tintColors()) {
            BedrockTypes.STRING.write(buffer, tint.type());
            buffer.writeIntLE(tint.colors().size());
            for (String color : tint.colors()) {
                BedrockTypes.STRING.write(buffer, color);
            }
        }

        buffer.writeBoolean(value.premium());
        buffer.writeBoolean(value.persona());
        buffer.writeBoolean(value.capeOnClassic());
        buffer.writeBoolean(value.primaryUser());
        buffer.writeBoolean(value.overridingPlayerAppearance());
    }

}
