package xylo_datapacks.energy_manipulation.glyphs.valueType;

import org.lwjgl.system.ffm.mapping.Mapping;
import xylo_datapacks.energy_manipulation.glyphs.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyphs.valueType.interfaces.ComparableGlyphValueInterface;
import xylo_datapacks.energy_manipulation.glyphs.valueType.interfaces.NumericGlyphValueInterface;
import xylo_datapacks.energy_manipulation.glyphs.valueType.interfaces.SortableGlyphValueInterface;

public class IntValueType extends GlyphValueType implements NumericGlyphValueInterface, ComparableGlyphValueInterface, SortableGlyphValueInterface {
    class IntGlyphValue extends BaseGlyphValue {
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

        public int value;
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

    // ~Interfaces
    /*================================================================================================================*/
}
