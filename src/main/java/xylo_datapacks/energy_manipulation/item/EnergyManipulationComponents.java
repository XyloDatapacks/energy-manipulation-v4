package xylo_datapacks.energy_manipulation.item;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import xylo_datapacks.energy_manipulation.EnergyManipulation;

import java.util.function.UnaryOperator;


public class EnergyManipulationComponents {
    public static final DataComponentType<ItemContainerContents> SPELL_BOOK_STORAGE = registerItemComponent(
            "spell_book_storage", b -> b.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding()
    );
    public static final DataComponentType<CustomData> SPELL_CONTAINER = registerItemComponent("spell_container", b -> b.persistent(CustomData.CODEC));
    
    
    public static <T> DataComponentType<T> registerItemComponent(String name, final UnaryOperator<DataComponentType.Builder<T>> builder) {
        Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name);
        DataComponentType<T> componentType = builder.apply(DataComponentType.builder()).build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, identifier, componentType);
        PolymerComponent.registerDataComponent(componentType);
        return componentType;
    }
    
    public static void initialize() {
        
    }
}
