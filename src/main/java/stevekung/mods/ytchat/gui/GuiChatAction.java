/*
 * Copyright 2017-2019 Google Inc.
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

package stevekung.mods.ytchat.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import stevekung.mods.stevekungslib.utils.JsonUtils;
import stevekung.mods.stevekungslib.utils.LangUtils;
import stevekung.mods.ytchat.core.YouTubeChatMod;
import stevekung.mods.ytchat.utils.YouTubeChatService;

@OnlyIn(Dist.CLIENT)
public class GuiChatAction extends GuiScreen
{
    private GuiButton temporaryBanButton;
    private GuiButton banButton;
    private GuiButton unbanButton;
    private GuiButton addModerator;
    private GuiButton removeModerator;
    private final YouTubeChatService service;
    private final String messageId;
    private final String channelId;
    private final String moderatorId;
    private final String displayName;

    public GuiChatAction(String messageId, String channelId, String moderatorId, String displayName)
    {
        this.mc = Minecraft.getInstance();
        this.messageId = messageId;
        this.channelId = channelId;
        this.moderatorId = moderatorId;
        this.displayName = displayName;
        this.service = YouTubeChatService.getService();
    }

    @Override
    public void initGui()
    {
        int xChat = this.width / 2 - 120;
        int yChat = this.height / 2 - 25;
        int xMod = this.width / 2 + 4;
        this.addButton(new GuiButton(0, xChat, yChat - 30, 120, 20, LangUtils.translate("menu.delete_chat"))
        {
            @Override
            public void onClick(double mouseX, double mouseZ)
            {
                Runnable response = () -> YouTubeChatMod.LOGGER.printYTMessage(LangUtils.translateComponent("message.chat_message_deleted").setStyle(JsonUtils.green()));
                GuiChatAction.this.service.deleteMessage(GuiChatAction.this.messageId, response);
                GuiChatAction.this.mc.displayGuiScreen(null);
            }
        });
        this.addButton(this.temporaryBanButton = new GuiButton(1, xChat, yChat - 8, 120, 20, LangUtils.translate("menu.temporary_ban"))
        {
            @Override
            public void onClick(double mouseX, double mouseZ)
            {
                Runnable response = () -> YouTubeChatMod.LOGGER.printYTMessage(LangUtils.translateComponent("message.user").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(" " + GuiChatAction.this.displayName + " ").setStyle(JsonUtils.darkRed()).appendSibling(LangUtils.translateComponent("message.temporary_banned").setStyle(JsonUtils.green()))));
                GuiChatAction.this.service.banUser(GuiChatAction.this.channelId, response, true);
                GuiChatAction.this.mc.displayGuiScreen(null);
            }
        });
        this.addButton(this.banButton = new GuiButton(2, xChat, yChat + 14, 120, 20, LangUtils.translate("menu.ban"))
        {
            @Override
            public void onClick(double mouseX, double mouseZ)
            {
                Runnable response = () -> YouTubeChatMod.LOGGER.printYTMessage(LangUtils.translateComponent("message.user").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(" " + GuiChatAction.this.displayName + " ").setStyle(JsonUtils.darkRed()).appendSibling(LangUtils.translateComponent("message.banned").setStyle(JsonUtils.green()))));
                GuiChatAction.this.service.banUser(GuiChatAction.this.channelId, response, false);
                GuiChatAction.this.mc.displayGuiScreen(null);
            }
        });
        this.addButton(this.unbanButton = new GuiButton(3, xChat, yChat + 36, 120, 20, LangUtils.translate("menu.unban"))
        {
            @Override
            public void onClick(double mouseX, double mouseZ) //TODO Fix this
            {
                Runnable response = () -> YouTubeChatMod.LOGGER.printYTMessage(LangUtils.translateComponent("message.user").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(" " + GuiChatAction.this.displayName + " ").setStyle(JsonUtils.darkRed()).appendSibling(LangUtils.translateComponent("message.unbanned").setStyle(JsonUtils.green()))));
                GuiChatAction.this.service.unbanUser(GuiChatAction.this.channelId, response);
                GuiChatAction.this.mc.displayGuiScreen(null);
            }
        });

        this.addButton(this.addModerator = new GuiButton(4, xMod, yChat - 30, 120, 20, LangUtils.translate("menu.add_moderator"))
        {
            @Override
            public void onClick(double mouseX, double mouseZ)
            {
                Runnable response = () -> YouTubeChatMod.LOGGER.printYTMessage(LangUtils.translateComponent("message.added").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(" " + GuiChatAction.this.displayName + " ").setStyle(JsonUtils.blue()).appendSibling(LangUtils.translateComponent("message.moderator_added").setStyle(JsonUtils.green()))));
                GuiChatAction.this.service.addModerator(GuiChatAction.this.channelId, response);
                GuiChatAction.this.mc.displayGuiScreen(null);
            }
        });
        this.addButton(this.removeModerator = new GuiButton(5, xMod, yChat - 8, 120, 20, LangUtils.translate("menu.remove_moderator"))
        {
            @Override
            public void onClick(double mouseX, double mouseZ)
            {
                Runnable response = () -> YouTubeChatMod.LOGGER.printYTMessage(LangUtils.translateComponent("message.removed").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(" " + GuiChatAction.this.displayName + " ").setStyle(JsonUtils.blue()).appendSibling(LangUtils.translateComponent("message.moderator_removed").setStyle(JsonUtils.green()))));
                GuiChatAction.this.service.removeModerator(GuiChatAction.this.moderatorId, response);
                GuiChatAction.this.mc.displayGuiScreen(null);
            }
        });
        this.addButton(new GuiButton(50, this.width / 2 - 100, this.height / 2 + 60, LangUtils.translate("gui.cancel"))
        {
            @Override
            public void onClick(double mouseX, double mouseZ)
            {
                GuiChatAction.this.mc.displayGuiScreen(null);
            }
        });

        if (YouTubeChatService.channelOwnerId.equals(this.channelId))
        {
            this.banButton.enabled = false;
            this.temporaryBanButton.enabled = false;
            this.addModerator.enabled = false;
            this.removeModerator.enabled = false;
            this.unbanButton.enabled = false;
        }
        if (this.moderatorId.isEmpty())
        {
            this.removeModerator.enabled = false;
        }
        this.unbanButton.enabled = false;//TODO Temporary disabled
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, LangUtils.translate("menu.select_chat_action"), this.width / 2, this.height / 2 - 75, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }
}