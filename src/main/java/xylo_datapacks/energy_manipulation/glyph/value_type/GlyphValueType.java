package xylo_datapacks.energy_manipulation.glyph.value_type;

import net.minecraft.nbt.Tag;

import java.util.Optional;

public abstract class GlyphValueType {
    
    abstract class BaseGlyphValue extends GlyphValue {

        @Override
        public final boolean isOfType(GlyphValueType glyphValueType) {
            return glyphValueType == getValueType();
        }
    }
    
    public abstract GlyphValue MakeDefaulted();

    public boolean hasOperations() { return false; }
    
    public ValueSelectorType getValueSelectorType() { return ValueSelectorType.NONE; }

    public boolean hasValueSelector() { return getValueSelectorType() != ValueSelectorType.NONE; }

    public Optional<Tag> serialize(GlyphValue value) {
        return Optional.empty();
    }

    public Optional<GlyphValue> deserialize(Tag value) {
        return Optional.empty();
    }
}
