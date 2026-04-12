package de.winkler.springboot.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.RoleRepository;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;

@Component
public class PrepareDatabase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void prepareDatabase() {
        UserEntity frosch = UserEntity.UserBuilder
                .of(Nickname.of("Frosch"), "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .build();

        UserEntity testA = UserEntity.UserBuilder.of(Nickname.of("TestA"), "PasswordTestA")
                .firstname("VornameA")
                .name("NachnameA")
                .build();

        UserEntity testB = UserEntity.UserBuilder.of(Nickname.of("TestB"), "PasswordTestB")
                .firstname("VornameB")
                .name("NachnameB")
                .build();

        UserEntity admin = UserEntity.UserBuilder.of(Nickname.of("ADMIN"), "secret-password")
                .firstname("admin")
                .name("admin")
                .build();

        userRepository.saveAll(List.of(frosch, testA, testB, admin));

        RoleEntity adminRole = RoleEntity.RoleBuilder.of("ROLE_ADMIN");
        RoleEntity userRole = RoleEntity.RoleBuilder.of("ROLE_USER");
        roleRepository.saveAll(List.of(adminRole, userRole));

        admin.addRole(adminRole);
        frosch.addRole(userRole);
        userRepository.saveAll(List.of(admin, frosch));
    }

}
