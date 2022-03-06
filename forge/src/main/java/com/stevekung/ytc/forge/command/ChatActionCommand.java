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

package com.stevekung.ytc.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 *
 * Do an action with current chat message. [Delete, Ban, Temporary Ban, Add Moderator]
 * @author SteveKunG
 *
 */
public class ChatActionCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("ytcaction")
                .then(Commands.argument("messageId", StringArgumentType.word())
                        .then(Commands.argument("channelId", StringArgumentType.word())
                                .then(Commands.argument("moderatorId", StringArgumentType.word())
                                        .then(Commands.argument("displayName", StringArgumentType.word())
                                                .executes(context -> doChatAction(StringArgumentType.getString(context, "messageId"), StringArgumentType.getString(context, "channelId"), StringArgumentType.getString(context, "moderatorId"), StringArgumentType.getString(context, "displayName"))))))));
    }

    private static int doChatAction(String messageId, String channelId, String moderatorId, String displayName)
    {
        //TODO YouTubeChat.schedule(() -> Minecraft.getInstance().setScreen(new ChatActionScreen(messageId, channelId, moderatorId, displayName)), 2);
        return 1;
    }
}