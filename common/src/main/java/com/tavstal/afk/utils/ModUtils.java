package com.tavstal.afk.utils;

import java.text.MessageFormat;

import com.mojang.brigadier.LiteralMessage;
import com.tavstal.afk.Constants;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class ModUtils {

   public static Component Literal(String text) {
      return net.minecraft.network.chat.ComponentUtils.fromMessage(new LiteralMessage(text));
   }

    public static void SendChatMessage(Entity entity, String text) {
      var server = entity.getServer();
      if (server == null)
      {
         Constants.LOG.error("SendChatMessage -> Failed to get the server.");
         return;
      }

      var messageComponent = Literal(text);
      // Send Message to the server
      server.sendMessage(messageComponent, null);
      // Send Message to all clients
      for (var player : server.getPlayerList().getPlayers()) {
         player.sendMessage(messageComponent, player.getUUID());
      }
   }

    public static void SendChatMessage(Entity entity, String text, Object ... args) {
      var server = entity.getServer();
      if (server == null)
      {
         Constants.LOG.error("SendChatMessage -> Failed to get the server.");
         return;
      }

      var messageComponent = Literal(MessageFormat.format(text, args));
      // Send Message to the server
      server.sendMessage(messageComponent, null);
      // Send Message to all clients
      for (var player : server.getPlayerList().getPlayers()) {
         player.sendMessage(messageComponent, player.getUUID());
      }
   }
}
