package xylo_datapacks.energy_manipulation.spell_editor.modal_menues;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditor;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorGui;

import java.util.concurrent.atomic.AtomicInteger;

public class GlyphSelectorGui extends SimpleGui {
    protected final SpellEditor editor;
    protected final int editorPage;
    protected final GlyphInstance instance;
    protected final int pinIndex;
    
    public GlyphSelectorGui(ServerPlayer player, SpellEditor editor, int editorPage, GlyphInstance instance, int pinIndex) {
        super(MenuType.GENERIC_9x3, player, false);

        this.editor = editor;
        this.editorPage = editorPage;
        this.instance = instance;
        this.pinIndex = pinIndex;

        this.setTitle(Component.literal("Spell Editor"));
        this.setupToolbar();
        this.displayCompatibleGlyphs();
    }

    public SpellEditor getSpellEditor() {
        return editor;
    }

    public GlyphInstance getGlyphInstance() {
        return instance;
    }
    
    public int getPinIndex() {
        return pinIndex;
    }

    protected void setupToolbar() {
        this.setSlot(26, new GuiElementBuilder(Items.RED_WOOL)
                .setName(Component.literal("Cancel"))
                .setCallback(clickType -> {
                    goBackToEditor();
                })
                .build());
    }
    
    protected void goBackToEditor() {
        SpellEditorGui newEditorGui = new SpellEditorGui(player, editor, editorPage);
        newEditorGui.open();
    }

    protected void displayCompatibleGlyphs() {
        AtomicInteger currentSlot = new AtomicInteger(0);
        editor.forEachCompatibleGlyph(instance, pinIndex, glyph -> {
            int slotIndex = currentSlot.getAndIncrement();
            if (slotIndex < 26) {
                this.setSlot(slotIndex, GlyphSelectorGuiUtils.makeGlyphOptionGuiElement(this, glyph));
            }
        });
    }
}
