package xylo_datapacks.energy_manipulation.glyphs;

import xylo_datapacks.energy_manipulation.glyphs.payload.GlyphPayload;
import xylo_datapacks.energy_manipulation.glyphs.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyphs.pin.OutputPin;

import java.util.ArrayList;
import java.util.List;

public class GlyphInstance {
    
    public GlyphInstance(Glyph glyph) {
        this.glyph = glyph;
        this.inputPins = new ArrayList<>();
    }
    
    public Glyph glyph;

    public OutputPin outputPin;
    
    public List<InputPin> inputPins;
    
    public GlyphPayload payload;
}
