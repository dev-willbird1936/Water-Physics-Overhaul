package net.skds.wpo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.skds.wpo.client.ClientEvents;
import net.skds.wpo.config.WPOConfigScreen;
import net.skds.wpo.network.PacketHandler;
import net.skds.wpo.registry.BlockStateProps;
import net.skds.wpo.registry.Entities;
import net.skds.wpo.registry.FBlocks;
import net.skds.wpo.registry.Items;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WPO.MOD_ID)
public class WPO
{
    public static final String MOD_ID = "wpo";
    public static final String MOD_NAME = "Water Physics Overhaul";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static Events EVENTS = new Events();

    public WPO() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(EVENTS);
        MinecraftForge.EVENT_BUS.register(this);
      
        WPOConfig.init();
        Items.register();
        FBlocks.register();
        Entities.register();
        PacketHandler.init();
    }
    

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(WPO::validateExtraStateInjection);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {  
        ClientEvents.setup(event);
        event.enqueueWork(() -> ModList.get().getModContainerById(MOD_ID).ifPresent(container ->
                container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory(WPOConfigScreen::new))));
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
