package gle.carpoolspring.repository;

import gle.carpoolspring.model.Chat;
import gle.carpoolspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @EntityGraph(attributePaths = {"participants"})
    List<Chat> findByParticipantsContaining(User user);

    @EntityGraph(attributePaths = {"participants"})
    @Query("SELECT c FROM Chat c JOIN FETCH c.participants WHERE c.id = :chatId")
    Chat findChatWithParticipants(int chatId);

    Optional<Chat> findByAnnonce_IdAnnonceAndParticipantsContaining(int rideId, User participant);
}