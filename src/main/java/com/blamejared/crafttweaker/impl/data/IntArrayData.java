package com.blamejared.crafttweaker.impl.data;

import com.blamejared.crafttweaker.api.annotations.*;
import com.blamejared.crafttweaker.api.data.*;
import com.blamejared.crafttweaker_annotations.annotations.*;
import net.minecraft.nbt.*;
import org.openzen.zencode.java.*;

import java.util.*;

/**
 * @docParam this [4, 128, 256, 1024]
 */
@ZenCodeType.Name("crafttweaker.api.data.IntArrayData")
@ZenRegister
@Document("vanilla/api/data/IntArrayData")
public class IntArrayData implements ICollectionData {
    
    private final IntArrayNBT internal;
    
    public IntArrayData(IntArrayNBT internal) {
        this.internal = internal;
    }
    
    @ZenCodeType.Constructor
    public IntArrayData(int[] internal) {
        this.internal = new IntArrayNBT(internal);
    }
    
    @Override
    public IData copy() {
        return new IntArrayData(getInternal());
    }
    
    @Override
    public IData copyInternal() {
        return new IntArrayData(getInternal().copy());
    }
    
    @Override
    public IntArrayNBT getInternal() {
        return internal;
    }
    
    @Override
    public IntData setAt(int index, IData value) {
        if(value instanceof NumberNBT) {
            return new IntData(getInternal().set(index, IntNBT.valueOf(((INumberData) value).getInt())));
        } else {
            return null;
        }
    }
    
    
    @Override
    public void add(int index, IData value) {
        if(value instanceof INumberData) {
            getInternal().add(index, IntNBT.valueOf(((INumberData) value).getInt()));
        }
    }
    
    @Override
    public void add(IData value) {
        if(value instanceof INumberData) {
            getInternal().add(IntNBT.valueOf(((INumberData) value).getInt()));
        }
    }
    
    @Override
    public IntData remove(int index) {
        return new IntData(getInternal().remove(index));
    }
    
    @Override
    public IData getAt(int index) {
        return new IntData(getInternal().get(index));
    }
    
    @Override
    public int size() {
        return getInternal().size();
    }
    
    @Override
    public void clear() {
        getInternal().clear();
    }
    
    @Override
    public String asString() {
        StringBuilder result = new StringBuilder();
        result.append('[');
        boolean first = true;
        for(IntNBT nbt : getInternal()) {
            if(first) {
                first = false;
            } else {
                result.append(", ");
            }
            result.append(nbt.getInt());
        }
        result.append(']');
        return result.toString();
    }
    
    @Override
    public List<IData> asList() {
        final int[] intArray = getInternal().getIntArray();
        List<IData> list = new ArrayList<>(intArray.length);
        for(int i : intArray) {
            list.add(new IntData(i));
        }
        return list;
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        
        IntArrayData that = (IntArrayData) o;
    
        return internal.equals(that.internal);
    }
    
    @Override
    public int hashCode() {
        
        return internal.hashCode();
    }
    
}
