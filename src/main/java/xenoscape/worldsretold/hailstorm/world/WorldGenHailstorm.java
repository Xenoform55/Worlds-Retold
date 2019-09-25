package xenoscape.worldsretold.hailstorm.world;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xenoscape.worldsretold.hailstorm.config.ConfigHailstormWorldGen;
import xenoscape.worldsretold.hailstorm.init.HailstormBlocks;
import xenoscape.worldsretold.hailstorm.world.feature.WorldGenOverlayedFlower;
import xenoscape.worldsretold.hailstorm.world.structure.*;

public class WorldGenHailstorm {

	@SubscribeEvent
	public void generateFeature(DecorateBiomeEvent.Post event) {
		final int blockX = (event.getChunkPos().x << 4) + event.getRand().nextInt(16) + 8;
		final int blockZ = (event.getChunkPos().z << 4) + event.getRand().nextInt(16) + 8;

		generateOre(HailstormBlocks.CRYONITE_ORE.getDefaultState(), 8, 10, 0, 32, BlockMatcher.forBlock(Blocks.STONE),
				event.getWorld(), event.getRand());
		if (ConfigHailstormWorldGen.areBouldersEnabled) {
			generateBoulders(event.getWorld(), event.getRand(), blockX, blockZ);
		}
		if (ConfigHailstormWorldGen.areFlowersEnabled) {
			generateFlowers(event.getWorld(), event.getRand(), blockX, blockZ);
		}

		if (ConfigHailstormWorldGen.isHailstormShrineEnabled) {
			generateHailstormShrine(event.getWorld(), event.getRand(), blockX, blockZ);
		}
		if (ConfigHailstormWorldGen.isIceTowerEnabled) {
			generateIceTower(event.getWorld(), event.getRand(), blockX, blockZ);
		}
		if (ConfigHailstormWorldGen.isBlimpCampEnabled) {
			generateBlimpCamp(event.getWorld(), event.getRand(), blockX, blockZ);
		}
		if (ConfigHailstormWorldGen.isIcyTavernEnabled) {
			generateIcyTavern(event.getWorld(), event.getRand(), blockX, blockZ);
		}
		if (ConfigHailstormWorldGen.isSnowTempleEnabled) {
			generateSnowTemple(event.getWorld(), event.getRand(), blockX, blockZ);
		}
		if (ConfigHailstormWorldGen.isMiningStationEnabled) {
			generateMiningStation(event.getWorld(), event.getRand(), blockX, blockZ);
		}
		if (ConfigHailstormWorldGen.isRuinedTavernEnabled) {
			generateRuinedTavern(event.getWorld(), event.getRand(), blockX, blockZ);
		}
		if (ConfigHailstormWorldGen.isRuinedTempleEnabled) {
			generateRuinedTemple(event.getWorld(), event.getRand(), blockX, blockZ);
		}
	}

