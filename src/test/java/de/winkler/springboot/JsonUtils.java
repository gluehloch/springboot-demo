package de.winkler.springboot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

    public static String toString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        try {
            return ow.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (T) mapper.readValue(json, clazz);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
