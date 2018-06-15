package letscode.telegrambot.service;

import letscode.telegrambot.bot.LetsCodeBot;
import letscode.telegrambot.config.KeyboardReply;
import letscode.telegrambot.domain.BotChat;
import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.domain.BotUser;
import letscode.telegrambot.repo.ChatRepo;
import letscode.telegrambot.repo.MessageRepo;
import letscode.telegrambot.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final ChatService chatService;
    private final UserService userService;
    private final MessageRepo messageRepo;
    private final ChatRepo chatRepo;
    private final UserRepo userRepo;
    private final SendService sendService;


    @Autowired
    public MessageService(
            ChatService chatService,
            UserService userService,
            MessageRepo messageRepo,
            ChatRepo chatRepo,
            UserRepo userRepo,
            SendService sendService
    ) {
        this.chatService = chatService;
        this.userService = userService;
        this.messageRepo = messageRepo;
        this.chatRepo = chatRepo;
        this.userRepo = userRepo;
        this.sendService = sendService;
    }

    /**
     * Метод сохраняет сообщения в Базу, если сообщение было ответом на вопрос то сохраняет его как отввет
     * в случае удачного нахождения id в тексте, и удачного нахождения сообщения с текущим id.
     * @param message - принятое сообщение из чата
     * @return - возвращает тупо запись в БД
     */
    public BotMessage saveIncoming(Message message) {
        BotMessage botMessage = getBotMessage(message);
        boolean isReply = message.getReplyToMessage()!=null; // проверка Reply статус сообщения

        if (!isReply) {
            botMessage.setText(message.getText().substring(1)); // если сообщение не Ответ то записываем его как вопрос предварительно вырезав тэг знака вопроса
        } else {    //если сообщение getReplyToMessage()
            try {
                Integer messageId = extractId(message.getReplyToMessage().getText()); // извлекаем id сообщения из текста
                botMessage.setAnswerFor(messageRepo.findById((long)messageId).get()); // устанавливаем сообщению перед сохранением в AnswerFor сообщение найденное по ID.
                sendService.sendMessage(message,"Спасибо за ответ, возможно автору это поможет.");
            } catch (Exception ex) {    //таким способом сделал проверку на парсинг числа из текста, и нахождение сообщения в базе
                //хз что сюда ставить, впринципе обработка exception в случае нахождения тут не особо нужна.
            }
        }
        return messageRepo.save(botMessage);
    }

    private BotMessage getBotMessage(Message message) {
        BotChat botChat = chatRepo.findById(message.getChat().getId())
                .orElseGet(() -> chatService.saveIncoming(message.getChat()));

        BotUser botUser = userRepo.findById(message.getFrom().getId())
                .orElseGet(() -> userService.saveIncoming(message.getFrom()));

        BotMessage botMessage = new BotMessage();

        botMessage.setId(Long.valueOf(message.getMessageId()));
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

    /**
     * Устанавливаем статус done = true; чтобы отметить сообщение выполненным
     * @param quest
     */
    public void setDone(BotMessage quest) {
        quest.setDone(true);
        messageRepo.save(quest);
    }

    /**
     * Извлекаем id из текста и возвращаем его
     * @param text - текст в котором ищем ID
     * @return - возвращаем INTEGER
     */
    private Integer extractId(String text) {
        String idT = text.substring(text.indexOf("#")+1,
                text.indexOf(":"));

        return Integer.parseInt(idT);
    }

}
