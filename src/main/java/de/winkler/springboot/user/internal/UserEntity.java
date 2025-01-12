package de.winkler.springboot.user.internal;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

import org.hibernate.annotations.NaturalId;

import de.winkler.springboot.user.Nickname;

@Entity(name = "User")
@Table(name = "END_USER")
public class UserEntity implements de.winkler.springboot.persistence.Id<UserEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId(mutable = true)
    @Embedded
    private Nickname nickname;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "firstname", length = 50, nullable = false)
    private String firstname;

    @Column(name = "password", length = 50, nullable = false)
    private String password;

    @Column(name = "age")
    private int age;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<RoleEntity> roles = new HashSet<>();

    @Override
    public Long id() {
        return id;
    }

    @Override
    public UserEntity type() {
        return this;
    }

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public Nickname getNickname() {
        return nickname;
    }

    public void setNickname(Nickname nickname) {
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
                ", age='" + age + '\'' +
                '}';
    }

    public static class UserBuilder {
        private Nickname nickname;
        private String password;
        private String name;
        private int age;

        private String firstname;

        private UserBuilder() {
        }

        public static UserBuilder of(Nickname nickname, String password) {
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

        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }

        public UserEntity build() {
            UserEntity ue = new UserEntity();
            ue.setNickname(nickname);
            ue.setPassword(password);
            ue.setFirstname(firstname);
            ue.setName(name);
            ue.setAge(age);
            return ue;
        }
    }

}
