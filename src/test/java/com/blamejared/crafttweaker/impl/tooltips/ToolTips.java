package com.blamejared.crafttweaker.impl.tooltips;

import com.blamejared.crafttweaker.CraftTweakerTest;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.item.tooltip.ITooltipFunction;
import com.blamejared.crafttweaker.impl.actions.items.tooltips.ActionAddTooltip;
import com.blamejared.crafttweaker.impl.events.CTClientEventHandler;
import com.blamejared.crafttweaker.impl.util.text.MCTextComponent;
import net.minecraft.util.text.ITextComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolTips extends CraftTweakerTest {
    
    
    @AfterEach
    public void afterEach() {
        
        CTClientEventHandler.TOOLTIPS.clear();
    }
    
    @Test
    public void addTooltipCreatesAction() {
        
        final IItemStack redstone = testContext.mockItems.redstone;
        redstone.addTooltip(getTextComponent());
        
        testContext.actionApplier.shouldHaveAction(ActionAddTooltip.class, 1);
    }
    
    @Nonnull
    private MCTextComponent getTextComponent() {
        
        return new MCTextComponent(ITextComponent.getTextComponentOrEmpty("Hello World"));
    }
    
    @Test
    public void actionAddTooltipAddsToClientMap() {
        //Arrange
        final IItemStack redstone = testContext.mockItems.redstone;
        final MCTextComponent textComponent = getTextComponent();
        final ActionAddTooltip actionAddTooltip = new ActionAddTooltip(redstone, textComponent);
        final List<MCTextComponent> tooltip = new ArrayList<>();
        
        //Act
        actionAddTooltip.apply();
        
        //Assert
        assertThat(CTClientEventHandler.TOOLTIPS).containsOnlyKeys(redstone);
        final LinkedList<ITooltipFunction> functions = CTClientEventHandler.TOOLTIPS.get(redstone);
        assertThat(functions.size()).isEqualTo(1);
        
        //Act again
        final ITooltipFunction iTooltipFunction = functions.get(0);
        iTooltipFunction.apply(redstone, tooltip, false);
        
        //Assert Again
        assertThat(tooltip.size()).isEqualTo(1);
        assertThat(tooltip.get(0)).isEqualTo(textComponent);
    }
    
}
