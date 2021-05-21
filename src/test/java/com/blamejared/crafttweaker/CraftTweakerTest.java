package com.blamejared.crafttweaker;

import com.blamejared.crafttweaker.test_api.context.TestContext;
import org.junit.jupiter.api.BeforeEach;


public class CraftTweakerTest {
    
    protected TestContext testContext;
    
    @BeforeEach
    public void beforeEach() {
        
        testContext = new TestContext();
    }
    
}
