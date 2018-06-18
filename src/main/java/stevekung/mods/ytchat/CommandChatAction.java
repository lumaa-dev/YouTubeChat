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

package stevekung.mods.ytchat;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

/**
 *
 * Do an action with current chat message. [Delete, Ban, Temporary ban, Add moderator]
 * Usage: /ytcaction <message_id> <channel_id>
 * @author SteveKunG
 *
 */
public class CommandChatAction extends ClientCommandBase
{
    private ChatService service;

    public CommandChatAction(ChatService service)
    {
        this.service = service;
    }

    @Override
    public String getName()
    {
        return "ytcaction";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return this.getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new WrongUsageException(this.getUsage());
        }
        new GuiChatAction(this.service, args[0], args[1], ClientCommandBase.getChatComponentFromNthArg(args, 2).createCopy().getUnformattedText()).display();
    }

    private String getUsage()
    {
        return "/ytcaction <message_id> <channel_id>";
    }
}