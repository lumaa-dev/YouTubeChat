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

package com.stevekung.ytc.command;

import java.io.File;
import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import com.stevekung.ytc.command.arguments.ProfileNameArgumentType;
import com.stevekung.ytc.service.AuthService;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.ChatUtils;
import com.stevekung.ytc.utils.PlatformConfig;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.util.StringUtil;

/**
 * An in-game command for managing the YouTube Chat service
 */
public class YouTubeChatCommand
{
    public YouTubeChatCommand(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        dispatcher.register(ClientCommandManager.literal("ytc")
                .then(ClientCommandManager.literal("start").executes(requirement -> startService(null))
                        .then(ClientCommandManager.argument("profile", com.stevekung.ytc.command.arguments.ProfileNameArgumentType.create()).executes(requirement -> startService(com.stevekung.ytc.command.arguments.ProfileNameArgumentType.getProfile(requirement, "profile")))))
                .then(ClientCommandManager.literal("stop").executes(requirement -> stopService()))
                .then(ClientCommandManager.literal("list").executes(requirement -> listProfile()))
                .then(ClientCommandManager.literal("logout").then(ClientCommandManager.argument("profile", com.stevekung.ytc.command.arguments.ProfileNameArgumentType.create()))
                        .executes(requirement -> logout(ProfileNameArgumentType.getProfile(requirement, "profile")))));
    }

    private static int startService(String profile)
    {
        String clientSecret = PlatformConfig.getClientSecret();
        YouTubeChatService service = YouTubeChatService.getService();

        if (clientSecret.isEmpty())
        {
            throw new CommandRuntimeException(TextComponentUtils.component("[YouTubeChat] No client secret configurated"));
        }
        if (service.hasExecutor())
        {
            throw new CommandRuntimeException(TextComponentUtils.component("[YouTubeChat] Service is already started"));
        }

        service.start(clientSecret, StringUtil.isNullOrEmpty(profile) ? null : profile);
        YouTubeChatService.receiveChat = true;
        service.subscribe();
        return 1;
    }

    private static int stopService()
    {
        YouTubeChatService service = YouTubeChatService.getService();

        if (!service.hasExecutor())
        {
            throw new CommandRuntimeException(TextComponentUtils.component("[YouTubeChat] Service is not started"));
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

        ChatUtils.printYTMessage(TextComponentUtils.component("Current login profiles list"));

        if (AuthService.USER_DIR.listFiles().length < 1)
        {
            ClientUtils.printClientMessage(TextComponentUtils.formatted("- Empty login profiles!", ChatFormatting.RED));
        }
        for (File file : AuthService.USER_DIR.listFiles())
        {
            ClientUtils.printClientMessage(TextComponentUtils.component("- ").append(TextComponentUtils.formatted(file.getName(), ChatFormatting.GOLD)));
        }
        return 1;
    }

    private static int logout(String profile)
    {
        YouTubeChatService service = YouTubeChatService.getService();

        if (service.hasExecutor())
        {
            service.unsubscribe();
            service.stop(true);
        }

        try
        {
            if (profile.isEmpty())
            {
                AuthService.clearCurrentCredentials();
            }
            else
            {
                AuthService.clearCredentials(profile);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            ChatUtils.printExceptionMessage(e.getMessage());
        }
        return 1;
    }
}