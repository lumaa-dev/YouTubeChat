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

/**
 *
 * GUI for select an action. [Delete, Ban, Temporary ban, Add moderator]
 * @author SteveKunG
 *
 */
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    GuiChatAction(ChatService service, String messageId)
    {
        this.mc = Minecraft.getMinecraft();
        this.service = service;
        this.messageId = messageId;
    }

    public void display()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        this.mc.displayGuiScreen(this);
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public void initGui()
    {
        this.buttonList.add(this.deleteButton = new GuiButton(0, this.width / 2 - 170, this.height / 5 + 96, 80, 20, "Delete"));
        this.buttonList.add(this.banButton = new GuiButton(1, this.width / 2 - 83, this.height / 5 + 96, 80, 20, "Ban WIP"));
        this.buttonList.add(this.temporaryBanButton = new GuiButton(2, this.width / 2 + 4, this.height / 5 + 96, 80, 20, "Temporary Ban WIP"));
        this.buttonList.add(this.addModerator = new GuiButton(3, this.width / 2 + 90, this.height / 5 + 96, 80, 20, "Add Moderator WIP"));
        this.buttonList.add(this.cancelButton = new GuiButton(4, this.width / 2 - 100, this.height / 5 + 120, I18n.format("gui.cancel")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                Runnable response = () -> ModLogger.printYTMessage(StreamChat.json.text("Message deleted").setStyle(StreamChat.json.green()));
                this.service.deleteMessage(this.messageId, response);
            }
            if (button.id == 1)
            {
                Runnable response = () -> ModLogger.printYTMessage(StreamChat.json.text("User banned").setStyle(StreamChat.json.green()));
                this.service.banUser(this.messageId, response, true);
            }
            if (button.id == 2)
            {
                Runnable response = () -> ModLogger.printYTMessage(StreamChat.json.text("User temporary banned").setStyle(StreamChat.json.green()));
                this.service.banUser(this.messageId, response, false);
            }
            if (button.id == 3)
            {
                Runnable response = () -> ModLogger.printYTMessage(StreamChat.json.text("Added moderator").setStyle(StreamChat.json.green()));
                this.service.addModerator(this.messageId, response);
            }
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Do an action for this message", this.width / 2, 120, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}