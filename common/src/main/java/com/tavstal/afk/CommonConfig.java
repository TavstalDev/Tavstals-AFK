package com.tavstal.afk;

public class CommonConfig {
    public boolean EnableDebugMode = false;
    public String Prefix = "§6[AFK]";
    public String Suffix = "";
    public int AutoAFKInterval = 600;
    public int PlayerPercentToResetTime = 100;

    public boolean DisableOnAttackBlock = true;
    public boolean DisableOnAttackEntity = true;
    public boolean DisableOnUseBlock = false;
    public boolean DisableOnUseEntity = false;
    public boolean DisableOnUseItem = true;
    public boolean DisableOnWorldChange = true;
    public boolean DisableOnChatting = true;
    public boolean DisableOnMove = true;
    public boolean DisableOnRespawn = true;
}
