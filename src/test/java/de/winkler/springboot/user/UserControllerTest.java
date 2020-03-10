package de.winkler.springboot.user;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import de.winkler.springboot.ObjectToJsonString;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void shouldReturnSomeUsers() throws Exception {
        UserEntity frosch = UserEntity.UserBuilder
                .of("Frosch", "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .build();

        UserEntity testA = UserEntity.UserBuilder.of("TestA", "PasswordTestA")
                .firstname("VornameA")
                .name("NachnameA")
                .build();

        UserEntity testB = UserEntity.UserBuilder.of("TestB", "PasswordTestB")
                .firstname("VornameB")
                .name("NachnameB")
                .build();

        userRepository.saveAll(List.of(frosch, testA, testB));

        //
        // Get all users
        //

        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Winkler")))
                .andExpect(jsonPath("$[1].name", is("NachnameA")))
                .andExpect(jsonPath("$[2].name", is("NachnameB")))
                .andExpect(content().string(containsString("Frosch")));

        UserEntity testC = UserEntity.UserBuilder.of("TestC", "PasswordTestC")
                .firstname("VornameC")
                .name("NachnameC")
                .build();

        //
        // Create user
        //

        this.mockMvc.perform(
                post("/user").contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectToJsonString.toString(testC)))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Winkler")))
                .andExpect(jsonPath("$[1].name", is("NachnameA")))
                .andExpect(jsonPath("$[2].name", is("NachnameB")))
                .andExpect(jsonPath("$[3].name", is("NachnameC")));

        UserEntity persistedUserC = userRepository.findByNickname("TestC");
        testC.setId(persistedUserC.getId());

        //
        // Update user
        //

        testC.setName("NachnameC_Neu");
        this.mockMvc.perform(
                put("/user").contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectToJsonString.toString(testC)))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Winkler")))
                .andExpect(jsonPath("$[1].name", is("NachnameA")))
                .andExpect(jsonPath("$[2].name", is("NachnameB")))
                .andExpect(jsonPath("$[3].name", is("NachnameC_Neu")));
    }

}