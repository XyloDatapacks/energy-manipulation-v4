package xylo_datapacks.energy_manipulation.utils;


import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xylo_datapacks.energy_manipulation.item.ItemDoubleSwapInterface;

import java.util.UUID;

public class DoubleSwapTracker {
    private static final Object2LongOpenHashMap<UUID> lastSwapTicks = new Object2LongOpenHashMap<>();
    private static final int ONE_SECOND_TICKS = 20;

    public static void onAfterSwap(ServerPlayer player) {
        UUID uuid = player.getUUID();
        long currentTicks = player.level().getGameTime();

        if (lastSwapTicks.containsKey(uuid)) {
            // We already swapped once, so check if this second swap is within the time limit.
            long delta = currentTicks - lastSwapTicks.getLong(uuid);

            if (delta > 0 && delta <= ONE_SECOND_TICKS) {
                // Since we swapped back in time, call the function.
                ItemStack itemInMainHand = player.getMainHandItem();
                if (itemInMainHand.getItem() instanceof ItemDoubleSwapInterface doubleSwapItem) {
                    doubleSwapItem.onDoubleSwap(player.level(), player, itemInMainHand);
                }
                lastSwapTicks.removeLong(uuid);
                
                // Return only if we were inside the time window, so we can let this swap count as the first one
                // if the previous double swap already expired.
                return;
            }
        }

        // Record the first swap if holding an item that tracks double swap.
        ItemStack itemInOffhand = player.getOffhandItem();
        if (itemInOffhand.getItem() instanceof ItemDoubleSwapInterface) {
            lastSwapTicks.put(uuid, currentTicks);
        }
    }
}