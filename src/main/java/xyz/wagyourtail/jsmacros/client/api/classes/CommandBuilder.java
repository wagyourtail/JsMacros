package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.BuiltInExceptions;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.block.Block;
import net.minecraft.command.*;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.client.ClientCommandHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @since 1.4.2
 */
 @SuppressWarnings("unused")
public class CommandBuilder {

    private static final CommandDispatcher<ICommandSender> dispatcher = new CommandDispatcher<>();
    private static final BuiltInExceptions exception = new BuiltInExceptions();
    private final LiteralArgumentBuilder<ICommandSender> head;
    private final Stack<ArgumentBuilder<ICommandSender, ?>> pointer = new Stack<>();

    public CommandBuilder(String name) {
        head = LiteralArgumentBuilder.literal(name);
        pointer.push(head);
    }

    private void argument(String name, Supplier<ArgumentType<?>> type) {
        ArgumentBuilder<ICommandSender, ?> arg = RequiredArgumentBuilder.argument(name, type.get());

        pointer.push(arg);
    }

    public CommandBuilder literalArg(String name) {
        ArgumentBuilder<ICommandSender, ?> arg = LiteralArgumentBuilder.literal(name);

        pointer.push(arg);
        return this;
    }

    public CommandBuilder angleArg(String name) {
        throw new NullPointerException("does not exist in 1.16.1");
    }

    public CommandBuilder blockArg(String name) {
        argument(name, BlockArgumentType::block);
        return this;
    }

    public CommandBuilder booleanArg(String name) {
        argument(name, BoolArgumentType::bool);
        return this;
    }

    public CommandBuilder colorArg(String name) {
        argument(name, ColorArgumentType::new);
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
        argument(name, IdentifierArgumentType::new);
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
        argument(name, NumberRangeArgumentType.IntegerRangeArgumentType::new);
        return this;
    }

    public CommandBuilder itemArg(String name) {
        argument(name, ItemArgumentType::new);
        return this;
    }

    public CommandBuilder nbtArg(String name) {
        argument(name, NBTArgumentType::new);
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
        argument(name, TextArgumentType::new);
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
            ArgumentBuilder<ICommandSender, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    public CommandBuilder or(int argumentLevel) {
        argumentLevel = Math.max(1, argumentLevel);
        while (pointer.size() > argumentLevel) {
            ArgumentBuilder<ICommandSender, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    public void register() {
        or(1);
        LiteralCommandNode<ICommandSender> node = dispatcher.register(head);
        ClientCommandHandler.instance.registerCommand(new ICommand() {
            @Override
            public String getCommandName() {
                return node.getName();
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return node.getUsageText();
            }

            @Override
            public List<String> getCommandAliases() {
                return new ArrayList<>();
            }

            @Override
            public void processCommand(ICommandSender sender, String[] args) throws CommandException {
                try {
                    dispatcher.execute(getCommandName() + (args.length > 0 ? " " + String.join(" ", args) : ""), sender);
                } catch (CommandSyntaxException e) {
                    throw new CommandException(e.getMessage());
                }
            }

            @Override
            public boolean canCommandSenderUseCommand(ICommandSender sender) {
                return true;
            }

            @Override
            public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                ParseResults<ICommandSender> pr = dispatcher.parse(getCommandName() + (args.length > 0 ? " " + String.join(" ", args) : ""), sender);
                try {
                    return dispatcher.getCompletionSuggestions(pr).get().getList().stream().map(Suggestion::getText).collect(Collectors.toList());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    throw new RuntimeException("error");
                }
            }

            @Override
            public boolean isUsernameIndex(String[] args, int index) {
                return false;
            }

            @Override
            public int compareTo(@NotNull ICommand o) {
                return getCommandName().compareTo(o.getCommandName());
            }
        });
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
                throw new SimpleCommandExceptionType((Message) new ChatComponentTranslation("jsmacros.commandfailedregex", "/" + pattern.pattern() + "/")).createWithContext(reader);
            }
        }
    }

    private static class BlockArgumentType implements ArgumentType<Block> {


        @Override
        public Block parse(StringReader reader) throws CommandSyntaxException {
            try {
                return CommandBase.getBlockByText(null, reader.readStringUntil(' '));
            } catch (NumberInvalidException e) {
                throw exception.readerInvalidInt().create(e.getMessage());
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CompletableFuture.supplyAsync(() -> {
                List<Suggestion> sugs = CommandBase.getListOfStringsMatchingLastWord(new String[] {builder.getRemaining()}, Block.blockRegistry.getKeys()).stream().map(e -> new Suggestion(null, e)).collect(Collectors.toList());
                return new Suggestions(null, sugs);
            });
        }

        public static BlockArgumentType block() {
            return new BlockArgumentType();
        }
    }

