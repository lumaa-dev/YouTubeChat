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
            ModLogger.printYTMessage(YouTubeChat.json.text(unicode + userDisplayName).setChatStyle(author.getIsChatOwner() ? YouTubeChat.json.gold().setChatClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setChatHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setChatStyle(YouTubeChat.json.white()))) : author.getIsChatModerator() ? YouTubeChat.json.blue().setChatClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setChatHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setChatStyle(YouTubeChat.json.white()))) : YouTubeChat.json.gray().setChatClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setChatHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setChatStyle(YouTubeChat.json.white())))).appendSibling(YouTubeChat.json.text(": " + message).setChatStyle(YouTubeChat.json.white().setChatClickEvent(YouTubeChat.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setChatHoverEvent(YouTubeChat.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChat.json.text("Click to do action this message").setChatStyle(YouTubeChat.json.white()))))), ConfigManager.getInstance().getRightSideChat());
        }
        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            ModLogger.printYTMessage(YouTubeChat.json.text("Received ").setChatStyle(YouTubeChat.json.green()).appendSibling(YouTubeChat.json.text(superChatDetails.getAmountDisplayString()).setChatStyle(YouTubeChat.json.gold()).appendSibling(YouTubeChat.json.text(" from ").setChatStyle(YouTubeChat.json.green())).appendSibling(YouTubeChat.json.text(author.getDisplayName()).setChatStyle(author.getIsChatModerator() ? YouTubeChat.json.blue() : YouTubeChat.json.white()))), ConfigManager.getInstance().getRightSideChat());
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