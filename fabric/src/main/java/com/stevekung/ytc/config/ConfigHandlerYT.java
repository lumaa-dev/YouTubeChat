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

package com.stevekung.ytc.config;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import com.stevekung.stevekungslib.utils.ConfigHandlerBase;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.ytc.core.YouTubeChat;

public class ConfigHandlerYT extends ConfigHandlerBase
{
    private YouTubeChatConfig config;

    public ConfigHandlerYT()
    {
        super(YouTubeChat.MOD_ID);
    }

    public YouTubeChatConfig getConfig()
    {
        if (this.config == null)
        {
            try
            {
                this.loadConfig();
            }
            catch (IOException e)
            {
                YouTubeChat.LOGGER.error("Failed to load config, using default.", e);
                return new YouTubeChatConfig();
            }
        }
        return this.config;
    }

    @Override
    public void loadConfig() throws IOException
    {
        this.configFile.getParentFile().mkdirs();

        if (!this.configFile.exists())
        {
            YouTubeChat.LOGGER.error("Unable to find config file, creating new one.");
            this.config = new YouTubeChatConfig();
            this.saveConfig();
        }
        else
        {
            this.config = GSON.fromJson(ConfigHandlerBase.readFile(this.configFile.toPath().toString(), Charset.defaultCharset()), YouTubeChatConfig.class);
        }
    }

    @Override
    public void saveConfig() throws IOException
    {
        this.configFile.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(this.configFile);
        TextComponentUtils.toJson(this.config, writer);
        writer.close();
    }
}