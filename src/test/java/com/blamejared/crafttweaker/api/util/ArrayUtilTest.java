package com.blamejared.crafttweaker.api.util;

import org.assertj.core.internal.bytebuddy.asm.Advice;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ArrayUtilTest {
    
    @SuppressWarnings("unused")
    public static Stream<Arguments> getArraysWithEvenNumberOfParameters() {
        
        return Stream.<Arguments> builder().add(Arguments.of(new String[] {}, new String[] {}))
                .add(Arguments.of(new String[] {"a", "b"}, new String[] {"b", "a"}))
                .add(Arguments.of(new String[] {"a", "b", "c", "d"}, new String[] {"d", "c", "b", "a"}))
                .add(Arguments.of(new String[] {"a", "b", "c", "d", "e", "f"}, new String[] {"f", "e", "d", "c", "b", "a"}))
                .add(Arguments.of(new String[] {"a", null, "c", null, "e", "f"}, new String[] {"f", "e", null, "c", null, "a"}))
                .build();
    }
    
    @SuppressWarnings("unused")
    public static Stream<Arguments> getArraysWithOddNumberOfParameters() {
        
        return Stream.<Arguments> builder().add(Arguments.of(new String[] {"a"}, new String[] {"a"}))
                .add(Arguments.of(new String[] {"a", "b", "c"}, new String[] {"c", "b", "a"}))
                .add(Arguments.of(new String[] {"a", "b", "c", "d", "e"}, new String[] {"e", "d", "c", "b", "a"}))
                .add(Arguments.of(new String[] {"a", null, "c", null, "e"}, new String[] {"e", null, "c", null, "a"}))
                .build();
    }
    
    private static void doTheTest(String[] before, String[] expectedMirrored) {
        
        final String[] beforeClone = before.clone();
        
        final String[] mirrored = ArrayUtil.mirror(before);
        assertArrayEquals(beforeClone, before, "ArrayUtil may not modify the actual array!");
        assertArrayEquals(expectedMirrored, mirrored, "Mirrored must match expectedMirrored!");
        assertArrayEquals(before, ArrayUtil.mirror(mirrored), "Mirroring twice must restore order!");
    }
    
    @ParameterizedTest
    @MethodSource("getArraysWithEvenNumberOfParameters")
    public void testMirrorWorksForEvenNumberOfItemsInArray(String[] before, String[] expectedMirrored) {
        
        doTheTest(before, expectedMirrored);
    }
    
    @ParameterizedTest
    @MethodSource("getArraysWithOddNumberOfParameters")
    public void testMirrorWorksForOddNumberOfItemsInArray(String[] before, String[] expectedMirrored) {
        
        doTheTest(before, expectedMirrored);
    }
    
}