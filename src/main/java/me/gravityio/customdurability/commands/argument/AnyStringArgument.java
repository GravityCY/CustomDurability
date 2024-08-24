package me.gravityio.customdurability.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;
import java.util.List;

public class AnyStringArgument implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = List.of(
            "this_argument_takes_anything:colons*asterisks,everything_except_spaces!",
            "the_number_1:is_cool!"
    );

    public static AnyStringArgument string() {
        return new AnyStringArgument();
    }

    public static String get(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name,String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    @Override
    public Collection<String> getExamples() {
        return AnyStringArgument.EXAMPLES;
    }
}
