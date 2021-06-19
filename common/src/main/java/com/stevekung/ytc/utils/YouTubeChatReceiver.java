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

import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.ytc.service.ChatService;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.event.ChatReceivedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

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
            for (String word : PlatformConfig.getBannedRudeWords())
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
            for (String word : PlatformConfig.getRudeWords())
            {
                if (message.contains(word) && ignoreCheck)
                {
                    switch (PlatformConfig.getRudeWordAction())
                    {
                        default:
                        case DELETE:
                            YouTubeChatService.getService().deleteMessage(event.getMessageId(), () -> {});
                            break;
                        case TEMPORARY_BAN:
                            YouTubeChatService.getService().banUser(author.getChannelId(), () -> ChatUtils.printYTMessage(TextComponentUtils.formatted("User ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(userDisplayName + " ", ChatFormatting.DARK_RED).append(TextComponentUtils.formatted("was automatically temporary banned!", ChatFormatting.DARK_RED)))), false);
                            break;
                    }
                    return;
                }
            }
        }

        ChatFormatting color = owner ? ChatFormatting.GOLD : moderator ? ChatFormatting.BLUE : ChatFormatting.GRAY;
        MutableComponent formatted = TextComponentUtils.formatted(unicode + userDisplayName, color);
        ChatUtils.print(formatted.setStyle(formatted.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + event.getMessageId() + " " + author.getChannelId() + " " + userDisplayName)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentUtils.formatted("Click to do action this message", ChatFormatting.WHITE)))).append(TextComponentUtils.formatted(": " + message, ChatFormatting.WHITE).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ytcaction " + event.getMessageId() + " " + author.getChannelId() + " " + event.getModeratorId() + " " + userDisplayName)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentUtils.formatted("Click to do action this message", ChatFormatting.WHITE))))));

        if (superChatDetails != null && superChatDetails.getAmountMicros() != null && superChatDetails.getAmountMicros().longValue() > 0)
        {
            ChatUtils.printYTMessage(TextComponentUtils.formatted("Received ", ChatFormatting.GREEN).append(TextComponentUtils.formatted(superChatDetails.getAmountDisplayString(), ChatFormatting.GOLD).append(TextComponentUtils.formatted(" from ", ChatFormatting.GREEN)).append(TextComponentUtils.formatted(author.getDisplayName(), moderator ? ChatFormatting.BLUE : ChatFormatting.WHITE))));
        }
    }
}