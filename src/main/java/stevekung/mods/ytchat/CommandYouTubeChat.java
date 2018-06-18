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

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

/**
 * An in-game command for managing the YouTube Chat service. Usage:
 *
 * /ytc <start|stop|logout|echo_start|echo_stop|post>
 */
public class CommandYouTubeChat extends ClientCommandBase
{
    private ChatService service;
    static boolean isReceivedChat;

    public CommandYouTubeChat(ChatService service)
    {
        this.service = service;
    }

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
        String clientSecret = ConfigManager.clientSecret;

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
            if (this.service.executor != null)
            {
                throw new CommandException("Service is already initialized");
            }

            ITextComponent component = ClientCommandBase.getChatComponentFromNthArg(args, 1);
            String profile = component.createCopy().getUnformattedText();

            if (profile.isEmpty())
            {
                this.service.start(ConfigManager.liveVideoId, clientSecret, null);
            }
            else
            {
                this.service.start(ConfigManager.liveVideoId, clientSecret, profile);
            }

            if (ConfigManager.autoReceiveChat)
            {
                CommandYouTubeChat.isReceivedChat = true;
                this.service.subscribe(YouTubeChatReceiver.getInstance());
            }
        }
        else if (args[0].equalsIgnoreCase("stop"))
        {
            if (this.service.executor == null)
            {
                throw new CommandException("Service is not initialized");
            }

            this.service.stop(false);

            if (ConfigManager.autoReceiveChat)
            {
                CommandYouTubeChat.isReceivedChat = false;
                this.service.unsubscribe(YouTubeChatReceiver.getInstance());
            }
        }
        else if (args[0].equalsIgnoreCase("list"))
        {
            if (!YouTubeChat.configDirectory.exists())
            {
                ModLogger.printExceptionMessage("Folder doesn't exist!");
                return;
            }

            ModLogger.printYTMessage(YouTubeChat.json.text("Current login profiles list").setStyle(YouTubeChat.json.white()));

            if (YouTubeChat.configDirectory.listFiles().length < 1)
            {
                sender.sendMessage(YouTubeChat.json.text("- Empty login profiles!").setStyle(YouTubeChat.json.red()));
            }
            for (File file : YouTubeChat.configDirectory.listFiles())
            {
                sender.sendMessage(YouTubeChat.json.text("- ").appendSibling(YouTubeChat.json.text(file.getName()).setStyle(YouTubeChat.json.gold())));
            }
        }
        else if (args[0].equalsIgnoreCase("logout"))
        {
            if (this.service.executor != null)
            {
                this.service.stop(true);
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
            CommandYouTubeChat.isReceivedChat = true;
            this.service.subscribe(YouTubeChatReceiver.getInstance());
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
            CommandYouTubeChat.isReceivedChat = false;
            this.service.unsubscribe(YouTubeChatReceiver.getInstance());
        }
        else if (args[0].equalsIgnoreCase("post"))
        {
            if (args.length == 1)
            {
                throw new WrongUsageException("/ytc post <message>");
            }
            if (this.service.executor == null)
            {
                throw new CommandException("Service is not initialized");
            }
            String message = ClientCommandBase.getChatComponentFromNthArg(args, 1).createCopy().getUnformattedText();
            Consumer<String> id = i -> ModLogger.printYTMessage(YouTubeChat.json.text("Message posted").setStyle(YouTubeChat.json.green()));
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
            return CommandBase.getListOfStringsMatchingLastWord(args, "start", "stop", "list", "logout", "echo_start", "echo_stop", "post");
        }
        if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("logout"))
            {
                if (YouTubeChat.configDirectory.exists())
                {
                    List<String> list = new LinkedList<>();

                    for (File file : YouTubeChat.configDirectory.listFiles())
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
        return "/ytc <start|stop|list|logout|echo_start|echo_stop|post>";
    }
}