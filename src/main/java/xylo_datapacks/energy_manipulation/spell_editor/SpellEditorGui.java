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
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.variable.RawValueGlyph;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class SpellEditorGui extends SimpleGui {
    static final int pageSize = 9*5;
    private final SpellEditor editor;
    private int currentPage;

    public SpellEditorGui(ServerPlayer player, SpellEditor editor) {
        super(MenuType.GENERIC_9x6, player, false);
        
        this.editor = editor;
        this.editor.onInstanceChangedCallback = this::onInstanceChanged;
        this.currentPage = 0;

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
        if (glyphInstance == null || isOutOfGlyphsDrawingSpace(currentSlot)) {
            return;
        }

        safeAddGlyphGuiElement(currentSlot, () -> generateGlyphDecoratorGuiElement(glyphInstance));
        
        for (int i = 0; i < glyphInstance.inputPins.size(); i++) {
            int pinIndex = i;

            safeAddGlyphGuiElement(currentSlot, () -> generatePinDecoratorPrePinGuiElement(glyphInstance, pinIndex));
            safeAddGlyphGuiElement(currentSlot, () -> generatePinGuiElement(glyphInstance, pinIndex));
            
            // Recursive call for the connected glyph.
            recursiveCreateSpellGuiElements(glyphInstance.inputPins.get(i).connectedGlyph, currentSlot);

            safeAddGlyphGuiElement(currentSlot, () -> generatePinDecoratorPostPinGuiElement(glyphInstance, pinIndex));
        }

        safeAddGlyphGuiElement(currentSlot, () -> generateGlyphPostPinsDecoratorGuiElement(glyphInstance) );
    }
    
    /** @return true if currentSlot is out of bounds. */
    public boolean isOutOfGlyphsDrawingSpace(AtomicInteger currentSlot) {
        return currentSlot.get() >= pageSize * (currentPage + 1);
    }
    
    /** Adds a glyph related gui element at the correct slot only if possible / needed. */
    public void safeAddGlyphGuiElement(AtomicInteger currentSlot, Supplier<Optional<SimpleGuiElement>> supplier) {
        // Get new slot index.
        int slotIndex = currentSlot.get();
        // If slot index exceeds the last representable index then quit.
        if (isOutOfGlyphsDrawingSpace(currentSlot)) {
            return;
        }

        // Only start generating gui elements if we reached a slot index that is visible.
        if (slotIndex >= pageSize * (currentPage)) {
            // Add new element.
            Optional<SimpleGuiElement> optionalElement = supplier.get();
            if (optionalElement.isPresent()) {
                this.setSlot(slotIndex % pageSize, optionalElement.get());
                
                // If we were able to add the element then increment the current slot.
                currentSlot.incrementAndGet();
            }
        }
    }
    
    public Optional<SimpleGuiElement> generatePinGuiElement(GlyphInstance glyphInstance, int pinIndex) {
        InputPin pinToDisplay = glyphInstance.glyph.getInputPin(glyphInstance, pinIndex).get();
        GlyphInstance connectedGlyphInstance = pinToDisplay.connectedGlyph;
        String connectedGlyphDisplayName = connectedGlyphInstance.glyph.getClass().getSimpleName();
       
        InputPinDefinition pinDefinitionToDisplay = glyphInstance.glyph.getInputPinDefinition(pinIndex).get();
        String pinDisplayName = pinDefinitionToDisplay.pinName;
        
        return Optional.of(new GuiElementBuilder(Items.PAPER)
                .setName(Component.literal("Pin: " + pinDisplayName + " | " + connectedGlyphDisplayName))
                .setCallback(clickType -> {
                    String compatibleGlyphs = editor.printCompatibleGlyphs(glyphInstance, pinIndex);
                    player.sendSystemMessage(Component.literal(compatibleGlyphs));
                })
                .build());
    }
    
    public Optional<SimpleGuiElement> generatePinDecoratorPrePinGuiElement(GlyphInstance glyphInstance, int pinIndex) {
        if (glyphInstance.glyph.getInputPinMode() ==  InputPinMode.ARRAY) {
            return Optional.of(new GuiElementBuilder(Items.MAP)
                    .setName(Component.literal("+ / -"))
                    .setCallback(clickType -> {
                        String outputString = (clickType.isRight ? "Removing pin" : "Adding pin") + " " + pinIndex;
                        player.sendSystemMessage(Component.literal(outputString));
                    })
                    .build());
        }
        
        return Optional.empty();
    }
    
    public Optional<SimpleGuiElement> generatePinDecoratorPostPinGuiElement(GlyphInstance glyphInstance, int pinIndex) {
        return Optional.empty();
    }

    public Optional<SimpleGuiElement> generateGlyphDecoratorGuiElement(GlyphInstance glyphInstance) {
        if (glyphInstance.glyph.getInputPinMode() ==  InputPinMode.ARRAY) {
            return Optional.of(new GuiElementBuilder(Items.LIGHT_BLUE_CONCRETE)
                    .setName(Component.literal("("))
                    .build());
        }

        if (glyphInstance.glyph.getInputPinMode() ==  InputPinMode.VALUE) {
            if (glyphInstance.glyph instanceof RawValueGlyph) {
                if (glyphInstance.outputPin.valueType.hasValueSelector()) {
                    return Optional.of(new GuiElementBuilder(Items.REDSTONE_TORCH)
                            .setName(Component.literal("?"))
                            .setCallback(clickType -> {
                                String outputString = "selecting value for " + glyphInstance.glyph.getClass().getSimpleName();
                                player.sendSystemMessage(Component.literal(outputString));
                            })
                            .build());
                }
            }
        }
        
        return Optional.empty();
    }

    public Optional<SimpleGuiElement> generateGlyphPostPinsDecoratorGuiElement(GlyphInstance glyphInstance) {
        if (glyphInstance.glyph.getInputPinMode() ==  InputPinMode.ARRAY) {
            return Optional.of(new GuiElementBuilder(Items.ORANGE_CONCRETE)
                    .setName(Component.literal(")+"))
                    .setCallback(clickType -> {
                        String outputString = "Adding pin " + glyphInstance.inputPins.size();
                        player.sendSystemMessage(Component.literal(outputString));
                    })
                    .build());
        }
        
        return Optional.empty();
    }
}