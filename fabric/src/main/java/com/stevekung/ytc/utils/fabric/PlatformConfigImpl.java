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

package com.stevekung.ytc.utils.fabric;

import java.util.List;

import com.stevekung.ytc.core.YouTubeChatFabric;
import com.stevekung.ytc.utils.RudeWordAction;

public class PlatformConfigImpl
{
    public static String getClientSecret()
    {
        return YouTubeChatFabric.CONFIG.getConfig().clientSecret;
    }

    public static boolean getEnableVersionCheck()
    {
        return YouTubeChatFabric.CONFIG.getConfig().enableVersionChecker;
    }

    public static String getOwnerIcon()
    {
        return YouTubeChatFabric.CONFIG.getConfig().ownerIcon;
    }

    public static String getModeratorIcon()
    {
        return YouTubeChatFabric.CONFIG.getConfig().moderatorIcon;
    }

    public static List<String> getRudeWords()
    {
        return YouTubeChatFabric.CONFIG.getConfig().rudeWords;
    }

    public static List<String> getBannedRudeWords()
    {
        return YouTubeChatFabric.CONFIG.getConfig().bannedRudeWords;
    }

    public static RudeWordAction getRudeWordAction()
    {
        return YouTubeChatFabric.CONFIG.getConfig().rudeWordAction;
    }
}