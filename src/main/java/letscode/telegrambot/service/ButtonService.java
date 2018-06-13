package letscode.telegrambot.service;

import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.domain.BotUser;
import letscode.telegrambot.domain.Buttons;
import letscode.telegrambot.repo.MessageRepo;
import letscode.telegrambot.utils.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;

/**
 * Выполняем методы согласно команде.
 */


@Service
public class ButtonService {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepo messageRepo;

    private BotUser botUser;

    public void getData(Message message, String text) {

        Buttons button = Buttons.get(text);
        MessageBuilder messageBuilder = new MessageBuilder();
        switch (button) {
            case HELP:

                break;
            case QUESTIONS_LIST:
                List<BotMessage> botMessageList = messageRepo.findAllByDoneIsFalse();
                messageBuilder.createQuestions(message,botMessageList);
                break;
            case MY_QUESTIONS_LIST:
                break;
            case KNOWLEDGE_BASE:
                break;

        }

    }
    private int extractId(String text) {
        int id = Integer.parseInt(text.substring(text.indexOf("#")+1,
                text.indexOf(":")));
        return id;
    }

    public void getCall(CallbackQuery callbackQuery) {
        Buttons button = Buttons.get(callbackQuery.getData());
        MessageBuilder messageBuilder = new MessageBuilder();
        switch (button) {
            case OPEN_QUESTIONS:
                BotMessage quest = messageRepo.findById(extractId(callbackQuery.getMessage().getText()));
                messageBuilder.detailedQuestions(quest, callbackQuery);
                break;
        }
    }
}
