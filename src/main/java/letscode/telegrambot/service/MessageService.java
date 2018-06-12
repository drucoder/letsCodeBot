package letscode.telegrambot.service;

import letscode.telegrambot.domain.BotChat;
import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.domain.BotUser;
import letscode.telegrambot.repo.ChatRepo;
import letscode.telegrambot.repo.MessageRepo;
import letscode.telegrambot.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

@Service
public class MessageService {
    private final ChatService chatService;
    private final UserService userService;
    private final MessageRepo messageRepo;
    private final ChatRepo chatRepo;
    private final UserRepo userRepo;

    @Autowired
    public MessageService(
            ChatService chatService,
            UserService userService,
            MessageRepo messageRepo,
            ChatRepo chatRepo,
            UserRepo userRepo
    ) {
        this.chatService = chatService;
        this.userService = userService;
        this.messageRepo = messageRepo;
        this.chatRepo = chatRepo;
        this.userRepo = userRepo;
    }

    public BotMessage saveIncoming(Message message) {
        BotMessage botMessage = getBotMessage(message);

        return messageRepo.save(botMessage);
    }

    private BotMessage getBotMessage(Message message) {
        BotChat botChat = chatRepo.findById(message.getChat().getId())
                .orElseGet(() -> chatService.saveIncoming(message.getChat()));

        BotUser botUser = userRepo.findById(message.getFrom().getId())
                .orElseGet(() -> userService.saveIncoming(message.getFrom()));

        BotMessage botMessage = new BotMessage();

        botMessage.setId(message.getMessageId());
        botMessage.setText(message.getText());
        botMessage.setChat(botChat);
        botMessage.setFrom(botUser);
        return botMessage;
    }

    public BotMessage saveOutgoing(Message message, BotMessage originalMessage) {
        BotMessage botMessage = getBotMessage(message);

        botMessage.setAnswerFor(originalMessage);

        return messageRepo.save(botMessage);
    }

    public BotMessage updateMessageText(Update update) {
        String messageText = update.getEditedMessage().getText();

        BotMessage answer = messageRepo.findByAnswerForId(update.getEditedMessage().getMessageId());

        answer.setText(messageText);
        answer.getAnswerFor().setText(messageText);

        return messageRepo.save(answer);
    }
}