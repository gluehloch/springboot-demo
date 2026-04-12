package de.winkler.springboot;

import java.util.List;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class JsonUtils3 {

    private static final JsonMapper JSON_MAPPER;

    static {
        JSON_MAPPER = JsonMapper.builder().disable(SerializationFeature.WRAP_ROOT_VALUE).build();
        // OBJECT_MAPPER.disable(SerializationFeature.WRAP_ROOT_VALUE);
    }

    public static String toString(Object object) {
        ObjectWriter ow = JSON_MAPPER.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return (T) JSON_MAPPER.readValue(json, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(String json) {
        return (List<T>) JSON_MAPPER.readValue(json, List.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> cast(List<? super T> collection, Class<T> clazz) {
        return (List<T>) collection;
    }
    
    public static <T> List<T> toTypedList(String json, TypeReference<List<T>> typeReference) {
        return JSON_MAPPER.readValue(json, typeReference);
    }
    

}
