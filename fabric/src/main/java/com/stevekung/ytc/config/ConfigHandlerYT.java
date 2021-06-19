package com.stevekung.ytc.config;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import com.stevekung.stevekungslib.utils.ConfigHandlerBase;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.ytc.core.YouTubeChatMod;

public class ConfigHandlerYT extends ConfigHandlerBase
{
    private YouTubeChatConfig config;

    public ConfigHandlerYT()
    {
        super(YouTubeChatMod.MOD_ID);
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
                YouTubeChatMod.LOGGER.error("Failed to load config, using default.", e);
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
            YouTubeChatMod.LOGGER.error("Unable to find config file, creating new one.");
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