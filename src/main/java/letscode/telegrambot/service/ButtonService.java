package letscode.telegrambot.service;

import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.domain.BotUser;
import letscode.telegrambot.domain.Buttons;
import letscode.telegrambot.repo.MessageRepo;
import letscode.telegrambot.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.List;
import java.util.Optional;

/**
 * Выполняем методы согласно команде.
 */


@Service
public class ButtonService {

    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final MessageService messageService;
    private final SendService sendService;

    @Autowired
    public ButtonService(MessageRepo messageRepo,
                         MessageService messageService,
                         SendService sendService,
                         UserRepo userRepo) {
        this.messageRepo = messageRepo;
        this.messageService = messageService;
        this.sendService = sendService;
        this.userRepo = userRepo;
    }

    /**
     * Объеденил 2 метода getData и getCall в один <code>executeCommand(Update)</code>
     * Метод принимает update из чата, определяет его является ли update обычным сообщением или CallBackQuery
     * распарсивает update на (Message) message и (String) receiveCommand. Дальше находит свой case и выполняет метод,
     * еще мне не нравилось уведомление Идеи о том что может выхватиться NULL, поставил предварительную проверку на NULL,
     * хотя на Null есть проверка в LetsCodeBot. ;)
     *
     * @param update - принимаемый update из чата
     */
    public void executeCommand(Update update) {

        String receiveCommand;
        Message message;

        if (update.getCallbackQuery() != null) {      //проверяем содержит ли update в себе CallbackQuery()
            receiveCommand = update.getCallbackQuery().getData();   // получаем команду отправленную через CallbackQuery()
            message = update.getCallbackQuery().getMessage();   //получаем сообщение отправленное через CallbackQuery()
        } else {
            receiveCommand = update.getMessage().getText(); //простое распарсивание команды из чата
            message = update.getMessage();                  //и сообщения
        }

        Buttons button = Buttons.get(receiveCommand);
        Optional<BotMessage> botMessage;
        BotUser botUser = new BotUser();
        if (button != null) {
            switch (button) {

                case HELP:
                    sendService.sendMessage(message,"Привет, для того чтобы задать вопрос с сохранением в БД поставь перед вопросом <b>?</b>... ");
                    break;

                case QUESTIONS_LIST:    // Список всех вопросов
                    boolean isAnswer = false;
                    List<BotMessage> botMessageList = messageRepo.findAllByDoneIsFalseAndAnswerForNull();   //находим все сообщения ByDoneIsFalseAndAnswerForNull()
                    sendService.sendMessageList(message, botMessageList, isAnswer);
                    break;

                case MY_QUESTIONS_LIST: // Список вопросов пользователя
                    Integer id = message.getFrom().getId();
                    Optional<BotUser> user = userRepo.findById(id);
                    List<BotMessage> userMessageList = messageRepo.findAllByFromIsAndDoneIsFalse(user.get());
                    if (!userMessageList.isEmpty()) {
                        sendService.sendMessageList(message, userMessageList, false);
                    } else {
                        sendService.sendMessage(message, "Ты очень умный, или очень скромный, твой список пуст...");
                    }
                    break;

                case KNOWLEDGE_BASE:
                    //TODO: База знаний
                    break;

                case OPEN_QUESTIONS:    //Обрабатываем callBackQuerry - getQuest(Открыть вопрос)
                    isAnswer = false;
                    botMessage = messageRepo.findById(
                            extractId(message.getText())
                    );
                    long count = messageRepo.countAllByAnswerFor(botMessage.get());    //счетчик ответов.
                    sendService.openMessage(botMessage.get(), update.getCallbackQuery(), count, isAnswer); //создаем сообщение с вопросом
                    break;

                case SET_DONE:  //Обрабатываем callBackQuerry - Устанавливаем статус решенный
                    messageService.setDone(
                            messageRepo.findById(
                                    extractId(
                                            message.getText()
                                    )
                            )
                    );
                    sendService.sendMessage(message,
                            "Ваш вопрос закрыт, не забудьте отблагадорить тех, " +
                                    "кто принимал участие в решении вашей проблемы");
                    break;

                case GET_ANSWER_LIST:   //Обрабатываем callBackQuerry - список ответов
                    isAnswer = true;

                    botMessage = messageRepo.findById(extractId(message.getText()));
                    List<BotMessage> answerList = messageRepo
                            .findAllByAnswerFor(botMessage.get());
                    sendService.sendMessageList(message, answerList, isAnswer);
                    break;

                case GET_ANSWER:    //Обрабатываем callBackQuerry - конкретный ответ на вопрос
                    isAnswer = true;
                    botMessage = messageRepo.findById(
                            extractId(message.getText())
                    );
                    sendService.openMessage(botMessage.get(), update.getCallbackQuery(), 0, isAnswer);
                    break;
            }
        }
    }

    /**
     * Метод для извлечения id из текста
     *
     * @param text принимаемый текст
     * @return - возвращаем Long
     */
    private long extractId(String text) {
        return Long.parseLong(text.substring(text.indexOf("#") + 1,
                text.indexOf(":")));
    }
}
