package xylo_datapacks.energy_manipulation.glyph.specialized.variable;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.Optional;

public class VarSetterGlyph extends Glyph {
    static public String NAME_PIN = "name";
    static public String VALUE_PIN = "value";
    
    public VarSetterGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;

        this.RegisterPinDefinition(NAME_PIN, glyph -> {
            return glyph == GlyphsRegistry.RAW_VALUE_GLYPH;
        });

        this.RegisterPinDefinition(VALUE_PIN, glyph -> true);

        this.outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType == GlyphsRegistry.EXECUTION_VALUE_TYPE;
        };

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
    public void onInputPinGlyphPayloadChanged(GlyphInstance glyphInstance, int pinIndex) {
        // If the name pin changed, we want to update the value type of VALUE_PIN according to the variable type.
        if (this.getInputPinDefinition(pinIndex).get().pinName.equals(NAME_PIN)) {
            this.getInputPin(glyphInstance, VALUE_PIN).ifPresent(valuePin -> {

                // Get variable name value from NAME_PIN.
                GlyphInstance rawValueGlyphInstance = this.getInputPin(glyphInstance, NAME_PIN).flatMap(InputPin::getConnectedGlyph).get();
                Optional<GlyphValue> varNameValue = ((RawValueGlyph) rawValueGlyphInstance.glyph).getPayloadValue(rawValueGlyphInstance);
                
                // Get the new value type from var name value
                Optional<GlyphValueType> newValueType = varNameValue.flatMap(GlyphsRegistry.VAR_NAME_VALUE_TYPE::getVarValueType);

                if (newValueType.isPresent()) {
                    // Update value type for VALUE_PIN.
                    GlyphValueType oldValueType = valuePin.valueType;
                    valuePin.valueType = newValueType.get();

                    // If the value type of VALUE_PIN changed, reset the connection.
                    if (oldValueType != valuePin.valueType) {
                        this.resetConnection(glyphInstance, VALUE_PIN);
                    }
                }
            });
        }
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        GlyphValue varNameValue = this.evaluatePin(executionContext, glyphInstance, NAME_PIN);

        String varName = GlyphsRegistry.VAR_NAME_VALUE_TYPE.getVarName(varNameValue);
        Optional<GlyphValueType> varType = GlyphsRegistry.VAR_NAME_VALUE_TYPE.getVarValueType(varNameValue);

        // If we have no name or type, we cannot set the variable.
        if (varName.isEmpty() || varType.isEmpty()) {
            return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(0);
        }

        // get the new value for the variable
        GlyphValue newVarValueValue = this.evaluatePin(executionContext, glyphInstance, VALUE_PIN);
        if (!newVarValueValue.isOfType(varType.get())) {
            // Accounts for execution errors coming from value pin.
            EnergyManipulation.LOGGER.warn("VarSetterGlyph: variable {} is of type {}. Cannot assign value of type {} to it!", varName, varType.get().getClass().getSimpleName(), newVarValueValue.getValueType().getClass().getSimpleName());
            return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(0);
        }

        // Set variable value
        executionContext.setVariable(varName, newVarValueValue);
        return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(1);
    }
}
