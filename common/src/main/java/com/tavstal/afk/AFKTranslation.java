package com.tavstal.afk;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;
import java.util.function.Supplier;

public class AFKTranslation {
    public static final Supplier<String> AFKOnMsg;
    public static final Supplier<String> AFKOffMsg;
    public static final Supplier<String> SleepStartMsg;
    public static final Supplier<String> SleepStopMsg;
    public static final Supplier<String> SleepResetMsg;
    public static final Supplier<String> PlayerNotFoundMsg;
    public static final Supplier<String> AFKAdminCommandSyntax;
    public static final Supplier<String> AFKStatusChangedMsg;
    public static final Supplier<String> AFKStatusAway;
    public static final Supplier<String> AFKStatusOnline;

    static {
        IConfigBuilder builder = ConfigBuilders.newTomlConfig(Constants.MOD_ID, "translation", true).dontSync();
        
        AFKOnMsg = builder.onlyOnServer().define("AFKOnMsg", "§6{0} is now AFK.", 0, 128);
        AFKOffMsg = builder.onlyOnServer().define("AFKOffMsg", "§6{0} is no longer AFK.", 0, 128);
        SleepStartMsg = builder.onlyOnServer().define("SleepStartMsg", "§e{0} is sleeping. {1} player(s) needed to reset.", 0, 128);
        SleepStopMsg = builder.onlyOnServer().define("SleepStopMsg", "§c{0} stopped sleeping. {1} player(s) needed to reset.", 0, 128);
        SleepResetMsg = builder.onlyOnServer().define("SleepResetMsg", "§aSleeping through this night.", 0, 128);
        PlayerNotFoundMsg = builder.onlyOnServer().define("PlayerNotFoundMsg", "§cPlayer with '{0}' name does not exist.", 0, 128);
        AFKAdminCommandSyntax = builder.onlyOnServer().define("AFKAdminCommandSyntax", "&cWrong syntax! Usage: /afkadmin [player]", 0, 128);
        AFKStatusChangedMsg = builder.onlyOnServer().define("AFKStatusChangedMsg", "§aYou have successfully changed {0}'s AFK status to: {1}", 0, 128);
        AFKStatusAway = builder.onlyOnServer().define("AFKStatusAway", "§6away", 0, 16);
        AFKStatusOnline = builder.onlyOnServer().define("AFKStatusOnline", "§2online", 0, 16);
        builder.build();
    }
}