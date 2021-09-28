package de.winkler.springboot;

import de.winkler.springboot.user.SecurityConstants;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ControllerUtils {

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
