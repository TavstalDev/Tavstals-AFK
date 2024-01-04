package com.tavstal.afk.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.Packet;

@Mixin(PlayerMa)
public class ChatMixin {
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"), method = "method_31286", cancellable = true)
	public void onBroadkscastChatMessage(String string, CallbackInfo info) {
		
    }
}
