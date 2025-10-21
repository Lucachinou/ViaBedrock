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
package net.raphimc.viabedrock.protocol.rewriter.blockentity;

import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.nbt.tag.IntTag;
import com.viaversion.nbt.tag.ListTag;
import com.viaversion.nbt.tag.StringTag;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntityImpl;
import net.raphimc.viabedrock.ViaBedrock;
import net.raphimc.viabedrock.api.chunk.BedrockBlockEntity;
import net.raphimc.viabedrock.protocol.BedrockProtocol;
import net.raphimc.viabedrock.protocol.data.BedrockMappingData;
import net.raphimc.viabedrock.protocol.rewriter.BlockEntityRewriter;

import java.util.Collections;
import java.util.logging.Level;

public class DecoratedPotBlockEntityRewriter implements BlockEntityRewriter.Rewriter {

    @Override
    public BlockEntity toJava(UserConnection user, BedrockBlockEntity bedrockBlockEntity) {
        final CompoundTag bedrockTag = bedrockBlockEntity.tag();
        final CompoundTag javaTag = new CompoundTag();

        final ListTag<StringTag> bedrockSherds = bedrockTag.getListTag("sherds", StringTag.class);
        if (bedrockSherds != null) {
            final ListTag<StringTag> javaSherds = new ListTag<>(StringTag.class);
            for (StringTag bedrockSherd : bedrockSherds) {
                final String bedrockIdentifier = bedrockSherd.getValue();
                final BedrockMappingData.JavaItemMapping itemMapping = BedrockProtocol.MAPPINGS.getBedrockToJavaMetaItems().getOrDefault(bedrockIdentifier, Collections.emptyMap()).getOrDefault(null, null);
                if (itemMapping != null) {
                    javaSherds.add(new StringTag(itemMapping.identifier()));
                } else if (bedrockIdentifier.isEmpty()) {
                    javaSherds.add(new StringTag("minecraft:brick"));
                } else {
                    ViaBedrock.getPlatform().getLogger().log(Level.WARNING, "Missing item: " + bedrockIdentifier);
                    javaSherds.add(new StringTag("minecraft:brick"));
                }
            }
            javaTag.put("sherds", javaSherds);
        }
        this.copyItem(user, bedrockTag, javaTag, "item");
        this.copy(bedrockTag, javaTag, "LootTable", StringTag.class);
        this.copy(bedrockTag, javaTag, "LootTableSeed", IntTag.class);

        return new BlockEntityImpl(bedrockBlockEntity.packedXZ(), bedrockBlockEntity.y(), -1, javaTag);
    }

}
