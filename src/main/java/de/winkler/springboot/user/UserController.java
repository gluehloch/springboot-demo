package de.winkler.springboot.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/user")
    public UserEntity createUser(@RequestBody UserEntity user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already an ID.");
        }

        return userService.createUser(user.getNickname(), user.getName(), user.getFirstname(), user.getPassword());
    }

    /*
    public Token login(String nickname, String password) {

    }
    */

}
