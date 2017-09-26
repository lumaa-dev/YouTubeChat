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

import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;
import com.google.youtube.gaming.chat.YouTubeChatService.YouTubeChatMessageListener;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatStyle;

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
    public void onMessageReceived(LiveChatMessageAuthorDetails author, LiveChatSuperChatDetails superChatDetails, String id, String message)
    {
        if (!ConfigManager.showSuperChatOnly)
        {
            String unicode = "";
            String userDisplayName = author.getDisplayName();

            if (author.getIsChatOwner())
            {
                unicode = this.getUnicode(ConfigManager.ownerUnicodeIcon);
            }
            if (author.getIsVerified())
            {
                unicode = "\u2713 ";
            }
            if (author.getIsChatModerator())
            {
                unicode = this.getUnicode(ConfigManager.moderatorUnicodeIcon);
            }
            if (author.getIsVerified() && author.getIsChatModerator())
            {
                unicode = "\u2713 " + this.getUnicode(ConfigManager.moderatorUnicodeIcon);
            }
            if (!ConfigManager.rudeWordList.isEmpty())
            {
                for (String word : ConfigManager.rudeWordList.split(","))
                {
                    if (message.contains(word) && !author.getIsChatOwner() && !author.getIsVerified() && !author.getIsChatModerator())
                    {
                        Runnable response;

                        switch (ConfigManager.rudeWordAction)
                        {
                        case "delete":
                            response = () -> {};
                            YouTubeChat.getService().deleteMessage(id, response);
                            break;
                        case "ban":
                            response = () -> ModLogger.printYTMessage(YouTubeChat.json.text("User ").setChatStyle(YouTubeChat.json.green()).appendSibling(YouTubeChat.json.text(userDisplayName + " ").setChatStyle(YouTubeChat.json.darkRed()).appendSibling(YouTubeChat.json.text("was automatically banned!").setChatStyle(YouTubeChat.json.green()))));
                            YouTubeChat.getService().banUser(author.getChannelId(), response, false);
                            break;
                        case "temporary_ban":
                            response = () -> ModLogger.printYTMessage(YouTubeChat.json.text("User ").setChatStyle(YouTubeChat.json.green()).appendSibling(YouTubeChat.json.text(userDisplayName + " ").setChatStyle(YouTubeChat.json.darkRed()).appendSibling(YouTubeChat.json.text("was automatically temporary banned!").setChatStyle(YouTubeChat.json.green()))));
                            YouTubeChat.getService().banUser(author.getChannelId(), response, false);
                            break;
                        }
                    }
                }
            }
            ChatStyle color = author.getIsChatOwner() ? YouTubeChat.json.gold() : author.getIsChatModerator() ? YouTubeChat.json.blue() : YouTubeChat.json.gray();
            ModLogger.printYTMessage(YouTubeChat.json.text(unicode + userDisplayName).setChatStyle(color.setChatClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setChatHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setChatStyle(YouTubeChat.json.white())))).appendSibling(YouTubeChat.json.text(": " + message).setChatStyle(YouTubeChat.json.white().setChatClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setChatHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setChatStyle(YouTubeChat.json.white()))))));
        }
        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            ModLogger.printYTMessage(YouTubeChat.json.text("Received ").setChatStyle(YouTubeChat.json.green()).appendSibling(YouTubeChat.json.text(superChatDetails.getAmountDisplayString()).setChatStyle(YouTubeChat.json.gold()).appendSibling(YouTubeChat.json.text(" from ").setChatStyle(YouTubeChat.json.green())).appendSibling(YouTubeChat.json.text(author.getDisplayName()).setChatStyle(author.getIsChatModerator() ? YouTubeChat.json.blue() : YouTubeChat.json.white()))));
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