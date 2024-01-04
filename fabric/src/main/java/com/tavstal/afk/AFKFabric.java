package com.tavstal.afk;

import com.tavstal.afk.platform.FabricPlatformHelper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import com.google.common.reflect.Reflection;

public class AFKFabric implements ModInitializer {
    
	private boolean _isInitialized = false;

    @Override
    public void onInitialize() {
        
		// CHECK IF THE MOD IS LOADED ON CLIENT
		var helper = new FabricPlatformHelper();
		if (helper.isClientSide()) {
			Constants.LOG.error("{} should be only loaded on the server.", Constants.MOD_NAME);
			return;
		}

		// SERVER START TICK EVENT
		ServerTickEvents.START_SERVER_TICK.register((server) -> {
			if (_isInitialized)
			{
				return;
			}

			_isInitialized = true;
			Reflection.initialize(FabricConfig.class);
			AFKCommon.init(server, new CommonConfig(FabricConfig.EnableDebugMode.get(), FabricConfig.Prefix.get(), FabricConfig.Suffix.get(),
			FabricConfig.AutoAFKInterval.get(), FabricConfig.PlayerPercentToResetTime.get(), FabricConfig.DisableOnAttackBlock.get(),
			FabricConfig.DisableOnAttackEntity.get(), FabricConfig.DisableOnUseBlock.get(), FabricConfig.DisableOnUseEntity.get(),
			FabricConfig.DisableOnUseItem.get(), FabricConfig.DisableOnWorldChange.get(), FabricConfig.DisableOnChatting.get(),
			FabricConfig.DisableOnMove.get(), FabricConfig.DisableOnRespawn.get()));
		});

		// Player Connected Event
		ServerPlayConnectionEvents.JOIN.register((handler, sender, client) -> AFKEvents.OnPlayerConnected(handler.player));

		// Player Disconnected Event
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> AFKEvents.OnPlayerDisconnected(handler.player));

		// Server Tick Event
		ServerTickEvents.END_SERVER_TICK.register((server) -> AFKEvents.OnServerTick(server));

		// Sleeping Started Event
		EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> AFKEvents.OnEntitySleepStarts(entity));

		// Sleeping Stopped Event
		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> AFKEvents.OnEntitySleepStopped(entity));

		// Attack Block Event
		if (FabricConfig.DisableOnAttackBlock.get())
		{
			AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> AFKEvents.OnAttackBlock(player));
		}

		// Attack Entity Event
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> AFKEvents.OnAttackEntity(player, entity));

		// Use Block Event
		if (FabricConfig.DisableOnUseBlock.get())
		{
			UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> AFKEvents.OnUseBlock(player));
		}

		// Use Entity Event
		if (FabricConfig.DisableOnUseEntity.get())
		{
			UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> AFKEvents.OnUseEntity(player));
		}
		
		// Use Item Event
		if (FabricConfig.DisableOnUseItem.get()) 
		{
			UseItemCallback.EVENT.register((player, world, hand) -> AFKEvents.OnUseItem(player));
		}

		// Player World Change Event
		if (FabricConfig.DisableOnWorldChange.get())
		{
			ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> 
			AFKEvents.OnPlayerChangesWorld(player, origin));
		}

		// Player Respawned Event
		if (FabricConfig.DisableOnRespawn.get())
		{
			ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> AFKEvents.OnPlayerRespawned(newPlayer));
		}

		// Player Chatted Event
		if (FabricConfig.DisableOnChatting.get())
		{
			
			ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> AFKEvents.OnChatted(sender));
		}
    }
}
