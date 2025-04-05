package de.winkler.springboot.user;

import java.text.MessageFormat;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Nickname other = (Nickname) obj;
        return Objects.equals(value, other.value);
    }

}
