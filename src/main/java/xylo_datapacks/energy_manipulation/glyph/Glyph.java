package xylo_datapacks.energy_manipulation.glyph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.editor_data.GlyphEditorData;
import xylo_datapacks.energy_manipulation.glyph.editor_data.InputPinEditorData;
import xylo_datapacks.energy_manipulation.glyph.execution.ExecutionContext;
import xylo_datapacks.energy_manipulation.glyph.payload.GlyphPayload;
import xylo_datapacks.energy_manipulation.glyph.pin.*;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Remember to always use qualified access to members of this class to avoid calling methods using the 
 * wrong Glyph object.
 *  {@snippet : 
 *    connect(GlyphInstance glyphInstance, GlyphInstance other) {
 *        onPinConnectionChanged(glyphInstance);
 *
 *        onConnected(other);  // <- WRONG!
 *        parentInstance.glyph.onConnected(other); // <- RIGHT!
 *    }
 *  }
 * 
 */
public class Glyph {

    protected OutputPinDefinition outputPinDefinition = new OutputPinDefinition();
    protected List<InputPinDefinition> inputPinDefinitions = new ArrayList<>();
    protected InputPinMode inputPinMode = InputPinMode.NONE;
    protected GlyphEditorData editorData = new GlyphEditorData();
    
    public Glyph() {
    }

    public void RegisterPinDefinition(String pinName, Predicate<Glyph> glyphFilter) {
        if (this.inputPinMode == InputPinMode.NONE) {
            EnergyManipulation.LOGGER.warn("Cannot register a pin definition if inputPinMode is not set for this Glyph!");
            return;
        }

        if (this.inputPinMode == InputPinMode.VALUE) {
            EnergyManipulation.LOGGER.warn("Trying to register a Glyph pin, while no pin is expected!");
            return;
        }

        if (this.inputPinMode == InputPinMode.ARRAY && !this.inputPinDefinitions.isEmpty()) {
            EnergyManipulation.LOGGER.warn("Glyphs with input pin arrays can only register one input pin definition!");
            return;
        }

        InputPinDefinition inputPinDefinition = new InputPinDefinition(pinName);
        inputPinDefinition.glyphFilter = glyphFilter;

        this.inputPinDefinitions.add(inputPinDefinition);
        this.editorData.inputPinsEditorData.put(pinName, new InputPinEditorData());
    }
    
    /*================================================================================================================*/
    // Instantiation
    
    public GlyphInstance instantiate(GlyphValueType outputValueType) {
        
        // do not allow creation if we cannot support output value type
        if (outputValueType == null || !this.outputPinDefinition.valueTypeCompatibilityPredicate.test(outputValueType)) {
            EnergyManipulation.LOGGER.warn("Trying to instantiate a GlyphInstance with a non supported output pin type!");
            return null;
        }

        // Create new instance
        GlyphInstance glyphInstance = this.createInstance_internal();

        // Create input pins
        if (this.inputPinMode == InputPinMode.STANDARD) {
            this.inputPinDefinitions.forEach(inputPinDefinition -> {
                InputPin newInputPin = new InputPin(new WeakReference<>(glyphInstance));
                glyphInstance.inputPins.add(newInputPin);
            });
        }
        
        // Create output pin and set value type
        glyphInstance.outputPin = new OutputPin(new WeakReference<>(glyphInstance));
        glyphInstance.outputPin.valueType = outputValueType;
        
        // Custom initialization for pins
        this.initializePins(glyphInstance);
        
        // Create payload
        glyphInstance.payload = this.createPayload(glyphInstance);
        this.initializePayload(glyphInstance);
        
        return glyphInstance;
    }
    
    protected GlyphInstance createInstance_internal() {
        return new GlyphInstance(this);
    }
    
    public void initializePins(GlyphInstance glyphInstance) {}

    public GlyphPayload createPayload(GlyphInstance glyphInstance) {
        return null;
    }

    public void initializePayload(GlyphInstance glyphInstance) {}

