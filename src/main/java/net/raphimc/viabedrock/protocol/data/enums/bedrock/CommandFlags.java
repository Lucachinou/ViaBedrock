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
package net.raphimc.viabedrock.protocol.data.enums.bedrock;

public class CommandFlags {

    public static final int TEST_USAGE = 1 << 0;
    public static final int HIDDEN_FROM_COMMAND_BLOCK = 1 << 1;
    public static final int HIDDEN_FROM_PLAYER = 1 << 2;
    public static final int HIDDEN_FROM_AUTOMATION = 1 << 3;
    public static final int LOCAL_SYNC = 1 << 4;
    public static final int EXECUTE_DISALLOWED = 1 << 5;
    public static final int MESSAGE_TYPE = 1 << 6;
    public static final int NOT_CHEAT = 1 << 7;
    public static final int ASYNC = 1 << 8;

}
