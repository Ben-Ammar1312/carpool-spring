package gle.carpoolspring.repositories;

import gle.carpoolspring.models.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConducteurReository extends JpaRepository<Conducteur, Integer> {
}
