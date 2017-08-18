package com.google.youtube.gaming.chat;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandDeleteChat extends CommandBase
{
    private ChatService service;

    public CommandDeleteChat(ChatService service)
    {
        this.service = service;
    }

    @Override
    public String getCommandName()
    {
        return "ytcdel";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return this.getUsage();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new WrongUsageException(this.getUsage());
        }
        String messageId = args[0];
        Runnable response = () -> ModLogger.printYTMessage(YouTubeChat.json.text("Message deleted").setChatStyle(YouTubeChat.json.green()));
        this.service.deleteMessage(messageId, response);
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    private String getUsage()
    {
        return "/ytcdel <message_id>";
    }
}