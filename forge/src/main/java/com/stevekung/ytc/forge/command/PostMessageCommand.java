/*
 * Copyright 2017-2021 Google Inc.
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

package com.stevekung.ytc.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.stevekung.stevekunglib.forge.utils.client.command.ClientCommands;
import com.stevekung.stevekunglib.forge.utils.client.command.IClientCommand;
import com.stevekung.stevekunglib.forge.utils.client.command.IClientSharedSuggestionProvider;
import com.stevekung.stevekunglib.utils.TextComponentUtils;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.ChatUtils;
import com.stevekung.ytc.utils.PlatformConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;

public class PostMessageCommand implements IClientCommand
{
    @Override
    public void register(CommandDispatcher<IClientSharedSuggestionProvider> dispatcher)
    {
        dispatcher.register(ClientCommands.literal("yt")
                .then(ClientCommands.argument("message", StringArgumentType.greedyString())
                        .executes(requirement -> postMessage(StringArgumentType.getString(requirement, "message")))));
    }

    private static int postMessage(String message)
    {
        var clientSecret = PlatformConfig.getClientSecret();
        var service = YouTubeChatService.getService();

        if (clientSecret.isEmpty())
        {
            throw new CommandRuntimeException(TextComponentUtils.component("[YouTubeChat] No client secret configurated"));
        }
        if (!service.hasExecutor())
        {
            throw new CommandRuntimeException(TextComponentUtils.component("[YouTubeChat] Service is not started"));
        }
        service.postMessage(message, i -> ChatUtils.printYTOverlayMessage(TextComponentUtils.formatted("Message posted!", ChatFormatting.GREEN)));
        return 1;
    }
}