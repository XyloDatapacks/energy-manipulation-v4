package xylo_datapacks.energy_manipulation.glyph.execution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record PersistentVariablesContainer(Map<String, @NonNull GlyphValue> variables) {

    public PersistentVariablesContainer(Map<String, @NonNull GlyphValue> variables) {
        this.variables = new LinkedHashMap<>();
        
        variables.forEach((name, value) -> {
            this.variables.put(name, value.copy());
        });
    }

    public PersistentVariablesContainer() {
        this(new LinkedHashMap<>());
    }
    
    public PersistentVariablesContainer(PersistentVariablesContainer other) {
        this(other.variables);
    }
    
    public void copyFrom(PersistentVariablesContainer other) {
        other.variables.forEach((name, value) -> {
            this.variables.put(name, value.copy());
        });
    }
    

    public static final Codec<PersistentVariablesContainer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, GlyphValue.CODEC).xmap(
                            // 1. Deserialization: Wrap the resulting map in a LinkedHashMap to preserve order
                            LinkedHashMap::new,
                            // 2. Serialization: DFU crashes on null map values. We filter them out here.
                            map -> map.entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            Map.Entry::getValue,
                                            (existing, replacement) -> existing,
                                            LinkedHashMap::new
                                    ))
                            ).fieldOf("variables").forGetter(varContainer -> (LinkedHashMap<String, GlyphValue>) varContainer.variables)
            ).apply(instance, PersistentVariablesContainer::new)
    );
}
