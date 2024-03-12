package com.tavstal.afk.commands;

import java.text.MessageFormat;

import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import com.mojang.brigadier.context.*;
import com.tavstal.afk.AFKCommon;
import com.tavstal.afk.AFKTranslation;
import com.tavstal.afk.utils.EntityUtils;
import com.tavstal.afk.utils.ModUtils;
import com.tavstal.afk.utils.PlayerUtils;

import net.minecraft.world.entity.Entity;

public class AFKAdminCommand {
 
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("afkadmin").requires(source -> {
            return source.hasPermission(3);
        }).then(
            Commands.argument("player", StringArgumentType.word()).executes((command) -> {return execute(command);})
            ).executes((command) -> {
                return executeSyntax(command);
            })
        );
    }
    private static int execute(CommandContext<CommandSourceStack> command){
        Entity caller = command.getSource().getEntity();
        if (caller == null)
            return 0;

        String targetName = StringArgumentType.getString(command, "player");
            
        MinecraftServer server = caller.getServer();
        ServerPlayer target = server.getPlayerList().getPlayerByName(targetName);

        if (target == null)
        {
            caller.sendSystemMessage(ModUtils.Literal(MessageFormat.format(AFKTranslation.PlayerNotFoundMsg.get(), targetName)));
            return 0;
        }

        if (PlayerUtils.IsAFK(target.getStringUUID()))
        {
            AFKCommon.ChangeAFKMode(target, false);
            caller.sendSystemMessage(ModUtils.Literal(MessageFormat.format(AFKTranslation.AFKStatusChangedMsg.get(), EntityUtils.GetName(target), AFKTranslation.AFKStatusOnline.get())));
        }
        else
        {
            AFKCommon.ChangeAFKMode(target, true);
            caller.sendSystemMessage(ModUtils.Literal(MessageFormat.format(AFKTranslation.AFKStatusChangedMsg.get(), EntityUtils.GetName(target), AFKTranslation.AFKStatusAway.get())));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSyntax(CommandContext<CommandSourceStack> command) {
        command.getSource().getEntity().sendSystemMessage(ModUtils.Literal(AFKTranslation.AFKAdminCommandSyntax.get()));
        return 0;
    }
}