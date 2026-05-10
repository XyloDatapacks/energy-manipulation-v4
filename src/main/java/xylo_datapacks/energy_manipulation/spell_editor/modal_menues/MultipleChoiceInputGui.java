package xylo_datapacks.energy_manipulation.spell_editor.modal_menues;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditor;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorButtonsRegistry;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorGui;

public class MultipleChoiceInputGui extends SimpleGui {
    protected final SpellEditor editor;
    protected final int editorPage;
    protected final GlyphInstance instance;
    protected final GlyphValue initialValue;

    public MultipleChoiceInputGui(ServerPlayer player, SpellEditor editor, int editorPage, GlyphInstance instance) {
        super(MenuType.GENERIC_9x3, player, false);

        this.editor = editor;
        this.editorPage = editorPage;
        this.instance = instance;
        this.initialValue = GlyphsRegistry.RAW_VALUE_GLYPH.getPayloadValue(instance).get();

        this.setTitle(Component.literal("Spell Editor"));
        this.setupToolbar();
        this.displaySelector();
    }

    public SpellEditor getSpellEditor() {
        return editor;
    }

    public GlyphInstance getGlyphInstance() {
        return instance;
    }

    protected void setupToolbar() {
        this.setSlot(26, new GuiElementBuilder(SpellEditorButtonsRegistry.CANCEL_BUTTON.get())
                .setName(Component.literal("Cancel"))
                .setCallback(clickType -> {
                    GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(instance, initialValue);
                    goBackToEditor();
                })
                .build());
    }

    protected void goBackToEditor() {
        SpellEditorGui newEditorGui = new SpellEditorGui(player, editor, editorPage, false);
        newEditorGui.open();
    }

    protected void displaySelector() {
        
    }
}
