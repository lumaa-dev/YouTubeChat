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

package com.stevekung.ytc.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.ChatUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;

/**
 *
 * GUI for select an action. [Delete, Ban, Temporary Ban, Add Moderator]
 * @author SteveKunG
 *
 */
public class ChatActionScreen extends Screen
{
    private final String messageId;
    private final String channelId;
    private final String moderatorId;
    private final String displayName;

    public ChatActionScreen(String messageId, String channelId, String moderatorId, String displayName)
    {
        super(TextComponent.EMPTY);
        this.messageId = messageId;
        this.channelId = channelId;
        this.moderatorId = moderatorId;
        this.displayName = displayName;
    }

    @Override
    public void init()
    {
        var xChat = this.width / 2 - 120;
        var yChat = this.height / 2 - 25;
        var xMod = this.width / 2 + 4;

        Button temporaryBanButton;
        Button banButton;
        Button unbanButton;
        Button addModerator;
        Button removeModerator;

        this.addRenderableWidget(new Button(xChat, yChat - 30, 120, 20, LangUtils.translate("menu.delete"), button -> YouTubeChatService.getService().deleteMessage(this.messageId, () -> ChatUtils.printYTMessage(TextComponentUtils.formatted("Message deleted!", ChatFormatting.GREEN)))));
        this.addRenderableWidget(temporaryBanButton = new Button(xChat, yChat - 8, 120, 20, LangUtils.translate("menu.temporary_ban"), button ->
        {
            Runnable response = () -> ChatUtils.printYTMessage(TextComponentUtils.formatted("User ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(this.displayName + " ", ChatFormatting.DARK_RED).append(TextComponentUtils.formatted("was temporary banned!", ChatFormatting.GREEN))));
            YouTubeChatService.getService().banUser(this.channelId, response, true);
        }));
        this.addRenderableWidget(banButton = new Button(xChat, yChat + 14, 120, 20, LangUtils.translate("menu.ban"), button ->
        {
            Runnable response = () -> ChatUtils.printYTMessage(TextComponentUtils.formatted("User ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(this.displayName + " ", ChatFormatting.DARK_RED).append(TextComponentUtils.formatted("was banned!", ChatFormatting.GREEN))));
            YouTubeChatService.getService().banUser(this.channelId, response, false);
        }));
        this.addRenderableWidget(unbanButton = new Button(xChat, yChat + 36, 120, 20, LangUtils.translate("menu.unban"), button ->
        {
            Runnable response = () -> ChatUtils.printYTMessage(TextComponentUtils.formatted("User ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(this.displayName + " ", ChatFormatting.DARK_RED).append(TextComponentUtils.formatted("has been unbanned!", ChatFormatting.GREEN))));
            YouTubeChatService.getService().unbanUser(this.channelId, response);
        }));

        this.addRenderableWidget(addModerator = new Button(xMod, yChat - 30, 120, 20, LangUtils.translate("menu.add_moderator"), button ->
        {
            Runnable response = () -> ChatUtils.printYTMessage(TextComponentUtils.formatted("Added ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(this.displayName + " ", ChatFormatting.BLUE).append(TextComponentUtils.formatted("to moderator!", ChatFormatting.GREEN))));
            YouTubeChatService.getService().addModerator(this.channelId, response);
        }));
        this.addRenderableWidget(removeModerator = new Button(xMod, yChat - 8, 120, 20, LangUtils.translate("menu.remove_moderator"), button ->
        {
            Runnable response = () -> ChatUtils.printYTMessage(TextComponentUtils.formatted("Removed ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(this.displayName + " ", ChatFormatting.BLUE).append(TextComponentUtils.formatted("from moderator!", ChatFormatting.GREEN))));
            YouTubeChatService.getService().removeModerator(this.moderatorId, response);
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 2 + 60, 200, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(null)));

        if (YouTubeChatService.ownerChannelId.equals(this.channelId))
        {
            banButton.active = false;
            temporaryBanButton.active = false;
            addModerator.active = false;
            removeModerator.active = false;
            unbanButton.active = false;
        }
        if (this.moderatorId.isEmpty())
        {
            removeModerator.active = false;
        }
        unbanButton.active = false;//TODO Temporary disabled
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        GuiComponent.drawCenteredString(poseStack, this.font, LangUtils.translate("menu.select_action"), this.width / 2, this.height / 2 - 75, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}