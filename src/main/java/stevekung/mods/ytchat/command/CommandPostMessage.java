/*
 * Copyright 2017-2018 Google Inc.
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

package stevekung.mods.ytchat.command;

import java.util.function.Consumer;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import stevekung.mods.stevekunglib.utils.JsonUtils;
import stevekung.mods.stevekunglib.utils.client.ClientCommandBase;
import stevekung.mods.ytchat.config.ConfigManagerYT;
import stevekung.mods.ytchat.utils.LoggerYT;
import stevekung.mods.ytchat.utils.YouTubeChatService;

public class CommandPostMessage extends ClientCommandBase
{
    @Override
    public String getName()
    {
        return "ytm";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return this.getUsage();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String clientSecret = ConfigManagerYT.youtube_chat_general.clientSecret;
        YouTubeChatService service = YouTubeChatService.getService();
        boolean hasExecutor = service.getExecutor() != null;

        if (clientSecret.isEmpty())
        {
            throw new CommandException("No client secret configurated");
        }
        if (args.length == 0)
        {
            throw new WrongUsageException(this.getUsage());
        }
        if (!hasExecutor)
        {
            throw new CommandException("Service is not initialized");
        }
        String message = ClientCommandBase.getChatComponentFromNthArg(args, 0).createCopy().getUnformattedText();
        Consumer<String> id = i -> LoggerYT.printYTMessage(JsonUtils.create("Message posted").setStyle(JsonUtils.green()));
        service.postMessage(message, id);
    }

    private String getUsage()
    {
        return "/ytm <message>";
    }
}