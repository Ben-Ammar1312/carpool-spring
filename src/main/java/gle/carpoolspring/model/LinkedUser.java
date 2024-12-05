package gle.carpoolspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "linked_users")
@Getter
@Setter
public class LinkedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_user1", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "id_user2", nullable = false)
    private User user2;
}
