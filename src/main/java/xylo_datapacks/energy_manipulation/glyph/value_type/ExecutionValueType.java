package xylo_datapacks.energy_manipulation.glyph.value_type;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.Optional;

public class ExecutionValueType extends GlyphValueType {
    
    static class ExecutionGlyphValue extends BaseGlyphValue {
        public int value;
        
        ExecutionGlyphValue(GlyphValueType type, int value) {
            super(type);
            this.value = value;
        }

        @Override
        public String getDebugString() {
            return value > 0 ? "success (" + value + ")" : "failure";
        }
    }

    public GlyphValue makeExecutionGlyphValue(int value) {
        return new ExecutionGlyphValue(this, value);
    }
    
    public int getExecutionGlyphValue(GlyphValue glyphValue) {
        if (glyphValue instanceof ExecutionGlyphValue) {
            return ((ExecutionGlyphValue) glyphValue).value;
        }
        return 0;
    }

    @Override
    public GlyphValue MakeDefaulted() {
        return makeExecutionGlyphValue(0);
    }

    @Override
    public Optional<Tag> serialize(GlyphValue value) {
        return Codec.INT.encodeStart(NbtOps.INSTANCE, getExecutionGlyphValue(value))
                .resultOrPartial(err -> System.err.println("Failed to encode execution glyph value: " + err));
    }

    @Override
    public Optional<GlyphValue> deserialize(Tag value) {
        return Codec.INT.parse(NbtOps.INSTANCE, value)
                .resultOrPartial(err -> System.err.println("Failed to parse execution glyph value: " + err))
                .map(this::makeExecutionGlyphValue);
    }
}
