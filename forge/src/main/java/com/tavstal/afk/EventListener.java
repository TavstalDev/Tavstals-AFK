package com.tavstal.afk;

import java.util.concurrent.TimeUnit;

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
        AFKCommon.init(event.getServer(), new CommonConfig(ForgeConfig.EnableDebugMode.get(), ForgeConfig.Prefix.get(), ForgeConfig.Suffix.get(),
			ForgeConfig.AutoAFKInterval.get(), ForgeConfig.PlayerPercentToResetTime.get(), ForgeConfig.DisableOnAttackBlock.get(),
			ForgeConfig.DisableOnAttackEntity.get(), ForgeConfig.DisableOnUseBlock.get(), ForgeConfig.DisableOnUseEntity.get(),
			ForgeConfig.DisableOnUseItem.get(), ForgeConfig.DisableOnWorldChange.get(), ForgeConfig.DisableOnChatting.get(),
			ForgeConfig.DisableOnMove.get(), ForgeConfig.DisableOnRespawn.get()));
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        AFKEvents.OnServerTick(event.getServer());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        AFKEvents.OnPlayerConnected(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        AFKEvents.OnPlayerDisconnected(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerChatted(ServerChatEvent event) {
        if (ForgeConfig.DisableOnChatting.get())
            AFKEvents.OnChatted(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerSleepStarted(PlayerSleepInBedEvent event) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            if (!event.isCanceled() && event.getPlayer().isSleeping()) {
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
        if (ForgeConfig.DisableOnWorldChange.get())
            AFKEvents.OnPlayerChangesWorld(event.getPlayer(), event.getTo().toString());
    }

    @SubscribeEvent
    public void onPlayerRespawned(PlayerRespawnEvent event) {
        if (ForgeConfig.DisableOnRespawn.get())
            AFKEvents.OnPlayerRespawned(event.getPlayer());
    }

    // Left Click Block
    @SubscribeEvent
    public void onPlayerAttackedBlock(LeftClickBlock event) {
        if (ForgeConfig.DisableOnAttackBlock.get())
            AFKEvents.OnAttackBlock(event.getPlayer());
    }

    // Right Click Block
    @SubscribeEvent
    public void onPlayerUsedBlock(RightClickBlock event) {
        if (ForgeConfig.DisableOnUseBlock.get())
            AFKEvents.OnUseBlock(event.getPlayer());
    }

    // Left Click Entity
    @SubscribeEvent
    public void onPlayerAttackedEntity(AttackEntityEvent event) {
        if (ForgeConfig.DisableOnAttackEntity.get())
            AFKEvents.OnAttackEntity(event.getPlayer(), event.getTarget());  
    }

    // Right Click Entity
    @SubscribeEvent
    public void onPlayerUsedEntity(EntityInteract event) {
        if (ForgeConfig.DisableOnUseEntity.get())
            AFKEvents.OnUseEntity(event.getPlayer());
    }

    // Left Click Empty
    @SubscribeEvent
    public void onPlayerUsedItem(LeftClickEmpty event) {
        if (ForgeConfig.DisableOnUseItem.get())
            AFKEvents.OnUseItem(event.getPlayer());
    }

    // Right Click Item
    @SubscribeEvent
    public void onPlayerUsedItem(RightClickItem event) {
        if (ForgeConfig.DisableOnUseItem.get())
            AFKEvents.OnUseItem(event.getPlayer());
    }
}
