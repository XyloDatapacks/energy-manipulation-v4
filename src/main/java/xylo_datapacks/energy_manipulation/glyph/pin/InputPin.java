package xylo_datapacks.energy_manipulation.glyph.pin;

import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.lang.ref.WeakReference;
import java.util.Optional;

public class InputPin {

    /** The GlyphInstance the pin belongs to. */
    public WeakReference<GlyphInstance> owner;

    /** The ValueType for this pin */
    public GlyphValueType valueType;

    /** We keep a reference to the connected glyph directly since they only have one output pin. 
     * This way we avoid having to make the output pin responsible for keeping a glyph in memory. */
    protected GlyphInstance connectedGlyph;
    
    public InputPin(WeakReference<GlyphInstance> owner) {
        this.owner = owner;
    }
    
    public Optional<GlyphInstance> getConnectedGlyph() {
        return Optional.ofNullable(this.connectedGlyph);
    }

    public void setConnectedGlyph(@Nullable GlyphInstance connectedGlyph) {
        // Detach the old glyph instance from this pin.
        if (this.connectedGlyph != null) {
            this.connectedGlyph.outputPin.setConnectedPin(null);
        }
        // Set the new glyph instance.
        this.connectedGlyph = connectedGlyph;
    }
}
