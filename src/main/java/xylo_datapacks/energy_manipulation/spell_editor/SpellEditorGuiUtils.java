package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.RawValueGlyph;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.GlyphSelectorGui;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.MultipleChoiceInputGui;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class SpellEditorGuiUtils {

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
                .setName(Component.literal("Pin: " + pinDisplayName + " | " + connectedGlyphDisplayName))
                .setCallback(clickType -> editorGui.openGlyphSelector(glyphInstance, pinIndex))
                .build();
    }
    
    public static SimpleGuiElement makeArrayPinDecoratorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance, int pinIndex) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.INSERT_OR_REMOVE_ELEMENT_BUTTON.get())
                .setName(Component.literal("+ / -"))
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
                .build();
    }

    public static SimpleGuiElement makeArrayGlyphTerminatorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.ADD_ELEMENT_BUTTON.get())
                .setName(Component.literal(")+"))
                .setCallback(clickType -> editorGui.addArrayPin(glyphInstance))
                .build();
    }

    public static SimpleGuiElement makeRawValueSelectorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance) {
        Optional<GlyphValue> glyphValue = GlyphsRegistry.RAW_VALUE_GLYPH.getPayloadValue(glyphInstance);
        
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getValueTypeButtonStack(glyphInstance.outputPin.valueType))
                .setName(Component.literal(glyphValue.isPresent() ? glyphValue.get().getDebugString() : "Unset Value"))
                .setCallback(clickType -> editorGui.openValueSelector(glyphInstance))
                .build();
    }

    public static SimpleGuiElement makeGlyphOptionGuiElement(GlyphSelectorGui selectorGui, Glyph glyph, GlyphValueType valueType) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getGlyphButtonStack(glyph, valueType))
                .setName(Component.literal(glyph.getClass().getSimpleName()))
                .setCallback(clickType -> {
                    GlyphUtils.connectNewGlyph(selectorGui.getGlyphInstance(), selectorGui.getPinIndex(), glyph);
                    selectorGui.goBackToEditor();
                })
                .build();
    }
    
    public static SimpleGuiElement makeOperatorPinDecoratorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance, int pinIndex) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getOperatorSeparatorButtonStack(glyphInstance.glyph))
                .setName(Component.literal(""))
                .build();
    }

    public static SimpleGuiElement makeValueTypeOptionElement(MultipleChoiceInputGui multipleChoiceInputGui, GlyphValueType valueType) {
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getValueTypeButtonStack(valueType))
                .setName(Component.literal(valueType.getClass().getSimpleName()))
                .setCallback(clickType -> {
                    GlyphInstance glyphInstance = multipleChoiceInputGui.getGlyphInstance();
                    ((RawValueGlyph) glyphInstance.glyph).setPayloadValue(glyphInstance, GlyphsRegistry.CLASS_VALUE_TYPE.makeClassGlyphValue(valueType));
                    multipleChoiceInputGui.goBackToEditor();
                })
                .build();
    }
}
