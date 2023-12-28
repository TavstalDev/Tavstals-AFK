package com.tavstal.afk;

import com.google.common.reflect.Reflection;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class AFKForge {
    
    public AFKForge() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        MinecraftForge.EVENT_BUS.register(new EventListener());
        Reflection.initialize(ForgeConfig.class);
    }
}