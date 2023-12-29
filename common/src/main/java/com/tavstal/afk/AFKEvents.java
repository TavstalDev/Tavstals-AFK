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

public class AFKEvents {
    
    public static InteractionResult OnPlayerConnected(Player player) {
        Constants.LOG.debug("PLAYER_CONNECT was called by {}", PlayerUtils.GetName(player));
		AFKCommon.PlayerLastMovements.put(player.getStringUUID(), new LastMovement(player.position(), player.blockPosition(), player.yHeadRot, LocalDateTime.now()));
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

    public static InteractionResult OnServerTick(MinecraftServer server) {
        for (var player : server.getPlayerList().getPlayers()) {
            var uuid = player.getStringUUID();
            var combatTracker = player.getCombatTracker();
            LastMovement lastMove = AFKCommon.PlayerLastMovements.get(uuid);
            
            // NOTES
            // HorizontalCollisions do not activate
            // isPushedByFluid is always true

            boolean isTeleported = MathUtils.Distance(player.blockPosition(), lastMove.LastBlockPosition) > 3;
            boolean isChangedBlockPosition = lastMove.LastBlockPosition.getX() != player.blockPosition().getX() || lastMove.LastBlockPosition.getY() != player.blockPosition().getY() || lastMove.LastBlockPosition.getZ() != player.blockPosition().getZ();
            
            boolean isInMovingVehicle = player.isPassenger();
            if (player.getVehicle() != null) {
                var vehicle = player.getVehicle();
                isInMovingVehicle = vehicle.getControllingPassenger() != player || vehicle.hasImpulse;
            }
            
            boolean isMovedUnwillingly = player.isInPowderSnow || player.isChangingDimension() || player.isInWater() || player.isInLava() || isInMovingVehicle || player.isFallFlying() || player.isHurt() || combatTracker.isTakingDamage() || isTeleported;
            // Should disable AFK no matter what because player had input
            boolean shouldDisableAFK = player.isSprinting() || player.isShiftKeyDown() || player.isUsingItem() || player.yHeadRot != lastMove.HeadRotation;

            // CHECK IF CHANGED POSITION
            if (isChangedBlockPosition)
            {
                // If the player is teleported then skips x ticks
                if (isTeleported)
                {
                    lastMove.TeleportTTL = 5;
                }

                // If the player is pushed by smth then skips x ticks 
                if (player.hasImpulse)
                {
                    lastMove.ImpulseTTL = 10;
                }

                // CHECK IF PLAYER MOVED WILLINGLY
                if ((!isMovedUnwillingly || shouldDisableAFK) && lastMove.TeleportTTL == 0 && lastMove.ImpulseTTL == 0)
                {
                    if (AFKCommon.CONFIG().DisableOnMove)
                        AFKCommon.ChangeAFKMode(player, false);

                    lastMove.Date = LocalDateTime.now();
                }

                if (lastMove.TeleportTTL > 0)
                    lastMove.TeleportTTL -= 1;

                if (lastMove.ImpulseTTL > 0)
                    lastMove.ImpulseTTL -= 1;

                lastMove.LastPosition = player.position();
                lastMove.HeadRotation = player.yHeadRot;
                lastMove.LastBlockPosition = player.blockPosition();
                AFKCommon.PlayerLastMovements.put(uuid, lastMove);
            }
            else
            {
                // AUTO AFK Check
                if (!(AFKCommon.GetAfkingPlayers().contains(uuid) || combatTracker.isInCombat()))
                {
                    if (Duration.between(lastMove.Date, LocalDateTime.now()).toSeconds() > AFKCommon.CONFIG().AutoAFKInterval && AFKCommon.CONFIG().AutoAFKInterval > 0) {
                        AFKCommon.ChangeAFKMode(player, true);
                    }
                }
            }
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
