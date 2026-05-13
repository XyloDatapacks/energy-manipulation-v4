package xylo_datapacks.energy_manipulation.spell_editor;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.VarDefinitionGlyph;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.*;
import java.util.function.Consumer;

public class SpellEditor {
    static protected final SpellEditorRootGlyph ROOT_GLYPH = new SpellEditorRootGlyph();

    protected final SpellEditorRootGlyphInstance rootGlyphInstance;
    protected GlyphInstance originalGlyphInstance;
    protected GlyphInstance currentGlyphInstance;
    protected Map<GlyphInstance, SpellEditorVariable> registeredVariables = new LinkedHashMap<>();

    protected record SpellEditorVariable(String name, GlyphValueType valueType) {} 

    /*================================================================================================================*/
    // RootGlyph
    
    protected static class SpellEditorRootGlyphInstance extends GlyphInstance {

        public SpellEditorRootGlyphInstance(Glyph glyph) {
            super(glyph);
            this.onInstanceChangedCallback = changedInstance -> {};
        }

        Consumer<GlyphInstance> onInstanceChangedCallback;
    }

    protected static class SpellEditorRootGlyph extends Glyph {
        static public String INPUT_PIN = "Input";
        
        public SpellEditorRootGlyph() {
            super();

            this.inputPinMode = InputPinMode.STANDARD;
            this.RegisterPinDefinition(INPUT_PIN, glyph -> true);
        }

        @Override
        protected GlyphInstance createInstance_internal() {
            return new SpellEditorRootGlyphInstance(this);
        }

        @Override
        public void initializePins(GlyphInstance glyphInstance) {
            this.getInputPin(glyphInstance, INPUT_PIN).ifPresent(inputPin -> {
                inputPin.valueType = glyphInstance.outputPin.valueType;
            });
        }

        @Override
        public void onInputPinConnectionChanged(GlyphInstance glyphInstance, int pinIndex) {
            ((SpellEditorRootGlyphInstance) glyphInstance).onInstanceChangedCallback.accept(glyphInstance);
        }

        @Override
        public void onDescendantGlyphStateChanged(GlyphInstance glyphInstance, GlyphInstance descendantInstance, int pinIndex) {
            ((SpellEditorRootGlyphInstance) glyphInstance).onInstanceChangedCallback.accept(descendantInstance);
        }
    }

    // ~RootGlyph
    /*================================================================================================================*/

    public SpellEditor() {
        rootGlyphInstance = (SpellEditorRootGlyphInstance) ROOT_GLYPH.instantiate(GlyphsRegistry.EXECUTION_VALUE_TYPE);
        rootGlyphInstance.onInstanceChangedCallback = this::onInstanceChanged;
    }

    /*================================================================================================================*/
    // TrackedInstance
    
    public void reset() {
        this.currentGlyphInstance = null;
        this.originalGlyphInstance = null;
        GlyphUtils.resetConnection(rootGlyphInstance, SpellEditorRootGlyph.INPUT_PIN);
    }

    public void initialize(GlyphInstance glyphInstance) {
        this.currentGlyphInstance = glyphInstance;
        this.originalGlyphInstance = GlyphUtils.copyGlyphInstance(glyphInstance);
        GlyphUtils.connectGlyph(rootGlyphInstance, SpellEditorRootGlyph.INPUT_PIN, currentGlyphInstance);
    }
    
    public void saveChanges() {
        this.originalGlyphInstance = GlyphUtils.copyGlyphInstance(currentGlyphInstance);
    }
    
    public void restoreGlyphInstance() {
        currentGlyphInstance = GlyphUtils.copyGlyphInstance(originalGlyphInstance);
        GlyphUtils.connectGlyph(rootGlyphInstance, SpellEditorRootGlyph.INPUT_PIN, currentGlyphInstance);
    }

    public GlyphInstance getCurrentGlyphInstance() {
        return currentGlyphInstance;
    }

    public void onInstanceChanged(GlyphInstance changedInstance) {
        UpdateVariableRegistry();
    }

    // ~TrackedInstance
    /*================================================================================================================*/

    /*================================================================================================================*/
    // Variables
    
    public void registerVariable(GlyphInstance varDefGlyphInstance, String name, GlyphValueType valueType) {
        registeredVariables.put(varDefGlyphInstance, new SpellEditorVariable(name, valueType));
    }
    
