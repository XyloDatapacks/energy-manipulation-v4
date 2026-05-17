package xylo_datapacks.energy_manipulation.entity.shape;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.entity.EnergyManipulationEntities;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.execution.PersistentVariablesContainer;

public class ProjectileShape extends AbstractArrow implements PolymerEntity {
    public PersistentVariablesContainer persistentVarContainer = new PersistentVariablesContainer();
    public GlyphInstance onImpactProgram;
    public GlyphInstance onImpactEffect;

    protected Vec3 lastVelocity = Vec3.ZERO;
    
    public ProjectileShape(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public ProjectileShape(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(EnergyManipulationEntities.PROJECTILE_SHAPE, x, y, z, level, pickupItemStack, firedFromWeapon);
    }

    public ProjectileShape(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(EnergyManipulationEntities.PROJECTILE_SHAPE, owner, level, pickupItemStack, firedFromWeapon);
    }

    public ProjectileShape(Level level, ExecutionContext executionContext, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        LivingEntity owner = (LivingEntity) executionContext.getOwner();
        this(level, executionContext.position.x, executionContext.position.y, executionContext.position.z, pickupItemStack, firedFromWeapon);
        this.setOwner(owner);
    }

    @Override
    public void tick() {
        if (!this.isInGround()) {
            this.lastVelocity = this.getDeltaMovement();
        }
        
        super.tick();
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ARROW;
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
            Vec3 velocity = this.lastVelocity;
            double horizontalLength = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

            // Adding the negative signs fixes the Minecraft coordinate system offset
            float yRot = (float) (-Math.atan2(velocity.x, velocity.z) * (180.0 / Math.PI));
            float xRot = (float) (-Math.atan2(velocity.y, horizontalLength) * (180.0 / Math.PI));

            lastYRot = Mth.wrapDegrees(yRot);
            lastXRot = Mth.wrapDegrees(xRot);
        }

        ExecutionContext executionContext = new ExecutionContext(level(), this.owner, getWeaponItem(), 1.f);
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
        
        output.store("persistent_var_container", PersistentVariablesContainer.CODEC, persistentVarContainer);
        output.storeNullable("on_impact_program", GlyphInstance.CODEC, onImpactProgram);
        output.storeNullable("on_impact_effect", GlyphInstance.CODEC, onImpactEffect);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);

        persistentVarContainer = input.read("persistent_var_container", PersistentVariablesContainer.CODEC).orElse(new PersistentVariablesContainer());
        onImpactProgram = input.read("on_impact_program", GlyphInstance.CODEC).orElse(null);
        onImpactEffect = input.read("on_impact_effect", GlyphInstance.CODEC).orElse(null);
    }
}
