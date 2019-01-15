package xenoscape.worldsretold.hailstorm.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xenoscape.worldsretold.WorldsRetold;
import xenoscape.worldsretold.defaultmod.config.ConfigModules;

public class HailstormSounds {

	public static final SoundEvent FREEZING = createEvent("freezing");
	public static final SoundEvent ENTITY_SNAKE_HISS = createEvent("snakehiss");
	public static final SoundEvent ENTITY_SNAKE_HURT = createEvent("snakehurt");
	public static final SoundEvent ENTITY_SNAKE_DEATH = createEvent("snakedeath");
	public static final SoundEvent ENTITY_SNAKE_STRIKE = createEvent("snakestrike");
	public static final SoundEvent ENTITY_MUMMY_AMBIENT = createEvent("mummyambient");
	public static final SoundEvent ENTITY_MUMMY_HURT = createEvent("mummyhurt");
	public static final SoundEvent ENTITY_MUMMY_DEATH = createEvent("mummydeath");
	public static final SoundEvent ENTITY_MUMMY_INFECT = createEvent("mummyinfect");

    @SubscribeEvent
	public static void registerSounds(final RegistryEvent.Register<SoundEvent> evt) {

		if (ConfigModules.isHailstormEnabled == true) {
			evt.getRegistry().register(HailstormSounds.FREEZING);
			evt.getRegistry().register(HailstormSounds.ENTITY_SNAKE_HISS);
			evt.getRegistry().register(HailstormSounds.ENTITY_SNAKE_HURT);
			evt.getRegistry().register(HailstormSounds.ENTITY_SNAKE_DEATH);
			evt.getRegistry().register(HailstormSounds.ENTITY_SNAKE_STRIKE);
			evt.getRegistry().register(HailstormSounds.ENTITY_MUMMY_AMBIENT);
			evt.getRegistry().register(HailstormSounds.ENTITY_MUMMY_HURT);
			evt.getRegistry().register(HailstormSounds.ENTITY_MUMMY_DEATH);
			evt.getRegistry().register(HailstormSounds.ENTITY_MUMMY_INFECT);
		}
	}

	private static SoundEvent createEvent(final String soundName) {
		final ResourceLocation soundID = new ResourceLocation(WorldsRetold.MODID, soundName);
		return new SoundEvent(soundID).setRegistryName(soundID);
	}
}
