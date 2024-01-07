package com.tavstal.afk.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public interface ChatMessageSentCallback {
    Event<ChatMessageSentCallback> EVENT = EventFactory.createArrayBacked(ChatMessageSentCallback.class,
        (listeners) -> (player, message, component) -> {
            for (ChatMessageSentCallback listener : listeners) {
            InteractionResult result = listener.interact(player, message, component);
     
                if(result != InteractionResult.PASS) {
                    return result;
                }
            }
        
        
        return InteractionResult.PASS;
    });
     
    InteractionResult interact(ServerPlayer player, String message, Component component);
}
