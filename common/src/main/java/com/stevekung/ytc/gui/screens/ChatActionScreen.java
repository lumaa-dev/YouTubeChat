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

package com.stevekung.ytc.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.ChatUtils;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/**
 *
 * GUI for select an action. [Delete, Ban, Temporary Ban, Add Moderator]
 * @author SteveKunG
 *
 */
public class ChatActionScreen extends Screen
{
    private static final Component DELETE = new TranslatableComponent("selectServer.delete");
    private static final Component TEMPORARY_BAN = new TranslatableComponent("menu.temporary_ban");
    private static final Component BAN = new TranslatableComponent("menu.ban");
    private static final Component UNBAN = new TranslatableComponent("menu.unban");
    private static final Component ADD_MODERATOR = new TranslatableComponent("menu.add_moderator");
    private static final Component REMOVE_MODERATOR = new TranslatableComponent("menu.remove_moderator");
    private static final Component SELECT_ACTION = new TranslatableComponent("menu.select_action");

    private static final Component MESSAGE_DELETED = new TranslatableComponent("message.deleted");
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

        this.addRenderableWidget(new Button(xChat, yChat - 30, 120, 20, DELETE, button -> YouTubeChatService.getService().deleteMessage(this.messageId, () -> ChatUtils.printChatMessage(MESSAGE_DELETED))));
        this.addRenderableWidget(temporaryBanButton = new Button(xChat, yChat - 8, 120, 20, TEMPORARY_BAN, button ->
        {
            Runnable response = () -> ChatUtils.printChatMessage(new TranslatableComponent("message.temporarily_banned", this.displayName));
            YouTubeChatService.getService().banUser(this.channelId, response, true);
        }));
        this.addRenderableWidget(banButton = new Button(xChat, yChat + 14, 120, 20, BAN, button ->
        {
            Runnable response = () -> ChatUtils.printChatMessage(new TranslatableComponent("message.user_banned", this.displayName));
            YouTubeChatService.getService().banUser(this.channelId, response, false);
        }));
        this.addRenderableWidget(unbanButton = new Button(xChat, yChat + 36, 120, 20, UNBAN, button ->
        {
            Runnable response = () -> ChatUtils.printChatMessage(new TranslatableComponent("message.user_unbanned", this.displayName));
            YouTubeChatService.getService().unbanUser(this.channelId, response);
        }));
        this.addRenderableWidget(addModerator = new Button(xMod, yChat - 30, 120, 20, ADD_MODERATOR, button ->
        {
            Runnable response = () -> ChatUtils.printChatMessage(new TranslatableComponent("message.add_moderator", this.displayName));
            YouTubeChatService.getService().addModerator(this.channelId, response);
        }));
        this.addRenderableWidget(removeModerator = new Button(xMod, yChat - 8, 120, 20, REMOVE_MODERATOR, button ->
        {
            Runnable response = () -> ChatUtils.printChatMessage(new TranslatableComponent("message.remove_moderator", this.displayName));
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
        GuiComponent.drawCenteredString(poseStack, this.font, SELECT_ACTION, this.width / 2, this.height / 2 - 75, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}