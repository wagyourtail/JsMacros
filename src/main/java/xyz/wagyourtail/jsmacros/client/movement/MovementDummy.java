package xyz.wagyourtail.jsmacros.client.movement;

import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("EntityConstructor")
public class MovementDummy extends LivingEntity {

    private List<Vec3d> coordsHistory = new ArrayList<>();
    private List<PlayerInput> inputs = new ArrayList<>();

    // Is used for checking the depthstrider enchant
    private Map<EquipmentSlot, ItemStack> equippedStack = new HashMap<>(6);
    private int jumpingCooldown;

    public MovementDummy(MovementDummy player) {
        this(player.getEntityWorld(), player.getPos(), player.getVelocity(), player.getBoundingBox(), player.onGround, player.isSprinting(), player.isSneaking());
        this.inputs = new ArrayList<>(player.getInputs());
        this.coordsHistory = new ArrayList<>(player.getCoordsHistory());
        this.jumpingCooldown = player.jumpingCooldown;
        this.equippedStack = player.equippedStack;
    }

    public MovementDummy(ClientPlayerEntity player) {
        this(player.getEntityWorld(), player.getPos(), player.getVelocity(), player.getBoundingBox(), player.onGround, player.isSprinting(), player.isSneaking());
        for (EquipmentSlot value : EquipmentSlot.values()) {
            equippedStack.put(value, player.getEquippedStack(value).copy());
        }
    }

    public MovementDummy(World world, Vec3d pos, Vec3d velocity, Box hitBox, boolean onGround, boolean isSprinting, boolean isSneaking) {
        super(EntityType.PLAYER, world);
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
        this.setVelocity(velocity);
        this.setBoundingBox(hitBox);
        this.setSprinting(isSprinting);
        this.setSneaking(isSneaking);
        this.stepHeight = 0.6F;
        this.onGround = onGround;
        this.coordsHistory.add(this.getPos());

        for (EquipmentSlot value : EquipmentSlot.values()) {
            equippedStack.put(value, new ItemStack(null));
        }
    }

    public List<Vec3d> getCoordsHistory() {
        return coordsHistory;
    }

    public List<PlayerInput> getInputs() {
        return inputs;
    }

    public Vec3d applyInput(PlayerInput input) {
        inputs.add(input); // We use this and not the clone, since the clone may be modified?
        PlayerInput currentInput = input.clone();
        this.yaw = currentInput.yaw;

        Vec3d velocity = this.getVelocity();
        double velX = velocity.x;
        double velY = velocity.y;
        double velZ = velocity.z;
        if (Math.abs(velocity.x) < 0.003D) {
            velX = 0.0D;
        }
        if (Math.abs(velocity.y) < 0.003D) {
            velY = 0.0D;
        }
        if (Math.abs(velocity.z) < 0.003D) {
            velZ = 0.0D;
        }
        this.setVelocity(velX, velY, velZ);

        /** Sneaking start **/
        if (this.isSneaking() && this.wouldPoseNotCollide(EntityPose.CROUCHING)) {
            // Yeah this looks dumb, but that is the way minecraft does it
            currentInput.movementSideways = (float) ((double) currentInput.movementSideways * 0.3D);
            currentInput.movementForward = (float) ((double) currentInput.movementForward * 0.3D);
        }
        this.setSneaking(currentInput.sneaking);
        /** Sneaking end **/

        /** Sprinting start **/
        boolean hasHungerToSprint = true;
        if (!this.isSprinting() && !currentInput.sneaking && hasHungerToSprint && !this.hasStatusEffect(StatusEffects.BLINDNESS) && currentInput.sprinting) {
            this.setSprinting(true);
        }

        if (this.isSprinting() && (currentInput.movementForward <= 1.0E-5F || this.horizontalCollision)) {
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
        /** Juming END **/

        this.travel(new Vec3d(currentInput.movementSideways * 0.98, 0.0, currentInput.movementForward * 0.98));

        /* flyingSpeed only gets set after travel */
        this.flyingSpeed = this.isSprinting() ? 0.026F : 0.02F;

        return this.getPos();
    }

    /**
     * We have to do this "inject" since the the applyClimbingSpeed() method
     * in LivingEntity is checking if we are a PlayerEntity, we want to apply the outcome of this check,
     * so this is why we need to set the y-velocity to 0.<p>
     */
    @Override
    public void move(MovementType mt, Vec3d velocity) {
        if (this.isClimbing() && velocity.getY() < 0.0D && this.getBlockState().getBlock() != Blocks.SCAFFOLDING && this.isHoldingOntoLadder()) {
            this.setVelocity(velocity.getX(), 0, velocity.getZ());
        }
        super.move(mt, this.getVelocity());
    }

    @Override
    protected boolean canClimb() {
        return !this.onGround || !this.isSneaking();
    }

    @Override
    public boolean canMoveVoluntarily() {
        return true;
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.setMovementSpeed(sprinting ? 0.13F : 0.1F);
    }

    @Override
    public ItemStack getMainHandStack() {
        return new ItemStack(Items.AIR);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return equippedStack.get(slot);
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public Arm getMainArm() {
        // This is just for rendering
        return Arm.RIGHT;
    }

    @Override
    public MovementDummy clone() {
        return new MovementDummy(this);
    }
}
