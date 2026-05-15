package xylo_datapacks.energy_manipulation.glyph;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperationGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator.SumOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.DebugGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.PrintStringGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.ProgramGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.*;
import xylo_datapacks.energy_manipulation.glyph.value_type.*;

import java.util.function.Supplier;

public class GlyphsRegistry {
    public static final String GLYPH_PATH = "glyph";
    public static final String VALUE_TYPE_PATH = "value_type";
    
    // Glyph registry
    public static final ResourceKey<Registry<Glyph>> GLYPH_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "glyph_registry"));
    public static final Registry<Glyph> GLYPH = FabricRegistryBuilder.create(GLYPH_REGISTRY_KEY).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    // Value Type registry
    public static final ResourceKey<Registry<GlyphValueType>> VALUE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "glyph_value_type_registry"));
    public static final Registry<GlyphValueType> VALUE_TYPE = FabricRegistryBuilder.create(VALUE_TYPE_REGISTRY_KEY).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    
    static public final RawValueGlyph RAW_VALUE_GLYPH = registerGlyph("raw_value", RawValueGlyph::new);
    static public final ProgramGlyph PROGRAM_GLYPH = registerGlyph("program", ProgramGlyph::new);
    static public final PrintStringGlyph PRINT_STRING_GLYPH = registerGlyph("print_string", PrintStringGlyph::new);
    static public final DebugGlyph DEBUG_GLYPH = registerGlyph("debug", DebugGlyph::new);
    static public final OperationGlyph OPERATION_GLYPH = registerGlyph("operation", OperationGlyph::new);
    static public final SumOperatorGlyph SUM_OPERATOR_GLYPH = registerGlyph("sum_operator", SumOperatorGlyph::new);
    static public final FromConversionGlyph FROM_CONVERSION_GLYPH = registerGlyph("from_conversion", FromConversionGlyph::new);
    static public final VarDefinitionGlyph VAR_DEFINITION_GLYPH = registerGlyph("var_definition", VarDefinitionGlyph::new);
    static public final VarSetterGlyph VAR_SETTER_GLYPH = registerGlyph("var_setter", VarSetterGlyph::new);
    static public final VarGetterGlyph VAR_GETTER_GLYPH = registerGlyph("var_getter", VarGetterGlyph::new);
    
    static public final ClassValueType CLASS_VALUE_TYPE = registerValueType("class", ClassValueType::new);
    static public final StringValueType STRING_VALUE_TYPE = registerValueType("string", StringValueType::new);
    static public final BoolValueType BOOL_VALUE_TYPE = registerValueType("bool", BoolValueType::new);
    static public final IntValueType INT_VALUE_TYPE = registerValueType("int", IntValueType::new);
    static public final ExecutionValueType EXECUTION_VALUE_TYPE = registerValueType("execution", ExecutionValueType::new);
    static public final ExecutionErrorValueType EXECUTION_ERROR_VALUE_TYPE = registerValueType("execution_error", ExecutionErrorValueType::new);
    static public final VarNameValueType VAR_NAME_VALUE_TYPE = registerValueType("var_name", VarNameValueType::new);

    
    public static String getGlyphPath(Glyph glyph) {
        return GlyphsRegistry.GLYPH.getKey(glyph).getPath();
    }
    
    public static String getGlyphSimplePath(Glyph glyph) {
        return getGlyphPath(glyph).replace(GLYPH_PATH + "/", "");
    }
    
    public static String makeGlyphSimplePath(Identifier glyphIdentifier) {
        return glyphIdentifier.getPath().replace(GLYPH_PATH + "/", "");
    }

    public static String makeGlyphTranslationKey(Identifier glyphIdentifier) {
        return GLYPH_PATH + "." + EnergyManipulation.MOD_ID + "." + makeGlyphSimplePath(glyphIdentifier);
    }
    
    public static String getGlyphTypeSpecifyPath(Glyph glyph, GlyphValueType valueType) {
        return getGlyphPath(glyph) + "/" + getValueTypeSimplePath(valueType);
    }
    
    

    public static String getValueTypePath(GlyphValueType valueType) {
        return GlyphsRegistry.VALUE_TYPE.getKey(valueType).getPath();
    }

    public static String getValueTypeSimplePath(GlyphValueType valueType) {
        return getValueTypePath(valueType).replace(VALUE_TYPE_PATH + "/", "");
    }

    public static String makeValueTypeSimplePath(Identifier valueTypeIdentifier) {
        return valueTypeIdentifier.getPath().replace(VALUE_TYPE_PATH + "/", "");
    }

    public static String makeValueTypeTranslationKey(Identifier valueTypeIdentifier) {
        return VALUE_TYPE_PATH + "." + EnergyManipulation.MOD_ID + "." + makeValueTypeSimplePath(valueTypeIdentifier);
    }
    
    /*----------------------------------------------------------------------------------------------------------------*/

    public static <T extends Glyph> T registerGlyph(String name, Supplier<T> factory) {
        T glyph = factory.get();
        Registry.register(GLYPH, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, GLYPH_PATH + "/" + name), glyph);
        return glyph;
    }

    public static <T extends GlyphValueType> T registerValueType(String name, Supplier<T> factory) {
        T valueType = factory.get();
        Registry.register(VALUE_TYPE, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, VALUE_TYPE_PATH + "/" + name), valueType);
        return valueType;
    }
    
    public static void initialize() {
        EnergyManipulation.LOGGER.info("Registering Glyphs for " + EnergyManipulation.MOD_ID);
    }
}

