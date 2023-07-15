package de.winkler.springboot.json;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.winkler.springboot.user.Nickname;

class JacksonSerialiizerTest {
    
    @Test
    void serializer() throws Exception {
        Nickname nickname = Nickname.of("Frosch");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(nickname);
        System.out.println(jsonString);
    }

}
