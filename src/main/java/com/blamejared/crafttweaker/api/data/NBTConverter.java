package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.impl.data.ByteArrayData;
import com.blamejared.crafttweaker.impl.data.ByteData;
import com.blamejared.crafttweaker.impl.data.DoubleData;
import com.blamejared.crafttweaker.impl.data.FloatData;
import com.blamejared.crafttweaker.impl.data.IntArrayData;
import com.blamejared.crafttweaker.impl.data.IntData;
import com.blamejared.crafttweaker.impl.data.ListData;
import com.blamejared.crafttweaker.impl.data.LongArrayData;
import com.blamejared.crafttweaker.impl.data.LongData;
import com.blamejared.crafttweaker.impl.data.MapData;
import com.blamejared.crafttweaker.impl.data.ShortData;
import com.blamejared.crafttweaker.impl.data.StringData;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;

public class NBTConverter {
    
    public static IData convert(INBT nbt) {
        if(nbt == null) {
            return null;
        }
        switch(nbt.getId()) {
            case 1:
                return new ByteData((ByteNBT) nbt);
            case 2:
                return new ShortData((ShortNBT) nbt);
            case 3:
                return new IntData((IntNBT) nbt);
            case 4:
                return new LongData((LongNBT) nbt);
            case 5:
                return new FloatData((FloatNBT) nbt);
            case 6:
                return new DoubleData((DoubleNBT) nbt);
            case 7:
                return new ByteArrayData((ByteArrayNBT) nbt);
            case 8:
                return new StringData((StringNBT) nbt);
            case 9:
                return new ListData((ListNBT) nbt);
            case 10:
                return new MapData((CompoundNBT) nbt);
            case 11:
                return new IntArrayData((IntArrayNBT) nbt);
            case 12:
                return new LongArrayData((LongArrayNBT) nbt);
            default:
                return null;
        }
    }

    public static MapData convert(CompoundNBT nbt) {
        return nbt == null ? null : new MapData(nbt);
    }
}
