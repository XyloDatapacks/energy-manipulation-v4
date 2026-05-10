package xylo_datapacks.energy_manipulation.client.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.CustomModelDataProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperatorGlyphInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValueType;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorButtonsRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        
        // Static buttons
        allButtons.addAll(SpellEditorButtonsRegistry.SPELL_EDITOR_BUTTON.keySet().stream().map(Identifier::getPath).toList());
        // Glyphs
        allButtons.addAll(GlyphsRegistry.GLYPH.keySet().stream().map(Identifier::getPath).toList());
        // Value types
        allButtons.addAll(GlyphsRegistry.VALUE_TYPE.keySet().stream().map(Identifier::getPath).toList());
        // Type specific raw value glyphs
        String rawValueGlyphKey = GlyphsRegistry.GLYPH.getKey(GlyphsRegistry.RAW_VALUE_GLYPH).getPath();
        allButtons.addAll(GlyphsRegistry.VALUE_TYPE.stream()
                .filter(GlyphValueType::hasValueSelector)
                .map(valueType -> rawValueGlyphKey + "_" + GlyphsRegistry.VALUE_TYPE.getKey(valueType).getPath())
                .toList());
        // Operator glyphs pin separator
        allButtons.addAll(GlyphsRegistry.GLYPH.stream()
                .filter(OperatorGlyphInterface.class::isInstance)
                .map(valueType -> GlyphsRegistry.GLYPH.getKey(valueType).getPath() + "_separator")
                .toList());
        
        System.out.println("Generating models for buttons: " + allButtons);

        // Generate individual model for each button.
        List<SelectItemModel.SwitchCase<String>> switchCases = new ArrayList<>();
        for (String buttonName : allButtons) {
            Identifier modelIdentifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "item/gui/button/" + buttonName);

            ModelTemplates.FLAT_ITEM.create(modelIdentifier, TextureMapping.singleSlot(TextureSlot.LAYER0, new Material(modelIdentifier)), generator.modelOutput);

            ItemModel.Unbaked caseModel = ItemModelUtils.plainModel(modelIdentifier);
            switchCases.add(ItemModelUtils.when(buttonName, caseModel));
        }

        // Add fallback model for unknown buttons.
        ItemModel.Unbaked fallbackModel = ItemModelUtils.plainModel(Identifier.withDefaultNamespace("item/barrier"));

        // Create the main model for GuiButtonItem. It checks for custom data property 0.
        generator.itemModelOutput.accept(baseItem, ItemModelUtils.select(new CustomModelDataProperty(0), fallbackModel, switchCases));
    }
    
}
