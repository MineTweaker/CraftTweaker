package com.blamejared.crafttweaker.test_api.mocks.container;

import com.blamejared.crafttweaker.api.item.IItemStack;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class MockCraftingInventory extends CraftingInventory {
    
    private boolean initialized = false;
    
    public MockCraftingInventory() {
        
        this(new MockContainer(), 3, 3);
    }
    
    public MockCraftingInventory(Container eventHandlerIn, int width, int height) {
        
        super(eventHandlerIn, width, height);
    }
    
    public void setInputs(IItemStack[][] stack) {
        
        initialized = true;
        for(int rowIndex = 0; rowIndex < stack.length; rowIndex++) {
            for(int columnIndex = 0; columnIndex < stack[rowIndex].length; columnIndex++) {
                setInventorySlotContents(rowIndex * getWidth() + columnIndex, stack[rowIndex][columnIndex].getInternal());
            }
        }
    }
    
    @Override
    @Nonnull
    public ItemStack getStackInSlot(int index) {
        
        if(!initialized) {
            throw new IllegalStateException("Not initialized! Call #setInputs first!");
        }
        
        
        return super.getStackInSlot(index);
    }
    
}
