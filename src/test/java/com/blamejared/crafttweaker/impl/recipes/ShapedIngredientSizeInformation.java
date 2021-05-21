package com.blamejared.crafttweaker.impl.recipes;

import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.test_api.mocks.items.MockItems;

final class ShapedIngredientSizeInformation {
    
    public final IIngredient[][] ingredients;
    public final int expectedWidth;
    public final int expectedHeight;
    
    public ShapedIngredientSizeInformation(IIngredient[][] ingredients, int expectedWidth, int expectedHeight) {
        
        this.ingredients = ingredients;
        this.expectedWidth = expectedWidth;
        this.expectedHeight = expectedHeight;
    }
    
    /**
     * Used by {@link com.blamejared.crafttweaker.impl.recipes.CTRecipeShapedTest#recipeCalculatesProperDimensions(ShapedIngredientSizeInformation)}
     */
    @SuppressWarnings("unused")
    public static ShapedIngredientSizeInformation[] getSizeInformationForDimensionValidation() {
        
        final IItemStack r = new MockItems().redstone;
        
        return new ShapedIngredientSizeInformation[] {
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{r, r, r}, {r, r, r}, {r, r, r}}, 3, 3
                ),
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{r, r}, {r, r}, {r, r}}, 2, 3
                ),
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{r, r, r}, {r, r, r}}, 3, 2
                ),
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{r, r}, {r, r}}, 2, 2
                ),
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{r, r, r}}, 3, 1
                ),
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{r}, {r}, {r}}, 1, 3
                ),
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{r, r, r}, {}, {r, r, r}}, 3, 3
                ),
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{r, r, r}, {r, r}, {r}}, 3, 3
                ),
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{}, {}, {r}}, 1, 3
                ),
                new ShapedIngredientSizeInformation(
                        new IIngredient[][] {{r}, {r, r}, {r, r, r}}, 3, 3
                ),
        };
    }
    
}
