package com.blamejared.crafttweaker.impl.data;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.INumberData;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.nbt.FloatNBT;
import org.openzen.zencode.java.ZenCodeType;

/**
 * @docParam this 8.5
 */
@ZenCodeType.Name("crafttweaker.api.data.FloatData")
@ZenRegister
@Document("vanilla/api/data/FloatData")
public class FloatData implements INumberData {
    
    private final FloatNBT internal;
    
    public FloatData(FloatNBT internal) {
        this.internal = internal;
    }
    
    @ZenCodeType.Constructor
    public FloatData(float internal) {
        this.internal = FloatNBT.valueOf(internal);
    }
    
    @Override
    public IData copy() {
        return new FloatData(getInternal());
    }
    
    @Override
    public IData copyInternal() {
        return new FloatData(getInternal().copy());
    }
    
    @Override
    public FloatNBT getInternal() {
        return internal;
    }
    
    @Override
    public boolean contains(IData data) {
        if(data instanceof FloatData) {
            return getInternal().getFloat() == ((FloatData) data).getInternal().getFloat();
        }
        return false;
    }
    
    @Override
    public String asString() {
        return getInternal().getFloat() + " as float";
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        
        FloatData floatData = (FloatData) o;
    
        return internal.equals(floatData.internal);
    }
    
    @Override
    public int hashCode() {
        
        return internal.hashCode();
    }
    
}