    public static class ColorArgumentType implements ArgumentType<EnumChatFormatting> {

        @Override
        public EnumChatFormatting parse(StringReader reader) throws CommandSyntaxException {
            try {
                return EnumChatFormatting.getValueByName(reader.readStringUntil(' '));
            } catch (IllegalArgumentException e) {
                throw exception.readerExpectedSymbol().create(e.getMessage());
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CompletableFuture.supplyAsync(() -> {
                List<Suggestion> sugs = CommandBase.getListOfStringsMatchingLastWord(new String[] {builder.getRemaining()}, Arrays.stream(EnumChatFormatting.values()).map(EnumChatFormatting::getFriendlyName).collect(Collectors.toList())).stream().map(e -> new Suggestion(null, e)).collect(Collectors.toList());
                return new Suggestions(null, sugs);
            });
        }

    }

    public static class NBTArgumentType implements ArgumentType<NBTTagCompound> {

        @Override
        public NBTTagCompound parse(StringReader reader) throws CommandSyntaxException {
            try {
                int cursor = reader.getCursor();
                String s = reader.getRemaining();
                if (!s.startsWith("{")) throw exception.readerExpectedSymbol().create("{");
                Matcher m = Pattern.compile("[{}]").matcher(s);
                int i = 0;
                while (m.find()) {
                    if (m.group().equals("{")) ++i;
                    else if (--i == 0) {
                        break;
                    }
                }
                reader.setCursor(cursor + m.end());
                return JsonToNBT.getTagFromJson(s.substring(0, m.end()));
            } catch (NBTException e) {
                throw exception.readerExpectedSymbol().create(e.getStackTrace());
            }
        }
    }

    public static class TextArgumentType implements ArgumentType<IChatComponent> {

        @Override
        public IChatComponent parse(StringReader reader) throws CommandSyntaxException {
            int cursor = reader.getCursor();
            String s = reader.getRemaining();
            if (s.startsWith("\"")) {
                return new ChatComponentText(reader.readQuotedString());
            }
            if (!s.startsWith("{") && !s.startsWith("[")) throw exception.readerExpectedSymbol().create("{");
            Matcher m = Pattern.compile("[\\[\\]{}]").matcher(s);
            int i = 0;
            while (m.find()) {
                if (m.group().equals("{") || m.group().equals("[")) ++i;
                else if (--i == 0) {
                    break;
                }
            }
            reader.setCursor(cursor + m.end());
            try {
                return IChatComponent.Serializer.jsonToComponent(s.substring(0, m.end()));
            } catch (JsonSyntaxException e) {
                throw exception.readerExpectedSymbol().create(e.getMessage());
            }
        }
    }

    public static class ItemArgumentType implements ArgumentType<Item> {

        @Override
        public Item parse(StringReader reader) throws CommandSyntaxException {
            try {
                return CommandBase.getItemByText(null, reader.readStringUntil(' '));
            } catch (NumberInvalidException e) {
                throw exception.readerInvalidInt().create(e.getMessage());
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CompletableFuture.supplyAsync(() -> {
                List<Suggestion> sugs = CommandBase.getListOfStringsMatchingLastWord(new String[] {builder.getRemaining()}, Item.itemRegistry.getKeys()).stream().map(e -> new Suggestion(null, e)).collect(Collectors.toList());
                return new Suggestions(null, sugs);
            });
        }

    }

    public static class IdentifierArgumentType implements ArgumentType<ResourceLocation> {

        @Override
        public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
            return new ResourceLocation(reader.readStringUntil(' '));
        }

    }

    public interface NumberRangeArgumentType<T extends NumberRangeArgumentType.NumberRange<?>> extends ArgumentType<T> {


        class FloatRangeArgumentType implements NumberRangeArgumentType<NumberRange.FloatRange> {

            @Override
            public NumberRange.FloatRange parse(StringReader reader) throws CommandSyntaxException {
                return NumberRange.FloatRange.parse(reader);
            }

        }

        class IntegerRangeArgumentType implements NumberRangeArgumentType<NumberRange.IntRange> {

            @Override
            public NumberRange.IntRange parse(StringReader reader) throws CommandSyntaxException {
                return NumberRange.IntRange.parse(reader);
            }

        }


