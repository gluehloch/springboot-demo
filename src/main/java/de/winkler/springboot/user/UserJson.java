package de.winkler.springboot.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an UserEntity but without the entity.
 */
public class UserJson {

    private String nickname;
    private String name;
    private String firstname;
    private String password;
    private List<String> roles = new ArrayList<String>();

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
