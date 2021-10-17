package de.winkler.springboot.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import de.winkler.springboot.persistence.Id;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByName(String name);

    Optional<UserEntity> findByNickname(Nickname nickname);

    Page<UserEntity> findAll(Pageable pageable);

    default Optional<UserEntity> findById(Id<UserEntity> userId) {
        return findById(userId.id());
    }

}
