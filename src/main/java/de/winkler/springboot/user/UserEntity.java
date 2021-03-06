package de.winkler.springboot.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

@Entity(name = "User")
@Table(name = "USER")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    @Column(name = "nickname", length = 30, nullable = false)
    private String nickname;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "firstname", length = 50, nullable = false)
    private String firstname;

    @Column(name = "password", length = 50, nullable = false)
    private String password;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<RoleEntity> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addRole(RoleEntity role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(RoleEntity role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", name='" + name + '\'' +
                ", firstname='" + firstname + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static class UserBuilder {
        private String nickname;
        private String password;
        private String name;

        private String firstname;

        private UserBuilder() {
        }

        public static UserBuilder of(String nickname, String password) {
            UserBuilder ub = new UserBuilder();
            ub.nickname = nickname;
            ub.password = password;
            return ub;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder firstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public UserEntity build() {
            UserEntity ue = new UserEntity();
            ue.setNickname(nickname);
            ue.setPassword(password);
            ue.setFirstname(firstname);
            ue.setName(name);
            return ue;
        }
    }

}
