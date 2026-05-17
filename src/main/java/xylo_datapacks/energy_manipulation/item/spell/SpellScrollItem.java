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

    @Deprecated
    public GlyphInstance getSpellFromCustomData(ItemStack itemStack) {
        Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, SpellContentNbtKey);
        
        // Try to deserialize the spell otherwise creates an empty program glyph.
        return EnergyManipulationItemsUtils.getTag(itemStack, identifier).flatMap(tag -> {
            // If we can find the compound tag, try to deserialize the spell.
            if (tag instanceof CompoundTag compoundTag) {
                return GlyphUtils.deserializeInstance(compoundTag, GlyphsRegistry.EXECUTION_VALUE_TYPE);
            }
            // If there was no compound return empty to let orElse() take care of it.
            return Optional.empty();
        }).orElse(GlyphsRegistry.PROGRAM_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE));
    }

    @Deprecated
    public void setSpellToCustomData(ItemStack itemStack, GlyphInstance glyphInstance) {
        Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, SpellContentNbtKey);
        
        // Serialize the spell and set in compound
        EnergyManipulationItemsUtils.setTag(itemStack, identifier, GlyphUtils.serializeInstance(glyphInstance));
    }

    public GlyphInstance getSpell(ItemStack itemStack) {
        return Optional.ofNullable(itemStack.get(EnergyManipulationComponents.SPELL_CONTAINER))
                .orElse(GlyphsRegistry.PROGRAM_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE));
    }

    public void setSpell(ItemStack itemStack, GlyphInstance glyphInstance) {
        itemStack.set(EnergyManipulationComponents.SPELL_CONTAINER, glyphInstance);
    }
}