    /** Called after modifying the payload of this glyphInstance. */
    public void NotifyPayloadChanged(GlyphInstance glyphInstance) {
        this.onPayloadChanged(glyphInstance);

        Optional<InputPin> parentInputPin = glyphInstance.outputPin.getConnectedPin();
        Optional<GlyphInstance> parentGlyphInstance = parentInputPin.map(pin -> pin.owner.get());
        if (parentGlyphInstance.isPresent()) {
            int parentInputPinIndex = parentGlyphInstance.get().inputPins.indexOf(parentInputPin.get());
            parentGlyphInstance.get().glyph.NotifyInputPinGlyphPayloadChanged(parentGlyphInstance.get(), parentInputPinIndex);
        }
    }

    /** Called when a connected glyph's payload changes.
     * @param glyphInstance this glyph instance.
     * @param pinIndex the index of the input pin (of glyphInstance) whose connected glyphInstance payload changed.
     */
    public void NotifyInputPinGlyphPayloadChanged(GlyphInstance glyphInstance, int pinIndex) {
        this.onInputPinGlyphPayloadChanged(glyphInstance, pinIndex);
    }

    // ~Instantiation
    /*================================================================================================================*/

    /*================================================================================================================*/
    // PinManagement
    
    public InputPinMode getInputPinMode() {
        return this.inputPinMode;
    }
    
    public OutputPinDefinition getOutputPinDefinition() {
        return this.outputPinDefinition;
    }
    
    public boolean hasInputPins(GlyphInstance glyphInstance) {
        return (this.inputPinMode == InputPinMode.ARRAY || this.inputPinMode == InputPinMode.STANDARD) && !glyphInstance.inputPins.isEmpty();
    }

    /** Always returns the first pin index if inputPinMode is ARRAY. */
    public int getInputPinIndex(String pinName) {
        if (this.inputPinMode == InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("While using InputPinMode.ARRAY, getting an input pin index by name will always return the first pin if it exists!");
        }
        
        for (int i = 0; i < this.inputPinDefinitions.size(); i++) {
            if (this.inputPinDefinitions.get(i).pinName.equals(pinName)) {
                return i;
            }
        }
        return -1; // Not found
    }
    
    public List<InputPinDefinition> getInputPinDefinitions() {
        return this.inputPinDefinitions;
    }

    public Optional<InputPinDefinition> getInputPinDefinition(String pinName) {
        int pinIndex = this.getInputPinIndex(pinName);
        return this.getInputPinDefinition(pinIndex);
    }

    public Optional<InputPinDefinition> getInputPinDefinition(int pinIndex) {
        if (this.inputPinMode == InputPinMode.ARRAY && pinIndex > 0) {
            pinIndex = 0;
        }

        if (pinIndex >= 0 && pinIndex < this.inputPinDefinitions.size()) {
            return Optional.ofNullable(this.inputPinDefinitions.get(pinIndex));
        }
        return Optional.empty();
    }
    
    /** Always returns the first pin if inputPinMode is ARRAY. */
    public Optional<InputPin> getInputPin(GlyphInstance glyphInstance, String pinName) {
        if (this.inputPinMode == InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("While using InputPinMode.ARRAY, getting an input pin by name will always return the first pin if it exists!");
        }
        
        int pinIndex = this.getInputPinIndex(pinName);
        return this.getInputPin(glyphInstance, pinIndex);
    }

    public Optional<InputPin> getInputPin(GlyphInstance glyphInstance, int pinIndex) {
         if (pinIndex >= 0 && pinIndex < glyphInstance.inputPins.size()) {
            return Optional.ofNullable(glyphInstance.inputPins.get(pinIndex));
         }
         return Optional.empty();
    }
    
