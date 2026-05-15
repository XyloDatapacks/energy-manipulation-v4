package xylo_datapacks.energy_manipulation.glyph.value_type;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;

import java.util.Optional;

public class ClassValueType extends GlyphValueType {

    static class ClassGlyphValue extends BaseGlyphValue {
        public GlyphValueType value;

        ClassGlyphValue(GlyphValueType type, GlyphValueType value) {
            super(type);
            this.value = value;
        }

        @Override
        public String getDebugString() {
            if (value == null) {
                return "None";
            }
            return value.getClass().getSimpleName();
        }
    }

    public GlyphValue makeClassGlyphValue(GlyphValueType value) {
        return new ClassGlyphValue(this, value);
    }

    public Optional<GlyphValueType> getClassGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof ClassGlyphValue classGlyphValue) {
            return Optional.ofNullable(classGlyphValue.value);
        }
        return Optional.empty();
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeClassGlyphValue(null);
    }

    @Override
    public ValueSelectorType getValueSelectorType() { return ValueSelectorType.MULTIPLE_CHOICE; }

    @Override
    public Optional<Tag> serialize(GlyphValue value) {
        String valueTypeIdentifierString = getClassGlyphValue(value).map(GlyphsRegistry.VALUE_TYPE::getKey).map(String::valueOf).orElse("null");
        return Codec.STRING.encodeStart(NbtOps.INSTANCE, valueTypeIdentifierString)
                .resultOrPartial(err -> System.err.println("Failed to encode class glyph value: " + err));
    }

    @Override
    public Optional<GlyphValue> deserialize(Tag value) {
        return Codec.STRING.parse(NbtOps.INSTANCE, value)
                .resultOrPartial(err -> System.err.println("Failed to parse class glyph value: " + err))
                .map(Identifier::tryParse)
                .map(GlyphsRegistry.VALUE_TYPE::getValue)
                .map(this::makeClassGlyphValue);
    }
}
