package com.blamejared.crafttweaker.impl_native.block.material;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.block.material.Material;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ExpandMaterialTest {
    
    @SuppressWarnings("unused")
    public static Stream<Arguments> getKnownMaterials() {
        
        return Arrays.stream(Material.class.getFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()) && field.getType().equals(Material.class))
                .map(LamdbaExceptionUtils.rethrowFunction(field -> Arguments.of(field.getName(), field.get(null))));
        
    }
    
    @ParameterizedTest
    @MethodSource("getKnownMaterials")
    void tryGetContainsKnownMaterials(String materialName, Material expectedMaterial) {
        
        //Arrange - none
        //Act
        final Material material = ExpandMaterial.tryGet(materialName);
        
        //Assert
        assertThat(material).isSameAs(expectedMaterial);
    }
    
    @Test
    void tryGetReturnsNullForUnknownMaterialName() {
        //Arrange
        final String materialName = "SomeUnknownMaterialName";
        
        //Act
        final Material material = ExpandMaterial.tryGet(materialName);
        
        //Assert
        assertThat(material).isNull();
    }
    
    @Test
    void tryGetIsCaseInsensitive() {
        //Arrange
        final String materialName = "aIr";
        
        //Act
        final Material material = ExpandMaterial.tryGet(materialName);
        
        //Assert
        assertThat(material).isNotNull();
    }
    
    @Test
    void getCommandStringUsesMaterialNameForKnownMaterial() {
        //Arrange
        final Material material = Material.AIR;
        final String expectedBEP = "<blockmaterial:AIR>";
        
        //Act
        final String commandString = ExpandMaterial.getCommandString(material);
        
        //Assert
        assertThat(commandString).isEqualTo(expectedBEP);
    }
    
}