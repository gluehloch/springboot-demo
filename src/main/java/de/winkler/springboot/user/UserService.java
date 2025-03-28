package de.winkler.springboot.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.winkler.springboot.user.internal.RoleEntity;
import de.winkler.springboot.user.internal.RoleRepository;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;

@Service
@Transactional(readOnly = true)
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
    public UserEntity update(UserUpdateJson user) {
        Objects.requireNonNull(user, "user is null");
        Objects.requireNonNull(user.getNickname(), "user.nickname is null");

        UserEntity persistedUser = userRepository.findByNickname(Nickname.of(user.getNickname())).orElseThrow(
                () -> new EntityNotFoundException("User with nickname=[" + user.getNickname() + "] was not found."));

        persistedUser.setFirstname(user.getFirstname());
        persistedUser.setName(user.getName());

        return persistedUser;
    }

    public List<UserEntity> findAll() {
        Iterable<UserEntity> all = userRepository.findAll();

        List<UserEntity> users = new ArrayList<>();
        for (UserEntity user : all) {
            users.add(user);
        }

        return users;
    }

    public Page<UserJson> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserEntityToJson::from);
    }

    public UserEntity findByName(String name) {
        return userRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
    }

    public UserEntity findByNickname(Nickname nickname) {
        return userRepository.findByNickname(nickname).orElseThrow(EntityNotFoundException::new);
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