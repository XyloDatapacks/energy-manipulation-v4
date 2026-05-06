package xylo_datapacks.energy_manipulation.glyphs;

import xylo_datapacks.energy_manipulation.glyphs.pins.InputPin;
import xylo_datapacks.energy_manipulation.glyphs.pins.OutputPin;

import java.util.List;

public class GlyphInstance {
    
    public GlyphInstance() {
        
    }
    
    Glyph glyph;

    OutputPin outputPin;
    
    List<InputPin> inputPins;
    
    Object payload;
}
