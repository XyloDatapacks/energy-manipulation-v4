package xylo_datapacks.energy_manipulation.spell_editor;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SpellEditorButtonsRegistry {
    // Buttons registry
    public static final ResourceKey<Registry<Supplier<ItemStack>>> SPELL_EDITOR_BUTTON_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "spell_editor_button_registry"));
    public static final Registry<Supplier<ItemStack>> SPELL_EDITOR_BUTTON = FabricRegistryBuilder.create(SPELL_EDITOR_BUTTON_REGISTRY_KEY).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    // Glyph buttons registry (fix for data gen since I do not have good textures for the glyphs and I need to scale them up more)
    public static final ResourceKey<Registry<Supplier<ItemStack>>> SPELL_EDITOR_GLYPH_BUTTON_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "spell_editor_glyph_button_registry"));
    public static final Registry<Supplier<ItemStack>> SPELL_EDITOR_GLYPH_BUTTON = FabricRegistryBuilder.create(SPELL_EDITOR_GLYPH_BUTTON_REGISTRY_KEY).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    
    public static final Supplier<ItemStack> CONFIRM_BUTTON = registerButton("confirm_button", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    public static final Supplier<ItemStack> CONFIRM_BUTTON_DISABLED = registerButton("confirm_button_disabled", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    public static final Supplier<ItemStack> CANCEL_BUTTON = registerButton("cancel_button", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    public static final Supplier<ItemStack> NEXT_PAGE_BUTTON = registerButton("next_page_button", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    public static final Supplier<ItemStack> PREVIOUS_PAGE_BUTTON = registerButton("previous_page_button", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    public static final Supplier<ItemStack> NEXT_PAGE_BUTTON_DISABLED = registerButton("next_page_button_disabled", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    public static final Supplier<ItemStack> PREVIOUS_PAGE_BUTTON_DISABLED = registerButton("previous_page_button_disabled", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    
    public static final Supplier<ItemStack> INSERT_OR_REMOVE_ELEMENT_BUTTON = registerGlyphButton("insert_or_remove_element_button", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    public static final Supplier<ItemStack> ADD_ELEMENT_BUTTON = registerGlyphButton("add_element_button", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    public static final Supplier<ItemStack> ARRAY_START_BUTTON = registerGlyphButton("array_start_button", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    public static final Supplier<ItemStack> EMPTY_PIN_BUTTON = registerGlyphButton("empty_pin_button", () -> setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance()));
    
    public static ItemStack getGlyphButtonStack(Glyph glyph, GlyphValueType outputValueType) {
        ItemStack glyphButtonStack = setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance());
        String customModelDataName = GlyphsRegistry.GLYPH.getKey(glyph).getPath();
        if (glyph.getEditorData().bHasTypeDependentTexture) {
            customModelDataName = customModelDataName + "_" + GlyphsRegistry.VALUE_TYPE.getKey(outputValueType).getPath();
        }
        setCustomModelData(glyphButtonStack, customModelDataName);
        return glyphButtonStack;
    }

    public static ItemStack getValueTypeButtonStack(GlyphValueType valueType) {
        ItemStack valueSelectorButtonStack = setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance());
        setCustomModelData(valueSelectorButtonStack, GlyphsRegistry.VALUE_TYPE.getKey(valueType).getPath());
        return valueSelectorButtonStack;
    }

    public static ItemStack getOperatorSeparatorButtonStack(Glyph glyph) {
        ItemStack operatorSeparatorButtonStack = setTooltipStyle(EnergyManipulationItems.GUI_BUTTON.getDefaultInstance());
        setCustomModelData(operatorSeparatorButtonStack, GlyphsRegistry.GLYPH.getKey(glyph).getPath() + "_separator");
        return operatorSeparatorButtonStack;
    }

    protected static void setCustomModelData(ItemStack stack, String name) {
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
    
    public static ItemStack setTooltipStyle(ItemStack stack) {
        stack.set(DataComponents.TOOLTIP_STYLE, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "spell_book/button"));
        return stack;
    }
    
    /*--------------------------------------------------------------------------------------*/

    public static Supplier<ItemStack> registerGlyphButton(String name, Supplier<ItemStack> factory) {
        Supplier<ItemStack> output = () -> {
            ItemStack stack = factory.get();
            setCustomModelData(stack, name);
            return stack;
        };
        Registry.register(SPELL_EDITOR_GLYPH_BUTTON, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name), output);
        return output;
    }

    public static Supplier<ItemStack> registerButton(String name, Supplier<ItemStack> factory) {
        Supplier<ItemStack> output = () -> {
            ItemStack stack = factory.get();
            setCustomModelData(stack, name);
            return stack;
        };
        Registry.register(SPELL_EDITOR_BUTTON, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name), output);
        return output;
    }

    public static void initialize() {
    }
}
