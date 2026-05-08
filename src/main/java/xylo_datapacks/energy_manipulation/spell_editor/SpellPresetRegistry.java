package xylo_datapacks.energy_manipulation.spell_editor;

import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperationGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator.IntToString;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.operator.SumOperatorGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.runnable.PrintStringGlyph;

public class SpellPresetRegistry {
    
    public static GlyphInstance makeSumTest() {
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
        
        return sumStringPrint;
    }

    public static GlyphInstance makeProgramTest() {
        GlyphInstance programGlyph = GlyphsRegistry.PROGRAM_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        programGlyph.glyph.addPin(programGlyph);
        programGlyph.glyph.addPin(programGlyph);
        
        GlyphUtils.connectGlyph(programGlyph, 0, makeSumTest());
        GlyphUtils.connectGlyph(programGlyph, 1, makeSumTest());

        return programGlyph;
    }
}
