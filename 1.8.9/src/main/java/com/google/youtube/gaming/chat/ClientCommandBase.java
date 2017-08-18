package com.google.youtube.gaming.chat;

import net.minecraft.command.CommandBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.ForgeHooks;

public abstract class ClientCommandBase extends CommandBase
{
    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    protected static IChatComponent getChatComponentFromNthArg(String[] args, int index)
    {
        IChatComponent component = new ChatComponentText("");

        for (int i = index; i < args.length; ++i)
        {
            if (i > index)
            {
                component.appendText(" ");
            }
            IChatComponent component1 = ForgeHooks.newChatWithLinks(args[i]);
            component.appendSibling(component1);
        }
        return component;
    }
}