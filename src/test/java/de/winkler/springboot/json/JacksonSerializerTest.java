package de.winkler.springboot.json;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.winkler.springboot.user.Nickname;

class JacksonSerialiizerTest {
    
    @Test
    void serializer() throws Exception {
        Nickname nickname = Nickname.of("Frosch");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(nickname);
        assertThat(jsonString).isEqualTo("{\"value\":\"Frosch\"}");
        objectMapper.readTree(jsonString);
    }

}
