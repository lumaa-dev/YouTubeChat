/*
 * Copyright 2017-2021 Google Inc.
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

import com.stevekung.stevekunglib.utils.TextComponentUtils;
import com.stevekung.stevekunglib.utils.client.ClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 *
 * Custom mod logger
 * @author SteveKunG
 *
 */
public class ChatUtils
{
    public static void print(Component component)
    {
        MutableComponent message = TextComponentUtils.formatted("[YT] ", ChatFormatting.RED).append(component);
        ClientUtils.printClientMessage(message);
    }

    public static void printYTMessage(Component component)
    {
        MutableComponent message = TextComponentUtils.formatted("[YouTubeChat] ", ChatFormatting.RED).append(component);
        ClientUtils.printClientMessage(message);
    }

    public static Component printYTOverlayMessage(Component component)
    {
        return TextComponentUtils.formatted("[YouTubeChat] ", ChatFormatting.RED).append(component);
    }

    public static void printExceptionMessage(String message)
    {
        ClientUtils.printClientMessage(TextComponentUtils.formatted("[YouTubeChat] ", ChatFormatting.RED).append(TextComponentUtils.formatted(message, ChatFormatting.DARK_RED)));
    }
}