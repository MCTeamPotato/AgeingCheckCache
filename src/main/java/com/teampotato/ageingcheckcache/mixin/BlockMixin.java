package com.teampotato.ageingcheckcache.mixin;

import com.teampotato.ageingcheckcache.api.ExtendedBlock;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public class BlockMixin implements ExtendedBlock {
    @Unique private boolean ageingCheckCache$isTrackableSpawner;

    @Override
    public boolean ageingCheckCache$isTrackableSpawner() {
        return this.ageingCheckCache$isTrackableSpawner;
    }

    @Override
    public void ageingCheckCache$setIsTrackableSpawner(boolean isTrackableSpawner) {
        this.ageingCheckCache$isTrackableSpawner = isTrackableSpawner;
    }
}
