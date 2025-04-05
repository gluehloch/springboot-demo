package de.winkler.springboot.user;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = UserProfileImpl.class)
public interface UserProfile {

    @JsonProperty()
    Nickname nickname();

    @JsonProperty()
    String name();

    @JsonProperty()
    String firstname();

    @JsonProperty()
    int age();

    @JsonProperty()
    Set<Role> roles();

}
