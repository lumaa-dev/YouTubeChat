/*
 * Copyright 2017-2019 Google Inc.
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

package stevekung.mods.ytchat.utils;

import net.minecraft.util.text.ITextComponent;
import stevekung.mods.stevekungslib.utils.JsonUtils;
import stevekung.mods.stevekungslib.utils.LoggerBase;
import stevekung.mods.stevekungslib.utils.client.ClientUtils;
import stevekung.mods.ytchat.config.YouTubeChatConfig;
import stevekung.mods.ytchat.core.EventHandlerYT;

/**
 *
 * Custom mod logger
 * @author SteveKunG
 *
 */
public class LoggerYT extends LoggerBase
{
    public LoggerYT()
    {
        super("YouTube Chat", false);
    }

    public void printYTMessage(ITextComponent component)
    {
        ITextComponent message = JsonUtils.create("[YTChat] ").setStyle(JsonUtils.red()).appendSibling(component);

        if (YouTubeChatConfig.GENERAL.displayChatRightSide.get())
        {
            message = component.appendSibling(JsonUtils.create(" [YTChat]").setStyle(JsonUtils.red()));
            EventHandlerYT.rightStreamGui.printYTChatMessage(message);
        }
        else
        {
            ClientUtils.printClientMessage(message);
        }
    }

    public ITextComponent printYTOverlayMessage(ITextComponent component)
    {
        ITextComponent message = JsonUtils.create("[YTChat] ").setStyle(JsonUtils.red()).appendSibling(component);
        return message;
    }

    public void printExceptionMessage(String message)
    {
        if (YouTubeChatConfig.GENERAL.displayChatRightSide.get())
        {
            EventHandlerYT.rightStreamGui.printYTChatMessage(JsonUtils.create(message).setStyle(JsonUtils.darkRed()).appendSibling(JsonUtils.create(" [YTChatException]").setStyle(JsonUtils.red())));
        }
        else
        {
            ClientUtils.printClientMessage(JsonUtils.create("[YTChatException] ").setStyle(JsonUtils.red()).appendSibling(JsonUtils.create(message).setStyle(JsonUtils.darkRed())));
        }
    }
}