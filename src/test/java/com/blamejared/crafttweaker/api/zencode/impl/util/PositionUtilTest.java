package com.blamejared.crafttweaker.api.zencode.impl.util;

import org.junit.jupiter.api.Test;
import org.openzen.zencode.shared.CodePosition;

import static org.assertj.core.api.Assertions.assertThat;

class PositionUtilTest {
    
    @Test
    public void getZCScriptPositionFromStackTraceReturnsUnknownForStackTraceFromJava() {
        //Arrange
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        //Act
        final CodePosition position = PositionUtil.getZCScriptPositionFromStackTrace(stackTrace);
        
        //Assert
        assertThat(position).isSameAs(CodePosition.UNKNOWN);
    }
    
    @Test
    public void getZCScriptPositionFromStackTraceReturnsPositionForStackTraceFromZC() {
        //Arrange
        final int lineNumber = 3;
        final String fileName = "test.zs";
        final String methodName = "methodName";
        final String declaringClass = "DeclaringClass";
        
        final StackTraceElement element = new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
        final StackTraceElement[] stackTrace = new StackTraceElement[] {element};
        
        //Act
        final CodePosition position = PositionUtil.getZCScriptPositionFromStackTrace(stackTrace);
        
        //Assert
        assertThat(position).isNotSameAs(CodePosition.UNKNOWN);
        assertThat(position.fromLine).isEqualTo(lineNumber);
        assertThat(position.toLine).isEqualTo(lineNumber);
        assertThat(position.getFilename()).isEqualTo(fileName);
    }
    
    @Test
    public void getZCScriptPositionFromStackTraceReturnsFirstPositionForStackTraceFromZC() {
        //Arrange
        final int lineNumber = 3;
        final String firstFileName = "test.zs";
        final String secondFileName = "other_file.zs";
        final String methodName = "methodName";
        final String declaringClass = "DeclaringClass";
        
        final StackTraceElement firstElement = new StackTraceElement(declaringClass, methodName, firstFileName, lineNumber);
        final StackTraceElement secondElement = new StackTraceElement(declaringClass, methodName, secondFileName, lineNumber);
        final StackTraceElement[] stackTrace = new StackTraceElement[] {firstElement, secondElement};
        
        //Act
        final CodePosition position = PositionUtil.getZCScriptPositionFromStackTrace(stackTrace);
        
        //Assert
        assertThat(position).isNotSameAs(CodePosition.UNKNOWN);
        assertThat(position.getFilename())
                .isEqualTo(firstFileName)
                .isNotEqualTo(secondFileName);
    }
    
}