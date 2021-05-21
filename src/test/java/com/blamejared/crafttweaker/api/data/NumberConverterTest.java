package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.impl.data.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class NumberConverterTest {
    
    @Test
    @SuppressWarnings("ConstantConditions")
    public void nullReturnsNull() {
        //Arrange - none
        //Act
        final IData convert = NumberConverter.convertNumber(null);
        
        //Assert
        assertThat(convert).isNull();
    }
    
    @Test
    public void integerReturnsIntegerData() {
        //Arrange
        final int value = 13;
        
        //Act
        final IData convert = NumberConverter.convertNumber(value);
        
        //Assert
        assertThat(convert).isEqualTo(new IntData(value));
    }
    
    @Test
    public void byteReturnsByteData() {
        //Arrange
        final byte value = 13;
        
        //Act
        final IData convert = NumberConverter.convertNumber(value);
        
        //Assert
        assertThat(convert).isEqualTo(new ByteData(value));
    }
    
    @Test
    public void doubleReturnsDoubleData() {
        //Arrange
        final double value = 13;
        
        //Act
        final IData convert = NumberConverter.convertNumber(value);
        
        //Assert
        assertThat(convert).isEqualTo(new DoubleData(value));
    }
    
    @Test
    public void floatReturnsFloatData() {
        //Arrange
        final float value = 13;
        
        //Act
        final IData convert = NumberConverter.convertNumber(value);
        
        //Assert
        assertThat(convert).isEqualTo(new FloatData(value));
    }
    
    @Test
    public void longReturnsLongData() {
        //Arrange
        final long value = 13;
        
        //Act
        final IData convert = NumberConverter.convertNumber(value);
        
        //Assert
        assertThat(convert).isEqualTo(new LongData(value));
    }
    
    @Test
    public void shortReturnsShortData() {
        //Arrange
        final short value = 13;
        
        //Act
        final IData convert = NumberConverter.convertNumber(value);
        
        //Assert
        assertThat(convert).isEqualTo(new ShortData(value));
    }
    
    @Test
    public void unknownNumberTypeReturnsDoubleData() {
        //Arrange
        final double doubleValue = 123;
        final BigDecimal bigDecimal = new BigDecimal(doubleValue);
        
        //Act
        final IData convert = NumberConverter.convertNumber(bigDecimal);
        
        //Assert
        assertThat(convert)
                .isInstanceOf(DoubleData.class)
                .isEqualTo(new DoubleData(doubleValue));
    }
    
}