	private void generateOre(IBlockState blockToGen, int blockAmount, int chancesToSpawn, int minHeight, int maxHeight,
			Predicate<IBlockState> blockToReplace, World world, Random rand) {
		if (minHeight < 0 || maxHeight > 256 || minHeight > maxHeight)
			throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator");

		WorldGenMinable generator = new WorldGenMinable(blockToGen, blockAmount, blockToReplace);
		int heightdiff = maxHeight - minHeight + 1;
		for (int i = 0; i < chancesToSpawn; i++) {
			int y = minHeight + rand.nextInt(heightdiff);
			BlockPos pos = new BlockPos(rand.nextInt(16), y, rand.nextInt(16));
			Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
			if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) {
				generator.generate(world, rand, pos);
			}
		}
	}

	private void generateBoulders(World world, Random rand, int blockX, int blockZ) {
		WorldGenBlockBlob generator = null;
			switch (rand.nextInt(3)) {
			case 0:
				generator = new WorldGenBlockBlob(Blocks.STONE, 2);
				break;
			case 1:
				generator = new WorldGenBlockBlob(Blocks.MOSSY_COBBLESTONE, 0);
				break;
			case 3:
				generator = new WorldGenBlockBlob(Blocks.COBBLESTONE, 0);
				break;
		}
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y + 1, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
		if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.CONIFEROUS))
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END) && rand.nextInt(25) == 0 && biome != null
				&& generator != null) {
			generator.generate(world, rand, pos);
		}
	}

	private void generateFlowers(World world, Random rand, int blockX, int blockZ) {
		WorldGenOverlayedFlower generator = null;
		switch (rand.nextInt(2)) {
		case 0:
			generator = new WorldGenOverlayedFlower(HailstormBlocks.ARCTIC_WILLOW);
			break;
		case 1:
			generator = new WorldGenOverlayedFlower(HailstormBlocks.BOREAL_ORCHID);
			break;
		}
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
		if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.CONIFEROUS))
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END) && rand.nextInt(30) == 0 && biome != null
				&& generator != null) {
			generator.generate(world, rand, pos);
		}
	}

	private void generateHailstormShrine(World world, Random rand, int blockX, int blockZ) {
		StructureHailstormShrine generator = new StructureHailstormShrine();
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
			if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && biome != null
					&& generator != null && rand.nextInt(5) == 0) {
				generator.generate(world, rand, pos);
			}
	}

	private void generateIceTower(World world, Random rand, int blockX, int blockZ) {
		StructureIceTower generator = new StructureIceTower();
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
		for (int i = 0; i < 10; ++i) {
		if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && biome != null
				&& generator != null) {
			generator.generate(world, rand, pos);
			}
		}
	}

	private void generateBlimpCamp(World world, Random rand, int blockX, int blockZ) {
		StructureBlimpCamp generator = new StructureBlimpCamp();
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
		for (int i = 0; i < 20; ++i) {
			if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && biome != null
					&& generator != null) {
				generator.generate(world, rand, pos);
			}
		}
	}

	private void generateIcyTavern(World world, Random rand, int blockX, int blockZ) {
		StructureIcyTavern generator = new StructureIcyTavern();
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
			if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && biome != null
					&& generator != null && rand.nextInt(5) == 0) {
				generator.generate(world, rand, pos);
		}
	}

	private void generateSnowTemple(World world, Random rand, int blockX, int blockZ) {
		StructureSnowTemple generator = new StructureSnowTemple();
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
			if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && biome != null
					&& generator != null && rand.nextInt(30) == 0) {
				generator.generate(world, rand, pos);
			}
	}

	private void generateMiningStation(World world, Random rand, int blockX, int blockZ) {
		StructureMiningStation generator = new StructureMiningStation();
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
		for (int i = 0; i < 10; ++i) {
			if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && biome != null
					&& generator != null && rand.nextInt(25) == 0) {
				generator.generate(world, rand, pos);
			}
		}
	}

	private void generateRuinedTavern(World world, Random rand, int blockX, int blockZ) {
		StructureRuinedTavern generator = new StructureRuinedTavern();
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
		for (int i = 0; i < 10; ++i) {
			if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && biome != null
					&& generator != null) {
				generator.generate(world, rand, pos);
			}
		}
	}

	private void generateRuinedTemple(World world, Random rand, int blockX, int blockZ) {
		StructureRuinedTemple generator = new StructureRuinedTemple();
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
		for (int i = 0; i < 10; ++i) {
			if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
					&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && biome != null
					&& generator != null && rand.nextInt(20) == 0) {
				generator.generate(world, rand, pos);
			}
		}
	}

	public static int getGroundFromAbove(World world, int x, int z) {
		int y = 255;
		boolean foundGround = false;
		while (!foundGround && y-- >= 0) {
			Block blockAt = world.getBlockState(new BlockPos(x, y, z)).getBlock();
			foundGround = blockAt == Blocks.GRASS || blockAt == Blocks.SNOW || blockAt == Blocks.SNOW_LAYER;
		}
		return y;
	}

	public static boolean canSpawnHere(final World world, final BlockPos min, BlockPos max) {
		return isCornerValid(world, min) && isCornerValid(world, max) &&
				isCornerValid(world, new BlockPos(max.getX(), min.getY(), min.getZ())) &&
				isCornerValid(world, new BlockPos(min.getX(), min.getY(), max.getZ()));
	}

	public static boolean isCornerValid(final World world, final BlockPos pos) {
		final int groundY = getGroundFromAbove(world, pos.getX(), pos.getZ());
		return groundY > pos.getY() - 4 && groundY < pos.getY() + 4;
	}
}