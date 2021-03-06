package com.blamejared.crafttweaker.impl.recipes.handlers.vanilla;

import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.api.recipes.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipes.IReplacementRule;
import com.blamejared.crafttweaker.api.util.StringUtils;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@IRecipeHandler.For(SmithingRecipe.class)
public final class SmithingRecipeHandler implements IRecipeHandler<SmithingRecipe> {
    
    @Override
    public String dumpToCommandString(final IRecipeManager manager, final SmithingRecipe recipe) {
        
        return String.format(
                "smithing.addRecipe(%s, %s, %s, %s);",
                StringUtils.quoteAndEscape(recipe.getId()),
                new MCItemStackMutable(recipe.getRecipeOutput()).getCommandString(),
                IIngredient.fromIngredient(recipe.base).getCommandString(),
                IIngredient.fromIngredient(recipe.addition).getCommandString()
        );
    }
    
    @Override
    public Optional<Function<ResourceLocation, SmithingRecipe>> replaceIngredients(final IRecipeManager manager, final SmithingRecipe recipe, final List<IReplacementRule> rules) {
        final Optional<Ingredient> base = IRecipeHandler.attemptReplacing(recipe.base, Ingredient.class, recipe, rules);
        final Optional<Ingredient> addition = IRecipeHandler.attemptReplacing(recipe.addition, Ingredient.class, recipe, rules);
        
        if (!base.isPresent() && !addition.isPresent()) return Optional.empty();
        
        return Optional.of(id -> new SmithingRecipe(id, base.orElseGet(() -> recipe.base), addition.orElseGet(() -> recipe.addition), recipe.getRecipeOutput()));
    }
    
}
