/*
 * Copyright 2017-2019 Google Inc.
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

import java.io.File;
import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;

import joptsimple.internal.Strings;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import stevekung.mods.stevekungslib.utils.JsonUtils;
import stevekung.mods.stevekungslib.utils.LangUtils;
import stevekung.mods.ytchat.auth.Authentication;
import stevekung.mods.ytchat.command.arguments.YouTubeProfileArgumentType;
import stevekung.mods.ytchat.config.YouTubeChatConfig;
import stevekung.mods.ytchat.core.EventHandlerYT;
import stevekung.mods.ytchat.core.YouTubeChatMod;
import stevekung.mods.ytchat.utils.YouTubeChatReceiver;
import stevekung.mods.ytchat.utils.YouTubeChatService;

/**
 * An in-game command for managing the YouTube Chat service.
 *
 * Usage: /ytc <start|stop|list|logout>
 */
public class YouTubeChatCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("ytc").requires(requirement -> requirement.hasPermissionLevel(0))
                .then(Commands.literal("start").executes(requirement -> YouTubeChatCommand.startService(requirement.getSource(), null)).then(Commands.argument("profile", YouTubeProfileArgumentType.create()).executes(requirement -> YouTubeChatCommand.startService(requirement.getSource(), YouTubeProfileArgumentType.getProfile(requirement, "profile")))))
                .then(Commands.literal("stop").executes(requirement -> YouTubeChatCommand.stopService(requirement.getSource())))
                .then(Commands.literal("list").executes(requirement -> YouTubeChatCommand.listProfiles(requirement.getSource())))
                .then(Commands.literal("logout").executes(requirement -> YouTubeChatCommand.logoutService(requirement.getSource(), null)).then(Commands.argument("profile", YouTubeProfileArgumentType.create()).executes(requirement -> YouTubeChatCommand.logoutService(requirement.getSource(), YouTubeProfileArgumentType.getProfile(requirement, "profile"))))));
    }

    private static int startService(CommandSource source, String profile)
    {
        String clientSecret = YouTubeChatConfig.GENERAL.clientSecret.get();
        YouTubeChatService service = YouTubeChatService.getService();

        if (service.hasExecutor())
        {
            throw new CommandException(LangUtils.translateComponent("commands.service_is_init"));
        }
        service.start(clientSecret, profile);
        EventHandlerYT.isReceivedChat = true;
        service.subscribe(YouTubeChatReceiver.getInstance());
        return 0;
    }

    private static int stopService(CommandSource source)
    {
        YouTubeChatService service = YouTubeChatService.getService();

        if (!service.hasExecutor())
        {
            throw new CommandException(LangUtils.translateComponent("commands.service_not_init"));
        }
        service.stop(false);
        EventHandlerYT.isReceivedChat = false;
        service.unsubscribe(YouTubeChatReceiver.getInstance());
        return 0;
    }

    private static int listProfiles(CommandSource source)
    {
        if (!Authentication.userDir.exists())
        {
            source.sendErrorMessage(LangUtils.translateComponent("commands.folder_not_exist"));
        }

        YouTubeChatMod.LOGGER.printYTMessage(LangUtils.translateComponent("commands.current_login_list").setStyle(JsonUtils.white()));

        if (Authentication.userDir.listFiles().length < 1)
        {
            source.sendFeedback(JsonUtils.create("- " + LangUtils.translateComponent("commands.empty_login_profiles")).setStyle(JsonUtils.red()), false);
        }
        for (File file : Authentication.userDir.listFiles())
        {
            source.sendFeedback(JsonUtils.create("- ").appendSibling(JsonUtils.create(file.getName()).setStyle(JsonUtils.gold())), false);
        }
        return 0;
    }

    private static int logoutService(CommandSource source, String profile)
    {
        YouTubeChatService service = YouTubeChatService.getService();

        if (service.hasExecutor())
        {
            service.stop(true);
        }

        try
        {
            if (Strings.isNullOrEmpty("profile"))
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
            YouTubeChatMod.LOGGER.printExceptionMessage(e.getMessage());
        }
        return 0;
    }
}