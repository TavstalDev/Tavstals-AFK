package com.tavstal.afk.events;

public class ServerMessageEvent {
    
    public static final Event<ChatMessage> EVENT = EventFactory.createArrayBacked(ChatMessage.class, handlers -> (message, sender, params) -> {
		for (ChatMessage handler : handlers) {
			handler.onChatMessage(message, sender, params);
		}
	}); 
}
