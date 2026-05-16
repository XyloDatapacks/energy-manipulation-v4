package xylo_datapacks.energy_manipulation.glyph.execution;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ExecutionContext {
    protected Level level;
    protected final EntityReference<Entity> owner;
    public final ItemStack spellBookStack;

    protected EntityReference<Entity> target;
    
    public final Map<String, GlyphValue> variables = new LinkedHashMap<>();

    
    public ExecutionContext(Level level, EntityReference<Entity> owner, ItemStack spellBookStack) {
        this.level = level;
        this.owner = owner;
        this.spellBookStack = spellBookStack;
    }
    
    public ExecutionContext(Level level, Entity owner, ItemStack spellBookStack) {
        this(level, EntityReference.of(owner), spellBookStack);
    }
    
    public @Nullable Entity getOwner() {
        return EntityReference.getEntity(this.owner, this.level);
    }
    
    public Optional<ServerPlayer> getServerPlayer() {
        if (this.getOwner() instanceof ServerPlayer player) {
            return Optional.of(player);
        }
        return Optional.empty();
    }

    protected void setTarget(@Nullable final EntityReference<Entity> target) {
        this.target = target;
    }

    public void setTarget(@Nullable final Entity target) {
        this.setTarget(EntityReference.of(target));
    }

    public @Nullable Entity getTarget() {
        return EntityReference.getEntity(this.target, this.level);
    }
    
    
    
    public void setVariable(String name, GlyphValue value) {
        this.variables.put(name, value);
    }
    
    public Optional<GlyphValue> getVariable(String name) {
        if (this.variables.containsKey(name)) {
            return Optional.of(this.variables.get(name));
        }
        return Optional.empty();
    }
}
