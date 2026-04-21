package net.skds.wpo.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.skds.wpo.item.AdvancedBucket;
import net.skds.wpo.registry.WPODataComponents;

public class ExtendedFHIS extends FluidHandlerItemStack {

	public ExtendedFHIS(ItemStack container, int capacity) {
		super(WPODataComponents.ADVANCED_BUCKET_FLUID, container, capacity);
	}

    protected void setFluid(FluidStack fluid) {
		super.setFluid(fluid);
		AdvancedBucket.updateDamage(container);
	}	
}
