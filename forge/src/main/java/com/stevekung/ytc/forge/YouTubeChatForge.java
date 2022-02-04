/*
 * Copyright 2017-2022 Google Inc.
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

import com.stevekung.ytc.core.YouTubeChat;
import com.stevekung.ytc.forge.command.ChatActionCommand;
import com.stevekung.ytc.forge.command.PostMessageCommand;
import com.stevekung.ytc.forge.command.YouTubeChatCommand;
import com.stevekung.ytc.forge.config.YouTubeChatConfig;
import com.stevekung.ytc.forge.event.EventHandlerForge;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(YouTubeChat.MOD_ID)
public class YouTubeChatForge
{
    public YouTubeChatForge()
    {
        YouTubeChat.init();
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().register(YouTubeChatConfig.class);
        MinecraftForge.EVENT_BUS.register(new EventHandlerForge());
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, YouTubeChatConfig.SPEC);
    }

    @SubscribeEvent
    public void registerClientCommands(RegisterClientCommandsEvent event)
    {
        ChatActionCommand.register(event.getDispatcher());
        PostMessageCommand.register(event.getDispatcher());
        YouTubeChatCommand.register(event.getDispatcher());
    }
}