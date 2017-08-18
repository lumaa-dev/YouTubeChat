package com.google.youtube.gaming.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

public class ModLogger
{
    private static final Logger LOG;

    static
    {
        LOG = LogManager.getLogger("YTChat");
    }

    public static void info(String message)
    {
        ModLogger.LOG.info(message);
    }

    public static void error(String message)
    {
        ModLogger.LOG.error(message);
    }

    public static void warning(String message)
    {
        ModLogger.LOG.warn(message);
    }

    public static void info(String message, Object... obj)
    {
        ModLogger.LOG.info(message, obj);
    }

    public static void error(String message, Object... obj)
    {
        ModLogger.LOG.error(message, obj);
    }

    public static void warning(String message, Object... obj)
    {
        ModLogger.LOG.warn(message, obj);
    }

    public static void printYTMessage(IChatComponent component)
    {
        Minecraft.getMinecraft().thePlayer.addChatMessage(YouTubeChat.json.text("[YTChat] ").setChatStyle(YouTubeChat.json.red()).appendSibling(component));
    }

    public static void printExceptionMessage(String message)
    {
        Minecraft.getMinecraft().thePlayer.addChatMessage(YouTubeChat.json.text("[YTChatException] ").setChatStyle(YouTubeChat.json.red()).appendSibling(YouTubeChat.json.text(message).setChatStyle(YouTubeChat.json.darkRed())));
    }
}