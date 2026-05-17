package xylo_datapacks.energy_manipulation.glyph;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.payload.GlyphPayload;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.OutputPin;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GlyphInstance {

    public Glyph glyph;

    /** Set by glyph during instantiation. */
    public OutputPin outputPin;

    public List<InputPin> inputPins;

    /** Set by glyph during instantiation. */
    public GlyphPayload payload;
    
    public GlyphInstance(Glyph glyph) {
        this.glyph = glyph;
        this.inputPins = new ArrayList<>();
    }

    public static final Codec<GlyphInstance> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                // Serialize value type as an identifier
                Identifier.CODEC.fieldOf("value_type").forGetter(glyphInstance -> {
                    return Optional.ofNullable(GlyphsRegistry.VALUE_TYPE.getKey(glyphInstance.outputPin.valueType))
                            .orElse(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "null"));
                }),
                // Serialize instance as a compound tag
                CompoundTag.CODEC.fieldOf("instance").forGetter(GlyphUtils::serializeInstance)
        )
        .apply(instance, (identifier, compoundTag) -> {
            // Reconstruct during deserialization
            GlyphValueType valueType = GlyphsRegistry.VALUE_TYPE.getValue(identifier);
            if (valueType != null) {
                GlyphInstance glyphInstance = GlyphUtils.deserializeInstance(compoundTag, valueType).orElse(null);
                if (glyphInstance != null) {
                    return glyphInstance;
                }
            }

            // We failed to deserialize, return a default instance.
            return new Glyph().instantiate(GlyphsRegistry.VALUE_TYPE.getValue(identifier));
        });
    });
}