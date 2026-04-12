package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerWithRestTestClientBindToMockMvc {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrepareDatabase prepareDatabase;

    @BeforeEach
    void before() {
        prepareDatabase.prepareDatabase();
    }

    @Test
    @Tag("controller")
    @DisplayName("Only users with the role ADMIN should be able to see all users.")
    void onlyAdminCanSeeAllUsersWithRestClientBindToMockMvc() {
        final var client2 = RestTestClient.bindTo(mockMvc).build();
        assertThat(client2).isNotNull();

        client2.get().uri("/user").exchange().expectStatus().isForbidden();
    }

}
