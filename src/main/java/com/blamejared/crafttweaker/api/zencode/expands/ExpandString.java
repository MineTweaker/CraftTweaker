package com.blamejared.crafttweaker.api.zencode.expands;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.impl.data.StringData;
import com.blamejared.crafttweaker.impl.util.text.MCTextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Expansion("string")
public class ExpandString {
    
    @ZenCodeType.Caster(implicit = true)
    public static IData asData(String value) {
        return new StringData(value);
    }
    
    
    @ZenCodeType.Caster()
    public static int asInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException e) {
            throw new NumberFormatException("\"" + value + "\" is not an integer!");
        }
    }
    
    @ZenCodeType.Caster()
    public static double asDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch(NumberFormatException e) {
            throw new NumberFormatException("\"" + value + "\" is not a double!");
        }
    }
    
    @ZenCodeType.Caster(implicit = true)
    public static MCTextComponent asTextComponent(String value) {
        return new MCTextComponent(new StringTextComponent(value));
    }

    @ZenCodeType.Method
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MOD)
    public static String format(String value, Object... args) {
        return String.format(value, args);
    }

    @ZenCodeType.Method
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MOD)
    public static String format(String format, String... args) {
        return String.format(format, (Object[]) args);
    }
}
