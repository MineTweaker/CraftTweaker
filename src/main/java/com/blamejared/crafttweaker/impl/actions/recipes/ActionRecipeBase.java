package com.blamejared.crafttweaker.impl.actions.recipes;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public abstract class ActionRecipeBase implements IRuntimeAction {
    
    private final IRecipeManager manager;
    
    public ActionRecipeBase(IRecipeManager manager) {
        
        this.manager = manager;
    }
    
    public IRecipeManager getManager() {
        
        return manager;
    }
    
    
    public Map<ResourceLocation, IRecipe<?>> getRecipes() {
        
        return this.getManager().getRecipes();
    }
    
    public IRecipeType<?> getRecipeType() {
        
        return this.getManager().getRecipeType();
    }
    
    public ResourceLocation getRecipeTypeName() {
        
        return Registry.RECIPE_TYPE.getKey(getRecipeType());
    }
    
    
}
