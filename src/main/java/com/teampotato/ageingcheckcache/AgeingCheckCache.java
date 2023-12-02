package com.teampotato.ageingcheckcache;

import com.mrbysco.ageingspawners.config.SpawnerConfig;
import com.teampotato.ageingcheckcache.api.ExtendedBlock;
import com.teampotato.ageingcheckcache.api.ExtendedEntityType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Mod(AgeingCheckCache.MOD_ID)
public class AgeingCheckCache {
    public static final String MOD_ID = "ageingcheckcache";

    private static final Logger LOGGER = LogManager.getLogger("AgeingCheckCache");

    private static final ForgeConfigSpec CONFIG;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> TRACKABLE_SPAWNERS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("AgeingCheckCache");
        TRACKABLE_SPAWNERS = builder.defineList("TrackableSpawners", ObjectArrayList.wrap(new String[]{"minecraft:spawner"}), o -> true);
        builder.pop();
        CONFIG = builder.build();
    }

    public AgeingCheckCache() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(() -> {
                for (EntityType<?> entityType : ForgeRegistries.ENTITIES) {
                    ((ExtendedEntityType)entityType).ageingCheckCache$setIsWhitelisted(ageingCheckCache$whitelistContains(entityType));
                    ((ExtendedEntityType)entityType).ageingCheckCache$setIsBlacklisted(ageingCheckCache$blacklistContains(entityType.getRegistryName()));
                    ((ExtendedEntityType)entityType).ageingCheckCache$setMaxSpawnCount(ageingCheckCache$getMaxSpawnCount(entityType));
                }
                for (Block block : ForgeRegistries.BLOCKS) {
                    ResourceLocation id = block.getRegistryName();
                    if (id == null) continue;
                    ((ExtendedBlock)block).ageingCheckCache$setIsTrackableSpawner(TRACKABLE_SPAWNERS.get().contains(id.toString()));
                }
            });
            LOGGER.info("AgeingCheckCache has set up successfully.");
        });
    }

    private static boolean ageingCheckCache$blacklistContains(@Nullable ResourceLocation registryName) {
        if (registryName == null) return false;
        return SpawnerConfig.SERVER.blacklist.get().contains(registryName.toString());
    }

    private static final List<ResourceLocation> WHITE_LIST = new ObjectArrayList<>();

    private static boolean ageingCheckCache$whitelistContains(EntityType<?> entityType) {
        if (!WHITE_LIST.isEmpty()) return WHITE_LIST.contains(entityType.getRegistryName());
        synchronized (WHITE_LIST) {
            for (String info : SpawnerConfig.SERVER.whitelist.get()) {
                if (info.isEmpty()) continue;
                if (info.contains(";")) {
                    String[] infoArray = info.split(";");
                    if (infoArray.length > 1) WHITE_LIST.add(new ResourceLocation(infoArray[0]));
                } else {
                    WHITE_LIST.add(new ResourceLocation(info));
                }
            }
        }

        return WHITE_LIST.contains(entityType.getRegistryName());
    }

    private static int ageingCheckCache$getMaxSpawnCount(@NotNull EntityType<?> entityType) {
        List<? extends String> whitelist = SpawnerConfig.SERVER.whitelist.get();
        ResourceLocation id = entityType.getRegistryName();
        if (id == null) return SpawnerConfig.SERVER.whitelistMaxSpawnCount.get();
        for (String info : whitelist) {
            if (info.contains(";")) {
                String[] infoArray = info.split(";");
                if (infoArray.length > 1 && infoArray[0].equals(id.toString())) {
                    return NumberUtils.toInt(infoArray[1]);
                }
            }
        }

        return SpawnerConfig.SERVER.whitelistMaxSpawnCount.get();
    }
}
