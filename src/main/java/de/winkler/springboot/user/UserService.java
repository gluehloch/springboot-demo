package de.winkler.springboot.user;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserEntity createUser(String name, String firstname) {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setFirstname(firstname);
        return userRepository.save(user);
    }

    public UserEntity findUser(String name) {
        return userRepository.findByName(name);
    }

}