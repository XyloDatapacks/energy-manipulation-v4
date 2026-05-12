package xylo_datapacks.energy_manipulation.glyph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPin;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;

import java.util.Optional;
import java.util.function.Consumer;

public class GlyphUtils {

    public static GlyphInstance copyGlyphInstance(GlyphInstance glyphInstance) {
        return deserializeInstance(serializeInstance(glyphInstance), glyphInstance.outputPin.valueType).get();
    }

    public static CompoundTag serializeInstance(GlyphInstance glyphInstance) {
        return glyphInstance.glyph.serializeInstance(glyphInstance);
    }

    public static Optional<GlyphInstance> deserializeInstance(CompoundTag glyphInstanceCompound, GlyphValueType valueType) {
        return glyphInstanceCompound.getString("id").map(Identifier::parse).map(GlyphsRegistry.GLYPH::getValue).flatMap(glyph -> {
            return Optional.ofNullable(glyph.instantiate(valueType)).map(glyphInstance -> {
                deserializeInstance(glyphInstanceCompound, glyphInstance);
                return glyphInstance;
            });
        });
    }
    
    public static void deserializeInstance(CompoundTag glyphInstanceCompound, GlyphInstance destination) {
        destination.glyph.deserializeInstance(glyphInstanceCompound, destination);
    }
    
    /** @param callback consumer passing as parameter the newly created GlyphInstance. */
    public static boolean connectNewGlyphWithCallback(GlyphInstance glyphInstance, String pinName, Glyph glyphToCreate, Consumer<GlyphInstance> callback) {
        if (connectNewGlyph(glyphInstance, pinName, glyphToCreate)) {
            callback.accept(glyphInstance.glyph.getInputPin(glyphInstance, pinName).get().connectedGlyph);
            return true;
        }
        return false;
    }

    /** @param callback consumer passing as parameter the newly created GlyphInstance. */
    public static boolean connectNewGlyphWithCallback(GlyphInstance glyphInstance, int pinIndex, Glyph glyphToCreate, Consumer<GlyphInstance> callback) {
        if (connectNewGlyph(glyphInstance, pinIndex, glyphToCreate)) {
            callback.accept(glyphInstance.glyph.getInputPin(glyphInstance, pinIndex).get().connectedGlyph);
            return true;
        }
        return false;
    }

    public static boolean connectNewGlyph(GlyphInstance glyphInstance, String pinName, Glyph glyphToCreate) {
        Optional<InputPin> targetPin = glyphInstance.glyph.getInputPin(glyphInstance, pinName);
        if (targetPin.isEmpty()) {
            return false;
        }

        GlyphValueType desiredValueType = targetPin.get().valueType;
        GlyphInstance glyphToConnect = glyphToCreate.instantiate(desiredValueType);
        if (glyphToConnect == null) {
            return false;
        }

        return connectGlyph(glyphInstance, pinName, glyphToConnect);
    }

    public static boolean connectNewGlyph(GlyphInstance glyphInstance, int pinIndex, Glyph glyphToCreate) {
        Optional<InputPin> targetPin = glyphInstance.glyph.getInputPin(glyphInstance, pinIndex);
        if (targetPin.isEmpty()) {
            return false;
        }

        GlyphValueType desiredValueType = targetPin.get().valueType;
        GlyphInstance glyphToConnect = glyphToCreate.instantiate(desiredValueType);
        if (glyphToConnect == null) {
            return false;
        }

        return connectGlyph(glyphInstance, pinIndex, glyphToConnect);
    }

    public static boolean connectGlyph(GlyphInstance glyphInstance, String pinName, GlyphInstance glyphToConnect) {
        return glyphInstance.glyph.connectGlyph(glyphInstance, pinName, glyphToConnect);
    }

    public static boolean connectGlyph(GlyphInstance glyphInstance, int pinIndex, GlyphInstance glyphToConnect) {
        return glyphInstance.glyph.connectGlyph(glyphInstance, pinIndex, glyphToConnect);
    }

    public static void resetConnection(GlyphInstance glyphInstance, String pinName) {
        glyphInstance.glyph.resetConnection(glyphInstance, pinName);
    }

    public static void resetConnection(GlyphInstance glyphInstance, int pinIndex) {
        glyphInstance.glyph.resetConnection(glyphInstance, pinIndex);
    }

    public static GlyphValue execute(ExecutionContext executionContext, GlyphInstance glyphInstance) {
        return glyphInstance.glyph.execute(executionContext, glyphInstance);
    }
}
