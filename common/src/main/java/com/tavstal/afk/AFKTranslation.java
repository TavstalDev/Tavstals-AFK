package com.tavstal.afk;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;
import java.util.function.Supplier;

public class AFKTranslation {
    public static final Supplier<String> AFKOnMessage;
    public static final Supplier<String> AFKOffMessage;
    public static final Supplier<String> SleepStartMessage;
    public static final Supplier<String> SleepStopMessage;
    public static final Supplier<String> SleepResetMessage;

    static {
        IConfigBuilder builder = ConfigBuilders.newTomlConfig(Constants.MOD_ID, "translation", true).dontSync();
        
        AFKOnMessage = builder.onlyOnServer().define("AFKOnMessage", "§6{0} is now AFK.", 0, 128);
        AFKOffMessage = builder.onlyOnServer().define("AFKOffMessage", "§6{0} is no longer AFK.", 0, 128);
        SleepStartMessage = builder.onlyOnServer().define("SleepStartMessage", "§e{0} is sleeping. {1} player(s) needed to reset.", 0, 128);
        SleepStopMessage = builder.onlyOnServer().define("SleepStopMessage", "§c{0} stopped sleeping. {1} player(s) needed to reset.", 0, 128);
        SleepResetMessage = builder.onlyOnServer().define("SleepResetMessage", "§aSleeping through this night.", 0, 128);
        builder.build();
    }
}