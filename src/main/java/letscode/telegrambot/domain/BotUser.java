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

    public BotUser() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Boolean getBot() {
        return isBot;
    }

    public void setIsBot(Boolean bot) {
        isBot = bot;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
