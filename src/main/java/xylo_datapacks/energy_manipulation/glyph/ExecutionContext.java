package xylo_datapacks.energy_manipulation.glyph;

import net.minecraft.server.level.ServerPlayer;

public class ExecutionContext {
    public final ServerPlayer player;

    public ExecutionContext(ServerPlayer player) {
        this.player = player;
    }
}
