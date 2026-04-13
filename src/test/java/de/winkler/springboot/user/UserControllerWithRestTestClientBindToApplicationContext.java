package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient.BodySpec;
import org.springframework.test.web.servlet.client.RestTestClient.RequestHeadersSpec;
import org.springframework.test.web.servlet.client.RestTestClient.ResponseSpec;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import de.winkler.springboot.JsonUtils2;
import de.winkler.springboot.JsonUtils3;
import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.RoleRepository;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;
import tools.jackson.core.type.TypeReference;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerWithRestTestClientBindToApplicationContext {

    @Autowired
    private PrepareDatabase prepareDatabase;
    
    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeEach
    void before() {
        prepareDatabase.prepareDatabase();
    }

    @Test
    @Tag("controller")
    @DisplayName("Only users with the role ADMIN should be able to see all users.")
    void onlyAdminCanSeeAllUsersWithRestClientBindToApplicationContext() {
        final var client = RestTestClient.bindToApplicationContext(applicationContext).build();
        // Spring Security ist mit 'RestTestClient' nicht aktiviert. Warum nicht?
        // => https://github.com/spring-projects/spring-framework/issues/35646
        // Siehe #onlyAdminCanSeeAllUsersWithRestClientBindToMockMvc() für eine Alternative.

        ExchangeResult returnResult = client.get().uri("/user")
                .exchange()
                .expectStatus()
                .isOk().returnResult();

        byte[] responseBodyContent = returnResult.getResponseBodyContent();
        String responseBody = new String(responseBodyContent);

        System.out.println("Response Body:" + responseBody);

        JsonUtils2.toList(responseBody).forEach(System.out::println);
        List<UserProfile> list = JsonUtils3.toTypedList(responseBody, new TypeReference<List<UserProfile>>() {
        });
        assertThat(list).hasSize(4);

        //        ObjectMapper objectMapper = new ObjectMapper();
        //        List<UserProfile> convertValue = objectMapper.readValue(responseBodyContent, new TypeReference<List<UserProfile>>() {});

        // expcted:
        // client.get().uri("/user").exchange().expectStatus().isUnauthorized();
        ParameterizedTypeReference<List<UserProfileImpl>> typeRef = new ParameterizedTypeReference<List<UserProfileImpl>>() {
        };

        BodySpec<List<UserProfileImpl>, ?> expectBody = client.get().uri("/user")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<List<UserProfileImpl>>() {
                });
    }

    
    @Test
    @Tag("controller")
    @DisplayName("Only users with the role ADMIN should be able to see all users.")
    void onlyUserCanSeeHisProfile() {
        final var client = RestTestClient.bindToApplicationContext(applicationContext).build();

        getUser(client, "Frosch").expectStatus().isOk()
                .expectBody(UserProfileImpl.class)
                .value(userProfile -> {
                    assertThat(userProfile.nickname().value()).isEqualTo("Frosch");
                    assertThat(userProfile.name()).isEqualTo("Winkler");
                    assertThat(userProfile.firstname()).isEqualTo("Andre");
                });
    }

    private ResponseSpec getUsers(RestTestClient client) {
        return client.get().uri("/user").exchange();
    }

    private ResponseSpec getUser(RestTestClient client, String nickname) {
        return client.get().uri("/user/Frosch").exchange();
    }

}
