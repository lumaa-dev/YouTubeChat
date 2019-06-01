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

package stevekung.mods.ytchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import stevekung.mods.stevekungslib.utils.LangUtils;
import stevekung.mods.ytchat.gui.GuiChatAction;
import stevekung.mods.ytchat.utils.YouTubeChatService;

public class ChatActionCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("ytcaction").requires(requirement -> requirement.hasPermissionLevel(0))
                .then(Commands.argument("message_id", StringArgumentType.word()).then(Commands.argument("channel_id", StringArgumentType.word()).then(Commands.argument("moderator_id", StringArgumentType.word()).then(Commands.argument("name", StringArgumentType.greedyString()))))
                        .executes(requirement -> ChatActionCommand.display(requirement.getSource(), StringArgumentType.getString(requirement, "message_id"), StringArgumentType.getString(requirement, "channel_id"), StringArgumentType.getString(requirement, "moderator_id"), StringArgumentType.getString(requirement, "name")))));
    }

    private static int display(CommandSource source, String messageId, String channelId, String moderatorId, String displayName)
    {
        if (!YouTubeChatService.getService().hasExecutor())
        {
            throw new CommandException(LangUtils.translateComponent("commands.service_not_init"));
        }
        Minecraft.getInstance().addScheduledTask(() -> Minecraft.getInstance().displayGuiScreen(new GuiChatAction(messageId, channelId, moderatorId, displayName)));
        return 0;
    }
}