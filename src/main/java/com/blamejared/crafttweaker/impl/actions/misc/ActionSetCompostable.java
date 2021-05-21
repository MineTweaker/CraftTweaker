package com.blamejared.crafttweaker.impl.actions.misc;

import com.blamejared.crafttweaker.api.actions.IUndoableAction;
import com.blamejared.crafttweaker.api.item.IItemStack;
import net.minecraft.block.ComposterBlock;
import net.minecraftforge.fml.LogicalSide;

public class ActionSetCompostable implements IUndoableAction {
    
    private final IItemStack stack;
    private final float newValue;
    private final float oldValue;
    
    public ActionSetCompostable(IItemStack stack, float newValue) {
        this.stack = stack;
        this.newValue = newValue;
        this.oldValue = ComposterBlock.CHANCES.getOrDefault(stack.getInternal().getItem(), 0);
    }
    
    @Override
    public void undo() {
        ComposterBlock.CHANCES.put(stack.getInternal().getItem(), oldValue);
    }
    
    @Override
    public String describeUndo() {
        return "Undoing setting Composter value of: " + stack.getCommandString() + ", to: " + newValue + ", reverting to: " + oldValue;
    }
    
    @Override
    public void apply() {
        ComposterBlock.CHANCES.put(stack.getInternal().getItem(), newValue);
    }
    
    @Override
    public String describe() {
        return "Setting Composter value of: " + stack.getCommandString() + ", to: " + newValue + ", from: " + oldValue;
    }
    
    @Override
    public boolean shouldApplyOn(LogicalSide side) {
        return true;
    }
}
