package org.vinsert.bot.script.api.tools;

import org.vinsert.bot.script.ScriptContext;

/**
 * Skill utilities
 * 
 * @author tommo
 * @author Kosaki
 */
public class Skills {

	public static final int ATTACK = 0;
	public static final int DEFENSE = 1;
	public static final int STRENGTH = 2;
	public static final int HITPOINTS = 3;
	public static final int RANGE = 4;
	public static final int PRAYER = 5;
	public static final int MAGIC = 6;
	public static final int COOKING = 7;
	public static final int WOODCUTTING = 8;
	public static final int FLETCHING = 9;
	public static final int FISHING = 10;
	public static final int FIREMAKING = 11;
	public static final int CRAFTING = 12;
	public static final int SMITHING = 13;
	public static final int MINING = 14;
	public static final int HERBLORE = 15;
	public static final int AGILITY = 16;
	public static final int THIEVING = 17;
	public static final int SLAYER = 18;
	public static final int FARMING = 19;
	public static final int RUNECRAFTING = 20;
	public static final int HUNTER = 21;
	public static final int CONSTRUCTION = 22;
	public static final int SUMMONING = 23;
	
	public static final int[] EXPERIENCE_TABLE = { 0, 0, 83, 174, 276, 388, 512, 650,
			801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523,
			3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031,
			13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408,
			33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127,
			83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636,
			184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599,
			407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445,
			899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200,
			1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594,
			3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253,
			7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431 };

	private ScriptContext ctx;

	public Skills(ScriptContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Gets the current experience in a skill. Example usage:
	 * <code>skills.getExperience(Skills.SKILL_SLAYER);<code>
	 * 
	 * @param skill
	 *            skill is a constant starting with SKILL_
	 * @return integer: The experience in the skill or -1 if invalid skill or an
	 *         error occurred
	 */
	public int getExperience(int skill) {
		int[] skills = ctx.getClient().getSkillExperiences();
		if (skills == null || skills.length == 0 || skill < 0
				|| skill >= skills.length) {
			return -1;
		}
		return skills[skill];
	}
	
	/**
	 * Returns the base skill level (the unmodified maximum)
	 * @param skill The skill to lookup
	 * @return The base level, or -1 if invalid
	 */
	public int getBaseLevel(int skill) {
		int[] skills = ctx.getClient().getSkillBases();
		if (skills == null || skills.length == 0 || skill < 0
				|| skill >= skills.length) {
			return -1;
		}
		return skills[skill];
	}

	/**
	 * Gets the current level in the given skill. Example usage:
	 * <code>skills.getLevel(Skills.SKILL_SLAYER);<code>
	 * 
	 * @param skill
	 *            skill is constant starting with SKILL_
	 * @return integer: The level in the skill or -1 if invalid skill or an
	 *         error occured
	 */
	public int getLevel(int skill) {
		int[] skills = ctx.getClient().getSkillLevels();
		if (skills == null || skills.length == 0 || skill < 0
				|| skill >= skills.length) {
			return -1;
		}
		return skills[skill];
	}

	/**
	 * Gets the amount of experience needed until next level. Example usage:
	 * <code>skills.getExperienceToNextLevel(Skills.SKILL_SLAYER);</code>
	 * 
	 * @param skill
	 *            skill is constant starting with SKILL_
	 * @return integer: The amount of experience until next level or 0 if level
	 *         99. Returns -1 if invalid skill or an error occured.
	 */
	public int getExperienceToNextLevel(int skill) {
		int level = getLevel(skill);
		if (level == -1) {
			return -1;
		}
		if (level == 99)
			return 0;

		int experience = getExperience(skill);
		int needed = EXPERIENCE_TABLE[level + 1];
		return needed - experience;
	}

	/**
	 * Gets the amount of experience until level specified in level in the skill
	 * in skill.
	 * 
	 * @param skill
	 *            skill is constant and starts with SKILL_
	 * @param level
	 *            level is a valid level between 1 and 99.
	 * @return integer: the amount of experience to the level or -1 if wrong
	 *         arguments or an error occured.
	 */
	public int getExperienceToLevel(int skill, int level) {
		if (level < 0 || level > 99)
			return -1;
		int experience = getExperience(skill);
		if (experience == -1)
			return -1;
		return EXPERIENCE_TABLE[level] - experience;
	}

	/**
	 * Gets the percentage until next level
	 * 
	 * @param skill
	 *            skill is constant and starts with SKILL_
	 * @return integer: 0-100% or -1 if wrong argument or an error occured.
	 * @Fixed by Fatality
	 */
	public int getPercentageToNextLevel(int skill) {
		int currLvl = getLevel(skill);
		if (currLvl == 99)
			return 0;
		int expTot = EXPERIENCE_TABLE[currLvl + 1] - EXPERIENCE_TABLE[currLvl];
		if (expTot == 0)
			return 0;
		int completedXP = getExperience(skill) - EXPERIENCE_TABLE[currLvl];
		return (100 * completedXP / expTot);
	}

}