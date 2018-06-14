package letscode.telegrambot.service;

import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.domain.Buttons;
import letscode.telegrambot.repo.MessageRepo;
import letscode.telegrambot.utils.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;

/**
 * Выполняем методы согласно команде.
 */


@Service
public class ButtonService {

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private MessageService messageService;

    private boolean isAnswer;

    /**
     * Отрабатываем команды кнопок меню
     * @param message - принятое сообщение из чата
     * @param text - текст( команда из чата)
     */
    public void getData(Message message, String text) {

        Buttons button = Buttons.get(text);
        MessageBuilder messageBuilder = new MessageBuilder();

        switch (button) {
            case HELP:

                break;
            case QUESTIONS_LIST:
                isAnswer = false;
                List<BotMessage> botMessageList = messageRepo.findAllByDoneIsFalseAndAnswerForNull();
                messageBuilder.createMessageList(message,botMessageList,isAnswer);
                break;
            case MY_QUESTIONS_LIST:
                break;
            case KNOWLEDGE_BASE:
                break;

        }

    }

    public void getCall(CallbackQuery callbackQuery) {
        Buttons button = Buttons.get(callbackQuery.getData());
        MessageBuilder messageBuilder = new MessageBuilder();
        BotMessage botMessage;

        switch (button) {
            case OPEN_QUESTIONS:    //Обрабатываем callBackQuerry - getQuest
                isAnswer = false;
                botMessage = messageRepo.findById(   //находим вопрос по id из callBackQuerry.getMessage.getText()
                        extractId(
                                callbackQuery
                                        .getMessage()
                                        .getText()
                        )
                );

                long count = messageRepo.countAllByAnswerFor(botMessage);    //счетчик ответов.
                messageBuilder.detailedQuestions(botMessage, callbackQuery, count,isAnswer); //
                break;
            case SET_DONE:
                messageService.setDone(callbackQuery);
                messageBuilder.createDynamicMessage(callbackQuery,"Ваш вопрос закрыт, не забудьте отблагадорить тех, кто принимал участие в решении вашей проблемы");
                break;
            case GET_ANSWER_LIST:
                isAnswer = true;
                List<BotMessage> answerList = messageRepo
                        .findAllByAnswerFor(messageRepo
                                .findById(
                                        extractId(
                                            callbackQuery
                                                .getMessage()
                                                .getText())
                                )
                        );
                messageBuilder.createMessageList(callbackQuery.getMessage(),answerList,isAnswer);
                break;
            case GET_ANSWER:
                isAnswer = true;
                botMessage = messageRepo.findById(   //находим вопрос по id из callBackQuerry.getMessage.getText()
                        extractId(
                                callbackQuery
                                        .getMessage()
                                        .getText()
                        )
                );
                messageBuilder.detailedQuestions(botMessage,callbackQuery,0, isAnswer);

        }
    }

    private int extractId(String text) {
        int id = Integer.parseInt(text.substring(text.indexOf("#")+1,
                text.indexOf(":")));
        return id;
    }
}
