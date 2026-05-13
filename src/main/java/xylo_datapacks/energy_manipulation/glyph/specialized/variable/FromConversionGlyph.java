package xylo_datapacks.energy_manipulation.glyph.specialized.variable;

import xylo_datapacks.energy_manipulation.glyph.*;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.StringConvertibleValueInterface;

import java.util.Optional;

public class FromConversionGlyph extends Glyph {
    static public String TYPE_PIN = "type";
    static public String VALUE_PIN = "value";
    
    public FromConversionGlyph() {
        super();
        
        this.inputPinMode = InputPinMode.STANDARD;
        
        this.RegisterPinDefinition(TYPE_PIN, glyph -> {
            return glyph == GlyphsRegistry.RAW_VALUE_GLYPH;
        });
        
        this.RegisterPinDefinition(VALUE_PIN, glyph -> {
            return !(glyph instanceof FromConversionGlyph);
        });
       
        this.outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            // TODO: only allow types that have any conversion instead of hardcoding.
            if (valueType == GlyphsRegistry.STRING_VALUE_TYPE || valueType == GlyphsRegistry.INT_VALUE_TYPE) {
                return true;
            }
            return false; 
        };

        this.editorData.bHasTypeDependentTexture = true;
        this.getInputPinEditorData(TYPE_PIN).ifPresent(pinEditorData -> pinEditorData.bHiddenInEditor = true);
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
    }

    @Override
    public void onInputPinGlyphPayloadChanged(GlyphInstance glyphInstance, int pinIndex) {
        // When the payload, of the raw value glyph, connected to TYPE_PIN, changes, we want to update the value type of VALUE_PIN.
        if (this.getInputPinDefinition(pinIndex).get().pinName.equals(TYPE_PIN)) {
            this.getInputPin(glyphInstance, VALUE_PIN).ifPresent(valuePin -> {
                
                // newValueType is the payload from the raw value glyph connected to TYPE_PIN.
                GlyphInstance rawValueGlyphInstance = this.getInputPin(glyphInstance, TYPE_PIN).flatMap(InputPin::getConnectedGlyph).get();
                Optional<GlyphValue> classValue = ((RawValueGlyph) rawValueGlyphInstance.glyph).getPayloadValue(rawValueGlyphInstance);
                Optional<GlyphValueType> newValueType = classValue.flatMap(GlyphsRegistry.CLASS_VALUE_TYPE::getClassGlyphValue);
                
                if (newValueType.isPresent()) {
                    // Update value typ for VALUE_PIN.
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
        GlyphValue typePinValue = this.evaluatePin(executionContext, glyphInstance, TYPE_PIN);
        GlyphValue valuePinValue = this.evaluatePin(executionContext, glyphInstance, VALUE_PIN);
        
        Optional<GlyphValueType> inputValueType = GlyphsRegistry.CLASS_VALUE_TYPE.getClassGlyphValue(typePinValue);
        if (inputValueType.isEmpty()) {
            return GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("FromConversionGlyph could not convert the input value since type is not a class!");
        }
        
        // If input and output value types are the same, no conversion is needed.
        if (glyphInstance.outputPin.valueType == inputValueType.get()) {
            return valuePinValue;
        }
        
        // If input is string, and ValueFromString is available for the output type, use it.
        if (inputValueType.get() == GlyphsRegistry.STRING_VALUE_TYPE) {
            if (glyphInstance.outputPin.valueType instanceof StringConvertibleValueInterface stringConvertibleOutputValueType) {
                String inputString = GlyphsRegistry.STRING_VALUE_TYPE.getStringGlyphValue(valuePinValue);
                return stringConvertibleOutputValueType.ValueFromString(inputString);
            }
        }

        // If output is string, and ValueToString is available for the input type, use it.
        if (glyphInstance.outputPin.valueType == GlyphsRegistry.STRING_VALUE_TYPE) {
            if (inputValueType.get() instanceof StringConvertibleValueInterface stringConvertibleInputValueType) {
                String inputString = stringConvertibleInputValueType.ValueToString(valuePinValue);
                return GlyphsRegistry.STRING_VALUE_TYPE.makeStringGlyphValue(inputString);
            }
        }

        return GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("FromConversionGlyph no conversion available from " + inputValueType.getClass().getSimpleName() + " to " +  glyphInstance.outputPin.valueType.getClass().getSimpleName());
    }
}
