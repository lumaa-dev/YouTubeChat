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

package com.google.youtube.gaming.chat;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Main entry point for YouTube Chat. Provides the chat service API to other mods, e.g.
 *
 * YouTubeChatService youTubeChatService = YouTubeChat.getService();
 */
@Mod(modid = YouTubeChat.MODID, name = YouTubeChat.NAME, version = YouTubeChat.VERSION, guiFactory = YouTubeChat.GUI_FACTORY)
public class YouTubeChat
{
    public static final String MODID = "youtube_chat";
    public static final String NAME = "YouTube Chat";
    public static final String VERSION = "1.3.1-1.7.10";
    public static final String GUI_FACTORY = "com.google.youtube.gaming.chat.ConfigGuiFactory";
    private static YouTubeChatService service;
    public static final JsonUtil json = new JsonUtil();
    public static GuiRightStreamChat rightStreamGui;
    private static boolean onDisconnected;
    private static boolean onConnected;
    private int ticks = 40;

    public static synchronized YouTubeChatService getService()
    {
        if (service == null)
        {
            service = new ChatService();
        }
        return service;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        YouTubeChat.initModInfo(event.getModMetadata());
        ConfigManager.initialize(event.getSuggestedConfigurationFile());
        ChatService service = (ChatService) YouTubeChat.getService();
        ClientCommandHandler.instance.registerCommand(new CommandYouTubeChat(service));
        ClientCommandHandler.instance.registerCommand(new CommandClearRightChat());
        ClientCommandHandler.instance.registerCommand(new CommandChatAction(service));
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        YouTubeChat.rightStreamGui = new GuiRightStreamChat(Minecraft.getMinecraft());
        onConnected = true;
    }

    @SubscribeEvent
    public void onClientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        onDisconnected = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (CommandYouTubeChat.isReceivedChat)
        {
            if (onConnected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    YouTubeChat.service.subscribe(YouTubeChatReceiver.getInstance());
                    onConnected = false;
                    this.ticks = 40;
                }
            }
            if (onDisconnected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    YouTubeChat.service.unsubscribe(YouTubeChatReceiver.getInstance());
                    onDisconnected = false;
                    this.ticks = 40;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPreInfoRender(RenderGameOverlayEvent.Pre event)
    {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
        {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glPushMatrix();
            GL11.glTranslatef(width / 2, height - 48, 0.0F);
            YouTubeChat.rightStreamGui.drawChat(Minecraft.getMinecraft().ingameGUI.getUpdateCounter());
            GL11.glPopMatrix();
        }
    }

    private static void initModInfo(ModMetadata info)
    {
        info.autogenerated = false;
        info.modId = YouTubeChat.MODID;
        info.name = YouTubeChat.NAME;
        info.description = "Enables interaction with YouTube Live Stream Chat.";
        info.version = YouTubeChat.VERSION;
        info.authorList = Arrays.asList("SteveKunG", "PeregrineZ", "jimrogers");
        info.credits = "Credit to PeregrineZ for implemented Example Features";
    }
}