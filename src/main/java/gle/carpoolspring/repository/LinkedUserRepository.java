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
            "WHEN l.user1.id_user = :userId THEN l.user2.id_user " +
            "WHEN l.user2.id_user = :userId THEN l.user1.id_user " +
            "END " +
            "FROM LinkedUser l " +
            "WHERE l.user1.id_user = :userId OR l.user2.id_user = :userId")
    List<Integer> findLinkedUserIdsByUserId(@Param("userId") int userId);

}

