package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.impl.data.IntData;
import com.blamejared.crafttweaker.impl.data.ListData;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;
import java.util.Map;

/**
 * The IData interface is a generic Interface for handling Data like NBT.
 * You can cast about all primitives (short, double, string, int, ...) as well as certain arrays to IData.
 * Remember that while they offer similar features, IData and their counterparts are NOT the same, which is why they will be referred to as DataTypes (e.g. {@link com.blamejared.crafttweaker.impl.data.ByteData}).
 *
 * @docParam this {Display: {lore: ["Hello", "World"]}}
 */
@ZenCodeType.Name("crafttweaker.api.data.IData")
@ZenRegister
@Document("vanilla/api/data/IData")
public interface IData {
    
    TextFormatting SYNTAX_HIGHLIGHTING_KEY = TextFormatting.AQUA;
    TextFormatting SYNTAX_HIGHLIGHTING_STRING = TextFormatting.GREEN;
    TextFormatting SYNTAX_HIGHLIGHTING_QUOTE = TextFormatting.GRAY;
    TextFormatting SYNTAX_HIGHLIGHTING_NUMBER = TextFormatting.GOLD;
    TextFormatting SYNTAX_HIGHLIGHTING_TYPE = TextFormatting.RED;
    TextFormatting SYNTAX_HIGHLIGHTING_AS = TextFormatting.GRAY;
    
    /**
     * Gets the ID of the internal NBT tag.
     *
     * Used to determine what NBT type is stored (in a list for example)
     *
     * @return ID of the NBT tag that this data represents.
     */
    @ZenCodeType.Method
    default byte getId() {
        
        return getInternal().getId();
    }
    
    /**
     * Makes a copy of this IData.
     *
     * IData is immutable by default, use this to create a proper copy of the object.
     *
     * @return a copy of this IData.
     */
    @ZenCodeType.Method
    IData copy();
    
    /**
     * Makes a copy of this IData with a copy of the internal INBT object.
     *
     * @return a copy of this IData with a copy of the internal INBT object.
     */
    IData copyInternal();
    
    /**
     * Gets the internal INBT stored in this IData.
     *
     * @return the vanilla INBT tag that this IData represents.
     */
    INBT getInternal();
    
    
    /**
     * Gets the String representation of the internal INBT tag
     *
     * @return String that represents the internal INBT of this IData.
     */
    @ZenCodeType.Method
    default String getString() {
        
        return getInternal().getString();
    }
    
    /**
     * Checks if this IData contains another IData, mainly used in subclasses of {@link com.blamejared.crafttweaker.api.data.ICollectionData}, is the same as an equals check on other IData types
     *
     * @param data data to check if it is contained
     *
     * @return true if the given IData is contained in this IData
     *
     * @docParam data "Display"
     */
    @ZenCodeType.Method
    @ZenCodeType.Operator(ZenCodeType.OperatorType.CONTAINS)
    default boolean contains(IData data) {
        
        return getInternal().equals(data.getInternal());
    }
    
    /**
     * Gets a Map<String, IData> representation of this IData, returns null on anything but {@link com.blamejared.crafttweaker.impl.data.MapData}.
     *
     * @return null if this IData is not a map.
     */
    @ZenCodeType.Method
    default Map<String, IData> asMap() {
        
        return null;
    }
    
    /**
     * Gets a List<IData> representation of this IData, returns null on anything but {@link com.blamejared.crafttweaker.impl.data.ListData}.
     *
     * @return null if this IData is not a list.
     */
    @ZenCodeType.Method
    default List<IData> asList() {
        
        return null;
    }
    
    /**
     * Gets the String representation of this IData
     *
     * @return String that represents this IData (value and type).
     */
    @ZenCodeType.Method
    String asString();
    
    /**
     * Gets the Formatted String representation of this IData
     *
     * @return String that represents this IData (value and type).
     */
    default ITextComponent asFormattedComponent(String indentation, int indentDepth) {
        return new StringTextComponent(toJsonString());
    }
    
    String toJsonString();
    
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default INumberData asNumber() {
        
        if(this instanceof INumberData) {
            return (INumberData) this;
        }
        
        return new IntData(0);
    }
    
    @ZenCodeType.Caster
    @ZenCodeType.Method
    default ICollectionData asCollection() {
        
        if(this instanceof ICollectionData) {
            return (ICollectionData) this;
        }
        
        return new ListData();
    }
    
    
}
