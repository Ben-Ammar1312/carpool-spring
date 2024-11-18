package gle.carpoolspring.repository;

import gle.carpoolspring.model.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConducteurRepository extends JpaRepository<Conducteur, Integer> {
    Conducteur findByEmail(String email);
}
