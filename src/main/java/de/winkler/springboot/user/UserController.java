package de.winkler.springboot.user;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    //
    // TODO Just an example for a generic result object on REST controller level.
    //
    @GetMapping("/user/getSomething")
    public ResponseEntity<UserProfile> doSomething() {
        // Optional::of und Optional::ofNullable nehmen ein Object entgegen.
        // ResponseEntity::of and ResponseEntity::ofNullable Hier nimmt ::of ein java.util.Optional entgegen. ::ofNullable ein Object.

        ResponseEntity<String> stringResponseEntity = ResponseEntity.ofNullable("Das ist ein Body.");
        ResponseEntity<Object> objectResponseEntity = ResponseEntity.of(Optional.empty());
        // ResponseEntity.of("Das ist ein Body!"); compile error
        ResponseEntity<String> ok = ResponseEntity.ok("Das ist ein Body.");
        ResponseEntity<String> badRequest = ResponseEntity.badRequest().body("Bad Request");

        ResponseEntity.ok(new RestServiceResult<>("Das ist ein Body."));
        ResponseEntity.badRequest().body(new RestServiceResult<>("Das ist ein Body"));

        ResponseEntity<Object> responseEntity = ResponseEntity.ok().build();
        Object body = responseEntity.getBody();
        boolean hasBody = responseEntity.hasBody();

        return ResponseEntity.of(Optional.empty());
    }

    public static class RestServiceResult<T> {
        private final T result;
        private final ValidationMessage message;
        RestServiceResult(T result) {
            this(result, null);
        }
        RestServiceResult(T result, ValidationMessage validationMessage) {
            this.result = result;
            this.message = validationMessage;
        }

        public T getResult() {
            return result;
        }

        public ValidationMessage getMessage() {
            return message;
        }
    }

    public enum Severity {
        OK, ERROR;
    }

    public static class ValidationMessage {
        public Severity severity;
        public String message;
    }

}
