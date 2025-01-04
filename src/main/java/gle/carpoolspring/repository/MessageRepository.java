package gle.carpoolspring.repository;

import gle.carpoolspring.model.Chat;
import gle.carpoolspring.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByAnnonce_IdAnnonceOrderByTimestampAsc(int rideId);
    List<Message> findByChat_IdOrderByTimestampAsc(int chatId);
    List<Message> findByChatIdOrderByTimestampAsc(int chatId);
    int countByReceiver_IdUserAndIsReadFalse(int receiverId);
    List<Message> findByChat_IdAndReceiver_IdUserAndIsReadFalse(int chatId, int receiverId);

}
