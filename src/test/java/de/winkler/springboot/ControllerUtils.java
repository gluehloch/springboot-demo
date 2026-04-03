package de.winkler.springboot;

import java.util.Optional;

import org.springframework.test.web.servlet.assertj.MockMvcTester;

import de.winkler.springboot.user.SecurityConstants;

public class ControllerUtils {

    public static Optional<String> loginAndGetToken(MockMvcTester mockMvcTester, String user, String password) {
    	final var exchangeResult = mockMvcTester
    			.post()
    			.uri("/login")
    			.queryParam("nickname", user)
    			.queryParam("password", password)
    			.exchange();
    	
    	final var mvcResult = exchangeResult.getMvcResult();
    	final String authorizationHeader = mvcResult.getResponse().getHeader(SecurityConstants.HEADER_STRING);
    	
        return authorizationHeader != null
                ? Optional.of(authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "").trim())
                : Optional.empty();
    }

}
