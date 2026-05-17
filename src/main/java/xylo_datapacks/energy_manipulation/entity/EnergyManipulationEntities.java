package xylo_datapacks.energy_manipulation.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.entity.shape.ProjectileShape;
import xylo_datapacks.energy_manipulation.glyph.Glyph;

public class EnergyManipulationEntities {
    // Shape registry
    public static final ResourceKey<Registry<EntityType<?>>> SHAPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "shape_entity_registry"));
    public static final Registry<EntityType<?>> SHAPE = FabricRegistryBuilder.create(SHAPE_REGISTRY_KEY).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();

    public static final EntityType<ProjectileShape> PROJECTILE_SHAPE = registerShapeEntity("projectile_shape",
            EntityType.Builder.<ProjectileShape>of(ProjectileShape::new, MobCategory.MISC)
                    .sized(0.7F, 0.65F)
                    .eyeHeight(0.26F)
                    .clientTrackingRange(10)
    );
    
    
    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name));
        EntityType<T> entityType = builder.build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, entityType);
        PolymerEntityUtils.registerType(entityType);
        return entityType;
    }

    private static <T extends Entity> EntityType<T> registerShapeEntity(String name, EntityType.Builder<T> builder) {
        EntityType<T> entityType = register(name, builder);
        
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name));
        Registry.register(SHAPE, key, entityType);
        
        return entityType;
    }

    public static void registerAttributes() {

    }

    public static void initialize() {
        EnergyManipulation.LOGGER.info("Registering EntityTypes for " + EnergyManipulation.MOD_ID);
    }
}
