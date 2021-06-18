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

package com.stevekung.mods.ytc.command;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.stevekung.mods.ytc.config.ConfigManagerYT;
import com.stevekung.mods.ytc.core.EventHandlerYT;
import com.stevekung.mods.ytc.service.AuthService;
import com.stevekung.mods.ytc.service.YouTubeChatService;
import com.stevekung.mods.ytc.utils.LoggerYT;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import stevekung.mods.stevekunglib.utils.JsonUtils;
import stevekung.mods.stevekunglib.utils.client.ClientCommandBase;

/**
 * An in-game command for managing the YouTube Chat service. Usage:
 *
 * /ytc <start|stop|logout|list>
 */
public class CommandYouTubeChat extends ClientCommandBase
{
    @Override
    public String getName()
    {
        return "ytc";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return this.getUsage();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String clientSecret = ConfigManagerYT.YOUTUBE_CHAT_GENERAL.clientSecret;
        YouTubeChatService service = YouTubeChatService.getService();

        if (clientSecret.isEmpty())
        {
            throw new CommandException("[YouTubeChat] No client secret configurated");
        }
        if (args.length == 0)
        {
            throw new WrongUsageException(this.getUsage());
        }
        if (args[0].equalsIgnoreCase("start"))
        {
            if (service.hasExecutor())
            {
                throw new CommandException("[YouTubeChat] Service is already started");
            }

            ITextComponent component = ClientCommandBase.getChatComponentFromNthArg(args, 1);
            String profile = component.createCopy().getUnformattedText();
            service.start(clientSecret, profile.isEmpty() ? null : profile);
            EventHandlerYT.chatReceived = true;
            service.subscribe();
        }
        else if (args[0].equalsIgnoreCase("stop"))
        {
            if (!service.hasExecutor())
            {
                throw new CommandException("[YouTubeChat] Service is not started");
            }

            service.stop(false);
            EventHandlerYT.chatReceived = false;
            service.unsubscribe();
        }
        else if (args[0].equalsIgnoreCase("list"))
        {
            if (!AuthService.USER_DIR.exists())
            {
                LoggerYT.printExceptionMessage("Folder doesn't exist!");
                return;
            }

            LoggerYT.printYTMessage(JsonUtils.create("Current login profiles list").setStyle(JsonUtils.white()));

            if (AuthService.USER_DIR.listFiles().length < 1)
            {
                sender.sendMessage(JsonUtils.create("- Empty login profiles!").setStyle(JsonUtils.red()));
            }
            for (File file : AuthService.USER_DIR.listFiles())
            {
                sender.sendMessage(JsonUtils.create("- ").appendSibling(JsonUtils.create(file.getName()).setStyle(JsonUtils.gold())));
            }
        }
        else if (args[0].equalsIgnoreCase("logout"))
        {
            if (service.hasExecutor())
            {
                service.unsubscribe();
                service.stop(true);
            }

            ITextComponent component = ClientCommandBase.getChatComponentFromNthArg(args, 1);
            String profile = component.createCopy().getUnformattedText();

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
                LoggerYT.printExceptionMessage(e.getMessage());
            }
        }
        else
        {
            throw new WrongUsageException(this.getUsage());
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return CommandBase.getListOfStringsMatchingLastWord(args, "start", "stop", "list", "logout");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("logout")) && AuthService.USER_DIR.exists())
        {
            List<String> list = Lists.newArrayList();

            for (File file : AuthService.USER_DIR.listFiles())
            {
                list.add(file.getName());
            }
            return CommandBase.getListOfStringsMatchingLastWord(args, list);
        }
        return super.getTabCompletions(server, sender, args, pos);
    }

    private String getUsage()
    {
        return "/ytc <start|stop|list|logout>";
    }
}