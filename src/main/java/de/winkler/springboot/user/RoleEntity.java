package de.winkler.springboot.user;

import java.util.Objects;

import javax.persistence.*;

import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "ROLE")
public class RoleEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    @Column(name = "rolename", length = 15, nullable = false, unique = true)
    private String rolename;

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
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

}
