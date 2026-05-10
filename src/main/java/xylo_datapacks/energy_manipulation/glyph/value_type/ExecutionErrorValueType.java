package xylo_datapacks.energy_manipulation.glyph.value_type;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;

import java.util.Optional;

public class ExecutionErrorValueType extends GlyphValueType {
   
    class ExecutionErrorGlyphValue extends BaseGlyphValue {
        public String message;
        
        ExecutionErrorGlyphValue(String message) {
            this.message = message;
        }

        @Override
        public GlyphValueType getValueType() {
            return GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE;
        }

        @Override
        public String getDebugString() {
            return message;
        }
    }

    public GlyphValue makeExecutionErrorGlyphValue(String value) {
        return new ExecutionErrorGlyphValue(value);
    }

    public String getExecutionErrorGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof ExecutionErrorGlyphValue) {
            return ((ExecutionErrorGlyphValue) glyphValue).message;
        }
        return "";
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeExecutionErrorGlyphValue("Unexpected Execution Error");
    }

    @Override
    public Optional<Tag> serialize(GlyphValue value) {
        return Codec.STRING.encodeStart(NbtOps.INSTANCE, getExecutionErrorGlyphValue(value))
                .resultOrPartial(err -> System.err.println("Failed to encode execution error glyph value: " + err));
    }

    @Override
    public Optional<GlyphValue> deserialize(Tag value) {
        return Codec.STRING.parse(NbtOps.INSTANCE, value)
                .resultOrPartial(err -> System.err.println("Failed to parse execution error glyph value: " + err))
                .map(this::makeExecutionErrorGlyphValue);
    }
}