package de.winkler.springboot.user;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = UserProfileImpl.class)
public interface UserProfile {

    Nickname nickname();
    String name();
    String firstname();
    int age();
    Set<Role> roles();

}
