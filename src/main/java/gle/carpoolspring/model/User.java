package gle.carpoolspring.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gle.carpoolspring.enums.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@Entity
@Table( name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idUser")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
public class User {

    // Example using `int` for ID; change to Long if you prefer
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUser;


    // Carpool fields (sample)
    @NotBlank(message = "Nom is required.")
    private String nom;

    @NotBlank(message = "Prénom is required.")
    private String prenom;

    private String telephone;

    @Email(message = "Please provide a valid email address.")
    @NotBlank(message = "Email is required.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    @Transient
    private String confirmPassword;

    private String adresse;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    // File upload (transient/not stored in DB)
    @Transient
    private MultipartFile photoFile;
    private String photo;
    private String cin;
    private Float note;

    // Verification flags
    private boolean emailVerified = false;
    private boolean smsVerified = false;
    private boolean enabled = false;
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;

    @Transient
    @AssertTrue(message = "You must agree to the terms.")
    private Boolean agreeTerms;

    // Example relationships
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> sentMessages = new HashSet<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> receivedMessages = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Reclamation> reclamations;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private Set<Chat> chats;

    // Constructors
    public User() {
    }

    // Convenience constructor if your JWT signup uses something like:
    // new User(username, email, password);
    public User( String email, String password) {
        this.email = email;
        this.password = password;
    }

    // equals & hashCode on idUser (or id) so each user is unique by DB identity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return this.idUser == that.idUser;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser);
    }
}