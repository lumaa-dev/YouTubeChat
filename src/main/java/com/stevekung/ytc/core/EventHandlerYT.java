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

import com.stevekung.ytc.service.YouTubeChatService;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class EventHandlerYT
{
    public static boolean chatReceived;
    private boolean disconnected;
    private boolean connected;
    private int ticks = 40;
    private final Minecraft mc;
    private boolean initVersionCheck;

    public EventHandlerYT()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        if (EventHandlerYT.chatReceived)
        {
            this.connected = true;
        }
    }

    @SubscribeEvent
    public void onClientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        if (EventHandlerYT.chatReceived)
        {
            this.disconnected = true;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (EventHandlerYT.chatReceived)
        {
            if (this.connected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    YouTubeChatService.getService().subscribe();
                    this.connected = false;
                    this.ticks = 40;
                }
            }
            if (this.disconnected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    YouTubeChatService.getService().unsubscribe();
                    this.disconnected = false;
                    this.ticks = 40;
                }
            }
        }

        if (this.mc.player != null)
        {
            if (!this.initVersionCheck)
            {
                YouTubeChatMod.CHECKER.startCheckIfFailed();
            }
            this.initVersionCheck = true;
        }
    }

    //TODO
    //    @SubscribeEvent
    //    public void onClientSendChat(ClientChatEvent event)
    //    {
    //        String message = event.getMessage();
    //
    //        if (message.contains("$set_live"))
    //        {
    //            YouTubeChatService.liveVideoId = message.replace("$set_live ", "");
    //        }
    //    }
}