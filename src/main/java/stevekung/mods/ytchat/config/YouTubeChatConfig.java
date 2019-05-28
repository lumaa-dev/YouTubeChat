package stevekung.mods.ytchat.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import stevekung.mods.ytchat.core.YouTubeChatMod;

public class YouTubeChatConfig
{
    public static final ForgeConfigSpec.Builder GENERAL_BUILDER = new ForgeConfigSpec.Builder();
    public static final YouTubeChatConfig.General GENERAL = new YouTubeChatConfig.General(YouTubeChatConfig.GENERAL_BUILDER);

    public static class General
    {
        // General
        public final ForgeConfigSpec.ConfigValue<String> clientSecret;
        public final ForgeConfigSpec.BooleanValue enableVersionChecker;

        // Chat
        public final ForgeConfigSpec.ConfigValue<String> ownerUnicodeIcon;
        public final ForgeConfigSpec.ConfigValue<String> moderatorUnicodeIcon;
        public final ForgeConfigSpec.ConfigValue<String> banRudeWordList;
        public final ForgeConfigSpec.ConfigValue<String> rudeWordList;
        public final ForgeConfigSpec.ConfigValue<String> rudeWordAction;
        public final ForgeConfigSpec.BooleanValue showSuperChatOnly;
        public final ForgeConfigSpec.BooleanValue displayChatRightSide;

        General(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General settings")
            .push("general");

            this.clientSecret = builder
                    .comment("The client secret from Google API console.")
                    .translation("youtube_chat.configgui.client_secret")
                    .define("clientSecret", "");

            this.enableVersionChecker = builder
                    .comment("")
                    .translation("youtube_chat.configgui.enable_version_checker")
                    .define("enableVersionChecker", true);

            builder.pop();

            builder.comment("Chat settings")
            .push("chat");

            this.ownerUnicodeIcon = builder
                    .comment("Put your fancy unicode, it will display in front of your channel name")
                    .translation("youtube_chat.configgui.owner_unicode_icon")
                    .define("ownerUnicodeIcon", "");

            this.moderatorUnicodeIcon = builder
                    .comment("Put your fancy unicode, it will display in front of moderator channel")
                    .translation("youtube_chat.configgui.moderator_unicode_icon")
                    .define("moderatorUnicodeIcon", "");

            this.banRudeWordList = builder
                    .comment("Put the list of rude word, this will be automatically ban user when message is received. split by \",\"")
                    .translation("youtube_chat.configgui.ban_rude_word_list")
                    .define("banRudeWordList", "");

            this.rudeWordList = builder
                    .comment("Put the list of rude word, this will be automatically do an action with message when received. split by \",\"")
                    .translation("youtube_chat.configgui.rude_word_list")
                    .define("rudeWordList", "");

            this.rudeWordAction = builder
                    .comment("If the message contain rude word, this will does an action with selected action")
                    .translation("youtube_chat.configgui.rude_word_action")
                    .define("rudeWordAction", RudeWordAction.DELETE.name());

            this.showSuperChatOnly = builder
                    .comment("Display super chats only")
                    .translation("youtube_chat.configgui.show_super_chat_only")
                    .define("showSuperChatOnly", false);

            this.displayChatRightSide = builder
                    .comment("Move only YouTube Chat into Right Side of the screen, render same as Chat")
                    .translation("youtube_chat.configgui.display_chat_right_side")
                    .define("displayChatRightSide", false);

            builder.pop();
        }
    }

    public enum RudeWordAction
    {
        DELETE, TEMPORARY_BAN
    }

    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event)
    {
        YouTubeChatMod.LOGGER.info("Loaded config file {}", event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.ConfigReloading event)
    {
        YouTubeChatMod.LOGGER.info("YouTube Chat config just got changed on the file system");
    }
}