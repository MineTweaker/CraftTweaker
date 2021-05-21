package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.impl.data.*;
import net.minecraft.nbt.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class NBTConverterTest {
    
    @Test
    @SuppressWarnings("ConstantConditions")
    public void nullReturnsNull() {
        //Arrange - none
        //Act
        final IData convert = NBTConverter.convert(null);
        
        //Assert
        assertThat(convert).isNull();
    }
    
    @Test
    public void byteNBTReturnsByteData() {
        //Arrange
        final byte value = 13;
        final ByteNBT byteNBT = ByteNBT.valueOf(value);
        
        //Act
        final IData convert = NBTConverter.convert(byteNBT);
        
        //Assert
        assertThat(convert).isEqualTo(new ByteData(value));
    }
    
    @Test
    public void shortNBTReturnsShortData() {
        //Arrange
        final short value = 15;
        final ShortNBT shortNbt = ShortNBT.valueOf(value);
        
        //Act
        final IData convert = NBTConverter.convert(shortNbt);
        
        //Assert
        assertThat(convert).isEqualTo(new ShortData(value));
    }
    
    @Test
    public void intNBTReturnsIntData() {
        //Arrange
        final int value = 15;
        final IntNBT intNbt = IntNBT.valueOf(value);
        
        //Act
        final IData convert = NBTConverter.convert(intNbt);
        
        //Assert
        assertThat(convert).isEqualTo(new IntData(value));
    }
    
    @Test
    public void longNBTReturnsLongData() {
        //Arrange
        final long value = 15;
        final LongNBT longNbt = LongNBT.valueOf(value);
        
        //Act
        final IData convert = NBTConverter.convert(longNbt);
        
        //Assert
        assertThat(convert).isEqualTo(new LongData(value));
    }
    
    @Test
    public void floatNBTReturnsFloatData() {
        //Arrange
        final float value = 15;
        final FloatNBT floatNbt = FloatNBT.valueOf(value);
        
        //Act
        final IData convert = NBTConverter.convert(floatNbt);
        
        //Assert
        assertThat(convert).isEqualTo(new FloatData(value));
    }
    
    @Test
    public void doubleNBTReturnsDoubleData() {
        //Arrange
        final double value = 15;
        final DoubleNBT doubleNbt = DoubleNBT.valueOf(value);
        
        //Act
        final IData convert = NBTConverter.convert(doubleNbt);
        
        //Assert
        assertThat(convert).isEqualTo(new DoubleData(value));
    }
    
    @Test
    public void stringNBTReturnsStringData() {
        //Arrange
        final String value = "Hello World";
        final StringNBT stringNBT = StringNBT.valueOf(value);
        
        //Act
        final IData convert = NBTConverter.convert(stringNBT);
        
        //Assert
        assertThat(convert).isEqualTo(new StringData(value));
    }
    
    
    @Test
    public void byteArrayNBTReturnsByteArrayData() {
        //Arrange
        final byte[] value = new byte[] {1, 2, 3};
        final ByteArrayNBT byteArrayNBT = new ByteArrayNBT(value);
        
        //Act
        final IData convert = NBTConverter.convert(byteArrayNBT);
        
        //Assert
        assertThat(convert).isEqualTo(new ByteArrayData(value));
    }
    
    @Test
    public void intArrayNBTReturnsIntArrayData() {
        //Arrange
        final int[] value = new int[] {1, 2, 3};
        final IntArrayNBT intArrayNBT = new IntArrayNBT(value);
        
        //Act
        final IData convert = NBTConverter.convert(intArrayNBT);
        
        //Assert
        assertThat(convert).isEqualTo(new IntArrayData(value));
    }
    
    @Test
    public void longArrayNBTReturnsLongArrayData() {
        //Arrange
        final long[] value = new long[] {1, 2, 3};
        final LongArrayNBT longArrayNBT = new LongArrayNBT(value);
        
        //Act
        final IData convert = NBTConverter.convert(longArrayNBT);
        
        //Assert
        assertThat(convert).isEqualTo(new LongArrayData(value));
    }
    
    @Test
    public void listNBTReturnsListData() {
        //Arrange
        final ListNBT listNBT = new ListNBT();
        listNBT.add(IntNBT.valueOf(1));
        listNBT.add(IntNBT.valueOf(2));
        listNBT.add(IntNBT.valueOf(3));
        
        //Act
        final IData convert = NBTConverter.convert(listNBT);
        
        //Assert
        assertThat(convert).isEqualTo(new ListData(listNBT));
    }
    
    @Test
    public void compoundNBTReturnsMapData() {
        //Arrange
        final CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("Hello", 0);
        compoundNBT.putInt("World", 1);
        
        //Act
        final IData convert = NBTConverter.convert(compoundNBT);
        
        assertThat(convert).isEqualTo(new MapData(compoundNBT));
    }
    
}