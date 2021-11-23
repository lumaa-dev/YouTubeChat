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

package com.stevekung.ytc.forge;

import com.stevekung.stevekunglib.forge.utils.ForgeCommonUtils;
import com.stevekung.stevekunglib.forge.utils.ModVersionChecker;
import com.stevekung.stevekunglib.forge.utils.client.command.ClientCommands;
import com.stevekung.ytc.forge.command.ChatActionCommand;
import com.stevekung.ytc.forge.command.PostMessageCommand;
import com.stevekung.ytc.forge.command.YouTubeChatCommand;
import com.stevekung.ytc.forge.config.YouTubeChatConfig;
import com.stevekung.ytc.forge.event.EventHandlerForge;
import com.stevekung.ytc.core.YouTubeChat;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(YouTubeChat.MOD_ID)
public class YouTubeChatForge
{
    public static final ModVersionChecker CHECKER = new ModVersionChecker(YouTubeChat.MOD_ID);

    public YouTubeChatForge()
    {
        EventBuses.registerModEventBus(YouTubeChat.MOD_ID, ForgeCommonUtils.getModEventBus());
        YouTubeChat.init();
        ForgeCommonUtils.registerClientOnly();
        ForgeCommonUtils.addModListener(this::phaseOne);
        ForgeCommonUtils.addModListener(this::loadComplete);

        ForgeCommonUtils.registerConfig(ModConfig.Type.CLIENT, YouTubeChatConfig.SPEC);
        ForgeCommonUtils.registerConfigScreen((mc, parent) -> ForgeCommonUtils.openConfigFile(parent, YouTubeChat.MOD_ID, ModConfig.Type.CLIENT));
        ForgeCommonUtils.registerModEventBus(YouTubeChatConfig.class);
        ForgeCommonUtils.registerEventHandler(new EventHandlerForge());
    }

    private void phaseOne(FMLClientSetupEvent event)
    {
        this.registerClientCommands();
    }

    private void loadComplete(FMLLoadCompleteEvent event)
    {
        if (YouTubeChatConfig.GENERAL.enableVersionChecker.get())
        {
            YouTubeChatForge.CHECKER.startCheck();
        }
    }

    private void registerClientCommands()
    {
        ClientCommands.register(new ChatActionCommand());
        ClientCommands.register(new PostMessageCommand());
        ClientCommands.register(new YouTubeChatCommand());
        YouTubeChat.LOGGER.info("Registering client side commands");
    }
}