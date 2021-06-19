package com.stevekung.ytc.utils.forge;

import java.util.List;

import com.stevekung.ytc.config.YouTubeChatConfig;
import com.stevekung.ytc.utils.RudeWordAction;

public class PlatformConfigImpl
{
    public static String getClientSecret()
    {
        return YouTubeChatConfig.GENERAL.clientSecret.get();
    }

    public static boolean getEnableVersionCheck()
    {
        return YouTubeChatConfig.GENERAL.enableVersionChecker.get();
    }

    public static String getOwnerIcon()
    {
        return YouTubeChatConfig.CHAT.ownerIcon.get();
    }

    public static String getModeratorIcon()
    {
        return YouTubeChatConfig.CHAT.moderatorIcon.get();
    }

    public static List<String> getRudeWords()
    {
        return YouTubeChatConfig.CHAT.rudeWords.get();
    }

    public static List<String> getBannedRudeWords()
    {
        return YouTubeChatConfig.CHAT.bannedRudeWords.get();
    }

    public static RudeWordAction getRudeWordAction()
    {
        return YouTubeChatConfig.CHAT.rudeWordAction.get();
    }
}