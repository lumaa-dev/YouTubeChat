/**
 * Copyright 2017 Google Inc.
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

package com.google.youtube.gaming.chat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;
import com.google.youtube.gaming.chat.StreamChatService.YouTubeChatMessageListener;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * An in-game command for managing the YouTube Chat service. Usage:
 *
 * /ytchat <start|stop|logout|echo_start|echo_stop|post>
 */
public class CommandYouTubeChat extends ClientCommandBase implements YouTubeChatMessageListener
{
    private ChatService service;

    public CommandYouTubeChat(ChatService service)
    {
        this.service = service;
    }

    @Override
    public String getName()
    {
        return "ytchat";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return this.getUsage();
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("ytc");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        ConfigManager configuration = ConfigManager.getInstance();
        String clientSecret = configuration.getClientSecret();

        if (args.length == 0)
        {
            throw new WrongUsageException(this.getUsage());
        }
        if (args[0].equalsIgnoreCase("start"))
        {
            if (clientSecret.isEmpty())
            {
                throw new CommandException("No client secret configurated");
            }
            if (this.service.executor != null)
            {
                throw new CommandException("Service is already initialized");
            }
            this.service.start(configuration.getVideoId(), clientSecret);
        }
        else if (args[0].equalsIgnoreCase("stop"))
        {
            if (clientSecret.isEmpty())
            {
                throw new CommandException("No client secret configurated");
            }
            if (this.service.executor == null)
            {
                throw new CommandException("Service is not initialized");
            }
            this.service.stop(false);
        }
        else if (args[0].equalsIgnoreCase("logout"))
        {
            if (clientSecret.isEmpty())
            {
                throw new CommandException("No client secret configurated");
            }
            if (this.service.executor == null)
            {
                throw new CommandException("Service is not initialized");
            }

            this.service.stop(true);

            try
            {
                Auth.clearCredentials();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                ModLogger.printExceptionMessage(e.getMessage());
            }
        }
        else if (args[0].equalsIgnoreCase("echo_start"))
        {
            if (!this.service.isInitialized())
            {
                throw new CommandException("Service is not initialized");
            }
            if (!this.service.listeners.isEmpty())
            {
                throw new CommandException("Service is already start receiving live chat message");
            }
            this.service.subscribe(this);
        }
        else if (args[0].equalsIgnoreCase("echo_stop"))
        {
            if (!this.service.isInitialized())
            {
                throw new CommandException("Service is not initialized");
            }
            if (this.service.listeners.isEmpty())
            {
                throw new CommandException("Service is stop receiving live chat message");
            }
            this.service.unsubscribe(this);
        }
        else if (args[0].equalsIgnoreCase("post"))
        {
            if (args.length == 1)
            {
                throw new WrongUsageException("/ytchat post <message>");
            }
            String message = ClientCommandBase.getChatComponentFromNthArg(args, 1).createCopy().getUnformattedText();
            Consumer<String> id = i -> ModLogger.printYTMessage(StreamChat.json.text("Message posted").setStyle(StreamChat.json.green()));
            this.service.postMessage(message, id);
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
            return CommandBase.getListOfStringsMatchingLastWord(args, "start", "stop", "logout", "echo_start", "echo_stop", "post");
        }
        return super.getTabCompletions(server, sender, args, pos);
    }

    @Override
    public void onMessageReceived(LiveChatMessageAuthorDetails author, LiveChatSuperChatDetails superChatDetails, String id, String message)
    {
        if (!ConfigManager.getInstance().getSuperOnly())
        {
            String unicode = author.getIsVerified() ? "\u2713 " : !author.getIsChatOwner() ? ConfigManager.getInstance().getOwnerUnicode() : author.getIsChatModerator() ? "" : "";//TODO
            ModLogger.printYTMessage(StreamChat.json.text(unicode + author.getDisplayName()).setStyle(author.getIsChatOwner() ? StreamChat.json.gold() : author.getIsChatModerator() ? StreamChat.json.blue() : StreamChat.json.gray()).appendSibling(StreamChat.json.text(": " + message).setStyle(StreamChat.json.white().setClickEvent(StreamChat.json.click(ClickEvent.Action.RUN_COMMAND, "ytcaction " + id)).setHoverEvent(StreamChat.json.hover(HoverEvent.Action.SHOW_TEXT, StreamChat.json.text("Click to do action this message").setStyle(StreamChat.json.white()))))));
        }
        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            ModLogger.printYTMessage(StreamChat.json.text("Received ").setStyle(StreamChat.json.green()).appendSibling(StreamChat.json.text(superChatDetails.getAmountDisplayString()).setStyle(StreamChat.json.gold()).appendSibling(StreamChat.json.text(" from ").setStyle(StreamChat.json.green())).appendSibling(StreamChat.json.text(author.getDisplayName()).setStyle(author.getIsChatModerator() ? StreamChat.json.blue() : StreamChat.json.white()))));
        }
    }

    private String getUsage()
    {
        return "/ytchat <start|stop|logout|echo_start|echo_stop|post>";
    }
}