package xylo_datapacks.energy_manipulation.glyph.specialized.runnable.runnable;

import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.concurrent.atomic.AtomicInteger;

public class ProgramGlyph extends Glyph {
    static public String INSTRUCTION_PIN = "Instruction";

    public ProgramGlyph() {
        super();

        this.inputPinMode = InputPinMode.ARRAY;
        RegisterPinDefinition(INSTRUCTION_PIN, glyph -> true);
        outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType == GlyphsRegistry.EXECUTION_VALUE_TYPE;
        };
    }

    @Override
    public void initializeNewPin(GlyphInstance glyphInstance, InputPin newInputPin) {
        if (newInputPin != null) {
            newInputPin.valueType = GlyphsRegistry.EXECUTION_VALUE_TYPE;
        }
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {

        AtomicInteger SuccessCount = new AtomicInteger(0);
        
        for (int i = 0; i < glyphInstance.inputPins.size(); i++) {
            GlyphValue executionResult = evaluatePin(executionContext, glyphInstance, i);
            if (GlyphsRegistry.EXECUTION_VALUE_TYPE.getExecutionGlyphValue(executionResult) > 0) {
                SuccessCount.incrementAndGet();
            }
        }
        
        return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(SuccessCount.get());
    }
}