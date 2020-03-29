package de.winkler.springboot.user;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findByNameOrderByNameAsc(String name);

    UserEntity findByNickname(String nickname);

}
