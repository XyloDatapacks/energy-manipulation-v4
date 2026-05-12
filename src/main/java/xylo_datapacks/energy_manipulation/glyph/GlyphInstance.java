package xylo_datapacks.energy_manipulation.glyph;

import xylo_datapacks.energy_manipulation.glyph.payload.GlyphPayload;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.OutputPin;

import java.util.ArrayList;
import java.util.List;

public class GlyphInstance {

    public Glyph glyph;

    /** Set by glyph during instantiation. */
    public OutputPin outputPin;

    public List<InputPin> inputPins;

    /** Set by glyph during instantiation. */
    public GlyphPayload payload;
    
    public GlyphInstance(Glyph glyph) {
        this.glyph = glyph;
        this.inputPins = new ArrayList<>();
    }
}
