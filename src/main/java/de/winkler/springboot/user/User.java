package de.winkler.springboot.user;

import java.util.Set;

import de.winkler.springboot.user.internal.RoleEntity;

public interface User {

    Nickname nickname();
    String password();
    String name();
    String firstname();
    int age();
    
    Set<Role> roles();

}
