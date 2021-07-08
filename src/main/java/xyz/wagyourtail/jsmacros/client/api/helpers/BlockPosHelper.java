package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.util.BlockPos;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.2.6
 */
@SuppressWarnings("unused")
public class BlockPosHelper extends BaseHelper<BlockPos> {
    
    public BlockPosHelper(BlockPos b) {
        super(b);
    }
    
    /**
     * @since 1.2.6
     * @return the {@code x} value of the block.
     */
    public int getX() {
        return base.getX();
    }
    
    /**
     * @since 1.2.6
     * @return the {@code y} value of the block.
     */
    public int getY() {
        return base.getY();
    }
    
    /**
     * @since 1.2.6
     * @return the {@code z} value of the block.
     */
    public int getZ() {
        return base.getZ();
    }
    
    public String toString() {
        return String.format("BlockPosHelper:{\"x\": %d, \"y\": %d, \"z\": %d}", base.getX(), base.getY(), base.getZ());
    }
}
