package com.tavstal.afk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.tavstal.afk.models.PlayerData;
import com.tavstal.afk.models.TeamData;
import com.tavstal.afk.utils.EntityUtils;
import com.tavstal.afk.utils.PlayerUtils;
import com.tavstal.afk.utils.WorldUtils;
import com.tavstal.afk.utils.MathUtils;
import com.tavstal.afk.utils.ModUtils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AFKEvents {
    
    public static InteractionResult OnPlayerConnected(Player player) {
        Constants.LOG.debug("PLAYER_CONNECT was called by {}", EntityUtils.GetName(player));
        AFKCommon.PutPlayerData(player.getStringUUID(), new PlayerData(EntityUtils.GetPosition(player), EntityUtils.GetBlockPosition(player), player.yHeadRot, LocalDateTime.now()));
        
        PlayerData data = AFKCommon.GetPlayerData(player.getStringUUID());
        if (data != null)
        {
            data.Teams.add(new TeamData(WorldUtils.GetDisplayName(EntityUtils.GetLevel(player)), 0));
            AFKCommon.PutPlayerData(player.getStringUUID(), data);
            PlayerUtils.RefreshPlayerTeams(player);
        }

        return InteractionResult.PASS;
    }

    public static InteractionResult OnPlayerDisconnected(Player player) {
        Constants.LOG.debug("PLAYER_DISCONNECT was called by {}", EntityUtils.GetName(player));
		var uuid = player.getStringUUID();
		AFKCommon.ChangeAFKMode(player, false);
		AFKCommon.RemovePlayerData(uuid);

        var server = player.getServer();
        if (server == null)
        {
            Constants.LOG.error("OnPlayerDisconnected -> Failed to get the server.");
            return InteractionResult.PASS;
        }

		var worldKey = WorldUtils.GetName(EntityUtils.GetLevel(player));
		if (player.isSleeping()) {
            ModUtils.BroadcastMessageByWorld(player, AFKTranslation.SleepStopMsg.get(), worldKey,
            EntityUtils.GetName(player), MathUtils.Clamp(AFKCommon.GetRequiredPlayersToReset(server, worldKey), 0, server.getMaxPlayers()));
            PlayerUtils.RemoveFromTeam(player, "sleep");
		}

        int requiredPlayersToReset = AFKCommon.GetRequiredPlayersToReset(server, worldKey);
		if (requiredPlayersToReset <= 0)
		{
            ModUtils.BroadcastMessageByWorld(player, AFKTranslation.SleepResetMsg.get(), worldKey); 
			AFKCommon.WakeUp(EntityUtils.GetServerLevel(player), server);
		}
        return InteractionResult.PASS;
    }

    public static InteractionResult OnPlayerRespawned(Player oldPlayer, Player newPlayer) {
        Constants.LOG.debug("AFTER_RESPAWN was called by {}", EntityUtils.GetName(newPlayer));
		AFKCommon.ChangeAFKMode(newPlayer, false);

        if (AFKConfig.EnableWorldTab.get())
        {
            if (oldPlayer != null)
            {
                Level oldLevel = EntityUtils.GetLevel(oldPlayer);
                Level newLevel = EntityUtils.GetLevel(newPlayer);

                if (newLevel.dimension() != oldLevel.dimension()) {
                    
                    PlayerData data = AFKCommon.GetPlayerData(newPlayer.getStringUUID());
                    if (data == null)
                        return InteractionResult.PASS;

                    data.Teams.removeIf((x) -> x.Name == WorldUtils.GetDisplayName(oldLevel));
                    TeamData worldTeam = new TeamData(WorldUtils.GetDisplayName(newLevel), 0);
                    if (!data.Teams.contains(worldTeam))
                    {
                        data.Teams.add(worldTeam);
                    }
                    AFKCommon.PutPlayerData(newPlayer.getStringUUID(), data);
                    PlayerUtils.RefreshPlayerTeams(newPlayer);
            
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult OnServerTick(MinecraftServer server) {
        for (var player : server.getPlayerList().getPlayers()) {
            var uuid = player.getStringUUID();
            //var combatTracker = player.getCombatTracker();
            PlayerData data = AFKCommon.GetPlayerData(uuid);
            
            if (data == null)
                continue;

            if (data.LastBlockPosition == null || data.LastPosition == null)
                continue;

            // NOTES
            // HorizontalCollisions do not activate
            // isPushedByFluid is always true

            boolean isTeleported = MathUtils.Distance(player.blockPosition(), data.LastBlockPosition) > 3;
            boolean isChangedBlockPosition = data.LastBlockPosition.getX() != player.blockPosition().getX() || data.LastBlockPosition.getY() != player.blockPosition().getY() || data.LastBlockPosition.getZ() != player.blockPosition().getZ();
            
            boolean isInMovingVehicle = player.isPassenger();
            var playerVehicle = player.getVehicle();
            if (playerVehicle != null) {
                isInMovingVehicle = playerVehicle.getControllingPassenger() != player || playerVehicle.hasImpulse;
            }
            
            boolean isMovedUnwillingly = player.isInPowderSnow || player.isChangingDimension() || player.isInWater() || player.isInLava() || isInMovingVehicle || player.isFallFlying() || player.isHurt() || isTeleported;
            // Should disable AFK no matter what because player had input
            boolean shouldDisableAFK = player.isSprinting() || player.isShiftKeyDown() || player.isUsingItem() || player.yHeadRot != data.HeadRotation;

            // CHECK IF CHANGED POSITION
            if (isChangedBlockPosition)
            {
                // If the player is teleported then skips x ticks
                if (isTeleported)
                {
                    data.TeleportTTL = 5;
                }

                // If the player is pushed by smth then skips x ticks 
                if (player.hasImpulse)
                {
                    data.ImpulseTTL = 10;
                }

                // CHECK IF PLAYER MOVED WILLINGLY
                if ((!isMovedUnwillingly || shouldDisableAFK) && data.TeleportTTL == 0 && data.ImpulseTTL == 0)
                {
                    if (AFKConfig.DisableOnMove.get())
                        AFKCommon.ChangeAFKMode(player, false);

                    data.Date = LocalDateTime.now();
                }

                if (data.TeleportTTL > 0)
                    data.TeleportTTL -= 1;

                if (data.ImpulseTTL > 0)
                    data.ImpulseTTL -= 1;

                data.LastPosition = EntityUtils.GetPosition(player);
                data.HeadRotation = player.yHeadRot;
                data.LastBlockPosition = EntityUtils.GetBlockPosition(player);
                AFKCommon.PutPlayerData(uuid, data);
            }
            else
            {
                // AUTO AFK Check
                if (!(PlayerUtils.IsAFK(uuid) || player.isHurt()))
                {
                    if (Duration.between(data.Date, LocalDateTime.now()).toSeconds() > AFKConfig.AutoAFKInterval.get() && AFKConfig.AutoAFKInterval.get() > 0) {
                        AFKCommon.ChangeAFKMode(player, true);
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult OnChatted(Player player) {
        Constants.LOG.debug("CHAT_MESSAGE was called by {}", EntityUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResult OnAttackBlock(Player player) {
        Constants.LOG.debug("ATTACK_BLOCK was called by {}", EntityUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResult OnAttackEntity(Player player, Entity entity) {
        Constants.LOG.debug("ATTACK_ENTITY was called by {}", EntityUtils.GetName(player));
        if (AFKConfig.DisableOnAttackEntity.get())
		    AFKCommon.ChangeAFKMode(player, false);
		return InteractionResult.PASS;
    }

    public static InteractionResult OnUseBlock(Player player) {
        Constants.LOG.debug("USE_BLOCK was called by {}", EntityUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResult OnUseEntity(Player player) {
        Constants.LOG.debug("USE_ENTITY was called by {}", EntityUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResultHolder<ItemStack> OnUseItem(Player player) {
        Constants.LOG.debug("USE_ITEM was called by {}", EntityUtils.GetName(player));
        AFKCommon.ChangeAFKMode(player, false);
        return InteractionResultHolder.pass(null);
    }

    public static InteractionResult OnPlayerChangesWorld(Player player, ServerLevel level) {
        Constants.LOG.debug("WORLD_CHANGE_1 was called by {}", EntityUtils.GetName(player));
		AFKCommon.ChangeAFKMode(player, false);
        return InteractionResult.PASS;
    }

    public static InteractionResult OnPlayerChangesWorld(Player player, String worldKey) {
        Constants.LOG.debug("WORLD_CHANGE_2 was called by {}", EntityUtils.GetName(player));
		AFKCommon.ChangeAFKMode(player, false);

        return InteractionResult.PASS;
    }

    public static InteractionResult OnPlayerChangesWorld(Player player, Level fromLevel, Level toLevel) {
        Constants.LOG.debug("WORLD_CHANGE_2 was called by {}", EntityUtils.GetName(player));
		AFKCommon.ChangeAFKMode(player, false);

        if (AFKConfig.EnableWorldTab.get())
        {
            PlayerData data = AFKCommon.GetPlayerData(player.getStringUUID());
            if (data == null)
                return InteractionResult.PASS;

            data.Teams.removeIf((x) -> x.Name == WorldUtils.GetDisplayName(fromLevel));
            TeamData worldTeam = new TeamData(WorldUtils.GetDisplayName(toLevel), 0);
            if (!data.Teams.contains(worldTeam))
            {
                data.Teams.add(worldTeam);
            }
            AFKCommon.PutPlayerData(player.getStringUUID(), data);
            PlayerUtils.RefreshPlayerTeams(player);
        }

        return InteractionResult.PASS;
    }

    public static InteractionResult OnEntitySleepStarts(Entity entity) {
        if (!EntityUtils.IsPlayer(entity))
			return InteractionResult.PASS;

			Constants.LOG.debug("START_SLEEPING was called by {}", EntityUtils.GetName(entity));
			var server = entity.getServer();
            if (server == null)
            {
                Constants.LOG.error("OnEntitySleepStarts -> Failed to get the server.");
                return InteractionResult.PASS;
            }

			var worldKey = WorldUtils.GetName(EntityUtils.GetLevel(entity));

            int requiredPlayersToReset = AFKCommon.GetRequiredPlayersToReset(server, worldKey);
            if (server.getPlayerCount() > 1)
            {
                ModUtils.BroadcastMessageByWorld(entity, AFKTranslation.SleepStartMsg.get(), worldKey, 
                EntityUtils.GetName(entity), MathUtils.Clamp(requiredPlayersToReset, 0, server.getMaxPlayers()));
            }

            if (AFKConfig.EnableSleepTab.get())
            {
                //PlayerUtils.AddToTeam((Player)entity, "sleep");
                PlayerData data = AFKCommon.GetPlayerData(entity.getStringUUID());
                if (data != null)
                {
                    if (!data.Teams.contains(Constants.TEAM_SLEEP))
                    {
                        data.Teams.add(Constants.TEAM_SLEEP);
                        AFKCommon.PutPlayerData(entity.getStringUUID(), data);
                        PlayerUtils.RefreshPlayerTeams((Player)entity);
                    }
                }
            }

			if (requiredPlayersToReset <= 0)
			{
                if (server.getPlayerCount() > 1)
                    ModUtils.BroadcastMessageByWorld(entity, AFKTranslation.SleepResetMsg.get(), worldKey); 
				AFKCommon.WakeUp(EntityUtils.GetServerLevel(entity), server);
			}
        return InteractionResult.PASS;
    }

    public static InteractionResult OnEntitySleepStopped(Entity entity) {
        if (!EntityUtils.IsPlayer(entity))
			return InteractionResult.PASS;

			Constants.LOG.debug("STOP_SLEEPING was called by {}", EntityUtils.GetName(entity));
			var server = entity.getServer();
            if (server == null)
            {
                Constants.LOG.error("OnEntitySleepStopped -> Failed to get the server.");
                return InteractionResult.PASS;
            }

            if (AFKConfig.EnableSleepTab.get())
            {
                //PlayerUtils.RemoveFromTeam((Player)entity, "sleep");
                PlayerData data = AFKCommon.GetPlayerData(entity.getStringUUID());
                if (data != null)
                {
                    if (data.Teams.contains(Constants.TEAM_SLEEP))
                    {
                        data.Teams.remove(Constants.TEAM_SLEEP);
                        AFKCommon.PutPlayerData(entity.getStringUUID(), data);
                        PlayerUtils.RefreshPlayerTeams((Player)entity);
                    }
                }
            }
            

			var worldKey = WorldUtils.GetName(EntityUtils.GetLevel(entity));
			if (!worldKey.equals(AFKCommon.GetLastWorldSleepReset()))
			{
				ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
				executorService.schedule(() -> {
                    if (server.getPlayerCount() > 1)
                        ModUtils.BroadcastMessageByWorld(entity, AFKTranslation.SleepStopMsg.get(), worldKey, EntityUtils.GetName(entity),
                        MathUtils.Clamp(AFKCommon.GetRequiredPlayersToReset(server, worldKey), 0, server.getMaxPlayers()));
				}, 10, TimeUnit.MILLISECONDS);
			}
        return InteractionResult.PASS;
    }
}
