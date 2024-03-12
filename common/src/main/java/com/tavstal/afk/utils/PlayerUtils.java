package com.tavstal.afk.utils;

import com.tavstal.afk.AFKCommon;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class PlayerUtils {

   public static boolean IsAFK(String uuid) {
      var data = AFKCommon.GetPlayerData(uuid);
      if (data == null)
         return false;
      
      return data.IsAFK;
   }

   public static boolean IsSleeping(Player player) {
      var data = AFKCommon.GetPlayerData(player.getStringUUID());
      if (data == null)
         return false;
      
      return data.IsAFK || player.isSleeping();
   }

   public static boolean AddToTeam(Player player, String team) {
      if (player == null)
         return false;

      MinecraftServer server = player.getServer();
      if (server == null)
         return false;

      if (team == null)
         return false;

      Scoreboard scoreboard = server.getScoreboard();
      if (scoreboard == null)
         return false;

      PlayerTeam playerTeam = scoreboard.getPlayerTeam(team);
      if (playerTeam == null)
         return false;

      try {
         scoreboard.addPlayerToTeam(EntityUtils.GetName(player), playerTeam);
         return true;
      }
      catch(Exception ex) { 
         return false;
      }
   }

   public static boolean RemoveFromTeam(Player player, String team) {
      if (player == null)
         return false;

      MinecraftServer server = player.getServer();
      if (server == null)
         return false;

      if (team == null)
         return false;

      Scoreboard scoreboard = server.getScoreboard();
      if (scoreboard == null)
         return false;
   
      PlayerTeam playerTeam = scoreboard.getPlayerTeam(team);
      if (playerTeam == null)
         return false;

      try {
         scoreboard.removePlayerFromTeam(EntityUtils.GetName(player), playerTeam);
         return true;
      }
      catch(Exception ex) { 
         return false;
      }
   }
}
