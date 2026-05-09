package xylo_datapacks.energy_manipulation.item.spell;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.spell_editor.SpellPresetRegistry;

public class SpellScrollItem extends Item implements PolymerItem {

    public SpellScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PAPER;
    }

    public GlyphInstance getSpell(ItemStack itemStack) {
        return SpellPresetRegistry.makeProgramTest(); // TODO: implement
    }

    public void setSpell(GlyphInstance currentGlyphInstance) {
        // TODO: implement
    }
}
