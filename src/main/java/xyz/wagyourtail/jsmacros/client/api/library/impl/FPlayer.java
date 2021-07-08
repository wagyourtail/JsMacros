package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.movement.MovementDummy;
import xyz.wagyourtail.jsmacros.client.movement.MovementQueue;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Functions for getting and modifying the player's state.
 *
 * An instance of this class is passed to scripts as the {@code Player} variable.
 *
 * @author Wagyourtail
 */
@Library("Player")
@SuppressWarnings("unused")
public class FPlayer extends BaseLibrary {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * @return the Inventory handler
     * @see xyz.wagyourtail.jsmacros.client.api.classes.Inventory
     */
    public Inventory<?> openInventory() {
        assert mc.player != null && mc.player.inventory != null;
        return Inventory.create();
    }

    /**
     * @return the player entity wrapper.
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper
     * @since 1.0.3
     */
    public ClientPlayerEntityHelper<ClientPlayerEntity> getPlayer() {
        assert mc.player != null;
        return new ClientPlayerEntityHelper<>(mc.player);
    }

    /**
     * @return the player's current gamemode.
     * @since 1.0.9
     */
    public String getGameMode() {
        assert mc.interactionManager != null;
        GameMode mode = mc.interactionManager.getCurrentGameMode();
        if (mode == null) mode = GameMode.NOT_SET;
        return mode.getName();
    }

    /**
     * @param distance
     * @param fluid
     * @return the block/liquid the player is currently looking at.
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper
     * @since 1.0.5
     */
    public BlockDataHelper rayTraceBlock(double distance, boolean fluid) {
        assert mc.world != null;
        assert mc.player != null;
        BlockHitResult h = (BlockHitResult) mc.player.raycast(distance, 0, fluid);
        if (h.getType() == HitResult.Type.MISS) return null;
        BlockState b = mc.world.getBlockState(h.getBlockPos());
        BlockEntity t = mc.world.getBlockEntity(h.getBlockPos());
        if (b.getBlock().equals(Blocks.VOID_AIR)) return null;
        return new BlockDataHelper(b, t, h.getBlockPos());
    }

    /**
     * @return the entity the player is currently looking at.
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper
     * @since 1.0.5
     */
    public EntityHelper<Entity> rayTraceEntity() {
        if (mc.targetedEntity != null) return new EntityHelper<>(mc.targetedEntity);
        else return null;
    }

    /**
     * Write to a sign screen if a sign screen is currently open.
     *
     * @param l1
     * @param l2
     * @param l3
     * @param l4
     * @return {@link java.lang.Boolean boolean} of success.
     * @since 1.2.2
     */
    public boolean writeSign(String l1, String l2, String l3, String l4) {
        if (mc.currentScreen instanceof SignEditScreen) {
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(0, l1);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(1, l2);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(2, l3);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(3, l4);
            return true;
        }
        return false;
    }

    /**
     * @param folder
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     * @see #takeScreenshot(String, String, MethodWrapper)
     * @since 1.2.6
     */
    public void takeScreenshot(String folder, MethodWrapper<TextHelper, Object, Object> callback) {
        assert folder != null;
        ScreenshotUtils.saveScreenshot(new File(Core.instance.config.macroFolder, folder), mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
                mc.getFramebuffer(), (text) -> {
                    if (callback != null) callback.accept(new TextHelper(text));
                });
    }

    /**
     * Take a screenshot and save to a file.
     * <p>
     * {@code file} is the optional one, typescript doesn't like it not being the last one that's optional
     *
     * @param folder
     * @param file
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     * @since 1.2.6
     */
    public void takeScreenshot(String folder, String file, MethodWrapper<TextHelper, Object, Object> callback) {
        assert folder != null && file != null;
        ScreenshotUtils.saveScreenshot(new File(Core.instance.config.macroFolder, folder), file, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
                mc.getFramebuffer(), (text) -> {
                    if (callback != null) callback.accept(new TextHelper(text));
                });
    }

    /**
     * Creates a new PlayerInput object.
     *
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput PlayerInput}.
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput createPlayerInput() {
        return new PlayerInput();
    }

    /**
     * Creates a new PlayerInput object.
     *
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput PlayerInput}.
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput createPlayerInput(double movementForward, double movementSideways, double yaw) {
        return new PlayerInput((float)movementForward, (float)movementSideways, (float)yaw);
    }

    /**
     * Creates a new PlayerInput object.
     *
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput PlayerInput}.
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput createPlayerInput(double movementForward, double yaw, boolean jumping, boolean sprinting) {
        return new PlayerInput((float)movementForward, (float)yaw, jumping, sprinting);
    }

    /**
     * Creates a new PlayerInput object.
     *
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput PlayerInput}.
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput createPlayerInput(double movementForward, double movementSideways, double yaw, double pitch, boolean jumping, boolean sneaking, boolean sprinting) {
        return new PlayerInput(movementForward, movementSideways, yaw, pitch, jumping, sneaking, sprinting);
    }


    /**
     * Parses each row of CSV string into a {@code PlayerInput}.
     * The capitalization of the header matters.<br>
     * About the columns:
     * <ul>
     *   <li> {@code movementForward} and {@code movementSideways} as a number</li>
     *   <li>{@code yaw} and {@code pitch} as an absolute number</li>
     *   <li>{@code jumping}, {@code sneaking} and {@code sprinting} have to be boolean</li>
     * </ul>
     * <p>
     * The separation must be a "," it's a csv...(but spaces don't matter)<br>
     * Quoted values don't work
     *
     * @param csv CSV string to be parsed
     * @return {@code List<PlayerInput>} Each row parsed as a {@code PlayerInput}
     * @see PlayerInput#PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public List<PlayerInput> createPlayerInputsFromCsv(String csv) throws NoSuchFieldException, IllegalAccessException {
        return PlayerInput.fromCsv(csv);
    }

    /**
     * Parses a JSON string into a {@code PlayerInput} Object
     * For details see {@code PlayerInput.fromCsv()}, on what has to be present.<br>
     * Capitalization of the keys matters.
     *
     * @param json JSON string to be parsed
     * @return The JSON parsed into a {@code PlayerInput}
     * @see #createPlayerInputsFromCsv(String)
     * @since 1.4.0
     */
    public PlayerInput createPlayerInputsFromJson(String json) throws NoSuchFieldException, IllegalAccessException {
        return PlayerInput.fromJson(json);
    }

