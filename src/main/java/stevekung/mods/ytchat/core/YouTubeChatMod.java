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

package stevekung.mods.ytchat.core;

import java.io.File;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import stevekung.mods.stevekungslib.client.gui.GuiChatRegistry;
import stevekung.mods.stevekungslib.utils.CommonUtils;
import stevekung.mods.stevekungslib.utils.GameProfileUtils;
import stevekung.mods.stevekungslib.utils.VersionChecker;
import stevekung.mods.ytchat.auth.Authentication;
import stevekung.mods.ytchat.command.ChatActionCommand;
import stevekung.mods.ytchat.command.PostMessageCommand;
import stevekung.mods.ytchat.command.YouTubeChatCommand;
import stevekung.mods.ytchat.config.YouTubeChatConfig;
import stevekung.mods.ytchat.gui.GuiYouTubeChat;
import stevekung.mods.ytchat.utils.LoggerYT;

@Mod(YouTubeChatMod.MOD_ID)
public class YouTubeChatMod
{
    public static final String NAME = "YouTube Chat";
    public static final String MOD_ID = "youtube_chat";
    private static final String URL = "https://minecraft.curseforge.com/projects/youtube-chat";

    public static final LoggerYT LOGGER = new LoggerYT();
    public static VersionChecker CHECKER;

    public YouTubeChatMod()
    {
        CommonUtils.addModListener(this::setup);
        CommonUtils.addModListener(this::loadComplete);
        CommonUtils.addListener(this::serverStarting);

        CommonUtils.registerConfig(ModConfig.Type.CLIENT, YouTubeChatConfig.GENERAL_BUILDER);
        CommonUtils.registerModEventBus(YouTubeChatConfig.class);
    }

    private void setup(FMLClientSetupEvent event)
    {
        Authentication.configDirectory = new File(FMLPaths.CONFIGDIR.get().toFile(), Authentication.CREDENTIALS_DIRECTORY);
        Authentication.userDir = new File(Authentication.configDirectory, GameProfileUtils.getUUID().toString());
        CommonUtils.registerEventHandler(this);
        CommonUtils.registerEventHandler(new EventHandlerYT());

        YouTubeChatMod.CHECKER = new VersionChecker(this, YouTubeChatMod.NAME, YouTubeChatMod.URL);

        if (YouTubeChatConfig.GENERAL.enableVersionChecker.get())
        {
            YouTubeChatMod.CHECKER.startCheck();
        }
    }

    private void loadComplete(FMLLoadCompleteEvent event)
    {
        GuiChatRegistry.register(new GuiYouTubeChat());
    }

    private void serverStarting(FMLServerStartingEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
        ChatActionCommand.register(dispatcher);
        PostMessageCommand.register(dispatcher);
        YouTubeChatCommand.register(dispatcher);
    }
}