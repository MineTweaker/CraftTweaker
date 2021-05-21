package com.blamejared.crafttweaker.test_api.context;

import com.blamejared.crafttweaker.api.actions.ActionApplier;
import com.blamejared.crafttweaker.api.actions.IAction;
import org.assertj.core.api.Condition;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class TestActionApplier implements ActionApplier {
    
    private final List<IAction> allActions = new ArrayList<>();
    
    @Override
    public void apply(IAction action) {
        
        allActions.add(action);
    }
    
    public List<IAction> getAllActions() {
        
        return allActions;
    }
    
    public void shouldHaveAction(Class<? extends IAction> actionClass, int times) {
        
        final Condition<IAction> condition = new Condition<>(actionClass::isInstance, "Action is instanceof %s", actionClass);
        assertThat(allActions).areExactly(times, condition);
    }
    
}
