/*
 * Copyright 2017-2022 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stevekung.ytc.forge.command.clientcommands;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@SuppressWarnings("unchecked")
public class ClientCommands
{
    private static final CommandDispatcher<IClientSharedSuggestionProvider> DISPATCHER = new CommandDispatcher<>();
    private static final List<IClientCommand> CLIENT_COMMANDS = Lists.newArrayList();

    public static void register(IClientCommand command)
    {
        ClientCommands.CLIENT_COMMANDS.add(command);
    }

    private static List<IClientCommand> getCommands()
    {
        return ClientCommands.CLIENT_COMMANDS;
    }

    public static LiteralArgumentBuilder<IClientSharedSuggestionProvider> literal(String name)
    {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<IClientSharedSuggestionProvider, T> argument(String name, ArgumentType<T> type)
    {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static void buildDispatcher()
    {
        ClientCommands.getCommands().forEach(command -> command.register(DISPATCHER));
    }

    @SuppressWarnings("rawtypes")
    public static void buildSuggestion(CommandDispatcher dispatcher)
    {
        ClientCommands.getCommands().forEach(command -> command.register(dispatcher));
    }

    public static int execute(String input, IClientSharedSuggestionProvider provider) throws CommandSyntaxException
    {
        return DISPATCHER.execute(input, provider);
    }

    public static boolean hasCommand(String name)
    {
        return DISPATCHER.findNode(Collections.singleton(name)) != null;
    }
}