package xylo_datapacks.energy_manipulation.font;


import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import xylo_datapacks.energy_manipulation.EnergyManipulation;

public class EnergyManipulationFonts {
    public static FontDescription.Resource SPELL_BOOK_GUI = new FontDescription.Resource(Identifier.fromNamespaceAndPath(EnergyManipulation.MOD_ID, "spell_book/gui"));

    public static void initialize() {
    }
}
