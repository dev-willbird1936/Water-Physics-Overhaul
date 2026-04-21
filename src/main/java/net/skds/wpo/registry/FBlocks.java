package net.skds.wpo.registry;

import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skds.wpo.WPO;

public class FBlocks {
	
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WPO.MOD_ID);
	
	public static void register(IEventBus modBus) {
		BLOCKS.register(modBus);
	}
}
