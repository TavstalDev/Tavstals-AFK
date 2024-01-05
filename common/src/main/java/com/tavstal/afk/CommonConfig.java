package com.tavstal.afk;

public class CommonConfig {
    public boolean EnableDebugMode;
    public boolean ShouldBroadcastMessages;
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

    public String AFKOnMessage;
    public String AFKOffMessage;
    public String SleepStartMessage;
    public String SleepStopMessage;
    public String SleepResetMessage;
    
    public CommonConfig(boolean enableDebugMode, boolean broadcastMessage, String prefix, String suffix, int autoAFKInterval,
        int playerPercentToResetTime, boolean disableOnAttackBlock, boolean disableOnAttackEntity,
        boolean disableOnUseBlock, boolean disableOnUseEntity, boolean disableOnUseItem,
        boolean disableOnWorldChange, boolean disableOnChatting, boolean disableOnMove, boolean disableOnRespawn,
        String afkOnMessage, String afkOffMessage, String sleepStartMesssage, String sleepStopMessage,
        String sleepResetMessage) 
    {
        EnableDebugMode = enableDebugMode;
        ShouldBroadcastMessages = broadcastMessage;
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

        AFKOnMessage = afkOnMessage;
        AFKOffMessage = afkOffMessage;
        SleepStartMessage = sleepStartMesssage;
        SleepStopMessage = sleepStopMessage;
        SleepResetMessage = sleepResetMessage;
    }
}
