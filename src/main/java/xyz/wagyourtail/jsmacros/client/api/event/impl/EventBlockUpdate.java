package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "BlockUpdate", oldName = "BLOCK_UPDATE")
public class EventBlockUpdate implements BaseEvent {
    public final BlockDataHelper block;
    public final String updateType;
    
    public EventBlockUpdate(IBlockState block, TileEntity blockEntity, BlockPos blockPos, String updateType) {
        this.block = new BlockDataHelper(block, blockEntity, blockPos);
        this.updateType = updateType;
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"block\": %s}", this.getEventName(), block.toString());
    }
}
