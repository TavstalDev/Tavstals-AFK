package com.tavstal.afk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.tavstal.afk.models.LastMovement;
import com.tavstal.afk.utils.EntityUtils;
import com.tavstal.afk.utils.PlayerUtils;
import com.tavstal.afk.utils.WorldUtils;
import com.tavstal.afk.utils.MathUtils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class AFKEvents {
    
    public static InteractionResult OnPlayerConnected(Player player) {
        Constants.LOG.debug("PLAYER_CONNECT was called by {}", PlayerUtils.GetName(player));
		AFKCommon.PlayerLastMovements.put(player.getStringUUID(), new LastMovement(player.position(), LocalDateTime.now()));
        return InteractionResult.PASS;
    }

    public static InteractionResult OnPlayerDisconnected(Player player) {
        Constants.LOG.debug("PLAYER_DISCONNECT was called by {}", PlayerUtils.GetName(player));
		var uuid = player.getStringUUID();
		AFKCommon.ChangeAFKMode(player, false);
		AFKCommon.PlayerLastMovements.remove(uuid);

        var server = player.getServer();
		var worldKey = WorldUtils.GetName(player.level);
		if (AFKCommon.SleepingPlayers.get(worldKey).contains(uuid)) {
			AFKCommon.SleepingPlayers.get(worldKey).remove(uuid);
            AFKCommon.SendChatMessage(player, "§c{0} stopped sleeping. {1} player(s) needed.", 
            PlayerUtils.GetName(player), MathUtils.Clamp(AFKCommon.GetRequiredPlayersToReset(server, worldKey), 0, server.getMaxPlayers()));
		}
        return InteractionResult.PASS;
    }

    public static InteractionResult OnPlayerRespawned(Player player) {
        Constants.LOG.debug("AFTER_RESPAWN was called by {}", PlayerUtils.GetName(player));
		AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResult OnPlayerMove(Player player, Vec3 from, Vec3 to) {

        return InteractionResult.PASS;
    }

    public static InteractionResult OnServerTick(MinecraftServer server) {
        for (var player : server.getPlayerList().getPlayers()) {
            var uuid = player.getStringUUID();
            LastMovement lastMove = AFKCommon.PlayerLastMovements.get(uuid);

            
            boolean movedUnwillingly = (lastMove.IsHurt && !player.isSprinting()) || player.isInPowderSnow || player.isInLava() || player.isInWater() || player.isPassenger();
            if ((player.isSprinting() || (PlayerUtils.GetForwardSpeed(player, lastMove.LastPosition) > 0.15F || PlayerUtils.GetSidewaysSpeed(player, lastMove.LastPosition) > 0.15F) && player.isOnGround() || player.isFallFlying() || player.isSwimming()) && !movedUnwillingly) {
                if (AFKCommon.CONFIG().DisableOnMove)
                    AFKCommon.ChangeAFKMode(player, false);

                lastMove.Date = LocalDateTime.now();
            }
            else {
                if (!(AFKCommon.GetAfkingPlayers().contains(uuid) && player.isDeadOrDying()))
                {
                    try
                    {
                        if (Duration.between(lastMove.Date, LocalDateTime.now()).toSeconds() > AFKCommon.CONFIG().AutoAFKInterval && AFKCommon.CONFIG().AutoAFKInterval > 0) {
                            AFKCommon.ChangeAFKMode(player, true);
                        }
                    } catch (Exception ex)
                    {
                        Constants.LOG.error(ex.getMessage());
                    }
                }
            }

            lastMove.LastPosition = player.position();
            if (lastMove.IsHurt && player.isOnGround())
                lastMove.IsHurt = false;
            AFKCommon.PlayerLastMovements.put(uuid, lastMove);
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult OnChatted(Player player) {
        Constants.LOG.debug("CHAT_MESSAGE was called by {}", PlayerUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResult OnAttackBlock(Player player) {
        Constants.LOG.debug("ATTACK_BLOCK was called by {}", PlayerUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResult OnAttackEntity(Player player, Entity entity) {
        Constants.LOG.debug("ATTACK_ENTITY was called by {}", PlayerUtils.GetName(player));

        if (AFKCommon.CONFIG().DisableOnAttackEntity)
		    AFKCommon.ChangeAFKMode(player, false);

        if (EntityUtils.IsPlayer(entity)) {
            Player target = (Player)entity;

            var lastMovement = AFKCommon.PlayerLastMovements.get(target.getStringUUID());
            lastMovement.IsHurt = true;
        }
		return InteractionResult.PASS;
    }

    public static InteractionResult OnUseBlock(Player player) {
        Constants.LOG.debug("USE_BLOCK was called by {}", PlayerUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResult OnUseEntity(Player player) {
        Constants.LOG.debug("USE_ENTITY was called by {}", PlayerUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResultHolder<ItemStack> OnUseItem(Player player) {
        Constants.LOG.debug("USE_ITEM was called by {}", PlayerUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResultHolder.pass(null);
    }

    public static InteractionResult OnPlayerChangesWorld(Player player, ServerLevel level) {
        Constants.LOG.debug("WORLD_CHANGE_1 was called by {}", PlayerUtils.GetName(player));
		AFKCommon.ChangeAFKMode(player, false);

		String uuid = player.getStringUUID();
		var worldKey = WorldUtils.GetName(level);
		if (AFKCommon.SleepingPlayers.get(worldKey).contains(uuid)) {
			AFKCommon.SleepingPlayers.get(worldKey).remove(uuid);
		}
        return InteractionResult.PASS;
    }

    public static InteractionResult OnPlayerChangesWorld(Player player, String worldKey) {
        Constants.LOG.debug("WORLD_CHANGE_2 was called by {}", PlayerUtils.GetName(player));
		AFKCommon.ChangeAFKMode(player, false);

		String uuid = player.getStringUUID();
		if (AFKCommon.SleepingPlayers.get(worldKey).contains(uuid)) {
			AFKCommon.SleepingPlayers.get(worldKey).remove(uuid);
		}
        return InteractionResult.PASS;
    }

    public static InteractionResult OnEntitySleepStarts(Entity entity) {
        if (!EntityUtils.IsPlayer(entity))
			return InteractionResult.PASS;

			Constants.LOG.debug("START_SLEEPING was called by {}", EntityUtils.GetName(entity));
			var server = entity.getServer();
			String uuid = entity.getStringUUID();
			var worldKey = WorldUtils.GetName(entity.getLevel());
			if (!AFKCommon.SleepingPlayers.get(worldKey).contains(uuid)) {
				AFKCommon.SleepingPlayers.get(worldKey).add(uuid);
			}

            int requiredPlayersToReset = AFKCommon.GetRequiredPlayersToReset(server, worldKey);
            AFKCommon.SendChatMessage(entity, "§e{0} is sleeping. {1} player(s) needed.", 
            EntityUtils.GetName(entity), MathUtils.Clamp(requiredPlayersToReset, 0, server.getMaxPlayers()));

			if (requiredPlayersToReset <= 0)
			{
                AFKCommon.SendChatMessage(entity, "§aSleeping through this night."); 
				AFKCommon.WakeUp(server.getLevel(entity.level.dimension()), server);
			}
        return InteractionResult.PASS;
    }

    public static InteractionResult OnEntitySleepStopped(Entity entity) {
        if (!EntityUtils.IsPlayer(entity))
			return InteractionResult.PASS;

			Constants.LOG.debug("STOP_SLEEPING was called by {}", EntityUtils.GetName(entity));
			var uuid = entity.getStringUUID();
			var server = entity.getServer();
			var worldKey = WorldUtils.GetName(entity.getLevel());
			
			if (AFKCommon.SleepingPlayers.get(worldKey).contains(uuid)) {
				AFKCommon.SleepingPlayers.get(worldKey).remove(uuid);
			}


			if (!worldKey.equals(AFKCommon.GetLastWorldSleepReset()))
			{
				ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
				executorService.schedule(() -> {
                    AFKCommon.SendChatMessage(entity, "§c{0} stopped sleeping. {1} player(s) needed.", EntityUtils.GetName(entity),
                    MathUtils.Clamp(AFKCommon.GetRequiredPlayersToReset(server, worldKey), 0, server.getMaxPlayers()));
				}, 10, TimeUnit.MILLISECONDS);
			}
        return InteractionResult.PASS;
    }
}
