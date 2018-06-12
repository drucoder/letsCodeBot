package letscode.telegrambot.service;

import letscode.telegrambot.domain.BotChat;
import letscode.telegrambot.domain.BotChatType;
import letscode.telegrambot.repo.ChatRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Chat;

@Service
public class ChatService {
    private final ChatRepo chatRepo;

    @Autowired
    public ChatService(ChatRepo chatRepo) {
        this.chatRepo = chatRepo;
    }

    public BotChat saveIncoming(Chat chat) {
        BotChat botChat = new BotChat();

        BeanUtils.copyProperties(chat, botChat);
        botChat.setType(BotChatType.of(chat));

        return chatRepo.save(botChat);
    }
}
