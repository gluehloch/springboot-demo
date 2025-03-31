package de.winkler.springboot.user;

public interface User {

    Nickname nickname();
    String name();
    String firstname();
    int age();
    
    // Set<Role> roles();

}
