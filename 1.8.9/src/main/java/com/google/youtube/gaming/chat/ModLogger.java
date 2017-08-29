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
import net.minecraft.util.IChatComponent;

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

    public static void printYTMessage(IChatComponent component, boolean isRight)
    {
        if (isRight)
        {
            StreamChat.rightStreamGui.printChatMessage(StreamChat.json.text("[YTChat] ").setChatStyle(StreamChat.json.red()).appendSibling(component));
        }
        else
        {
            if (Minecraft.getMinecraft().thePlayer != null)
            {
                Minecraft.getMinecraft().thePlayer.addChatMessage(StreamChat.json.text("[YTChat] ").setChatStyle(StreamChat.json.red()).appendSibling(component));
            }
        }
    }

    public static void printExceptionMessage(String message)
    {
        if (Minecraft.getMinecraft().thePlayer != null)
        {
            Minecraft.getMinecraft().thePlayer.addChatMessage(StreamChat.json.text("[YTChatException] ").setChatStyle(StreamChat.json.red()).appendSibling(StreamChat.json.text(message).setChatStyle(StreamChat.json.darkRed())));
        }
    }
}