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
package growthcraft.milk.common.block;

import java.util.List;
import java.util.Random;

import growthcraft.core.common.block.GrcBlockContainer;
import growthcraft.milk.common.tileentity.TileEntityCheeseVat;
import growthcraft.milk.GrowthCraftMilk;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCheeseVat extends GrcBlockContainer
{
	public BlockCheeseVat()
	{
		super(Material.iron);
		setResistance(10.0F);
		setHardness(5.0F);
		setStepSound(soundTypeMetal);
		setUnlocalizedName("cheese_vat");
		setCreativeTab(GrowthCraftMilk.creativeTab);
		setTileEntityType(TileEntityCheeseVat.class);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		super.randomDisplayTick(world, pos, state, rand);
		if (rand.nextInt(12) == 0)
		{
			final TileEntityCheeseVat te = getTileEntity(world, pos);
			if (te != null)
			{
				if (te.isWorking())
				{
					for (int i = 0; i < 3; ++i)
					{
						final double px = pos.getX() + 0.5d + (rand.nextFloat() - 0.5d);
						final double py = pos.getY() + (1d / 16d);
						final double pz = pos.getZ() + 0.5d + (rand.nextFloat() - 0.5d);
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, px, py, pz, 0.0D, 1d / 32d, 0.0D);
						world.playSoundEffect(px, py, pz, "liquid.lavapop", 0.3f, 0.5f);
					}
				}
			}
		}
	}

	@Override
	public void setBlockBoundsForItemRender()
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axis, List list, Entity entity)
	{
		final float unit = 1f / 16f;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, unit, 1.0F);
		super.addCollisionBoxesToList(world, pos, state, axis, list, entity);

		this.setBlockBounds(0.0F, 0.0F, 0.0F, unit, 1.0F, 1.0F);
		super.addCollisionBoxesToList(world, pos, state, axis, list, entity);

		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, unit);
		super.addCollisionBoxesToList(world, pos, state, axis, list, entity);

		this.setBlockBounds(1.0F - unit, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(world, pos, state, axis, list, entity);

		this.setBlockBounds(0.0F, 0.0F, 1.0F - unit, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(world, pos, state, axis, list, entity);

		this.setBlockBoundsForItemRender();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		return true;
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos)
	{
		final TileEntityCheeseVat te = getTileEntity(world, pos);
		if (te != null)
		{
			return te.calcRedstone();
		}
		return 0;
	}
}
