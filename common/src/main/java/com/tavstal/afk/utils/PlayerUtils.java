package com.tavstal.afk.utils;

import com.tavstal.afk.AFKCommon;

import net.minecraft.world.entity.player.Player;

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
}
