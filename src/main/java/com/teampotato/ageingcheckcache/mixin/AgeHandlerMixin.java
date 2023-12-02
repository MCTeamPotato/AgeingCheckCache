package com.teampotato.ageingcheckcache.mixin;

import com.mrbysco.ageingspawners.config.SpawnerConfig;
import com.mrbysco.ageingspawners.handler.AgeHandler;
import com.mrbysco.ageingspawners.util.AgeingWorldData;
import com.teampotato.ageingcheckcache.api.ExtendedBlock;
import com.teampotato.ageingcheckcache.api.ExtendedEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AgeHandler.class)
public abstract class AgeHandlerMixin {
    @Shadow public abstract void ageTheSpawner(Level world, BaseSpawner spawner, int maxCount);

    @Inject(method = "SpawnEvent", at = @At("HEAD"), cancellable = true, remap = false)
    private void onProcessSpawnEvent(LivingSpawnEvent.CheckSpawn event, CallbackInfo ci) {
        ci.cancel();
        if (event.isSpawner() && !event.isCanceled() && event.getWorld() instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) event.getWorld();
            BaseSpawner spawner = event.getSpawner();
            if (spawner == null) return;
            Block spawnerBlock = world.getBlockState(spawner.getPos()).getBlock();
            if (((ExtendedBlock)spawnerBlock).ageingCheckCache$isTrackableSpawner()) {
                EntityType<?> entityType = event.getEntity().getType();
                if (SpawnerConfig.SERVER.spawnerMode.get().equals(SpawnerConfig.EnumAgeingMode.BLACKLIST)) {
                    this.ageingCheckCache$handleBlacklist(world, spawner, entityType);
                } else {
                    this.ageingCheckCache$handleWhitelist(world, spawner, entityType);
                }
            }
        }
    }

    @Unique
    public void ageingCheckCache$handleBlacklist(Level world, BaseSpawner spawner, @NotNull EntityType<?> entityType) {
        if (!((ExtendedEntityType)entityType).ageingCheckCache$isBlacklisted()) {
            this.ageTheSpawner(world, spawner, SpawnerConfig.SERVER.blacklistMaxSpawnCount.get());
        } else {
            ResourceLocation dimensionLocation = world.dimension().location();
            AgeingWorldData worldData = AgeingWorldData.get(world);
            Map<BlockPos, Integer> locationMap = worldData.getMapFromWorld(dimensionLocation);
            locationMap.remove(spawner.getPos());
            worldData.setMapForWorld(dimensionLocation, locationMap);
            worldData.setDirty();
        }
    }

    @Unique
    public void ageingCheckCache$handleWhitelist(Level world, BaseSpawner spawner, @NotNull EntityType<?> entityType) {
        if (((ExtendedEntityType)entityType).ageingCheckCache$isWhitelisted()) {
            int maxSpawnCount = ((ExtendedEntityType)entityType).ageingCheckCache$getMaxSpawnCount();
            this.ageTheSpawner(world, spawner, maxSpawnCount);
        } else {
            ResourceLocation dimensionLocation = world.dimension().location();
            AgeingWorldData worldData = AgeingWorldData.get(world);
            Map<BlockPos, Integer> locationMap = worldData.getMapFromWorld(dimensionLocation);
            locationMap.remove(spawner.getPos());
            worldData.setMapForWorld(dimensionLocation, locationMap);
            worldData.setDirty();
        }
    }
}
