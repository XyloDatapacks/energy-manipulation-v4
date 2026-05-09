package xylo_datapacks.energy_manipulation.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import xylo_datapacks.energy_manipulation.EnergyManipulation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EnergyManipulationItemsUtils {

    public static Optional<Tag> getTag(ItemStack itemStack, Identifier path) {
        // Get custom data from stack.
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return Optional.empty();
        }
        
        // Get the namespaced compound.
        Optional<CompoundTag> modCompound = customData.copyTag().getCompound(path.getNamespace());
        if (modCompound.isEmpty()) {
            return Optional.empty();
        }
        
        // Break the path and find the innermost compound wrapping the tag we are looking for.
        String[] compoundKeys = path.getPath().split("\\.");
        CompoundTag latestCompound = modCompound.get();
        for (int i = 0; i < (compoundKeys.length - 1); i++) {
            latestCompound = latestCompound.getCompound(compoundKeys[i]).orElse(null);
            // Return if the hierarchy is broken
            if (latestCompound == null) {
                return Optional.empty();
            }
        }
        
        // If we reached here we actually found the compound wrapping the element we are looking for
        return Optional.ofNullable(latestCompound.get(compoundKeys[compoundKeys.length - 1]));
    }

    public static void setTag(ItemStack itemStack, Identifier path, Tag tag) {
        itemStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData ->
                customData.update(compound -> {
                    CompoundTag modCompound = compound.getCompoundOrEmpty(path.getNamespace());

                    List<String> compoundKeys = Arrays.stream(path.getPath().split("\\.")).toList();
                    updateRecursive(modCompound, compoundKeys.subList(0, compoundKeys.size() - 1), compoundTag -> {
                        compoundTag.put(compoundKeys.getLast(), tag);
                    });

                    compound.put(EnergyManipulation.MOD_ID, modCompound);
                })
        );
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
