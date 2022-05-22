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

package com.stevekung.ytc.utils;

import com.stevekung.ytc.core.YouTubeChat;
import com.stevekung.ytc.service.ChatService;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.event.ChatReceivedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;

/**
 *
 * The class that handled for ChatService.Listener interface.
 * @author SteveKunG
 *
 */
public class YouTubeChatReceiver implements ChatService.Listener
{
    public static final YouTubeChatReceiver INSTANCE = new YouTubeChatReceiver();

    @Override
    public void onChatReceived(ChatReceivedEvent event)
    {
        var author = event.author();
        var superChatDetails = event.superChatDetails();
        var message = event.message();
        var userDisplayName = author.getDisplayName();
        var owner = author.getIsChatOwner();
        var verified = author.getIsVerified();
        var moderator = author.getIsChatModerator();
        var ignoreCheck = !owner && !verified && !moderator;
        var unicode = "";

        if (owner)
        {
            unicode = YouTubeChat.CONFIG.chat.ownerIcon;
        }
        if (verified)
        {
            unicode = "✓ ";

            if (moderator)
            {
                unicode = "✓ " + YouTubeChat.CONFIG.chat.moderatorIcon;
            }
        }
        if (moderator)
        {
            unicode = YouTubeChat.CONFIG.chat.moderatorIcon;
        }

        if (!YouTubeChat.CONFIG.chat.bannedRudeWords.isEmpty())
        {
            for (var word : YouTubeChat.CONFIG.chat.bannedRudeWords)
            {
                if (message.contains(word) && ignoreCheck)
                {
                    YouTubeChatService.getService().banUser(author.getChannelId(), () -> ChatUtils.printChatMessage(new TranslatableComponent("message.user_auto_banned", userDisplayName)), false);
                    return;
                }
            }
        }
        if (!YouTubeChat.CONFIG.chat.rudeWords.isEmpty())
        {
            for (var word : YouTubeChat.CONFIG.chat.rudeWords)
            {
                if (message.contains(word) && ignoreCheck)
                {
                    switch (YouTubeChat.CONFIG.chat.rudeWordAction)
                    {
                        case DELETE -> YouTubeChatService.getService().deleteMessage(event.messageId(), () ->
                        {
                        });
                        case TEMPORARY_BAN -> YouTubeChatService.getService().banUser(author.getChannelId(), () -> ChatUtils.printChatMessage(new TranslatableComponent("message.user_auto_temporary_banned", userDisplayName)), false);
                    }
                    return;
                }
            }
        }

        var color = owner ? ChatFormatting.GOLD : moderator ? ChatFormatting.BLUE : ChatFormatting.GRAY;
        var formatted = new TextComponent(unicode + userDisplayName).withStyle(color);
        var runAction = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + event.messageId() + " " + author.getChannelId() + " " + event.moderatorId() + " " + userDisplayName)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("message.select_message_action")));
        ChatUtils.print(formatted.append(new TextComponent(": " + message).withStyle(ChatFormatting.WHITE)).withStyle(runAction));

        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            ChatUtils.printChatMessage(new TranslatableComponent("message.superchat_received", superChatDetails.getAmountDisplayString(), userDisplayName));
        }
    }
}