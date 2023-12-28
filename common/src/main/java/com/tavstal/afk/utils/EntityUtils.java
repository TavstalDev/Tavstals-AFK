package com.tavstal.afk.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntityUtils {

   public static String GetName(Entity entity) {
      return entity.getName().getString();
   } 

   public static boolean IsPlayer(Entity entity)
   {
      return entity instanceof Player; 
   }
}
