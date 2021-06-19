package com.stevekung.ytc.utils.fabric;

import java.util.List;

import com.stevekung.ytc.core.YouTubeChatFabricMod;
import com.stevekung.ytc.utils.RudeWordAction;

public class PlatformConfigImpl
{
    public static String getClientSecret()
    {
        return YouTubeChatFabricMod.CONFIG.getConfig().clientSecret;
    }

    public static boolean getEnableVersionCheck()
    {
        return YouTubeChatFabricMod.CONFIG.getConfig().enableVersionChecker;
    }

    public static String getOwnerIcon()
    {
        return YouTubeChatFabricMod.CONFIG.getConfig().ownerIcon;
    }

    public static String getModeratorIcon()
    {
        return YouTubeChatFabricMod.CONFIG.getConfig().moderatorIcon;
    }

    public static List<String> getRudeWords()
    {
        return YouTubeChatFabricMod.CONFIG.getConfig().rudeWords;
    }

    public static List<String> getBannedRudeWords()
    {
        return YouTubeChatFabricMod.CONFIG.getConfig().bannedRudeWords;
    }

    public static RudeWordAction getRudeWordAction()
    {
        return YouTubeChatFabricMod.CONFIG.getConfig().rudeWordAction;
    }
}