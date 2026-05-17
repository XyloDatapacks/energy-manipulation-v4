package xylo_datapacks.energy_manipulation.glyph.execution;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ExecutionContext {
    protected Level level;
    protected final EntityReference<Entity> owner;
    public final ItemStack spellBookStack;

    protected EntityReference<Entity> target;
    
    public final Map<String, @NonNull GlyphValue> executionVariables = new LinkedHashMap<>();
    protected final PersistentVariablesContainer persistentVarContainer = new PersistentVariablesContainer();

    
    public ExecutionContext(Level level, EntityReference<Entity> owner, ItemStack spellBookStack) {
        this.level = level;
        this.owner = owner;
        this.spellBookStack = spellBookStack;
    }
    
    public ExecutionContext(Level level, Entity owner, ItemStack spellBookStack) {
        this(level, EntityReference.of(owner), spellBookStack);
    }
    
    /** To be called if this execution context needs to be recycled for a new execution. */
    public void initialize() {
        executionVariables.clear();
        persistentVarContainer.variables().clear();
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
    
    
    
    public void setVariable(String name, @NonNull GlyphValue value) {
        this.executionVariables.put(name, value);
    }
    
    public Optional<GlyphValue> getVariable(String name) {
        if (this.executionVariables.containsKey(name)) {
            return Optional.of(this.executionVariables.get(name));
        }
        return Optional.empty();
    }

    /** The correlated execution variable must already exist. */
    public void registerPersistentVariable(String name) {
        this.persistentVarContainer.variables().put(name, getVariable(name)
                .orElseThrow(() -> new RuntimeException("Cannot register a persistent variable if no execution variable exists with that name! (" + name + ")")));
    }
    
    public Optional<GlyphValue> getCachedPersistentVariable(String name) {
        if (this.persistentVarContainer.variables().containsKey(name)) {
            return Optional.ofNullable(this.persistentVarContainer.variables().get(name));
        }
        return Optional.empty();
    }

    public void copyPersistentVariables(PersistentVariablesContainer destination) {
        cachePersistentVariables();
        destination.copyFrom(persistentVarContainer);
    }
    
    public void importPersistentVariables(PersistentVariablesContainer source) {
        persistentVarContainer.copyFrom(source);
        loadCachedPersistentVariables();
    }

    protected void cachePersistentVariables() {
        persistentVarContainer.variables().keySet().forEach(name -> {
            if (executionVariables.containsKey(name)) {
                persistentVarContainer.variables().put(name, executionVariables.get(name).copy());
            }
        });
    }

    protected void loadCachedPersistentVariables() {
        persistentVarContainer.variables().keySet().forEach(name -> {
            GlyphValue value = persistentVarContainer.variables().get(name);
            if (value != null) {
                executionVariables.put(name, value.copy());
            }
            else {
                // Should not happen, since variables are NotNull
                EnergyManipulation.LOGGER.error("loadCachedPersistentVariables >> Persistent variable {} is null", name);
            }
        });
    }
}
