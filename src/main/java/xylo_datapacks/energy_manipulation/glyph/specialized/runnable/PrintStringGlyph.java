package xylo_datapacks.energy_manipulation.glyph.specialized.runnable;

import net.minecraft.network.chat.Component;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

public class PrintStringGlyph extends Glyph {
    static public String STRING_PIN = "String";
    
    public PrintStringGlyph() {
        super();
        
        this.inputPinMode = InputPinMode.STANDARD;
        this.RegisterPinDefinition(STRING_PIN, glyph -> true);
        this.outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> { 
            return valueType == GlyphsRegistry.EXECUTION_VALUE_TYPE; 
        };
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, STRING_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.STRING_VALUE_TYPE;
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue stringValue = this.evaluatePin(executionContext, glyphInstance, STRING_PIN);
        String string = GlyphsRegistry.STRING_VALUE_TYPE.getStringGlyphValue(stringValue);

        String message = ">> " + string;
        executionContext.player.sendSystemMessage(Component.literal(message));
        EnergyManipulation.LOGGER.info(message);
        
        return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(1);
    }
}
