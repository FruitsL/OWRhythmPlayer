package com.fruitcoding.owrhythmplayer.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class JSONMap<K, V> {
    @Getter @Setter
    Map<K, V> map = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();

    abstract String getFilePath();

    public void mapToJSON() throws IOException {
        mapper.writeValue(new File(getFilePath()), map);
    }

    public void jsonToMap(Class<?> clazz1, Class<?> clazz2) throws IOException {
        map = mapper.readValue(new File(getFilePath()), mapper.getTypeFactory().constructMapType(Map.class, clazz1, clazz2));
    }

    public void creatJSONFile() throws IOException {
        File file = new File(getFilePath());
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
    }
}
