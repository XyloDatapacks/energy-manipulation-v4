package xylo_datapacks.energy_manipulation.glyphs.pins;

import xylo_datapacks.energy_manipulation.glyphs.Glyph;
import xylo_datapacks.energy_manipulation.glyphs.values.GlyphValueType;

import java.lang.ref.WeakReference;

public class OutputPin {

    WeakReference<Glyph> owner;
    GlyphValueType valueType;

    /** We keep a weak reference to the pin connected to this output pin, to allow navigating backward. */
    WeakReference<InputPin> connectedPin;
}
