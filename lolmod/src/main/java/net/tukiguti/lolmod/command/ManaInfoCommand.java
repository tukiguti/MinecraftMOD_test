package net.tukiguti.lolmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.tukiguti.lolmod.stat.mana.ManaSystem;
import net.tukiguti.lolmod.stat.mana.capability.IMana;
import net.minecraftforge.common.util.LazyOptional;

public class ManaInfoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("manainfo")
                .executes(ManaInfoCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            LazyOptional<IMana> manaCapability = player.getCapability(ManaSystem.MANA);

            if (manaCapability.isPresent()) {
                IMana mana = manaCapability.orElseThrow(() -> new IllegalStateException("Mana capability is missing"));
                sendMessage(player, "Current Mana: " + mana.getMana() + "/" + mana.getMaxMana());
                sendMessage(player, "Player Level: " + player.experienceLevel);
                // 追加のデバッグ情報をここに表示
            } else {
                sendMessage(player, "Mana capability not found!");
            }
        }
        return 1;
    }

    private static void sendMessage(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }
}
