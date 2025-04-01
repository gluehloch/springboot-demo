package de.winkler.springboot.user;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.winkler.springboot.user.internal.RoleEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserImpl implements User {

    private final Nickname nickname;
    private final String password;
    private final String name;
    private final String firstname;
    private final int age;

    @JsonCreator
    public UserImpl(
            @JsonProperty("nickname") final String nickname,
            @JsonProperty("password") final String password,
            @JsonProperty("name") final String name,
            @JsonProperty("firstname") final String firstname,
            @JsonProperty("age") final int age) {
        this.nickname = Nickname.of(nickname);
        this.password = password;
        this.name = name;
        this.firstname = firstname;
        this.age = age;
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
    public int age() {
        return age;
    }

    @Override
    public Set<RoleEntity> roles() {
        return Set.of();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserImpl user = (UserImpl) o;
        return age == user.age && Objects.equals(nickname, user.nickname) && Objects.equals(password,
                user.password) && Objects.equals(name, user.name) && Objects.equals(firstname,
                user.firstname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, password, name, firstname, age);
    }
}
