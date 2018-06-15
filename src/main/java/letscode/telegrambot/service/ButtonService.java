package letscode.telegrambot.service;

import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.domain.Buttons;
import letscode.telegrambot.repo.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.List;

/**
 * Выполняем методы согласно команде.
 */


@Service
public class ButtonService {

    private final MessageRepo messageRepo;
    private final MessageService messageService;
    private final SendService sendService;

    @Autowired
    public ButtonService(MessageRepo messageRepo,
                         MessageService messageService,
                         SendService sendService) {
        this.messageRepo = messageRepo;
        this.messageService = messageService;
        this.sendService = sendService;
    }

    /**
     * Объеденил 2 метода getData и getCall в один <code>executeCommand(Update)</code>
     * Метод принимает update из чата, определяет его является ли update обычным сообщением или CallBackQuery
     * распарсивает update на (Message) message и (String) receiveCommand. Дальше находит свой case и выполняет метод,
     * еще мне не нравилось уведомление Идеи о том что может выхватиться NULL, поставил предварительную проверку на NULL,
     * хотя на Null есть проверка в LetsCodeBot. ;)
     * @param update - принимаемый update из чата
     */
    public void executeCommand(Update update) {

        String receiveCommand;
        Message message;
        BotMessage botMessage;

        if (update.getCallbackQuery()!=null) {      //проверяем содержит ли update в себе CallbackQuery()
            receiveCommand = update.getCallbackQuery().getData();   // получаем команду отправленную через CallbackQuery()
            message = update.getCallbackQuery().getMessage();   //получаем сообщение отправленное через CallbackQuery()
        } else {
            receiveCommand = update.getMessage().getText(); //простое распарсивание команды из чата
            message = update.getMessage();                  //и сообщения
        }

        Buttons button = Buttons.get(receiveCommand);

        if (button != null) {
            switch (button) {

                case HELP:
                    //TODO: FAQ по боту.
                    break;

                case QUESTIONS_LIST:
                    boolean isAnswer = false;
                    List<BotMessage> botMessageList = messageRepo.findAllByDoneIsFalseAndAnswerForNull();   //находим все сообщения ByDoneIsFalseAndAnswerForNull()
                    sendService.sendMessageList(message,botMessageList, isAnswer);
                    break;

                case MY_QUESTIONS_LIST:
                    //TODO: Список вопросов пользователя
                    break;

                case KNOWLEDGE_BASE:
                    //TODO: База знаний
                    break;

                case OPEN_QUESTIONS:    //Обрабатываем callBackQuerry - getQuest
                    isAnswer = false;
                    botMessage = messageRepo.findById(   //находим вопрос по id из callBackQuerry.getMessage.getText()
                            extractId(message.getText())
                    );
    
                    long count = messageRepo.countAllByAnswerFor(botMessage);    //счетчик ответов.
                    sendService.openMessage(botMessage, update.getCallbackQuery(), count, isAnswer);
                    break;
    
                case SET_DONE:
                    messageService.setDone(messageRepo.findById(
                            extractId(message.getText())
                    ));
                    sendService.sendMessage(message,
                            "Ваш вопрос закрыт, не забудьте отблагадорить тех, кто принимал участие в решении вашей проблемы");
                    break;
    
                case GET_ANSWER_LIST:
                    isAnswer = true;
                    List<BotMessage> answerList = messageRepo
                            .findAllByAnswerFor(messageRepo
                                    .findById(
                                            extractId(
                                                    message.getText()
                                            )
                                    )
                            );
                    sendService.sendMessageList(message, answerList, isAnswer);
                    break;
    
                case GET_ANSWER:
                    isAnswer = true;
                    botMessage = messageRepo.findById(   //находим вопрос по id из callBackQuerry.getMessage.getText()
                            extractId(
                                    message.getText()
                            )
                    );
                    sendService.openMessage(botMessage, update.getCallbackQuery(), 0, isAnswer);
                    break;
            }
        }
    }

    /**
     * Метод для извлечения id из текста
     * @param text принимаемый текст
     * @return - возвращаем Long
     */
    private int extractId(String text) {
        return Integer.parseInt(text.substring(text.indexOf("#") + 1,
                text.indexOf(":")));
    }
}
