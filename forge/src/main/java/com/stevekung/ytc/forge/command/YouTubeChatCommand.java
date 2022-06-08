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
import com.stevekung.ytc.command.arguments.AuthProfileArgumentType;
import com.stevekung.ytc.core.YouTubeChat;
import com.stevekung.ytc.service.AuthService;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.ChatUtils;
import com.stevekung.ytc.utils.YouTubeCommandRuntimeException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

/**
 * An in-game command for managing the YouTube Chat service
 */
public class YouTubeChatCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("ytc")
                .then(Commands.literal("start").executes(context -> startService(null))
                        .then(Commands.argument("profile", AuthProfileArgumentType.create()).executes(context -> startService(AuthProfileArgumentType.getProfile(context, "profile")))))
                .then(Commands.literal("stop").executes(context -> stopService()))
                .then(Commands.literal("list").executes(context -> listProfile()))
                .then(Commands.literal("logout").executes(context -> logout(null))
                        .then(Commands.argument("profile", AuthProfileArgumentType.create()).executes(context -> logout(AuthProfileArgumentType.getProfile(context, "profile"))))));
    }

    private static int startService(String profile)
    {
        var clientSecret = YouTubeChat.CONFIG.general.clientSecret;
        var service = YouTubeChatService.getService();

        if (clientSecret.isEmpty())
        {
            throw new YouTubeCommandRuntimeException(Component.translatable("commands.yt.no_client_secret"));
        }
        if (service.hasExecutor())
        {
            throw new YouTubeCommandRuntimeException(Component.translatable("commands.yt.service_already_start"));
        }

        service.start(clientSecret, profile);
        YouTubeChatService.receiveChat = true;
        service.subscribe();
        return 1;
    }

    private static int stopService()
    {
        var service = YouTubeChatService.getService();

        if (!service.hasExecutor())
        {
            throw new YouTubeCommandRuntimeException(Component.translatable("commands.yt.service_not_start"));
        }

        service.stop(false);
        YouTubeChatService.receiveChat = false;
        service.unsubscribe();
        return 1;
    }

    private static int listProfile()
    {
        if (!AuthService.USER_DIR.exists())
        {
            ChatUtils.printExceptionMessage("Folder doesn't exist!");
            return 1;
        }

        ChatUtils.printChatMessage(Component.translatable("commands.yt.list_login_profiles"));

        if (AuthService.USER_DIR.listFiles().length < 1)
        {
            ChatUtils.printMessage(Component.literal("- ").withStyle(ChatFormatting.RED).append(Component.translatable("commands.yt.empty_login_profiles")));
        }
        for (var file : AuthService.USER_DIR.listFiles())
        {
            ChatUtils.printMessage(Component.literal("- ").append(file.getName()));
        }
        return 1;
    }

    private static int logout(String profile)
    {
        var service = YouTubeChatService.getService();

        if (service.hasExecutor())
        {
            service.unsubscribe();
            service.stop(true);
        }
        else
        {
            throw new YouTubeCommandRuntimeException(Component.translatable("commands.yt.service_not_start"));
        }

        if (StringUtil.isNullOrEmpty(profile))
        {
            AuthService.clearCurrentCredential();
        }
        else
        {
            AuthService.clearCredential(profile);
        }
        return 1;
    }
}