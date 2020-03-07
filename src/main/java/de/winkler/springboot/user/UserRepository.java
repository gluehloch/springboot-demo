package de.winkler.springboot.user;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    public UserEntity findByName(String name);

}
