/*
 * Copyright 2017-2022 Google Inc.
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

package com.stevekung.ytc.utils;

import com.stevekung.ytc.core.YouTubeChat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

/**
 *
 * Custom mod logger
 * @author SteveKunG
 *
 */
public class ChatUtils
{
    public static void print(Component toAppend)
    {
        var message = new TextComponent("[YT]").append(" ").withStyle(ChatFormatting.RED).append(toAppend);
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }

    public static void printMessage(Component message)
    {
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }

    public static void printChatMessage(Component toAppend)
    {
        var message = new TextComponent("[" + YouTubeChat.NAME + "]").append(" ").withStyle(ChatFormatting.RED).append(toAppend);
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }

    public static void printOverlayMessage(Component toAppend)
    {
        var message = new TextComponent("[YT]").append(" ").withStyle(ChatFormatting.RED).append(toAppend);
        Minecraft.getInstance().gui.setOverlayMessage(message, false);
    }

    public static void printExceptionMessage(String exception)
    {
        var message = new TextComponent("[" + YouTubeChat.NAME + "]").append(" ").withStyle(ChatFormatting.RED).append(new TextComponent(exception).withStyle(ChatFormatting.DARK_RED));
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }
}