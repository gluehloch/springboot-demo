package de.winkler.springboot.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="USER")
public class UserEntity {

    @Id
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "firstname", length = 50, nullable = false)
    private String firstname;

    public Long getId() {
        return id;
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

}
