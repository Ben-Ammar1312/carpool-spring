package gle.carpoolspring.repository;

import gle.carpoolspring.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
    Paiement findByPaymentIntentId(String paymentIntentId);
}