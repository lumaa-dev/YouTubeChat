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

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 *
 * Used for creating compact TextComponent
 * @author SteveKunG
 *
 */
public class JsonUtil
{
    public ChatComponentText text(String text)
    {
        return new ChatComponentText(text);
    }

    public ChatStyle style()
    {
        return new ChatStyle();
    }

    public ChatStyle white()
    {
        return this.style().setColor(EnumChatFormatting.WHITE);
    }

    public ChatStyle gray()
    {
        return this.style().setColor(EnumChatFormatting.GRAY);
    }

    public ChatStyle red()
    {
        return this.style().setColor(EnumChatFormatting.RED);
    }

    public ChatStyle darkRed()
    {
        return this.style().setColor(EnumChatFormatting.DARK_RED);
    }

    public ChatStyle green()
    {
        return this.style().setColor(EnumChatFormatting.GREEN);
    }

    public ChatStyle gold()
    {
        return this.style().setColor(EnumChatFormatting.GOLD);
    }

    public ChatStyle blue()
    {
        return this.style().setColor(EnumChatFormatting.BLUE);
    }

    public ClickEvent click(ClickEvent.Action action, String url)
    {
        return new ClickEvent(action, url);
    }

    public HoverEvent hover(HoverEvent.Action action, IChatComponent text)
    {
        return new HoverEvent(action, text);
    }
}