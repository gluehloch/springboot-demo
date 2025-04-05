package de.winkler.springboot.user;

public class UserUpdateJson {

    private Nickname nickname;
    private String name;
    private String firstname;

    public UserUpdateJson() {
    }

    public UserUpdateJson(Nickname nickname, String name, String firstname) {
        this.nickname = nickname;
        this.name = name;
        this.firstname = firstname;
    }

    public Nickname getNickname() {
        return nickname;
    }

    public void setNickname(Nickname nickname) {
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
