package de.winkler.springboot.user;

import java.util.Optional;

/**
 * Transforms Entity to JSON representation.
 */
public class UserEntityToJson {

    public static Optional<UserJson> to(Optional<User> user) {
        return user.map(UserEntityToJson::to);
    }

    public static UserJson to(User user) {
        UserJson json = new UserJson();
        json.setFirstname(user.firstname());
        json.setName(user.name());
        json.setNickname(user.nickname());
        json.setRoles(user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList()));
        return json;
    }

}
