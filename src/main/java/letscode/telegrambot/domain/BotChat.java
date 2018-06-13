package letscode.telegrambot.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "telegram_chat")
@Data
public class BotChat {
    @Id
    private Long id;
    private BotChatType type;
    private String title;
    private String firstName;
    private String lastName;
    private String userName;

}
