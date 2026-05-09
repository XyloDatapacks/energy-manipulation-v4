package xylo_datapacks.energy_manipulation.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import xylo_datapacks.energy_manipulation.EnergyManipulation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EnergyManipulationItemsUtils {

    public static Optional<CompoundTag> getCompoundTag(ItemStack itemStack, Identifier path) {
        // Get custom data from stack.
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        
        // Get the namespaced compound.
        Optional<CompoundTag> modCompound = customData.copyTag().getCompound(path.getNamespace());
        if (modCompound.isEmpty()) {
            return Optional.empty();
        }
        
        // Break the path and find the innermost compound.
        String[] compoundKeys = path.getPath().split("\\.");
        CompoundTag latestCompound = modCompound.get();
        for (String compoundKey : compoundKeys) {
            Optional<CompoundTag> foundCompound = latestCompound.getCompound(compoundKey);
            if (foundCompound.isEmpty()) {
                return Optional.empty();
            }
            latestCompound = foundCompound.get();
        }
        
        // If we reached here we actually found the compound we are looking for, and it is valid. 
        return Optional.of(latestCompound);
    }
    
    public static void updateCompoundTag(ItemStack itemStack, Identifier path, Consumer<CompoundTag> consumer) {
        itemStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData ->
                customData.update(compound -> {
                    CompoundTag modCompound = compound.getCompoundOrEmpty(path.getNamespace());

                    List<String> compoundKeys = Arrays.stream(path.getPath().split("\\.")).toList();
                    updateRecursive(modCompound, compoundKeys, consumer);
                    
                    compound.put(EnergyManipulation.MOD_ID, modCompound);
                })
        );
    }
    
    private static void updateRecursive(CompoundTag compound, List<String> compoundKeys, Consumer<CompoundTag> consumer) {
        if (compoundKeys.isEmpty() || compoundKeys.getFirst().isEmpty()) {
            consumer.accept(compound);
        } else {
            CompoundTag innerCompound = compound.getCompoundOrEmpty(compoundKeys.getFirst());
            updateRecursive(innerCompound, compoundKeys.subList(1, compoundKeys.size()), consumer);
            compound.put(compoundKeys.getFirst(), innerCompound);
        }
    }
    
    
}
