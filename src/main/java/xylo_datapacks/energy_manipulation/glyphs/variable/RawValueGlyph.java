package xylo_datapacks.energy_manipulation.glyphs.variable;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyphs.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.payload.GlyphGenericPayload;
import xylo_datapacks.energy_manipulation.glyphs.payload.GlyphPayload;
import xylo_datapacks.energy_manipulation.glyphs.pins.InputPinMode;
import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValue;

import java.util.Optional;

public class RawValueGlyph extends Glyph {

    public RawValueGlyph() {
        super();

        this.inputPinMode = InputPinMode.VALUE;
        outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType != null && valueType.hasValueSelector();
        };
    }

    @Override
    public GlyphPayload createPayload(GlyphInstance glyphInstance) {
        return new GlyphGenericPayload();
    }
    
    protected GlyphGenericPayload getPayload(GlyphInstance glyphInstance) {
        return (GlyphGenericPayload) glyphInstance.payload;
    }

    public Optional<GlyphValue> getPayloadValue(GlyphInstance glyphInstance) {
        if (!(getPayload(glyphInstance).content instanceof GlyphValue)) {
            EnergyManipulation.LOGGER.warn("Payload from instances of RawValueGlyph must contain a GlyphValue!");
            return Optional.empty();
        }
        
        return Optional.ofNullable((GlyphValue) getPayload(glyphInstance).content);
    }
    
    public void setPayloadValue(GlyphInstance glyphInstance, GlyphValue payloadValue) {
        if (!payloadValue.isOfType(glyphInstance.outputPin.valueType)) {
            EnergyManipulation.LOGGER.warn("Trying to assign payload of wrong type to RawValueGlyph!");
            return;
        }

        getPayload(glyphInstance).content = payloadValue;
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        if (getPayload(glyphInstance).content instanceof GlyphValue) {
            return getPayloadValue(glyphInstance).orElse(new GlyphValue());
        }
        
        EnergyManipulation.LOGGER.warn("Payload contained by instances of RawValueGlyph, must be of type GlyphValue!");
        return new GlyphValue();
    }
}
