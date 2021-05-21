package com.blamejared.crafttweaker.test_api.context;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.test_api.mocks.items.MockItems;

public class TestContext {
    
    public final TestMinecraftContext minecraftContext;
    public final TestActionApplier actionApplier;
    public final MockItems mockItems;
    
    public TestContext() {
        
        minecraftContext = new TestMinecraftContextUnsafe();
        actionApplier = new TestActionApplier();
        mockItems = new MockItems();
        
        CraftTweakerAPI.setActionApplier(actionApplier);
    }
    
}
