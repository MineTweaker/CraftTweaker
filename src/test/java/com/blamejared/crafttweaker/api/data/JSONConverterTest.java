package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.impl.data.IntData;
import com.blamejared.crafttweaker.impl.data.ListData;
import com.blamejared.crafttweaker.impl.data.MapData;
import com.blamejared.crafttweaker.impl.data.StringData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;


class JSONConverterTest {
    
    @Test
    @SuppressWarnings("ConstantConditions")
    public void nullElementReturnsNull() {
        //Arrange - none
        //Act
        final IData convert = JSONConverter.convert((JsonElement) null);
        
        assertThat(convert).isNull();
    }
    
    @Test
    @SuppressWarnings("ConstantConditions")
    public void nullObjectReturnsNull() {
        //Arrange - none
        //Act
        final IData convert = JSONConverter.convert((JsonObject) null);
        
        assertThat(convert).isNull();
    }
    
    @Test
    public void jsonNullReturnsNull() {
        //Arrange - none
        final JsonElement jsonNull = JsonNull.INSTANCE;
        
        //Act
        final IData convert = JSONConverter.convert(jsonNull);
        
        assertThat(convert).isNull();
    }
    
    @Test
    public void numberTypeCreatesNumberData() {
        
        //Arrange
        final int value = 10;
        final JsonPrimitive numberPrimitive = new JsonPrimitive(value);
        
        //Act
        final IData convert = JSONConverter.convert(numberPrimitive);
        
        //Assert
        assertThat(convert).isEqualTo(new IntData(value));
    }
    
    @Test
    public void stringCreatesStringData() {
        //Arrange
        final String value = "Hello world";
        final JsonPrimitive stringPrimitive = new JsonPrimitive(value);
        
        //Act
        final IData convert = JSONConverter.convert(stringPrimitive);
        
        //Assert
        assertThat(convert).isEqualTo(new StringData(value));
    }
    
    @Test
    public void booleanCreatesStringData() {
        //Arrange
        final boolean value = false;
        final JsonPrimitive booleanPrimitive = new JsonPrimitive(value);
        
        //Act
        final IData convert = JSONConverter.convert(booleanPrimitive);
        
        //Assert
        assertThat(convert).isEqualTo(new StringData(String.valueOf(value)));
    }
    
    @Test
    public void jsonArrayReturnsListData() {
        //Arrange
        final JsonArray jsonArray = new JsonArray();
        jsonArray.add(1);
        jsonArray.add(2);
        jsonArray.add(3);
        
        //Act
        final IData convert = JSONConverter.convert(jsonArray);
        
        //Assert
        assertThat(convert).isInstanceOfSatisfying(
                ListData.class,
                listData -> assertThat(listData.asList())
                        .contains(new IntData(1), atIndex(0))
                        .contains(new IntData(2), atIndex(1))
                        .contains(new IntData(3), atIndex(2))
        );
        
    }
    
    @Test
    public void jsonObjectReturnsMapData() {
        //Arrange
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add("num", new JsonPrimitive(0));
        jsonObject.addProperty("greeting", "Hello World");
        
        //Act
        final IData convert = JSONConverter.convert(jsonObject);
        
        //Assert
        assertThat(convert).isInstanceOfSatisfying(
                MapData.class,
                mapData -> assertThat(mapData.asMap()).containsOnlyKeys("num", "greeting")
                        .containsEntry("num", new IntData(0))
                        .containsEntry("greeting", new StringData("Hello World"))
        );
    }
    
}