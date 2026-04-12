package de.winkler.springboot.json;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.winkler.springboot.user.Nickname;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class JacksonSerializerTest {

    @Test
    void serializer() throws Exception {
        Nickname nickname = Nickname.of("Frosch");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(nickname);
        assertThat(jsonString).isEqualTo("{\"value\":\"Frosch\"}");
        objectMapper.readTree(jsonString);
        
        Nickname nickname2 = Nickname.of("Lars");
        Nickname nickname3 = Nickname.of("Adam");
        Nickname nickname4 = Nickname.of("Erwin");
        
        List<Nickname> list = List.of(nickname, nickname2, nickname3, nickname4);
        String jsonList = objectMapper.writeValueAsString(list);
        assertThat(jsonList).isEqualTo("[{\"value\":\"Frosch\"},{\"value\":\"Lars\"},{\"value\":\"Adam\"},{\"value\":\"Erwin\"}]");
        
        JsonNode tree = objectMapper.readTree(jsonList);
        assertThat(tree.isArray()).isTrue();
        
        List<Nickname> nicknames = objectMapper.readValue(jsonList, new TypeReference<List<Nickname>>() {});
        assertThat(nicknames).hasSize(4);
    }

    @Test
    void mapToJson() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("key1","value1");
        payload.put("key2","value2");
        payload.put("key3", 1001L);

        String json = new ObjectMapper().writeValueAsString(payload);
        assertThat(json).isEqualTo("{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":1001}");
    }
}
