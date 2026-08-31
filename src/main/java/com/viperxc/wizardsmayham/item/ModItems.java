package com.viperxc.wizardsmayham.item;

import com.viperxc.wizardsmayham.WizardsMayham;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public final class ModItems {
    public static final Item MAGIC_WAND = register("magic_wand", WandItem::new, new Item.Properties().stacksTo(1));
    public static final Item MANA_RESTORER = register("mana_restorer", ManaRestorerItem::new, new Item.Properties().stacksTo(16));
    public static final Item ANCIENT_MAGIC_BOOK = register("ancient_magic_book", Item::new, new Item.Properties().stacksTo(1));

    private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(WizardsMayham.MOD_ID, name)
        );
        Item item = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void initialize() {
        WizardsMayham.LOGGER.info("Registering Wizards and Mayham items");
    }
}
