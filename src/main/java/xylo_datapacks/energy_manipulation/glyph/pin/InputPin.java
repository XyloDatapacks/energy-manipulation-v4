package xylo_datapacks.energy_manipulation.glyph.pin;

import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.lang.ref.WeakReference;

public class InputPin {

    /** The GlyphInstance the pin belongs to. */
    public WeakReference<GlyphInstance> owner;

    /** The ValueType for this pin */
    public GlyphValueType valueType;

    /** We keep a reference to the connected glyph directly since they only have one output pin. 
     * This way we avoid having to make the output pin responsible for keeping a glyph in memory. */
    public GlyphInstance connectedGlyph;
    
    public InputPin(WeakReference<GlyphInstance> owner) {
        this.owner = owner;
    }
}
