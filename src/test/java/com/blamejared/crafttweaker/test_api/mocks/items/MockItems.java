package com.blamejared.crafttweaker.test_api.mocks.items;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.item.MCItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Currently this still initializes the registry, can we get around that?
 */
public class MockItems {
    
    public IItemStack empty = MCItemStack.EMPTY.get();
    
    private final Item bedrock_item = new Item(new Item.Properties());
    
    public IItemStack bedrock = new MCItemStack(new ItemStack(bedrock_item));
    public IItemStack redstone = new MCItemStack(new ItemStack(Items.REDSTONE));
    public IItemStack ironIngot = new MCItemStack(new ItemStack(Items.IRON_INGOT));
    public IItemStack ironSword = new MCItemStack(new ItemStack(Items.IRON_SWORD));
    public IItemStack ironNugget = new MCItemStack(new ItemStack(Items.IRON_NUGGET));
    
}
