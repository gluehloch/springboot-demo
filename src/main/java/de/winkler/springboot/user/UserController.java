package de.winkler.springboot.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;

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
    public UserJson create(@RequestBody UserCreateJson user) {
        return UserEntityToJson.from(userService.create(
                user.getNickname(), user.getName(), user.getFirstname(), user.getPassword()));
    }

    @PutMapping("/user")
    @PreAuthorize("#user.nickname == authentication.name")
    public UserJson update(@RequestBody UserUpdateJson user) {
        return UserEntityToJson.from(userService.update(user));
    }

    @PutMapping("/user/role")
    @RolesAllowed("ROLE_ADMIN")
    public UserJson addRole(@RequestParam String nickname, @RequestParam("role") String roleName) {
        return UserEntityToJson.from(userService.addRole(Nickname.of(nickname), roleName));
    }

}
