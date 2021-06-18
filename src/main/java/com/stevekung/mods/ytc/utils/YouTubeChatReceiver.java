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

package com.stevekung.mods.ytc.utils;

import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;
import com.stevekung.mods.ytc.config.ConfigManagerYT;
import com.stevekung.mods.ytc.service.ChatService;
import com.stevekung.mods.ytc.service.YouTubeChatService;
import com.stevekung.mods.ytc.utils.event.ChatReceivedEvent;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import stevekung.mods.stevekunglib.utils.JsonUtils;

/**
 *
 * The class that handled for YouTubeChatListener interface.
 * @author SteveKunG
 *
 */
public class YouTubeChatReceiver implements ChatService.Listener
{
    public static final YouTubeChatReceiver INSTANCE = new YouTubeChatReceiver();

    @Override
    public void onChatReceived(ChatReceivedEvent event)
    {
        LiveChatMessageAuthorDetails author = event.getAuthor();
        LiveChatSuperChatDetails superChatDetails = event.getSuperChatDetails();
        String message = event.getMessage();
        String userDisplayName = author.getDisplayName();
        boolean owner = author.getIsChatOwner();
        boolean verified = author.getIsVerified();
        boolean moderator = author.getIsChatModerator();
        boolean ignoreCheck = !owner && !verified && !moderator;

        String unicode = "";

        if (owner)
        {
            unicode = ConfigManagerYT.YOUTUBE_CHAT_CHAT.ownerIcon;
        }
        if (verified)
        {
            unicode = "✓ ";

            if (moderator)
            {
                unicode = "✓ " + ConfigManagerYT.YOUTUBE_CHAT_CHAT.moderatorIcon;
            }
        }
        if (moderator)
        {
            unicode = ConfigManagerYT.YOUTUBE_CHAT_CHAT.moderatorIcon;
        }

        if (!ConfigManagerYT.YOUTUBE_CHAT_CHAT.bannedRudeWordList.isEmpty())
        {
            for (String word : ConfigManagerYT.YOUTUBE_CHAT_CHAT.bannedRudeWordList.split(","))
            {
                if (message.contains(word) && ignoreCheck)
                {
                    YouTubeChatService.getService().banUser(author.getChannelId(), () -> LoggerYT.printYTMessage(JsonUtils.create("User ").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(userDisplayName + " ").setStyle(JsonUtils.darkRed()).appendSibling(JsonUtils.create("was automatically banned!").setStyle(JsonUtils.green())))), false);
                    return;
                }
            }
        }
        if (!ConfigManagerYT.YOUTUBE_CHAT_CHAT.rudeWordList.isEmpty())
        {
            for (String word : ConfigManagerYT.YOUTUBE_CHAT_CHAT.rudeWordList.split(","))
            {
                if (message.contains(word) && ignoreCheck)
                {
                    switch (ConfigManagerYT.YOUTUBE_CHAT_CHAT.rudeWordAction)
                    {
                        default:
                        case DELETE:
                            YouTubeChatService.getService().deleteMessage(event.getMessageId(), () -> {});
                            break;
                        case TEMPORARY_BAN:
                            YouTubeChatService.getService().banUser(author.getChannelId(), () -> LoggerYT.printYTMessage(JsonUtils.create("User ").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(userDisplayName + " ").setStyle(JsonUtils.darkRed()).appendSibling(JsonUtils.create("was automatically temporary banned!").setStyle(JsonUtils.green())))), false);
                            break;
                    }
                    return;
                }
            }
        }

        Style color = owner ? JsonUtils.gold() : moderator ? JsonUtils.blue() : JsonUtils.gray();
        LoggerYT.print(JsonUtils.create(unicode + userDisplayName).setStyle(color.setClickEvent(JsonUtils.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + event.getMessageId() + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_TEXT, JsonUtils.create("Click to do action this message").setStyle(JsonUtils.white())))).appendSibling(JsonUtils.create(": " + message).setStyle(JsonUtils.white().setClickEvent(JsonUtils.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + event.getMessageId() + " " + author.getChannelId() + " " + event.getModeratorId() + " " + userDisplayName)).setHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_TEXT, JsonUtils.create("Click to do action this message").setStyle(JsonUtils.white()))))));

        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            LoggerYT.printYTMessage(JsonUtils.create("Received ").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(superChatDetails.getAmountDisplayString()).setStyle(JsonUtils.gold()).appendSibling(JsonUtils.create(" from ").setStyle(JsonUtils.green())).appendSibling(JsonUtils.create(author.getDisplayName()).setStyle(moderator ? JsonUtils.blue() : JsonUtils.white()))));
        }
    }
}