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

package com.stevekung.ytc.utils;

import java.util.List;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformConfig
{
    @ExpectPlatform
    public static String clientSecret()
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String ownerIcon()
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String moderatorIcon()
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<String> rudeWords()
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<String> bannedRudeWords()
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static RudeWordAction rudeWordAction()
    {
        throw new AssertionError();
    }
}