package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "refreshtoken", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id", name = "UK81otwtvdhcw7y3ipoijtlb1g3")
})
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "idUser", nullable = false)
    private User user;

    @Column(nullable= false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
