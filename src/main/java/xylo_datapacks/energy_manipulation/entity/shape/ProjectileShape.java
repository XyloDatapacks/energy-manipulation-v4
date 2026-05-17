package xylo_datapacks.energy_manipulation.entity.shape;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.entity.EnergyManipulationEntities;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.execution.PersistentVariablesContainer;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;
import xylo_datapacks.energy_manipulation.utils.DataComponentsUtils;

import java.util.Objects;
import java.util.function.Consumer;

public class ProjectileShape extends AbstractArrow implements PolymerEntity {
    private final ElementHolder visualHolder = new ElementHolder();
    ShapeDisplayElement modelDisplay = new ShapeDisplayElement("projectile_shape");
    
    public PersistentVariablesContainer persistentVarContainer = new PersistentVariablesContainer();
    public GlyphInstance onImpactProgram;
    public GlyphInstance onImpactEffect;

    protected Vec3 lastVelocity = Vec3.ZERO;
    
    public ProjectileShape(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        createVisuals();
    }

    protected ProjectileShape(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(EnergyManipulationEntities.PROJECTILE_SHAPE, x, y, z, level, pickupItemStack, firedFromWeapon);
    }

    public ProjectileShape(Level level, ExecutionContext executionContext, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        EntityReference<LivingEntity> contextOwner = executionContext.getOwnerReference();
        this(level, executionContext.position.x, executionContext.position.y, executionContext.position.z, pickupItemStack, firedFromWeapon);
        this.owner = EntityReference.of(contextOwner.getUUID());

        createVisuals();
    }

    @Override
    public void tick() {
        if (!this.isInGround()) {
            this.lastVelocity = this.getDeltaMovement();
        }
        
        super.tick();

        // Update the model display rotation.
        modelDisplay.setRotation(getTrueXRot(), getTrueYRot());
        // Thick visual entities.
        visualHolder.tick();
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.MARKER;
    }
    
    @Override
    protected @NonNull ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }
    
    @Override
    protected void onHit(@NonNull HitResult hitResult) {
        super.onHit(hitResult);

        float lastYRot = 0.f;
        float lastXRot = 0.f;
        
        if (this.lastVelocity.lengthSqr() > 0.001) {
            lastYRot = getTrueYRot();
            lastXRot = getTrueXRot();
        }

        EntityReference<LivingEntity> caster = EntityReference.of(Objects.requireNonNull(this.owner).getUUID());
        ExecutionContext executionContext = new ExecutionContext(level(), caster, getWeaponItem(), 1.f);
        executionContext.setHitResult(hitResult);
        
        if (onImpactEffect != null) {
            executionContext.initialize(hitResult.getLocation(), lastYRot, lastXRot);
            executionContext.importPersistentVariables(persistentVarContainer);
            GlyphUtils.execute(executionContext, onImpactEffect);
        }
        
        if (onImpactProgram != null) {
            executionContext.initialize(hitResult.getLocation(), lastYRot, lastXRot);
            executionContext.importPersistentVariables(persistentVarContainer);
            GlyphUtils.execute(executionContext, onImpactProgram);
        }
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        
        output.store("last_velocity", Vec3.CODEC, lastVelocity);
        output.store("persistent_var_container", PersistentVariablesContainer.CODEC, persistentVarContainer);
        output.storeNullable("on_impact_program", GlyphInstance.CODEC, onImpactProgram);
        output.storeNullable("on_impact_effect", GlyphInstance.CODEC, onImpactEffect);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);

        input.read("last_velocity", Vec3.CODEC).ifPresent(lastVelocity -> this.lastVelocity = lastVelocity);
        persistentVarContainer = input.read("persistent_var_container", PersistentVariablesContainer.CODEC).orElse(new PersistentVariablesContainer());
        onImpactProgram = input.read("on_impact_program", GlyphInstance.CODEC).orElse(null);
        onImpactEffect = input.read("on_impact_effect", GlyphInstance.CODEC).orElse(null);
    }
    
    public float getTrueYRot() {
        // Adding the negative signs fixes the Minecraft coordinate system offset
        float yRot = (float) (-Math.atan2(this.lastVelocity.x, this.lastVelocity.z) * (180.0 / Math.PI));
        return Mth.wrapDegrees(yRot);
    }
    
    public float getTrueXRot() {
        double horizontalLength = Math.sqrt(this.lastVelocity.x * this.lastVelocity.x + this.lastVelocity.z * this.lastVelocity.z);
        
        // Adding the negative signs fixes the Minecraft coordinate system offset
        float xRot = (float) (-Math.atan2(this.lastVelocity.y, horizontalLength) * (180.0 / Math.PI));
        return Mth.wrapDegrees(xRot);
    }
    
    public void createVisuals() {
        EntityAttachment.of(this.visualHolder, this);
        
        modelDisplay.setRotation(getTrueXRot(), getTrueYRot());
        this.visualHolder.addElement(modelDisplay);
    }
}
