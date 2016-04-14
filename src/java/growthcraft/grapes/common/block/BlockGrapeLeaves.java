package growthcraft.grapes.common.block;

import java.util.Random;

import growthcraft.api.core.util.BlockFlags;
import growthcraft.core.common.block.IBlockRope;
import growthcraft.core.GrowthCraftCore;
import growthcraft.core.util.BlockCheck;
import growthcraft.grapes.GrowthCraftGrapes;
import growthcraft.grapes.util.GrapeBlockCheck;

import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGrapeLeaves extends BlockLeavesBase implements IBlockRope, IGrowable
{
	private final int grapeLeavesGrowthRate = GrowthCraftGrapes.getConfig().grapeLeavesGrowthRate;
	private final int grapeSpawnRate = GrowthCraftGrapes.getConfig().grapeSpawnRate;
	// how far can a grape leaf grow before it requires support from a trunk
	private final int grapeVineSupportedLength = GrowthCraftGrapes.getConfig().grapeVineSupportedLength;

	public BlockGrapeLeaves()
	{
		super(Material.leaves, false);
		setTickRandomly(true);
		setHardness(0.2F);
		setLightOpacity(1);
		setStepSound(soundTypeGrass);
		setUnlocalizedName("grc.grape_leaves");
		setCreativeTab(null);
	}

	private boolean isTrunk(World world, BlockPos pos)
	{
		return GrapeBlockCheck.isGrapeVineTrunk(world.getBlockState(pos));
	}

	public boolean isSupportedByTrunk(World world, BlockPos pos)
	{
		return isTrunk(world, pos.down());
	}

	/**
	 * Use this method to check if the block can grow outwards on a rope
	 *
	 * @param world - the world
	 * @param x - x coord
	 * @param y - y coord
	 * @param z - z coord
	 * @return true if the block can grow here, false otherwise
	 */
	public boolean canGrowOutwardsOnRope(World world, BlockPos pos)
	{
		if (BlockCheck.isRope(world.getBlockState(pos.west()))) return true;
		if (BlockCheck.isRope(world.getBlockState(pos.east()))) return true;
		if (BlockCheck.isRope(world.getBlockState(pos.north()))) return true;
		if (BlockCheck.isRope(world.getBlockState(pos.south()))) return true;
		return false;
	}

	public boolean canGrowOutwards(World world, BlockPos pos)
	{
		final boolean leavesTotheSouth = world.getBlockState(pos.south()).getBlock() == this;
		final boolean leavesToTheNorth = world.getBlockState(pos.north()).getBlock() == this;
		final boolean leavesToTheEast = world.getBlockState(pos.east()).getBlock() == this;
		final boolean leavesToTheWest = world.getBlockState(pos.west()).getBlock() == this;

		if (!leavesTotheSouth && !leavesToTheNorth && !leavesToTheEast && !leavesToTheWest) return false;

		for (int i = 1; i <= grapeVineSupportedLength; ++i)
		{
			final BlockPos down = pos.down();
			if (leavesTotheSouth && isTrunk(world, down.south(i))) return true;
			if (leavesToTheNorth && isTrunk(world, down.north(i))) return true;
			if (leavesToTheEast && isTrunk(world, down.east(i))) return true;
			if (leavesToTheWest && isTrunk(world, down.west(i))) return true;
		}
		return false;
	}

	/**
	 * Variation of canGrowOutwards, use this method to check rope blocks
	 *
	 * @param world - the world
	 * @param x - x coord
	 * @param y - y coord
	 * @param z - z coord
	 * @return true if the block can grow here, false otherwise
	 */
	public boolean canGrowHere(World world, BlockPos pos)
	{
		if (BlockCheck.isRope(world.getBlockState(pos)))
		{
			return canGrowOutwards(world, pos);
		}
		return false;
	}

	private void setGrapeBlock(World world, BlockPos pos)
	{
		world.setBlockState(pos, GrowthCraftGrapes.blocks.grapeBlock.getBlock().getDefaultState(), BlockFlags.UPDATE_AND_SYNC);
	}

	public boolean growGrapeBlock(World world, BlockPos pos)
	{
		final BlockPos dpos = pos.down();
		if (world.isAirBlock(dpos))
		{
			if (!world.isRemote)
			{
				setGrapeBlock(world, dpos);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
	{
		return false;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
	{
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, IBlockState state)
	{
		final BlockPos belowPos = pos.down();
		if (world.isAirBlock(belowPos) && (random.nextInt(grapeSpawnRate) == 0))
		{
			setGrapeBlock(world, belowPos);
		}

		if (world.rand.nextInt(grapeLeavesGrowthRate) == 0)
		{
			if (canGrowOutwards(world, pos))
			{
				final EnumFacing dir = BlockCheck.randomDirection4(random);
				final BlockPos growPos = pos.offset(dir);
				if (canGrowHere(world, growPos))
				{
					world.setBlockState(growPos, getDefaultState(), BlockFlags.UPDATE_AND_SYNC);
				}
			}
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
	{
		super.updateTick(world, pos, state, random);
		if (!canBlockStay(world, pos))
		{
			world.setBlockState(pos, GrowthCraftCore.blocks.ropeBlock.getBlock().getDefaultState());
		}
		else
		{
			grow(world, random, pos, state);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		super.randomDisplayTick(worldIn, pos, state, rand);
        if (worldIn.isRainingAt(pos.up()) && !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && rand.nextInt(15) == 1)
        {
            double d0 = (double)((float)pos.getX() + rand.nextFloat());
            double d1 = (double)pos.getY() - 0.05D;
            double d2 = (double)((float)pos.getZ() + rand.nextFloat());
            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        }
	}

	public boolean canBlockStay(World world, BlockPos pos)
	{
		if (this.isSupportedByTrunk(world, pos))
		{
			return true;
		}
		else
		{
			for (EnumFacing dir : BlockCheck.DIR4)
			{
				for (int i = 1; i <= grapeVineSupportedLength; ++i)
				{
					final BlockPos newPos = pos.offset(dir, i);
					final IBlockState state = world.getBlockState(pos);
					if (state == null || state.getBlock() != this)
					{
						break;
					}
					else if (isSupportedByTrunk(world, pos))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, BlockPos pos)
	{
		return GrowthCraftGrapes.items.grapeSeeds.getItem();
	}

	@Override
	public boolean isLeaves(IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		return false;
	}

	@Override
	public boolean canConnectRopeTo(IBlockAccess world, BlockPos pos)
	{
		return BlockCheck.isRopeBlock(world.getBlockState(pos));
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		return GrowthCraftCore.items.rope.getItem();
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return Blocks.leaves.isOpaqueCube();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		final double d0 = 0.5D;
		final double d1 = 1.0D;
		return ColorizerFoliage.getFoliageColor(d0, d1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(IBlockState state)
	{
		return ColorizerFoliage.getFoliageColorBasic();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
	{
		return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
	}
}
