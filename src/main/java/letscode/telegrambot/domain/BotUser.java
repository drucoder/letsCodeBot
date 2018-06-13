package letscode.telegrambot.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "telegram_user")
@Data
public class BotUser {
    @Id
    public Integer id;
    private String firstName;
    private Boolean isBot;
    private String lastName;
    private String userName;

}
