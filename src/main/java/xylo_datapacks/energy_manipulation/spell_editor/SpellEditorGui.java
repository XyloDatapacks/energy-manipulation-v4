package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;

import java.util.concurrent.atomic.AtomicInteger;

public class SpellEditorGui extends SimpleGui {
    private final SpellEditor editor;

    public SpellEditorGui(ServerPlayer player, SpellEditor editor) {
        super(MenuType.GENERIC_9x6, player, false);
        this.editor = editor;
        this.editor.onInstanceChangedCallback = this::onInstanceChanged;

        this.setTitle(Component.literal("Spell Editor"));
        this.setupSlots();
    }

    private void setupSlots() {
        this.setSlot(50, new GuiElementBuilder(Items.RED_WOOL)
                .setName(Component.literal("Cancel"))
                .setCallback(clickType -> {
                    player.sendSystemMessage(Component.literal("Clicked cancel"));
                })
                .build());

        this.setSlot(51, new GuiElementBuilder(Items.GREEN_WOOL)
                .setName(Component.literal("Confirm"))
                .setCallback(clickType -> {
                    player.sendSystemMessage(Component.literal("Clicked confirm"));
                })
                .build());
        
        this.setSlot(52, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                .setName(Component.literal("Previous Page"))
                .setCallback(clickType -> {
                    player.sendSystemMessage(Component.literal("Clicked previous page"));
                })
                .build());

        this.setSlot(53, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                .setName(Component.literal("Next Page"))
                .setCallback(clickType -> {
                    player.sendSystemMessage(Component.literal("Clicked next page"));
                })
                .build());
    }

    @Override
    public void onManualClose() {
        super.onManualClose();
        onClose();
    }

    @Override
    public void onPlayerClose(boolean success) {
        super.onPlayerClose(success);
        onClose();
    }
    
    public void onClose() {
    }
    
    public void onInstanceChanged() {
        AtomicInteger currentSlot = new AtomicInteger(0);
        recursiveCreateSpellGuiElements(editor.currentGlyphInstance, currentSlot);
    }
    
    public void recursiveCreateSpellGuiElements(GlyphInstance glyphInstance, AtomicInteger currentSlot) {
        // Stop this branch if no glyph instance.
        if (glyphInstance == null) {
            return;
        }
        
        for (int i = 0; i < glyphInstance.inputPins.size(); i++) {
            // Get new slot index, and if we are overflowing stop here.
            int slotIndex = currentSlot.getAndIncrement();
            if (slotIndex >= 44) {
                return;
            }
            // Add new element.
            this.setSlot(slotIndex, generatePinGuiElement(glyphInstance, i));
            // Recursive call for the connected glyph.
            recursiveCreateSpellGuiElements(glyphInstance.inputPins.get(i).connectedGlyph, currentSlot);
        }
    }
    
    public SimpleGuiElement generatePinGuiElement(GlyphInstance glyphInstance, int pinIndex) {
        InputPin pinToDisplay = glyphInstance.glyph.getInputPin(glyphInstance, pinIndex).get();
        GlyphInstance connectedGlyphInstance = pinToDisplay.connectedGlyph;
        String connectedGlyphDisplayName = connectedGlyphInstance.glyph.getClass().getSimpleName();
       
        InputPinDefinition pinDefinitionToDisplay = glyphInstance.glyph.getInputPinDefinition(pinIndex).get();
        String pinDisplayName = pinDefinitionToDisplay.pinName;
        
        return new GuiElementBuilder(Items.PAPER)
                .setName(Component.literal("Pin: " + pinDisplayName + " | " + connectedGlyphDisplayName))
                .setCallback(clickType -> {
                    String compatibleGlyphs = editor.printCompatibleGlyphs(glyphInstance, pinIndex);
                    player.sendSystemMessage(Component.literal(compatibleGlyphs));
                })
                .build();
    }
}