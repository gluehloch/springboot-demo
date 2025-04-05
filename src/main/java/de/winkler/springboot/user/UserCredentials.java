package de.winkler.springboot.user;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface UserCredentials {

    @JsonProperty()
    Nickname nickname();

    @JsonProperty()
    String password();

    @JsonProperty()
    String name();

    @JsonProperty()
    String firstname();

    @JsonProperty()
    Set<Role> roles();

}
