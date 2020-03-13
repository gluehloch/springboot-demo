package de.winkler.springboot.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final UserService userService;
    private final LoginService loginService;

    @Autowired
    public LoginController(UserService userService, LoginService loginService) {
        this.userService = userService;
        this.loginService = loginService;
    }

    @CrossOrigin
    @PostMapping("/login")
    public Token login(@RequestParam String nickname, @RequestParam String password) {
        if (loginService.login(nickname, password)) {
            UserEntity user = userService.findByNickname(nickname);
            return loginService.token(user);
        } else {
            return null;
        }
    }

    @CrossOrigin
    @PostMapping("/logout")
    public Token logout(@RequestBody Token token) {
        return loginService.logout(token);
    }

    @GetMapping("/validate")
    public boolean validate(@RequestBody Token token) {
        return loginService.validate(token.getContent()).isPresent();
    }

}
