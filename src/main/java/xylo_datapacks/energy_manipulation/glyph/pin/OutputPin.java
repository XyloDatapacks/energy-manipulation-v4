package xylo_datapacks.energy_manipulation.glyph.pin;

import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.lang.ref.WeakReference;

public class OutputPin {

    /** The GlyphInstance the pin belongs to. */
    public WeakReference<GlyphInstance> owner;

    /** The ValueType for this pin */
    public GlyphValueType valueType;

    /** We keep a weak reference to the pin connected to this output pin, to allow navigating backward. */
    public WeakReference<InputPin> connectedPin;
    
    public OutputPin(WeakReference<GlyphInstance> owner) {
        this.owner = owner;
    }
}
