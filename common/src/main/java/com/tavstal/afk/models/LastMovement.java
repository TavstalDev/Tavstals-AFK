package com.tavstal.afk.models;

import java.time.LocalDateTime;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class LastMovement {
    public Vec3 LastPosition;
    public BlockPos LastBlockPosition;
    public float HeadRotation;
    public int TeleportTTL;
    public int ImpulseTTL;
    public LocalDateTime Date;
    public boolean IsHurt;

    public LastMovement(Vec3 lastPosition, BlockPos lastBlockPosition, float headRotation, LocalDateTime date) {
        LastPosition = lastPosition;
        LastBlockPosition = lastBlockPosition;
        HeadRotation = headRotation;
        Date = date;
    }
}
