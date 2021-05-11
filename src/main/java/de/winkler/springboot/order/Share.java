package de.winkler.springboot.order;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity(name = "Share")
@Table(name = "SHARE")
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    @Column(name = "wkn", length = 50, nullable = false, unique = true)
    private String wkn;

}
