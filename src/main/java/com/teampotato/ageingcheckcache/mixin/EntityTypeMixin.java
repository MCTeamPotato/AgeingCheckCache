package com.teampotato.ageingcheckcache.mixin;

import com.teampotato.ageingcheckcache.api.ExtendedEntityType;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public class EntityTypeMixin implements ExtendedEntityType {
    @Unique private int ageingCheckCache$maxSpawnCount;

    @Unique private boolean ageingCheckCache$isWhitelisted, ageingCheckCache$isBlacklisted;

    @Override
    public int ageingCheckCache$getMaxSpawnCount() {
        return this.ageingCheckCache$maxSpawnCount;
    }

    @Override
    public void ageingCheckCache$setMaxSpawnCount(int maxSpawnCount) {
        this.ageingCheckCache$maxSpawnCount = maxSpawnCount;
    }

    @Override
    public boolean ageingCheckCache$isWhitelisted() {
        return this.ageingCheckCache$isWhitelisted;
    }

    @Override
    public void ageingCheckCache$setIsWhitelisted(boolean isWhitelisted) {
        this.ageingCheckCache$isWhitelisted = isWhitelisted;
    }

    @Override
    public boolean ageingCheckCache$isBlacklisted() {
        return this.ageingCheckCache$isBlacklisted;
    }

    @Override
    public void ageingCheckCache$setIsBlacklisted(boolean isBlacklisted) {
        this.ageingCheckCache$isBlacklisted = isBlacklisted;
    }
}
