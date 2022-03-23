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

package com.stevekung.ytc.core;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.stevekung.ytc.gui.screens.ChatActionScreen;
import com.stevekung.ytc.service.AuthService;
import net.minecraft.client.Minecraft;

public class YouTubeChat
{
    public static final String NAME = "YouTube Chat";
    public static final String MOD_ID = "youtube_chat";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static int openTick;
    public static ChatActionScreen actionScreen;

    public static void init()
    {
        AuthService.CONFIG_DIR = new File(new File(Minecraft.getInstance().gameDirectory, "config"), AuthService.CREDENTIALS_DIRECTORY);
        AuthService.USER_DIR = new File(AuthService.CONFIG_DIR, Minecraft.getInstance().getUser().getUuid());
    }

    public static void clientTick(Minecraft mc)
    {
        if (--YouTubeChat.openTick > 0)
        {
            mc.setScreen(YouTubeChat.actionScreen);
            YouTubeChat.openTick = -1;
            YouTubeChat.actionScreen = null;
        }
    }

    public static void schedule(Runnable runnable, long delay)
    {
        var task = new TimerTask()
        {
            @Override
            public void run()
            {
                runnable.run();
            }
        };
        new Timer().schedule(task, delay);
    }
}