package de.winkler.springboot.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface PrivilegeRepository extends CrudRepository<PrivilegeEntity, Long> {

    PrivilegeEntity findByName(String name);

    @Query("SELECT p FROM Privilege p JOIN FETCH p.roles r JOIN FETCH r.users u  WHERE u.nickname = :nickname")
    Set<PrivilegeEntity> findByNickname(Nickname nickname);

}
