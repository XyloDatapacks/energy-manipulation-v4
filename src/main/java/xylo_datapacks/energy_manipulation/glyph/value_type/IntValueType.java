package xylo_datapacks.energy_manipulation.glyph.value_type;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.ComparableGlyphValueInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.NumericGlyphValueInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.SortableGlyphValueInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.value_interface.StringConvertibleValueInterface;

import java.util.Optional;

public class IntValueType extends GlyphValueType implements NumericGlyphValueInterface, ComparableGlyphValueInterface, SortableGlyphValueInterface, StringConvertibleValueInterface {
   
    class IntGlyphValue extends BaseGlyphValue {
        public int value;
        
        IntGlyphValue(int value) {
            this.value = value;
        }

        @Override
        public GlyphValueType getValueType() {
            return GlyphsRegistry.INT_VALUE_TYPE;
        }

        @Override
        public String getDebugString() {
            return String.valueOf(value);
        }
    }

    public GlyphValue makeIntGlyphValue(int value) {
        return new IntGlyphValue(value);
    }
    
    public int getIntGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof IntGlyphValue) {
            return ((IntGlyphValue) glyphValue).value;
        }
        return 0;
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeIntGlyphValue(0);
    }

    @Override
    public boolean hasOperations() {
        return true;
    }

    @Override
    public boolean hasValueSelector() { return true; }

    @Override
    public Optional<Tag> serialize(GlyphValue value) {
        return Codec.INT.encodeStart(NbtOps.INSTANCE, getIntGlyphValue(value))
                .resultOrPartial(err -> System.err.println("Failed to encode int glyph value: " + err));
    }

    @Override
    public Optional<GlyphValue> deserialize(Tag value) {
        return Codec.INT.parse(NbtOps.INSTANCE, value)
                .resultOrPartial(err -> System.err.println("Failed to parse int glyph value: " + err))
                .map(this::makeIntGlyphValue);
    }

    /*================================================================================================================*/
    // Interfaces
    
    @Override
    public GlyphValue add(GlyphValue a, GlyphValue b) {
        int numberA = getIntGlyphValue(a);
        int numberB = getIntGlyphValue(b);
        return GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(numberA + numberB);
    }

    @Override
    public GlyphValue subtract(GlyphValue a, GlyphValue b) {
        int numberA = getIntGlyphValue(a);
        int numberB = getIntGlyphValue(b);
        return GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(numberA - numberB);
    }

    @Override
    public GlyphValue multiply(GlyphValue a, GlyphValue b) {
        int numberA = getIntGlyphValue(a);
        int numberB = getIntGlyphValue(b);
        return GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(numberA * numberB);
    }

    @Override
    public GlyphValue divide(GlyphValue a, GlyphValue b) {
        int numberA = getIntGlyphValue(a);
        int numberB = getIntGlyphValue(b);
        return GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(numberA / numberB);
    }

    @Override
    public GlyphValue power(GlyphValue value, GlyphValue exponent) {
        int numberValue = getIntGlyphValue(value);
        int numberExponent = getIntGlyphValue(exponent);
        return GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue(Math.powExact(numberValue, numberExponent));
    }

    @Override
    public GlyphValue sqrt(GlyphValue value) {
        int numberValue = getIntGlyphValue(value);
        return GlyphsRegistry.INT_VALUE_TYPE.makeIntGlyphValue((int) Math.sqrt(numberValue)); // casting to int instead of flooring to round toward zero
    }

    @Override
    public GlyphValue equals(GlyphValue a, GlyphValue b) {
        int numberA = getIntGlyphValue(a);
        int numberB = getIntGlyphValue(b);
        return GlyphsRegistry.BOOL_VALUE_TYPE.makeBoolGlyphValue(numberA == numberB);
    }

    @Override
    public GlyphValue lessThen(GlyphValue a, GlyphValue b) {
        int numberA = getIntGlyphValue(a);
        int numberB = getIntGlyphValue(b);
        return GlyphsRegistry.BOOL_VALUE_TYPE.makeBoolGlyphValue(numberA < numberB);
    }

    @Override
    public GlyphValue greaterThan(GlyphValue a, GlyphValue b) {
        int numberA = getIntGlyphValue(a);
        int numberB = getIntGlyphValue(b);
        return GlyphsRegistry.BOOL_VALUE_TYPE.makeBoolGlyphValue(numberA > numberB);
    }

    @Override
    public GlyphValue ValueFromString(String value) {
        return makeIntGlyphValue(Integer.parseInt(value));
    }

    @Override
    public String ValueToString(GlyphValue value) {
        return getIntGlyphValue(value) + "";
    }

    // ~Interfaces
    /*================================================================================================================*/
}
