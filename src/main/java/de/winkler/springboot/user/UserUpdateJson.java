package de.winkler.springboot.user;

public class UserUpdateJson {

    private String nickname;
    private String name;
    private String firstname;

    public UserUpdateJson() {
    }

    public UserUpdateJson(String nickname, String name, String firstname) {
        this.nickname = nickname;
        this.name = name;
        this.firstname = firstname;
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

}
