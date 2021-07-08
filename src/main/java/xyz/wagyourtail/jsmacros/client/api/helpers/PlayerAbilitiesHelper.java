package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.player.PlayerCapabilities;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.0.3
 */
@SuppressWarnings("unused")
public class PlayerAbilitiesHelper extends BaseHelper<PlayerCapabilities> {
	
	public PlayerAbilitiesHelper(PlayerCapabilities a) {
		super(a);
	}
	
	/**
	 * @since 1.0.3
	 * @return whether the player can be damaged.
	 */
	public boolean getInvulnerable() {
		return base.disableDamage;
	}
	
	/**
	 * @since 1.0.3
	 * @return if the player is currently flying.
	 */
	public boolean getFlying() {
		return base.isFlying;
	}
	
	/**
	 * @since 1.0.3
	 * @return if the player is allowed to fly.
	 */
	public boolean getAllowFlying() {
		return base.allowFlying;
	}
	
	/**
	 * @since 1.0.3
	 * @return if the player is in creative.
	 */
	public boolean getCreativeMode() {
		return base.isCreativeMode;
	}
	
	/**
	 * set the player flying state.
	 * 
	 * @since 1.0.3
	 * @param b
	 * @return
	 */
	public PlayerAbilitiesHelper setFlying(boolean b) {
		base.isFlying = b;
		return this;
	}
	
	/**
	 * set the player allow flying state.
	 * 
	 * @since 1.0.3
	 * @param b
	 * @return
	 */
	public PlayerAbilitiesHelper setAllowFlying(boolean b) {
		base.allowFlying = b;
		return this;
	}
	
	/**
	 * @since 1.0.3
	 * @return the player fly speed multiplier.
	 */
	public float getFlySpeed() {
		return base.getFlySpeed();
	}
	
	/**
	 * set the player fly speed multiplier.
	 * 
	 * @since 1.0.3
	 * @param flySpeed
	 * @return
	 */
	public PlayerAbilitiesHelper setFlySpeed(double flySpeed) {
		base.setFlySpeed((float)flySpeed);
		return this;
	}
}
