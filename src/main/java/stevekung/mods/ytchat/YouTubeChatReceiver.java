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

package stevekung.mods.ytchat;

import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import stevekung.mods.ytchat.AbstractChatService.YouTubeChatMessageListener;
import stevekung.mods.ytchat.auth.YouTubeChatService;
import stevekung.mods.ytchat.core.YouTubeChatMod;

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
        if (ConfigManager.removeColorFormatting)
        {
            message = TextFormatting.getTextWithoutFormattingCodes(message);
        }
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
                            YouTubeChatService.getService().deleteMessage(id, response);
                            break;
                        case "ban":
                            response = () -> LoggerYT.printYTMessage(YouTubeChatMod.json.text("User ").setStyle(YouTubeChatMod.json.green()).appendSibling(YouTubeChatMod.json.text(userDisplayName + " ").setStyle(YouTubeChatMod.json.darkRed()).appendSibling(YouTubeChatMod.json.text("was automatically banned!").setStyle(YouTubeChatMod.json.green()))));
                            YouTubeChatService.getService().banUser(author.getChannelId(), response, false);
                            break;
                        case "temporary_ban":
                            response = () -> LoggerYT.printYTMessage(YouTubeChatMod.json.text("User ").setStyle(YouTubeChatMod.json.green()).appendSibling(YouTubeChatMod.json.text(userDisplayName + " ").setStyle(YouTubeChatMod.json.darkRed()).appendSibling(YouTubeChatMod.json.text("was automatically temporary banned!").setStyle(YouTubeChatMod.json.green()))));
                            YouTubeChatService.getService().banUser(author.getChannelId(), response, false);
                            break;
                        }
                        return;
                    }
                }
            }
            Style color = author.getIsChatOwner() ? YouTubeChatMod.json.gold() : author.getIsChatModerator() ? YouTubeChatMod.json.blue() : YouTubeChatMod.json.gray();
            LoggerYT.printYTMessage(YouTubeChatMod.json.text(unicode + userDisplayName).setStyle(color.setClickEvent(YouTubeChatMod.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(YouTubeChatMod.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChatMod.json.text("Click to do action this message").setStyle(YouTubeChatMod.json.white())))).appendSibling(YouTubeChatMod.json.text(": " + message).setStyle(YouTubeChatMod.json.white().setClickEvent(YouTubeChatMod.json.click(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + id + " " + author.getChannelId() + " " + userDisplayName)).setHoverEvent(YouTubeChatMod.json.hover(HoverEvent.Action.SHOW_TEXT, YouTubeChatMod.json.text("Click to do action this message").setStyle(YouTubeChatMod.json.white()))))));
        }
        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            LoggerYT.printYTMessage(YouTubeChatMod.json.text("Received ").setStyle(YouTubeChatMod.json.green()).appendSibling(YouTubeChatMod.json.text(superChatDetails.getAmountDisplayString()).setStyle(YouTubeChatMod.json.gold()).appendSibling(YouTubeChatMod.json.text(" from ").setStyle(YouTubeChatMod.json.green())).appendSibling(YouTubeChatMod.json.text(author.getDisplayName()).setStyle(author.getIsChatModerator() ? YouTubeChatMod.json.blue() : YouTubeChatMod.json.white()))));
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