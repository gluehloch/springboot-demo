package de.winkler.springboot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import de.winkler.springboot.user.SecurityConstants;

public class ControllerUtils {

    private static final String HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME = "mvcHandlerMappingIntrospector";

    public static HandlerMappingIntrospector getIntrospector(HttpSecurity http) { 
        ApplicationContext context = http.getSharedObject(ApplicationContext.class); 
        return context.getBean(HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME, HandlerMappingIntrospector.class); 
    } 

    public String login(RestTestClient restTestClient, String user, String password) {
        final var exchangeResult = restTestClient.post().uri(uriBuilder -> {
           uriBuilder.path("/login").queryParam("nickname", user).queryParam("password", password);
           return uriBuilder.build();
        }).exchange();

        // Get the Authorization header from the response
        String authorizationHeader = exchangeResult
                .getResponseHeaders()
                .getFirst(SecurityConstants.HEADER_STRING);

        return authorizationHeader != null
                ? authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "").trim()
                : null;
    }


    public static MvcResult login(MockMvc mockMvc, String user, String password) {
        ResultActions loginAction = null;
        try {
            loginAction = mockMvc.perform(
                    post("/login")
                            .param("nickname", user)
                            .param("password", password))
                    .andDo(print())
                    .andExpect(status().isOk());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return loginAction.andReturn();
    }

    public static String extractJwt(MvcResult result) {
        String authorizationHeader = result.getResponse().getHeader(SecurityConstants.HEADER_STRING);
        return authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, " ");
    }

    public static String loginAndGetToken(MockMvc mockMvc, String user, String password) {
        return ControllerUtils.extractJwt(login(mockMvc, user, password));
    }

}
