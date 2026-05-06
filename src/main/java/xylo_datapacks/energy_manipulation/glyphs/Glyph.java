package xylo_datapacks.energy_manipulation.glyphs;

import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyphs.pins.*;
import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValue;
import xylo_datapacks.energy_manipulation.glyphs.valueType.GlyphValueType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Glyph {

    public OutputPinDefinition outputPinDefinition;
    public List<InputPinDefinition> inputPinDefinitions;
    public InputPinMode inputPinMode;
    
    public Glyph() {
        outputPinDefinition = new OutputPinDefinition();
        inputPinDefinitions = new ArrayList<>();
        inputPinMode = InputPinMode.NONE;
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

        InputPinDefinition inputPinDefinition = new InputPinDefinition(pinName);
        inputPinDefinition.nodeFilter = nodeFilter;

        inputPinDefinitions.add(inputPinDefinition);
    }
    
    /*================================================================================================================*/
    // Instantiation
    
    public GlyphInstance instantiate(GlyphValueType outputValueType) {
        
        // do not allow creation if we cannot support output value type
        if (outputPinDefinition.valueTypeCompatibilityPredicate.test(outputValueType)) {
            EnergyManipulation.LOGGER.warn("Trying to instantiate a GlyphInstance with a non supported output pin type!");
            return null;
        }

        // Create new instance
        GlyphInstance glyphInstance = new GlyphInstance();

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
        return new GlyphPayload();
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
         if (pinIndex > 0 && pinIndex < glyphInstance.inputPins.size()) {
            return Optional.ofNullable(glyphInstance.inputPins.get(pinIndex));
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

    public void connectGlyph(GlyphInstance glyphInstance, String pinName, GlyphInstance glyphToConnect) {
        if (inputPinMode != InputPinMode.STANDARD) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance by pinName if inputPinMode is not STANDARD!");
            return;
        }
        
        int pinIndex = getInputPinIndex(pinName);
        connectGlyph_Internal(glyphInstance, pinIndex, glyphToConnect);
    }

    public void connectGlyph(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        if (inputPinMode != InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance by pinIndex if inputPinMode is not ARRAY!");
            return;
        }
       
        connectGlyph_Internal(glyphInstance, pinIndex, glyphToConnect);
    }

    protected void connectGlyph_Internal(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        if (!hasInputPins(glyphInstance)) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance to one with no input pins!");
            return;
        }
        
        if (!(pinIndex > 0 && pinIndex < glyphInstance.inputPins.size())) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance to a non existent input pin!");
            return;
        }
        
        InputPin targetPin = glyphInstance.inputPins.get(pinIndex);
        targetPin.connectedGlyph = glyphToConnect;
        glyphToConnect.outputPin.connectedPin = new WeakReference<>(targetPin);
        
        refreshPins(glyphInstance);

        OnConnected(glyphToConnect);
    }
    
    public void OnConnected(GlyphInstance glyphInstance) {
        refreshPins(glyphInstance);
    }
    
    // ~Connections
    /*================================================================================================================*/

    /*================================================================================================================*/
    // Execution
    
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        return new GlyphValue();
    }

    public GlyphValue evaluatePin(ExecutionContext executionContext, GlyphInstance glyphInstance, String pinName) {
        Optional<InputPin> targetPin = getInputPin(glyphInstance, pinName);
        if (targetPin.isEmpty() || targetPin.get().connectedGlyph == null) {
            return new GlyphValue();
        }

        return execute(executionContext, targetPin.get().connectedGlyph);
    }

    public GlyphValue evaluatePin(ExecutionContext executionContext, GlyphInstance glyphInstance, int pinIndex) {
        Optional<InputPin> targetPin = getInputPin(glyphInstance, pinIndex);
        if (targetPin.isEmpty() || targetPin.get().connectedGlyph == null) {
            return new GlyphValue();
        }

        return execute(executionContext, targetPin.get().connectedGlyph);
    }

    // ~Execution
    /*================================================================================================================*/
}
