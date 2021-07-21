package com.blamejared.crafttweaker.api.zencode.expands;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.impl.data.MapData;
import org.openzen.zencode.java.ZenCodeType;

import java.util.HashMap;
import java.util.Map;

@ZenRegister
@ZenCodeType.Expansion("crafttweaker.api.data.IData[string]")
public class ExpandMap {
    
    @ZenCodeType.Caster(implicit = true)
    public static MapData asData(Map<String, IData> map) {
        return new MapData(map);
    }
    
    @ZenRegister
    @ZenCodeType.Expansion("string[string]")
    public static class ExpandMapString {
        
        @ZenCodeType.Caster(implicit = true)
        public static MapData asData(Map<String, String> map) {
            final Map<String, IData> stringIDataMap = new HashMap<>();
            map.forEach((s, s2) -> stringIDataMap.put(s, ExpandString.asData(s2)));
            return ExpandMap.asData(stringIDataMap);
        }
        
    }
}


