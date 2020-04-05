package de.winkler.springboot.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    /**
     * Abfrage aller Rollen eines Users.
     *
     * @param nickname Der Nickname des gesuchten Users.
     * @return Eine Liste mit Rollen zu dem gesuchten User.
     */
    @Query("SELECT r FROM Role r JOIN FETCH r.users u WHERE u.nickname = :nickname")
    List<RoleEntity> findRoles(String nickname);

}
