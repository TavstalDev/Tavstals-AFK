package com.tavstal.afk.models;

import java.time.LocalDateTime;

import net.minecraft.world.phys.Vec3;

public class LastMovement {
    public Vec3 LastPosition;
    public LocalDateTime Date;
    public boolean IsHurt;

    public LastMovement(Vec3 lastPosition, LocalDateTime date) {
        LastPosition = lastPosition;
        Date = date;
        IsHurt = false;
    }
}
