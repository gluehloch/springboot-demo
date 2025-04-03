package de.winkler.springboot.user;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCredentialsImpl implements UserCredentials {

    private final Nickname nickname;
    private final String password;
    private final String name;
    private final String firstname;
    private final Set<Role> roles;

    @JsonCreator
    public UserCredentialsImpl(
            @JsonProperty("nickname") final String nickname,
            @JsonProperty("password") final String password,
            @JsonProperty("name") final String name,
            @JsonProperty("firstname") final String firstname,
            @JsonProperty("roles") final Set<? extends Role> roles) {
        this.nickname = Nickname.of(nickname);
        this.password = password;
        this.name = name;
        this.firstname = firstname;
        this.roles = Set.copyOf(roles);
    }

    @Override
    public Nickname nickname() {
        return nickname;
    }

    @Override
    public String password() {
        return password;
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
    public Set<Role> roles() {
        return Set.of();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserCredentialsImpl user = (UserCredentialsImpl) o;
        return Objects.equals(nickname, user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }

}
