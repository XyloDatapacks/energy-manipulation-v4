package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.font.EnergyManipulationFonts;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperatorGlyphInterface;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.RawValueGlyph;
import xylo_datapacks.energy_manipulation.glyph.value_type.EnumValueType;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.StringConvertibleValueInterface;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationComponents;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;
import xylo_datapacks.energy_manipulation.item.spell.SpellBookItem;
import xylo_datapacks.energy_manipulation.item.spell.SpellScrollItem;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.GlyphSelectorGui;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.MultipleChoiceInputGui;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.StringInputGui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class SpellEditorGui extends SimpleGui {
    protected boolean initializedInputInventory = false;
    protected final SimpleContainer inputInventory = new SimpleContainer(5) {
        @Override
        public void setChanged() {
            super.setChanged();
            
            if (initializedInputInventory) {
                saveItems();
            }
        }
    };
    
    static final int PAGE_SIZE = 9*5;
    protected final SpellEditor editor;
    protected int currentPage;
    protected boolean bIsLastPage = false;

    public SpellEditorGui(ServerPlayer player, SpellEditor editor, int currentPage, boolean bLoadSpellFromItem) {
        super(MenuType.GENERIC_9x6, player, false);

        this.editor = editor;
        this.currentPage = currentPage;

        this.loadItems();
        if (bLoadSpellFromItem) {
            onScrollChanged(getScrollStack());
        }
        this.setTitle(getTitleText());
        this.setupToolbar();
        this.rebuildSpellGui();
    }

    public SpellEditorGui(ServerPlayer player, SpellEditor editor, int currentPage) {
        this(player, editor, currentPage, true);
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

    public ItemStack getScrollStack() {
        return inputInventory.getItem(SpellBookItem.SPELL_SCROLL_INDEX);
    }

    protected MutableComponent getTitleText() {
        return Component.empty()
                .append(Component.literal("\uF101\uE001\uF001\uF201").setStyle(Style.EMPTY.withFont(EnergyManipulationFonts.SPELL_BOOK_GUI).withColor(0xFFFFFF)))
                .append(Component.literal("Spell Book").setStyle(Style.EMPTY.withFont(FontDescription.DEFAULT)));
    }
    
    protected void setupToolbar() {
        
        int toolBarFirstIndex = 45;
        this.setSlot(toolBarFirstIndex + SpellBookItem.SPELL_UTILITY_1_INDEX, new Slot(inputInventory, SpellBookItem.SPELL_UTILITY_1_INDEX, 0, 0));
        this.setSlot(toolBarFirstIndex + SpellBookItem.SPELL_UTILITY_2_INDEX, new Slot(inputInventory, SpellBookItem.SPELL_UTILITY_2_INDEX, 0, 0));
        this.setSlot(toolBarFirstIndex + SpellBookItem.SPELL_UTILITY_3_INDEX, new Slot(inputInventory, SpellBookItem.SPELL_UTILITY_3_INDEX, 0, 0));
        this.setSlot(toolBarFirstIndex + SpellBookItem.SPELL_UTILITY_4_INDEX, new Slot(inputInventory, SpellBookItem.SPELL_UTILITY_4_INDEX, 0, 0));
        
        SpellEditorGuiSlot scrollSlot = makeScrollSlot();
        this.setSlot(toolBarFirstIndex + SpellBookItem.SPELL_SCROLL_INDEX, scrollSlot);
        scrollSlot.onItemStackChangedCallback = this::onScrollChanged;
        
        this.setSlot(50, new GuiElementBuilder(SpellEditorButtonsRegistry.CANCEL_BUTTON.get())
                .setName(Component.literal("Cancel"))
                .hideTooltip()
                .setCallback(this::revertSpellChanges)
                .build());

        this.setSlot(51, new GuiElementBuilder(SpellEditorButtonsRegistry.CONFIRM_BUTTON.get())
                .setName(Component.literal("Confirm"))
                .hideTooltip()
                .setCallback(this::saveSpellChanges)
                .build());
        
        refreshPageButtons();
    }
    
    public void refreshPageButtons() {
        ItemStack prevPageStack = currentPage > 0 ? SpellEditorButtonsRegistry.PREVIOUS_PAGE_BUTTON.get() : SpellEditorButtonsRegistry.PREVIOUS_PAGE_BUTTON_DISABLED.get();
        this.setSlot(52, new GuiElementBuilder(prevPageStack)
                .setName(Component.literal("Previous Page"))
                .hideTooltip()
                .setCallback(this::previousPage)
                .build());

        ItemStack nextPageStack = !bIsLastPage ? SpellEditorButtonsRegistry.NEXT_PAGE_BUTTON.get() : SpellEditorButtonsRegistry.NEXT_PAGE_BUTTON_DISABLED.get();
        this.setSlot(53, new GuiElementBuilder(nextPageStack)
                .setName(Component.literal("Next Page"))
                .hideTooltip()
                .setCallback(this::nextPage)
                .build());
    }
    
    protected SpellEditorGuiSlot makeScrollSlot() {
        return new SpellEditorGuiSlot(inputInventory, SpellBookItem.SPELL_SCROLL_INDEX, 0, 0) {
            @Override
            public boolean mayPlace(@NonNull ItemStack itemStack) {
                return itemStack.is(EnergyManipulationItems.SPELL_SCROLL);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    @Override
    public boolean onAnyClick(int index, ClickType type, ContainerInput action) {
        
        // Prevent actions on selected slot
        int selectedHotbarSlotIndex = getHotbarSlotIndex(this.wrappedMenu.slots.size(), player.getInventory().getSelectedSlot());
        if (index == selectedHotbarSlotIndex) {
            return false;
        }

        // Prevent quick swap to selected swap
        if (action == ContainerInput.SWAP && type.value == player.getInventory().getSelectedSlot() + 1) {
            return false;
        }
        
        return super.onAnyClick(index, type, action);
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

        ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldStack.getItem() instanceof SpellBookItem spellBookItem) {
            spellBookItem.closeBookGui(player, heldStack);
        }
    }
    
    public void onClose() {
    }
    
    public void loadItems() {
        initializedInputInventory = false;
        
        ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldStack.getItem() instanceof SpellBookItem spellBookItem) {
            spellBookItem.getBookContent(heldStack, inputInventory.getItems());
        }
        
        initializedInputInventory = true;
    }
    
    public void saveItems() {
        ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldStack.getItem() instanceof SpellBookItem spellBookItem) {
            spellBookItem.setBookContent(heldStack, inputInventory.getItems());
        }
    }

    /*================================================================================================================*/
    // SpellGuiElements
    
    public boolean isOutOfGlyphsDrawingSpace(AtomicInteger currentSlot) {
        return SpellEditorGuiUtils.isOutOfGlyphsDrawingSpace(PAGE_SIZE, currentPage, currentSlot);
    }
    
    public void safeAddGlyphGuiElement(AtomicInteger currentSlot, Supplier<Optional<SimpleGuiElement>> supplier) {
        SpellEditorGuiUtils.safeAddGlyphGuiElement(this, PAGE_SIZE, currentPage, currentSlot, supplier);
    }
    
    public void rebuildSpellGui() {
        AtomicInteger currentSlot = new AtomicInteger(0);
        recursiveCreateSpellGuiElements(editor.getCurrentGlyphInstance(), currentSlot);
        
        // If there is still space left, this is the last page.
        bIsLastPage = !isOutOfGlyphsDrawingSpace(currentSlot);
        
        // Clear remaining slots.
        while (!isOutOfGlyphsDrawingSpace(currentSlot)) {
            clearSlot(currentSlot.getAndIncrement() % PAGE_SIZE);
        }

        // refresh page buttons because page might have changed.
        refreshPageButtons();
    }
    
    public void recursiveCreateSpellGuiElements(@Nullable GlyphInstance glyphInstance, AtomicInteger currentSlot) {
        // Stop this branch if no glyph instance.
        if (glyphInstance == null || isOutOfGlyphsDrawingSpace(currentSlot)) {
            return;
        }

        // Add decorator right after the glyph
        safeAddGlyphGuiElement(currentSlot, () -> generateGlyphDecoratorGuiElement(glyphInstance));
        
        for (int i = 0; i < glyphInstance.inputPins.size(); i++) {
            int pinIndex = i;
            
            boolean bHidden = glyphInstance.glyph.getInputPinEditorData(pinIndex)
                    .map(editorData -> editorData.bHiddenInEditor)
                    .orElse(false);

            if (!bHidden) {
                // Add decorator for this pin to show before the pin itself.
                safeAddGlyphGuiElement(currentSlot, () -> generatePinDecoratorPrePinGuiElement(glyphInstance, pinIndex));

                // Add element for the pin.
                safeAddGlyphGuiElement(currentSlot, () -> generatePinGuiElement(glyphInstance, pinIndex));
            }
            
            // Recursive call for the connected glyph.
            recursiveCreateSpellGuiElements(glyphInstance.inputPins.get(i).getConnectedGlyph().orElse(null), currentSlot);

            if (!bHidden) {
                // Add decorator for this pin to show after the pin itself and all its sub-pins.
                safeAddGlyphGuiElement(currentSlot, () -> generatePinDecoratorPostPinGuiElement(glyphInstance, pinIndex));
            }
        }

        // Add decorator for the glyph after all its pins
        safeAddGlyphGuiElement(currentSlot, () -> generateGlyphDecoratorPostPinsGuiElement(glyphInstance) );
    }
    
    public Optional<SimpleGuiElement> generatePinGuiElement(GlyphInstance glyphInstance, int pinIndex) {
        return Optional.of(SpellEditorGuiUtils.makePinGuiElement(this, glyphInstance, pinIndex));
    }
    
    public Optional<SimpleGuiElement> generatePinDecoratorPrePinGuiElement(GlyphInstance glyphInstance, int pinIndex) {
        if (glyphInstance.glyph.getInputPinMode() ==  InputPinMode.ARRAY) {
            return Optional.of(SpellEditorGuiUtils.makeArrayPinDecoratorGuiElement(this, glyphInstance, pinIndex));
        }
        
        if (pinIndex > 0 && glyphInstance.glyph instanceof OperatorGlyphInterface) {
            return Optional.of(SpellEditorGuiUtils.makeOperatorPinDecoratorGuiElement(this, glyphInstance, pinIndex));
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
    
    /*================================================================================================================*/
    // GuiButtonsLogic
    
    public void onScrollChanged(ItemStack itemStack) {
        if (itemStack.getItem() instanceof SpellScrollItem spellScrollItem) {
            editor.initialize(spellScrollItem.getSpell(itemStack));
        } else {
            editor.reset();
        }
        
        currentPage = 0;
        rebuildSpellGui();
    }

    public void revertSpellChanges() {
        // Revert unsaved changes.
        editor.restoreGlyphInstance();
        rebuildSpellGui();
    }

    public void saveSpellChanges() {
        ItemStack scrollStack = getScrollStack();
        if (scrollStack.getItem() instanceof SpellScrollItem spellScrollItem) {
            // Save current version of the spell on the item.
            spellScrollItem.setSpell(scrollStack, editor.getCurrentGlyphInstance());
            // Update cached version of the spell to allow reverting to this point.
            editor.saveChanges();
            
            saveItems();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            rebuildSpellGui();
        }
    }

    public void nextPage() {
        if (!bIsLastPage) {
            currentPage++;
            rebuildSpellGui();
        }
    }

    public void openGlyphSelector(GlyphInstance glyphInstance, int pinIndex) {
        onClose();
        
        GlyphSelectorGui gui = new GlyphSelectorGui(getPlayer(), getSpellEditor(), getCurrentPage(), glyphInstance, pinIndex);
        gui.open();
    }

    public void removeArrayPin(GlyphInstance glyphInstance, int pinIndex) {
        glyphInstance.glyph.removePin(glyphInstance, pinIndex);
        rebuildSpellGui();
    }

    public void insertArrayPin(GlyphInstance glyphInstance, int pinIndex) {
        glyphInstance.glyph.insertPin(glyphInstance, pinIndex);
        rebuildSpellGui();
    }

    public void addArrayPin(GlyphInstance glyphInstance) {
        glyphInstance.glyph.addPin(glyphInstance);
        rebuildSpellGui();
    }

    public void openValueSelector(GlyphInstance glyphInstance) {
        onClose();
        
        SimpleGui gui;
        if (glyphInstance.outputPin.valueType instanceof StringConvertibleValueInterface) {
            gui = new StringInputGui(getPlayer(), getSpellEditor(), getCurrentPage(), glyphInstance, ((inputGui, value) -> {
                if (inputGui.getGlyphInstance().outputPin.valueType instanceof StringConvertibleValueInterface stringConvertibleValueInterface) {
                    return stringConvertibleValueInterface.isValidString(value);
                }
                return false;
            }));
        } else {
            gui = new MultipleChoiceInputGui(getPlayer(), getSpellEditor(), getCurrentPage(), glyphInstance, inputGui -> {
                
                // Options for CLASS_VALUE_TYPE outputs.
                if (inputGui.getGlyphInstance().outputPin.valueType == GlyphsRegistry.CLASS_VALUE_TYPE) {
                    return GlyphsRegistry.VALUE_TYPE.stream()
                            .filter(valueType -> {
                                // Filter by rawValueGlyph's parent.
                                Optional<GlyphInstance> parentInstance = inputGui.getGlyphInstance().glyph.getParentGlyphInstance(inputGui.getGlyphInstance());
                                if (parentInstance.isPresent()) {

                                    // If parent is FROM_CONVERSION_GLYPH, only show classes that allow conversion.
                                    if (parentInstance.get().glyph == GlyphsRegistry.FROM_CONVERSION_GLYPH) {
                                        return parentInstance.get().glyph.getOutputPinDefinition().valueTypeCompatibilityPredicate.test(valueType);
                                    }

                                    // If parent is VAR_DEFINITION_GLYPH, only show classes that can be variable.
                                    if (parentInstance.get().glyph == GlyphsRegistry.VAR_DEFINITION_GLYPH) {
                                        return valueType.canBeVariable();
                                    }

                                    // Other filters by parent are to be put here.
                                }
                                return true;
                            })
                            .map(valueType -> {
                                return SpellEditorGuiUtils.makeValueTypeOptionElement(inputGui, valueType);
                            })
                            .toList();
                }

                // Options for VAR_NAME_VALUE_TYPE outputs.
                if (inputGui.getGlyphInstance().outputPin.valueType == GlyphsRegistry.VAR_NAME_VALUE_TYPE) {
                    return editor.getInScopeVariables(inputGui.getGlyphInstance()).entrySet().stream()
                            .filter(entry -> {
                                // Filter by rawValueGlyph's parent.
                                Optional<GlyphInstance> parentInstance = inputGui.getGlyphInstance().glyph.getParentGlyphInstance(inputGui.getGlyphInstance());
                                if (parentInstance.isPresent()) {

                                    // If parent is VAR_GETTER_GLYPH, only show variables compatible with the output type.
                                    if (parentInstance.get().glyph == GlyphsRegistry.VAR_GETTER_GLYPH) {
                                        return parentInstance.get().outputPin.valueType == entry.getValue().valueType();
                                    }
                                    
                                    // Other filters by parent are to be put here.
                                }

                                // TODO: filter so that only variables in scope are shown.
                                return true;
                            })
                            .map(entry -> {
                                return SpellEditorGuiUtils.makeVariableOptionElement(inputGui, entry.getValue().name(), entry.getValue().valueType());
                            })
                            .toList();
                }

                // Options for VAR_NAME_VALUE_TYPE outputs.
                if (inputGui.getGlyphInstance().outputPin.valueType instanceof EnumValueType<?> enumValueType) {
                    return SpellEditorGuiUtils.makeEnumOptionElements(inputGui, enumValueType);
                }
                
                return new ArrayList<>();
            });
        }
        gui.open();
    }
    
    // ~GuiButtonsLogic
    /*================================================================================================================*/
}