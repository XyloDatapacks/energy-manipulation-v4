package xylo_datapacks.energy_manipulation.glyph.specialized.variable;

import xylo_datapacks.energy_manipulation.glyph.*;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.Optional;

public class VarDefinitionGlyph extends Glyph {
    static public String TYPE_PIN = "type";
    static public String NAME_PIN = "name";
    
    public VarDefinitionGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;

        this.RegisterPinDefinition(TYPE_PIN, glyph -> {
            return glyph == GlyphsRegistry.RAW_VALUE_GLYPH;
        });

        this.RegisterPinDefinition(NAME_PIN, glyph -> {
            return glyph == GlyphsRegistry.RAW_VALUE_GLYPH;
        });

        this.outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType == GlyphsRegistry.EXECUTION_VALUE_TYPE;
        };
        
        this.getInputPinEditorData(TYPE_PIN).ifPresent(pinEditorData -> pinEditorData.bHiddenInEditor = true);
        this.getInputPinEditorData(NAME_PIN).ifPresent(pinEditorData -> pinEditorData.bHiddenInEditor = true);
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, TYPE_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.CLASS_VALUE_TYPE;

            // Initialize connection to a raw value glyph (of CLASS_VALUE_TYPE)
            GlyphUtils.connectNewGlyphWithCallback(glyphInstance, TYPE_PIN, GlyphsRegistry.RAW_VALUE_GLYPH, connectedInstance -> {
                GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(connectedInstance, GlyphsRegistry.CLASS_VALUE_TYPE.MakeDefaulted());
            });
        });

        this.getInputPin(glyphInstance, NAME_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.STRING_VALUE_TYPE;

            // Initialize connection to a raw value glyph (of STRING_VALUE_TYPE)
            GlyphUtils.connectNewGlyphWithCallback(glyphInstance, NAME_PIN, GlyphsRegistry.RAW_VALUE_GLYPH, connectedInstance -> {
                GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(connectedInstance, GlyphsRegistry.STRING_VALUE_TYPE.MakeDefaulted());
            });
        });
    }

    @Override
    public void onInputPinGlyphPayloadChanged(GlyphInstance glyphInstance, int pinIndex) {
        // Workaround to send update to editor.
        this.NotifyInputPinConnectionChanged(glyphInstance, pinIndex);
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue varNameValue = evaluatePin(executionContext, glyphInstance, NAME_PIN);
        GlyphValue varTypeValue = evaluatePin(executionContext, glyphInstance, TYPE_PIN);

        String varName = GlyphsRegistry.STRING_VALUE_TYPE.getStringGlyphValue(varNameValue);
        Optional<GlyphValueType> varType = GlyphsRegistry.CLASS_VALUE_TYPE.getClassGlyphValue(varTypeValue);

        // If we have no name or type, we cannot register the variable.
        if (varName.isEmpty() || varType.isEmpty()) {
            return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(0);
        }
        
        // Register variable with default value.
        executionContext.setVariable(varName, varType.get().MakeDefaulted());
        return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(1);
    }
}
