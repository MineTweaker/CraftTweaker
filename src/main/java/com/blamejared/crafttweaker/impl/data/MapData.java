package com.blamejared.crafttweaker.impl.data;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.NBTConverter;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.google.common.base.Strings;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.openzen.zencode.java.ZenCodeType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;


/**
 * @docParam this {Hello : "World", Somewhere: "Over the rainbow"}
 */
@ZenCodeType.Name("crafttweaker.api.data.MapData")
@ZenRegister
@Document("vanilla/api/data/MapData")
public class MapData implements IData {
    
    private final CompoundNBT internal;
    private final Set<String> boolDataKeys;
    
    public MapData(CompoundNBT internal) {
        
        this(internal, new HashSet<>());
    }
    
    @ZenCodeType.Constructor
    public MapData() {
        
        this(new CompoundNBT());
    }
    
    @ZenCodeType.Constructor
    public MapData(Map<String, IData> map) {
        
        this();
        putAll(map);
    }
    
    public MapData(CompoundNBT internal, Set<String> boolDataKeys) {
        
        this.internal = internal;
        this.boolDataKeys = boolDataKeys;
    }
    
    /**
     * Adds all entries from the given map into this one.
     * Can override existing keys.
     *
     * @param map The other entries to be added to this map
     *
     * @docParam map {Hello: "Goodbye", Item: "Bedrock"}
     */
    @ZenCodeType.Method
    public void putAll(Map<String, IData> map) {
        
        map.forEach((s, iData) -> {
            getInternal().put(s, iData.getInternal());
            if(iData instanceof BoolData) {
                boolDataKeys.add(s);
            }
        });
    }
    
    /**
     * Adds all entries from the given IData to this entry
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.ADD)
    public MapData opAdd(IData data) {
        
        putAll(data.asMap());
        return this;
    }
    
    
    @ZenCodeType.Getter("keySet")
    public Set<String> getKeySet() {
        
        return getInternal().keySet();
    }
    
    @ZenCodeType.Getter("size")
    public int getSize() {
        
        return getInternal().size();
    }
    
    /**
     * Adds sets the value for the given key or creates a new entry if it did not exist before.
     *
     * @param key   The key to set the value for.
     * @param value The value to set.
     *
     * @return The previous value if present, null otherwise
     *
     * @docParam key "Hello"
     * @docParam value "Goodbye"
     */
    @ZenCodeType.Method
    @ZenCodeType.Nullable
    @ZenCodeType.Operator(ZenCodeType.OperatorType.INDEXSET)
    public IData put(String key, IData value) {
        
        if(value instanceof BoolData) {
            boolDataKeys.add(key);
        }
        return NBTConverter.convert(getInternal().put(key, value.getInternal()));
    }
    
    /**
     * Retrieves the value associated with the key
     *
     * @param key The key to search for
     *
     * @return The value if present, null otherwise
     *
     * @docParam key "Hello"
     */
    @ZenCodeType.Method
    @ZenCodeType.Nullable
    @ZenCodeType.Operator(ZenCodeType.OperatorType.INDEXGET)
    public IData getAt(String key) {
        
        if(boolDataKeys.contains(key)) {
            return new BoolData(getInternal().getByte(key) == 1);
        }
        return NBTConverter.convert(getInternal().get(key));
    }
    
    /**
     * Checks if the Map contains the given key.
     *
     * @param key The key to search for
     *
     * @return True if the Map contains the key
     *
     * @docParam key "Hello"
     */
    @ZenCodeType.Method
    public boolean contains(String key) {
        
        return getInternal().contains(key);
    }
    
    /**
     * Removes the entry with the given key from the Map
     *
     * @param key The key of the entry to remove
     *
     * @docParam key "Somewhere"
     */
    @ZenCodeType.Method
    public void remove(String key) {
        
        boolDataKeys.remove(key);
        getInternal().remove(key);
    }
    
    @ZenCodeType.Getter("isEmpty")
    public boolean isEmpty() {
        
        return getInternal().isEmpty();
    }
    
