package com.blamejared.crafttweaker.api.data;

import com.blamejared.crafttweaker.impl.data.ListData;
import com.blamejared.crafttweaker.impl.data.MapData;
import com.blamejared.crafttweaker.impl.data.StringData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONConverter {
    public static IData convert(JsonElement json) {
        if(json == null) {
            return null;
        }

        if (json.isJsonPrimitive()) {
            final JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isString() || primitive.isBoolean()) {
                return new StringData(primitive.getAsString());
            } else {
                //Must be number
                return NumberConverter.convertNumber(primitive.getAsNumber());
            }

        } else if (json.isJsonArray()) {
            final JsonArray asJsonArray = json.getAsJsonArray();
            final List<IData> dataList = new ArrayList<>(asJsonArray.size());
            for (JsonElement jsonElement : asJsonArray) {
                dataList.add(JSONConverter.convert(jsonElement));
            }
            return new ListData(dataList);
        } else if (json.isJsonObject()) {
            return convert(json.getAsJsonObject());
        } else {
            //Must be jsonNull
            //Otherwise, good as fallthrough I guess?
            return null;
        }
    }
    
    public static MapData convert(JsonObject jsonObject) {
        if(jsonObject == null) {
            return null;
        }
        
        final Map<String, IData> dataMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
            dataMap.put(e.getKey(), JSONConverter.convert(e.getValue()));
        }
        return new MapData(dataMap);
    }
}
