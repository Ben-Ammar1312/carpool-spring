package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER fetching to load 'annonce' with 'chat'
    @JoinColumn(name = "annonce_id", nullable = false) // Ensure this matches your DB schema
    private Annonce annonce;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages = new HashSet<>();

    // Methods to add/remove participants and messages

    // Participants
    public void addParticipant(User user) {
        participants.add(user);
        user.getChats().add(this);
    }

    public void removeParticipant(User user) {
        participants.remove(user);
        user.getChats().remove(this);
    }

    // Messages
    public void addMessage(Message message) {
        messages.add(message);
        message.setChat(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setChat(null);
    }

}