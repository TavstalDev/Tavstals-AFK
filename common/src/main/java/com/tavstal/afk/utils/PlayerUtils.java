package com.tavstal.afk.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PlayerUtils {

   public static String GetName(Player player) {
      return player.getName().getString();
   } 

   public static double GetSpeed(Player player, Vec3 oldPos) {
      var deltaX = player.getX() - oldPos.x;
      var deltaY = player.getY() - oldPos.y;
      var deltaZ = player.getZ() - oldPos.z;

      return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));
   }

   public static double GetWalkSpeed(Player player, Vec3 oldPos) {
      var deltaX = player.getX() - oldPos.x;
      var deltaZ = player.getZ() - oldPos.z;
      return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
   }

   public static double distanceToSqr(Player player, Vec3 vec3) {
        double d = player.getX() - vec3.x;
        double e = player.getY() - vec3.y;
        return d * d + e * e;
    }
}
