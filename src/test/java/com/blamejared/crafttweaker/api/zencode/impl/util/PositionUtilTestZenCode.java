package com.blamejared.crafttweaker.api.zencode.impl.util;

import org.junit.jupiter.api.Test;
import org.openzen.zencode.java.ZenCodeGlobals;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zenscript.scriptingexample.tests.helpers.ScriptBuilder;
import org.openzen.zenscript.scriptingexample.tests.helpers.ZenCodeTest;

import java.util.ArrayList;
import java.util.List;

public class PositionUtilTestZenCode extends ZenCodeTest {
    
    @Override
    public List<Class<?>> getRequiredClasses() {
        
        final ArrayList<Class<?>> classes = new ArrayList<>(super.getRequiredClasses());
        classes.add(ActionClass.class);
        return classes;
    }
    
    @Test
    public void getPositionReturnsProperPosition() {
        
        //Arrange
        final String fileName = "positionTestFile.zs";
        final ScriptBuilder scriptBuilder = ScriptBuilder.create()
                .startNewScript(fileName)
                .add("println(getCurrentFileFull());");
        
        //Act
        scriptBuilder.execute(this);
        
        //Assert
        logger.assertPrintOutputSize(1);
        
        final String expectedFileNameFull = String.format("%s:1:0", fileName);
        logger.assertPrintOutput(0, expectedFileNameFull);
    }
    
    @Test
    public void getPositionReturnsFileNameOfTopMostFile() {
        
        //Arrange
        final String fileNameCallee = "positionTestFileCallee.zs";
        final String fileNameCaller = "positionTestFileCaller.zs";
        final ScriptBuilder scriptBuilder = ScriptBuilder.create()
                .startNewScript(fileNameCallee)
                .add("public function doTheThing() as void {")
                .add("    println(getCurrentFileName());")
                .add("}")
                .startNewScript(fileNameCaller)
                .add("doTheThing();");
        
        //Act
        scriptBuilder.execute(this);
        
        //Assert
        logger.assertPrintOutputSize(1);
        logger.assertPrintOutput(0, fileNameCallee);
    }
    
    
    @ZenCodeType.Name(".ActionClass")
    public static final class ActionClass {
        
        @ZenCodeGlobals.Global
        public static String getCurrentFileName() {
            
            return PositionUtil.getZCScriptPositionFromStackTrace().getFilename();
        }
        
        @ZenCodeGlobals.Global
        public static String getCurrentFileFull() {
            
            return PositionUtil.getZCScriptPositionFromStackTrace().toString();
        }
        
    }
    
}
