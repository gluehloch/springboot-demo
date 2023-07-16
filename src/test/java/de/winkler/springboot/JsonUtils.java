package de.winkler.springboot;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    }

    public static String toString(Object object) {
        ObjectWriter ow = OBJECT_MAPPER.writer().withDefaultPrettyPrinter();

        try {
            return ow.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return (T) OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static <T> List<T> toList(String json) {
        try {
            return (List<T>) OBJECT_MAPPER.readValue(json, List.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(ex);
        }        
    }

}
