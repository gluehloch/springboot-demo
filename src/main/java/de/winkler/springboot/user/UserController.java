package de.winkler.springboot.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;
    private final LoginService loginService;

    @Autowired
    public UserController(UserService userService, LoginService loginService) {
        this.userService = userService;
        this.loginService = loginService;
    }

    @GetMapping("/user/{id}")
    public UserEntity findUser(@PathVariable Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return userService.find(id);
    }

    @GetMapping("/user")
    public List<UserEntity> findAll() {
        return userService.findAll();
    }

    @PostMapping("/user")
    public UserEntity create(@RequestBody UserEntity user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already an ID.");
        }

        return userService.create(user.getNickname(), user.getName(), user.getFirstname(), user.getPassword());
    }

    @PutMapping("/user")
    public UserEntity update(@RequestBody UserEntity user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Missing ID");
        }

        UserEntity persistedUser = userService.find(user.getId());
        if (persistedUser == null) {
            throw new EntityNotFoundException("User with ID was not found: [" + user.getId() + "].");
        }

        persistedUser.setFirstname(user.getFirstname());
        persistedUser.setName(user.getName());
        persistedUser.setNickname(user.getNickname());
        persistedUser.setPassword(user.getPassword());

        return persistedUser;
    }

    @PostMapping("/login")
    public Token login(@RequestParam String nickname, @RequestParam String password) {
        return loginService.login(nickname, password);
    }

    @PostMapping("/logout")
    public Token logout(@RequestBody Token token) {
        return loginService.logout(token);
    }

}
