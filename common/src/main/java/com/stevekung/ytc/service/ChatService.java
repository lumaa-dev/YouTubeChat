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

import java.util.function.Consumer;

import com.stevekung.ytc.utils.event.ChatReceivedEvent;

/**
 * An interface for other mods to subscribe to the live chat stream, post messages, or delete
 * messages.
 */
public interface ChatService
{
    /**
     * Notifies subscribers of chat message details when a message is received.
     */
    interface Listener
    {
        void onChatReceived(ChatReceivedEvent event);
    }

    /**
     * Subscribes to the live chat stream. Only safe to call on the Minecraft main thread.
     */
    void subscribe();

    /**
     * Unsubscribes from the live chat stream. Only safe to call on the Minecraft main thread.
     */
    void unsubscribe();

    /**
     * Posts a message to the live chat stream.
     *
     * @param message The message to post.
     * @param onComplete Callback when the operation is complete, which is passed the id of the posted
     *     message.
     */
    void postMessage(String message, Consumer<String> onComplete);

    /**
     * Deletes a message from the live chat stream.
     *
     * @param messageId The message id to delete.
     * @param onComplete Callback when the operation is complete.
     */
    void deleteMessage(String messageId, Runnable onComplete);

    /**
     * Bans a user from the live chat stream.
     *
     * @param channelId The channel id to ban.
     * @param onComplete Callback when the operation is complete.
     * @param temporary Temporary ban user from chat (5 min). If not user will permanently ban from channel.
     */
    void banUser(String channelId, Runnable onComplete, boolean temporary);

    /**
     * Adds moderator from the live chat stream.
     *
     * @param channelId The channel id to add.
     * @param onComplete Callback when the operation is complete.
     */
    void addModerator(String channelId, Runnable onComplete);

    void unbanUser(String channelId, Runnable onComplete);//TODO Fix this

    /**
     * Removes moderator from channel.
     *
     * @param moderatorId The moderator id to remove.
     * @param onComplete Callback when the operation is complete.
     */
    void removeModerator(String moderatorId, Runnable onComplete);
}