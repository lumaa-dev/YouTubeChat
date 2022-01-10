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

package com.stevekung.ytc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.Collections;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stevekung.ytc.core.YouTubeChat;
import com.stevekung.ytc.utils.*;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;

/**
 * Manages connection to the YouTube chat service, posting chat messages, deleting chat messages,
 * polling for chat messages and notifying subscribers.
 */
public class YouTubeChatService implements ChatService
{
    public static final String LIVE_CHAT_FIELDS = "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,isVerified),snippet(displayMessage,superChatDetails),id),nextPageToken,pollingIntervalMillis";
    public static boolean receiveChat;
    private static final Gson GSON = new GsonBuilder().registerTypeAdapterFactory(new RecordTypeAdapterFactory()).create();
    private ExecutorService executor;
    private YouTube youtube;
    private String liveChatId;
    private boolean initialized;
    private boolean failed;
    private Listener listener;
    private String nextPageToken;
    private Timer pollTimer;
    private long nextPoll;
    private static YouTubeChatService INSTANCE;
    public static String ownerChannelId = "";
    public static String currentLoginProfile = "";
    public static String liveVideoId = "";
    public static String currentLiveViewCount = "";

    public static synchronized YouTubeChatService getService()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new YouTubeChatService();
        }
        return INSTANCE;
    }

    @Override
    public void subscribe()
    {
        if (this.getListener() == null)
        {
            this.listener = YouTubeChatReceiver.INSTANCE;

            if (this.initialized && this.pollTimer == null)
            {
                this.poll(Math.max(0, this.nextPoll - System.currentTimeMillis()));
            }
        }
    }

    @Override
    public void unsubscribe()
    {
        if (this.getListener() != null)
        {
            this.listener = null;
            this.stopPolling();
        }
    }

    @Override
    public void postMessage(String message, Consumer<String> onComplete)
    {
        if (!this.initialized)
        {
            onComplete.accept(null);
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                var liveChatMessage = new LiveChatMessage();
                var snippet = new LiveChatMessageSnippet();
                snippet.setType("textMessageEvent");
                snippet.setLiveChatId(this.getLiveChatId());
                var details = new LiveChatTextMessageDetails();
                details.setMessageText(message);
                snippet.setTextMessageDetails(details);
                liveChatMessage.setSnippet(snippet);
                var liveChatInsert = this.getYoutube().liveChatMessages().insert(Collections.singletonList("snippet"), liveChatMessage);
                var response = liveChatInsert.execute();
                onComplete.accept(response.getId());
            }
            catch (GoogleJsonResponseException e)
            {
                this.printGoogleJsonException(e);
            }
            catch (Throwable t)
            {
                onComplete.accept(null);
                ChatUtils.printExceptionMessage(t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void deleteMessage(String messageId, Runnable onComplete)
    {
        if (StringUtil.isNullOrEmpty(messageId))
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                var liveChatDelete = this.getYoutube().liveChatMessages().delete(messageId);
                liveChatDelete.execute();
                onComplete.run();
            }
            catch (GoogleJsonResponseException e)
            {
                this.printGoogleJsonException(e);
            }
            catch (Throwable t)
            {
                ChatUtils.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    @Override
    public void banUser(String channelId, Runnable onComplete, boolean temporary)
    {
        if (StringUtil.isNullOrEmpty(channelId))
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                var liveChatBan = new LiveChatBan();
                var snippet = new LiveChatBanSnippet();
                var details = new ChannelProfileDetails();
                snippet.setType(temporary ? "temporary" : "permanent");
                snippet.setLiveChatId(this.getLiveChatId());
                snippet.setBanDurationSeconds(BigInteger.valueOf(300));
                details.setChannelId(channelId);
                snippet.setBannedUserDetails(details);
                liveChatBan.setSnippet(snippet);
                var liveChatBanInsert = this.getYoutube().liveChatBans().insert(Collections.singletonList("snippet"), liveChatBan);
                liveChatBanInsert.execute();
                onComplete.run();
            }
            catch (GoogleJsonResponseException e)
            {
                this.printGoogleJsonException(e);
            }
            catch (Throwable t)
            {
                ChatUtils.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    @Override
    public void addModerator(String channelId, Runnable onComplete)
    {
        if (StringUtil.isNullOrEmpty(channelId))
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                var liveChatMod = new LiveChatModerator();
                var snippet = new LiveChatModeratorSnippet();
                var details = new ChannelProfileDetails();
                snippet.setLiveChatId(this.getLiveChatId());
                details.setChannelId(channelId);
                snippet.setModeratorDetails(details);
                liveChatMod.setSnippet(snippet);
                var liveChatModInsert = this.getYoutube().liveChatModerators().insert(Collections.singletonList("snippet"), liveChatMod);
                liveChatModInsert.execute();
                onComplete.run();
            }
            catch (GoogleJsonResponseException e)
            {
                this.printGoogleJsonException(e);
            }
            catch (Throwable t)
            {
                ChatUtils.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    @Override
    public void unbanUser(String id, Runnable onComplete)//TODO Fix this
    {
        if (StringUtil.isNullOrEmpty(id))
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                var liveChatBanDelete = this.getYoutube().liveChatBans().delete(id);
                liveChatBanDelete.setId(id);
                liveChatBanDelete.execute();
                onComplete.run();
            }
            catch (GoogleJsonResponseException e)
            {
                this.printGoogleJsonException(e);
            }
            catch (Throwable t)
            {
                ChatUtils.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    @Override
    public void removeModerator(String moderatorId, Runnable onComplete)
    {
        if (StringUtil.isNullOrEmpty(moderatorId))
        {
            onComplete.run();
            return;
        }

        this.executor.execute(() ->
        {
            try
            {
                var liveChatModDelete = this.getYoutube().liveChatModerators().delete(moderatorId);
                liveChatModDelete.setId(moderatorId);
                liveChatModDelete.execute();
                onComplete.run();
            }
            catch (GoogleJsonResponseException e)
            {
                this.printGoogleJsonException(e);
            }
            catch (Throwable t)
            {
                ChatUtils.printExceptionMessage(t.getMessage());
                t.printStackTrace();
                onComplete.run();
            }
        });
    }

    public void start(String clientSecret, String defaultAuthName)
    {
        ChatUtils.printChatMessage(new TranslatableComponent("message.service_start"));
        this.executor = Executors.newCachedThreadPool();
        this.executor.execute(() ->
        {
            try
            {
                // Authorize the request
                var fileName = Strings.isNullOrEmpty(defaultAuthName) ? YouTubeChat.MOD_ID : defaultAuthName;
                // Build auth scopes
                var credential = AuthService.authorize(Lists.newArrayList(YouTubeScopes.YOUTUBE_FORCE_SSL, YouTubeScopes.YOUTUBE), clientSecret, fileName);
                YouTubeChatService.currentLoginProfile = fileName;

                // This object is used to make YouTube Data API requests
                this.youtube = new YouTube.Builder(AuthService.HTTP_TRANSPORT, AuthService.GSON_FACTORY, credential).setApplicationName(YouTubeChat.NAME).build();

                var broadcastList = this.getYoutube().liveBroadcasts().list(Collections.singletonList("snippet")).setFields("items/snippet/liveChatId,items/snippet/channelId").setBroadcastType("all").setBroadcastStatus("active");

                for (var broadcast : broadcastList.execute().getItems())
                {
                    this.liveChatId = broadcast.getSnippet().getLiveChatId();
                    YouTubeChatService.ownerChannelId = broadcast.getSnippet().getChannelId();

                    if (!StringUtil.isNullOrEmpty(this.getLiveChatId()))
                    {
                        YouTubeChat.LOGGER.info("Live Chat ID: {}", this.getLiveChatId());
                        break;
                    }
                }

                if (StringUtil.isNullOrEmpty(this.getLiveChatId()))
                {
                    ChatUtils.printExceptionMessage("Couldn't find live stream on current channel");
                    this.stop(false);
                    return;
                }

                // Initialize next page token
                var response = this.getYoutube().liveChatMessages().list(this.getLiveChatId(), Collections.singletonList("snippet")).setFields("nextPageToken, pollingIntervalMillis").execute();
                this.setNextPageToken(response.getNextPageToken());
                this.initialized = true;

                if (this.pollTimer == null && this.getListener() != null)
                {
                    this.poll(response.getPollingIntervalMillis());
                }
                else
                {
                    this.nextPoll = System.currentTimeMillis() + response.getPollingIntervalMillis();
                }
            }
            catch (GoogleJsonResponseException e)
            {
                this.printGoogleJsonException(e);
                this.failed = true;
                this.stop(false);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    public void stop(boolean logout)
    {
        this.stopPolling();

        if (this.hasExecutor())
        {
            this.executor.shutdown();
            this.executor = null;
        }
        this.liveChatId = null;
        this.initialized = false;

        if (this.failed)
        {
            ChatUtils.printChatMessage(new TranslatableComponent("message.service_stop_fail"));
        }
        else
        {
            ChatUtils.printChatMessage(new TranslatableComponent(logout ? "message.service_stop_logout" : "message.service_stop"));
        }
    }

    public boolean hasExecutor()
    {
        return this.executor != null;
    }

    public Listener getListener()
    {
        return this.listener;
    }

    public void getCurrentViewCount()
    {
        try
        {
            var url = new URL("https://www.youtube.com/live_stats?v=" + YouTubeChatService.liveVideoId);
            var reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
            String view;

            while ((view = reader.readLine()) != null)
            {
                YouTubeChatService.currentLiveViewCount = view;
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public YouTube getYoutube()
    {
        return this.youtube;
    }

    public String getLiveChatId()
    {
        return this.liveChatId;
    }

    public String getNextPageToken()
    {
        return this.nextPageToken;
    }

    public void setNextPageToken(String nextPageToken)
    {
        this.nextPageToken = nextPageToken;
    }

    public void poll(long delay)
    {
        this.pollTimer = new Timer();
        this.pollTimer.schedule(new PollingTask(), delay);
    }

    private void stopPolling()
    {
        if (this.pollTimer != null)
        {
            this.pollTimer.cancel();
            this.pollTimer = null;
        }
    }

    private void printGoogleJsonException(GoogleJsonResponseException e)
    {
        try
        {
            var googleEx = GSON.fromJson(e.getDetails().toPrettyString(), GoogleJsonException.class);
            ChatUtils.printExceptionMessage("Error Code: " + googleEx.errorCode());
            ChatUtils.printExceptionMessage("Message: " + googleEx.message());
        }
        catch (IOException ignored) {}
        e.printStackTrace();
    }
}