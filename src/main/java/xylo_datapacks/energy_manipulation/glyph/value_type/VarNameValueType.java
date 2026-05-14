package xylo_datapacks.energy_manipulation.glyph.value_type;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;

import java.util.Optional;

public class VarNameValueType extends GlyphValueType {
    static public String NAME_NBT_KEY = "name";
    static public String TYPE_NBT_KEY = "type";
    
    public record VariableDescription(String name, GlyphValueType valueType) {}

    static class VarNameGlyphValue extends BaseGlyphValue {
        public String name;
        public GlyphValueType valueType;

        VarNameGlyphValue(String name, GlyphValueType valueType) {
            this.name = name;
            this.valueType = valueType;
        }

        @Override
        public GlyphValueType getValueType() {
            return GlyphsRegistry.VAR_NAME_VALUE_TYPE;
        }

        @Override
        public String getDebugString() {
            String valueTypeName = valueType != null ? valueType.getClass().getSimpleName() : "null";
            return name + " (" + valueTypeName + ")";
        }
    }

    public GlyphValue makeVarNameValue(String value, GlyphValueType valueType) {
        return new VarNameGlyphValue(value, valueType);
    }

    public GlyphValue makeVarNameValue(VariableDescription description) {
        return makeVarNameValue(description.name, description.valueType);
    }
    
    public String getVarName(GlyphValue glyphValue) {
        if (glyphValue instanceof VarNameGlyphValue varNameGlyphValue) {
            return varNameGlyphValue.name;
        }
        return "";
    }

    public Optional<GlyphValueType> getVarValueType(GlyphValue glyphValue) {
        if (glyphValue instanceof VarNameGlyphValue varNameGlyphValue) {
            return Optional.ofNullable(varNameGlyphValue.valueType);
        }
        return Optional.empty();
    }
    
    public VariableDescription getVarDescription(GlyphValue glyphValue) {
        if (glyphValue instanceof VarNameGlyphValue varNameGlyphValue) {
            return new VariableDescription(varNameGlyphValue.name, varNameGlyphValue.valueType);
        }
        return new VariableDescription("", null);
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeVarNameValue("", null);
    }

    @Override
    public ValueSelectorType getValueSelectorType() { return ValueSelectorType.MULTIPLE_CHOICE; }
    
    @Override
    public Optional<Tag> serialize(GlyphValue value) {
        if (value instanceof VarNameGlyphValue input) {
            CompoundTag output = new CompoundTag();

            output.putString(NAME_NBT_KEY, input.name);
            String valueTypeIdentifierString = Optional.ofNullable(GlyphsRegistry.VALUE_TYPE.getKey(input.valueType)).map(String::valueOf).orElse("null");
            output.putString(TYPE_NBT_KEY, valueTypeIdentifierString);

            return Optional.of(output);
        }   
        return Optional.empty();
    }

    @Override
    public Optional<GlyphValue> deserialize(Tag value) {
        if (value instanceof CompoundTag input) {
            VarNameGlyphValue output = (VarNameGlyphValue) MakeDefaulted();

            Optional<String> varName = input.getString(NAME_NBT_KEY);

            Optional<GlyphValueType> varValueType = input.getString(TYPE_NBT_KEY)
                    .map(Identifier::tryParse)
                    .map(GlyphsRegistry.VALUE_TYPE::getValue);

            varName.ifPresent(name -> output.name = name);
            varValueType.ifPresent(type -> output.valueType = type);
            return Optional.of(output);
        }
        return Optional.empty();
    }
}
