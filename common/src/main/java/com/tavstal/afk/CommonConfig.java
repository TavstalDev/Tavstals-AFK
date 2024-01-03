package com.tavstal.afk;

public class CommonConfig {
    public boolean EnableDebugMode;
    public String Prefix;
    public String Suffix;
    public int AutoAFKInterval;
    public int PlayerPercentToResetTime;

    public boolean DisableOnAttackBlock;
    public boolean DisableOnAttackEntity;
    public boolean DisableOnUseBlock;
    public boolean DisableOnUseEntity;
    public boolean DisableOnUseItem;
    public boolean DisableOnWorldChange;
    public boolean DisableOnChatting;
    public boolean DisableOnMove;
    public boolean DisableOnRespawn;
    
    public CommonConfig(boolean enableDebugMode, String prefix, String suffix, int autoAFKInterval,
        int playerPercentToResetTime, boolean disableOnAttackBlock, boolean disableOnAttackEntity,
        boolean disableOnUseBlock, boolean disableOnUseEntity, boolean disableOnUseItem,
        boolean disableOnWorldChange, boolean disableOnChatting, boolean disableOnMove, boolean disableOnRespawn) 
    {
        EnableDebugMode = enableDebugMode;
        Prefix = prefix;
        Suffix = suffix;
        AutoAFKInterval = autoAFKInterval;
        PlayerPercentToResetTime = playerPercentToResetTime;
        DisableOnAttackBlock = disableOnAttackBlock;
        DisableOnAttackEntity = disableOnAttackEntity;
        DisableOnUseBlock = disableOnUseBlock;
        DisableOnUseEntity = disableOnUseEntity;
        DisableOnUseItem = disableOnUseItem;
        DisableOnWorldChange = disableOnWorldChange;
        DisableOnChatting = disableOnChatting;
        DisableOnMove = disableOnMove;
        DisableOnRespawn = disableOnRespawn;
    }
}
