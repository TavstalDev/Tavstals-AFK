package com.tavstal.afk.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EntityUtils {

   public static String GetName(Entity entity) {
      return entity.getName().getString();
   } 

   public static boolean IsPlayer(Entity entity)
   {
      return entity instanceof Player; 
   }

   public static Level GetLevel(Entity entity) {
      return entity.level;
   }
}
