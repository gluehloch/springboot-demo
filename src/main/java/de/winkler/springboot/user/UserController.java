package de.winkler.springboot.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public UserEntity findUser(@PathVariable Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return userService.findUser(id);
    }

    public Token login(String nickname, String password) {

    }

}
