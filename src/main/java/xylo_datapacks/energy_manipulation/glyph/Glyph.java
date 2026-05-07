package xylo_datapacks.energy_manipulation.glyph;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.payload.GlyphPayload;
import xylo_datapacks.energy_manipulation.glyph.pin.*;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Glyph {

    public OutputPinDefinition outputPinDefinition;
    public List<InputPinDefinition> inputPinDefinitions;
    public InputPinMode inputPinMode;
    
    public Glyph() {
        this.outputPinDefinition = new OutputPinDefinition();
        this.inputPinDefinitions = new ArrayList<>();
        this.inputPinMode = InputPinMode.NONE;
    }

    public void RegisterPinDefinition(String pinName, Predicate<Glyph> nodeFilter) {
        if (inputPinMode == InputPinMode.NONE) {
            EnergyManipulation.LOGGER.warn("Cannot register a pin definition if inputPinMode is not set for this Glyph!");
            return;
        }

        if (inputPinMode == InputPinMode.VALUE) {
            EnergyManipulation.LOGGER.warn("Trying to register a Glyph pin, while no pin is expected!");
            return;
        }

        if (inputPinMode == InputPinMode.ARRAY && !inputPinDefinitions.isEmpty()) {
            EnergyManipulation.LOGGER.warn("Glyphs with input pin arrays can only register one input pin definition!");
            return;
        }

        InputPinDefinition inputPinDefinition = new InputPinDefinition(pinName);
        inputPinDefinition.nodeFilter = nodeFilter;

        inputPinDefinitions.add(inputPinDefinition);
    }
    
    /*================================================================================================================*/
    // Instantiation
    
    public GlyphInstance instantiate(GlyphValueType outputValueType) {
        
        // do not allow creation if we cannot support output value type
        if (outputValueType == null || !outputPinDefinition.valueTypeCompatibilityPredicate.test(outputValueType)) {
            EnergyManipulation.LOGGER.warn("Trying to instantiate a GlyphInstance with a non supported output pin type!");
            return null;
        }

        // Create new instance
        GlyphInstance glyphInstance = new GlyphInstance(this);

        // Create input pins
        if (inputPinMode == InputPinMode.STANDARD) {
            inputPinDefinitions.forEach(inputPinDefinition -> {
                InputPin newInputPin = new InputPin(new WeakReference<>(glyphInstance));
                glyphInstance.inputPins.add(newInputPin);
            });
        }
        
        // Create output pin and set value type
        glyphInstance.outputPin = new OutputPin(new WeakReference<>(glyphInstance));
        glyphInstance.outputPin.valueType = outputValueType;
        
        // Custom initialization for pins
        initializePins(glyphInstance);
        refreshPins(glyphInstance);
        
        // Create payload
        glyphInstance.payload = createPayload(glyphInstance);
        initializePayload(glyphInstance);
        
        return glyphInstance;
    }
    
    public void initializePins(GlyphInstance glyphInstance) {}

    /** Called every time a pin connection changes. */
    public void refreshPins(GlyphInstance glyphInstance) {}

    public GlyphPayload createPayload(GlyphInstance glyphInstance) {
        return null;
    }

    public void initializePayload(GlyphInstance glyphInstance) {}

    // ~Instantiation
    /*================================================================================================================*/

    /*================================================================================================================*/
    // PinManagement
    
    public boolean hasInputPins(GlyphInstance glyphInstance) {
        return (inputPinMode == InputPinMode.ARRAY || inputPinMode == InputPinMode.STANDARD) && !glyphInstance.inputPins.isEmpty();
    }

    public int getInputPinIndex(String pinName) {
        for (int i = 0; i < inputPinDefinitions.size(); i++) {
            if (inputPinDefinitions.get(i).pinName.equals(pinName)) {
                return i;
            }
        }
        return -1; // Not found
    }
    
    public Optional<InputPin> getInputPin(GlyphInstance glyphInstance, String pinName) {
        int pinIndex = getInputPinIndex(pinName);
        return getInputPin(glyphInstance, pinIndex);
    }

    public Optional<InputPin> getInputPin(GlyphInstance glyphInstance, int pinIndex) {
         if (pinIndex >= 0 && pinIndex < glyphInstance.inputPins.size()) {
            return Optional.ofNullable(glyphInstance.inputPins.get(pinIndex));
         }
         return Optional.empty();
    }

    public Optional<InputPinDefinition> getInputPinDefinition(String pinName) {
        int pinIndex = getInputPinIndex(pinName);
        return getInputPinDefinition(pinIndex);
    }

    public Optional<InputPinDefinition> getInputPinDefinition(int pinIndex) {
        if (inputPinMode != InputPinMode.ARRAY) {
            pinIndex = 0;
        }
        
        if (pinIndex >= 0 && pinIndex < inputPinDefinitions.size()) {
            return Optional.ofNullable(inputPinDefinitions.get(pinIndex));
        }
        return Optional.empty();
    }
    
    public void addPin(GlyphInstance glyphInstance) {
        if (inputPinMode != InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("Cannot add pins if inputPinMode is not ARRAY!");
            return;
        }

        InputPin newInputPin = new InputPin(new WeakReference<>(glyphInstance));
        glyphInstance.inputPins.add(newInputPin);
        refreshPins(glyphInstance);
    }

    public void removePin(GlyphInstance glyphInstance, int index) {
        if (inputPinMode != InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("Cannot remove pins if inputPinMode is not ARRAY!");
            return;
        }

        glyphInstance.inputPins.remove(index);
        refreshPins(glyphInstance);
    }

    public void insertPin(GlyphInstance glyphInstance, int index) {
        if (inputPinMode != InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("Cannot insert pins if inputPinMode is not ARRAY!");
            return;
        }

        InputPin newInputPin = new InputPin(new WeakReference<>(glyphInstance));
        glyphInstance.inputPins.add(index, newInputPin);
        refreshPins(glyphInstance);
    }

    // ~PinManagement
    /*================================================================================================================*/

    /*================================================================================================================*/
    // Connections
    
    protected boolean canConnectToPin_Internal(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        Optional<InputPin> inputPin = getInputPin(glyphInstance, pinIndex);
        Optional<InputPinDefinition> inputPinDefinition = getInputPinDefinition(pinIndex);
        if (inputPin.isEmpty() || inputPinDefinition.isEmpty()) {
            return false;
        }
        
        // Check for matching value type in input pin and output pin 
        if (inputPin.get().valueType == null || (inputPin.get().valueType != glyphToConnect.outputPin.valueType)) {
            return false;
        }
        
        // Verify that the glyph we are trying to connect is acceptable for this input pin
        if (!inputPinDefinition.get().nodeFilter.test(glyphToConnect.glyph)) {
            return false;
        }
            
        return true;
    }

    public boolean connectGlyph(GlyphInstance glyphInstance, String pinName, GlyphInstance glyphToConnect) {
        if (inputPinMode != InputPinMode.STANDARD) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance by pinName if inputPinMode is not STANDARD!");
            return false;
        }
        
        int pinIndex = getInputPinIndex(pinName);
        return connectGlyph_Internal(glyphInstance, pinIndex, glyphToConnect);
    }

    public boolean connectGlyph(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        if (inputPinMode != InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance by pinIndex if inputPinMode is not ARRAY!");
            return false;
        }
        
        return connectGlyph_Internal(glyphInstance, pinIndex, glyphToConnect);
    }

    protected boolean connectGlyph_Internal(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        if (!hasInputPins(glyphInstance)) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance to one with no input pins!");
            return false;
        }
        
        if (!(pinIndex >= 0 && pinIndex < glyphInstance.inputPins.size())) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance to a non existent input pin!");
            return false;
        }
        
        if (!canConnectToPin_Internal(glyphInstance, pinIndex, glyphToConnect)) {
            EnergyManipulation.LOGGER.warn("Cannot connect GlyphInstance to specified input pin, as they are not compatible!");
            return false;
        }
        
        InputPin targetPin = glyphInstance.inputPins.get(pinIndex);
        targetPin.connectedGlyph = glyphToConnect;
        glyphToConnect.outputPin.connectedPin = new WeakReference<>(targetPin);
        
        refreshPins(glyphInstance);

        OnConnected(glyphToConnect);
        return true;
    }
    
    public void OnConnected(GlyphInstance glyphInstance) {
        refreshPins(glyphInstance);
    }
    
    // ~Connections
    /*================================================================================================================*/

    /*================================================================================================================*/
    // Execution
    
    public static GlyphValue executeStatic(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        return glyphInstance.glyph.execute(executionContext, glyphInstance); 
    }
    
    /** @return an object derived from GlyphValue. It must NEVER be null, instead use GlyphValue or ExecutionErrorGlyphValue in case of exceptions */
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        return new GlyphValue();
    }

    public GlyphValue evaluatePin(ExecutionContext executionContext, GlyphInstance glyphInstance, String pinName) {
        Optional<InputPin> targetPin = getInputPin(glyphInstance, pinName);
        return evaluatePin_Internal(executionContext, glyphInstance, targetPin.orElse(null));
    }

    public GlyphValue evaluatePin(ExecutionContext executionContext, GlyphInstance glyphInstance, int pinIndex) {
        Optional<InputPin> targetPin = getInputPin(glyphInstance, pinIndex);
        return evaluatePin_Internal(executionContext, glyphInstance, targetPin.orElse(null));
    }

    protected GlyphValue evaluatePin_Internal(ExecutionContext executionContext, GlyphInstance glyphInstance, InputPin targetPin) {
        if (targetPin == null || targetPin.connectedGlyph == null) {
            return new GlyphValue();
        }

        GlyphInstance instanceAtPin = targetPin.connectedGlyph;
        GlyphValue glyphValue = instanceAtPin.glyph.execute(executionContext, instanceAtPin);

        if (glyphValue == null) {
            EnergyManipulation.LOGGER.warn("Evaluation of glyph [{}] connected to a pin of [{}] resulted in null GlyphValue. This behaviour is unsupported!", instanceAtPin.glyph.getClass().toString(), glyphInstance.glyph.getClass().toString());
            return new GlyphValue();
        }

        if (glyphValue.isOfType(GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE)) {
            EnergyManipulation.LOGGER.warn("Evaluation of glyph [{}] connected to a pin of [{}] resulted in the following error: {}", instanceAtPin.glyph.getClass().toString(), glyphInstance.glyph.getClass().toString(), glyphValue.getDebugString());
        }
        else if (!glyphValue.isOfType(targetPin.valueType)) {
            EnergyManipulation.LOGGER.warn("Evaluation of glyph [{}] connected to a pin of [{}] resulted in incompatible value type! Make sure \"execute\" method returns the same value type as the instance's outputPin.", instanceAtPin.glyph.getClass().toString(), glyphInstance.glyph.getClass().toString());
        }
        
        return glyphValue;
    }

    // ~Execution
    /*================================================================================================================*/

    /*================================================================================================================*/
    // StaticHelpers
    
    /** @param callback consumer passing as parameter the newly created GlyphInstance. */
    public static boolean connectNewGlyphWithCallbackStatic(GlyphInstance glyphInstance, String pinName, Glyph glyphToCreate, Consumer<GlyphInstance> callback) {
        if (connectNewGlyphStatic(glyphInstance, pinName, glyphToCreate)) {
            callback.accept(glyphInstance.glyph.getInputPin(glyphInstance, pinName).get().connectedGlyph);
            return true;
        }
        return false;
    }

    /** @param callback consumer passing as parameter the newly created GlyphInstance. */
    public static boolean connectNewGlyphWithCallbackStatic(GlyphInstance glyphInstance, int pinIndex, Glyph glyphToCreate, Consumer<GlyphInstance> callback) {
        if (connectNewGlyphStatic(glyphInstance, pinIndex, glyphToCreate)) {
            callback.accept(glyphInstance.glyph.getInputPin(glyphInstance, pinIndex).get().connectedGlyph);
            return true;
        }
        return false;
    }

    public static boolean connectNewGlyphStatic(GlyphInstance glyphInstance, String pinName, Glyph glyphToCreate) {
        Optional<InputPin> targetPin = glyphInstance.glyph.getInputPin(glyphInstance, pinName);
        if (targetPin.isEmpty()) {
            return false;
        }

        GlyphValueType desiredValueType = targetPin.get().valueType;
        GlyphInstance glyphToConnect = glyphToCreate.instantiate(desiredValueType);
        if (glyphToConnect == null) {
            return false;
        }

        return connectGlyphStatic(glyphInstance, pinName, glyphToConnect);
    }

    public static boolean connectNewGlyphStatic(GlyphInstance glyphInstance, int pinIndex, Glyph glyphToCreate) {
        Optional<InputPin> targetPin = glyphInstance.glyph.getInputPin(glyphInstance, pinIndex);
        if (targetPin.isEmpty()) {
            return false;
        }

        GlyphValueType desiredValueType = targetPin.get().valueType;
        GlyphInstance glyphToConnect = glyphToCreate.instantiate(desiredValueType);
        if (glyphToConnect == null) {
            return false;
        }

        return connectGlyphStatic(glyphInstance, pinIndex, glyphToConnect);
    }

    public static boolean connectGlyphStatic(GlyphInstance glyphInstance, String pinName, GlyphInstance glyphToConnect) {
        return glyphInstance.glyph.connectGlyph(glyphInstance, pinName, glyphToConnect);
    }

    public static boolean connectGlyphStatic(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        return glyphInstance.glyph.connectGlyph(glyphInstance, pinIndex, glyphToConnect);
    }

    // ~StaticHelpers
    /*================================================================================================================*/
}
