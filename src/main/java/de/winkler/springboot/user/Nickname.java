package de.winkler.springboot.user;

import java.text.MessageFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Nickname {

    @Column(name = "nickname", length = 30, nullable = false)
    private String value;

    public String getValue() {
        return value;
    }

    public String value() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Nickname=[{0}]", this.value);
    }

    public static Nickname of(String nickname) {
        Nickname nn = new Nickname();
        nn.setValue(nickname);
        return nn;
    }

}
