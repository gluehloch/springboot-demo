package de.winkler.springboot.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserProfile> findUser(@PathVariable String nickname) {
        return ResponseEntity.of(userService.findUserProfile(Nickname.of(nickname)));
    }

    @GetMapping("/user")
    @RolesAllowed("ROLE_ADMIN")
    public List<UserProfile> findAll() {
        return userService.findAll();
    }

    @PostMapping("/user")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<UserProfile> create(@RequestBody UserCreateJson user) {
        return ResponseEntity.ofNullable(userService.create(
                user.getNickname().getValue(),
                user.getName(),
                user.getFirstname(),
                user.getPassword()));
    }

    @PutMapping("/user")
    @PreAuthorize("#user.nickname.value == authentication.name")
    public ResponseEntity<UserProfile> update(@RequestBody UserUpdateJson user) {
        return ResponseEntity.ofNullable(userService.update(user));
    }

    @PutMapping("/user/role")
    @RolesAllowed("ROLE_ADMIN")
    public UserProfile addRole(@RequestParam String nickname, @RequestParam("role") String roleName) {
        return UserEntityMapper.to(userService.addRole(Nickname.of(nickname), roleName));
    }

}
