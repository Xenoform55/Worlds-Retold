package xenoscape.worldsretold.hailstorm.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHailstormEntity extends Configuration {

	private static final String CATEGORY_PASSIVE = "passive";
	private static final String CATEGORY_HOSTILE = "hostile";

	// Passive
	public static boolean isCaribouEnabled;
	public static boolean isPenguinEnabled;
	public static boolean isNixEnabled;
	
	//Hostile
	public static boolean isWightEnabled;
	public static boolean isGuardsmanEnabled;
	public static boolean isBlizzardEnabled;
	public static boolean isSnowRollerEnabled;

	public ConfigHailstormEntity(File file) {
		super(file);
		reload();
	}

	public void reload() {
		this.load();
		// Passive
		isCaribouEnabled = getBoolean("Enable Caribou", CATEGORY_PASSIVE, true,
				"A herdable animal that will run away from you unless you tame it.");
		isPenguinEnabled = getBoolean("Enable Penguin", CATEGORY_PASSIVE, true,
				"A cute little fellow that spawns in snow biomes.");
		isNixEnabled = getBoolean("Enable Nix", CATEGORY_PASSIVE, true,
				"A farmable ice creature that spawns packed ice randomly.");
		
		// Hostile
		isWightEnabled = getBoolean("Enable Wight", CATEGORY_HOSTILE, true,
				"A frozen undead corpse with a paralyzing touch.");
		isGuardsmanEnabled = getBoolean("Enable Guardsman", CATEGORY_HOSTILE, true,
				"An armored ice elemental spawning on the surface of snow biomes.");
		isBlizzardEnabled = getBoolean("Enable Blizzard", CATEGORY_HOSTILE, true,
				"A cloud elemental that scours the clouds in search of prey.");
		isSnowRollerEnabled = getBoolean("Enable Snow Roller", CATEGORY_HOSTILE, true,
				"A living snowball that grows as it consumes the ground beneath it.");
		this.save();
	}

	private void setCategoryLanguageKey(String category) {
		this.setCategoryLanguageKey(category, "worldsretold.config." + category + ".name");
	}
}