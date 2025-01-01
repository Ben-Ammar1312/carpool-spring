package gle.carpoolspring.repository;

import gle.carpoolspring.enums.Status;
import gle.carpoolspring.model.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Integer> {
    // Fetch complaints, ordered by status (open first)
    List<Reclamation> findAllByOrderByStatusDesc();

    long countByStatus(Status status);
}
