package xylo_datapacks.energy_manipulation.spell_editor;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphUtils;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinMode;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.RawValueGlyph;
import xylo_datapacks.energy_manipulation.glyph.specialized.variable.VarDefinitionGlyph;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.glyph.value_type.VarNameValueType;

import java.util.*;
import java.util.function.Consumer;

public class SpellEditor {
    static protected final SpellEditorRootGlyph ROOT_GLYPH = new SpellEditorRootGlyph();

    protected final SpellEditorRootGlyphInstance rootGlyphInstance;
    protected GlyphInstance originalGlyphInstance;
    protected GlyphInstance currentGlyphInstance;
    protected Map<GlyphInstance, VarNameValueType.VariableDescription> registeredVariables = new LinkedHashMap<>();


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
        // If the changed instance is a variable definition with an already registered variable, update the variable users.
        if (changedInstance.glyph == GlyphsRegistry.VAR_DEFINITION_GLYPH && registeredVariables.containsKey(changedInstance)) {
            updateVariableUsers(changedInstance);
        }
        
        UpdateVariableRegistry();
    }

    // ~TrackedInstance
    /*================================================================================================================*/

    /*================================================================================================================*/
    // Variables
    
    public void registerVariable(GlyphInstance varDefGlyphInstance, String name, GlyphValueType valueType) {
        registeredVariables.put(varDefGlyphInstance, new VarNameValueType.VariableDescription(name, valueType));
    }
    
    public void unregisterVariable(GlyphInstance varDefGlyphInstance) {
        registeredVariables.remove(varDefGlyphInstance);
    }
    
    /** find the raw value glyphs, outputting a var name value, that use the old name, so we can update 
     * them to the new name. */
    public void updateVariableUsers(GlyphInstance varDefInstance) {
        // Get old variable description.
        VarNameValueType.VariableDescription oldVarDescription = registeredVariables.get(varDefInstance);
        // Get new variable description.
        GlyphValue defVarNameValue = ((VarDefinitionGlyph) varDefInstance.glyph).getVarNameValue(varDefInstance);
        VarNameValueType.VariableDescription newVarDescription = GlyphsRegistry.VAR_NAME_VALUE_TYPE.getVarDescription(defVarNameValue);

        //EnergyManipulation.LOGGER.warn("old var {} -> new var {} ", oldVarDescription, newVarDescription);
        
        // Cannot update the variable name if the value type changed.
        if (oldVarDescription.valueType() == newVarDescription.valueType()) {
            GlyphInstance scopeEnclosingInstance = getScopeEnclosingInstance(varDefInstance).orElse(null);

            if (scopeEnclosingInstance == null) {
                // The variable definition instance is detached.
                return;
            }

            //EnergyManipulation.LOGGER.warn("Found enclosing scope");

            // Get all raw value glyphs that output a var name value.
            List<GlyphInstance> varNameSelectorInstances = new ArrayList<>();
            scopeEnclosingInstance.glyph.getDescendants(scopeEnclosingInstance, varNameSelectorInstances, instance -> {
                return instance.glyph == GlyphsRegistry.RAW_VALUE_GLYPH && instance.outputPin.valueType == GlyphsRegistry.VAR_NAME_VALUE_TYPE;
            });
            
            //EnergyManipulation.LOGGER.warn("Found {} raw value glyphs", varNameSelectorInstances.size());

            // Filter found instances to only those that use the old variable name. Then update the variable name.
            varNameSelectorInstances.stream().forEach(rawValueInstance -> {
                VarNameValueType.VariableDescription instanceVarDescription = ((RawValueGlyph) rawValueInstance.glyph).getPayloadValue(rawValueInstance).map(GlyphsRegistry.VAR_NAME_VALUE_TYPE::getVarDescription).orElse(null);
                if (instanceVarDescription == null) {
                    return;
                }
                
                // Only update the variable name if the name WAS the same.
                if (!instanceVarDescription.name().equals(oldVarDescription.name())) {
                    return;
                }
                
                // Cannot update the variable name if the value type is different.
                if (instanceVarDescription.valueType() != newVarDescription.valueType()) {
                    return;
                }
                
                // Update the variable name.
                ((RawValueGlyph) rawValueInstance.glyph).setPayloadValue(
                        rawValueInstance,
                        GlyphsRegistry.VAR_NAME_VALUE_TYPE.makeVarNameValue(newVarDescription)
                );
            });
        }
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
    
    protected Optional<GlyphInstance> getScopeEnclosingInstance(GlyphInstance varDefInstance) {
        return varDefInstance.glyph.getClosestParent(varDefInstance, parent -> parent.glyph == GlyphsRegistry.PROGRAM_GLYPH);
    }
    
    public boolean isInScope(GlyphInstance varDefInstance, GlyphInstance varNameSelectorInstance) {
        // Get the enclosing scope of the variable definition instance.
        Optional<GlyphInstance> scopeEnclosingInstance = getScopeEnclosingInstance(varDefInstance);
        if (scopeEnclosingInstance.isEmpty()) {
            // The variable definition instance is detached.
            return false;
        }
        
        Optional<GlyphInstance> enclosingInstanceOrContextCutter = varNameSelectorInstance.glyph.getClosestParent(varNameSelectorInstance, parent -> {
            return parent == scopeEnclosingInstance.get() || parent.glyph == GlyphsRegistry.GENERATE_SHAPE_GLYPH;
        });
        
        // Can only be in scope if varNameSelectorInstance is a descendant of the enclosing scope, and there is no 
        // context cutter in between.
        return enclosingInstanceOrContextCutter.isPresent() && enclosingInstanceOrContextCutter.get() == scopeEnclosingInstance.get();   
    }

    public boolean isInScope(String varName, GlyphValueType varValueType, GlyphInstance varNameSelectorInstance) {
        GlyphInstance varDefInstance = registeredVariables.entrySet().stream()
                .filter(entry -> {
                    return entry.getValue().name().equals(varName) && entry.getValue().valueType() == varValueType;
                })
                .map(Map.Entry::getKey).findFirst().orElse(null);
        
        return varDefInstance != null && isInScope(varDefInstance, varNameSelectorInstance);
    }
    
    public Map<GlyphInstance, VarNameValueType.VariableDescription> getInScopeVariables(GlyphInstance varNameSelectorInstance) {
        Map<GlyphInstance, VarNameValueType.VariableDescription> output = new LinkedHashMap<>();
        
        registeredVariables.forEach((varDefInstance, editorVar) -> {
            if (isInScope(varDefInstance, varNameSelectorInstance)) {
                output.put(varDefInstance, editorVar);
            }
        });
        
        return output;    
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
