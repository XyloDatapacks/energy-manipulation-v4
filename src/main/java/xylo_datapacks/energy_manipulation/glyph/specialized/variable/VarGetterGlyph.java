package xylo_datapacks.energy_manipulation.glyph.specialized.variable;

import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.Optional;

public class VarGetterGlyph extends Glyph {
    static public String NAME_PIN = "name";
    
    public VarGetterGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        
        this.RegisterPinDefinition(NAME_PIN, glyph -> {
            return glyph == GlyphsRegistry.RAW_VALUE_GLYPH;
        });

        this.outputPinDefinition.valueTypeCompatibilityPredicate = GlyphValueType::canBeVariable;

        this.getInputPinEditorData(NAME_PIN).ifPresent(pinEditorData -> pinEditorData.bHiddenInEditor = true);
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, NAME_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.VAR_NAME_VALUE_TYPE;

            // Initialize connection to a raw value glyph (of VAR_NAME_VALUE_TYPE)
            GlyphUtils.connectNewGlyphWithCallback(glyphInstance, NAME_PIN, GlyphsRegistry.RAW_VALUE_GLYPH, connectedInstance -> {
                GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(connectedInstance, GlyphsRegistry.VAR_NAME_VALUE_TYPE.MakeDefaulted());
            });
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue varNameValue = this.evaluatePin(executionContext, glyphInstance, NAME_PIN);

        String varName = GlyphsRegistry.VAR_NAME_VALUE_TYPE.getVarName(varNameValue);
        GlyphValueType requestedOutputType = glyphInstance.outputPin.valueType;
        
        Optional<GlyphValue> output = executionContext.getVariable(varName);
        
        if (output.isEmpty()) {
            return GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("VarGetterGlyph: variable does not exist");
        }
        
        if (!output.get().isOfType(requestedOutputType)) {
            return GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("VarGetterGlyph: variable " + varName + " is not of type " + requestedOutputType.getClass().getSimpleName());
        }

        return output.get();
    }
}
