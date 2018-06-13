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
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "answer_for_id")
    private BotMessage answerFor;

    public BotMessage() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BotChat getChat() {
        return chat;
    }

    public void setChat(BotChat chat) {
        this.chat = chat;
    }

    public BotUser getFrom() {
        return from;
    }

    public void setFrom(BotUser from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BotMessage getAnswerFor() {
        return answerFor;
    }

    public void setAnswerFor(BotMessage answerFor) {
        this.answerFor = answerFor;
    }
}
