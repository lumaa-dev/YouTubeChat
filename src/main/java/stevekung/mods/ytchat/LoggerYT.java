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

package stevekung.mods.ytchat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import stevekung.mods.ytchat.core.YouTubeChatMod;

/**
 *
 * Custom mod logger
 * @author SteveKunG
 *
 */
public class LoggerYT
{
    private static final Logger LOG;

    static
    {
        LOG = LogManager.getLogger("YTChat");
    }

    public static void info(String message)
    {
        LoggerYT.LOG.info(message);
    }

    public static void error(String message)
    {
        LoggerYT.LOG.error(message);
    }

    public static void warning(String message)
    {
        LoggerYT.LOG.warn(message);
    }

    public static void info(String message, Object... obj)
    {
        LoggerYT.LOG.info(message, obj);
    }

    public static void error(String message, Object... obj)
    {
        LoggerYT.LOG.error(message, obj);
    }

    public static void warning(String message, Object... obj)
    {
        LoggerYT.LOG.warn(message, obj);
    }

    public static void printYTMessage(ITextComponent component)
    {
        if (ConfigManager.displayChatRightSide)
        {
            YouTubeChatMod.rightStreamGui.printChatMessage(YouTubeChatMod.json.text("[YTChat] ").setStyle(YouTubeChatMod.json.red()).appendSibling(component));
        }
        else
        {
            if (Minecraft.getMinecraft().player != null)
            {
                Minecraft.getMinecraft().player.sendMessage(YouTubeChatMod.json.text("[YTChat] ").setStyle(YouTubeChatMod.json.red()).appendSibling(component));
            }
        }
    }

    public static void printExceptionMessage(String message)
    {
        if (Minecraft.getMinecraft().player != null)
        {
            Minecraft.getMinecraft().player.sendMessage(YouTubeChatMod.json.text("[YTChatException] ").setStyle(YouTubeChatMod.json.red()).appendSibling(YouTubeChatMod.json.text(message).setStyle(YouTubeChatMod.json.darkRed())));
        }
    }
}