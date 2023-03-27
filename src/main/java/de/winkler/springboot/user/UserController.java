package de.winkler.springboot.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{nickname}")
    @PreAuthorize("#nickname == authentication.name")
    public UserJson findUser(@PathVariable String nickname) {
        return UserEntityToJson.from(userService.findByNickname(Nickname.of(nickname)));
    }

    @GetMapping("/user")
    @RolesAllowed("ROLE_ADMIN")
    public List<UserJson> findAll() {
        return userService.findAll().stream().map(UserEntityToJson::from).collect(Collectors.toList());
    }

    @PostMapping("/user")
    @RolesAllowed("ROLE_ADMIN")
    public UserJson create(@RequestBody UserJson user) {
        return UserEntityToJson.from(userService.create(
                user.getNickname(), user.getName(), user.getFirstname(), user.getPassword()));
    }

    @PutMapping("/user")
    @PreAuthorize("#user.nickname == authentication.name")
    public UserJson update(@RequestBody UserJson user) {
        return UserEntityToJson.from(userService.update(
                UserEntity.UserBuilder.of(Nickname.of(user.getNickname()), user.getPassword())
                        .firstname(user.getFirstname())
                        .name(user.getName())
                        .build()));
    }

    @PutMapping("/user/role")
    @RolesAllowed("ROLE_ADMIN")
    public UserJson addRole(@RequestParam("nickname") String nickname, @RequestParam("role") String roleName) {
        return UserEntityToJson.from(userService.addRole(Nickname.of(nickname), roleName));
    }

}
