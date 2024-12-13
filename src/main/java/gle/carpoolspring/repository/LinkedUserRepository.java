package gle.carpoolspring.repository;

import gle.carpoolspring.model.LinkedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkedUserRepository extends JpaRepository<LinkedUser, Integer> {

    @Query("SELECT CASE " +
            "WHEN l.user1.idUser = :userId THEN l.user2.idUser " +
            "WHEN l.user2.idUser = :userId THEN l.user1.idUser " +
            "END " +
            "FROM LinkedUser l " +
            "WHERE l.user1.idUser = :userId OR l.user2.idUser = :userId")
    List<Integer> findLinkedUserIdsByUserId(@Param("userId") int userId);

}

