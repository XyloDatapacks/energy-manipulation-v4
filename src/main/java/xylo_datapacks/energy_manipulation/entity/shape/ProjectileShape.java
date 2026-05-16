package xylo_datapacks.energy_manipulation.entity.shape;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.entity.EnergyManipulationEntities;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;

public class ProjectileShape extends AbstractArrow implements PolymerEntity {
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
            GlyphUtils.execute(executionContext, onImpactEffect);
        }
        
        if (onImpactProgram != null) {
            GlyphUtils.execute(executionContext, onImpactProgram);
        }
    }
}
