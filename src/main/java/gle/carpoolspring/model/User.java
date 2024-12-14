package gle.carpoolspring.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gle.carpoolspring.enums.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public  class User implements UserDetails , Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUser;
    @NotBlank(message = "Nom (Last Name) is required.")
    private String nom;

    @NotBlank(message = "Prénom (First Name) is required.")
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

    @Transient
    private MultipartFile photoFile;

    private String photo;
    private String cin;
    private Float note;
    private boolean emailVerified = false;
    private boolean smsVerified = false;
    private boolean enabled = false;
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    @Transient
    @AssertTrue(message = "You must agree to the terms.")
    private Boolean agreeTerms;


    @OneToMany(mappedBy = "sender")
    @JsonIgnore
    private List<gle.carpoolspring.model.Message> sentMessages;


    @OneToMany(mappedBy = "receiver")
    @JsonIgnore
    private List<gle.carpoolspring.model.Message> receivedMessages;


    @OneToMany(mappedBy = "user")
    private List<gle.carpoolspring.model.Reclamation> reclamations;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


}
