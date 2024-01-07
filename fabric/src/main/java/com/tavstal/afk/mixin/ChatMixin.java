package com.tavstal.afk.mixin;

import com.tavstal.afk.callbacks.ChatMessageSentCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.world.InteractionResult;


@Mixin(ServerGamePacketListenerImpl.class)
public class ChatMixin {
    
    @Shadow public ServerPlayer player;

    @Inject(method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V", at = @At("HEAD"), cancellable = true)
    private void injectChatEvent(TextFilter.FilteredText filteredText, CallbackInfo ci) {
        Component message = new TextComponent(filteredText.getRaw());
        if (message.getString().startsWith("/"))
            return;

        InteractionResult result = ChatMessageSentCallback.EVENT.invoker().interact(player, message.getString(), message);
        if (result == InteractionResult.FAIL)
            ci.cancel();
    }
}
