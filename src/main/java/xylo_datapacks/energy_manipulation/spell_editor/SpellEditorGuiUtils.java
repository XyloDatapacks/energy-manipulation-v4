package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.GlyphSelectorGui;

import java.util.Optional;

public class SpellEditorGuiUtils {
    
    public static SimpleGuiElement makePinGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance, int pinIndex) {
        InputPin pinToDisplay = glyphInstance.glyph.getInputPin(glyphInstance, pinIndex).get();
        GlyphInstance connectedGlyphInstance = pinToDisplay.connectedGlyph;
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
        
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getValueSelectorButtonStack(glyphInstance.outputPin.valueType))
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
    
}
