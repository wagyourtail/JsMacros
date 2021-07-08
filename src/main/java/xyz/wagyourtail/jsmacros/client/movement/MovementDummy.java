package xyz.wagyourtail.jsmacros.client.movement;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MovementDummy extends EntityPlayer {

    public List<PositionCommon.Pos3D> coordsHistory = new ArrayList<>();
    private PlayerInput currentInput;
    private int jumpingCooldown;
    private float walkSpeed;

    private ItemStack heldItem = null;

    private final List<ItemStack> armorItems = new ArrayList<>(4);
    private final List<ItemStack> equippedStack = new ArrayList<>(5);

    public MovementDummy(World wasd) {
        super(wasd, null);
        throw new RuntimeException("Shouldn't be called like this");
    }

    public MovementDummy(EntityPlayerSP player) {
        this(player.worldObj, new PositionCommon.Pos3D(player.posX, player.posY, player.posZ), new PositionCommon.Pos3D(player.motionX, player.motionY, player.motionZ), player.getEntityBoundingBox(), player.onGround, player.isSprinting(), player.isSneaking());
        this.walkSpeed = player.capabilities.getWalkSpeed();
        heldItem = player.getHeldItem();
        for (int i = 0; i < 4; ++i) {
            ItemStack armor = player.getCurrentArmor(i);
            if (armor != null) armor = armor.copy();
            this.armorItems.add(armor);
        }
        for (int i = 0; i < 5; ++i) {
            ItemStack equipment = player.getEquipmentInSlot(i);
            if (equipment != null) equipment = equipment.copy();
            this.equippedStack.add(equipment);
        }
    }

    public MovementDummy(World world, PositionCommon.Pos3D pos, PositionCommon.Pos3D velocity, AxisAlignedBB hitBox, boolean onGround, boolean isSprinting, boolean isSneaking) {
        super(world, new GameProfile(UUID.randomUUID(), "dummy"));
        this.setPosition(pos.getX(), pos.getY(), pos.getZ());
        this.setVelocity(velocity.x, velocity.y, velocity.z);
        this.setEntityBoundingBox(hitBox);
        this.setSprinting(isSprinting);
        this.setSneaking(isSneaking);
        this.stepHeight = 0.6F;
        this.onGround = onGround;
        this.coordsHistory.add(pos);
        for (int i = 0; i < 4; ++i) {
            this.armorItems.add(null);
        }
        for (int i = 0; i < 5; ++i) {
            equippedStack.add(null);
        }
    }

    public PositionCommon.Pos3D applyInput(PlayerInput input) {
        this.currentInput = input.clone();
        this.rotationYaw = currentInput.yaw;

        double velX = this.motionX;
        double velY = this.motionY;
        double velZ = this.motionZ;
        if (Math.abs(this.motionX) < 0.003D) {
            velX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.003D) {
            velY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.003D) {
            velZ = 0.0D;
        }
        this.setVelocity(velX, velY, velZ);

        /** Sneaking start **/
        if (this.isSneaking()) {
            // Yeah this looks dumb, but that is the way minecraft does it
            currentInput.movementSideways = (float) ((double) currentInput.movementSideways * 0.3D);
            currentInput.movementForward = (float) ((double) currentInput.movementForward * 0.3D);
        }
        this.setSneaking(currentInput.sneaking);
        /** Sneaking end **/

        /** Sprinting start **/
        boolean hasHungerToSprint = true;
        if (!this.isSprinting() && !this.currentInput.sneaking && hasHungerToSprint && this.getActivePotionEffect(Potion.blindness) != null && this.currentInput.sprinting) {
            this.setSprinting(true);
        }

        if (this.isSprinting() && (this.currentInput.movementForward <= 1.0E-5F || this.isCollidedHorizontally)) {
            this.setSprinting(false);
        }
        /** Sprinting end **/

        /** Jumping start **/
        if (this.jumpingCooldown > 0) {
            --this.jumpingCooldown;
        }

        if (currentInput.jumping) {
            if (this.onGround && this.jumpingCooldown == 0) {
                this.jump();
                this.jumpingCooldown = 10;
            }
        } else {
            this.jumpingCooldown = 0;
        }
        /** Jumping END **/

        this.moveStrafing = this.currentInput.movementSideways;
        this.moveForward = this.currentInput.movementForward;

        PositionCommon.Pos3D velocity = movementInputToVelocity(new PositionCommon.Pos3D(this.moveForward, 0, this.moveStrafing), walkSpeed, rotationYaw);
        motionX += velocity.x;
        motionY += velocity.y;
        motionZ += velocity.z;

        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        return new PositionCommon.Pos3D(this.posX, this.posY, this.posZ);
    }

    private static PositionCommon.Pos3D movementInputToVelocity(PositionCommon.Pos3D movementInput, float speed, float yaw) {
        double d = movementInput.toVector().getMagnitude();
        d = d * d;
        if (d < 1.0E-7D) {
            return PositionCommon.Pos3D.ZERO;
        } else {
            PositionCommon.Pos3D vec3d = (d > 1.0D ? normalize(movementInput) : movementInput).multiply(new PositionCommon.Pos3D(speed, speed, speed));
            float f = MathHelper.sin(yaw * 0.017453292F);
            float g = MathHelper.cos(yaw * 0.017453292F);
            return new PositionCommon.Pos3D(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
        }
    }

    private static PositionCommon.Pos3D normalize(PositionCommon.Pos3D start) {
        double d = MathHelper.sqrt_double(start.x * start.x + start.y * start.y + start.z * start.z);
        return d < 1.0E-4D ? PositionCommon.Pos3D.ZERO : new PositionCommon.Pos3D(start.x / d, start.y / d, start.z / d);
    }

    @Override
    public boolean isServerWorld() {
        return false;
    }

    @Override
    public ItemStack getHeldItem() {
        return heldItem;
    }

    @Override
    public ItemStack getEquipmentInSlot(int slotIn) {
        return equippedStack.get(slotIn);
    }

    @Override
    public ItemStack getCurrentArmor(int slotIn) {
        return armorItems.get(slotIn);
    }

    @Override
    public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {

    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public void addMovementStat(double p_71000_1_, double p_71000_3_, double p_71000_5_) {
    }

    @Override
    public ItemStack[] getInventory() {
        return new ItemStack[0];
    }

    /**
     * We have to do this "inject" since the the applyClimbingSpeed() method
     * in LivingEntity is checking if we are a PlayerEntity, we want to apply the outcome of this check,
     * so this is why we need to set the y-velocity to 0.<p>
     */
//    @Override
//    public Vec3d method_26318(Vec3d movementInput, float f) {
//        if (this.isClimbing() && this.getVelocity().getY() < 0.0D && !this.getBlockState().isOf(Blocks.SCAFFOLDING) && this.isHoldingOntoLadder()) {
//            this.setVelocity(this.getVelocity().getX(), 0, this.getVelocity().getZ());
//        }
//        return super.method_26318(movementInput, f);
//    }

}
