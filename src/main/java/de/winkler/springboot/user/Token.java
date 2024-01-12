package de.winkler.springboot.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

    private final String content;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Token(@JsonProperty("content") String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return "Token=[content='%s']".formatted(content);
    }

}
