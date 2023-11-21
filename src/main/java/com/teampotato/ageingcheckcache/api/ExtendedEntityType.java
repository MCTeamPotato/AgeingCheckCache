package com.teampotato.ageingcheckcache.api;

public interface ExtendedEntityType {
    int ageingCheckCache$getMaxSpawnCount();
    void ageingCheckCache$setMaxSpawnCount(int maxSpawnCount);

    boolean ageingCheckCache$isWhitelisted();
    void ageingCheckCache$setIsWhitelisted(boolean isWhitelisted);

    boolean ageingCheckCache$isBlacklisted();
    void ageingCheckCache$setIsBlacklisted(boolean isBlacklisted);
}
