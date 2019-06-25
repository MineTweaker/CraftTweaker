package com.blamejared.crafttweaker.impl.managers;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.*;
import com.blamejared.crafttweaker.api.managers.*;
import net.minecraft.item.crafting.*;
import org.openzen.zencode.java.*;

@ZenRegister
@ZenCodeType.Name("crafttweaker.api.CTRecipeManager")
public class CTRecipeManager implements IRegistryManager {
    
    @ZenCodeGlobals.Global("recipes")
    public static final CTRecipeManager INSTANCE = new CTRecipeManager();
    public static RecipeManager recipeManager;
    //public static RecipeManager vanillaRecipeManager = null;
    //private final List<ActionAddCraftingRecipe> addedRecipes = new ArrayList<>();
    
    private CTRecipeManager() {
    }
    
    @ZenCodeType.Method
    public void addShaped(String recipeName, IItemStack output, IIngredient[][] ingredients, @ZenCodeType.Nullable RecipeFunctionShaped recipeFunction) {
        //addedRecipes.add(new ActionAddCraftingRecipe.Shaped(recipeName, output, ingredients, true, recipeFunction));
    }
    
    @ZenCodeType.Method
    public void addShapeless(String recipeName, IItemStack output, IIngredient[] ingredients, @ZenCodeType.Nullable RecipeFunctionShapeless recipeFunction) {
        //addedRecipes.add(new ActionAddCraftingRecipe.Shapeless(recipeName, output, ingredients, recipeFunction));
    }
    
    @ZenCodeType.Method
    public void removeRecipe(IIngredient output) {
        //ActionRemoveRecipeNoIngredients.INSTANCE.addOutput(output);
    }
    
    @Override
    public void removeByName(String name) {
        //Remove by name here
    }
    
    //public List<ActionAddCraftingRecipe> getAddedRecipes() {
    //    return addedRecipes;
    //}
    
    
    @FunctionalInterface
    @ZenRegister
    public interface RecipeFunctionShaped {
        
        IItemStack process(IItemStack usualOut, IItemStack[][] inputs);
    }
    
    @FunctionalInterface
    @ZenRegister
    public interface RecipeFunctionShapeless {
        
        IItemStack process(IItemStack usualOut, IItemStack[] inputs);
    }
}
