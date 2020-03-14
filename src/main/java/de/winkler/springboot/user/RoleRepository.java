package de.winkler.springboot.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    @Query(value = "SELECT role FROM RoleEntity role JOIN UserRoleEntity ur JOIN UserEntity u WHERE u.nickname = :nickname")
    List<RoleEntity> findRoles(String nickname);

}
