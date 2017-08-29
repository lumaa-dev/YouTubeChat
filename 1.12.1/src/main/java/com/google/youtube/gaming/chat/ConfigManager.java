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

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * Configuration settings for YouTube Chat.
 */
public class ConfigManager
{
    private static ConfigManager instance;
    private static Configuration config;
    private String clientSecret;
    private String videoId;
    private String ownerUnicodeIcon;
    private String moderatorUnicodeIcon;
    private boolean superOnly;
    private boolean streamChatRightSide;
    private boolean autoReceiveChat;

    public static void initialize(File path)
    {
        instance = new ConfigManager(path);
    }

    public static ConfigManager getInstance()
    {
        return instance;
    }

    private ConfigManager(File path)
    {
        config = new Configuration(path);
        config.load();
        this.addGeneralConfig();

        if (config.hasChanged())
        {
            config.save();
        }
    }

    private void addGeneralConfig()
    {
        this.clientSecret = config.get(Configuration.CATEGORY_GENERAL, "Client Secret", "", "The client secret from Google API console").getString();
        this.videoId = config.get(Configuration.CATEGORY_GENERAL, "Video ID", "", "The id of the live video").getString();
        this.superOnly = config.get(Configuration.CATEGORY_GENERAL, "Show Super Chat Only", false, "Receive super chats only").getBoolean();
        this.ownerUnicodeIcon = config.get(Configuration.CATEGORY_GENERAL, "Channel Owner Icon (Unicode)", "", "Put your fancy unicode, it will display in front of your channel").getString();
        this.moderatorUnicodeIcon = config.get(Configuration.CATEGORY_GENERAL, "Moderator Icon (Unicode)", "", "Put your fancy unicode, it will display in front of moderator channel").getString();
        this.streamChatRightSide = config.get(Configuration.CATEGORY_GENERAL, "Display Stream Chat on Right Side", false, "Move Stream Chat into Right Side of the screen").getBoolean();
        this.autoReceiveChat = config.get(Configuration.CATEGORY_GENERAL, "Automatic Receive Chat", false, "When you starting service, This will automatically subscribe stream chat service").getBoolean();
    }

    public void reset()
    {
        if (config != null)
        {
            config.save();
        }
        this.addGeneralConfig();
    }

    public Configuration getConfig()
    {
        return config;
    }

    public String getVideoId()
    {
        return this.videoId;
    }

    public String getClientSecret()
    {
        return this.clientSecret;
    }

    public boolean getSuperOnly()
    {
        return this.superOnly;
    }

    public String getOwnerUnicode()
    {
        return this.ownerUnicodeIcon;
    }

    public String getModeratorUnicode()
    {
        return this.moderatorUnicodeIcon;
    }

    public boolean getRightSideChat()
    {
        return this.streamChatRightSide;
    }

    public boolean getAutoReceiveChat()
    {
        return this.autoReceiveChat;
    }
}