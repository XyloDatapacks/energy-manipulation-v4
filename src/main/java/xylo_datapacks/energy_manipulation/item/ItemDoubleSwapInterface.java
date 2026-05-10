package xylo_datapacks.energy_manipulation.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public interface ItemDoubleSwapInterface {
    
    public void onDoubleSwap(@NonNull Level level, @NonNull Player player, @NonNull ItemStack stack);
}
