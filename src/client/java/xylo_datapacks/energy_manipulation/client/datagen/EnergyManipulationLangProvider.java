package xylo_datapacks.energy_manipulation.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.EnergyManipulation;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.EnumValueType;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EnergyManipulationLangProvider extends FabricLanguageProvider {

    public EnergyManipulationLangProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, @NonNull TranslationBuilder translationBuilder) {
        try {
            Path existingFilePath = this.packOutput.getModContainer().findPath("assets/" + EnergyManipulation.MOD_ID + "/lang/en_us.existing.json").get();
            translationBuilder.add(existingFilePath);
        } catch (IOException e) {
            EnergyManipulation.LOGGER.error("Failed to merge manual language files!", e);
        }

        translationBuilder.add(EnergyManipulationItems.SPELL_BOOK, "Spell Book");
        translationBuilder.add(EnergyManipulationItems.SPELL_SCROLL, "Spell Scroll");

        generateGlyphTranslations(translationBuilder);
        generateValueTypesTranslations(translationBuilder);
    }

    public void generateGlyphTranslations(@NonNull TranslationBuilder translationBuilder) {
        GlyphsRegistry.GLYPH.entrySet().forEach(entry -> {
            String translationKey = GlyphsRegistry.makeGlyphTranslationKey(entry.getKey().identifier());
            
            // Add the glyph itself.
            translationBuilder.add(
                    translationKey, 
                    simplePathToDefaultTranslation(entry.getKey().identifier().getPath())
            );
            //translationBuilder.add(translationKey + ".description", "");
            
            // Add all input pin names.
            entry.getValue().getInputPinDefinitions().forEach(inputPinDefinition -> {
                String pinTranslationKey = translationKey + "." + inputPinDefinition.pinName;
                translationBuilder.add(
                        pinTranslationKey,
                        simplePathToDefaultTranslation(inputPinDefinition.pinName)
                );
                //translationBuilder.add(pinTranslationKey + ".description", "");
            });
        });
    }

    public void generateValueTypesTranslations(@NonNull TranslationBuilder translationBuilder) {
        GlyphsRegistry.VALUE_TYPE.entrySet().forEach(entry -> {
            String translationKey = GlyphsRegistry.makeValueTypeTranslationKey(entry.getKey().identifier());

            // Add the value type.
            translationBuilder.add(
                    translationKey,
                    simplePathToDefaultTranslation(entry.getKey().identifier().getPath())
            );

            // If enum, add its values.
            if (entry.getValue() instanceof EnumValueType<?> enumValueType) {
                enumValueType.getValuesId().forEach(valueId -> {
                    translationBuilder.add(
                            translationKey + "." + valueId,
                            simplePathToDefaultTranslation(valueId)
                    );
                });
            }
        });
    }

    public static String simplePathToDefaultTranslation(String simplePath) {
        return Arrays.stream(simplePath.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
