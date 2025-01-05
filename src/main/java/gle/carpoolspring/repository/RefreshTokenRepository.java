package gle.carpoolspring.repository;
import gle.carpoolspring.model.User;
import gle.carpoolspring.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByExpiryDateBefore(Instant now);
    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.idUser = :userId")
    void deleteByUserId(@Param("userId") int userId);
}
