package gle.carpoolspring.repository;

import gle.carpoolspring.model.Passager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassagerRepository extends JpaRepository<Passager, Integer> {
}
