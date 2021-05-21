package com.blamejared.crafttweaker.test_api.helper;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

@SuppressWarnings("sunapi")
public class UnsafeHelper {
    
    private static final Unsafe unsafe;
    
    static {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static <T> T instantiate(Class<T> theClass) {
        
        try {
            //noinspection unchecked
            return (T) unsafe.allocateInstance(theClass);
        } catch(InstantiationException e) {
            throw new RuntimeException("Could not allocateInstance", e);
        }
    }
    
}
