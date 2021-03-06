package com.blamejared.crafttweaker.impl_native.event.entity;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.openzen.zencode.java.ZenCodeType;

/**
 * @docEvent canceled it will deny the entity to join the world
 */
@ZenRegister
@Document("vanilla/api/event/entity/MCEntityJoinWorldEvent")
@NativeTypeRegistration(value = EntityJoinWorldEvent.class, zenCodeName = "crafttweaker.api.event.entity.MCEntityJoinWorldEvent")
public class ExpandEntityJoinWorldEvent {
    
    @ZenCodeType.Getter("world")
    public static World getWorld(EntityJoinWorldEvent internal) {
        
        return internal.getWorld();
    }
    
}
