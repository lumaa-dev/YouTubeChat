package com.google.youtube.gaming.chat;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandChatAction extends ClientCommandBase
{
    private ChatService service;

    public CommandChatAction(ChatService service)
    {
        this.service = service;
    }

    @Override
    public String getCommandName()
    {
        return "ytchataction";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return this.getCommandName();
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("ytcaction");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new WrongUsageException(this.getUsage());
        }
        new GuiChatAction(this.service, args[0]).display();
    }

    private String getUsage()
    {
        return "/ytchataction <message_id>";
    }
}