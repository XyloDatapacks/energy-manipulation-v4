package xylo_datapacks.energy_manipulation.glyph;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.specialized.effect.FlameEffectGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperationGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator.SumOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.DebugGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.GenerateShape;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.PrintStringGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.ProgramGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.shape.MovementType;
import xylo_datapacks.energy_manipulation.glyph.specialized.shape.ProjectileShapeGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.*;
import xylo_datapacks.energy_manipulation.glyph.value_type.*;

import java.util.Objects;
import java.util.function.Supplier;

public class GlyphsRegistry {
    // Glyph registry
    public static final ResourceKey<Registry<Glyph>> GLYPH_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "glyph"));
    public static final Registry<Glyph> GLYPH = FabricRegistryBuilder.create(GLYPH_REGISTRY_KEY).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    // Value Type registry
    public static final ResourceKey<Registry<GlyphValueType>> VALUE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "glyph_value_type"));
    public static final Registry<GlyphValueType> VALUE_TYPE = FabricRegistryBuilder.create(VALUE_TYPE_REGISTRY_KEY).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    
    static public final RawValueGlyph RAW_VALUE_GLYPH = registerGlyph("raw_value", RawValueGlyph::new);
    static public final ProgramGlyph PROGRAM_GLYPH = registerGlyph("program", ProgramGlyph::new);
    static public final PrintStringGlyph PRINT_STRING_GLYPH = registerGlyph("print_string", PrintStringGlyph::new);
    static public final DebugGlyph DEBUG_GLYPH = registerGlyph("debug", DebugGlyph::new);
    static public final OperationGlyph OPERATION_GLYPH = registerGlyph("operation", OperationGlyph::new);
    static public final SumOperatorGlyph SUM_OPERATOR_GLYPH = registerGlyph("sum_operator", SumOperatorGlyph::new);
    static public final FromConversionGlyph FROM_CONVERSION_GLYPH = registerGlyph("from_conversion", FromConversionGlyph::new);
    static public final VarDefinitionGlyph VAR_DEFINITION_GLYPH = registerGlyph("var_definition", VarDefinitionGlyph::new);
    static public final PersistentVarDefGlyph PERSISTENT_VAR_DEF_GLYPH = registerGlyph("persistent_var_definition", PersistentVarDefGlyph::new);
    static public final VarSetterGlyph VAR_SETTER_GLYPH = registerGlyph("var_setter", VarSetterGlyph::new);
    static public final VarGetterGlyph VAR_GETTER_GLYPH = registerGlyph("var_getter", VarGetterGlyph::new);
    static public final GenerateShape GENERATE_SHAPE_GLYPH = registerGlyph("generate_shape", GenerateShape::new);
    static public final ProjectileShapeGlyph PROJECTILE_SHAPE_GLYPH = registerGlyph("projectile_shape", ProjectileShapeGlyph::new);
    static public final FlameEffectGlyph FLAME_EFFECT_GLYPH = registerGlyph("flame_effect", FlameEffectGlyph::new);
    
    static public final ClassValueType CLASS_VALUE_TYPE = registerValueType("class", ClassValueType::new);
    static public final StringValueType STRING_VALUE_TYPE = registerValueType("string", StringValueType::new);
    static public final BoolValueType BOOL_VALUE_TYPE = registerValueType("bool", BoolValueType::new);
    static public final IntValueType INT_VALUE_TYPE = registerValueType("int", IntValueType::new);
    static public final ExecutionValueType EXECUTION_VALUE_TYPE = registerValueType("execution", ExecutionValueType::new);
    static public final ExecutionErrorValueType EXECUTION_ERROR_VALUE_TYPE = registerValueType("execution_error", ExecutionErrorValueType::new);
    static public final VarNameValueType VAR_NAME_VALUE_TYPE = registerValueType("var_name", VarNameValueType::new);
    static public final EnumValueType<MovementType> MOVEMENT_TYPE_VALUE_TYPE = registerValueType("movement_type", () -> new EnumValueType<>(MovementType.class));


    
    public static String getGlyphPath(Glyph glyph) {
        return Objects.requireNonNull(GlyphsRegistry.GLYPH.getKey(glyph)).getPath();
    }

    public static String getGlyphResourcePath(Glyph glyph) {
        return GLYPH_REGISTRY_KEY.identifier().getPath() + "/" + getGlyphPath(glyph);
    }
    
    public static String addGlyphResourcePath(String path) {
        return GLYPH_REGISTRY_KEY.identifier().getPath() + "/" + path;
    }
    
    public static String getGlyphTranslationKey(Glyph glyph) {
        return GLYPH_REGISTRY_KEY.identifier().getPath() + "." + EnergyManipulation.MOD_ID + "." + getGlyphPath(glyph);   
    }

    public static String makeGlyphTranslationKey(Identifier glyphIdentifier) {
        return GLYPH_REGISTRY_KEY.identifier().getPath() + "." + EnergyManipulation.MOD_ID + "." + glyphIdentifier.getPath();
    }
    
    public static String getGlyphTypeSpecifyPath(Glyph glyph, GlyphValueType valueType) {
        return getGlyphPath(glyph) + "/" + getValueTypePath(valueType);
    }
    
    

    public static String getValueTypePath(GlyphValueType valueType) {
        return Objects.requireNonNull(GlyphsRegistry.VALUE_TYPE.getKey(valueType)).getPath();
    }

    public static String getValueTypeResourcePath(GlyphValueType valueType) {
        return VALUE_TYPE_REGISTRY_KEY.identifier().getPath() + "/" + getValueTypePath(valueType);
    }

    public static String addValueTypeResourcePath(String path) {
        return VALUE_TYPE_REGISTRY_KEY.identifier().getPath() + "/" + path;
    }
    
    public static String getValueTypeTranslationKey(GlyphValueType valueType) {
        return VALUE_TYPE_REGISTRY_KEY.identifier().getPath() + "." + EnergyManipulation.MOD_ID + "." + getValueTypePath(valueType);
    }

    public static String makeValueTypeTranslationKey(Identifier valueTypeIdentifier) {
        return VALUE_TYPE_REGISTRY_KEY.identifier().getPath() + "." + EnergyManipulation.MOD_ID + "." + valueTypeIdentifier.getPath();
    }
    
    /*----------------------------------------------------------------------------------------------------------------*/

    public static <T extends Glyph> T registerGlyph(String name, Supplier<T> factory) {
        Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name);
        ResourceKey<Glyph> glyphKey = ResourceKey.create(GLYPH_REGISTRY_KEY, identifier);
        
        T glyph = factory.get();
        Registry.register(GLYPH, glyphKey, glyph);
        return glyph;
    }

    public static <T extends GlyphValueType> T registerValueType(String name, Supplier<T> factory) {
        Identifier identifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name);
        ResourceKey<GlyphValueType> valueTypeKey = ResourceKey.create(VALUE_TYPE_REGISTRY_KEY, identifier);
        
        T valueType = factory.get();
        Registry.register(VALUE_TYPE, valueTypeKey, valueType);
        return valueType;
    }
    
    public static void initialize() {
        EnergyManipulation.LOGGER.info("Registering Glyphs for " + EnergyManipulation.MOD_ID);
    }
}

