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

package com.stevekung.ytc.forge.mixin.multiplayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.stevekung.ytc.forge.command.clientcommands.ClientCommands;
import net.minecraft.client.ClientTelemetryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener
{
    @Shadow
    CommandDispatcher<SharedSuggestionProvider> commands;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void youtubeChat$init(Minecraft mc, Screen screen, Connection connection, GameProfile profile, ClientTelemetryManager clientTelemetryManager, CallbackInfo info)
    {
        ClientCommands.buildSuggestion(this.commands);
        ClientCommands.buildDispatcher();
    }

    @Inject(method = "handleCommands", at = @At("TAIL"))
    private void youtubeChat$handleCommands(ClientboundCommandsPacket packet, CallbackInfo info)
    {
        ClientCommands.buildSuggestion(this.commands);
    }
}