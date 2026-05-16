package xylo_datapacks.energy_manipulation.glyph.specialized.shape;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.entity.shape.ProjectileShape;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.execution.PersistentVariablesContainer;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.effect.EffectGlyphInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.List;
import java.util.Optional;

public class ProjectileShapeGlyph extends Glyph implements ShapeGlyphInterface {
    static public String MOVEMENT_TYPE_PIN = "movement_type";
    static public String EFFECT_PIN = "effect";
    static public String ON_IMPACT_PIN = "on_impact";

    public ProjectileShapeGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        this.RegisterPinDefinition(MOVEMENT_TYPE_PIN, glyph -> true);
        this.RegisterPinDefinition(EFFECT_PIN, EffectGlyphInterface.class::isInstance);
        this.RegisterPinDefinition(ON_IMPACT_PIN, glyph -> {
            return glyph == GlyphsRegistry.PROGRAM_GLYPH;
        });

        this.outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType == GlyphsRegistry.EXECUTION_VALUE_TYPE;
        };
        this.outputPinDefinition.glyphFilter = glyph -> glyph == GlyphsRegistry.GENERATE_SHAPE_GLYPH;

        this.getInputPinEditorData(MOVEMENT_TYPE_PIN).ifPresent(pinEditorData -> pinEditorData.bHiddenInEditor = true);
        this.getInputPinEditorData(ON_IMPACT_PIN).ifPresent(pinEditorData -> pinEditorData.bHiddenInEditor = true);
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, MOVEMENT_TYPE_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.MOVEMENT_TYPE_VALUE_TYPE;

            // Initialize connection to a raw value glyph (of MOVEMENT_TYPE_VALUE_TYPE)
            GlyphUtils.connectNewGlyph(glyphInstance, MOVEMENT_TYPE_PIN, GlyphsRegistry.RAW_VALUE_GLYPH);
        });

        this.getInputPin(glyphInstance, EFFECT_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.EXECUTION_VALUE_TYPE;
        });

        this.getInputPin(glyphInstance, ON_IMPACT_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.EXECUTION_VALUE_TYPE;

            // Initialize connection to a program glyph
            GlyphUtils.connectNewGlyph(glyphInstance, ON_IMPACT_PIN, GlyphsRegistry.PROGRAM_GLYPH);
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue movementTypeValue = this.evaluatePin(executionContext, glyphInstance, MOVEMENT_TYPE_PIN);
        MovementType movementType = GlyphsRegistry.MOVEMENT_TYPE_VALUE_TYPE.getEnumGlyphValue(movementTypeValue);
        
        ServerLevel serverLevel = executionContext.getServerPlayer().get().level();
        Optional<ProjectileShape> spawnedShape = shoot(serverLevel, (LivingEntity) executionContext.getOwner(), InteractionHand.MAIN_HAND, executionContext.spellBookStack, List.of(new ItemStack(Items.ARROW)), 2.0F, 0.0F, false, null);
        
        spawnedShape.ifPresent(projectileShape -> {
            executionContext.copyPersistentVariables(projectileShape.persistentVarContainer);
            projectileShape.onImpactEffect = GlyphUtils.copyGlyphInstance(this.getInputPin(glyphInstance, EFFECT_PIN).flatMap(InputPin::getConnectedGlyph).get());
            projectileShape.onImpactProgram = GlyphUtils.copyGlyphInstance(this.getInputPin(glyphInstance, ON_IMPACT_PIN).flatMap(InputPin::getConnectedGlyph).get());
        });
        
        return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(1);
    }

    protected Optional<ProjectileShape> shoot(
            final ServerLevel level,
            final LivingEntity shooter,
            final InteractionHand hand,
            final ItemStack weapon,
            final List<ItemStack> projectiles,
            final float power,
            final float uncertainty,
            final boolean isCrit,
            @Nullable final LivingEntity targetOverride
    ) {
        ProjectileShape projectileEntityToSpawn = null;
        
        float maxAngle = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
        float angleStep = projectiles.size() == 1 ? 0.0F : 2.0F * maxAngle / (projectiles.size() - 1);
        float angleOffset = (projectiles.size() - 1) % 2 * angleStep / 2.0F;
        float direction = 1.0F;

        for (int i = 0; i < projectiles.size(); i++) {
            ItemStack projectile = (ItemStack)projectiles.get(i);
            if (!projectile.isEmpty()) {
                float angle = angleOffset + direction * ((i + 1) / 2) * angleStep;
                direction = -direction;
                int index = i;

                projectileEntityToSpawn = Projectile.spawnProjectile(
                        this.createProjectile(level, shooter, weapon, projectile, isCrit),
                        level,
                        projectile,
                        projectileEntity -> this.shootProjectile(shooter, projectileEntity, index, power, uncertainty, angle, targetOverride)
                );
                
                if (weapon.isEmpty()) {
                    break;
                }
            }
        }
        
        return Optional.ofNullable(projectileEntityToSpawn);
    }

    protected ProjectileShape createProjectile(final Level level, final LivingEntity shooter, final ItemStack weapon, final ItemStack projectile, final boolean isCrit) {
        ProjectileShape projectileShape = new ProjectileShape(level, shooter, projectile.copyWithCount(1), weapon);
        if (isCrit) {
            projectileShape.setCritArrow(true);
        }

        return projectileShape;
    }
    
    protected void shootProjectile(
            final LivingEntity shooter,
            final Projectile projectileEntity,
            final int index,
            final float power,
            final float uncertainty,
            final float angle,
            @Nullable final LivingEntity targetOverride
    ) {
        projectileEntity.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0.0F, power, uncertainty);
    }
}
