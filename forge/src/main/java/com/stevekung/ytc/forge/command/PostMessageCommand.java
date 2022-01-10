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

package com.stevekung.ytc.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.stevekung.ytc.forge.command.clientcommands.ClientCommands;
import com.stevekung.ytc.forge.command.clientcommands.IClientCommand;
import com.stevekung.ytc.forge.command.clientcommands.IClientSharedSuggestionProvider;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.ChatUtils;
import com.stevekung.ytc.utils.PlatformConfig;
import com.stevekung.ytc.utils.YouTubeCommandRuntimeException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

public class PostMessageCommand implements IClientCommand
{
    @Override
    public void register(CommandDispatcher<IClientSharedSuggestionProvider> dispatcher)
    {
        dispatcher.register(ClientCommands.literal("yt")
                .then(ClientCommands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> postMessage(StringArgumentType.getString(context, "message")))));
    }

    private static int postMessage(String message)
    {
        var clientSecret = PlatformConfig.clientSecret();
        var service = YouTubeChatService.getService();

        if (clientSecret.isEmpty())
        {
            throw new YouTubeCommandRuntimeException(new TranslatableComponent("commands.yt.no_client_secret"));
        }
        if (!service.hasExecutor())
        {
            throw new YouTubeCommandRuntimeException(new TranslatableComponent("commands.yt.service_not_start"));
        }
        service.postMessage(message, id -> ChatUtils.printOverlayMessage(new TranslatableComponent("commands.yt.message_posted").withStyle(ChatFormatting.GREEN)));
        return 1;
    }
}