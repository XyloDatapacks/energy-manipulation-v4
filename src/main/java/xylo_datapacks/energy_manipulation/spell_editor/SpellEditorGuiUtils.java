package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.variable.RawValueGlyph;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.Optional;

public class SpellEditorGuiUtils {
    
    public static SimpleGuiElement makePinGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance, int pinIndex) {
        InputPin pinToDisplay = glyphInstance.glyph.getInputPin(glyphInstance, pinIndex).get();
        GlyphInstance connectedGlyphInstance = pinToDisplay.connectedGlyph;
        String connectedGlyphDisplayName = connectedGlyphInstance != null ? connectedGlyphInstance.glyph.getClass().getSimpleName() : "None";

        InputPinDefinition pinDefinitionToDisplay = glyphInstance.glyph.getInputPinDefinition(pinIndex).get();
        String pinDisplayName = pinDefinitionToDisplay.pinName;
        
        return new GuiElementBuilder(connectedGlyphInstance != null ? Items.PAPER : Items.BARRIER)
                .setName(Component.literal("Pin: " + pinDisplayName + " | " + connectedGlyphDisplayName))
                .setCallback(clickType -> {
                    // TODO: open glyph selector gui
                    
                    String compatibleGlyphs = editorGui.getSpellEditor().printCompatibleGlyphs(glyphInstance, pinIndex);
                    editorGui.getPlayer().sendSystemMessage(Component.literal(compatibleGlyphs));
                })
                .build();
    }
    
    public static SimpleGuiElement makeArrayPinDecoratorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance, int pinIndex) {
        return new GuiElementBuilder(Items.MAP)
                .setName(Component.literal("+ / -"))
                .setCallback(clickType -> {
                    
                    if (clickType.isRight) {
                        // Remove pin
                        glyphInstance.glyph.removePin(glyphInstance, pinIndex);
                    }
                    else {
                        // Insert pin
                        glyphInstance.glyph.insertPin(glyphInstance, pinIndex);
                    }
                    // refresh ui
                    editorGui.onInstanceChanged();
                    
                    String outputString = (clickType.isRight ? "Removing pin" : "Adding pin") + " " + pinIndex;
                    editorGui.getPlayer().sendSystemMessage(Component.literal(outputString));
                })
                .build();
    }

    public static SimpleGuiElement makeArrayGlyphOpenerGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance) {
        return new GuiElementBuilder(Items.LIGHT_BLUE_CONCRETE)
                .setName(Component.literal("("))
                .build();
    }

    public static SimpleGuiElement makeArrayGlyphTerminatorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance) {
        return new GuiElementBuilder(Items.ORANGE_CONCRETE)
                .setName(Component.literal(")+"))
                .setCallback(clickType -> {
                    
                    // Add new pin and refresh
                    glyphInstance.glyph.addPin(glyphInstance);
                    editorGui.onInstanceChanged();

                    String outputString = "Adding pin " + (glyphInstance.inputPins.size() - 1);
                    editorGui.getPlayer().sendSystemMessage(Component.literal(outputString));
                })
                .build();
    }

    public static SimpleGuiElement makeRawValueSelectorGuiElement(SpellEditorGui editorGui, GlyphInstance glyphInstance) {
        Optional<GlyphValue> glyphValue = GlyphsRegistry.RAW_VALUE_GLYPH.getPayloadValue(glyphInstance);
        
        return new GuiElementBuilder(Items.REDSTONE_TORCH)
                .setName(Component.literal(glyphValue.isPresent() ? glyphValue.get().getDebugString() : "Unset Value"))
                .setCallback(clickType -> {
                    // TODO: open glyph value selector gui
                    
                    String outputString = "selecting value for " + glyphInstance.glyph.getClass().getSimpleName();
                    editorGui.getPlayer().sendSystemMessage(Component.literal(outputString));
                })
                .build();
    }
    
}
