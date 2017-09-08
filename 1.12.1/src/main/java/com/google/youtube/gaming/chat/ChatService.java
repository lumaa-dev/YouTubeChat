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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.*;

import net.minecraft.client.Minecraft;

/**
 * Manages connection to the YouTube chat service, posting chat messages, deleting chat messages,
 * polling for chat messages and notifying subcribers.
 */
class ChatService implements YouTubeChatService
{
    private static final String LIVE_CHAT_FIELDS = "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,isVerified,profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt),id),nextPageToken,pollingIntervalMillis";
    protected ExecutorService executor;
    private YouTube youtube;
    private String liveChatId;
    private boolean isInitialized;
    protected List<YouTubeChatMessageListener> listeners;
    private String nextPageToken;
    private Timer pollTimer;
    private long nextPoll;
    public static String channelOwnerId = "";

    public ChatService()
    {
        this.listeners = new ArrayList<>();
    }

    public void start(final String videoId, final String clientSecret)
    {
        this.executor = Executors.newCachedThreadPool();
        this.executor.execute(() ->
        {
            try
            {
                // Build auth scopes
                List<String> scopes = new ArrayList<>();
                scopes.add(YouTubeScopes.YOUTUBE_FORCE_SSL);
                scopes.add(YouTubeScopes.YOUTUBE);

                // Authorize the request
                Credential credential = Authentication.authorize(scopes, clientSecret, YouTubeChat.MODID);

                // This object is used to make YouTube Data API requests
                this.youtube = new YouTube.Builder(Authentication.HTTP_TRANSPORT, Authentication.JSON_FACTORY, credential).setApplicationName(YouTubeChat.NAME).build();

                // Get the live chat id
                String identity;

                if (videoId != null && !videoId.isEmpty())
                {
                    identity = "videoId " + videoId;
                    YouTube.Videos.List videoList = this.youtube.videos().list("liveStreamingDetails").setFields("items/liveStreamingDetails/activeLiveChatId").setId(videoId);
                    VideoListResponse response = videoList.execute();

                    for (Video video : response.getItems())
                    {
                        this.liveChatId = video.getLiveStreamingDetails().getActiveLiveChatId();

                        if (this.liveChatId != null && !this.liveChatId.isEmpty())
                        {
                            ModLogger.info("Live chat id: {}", this.liveChatId);
                            break;
                        }
                    }
                }
                else
                {
                    identity = "current user";
                    YouTube.LiveBroadcasts.List broadcastList = this.youtube.liveBroadcasts().list("snippet").setFields("items/snippet/liveChatId,items/snippet/channelId").setBroadcastType("all").setBroadcastStatus("active");
                    LiveBroadcastListResponse broadcastListResponse = broadcastList.execute();

                    for (LiveBroadcast broadcast : broadcastListResponse.getItems())
                    {
                        this.liveChatId = broadcast.getSnippet().getLiveChatId();
                        ChatService.channelOwnerId = broadcast.getSnippet().getChannelId();

                        if (this.liveChatId != null && !this.liveChatId.isEmpty())
                        {
                            ModLogger.info("Live chat id: {}", this.liveChatId);
                            break;
                        }
                    }
                }

                if (this.liveChatId == null || this.liveChatId.isEmpty())
                {
                    ModLogger.printExceptionMessage("Could not find live chat for " + identity);
                    return;
                }

                // Initialize next page token
                LiveChatMessageListResponse response = this.youtube.liveChatMessages().list(this.liveChatId, "snippet").setFields("nextPageToken, pollingIntervalMillis").execute();
                this.nextPageToken = response.getNextPageToken();
                this.isInitialized = true;

                if (this.pollTimer == null && !this.listeners.isEmpty())
                {
                    this.poll(response.getPollingIntervalMillis());
                }
                else
                {
                    this.nextPoll = System.currentTimeMillis() + response.getPollingIntervalMillis();
                }
                ModLogger.printYTMessage(YouTubeChat.json.text("Service started").setStyle(YouTubeChat.json.green()));
            }
            catch (Throwable t)
            {
                ModLogger.printExceptionMessage(t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void stop(boolean isLogout)
    {
        this.stopPolling();

        if (this.executor != null)
        {
            this.executor.shutdown();
            this.executor = null;
        }
        this.liveChatId = null;
        this.isInitialized = false;
        ModLogger.printYTMessage(YouTubeChat.json.text(isLogout ? "Stopped service and logout" : "Service stopped").setStyle(YouTubeChat.json.green()));
    }

    @Override
    public boolean isInitialized()
    {
        return this.isInitialized;
    }

    @Override
    public void subscribe(YouTubeChatMessageListener listener)
    {
        if (!this.listeners.contains(listener))
        {
            this.listeners.add(listener);
            ModLogger.printYTMessage(YouTubeChat.json.text("Started receiving live chat message").setStyle(YouTubeChat.json.white()));

            if (this.isInitialized && this.pollTimer == null)
            {
                this.poll(Math.max(0, this.nextPoll - System.currentTimeMillis()));
            }
        }
    }

    @Override
    public void unsubscribe(YouTubeChatMessageListener listener)
    {
        if (this.listeners.contains(listener))
        {
            this.listeners.remove(listener);
            ModLogger.printYTMessage(YouTubeChat.json.text("Stopped receiving live chat message").setStyle(YouTubeChat.json.white()));

            if (this.listeners.size() == 0)
            {
                this.stopPolling();
            }
        }
    }

    /**
     * Posts a live chat message and notifies the caller of the message Id posted.
     */
    @Override
    public void postMessage(final String message, final Consumer<String> onComplete)
    {
        if (!this.isInitialized)
        {
            onComplete.accept(null);
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                LiveChatMessage liveChatMessage = new LiveChatMessage();
                LiveChatMessageSnippet snippet = new LiveChatMessageSnippet();
                snippet.setType("textMessageEvent");
                snippet.setLiveChatId(this.liveChatId);
                LiveChatTextMessageDetails details = new LiveChatTextMessageDetails();
                details.setMessageText(message);
                snippet.setTextMessageDetails(details);
                liveChatMessage.setSnippet(snippet);
                YouTube.LiveChatMessages.Insert liveChatInsert = this.youtube.liveChatMessages().insert("snippet", liveChatMessage);
                LiveChatMessage response = liveChatInsert.execute();
                onComplete.accept(response.getId());
            }
            catch (Throwable t)
            {
                onComplete.accept(null);
                ModLogger.printExceptionMessage(t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void deleteMessage(final String messageId, final Runnable onComplete)
    {
        if (messageId == null || messageId.isEmpty())
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                YouTube.LiveChatMessages.Delete liveChatDelete = this.youtube.liveChatMessages().delete(messageId);
                liveChatDelete.execute();
                onComplete.run();
            }
            catch (Throwable t)
            {
                ModLogger.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    @Override
    public void banUser(final String channelId, final Runnable onComplete, final boolean temporary)
    {
        if (channelId == null || channelId.isEmpty())
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                LiveChatBan liveChatBan = new LiveChatBan();
                LiveChatBanSnippet snippet = new LiveChatBanSnippet();
                ChannelProfileDetails details = new ChannelProfileDetails();
                snippet.setType(temporary ? "temporary" : "permanent");
                snippet.setLiveChatId(this.liveChatId);
                snippet.setBanDurationSeconds(BigInteger.valueOf(300));
                details.setChannelId(channelId);
                snippet.setBannedUserDetails(details);
                liveChatBan.setSnippet(snippet);
                YouTube.LiveChatBans.Insert liveChatBanInsert = this.youtube.liveChatBans().insert("snippet", liveChatBan);
                liveChatBanInsert.execute();
                onComplete.run();
            }
            catch (Throwable t)
            {
                ModLogger.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    @Override
    public void addModerator(String channelId, Runnable onComplete)
    {
        if (channelId == null || channelId.isEmpty())
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                LiveChatModerator liveChatMod = new LiveChatModerator();
                LiveChatModeratorSnippet snippet = new LiveChatModeratorSnippet();
                ChannelProfileDetails details = new ChannelProfileDetails();
                snippet.setLiveChatId(this.liveChatId);
                details.setChannelId(channelId);
                snippet.setModeratorDetails(details);
                liveChatMod.setSnippet(snippet);
                YouTube.LiveChatModerators.Insert liveChatModInsert = this.youtube.liveChatModerators().insert("snippet", liveChatMod);
                liveChatModInsert.execute();
                onComplete.run();
            }
            catch (Throwable t)
            {
                ModLogger.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    private void poll(long delay)
    {
        this.pollTimer = new Timer();
        this.pollTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    // Check if game is paused
                    Minecraft mc = Minecraft.getMinecraft();

                    if (mc.isGamePaused())
                    {
                        ChatService.this.poll(200);
                        return;
                    }

                    // Get chat messages from YouTube
                    LiveChatMessageListResponse response = ChatService.this.youtube.liveChatMessages().list(ChatService.this.liveChatId, "snippet, authorDetails").setPageToken(ChatService.this.nextPageToken).setFields(LIVE_CHAT_FIELDS).execute();
                    ChatService.this.nextPageToken = response.getNextPageToken();
                    final List<LiveChatMessage> messages = response.getItems();

                    // Broadcast message to listeners on main thread
                    for (int i = 0; i < messages.size(); i++)
                    {
                        LiveChatMessage message = messages.get(i);
                        LiveChatMessageSnippet snippet = message.getSnippet();
                        ChatService.this.broadcastMessage(message.getAuthorDetails(), snippet.getSuperChatDetails(), message.getId(), snippet.getDisplayMessage());
                    }
                    ChatService.this.poll(response.getPollingIntervalMillis());
                }
                catch (Throwable t)
                {
                    ModLogger.printExceptionMessage(t.getMessage());
                    t.printStackTrace();
                }
            }
        }, delay);
    }

    void broadcastMessage(LiveChatMessageAuthorDetails author, LiveChatSuperChatDetails details, String id, String message)
    {
        for (YouTubeChatMessageListener listener : new ArrayList<>(this.listeners))
        {
            listener.onMessageReceived(author, details, id, message);
        }
    }

    private void stopPolling()
    {
        if (this.pollTimer != null)
        {
            this.pollTimer.cancel();
            this.pollTimer = null;
        }
    }
}