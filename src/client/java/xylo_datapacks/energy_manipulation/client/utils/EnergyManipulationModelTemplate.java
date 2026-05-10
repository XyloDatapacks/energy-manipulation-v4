package xylo_datapacks.energy_manipulation.client.utils;


import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

public interface EnergyManipulationModelTemplate {

    public Identifier energy_manipulation$createWithGuiScale(final Identifier target, final TextureMapping textures, final BiConsumer<Identifier, ModelInstance> output, Vector3f guiScale);
}
