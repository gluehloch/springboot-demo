package de.winkler.springboot.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity(name = "Role")
@Table(name = "ROLE")
public class RoleEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId(mutable = true) // Nickname kann man hier ändern. Ist das in Ordnung?
    @Column(name = "name", length = 15, nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "roles_privileges",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Set<PrivilegeEntity> privileges = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void addPrivilege(PrivilegeEntity privilege) {
        privileges.add(privilege);
        privilege.getRoles().add(this);
    }

    public Set<PrivilegeEntity> getPrivileges() {
        return privileges;
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
        return Objects.equals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    public static class RoleBuilder {
        private RoleBuilder() {
        }

        public static RoleEntity of(String name) {
            RoleEntity role = new RoleEntity();
            role.setName(name);
            return role;
        }
    }

}
