/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 IceDragon200
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package growthcraft.bees.common.tileentity.device;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.netty.buffer.ByteBuf;

import growthcraft.api.bees.BeesRegistry;
import growthcraft.api.bees.IFlowerBlockEntry;
import growthcraft.api.core.util.RandomUtils;
import growthcraft.api.core.util.BlockFlags;
import growthcraft.bees.common.tileentity.TileEntityBeeBox;
import growthcraft.bees.GrowthCraftBees;
import growthcraft.core.common.tileentity.device.DeviceBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class DeviceBeeBox extends DeviceBase
{
	// Temp variable used by BlockBeeBox for storing flower lists
	@SuppressWarnings({"rawtypes", "unchecked"})
	public List<IBlockState> flowerList = new ArrayList<IBlockState>();
	private final float honeyCombSpawnRate = GrowthCraftBees.getConfig().beeBoxHoneyCombSpawnRate;
	private final float honeySpawnRate = GrowthCraftBees.getConfig().beeBoxHoneySpawnRate;
	private final float beeSpawnRate = GrowthCraftBees.getConfig().beeBoxBeeSpawnRate;
	private final float flowerSpawnRate = GrowthCraftBees.getConfig().beeBoxFlowerSpawnRate;
	private final int flowerRadius = GrowthCraftBees.getConfig().beeBoxFlowerRadius;
	private final float bonus = GrowthCraftBees.getConfig().beeBoxBonusMultiplier;
	private Random random = new Random();
	private int bonusTime;

	public DeviceBeeBox(TileEntityBeeBox te)
	{
		super(te);
	}

	protected TileEntityBeeBox getParentTile()
	{
		if (parent instanceof TileEntityBeeBox)
		{
			return (TileEntityBeeBox)parent;
		}
		return null;
	}

	public int getBonusTime(int t)
	{
		return bonusTime;
	}

	public void setBonusTime(int t)
	{
		this.bonusTime = t;
	}

	public boolean hasBonus()
	{
		return bonusTime > 0;
	}

	// for lack of a better name, can this BeeBox do any work?
	private boolean canDoWork()
	{
		final BlockPos pos = parent.getPos();
		if (getWorld().canLightningStrikeAt(pos.up()))
			return false;
		final IBlockState state = getWorld().getBlockState(pos);
		return state.getBlock().getLightValue(world, pos) >= 7;
	}

	/**
	 * Is the provided block a flower?
	 *
	 * @param block - block to check
	 * @param meta - block's metadata
	 * @return true, the block is a recognized flower, false otherwise
	 */
	private boolean isBlockFlower(IBlockState state)
	{
		return BeesRegistry.instance().isBlockFlower(state.getBlock(), 0);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<List<IBlockState>> gatherFlowersInRadius(World world, BlockPos pos, int checkSize, List<List> list)
	{
		final int i = pos.getX() - ((checkSize - 1) / 2);
		final int k = pos.getZ() - ((checkSize - 1) / 2);

		for (int xLoop = -checkSize; xLoop < checkSize; xLoop++)
		{
			for (int yLoop = -checkSize; yLoop < checkSize; yLoop++)
			{
				final BlockPos flowerPos = new BlockPos(i + xLoop, pos.getY(), k + yLoop);
				if (!world.isAirBlock(flowerPos))
				{
					final IBlockState flower = world.getBlockState(flowerPos);
					if (flower != null)
					{
						if (isBlockFlower(flower))
						{
							list.add(flower);
						}
					}
				}
			}
		}
		return list;
	}

	private float calcGrowthRate(World world, BlockPos pos)
	{
		final int checkSize = 5;
		final int i = x - ((checkSize - 1) / 2);
		final int k = z - ((checkSize - 1) / 2);
		float f = 1.0F;

		for (int loopx = -checkSize; loopx < checkSize; loopx++)
		{
			for (int loopz = -checkSize; loopz < checkSize; loopz++)
			{
				final Block flower = world.getBlock(i + loopx, y, k + loopz);
				final int fm = world.getBlockMetadata(i + loopx, y, k + loopz);
				final Block soil = world.getBlock(i + loopx, y - 1, k + loopz);
				float f1 = 0.0F;

				if (soil == Blocks.grass)
				{
					//f1 = 1.0F;
					f1 = 0.36F;

					if (isBlockFlower(flower, fm))
					{
						//f1 = 3.0F;
						f1 = 1.08F;
					}
				}
				else if (flower == Blocks.flower_pot && (world.getBlockMetadata(i + loopx, y, k + loopz) == 1 ||
					world.getBlockMetadata(i + loopx, y, k + loopz) == 2))
				{
					//f1 = 2.0F;
					f1 = 0.72F;
				}

				f1 /= 4.0F;

				f += f1;
			}
		}

		final TileEntityBeeBox te = getParentTile();

		if (te != null)
		{
			final int bees = te.countBees();
			final float div = 2.0F - (0.015625F * bees);

			f /= div;

			if (te.hasBonus())
			{
				f *= this.bonus;
			}
		}

		return f;
	}

	public float getGrowthRate()
	{
		return calcGrowthRate(getWorld(), parent.xCoord, parent.yCoord, parent.zCoord);
	}

	public void update()
	{
		if (bonusTime > 0) bonusTime--;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void updateTick()
	{
		final TileEntityBeeBox te = getParentTile();
		if (!canDoWork() || !te.hasBees()) return;

		float f = getGrowthRate();

		if (te.countCombs() < 27)
		{
			if (te.hasMaxBees())
			{
				if (random.nextInt((int)(honeyCombSpawnRate / f) + 1) == 0)
				{
					te.spawnHoneyComb();
				}
			}
			else
			{
				if (random.nextInt(5) == 0)
				{
					if (random.nextInt((int)(beeSpawnRate / f) + 1) == 0)
					{
						te.spawnBee();
					}
				}
				else
				{
					if (random.nextInt((int)(honeyCombSpawnRate / f) + 1) == 0)
					{
						te.spawnHoneyComb();
					}
				}
			}
		}
		else
		{
			if (random.nextInt((int)(honeySpawnRate / f) + 1) == 0)
			{
				if (te.hasMaxBees())
				{
					te.fillHoneyComb();
				}
				else
				{
					if (random.nextInt(5) == 0)
					{
						te.spawnBee();
					}
					else
					{
						te.fillHoneyComb();
					}
				}
			}
		}

		f = 7.48F / (2.0F - (0.015625F * te.countBees()));
		if (te.hasBonus())
		{
			f *= bonus;
		}

		final int spawnRate = (int)(this.flowerSpawnRate / f) + 1;
		if (random.nextInt(spawnRate) == 0)
		{
			final int checkSize = flowerRadius;

			flowerList.clear();
			final BlockPos pos = te.getPos();
			gatherFlowersInRadius(getWorld(), pos, checkSize, flowerList);

			if (!flowerList.isEmpty())
			{
				final int randomX = pos.getX() + random.nextInt(checkSize * 2) - checkSize;
				final int randomZ = pos.getZ() + random.nextInt(checkSize * 2) - checkSize;
				final BlockPos flowerPos = new BlockPos(randomX, pos.getY(), randomZ);
				final IBlockState flowerState = RandomUtils.sample(random, flowerList);
				if (flowerState != null)
				{
					final IFlowerBlockEntry entry = BeesRegistry.instance().getFlowerBlockEntry(flowerState.getBlock(), 0);
					if (entry != null)
					{
						if (entry.canPlaceAt(getWorld(), flowerPos))
						{
							getWorld().setBlockState(flowerPos, block.getDefaultState(), BlockFlags.SYNC);
						}
					}
				}
			}
		}
	}

	public void updateClientTick()
	{
	}

	@Override
	public void readFromNBT(NBTTagCompound data)
	{
		super.readFromNBT(data);
		this.bonusTime = data.getInteger("bonus_time");
	}

	@Override
	public void writeToNBT(NBTTagCompound data)
	{
		super.writeToNBT(data);
		data.setInteger("bonus_time", bonusTime);
	}

	@Override
	public boolean readFromStream(ByteBuf buf)
	{
		super.readFromStream(buf);
		this.bonusTime = buf.readInt();
		return false;
	}

	/**
	 * @param buf - buffer to write to
	 */
	@Override
	public boolean writeToStream(ByteBuf buf)
	{
		super.writeToStream(buf);
		buf.writeInt(bonusTime);
		return false;
	}
}
