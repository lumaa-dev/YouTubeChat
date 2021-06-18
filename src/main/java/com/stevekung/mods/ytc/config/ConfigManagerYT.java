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

package com.stevekung.mods.ytc.config;

import com.stevekung.mods.ytc.core.YouTubeChatMod;

import net.minecraftforge.common.config.Config;

@Config(modid = YouTubeChatMod.MOD_ID)
public class ConfigManagerYT
{
    @Config.LangKey(value = "youtube_chat_general")
    public static final General YOUTUBE_CHAT_GENERAL = new General();

    @Config.LangKey(value = "youtube_chat_chat")
    public static final Chat YOUTUBE_CHAT_CHAT = new Chat();

    // Main Settings
    public static class General
    {
        @Config.Comment("The client secret from Google API console.")
        @Config.Name(value = "Client Secret")
        public String clientSecret = "";

        @Config.Name(value = "Enable Version Checker")
        public boolean enableVersionChecker = true;
    }

    // Chat Settings
    public static class Chat
    {
        @Config.Comment("Put your fancy unicode, it will display in front of your channel name")
        @Config.Name(value = "Channel Owner Icon")
        public String ownerIcon = "";

        @Config.Comment("Put your fancy unicode, it will display in front of moderator channel")
        @Config.Name(value = "Moderator Icon")
        public String moderatorIcon = "";

        @Config.Comment("Put the list of rude word, this will be automatically ban user when message is received. split by \",\"")
        @Config.Name(value = "Banned Rude Word List")
        public String bannedRudeWordList = "";

        @Config.Comment("Put the list of rude word, this will be automatically do an action with message when received. split by \",\"")
        @Config.Name(value = "Rude Word List")
        public String rudeWordList = "";

        @Config.Comment("If the message contain rude word, this will does an action with selected action")
        @Config.Name(value = "Rude Word Action")
        public RudeWordAction rudeWordAction = RudeWordAction.DELETE;

        public enum RudeWordAction
        {
            DELETE, TEMPORARY_BAN;
        }
    }
}