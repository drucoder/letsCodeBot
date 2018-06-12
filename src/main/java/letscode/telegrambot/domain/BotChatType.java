package letscode.telegrambot.domain;

import lombok.ToString;
import org.telegram.telegrambots.api.objects.Chat;

@ToString(of = {"name"})
public enum BotChatType {
    USER("private"),
    GROUP("group"),
    CHANNEL("channel"),
    SUPERGROUP("supergroup");

    private String name;

    BotChatType(String name) {
        this.name = name;
    }

    public static BotChatType of(Chat chat) {
        if (chat.isUserChat()) {
            return USER;
        } else if (chat.isGroupChat()) {
            return GROUP;
        } else if (chat.isChannelChat()) {
            return CHANNEL;
        } else if (chat.isSuperGroupChat()) {
            return SUPERGROUP;
        } else {
            return null;
        }
    }
}
