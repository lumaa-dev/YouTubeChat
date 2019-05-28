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

import java.util.function.Consumer;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;
import stevekung.mods.stevekungslib.utils.JsonUtils;
import stevekung.mods.stevekungslib.utils.LangUtils;
import stevekung.mods.stevekungslib.utils.client.ClientUtils;
import stevekung.mods.ytchat.config.YouTubeChatConfig;
import stevekung.mods.ytchat.core.YouTubeChatMod;
import stevekung.mods.ytchat.utils.YouTubeChatService;

public class PostMessageCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("ytm").requires(requirement -> requirement.hasPermissionLevel(0)).then(Commands.argument("message", MessageArgument.message())).executes(requirement -> PostMessageCommand.postMessage(requirement.getSource(), MessageArgument.getMessage(requirement, "message"))));
    }

    private static int postMessage(CommandSource source, ITextComponent component)
    {
        String clientSecret = YouTubeChatConfig.GENERAL.clientSecret.get();
        YouTubeChatService service = YouTubeChatService.getService();

        if (clientSecret.isEmpty())
        {
            throw new CommandException(LangUtils.translateComponent("commands.no_client_secret_config"));
        }
        if (!service.hasExecutor())
        {
            throw new CommandException(LangUtils.translateComponent("commands.service_not_init"));
        }
        String message = component.createCopy().getUnformattedComponentText();
        Consumer<String> id = i -> ClientUtils.setOverlayMessage(YouTubeChatMod.LOGGER.printYTOverlayMessage(JsonUtils.create("Message posted").setStyle(JsonUtils.green())));
        service.postMessage(message, id);
        return 0;
    }
}