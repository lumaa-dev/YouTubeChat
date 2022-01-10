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

package com.stevekung.ytc.command.arguments;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.stevekung.ytc.service.AuthService;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class AuthProfileArgumentType implements ArgumentType<String>
{
    private static final SimpleCommandExceptionType INVALID_ARGS = new SimpleCommandExceptionType(new TranslatableComponent("argument.id.invalid"));

    private AuthProfileArgumentType() {}

    public static AuthProfileArgumentType create()
    {
        return new AuthProfileArgumentType();
    }

    public static String getProfile(CommandContext<?> context, String name)
    {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        var suggestions = Suggestions.empty();

        if (AuthService.USER_DIR.listFiles() != null)
        {
            var resList = Arrays.stream(AuthService.USER_DIR.listFiles()).map(File::getName).collect(Collectors.toList());
            suggestions = AuthProfileArgumentType.suggestIterable(resList, builder);
        }
        return suggestions;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        return AuthProfileArgumentType.read(reader);
    }

    @Override
    public Collection<String> getExamples()
    {
        return Collections.singletonList("profile");
    }

    private static CompletableFuture<Suggestions> suggestIterable(Iterable<String> iterable, SuggestionsBuilder builder)
    {
        var typedString = builder.getRemaining().toLowerCase(Locale.ROOT);
        AuthProfileArgumentType.applySuggest(iterable, typedString, string1 -> string1, builder::suggest);
        return builder.buildFuture();
    }

    private static void applySuggest(Iterable<String> iterable, String typedString, Function<String, String> function, Consumer<String> consumer)
    {
        for (var name : iterable)
        {
            var name2 = function.apply(name);

            if (name2.startsWith(typedString))
            {
                consumer.accept(name);
            }
        }
    }

    private static String read(StringReader reader) throws CommandSyntaxException
    {
        var cursor = reader.getCursor();

        while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek()))
        {
            reader.skip();
        }

        var string = reader.getString().substring(cursor, reader.getCursor());

        try
        {
            return string;
        }
        catch (ResourceLocationException e)
        {
            reader.setCursor(cursor);
            throw AuthProfileArgumentType.INVALID_ARGS.createWithContext(reader);
        }
    }
}