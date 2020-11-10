package de.winkler.springboot.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
            PrivilegeRepository privilegeRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }

    @Transactional
    public UserEntity create(String nickname, String name, String firstname, String password) {
        UserEntity user = new UserEntity();
        user.setNickname(nickname);
        user.setName(name);
        user.setFirstname(firstname);
        user.setPassword(password);
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity update(UserEntity user) {
        if (StringUtils.isBlank(user.getNickname())) {
            throw new IllegalArgumentException("user nickname is missing");
        }

        UserEntity persistedUser = userRepository.findByNickname(user.getNickname()).orElseThrow(
                () -> new EntityNotFoundException("User with nickname=[" + user.getId() + "] was not found."));

        persistedUser.setFirstname(user.getFirstname());
        persistedUser.setName(user.getName());
        persistedUser.setNickname(user.getNickname());

        return persistedUser;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> findAll() {
        Iterable<UserEntity> all = userRepository.findAll();

        List<UserEntity> users = new ArrayList<>();
        for (UserEntity user : all) {
            users.add(user);
        }

        return users;
    }

    @Transactional
    public UserEntity findByName(String name) {
        return userRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException());
    }

    @Transactional
    public UserEntity findByNickname(String nickname) {
        return userRepository.findByNickname(nickname).orElseThrow(() -> new EntityNotFoundException());
    }

    @Transactional
    public UserEntity addRole(String nickname, String roleName) {
        RoleEntity roleEntity = roleRepository.findByName(roleName).orElseThrow(
                () -> new EntityNotFoundException(String.format("There is no role with name=[%s]", roleName)));

        UserEntity persistedUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new EntityNotFoundException(String.format("There is no user with nickname=[%s].", nickname)));

        persistedUser.addRole(roleEntity);

        return persistedUser;
    }

}