        abstract class NumberRange<U> {
            protected final U min;
            private final U max;

            protected NumberRange(U min, U max) {
                this.min = min;
                this.max = max;
            }

            public U getMin() {
                return min;
            }

            public U getMax() {
                return max;
            }

            public boolean isDummy() {
                return this.min == null && this.max == null;
            }

            protected static <T extends Number, R extends NumberRange<T>> R parse(StringReader commandReader, CommandFactory<T, R> commandFactory, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier, Function<T, T> mapper) throws CommandSyntaxException {
                if (!commandReader.canRead()) {
                    throw exception.dispatcherUnknownArgument().create();
                } else {
                    int i = commandReader.getCursor();

                    try {
                        T number = map(fromStringReader(commandReader, converter, exceptionTypeSupplier), mapper);
                        T number3;
                        if (commandReader.canRead(2) && commandReader.peek() == '.' && commandReader.peek(1) == '.') {
                            commandReader.skip();
                            commandReader.skip();
                            number3 = map(fromStringReader(commandReader, converter, exceptionTypeSupplier), mapper);
                            if (number == null && number3 == null) {
                                throw exception.dispatcherParseException().create("ended early");
                            }
                        } else {
                            number3 = number;
                        }

                        if (number == null && number3 == null) {
                            throw exception.dispatcherParseException().create("ended early");
                        } else {
                            return commandFactory.create(commandReader, number, number3);
                        }
                    } catch (CommandSyntaxException var8) {
                        commandReader.setCursor(i);
                        throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), i);
                    }
                }
            }

            @Nullable
            private static <T extends Number> T fromStringReader(StringReader reader, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier) throws CommandSyntaxException {
                int i = reader.getCursor();

                while(reader.canRead() && isNextCharValid(reader)) {
                    reader.skip();
                }

                String string = reader.getString().substring(i, reader.getCursor());
                if (string.isEmpty()) {
                    return null;
                } else {
                    try {
                        return converter.apply(string);
                    } catch (NumberFormatException var6) {
                        throw exceptionTypeSupplier.get().createWithContext(reader, string);
                    }
                }
            }

            private static boolean isNextCharValid(StringReader reader) {
                char c = reader.peek();
                if ((c < '0' || c > '9') && c != '-') {
                    if (c != '.') {
                        return false;
                    } else {
                        return !reader.canRead(2) || reader.peek(1) != '.';
                    }
                } else {
                    return true;
                }
            }

            @Nullable
            private static <T> T map(@Nullable T object, Function<T, T> function) {
                return object == null ? null : function.apply(object);
            }



            public static class FloatRange extends NumberRange<Float> {

                protected FloatRange(Float min, Float max) {
                    super(min, max);
                }

                private static NumberRange.FloatRange create(StringReader reader, @Nullable Float min, @Nullable Float max) throws CommandSyntaxException {
                    if (min != null && max != null && min > max) {
                        throw exception.readerInvalidFloat().create(max);
                    } else {
                        return new NumberRange.FloatRange(min, max);
                    }
                }

                public static NumberRange.FloatRange parse(StringReader reader) throws CommandSyntaxException {
                    return parse(reader, (float_) -> float_);
                }

                public static NumberRange.FloatRange parse(StringReader reader, Function<Float, Float> mapper) throws CommandSyntaxException {
                    return parse(reader, FloatRange::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, mapper);
                }
            }

            public static class IntRange extends NumberRange<Integer> {

                protected IntRange(Integer min, Integer max) {
                    super(min, max);
                }

                private static NumberRange.IntRange parse(StringReader reader, @Nullable Integer min, @Nullable Integer max) throws CommandSyntaxException {
                    if (min != null && max != null && min > max) {
                        throw exception.readerInvalidInt().create(max);
                    } else {
                        return new NumberRange.IntRange(min, max);
                    }
                }

                public static NumberRange.IntRange parse(StringReader reader) throws CommandSyntaxException {
                    return fromStringReader(reader, (integer) -> integer);
                }

                public static NumberRange.IntRange fromStringReader(StringReader reader, Function<Integer, Integer> converter) throws CommandSyntaxException {
                    //this is not redundant, it fails to compile without -_-
                    return NumberRange.<Integer, IntRange>parse(reader, IntRange::parse, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, converter);
                }

            }
        }

        @FunctionalInterface
        interface CommandFactory<T extends Number, R extends NumberRange<T>> {
            R create(StringReader reader, @Nullable T min, @Nullable T max) throws CommandSyntaxException;
        }
    }
}
