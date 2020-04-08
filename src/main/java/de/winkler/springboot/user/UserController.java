package de.winkler.springboot.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public UserJson findUser(@PathVariable Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return UserEntityToJson.from(userService.find(id));
    }

    @GetMapping("/user")
    public List<UserJson> findAll() {
        return  userService.findAll().stream().map(UserEntityToJson::from).collect(Collectors.toList());
    }

    @PostMapping("/user")
    public UserJson create(@RequestBody UserEntity user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already an ID.");
        }

        return UserEntityToJson.from(userService.create(
                user.getNickname(), user.getName(), user.getFirstname(), user.getPassword()));
    }

    @PutMapping("/user")
    public UserJson update(@RequestBody UserEntity user) {
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

        return UserEntityToJson.from(persistedUser);
    }

}
