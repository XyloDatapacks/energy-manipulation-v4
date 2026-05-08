package xylo_datapacks.energy_manipulation.glyph;

import net.minecraft.resources.Identifier;
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
    public static final Map<Identifier, Glyph> GLYPHS = new LinkedHashMap<>();
    public static final Map<Identifier, GlyphValueType> VALUE_TYPES = new LinkedHashMap<>();
    
    static public RawValueGlyph RAW_VALUE_GLYPH = registerGlyph("raw_value_glyph", RawValueGlyph::new);
    static public ProgramGlyph PROGRAM_GLYPH = registerGlyph("program_glyph", ProgramGlyph::new);
    static public PrintStringGlyph PRINT_STRING_GLYPH = registerGlyph("print_string_glyph", PrintStringGlyph::new);
    static public DebugGlyph DEBUG_GLYPH = registerGlyph("debug_glyph", DebugGlyph::new);
    static public OperationGlyph OPERATION_GLYPH = registerGlyph("operation_glyph", OperationGlyph::new);
    static public SumOperatorGlyph SUM_OPERATOR_GLYPH = registerGlyph("sum_operator_glyph", SumOperatorGlyph::new);
    static public IntToStringOperatorGlyph INT_TO_STRING_OPERATOR_GLYPH = registerGlyph("int_to_string_operator_glyph", IntToStringOperatorGlyph::new);
    
    static public StringValueType STRING_VALUE_TYPE = registerValueType("string_value_type", StringValueType::new);
    static public BoolValueType BOOL_VALUE_TYPE = registerValueType("bool_value_type", BoolValueType::new);
    static public IntValueType INT_VALUE_TYPE = registerValueType("int_value_type", IntValueType::new);
    static public ExecutionValueType EXECUTION_VALUE_TYPE = registerValueType("execution_value_type", ExecutionValueType::new);
    static public ExecutionErrorValueType EXECUTION_ERROR_VALUE_TYPE = registerValueType("execution_error_value_type", ExecutionErrorValueType::new);

    public static <T extends Glyph> T registerGlyph(String name, Supplier<T> Factory) {
        T glyph = Factory.get();
        GLYPHS.put(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name), glyph);
        return glyph;
    }

    public static <T extends GlyphValueType> T registerValueType(String name, Supplier<T> Factory) {
        T valueType = Factory.get();
        VALUE_TYPES.put(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, name), valueType);
        return valueType;
    }
    
    public static void register() {
        EnergyManipulation.LOGGER.info("Registering Glyphs for " + EnergyManipulation.MOD_ID);
    }
}

