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
package growthcraft.bees.common.item;

import java.util.List;

import growthcraft.bees.GrowthCraftBees;
import growthcraft.core.common.item.GrcItemBase;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBeesWax extends GrcItemBase
{
	public ItemBeesWax()
	{
		super();
		setUnlocalizedName("grcbees.bees_wax");
		setCreativeTab(GrowthCraftBees.tab);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	public EnumBeesWax getEnumBeesWax(ItemStack stack)
	{
		return EnumBeesWax.VALUES.get(stack.getItemDamage());
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		final EnumBeesWax beesWax = getEnumBeesWax(stack);
		if (beesWax != null)
		{
			return super.getUnlocalizedName(stack) + "." + beesWax.basename;
		}
		return super.getUnlocalizedName(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubItems(Item item, CreativeTabs ct, List list)
	{
		for (EnumBeesWax beesWax : EnumBeesWax.VALUES)
		{
			list.add(beesWax.asStack());
		}
	}
}
