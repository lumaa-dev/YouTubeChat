package com.stevekung.ytc.config;

import java.util.Collections;
import java.util.List;

import com.stevekung.ytc.utils.RudeWordAction;

public class YouTubeChatConfig
{
    public String clientSecret = "";
    public boolean enableVersionChecker = true;
    public String ownerIcon = "";
    public String moderatorIcon = "";
    public List<String> bannedRudeWords = Collections.emptyList();
    public List<String> rudeWords = Collections.emptyList();
    public RudeWordAction rudeWordAction = RudeWordAction.DELETE;
}