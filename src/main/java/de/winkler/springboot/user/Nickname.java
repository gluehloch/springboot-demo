package de.winkler.springboot.user;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.text.MessageFormat;

@Embeddable
public class Nickname {

    @Column(name = "nickname", length = 30, nullable = false)
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Nickname=[{0}]", this.nickname);
    }

    public static Nickname of(String nickname) {
        Nickname nickname = new Nickname();
        nickname.setNickname(nick);
    }

}
