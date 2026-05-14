package xylo_datapacks.energy_manipulation.spell_editor.modal_menues;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import xylo_datapacks.energy_manipulation.font.EnergyManipulationFonts;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.spell_editor.*;

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

        this.setTitle(getTitleText());
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

    protected MutableComponent getTitleText() {
        return Component.empty()
                .append(Component.literal("\uF102\uE002\uF002\uF202").setStyle(Style.EMPTY.withFont(EnergyManipulationFonts.SPELL_BOOK_GUI).withColor(0xFFFFFF)))
                .append(Component.literal("Spell Book").setStyle(Style.EMPTY.withFont(FontDescription.DEFAULT)));
    }
    
    protected void setupToolbar() {
        this.setSlot(26, new GuiElementBuilder(SpellEditorButtonsRegistry.CANCEL_BUTTON.get())
                .setName(Component.literal("Cancel"))
                .hideTooltip()
                .setCallback(clickType -> {
                    goBackToEditor();
                })
                .build());
    }
    
    public void goBackToEditor() {
        SpellEditorGui newEditorGui = new SpellEditorGui(player, editor, editorPage, false);
        newEditorGui.open();
    }

    protected void displayCompatibleGlyphs() {
        AtomicInteger currentSlot = new AtomicInteger(0);
        GlyphValueType valueType = instance.glyph.getInputPin(instance, pinIndex).get().valueType;
        editor.forEachCompatibleGlyph(instance, pinIndex, glyph -> {
            int slotIndex = currentSlot.getAndIncrement();
            if (slotIndex < 26) {
                this.setSlot(slotIndex, SpellEditorGuiUtils.makeGlyphOptionGuiElement(this, glyph, valueType));
            }
        });
    }
}
