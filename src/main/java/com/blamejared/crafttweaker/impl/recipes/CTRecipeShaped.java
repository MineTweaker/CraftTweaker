package com.blamejared.crafttweaker.impl.recipes;

import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.api.util.ArrayUtil;
import com.blamejared.crafttweaker.impl.item.MCItemStack;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CTRecipeShaped implements ICraftingRecipe, net.minecraftforge.common.crafting.IShapedRecipe<CraftingInventory> {
    
    private static final IntPair INVALID = new IntPair(-1, -1);
    
    private final IIngredient[][] ingredients;
    private final IItemStack output;
    private final boolean mirrored;
    @Nullable
    private final IRecipeManager.RecipeFunctionMatrix function;
    private final ResourceLocation resourceLocation;
    
    private final int width, height;
    
    public CTRecipeShaped(String name, IItemStack output, IIngredient[][] ingredients, boolean mirrored, @Nullable IRecipeManager.RecipeFunctionMatrix function) {
        this.resourceLocation = new ResourceLocation("crafttweaker", name);
        this.output = output;
        this.ingredients = ingredients;
        this.mirrored = mirrored;
        this.function = function;
        this.height = ingredients.length;
        this.width = Arrays.stream(ingredients).mapToInt(row -> row.length).max().orElse(0);
    }
    
    private IntPair calculateOffset(CraftingInventory inv) {
        IntPair offset = calculateOffset(this.ingredients, inv);
        if(offset != INVALID || !mirrored)
            return offset;
        final IIngredient[][] ingredients = ArrayUtil.mirror(this.ingredients);
        offset = calculateOffset(ingredients, inv);
        if(offset != INVALID)
            return offset;
        
        for(int i = 0; i < ingredients.length; i++) {
            ingredients[i] = ArrayUtil.mirror(ingredients[i]);
        }
        
        offset = calculateOffset(ingredients, inv);
        if(offset != INVALID)
            return offset;
        
        return calculateOffset(ArrayUtil.mirror(ingredients), inv);
        
    }
    
    private IntPair calculateOffset(IIngredient[][] test, CraftingInventory inv) {
        for(int rowOffset = 0; rowOffset <= inv.getHeight() - test.length; rowOffset++) {
            offset:
            for(int columnOffset = 0; columnOffset <= inv.getWidth() - test[0].length; columnOffset++) {
                final boolean[] visited = new boolean[inv.getSizeInventory()];
                
                for(int rowIndex = 0; rowIndex < test.length; rowIndex++) {
                    final IIngredient[] row = test[rowIndex];
                    for(int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                        final IIngredient item = row[columnIndex];
                        final int slotNumber = (rowIndex + rowOffset) * inv.getWidth() + columnIndex + columnOffset;
                        final ItemStack stackInSlot = inv.getStackInSlot(slotNumber);
                        
                        if(item == null && !stackInSlot.isEmpty() || item != null && !item.matches(new MCItemStackMutable(stackInSlot))) {
                            continue offset;
                        }
                        visited[slotNumber] = true;
                    }
                }
                
                for(int i = 0; i < visited.length; i++) {
                    if(!visited[i] && !inv.getStackInSlot(i).isEmpty())
                        continue offset;
                }
                return new IntPair(rowOffset, columnOffset);
            }
        }
        return INVALID;
    }
    
    @Override
    public boolean matches(CraftingInventory inv, @Nullable World worldIn) {
        return calculateOffset(inv) != INVALID;
    }
    
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        final IntPair offset = calculateOffset(inv);
        if(offset == INVALID)
            return ItemStack.EMPTY;
        
        if(function == null)
            return getRecipeOutput();
        
        final int rowOffset;
        final int columnOffset;
        {
            rowOffset = offset.getX();
            columnOffset = offset.getY();
        }
        
        IItemStack[][] stacks = new IItemStack[height][width];
        for(int rowIndex = 0; rowIndex < this.ingredients.length; rowIndex++) {
            final IIngredient[] row = this.ingredients[rowIndex];
            for(int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                final IIngredient ingredient = row[columnIndex];
                if(ingredient == null)
                    continue;
                final int slotIndex = (rowIndex + rowOffset) * inv.getWidth() + columnIndex + columnOffset;
                stacks[rowIndex][columnIndex] = new MCItemStack(inv.getStackInSlot(slotIndex)).setAmount(1);
            }
        }
        return function.process(this.output, stacks).getImmutableInternal();
    }
    
    @Override
    public boolean canFit(int width, int height) {
        return width >= this.width && height >= this.height;
    }
    
    @Override
    public ItemStack getRecipeOutput() {
        return output.getInternal().copy();
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        final NonNullList<ItemStack> result = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        final int rowOffset;
        final int columnOffset;
        
        {
            final IntPair offset = calculateOffset(inv);
            rowOffset = offset.getX();
            columnOffset = offset.getY();
            
            if(offset == INVALID)
                return result;
        }
        
        for(int rowIndex = 0; rowIndex < this.ingredients.length; rowIndex++) {
            final IIngredient[] row = this.ingredients[rowIndex];
            for(int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                final IIngredient ingredient = row[columnIndex];
                if(ingredient == null)
                    continue;
                final int slotIndex = (rowIndex + rowOffset) * inv.getWidth() + columnIndex + columnOffset;
                result.set(slotIndex, ingredient.getRemainingItem(new MCItemStack(inv.getStackInSlot(slotIndex))).getInternal());
            }
        }
        
        return result;
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(this.height * this.width, Ingredient.EMPTY);
        for(int row = 0; row < this.ingredients.length; row++) {
            IIngredient[] ingredientRow = this.ingredients[row];
            for(int column = 0; column < ingredientRow.length; column++) {
                ingredients.set(row * width + column, ingredientRow[column].asVanillaIngredient());
            }
        }
        return ingredients;
    }
    
    @Override
    public boolean isDynamic() {
        return false;
    }
    
    @Override
    public String getGroup() {
        return ICraftingRecipe.super.getGroup();
    }
    
    @Override
    public ResourceLocation getId() {
        return resourceLocation;
    }
    
    @Override
    public IRecipeSerializer<CTRecipeShaped> getSerializer() {
        return SerializerShaped.INSTANCE;
    }
    
    @Override
    public int getRecipeWidth() {
        return width;
    }
    
    @Override
    public int getRecipeHeight() {
        return height;
    }
    
    
    public boolean isMirrored() {
        return mirrored;
    }
    
    private static final class IntPair {
        
        private final int x, y;
        
        private IntPair(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        int getX() {
            return x;
        }
        
        int getY() {
            return y;
        }
    }
    
}
