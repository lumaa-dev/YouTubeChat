/**
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.youtube.gaming.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

/**
 *
 * Custom mod logger
 * @author SteveKunG
 *
 */
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

    public static void printYTMessage(ITextComponent component, boolean isRight)
    {
        if (isRight)
        {
            YouTubeChat.rightStreamGui.printChatMessage(YouTubeChat.json.text("[YTChat] ").setStyle(YouTubeChat.json.red()).appendSibling(component));
        }
        else
        {
            if (Minecraft.getMinecraft().player != null)
            {
                Minecraft.getMinecraft().player.sendMessage(YouTubeChat.json.text("[YTChat] ").setStyle(YouTubeChat.json.red()).appendSibling(component));
            }
        }
    }

    public static void printExceptionMessage(String message)
    {
        if (Minecraft.getMinecraft().player != null)
        {
            Minecraft.getMinecraft().player.sendMessage(YouTubeChat.json.text("[YTChatException] ").setStyle(YouTubeChat.json.red()).appendSibling(YouTubeChat.json.text(message).setStyle(YouTubeChat.json.darkRed())));
        }
    }
}