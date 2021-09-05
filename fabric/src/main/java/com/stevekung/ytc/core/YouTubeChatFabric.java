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

package com.stevekung.ytc.core;

import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.ytc.command.ChatActionCommand;
import com.stevekung.ytc.command.PostMessageCommand;
import com.stevekung.ytc.command.YouTubeChatCommand;
import com.stevekung.ytc.config.YouTubeChatConfig;
import com.stevekung.ytc.service.YouTubeChatService;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.minecraft.client.Minecraft;

public class YouTubeChatFabric implements ClientModInitializer
{
    public static YouTubeChatConfig CONFIG;

    @Override
    public void onInitializeClient()
    {
        YouTubeChat.init();
        CommonUtils.initAntisteal(YouTubeChat.MOD_ID, YouTubeChatFabric.class, Minecraft.getInstance()::close);

        AutoConfig.register(YouTubeChatConfig.class, GsonConfigSerializer::new);
        YouTubeChatFabric.CONFIG = AutoConfig.getConfigHolder(YouTubeChatConfig.class).getConfig();

        new ChatActionCommand(ClientCommandManager.DISPATCHER);
        new PostMessageCommand(ClientCommandManager.DISPATCHER);
        new YouTubeChatCommand(ClientCommandManager.DISPATCHER);

        ClientLoginConnectionEvents.INIT.register((handler, mc) ->
        {
            if (YouTubeChatService.receiveChat)
            {
                CommonUtils.schedule(YouTubeChatService.getService()::subscribe, 40);
            }
        });
        ClientLoginConnectionEvents.DISCONNECT.register((handler, mc) ->
        {
            if (YouTubeChatService.receiveChat)
            {
                CommonUtils.schedule(YouTubeChatService.getService()::unsubscribe, 40);
            }
        });
    }
}