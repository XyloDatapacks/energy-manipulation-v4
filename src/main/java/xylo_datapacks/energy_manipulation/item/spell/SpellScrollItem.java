package xylo_datapacks.energy_manipulation.item.spell;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationComponents;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItemsUtils;
import xylo_datapacks.energy_manipulation.spell_editor.SpellPresetRegistry;

import java.util.Optional;

public class SpellScrollItem extends Item implements PolymerItem {
    public static String SpellContentNbtKey = "spell_content";

    public SpellScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PAPER;
    }

    /** @return A copy of the GlyphInstance contained in the scroll. */
    public GlyphInstance getSpell(ItemStack itemStack) {
        return Optional.ofNullable(itemStack.get(EnergyManipulationComponents.SPELL_CONTAINER))
                .map(GlyphUtils::copyGlyphInstance)
                .orElse(GlyphsRegistry.PROGRAM_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE));
    }

    /** Saves a copy of the given GlyphInstance into the scroll. */
    public void setSpell(ItemStack itemStack, @NonNull GlyphInstance glyphInstance) {
        itemStack.set(EnergyManipulationComponents.SPELL_CONTAINER, GlyphUtils.copyGlyphInstance(glyphInstance));
    }
}
