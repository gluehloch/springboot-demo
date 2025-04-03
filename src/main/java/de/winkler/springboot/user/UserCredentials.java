package de.winkler.springboot.user;

import java.util.Set;

public interface UserCredentials {

    Nickname nickname();
    String password();
    String name();
    String firstname();

    Set<Role> roles();

}
