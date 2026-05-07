package xylo_datapacks.energy_manipulation;

import xylo_datapacks.energy_manipulation.glyph.*;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.OperationGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.operator.IntToString;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operation.operator.SumOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.runnable.DebugGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.runnable.PrintStringGlyph;

public class Test {

    public static void main(String[] args) {
        GlyphsRegistry.register();
        ExecutionContext executionContext = new ExecutionContext();

        // Print a string
        GlyphInstance debugStringPrint = GlyphsRegistry.DEBUG_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        GlyphInstance stringPrint = GlyphsRegistry.PRINT_STRING_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        
        GlyphInstance rawString = GlyphsRegistry.RAW_VALUE_GLYPH.instantiate(GlyphsRegistry.STRING_VALUE_TYPE);
        GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(rawString, GlyphsRegistry.STRING_VALUE_TYPE.makeStringGlyphValue("hello world!"));
        // ((GlyphGenericPayload)rawString.payload).content = GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(3);
        
        GlyphUtils.connectGlyph(debugStringPrint, DebugGlyph.INPUT_PIN, stringPrint);
        GlyphUtils.connectGlyph(stringPrint, PrintStringGlyph.STRING_PIN, rawString);

        GlyphUtils.execute(executionContext, debugStringPrint);


        // A + B
        GlyphInstance sumStringPrint = GlyphsRegistry.PRINT_STRING_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        GlyphUtils.connectNewGlyphWithCallback(sumStringPrint, PrintStringGlyph.STRING_PIN, GlyphsRegistry.OPERATION_GLYPH, toStringOperation -> {

            GlyphUtils.connectNewGlyphWithCallback(toStringOperation, OperationGlyph.OPERATOR_PIN, GlyphsRegistry.INT_TO_STRING_OPERATOR_GLYPH, toStringOperator -> {
                GlyphUtils.connectNewGlyphWithCallback(toStringOperator, IntToString.INT_VALUE_PIN, GlyphsRegistry.OPERATION_GLYPH, sumOperation -> {

                    GlyphUtils.connectNewGlyphWithCallback(sumOperation, OperationGlyph.OPERATOR_PIN, GlyphsRegistry.SUM_OPERATOR_GLYPH, sumOperator -> {
                        GlyphUtils.connectNewGlyphWithCallback(sumOperator, SumOperatorGlyph.FIRST_VALUE_PIN, GlyphsRegistry.RAW_VALUE_GLYPH, fistNum -> {
                            GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(fistNum, GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(4));
                        });

                        GlyphUtils.connectNewGlyphWithCallback(sumOperator, SumOperatorGlyph.SECOND_VALUE_PIN, GlyphsRegistry.RAW_VALUE_GLYPH, secondNum -> {
                            GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(secondNum, GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(12));
                        });
                    });
                });
            });
        });

        GlyphUtils.execute(executionContext, sumStringPrint);
    }
}
