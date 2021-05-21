package com.blamejared.crafttweaker.test_api.mocks.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nonnull;

public class MockContainer extends Container {
    
    public MockContainer() {
        
        super(null, 0x815);
    }
    
    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        
        return false;
    }
    
}