    public void unregisterVariable(GlyphInstance varDefGlyphInstance) {
        registeredVariables.remove(varDefGlyphInstance);
    }
    
    public void UpdateVariableRegistry() {
        // Get all var definition instances.
        List<GlyphInstance> varDefinitionInstances = new ArrayList<>();
        rootGlyphInstance.glyph.getDescendants(rootGlyphInstance, varDefinitionInstances, instance -> {
            return instance.glyph == GlyphsRegistry.VAR_DEFINITION_GLYPH;
        });
        
        // Rebuild the variables' registry.
        registeredVariables.clear();
        varDefinitionInstances.forEach(varDefInstance -> {
            // Register the variable for this definition.
            GlyphValue varNameValue = ((VarDefinitionGlyph) varDefInstance.glyph).getVarNameValue(varDefInstance);
            String varName = GlyphsRegistry.VAR_NAME_VALUE_TYPE.getVarName(varNameValue);
            GlyphValueType varType = GlyphsRegistry.VAR_NAME_VALUE_TYPE.getVarValueType(varNameValue).orElse(null);
            
            registerVariable(varDefInstance, varName, varType);
        });

        // EnergyManipulation.LOGGER.info("Registered variables: {}", registeredVariables.values().stream().map(var -> var.name + " (" + Optional.ofNullable(var.valueType).map(Object::getClass).map(Class::getSimpleName).orElse("null") + ")").toList());
    }
    
    // ~Variables
    /*================================================================================================================*/
    
    public String printCompatibleGlyphs(GlyphInstance glyphInstance, String pinName) {
        int pinIndex = glyphInstance.glyph.getInputPinIndex(pinName);
        return printCompatibleGlyphs(glyphInstance, pinIndex);
    }

    public String printCompatibleGlyphs(GlyphInstance glyphInstance, int pinIndex) {
        String pinName = glyphInstance.glyph.getInputPinDefinition(pinIndex).get().pinName;

        StringBuilder output = new StringBuilder("Printing compatible Glyphs for pin [" + pinName + "] in Glyph [" + glyphInstance.glyph.getClass().getSimpleName() + "] \n");

        GlyphsRegistry.GLYPH.stream()
                .filter(glyph -> isCompatibleGlyph(glyphInstance, pinName, glyph))
                .forEach(glyph -> {
                    String entry = "- " + glyph.getClass().getSimpleName() + "\n";
                    output.append(entry);
                });

        EnergyManipulation.LOGGER.info(output.toString());
        return output.toString();
    }
    
    public void forEachCompatibleGlyph(GlyphInstance glyphInstance, int pinIndex, Consumer<Glyph> consumer) {
        GlyphsRegistry.GLYPH.stream()
                .filter(glyph -> isCompatibleGlyph(glyphInstance, pinIndex, glyph))
                .forEach(consumer);
    }
    
    public boolean isCompatibleGlyph(GlyphInstance glyphInstance, String pinName, Glyph glyphToTest) {
        int pinIndex = glyphInstance.glyph.getInputPinIndex(pinName);
        return isCompatibleGlyph(glyphInstance, pinIndex, glyphToTest);
    }

    public boolean isCompatibleGlyph(GlyphInstance glyphInstance, int pinIndex, Glyph glyphToTest) {
        Optional<InputPin> inputPin = glyphInstance.glyph.getInputPin(glyphInstance, pinIndex);
        Optional<InputPinDefinition> inputPinDefinition = glyphInstance.glyph.getInputPinDefinition(pinIndex);
        if (inputPin.isEmpty() || inputPinDefinition.isEmpty()) {
            return false;
        }

        // Check if the input pin value type is supported by the output pin of the glyph we are tring to connect
        if (inputPin.get().valueType == null || !glyphToTest.getOutputPinDefinition().valueTypeCompatibilityPredicate.test(inputPin.get().valueType)) {
            return false;
        }

        // Verify that the glyph we are trying to connect is acceptable for this input pin
        if (!inputPinDefinition.get().glyphFilter.test(glyphToTest)) {
            return false;
        }

        // Verify that this glyph is acceptable for the output pin of the glyph we are trying to connect
        if (!glyphToTest.getOutputPinDefinition().glyphFilter.test(glyphInstance.glyph)) {
            return false;
        }
        
        return true;
    }
    
}
