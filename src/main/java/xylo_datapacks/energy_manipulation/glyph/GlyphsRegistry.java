package xylo_datapacks.energy_manipulation.glyph;

import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.OperationGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.operator.IntToString;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.operator.SumOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.runnable.DebugGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.runnable.PrintStringGlyph;
import xylo_datapacks.energy_manipulation.glyph.value_type.*;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.variable.RawValueGlyph;

public class GlyphsRegistry {
    static public RawValueGlyph RAW_VALUE_GLYPH = new RawValueGlyph();
    static public PrintStringGlyph PRINT_STRING_GLYPH = new PrintStringGlyph();
    static public DebugGlyph DEBUG_GLYPH = new DebugGlyph();
    static public OperationGlyph OPERATION_GLYPH = new OperationGlyph();
    static public SumOperatorGlyph SUM_OPERATOR_GLYPH = new SumOperatorGlyph();
    static public IntToString INT_TO_STRING_OPERATOR_GLYPH = new IntToString();
    
    static public StringValueType STRING_VALUE_TYPE = new StringValueType();
    static public BoolValueType BOOL_VALUE_TYPE = new BoolValueType();
    static public IntValueType INT_VALUE_TYPE = new IntValueType();
    static public ExecutionValueType EXECUTION_VALUE_TYPE = new ExecutionValueType();
    static public ExecutionErrorValueType EXECUTION_ERROR_VALUE_TYPE = new ExecutionErrorValueType();
    
    public static void register() {
        
    }
}

