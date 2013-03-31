package org.vinsert.bot.script.api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.tools.Skills;
import org.vinsert.insertion.IPlayer;


/**
 * Wraps the {@link IPlayer} class in the client.
 * @author tommo
 * @author `Discardedx2
 */
public class Player extends Actor {
	
	private IPlayer player;
	private ScriptContext ctx;

	public Player(ScriptContext ctx, IPlayer player) {
		super(ctx, player);
		this.ctx = ctx;
		this.player = player;
	}
	
	/**
	 * @return The player's health
	 */
	public int getHealth() {
		return ctx.skills.getLevel(Skills.HITPOINTS);
	}
	
	/**
	 * @return The player's current health percentage
	 */
	@Override
	public int getHealthPercent() {
		return (ctx.players.getLocalPlayer().getHealth() * 100) / ctx.skills.getBaseLevel(Skills.HITPOINTS);
	}
	
	/**
	 * @return The player's max health
	 */
	public int getMaxHealth() {
		return ctx.skills.getBaseLevel(Skills.HITPOINTS);
	}
	
	/**
	 * @return The player's ingame username
	 */
	public String getName() {
		return player.getUsername();
	}
	
	/**
	 * Gets the combat level of this player.
	 * @return The combat level.
	 */
	public int getCombatLevel() {
		return player.getCombatLevel();
	}
	
	/**
	 * Gets the total level of this player.
	 * @return The total level.
	 */
	public int getTotalLevel() {
		return player.getTotalLevel();
	}
	
	/**
	 * Gets the appearance information for this player.
	 * @return The appearance of this player.
	 */
	public int[] getAppearance() {
		return player.getComposite().getAppearance();
	}
	
	/**
	 * Gets the color information for this player.
	 * @return The colors of this player.
	 */
	public int[] getColors() {
		return player.getComposite().getColors();
	}
	
	/**
	 * Checks to see if this player is of the male sex.
	 * @return {@code true} if this player is a male.
	 */
	public boolean isMale() {
		return player.getComposite().isMale();
	}
	
	/**
	 * Gets the npc id that this player is currently
	 * being rendered as. This may take place in quests, such as
	 * monkey madness where you get turned into an ape.
	 * @return The npc transformation id of this player.
	 */
	public int getNpcTrans() {
		return player.getComposite().getNpcTrans();
	}
	

}
