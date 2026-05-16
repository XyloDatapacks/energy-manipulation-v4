package xylo_datapacks.energy_manipulation.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;

import java.util.concurrent.CompletableFuture;

public class EnergyManipulationRecipeProvider extends FabricRecipeProvider {

    public EnergyManipulationRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registries, @NonNull RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                shapeless(RecipeCategory.TOOLS, EnergyManipulationItems.SPELL_BOOK)
                        .requires(Items.WRITABLE_BOOK)
                        .requires(Items.GLOWSTONE_DUST)
                        .requires(Items.REDSTONE)
                        .requires(Items.LAPIS_LAZULI)
                        .unlockedBy(getHasName(Items.WRITABLE_BOOK), has(Items.WRITABLE_BOOK))
                        .save(output);

                shapeless(RecipeCategory.TOOLS, EnergyManipulationItems.SPELL_SCROLL)
                        .requires(Items.PAPER)
                        .requires(Items.BLAZE_POWDER)
                        .unlockedBy(getHasName(EnergyManipulationItems.SPELL_BOOK), has(EnergyManipulationItems.SPELL_BOOK))
                        .save(output);

                TransmuteRecipeBuilder.transmute(RecipeCategory.TOOLS, Ingredient.of(EnergyManipulationItems.SPELL_SCROLL), Ingredient.of(Items.BLAZE_POWDER), new ItemStackTemplate(EnergyManipulationItems.SPELL_SCROLL, 2))
                        .unlockedBy(getHasName(EnergyManipulationItems.SPELL_SCROLL), has(EnergyManipulationItems.SPELL_SCROLL))
                        .save(output, BuiltInRegistries.ITEM.getKey(EnergyManipulationItems.SPELL_SCROLL) + "_copy");
            }
        };
    }

    @Override
    public @NonNull String getName() {
        return "EnergyManipulationRecipeProvider";
    }
}
