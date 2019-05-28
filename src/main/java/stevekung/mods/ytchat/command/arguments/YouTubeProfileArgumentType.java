package stevekung.mods.ytchat.command.arguments;

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

import net.minecraft.command.CommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.TextComponentString;
import stevekung.mods.stevekungslib.utils.LangUtils;
import stevekung.mods.ytchat.auth.Authentication;

public class YouTubeProfileArgumentType implements ArgumentType<String>
{
    private static final DynamicCommandExceptionType PROFILE_NOT_FOUND = new DynamicCommandExceptionType(obj -> new TextComponentString(LangUtils.translate("commands.ytc.not_found", obj)));
    private static final SimpleCommandExceptionType INVALID_ARGS = new SimpleCommandExceptionType(new TextComponentString(LangUtils.translate("argument.id.invalid")));

    public static YouTubeProfileArgumentType create()
    {
        return new YouTubeProfileArgumentType();
    }

    public static String getProfile(CommandContext<CommandSource> context, String name)
    {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        ArrayList<File> files = new ArrayList<>(Arrays.asList(Authentication.userDir.listFiles()));
        List<String> resList = files.stream().map(file -> file.getName()).collect(Collectors.toList());
        return YouTubeProfileArgumentType.suggestIterable(resList, builder);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        String fileName = YouTubeProfileArgumentType.read(reader);
        boolean exist = false;

        if (Authentication.userDir.exists())
        {
            for (File file : Authentication.userDir.listFiles())
            {
                String name = file.getName();

                if (name.equals(fileName))
                {
                    exist = true;
                }
            }
        }

        if (exist)
        {
            return fileName;
        }
        else
        {
            throw YouTubeProfileArgumentType.PROFILE_NOT_FOUND.create(fileName);
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
        YouTubeProfileArgumentType.applySuggest(iterable, typedString, string1 -> string1, builder::suggest);
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

        while (reader.canRead() && ResourceLocation.isValidPathCharacter(reader.peek()))
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
            throw YouTubeProfileArgumentType.INVALID_ARGS.createWithContext(reader);
        }
    }
}