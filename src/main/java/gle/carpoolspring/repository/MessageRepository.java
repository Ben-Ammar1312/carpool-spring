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

    @Query("SELECT m FROM Message m WHERE m.sender.idUser = :userId OR m.receiver.idUser = :userId")
    List<Message> findAllByUserId(@Param("userId") int userId);

    @Query("SELECT m FROM Message m WHERE (m.sender.idUser = :senderId AND m.receiver.idUser = :receiverId) " +
            "OR (m.sender.idUser = :receiverId AND m.receiver.idUser = :senderId)")
    List<Message> findMessagesBySenderAndReceiver(@Param("senderId") int senderId,
                                                  @Param("receiverId") int receiverId);


}




