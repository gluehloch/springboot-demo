package de.winkler.springboot.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    /**
     * Abfrage aller Rollen eines gesuchten Users. Die Abfrage ist ein nativer Query String formuliert. In
     * {@link #findRolesAsJPA(String)} findet sich die gleiche Abfrage als JPA Query.
     *
     * @param nickname Der Nickname des gesuchten Users.
     * @return Eine Liste mit Rollen zu dem gesuchten User.
     */
    @Query(nativeQuery = true, value =
            "SELECT role.* "
                    + "FROM "
                    + "    role role "
                    + "    JOIN user_role ur ON (ur.role_id = role.id) "
                    + "    JOIN user u ON (u.id = ur.user_id)"
                    + "WHERE "
                    + "    u.nickname = :nickname")
    List<RoleEntity> findRoles(String nickname);

    /**
     * Die gleiche Abfrage wie in {@link #findRoles(String)}. In diesem Fall aber als JPA Query. Die Query
     * funktioniert nur, wenn Hibernate als JPA Implementierung verwendet wird.
     *
     * @param nickname Der Nickname des gesuchten Users.
     * @return Eine Liste mit Rollen zu dem gesuchten User.
     */
    @Query("SELECT role "
            + "FROM Role role "
            + "JOIN UserRole ur ON (ur.role = role) "
            + "JOIN User u ON (u = ur.user)")
    List<RoleEntity> findRolesAsJPA(String nickname);

}
