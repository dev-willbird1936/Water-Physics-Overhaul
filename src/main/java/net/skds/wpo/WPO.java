package net.skds.wpo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.skds.wpo.network.PacketHandler;
import net.skds.wpo.registry.BlockStateProps;
import net.skds.wpo.registry.Entities;
import net.skds.wpo.registry.FBlocks;
import net.skds.wpo.registry.Items;
import net.skds.wpo.registry.WPODataComponents;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WPO.MOD_ID)
public class WPO
{
    public static final String MOD_ID = "wpo";
    public static final String MOD_NAME = "Water Physics Overhaul";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static Events EVENTS = new Events();

    public WPO(IEventBus modBus, ModContainer container) {
        modBus.addListener(this::setup);

        NeoForge.EVENT_BUS.register(EVENTS);
      
        WPOConfig.init(container);
        WPODataComponents.register(modBus);
        Items.register(modBus);
        FBlocks.register(modBus);
        Entities.register(modBus);
        PacketHandler.init(modBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            WPOClientHooks.init(modBus, container);
        }
    }
    

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(WPO::validateExtraStateInjection);
    }

    private static void validateExtraStateInjection() {
        boolean ok = true;
        ok &= validateExtraStateTarget(Blocks.OAK_STAIRS);
        ok &= validateExtraStateTarget(Blocks.OAK_DOOR);
        ok &= validateExtraStateTarget(Blocks.OAK_LEAVES);
        if (ok) {
            LOGGER.info("Verified WPO extra-state injection on representative waterloggable blocks");
        }
    }

    private static boolean validateExtraStateTarget(Block block) {
        BlockState state = block.defaultBlockState();
        boolean hasWaterlogged = state.hasProperty(BlockStateProperties.WATERLOGGED);
        boolean hasFluidLevel = state.hasProperty(BlockStateProps.FFLUID_LEVEL);
        if (hasWaterlogged && hasFluidLevel) {
            return true;
        }
        LOGGER.error("Missing WPO extra-state injection on {} (waterlogged={}, ffluid_level={})",
            BuiltInRegistries.BLOCK.getKey(block), hasWaterlogged, hasFluidLevel);
        return false;
    }
}
