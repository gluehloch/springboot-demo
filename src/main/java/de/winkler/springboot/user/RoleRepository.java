package de.winkler.springboot.user;

import java.util.List;
import java.util.Optional;

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
    List<RoleEntity> findRoles(Nickname nickname);

    /**
     * Find role by role name
     * @param roleName the name of the role
     * @return the role
     */
    @Query
    Optional<RoleEntity> findByName(String roleName);

}
