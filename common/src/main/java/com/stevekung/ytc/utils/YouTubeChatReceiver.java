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

package com.stevekung.ytc.utils;

import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.ytc.service.ChatService;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.event.ChatReceivedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

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
            unicode = PlatformConfig.getOwnerIcon();
        }
        if (verified)
        {
            unicode = "✓ ";

            if (moderator)
            {
                unicode = "✓ " + PlatformConfig.getModeratorIcon();
            }
        }
        if (moderator)
        {
            unicode = PlatformConfig.getModeratorIcon();
        }

        if (!PlatformConfig.getBannedRudeWords().isEmpty())
        {
            for (var word : PlatformConfig.getBannedRudeWords())
            {
                if (message.contains(word) && ignoreCheck)
                {
                    YouTubeChatService.getService().banUser(author.getChannelId(), () -> ChatUtils.printYTMessage(TextComponentUtils.formatted("User ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(userDisplayName + " ", ChatFormatting.DARK_RED).append(TextComponentUtils.formatted("was automatically banned!", ChatFormatting.GREEN)))), false);
                    return;
                }
            }
        }
        if (!PlatformConfig.getRudeWords().isEmpty())
        {
            for (var word : PlatformConfig.getRudeWords())
            {
                if (message.contains(word) && ignoreCheck)
                {
                    switch (PlatformConfig.getRudeWordAction())
                    {
                        case DELETE -> YouTubeChatService.getService().deleteMessage(event.messageId(), () ->
                        {
                        });
                        case TEMPORARY_BAN -> YouTubeChatService.getService().banUser(author.getChannelId(), () -> ChatUtils.printYTMessage(TextComponentUtils.formatted("User ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(userDisplayName + " ", ChatFormatting.DARK_RED).append(TextComponentUtils.formatted("was automatically temporary banned!", ChatFormatting.DARK_RED)))), false);
                    }
                    return;
                }
            }
        }

        var color = owner ? ChatFormatting.GOLD : moderator ? ChatFormatting.BLUE : ChatFormatting.GRAY;
        var formatted = TextComponentUtils.formatted(unicode + userDisplayName, color);
        var runAction = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + event.messageId() + " " + author.getChannelId() + " " + event.moderatorId() + " " + userDisplayName)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentUtils.formatted("Click to do action this message", ChatFormatting.WHITE)));
        ChatUtils.print(formatted.append(TextComponentUtils.formatted(": " + message, ChatFormatting.WHITE)).withStyle(runAction));

        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            ChatUtils.printYTMessage(TextComponentUtils.formatted("Received ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(superChatDetails.getAmountDisplayString(), ChatFormatting.GOLD).append(TextComponentUtils.formatted(" from ", ChatFormatting.GREEN)).append(TextComponentUtils.formatted(author.getDisplayName(), moderator ? ChatFormatting.BLUE : ChatFormatting.WHITE))));
        }
    }
}