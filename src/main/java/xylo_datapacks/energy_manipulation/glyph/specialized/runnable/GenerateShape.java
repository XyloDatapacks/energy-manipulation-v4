package xylo_datapacks.energy_manipulation.glyph.specialized.runnable;

import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.shape.ShapeGlyphInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

public class GenerateShape extends Glyph {
    static public String SHAPE_PIN = "shape";

    public GenerateShape() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        this.RegisterPinDefinition(SHAPE_PIN, ShapeGlyphInterface.class::isInstance);
        
        this.outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType == GlyphsRegistry.EXECUTION_VALUE_TYPE;
        };
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, SHAPE_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.EXECUTION_VALUE_TYPE;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue executionValue = this.evaluatePin(executionContext, glyphInstance, SHAPE_PIN);
        return executionValue;
    }
    
}
