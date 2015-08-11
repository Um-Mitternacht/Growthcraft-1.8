package growthcraft.rice.village;

import java.util.Random;

import growthcraft.rice.GrowthCraftRice;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class VillageHandlerRice implements IVillageTradeHandler
{
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random)
	{
		//		recipeList.add(new MerchantRecipe(new ItemStack(GrowthCraftRice.rice, 18 + random.nextInt(3)), new ItemStack(Item.emerald, 1)));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1 + random.nextInt(2)), new ItemStack(GrowthCraftRice.riceSake, 1, 1)));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 2 + random.nextInt(2)), new ItemStack(GrowthCraftRice.riceSake, 1, 2)));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 2 + random.nextInt(2)), new ItemStack(GrowthCraftRice.riceSake, 1, 3)));
	}
}
