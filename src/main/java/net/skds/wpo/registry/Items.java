package net.skds.wpo.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skds.wpo.WPO;
import net.skds.wpo.item.AdvancedBucket;
import net.skds.wpo.util.ExtendedFHIS;

public class Items {
	
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WPO.MOD_ID);
    
	public static final DeferredItem<Item> ADVANCED_BUCKET = ITEMS.register("advanced_bucket", () -> AdvancedBucket.getBucketForReg(Fluids.EMPTY));

	public static void register(IEventBus modBus) {
		ITEMS.register(modBus);
        modBus.addListener(Items::registerCapabilities);
	}

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
            Capabilities.FluidHandler.ITEM,
            (stack, context) -> new ExtendedFHIS(stack, 1000),
            ADVANCED_BUCKET.get()
        );
    }
}
