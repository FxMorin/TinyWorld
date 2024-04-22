package ca.fxco.TinyWorld.commands.arguments;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static net.minecraft.commands.SharedSuggestionProvider.suggest;
import static net.minecraft.commands.arguments.coordinates.WorldCoordinate.ERROR_EXPECTED_INT;

public class SectionPosArgument implements ArgumentType<SectionPos> {
    private static final Collection<String> EXAMPLES = Arrays.asList(
            "0 0 0", " -1 2 5" // "~ ~ ~", "~1 ~ ~-2", "^ ^ ^", "^-1 ^1 ^0"
    );
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(
            Component.translatable("argument.pos3d.incomplete")
    );

    public SectionPosArgument() {}

    public static SectionPosArgument sectionPos() {
        return new SectionPosArgument();
    }

    public static SectionPos getSectionPos(CommandContext<FabricClientCommandSource> commandContext, String string) {
        return commandContext.getArgument(string, SectionPos.class);
    }

    public SectionPos parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        int x = parseInt(stringReader);
        if (stringReader.canRead() && stringReader.peek() == ' ') {
            stringReader.skip();
            int y = parseInt(stringReader);
            if (stringReader.canRead() && stringReader.peek() == ' ') {
                stringReader.skip();
                int z = parseInt(stringReader);
                return SectionPos.of(x, y, z);
            } else {
                stringReader.setCursor(i);
                throw ERROR_NOT_COMPLETE.createWithContext(stringReader);
            }
        } else {
            stringReader.setCursor(i);
            throw ERROR_NOT_COMPLETE.createWithContext(stringReader);
        }
    }

    public static int parseInt(StringReader stringReader) throws CommandSyntaxException {
        if (!stringReader.canRead()) {
            throw ERROR_EXPECTED_INT.createWithContext(stringReader);
        }
        if (stringReader.peek() != ' ') {
            return stringReader.readInt();
        }
        return 0;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext,
                                                          SuggestionsBuilder suggestionsBuilder) {
        if (!(commandContext.getSource() instanceof FabricClientCommandSource clientCommandSource)) {
            return Suggestions.empty();
        }

        String string = suggestionsBuilder.getRemaining();
        return suggestSection(clientCommandSource, string, suggestionsBuilder, Commands.createValidator(this::parse));
    }

    private static CompletableFuture<Suggestions> suggestSection(FabricClientCommandSource commandSource,
                                                                 String string, SuggestionsBuilder suggestionsBuilder,
                                                                 Predicate<String> predicate) {
        List<String> list = Lists.newArrayList();
        LocalPlayer player = commandSource.getPlayer();
        if (player == null) {
            return suggest(list, suggestionsBuilder);
        }
        SectionPos pos = SectionPos.of(player.blockPosition());
        if (Strings.isNullOrEmpty(string)) {
            String sectionPosStr = pos.x() + " " + pos.y() + " " + pos.z();
            if (predicate.test(sectionPosStr)) {
                list.add(pos.x() + "");
                list.add(pos.x() + " " + pos.y());
                list.add(sectionPosStr);
            }
        } else {
            String[] strings = string.split(" ");
            if (strings.length == 1) {
                String sectionPosStr = strings[0] + " " + pos.y() + " " + pos.z();
                if (predicate.test(sectionPosStr)) {
                    list.add(strings[0] + " " + pos.y());
                    list.add(sectionPosStr);
                }
            } else if (strings.length == 2) {
                String sectionPosStr = strings[0] + " " + strings[1] + " " + pos.z();
                if (predicate.test(sectionPosStr)) {
                    list.add(sectionPosStr);
                }
            }
        }

        return suggest(list, suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
