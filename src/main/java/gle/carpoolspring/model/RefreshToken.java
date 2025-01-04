package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity(name = "refreshtoken")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "idUser")
    private User user;

    @Column(nullable= false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
