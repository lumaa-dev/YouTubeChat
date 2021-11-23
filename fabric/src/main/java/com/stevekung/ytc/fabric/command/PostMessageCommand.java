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

package com.stevekung.ytc.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.stevekung.stevekunglib.utils.TextComponentUtils;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.ChatUtils;
import com.stevekung.ytc.utils.PlatformConfig;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;

public class PostMessageCommand
{
    public PostMessageCommand(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        dispatcher.register(ClientCommandManager.literal("yt")
                .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                        .executes(requirement -> postMessage(StringArgumentType.getString(requirement, "message")))));
    }

    private static int postMessage(String message)
    {
        String clientSecret = PlatformConfig.getClientSecret();
        YouTubeChatService service = YouTubeChatService.getService();

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