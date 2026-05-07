package xylo_datapacks.energy_manipulation.glyph.pin;

import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.valueType.GlyphValueType;

import java.lang.ref.WeakReference;

public class OutputPin {

    public OutputPin(WeakReference<GlyphInstance> owner) {
        this.owner = owner;
    }
    
    public WeakReference<GlyphInstance> owner;
    public GlyphValueType valueType;

    /** We keep a weak reference to the pin connected to this output pin, to allow navigating backward. */
    public WeakReference<InputPin> connectedPin;
}
