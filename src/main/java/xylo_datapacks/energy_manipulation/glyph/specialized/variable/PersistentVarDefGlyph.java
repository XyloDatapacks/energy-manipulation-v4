package xylo_datapacks.energy_manipulation.glyph.specialized.variable;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.VarNameValueType;

public class PersistentVarDefGlyph extends Glyph {
    static public String VAR_DEF_PIN = "variable_definition";

    public PersistentVarDefGlyph() {
        super();

        this.inputPinMode = InputPinMode.STANDARD;
        this.RegisterPinDefinition(VAR_DEF_PIN, glyph -> {
            return glyph == GlyphsRegistry.VAR_DEFINITION_GLYPH;
        });
        
        this.outputPinDefinition.valueTypeCompatibilityPredicate = valueType -> {
            return valueType == GlyphsRegistry.EXECUTION_VALUE_TYPE;
        };
        
        this.getInputPinEditorData(VAR_DEF_PIN).ifPresent(pinEditorData -> pinEditorData.bHiddenInEditor = true);
    }

    @Override
    public void initializePins(GlyphInstance glyphInstance) {
        this.getInputPin(glyphInstance, VAR_DEF_PIN).ifPresent(inputPin -> {
            inputPin.valueType = GlyphsRegistry.EXECUTION_VALUE_TYPE;

            // Initialize connection to variable definition glyph
            GlyphUtils.connectNewGlyph(glyphInstance, VAR_DEF_PIN, GlyphsRegistry.VAR_DEFINITION_GLYPH);
        });
    }

    @Override
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        String varName = this.getInputPin(glyphInstance, VAR_DEF_PIN).flatMap(InputPin::getConnectedGlyph)
                .map(connectGlyph -> ((VarDefinitionGlyph) connectGlyph.glyph).getVarNameValue(connectGlyph))
                .map(GlyphsRegistry.VAR_NAME_VALUE_TYPE::getVarName)
                .orElseThrow(() -> new RuntimeException("Cannot execute PersistentVarDefGlyph without a connected VarDefinitionGlyph!"));

        // If there is a valid value for this persistent variable, we do not need to define it.
        GlyphValue existingVariable = executionContext.getCachedPersistentVariable(varName).orElse(null);
        if (existingVariable != null) {
            return GlyphsRegistry.EXECUTION_VALUE_TYPE.makeExecutionGlyphValue(1);
        }

        // Otherwise, register the persistent variable and evaluate the variable definition.
        executionContext.registerPersistentVariable(varName);
        return this.evaluatePin(executionContext, glyphInstance, VAR_DEF_PIN);
    }
    
}
