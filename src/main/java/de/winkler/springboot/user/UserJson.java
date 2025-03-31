package de.winkler.springboot.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an UserEntity but without the entity.
 */
public class UserJson implements User {

    private Nickname nickname;
    private String name;
    private String firstname;
    private int age;
    private List<String> roles = new ArrayList<String>();

    @Override
    public Nickname nickname() {
        return nickname;
    }

    public void setNickname(Nickname nickname) {
        this.nickname = nickname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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
