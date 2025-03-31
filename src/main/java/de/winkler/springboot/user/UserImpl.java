package de.winkler.springboot.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserImpl implements User {

    private final Nickname nickname;
    private final String name;
    private final String firstname;
    private final int age;

    @JsonCreator
    public UserImpl(
            @JsonProperty("nickname") final String nickname,
            @JsonProperty("name") final String name,
            @JsonProperty("firstname") final String firstname,
            @JsonProperty("age") final int age) {
        this.nickname = Nickname.of(nickname);
        this.name = name;
        this.firstname = firstname;
        this.age = age;
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

}
