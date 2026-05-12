package xylo_datapacks.energy_manipulation.glyph.specialized.variable;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.Optional;

public class FromConversionGlyph extends Glyph {
    static public String TYPE_PIN = "type";
    static public String VALUE_PIN = "value";
    
    public FromConversionGlyph() {
        super();
        
        this.inputPinMode = InputPinMode.STANDARD;
        
        this.RegisterPinDefinition(TYPE_PIN, glyph -> true);
        
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
            this.getInputPin(glyphInstance, VALUE_PIN).ifPresent(inputPin -> {
                
                // newValueType is the payload from the raw value glyph connected to TYPE_PIN.
                GlyphInstance rawValueGlyphInstance = this.getInputPin(glyphInstance, TYPE_PIN).get().connectedGlyph;
                Optional<GlyphValue> classValue = ((RawValueGlyph) rawValueGlyphInstance.glyph).getPayloadValue(rawValueGlyphInstance);
                Optional<GlyphValueType> newValueType = classValue.flatMap(GlyphsRegistry.CLASS_VALUE_TYPE::getClassGlyphValue);
                
                if (newValueType.isPresent()) {
                    // Update value typ for VALUE_PIN.
                    GlyphValueType oldValueType = inputPin.valueType;
                    inputPin.valueType = newValueType.get();
                    
                    // If the value type of VALUE_PIN changed, reset the connection.
                    if (oldValueType != inputPin.valueType) {
                        this.resetConnection(glyphInstance, VALUE_PIN);
                    }
                }
            });
        }
    }
}
