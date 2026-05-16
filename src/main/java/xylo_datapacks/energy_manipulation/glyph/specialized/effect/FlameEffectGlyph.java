package xylo_datapacks.energy_manipulation.glyph.specialized.effect;

import net.minecraft.world.entity.Entity;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.shape.ShapeGlyphInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

public class FlameEffectGlyph extends Glyph implements EffectGlyphInterface {
    static public String DURATION_PIN = "duration";

    public FlameEffectGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        this.RegisterPinDefinition(DURATION_PIN, glyph -> true);
        
        this.outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType == GlyphsRegistry.EXECUTION_VALUE_TYPE;
        };
        this.outputPinDefinition.glyphFilter = ShapeGlyphInterface.class::isInstance;
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, DURATION_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.INT_VALUE_TYPE;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue durationValue = this.evaluatePin(executionContext, glyphInstance, DURATION_PIN);
        int duration = GlyphsRegistry.INT_VALUE_TYPE.getIntGlyphValue(durationValue);
        
        Entity target = executionContext.getTarget();
        if (target != null) {
            target.setRemainingFireTicks(target.getRemainingFireTicks() + duration);
            return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(1);
        }

        return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(0);
    }
}
