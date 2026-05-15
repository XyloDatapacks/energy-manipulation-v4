package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import xylo_datapacks.energy_manipulation.font.EnergyManipulationFonts;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.RawValueGlyph;
import xylo_datapacks.energy_manipulation.glyph.value_type.EnumValueType;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.glyph.value_type.VarNameValueType;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.GlyphSelectorGui;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.MultipleChoiceInputGui;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class SpellEditorGuiUtils {
    static final Style PRIMARY_TOOLTIP_STYLE = Style.EMPTY.withFont(FontDescription.DEFAULT).withColor(ChatFormatting.BLACK).withoutShadow();
    static final Style ERROR_TOOLTIP_STYLE = Style.EMPTY.withFont(FontDescription.DEFAULT).withColor(ChatFormatting.DARK_RED).withoutShadow();
    static final Style ADVICE_TOOLTIP_STYLE = Style.EMPTY.withFont(FontDescription.DEFAULT).withColor(ChatFormatting.DARK_GRAY).withoutShadow();
    static final Style ICON_TOOLTIP_STYLE = Style.EMPTY.withFont(EnergyManipulationFonts.SPELL_BOOK_ICON).withColor(ChatFormatting.WHITE).withoutShadow();

    /** @return true if currentSlot is out of bounds. */
    public static boolean isOutOfGlyphsDrawingSpace(int pageSize, int currentPage, AtomicInteger currentSlot) {
        return currentSlot.get() >= pageSize * (currentPage + 1);
    }

    /** Adds a glyph related gui element at the correct slot only if possible / needed. */
    public static void safeAddGlyphGuiElement(SimpleGui gui, int pageSize, int currentPage, AtomicInteger currentSlot, Supplier<Optional<SimpleGuiElement>> supplier) {
        // Get new slot index.
        int slotIndex = currentSlot.get();
        // If slot index exceeds the last representable index then quit.
        if (isOutOfGlyphsDrawingSpace(pageSize, currentPage, currentSlot)) {
            return;
        }

        // Create new element.
        Optional<SimpleGuiElement> optionalElement = supplier.get();
        if (optionalElement.isPresent()) {
            // Only set the slot if visible in current page.
            if (slotIndex >= pageSize * (currentPage)) {
                gui.setSlot(slotIndex % pageSize, optionalElement.get());
            }

            // If the element exists, increment the slot count.
            currentSlot.incrementAndGet();
        }
    }
    
    public static SimpleGuiElement makePinGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance, int pinIndex) {
        InputPin pinToDisplay = glyphInstance.glyph.getInputPin(glyphInstance, pinIndex).get();
        GlyphInstance connectedGlyphInstance = pinToDisplay.getConnectedGlyph().orElse(null);
        String connectedGlyphDisplayName = connectedGlyphInstance != null ? connectedGlyphInstance.glyph.getClass().getSimpleName() : "None";

        InputPinDefinition pinDefinitionToDisplay = glyphInstance.glyph.getInputPinDefinition(pinIndex).get();
        String pinDisplayName = pinDefinitionToDisplay.pinName;
        
        ItemStack buttonStack = connectedGlyphInstance != null ? SpellEditorButtonsRegistry.getGlyphButtonStack(connectedGlyphInstance.glyph, pinToDisplay.valueType) : SpellEditorButtonsRegistry.EMPTY_PIN_BUTTON.get();
        return new GuiElementBuilder(buttonStack)
                .setName(Component.literal(connectedGlyphInstance == null ? "\uE000" : "\uE001").setStyle(ICON_TOOLTIP_STYLE).append(Component.literal(pinDisplayName).setStyle(PRIMARY_TOOLTIP_STYLE)))
                .setLore(List.of(
                        Component.literal("> " + connectedGlyphDisplayName).setStyle(PRIMARY_TOOLTIP_STYLE), 
                        Component.literal(""), 
                        makeClickActionComponent("L", "Change Glyph")
                ))
                .setCallback(clickType -> editorGui.openGlyphSelector(glyphInstance, pinIndex))
                .build();
    }
    
    public static SimpleGuiElement makeArrayPinDecoratorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance, int pinIndex) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.INSERT_OR_REMOVE_ELEMENT_BUTTON.get())
                .setName(makeClickActionComponent("L", "Add pin"))
                .setLore(List.of(
                        makeClickActionComponent("R", "Remove pin")
                ))
                .setCallback(clickType -> {
                    if (clickType.isRight) {
                        editorGui.removeArrayPin(glyphInstance, pinIndex);
                    }
                    else {
                        editorGui.insertArrayPin(glyphInstance, pinIndex);
                    }
                })
                .build();
    }

    public static SimpleGuiElement makeArrayGlyphOpenerGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.ARRAY_START_BUTTON.get())
                .setName(Component.literal("("))
                .hideTooltip()
                .build();
    }

    public static SimpleGuiElement makeArrayGlyphTerminatorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.ADD_ELEMENT_BUTTON.get())
                .setName(makeClickActionComponent("L", "Add pin"))
                .setCallback(clickType -> editorGui.addArrayPin(glyphInstance))
                .build();
    }

    public static SimpleGuiElement makeRawValueSelectorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance) {
        Optional<GlyphValue> glyphValue = GlyphsRegistry.RAW_VALUE_GLYPH.getPayloadValue(glyphInstance);
        GlyphValueType valueType = glyphInstance.outputPin.valueType;
        
        String displayValue;
        ItemStack buttonStack;
        boolean bValidValue = true;
        
        if (valueType instanceof EnumValueType<?> enumValueType) {
            // Enums can be displayed as their value name and use the icon specific to the enum value.
            displayValue = enumValueType.getValueId(glyphValue.get()); // TODO: translation string 
            buttonStack = SpellEditorButtonsRegistry.getEnumValueButtonStack(enumValueType, displayValue);
        }
        else if (valueType == GlyphsRegistry.VAR_NAME_VALUE_TYPE) {
            // Variables can be displayed as their name and use their type as icon.
            VarNameValueType.VariableDescription varDescription = glyphValue.map(GlyphsRegistry.VAR_NAME_VALUE_TYPE::getVarDescription).orElse(new VarNameValueType.VariableDescription("", null));
            GlyphValueType varValueType = varDescription.valueType();

            displayValue = varDescription.name();
            buttonStack = varValueType != null ? SpellEditorButtonsRegistry.getValueTypeButtonStack(varValueType) : SpellEditorButtonsRegistry.EMPTY_PIN_BUTTON.get();
            bValidValue = editorGui.editor.isInScope(varDescription.name(), varDescription.valueType(), glyphInstance);
        }
        else {
            // Simple values (numbers, strings, booleans, etc.) can just display the debug string and use the type as icon.
            displayValue = glyphValue.isPresent() ? glyphValue.get().getDebugString() : "Unset Value";
            buttonStack = valueType != null ? SpellEditorButtonsRegistry.getValueTypeButtonStack(valueType) : SpellEditorButtonsRegistry.EMPTY_PIN_BUTTON.get();
        }
        
        return new GuiElementBuilder(buttonStack)
                .setName(Component.literal(displayValue).setStyle(bValidValue ? PRIMARY_TOOLTIP_STYLE : ERROR_TOOLTIP_STYLE))
                .setCallback(clickType -> editorGui.openValueSelector(glyphInstance))
                .build();
    }

    public static SimpleGuiElement makeGlyphOptionGuiElement(GlyphSelectorGui selectorGui, Glyph glyph, GlyphValueType valueType) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getGlyphButtonStack(glyph, valueType))
                .setName(Component.literal(glyph.getClass().getSimpleName()).setStyle(PRIMARY_TOOLTIP_STYLE))
                .setLore(List.of(
                        Component.literal("...").setStyle(PRIMARY_TOOLTIP_STYLE), 
                        Component.literal(""), 
                        makeClickActionComponent("L", "Select Glyph")
                ))
                .setCallback(clickType -> {
                    GlyphUtils.connectNewGlyph(selectorGui.getGlyphInstance(), selectorGui.getPinIndex(), glyph);
                    selectorGui.goBackToEditor();
                })
                .build();
    }
    
    public static SimpleGuiElement makeOperatorPinDecoratorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance, int pinIndex) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getOperatorSeparatorButtonStack(glyphInstance.glyph))
                .setName(Component.literal(""))
                .hideTooltip()
                .build();
    }

    public static SimpleGuiElement makeValueTypeOptionElement(MultipleChoiceInputGui multipleChoiceInputGui, GlyphValueType valueType) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getValueTypeButtonStack(valueType))
                .setName(Component.literal(valueType.getClass().getSimpleName()).setStyle(PRIMARY_TOOLTIP_STYLE))
                .setLore(List.of(
                        Component.literal("...").setStyle(PRIMARY_TOOLTIP_STYLE), 
                        Component.literal(""), 
                        makeClickActionComponent("L", "Select Value Type")
                ))
                .setCallback(clickType -> {
                    GlyphInstance glyphInstance = multipleChoiceInputGui.getGlyphInstance();
                    ((RawValueGlyph) glyphInstance.glyph).setPayloadValue(glyphInstance, GlyphsRegistry.CLASS_VALUE_TYPE.makeClassGlyphValue(valueType));
                    multipleChoiceInputGui.goBackToEditor();
                })
                .build();
    }

    public static SimpleGuiElement makeVariableOptionElement(MultipleChoiceInputGui multipleChoiceInputGui, String varName, GlyphValueType varValueType) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getValueTypeButtonStack(varValueType))
                .setName(Component.literal(varName).setStyle(PRIMARY_TOOLTIP_STYLE))
                .setLore(List.of(
                        Component.literal(""), 
                        makeClickActionComponent("L", "Select Variable")
                ))
                .setCallback(clickType -> {
                    GlyphInstance glyphInstance = multipleChoiceInputGui.getGlyphInstance();
                    ((RawValueGlyph) glyphInstance.glyph).setPayloadValue(glyphInstance, GlyphsRegistry.VAR_NAME_VALUE_TYPE.makeVarNameValue(varName, varValueType));
                    multipleChoiceInputGui.goBackToEditor();
                })
                .build();
    }

    public static SimpleGuiElement makeEnumOptionElement(MultipleChoiceInputGui multipleChoiceInputGui, String valueName, EnumValueType<?> enumValueType) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getEnumValueButtonStack(enumValueType, valueName.toLowerCase()))
                .setName(Component.literal(valueName.toLowerCase()).setStyle(PRIMARY_TOOLTIP_STYLE)) // TODO: translation string 
                .setLore(List.of(
                        Component.literal(""),
                        makeClickActionComponent("L", "Select")
                ))
                .setCallback(clickType -> {
                    GlyphInstance glyphInstance = multipleChoiceInputGui.getGlyphInstance();
                    ((RawValueGlyph) glyphInstance.glyph).setPayloadValue(glyphInstance, enumValueType.makeEnumGlyphValue(valueName));
                    multipleChoiceInputGui.goBackToEditor();
                })
                .build();
    }
    
    public static Component makeClickActionComponent(String click, String action) {
        return Component.literal(click).setStyle(ICON_TOOLTIP_STYLE)
                .append(Component.literal(" " + action).setStyle(ADVICE_TOOLTIP_STYLE));
    }
}