    /**
     * Merges this map and the other map.
     * If entries from this map and the other map share the values are tried to be merged.
     * If that does not work, then the value from the other map is used.
     *
     * @param other The other map.
     *
     * @return This map, after the merge
     *
     * @docParam other {Doodle: "Do"}
     */
    @ZenCodeType.Method
    public MapData merge(MapData other) {
        
        Set<String> newBoolDataKeys = new HashSet<>(boolDataKeys);
        newBoolDataKeys.addAll(other.boolDataKeys);
        return new MapData(getInternal().merge(other.getInternal()), newBoolDataKeys);
    }
    
    @Override
    public IData copy() {
        
        return new MapData(getInternal(), new HashSet<>(boolDataKeys));
    }
    
    @Override
    public IData copyInternal() {
        
        return new MapData(getInternal().copy(), new HashSet<>(boolDataKeys));
    }
    
    @Override
    public CompoundNBT getInternal() {
        
        return internal;
    }
    
    @Override
    public Map<String, IData> asMap() {
        
        Map<String, IData> newMap = new HashMap<>();
        getInternal().keySet().forEach(s -> newMap.put(s, getAt(s)));
        return newMap;
    }
    
    @Override
    public boolean contains(IData data) {
        
        if(data instanceof StringData) {
            return this.getInternal().contains(data.asString());
        }
        
        
        Map<String, IData> dataMap = data.asMap();
        if(dataMap == null) {
            return false;
        }
        
        for(Map.Entry<String, IData> dataEntry : dataMap.entrySet()) {
            if(!this.getInternal().contains(dataEntry.getKey())) {
                return false;
            } else if(!NBTConverter.convert(this.getInternal().get(dataEntry.getKey()))
                    .contains(dataEntry.getValue())) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String asString() {
        
        StringBuilder result = new StringBuilder();
        result.append('{');
        boolean first = true;
        for(String key : getInternal().keySet()) {
            IData value = this.getAt(key);
            if(first) {
                first = false;
            } else {
                result.append(", ");
            }
            
            if(isValidIdentifier(key)) {
                result.append(key);
            } else {
                result.append("\"").append(key).append("\"");
            }
            
            result.append(": ");
            result.append(value.asString());
        }
        result.append('}');
        return result.toString();
    }
    
    @Override
    public ITextComponent asFormattedComponent(String indentation, int indentDepth) {
        
        if(isEmpty()) {
            return new StringTextComponent("{}");
        } else {
            IFormattableTextComponent component = new StringTextComponent("{");
            
            if(!indentation.isEmpty()) {
                component.appendString("\n");
            }
            
            
            Iterator<String> iter = getKeySet().iterator();
            while(iter.hasNext()) {
                String next = iter.next();
                IFormattableTextComponent child = (new StringTextComponent(Strings.repeat(indentation, indentDepth + 1)))
                        .append(new StringTextComponent(next)
                                .mergeStyle(IData.SYNTAX_HIGHLIGHTING_KEY))
                        .appendString(String.valueOf(':'))
                        .appendString(" ")
                        .append(getAt(next).asFormattedComponent(indentation, indentDepth + 1));
                
                if(iter.hasNext()) {
                    child.appendString(String.valueOf(','))
                            .appendString(indentation.isEmpty() ? " " : "\n");
                }
                
                component.append(child);
            }
            
            if(!indentation.isEmpty()) {
                component.appendString("\n").appendString(Strings.repeat(indentation, indentDepth));
            }
            
            component.appendString("}");
            return component;
        }
    }
    
    
    private boolean isValidIdentifier(String str) {
        
        if(!Character.isJavaIdentifierStart(str.charAt(0))) {
            return false;
        }
        
        for(int i = 1; i < str.length(); i++) {
            if(!Character.isJavaIdentifierPart(str.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    @ZenCodeType.Caster(implicit = true)
    public Map<String, IData> castToMap() {
        
        return this.asMap();
    }
    
    @Override
    public String toJsonString() {
        
        final StringJoiner sj = new StringJoiner(",", "{", "}");
        this.asMap()
                .forEach((s, iData) -> sj.add(String.format(Locale.ENGLISH, "\"%s\" : %s", s, iData.toJsonString())));
        return sj.toString();
    }
    
}
