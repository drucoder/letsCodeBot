package letscode.telegrambot.domain;

import lombok.Data;

import javax.persistence.*;

@Table(name = "telegram_message")
@Entity
@Data
public class BotMessage {
    @Id
    private Integer id;
    @ManyToOne
    private BotChat chat;
    @ManyToOne
    @JoinColumn(name = "user_id_from")
    private BotUser from;
    @Column(length = 4096)
    private String text;
    private boolean done;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "answer_for_id")
    private BotMessage answerFor;

}
