package com.viperxc.wizardsmayham.item;

import com.viperxc.wizardsmayham.WizardsMayham;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ModItems {
    public static final Item MAGIC_WAND = register("magic_wand", new WandItem(new Item.Properties().stacksTo(1)));
    public static final Item MANA_RESTORER = register("mana_restorer", new ManaRestorerItem(new Item.Properties().stacksTo(16)));
    public static final Item ANCIENT_MAGIC_BOOK = register("ancient_magic_book", new Item(new Item.Properties().stacksTo(1)));

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(WizardsMayham.MOD_ID, id), item);
    }

    public static void initialize() {
        WizardsMayham.LOGGER.info("Registering Wizards and Mayham items");
    }
}
