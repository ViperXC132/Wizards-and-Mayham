package com.viperxc.wizardsmayham;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WizardsMayham implements ModInitializer {
    public static final String MOD_ID = "wizardsmayham";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Wizards and Mayham is initializing...");
    }
}
