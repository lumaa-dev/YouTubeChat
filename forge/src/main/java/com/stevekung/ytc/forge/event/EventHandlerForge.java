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

package com.stevekung.ytc.forge.event;

import com.stevekung.ytc.service.YouTubeChatService;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerForge
{
    //TODO Get current live video id directly
    @SubscribeEvent
    public void onClientSendChat(ClientChatEvent event)
    {
        var message = event.getMessage();

        if (message.contains("$set_live"))
        {
            YouTubeChatService.liveVideoId = message.replace("$set_live ", "");
        }
    }
}