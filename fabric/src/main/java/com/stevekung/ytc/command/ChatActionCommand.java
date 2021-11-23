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
import com.stevekung.stevekunglib.utils.CommonUtils;
import com.stevekung.ytc.gui.screens.ChatActionScreen;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

/**
 *
 * Do an action with current chat message. [Delete, Ban, Temporary Ban, Add Moderator]
 * @author SteveKunG
 *
 */
public class ChatActionCommand
{
    public ChatActionCommand(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        dispatcher.register(ClientCommandManager.literal("ytcaction")
                .then(ClientCommandManager.argument("messageId", StringArgumentType.word())
                        .then(ClientCommandManager.argument("channelId", StringArgumentType.word())
                                .then(ClientCommandManager.argument("moderatorId", StringArgumentType.word())
                                        .then(ClientCommandManager.argument("displayName", StringArgumentType.word())
                                                .executes(requirement -> doChatAction(StringArgumentType.getString(requirement, "messageId"), StringArgumentType.getString(requirement, "channelId"), StringArgumentType.getString(requirement, "moderatorId"), StringArgumentType.getString(requirement, "displayName"))))))));
    }

    private static int doChatAction(String messageId, String channelId, String moderatorId, String displayName)
    {
        CommonUtils.schedule(() -> Minecraft.getInstance().setScreen(new ChatActionScreen(messageId, channelId, moderatorId, displayName)), 2);
        return 1;
    }
}