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

package com.stevekung.ytc.core;

import java.io.File;

import com.stevekung.stevekunglib.utils.GameProfileUtils;
import com.stevekung.stevekunglib.utils.LoggerBase;
import com.stevekung.ytc.service.AuthService;
import dev.architectury.platform.Platform;

public class YouTubeChat
{
    public static final String NAME = "YouTube Chat";
    public static final String MOD_ID = "youtube_chat";
    public static final LoggerBase LOGGER = new LoggerBase(NAME);

    public static void init()
    {
        AuthService.CONFIG_DIR = new File(Platform.getConfigFolder().toFile(), AuthService.CREDENTIALS_DIRECTORY);
        AuthService.USER_DIR = new File(AuthService.CONFIG_DIR, GameProfileUtils.getUUID().toString());
    }
}