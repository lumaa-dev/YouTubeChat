/*
 * Copyright 2017-2018 Google Inc.
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

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import stevekung.mods.ytchat.gui.GuiRightChatYT;
import stevekung.mods.ytchat.utils.YouTubeChatReceiver;
import stevekung.mods.ytchat.utils.YouTubeChatService;

public class EventHandlerYT
{
    public static boolean isReceivedChat;
    public static GuiRightChatYT rightStreamGui;
    private boolean onDisconnected;
    private boolean onConnected;
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
        EventHandlerYT.rightStreamGui = new GuiRightChatYT(this.mc);

        if (EventHandlerYT.isReceivedChat)
        {
            this.onConnected = true;
        }
    }

    @SubscribeEvent
    public void onClientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        EventHandlerYT.rightStreamGui.clearChatMessages(true);

        if (EventHandlerYT.isReceivedChat)
        {
            this.onDisconnected = true;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (EventHandlerYT.isReceivedChat)
        {
            if (this.onConnected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    YouTubeChatService.getService().subscribe(YouTubeChatReceiver.getInstance());
                    this.onConnected = false;
                    this.ticks = 40;
                }
            }
            if (this.onDisconnected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    YouTubeChatService.getService().unsubscribe(YouTubeChatReceiver.getInstance());
                    this.onDisconnected = false;
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
        if (Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown(Keyboard.KEY_D))
        {
            if (EventHandlerYT.rightStreamGui != null)
            {
                EventHandlerYT.rightStreamGui.clearChatMessages(false);
            }
        }
    }

    @SubscribeEvent
    public void onPreInfoRender(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            ScaledResolution res = new ScaledResolution(this.mc);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.translate(width / 2, height - 48, 0.0F);
            EventHandlerYT.rightStreamGui.drawChat(this.mc.ingameGUI.getUpdateCounter());
            EventHandlerYT.rightStreamGui.drawRightChat(this.mc.ingameGUI.getUpdateCounter());
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onClientSendChat(ClientChatEvent event)
    {
        String message = event.getMessage();

        if (message.contains("$set_live"))
        {
            YouTubeChatService.liveVideoId = message.replace("$set_live ", "");
        }
    }
}