package xyz.wagyourtail.jsmacros.client.api.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.arguments.*;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.Stack;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 1.4.2
 */
 @SuppressWarnings("unused")
public class CommandBuilder {

    private final LiteralArgumentBuilder<FabricClientCommandSource> head;
    private final Stack<ArgumentBuilder<FabricClientCommandSource, ?>> pointer = new Stack<>();

    public CommandBuilder(String name) {
        head = ClientCommandManager.literal(name);
        pointer.push(head);
    }

    private void argument(String name, Supplier<ArgumentType<?>> type) {
        ArgumentBuilder<FabricClientCommandSource, ?> arg = ClientCommandManager.argument(name, type.get());

        pointer.push(arg);
    }

    public CommandBuilder literalArg(String name) {
        ArgumentBuilder<FabricClientCommandSource, ?> arg = ClientCommandManager.literal(name);

        pointer.push(arg);
        return this;
    }

    public CommandBuilder angleArg(String name) {
        throw new NullPointerException("does not exist in 1.16.1");
    }

    public CommandBuilder blockArg(String name) {
        argument(name, BlockStateArgumentType::blockState);
        return this;
    }

    public CommandBuilder booleanArg(String name) {
        argument(name, BoolArgumentType::bool);
        return this;
    }

    public CommandBuilder colorArg(String name) {
        argument(name, ColorArgumentType::color);
        return this;
    }

    public CommandBuilder doubleArg(String name) {
        argument(name, DoubleArgumentType::doubleArg);
        return this;
    }

    public CommandBuilder doubleArg(String name, double min, double max) {
        argument(name, () -> DoubleArgumentType.doubleArg(min, max));
        return this;
    }

    public CommandBuilder floatRangeArg(String name) {
        argument(name, NumberRangeArgumentType.FloatRangeArgumentType::new);
        return this;
    }

    public CommandBuilder longArg(String name) {
        argument(name, LongArgumentType::longArg);
        return this;
    }

    public CommandBuilder longArg(String name, long min, long max) {
        argument(name, () -> LongArgumentType.longArg(min, max));
        return this;
    }

    public CommandBuilder identifierArg(String name) {
        argument(name, IdentifierArgumentType::identifier);
        return this;
    }

    public CommandBuilder intArg(String name) {
        argument(name, IntegerArgumentType::integer);
        return this;
    }

    public CommandBuilder intArg(String name, int min, int max) {
        argument(name, () -> IntegerArgumentType.integer(min, max));
        return this;
    }

    public CommandBuilder intRangeArg(String name) {
        argument(name, NumberRangeArgumentType::numberRange);
        return this;
    }

    public CommandBuilder itemArg(String name) {
        argument(name, ItemStackArgumentType::itemStack);
        return this;
    }

    public CommandBuilder nbtArg(String name) {
        argument(name, NbtCompoundTagArgumentType::nbtCompound);
        return this;
    }

    public CommandBuilder greedyStringArg(String name) {
        argument(name, StringArgumentType::greedyString);
        return this;
    }

    public CommandBuilder quotedStringArg(String name) {
        argument(name, StringArgumentType::string);
        return this;
    }

    public CommandBuilder wordArg(String name) {
        argument(name, StringArgumentType::word);
        return this;
    }

    public CommandBuilder textArgType(String name) {
        argument(name, TextArgumentType::text);
        return this;
    }

    public CommandBuilder uuidArgType(String name) {
        throw new NullPointerException("does not exist in 1.15.2");
    }

    public CommandBuilder regexArgType(String name, String regex, String flags) {
        int fg = 0;
        for (int i = 0; i < flags.length(); ++i) {
            switch (flags.charAt(i)) {
                case 'i':
                    fg += Pattern.CASE_INSENSITIVE;
                    break;
                case 's':
                    fg += Pattern.DOTALL;
                    break;
                case 'u':
                    fg += Pattern.UNICODE_CHARACTER_CLASS;
                    break;
            }
        }
        int finalFg = fg;
        argument(name, () -> new RegexArgType(regex, finalFg));
        return this;
    }

    public CommandBuilder executes(MethodWrapper<CommandContextHelper, Object, Boolean> callback) {
        pointer.peek().executes((ctx) -> callback.apply(new CommandContextHelper(ctx)) ? 1 : 0);
        return this;
    }

    public CommandBuilder or() {
        if (pointer.size() > 1) {
            ArgumentBuilder<FabricClientCommandSource, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    public CommandBuilder or(int argumentLevel) {
        argumentLevel = Math.max(1, argumentLevel);
        while (pointer.size() > argumentLevel) {
            ArgumentBuilder<FabricClientCommandSource, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    private static class RegexArgType implements ArgumentType<String[]> {

        Pattern pattern;

        public RegexArgType(String regex, int flags) {
            this.pattern = Pattern.compile(regex, flags);
        }

        @Override
        public String[] parse(StringReader reader) throws CommandSyntaxException {
            int i = reader.getCursor();
            Matcher m = pattern.matcher(reader.getRemaining());
            if (m.find() && m.start() == 0) {
                String[] args = new String[m.groupCount() + 1];
                for (int j = 0; j < args.length; ++j) {
                    args[j] = m.group(j);
                }
                reader.setCursor(i + m.group(0).length());
                return args;
            } else {
                throw new SimpleCommandExceptionType(new TranslatableText("jsmacros.commandfailedregex", "/" + pattern.pattern() + "/")).createWithContext(reader);
            }
        }
    }

    public void register() {
        or(1);
        ClientCommandManager.DISPATCHER.register(head);
        ClientPlayNetworkHandler cpnh = MinecraftClient.getInstance().getNetworkHandler();
        if (cpnh != null) {
        ClientCommandInternals.addCommands((CommandDispatcher) cpnh.getCommandDispatcher(), (FabricClientCommandSource) cpnh.getCommandSource());
        }
    }
}
