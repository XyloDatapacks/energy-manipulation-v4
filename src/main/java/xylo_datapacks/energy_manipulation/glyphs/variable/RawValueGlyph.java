package xylo_datapacks.energy_manipulation.glyphs.variable;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyphs.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyphs.pins.InputPinMode;
import xylo_datapacks.energy_manipulation.glyphs.pins.OutputPin;
import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValue;

public class RawValueGlyph extends Glyph {

    public RawValueGlyph() {
        super();

        this.inputPinMode = InputPinMode.VALUE;
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        if (glyphInstance.payload.content instanceof GlyphValue) {
            return (GlyphValue) glyphInstance.payload.content;
        }
        
        EnergyManipulation.LOGGER.warn("Payload contained by instances of RawValueGlyph, must be of type GlyphValue!");
        return new GlyphValue();
    }
}
