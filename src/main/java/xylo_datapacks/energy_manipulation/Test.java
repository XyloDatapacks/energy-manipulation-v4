package xylo_datapacks.energy_manipulation;

import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.OperationGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.operator.IntToString;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.operator.SumOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.runnable.DebugGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.runnable.PrintStringGlyph;

public class Test {

    public static void main(String[] args) {
        GlyphsRegistry glyphsRegistry = new GlyphsRegistry();
        glyphsRegistry.initialize();
        ExecutionContext executionContext = new ExecutionContext();

        // Print a string
        GlyphInstance debugStringPrint = GlyphsRegistry.DEBUG_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        GlyphInstance stringPrint = GlyphsRegistry.PRINT_STRING_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        
        GlyphInstance rawString = GlyphsRegistry.RAW_VALUE_GLYPH.instantiate(GlyphsRegistry.STRING_VALUE_TYPE);
        GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(rawString, GlyphsRegistry.STRING_VALUE_TYPE.makeStringGlyphValue("hello world!"));
        // ((GlyphGenericPayload)rawString.payload).content = GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(3);
        
        Glyph.connectGlyphStatic(debugStringPrint, DebugGlyph.INPUT_PIN, stringPrint);
        Glyph.connectGlyphStatic(stringPrint, PrintStringGlyph.STRING_PIN, rawString);

        Glyph.executeStatic(executionContext, debugStringPrint);


        // A + B
        GlyphInstance sumStringPrint = GlyphsRegistry.PRINT_STRING_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        Glyph.connectNewGlyphWithCallbackStatic(sumStringPrint, PrintStringGlyph.STRING_PIN, GlyphsRegistry.OPERATION_GLYPH, toStringOperation -> {
            
            Glyph.connectNewGlyphWithCallbackStatic(toStringOperation, OperationGlyph.OPERATOR_PIN, GlyphsRegistry.INT_TO_STRING_OPERATOR_GLYPH, toStringOperator -> {
                Glyph.connectNewGlyphWithCallbackStatic(toStringOperator, IntToString.INT_VALUE_PIN, GlyphsRegistry.OPERATION_GLYPH, sumOperation -> {
                   
                    Glyph.connectNewGlyphWithCallbackStatic(sumOperation, OperationGlyph.OPERATOR_PIN, GlyphsRegistry.SUM_OPERATOR_GLYPH, sumOperator -> {
                        Glyph.connectNewGlyphWithCallbackStatic(sumOperator, SumOperatorGlyph.FIRST_VALUE_PIN, GlyphsRegistry.RAW_VALUE_GLYPH, fistNum -> {
                            GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(fistNum, GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(4));
                        });
    
                        Glyph.connectNewGlyphWithCallbackStatic(sumOperator, SumOperatorGlyph.SECOND_VALUE_PIN, GlyphsRegistry.RAW_VALUE_GLYPH, secondNum -> {
                            GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(secondNum, GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(12));
                        });
                    });
                });
            });
        });

        Glyph.executeStatic(executionContext, sumStringPrint);
    }
}
