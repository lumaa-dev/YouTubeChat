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

import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

/**
 * An in-game command that will mock chat messages for testing. Usage:
 *
 * /ytcmock authorId message
 */
public class CommandChatMock extends ClientCommandBase
{
    private ChatService service;

    public CommandChatMock(ChatService service)
    {
        this.service = service;
    }

    @Override
    public String getCommandName()
    {
        return "ytcmock";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return this.getUsage();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getUsage());
        }
        String author = args[0];
        String message = ClientCommandBase.getChatComponentFromNthArg(args, 1).createCopy().getUnformattedText();
        ModLogger.info("YouTubeChatMock received {} from {}", message, author);
        LiveChatMessageAuthorDetails authorDetails = new LiveChatMessageAuthorDetails();
        authorDetails.setDisplayName(author);
        authorDetails.setChannelId(author);
        this.service.broadcastMessage(authorDetails, new LiveChatSuperChatDetails(), "MOCK" , message);
    }

    private String getUsage()
    {
        return "/ytcmock <author_id> <message>";
    }
}