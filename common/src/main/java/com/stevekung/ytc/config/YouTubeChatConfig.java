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

package com.stevekung.ytc.config;

import java.util.Collections;
import java.util.List;

import com.stevekung.ytc.core.YouTubeChat;
import com.stevekung.ytc.utils.RudeWordAction;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = YouTubeChat.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/red_concrete.png")
public final class YouTubeChatConfig implements ConfigData
{
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public GeneralCategory general;

    @ConfigEntry.Category("chat")
    @ConfigEntry.Gui.TransitiveObject
    public ChatCategory chat;

    public YouTubeChatConfig()
    {
        this.general = new GeneralCategory();
        this.chat = new ChatCategory();
    }

    public static class GeneralCategory
    {
        @Comment("The client secret from Google API console.")
        public String clientSecret = "";
    }

    public static class ChatCategory
    {
        @Comment("Display unicode in front of Owner name.")
        public String ownerIcon = "";
        @Comment("Display unicode in front of Moderators name.")
        public String moderatorIcon = "";
        @Comment("List of rude words, this will be automatically do a selected action (delete or timeout) to the users.")
        public List<String> rudeWords = Collections.emptyList();
        @Comment("List of rude words, this will be automatically ban user when message is received.")
        public List<String> bannedRudeWords = Collections.emptyList();
        @Comment("Select an action to do with rude words.")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public RudeWordAction rudeWordAction = RudeWordAction.DELETE;
    }
}