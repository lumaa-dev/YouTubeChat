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

package com.stevekung.ytc.utils.forge;

import java.util.List;

import com.stevekung.ytc.config.YouTubeChatConfig;
import com.stevekung.ytc.utils.RudeWordAction;

public class PlatformConfigImpl
{
    public static String getClientSecret()
    {
        return YouTubeChatConfig.GENERAL.clientSecret.get();
    }

    public static boolean getEnableVersionCheck()
    {
        return YouTubeChatConfig.GENERAL.enableVersionChecker.get();
    }

    public static String getOwnerIcon()
    {
        return YouTubeChatConfig.CHAT.ownerIcon.get();
    }

    public static String getModeratorIcon()
    {
        return YouTubeChatConfig.CHAT.moderatorIcon.get();
    }

    public static List<String> getRudeWords()
    {
        return YouTubeChatConfig.CHAT.rudeWords.get();
    }

    public static List<String> getBannedRudeWords()
    {
        return YouTubeChatConfig.CHAT.bannedRudeWords.get();
    }

    public static RudeWordAction getRudeWordAction()
    {
        return YouTubeChatConfig.CHAT.rudeWordAction.get();
    }
}