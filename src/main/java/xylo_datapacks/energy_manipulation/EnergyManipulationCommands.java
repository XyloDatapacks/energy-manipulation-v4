package xylo_datapacks.energy_manipulation;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditor;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorGui;

public class EnergyManipulationCommands {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register(EnergyManipulationCommands::createCommands);
    }

    private static void createCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("energy_manipulation")
                .then(Commands.literal("spell_menu").executes(EnergyManipulationCommands::openSpellMenu)));
    }
    
    public static int openSpellMenu(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();

        if (player == null) {
            context.getSource().sendFailure(Component.literal("Only players can open a spell edit menu!"));
            return 0;
        }

        // Open gui
        SpellEditor spellEditor = new SpellEditor();
        SpellEditorGui gui = new SpellEditorGui(player, spellEditor);
        gui.open();
       
        context.getSource().sendSuccess(() -> {
            return Component.literal("Opening spell edit menu");
        }, false);
        return 1;
    }
    
}
