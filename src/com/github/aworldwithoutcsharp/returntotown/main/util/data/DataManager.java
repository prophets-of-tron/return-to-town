package com.github.aworldwithoutcsharp.returntotown.main.util.data;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

;

public class DataManager {
    private static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private static File file = new File("res/getData.yml");
    private static ProjectData data;
    static {
        try {
            data = mapper.readValue(file, ProjectData.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ProjectData getData() {
        return data;
    }
}
