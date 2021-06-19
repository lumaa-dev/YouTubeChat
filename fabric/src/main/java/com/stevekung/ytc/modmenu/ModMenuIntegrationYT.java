package com.stevekung.ytc.modmenu;

import java.io.IOException;
import java.util.Collections;

import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.ytc.config.YouTubeChatConfig;
import com.stevekung.ytc.core.YouTubeChatFabricMod;
import com.stevekung.ytc.utils.RudeWordAction;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuIntegrationYT implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen screen)
    {
        YouTubeChatConfig config = YouTubeChatFabricMod.CONFIG.getConfig();
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen).setTitle(LangUtils.translate("ui.youtube_chat.config.title"));
        builder.setSavingRunnable(() ->
        {
            try
            {
                YouTubeChatFabricMod.CONFIG.saveConfig();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        ConfigEntryBuilder entry = ConfigEntryBuilder.create();
        ConfigCategory generalCategory = builder.getOrCreateCategory(TextComponentUtils.component("General Settings"));
        generalCategory.addEntry(entry.startStrField(LangUtils.translate("youtube_chat.configgui.client_secret"), config.clientSecret).setTooltip(TextComponentUtils.component("The client secret from Google API console")).setSaveConsumer(value -> config.clientSecret = value).setDefaultValue("").build());
        generalCategory.addEntry(entry.startBooleanToggle(LangUtils.translate("youtube_chat.configgui.enable_version_checker"), config.enableVersionChecker).setSaveConsumer(value -> config.enableVersionChecker = value).setDefaultValue(true).build());

        ConfigCategory chatCategory = builder.getOrCreateCategory(TextComponentUtils.component("Chat Settings"));
        chatCategory.addEntry(entry.startStrField(LangUtils.translate("youtube_chat.configgui.owner_icon"), config.ownerIcon).setTooltip(TextComponentUtils.component("Display unicode in front of Owner")).setSaveConsumer(value -> config.ownerIcon = value).setDefaultValue("").build());
        chatCategory.addEntry(entry.startStrField(LangUtils.translate("youtube_chat.configgui.moderator_icon"), config.moderatorIcon).setTooltip(TextComponentUtils.component("Display unicode in front of Moderators")).setSaveConsumer(value -> config.moderatorIcon = value).setDefaultValue("").build());
        chatCategory.addEntry(entry.startStrList(LangUtils.translate("youtube_chat.configgui.rude_words"), config.rudeWords).setTooltip(TextComponentUtils.component("List of rude words, this will be automatically do a selected action (delete or timeout) to the users")).setSaveConsumer(value -> config.rudeWords = value).setDefaultValue(Collections.emptyList()).build());
        chatCategory.addEntry(entry.startStrList(LangUtils.translate("youtube_chat.configgui.banned_rude_words"), config.bannedRudeWords).setTooltip(TextComponentUtils.component("List of rude words, this will be automatically ban user when message is received")).setSaveConsumer(value -> config.bannedRudeWords = value).setDefaultValue(Collections.emptyList()).build());
        chatCategory.addEntry(entry.startEnumSelector(LangUtils.translate("youtube_chat.configgui.rude_word_action"), RudeWordAction.class, config.rudeWordAction).setTooltip(TextComponentUtils.component("Display unicode in front of Moderators")).setSaveConsumer(value -> config.rudeWordAction = value).setDefaultValue(RudeWordAction.DELETE).build());
        return builder.build();
    }
}