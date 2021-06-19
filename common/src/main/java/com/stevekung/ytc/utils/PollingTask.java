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

import java.util.TimerTask;

import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;
import com.stevekung.ytc.service.YouTubeChatService;
import com.stevekung.ytc.utils.event.ChatReceivedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

public class PollingTask extends TimerTask
{
    @Override
    public void run()
    {
        try
        {
            YouTubeChatService service = YouTubeChatService.getService();

            // Get current live view count
            if (!YouTubeChatService.liveVideoId.isEmpty())
            {
                service.getCurrentViewCount();
            }

            // Check if game is paused
            if (Minecraft.getInstance().isPaused())
            {
                service.poll(200);
                return;
            }

            // Get chat messages from YouTube
            LiveChatMessageListResponse chatResponse = service.getYoutube().liveChatMessages().list(service.getLiveChatId(), Lists.newArrayList("snippet", "authorDetails")).setPageToken(service.getNextPageToken()).setFields(YouTubeChatService.LIVE_CHAT_FIELDS).execute();
            // Get moderators list from YouTube
            LiveChatModeratorListResponse moderatorResponse = service.getYoutube().liveChatModerators().list(service.getLiveChatId(), Lists.newArrayList("snippet", "id")).setFields("items(id,snippet(moderatorDetails(channelId)))").execute();
            service.setNextPageToken(chatResponse.getNextPageToken());

            // Broadcast message to listeners on main thread
            for (LiveChatMessage message : chatResponse.getItems())
            {
                LiveChatMessageSnippet snippet = message.getSnippet();
                String moderatorId = "";

                for (LiveChatModerator moderator : moderatorResponse.getItems())
                {
                    if (message.getAuthorDetails().getChannelId().equals(moderator.getSnippet().getModeratorDetails().getChannelId()))
                    {
                        moderatorId = moderator.getId();
                    }
                }
                service.getListener().onChatReceived(new ChatReceivedEvent(message.getAuthorDetails(), snippet.getSuperChatDetails(), message.getId(), ChatFormatting.stripFormatting(snippet.getDisplayMessage()), moderatorId));
            }
            service.poll(chatResponse.getPollingIntervalMillis());
        }
        catch (Throwable t)
        {
            ChatUtils.printExceptionMessage(t.getMessage());
            t.printStackTrace();
        }
    }
}