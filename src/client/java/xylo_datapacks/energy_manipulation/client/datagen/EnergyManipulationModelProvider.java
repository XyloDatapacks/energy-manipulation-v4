package xylo_datapacks.energy_manipulation.client.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.CustomModelDataProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.client.utils.EnergyManipulationModelTemplate;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperatorGlyphInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorButtonsRegistry;

import java.util.ArrayList;
import java.util.List;

public class EnergyManipulationModelProvider extends FabricModelProvider {
    
    public EnergyManipulationModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators blockModelGenerators) {
        
    }

    @Override
    public void generateItemModels(@NonNull ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(EnergyManipulationItems.SPELL_BOOK, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(EnergyManipulationItems.SPELL_SCROLL, ModelTemplates.FLAT_ITEM);
        generateDynamicGuiItemModels(itemModelGenerators, EnergyManipulationItems.GUI_BUTTON);
    }

    public void generateDynamicGuiItemModels(ItemModelGenerators generator, Item baseItem) {
        List<String> allButtons = new ArrayList<>();
        List<String> allGlyphs = new ArrayList<>();
        
        // Static buttons
        allButtons.addAll(SpellEditorButtonsRegistry.SPELL_EDITOR_BUTTON.keySet().stream().map(Identifier::getPath).toList());
        
        
        // Glyphs
        allGlyphs.addAll(GlyphsRegistry.GLYPH.keySet().stream().map(Identifier::getPath).toList());
        // Value types
        allGlyphs.addAll(GlyphsRegistry.VALUE_TYPE.keySet().stream().map(Identifier::getPath).toList());
        // Type specific raw value glyphs
        String rawValueGlyphKey = GlyphsRegistry.GLYPH.getKey(GlyphsRegistry.RAW_VALUE_GLYPH).getPath();
        allGlyphs.addAll(GlyphsRegistry.VALUE_TYPE.stream()
                .filter(GlyphValueType::hasValueSelector)
                .map(valueType -> rawValueGlyphKey + "_" + GlyphsRegistry.VALUE_TYPE.getKey(valueType).getPath())
                .toList());
        // Operator glyphs pin separator
        allGlyphs.addAll(GlyphsRegistry.GLYPH.stream()
                .filter(OperatorGlyphInterface.class::isInstance)
                .map(valueType -> GlyphsRegistry.GLYPH.getKey(valueType).getPath() + "_separator")
                .toList());
        // Glyph buttons (treating them as glyphs since they need to be scaled too)
        allGlyphs.addAll(SpellEditorButtonsRegistry.SPELL_EDITOR_GLYPH_BUTTON.keySet().stream().map(Identifier::getPath).toList());

        
        // generate switch cases and their models
        List<SelectItemModel.SwitchCase<String>> switchCases = new ArrayList<>();
        createAndAddModelInstanceCases(generator, switchCases, allButtons, new Vector3f(1.f, 1.f, 1.f));
        createAndAddModelInstanceCases(generator, switchCases, allGlyphs, new Vector3f(2.f, 2.f, 1.f));

        // Add fallback model for unknown buttons.
        ItemModel.Unbaked fallbackModel = ItemModelUtils.plainModel(Identifier.withDefaultNamespace("item/barrier"));

        // Create the main model for GuiButtonItem. It checks for custom data property 0.
        generator.itemModelOutput.accept(baseItem, ItemModelUtils.select(new CustomModelDataProperty(0), fallbackModel, switchCases), new ClientItem.Properties(true, true, 1.F));
    }
    
    private void createAndAddModelInstanceCases(ItemModelGenerators generator, List<SelectItemModel.SwitchCase<String>> destination, List<String> buttonNames, Vector3f scale) {
        for (String buttonName : buttonNames) {
            Identifier modelIdentifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "item/gui/button/" + buttonName);

            if (scale.equals(1.f, 1.f, 1.f)) {
                ModelTemplates.FLAT_ITEM.create(modelIdentifier, TextureMapping.singleSlot(TextureSlot.LAYER0, new Material(modelIdentifier)), generator.modelOutput);
            } else {
                ((EnergyManipulationModelTemplate) ModelTemplates.FLAT_ITEM).energy_manipulation$createWithGuiScale(modelIdentifier, TextureMapping.singleSlot(TextureSlot.LAYER0, new Material(modelIdentifier)), generator.modelOutput, scale);
            }

            ItemModel.Unbaked caseModel = ItemModelUtils.plainModel(modelIdentifier);
            destination.add(ItemModelUtils.when(buttonName, caseModel));
        }
    }
    
}
