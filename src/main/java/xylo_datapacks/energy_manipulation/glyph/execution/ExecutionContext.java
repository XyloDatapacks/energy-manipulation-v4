package xylo_datapacks.energy_manipulation.glyph.execution;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ExecutionContext {
    protected Level level;
    protected final EntityReference<LivingEntity> owner;
    public final ItemStack spellBookStack;
    public final float power;

    protected InteractionHand interactionHand;
    protected EntityReference<Entity> targetEntity;
    protected HitResult hitResult;

    public Vec3 position;
    public float yRot;
    public float xRot;
    
    public final Map<String, @NonNull GlyphValue> executionVariables = new LinkedHashMap<>();
    protected final PersistentVariablesContainer persistentVarContainer = new PersistentVariablesContainer();

    
    public ExecutionContext(Level level, EntityReference<LivingEntity> owner, ItemStack spellBookStack, float power) {
        this.level = level;
        this.owner = owner;
        this.spellBookStack = spellBookStack;
        this.power = power;
    }
    
    public ExecutionContext(Level level, LivingEntity owner, ItemStack spellBookStack, float power) {
        this(level, EntityReference.of(owner), spellBookStack, power);
    }
    
    public void setTransform(Vec3 position, float yRot, float xRot) {
        this.position = position;
        this.yRot = yRot;
        this.xRot = xRot;
    }
    
    /** To be called if this execution context needs to be recycled for a new execution. */
    public void initialize(Vec3 position, float yRot, float xRot) {
        this.setTransform(position, yRot, xRot);
        this.executionVariables.clear();
        this.persistentVarContainer.variables().clear();
    }
    
    public Level getLevel() {
        return level;
    }
    
    public EntityReference<LivingEntity> getOwnerReference() {
        return owner;
    }

    public Optional<LivingEntity> getOwner() {
        return Optional.ofNullable(EntityReference.getLivingEntity(this.owner, this.level));
    }
    
    public Optional<ServerPlayer> getServerPlayer() {
        return this.getOwner().filter(ServerPlayer.class::isInstance).map(ServerPlayer.class::cast);
    }

    public float getXRot() {
        return this.xRot;
    }

    public float getYRot() {
        return this.yRot;
    }

    protected void setTargetEntity(@Nullable final EntityReference<Entity> targetEntity) {
        this.targetEntity = targetEntity;
    }

    public void setTargetEntity(@Nullable final Entity target) {
        this.setTargetEntity(EntityReference.of(target));
    }

    public @Nullable Entity getTargetEntity() {
        return EntityReference.getEntity(this.targetEntity, this.level);
    }
    
    /** If it is an EntityHitResult, it also updates targetEntity. */
    public void setHitResult(@NonNull final HitResult hitResult) {
        this.hitResult = hitResult;
        
        if (hitResult instanceof EntityHitResult entityHitResult) {
            this.setTargetEntity(entityHitResult.getEntity());
        }
    }
    
    public @Nullable HitResult getHitResult() {
        return this.hitResult;
    }
    
    public void setInteractionHand(@NonNull final InteractionHand hand) {
        this.interactionHand = hand;
    }
    
    public @Nullable InteractionHand getInteractionHand() {
        return this.interactionHand;
    }
    
    /*================================================================================================================*/
    // Variables
    
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

    // ~Variables
    /*================================================================================================================*/
}
