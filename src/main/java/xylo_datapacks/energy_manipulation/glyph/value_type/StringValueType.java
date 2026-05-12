package xylo_datapacks.energy_manipulation.glyph.value_type;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.ComparableGlyphValueInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.StringConvertibleValueInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.StringGlyphValueInterface;

import java.util.Optional;

public class StringValueType extends GlyphValueType implements StringGlyphValueInterface, ComparableGlyphValueInterface, StringConvertibleValueInterface {
    
    class StringGlyphValue extends BaseGlyphValue {
        public String value;
        
        StringGlyphValue(String value) {
            this.value = value;
        }

        @Override
        public GlyphValueType getValueType() {
            return GlyphsRegistry.STRING_VALUE_TYPE;
        }

        @Override
        public String getDebugString() {
            return value;
        }
    }

    public GlyphValue makeStringGlyphValue(String value) {
        return new StringGlyphValue(value);
    }
    
    public String getStringGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof StringGlyphValue) {
            return ((StringGlyphValue) glyphValue).value;
        }
        return "";
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeStringGlyphValue("");
    }

    @Override
    public boolean hasOperations() {
        return true;
    }

    @Override
    public ValueSelectorType getValueSelectorType() { return ValueSelectorType.INPUT; }
    
    @Override
    public Optional<Tag> serialize(GlyphValue value) {
        return Codec.STRING.encodeStart(NbtOps.INSTANCE, getStringGlyphValue(value))
                .resultOrPartial(err -> System.err.println("Failed to encode string glyph value: " + err));
    }

    @Override
    public Optional<GlyphValue> deserialize(Tag value) {
        return Codec.STRING.parse(NbtOps.INSTANCE, value)
                .resultOrPartial(err -> System.err.println("Failed to parse string glyph value: " + err))
                .map(this::makeStringGlyphValue);
    }

    /*================================================================================================================*/
    // Interfaces

    @Override
    public GlyphValue equals(GlyphValue a, GlyphValue b) {
        String stringA = getStringGlyphValue(a);
        String stringB = getStringGlyphValue(b);
        return GlyphsRegistry.BOOL_VALUE_TYPE.makeBoolGlyphValue(stringA.equals(stringB));
    }

    @Override
    public GlyphValue concat(GlyphValue a, GlyphValue b) {
        String stringA = getStringGlyphValue(a);
        String stringB = getStringGlyphValue(b);
        return makeStringGlyphValue(stringA.concat(stringB));
    }

    @Override
    public GlyphValue length(GlyphValue value) {
        String stringValue = getStringGlyphValue(value);
        return GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(stringValue.length());
    }

    @Override
    public GlyphValue ValueFromString(String value) {
        return makeStringGlyphValue(value);
    }

    @Override
    public String ValueToString(GlyphValue value) {
        return getStringGlyphValue(value);
    }

    @Override
    public boolean isValidString(String value) {
        return true;
    }

    // ~Interfaces
    /*================================================================================================================*/
}
