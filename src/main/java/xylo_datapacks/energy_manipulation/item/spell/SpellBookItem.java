package xylo_datapacks.energy_manipulation.item.spell;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItemsUtils;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditor;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorGui;

import java.util.List;
import java.util.Optional;

public class SpellBookItem extends Item implements PolymerItem {
    public static String BookContentNbtKey = "book_content";
    
    public SpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.WRITABLE_BOOK;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof ServerPlayer serverPlayer) {
            // Open gui
            SpellEditor spellEditor = new SpellEditor();
            SpellEditorGui gui = new SpellEditorGui(serverPlayer, spellEditor);
            gui.open();
            
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
        
        // return super.use(level, player, hand);
    }
    
    public void getBookContent(ItemStack itemStack, NonNullList<ItemStack> destination) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        
        Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, BookContentNbtKey);
        Optional<Tag> tag = EnergyManipulationItemsUtils.getTag(itemStack, identifier);
        
        // If no item stored then empty destination
        if (tag.isEmpty()) {
            destination.clear();
            return;
        }
        
        // Fill destination
        ItemContainerContents.CODEC.parse(NbtOps.INSTANCE, tag.get())
                .resultOrPartial(err -> System.err.println("Failed to decode container: " + err))
                .ifPresent(contents -> {
                    contents.copyInto(destination);
                });
    }
    
    public void setBookContent(ItemStack itemStack, List<ItemStack> bookContent) {
        ItemContainerContents contents = ItemContainerContents.fromItems(bookContent);
        
        ItemContainerContents.CODEC.encodeStart(NbtOps.INSTANCE, contents)
                .resultOrPartial(err -> System.err.println("Failed to encode container: " + err))
                .ifPresent(tag -> {
                    Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, BookContentNbtKey);
                    EnergyManipulationItemsUtils.setTag(itemStack, identifier, tag);
                });
    }
}
