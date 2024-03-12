package com.tavstal.afk.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class WorldUtils {

   public static String GetName(ServerLevel level) {
      return level.dimension().location().toString();
   } 

   public static String GetDisplayName(ServerLevel level) {
      return level.dimension().location().getPath();
   }

   public static String GetName(Level level) {
      return level.dimension().location().toString();
   } 

   public static String GetDisplayName(Level level) {
      return level.dimension().location().getPath();
   }
}
