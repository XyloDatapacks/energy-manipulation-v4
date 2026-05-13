package xylo_datapacks.energy_manipulation.glyph.execution;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ExecutionContext {
    public final LivingEntity owner;
    public final Map<String, GlyphValue> variables = new LinkedHashMap<>();

    public ExecutionContext(LivingEntity owner) {
        this.owner = owner;
    }
    
    public Optional<ServerPlayer> getServerPlayer() {
        if (this.owner instanceof ServerPlayer player) {
            return Optional.of(player);
        }
        return Optional.empty();
    }
    
    public void setVariable(String name, GlyphValue value) {
        this.variables.put(name, value);
    }
    
    public Optional<GlyphValue> getVariable(String name) {
        if (!this.variables.containsKey(name)) {
            return Optional.of(this.variables.get(name));
        }
        return Optional.empty();
    }
}
