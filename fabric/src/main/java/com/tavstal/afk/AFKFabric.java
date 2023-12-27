package com.tavstal.afk;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;

public class AFKFabric implements ModInitializer {
    
	private boolean _isInitialized = false;
	public final CommonConfig CONFIG = new CommonConfig();

    @Override
    public void onInitialize() {
        
        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");

		ServerTickEvents.START_SERVER_TICK.register((server) -> {
			if (_isInitialized)
			{
				return;
			}

			_isInitialized = true;
			AFKCommon.init(server, new CommonConfig());
		});

		if (CONFIG.DisableOnAttackBlock())
		{
			// Attack Block
			AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> AFKEvents.OnAttackBlock(player));
		}

		if (CONFIG.DisableOnAttackEntity())
		{
			// Attack Entity
			AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> AFKEvents.OnAttackEntity(player));
		}

		if (CONFIG.DisableOnUseBlock())
		{
			// Use Block
			UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> AFKEvents.OnUseBlock(player));
		}

		if (CONFIG.DisableOnUseEntity())
		{
			// Use Entity
			UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> AFKEvents.OnUseEntity(player));
		}
		
		if (CONFIG.DisableOnUseItem()) 
		{
			// Use Item
			UseItemCallback.EVENT.register((player, world, hand) -> AFKEvents.OnUseItem(player));
		}

		if (CONFIG.DisableOnWorldChange())
		{
			// World Change
			ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> 
			AFKEvents.OnPlayerChangesWorld(player, origin));
		}

		// Sleeping Start
		EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> AFKEvents.OnEntitySleepStarts(entity));

		// Sleep Stop
		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> AFKEvents.OnEntitySleepStopped(entity));

		if (CONFIG.DisableOnRespawn())
		{
			// Respawn
			ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> AFKEvents.OnPlayerRespawned(newPlayer));
		}

		// Player Connect
		ServerPlayConnectionEvents.JOIN.register((handler, sender, client) -> AFKEvents.OnPlayerConnected(handler.player));

		// Player Disconnect
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> AFKEvents.OnPlayerDisconnected(handler.player));

		// Server Tick
		ServerTickEvents.END_SERVER_TICK.register((server) -> AFKEvents.OnServerTick(server));

		// Register Chatted 
		if (CONFIG.DisableOnChatting())
		{
			ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> AFKEvents.OnChatted(sender));
		}

		// Register commands
		/*CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(LiteralArgumentBuilder.literal("afk")
			.executes(context -> {
			// For versions below 1.19, replace "Text.literal" with "new LiteralText".
			// For versions below 1.20, remode "() ->" directly.
			var source = context.getSource();
			var player = context.player;

			if (AFKCommon.GetAfkingPlayers().contains(player.getUuidAsString()))
				AFKCommon.ChangeAFKMode(player, false);
			else
				AFKCommon.ChangeAFKMode(player, true);
			return 1;
		})));*/
    }
}
