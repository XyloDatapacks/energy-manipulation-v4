package xylo_datapacks.energy_manipulation.item.spell;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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
import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationComponents;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItemsUtils;
import xylo_datapacks.energy_manipulation.item.ItemDoubleSwapInterface;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditor;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorGui;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class SpellBookItem extends Item implements PolymerItem, ItemDoubleSwapInterface {
    public static String BookContentNbtKey = "book_content";
    public static int SPELL_SCROLL_INDEX = 4;
    public static int SPELL_UTILITY_1_INDEX = 0;
    public static int SPELL_UTILITY_2_INDEX = 1;
    public static int SPELL_UTILITY_3_INDEX = 2;
    public static int SPELL_UTILITY_4_INDEX = 3;
    
    public SpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.WRITABLE_BOOK;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack itemStack = player.getItemInHand(hand);
            
            // Try cast spell.
            if (getSpell(itemStack).isPresent()) {
                GlyphUtils.execute(new ExecutionContext(serverPlayer), getSpell(itemStack).get());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onDoubleSwap(@NonNull Level level, @NonNull Player player, @NonNull ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            // Open gui
            SpellEditor spellEditor = new SpellEditor();
            SpellEditorGui gui = new SpellEditorGui(serverPlayer, spellEditor);
            gui.open();
        }
    }
    
    @Deprecated
    public void getBookContentFromCustomData(ItemStack itemStack, NonNullList<ItemStack> destination) {
        Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, BookContentNbtKey);
        Optional<Tag> tag = EnergyManipulationItemsUtils.getTag(itemStack, identifier);
        
        // If no item stored then empty destination
        if (tag.isEmpty()) {
            destination.clear();
            return;
        }
        
        // Fill destination
        ItemContainerContents.CODEC.parse(NbtOps.INSTANCE, tag.get())
                .resultOrPartial(err -> System.err.println("Failed to parse container: " + err))
                .ifPresent(contents -> {
                    contents.copyInto(destination);
                });
    }
    
    @Deprecated
    public void setBookContentToCustomData(ItemStack itemStack, List<ItemStack> bookContent) {
        ItemContainerContents contents = ItemContainerContents.fromItems(bookContent);
        
        ItemContainerContents.CODEC.encodeStart(NbtOps.INSTANCE, contents)
                .resultOrPartial(err -> System.err.println("Failed to encode container: " + err))
                .ifPresent(tag -> {
                    Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, BookContentNbtKey);
                    EnergyManipulationItemsUtils.setTag(itemStack, identifier, tag);
                });
    }
    
    public void getBookContent(ItemStack itemStack, NonNullList<ItemStack> destination) {
        Optional.ofNullable(itemStack.get(EnergyManipulationComponents.SPELL_BOOK_STORAGE)).ifPresent(itemContainer -> {
            itemContainer.copyInto(destination);
        });
    }

    public void setBookContent(ItemStack itemStack, List<ItemStack> bookContent) {
        itemStack.set(EnergyManipulationComponents.SPELL_BOOK_STORAGE, ItemContainerContents.fromItems(bookContent));
    }
    
    public Optional<GlyphInstance> getSpell(ItemStack itemStack) {
        NonNullList<ItemStack> bookContent = NonNullList.withSize(5, ItemStack.EMPTY);
        getBookContent(itemStack, bookContent);
        
        ItemStack scrollStack = bookContent.get(SPELL_SCROLL_INDEX);
        if (!scrollStack.isEmpty() && scrollStack.getItem() instanceof SpellScrollItem spellScrollItem) {
            return Optional.of(spellScrollItem.getSpell(scrollStack));
        }
        return Optional.empty();
    }
}
