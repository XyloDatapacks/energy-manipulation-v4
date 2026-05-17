package xylo_datapacks.energy_manipulation.glyph.value_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;

import java.util.Optional;

public abstract class GlyphValue {
    
    public boolean isOfType(GlyphValueType glyphValueType) { return false; }

    public GlyphValueType getValueType() { return null; }
    
    public String getDebugString() { return ""; }
    
    public abstract GlyphValue copy();

    
    public static final Codec<GlyphValue> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                // Serialize value type as an identifier
                Identifier.CODEC.fieldOf("value_type").forGetter(glyphValue -> {
                    return Optional.ofNullable(GlyphsRegistry.VALUE_TYPE.getKey(glyphValue.getValueType()))
                            .orElse(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "null"));
                }),
                // Serialize value as a tag
                Codec.PASSTHROUGH.fieldOf("value").forGetter(glyphValue -> {
                    Tag nbtTag = glyphValue.getValueType().serialize(glyphValue).orElse(new CompoundTag());
                    return new Dynamic<>(NbtOps.INSTANCE, nbtTag);
                })
        )
        .apply(instance, (identifier, dynamic) -> {
            // Reconstruct during deserialization
            GlyphValueType valueType = GlyphsRegistry.VALUE_TYPE.getValue(identifier);
            if (valueType != null) {
                Tag nbtTag = (Tag) dynamic.convert(NbtOps.INSTANCE).getValue();
                GlyphValue value = valueType.deserialize(nbtTag).orElse(null);
                if (value != null) {
                    return value;
                }
            }

            // We failed to deserialize, return an error value.
            return GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("Failed to deserialize GlyphValue!");
        });
    });
}
