package xylo_datapacks.energy_manipulation.entity.shape;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
    
    public ProjectileShape(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public ProjectileShape(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(EnergyManipulationEntities.PROJECTILE_SHAPE, x, y, z, level, pickupItemStack, firedFromWeapon);
    }

    public ProjectileShape(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(EnergyManipulationEntities.PROJECTILE_SHAPE, owner, level, pickupItemStack, firedFromWeapon);
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

        ExecutionContext executionContext = new ExecutionContext(level(), this.owner, getWeaponItem());
        
        if (hitResult instanceof EntityHitResult entityHitResult) {
            executionContext.setTarget(entityHitResult.getEntity());
        }
        
        if (onImpactEffect != null) {
            executionContext.initialize();
            executionContext.importPersistentVariables(persistentVarContainer);
            GlyphUtils.execute(executionContext, onImpactEffect);
        }
        
        if (onImpactProgram != null) {
            executionContext.initialize();
            executionContext.importPersistentVariables(persistentVarContainer);
            GlyphUtils.execute(executionContext, onImpactProgram);
        }
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        
        output.storeNullable("on_impact_program", GlyphInstance.CODEC, onImpactProgram);
        output.storeNullable("on_impact_effect", GlyphInstance.CODEC, onImpactEffect);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);

        onImpactProgram = input.read("on_impact_program", GlyphInstance.CODEC).orElse(null);
        onImpactEffect = input.read("on_impact_effect", GlyphInstance.CODEC).orElse(null);
    }
}
