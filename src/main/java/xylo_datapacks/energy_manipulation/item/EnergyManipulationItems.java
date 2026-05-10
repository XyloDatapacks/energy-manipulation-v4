package xylo_datapacks.energy_manipulation.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.item.spell.GuiButtonItem;
import xylo_datapacks.energy_manipulation.item.spell.SpellBookItem;
import xylo_datapacks.energy_manipulation.item.spell.SpellScrollItem;

import java.util.function.Function;

public class EnergyManipulationItems {
    public static final SpellBookItem SPELL_BOOK = registerItem("spell_book", SpellBookItem::new, new Item.Properties());
    public static final SpellScrollItem SPELL_SCROLL = registerItem("spell_scroll", SpellScrollItem::new, new Item.Properties());
    public static final GuiButtonItem GUI_BUTTON = registerItem("gui_button", GuiButtonItem::new, new Item.Properties());


    public static <T extends Item> T registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name);
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, identifier);
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
    
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
            output.accept(EnergyManipulationItems.SPELL_BOOK);
            output.accept(EnergyManipulationItems.SPELL_SCROLL);
        });
    }
}
