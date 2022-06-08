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

package com.stevekung.ytc.fabric.core;

import com.stevekung.ytc.core.YouTubeChat;
import com.stevekung.ytc.fabric.command.ChatActionCommand;
import com.stevekung.ytc.fabric.command.PostMessageCommand;
import com.stevekung.ytc.fabric.command.YouTubeChatCommand;
import com.stevekung.ytc.service.YouTubeChatService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;

public class YouTubeChatFabric implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        YouTubeChat.init();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
        {
            new ChatActionCommand(dispatcher);
            new PostMessageCommand(dispatcher);
            new YouTubeChatCommand(dispatcher);
        });
        ClientTickEvents.START_CLIENT_TICK.register(YouTubeChat::clientTick);
        ClientLoginConnectionEvents.INIT.register((handler, mc) ->
        {
            if (YouTubeChatService.receiveChat)
            {
                YouTubeChat.schedule(YouTubeChatService.getService()::subscribe, 40);
            }
        });
        ClientLoginConnectionEvents.DISCONNECT.register((handler, mc) ->
        {
            if (YouTubeChatService.receiveChat)
            {
                YouTubeChat.schedule(YouTubeChatService.getService()::unsubscribe, 40);
            }
        });
    }
}