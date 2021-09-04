package de.winkler.springboot.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByName(String name);

    Optional<UserEntity> findByNickname(String nickname);

    Page<UserEntity> findAll(Pageable pageable);

}
