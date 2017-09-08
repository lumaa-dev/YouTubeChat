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

import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

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
        if (!ConfigManager.getInstance().getSuperOnly())
        {
            String unicode = "";
            String userDisplayName = author.getDisplayName();

            if (author.getIsChatOwner())
            {
                unicode = this.getUnicode(ConfigManager.getInstance().getOwnerUnicode());
            }
            if (author.getIsVerified())
            {
                unicode = "\u2713 ";
            }
            if (author.getIsChatModerator())
            {
                unicode = this.getUnicode(ConfigManager.getInstance().getModeratorUnicode());
            }
            if (author.getIsVerified() && author.getIsChatModerator())
            {
                unicode = "\u2713 " + this.getUnicode(ConfigManager.getInstance().getModeratorUnicode());
            }
            for (String word : ConfigManager.getInstance().getRudeWordList().split(","))
            {
                if (message.contains(word) && !author.getIsChatOwner() && !author.getIsVerified() && !author.getIsChatModerator())
                {
                    Runnable response = () -> ModLogger.printYTMessage(YouTubeChat.json.text("User ").setStyle(YouTubeChat.json.green()).appendSibling(YouTubeChat.json.text(userDisplayName + " ").setStyle(YouTubeChat.json.darkRed()).appendSibling(YouTubeChat.json.text("was automatically banned!").setStyle(YouTubeChat.json.green()))), ConfigManager.getInstance().getRightSideChat());
                    YouTubeChat.getService().banUser(author.getChannelId(), response, false);
                }
            }
            ModLogger.printYTMessage(YouTubeChat.json.text(unicode + userDisplayName).setStyle(author.getIsChatOwner() ? YouTubeChat.json.gold().setClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setStyle(YouTubeChat.json.white()))) : author.getIsChatModerator() ? YouTubeChat.json.blue().setClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setStyle(YouTubeChat.json.white()))) : YouTubeChat.json.gray().setClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setStyle(YouTubeChat.json.white())))).appendSibling(YouTubeChat.json.text(": " + message).setStyle(YouTubeChat.json.white().setClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setStyle(YouTubeChat.json.white()))))), ConfigManager.getInstance().getRightSideChat());
        }
        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            ModLogger.printYTMessage(YouTubeChat.json.text("Received ").setStyle(YouTubeChat.json.green()).appendSibling(YouTubeChat.json.text(superChatDetails.getAmountDisplayString()).setStyle(YouTubeChat.json.gold()).appendSibling(YouTubeChat.json.text(" from ").setStyle(YouTubeChat.json.green())).appendSibling(YouTubeChat.json.text(author.getDisplayName()).setStyle(author.getIsChatModerator() ? YouTubeChat.json.blue() : YouTubeChat.json.white()))), ConfigManager.getInstance().getRightSideChat());
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