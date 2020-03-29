package de.winkler.springboot.user;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.findByNameOrderByNameAsc(name);
    }

    @Transactional
    public UserEntity findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Transactional
    public UserEntity find(long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }

}