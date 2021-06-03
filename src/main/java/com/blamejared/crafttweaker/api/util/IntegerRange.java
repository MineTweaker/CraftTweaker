package com.blamejared.crafttweaker.api.util;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.util.math.MathHelper;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Objects;
import java.util.Random;

@ZenRegister
@Document("vanilla/api/util/IntegerRange")
@ZenCodeType.Name("crafttweaker.api.util.IntegerRange")
public class IntegerRange {
    private final int min;
    private final int max;

    private IntegerRange(int min, int max) {
        if (min > max) throw new IllegalArgumentException("Minimum IntRange bound must not be more than maximum bound");
        this.min = min;
        this.max = max;
    }

    /**
     * Creates an IntegerRange.
     */
    @ZenCodeType.Method
    public static IntegerRange create(int min, int max) {
        return new IntegerRange(min, max);
    }

    /**
     * Creates an IntegerRange, with equal minimum and maximum.
     */
    @ZenCodeType.Method
    public static IntegerRange create(int n) {
        return new IntegerRange(n, n);
    }

    @ZenCodeType.Getter("min")
    public int getMin() {
        return min;
    }

    @ZenCodeType.Getter("max")
    public int getMax() {
        return max;
    }

    @ZenCodeType.Method
    public int getRandomValue(Random random) {
        return MathHelper.nextInt(random, min, max);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerRange that = (IntegerRange) o;
        return min == that.min && max == that.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    @Override
    public String toString() {
        return "IntegerRange{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}