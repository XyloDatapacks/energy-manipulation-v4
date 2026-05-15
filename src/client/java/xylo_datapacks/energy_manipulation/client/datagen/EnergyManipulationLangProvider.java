package xylo_datapacks.energy_manipulation.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.EnumValueType;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EnergyManipulationLangProvider extends FabricLanguageProvider {

    public EnergyManipulationLangProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, @NonNull TranslationBuilder translationBuilder) {
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
                    simplePathToDefaultTranslation(GlyphsRegistry.makeGlyphSimplePath(entry.getKey().identifier()))
            );
            
            // Add all input pin names.
            entry.getValue().getInputPinDefinitions().forEach(inputPinDefinition -> {
                translationBuilder.add(
                        translationKey + "." + inputPinDefinition.pinName,
                        simplePathToDefaultTranslation(inputPinDefinition.pinName)
                );
            });
        });
    }

    public void generateValueTypesTranslations(@NonNull TranslationBuilder translationBuilder) {
        GlyphsRegistry.VALUE_TYPE.entrySet().forEach(entry -> {
            String translationKey = GlyphsRegistry.makeValueTypeTranslationKey(entry.getKey().identifier());

            // Add the value type.
            translationBuilder.add(
                    translationKey,
                    simplePathToDefaultTranslation(GlyphsRegistry.makeValueTypeSimplePath(entry.getKey().identifier()))
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
