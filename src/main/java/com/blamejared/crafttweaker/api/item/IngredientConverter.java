package com.blamejared.crafttweaker.api.item;

import com.blamejared.crafttweaker.impl.item.MCIngredientList;
import com.blamejared.crafttweaker.impl.item.MCItemStack;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.blamejared.crafttweaker.impl.tag.expansions.ExpandItemTag;
import com.blamejared.crafttweaker.impl.tag.manager.TagManagerItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class IngredientConverter {
    
    public static IIngredient fromIngredient(Ingredient ingredient) {
        
        if(ingredient instanceof IngredientVanillaPlus) {
            return ((IngredientVanillaPlus) ingredient).getCrTIngredient();
        }
        
        if(ingredient == Ingredient.EMPTY) {
            return empty();
        }
        
        return fromIItemLists(ingredient.acceptedItems);
    }
    
    private static IIngredient fromIItemLists(Ingredient.IItemList... itemLists) {
        
        final IIngredient[] ingredients = Arrays.stream(itemLists)
                .map(IngredientConverter::fromIItemList)
                .filter(IngredientConverter::notEmptyStack)
                .toArray(IIngredient[]::new);
        
        return mergeIngredients(ingredients);
    }
    
    
    //All Ingredients have a reference to this
    //Allows us to check for tags
    private static IIngredient fromIItemList(Ingredient.IItemList itemList) {
        
        //Tags -> MCTag
        if(itemList instanceof Ingredient.TagList) {
            return fromTagList((Ingredient.TagList) itemList);
        }
        
        //Forge StackList, or anything else -> check matching stacks
        final IItemStack[] ingredients = itemList.getStacks()
                .stream()
                .filter(stack -> !stack.isEmpty())
                .map(MCItemStack::new)
                .toArray(IItemStack[]::new);
        
        return mergeIngredients(ingredients);
    }
    
    private static IIngredient fromTagList(Ingredient.TagList tagList) {
        
        final ResourceLocation location = TagManagerItem.INSTANCE.getTagCollection()
                .getDirectIdFromTag(tagList.tag);
        final MCTag<Item> itemMCTag = new MCTag<>(location, TagManagerItem.INSTANCE);
        return ExpandItemTag.asIIngredient(itemMCTag);
    }
    
    private static IIngredient empty() {
        
        return MCItemStack.EMPTY.get();
    }
    
    private static IIngredient mergeIngredients(IIngredient... ingredients) {
        
        if(ingredients.length == 0) {
            return empty();
        }
        
        if(ingredients.length == 1) {
            return ingredients[0];
        }
        return new MCIngredientList(ingredients);
    }
    
    private static boolean notEmptyStack(IIngredient iIngredient) {
        
        return !(iIngredient instanceof IItemStack && ((IItemStack) iIngredient).isEmpty());
    }
    
}
