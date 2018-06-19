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

package stevekung.mods.ytchat.command;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import stevekung.mods.stevekunglib.utils.JsonUtils;
import stevekung.mods.stevekunglib.utils.client.ClientCommandBase;
import stevekung.mods.ytchat.auth.Authentication;
import stevekung.mods.ytchat.config.ConfigManagerYT;
import stevekung.mods.ytchat.core.EventHandlerYT;
import stevekung.mods.ytchat.utils.LoggerYT;
import stevekung.mods.ytchat.utils.YouTubeChatReceiver;
import stevekung.mods.ytchat.utils.YouTubeChatService;

/**
 * An in-game command for managing the YouTube Chat service. Usage:
 *
 * /ytc <start|stop|logout|echo_start|echo_stop|post>
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
        if (args[0].equalsIgnoreCase("start"))
        {
            if (hasExecutor)
            {
                throw new CommandException("Service is already initialized");
            }

            ITextComponent component = ClientCommandBase.getChatComponentFromNthArg(args, 1);
            String profile = component.createCopy().getUnformattedText();

            if (profile.isEmpty())
            {
                service.start(ConfigManagerYT.youtube_chat_general.liveVideoId, clientSecret, null);
            }
            else
            {
                service.start(ConfigManagerYT.youtube_chat_general.liveVideoId, clientSecret, profile);
            }

            if (ConfigManagerYT.youtube_chat_chat.autoReceiveChat)
            {
                EventHandlerYT.isReceivedChat = true;
                service.subscribe(YouTubeChatReceiver.getInstance());
            }
        }
        else if (args[0].equalsIgnoreCase("stop"))
        {
            if (!hasExecutor)
            {
                throw new CommandException("Service is not initialized");
            }

            service.stop(false);

            if (ConfigManagerYT.youtube_chat_chat.autoReceiveChat)
            {
                EventHandlerYT.isReceivedChat = false;
                service.unsubscribe(YouTubeChatReceiver.getInstance());
            }
        }
        else if (args[0].equalsIgnoreCase("list"))
        {
            if (!Authentication.userDir.exists())
            {
                LoggerYT.printExceptionMessage("Folder doesn't exist!");
                return;
            }

            LoggerYT.printYTMessage(JsonUtils.create("Current login profiles list").setStyle(JsonUtils.white()));

            if (Authentication.userDir.listFiles().length < 1)
            {
                sender.sendMessage(JsonUtils.create("- Empty login profiles!").setStyle(JsonUtils.red()));
            }
            for (File file : Authentication.userDir.listFiles())
            {
                sender.sendMessage(JsonUtils.create("- ").appendSibling(JsonUtils.create(file.getName()).setStyle(JsonUtils.gold())));
            }
        }
        else if (args[0].equalsIgnoreCase("logout"))
        {
            if (hasExecutor)
            {
                service.stop(true);
            }

            ITextComponent component = ClientCommandBase.getChatComponentFromNthArg(args, 1);
            String profile = component.createCopy().getUnformattedText();

            try
            {
                if (profile.isEmpty())
                {
                    Authentication.clearCurrentCredentials();
                }
                else
                {
                    Authentication.clearCredentials(profile);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                LoggerYT.printExceptionMessage(e.getMessage());
            }
        }
        else if (args[0].equalsIgnoreCase("echo_start"))
        {
            if (!service.isInitialized())
            {
                throw new CommandException("Service is not initialized");
            }
            if (!service.getListeners().isEmpty())
            {
                throw new CommandException("Service is already start receiving live chat message");
            }
            EventHandlerYT.isReceivedChat = true;
            service.subscribe(YouTubeChatReceiver.getInstance());
        }
        else if (args[0].equalsIgnoreCase("echo_stop"))
        {
            if (!service.isInitialized())
            {
                throw new CommandException("Service is not initialized");
            }
            if (service.getListeners().isEmpty())
            {
                throw new CommandException("Service is stop receiving live chat message");
            }
            EventHandlerYT.isReceivedChat = false;
            service.unsubscribe(YouTubeChatReceiver.getInstance());
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
            return CommandBase.getListOfStringsMatchingLastWord(args, "start", "stop", "list", "logout", "echo_start", "echo_stop");
        }
        if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("logout"))
            {
                if (Authentication.userDir.exists())
                {
                    List<String> list = new LinkedList<>();

                    for (File file : Authentication.userDir.listFiles())
                    {
                        list.add(file.getName());
                    }
                    return CommandBase.getListOfStringsMatchingLastWord(args, list);
                }
            }
        }
        return super.getTabCompletions(server, sender, args, pos);
    }

    private String getUsage()
    {
        return "/ytc <start|stop|list|logout|echo_start|echo_stop>";
    }
}