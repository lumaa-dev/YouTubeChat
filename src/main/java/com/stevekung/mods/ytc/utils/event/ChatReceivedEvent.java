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

package com.stevekung.mods.ytc.utils.event;

import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;

public class ChatReceivedEvent
{
    private final LiveChatMessageAuthorDetails author;
    private final LiveChatSuperChatDetails superChatDetails;
    private final String messageId;
    private final String message;
    private final String moderatorId;

    public ChatReceivedEvent(LiveChatMessageAuthorDetails author, LiveChatSuperChatDetails superChatDetails, String messageId, String message, String moderatorId)
    {
        this.author = author;
        this.superChatDetails = superChatDetails;
        this.messageId = messageId;
        this.message = message;
        this.moderatorId = moderatorId;
    }

    public LiveChatMessageAuthorDetails getAuthor()
    {
        return this.author;
    }

    public LiveChatSuperChatDetails getSuperChatDetails()
    {
        return this.superChatDetails;
    }

    public String getMessageId()
    {
        return this.messageId;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getModeratorId()
    {
        return this.moderatorId;
    }
}