package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

public class CommandContextHelper extends BaseHelper<CommandContext<ICommandSender>> {
    public CommandContextHelper(CommandContext<ICommandSender> base) {
        super(base);
    }

    public Object getArg(String name) throws CommandSyntaxException {
        Object arg = base.getArgument(name, Object.class);
        if (arg instanceof Block) {
            arg = Block.blockRegistry.getNameForObject((Block) arg).toString();
        } else if (arg instanceof ResourceLocation) {
            arg = arg.toString();
        } else if (arg instanceof Item) {
            arg = Item.itemRegistry.getNameForObject((Item) arg).toString();
        } else if (arg instanceof NBTTagCompound) {
            arg = arg.toString();
        } else if (arg instanceof ChatComponentText) {
            arg = new TextHelper((ChatComponentText) arg);
        } else if (arg instanceof EnumChatFormatting) {
            arg = ((EnumChatFormatting) arg).getFriendlyName();
        }
        return arg;
    }

    public CommandContextHelper getChild() {
        return new CommandContextHelper(base.getChild());
    }

    public StringRange getRange() {
        return base.getRange();
    }

    public String getInput() {
        return base.getInput();
    }
}
