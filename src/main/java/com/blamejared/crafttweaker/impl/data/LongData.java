package com.blamejared.crafttweaker.impl.data;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.INumberData;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.nbt.LongNBT;
import org.openzen.zencode.java.ZenCodeType;

/**
 * @docParam this 800000000
 */
@ZenCodeType.Name("crafttweaker.api.data.LongData")
@ZenRegister
@Document("vanilla/api/data/LongData")
public class LongData implements INumberData {
    
    private final LongNBT internal;
    
    public LongData(LongNBT internal) {
        this.internal = internal;
    }
    
    @ZenCodeType.Constructor
    public LongData(long internal) {
        this.internal = LongNBT.valueOf(internal);
    }
    
    @Override
    public IData copy() {
        return new LongData(getInternal());
    }
    
    @Override
    public IData copyInternal() {
        return new LongData(getInternal().copy());
    }
    
    @Override
    public LongNBT getInternal() {
        return internal;
    }
    
    @Override
    public boolean contains(IData data) {
        if(data instanceof LongData) {
            return getInternal().getLong() == ((LongData) data).getInternal().getLong();
        }
        return false;
    }
    
    @Override
    public String asString() {
        return getInternal().getLong() + " as long";
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        
        LongData longData = (LongData) o;
    
        return internal.equals(longData.internal);
    }
    
    @Override
    public int hashCode() {
        
        return internal.hashCode();
    }
    
}
