package xylo_datapacks.energy_manipulation.item.spell;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationComponents;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItemsUtils;
import xylo_datapacks.energy_manipulation.item.ItemDoubleSwapInterface;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditor;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorGui;
import xylo_datapacks.energy_manipulation.utils.DataComponentsUtils;

import javax.management.Attribute;
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
        return Items.BOOK;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        return super.use(level, player, hand);
    }

    @Override
    public boolean releaseUsing(@NonNull ItemStack itemStack, @NonNull Level level, @NonNull LivingEntity entity, int remainingTime) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        if (entity instanceof ServerPlayer serverPlayer) {
            int timeHeld = this.getUseDuration(itemStack, entity) - remainingTime;
            if (timeHeld < getMinChargeTicks()) {
                return false;
            }

            // Try cast spell.
            if (getSpell(itemStack).isPresent()) {
                // Create execution context.
                ExecutionContext executionContext = new ExecutionContext(level, serverPlayer, itemStack, getPowerFromChargeTicks(timeHeld));
                
                // Set hit result
                double reachDistance = serverPlayer.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
                executionContext.setHitResult(ProjectileUtil.getHitResultOnViewVector(
                        entity,
                        target -> !target.isSpectator() && target.canBeHitByProjectile(),
                        reachDistance
                ));
                // Set interaction hand
                executionContext.setInteractionHand(player.getUsedItemHand());
                
                // Execute spell.
                GlyphUtils.execute(executionContext, getSpell(itemStack).get());
            }
        }

        return false;
    }

    @Override
    public void onDoubleSwap(@NonNull Level level, @NonNull Player player, @NonNull ItemStack stack) {
        openBookGui(player, stack);
    }

    public int getMinChargeTicks() {
        return 4; // 0.2 seconds
    }

    public int getFullChargeTicks() {
        return 20; // 1 second
    }
    
    protected float getPowerFromChargeTicks(int chargeTicks) {
        return (float) chargeTicks / (float) getFullChargeTicks();
    }
    
    public void openBookGui(@NonNull Player player, @NonNull ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            DataComponentsUtils.setCustomModelDataString(stack, "open_book");
            stack.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "spell_book_open"));
           
            // Force send inventory data to prevent visual artifacts.
            serverPlayer.containerMenu.sendAllDataToRemote();
            
            // Open gui.
            SpellEditor spellEditor = new SpellEditor();
            SpellEditorGui gui = new SpellEditorGui(serverPlayer, spellEditor);
            gui.open();
        }
    }
    
    public void closeBookGui(@NonNull Player player, @NonNull ItemStack stack) {
        DataComponentsUtils.setCustomModelDataString(stack, "closed_book");
        stack.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "spell_book"));
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
