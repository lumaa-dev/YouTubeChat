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

import com.stevekung.ytc.config.ConfigManagerYT;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.LoggerYT;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import stevekung.mods.stevekunglib.utils.JsonUtils;
import stevekung.mods.stevekunglib.utils.client.ClientCommandBase;

public class CommandPostMessage extends ClientCommandBase
{
    @Override
    public String getName()
    {
        return "yt";
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
        if (!service.hasExecutor())
        {
            throw new CommandException("[YouTubeChat] Service is not started");
        }
        String message = ClientCommandBase.getChatComponentFromNthArg(args, 0).createCopy().getUnformattedText();
        service.postMessage(message, i -> LoggerYT.printYTOverlayMessage(JsonUtils.create("Message posted").setStyle(JsonUtils.green())));
    }

    private String getUsage()
    {
        return "/yt <message>";
    }
}