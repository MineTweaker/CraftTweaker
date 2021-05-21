package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.impl.data.*;

public class NumberConverter {
    public static IData convertNumber(Number number) {
    
        if(number == null) {
            return null;
        }
        
        if(number instanceof Integer) {
            return new IntData(number.intValue());
        } else if(number instanceof Byte) {
            return new ByteData(number.byteValue());
        } else if(number instanceof Double) {
            return new DoubleData(number.doubleValue());
        } else if(number instanceof Float) {
            return new FloatData(number.floatValue());
        } else if(number instanceof Long) {
            return new LongData(number.longValue());
        } else if(number instanceof Short) {
            return new ShortData(number.shortValue());
        }

        //Fallback
        //I don't care what it is, double is the most precise so it should work for most cases.
        return new DoubleData(number.doubleValue());
    }
}
