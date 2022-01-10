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

package com.stevekung.ytc.forge.mixin.player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.stevekung.ytc.forge.command.clientcommands.ClientCommands;
import com.stevekung.ytc.forge.command.clientcommands.IClientSharedSuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer
{
    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(method = "chat", at = @At("HEAD"), cancellable = true)
    private void youtubeChat$chat(String message, CallbackInfo info)
    {
        var player = (LocalPlayer) (Object) this;

        if (message.length() < 2 || !message.startsWith("/"))
        {
            return;
        }
        if (!ClientCommands.hasCommand(message.substring(1).split(" ")[0]))
        {
            return;
        }

        try
        {
            // The game freezes when using heavy commands. Run your heavy code somewhere else pls
            var result = ClientCommands.execute(message.substring(1), (IClientSharedSuggestionProvider) new ClientSuggestionProvider(player.connection, this.minecraft));

            if (result != 0)
            {
                info.cancel(); // Prevent sending the message
            }
        }
        catch (CommandRuntimeException e)
        {
            player.displayClientMessage(e.getComponent().copy().withStyle(ChatFormatting.RED), false);
            info.cancel();
        }
        catch (CommandSyntaxException e)
        {
            player.displayClientMessage(new TextComponent(e.getContext()).withStyle(ChatFormatting.RED), false);
            info.cancel();
        }
        catch (Exception e)
        {
            player.displayClientMessage(new TranslatableComponent("command.failed").withStyle(ChatFormatting.RED), false);
            info.cancel();
        }
    }
}