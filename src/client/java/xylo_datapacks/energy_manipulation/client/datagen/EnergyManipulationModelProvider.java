package xylo_datapacks.energy_manipulation.client.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.client.renderer.item.properties.select.CustomModelDataProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.client.utils.EnergyManipulationModelTemplate;
import xylo_datapacks.energy_manipulation.entity.EnergyManipulationEntities;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.specialized.operation.OperatorGlyphInterface;
import xylo_datapacks.energy_manipulation.glyph.value_type.EnumValueType;
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
        generateSpellBookItemModel(itemModelGenerators, EnergyManipulationItems.SPELL_BOOK);
        itemModelGenerators.generateFlatItem(EnergyManipulationItems.SPELL_SCROLL, ModelTemplates.FLAT_ITEM);
        
        generateDynamicGuiItemModels(itemModelGenerators, EnergyManipulationItems.GUI_BUTTON);
        generateDynamicShapeItemModels(itemModelGenerators, EnergyManipulationItems.SHAPE_DISPLAY);
    }

    public void generateSpellBookItemModel(ItemModelGenerators generator, Item item) {
        ItemModel.Unbaked flatModel = ItemModelUtils.plainModel(generator.createFlatItemModel(item, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked inHandModel = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_closed"));
        
        ItemModel.Unbaked open = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_open"));
        ItemModel.Unbaked charging0 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_charging0"));
        ItemModel.Unbaked charging1 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_charging1"));
        ItemModel.Unbaked charging2 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_charging2"));
        ItemModel.Unbaked inHandWithCharge = ItemModelUtils.conditional(
                ItemModelUtils.isUsingItem(),
                ItemModelUtils.rangeSelect(new UseDuration(false), 0.05F, open,
                        ItemModelUtils.override(charging0, 0.2F),
                        ItemModelUtils.override(charging1, 0.55F), 
                        ItemModelUtils.override(charging2, 0.9F)),
                inHandModel
        );

        List<SelectItemModel.SwitchCase<String>> switchCases = new ArrayList<>();
        switchCases.add(ItemModelUtils.when("open_book", open));
        ItemModel.Unbaked inHandWithCustomModelData = ItemModelUtils.select(new CustomModelDataProperty(0), inHandWithCharge, switchCases);
        
        generator.itemModelOutput.accept(item, ItemModelGenerators.createFlatModelDispatch(flatModel, inHandWithCustomModelData));
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
        // Enum values
        List<EnumValueType<?>> enumValueTypes = new ArrayList<>(GlyphsRegistry.VALUE_TYPE.stream()
                .filter(EnumValueType.class::isInstance)
                .map(element -> (EnumValueType<?>) element)
                .toList());
        enumValueTypes.forEach(enumValueType -> {
            allGlyphs.addAll(enumValueType.getValuesId().stream()
                    .map(enumValue -> GlyphsRegistry.getValueTypePath(enumValueType) + "/" + enumValue)
                    .toList());
        });
        // Type-specific glyphs
        GlyphsRegistry.GLYPH.stream()
                .filter(glyph -> glyph.getEditorData().bHasTypeDependentTexture)
                .forEach(typeSpecificGlyph -> {
                    allGlyphs.addAll(GlyphsRegistry.VALUE_TYPE.stream()
                            .filter(GlyphValueType::hasValueSelector)
                            .map(valueType -> GlyphsRegistry.getGlyphTypeSpecifyPath(typeSpecificGlyph, valueType))
                            .toList());
                });
        // Operator glyphs pin separator
        allGlyphs.addAll(GlyphsRegistry.GLYPH.stream()
                .filter(OperatorGlyphInterface.class::isInstance)
                .map(glyph -> GlyphsRegistry.getGlyphPath(glyph) + "_separator")
                .toList());
        // Glyph buttons (treating them as glyphs since they need to be scaled too)
        allGlyphs.addAll(SpellEditorButtonsRegistry.SPELL_EDITOR_GLYPH_BUTTON.keySet().stream().map(Identifier::getPath).toList());

        
        // generate switch cases and their models
        List<SelectItemModel.SwitchCase<String>> switchCases = new ArrayList<>();
        createAndAddButtonModelInstanceCases(generator, switchCases, allButtons, new Vector3f(1.f, 1.f, 1.f));
        createAndAddButtonModelInstanceCases(generator, switchCases, allGlyphs, new Vector3f(2.f, 2.f, 1.f));

        // Add fallback model for unknown buttons.
        ItemModel.Unbaked fallbackModel = ItemModelUtils.plainModel(Identifier.withDefaultNamespace("item/barrier"));

        // Create the main model for GuiButtonItem. It checks for custom data property 0.
        generator.itemModelOutput.accept(baseItem, ItemModelUtils.select(new CustomModelDataProperty(0), fallbackModel, switchCases), new ClientItem.Properties(true, true, 1.F));
    }
    
    private void createAndAddButtonModelInstanceCases(ItemModelGenerators generator, List<SelectItemModel.SwitchCase<String>> destination, List<String> buttonNames, Vector3f scale) {
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
    
    public void generateDynamicShapeItemModels(ItemModelGenerators generator, Item baseItem) {
        List<String> allShapes = new ArrayList<>();

        // Add all shapes
        allShapes.addAll(EnergyManipulationEntities.SHAPE.keySet().stream().map(Identifier::getPath).toList());
        
        // generate switch cases and their models
        List<SelectItemModel.SwitchCase<String>> switchCases = new ArrayList<>();
        createAndAddShapeModelInstanceCases(generator, switchCases, allShapes, new Vector3f(1.f, 1.f, 1.f));

        // Add fallback model for unknown buttons.
        ItemModel.Unbaked fallbackModel = ItemModelUtils.plainModel(Identifier.withDefaultNamespace("item/barrier"));

        // Create the main model for GuiButtonItem. It checks for custom data property 0.
        generator.itemModelOutput.accept(baseItem, ItemModelUtils.select(new CustomModelDataProperty(0), fallbackModel, switchCases), new ClientItem.Properties(true, true, 1.F));
    }

    private void createAndAddShapeModelInstanceCases(ItemModelGenerators generator, List<SelectItemModel.SwitchCase<String>> destination, List<String> shapeNames, Vector3f scale) {
        for (String shapeName : shapeNames) {
            Identifier modelIdentifier = Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "item/shape/" + shapeName);

            //ModelTemplates.FLAT_ITEM.create(modelIdentifier, TextureMapping.singleSlot(TextureSlot.LAYER0, new Material(modelIdentifier)), generator.modelOutput);

            ItemModel.Unbaked caseModel = ItemModelUtils.plainModel(modelIdentifier);
            destination.add(ItemModelUtils.when(shapeName, caseModel));
        }
    }
}
