package de.winkler.springboot.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    @Query(nativeQuery = true, value =
            "SELECT role.* "
            + "FROM "
            + "    role role "
            + "    JOIN user_role ur ON (ur.role_id = role.id) "
            + "    JOIN user u ON (u.id = ur.user_id)"
            + "WHERE "
            + "    u.nickname = :nickname")
    List<RoleEntity> findRoles(String nickname);

}
