package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;

public class GlyphSelectorGuiUtils {

    public static SimpleGuiElement makeGlyphOptionGuiElement(GlyphSelectorGui selectorGui, Glyph glyph) {
        return new GuiElementBuilder(Items.PAPER)
                .setName(Component.literal(glyph.getClass().getSimpleName()))
                .setCallback(clickType -> {
                    GlyphUtils.connectNewGlyph(selectorGui.getGlyphInstance(), selectorGui.getPinIndex(), glyph);
                    selectorGui.goBackToEditor();
                })
                .build();
    }
    
}
