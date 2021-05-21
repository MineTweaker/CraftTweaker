package com.blamejared.crafttweaker.test_api.context;

import com.blamejared.crafttweaker.test_api.helper.UnsafeHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TestMinecraftContextUnsafe implements TestMinecraftContext {
    
    private static <T> T allocateInstance(Class<? extends T> clsToAllocate) {
        
        return UnsafeHelper.instantiate(clsToAllocate);
    }
    
    @Override
    public World getWorld() {
        
        return allocateInstance(ServerWorld.class);
    }
    
    @Override
    public PlayerEntity getPlayer() {
        
        final ServerPlayerEntity serverPlayerEntity = allocateInstance(ServerPlayerEntity.class);
        serverPlayerEntity.world = getWorld();
        return serverPlayerEntity;
    }
    
}