    public void addPin(GlyphInstance glyphInstance) {
        if (this.inputPinMode != InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("Cannot add pins if inputPinMode is not ARRAY!");
            return;
        }

        InputPin newInputPin = new InputPin(new WeakReference<>(glyphInstance));
        glyphInstance.inputPins.add(newInputPin);
        this.initializeNewPin(glyphInstance, newInputPin);
        this.NotifyInputPinConnectionChanged(glyphInstance, glyphInstance.inputPins.size() - 1);
    }

    public void removePin(GlyphInstance glyphInstance, int index) {
        if (this.inputPinMode != InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("Cannot remove pins if inputPinMode is not ARRAY!");
            return;
        }

        glyphInstance.inputPins.remove(index);
        this.NotifyInputPinConnectionChanged(glyphInstance, -1);
    }

    public void insertPin(GlyphInstance glyphInstance, int index) {
        if (this.inputPinMode != InputPinMode.ARRAY) {
            EnergyManipulation.LOGGER.warn("Cannot insert pins if inputPinMode is not ARRAY!");
            return;
        }

        InputPin newInputPin = new InputPin(new WeakReference<>(glyphInstance));
        glyphInstance.inputPins.add(index, newInputPin);
        this.initializeNewPin(glyphInstance, newInputPin);
        this.NotifyInputPinConnectionChanged(glyphInstance, index);
    }
    
    public void initializeNewPin(GlyphInstance glyphInstance, InputPin newInputPin) {}

    // ~PinManagement
    /*================================================================================================================*/

    /*================================================================================================================*/
    // Connections
    
    public Optional<GlyphInstance> getParentGlyphInstance(GlyphInstance glyphInstance) {
        Optional<InputPin> parentInputPin = glyphInstance.outputPin.getConnectedPin();
        return parentInputPin.map(pin -> pin.owner.get());
    }
    
    public int getParentInputPinIndex(GlyphInstance glyphInstance) {
        Optional<InputPin> parentInputPin = glyphInstance.outputPin.getConnectedPin();
        Optional<GlyphInstance> parentGlyphInstance = parentInputPin.map(pin -> pin.owner.get());
        return parentGlyphInstance.map(instance -> instance.inputPins.indexOf(parentInputPin.get())).orElse(-1);
    }
    
    public void getDescendants(GlyphInstance glyphInstance, List<GlyphInstance> destination, Predicate<GlyphInstance> glyphFilter) {
        for (InputPin inputPin : glyphInstance.inputPins) {
            if (inputPin != null) {
                inputPin.getConnectedGlyph().ifPresent(connectedGlyph -> {
                    // If the connected instance passes the filter, add it to the destination list.
                    if (glyphFilter.test(connectedGlyph)) {
                        destination.add(connectedGlyph);
                    }

                    // Recursively get descendants of the connected instance.
                    connectedGlyph.glyph.getDescendants(connectedGlyph, destination, glyphFilter); 
                });
            }
        }
    }
    
    public Optional<GlyphInstance> getClosestParent(GlyphInstance glyphInstance, Predicate<GlyphInstance> glyphFilter) {
        return this.getParentGlyphInstance(glyphInstance).flatMap(parent -> {
            if (glyphFilter.test(parent)) {
                return Optional.of(parent);
            }
            return parent.glyph.getClosestParent(parent, glyphFilter);
        });
    }
    
    protected boolean canConnectToPin_Internal(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        Optional<InputPin> inputPin = this.getInputPin(glyphInstance, pinIndex);
        Optional<InputPinDefinition> inputPinDefinition = this.getInputPinDefinition(pinIndex);
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
        if (this.inputPinMode != InputPinMode.STANDARD) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance by pinName if inputPinMode is not STANDARD!");
            return false;
        }
        
