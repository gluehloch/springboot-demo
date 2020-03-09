package de.winkler.springboot.user;

public class Token {

    private final String content;

    public Token(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return String.format("Token=[content='%s']", content);
    }

}
