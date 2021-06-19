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

package com.stevekung.ytc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;
import com.stevekung.stevekungslib.utils.client.command.IClientCommand;
import com.stevekung.stevekungslib.utils.client.command.IClientSharedSuggestionProvider;
import com.stevekung.ytc.gui.ChatActionScreen;
import net.minecraft.client.Minecraft;

/**
 *
 * Do an action with current chat message. [Delete, Ban, Temporary Ban, Add Moderator]
 * @author SteveKunG
 *
 */
public class ChatActionCommand implements IClientCommand
{
    @Override
    public void register(CommandDispatcher<IClientSharedSuggestionProvider> dispatcher)
    {
        dispatcher.register(ClientCommands.literal("ytcaction")
                .then(ClientCommands.argument("messageId", StringArgumentType.word())
                        .then(ClientCommands.argument("channelId", StringArgumentType.word())
                                .then(ClientCommands.argument("moderatorId", StringArgumentType.word())
                                        .then(ClientCommands.argument("displayName", StringArgumentType.word())
                                                .executes(requirement -> doChatAction(StringArgumentType.getString(requirement, "messageId"), StringArgumentType.getString(requirement, "channelId"), StringArgumentType.getString(requirement, "moderatorId"), StringArgumentType.getString(requirement, "displayName"))))))));
    }

    private static int doChatAction(String messageId, String channelId, String moderatorId, String displayName)
    {
        CommonUtils.schedule(() -> Minecraft.getInstance().setScreen(new ChatActionScreen(messageId, channelId, moderatorId, displayName)), 2);
        return 1;
    }
}