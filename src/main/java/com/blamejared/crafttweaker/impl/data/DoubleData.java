package com.blamejared.crafttweaker.impl.data;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.INumberData;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.nbt.DoubleNBT;
import org.openzen.zencode.java.ZenCodeType;

/**
 * @docParam this 3.25
 */
@ZenCodeType.Name("crafttweaker.api.data.DoubleData")
@ZenRegister
@Document("vanilla/api/data/DoubleData")
public class DoubleData implements INumberData {
    
    private final DoubleNBT internal;
    
    public DoubleData(DoubleNBT internal) {
        this.internal = internal;
    }
    
    @ZenCodeType.Constructor
    public DoubleData(double internal) {
        this.internal = DoubleNBT.valueOf(internal);
    }
    
    @Override
    public IData copy() {
        return new DoubleData(getInternal());
    }
    
    @Override
    public IData copyInternal() {
        return new DoubleData(getInternal().copy());
    }
    
    @Override
    public DoubleNBT getInternal() {
        return internal;
    }
    
    @Override
    public boolean contains(IData data) {
        if(data instanceof DoubleData) {
            return getInternal().getDouble() == ((DoubleData) data).getInternal().getDouble();
        }
        return false;
    }
    
    @Override
    public String asString() {
        return getInternal().getDouble() + " as double";
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        
        DoubleData that = (DoubleData) o;
    
        return internal.equals(that.internal);
    }
    
    @Override
    public int hashCode() {
        
        return internal.hashCode();
    }
    
}
