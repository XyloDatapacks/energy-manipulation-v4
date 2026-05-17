package xylo_datapacks.energy_manipulation.glyph.execution;

import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.LinkedHashMap;
import java.util.Map;

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
}
