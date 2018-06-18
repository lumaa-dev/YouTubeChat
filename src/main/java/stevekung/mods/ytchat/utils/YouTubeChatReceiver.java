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

package stevekung.mods.ytchat.utils;

import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;
import com.google.api.services.youtube.model.LiveChatUserBannedMessageDetails;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import stevekung.mods.stevekunglib.utils.JsonUtils;
import stevekung.mods.ytchat.auth.YouTubeChatService;
import stevekung.mods.ytchat.config.ConfigManagerYT;
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
    public void onMessageReceived(LiveChatMessageAuthorDetails author, LiveChatSuperChatDetails superChatDetails, String id, String message)
    {
        message = TextFormatting.getTextWithoutFormattingCodes(message);
        
        if (!ConfigManagerYT.youtube_chat_chat.showSuperChatOnly)
        {
            String unicode = "";
            String userDisplayName = author.getDisplayName();

            if (author.getIsChatOwner())
            {
                unicode = this.getUnicode(ConfigManagerYT.youtube_chat_chat.ownerUnicodeIcon);
            }
            if (author.getIsVerified())
            {
                unicode = "\u2713 ";
            }
            if (author.getIsChatModerator())
            {
                unicode = this.getUnicode(ConfigManagerYT.youtube_chat_chat.moderatorUnicodeIcon);
            }
            if (author.getIsVerified() && author.getIsChatModerator())
            {
                unicode = "\u2713 " + this.getUnicode(ConfigManagerYT.youtube_chat_chat.moderatorUnicodeIcon);
            }

            if (!ConfigManagerYT.youtube_chat_chat.bannedRudeWordList.isEmpty())
            {
                for (String word : ConfigManagerYT.youtube_chat_chat.bannedRudeWordList.split(","))
                {
                    if (message.contains(word) && !author.getIsChatOwner() && !author.getIsVerified() && !author.getIsChatModerator())
                    {
                        Runnable response = () -> LoggerYT.printYTMessage(JsonUtils.create("User ").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(userDisplayName + " ").setStyle(JsonUtils.darkRed()).appendSibling(JsonUtils.create("was automatically banned!").setStyle(JsonUtils.green()))));
                        YouTubeChatService.getService().banUser(author.getChannelId(), response, false);
                        return;
                    }
                }
            }
            if (!ConfigManagerYT.youtube_chat_chat.rudeWordList.isEmpty())
            {
                for (String word : ConfigManagerYT.youtube_chat_chat.rudeWordList.split(","))
                {
                    if (message.contains(word) && !author.getIsChatOwner() && !author.getIsVerified() && !author.getIsChatModerator())
                    {
                        Runnable response;

                        switch (ConfigManagerYT.youtube_chat_chat.rudeWordAction)
                        {
                        case DELETE:
                            response = () -> {};
                            YouTubeChatService.getService().deleteMessage(id, response);
                            break;
                        case TEMPORARY_BAN:
                            response = () -> LoggerYT.printYTMessage(JsonUtils.create("User ").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(userDisplayName + " ").setStyle(JsonUtils.darkRed()).appendSibling(JsonUtils.create("was automatically temporary banned!").setStyle(JsonUtils.green()))));
                            YouTubeChatService.getService().banUser(author.getChannelId(), response, false);
                            break;
                        }
                        return;
                    }
                }
            }
            Style color = author.getIsChatOwner() ? JsonUtils.gold() : author.getIsChatModerator() ? JsonUtils.blue() : JsonUtils.gray();
            LoggerYT.printYTMessage(JsonUtils.create(unicode + userDisplayName).setStyle(color.setClickEvent(JsonUtils.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_TEXT, JsonUtils.create("Click to do action this message").setStyle(JsonUtils.white())))).appendSibling(JsonUtils.create(": " + message).setStyle(JsonUtils.white().setClickEvent(JsonUtils.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_TEXT, JsonUtils.create("Click to do action this message").setStyle(JsonUtils.white()))))));
        }
        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            LoggerYT.printYTMessage(JsonUtils.create("Received ").setStyle(JsonUtils.green()).appendSibling(JsonUtils.create(superChatDetails.getAmountDisplayString()).setStyle(JsonUtils.gold()).appendSibling(JsonUtils.create(" from ").setStyle(JsonUtils.green())).appendSibling(JsonUtils.create(author.getDisplayName()).setStyle(author.getIsChatModerator() ? JsonUtils.blue() : JsonUtils.white()))));
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