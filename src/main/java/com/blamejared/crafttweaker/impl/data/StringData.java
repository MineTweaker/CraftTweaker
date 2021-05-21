package com.blamejared.crafttweaker.impl.data;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.util.StringUtils;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.nbt.StringNBT;
import org.openzen.zencode.java.ZenCodeType;

/**
 * @docParam this new StringData("Hello")
 */
@ZenCodeType.Name("crafttweaker.api.data.StringData")
@ZenRegister
@Document("vanilla/api/data/StringData")
public class StringData implements IData {
    
    private final StringNBT internal;
    
    public StringData(StringNBT internal) {
        this.internal = internal;
    }
    
    @ZenCodeType.Constructor
    public StringData(String internal) {
        this.internal = StringNBT.valueOf(internal);
    }
    
    @Override
    public IData copy() {
        return new StringData(getInternal());
    }
    
    @Override
    public IData copyInternal() {
        return new StringData(getInternal().copy());
    }
    
    @Override
    public StringNBT getInternal() {
        return internal;
    }
    
    
    @Override
    public boolean contains(IData data) {
        return asString().equals(data.asString());
    }
    
    /**
     * Concatenates the two string Datas and returns the result.
     *
     * @param data The other data to append
     *
     * @return A new StringData with the value concatenated.
     * @docParam data new StringData("World")
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.ADD)
    public StringData addTogether(StringData data) {
        return new StringData(internal.getString() + data.getInternal().getString());
    }
    
    @Override
    public String asString() {
        return StringUtils.quoteAndEscape(getInternal().getString()) + " as string";
    }
    
    @Override
    public String toJsonString() {
        return StringUtils.quoteAndEscape(getInternal().getString());
    }
    
    @Override
    public boolean equals(Object o) {
        
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        
        StringData that = (StringData) o;
        
        return internal.equals(that.internal);
    }
    
    @Override
    public int hashCode() {
        
        return internal.hashCode();
    }
    
}
