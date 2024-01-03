package com.tavstal.afk.commands;

import com.mojang.brigadier.*;
import net.minecraft.commands.*;
import com.mojang.brigadier.context.*;
import com.tavstal.afk.AFKCommon;
import com.tavstal.afk.utils.PlayerUtils;

import net.minecraft.world.entity.player.Player;

public class AFKCommand {
 
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("afk").executes((command) -> {
            return execute(command);
        }));
    }
    private static int execute(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player){
            Player player = (Player) command.getSource().getEntity();

            if (player == null)
                return 0;

            if (PlayerUtils.IsAFK(player.getStringUUID()))
                AFKCommon.ChangeAFKMode(player, false);
            else
                AFKCommon.ChangeAFKMode(player, true);
        }
        return Command.SINGLE_SUCCESS;
    }
}