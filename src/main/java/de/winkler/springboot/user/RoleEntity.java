package de.winkler.springboot.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.NaturalId;

@Entity(name = "Role")
@Table(name = "ROLE")
public class RoleEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    @Column(name = "rolename", length = 15, nullable = false, unique = true)
    private String rolename;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getRolename() {
        return rolename;
    }

    void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RoleEntity)) {
            return false;
        }

        RoleEntity other = (RoleEntity) o;
        return Objects.equals(getRolename(), other.getRolename());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRolename());
    }

    public static class RoleBuilder {
        private RoleBuilder() {
        }

        public static RoleEntity of(String rolename) {
            RoleEntity role = new RoleEntity();
            role.setRolename(rolename);
            return role;
        }
    }

}
