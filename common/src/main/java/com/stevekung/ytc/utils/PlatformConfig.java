package com.stevekung.ytc.utils;

import java.util.List;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformConfig
{
    @ExpectPlatform
    public static String getClientSecret()
    {
        throw new Error();
    }

    @ExpectPlatform
    public static boolean getEnableVersionCheck()
    {
        throw new Error();
    }

    @ExpectPlatform
    public static String getOwnerIcon()
    {
        throw new Error();
    }

    @ExpectPlatform
    public static String getModeratorIcon()
    {
        throw new Error();
    }

    @ExpectPlatform
    public static List<String> getRudeWords()
    {
        throw new Error();
    }

    @ExpectPlatform
    public static List<String> getBannedRudeWords()
    {
        throw new Error();
    }

    @ExpectPlatform
    public static RudeWordAction getRudeWordAction()
    {
        throw new Error();
    }
}