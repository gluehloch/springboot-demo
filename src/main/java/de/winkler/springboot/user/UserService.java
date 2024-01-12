package de.winkler.springboot.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public UserEntity create(String nickname, String name, String firstname, String password) {
        UserEntity user = new UserEntity();
        user.setNickname(Nickname.of(nickname));
        user.setName(name);
        user.setFirstname(firstname);
        user.setPassword(password);
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity update(UserEntity user) {
        if (user.getNickname() == null) {
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

    @Transactional(readOnly = true)
    public Page<UserJson> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserEntityToJson::from);
    }

    @Transactional
    public UserEntity findByName(String name) {
        return userRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException());
    }

    @Transactional
    public UserEntity findByNickname(Nickname nickname) {
        return userRepository.findByNickname(nickname).orElseThrow(() -> new EntityNotFoundException());
    }

    @Transactional
    public UserEntity addRole(Nickname nickname, String roleName) {
        RoleEntity roleEntity = roleRepository.findByName(roleName).orElseThrow(
                () -> new EntityNotFoundException("There is no role with name=[%s]".formatted(roleName)));

        UserEntity persistedUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new EntityNotFoundException("There is no user with nickname=[%s].".formatted(nickname)));

        persistedUser.addRole(roleEntity);

        return persistedUser;
    }

}