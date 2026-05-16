package xylo_datapacks.energy_manipulation.glyph.execution;

import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;

import java.util.LinkedHashMap;
import java.util.Map;

public record PersistentVariablesContainer(Map<String, @Nullable GlyphValue> variables) {

    public PersistentVariablesContainer() {
        this(new LinkedHashMap<>());
    }
    
    public PersistentVariablesContainer(PersistentVariablesContainer other) {
        this();
        this.copyFrom(other);
    }
    
    public void copyFrom(PersistentVariablesContainer other) {
        other.variables.forEach((name, value) -> {
            EnergyManipulation.LOGGER.warn("copying persistent variable: {} -> {}", name, value != null ? value.getDebugString() : "null");
            this.variables.put(name, value != null ? value.copy() : null);
        });
    }
}
