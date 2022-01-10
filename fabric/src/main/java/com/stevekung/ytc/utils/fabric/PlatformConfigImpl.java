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

package com.stevekung.ytc.utils.fabric;

import java.util.List;

import com.stevekung.ytc.fabric.core.YouTubeChatFabric;
import com.stevekung.ytc.utils.RudeWordAction;

public class PlatformConfigImpl
{
    public static String clientSecret()
    {
        return YouTubeChatFabric.CONFIG.general.clientSecret;
    }

    public static String ownerIcon()
    {
        return YouTubeChatFabric.CONFIG.chat.ownerIcon;
    }

    public static String moderatorIcon()
    {
        return YouTubeChatFabric.CONFIG.chat.moderatorIcon;
    }

    public static List<String> rudeWords()
    {
        return YouTubeChatFabric.CONFIG.chat.rudeWords;
    }

    public static List<String> bannedRudeWords()
    {
        return YouTubeChatFabric.CONFIG.chat.bannedRudeWords;
    }

    public static RudeWordAction rudeWordAction()
    {
        return YouTubeChatFabric.CONFIG.chat.rudeWordAction;
    }
}