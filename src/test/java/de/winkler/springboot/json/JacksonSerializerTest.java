package de.winkler.springboot.json;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.winkler.springboot.user.Nickname;

class JacksonSerializerTest {
    
    @Test
    void serializer() throws Exception {
        Nickname nickname = Nickname.of("Frosch");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(nickname);
        assertThat(jsonString).isEqualTo("{\"value\":\"Frosch\"}");
        objectMapper.readTree(jsonString);
    }

    @Test
    void mapToJson() throws JsonProcessingException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("key1","value1");
        payload.put("key2","value2");
        payload.put("key3", 1001L);

        String json = new ObjectMapper().writeValueAsString(payload);
        assertThat(json).isEqualTo("{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":1001}");
    }
}
