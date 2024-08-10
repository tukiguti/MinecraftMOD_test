package net.tukiguti.lolmod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.tukiguti.lolmod.util.LevelManager;

public class DebugCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lolmod_debug")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    LevelManager levelManager = LevelManager.get(player);
                    int level = levelManager.getLevel();
                    int xp = levelManager.getCurrentXP();
                    context.getSource().sendSuccess(Component.literal(
                            String.format("Player %s - Level: %d, XP: %d", player.getName().getString(), level, xp)
                    ), true);
                    return 1;
                })
        );
    }
}