package de.winkler.springboot.user.internal;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.winkler.springboot.user.Nickname;

public interface PrivilegeRepository extends CrudRepository<PrivilegeEntity, Long> {

    PrivilegeEntity findByName(String name);

    @Query("SELECT p FROM Privilege p JOIN FETCH p.roles r JOIN FETCH r.users u  WHERE u.nickname = :nickname")
    Set<PrivilegeEntity> findByNickname(Nickname nickname);

}
