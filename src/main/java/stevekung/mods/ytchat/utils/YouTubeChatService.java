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
import com.google.common.base.Strings;

import net.minecraft.client.Minecraft;
import stevekung.mods.stevekunglib.utils.JsonUtils;
import stevekung.mods.ytchat.auth.Authentication;
import stevekung.mods.ytchat.core.YouTubeChatMod;

/**
 * Manages connection to the YouTube chat service, posting chat messages, deleting chat messages,
 * polling for chat messages and notifying subscribers.
 */
public class YouTubeChatService implements AbstractChatService
{
    private static final String LIVE_CHAT_FIELDS = "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,isVerified),snippet(displayMessage,superChatDetails),id),nextPageToken,pollingIntervalMillis";
    private ExecutorService executor;
    private YouTube youtube;
    private String liveChatId;
    private boolean isInitialized;
    private final List<YouTubeChatMessageListener> listeners = new ArrayList<>();
    private String nextPageToken;
    private Timer pollTimer;
    private long nextPoll;
    private static YouTubeChatService INSTANCE;
    public static String channelOwnerId = "";
    public static String currentLoginProfile = "";

    public static synchronized YouTubeChatService getService()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new YouTubeChatService();
        }
        return INSTANCE;
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
            LoggerYT.printYTMessage(JsonUtils.create("Started receiving live chat message").setStyle(JsonUtils.white()));

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
            LoggerYT.printYTMessage(JsonUtils.create("Stopped receiving live chat message").setStyle(JsonUtils.white()));

            if (this.listeners.size() == 0)
            {
                this.stopPolling();
            }
        }
    }

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
                LoggerYT.printExceptionMessage(t.getMessage());
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
                LoggerYT.printExceptionMessage(t.getMessage());
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
                LoggerYT.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    @Override
    public void addModerator(final String channelId, final Runnable onComplete)
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
                LoggerYT.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    @Override
    public void unbanUser(final String id, final Runnable onComplete)//TODO Fix this
    {
        if (id == null || id.isEmpty())
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                YouTube.LiveChatBans.Delete liveChatBanDelete = this.youtube.liveChatBans().delete(id);
                liveChatBanDelete.setId(id);
                liveChatBanDelete.execute();
                onComplete.run();
            }
            catch (Throwable t)
            {
                LoggerYT.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    @Override
    public void removeModerator(final String moderatorId, final Runnable onComplete)
    {
        if (moderatorId == null || moderatorId.isEmpty())
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                YouTube.LiveChatModerators.Delete liveChatModDelete = this.youtube.liveChatModerators().delete(moderatorId);
                liveChatModDelete.setId(moderatorId);
                liveChatModDelete.execute();
                onComplete.run();
            }
            catch (Throwable t)
            {
                LoggerYT.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    public void start(final String videoId, final String clientSecret, final String defaultAuthName)
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
                String fileName = Strings.isNullOrEmpty(defaultAuthName) ? YouTubeChatMod.MOD_ID : defaultAuthName;
                Credential credential = Authentication.authorize(scopes, clientSecret, fileName);
                YouTubeChatService.currentLoginProfile = fileName;

                // This object is used to make YouTube Data API requests
                this.youtube = new YouTube.Builder(Authentication.HTTP_TRANSPORT, Authentication.JSON_FACTORY, credential).setApplicationName(YouTubeChatMod.NAME).build();

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
                            LoggerYT.info("Live Chat ID: {}", this.liveChatId);
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
                        YouTubeChatService.channelOwnerId = broadcast.getSnippet().getChannelId();

                        if (this.liveChatId != null && !this.liveChatId.isEmpty())
                        {
                            LoggerYT.info("Live Chat ID: {}", this.liveChatId);
                            break;
                        }
                    }
                }

                if (this.liveChatId == null || this.liveChatId.isEmpty())
                {
                    LoggerYT.printExceptionMessage("Could not find live chat for " + identity);
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
                LoggerYT.printYTMessage(JsonUtils.create("Service started").setStyle(JsonUtils.green()));
            }
            catch (Throwable t)
            {
                LoggerYT.printExceptionMessage(t.getMessage());
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
        LoggerYT.printYTMessage(JsonUtils.create(isLogout ? "Stopped service and logout" : "Service stopped").setStyle(JsonUtils.green()));
    }

    public ExecutorService getExecutor()
    {
        return this.executor;
    }

    public List<YouTubeChatMessageListener> getListeners()
    {
        return this.listeners;
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
                        YouTubeChatService.this.poll(200);
                        return;
                    }

                    // Get chat messages from YouTube
                    LiveChatMessageListResponse chatResponse = YouTubeChatService.this.youtube.liveChatMessages().list(YouTubeChatService.this.liveChatId, "snippet, authorDetails").setPageToken(YouTubeChatService.this.nextPageToken).setFields(LIVE_CHAT_FIELDS).execute();
                    // Get moderators list from YouTube
                    LiveChatModeratorListResponse moderatorResponse = YouTubeChatService.this.youtube.liveChatModerators().list(YouTubeChatService.this.liveChatId, "snippet, id").setFields("items(id,snippet(moderatorDetails(channelId)))").execute();
                    YouTubeChatService.this.nextPageToken = chatResponse.getNextPageToken();

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
                        YouTubeChatService.this.broadcastMessage(message.getAuthorDetails(), snippet.getSuperChatDetails(), message.getId(), snippet.getDisplayMessage(), moderatorId);
                    }
                    YouTubeChatService.this.poll(chatResponse.getPollingIntervalMillis());
                }
                catch (Throwable t)
                {
                    LoggerYT.printExceptionMessage(t.getMessage());
                    t.printStackTrace();
                }
            }
        }, delay);
    }

    private void broadcastMessage(LiveChatMessageAuthorDetails author, LiveChatSuperChatDetails details, String id, String message, String moderatorId)
    {
        for (YouTubeChatMessageListener listener : this.listeners)
        {
            listener.onMessageReceived(author, details, id, message, moderatorId);
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