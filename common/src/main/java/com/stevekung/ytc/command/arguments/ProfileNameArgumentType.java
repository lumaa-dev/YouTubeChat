package com.stevekung.ytc.command.arguments;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.stevekung.ytc.service.AuthService;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ProfileNameArgumentType implements ArgumentType<String>
{
    private static final DynamicCommandExceptionType PROFILE_NOT_FOUND = new DynamicCommandExceptionType(obj -> new TranslatableComponent("commands.ytprofile.not_found", obj));
    private static final SimpleCommandExceptionType INVALID_ARGS = new SimpleCommandExceptionType(new TranslatableComponent("argument.id.invalid"));

    public static ProfileNameArgumentType create()
    {
        return new ProfileNameArgumentType();
    }

    public static String getProfile(CommandContext<?> context, String name)
    {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        CompletableFuture<Suggestions> suggestions = Suggestions.empty();

        if (AuthService.USER_DIR.listFiles() != null)
        {
            List<String> resList = Arrays.stream(AuthService.USER_DIR.listFiles()).map(File::getName).collect(Collectors.toList());
            suggestions = ProfileNameArgumentType.suggestIterable(resList, builder);
        }
        return suggestions;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        String fileName = ProfileNameArgumentType.read(reader);
        boolean exist = false;

        if (AuthService.USER_DIR.exists())
        {
            for (File file : AuthService.USER_DIR.listFiles())
            {
                String name = file.getName();

                if (name.equals(fileName))
                {
                    exist = true;
                }
            }
        }

        try
        {
            if (exist)
            {
                return fileName;
            }
            else
            {
                throw ProfileNameArgumentType.PROFILE_NOT_FOUND.create(fileName);
            }
        }
        catch (Exception e)
        {
            return fileName;
        }
    }

    @Override
    public Collection<String> getExamples()
    {
        return Collections.singletonList("profile");
    }

    private static CompletableFuture<Suggestions> suggestIterable(Iterable<String> iterable, SuggestionsBuilder builder)
    {
        String typedString = builder.getRemaining().toLowerCase(Locale.ROOT);
        ProfileNameArgumentType.applySuggest(iterable, typedString, string1 -> string1, builder::suggest);
        return builder.buildFuture();
    }

    private static void applySuggest(Iterable<String> iterable, String typedString, Function<String, String> function, Consumer<String> consumer)
    {
        for (String name : iterable)
        {
            String name2 = function.apply(name);

            if (name2.startsWith(typedString))
            {
                consumer.accept(name);
            }
        }
    }

    private static String read(StringReader reader) throws CommandSyntaxException
    {
        int cursor = reader.getCursor();

        while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek()))
        {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try
        {
            return string;
        }
        catch (ResourceLocationException e)
        {
            reader.setCursor(cursor);
            throw ProfileNameArgumentType.INVALID_ARGS.createWithContext(reader);
        }
    }
}