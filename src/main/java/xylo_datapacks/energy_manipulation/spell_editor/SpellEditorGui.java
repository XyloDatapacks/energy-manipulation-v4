package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.RawValueGlyph;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class SpellEditorGui extends SimpleGui {
    protected final SimpleContainer inputInventory = new SimpleContainer(5);
    static final int PAGE_SIZE = 9*5;
    protected final SpellEditor editor;
    protected int currentPage;
    protected boolean bIsLastPage = false;

    public SpellEditorGui(ServerPlayer player, SpellEditor editor, int currentPage) {
        super(MenuType.GENERIC_9x6, player, false);

        this.editor = editor;
        this.currentPage = currentPage;

        this.setTitle(Component.literal("Spell Editor"));
        this.setupToolbar();
        this.rebuildSpellGui();
    }
    
    public SpellEditorGui(ServerPlayer player, SpellEditor editor) {
        this(player, editor, 0);
    }
    
    public SpellEditor getSpellEditor() {
        return editor;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }

    protected void setupToolbar() {
        this.setSlot(45, new Slot(inputInventory, 0, 0, 0));
        this.setSlot(46, new Slot(inputInventory, 1, 0, 0));
        this.setSlot(47, new Slot(inputInventory, 2, 0, 0));
        this.setSlot(48, new Slot(inputInventory, 3, 0, 0));
        this.setSlot(49, new Slot(inputInventory, 4, 0, 0));
        
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
                    if (currentPage > 0) {
                        currentPage--;
                        rebuildSpellGui();
                    }
                    // player.sendSystemMessage(Component.literal("Clicked previous page"));
                })
                .build());

        this.setSlot(53, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                .setName(Component.literal("Next Page"))
                .setCallback(clickType -> {
                    if (!bIsLastPage) {
                        currentPage++;
                        rebuildSpellGui();
                    }
                    // player.sendSystemMessage(Component.literal("Clicked next page"));
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
        rebuildSpellGui();
    }

    /*================================================================================================================*/
    // SpellGuiElements
    
    public void rebuildSpellGui() {
        AtomicInteger currentSlot = new AtomicInteger(0);
        recursiveCreateSpellGuiElements(editor.currentGlyphInstance, currentSlot);
        
        // If there is still space left, this is the last page.
        bIsLastPage = !isOutOfGlyphsDrawingSpace(currentSlot);
        
        // Clear remaining slots.
        while (!isOutOfGlyphsDrawingSpace(currentSlot)) {
            this.clearSlot(currentSlot.getAndIncrement() % PAGE_SIZE);
        }
    }

    /** @return true if currentSlot is out of bounds. */
    public boolean isOutOfGlyphsDrawingSpace(AtomicInteger currentSlot) {
        return currentSlot.get() >= PAGE_SIZE * (currentPage + 1);
    }
    
    public void recursiveCreateSpellGuiElements(GlyphInstance glyphInstance, AtomicInteger currentSlot) {
        // Stop this branch if no glyph instance.
        if (glyphInstance == null || isOutOfGlyphsDrawingSpace(currentSlot)) {
            return;
        }

        // Add decorator right after the glyph
        safeAddGlyphGuiElement(currentSlot, () -> generateGlyphDecoratorGuiElement(glyphInstance));
        
        for (int i = 0; i < glyphInstance.inputPins.size(); i++) {
            int pinIndex = i;

            // Add decorator for this pin to show before the pin itself.
            safeAddGlyphGuiElement(currentSlot, () -> generatePinDecoratorPrePinGuiElement(glyphInstance, pinIndex));
            
            // Add element for the pin.
            safeAddGlyphGuiElement(currentSlot, () -> generatePinGuiElement(glyphInstance, pinIndex));
            
            // Recursive call for the connected glyph.
            recursiveCreateSpellGuiElements(glyphInstance.inputPins.get(i).connectedGlyph, currentSlot);

            // Add decorator for this pin to show after the pin itself and all its sub-pins.
            safeAddGlyphGuiElement(currentSlot, () -> generatePinDecoratorPostPinGuiElement(glyphInstance, pinIndex));
        }

        // Add decorator for the glyph after all its pins
        safeAddGlyphGuiElement(currentSlot, () -> generateGlyphDecoratorPostPinsGuiElement(glyphInstance) );
    }
    
    /** Adds a glyph related gui element at the correct slot only if possible / needed. */
    public void safeAddGlyphGuiElement(AtomicInteger currentSlot, Supplier<Optional<SimpleGuiElement>> supplier) {
        // Get new slot index.
        int slotIndex = currentSlot.get();
        // If slot index exceeds the last representable index then quit.
        if (isOutOfGlyphsDrawingSpace(currentSlot)) {
            return;
        }

        // Create new element.
        Optional<SimpleGuiElement> optionalElement = supplier.get();
        if (optionalElement.isPresent()) {
            // Only set the slot if visible in current page.
            if (slotIndex >= PAGE_SIZE * (currentPage)) {
                this.setSlot(slotIndex % PAGE_SIZE, optionalElement.get());
            }

            // If the element exists, increment the slot count.
            currentSlot.incrementAndGet();
        }
    }
    
    public Optional<SimpleGuiElement> generatePinGuiElement(GlyphInstance glyphInstance, int pinIndex) {
        return Optional.of(SpellEditorGuiUtils.makePinGuiElement(this, glyphInstance, pinIndex));
    }
    
    public Optional<SimpleGuiElement> generatePinDecoratorPrePinGuiElement(GlyphInstance glyphInstance, int pinIndex) {
        if (glyphInstance.glyph.getInputPinMode() ==  InputPinMode.ARRAY) {
            return Optional.of(SpellEditorGuiUtils.makeArrayPinDecoratorGuiElement(this, glyphInstance, pinIndex));
        }
        
        return Optional.empty();
    }
    
    public Optional<SimpleGuiElement> generatePinDecoratorPostPinGuiElement(GlyphInstance glyphInstance, int pinIndex) {
        return Optional.empty();
    }

    public Optional<SimpleGuiElement> generateGlyphDecoratorGuiElement(GlyphInstance glyphInstance) {
        if (glyphInstance.glyph.getInputPinMode() ==  InputPinMode.ARRAY) {
            return Optional.of(SpellEditorGuiUtils.makeArrayGlyphOpenerGuiElement(this, glyphInstance));
        }

        if (glyphInstance.glyph.getInputPinMode() ==  InputPinMode.VALUE) {
            if (glyphInstance.glyph instanceof RawValueGlyph) {
                if (glyphInstance.outputPin.valueType.hasValueSelector()) {
                    return Optional.of(SpellEditorGuiUtils.makeRawValueSelectorGuiElement(this, glyphInstance));
                }
            }
        }
        
        return Optional.empty();
    }

    public Optional<SimpleGuiElement> generateGlyphDecoratorPostPinsGuiElement(GlyphInstance glyphInstance) {
        if (glyphInstance.glyph.getInputPinMode() ==  InputPinMode.ARRAY) {
            return Optional.of(SpellEditorGuiUtils.makeArrayGlyphTerminatorGuiElement(this, glyphInstance));
        }
        
        return Optional.empty();
    }

    // ~SpellGuiElements
    /*================================================================================================================*/
}