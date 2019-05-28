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

package stevekung.mods.ytchat.utils;

import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import stevekung.mods.stevekungslib.utils.JsonUtils;
import stevekung.mods.stevekungslib.utils.LangUtils;
import stevekung.mods.ytchat.config.YouTubeChatConfig;
import stevekung.mods.ytchat.core.YouTubeChatMod;
import stevekung.mods.ytchat.utils.AbstractChatService.YouTubeChatMessageListener;

/**
 *
 * The class that handled for YouTubeChatMessageListener interface.
 * @author SteveKunG
 *
 */
public class YouTubeChatReceiver implements YouTubeChatMessageListener
{
    private static YouTubeChatReceiver instance;

    public static YouTubeChatReceiver getInstance()
    {
        if (instance == null)
        {
            instance = new YouTubeChatReceiver();
        }
        return instance;
    }

    @Override
    public void onMessageReceived(LiveChatMessageAuthorDetails author, LiveChatSuperChatDetails superChatDetails, String id, String message, String moderatorId)
    {
        message = TextFormatting.getTextWithoutFormattingCodes(message);

        if (!YouTubeChatConfig.GENERAL.showSuperChatOnly.get())
        {
            String unicode = "";
            String userDisplayName = author.getDisplayName();

            if (author.getIsChatOwner())
            {
                unicode = this.getUnicode(YouTubeChatConfig.GENERAL.ownerUnicodeIcon.get());
            }
            if (author.getIsVerified())
            {
                unicode = "\u2713 ";
            }
            if (author.getIsChatModerator())
            {
                unicode = this.getUnicode(YouTubeChatConfig.GENERAL.moderatorUnicodeIcon.get());
            }
            if (author.getIsVerified() && author.getIsChatModerator())
            {
                unicode = "\u2713 " + this.getUnicode(YouTubeChatConfig.GENERAL.moderatorUnicodeIcon.get());
            }

            if (!YouTubeChatConfig.GENERAL.banRudeWordList.get().isEmpty())
            {
                for (String word : YouTubeChatConfig.GENERAL.banRudeWordList.get().split(","))
                {
                    if (message.contains(word) && !author.getIsChatOwner() && !author.getIsVerified() && !author.getIsChatModerator())
                    {
                        Runnable response = () -> YouTubeChatMod.LOGGER.printYTMessage(LangUtils.translateComponent("message.user").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(" " + userDisplayName + " ").setStyle(JsonUtils.darkRed()).appendSibling(LangUtils.translateComponent("message.auto_banned").setStyle(JsonUtils.green()))));
                        YouTubeChatService.getService().banUser(author.getChannelId(), response, false);
                        return;
                    }
                }
            }
            if (!YouTubeChatConfig.GENERAL.rudeWordList.get().isEmpty())
            {
                for (String word : YouTubeChatConfig.GENERAL.rudeWordList.get().split(","))
                {
                    if (message.contains(word) && !author.getIsChatOwner() && !author.getIsVerified() && !author.getIsChatModerator())
                    {
                        Runnable response;

                        if (YouTubeChatConfig.GENERAL.rudeWordAction.get() == YouTubeChatConfig.RudeWordAction.DELETE.name())
                        {
                            response = () -> {};
                            YouTubeChatService.getService().deleteMessage(id, response);
                        }
                        else
                        {
                            response = () -> YouTubeChatMod.LOGGER.printYTMessage(LangUtils.translateComponent("message.user").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(" " + userDisplayName + " ").setStyle(JsonUtils.darkRed()).appendSibling(LangUtils.translateComponent("message.auto_temporary_banned").setStyle(JsonUtils.green()))));
                            YouTubeChatService.getService().banUser(author.getChannelId(), response, false);
                        }
                        return;
                    }
                }
            }

            Style color = author.getIsChatOwner() ? JsonUtils.gold() : author.getIsChatModerator() ? JsonUtils.blue() : JsonUtils.gray();

            if (YouTubeChatConfig.GENERAL.displayChatRightSide.get())
            {
                YouTubeChatMod.LOGGER.printYTMessage(JsonUtils.create(message + " : ").setStyle(JsonUtils.white().setClickEvent(JsonUtils.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + moderatorId + " " + userDisplayName)).setHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_TEXT, JsonUtils.create("Click to do action this message").setStyle(JsonUtils.white())))).appendSibling(JsonUtils.create(unicode + userDisplayName).setStyle(color.setClickEvent(JsonUtils.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_TEXT, JsonUtils.create("Click to do action this message").setStyle(JsonUtils.white()))))));
            }
            else
            {
                YouTubeChatMod.LOGGER.printYTMessage(JsonUtils.create(unicode + userDisplayName).setStyle(color.setClickEvent(JsonUtils.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_TEXT, JsonUtils.create("Click to do action this message").setStyle(JsonUtils.white())))).appendSibling(JsonUtils.create(": " + message).setStyle(JsonUtils.white().setClickEvent(JsonUtils.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + moderatorId + " " + userDisplayName)).setHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_TEXT, JsonUtils.create("Click to do action this message").setStyle(JsonUtils.white()))))));
            }
        }
        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            YouTubeChatMod.LOGGER.printYTMessage(JsonUtils.create("Received ").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(superChatDetails.getAmountDisplayString()).setStyle(JsonUtils.gold()).appendSibling(JsonUtils.create(" from ").setStyle(JsonUtils.green())).appendSibling(JsonUtils.create(author.getDisplayName()).setStyle(author.getIsChatModerator() ? JsonUtils.blue() : JsonUtils.white()))));
        }
    }

    private String getUnicode(String raw)
    {
        String unicode = "";
        String str = raw.split(" ")[0];
        str = str.replace("\\", "");
        String[] arr = str.split("u");

        for (int i = 1; i < arr.length; i++)
        {
            int hexVal = Integer.parseInt(arr[i], 16);
            unicode += (char)hexVal + " ";
        }
        return unicode;
    }
}