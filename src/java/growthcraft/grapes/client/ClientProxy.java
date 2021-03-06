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
package growthcraft.grapes.client;

import growthcraft.core.client.renderer.block.statemap.GrcDomainStateMapper;
import growthcraft.core.client.util.GrcModelRegistry;
import growthcraft.grapes.common.CommonProxy;
import growthcraft.grapes.common.item.EnumGrapes;
import growthcraft.grapes.GrowthCraftGrapes;

public class ClientProxy extends CommonProxy
{
	private void registerBlockStates()
	{
		final GrcModelRegistry gmr = GrcModelRegistry.instance();
		gmr.registerAll(GrowthCraftGrapes.blocks.all, 0, GrowthCraftGrapes.resources);
		gmr.setCustomStateMapperForAll(GrowthCraftGrapes.blocks.all, new GrcDomainStateMapper(GrowthCraftGrapes.resources));
		for (EnumGrapes enumGrape : EnumGrapes.VALUES)
		{
			gmr.register(GrowthCraftGrapes.items.grapes, enumGrape.meta, GrowthCraftGrapes.resources.createModel("grapes_" + enumGrape.getBasename(), "inventory"));
			gmr.register(GrowthCraftGrapes.items.grapeSeeds, enumGrape.meta, GrowthCraftGrapes.resources.createModel("grape_seeds_" + enumGrape.getBasename(), "inventory"));
		}
	}

	@Override
	public void register()
	{
		super.register();
		registerBlockStates();
	}
}
