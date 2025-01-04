package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, length = 50, nullable = false)
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }
}