        int pinIndex = this.getInputPinIndex(pinName);
        return this.connectGlyph_Internal(glyphInstance, pinIndex, glyphToConnect);
    }

    public boolean connectGlyph(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        return this.connectGlyph_Internal(glyphInstance, pinIndex, glyphToConnect);
    }

    protected boolean connectGlyph_Internal(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        if (!this.hasInputPins(glyphInstance)) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance to one with no input pins!");
            return false;
        }
        
        if (!(pinIndex >= 0 && pinIndex < glyphInstance.inputPins.size())) {
            EnergyManipulation.LOGGER.warn("Cannot connect a GlyphInstance to a non existent input pin!");
            return false;
        }
        
        if (!this.canConnectToPin_Internal(glyphInstance, pinIndex, glyphToConnect)) {
            EnergyManipulation.LOGGER.warn("Cannot connect GlyphInstance to specified input pin, as they are not compatible!");
            return false;
        }
        
        InputPin targetPin = glyphInstance.inputPins.get(pinIndex);
        targetPin.setConnectedGlyph(glyphToConnect);
        glyphToConnect.outputPin.setConnectedPin(targetPin);
        
        this.NotifyInputPinConnectionChanged(glyphInstance, pinIndex);
        glyphToConnect.glyph.NotifyConnected(glyphToConnect);
        return true;
    }

    public void resetConnection(GlyphInstance glyphInstance, String pinName) {
        if (this.inputPinMode != InputPinMode.STANDARD) {
            EnergyManipulation.LOGGER.warn("Cannot reset a pin connection by pinName if inputPinMode is not STANDARD!");
            return;
        }
        
        this.resetConnection(glyphInstance, this.getInputPinIndex(pinName));   
    }

    public void resetConnection(GlyphInstance glyphInstance, int pinIndex) {
        if (!this.hasInputPins(glyphInstance)) {
            EnergyManipulation.LOGGER.warn("Cannot reset connection for a GlyphInstance with no input pins!");
            return;
        }

        if (!(pinIndex >= 0 && pinIndex < glyphInstance.inputPins.size())) {
            EnergyManipulation.LOGGER.warn("Cannot reset connection for a non existent input pin!");
            return;
        }

        InputPin targetPin = glyphInstance.inputPins.get(pinIndex);
        targetPin.setConnectedGlyph(null);
        this.NotifyInputPinConnectionChanged(glyphInstance, pinIndex);
    }
    
    /**
     * Called when a descendant glyph's input pin changes connection.
     * @param glyphInstance this glyph instance.
     * @param pinIndex the index of the input pin whose connection changed.
     */
    public void NotifyInputPinConnectionChanged(GlyphInstance glyphInstance, int pinIndex) {
        this.onInputPinConnectionChanged(glyphInstance, pinIndex);

        Optional<InputPin> parentInputPin = glyphInstance.outputPin.getConnectedPin();
        Optional<GlyphInstance> parentGlyphInstance = parentInputPin.map(pin -> pin.owner.get());
        if (parentGlyphInstance.isPresent()) {
            int parentInputPinIndex = parentGlyphInstance.get().inputPins.indexOf(parentInputPin.get());
            parentGlyphInstance.get().glyph.NotifyInputPinGlyphStateChanged(parentGlyphInstance.get(), parentInputPinIndex);

            parentGlyphInstance.get().glyph.NotifyDescendantGlyphStateChanged(parentGlyphInstance.get(), glyphInstance, pinIndex);
        }
    }

    /**
     * Called when a connected glyph's input pin changes connection.
     * @param glyphInstance this glyph instance.
     * @param pinIndex the index of the input pin (of glyph instance) whose connected glyph instance changed a connection.
     */
    public void NotifyInputPinGlyphStateChanged(GlyphInstance glyphInstance, int pinIndex) {
        this.onInputPinGlyphStateChanged(glyphInstance, pinIndex);
    }
    
    /**
     * Called when a descendant glyph's input pin changes connection.
     * @param glyphInstance this glyph instance.
     * @param descendantInstance the descendant glyph instance whose pin changed connection.
     * @param pinIndex the index of descendantInstance's input pin whose connection changed.
     */
    public void NotifyDescendantGlyphStateChanged(GlyphInstance glyphInstance, GlyphInstance descendantInstance, int pinIndex) {
        this.onDescendantGlyphStateChanged(glyphInstance, descendantInstance, pinIndex);
        
        // Propagate the change to the parent glyph instance, if any.
        this.getParentGlyphInstance(glyphInstance).ifPresent(parentGlyphInstance -> {
            parentGlyphInstance.glyph.NotifyDescendantGlyphStateChanged(parentGlyphInstance, descendantInstance, pinIndex);
        });
    }
    
    /** Called when this instance output pin is connected to another glyph instance. */
    public void NotifyConnected(GlyphInstance glyphInstance) {
        this.onConnected(glyphInstance);
    }
    
    // ~Connections
    /*================================================================================================================*/

    /*================================================================================================================*/
    // Execution
    
    /** @return an object derived from GlyphValue. It must NEVER be null, instead use GlyphValue or ExecutionErrorGlyphValue in case of exceptions */
    public GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        return GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue(this.getClass().getSimpleName() + " does not implement execute() method!");
    }

    public GlyphValue evaluatePin(ExecutionContext executionContext, GlyphInstance glyphInstance, String pinName) {
        Optional<InputPin> targetPin = this.getInputPin(glyphInstance, pinName);
        return this.evaluatePin_Internal(executionContext, glyphInstance, targetPin.orElse(null));
    }

    public GlyphValue evaluatePin(ExecutionContext executionContext, GlyphInstance glyphInstance, int pinIndex) {
        Optional<InputPin> targetPin = this.getInputPin(glyphInstance, pinIndex);
        return this.evaluatePin_Internal(executionContext, glyphInstance, targetPin.orElse(null));
    }

    protected GlyphValue evaluatePin_Internal(ExecutionContext executionContext, GlyphInstance glyphInstance, @Nullable InputPin targetPin) {
        GlyphValue glyphValue;

        if (targetPin == null) {
            glyphValue = GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("Cannot evaluate a non existent pin!");
        } 
        else if (targetPin.getConnectedGlyph().isEmpty()) {
            glyphValue = GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("Cannot evaluate a pin with no connection!");
        } 
        else {
            GlyphInstance instanceAtPin = targetPin.getConnectedGlyph().get();
            glyphValue = instanceAtPin.glyph.execute(executionContext, instanceAtPin);

            if (glyphValue == null) {
                glyphValue = GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("Evaluation of glyph [" + instanceAtPin.glyph.getClass().getSimpleName() + "] resulted in null GlyphValue. This behaviour is unsupported!");
            } 
            else if (!glyphValue.isOfType(GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE)) {
                // If not already an error, check for further errors.
                
                if (!glyphValue.isOfType(targetPin.valueType)) {
                    glyphValue = GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.makeExecutionErrorGlyphValue("Evaluation of glyph [" + instanceAtPin.glyph.getClass().getSimpleName() + "] resulted in incompatible value type! Make sure \"execute\" method returns the same value type as the instance's outputPin.");
                }
            }
        }

        logPinEvaluationError(executionContext, glyphInstance, targetPin, glyphValue);
        return glyphValue;
    }
    
    protected void logPinEvaluationError(ExecutionContext executionContext, GlyphInstance glyphInstance, InputPin targetPin, GlyphValue evaluationValue) {
        if (evaluationValue.isOfType(GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE)) {
            
            int pinIndex = glyphInstance.inputPins.indexOf(targetPin);
            String inputPinName = this.getInputPinDefinition(pinIndex).map(def -> def.pinName).orElse("null");

            EnergyManipulation.LOGGER.warn("Evaluation of pin [{} {}] from {}, resulted in the following error: {}", inputPinName, pinIndex, glyphInstance.glyph.getClass().getSimpleName(), GlyphsRegistry.EXECUTION_ERROR_VALUE_TYPE.getExecutionErrorGlyphValue(evaluationValue));
        }
    }

    // ~Execution
    /*================================================================================================================*/

    /*================================================================================================================*/
    // EditorData

    public GlyphEditorData getEditorData() {
        return this.editorData;
    }

    public Optional<InputPinEditorData> getInputPinEditorData(String pinName) {
        return Optional.ofNullable(this.editorData.inputPinsEditorData.get(pinName));
    }

    public Optional<InputPinEditorData> getInputPinEditorData(int pinIndex) {
        Optional<InputPinDefinition> inputPinDefinitions = this.getInputPinDefinition(pinIndex);
        String pinName = inputPinDefinitions.isPresent() ? inputPinDefinitions.get().pinName : "";
        return this.getInputPinEditorData(pinName);
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
        this.serializePayload(glyphInstance).ifPresent(payload -> output.put("payload", payload));

        // Serialize input pins
        if (this.hasInputPins(glyphInstance)) {
            ListTag inputPins = new ListTag();
            glyphInstance.inputPins.forEach(pin -> {
                // Since we are not storing index or id, we must store the empty connections too.
                inputPins.add(pin.getConnectedGlyph().map(GlyphUtils::serializeInstance).orElse(new CompoundTag()));
            });
            output.put("inputs", inputPins);
        }
        
        return output;
    }

    /** @param destination the glyph instance (of this glyph class!!!) to deserialize. */
    public void deserializeInstance(CompoundTag glyphInstanceCompound, GlyphInstance destination) {
        // In order for pins to be properly initialized we need to connect a newly created instance to its parent 
        // glyph instance before we can deserialize its data (in particular its input pins). 
        // If we were to deserialize a non-connected glyph instance, we would not be able to infer the output pin type. 
        // Since output pin type can affect the input pins type, we might not be able to connect its child glyphs after 
        // deserializing them.
        
        // Deserialize payload.
        glyphInstanceCompound.getCompound("payload").ifPresent(payload -> this.deserializePayload(payload, destination));

        // Deserialize input pins.
        ListTag inputPins = glyphInstanceCompound.getListOrEmpty("inputs");
        int pinCount = this.inputPinMode == InputPinMode.ARRAY ? inputPins.size() : Math.min(destination.inputPins.size(), inputPins.size());
        for (int i = 0; i < pinCount; i++) {
            int pinIndex = i;

            if (this.inputPinMode == InputPinMode.ARRAY) {
                this.addPin(destination);
            }
            
            // Deserialize connection's glyph instance.
            inputPins.getCompound(pinIndex).ifPresent(connectionCompound -> {
                // Extract glyph from connection's compound.
                connectionCompound.getString("id").map(Identifier::parse).map(GlyphsRegistry.GLYPH::getValue).ifPresent(connectedGlyph -> {
                    // Create and connect a glyph instance from the extracted glyph.
                    GlyphUtils.connectNewGlyphWithCallback(destination, pinIndex, connectedGlyph, connectedInstance -> {
                        // Deserialize connected instance data.
                        GlyphUtils.deserializeInstance(connectionCompound, connectedInstance);
                    });
                });
            });
        }
    }
    
    public Optional<CompoundTag> serializePayload(GlyphInstance glyphInstance) {
        return Optional.empty();
    }

    /** @param destination the glyph instance (of this glyph class!!!) to deserialize. */
    public void deserializePayload(CompoundTag payloadCompound, GlyphInstance destination) {
    }

    // ~Serialization
    /*================================================================================================================*/

    /*================================================================================================================*/
    // Callbacks

    public void onInputPinConnectionChanged(GlyphInstance glyphInstance, int pinIndex) {}

    public void onInputPinGlyphStateChanged(GlyphInstance glyphInstance, int pinIndex) {}

    public void onDescendantGlyphStateChanged(GlyphInstance glyphInstance, GlyphInstance descendantInstance, int pinIndex) {}
    
    public void onConnected(GlyphInstance glyphInstance) {}

    public void onPayloadChanged(GlyphInstance glyphInstance) {}
    
    public void onInputPinGlyphPayloadChanged(GlyphInstance glyphInstance, int pinIndex) {}

    // ~Callbacks
    /*================================================================================================================*/
}