    /**
     * Creates a new {@code PlayerInput} object with the current inputs of the player.
     *
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput PlayerInput} with the current inputs.
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput getCurrentPlayerInput() {
        assert mc.player != null;
        return new PlayerInput(mc.player.input, mc.player.yaw, mc.player.pitch, mc.player.isSprinting());
    }

    /**
     * Adds a new {@code PlayerInput} to {@code MovementQueue} to be executed
     *
     * @param input the PlayerInput to be executed
     * @see xyz.wagyourtail.jsmacros.client.movement.MovementQueue
     * @since 1.4.0
     */
    public void addInput(PlayerInput input) {
        MovementQueue.append(input, mc.player);
    }

    /**
     * Adds multiple new {@code PlayerInput} to {@code MovementQueue} to be executed
     *
     * @param inputs the PlayerInputs to be executed
     * @see xyz.wagyourtail.jsmacros.client.movement.MovementQueue
     * @since 1.4.0
     */
    public void addInputs(List<PlayerInput> inputs) {
        for (PlayerInput input : inputs) {
            addInput(input);
        }
    }

    /**
     * Clears all inputs in the {@code MovementQueue}
     *
     * @see xyz.wagyourtail.jsmacros.client.movement.MovementQueue
     * @since 1.4.0
     */
    public void clearInputs() {
        MovementQueue.clear();
    }

    public void setDrawPredictions(boolean val) {
        MovementQueue.setDrawPredictions(val);
    }

    /**
     * Predicts where one tick with a {@code PlayerInput} as input would lead to.
     *
     * @param input the PlayerInput for the prediction
     * @return the position after the input
     * @see #predictInput(PlayerInput, boolean)
     * @since 1.4.0
     */
    public PositionCommon.Pos3D predictInput(PlayerInput input) {
        return predictInput(input, false);
    }

    /**
     * Predicts where one tick with a {@code PlayerInput} as input would lead to.
     *
     * @param input the PlayerInput for the prediction
     * @param draw  whether to visualize the result or not
     * @return the position after the input
     * @since 1.4.0
     */
    public PositionCommon.Pos3D predictInput(PlayerInput input, boolean draw) {
        return predictInputs(Collections.singletonList(input), draw).get(0);
    }

    /**
     * Predicts where each {@code PlayerInput} executed in a row would lead
     * without drawing it.
     *
     * @param inputs the PlayerInputs for each tick for the prediction
     * @return the position after each input
     * @see #predictInputs(List, boolean)
     * @since 1.4.0
     */
    public List<PositionCommon.Pos3D> predictInputs(List<PlayerInput> inputs) {
        return predictInputs(inputs, false);
    }

    /**
     * Predicts where each {@code PlayerInput} executed in a row would lead
     *
     * @param inputs the PlayerInputs for each tick for the prediction
     * @param draw   whether to visualize the result or not
     * @return the position after each input
     * @since 1.4.0
     */
    public List<PositionCommon.Pos3D> predictInputs(List<PlayerInput> inputs, boolean draw) {
        assert mc.player != null;
        MovementDummy dummy = new MovementDummy(mc.player);
        List<PositionCommon.Pos3D> predictions = new ArrayList<>();
        for (PlayerInput input : inputs) {
            predictions.add(new PositionCommon.Pos3D(dummy.applyInput(input)));
        }
        if (draw) {
            for (PositionCommon.Pos3D point : predictions) {
                MovementQueue.predPoints.addPoint(point, 0.01, 0xff9500);
            }
        }
        return predictions;
    }

    /**
     * Adds a forward movement with a relative yaw value to the MovementQueue.
     *
     * @param yaw the relative yaw for the player
     * @since 1.4.0
     */
    public void moveForward(float yaw) {
        PlayerInput input = new PlayerInput();
        input.movementForward = 1.0F;
        input.yaw = getPlayer().getYaw() + yaw;
        addInput(input);
    }

    /**
     * Adds a backward movement with a relative yaw value to the MovementQueue.
     *
     * @param yaw the relative yaw for the player
     * @since 1.4.0
     */
    public void moveBackward(float yaw) {
        PlayerInput input = new PlayerInput();
        input.movementForward = -1.0F;
        input.yaw = getPlayer().getYaw() + yaw;
        addInput(input);
    }

    /**
     * Adds sideways movement with a relative yaw value to the MovementQueue.
     * @param yaw the relative yaw for the player
     * @since 1.4.2
     */
    public void moveStrafeLeft(float yaw) {
        PlayerInput input = new PlayerInput();
        input.movementSideways = -1.0F;
        input.yaw = getPlayer().getYaw() + yaw;
        addInput(input);
    }

    /**
     * Adds sideways movement with a relative yaw value to the MovementQueue.
     * @param yaw the relative yaw for the player
     * @since 1.4.2
     */
    public void moveStrafeRight(float yaw) {
        PlayerInput input = new PlayerInput();
        input.movementSideways = 1.0F;
        input.yaw = getPlayer().getYaw() + yaw;
        addInput(input);
    }
}
