package de.winkler.springboot.user;

public class UserCreateJson {

    private String nickname;
    private String name;
    private String firstname;
    private String password;

    public UserCreateJson() {
    }

    public UserCreateJson(String nickname, String name, String firstname, String password) {
        this.nickname = nickname;
        this.name = name;
        this.firstname = firstname;
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
