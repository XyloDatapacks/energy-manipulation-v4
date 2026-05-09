package xylo_datapacks.energy_manipulation.glyph;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperationGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator.IntToStringOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator.SumOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.DebugGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.PrintStringGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.ProgramGlyph;
import xylo_datapacks.energy_manipulation.glyph.value_type.*;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.RawValueGlyph;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GlyphsRegistry {
    // Glyph registry
    public static final ResourceKey<Registry<Glyph>> GLYPHS_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "glyph_registry"));
    public static final Registry<Glyph> GLYPH = FabricRegistryBuilder.create(GLYPHS_REGISTRY_KEY).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    // Value Type registry
    public static final ResourceKey<Registry<GlyphValueType>> VALUE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "glyph_value_type_registry"));
    public static final Registry<GlyphValueType> VALUE_TYPE = FabricRegistryBuilder.create(VALUE_TYPE_REGISTRY_KEY).attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    
    static public final RawValueGlyph RAW_VALUE_GLYPH = registerGlyph("raw_value_glyph", RawValueGlyph::new);
    static public final ProgramGlyph PROGRAM_GLYPH = registerGlyph("program_glyph", ProgramGlyph::new);
    static public final PrintStringGlyph PRINT_STRING_GLYPH = registerGlyph("print_string_glyph", PrintStringGlyph::new);
    static public final DebugGlyph DEBUG_GLYPH = registerGlyph("debug_glyph", DebugGlyph::new);
    static public final OperationGlyph OPERATION_GLYPH = registerGlyph("operation_glyph", OperationGlyph::new);
    static public final SumOperatorGlyph SUM_OPERATOR_GLYPH = registerGlyph("sum_operator_glyph", SumOperatorGlyph::new);
    static public final IntToStringOperatorGlyph INT_TO_STRING_OPERATOR_GLYPH = registerGlyph("int_to_string_operator_glyph", IntToStringOperatorGlyph::new);
    
    static public final StringValueType STRING_VALUE_TYPE = registerValueType("string_value_type", StringValueType::new);
    static public final BoolValueType BOOL_VALUE_TYPE = registerValueType("bool_value_type", BoolValueType::new);
    static public final IntValueType INT_VALUE_TYPE = registerValueType("int_value_type", IntValueType::new);
    static public final ExecutionValueType EXECUTION_VALUE_TYPE = registerValueType("execution_value_type", ExecutionValueType::new);
    static public final ExecutionErrorValueType EXECUTION_ERROR_VALUE_TYPE = registerValueType("execution_error_value_type", ExecutionErrorValueType::new);

    public static <T extends Glyph> T registerGlyph(String name, Supplier<T> Factory) {
        T glyph = Factory.get();
        Registry.register(GLYPH, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name), glyph);
        return glyph;
    }

    public static <T extends GlyphValueType> T registerValueType(String name, Supplier<T> Factory) {
        T valueType = Factory.get();
        Registry.register(VALUE_TYPE, Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name), valueType);
        return valueType;
    }
    
    public static void initialize() {
        EnergyManipulation.LOGGER.info("Registering Glyphs for " + EnergyManipulation.MOD_ID);
    }
}

