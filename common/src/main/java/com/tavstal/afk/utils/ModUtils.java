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

    public static void BroadcastMessage(Entity entity, String text) {
      var server = entity.getServer();
      if (server == null)
      {
         Constants.LOG.error("BroadcastMessage -> Failed to get the server.");
         return;
      }

      var messageComponent = Literal(text);
      // Send Message to the server
      server.sendSystemMessage(messageComponent);
      // Send Message to all clients
      for (var player : server.getPlayerList().getPlayers()) {
         player.sendSystemMessage(messageComponent);
      }
   }

    public static void BroadcastMessage(Entity entity, String text, Object ... args) {
      var server = entity.getServer();
      if (server == null)
      {
         Constants.LOG.error("BroadcastMessage -> Failed to get the server.");
         return;
      }

      var messageComponent = Literal(MessageFormat.format(text, args));
      // Send Message to the server
      server.sendSystemMessage(messageComponent);
      // Send Message to all clients
      for (var player : server.getPlayerList().getPlayers()) {
         player.sendSystemMessage(messageComponent);
      }
   }

   public static void BroadcastMessageByWorld(Entity entity, String text, String worldKey) {
      var server = entity.getServer();
      if (server == null)
      {
         Constants.LOG.error("BroadcastMessageByWorld -> Failed to get the server.");
         return;
      }

      var messageComponent = Literal(text);
      // Send Message to the server
      server.sendSystemMessage(messageComponent);
      // Send Message to all clients
      for (var player : server.getPlayerList().getPlayers()) {
         if (WorldUtils.GetName(EntityUtils.GetLevel(player)).equals(worldKey))
            player.sendSystemMessage(messageComponent);
      }
   }

    public static void BroadcastMessageByWorld(Entity entity, String text, String worldKey, Object ... args) {
      var server = entity.getServer();
      if (server == null)
      {
         Constants.LOG.error("BroadcastMessageByWorld -> Failed to get the server.");
         return;
      }

      var messageComponent = Literal(MessageFormat.format(text, args));
      // Send Message to the server
      server.sendSystemMessage(messageComponent);
      // Send Message to all clients
      for (var player : server.getPlayerList().getPlayers()) {
         if (WorldUtils.GetName(EntityUtils.GetLevel(player)).equals(worldKey))
            player.sendSystemMessage(messageComponent);
      }
   }
}
