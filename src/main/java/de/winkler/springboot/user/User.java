package de.winkler.springboot.user;

public interface User {

    Nickname nickname();
    String password();
    String name();
    String firstname();
    int age();
    
    // Set<Role> roles();

}
