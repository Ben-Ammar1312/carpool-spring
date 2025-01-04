package gle.carpoolspring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gle.carpoolspring.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findByTelephone(String telephone);
    boolean existsByEmail(String email);
    long countByEnabled(boolean b);
}