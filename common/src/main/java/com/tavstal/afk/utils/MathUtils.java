package com.tavstal.afk.utils;

public class MathUtils {

   public static int Clamp(int value, int max) {
      return value > max ? max : value;
   } 

   public static int Clamp(int value, int min, int max) {
      return value < min ? min : (value > max ? max : value);
   }
}
