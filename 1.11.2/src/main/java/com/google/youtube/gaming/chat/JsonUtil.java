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

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 *
 * Used for creating compact TextComponent
 * @author SteveKunG
 *
 */
public class JsonUtil
{
    public TextComponentString text(String text)
    {
        return new TextComponentString(text);
    }

    public Style style()
    {
        return new Style();
    }

    public Style white()
    {
        return this.style().setColor(TextFormatting.WHITE);
    }

    public Style gray()
    {
        return this.style().setColor(TextFormatting.GRAY);
    }

    public Style red()
    {
        return this.style().setColor(TextFormatting.RED);
    }

    public Style darkRed()
    {
        return this.style().setColor(TextFormatting.DARK_RED);
    }

    public Style green()
    {
        return this.style().setColor(TextFormatting.GREEN);
    }

    public Style gold()
    {
        return this.style().setColor(TextFormatting.GOLD);
    }

    public Style blue()
    {
        return this.style().setColor(TextFormatting.BLUE);
    }

    public ClickEvent click(ClickEvent.Action action, String url)
    {
        return new ClickEvent(action, url);
    }

    public HoverEvent hover(HoverEvent.Action action, ITextComponent text)
    {
        return new HoverEvent(action, text);
    }
}