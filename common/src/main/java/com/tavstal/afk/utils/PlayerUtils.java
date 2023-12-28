package com.tavstal.afk.utils;

import net.minecraft.world.entity.player.Player;

public class PlayerUtils {

   public static String GetName(Player player) {
      return player.getName().getString();
   } 
}
