package de.winkler.springboot.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{nickname}")
    public UserJson findUser(@PathVariable String nickname) {
        return UserEntityToJson.from(userService.findByNickname(nickname));
    }

    @GetMapping("/user")
    public List<UserJson> findAll() {
        return userService.findAll().stream().map(UserEntityToJson::from).collect(Collectors.toList());
    }

    @PostMapping("/user")
    public UserJson create(@RequestBody UserJson user) {
        return UserEntityToJson.from(userService.create(
                user.getNickname(), user.getName(), user.getFirstname(), user.getPassword()));
    }

    @PutMapping("/user")
    public UserJson update(@RequestBody UserJson user) {
        return UserEntityToJson.from(userService.update(
                UserEntity.UserBuilder.of(user.getNickname(), user.getPassword())
                        .firstname(user.getFirstname())
                        .name(user.getName())
                        .build()));
    }

    @PutMapping("/user/role")
    public UserJson addRole(@RequestParam("nickname") String nickname, @RequestParam("role") String roleName) {
        UserEntity userEntity = userService.findByNickname(nickname);
        // userService.addRole();

        return UserEntityToJson.from(userEntity);
    }

}
