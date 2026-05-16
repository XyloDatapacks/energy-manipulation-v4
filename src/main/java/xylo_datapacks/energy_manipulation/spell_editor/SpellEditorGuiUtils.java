package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
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
import xylo_datapacks.energy_manipulation.glyph.value_type.*;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.GlyphSelectorGui;
import xylo_datapacks.energy_manipulation.spell_editor.modal_menues.MultipleChoiceInputGui;
import xylo_datapacks.energy_manipulation.utils.LoreProcessor;
import xylo_datapacks.energy_manipulation.utils.ServerTranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class SpellEditorGuiUtils {
    static final Style PRIMARY_TOOLTIP_STYLE = Style.EMPTY.withFont(EnergyManipulationFonts.UNIFORM).withColor(ChatFormatting.BLACK).withoutShadow();
    static final Style TITLE_TOOLTIP_STYLE = Style.EMPTY.withFont(EnergyManipulationFonts.UNIFORM).withColor(ChatFormatting.BLACK).withBold(true).withoutShadow();
    static final Style ERROR_TOOLTIP_STYLE = Style.EMPTY.withFont(EnergyManipulationFonts.UNIFORM).withColor(ChatFormatting.DARK_RED).withoutShadow();
    static final Style ADVICE_TOOLTIP_STYLE = Style.EMPTY.withFont(EnergyManipulationFonts.UNIFORM).withColor(ChatFormatting.DARK_GRAY).withoutShadow();
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
        String connectedGlyphDisplayName = connectedGlyphInstance != null ? GlyphsRegistry.getGlyphTranslationKey(connectedGlyphInstance.glyph) : "";

        InputPinDefinition pinDefinitionToDisplay = glyphInstance.glyph.getInputPinDefinition(pinIndex).get();
        String pinDisplayName = GlyphsRegistry.getGlyphTranslationKey(glyphInstance.glyph) + "." + pinDefinitionToDisplay.pinName;
        
        ItemStack buttonStack = connectedGlyphInstance != null ? SpellEditorButtonsRegistry.getGlyphButtonStack(connectedGlyphInstance.glyph, pinToDisplay.valueType) : SpellEditorButtonsRegistry.EMPTY_PIN_BUTTON.get();
        return new GuiElementBuilder(buttonStack)
                .setName(Component.literal(connectedGlyphInstance == null ? "\uE000" : "\uE001").setStyle(ICON_TOOLTIP_STYLE)
                        .append(Component.literal(" ").setStyle(PRIMARY_TOOLTIP_STYLE))
                        .append(Component.translatable(pinDisplayName).setStyle(PRIMARY_TOOLTIP_STYLE)))
                .setLore(List.of(
                        Component.literal(">").setStyle(ICON_TOOLTIP_STYLE)
                                .append(Component.literal(" ").setStyle(PRIMARY_TOOLTIP_STYLE))
                                .append(Component.translatable(connectedGlyphDisplayName).setStyle(PRIMARY_TOOLTIP_STYLE)), 
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
        
        MutableComponent displayValue;
        ItemStack buttonStack;
        boolean bValidValue = true;
        
        if (valueType instanceof EnumValueType<?> enumValueType) {
            // Enums can be displayed as their value name and use the icon specific to the enum value.
            String enumValueId = enumValueType.getValueId(glyphValue.get());
            displayValue = Component.translatable(GlyphsRegistry.getValueTypeTranslationKey(valueType) + "." + enumValueId);
            buttonStack = SpellEditorButtonsRegistry.getEnumValueButtonStack(enumValueType, enumValueId);
        }
        else if (valueType == GlyphsRegistry.VAR_NAME_VALUE_TYPE) {
            // Variables can be displayed as their name and use their type as icon.
            VarNameValueType.VariableDescription varDescription = glyphValue.map(GlyphsRegistry.VAR_NAME_VALUE_TYPE::getVarDescription).orElse(new VarNameValueType.VariableDescription("", null));
            GlyphValueType varValueType = varDescription.valueType();

            displayValue = Component.literal(varDescription.name());
            buttonStack = varValueType != null ? SpellEditorButtonsRegistry.getValueTypeButtonStack(varValueType) : SpellEditorButtonsRegistry.EMPTY_PIN_BUTTON.get();
            bValidValue = editorGui.editor.isInScope(varDescription.name(), varDescription.valueType(), glyphInstance);
        }
        else {
            if (valueType == GlyphsRegistry.CLASS_VALUE_TYPE) {
                // Use the value type name.
                displayValue = ((ClassValueType) valueType).getClassGlyphValue(glyphValue.get()).map(classValueType -> {
                    return Component.translatable(GlyphsRegistry.getValueTypeTranslationKey(classValueType));
                }).orElse(Component.literal(""));
            }
            else {
                // Simple values (numbers, strings, booleans, etc.) can just display the debug string and use the type as icon.
                displayValue = Component.literal(glyphValue.isPresent() ? glyphValue.get().getDebugString() : "Unset Value");
            }
            buttonStack = valueType != null ? SpellEditorButtonsRegistry.getValueTypeButtonStack(valueType) : SpellEditorButtonsRegistry.EMPTY_PIN_BUTTON.get();
        }
        
        GlyphInstance parentInstance = glyphInstance.glyph.getParentGlyphInstance(glyphInstance).orElse(null);
        int parentInputPinIndex = glyphInstance.glyph.getParentInputPinIndex(glyphInstance);
        boolean hasValidParent = parentInstance != null && parentInputPinIndex != -1;
        
        if (hasValidParent) {
            boolean isParentPinHidden = parentInstance.glyph.getInputPinEditorData(parentInputPinIndex)
                    .map(editorData -> editorData.bHiddenInEditor)
                    .orElse(false);
            
            if (isParentPinHidden) {
                InputPinDefinition parentInputPinDefinition = parentInstance.glyph.getInputPinDefinition(parentInputPinIndex).orElse(null);
                String pinDisplayName = GlyphsRegistry.getGlyphTranslationKey(parentInstance.glyph) + "." + parentInputPinDefinition.pinName;
                
                return new GuiElementBuilder(buttonStack)
                        .setName(Component.literal("\uE001").setStyle(ICON_TOOLTIP_STYLE)
                                .append(Component.literal(" ").setStyle(PRIMARY_TOOLTIP_STYLE))
                                .append(Component.translatable(pinDisplayName).setStyle(PRIMARY_TOOLTIP_STYLE)))
                        .setLore(List.of(
                                Component.literal(">").setStyle(ICON_TOOLTIP_STYLE)
                                        .append(Component.literal(" ").setStyle(PRIMARY_TOOLTIP_STYLE))
                                        .append(displayValue.setStyle(bValidValue ? PRIMARY_TOOLTIP_STYLE : ERROR_TOOLTIP_STYLE)),
                                makeClickActionComponent("L", "Change Value")
                        ))
                        .setCallback(clickType -> editorGui.openValueSelector(glyphInstance))
                        .build();
            }
        }

        return new GuiElementBuilder(buttonStack)
                .setName(displayValue.setStyle(bValidValue ? PRIMARY_TOOLTIP_STYLE : ERROR_TOOLTIP_STYLE))
                .setLore(List.of(
                        makeClickActionComponent("L", "Change Value")
                ))
                .setCallback(clickType -> editorGui.openValueSelector(glyphInstance))
                .build();
    }

    public static SimpleGuiElement makeGlyphOptionGuiElement(GlyphSelectorGui selectorGui, Glyph glyph, GlyphValueType valueType) {
        List<Component> lore = new ArrayList<>();
        
        List<Component> glyphDescription = makeDescription(selectorGui, GlyphsRegistry.getGlyphTranslationKey(glyph) + ".description");
        if (!glyphDescription.isEmpty() && !glyphDescription.getFirst().getString().isEmpty()) {
            lore.add(Component.literal("").setStyle(PRIMARY_TOOLTIP_STYLE).append(glyphDescription.removeFirst()));
            glyphDescription.forEach(component -> {
                lore.add(Component.literal("").setStyle(PRIMARY_TOOLTIP_STYLE).append(component));
            });
        }
        
        glyph.getInputPinDefinitions().forEach(pinDefinition -> {
            String pinTranslationKey = GlyphsRegistry.getGlyphTranslationKey(glyph) + "." + pinDefinition.pinName;
            MutableComponent pinName = Component.literal("").setStyle(PRIMARY_TOOLTIP_STYLE)
                    .append(Component.literal("\uE002\uF101").setStyle(ICON_TOOLTIP_STYLE))
                    .append(Component.literal(" ").setStyle(PRIMARY_TOOLTIP_STYLE))
                    .append(Component.translatable(pinTranslationKey).setStyle(TITLE_TOOLTIP_STYLE));
            
            List<Component> pinDescription = makeDescription(selectorGui, pinTranslationKey + ".description");
            if (!pinDescription.isEmpty() && !pinDescription.getFirst().getString().isEmpty()) {
                pinName.append(Component.literal(": ").setStyle(PRIMARY_TOOLTIP_STYLE));
                pinName.append(pinDescription.removeFirst());
                
                lore.add(pinName);
                pinDescription.forEach(component -> {
                    lore.add(Component.literal("").setStyle(PRIMARY_TOOLTIP_STYLE).append(component));
                });
            }
            else {
                lore.add(pinName);   
            }
        });
        lore.addAll(List.of(
                makeClickActionComponent("L", "Select Glyph")
        ));
        
        
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getGlyphButtonStack(glyph, valueType))
                .setName(Component.translatable(GlyphsRegistry.getGlyphTranslationKey(glyph)).setStyle(TITLE_TOOLTIP_STYLE))
                .setLore(lore)
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
                .setName(Component.translatable(GlyphsRegistry.getValueTypeTranslationKey(valueType)).setStyle(PRIMARY_TOOLTIP_STYLE))
                .setLore(List.of(
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
                        makeClickActionComponent("L", "Select Variable")
                ))
                .setCallback(clickType -> {
                    GlyphInstance glyphInstance = multipleChoiceInputGui.getGlyphInstance();
                    ((RawValueGlyph) glyphInstance.glyph).setPayloadValue(glyphInstance, GlyphsRegistry.VAR_NAME_VALUE_TYPE.makeVarNameValue(varName, varValueType));
                    multipleChoiceInputGui.goBackToEditor();
                })
                .build();
    }

    public static <E extends Enum<E>> List<SimpleGuiElement> makeEnumOptionElements(MultipleChoiceInputGui multipleChoiceInputGui, EnumValueType<E> enumValueType) {
        return enumValueType.getConstantsStream()
                .map(enumValue -> SpellEditorGuiUtils.makeEnumOptionElement(multipleChoiceInputGui, enumValueType, enumValue))
                .toList();
    }

    public static <E extends Enum<E>> SimpleGuiElement makeEnumOptionElement(MultipleChoiceInputGui multipleChoiceInputGui, EnumValueType<E> enumValueType, E enumValue) {
        String valueId = enumValueType.getValueId(enumValue);
        
        return new GuiElementBuilder(SpellEditorButtonsRegistry.getEnumValueButtonStack(enumValueType, valueId))
                .setName(Component.translatable(GlyphsRegistry.getValueTypeTranslationKey(enumValueType) + "." + valueId).setStyle(PRIMARY_TOOLTIP_STYLE))
                .setLore(List.of(
                        makeClickActionComponent("L", "Select")
                ))
                .setCallback(clickType -> {
                    GlyphInstance glyphInstance = multipleChoiceInputGui.getGlyphInstance();
                    ((RawValueGlyph) glyphInstance.glyph).setPayloadValue(glyphInstance, enumValueType.makeEnumGlyphValue(enumValue));
                    multipleChoiceInputGui.goBackToEditor();
                })
                .build();
    }
    
    public static Component makeClickActionComponent(String click, String action) {
        return Component.literal("\uF002").setStyle(ICON_TOOLTIP_STYLE)
                .append(Component.literal(click).setStyle(ICON_TOOLTIP_STYLE))
                .append(Component.literal("\uF101").setStyle(ICON_TOOLTIP_STYLE))
                .append(Component.literal(" " + action).setStyle(ADVICE_TOOLTIP_STYLE));
    }
    
    public static List<Component> makeDescription(SimpleGui gui, String translationKey) {
        String playerLocale = gui.getPlayer().clientInformation().language();
        String rawText = ServerTranslator.getTranslation(playerLocale, translationKey);
        return LoreProcessor.processLore(rawText, 30);
    }
}
