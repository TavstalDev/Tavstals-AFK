package com.tavstal.afk;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;
import java.util.function.Supplier;

public class ForgeConfig {
    public static final Supplier<Boolean> EnableDebugMode;
    public static final Supplier<String> Prefix;
    public static final Supplier<String> Suffix;
    public static final Supplier<Integer> AutoAFKInterval;
    public static final Supplier<Integer> PlayerPercentToResetTime;

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
        IConfigBuilder builder = ConfigBuilders.newTomlConfig(Constants.MOD_ID, "config", false);

        EnableDebugMode = builder.define("EnableDebugMode", false);
        Prefix = builder.define("Prefix", "§6[AFK]", 0, 32);
        Suffix = builder.define("Suffix", "", 0, 32);
        AutoAFKInterval = builder.define("AutoAFKInterval", 600, 60, 3600);
        PlayerPercentToResetTime = builder.define("PlayerPercentToResetTime", 100, 0, 100);
        DisableOnAttackBlock = builder.define("DisableOnAttackBlock", true);
        DisableOnAttackEntity = builder.define("DisableOnAttackEntity", true);
        DisableOnUseBlock = builder.define("DisableOnUseBlock", false);
        DisableOnUseEntity = builder.define("DisableOnUseEntity", false);
        DisableOnUseItem = builder.define("DisableOnUseItem", true);
        DisableOnWorldChange = builder.define("DisableOnWorldChange", true);
        DisableOnChatting = builder.define("DisableOnChatting", true);
        DisableOnMove = builder.define("DisableOnMove", true);
        DisableOnRespawn = builder.define("DisableOnRespawn", true);

        builder.build();
    }
}
