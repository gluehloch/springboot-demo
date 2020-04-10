package de.winkler.springboot.user;

import java.util.stream.Collectors;

/**
 * Transforms Entity to JSON representation.
 */
public class UserEntityToJson {

    public static UserJson from(UserEntity user) {
        UserJson json = new UserJson();
        json.setFirstname(user.getFirstname());
        json.setName(user.getName());
        json.setNickname(user.getNickname());

        json.setRoles(user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList()));

        return json;
    }

}
