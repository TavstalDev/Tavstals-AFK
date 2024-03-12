package com.tavstal.afk;

import java.util.concurrent.TimeUnit;

import com.tavstal.afk.utils.WorldUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        AFKCommon.init(event.getServer());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        AFKEvents.OnServerTick(event.getServer());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        AFKEvents.OnPlayerConnected(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        AFKEvents.OnPlayerDisconnected(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerChatted(ServerChatEvent event) {
        if (AFKConfig.DisableOnChatting.get())
            AFKEvents.OnChatted(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerSleepStarted(PlayerSleepInBedEvent event) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            if (!event.isCanceled() && event.getEntity().isSleeping()) {
			    AFKEvents.OnEntitySleepStarts(event.getEntity());
            }
		}, 5, TimeUnit.MILLISECONDS);
		executorService.shutdown();
        
    }

    @SubscribeEvent
    public void onPlayerSleepEnded(PlayerWakeUpEvent event) {
        AFKEvents.OnEntitySleepStopped(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerChangeWorld(PlayerChangedDimensionEvent event) {
        if (AFKConfig.DisableOnWorldChange.get())
            AFKEvents.OnPlayerChangesWorld(event.getEntity(), event.getFrom().location().toString(), event.getTo().location().toString());
    }

    @SubscribeEvent
    public void onPlayerRespawned(PlayerRespawnEvent event) {
        if (AFKConfig.DisableOnRespawn.get())
            AFKEvents.OnPlayerRespawned(event.getEntity());
    }

    // Left Click Block
    @SubscribeEvent
    public void onPlayerAttackedBlock(LeftClickBlock event) {
        if (AFKConfig.DisableOnAttackBlock.get())
            AFKEvents.OnAttackBlock(event.getEntity());
    }

    // Right Click Block
    @SubscribeEvent
    public void onPlayerUsedBlock(RightClickBlock event) {
        if (AFKConfig.DisableOnUseBlock.get())
            AFKEvents.OnUseBlock(event.getEntity());
    }

    // Left Click Entity
    @SubscribeEvent
    public void onPlayerAttackedEntity(AttackEntityEvent event) {
        if (AFKConfig.DisableOnAttackEntity.get())
            AFKEvents.OnAttackEntity(event.getEntity(), event.getTarget());  
    }

    // Right Click Entity
    @SubscribeEvent
    public void onPlayerUsedEntity(EntityInteract event) {
        if (AFKConfig.DisableOnUseEntity.get())
            AFKEvents.OnUseEntity(event.getEntity());
    }

    // Left Click Empty
    @SubscribeEvent
    public void onPlayerUsedItem(LeftClickEmpty event) {
        if (AFKConfig.DisableOnUseItem.get())
            AFKEvents.OnUseItem(event.getEntity());
    }

    // Right Click Item
    @SubscribeEvent
    public void onPlayerUsedItem(RightClickItem event) {
        if (AFKConfig.DisableOnUseItem.get())
            AFKEvents.OnUseItem(event.getEntity());
    }
}
