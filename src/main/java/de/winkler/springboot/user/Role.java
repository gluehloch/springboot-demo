package de.winkler.springboot.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Role {

    @JsonProperty()
    String name();

}
