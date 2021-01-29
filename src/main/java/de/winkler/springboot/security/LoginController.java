package de.winkler.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.winkler.springboot.user.Token;

@RestController
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @CrossOrigin
    @PostMapping("/login")
    public Token login(@RequestParam String nickname, @RequestParam String password) {
        if (loginService.login(nickname, password)) {
            UserDetails userDetails = loginService.loadUserByUsername(nickname);
            return loginService.token(userDetails);
        } else {
            return null;
        }
    }

    @GetMapping("/validate")
    public boolean validate(@RequestBody Token token) {
        return loginService.validate(token.getContent()).isPresent();
    }

}
