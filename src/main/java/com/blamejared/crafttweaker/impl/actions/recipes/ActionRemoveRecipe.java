package com.blamejared.crafttweaker.impl.actions.recipes;

import com.blamejared.crafttweaker.api.brackets.CommandStringDisplayable;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import net.minecraft.item.crafting.IRecipe;

import java.util.function.Function;
import java.util.function.Predicate;

public class ActionRemoveRecipe extends ActionRecipeBase {
    
    protected final Predicate<IRecipe<?>> removePredicate;
    protected Function<ActionRecipeBase, String> describeFunction;
    
    public ActionRemoveRecipe(IRecipeManager manager, Predicate<IRecipe<?>> removePredicate) {
        
        super(manager);
        this.removePredicate = removePredicate;
        this.describeFunction = action -> "Removing \"" + getRecipeTypeName() + "\" recipes that match a custom condition";
    }
    
    public ActionRemoveRecipe(IRecipeManager manager, Predicate<IRecipe<?>> removePredicate, Function<ActionRecipeBase, String> describeFunction) {
        
        super(manager);
        this.removePredicate = removePredicate;
        this.describeFunction = describeFunction;
    }
    
    @Override
    public void apply() {
        
        getRecipes().keySet().removeIf(location -> removePredicate.test(getRecipes().get(location)));
    }
    
    @Override
    public String describe() {
        
        return describeFunction.apply(this);
    }
    
    
    @SuppressWarnings("UnusedReturnValue")
    public ActionRemoveRecipe describeDefaultRemoval(CommandStringDisplayable output) {
        
        this.describeFunction = action -> "Removing \"" + action.getRecipeTypeName() + "\" recipes with output: " + output + "\"";
        return this;
    }
    
}
