package gle.carpoolspring.service;

import gle.carpoolspring.model.Role;
import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.RoleRepository;
import gle.carpoolspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private TwilioVerifyService twilioVerifyService;

    /**
     * Check if a user exists by email.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Encode the user's password.
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Save a new user.
     */
    public void update(User user) {
        userRepository.save(user);
    }

    /**
     * Assign roles based on input strings.
     */
    public Set<Role> assignRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(roleStr -> {
                String roleName = "ROLE_" + roleStr.toUpperCase();
                Role foundRole = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Error: " + roleName + " is not found."));
                roles.add(foundRole);
            });
        }

        return roles;
    }

    /**
     * Initiate email and SMS verification.
     */
    public void initiateVerification(User user) {
        if (user.getTelephone() != null && !user.getTelephone().isEmpty()) {
            twilioVerifyService.sendVerificationCode(user.getTelephone());
        }
        verificationService.sendVerificationEmail(user);
    }

    /**
     * Find user by username
     */
    public User findByEmail(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User findById(int id) {
        return userRepository.findByIdUser(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    };
}