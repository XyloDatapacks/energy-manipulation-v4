package xylo_datapacks.energy_manipulation.client.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xylo_datapacks.energy_manipulation.client.utils.EnergyManipulationModelTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

@Mixin(ModelTemplate.class)
public abstract class ModelTemplateMixin implements EnergyManipulationModelTemplate {
    
    @Shadow @Final private Optional<Identifier> model;

    @Shadow
    protected abstract Map<TextureSlot, Material> createMap(TextureMapping mapping);
    
    /**
     * Usage example:
     * {@snippet : ((EnergyManipulationModelTemplate) ModelTemplates.FLAT_ITEM).energy_manipulation$createWithGuiScale(modelIdentifier, TextureMapping.singleSlot(TextureSlot.LAYER0, new Material(modelIdentifier)), generator.modelOutput, new Vector3f(1.f, 1.f, 1.f)); }
     */
    @Override
    public Identifier energy_manipulation$createWithGuiScale(final Identifier target, final TextureMapping textures, final BiConsumer<Identifier, ModelInstance> output, Vector3f guiScale) {
        Map<TextureSlot, Material> slots = createMap(textures);
        output.accept(target, (ModelInstance)() -> {
            JsonObject result = new JsonObject();
            model.ifPresent(m -> result.addProperty("parent", m.toString()));
            if (!slots.isEmpty()) {
                JsonObject textureObj = new JsonObject();
                slots.forEach((slot, value) -> {
                    JsonElement valueJson = Material.CODEC.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
                    textureObj.add(slot.getId(), valueJson);
                });
                result.add("textures", textureObj);

                JsonObject display = new JsonObject();
                JsonObject gui = new JsonObject();

                JsonArray scale = new JsonArray();
                scale.add(guiScale.x);
                scale.add(guiScale.y);
                scale.add(guiScale.x);

                gui.add("scale", scale);
                display.add("gui", gui);
                result.add("display", display);
            }

            return result;
        });
        return target;
    }
}
