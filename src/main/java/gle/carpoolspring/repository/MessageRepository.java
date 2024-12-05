package gle.carpoolspring.repository;

import gle.carpoolspring.model.Message;
import gle.carpoolspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("SELECT m FROM Message m WHERE m.sender.id_user = :userId OR m.receiver.id_user = :userId")
    List<Message> findAllByUserId(@Param("userId") int userId);

    @Query("SELECT m FROM Message m WHERE (m.sender.id_user = :senderId AND m.receiver.id_user = :receiverId) " +
            "OR (m.sender.id_user = :receiverId AND m.receiver.id_user = :senderId)")
    List<Message> findMessagesBySenderAndReceiver(@Param("senderId") int senderId,
                                                  @Param("receiverId") int receiverId);


}




