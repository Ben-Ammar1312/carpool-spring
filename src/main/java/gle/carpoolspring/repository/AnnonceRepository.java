package gle.carpoolspring.repository;

import gle.carpoolspring.models.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnonceRepository extends JpaRepository<Annonce, Integer> {
    List<Annonce> findByConducteur_id_user(int id_user);

}
