package com.blamejared.crafttweaker.impl.actions.brewing;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.actions.IUndoableAction;
import com.blamejared.crafttweaker.api.logger.LogLevel;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;

import java.lang.reflect.Field;
import java.util.List;

public abstract class ActionBrewingBase implements IUndoableAction {
    
    protected final List<IBrewingRecipe> recipes;
    
    private Field inputField;
    private Field reagentField;
    private Field outputField;
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected ActionBrewingBase(List<IBrewingRecipe> recipes) {
        
        this.recipes = recipes;
        try {
            //Raw so that the type constraints on findField work properly
            Class mixPredicate = Class.forName("net.minecraft.potion.PotionBrewing$MixPredicate");
            inputField = ObfuscationReflectionHelper.findField(mixPredicate, "field_185198_a");
            inputField.setAccessible(true);
            reagentField = ObfuscationReflectionHelper.findField(mixPredicate, "field_185199_b");
            reagentField.setAccessible(true);
            outputField = ObfuscationReflectionHelper.findField(mixPredicate, "field_185200_c");
            outputField.setAccessible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    protected Ingredient getItemReagent(Object mixInstance) {
        
        return getFromField(reagentField, mixInstance);
    }
    
    protected IRegistryDelegate<Potion> getPotionInput(Object mixInstance) {
        
        return getFromField(inputField, mixInstance);
    }
    
    protected IRegistryDelegate<Potion> getPotionOutput(Object mixInstance) {
        
        return getFromField(outputField, mixInstance);
    }
    
    @SuppressWarnings("unchecked")
    private <T> T getFromField(Field field, Object mixInstance) {
        
        try {
            return (T) field.get(mixInstance);
        } catch(IllegalAccessException e) {
            e.printStackTrace();//TODO: Remove me?
            CraftTweakerAPI.logger.throwing(LogLevel.DEBUG, "Could not get Field: ", e);
            return null;
        }
    }
    
}
