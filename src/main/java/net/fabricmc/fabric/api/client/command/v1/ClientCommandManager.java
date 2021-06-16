/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package net.fabricmc.fabric.api.client.command.v1;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

/**
 * This class is from <a target="_blank" href="https://github.com/FabricMC/fabric">Fabric-api</a>
 * under the <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0 license</a>
 * modified slightly to work on forge by removing net.fabricmc.api.Environment
 */
public final class ClientCommandManager {
    /**
     * The command dispatcher that handles client command registration and execution.
     */
    public static final CommandDispatcher<FabricClientCommandSource> DISPATCHER = new CommandDispatcher<>();

    private ClientCommandManager() {
    }

    /**
     * Creates a literal argument builder.
     *
     * @param name the literal name
     * @return the created argument builder
     */
    public static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a required argument builder.
     *
     * @param name the name of the argument
     * @param type the type of the argument
     * @param <T>  the type of the parsed argument value
     * @return the created argument builder
     */
    public static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}