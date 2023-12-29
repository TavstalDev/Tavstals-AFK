package com.tavstal.afk;

import net.minecraftforge.event.ServerChatEvent;
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
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        AFKEvents.OnPlayerConnected(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        AFKEvents.OnPlayerDisconnected(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerChatted(ServerChatEvent event) {
        AFKEvents.OnChatted(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerSleepStarted(PlayerSleepInBedEvent event) {
        AFKEvents.OnEntitySleepStarts(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerSleepEnded(PlayerWakeUpEvent event) {
        AFKEvents.OnEntitySleepStopped(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerChangeWorld(PlayerChangedDimensionEvent event) {
        AFKEvents.OnPlayerChangesWorld(event.getEntity(), event.getTo().toString());
    }

    @SubscribeEvent
    public void onPlayerRespawned(PlayerRespawnEvent event) {
        AFKEvents.OnPlayerRespawned(event.getEntity());
    }

    // Left Click Block
    @SubscribeEvent
    public void onPlayerAttackedBlock(LeftClickBlock event) {
        AFKEvents.OnAttackBlock(event.getEntity());
    }

    // Right Click Block
    @SubscribeEvent
    public void onPlayerUsedBlock(RightClickBlock event) {
        AFKEvents.OnUseBlock(event.getEntity());
    }

    // Left Click Entity
    @SubscribeEvent
    public void onPlayerAttackedEntity(AttackEntityEvent event) {
        AFKEvents.OnAttackEntity(event.getEntity(), event.getTarget());  
    }

    // Right Click Entity
    @SubscribeEvent
    public void onPlayerUsedEntity(EntityInteract event) {
        AFKEvents.OnUseEntity(event.getEntity());
    }

    // Left Click Empty
    @SubscribeEvent
    public void onPlayerUsedItem(LeftClickEmpty event) {
        AFKEvents.OnUseItem(event.getEntity());
    }

    // Right Click Item
    @SubscribeEvent
    public void onPlayerUsedItem(RightClickItem event) {
        AFKEvents.OnUseItem(event.getEntity());
    }
}
