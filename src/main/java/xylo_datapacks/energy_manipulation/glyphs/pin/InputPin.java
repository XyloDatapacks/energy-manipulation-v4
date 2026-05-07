package xylo_datapacks.energy_manipulation.glyphs.pin;

import xylo_datapacks.energy_manipulation.glyphs.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValueType;

import java.lang.ref.WeakReference;

public class InputPin {

    public InputPin(WeakReference<GlyphInstance> owner) {
        this.owner = owner;
    }

    public WeakReference<GlyphInstance> owner;
    public GlyphValueType valueType;

    /** We keep a reference to the connected glyph directly since they only have one output pin. 
     * This way we avoid having to make the output pin responsible for keeping a glyph in memory. */
    public GlyphInstance connectedGlyph;
}
