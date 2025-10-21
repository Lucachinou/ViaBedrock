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
package net.raphimc.viabedrock.api.brigadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class ValueArgumentType implements ArgumentType<Object> {

    private static final SimpleCommandExceptionType INVALID_VALUE_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Invalid value"));

    public static ValueArgumentType value() {
        return new ValueArgumentType();
    }

    @Override
    public Object parse(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw INVALID_VALUE_EXCEPTION.createWithContext(reader);
        }

        if (reader.peek() == '~') {
            reader.skip();
        }
        final float f = reader.canRead() && reader.peek() != ' ' ? reader.readFloat() : 0F;
        if (Float.isNaN(f) || Float.isInfinite(f)) {
            throw INVALID_VALUE_EXCEPTION.createWithContext(reader);
        }

        return null;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        final StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());

        if (!reader.canRead()) {
            builder.suggest("~");
        }

        return builder.buildFuture();
    }

}
