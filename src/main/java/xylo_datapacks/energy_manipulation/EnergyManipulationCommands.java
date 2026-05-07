package xylo_datapacks.energy_manipulation;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class EnergyManipulationCommands {

    public static int executeTestCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Called /command_two."), false);
        return 1;
    }

    public static int executeTestSubCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Called /sub_command_two."), false);
        return 1;
    }
    
}
