package com.blamejared.crafttweaker.test_api.context;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Provides an entry point for unit tests to get MC classes
 *
 * Currently the only implementation is {@link TestMinecraftContextUnsafe}, but later there may be one that
 * starts and runs a server as well
 */
public interface TestMinecraftContext {
    
    /**
     * Returns a world object.
     * It's not guaranteed that this will be the same for every invocation.
     */
    World getWorld();
    
    /**
     * Returns a player object.
     * It's not guaranteed that this will be the same for every invocation.
     */
    PlayerEntity getPlayer();
}
