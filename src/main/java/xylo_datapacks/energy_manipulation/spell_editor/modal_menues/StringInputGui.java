package xylo_datapacks.energy_manipulation.spell_editor.modal_menues;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.glyph.value_type.StringValueType;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.StringConvertibleValueInterface;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditor;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorGui;

public class StringInputGui extends AnvilInputGui {
    protected final SpellEditor editor;
    protected final int editorPage;
    protected final GlyphInstance instance;

    public StringInputGui(ServerPlayer player, SpellEditor editor, int editorPage, GlyphInstance instance) {
        super(player, false);

        this.editor = editor;
        this.editorPage = editorPage;
        this.instance = instance;

        this.setTitle(Component.literal("Spell Editor"));
        this.setDefaultInputValue(getValueAsString());
        
        this.setupToolbar();
    }

    public SpellEditor getSpellEditor() {
        return editor;
    }

    public GlyphInstance getGlyphInstance() {
        return instance;
    }

    protected void setupToolbar() {
        this.setSlot(1, new GuiElementBuilder(Items.RED_WOOL)
                .setName(Component.literal("Cancel"))
                .setCallback(clickType -> {
                    setValueFromString(defaultText);
                    goBackToEditor();
                })
                .build());
        
        this.setSlot(2, new GuiElementBuilder(Items.GREEN_WOOL)
                .setName(Component.literal("Confirm"))
                .setCallback(clickType -> {
                    if (isValidInput(getInput())) {
                        setValueFromString(getInput());
                        goBackToEditor();
                    }
                })
                .build());
    }

    protected void goBackToEditor() {
        SpellEditorGui newEditorGui = new SpellEditorGui(player, editor, editorPage);
        newEditorGui.open();
    }

    public String getValueAsString() {
        if (instance.outputPin.valueType instanceof StringConvertibleValueInterface stringConvertibleValueType) {
            return stringConvertibleValueType.ValueToString(GlyphsRegistry.RAW_VALUE_GLYPH.getPayloadValue(instance).get());
        }
        return "";
    }

    public void setValueFromString(String input) {
        if (instance.outputPin.valueType instanceof StringConvertibleValueInterface stringConvertibleValueType) {
            GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(instance, stringConvertibleValueType.ValueFromString(input));
        }
    }
    
    public boolean isValidInput(String input) {
        if (instance.outputPin.valueType == GlyphsRegistry.INT_VALUE_TYPE) {
            return input.matches("-?\\d+");
        }

        if (instance.outputPin.valueType == GlyphsRegistry.STRING_VALUE_TYPE) {
            return true;
        }
        
        return false;
    }
}
