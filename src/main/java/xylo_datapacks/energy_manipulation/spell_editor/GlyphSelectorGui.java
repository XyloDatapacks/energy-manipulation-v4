package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;

public class GlyphSelectorGui extends SimpleGui {
    private final SpellEditor editor;
    private final GlyphInstance instance;
    private final int pinIndex;
    
    public GlyphSelectorGui(ServerPlayer player, SpellEditor editor, GlyphInstance instance, int pinIndex) {
        super(MenuType.GENERIC_9x3, player, false);

        this.editor = editor;
        this.instance = instance;
        this.pinIndex = pinIndex;

        this.setTitle(Component.literal("Spell Editor"));
        this.setupToolbar();
        this.displayCompatibleGlyphs();
    }

    private void setupToolbar() {}
    
    private void displayCompatibleGlyphs() {
        
    }
}
