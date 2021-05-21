package com.blamejared.crafttweaker.impl.data;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.INumberData;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.nbt.IntNBT;
import org.openzen.zencode.java.ZenCodeType;

/**
 * @docParam this 8192
 */
@ZenCodeType.Name("crafttweaker.api.data.IntData")
@ZenRegister
@Document("vanilla/api/data/IntData")
public class IntData implements INumberData {
    
    private final IntNBT internal;
    
    public IntData(IntNBT internal) {
        this.internal = internal;
    }
    
    @ZenCodeType.Constructor
    public IntData(int internal) {
        this.internal = IntNBT.valueOf(internal);
    }
    
    @Override
    public IData copy() {
        return new IntData(getInternal());
    }
    
    @Override
    public IData copyInternal() {
        return new IntData(getInternal().copy());
    }
    
    @Override
    public IntNBT getInternal() {
        return internal;
    }
    
    @Override
    public boolean contains(IData data) {
        if(data instanceof IntData) {
            return getInternal().getInt() == ((IntData) data).getInternal().getInt();
        }
        return false;
    }
    
    @Override
    public String asString() {
        return getInternal().getInt() + " as int";
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        
        IntData intData = (IntData) o;
    
        return internal.equals(intData.internal);
    }
    
    @Override
    public int hashCode() {
        
        return internal.hashCode();
    }
    
}
