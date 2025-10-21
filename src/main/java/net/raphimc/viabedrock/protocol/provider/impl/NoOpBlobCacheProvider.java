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
package net.raphimc.viabedrock.protocol.provider.impl;

import net.raphimc.viabedrock.protocol.provider.BlobCacheProvider;

public class NoOpBlobCacheProvider extends BlobCacheProvider {

    @Override
    public void addBlob(final long hash, final byte[] blob) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasBlob(final long hash) {
        return false;
    }

    @Override
    public byte[] getBlob(final long hash) {
        throw new UnsupportedOperationException();
    }

}
