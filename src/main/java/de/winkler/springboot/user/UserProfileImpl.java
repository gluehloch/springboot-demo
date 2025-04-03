package de.winkler.springboot.user;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileImpl implements UserProfile {

    private final Nickname nickname;
    private final String name;
    private final String firstname;
    private final int age;
    private final Set<Role> roles;

    @JsonCreator
    public UserProfileImpl(
            @JsonProperty("nickname") final String nickname,
            @JsonProperty("name") final String name,
            @JsonProperty("firstname") final String firstname,
            @JsonProperty("age") final int age,
            @JsonProperty("roles") final Set<? extends Role> roles) {
        this.nickname = Nickname.of(nickname);
        this.name = name;
        this.firstname = firstname;
        this.age = age;
        this.roles = Set.copyOf(roles);
    }

    @Override
    public Nickname nickname() {
        return nickname;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String firstname() {
        return firstname;
    }

    @Override
    public int age() {
        return age;
    }

    @Override
    public Set<Role> roles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserProfileImpl user = (UserProfileImpl) o;
        return Objects.equals(nickname, user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }
}
