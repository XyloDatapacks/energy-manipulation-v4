package xylo_datapacks.energy_manipulation.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xylo_datapacks.energy_manipulation.utils.DoubleSwapTracker;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    
    @Inject(method = "handlePlayerAction", at = @At("RETURN"))
    private void handlePlayerActionTail(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl listenerInstance = (ServerGamePacketListenerImpl) (Object) this;

        if (listenerInstance.hasClientLoaded()) {
            if (packet.getAction() == ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
                DoubleSwapTracker.onAfterSwap(listenerInstance.player);
            }
        }
    }
}
