package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class BlockDataHelper extends BaseHelper<IBlockState> {
    private final Block b;
    private final BlockPos bp;
    private final TileEntity e;
    
    public BlockDataHelper(IBlockState b, TileEntity e, BlockPos bp) {
        super(b);
        this.b = b.getBlock();
        this.bp = bp;
        this.e = e;
    }
    
    /**
     * @since 1.1.7
     * 
     * @return the {@code x} value of the block.
     */
    public int getX() {
        return bp.getX();
    }
    
    /**
     * @since 1.1.7
     * 
     * @return the {@code y} value of the block.
     */
    public int getY() {
        return bp.getY();
    }
    
    /**
     * @since 1.1.7
     * 
     * @return the {@code z} value of the block.
     */
    public int getZ() {
        return bp.getZ();
    }
    
    /**
     * @return the item ID of the block.
     */
    public String getId() {
        return Block.blockRegistry.getNameForObject(b).toString();
    }
    
    /**
     * @return the translated name of the block.
     */
    public String getName() {
        return b.getLocalizedName();
    }
    
    /**
     * @return block NBT data as a {@link Map}.
     */
    public Map<String, String> getNBT() {
        if (e == null) return null;
        Map<String, String> m = new HashMap<>();
        NBTTagCompound t = e.serializeNBT();
        for (String s : t.getKeySet()) {
            m.put(s, t.getTag(s).toString());
        }
        return m;
    }
    
    /**
     * @since 1.1.7
     * 
     * @return block state data as a {@link Map}.
     */
    public Map<String, String> getBlockState() {
        Map<String, String> map = new HashMap<>();
        
        for (IProperty<?> e : base.getPropertyNames()) {
            map.put(e.getName(), base.getValue(e).toString());
        }
        return map;
    }
    
    /**
     * @since 1.2.7
     * 
     * @return the block pos.
     */
    public BlockPosHelper getBlockPos() {
        return new BlockPosHelper(bp);
    }
    
    public Block getRawBlock() {
        return b;
    }
    
    
    public IBlockState getRawBlockState() {
        return base;
    }
    
    public TileEntity getRawBlockEntity() {
        return e;
    }
    
    public String toString() {
        return String.format("BlockDataHelper:{\"x\":%d, \"y\":%d, \"z\":%d, \"id\":\"%s\"}", bp.getX(), bp.getY(), bp.getZ(), this.getId());
    }
}
