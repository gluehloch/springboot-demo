package de.winkler.springboot.user;

import java.util.Optional;
import java.util.stream.Collectors;

import de.winkler.springboot.user.internal.UserEntity;

/**
 * Transforms Entity to JSON representation.
 */
public class UserEntityMapper {

    public static Optional<UserProfile> to(Optional<UserEntity> user) {
        return user.map(UserEntityMapper::to);
    }

    public static UserProfile to(UserEntity user) {
        UserProfileImpl userProfile = new UserProfileImpl(
                user.getNickname().value(),
                user.getName(),
                user.getFirstname(),
                user.getAge(),
                user.roles().stream().map(r -> new RoleImpl(r.getName())).collect(Collectors.toSet()));
        return userProfile;
    }

    public static UserCredentials toUserCredentials(UserEntity user) {
        UserCredentialsImpl userCredentials = new UserCredentialsImpl(
                user.getNickname().value(),
                user.getPassword(),
                user.getName(),
                user.getFirstname(),
                user.roles().stream().map(r -> new RoleImpl(r.getName())).collect(Collectors.toSet()));
        return userCredentials;
    }

}
