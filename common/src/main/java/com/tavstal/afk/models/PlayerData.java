package com.tavstal.afk.models;

import java.time.LocalDateTime;
import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class PlayerData {
    public boolean IsAFK;
    public Vec3 LastPosition;
    public BlockPos LastBlockPosition;
    public float HeadRotation;
    public int TeleportTTL;
    public int ImpulseTTL;
    public LocalDateTime Date;
    public HashSet<TeamData> Teams;
    //public boolean IsHurt;

    public PlayerData(Vec3 lastPosition, BlockPos lastBlockPosition, float headRotation, LocalDateTime date) {
        LastPosition = lastPosition;
        LastBlockPosition = lastBlockPosition;
        HeadRotation = headRotation;
        Date = date;
        Teams = new HashSet<>();
    }
}
