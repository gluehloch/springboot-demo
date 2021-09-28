package de.winkler.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /*
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

    @CrossOrigin
    @GetMapping("/validate")
    public boolean validate(@RequestBody Token token) {
        return loginService.validate(token.getContent()).isPresent();
    }
    */

}
