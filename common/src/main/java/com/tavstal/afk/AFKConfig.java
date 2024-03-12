package com.tavstal.afk;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;
import java.util.function.Supplier;

public class AFKConfig {
    public static final Supplier<Boolean> EnableDebugMode;
    public static final Supplier<Boolean> ShouldBroadcastMessages;
    public static final Supplier<String> Prefix;
    public static final Supplier<String> Suffix;
    public static final Supplier<Integer> AutoAFKInterval;
    public static final Supplier<Integer> PlayerPercentToResetTime;

    public static final Supplier<Boolean> EnableSleepTab;
    public static final Supplier<String> SleepPrefix;
    public static final Supplier<String> SleepSuffix;

    public static final Supplier<Boolean> DisableOnAttackBlock;
    public static final Supplier<Boolean> DisableOnAttackEntity;
    public static final Supplier<Boolean> DisableOnUseBlock;
    public static final Supplier<Boolean> DisableOnUseEntity;
    public static final Supplier<Boolean> DisableOnUseItem;
    public static final Supplier<Boolean> DisableOnWorldChange;
    public static final Supplier<Boolean> DisableOnChatting;
    public static final Supplier<Boolean> DisableOnMove;
    public static final Supplier<Boolean> DisableOnRespawn;

    static {
        IConfigBuilder builder = ConfigBuilders.newTomlConfig(Constants.MOD_ID, "config", true).dontSync();
        
        //#region General
        builder.onlyOnServer().push("General");
        EnableDebugMode = builder.onlyOnServer().define("EnableDebugMode", false);
        ShouldBroadcastMessages = builder.onlyOnServer().define("ShouldBroadcastMessages", false);
        Prefix = builder.onlyOnServer().define("Prefix", "§6[AFK]", 0, 32);
        Suffix = builder.onlyOnServer().define("Suffix", "", 0, 32);
        AutoAFKInterval = builder.onlyOnServer().define("AutoAFKInterval", 600, 60, 3600);
        PlayerPercentToResetTime = builder.onlyOnServer().define("PlayerPercentToResetTime", 100, 0, 100);
        EnableSleepTab = builder.onlyOnServer().define("EnableSleepTab", true);
        SleepPrefix = builder.define("SleepPrefix", "§7[Sleeping]", 0, 32);
        SleepSuffix = builder.define("SleepSuffix", "", 0, 32);
        builder.pop();
        //#endregion
        //#region Disable AFK
        builder.onlyOnServer().push("Auto Disable AFK");
        DisableOnAttackBlock = builder.onlyOnServer().define("DisableOnAttackBlock", true);
        DisableOnAttackEntity = builder.onlyOnServer().define("DisableOnAttackEntity", true);
        DisableOnUseBlock = builder.onlyOnServer().define("DisableOnUseBlock", false);
        DisableOnUseEntity = builder.onlyOnServer().define("DisableOnUseEntity", false);
        DisableOnUseItem = builder.onlyOnServer().define("DisableOnUseItem", true);
        DisableOnWorldChange = builder.onlyOnServer().define("DisableOnWorldChange", true);
        DisableOnChatting = builder.onlyOnServer().define("DisableOnChatting", true);
        DisableOnMove = builder.onlyOnServer().define("DisableOnMove", true);
        DisableOnRespawn = builder.onlyOnServer().define("DisableOnRespawn", true);
        builder.pop();
        //#endregion
        builder.build();
    }
}