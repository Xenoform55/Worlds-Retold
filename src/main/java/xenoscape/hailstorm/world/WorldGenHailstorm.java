package xenoscape.hailstorm.world;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import xenoscape.hailstorm.init.MBlocks;
import xenoscape.hailstorm.world.feature.WorldGenOverlayedFlower;
import xenoscape.hailstorm.world.structure.StructureHailstormShrine;

public class WorldGenHailstorm implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {

		final int blockX = (chunkX << 4) + random.nextInt(16) + 8;
		final int blockZ = (chunkZ << 4) + random.nextInt(16) + 8;

		switch (world.provider.getDimension()) {
		case 0:
			generateOre(MBlocks.CRYONITE_ORE.getDefaultState(), 8, 10, 0, 32, BlockMatcher.forBlock(Blocks.STONE),
					world, random);
			generateFlowers(world, random, blockX, blockZ);
			generateBoulders(world, random, blockX, blockZ);
			generateHailstormShrine(world, random, blockX, blockZ);
			break;
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
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && rand.nextInt(5) == 0 && biome != null
				&& generator != null) {
			generator.generate(world, rand, pos);
		}
	}

	private void generateFlowers(World world, Random rand, int blockX, int blockZ) {
		WorldGenOverlayedFlower generator = new WorldGenOverlayedFlower(MBlocks.ARCTIC_WILLOW);
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
		if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && rand.nextInt(10) == 0 && biome != null
				&& generator != null) {
			generator.generate(world, rand, pos);
		}
	}

	private void generateBoulders(World world, Random rand, int blockX, int blockZ) {
		WorldGenBlockBlob generator = null;
		if (rand.nextInt(3) == 0) {
			switch (rand.nextInt(3)) {
			case 0:
				generator = new WorldGenBlockBlob(MBlocks.STONE_CRITTER_EGG, 2);
				break;
			case 1:
				generator = new WorldGenBlockBlob(MBlocks.PACKEDICE_CRITTER_EGG, 2);
				break;
			case 2:
				generator = new WorldGenBlockBlob(MBlocks.MOSSCOBBLE_CRITTER_EGG, 2);
				break;
			case 3:
				generator = new WorldGenBlockBlob(MBlocks.COBBLE_CRITTER_EGG, 2);
				break;
			}
		} else {
			switch (rand.nextInt(3)) {
			case 0:
				generator = new WorldGenBlockBlob(Blocks.STONE, 2);
				break;
			case 1:
				generator = new WorldGenBlockBlob(Blocks.PACKED_ICE, 2);
				break;
			case 2:
				generator = new WorldGenBlockBlob(Blocks.MOSSY_COBBLESTONE, 0);
				break;
			case 3:
				generator = new WorldGenBlockBlob(Blocks.COBBLESTONE, 0);
				break;
			}
		}
		int y = getGroundFromAbove(world, blockX, blockZ);
		BlockPos pos = new BlockPos(blockX, y + 1, blockZ);
		Biome biome = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider());
		if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
				&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) && rand.nextInt(25) == 0 && biome != null
				&& generator != null) {
			generator.generate(world, rand, pos);
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
		return isCornerValid(world, min) && isCornerValid(world, new BlockPos(max.getX(), min.getY(), min.getZ()))
				&& isCornerValid(world, max) && isCornerValid(world, new BlockPos(min.getX(), min.getY(), max.getZ()));
	}

	public static boolean isCornerValid(final World world, final BlockPos pos) {
		final int groundY = getGroundFromAbove(world, pos.getX(), pos.getZ());
		return groundY > pos.getY() - 3 && groundY < pos.getY() + 3;
	}

}
