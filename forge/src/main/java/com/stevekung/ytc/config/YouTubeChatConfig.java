package com.stevekung.ytc.config;

import java.util.Collections;
import java.util.List;

import com.stevekung.ytc.core.YouTubeChatMod;
import com.stevekung.ytc.utils.RudeWordAction;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public class YouTubeChatConfig
{
    public static final ForgeConfigSpec.Builder GENERAL_BUILDER = new ForgeConfigSpec.Builder();
    public static final YouTubeChatConfig.General GENERAL = new YouTubeChatConfig.General(YouTubeChatConfig.GENERAL_BUILDER);
    public static final YouTubeChatConfig.Chat CHAT = new YouTubeChatConfig.Chat(YouTubeChatConfig.GENERAL_BUILDER);

    public static class General
    {
        // General
        public final ForgeConfigSpec.ConfigValue<String> clientSecret;
        public final ForgeConfigSpec.BooleanValue enableVersionChecker;

        General(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General settings").push("general");

            this.clientSecret = builder.comment("The client secret from Google API console").translation("youtube_chat.configgui.client_secret").define("clientSecret", "");

            this.enableVersionChecker = builder.translation("youtube_chat.configgui.enable_version_checker").define("enableVersionChecker", true);

            builder.pop();
        }
    }

    public static class Chat
    {
        // General
        public final ForgeConfigSpec.ConfigValue<String> ownerIcon;
        public final ForgeConfigSpec.ConfigValue<String> moderatorIcon;
        public final ForgeConfigSpec.ConfigValue<List<String>> bannedRudeWords;
        public final ForgeConfigSpec.ConfigValue<List<String>> rudeWords;
        public final ForgeConfigSpec.EnumValue<RudeWordAction> rudeWordAction;

        Chat(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Chat settings").push("chat");

            this.ownerIcon = builder.comment("Display unicode in front of Owner").translation("youtube_chat.configgui.owner_icon").define("ownerIcon", "");

            this.moderatorIcon = builder.comment("Display unicode in front of Moderators").translation("youtube_chat.configgui.moderator_icon").define("moderatorIcon", "");

            this.rudeWords = builder.comment("List of rude words, this will be automatically do a selected action (delete or timeout) to the users").translation("youtube_chat.configgui.rude_words").define("rudeWords", Collections.emptyList());

            this.bannedRudeWords = builder.comment("List of rude words, this will be automatically ban user when message is received").translation("youtube_chat.configgui.banned_rude_words").define("bannedRudeWords", Collections.emptyList());

            this.rudeWordAction = builder.comment("Select an action to do with rude words").translation("youtube_chat.configgui.rude_word_action").defineEnum("rudeWordAction", RudeWordAction.DELETE);

            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event)
    {
        YouTubeChatMod.LOGGER.info("Loaded config file {}", event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event)
    {
        YouTubeChatMod.LOGGER.info("YouTube Chat config just got changed on the file system");
    }
}