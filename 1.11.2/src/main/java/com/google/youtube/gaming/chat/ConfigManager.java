/**
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.youtube.gaming.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Configuration settings for YouTube Chat.
 */
public class ConfigManager
{
    private static Configuration config;

    public static String clientSecret;
    public static String liveVideoId;
    public static String ownerUnicodeIcon;
    public static String moderatorUnicodeIcon;
    public static String rudeWordList;
    public static String rudeWordAction;

    public static boolean showSuperChatOnly;
    public static boolean displayChatRightSide;
    public static boolean autoReceiveChat;

    public static void init(File file)
    {
        ConfigManager.config = new Configuration(file);
        ConfigManager.syncConfig(true);
    }

    public static void syncConfig(boolean load)
    {
        if (!ConfigManager.config.isChild)
        {
            if (load)
            {
                ConfigManager.config.load();
            }
        }

        ConfigManager.config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, ConfigManager.addGeneralSetting());

        if (ConfigManager.config.hasChanged())
        {
            ConfigManager.config.save();
        }
    }

    private static List<String> addGeneralSetting()
    {
        Property prop;
        List<String> propOrder = new ArrayList<>();

        prop = ConfigManager.getProperty(Configuration.CATEGORY_GENERAL, "Client Secret", "");
        prop.setComment("The client secret from Google API console");
        ConfigManager.clientSecret = prop.getString();
        propOrder.add(prop.getName());

        prop = ConfigManager.getProperty(Configuration.CATEGORY_GENERAL, "Video ID", "");
        prop.setComment("The ID of the live video");
        ConfigManager.liveVideoId = prop.getString();
        propOrder.add(prop.getName());

        prop = ConfigManager.getProperty(Configuration.CATEGORY_GENERAL, "Channel Owner Icon (Unicode)", "");
        prop.setComment("Put your fancy unicode, it will display in front of your channel");
        ConfigManager.ownerUnicodeIcon = prop.getString();
        propOrder.add(prop.getName());

        prop = ConfigManager.getProperty(Configuration.CATEGORY_GENERAL, "Moderator Icon (Unicode)", "");
        prop.setComment("Put your fancy unicode, it will display in front of moderator channel");
        ConfigManager.moderatorUnicodeIcon = prop.getString();
        propOrder.add(prop.getName());

        prop = ConfigManager.getProperty(Configuration.CATEGORY_GENERAL, "Rude Word List", "");
        prop.setComment("Put the list of rude word, this will be automatically do an action with message when received. split by \",\"");
        ConfigManager.rudeWordList = prop.getString();
        propOrder.add(prop.getName());

        prop = ConfigManager.getProperty(Configuration.CATEGORY_GENERAL, "Rude Word Action", "delete");
        prop.setComment("If the message contain rude word, this will does an action with selected action");
        prop.setValidValues(new String[] { "delete", "ban", "temporary_ban" });
        ConfigManager.rudeWordAction = prop.getString();
        propOrder.add(prop.getName());

        prop = ConfigManager.getProperty(Configuration.CATEGORY_GENERAL, "Show Super Chat Only", false);
        prop.setComment("Display super chats only");
        ConfigManager.showSuperChatOnly = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManager.getProperty(Configuration.CATEGORY_GENERAL, "Display YouTube Chat on Right Side", false);
        prop.setComment("Move only YouTube Chat into Right Side of the screen");
        ConfigManager.displayChatRightSide = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManager.getProperty(Configuration.CATEGORY_GENERAL, "Automatic Receive Chat", false);
        prop.setComment("When you starting service, This will automatically subscribe YouTube Chat Service");
        ConfigManager.autoReceiveChat = prop.getBoolean();
        propOrder.add(prop.getName());

        return propOrder;
    }

    public static Property getProperty(String category, String name, boolean defaultValue)
    {
        return ConfigManager.config.get(category, name, defaultValue);
    }

    public static Property getProperty(String category, String name, String defaultValue)
    {
        return ConfigManager.config.get(category, name, defaultValue);
    }

    public static Configuration getConfig()
    {
        return ConfigManager.config;
    }
}