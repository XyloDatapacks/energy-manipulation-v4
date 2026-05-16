package xylo_datapacks.energy_manipulation.utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

import java.util.ArrayList;
import java.util.List;

public class DataComponentsUtils {

    public static void setCustomModelDataString(ItemStack stack, String name) {
        stack.update(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.EMPTY, customModelData -> {
            List<String> strings = new ArrayList<>(customModelData.strings());

            if (strings.size() <= 0) {
                strings.add("");
            }
            strings.set(0, name);

            return new CustomModelData(
                    customModelData.floats(),
                    customModelData.flags(),
                    strings,
                    customModelData.colors()
            );
        });
    }
    
}
