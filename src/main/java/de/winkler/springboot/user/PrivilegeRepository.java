package de.winkler.springboot.user;

import org.springframework.data.repository.CrudRepository;

public interface PrivilegeRepository extends CrudRepository<PrivilegeEntity, Long> {

    PrivilegeEntity findByName(String name);

}
