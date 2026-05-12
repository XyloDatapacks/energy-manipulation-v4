package xylo_datapacks.energy_manipulation.glyph.specialized.variable;

import net.minecraft.nbt.CompoundTag;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.payload.GlyphGenericPayload;
import xylo_datapacks.energy_manipulation.glyph.payload.GlyphPayload;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.Optional;

public class RawValueGlyph extends Glyph {

    public RawValueGlyph() {
        super();

        this.inputPinMode = InputPinMode.VALUE;
        this.outputPinDefinition.valueTypeCompatibilityPredicate = GlyphValueType::hasValueSelector;
    }

    @Override
    public GlyphPayload createPayload(GlyphInstance glyphInstance) {
        return new GlyphGenericPayload();
    }
    
    protected GlyphGenericPayload getPayload(GlyphInstance glyphInstance) {
        return (GlyphGenericPayload) glyphInstance.payload;
    }

    public Optional<GlyphValue> getPayloadValue(GlyphInstance glyphInstance) {
        Object PayloadContent = getPayload(glyphInstance).content;
        
        // If payload does not have content yet, return defaulted value.
        if (PayloadContent == null) {
            return Optional.of(glyphInstance.outputPin.valueType.MakeDefaulted());
        }
        
        if (PayloadContent instanceof GlyphValue payloadValue) {
            // If payload content is a GlyphValue, make sure that is the correct type, otherwise return empty.
            if (!payloadValue.isOfType(glyphInstance.outputPin.valueType)) {
                EnergyManipulation.LOGGER.warn("Payload from instances of RawValueGlyph must match the outputPin's ValueType!");
                return Optional.empty();
            }

            // If the content is the correct type of GlyphValue, return it.
            return Optional.of(payloadValue);
        }
        else {
            // The payload was not a GlyphValue, so return empty.
            EnergyManipulation.LOGGER.warn("Payload from instances of RawValueGlyph must contain a GlyphValue!");
            return Optional.empty();
        }
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
        return getPayloadValue(glyphInstance)
                .orElse(GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("RawValueGlyph could not return its payload as it is malformed!"));
    }

    @Override
    public Optional<CompoundTag> serializePayload(GlyphInstance glyphInstance) {
        CompoundTag output = new CompoundTag();
        getPayloadValue(glyphInstance).flatMap(glyphInstance.outputPin.valueType::serialize).ifPresent(valueTag -> {
            output.put("value", valueTag);
        });
        return Optional.of(output);
    }

    @Override
    public void deserializePayload(CompoundTag payloadCompound, GlyphInstance destination) {
        Optional.ofNullable(payloadCompound.get("value")).flatMap(destination.outputPin.valueType::deserialize).ifPresent(value -> {
            setPayloadValue(destination, value); 
        });
    }
}
