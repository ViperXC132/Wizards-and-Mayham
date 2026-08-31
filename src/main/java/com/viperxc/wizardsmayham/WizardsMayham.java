package com.viperxc.wizardsmayham;

import com.viperxc.wizardsmayham.item.ModItems;
import com.viperxc.wizardsmayham.magic.MagicDataStore;
import com.viperxc.wizardsmayham.magic.SpellRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WizardsMayham implements ModInitializer {
    public static final String MOD_ID = "wizardsmayham";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Wizards and Mayham core...");
        ModItems.initialize();
        SpellRegistry.initialize();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) return;
            MagicDataStore store = MagicDataStore.get(server);
            server.getPlayerList().getPlayers().forEach(player -> store.get(player.getUUID()).regen());
            store.markDirty();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            MagicDataStore.get(server).get(handler.getPlayer().getUUID());
            MagicDataStore.get(server).markDirty();
        });
    }
}
