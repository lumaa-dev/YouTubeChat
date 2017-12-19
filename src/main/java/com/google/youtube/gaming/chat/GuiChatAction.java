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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;

/**
 *
 * GUI for select an action. [Delete, Ban, Temporary ban, Add moderator]
 * @author SteveKunG
 *
 */
@SideOnly(Side.CLIENT)
public class GuiChatAction extends GuiScreen
{
    private GuiButton deleteButton;
    private GuiButton banButton;
    private GuiButton temporaryBanButton;
    private GuiButton addModerator;
    private GuiButton cancelButton;
    private ChatService service;
    private String messageId;
    private String channelId;
    private String displayName;

    GuiChatAction(ChatService service, String messageId, String channelId, String displayName)
    {
        this.mc = Minecraft.getMinecraft();
        this.service = service;
        this.messageId = messageId;
        this.channelId = channelId;
        this.displayName = displayName;
    }

    public void display()
    {
        if (this.service.executor == null)
        {
            return;
        }
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        this.mc.displayGuiScreen(this);
        MinecraftForge.EVENT_BUS.unregister(this);
        FMLCommonHandler.instance().bus().unregister(this);
    }

    @Override
    public void initGui()
    {
        this.buttonList.add(this.deleteButton = new GuiButton(0, this.width / 2 - 170, this.height / 5 + 96, 80, 20, "Delete"));
        this.buttonList.add(this.banButton = new GuiButton(1, this.width / 2 - 83, this.height / 5 + 96, 80, 20, "Ban"));
        this.buttonList.add(this.temporaryBanButton = new GuiButton(2, this.width / 2 + 4, this.height / 5 + 96, 80, 20, "Temporary Ban"));
        this.buttonList.add(this.addModerator = new GuiButton(3, this.width / 2 + 90, this.height / 5 + 96, 80, 20, "Add Moderator"));
        this.buttonList.add(this.cancelButton = new GuiButton(4, this.width / 2 - 100, this.height / 5 + 120, I18n.format("gui.cancel")));

        if (ChatService.channelOwnerId.equals(this.channelId))
        {
            this.banButton.enabled = false;
            this.temporaryBanButton.enabled = false;
            this.addModerator.enabled = false;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                Runnable response = () -> ModLogger.printYTMessage(YouTubeChat.json.text("Message deleted!").setChatStyle(YouTubeChat.json.green()));
                this.service.deleteMessage(this.messageId, response);
            }
            if (button.id == 1)
            {
                Runnable response = () -> ModLogger.printYTMessage(YouTubeChat.json.text("User ").setChatStyle(YouTubeChat.json.green()).appendSibling(YouTubeChat.json.text(this.displayName + " ").setChatStyle(YouTubeChat.json.darkRed()).appendSibling(YouTubeChat.json.text("was banned!").setChatStyle(YouTubeChat.json.green()))));
                this.service.banUser(this.channelId, response, false);
            }
            if (button.id == 2)
            {
                Runnable response = () -> ModLogger.printYTMessage(YouTubeChat.json.text("User ").setChatStyle(YouTubeChat.json.green()).appendSibling(YouTubeChat.json.text(this.displayName + " ").setChatStyle(YouTubeChat.json.darkRed()).appendSibling(YouTubeChat.json.text("was temporary banned!").setChatStyle(YouTubeChat.json.green()))));
                this.service.banUser(this.channelId, response, true);
            }
            if (button.id == 3)
            {
                Runnable response = () -> ModLogger.printYTMessage(YouTubeChat.json.text("Added ").setChatStyle(YouTubeChat.json.green()).appendSibling(YouTubeChat.json.text(this.displayName + " ").setChatStyle(YouTubeChat.json.blue()).appendSibling(YouTubeChat.json.text("to moderator!").setChatStyle(YouTubeChat.json.green()))));
                this.service.addModerator(this.channelId, response);
            }
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Do an action for this message", this.width / 2, 120, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}