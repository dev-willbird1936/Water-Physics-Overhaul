package net.skds.wpo;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.skds.wpo.client.ClientEvents;
import net.skds.wpo.config.WPOConfigScreen;

final class WPOClientHooks {

    private WPOClientHooks() {
    }

    static void init(IEventBus modBus, ModContainer container) {
        modBus.addListener((FMLClientSetupEvent event) -> {
            ClientEvents.setup(event);
            event.enqueueWork(() -> container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (modContainer, modListScreen) -> new WPOConfigScreen(modListScreen)
            ));
        });
    }
}
