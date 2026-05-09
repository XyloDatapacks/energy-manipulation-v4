package xylo_datapacks.energy_manipulation.glyph;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.editor_data.GlyphEditorData;
import xylo_datapacks.energy_manipulation.glyph.editor_data.InputPinEditorData;
import xylo_datapacks.energy_manipulation.glyph.payload.GlyphPayload;
import xylo_datapacks.energy_manipulation.glyph.pin.*;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import javax.swing.text.html.Option;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Glyph {

    protected OutputPinDefinition outputPinDefinition = new OutputPinDefinition();
    protected List<InputPinDefinition> inputPinDefinitions = new ArrayList<>();
    protected InputPinMode inputPinMode = InputPinMode.NONE;
    protected GlyphEditorData editorData = new GlyphEditorData();
    
    public Glyph() {
    }

    public void RegisterPinDefinition(String pinName, Predicate<Glyph> glyphFilter) {
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
        inputPinDefinition.glyphFilter = glyphFilter;

        inputPinDefinitions.add(inputPinDefinition);
        editorData.inputPinsEditorData.put(pinName, new InputPinEditorData());
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
    
    public InputPinMode getInputPinMode() {
        return inputPinMode;
    }
    
    public OutputPinDefinition getOutputPinDefinition() {
        return outputPinDefinition;
    }
    
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
    
    public List<InputPinDefinition> getInputPinDefinitions() {
        return inputPinDefinitions;
    }

    public Optional<InputPinDefinition> getInputPinDefinition(String pinName) {
        int pinIndex = getInputPinIndex(pinName);
        return getInputPinDefinition(pinIndex);
    }

    public Optional<InputPinDefinition> getInputPinDefinition(int pinIndex) {
        if (inputPinMode == InputPinMode.ARRAY) {
            pinIndex = 0;
        }

        if (pinIndex >= 0 && pinIndex < inputPinDefinitions.size()) {
            return Optional.ofNullable(inputPinDefinitions.get(pinIndex));
        }
        return Optional.empty();
    }
    
    public Optional<InputPin> getInputPin(GlyphInstance glyphInstance, String pinName) {
        if (inputPinMode == InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("While using InputPinMode.ARRAY, getting an input pin by name will always return the first pin if it exists!");
        }
        
        int pinIndex = getInputPinIndex(pinName);
        return getInputPin(glyphInstance, pinIndex);
    }

    public Optional<InputPin> getInputPin(GlyphInstance glyphInstance, int pinIndex) {
         if (pinIndex >= 0 && pinIndex < glyphInstance.inputPins.size()) {
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
        initializeNewPin(glyphInstance, newInputPin);
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
        initializeNewPin(glyphInstance, newInputPin);
        refreshPins(glyphInstance);
    }
    
    public void initializeNewPin(GlyphInstance glyphInstance, InputPin newInputPin) {}

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
        if (!inputPinDefinition.get().glyphFilter.test(glyphToConnect.glyph)) {
            return false;
        }

        // Verify that this glyph is acceptable for the output pin of the glyph we are trying to connect
        if (!glyphToConnect.glyph.outputPinDefinition.glyphFilter.test(glyphInstance.glyph)) {
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
            EnergyManipulation.LOGGER.warn("Evaluation of glyph [{}] connected to a pin of [{}] resulted in null GlyphValue. This behaviour is unsupported!", instanceAtPin.glyph.getClass().getSimpleName(), glyphInstance.glyph.getClass().getSimpleName());
            return new GlyphValue();
        }

        if (glyphValue.isOfType(GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE)) {
            EnergyManipulation.LOGGER.warn("Evaluation of glyph [{}] connected to a pin of [{}] resulted in the following error: {}", instanceAtPin.glyph.getClass().getSimpleName(), glyphInstance.glyph.getClass().getSimpleName(), glyphValue.getDebugString());
        }
        else if (!glyphValue.isOfType(targetPin.valueType)) {
            EnergyManipulation.LOGGER.warn("Evaluation of glyph [{}] connected to a pin of [{}] resulted in incompatible value type! Make sure \"execute\" method returns the same value type as the instance's outputPin.", instanceAtPin.glyph.getClass().getSimpleName(), glyphInstance.glyph.getClass().getSimpleName());
        }
        
        return glyphValue;
    }

    // ~Execution
    /*================================================================================================================*/

    /*================================================================================================================*/
    // EditorData

    public GlyphEditorData getEditorData() {
        return editorData;
    }

    public Optional<InputPinEditorData> getInputPinEditorData(String pinName) {
        return Optional.ofNullable(editorData.inputPinsEditorData.get(pinName));
    }

    public Optional<InputPinEditorData> getInputPinEditorData(int pinIndex) {
        Optional<InputPinDefinition> inputPinDefinitions = getInputPinDefinition(pinIndex);
        String pinName = inputPinDefinitions.isPresent() ? inputPinDefinitions.get().pinName : "";
        return getInputPinEditorData(pinName);
    }

    // ~EditorData
    /*================================================================================================================*/

    /*================================================================================================================*/
    // Serialization

    public CompoundTag serializeInstance(GlyphInstance glyphInstance) {
        CompoundTag output = new CompoundTag();
        
        // Serialize id
        output.putString("id", GlyphsRegistry.GLYPH.getKey(glyphInstance.glyph).toString());
        
        // Serialize payload
        serializePayload(glyphInstance).ifPresent(payload -> output.put("payload", payload));

        // Serialize input pins
        ListTag inputPins = new ListTag();
        glyphInstance.inputPins.forEach(pin -> {
            Optional.ofNullable(pin.connectedGlyph).map(GlyphUtils::serializeInstance).ifPresent(inputPins::add);
        });
        output.put("inputs", inputPins);
        
        return output;
    }

    public void deserializeInstance(CompoundTag glyphInstanceCompound, GlyphInstance destination) {
        // In order for pins to be properly initialized we need to connect a newly created instance to its parent 
        // glyph instance before we can deserialize its data (in particular its input pins). 
        // If we were to deserialize a non-connected glyph instance, we would not be able to infer the output pin type. 
        // Since output pin type can affect the input pins type, we might not be able to connect its child glyphs after 
        // deserializing them.
        
        // Deserialize payload.
        glyphInstanceCompound.getCompound("payload").ifPresent(payload -> deserializePayload(payload, destination));

        // Deserialize input pins.
        ListTag inputPins = glyphInstanceCompound.getListOrEmpty("inputs");
        for (int i = 0; i < Math.min(destination.inputPins.size(), inputPins.size()); i++) {
            int pinIndex = i;
            
            // Deserialize connection's glyph instance.
            inputPins.getCompound(pinIndex).ifPresent(connectionCompound -> {
                // Extract glyph from connection's compound.
                Glyph connectedGlyph = connectionCompound.getString("id").map(Identifier::parse).map(GlyphsRegistry.GLYPH::getValue).orElse(null);
                if (connectedGlyph != null) {
                    // Create and connect a glyph instance from the extracted glyph.
                    GlyphUtils.connectNewGlyphWithCallback(destination, pinIndex, connectedGlyph, connectedInstance -> {
                        // Deserialize connected instance data.
                        GlyphUtils.deserializeInstance(connectionCompound, connectedInstance);
                    });
                }
            });
        }
    }
    
    public Optional<CompoundTag> serializePayload(GlyphInstance glyphInstance) {
        return Optional.empty();
    }

    public void deserializePayload(CompoundTag payloadCompound, GlyphInstance destination) {
    }

    // ~Serialization
    /*================================================================================================================*/
}
