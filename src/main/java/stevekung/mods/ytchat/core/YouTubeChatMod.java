/*
 * Copyright 2017-2018 Google Inc.
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

package stevekung.mods.ytchat.core;

import java.io.File;
import java.util.Arrays;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stevekung.mods.stevekunglib.client.gui.GuiChatRegistry;
import stevekung.mods.stevekunglib.utils.CommonUtils;
import stevekung.mods.stevekunglib.utils.GameProfileUtils;
import stevekung.mods.stevekunglib.utils.VersionChecker;
import stevekung.mods.stevekunglib.utils.client.ClientUtils;
import stevekung.mods.ytchat.auth.Authentication;
import stevekung.mods.ytchat.command.CommandChatAction;
import stevekung.mods.ytchat.command.CommandPostMessage;
import stevekung.mods.ytchat.command.CommandYouTubeChat;
import stevekung.mods.ytchat.config.ConfigManagerYT;
import stevekung.mods.ytchat.gui.GuiYouTubeChat;
import stevekung.mods.ytchat.utils.LoggerYT;

@Mod(modid = YouTubeChatMod.MOD_ID, name = YouTubeChatMod.NAME, version = YouTubeChatMod.VERSION, clientSideOnly = true, dependencies = YouTubeChatMod.DEPENDENCIES, updateJSON = YouTubeChatMod.JSON_URL, certificateFingerprint = YouTubeChatMod.CERTIFICATE)
public class YouTubeChatMod
{
    public static final String NAME = "YouTube Chat";
    public static final String MOD_ID = "youtube_chat";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 4;
    private static final int BUILD_VERSION = 0;
    public static final String VERSION = YouTubeChatMod.MAJOR_VERSION + "." + YouTubeChatMod.MINOR_VERSION + "." + YouTubeChatMod.BUILD_VERSION;
    private static final String FORGE_VERSION = "after:forge@[14.23.5.2768,);";
    protected static final String DEPENDENCIES = "required-after:stevekung's_lib@[1.0.3,); " + YouTubeChatMod.FORGE_VERSION;
    protected static final String CERTIFICATE = "@FINGERPRINT@";
    private static final String URL = "https://minecraft.curseforge.com/projects/youtube-chat";
    protected static final String JSON_URL = "https://raw.githubusercontent.com/SteveKunG/VersionCheckLibrary/master/youtube_chat_version.json";

    @Instance(YouTubeChatMod.MOD_ID)
    public static YouTubeChatMod INSTANCE;

    public static VersionChecker CHECKER;
    public static boolean isDevelopment;

    static
    {
        try
        {
            YouTubeChatMod.isDevelopment = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        }
        catch (Exception e) {}
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        YouTubeChatMod.initModInfo(event.getModMetadata());
        Authentication.configDirectory = new File(event.getModConfigurationDirectory(), Authentication.CREDENTIALS_DIRECTORY);
        Authentication.userDir = new File(Authentication.configDirectory, GameProfileUtils.getUUID().toString());
        ClientUtils.registerCommand(new CommandYouTubeChat());
        ClientUtils.registerCommand(new CommandChatAction());
        ClientUtils.registerCommand(new CommandPostMessage());
        CommonUtils.registerEventHandler(this);
        CommonUtils.registerEventHandler(new EventHandlerYT());

        YouTubeChatMod.CHECKER = new VersionChecker(YouTubeChatMod.INSTANCE, YouTubeChatMod.NAME, YouTubeChatMod.URL);

        if (ConfigManagerYT.youtube_chat_general.enableVersionChecker)
        {
            YouTubeChatMod.CHECKER.startCheck();
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        GuiChatRegistry.register(new GuiYouTubeChat());
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        if (YouTubeChatMod.isDevelopment)
        {
            LoggerYT.info("Development environment detected! Ignore certificate check.");
        }
        else
        {
            throw new RuntimeException("Invalid fingerprint detected! This version will NOT be supported by the author!");
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(YouTubeChatMod.MOD_ID))
        {
            ConfigManager.sync(YouTubeChatMod.MOD_ID, Config.Type.INSTANCE);
        }
    }

    private static void initModInfo(ModMetadata info)
    {
        info.autogenerated = false;
        info.modId = YouTubeChatMod.MOD_ID;
        info.name = YouTubeChatMod.NAME;
        info.description = "Enables interaction with YouTube Live Stream Chat.";
        info.version = YouTubeChatMod.VERSION;
        info.url = YouTubeChatMod.URL;
        info.authorList = Arrays.asList("SteveKunG", "PeregrineZ", "jimrogers");
        info.credits = "Credit to PeregrineZ for implemented example features";
    }
}