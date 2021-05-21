package com.blamejared.crafttweaker.impl.data;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.ICollectionData;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.NBTConverter;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @docParam this ["Hello", "World", "!"]
 */
@ZenCodeType.Name("crafttweaker.api.data.ListData")
@ZenRegister
@Document("vanilla/api/data/ListData")
public class ListData implements ICollectionData {
    
    private final ListNBT internal;
    
    public ListData(ListNBT internal) {
        this.internal = internal;
    }
    
    @ZenCodeType.Constructor
    public ListData() {
        this.internal = new ListNBT();
    }
    
    @ZenCodeType.Constructor
    public ListData(List<IData> list) {
        this.internal = new ListNBT();
        if(list != null)
            list.forEach(iData -> getInternal().add(iData.getInternal()));
    }
    
    @ZenCodeType.Constructor
    public ListData(IData... array) {
        this(getArraySafe(array));
    }
    
    private static List<IData> getArraySafe(IData... array) {
        if(array == null) {
            array = new IData[0];
        }
        return Arrays.asList(array);
    }
    
    
    @Override
    public IData setAt(int index, IData value) {
        return NBTConverter.convert(getInternal().set(index, value.getInternal()));
    }
    
    @Override
    public void add(int index, IData value) {
        getInternal().add(index, value.getInternal());
    }
    
    @Override
    public void add(IData value) {
        getInternal().add(value.getInternal());
    }
    
    @Override
    public IData remove(int index) {
        return NBTConverter.convert(getInternal().remove(index));
    }
    
    @Override
    public IData getAt(int index) {
        return NBTConverter.convert(getInternal().get(index));
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
    public IData copy() {
        return new ListData(getInternal());
    }
    
    @Override
    public IData copyInternal() {
        return new ListData(getInternal().copy());
    }
    
    @Override
    public ListNBT getInternal() {
        return internal;
    }
    
    @Override
    public List<IData> asList() {
        List<IData> data = new ArrayList<>();
        for(INBT inbt : getInternal()) {
            data.add(NBTConverter.convert(inbt));
        }
        return data;
    }
    
    @Override
    public boolean contains(IData data) {
        List<IData> dataValues = data.asList();
        if(dataValues != null && containsList(dataValues))
            return true;
        
        for(INBT value : getInternal()) {
            if(NBTConverter.convert(value).contains(data))
                return true;
        }
        
        return false;
    }
    
    private boolean containsList(List<IData> dataValues) {
        outer:
        for(IData dataValue : dataValues) {
            for(INBT value : getInternal()) {
                if(NBTConverter.convert(value).contains(dataValue))
                    continue outer;
            }
            
            return false;
        }
        
        return true;
    }
    
    @Override
    public String asString() {
        StringBuilder output = new StringBuilder();
        output.append('[');
        boolean first = true;
        for(INBT inbt : getInternal()) {
            if(first) {
                first = false;
            } else {
                output.append(", ");
            }
            output.append(NBTConverter.convert(inbt).asString());
        }
        output.append(']');
        return output.toString();
    }
    
    @ZenCodeType.Caster(implicit = true)
    public List<IData> castToList() {
        return this.asList();
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        
        ListData listData = (ListData) o;
    
        return internal.equals(listData.internal);
    }
    
    @Override
    public int hashCode() {
        
        return internal.hashCode();
    }
    
}
