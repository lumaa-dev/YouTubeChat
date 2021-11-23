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

package com.stevekung.ytc.forge.config;

import java.util.Collections;
import java.util.List;

import com.stevekung.ytc.core.YouTubeChat;
import com.stevekung.ytc.utils.RudeWordAction;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class YouTubeChatConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final YouTubeChatConfig.General GENERAL = new YouTubeChatConfig.General(YouTubeChatConfig.BUILDER);
    public static final YouTubeChatConfig.Chat CHAT = new YouTubeChatConfig.Chat(YouTubeChatConfig.BUILDER);

    static
    {
        SPEC = BUILDER.build();
    }

    public static class General
    {
        public final ForgeConfigSpec.ConfigValue<String> clientSecret;
        public final ForgeConfigSpec.BooleanValue enableVersionChecker;

        General(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General settings").push("general");

            this.clientSecret = builder.comment("The client secret from Google API console").translation("text.autoconfig.youtube_chat.option.general.clientSecret").define("clientSecret", "");

            this.enableVersionChecker = builder.translation("text.autoconfig.youtube_chat.option.general.enableVersionChecker").define("enableVersionChecker", true);

            builder.pop();
        }
    }

    public static class Chat
    {
        public final ForgeConfigSpec.ConfigValue<String> ownerIcon;
        public final ForgeConfigSpec.ConfigValue<String> moderatorIcon;
        public final ForgeConfigSpec.ConfigValue<List<String>> bannedRudeWords;
        public final ForgeConfigSpec.ConfigValue<List<String>> rudeWords;
        public final ForgeConfigSpec.EnumValue<RudeWordAction> rudeWordAction;

        Chat(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Chat settings").push("chat");

            this.ownerIcon = builder.comment("Display unicode in front of Owner").translation("text.autoconfig.youtube_chat.option.chat.ownerIcon").define("ownerIcon", "");

            this.moderatorIcon = builder.comment("Display unicode in front of Moderators").translation("text.autoconfig.youtube_chat.option.chat.moderatorIcon").define("moderatorIcon", "");

            this.rudeWords = builder.comment("List of rude words, this will be automatically do a selected action (delete or timeout) to the users").translation("text.autoconfig.youtube_chat.option.chat.rudeWords").define("rudeWords", Collections.emptyList());

            this.bannedRudeWords = builder.comment("List of rude words, this will be automatically ban user when message is received").translation("text.autoconfig.youtube_chat.option.chat.bannedRudeWords").define("bannedRudeWords", Collections.emptyList());

            this.rudeWordAction = builder.comment("Select an action to do with rude words").translation("text.autoconfig.youtube_chat.option.chat.rudeWordAction").defineEnum("rudeWordAction", RudeWordAction.DELETE);

            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event)
    {
        YouTubeChat.LOGGER.info("Loaded config file {}", event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(ModConfigEvent.Reloading event)
    {
        YouTubeChat.LOGGER.info("YouTube Chat config just got changed on the file system");
    }
}