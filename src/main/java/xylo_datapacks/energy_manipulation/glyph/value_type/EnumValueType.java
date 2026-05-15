package xylo_datapacks.energy_manipulation.glyph.value_type;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.Optional;

public class EnumValueType<T extends Enum<T>> extends GlyphValueType {
    protected final Class<T> enumClass;
    protected final T defaultValue;
    protected final boolean canBeVariable;
    
    static class EnumGlyphValue<T extends Enum<T>> extends BaseGlyphValue {
        public T value;
        
        EnumGlyphValue(EnumValueType<T> type, T value) {
            super(type);
            this.value = value;
        }

        @Override
        public String getDebugString() {
            return value.name();
        }
    }

    public EnumValueType(Class<T> enumClass) {
        this(enumClass, false);
    }

    public EnumValueType(Class<T> enumClass, boolean canBeVariable) {
        this.enumClass = enumClass;
        this.canBeVariable = canBeVariable;
        
        // Set default value.
        T[] constants = this.enumClass.getEnumConstants();
        if (constants != null && constants.length > 0) {
            defaultValue = constants[0];
        }
        else {
            throw new IllegalStateException("EnumValueType: Enum " + enumClass.getName() + " has no constants!");
        }
    }

    public GlyphValue makeEnumGlyphValue(T value) {
        return new EnumGlyphValue<T>(this, value);
    }

    public GlyphValue makeEnumGlyphValue(String valueName) {
        return makeEnumGlyphValue(Enum.valueOf(enumClass, valueName));
    }
    
    public T getEnumGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof EnumGlyphValue<?> enumGlyphValue) {
            if (enumGlyphValue.type == this) {
                return enumClass.cast(enumGlyphValue.value);
            }
        }
        return defaultValue;
    }
    
    public Class<T> getEnumClass() {
        return enumClass;
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeEnumGlyphValue(defaultValue);
    }

    @Override
    public boolean canBeVariable() {
        return canBeVariable;
    }

    @Override
    public ValueSelectorType getValueSelectorType() { return ValueSelectorType.MULTIPLE_CHOICE; }
    
    @Override
    public Optional<Tag> serialize(GlyphValue value) {
        return Codec.STRING.encodeStart(NbtOps.INSTANCE, getEnumGlyphValue(value).name())
                .resultOrPartial(err -> System.err.println("Failed to encode Enum glyph value: " + err));
    }

    @Override
    public Optional<GlyphValue> deserialize(Tag value) {
        return Codec.STRING.parse(NbtOps.INSTANCE, value)
                .resultOrPartial(err -> System.err.println("Failed to parse Enum glyph value: " + err))
                .map(enumValueName -> Enum.valueOf(enumClass, enumValueName))
                .map(this::makeEnumGlyphValue);
    }
}
