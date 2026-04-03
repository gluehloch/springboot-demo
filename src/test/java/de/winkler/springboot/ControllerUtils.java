package de.winkler.springboot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import de.winkler.springboot.user.SecurityConstants;

public class ControllerUtils {

    public static String loginAndGetToken(MockMvcTester mockMvcTester, String user, String password) {
    	final var exchangeResult = mockMvcTester
    			.post()
    			.uri("/login")
    			.queryParam("nickname", user)
    			.queryParam("password", password)
    			.exchange();
    	
    	final var mvcResult = exchangeResult.getMvcResult();
    	final String authorizationHeader = mvcResult.getResponse().getHeader(SecurityConstants.HEADER_STRING);
    	
        return authorizationHeader != null
                ? authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "").trim()
                : null;
    }


    @Deprecated
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

    @Deprecated
    public static String extractJwt(MvcResult result) {
        String authorizationHeader = result.getResponse().getHeader(SecurityConstants.HEADER_STRING);
        return authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, " ");
    }

    @Deprecated
    public static String loginAndGetToken(MockMvc mockMvc, String user, String password) {
        return ControllerUtils.extractJwt(login(mockMvc, user, password));
    }

}
