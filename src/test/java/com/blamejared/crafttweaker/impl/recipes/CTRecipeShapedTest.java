package com.blamejared.crafttweaker.impl.recipes;

import com.blamejared.crafttweaker.CraftTweakerTest;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.test_api.context.TestContext;
import com.blamejared.crafttweaker.test_api.mocks.container.MockCraftingInventory;
import com.blamejared.crafttweaker.test_api.mocks.items.MockItems;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CTRecipeShapedTest extends CraftTweakerTest {
    
    private static final String name = "test_recipe";
    private final TestContext context = new TestContext();
    
    private IItemStack output;
    private IIngredient[][] ingredients;
    private boolean mirrored;
    private IRecipeManager.RecipeFunctionMatrix recipeFunctionMatrix;
    
    @BeforeEach
    void setUp() {
        
        final MockItems mockItems = context.mockItems;
        
        output = mockItems.ironSword;
        ingredients = new IIngredient[][] {
                {mockItems.redstone, mockItems.ironNugget, mockItems.redstone},
                {mockItems.redstone, mockItems.ironIngot, mockItems.redstone},
                {mockItems.redstone, mockItems.redstone, mockItems.redstone},
        };
        mirrored = false;
        recipeFunctionMatrix = null;
    }
    
    private CTRecipeShaped makeRecipe() {
        
        return new CTRecipeShaped(name, output, ingredients, mirrored, recipeFunctionMatrix);
    }
    
    @Test
    public void recipeIdIsInCrafttweakerNamespace() {
        
        //Arrange
        final CTRecipeShaped subject = makeRecipe();
        
        //Act
        final ResourceLocation id = subject.getId();
        
        //Assert
        assertThat(id).isNotNull();
        assertThat(id.getNamespace()).isEqualTo("crafttweaker");
    }
    
    @Test
    public void recipeIdUsesGivenNameAsPath() {
        
        //Arrange
        final CTRecipeShaped subject = makeRecipe();
        
        //Act
        final ResourceLocation id = subject.getId();
        
        //Assert
        assertThat(id).isNotNull();
        assertThat(id.getPath()).isEqualTo(name);
    }
    
    @ParameterizedTest
    @MethodSource("com.blamejared.crafttweaker.impl.recipes.ShapedIngredientSizeInformation#getSizeInformationForDimensionValidation")
    public void recipeCalculatesProperDimensions(ShapedIngredientSizeInformation sizeInformation) {
        //Arrange
        this.ingredients = sizeInformation.ingredients;
        final CTRecipeShaped subject = makeRecipe();
        
        //Act
        final int calculatedWidth = subject.getRecipeWidth();
        final int calculatedHeight = subject.getRecipeHeight();
        
        //Assert
        assertThat(calculatedWidth).isEqualTo(sizeInformation.expectedWidth);
        assertThat(calculatedHeight).isEqualTo(sizeInformation.expectedHeight);
    }
    
    @ParameterizedTest
    @MethodSource("com.blamejared.crafttweaker.impl.recipes.ShapedIngredientSizeInformation#getSizeInformationForDimensionValidation")
    public void recipeReturnsTrueIfItCanFit(ShapedIngredientSizeInformation sizeInformation) {
        //Arrange
        this.ingredients = sizeInformation.ingredients;
        final CTRecipeShaped subject = makeRecipe();
        
        final int expectedWidth = sizeInformation.expectedWidth;
        final int expectedHeight = sizeInformation.expectedHeight;
        
        //We check for tables that go from 0x0 to maxInventorySize x maxInventorySize
        final int maxInventorySize = 2 * Math.max(expectedWidth, expectedHeight);
        
        //Act
        for(int inventoryWidth = 0; inventoryWidth < maxInventorySize; inventoryWidth++) {
            for(int inventoryHeight = 0; inventoryHeight < maxInventorySize; inventoryHeight++) {
                
                boolean expectedResult = inventoryWidth >= expectedWidth && inventoryHeight >= expectedHeight;
                boolean result = subject.canFit(inventoryWidth, inventoryHeight);
                
                //Assert
                assertThat(result).isEqualTo(expectedResult);
            }
        }
    }
    
    @Test
    public void recipeShouldMatchForValidInput() {
        
        //Arrange
        final CTRecipeShaped subject = makeRecipe();
        final World world = context.minecraftContext.getWorld();
        final MockItems mockItems = context.mockItems;
        
        final MockCraftingInventory inventory = new MockCraftingInventory();
        inventory.setInputs(new IItemStack[][] {
                {mockItems.redstone, mockItems.ironNugget, mockItems.redstone},
                {mockItems.redstone, mockItems.ironIngot, mockItems.redstone},
                {mockItems.redstone, mockItems.redstone, mockItems.redstone},
        });
        
        
        //Act
        final boolean matches = subject.matches(inventory, world);
        
        //Assert
        assertThat(matches).isTrue();
    }
    
    @Test
    public void recipeShouldNotMatchForInvalidInput() {
        
        //Arrange
        final CTRecipeShaped subject = makeRecipe();
        final MockCraftingInventory inventory = new MockCraftingInventory();
        final MockItems mockItems = context.mockItems;
        inventory.setInputs(new IItemStack[][] {
                {mockItems.bedrock, mockItems.bedrock, mockItems.bedrock},
                {mockItems.bedrock, mockItems.bedrock, mockItems.bedrock},
                {mockItems.bedrock, mockItems.bedrock, mockItems.bedrock},
        });
        
        
        //Act
        final boolean matches = subject.matches(inventory, null);
        
        //Assert
        assertThat(matches).isFalse();
    }
    
}