package xylo_datapacks.energy_manipulation;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class EnergyManipulationCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(EnergyManipulationCommands::createTestCommand);
    }

    private static void createTestCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("test_command")
                .executes(EnergyManipulationCommands::executeTestCommand)
                .then(Commands.literal("sub_command").executes(EnergyManipulationCommands::executeTestSubCommand)));
    }
    
    public static int executeTestCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Called /command_two."), false);
        return 1;
    }

    public static int executeTestSubCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Called /sub_command_two."), false);
        return 1;
    }
    
}
