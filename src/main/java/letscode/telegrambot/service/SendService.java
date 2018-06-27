package letscode.telegrambot.service;

import letscode.telegrambot.bot.LetsCodeBot;
import letscode.telegrambot.config.KeyboardReply;
import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.repo.MessageRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;

@Slf4j
@Service
public class SendService {

    private final KeyboardReply keyboardReply;
    private final LetsCodeBot letsCodeBot;
    private final MessageRepo messageRepo;

    @Autowired
    public SendService(@Lazy LetsCodeBot letsCodeBot,
                       KeyboardReply keyboardReply,
                       MessageRepo messageRepo) {
        this.letsCodeBot = letsCodeBot;
        this.keyboardReply = keyboardReply;
        this.messageRepo = messageRepo;
    }

    /**
     * Метод создает тело сообщения
     *
     * @param receiveMessage - принятое сообщение из чата, для извлечения ID
     * @param botMessage     - Текст ответа от бота
     * @return - тело сообщения.
     */
    public SendMessage createMessageBody(Message receiveMessage, String botMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(receiveMessage.getChatId());
        sendMessage.enableHtml(true);
        if (!botMessage.isEmpty()) {
            sendMessage.setText(botMessage);
        } else {
            sendMessage.setText(receiveMessage.getText());
        }
        keyboardReply.setBottomButtons(sendMessage);
        return sendMessage;
    }

    /**
     * Метод отправляет сообщение в чат
     *
     * @param receiveMessage - принятое сообщение из чата
     * @param botText        - текст ответа бота.
     */
    public void sendMessage(Message receiveMessage, String botText) {
        letsCodeBot.send(createMessageBody(receiveMessage, botText));
    }

    /**
     * Метод создает сообщения списком.
     *
     * @param receiveMessage - принятое сообщение из чата для распарсивания
     * @param botMessageList - список сообщений
     * @param isAnswer       - так как этот метод используется для вывода сообщений вопросов и ответов,
     *                       то при формировании шапки, и кнопок используется эта булевая переменная
     */
    public void sendMessageList
    (Message receiveMessage, List<BotMessage> botMessageList, boolean isAnswer) {
        String getText, title, botText;

        for (BotMessage botMessage : botMessageList) {        //пробегаемся по списку botMessageList

            if (botMessage.getText().length() > 50) {       // если сообщение длиннее 50 символов режем его
                getText = botMessage.getText().substring(0, 50);
            } else {
                getText = botMessage.getText();
            }

            if (!isAnswer) {                                        //определяем, вопрос это или ответ.
                title = "<b>Вопрос #" + botMessage.getId() + ":</b>\n"; //шапка для вопроса
            } else {
                title = "<b>Ответ #" + botMessage.getId() + ":</b>\n";  //шапка для ответа.
            }

            botText = title +
                    "<code>" + getText + "</code>\n" +
                    "<b>Автор: " + botMessage.getFrom().getUserName() + "</b>";

            if (botMessage.getFileId() != null) {
                SendPhoto message = createPhotoMessage(receiveMessage, botMessage.getFileId(), botMessage.getId());
                letsCodeBot.sendImage(message);
            }
            SendMessage sendMessage = createMessageBody(receiveMessage, botText);
            keyboardReply.addOpenButton(sendMessage, isAnswer);
            letsCodeBot.send(sendMessage);
        }
    }

    /**
     * Сетод создает тело сообщения для отправки, содержит в себе файл изображения.
     *
     * @param receiveMessage - принятое сообщение
     * @param fileId         - имя файла
     * @param id             - Id вопроса.
     * @return
     */
    private SendPhoto createPhotoMessage(Message receiveMessage, String fileId, Integer id) {
        return new SendPhoto()
                .setChatId(receiveMessage.getChatId())
                .setCaption("Вопрос #" + id)
                .setPhoto(fileId);
    }

    /**
     * Метод отображает вопрос полностью.
     *
     * @param botMessage    - вопрос из БД
     * @param callbackQuery - перехваченный update из чата, который приходит в случае нажатия InlineKeyboardReply
     * @param count         - счетчик ответов.
     * @param isAnswer
     */
    public void openMessage(
            BotMessage botMessage,
            CallbackQuery callbackQuery,
            long count,
            boolean isAnswer) {
        String botText, title;

        boolean enableAnswer = count > 0;    // проверка, есть ли ответы на текущий вопрос.
        boolean isAuthor = callbackQuery     // проверяем, автора сообщения и пользователя нажавшего кнопку
                .getFrom()
                .getId()
                .equals(
                        botMessage
                                .getFrom()
                                .getId()
                );

        if (isAnswer) {         // устанавливаем шапку в зависимости от типа сообщения.
            title = "<b>Ответ #" + botMessage.getId() + ":</b>\n";
        } else {
            title = "<b>Вопрос #" + botMessage.getId() + ":</b>\n";
        }

        if (enableAnswer) {
            botText = title +
                    "<code>" + botMessage.getText() + "</code>\n" +
                    "<b>Автор: " + botMessage.getFrom().getUserName() + "    \uD83D\uDCAC: " + count + "</b>";
        } else {
            botText = title +
                    "<code>" + botMessage.getText() + "</code>\n" +
                    "<b>Автор: " + botMessage.getFrom().getUserName() + "</b>";
        }

        SendMessage sendMessage = createMessageBody(callbackQuery.getMessage(), botText);
        keyboardReply.addTwoLineKeyboard(sendMessage, isAuthor, enableAnswer, isAnswer);  //собираем клавиатуру
        letsCodeBot.send(sendMessage);
    }

    /**
     * Метод создает сообщении с решёнными вопросами. И тремя кнопками. посмотреть ответ, лайк, диз.
     *
     * @param receiveMessage - сообщение из чата
     * @param messageList    - список решённых сообщений
     */
    public void completeMessageList(Message receiveMessage,
                                    List<BotMessage> messageList) {
        String title, body, footer;

        for (BotMessage botMessage : messageList) {

            long count = messageRepo.countAllByAnswerFor(botMessage);   // считаем сколько ответов на этот вопрос.
            if (count > 0) {  //отображаем только закрытые с ответами

                title = "<b>Вопрос #" + botMessage.getId() + ":</b>\n"; //шапка сообщения
                body = "<code>" + botMessage.getText() + "</code>\n";   //тело
                footer = "<em>Автор вопроса: " + botMessage.getFrom().getUserName() + "</em>\n" +
                        "<em>Ответов: " + count + "</em>\n";    //нижняя часть

                //TODO придумать разделители между шапкой, телом и низом
                SendMessage sendMessage = createMessageBody(receiveMessage,
                        title + body + footer); //создаем тело сообщения
                keyboardReply.addTwoLineKeyboard(   //собираем клавиатуру
                        sendMessage,
                        false,              //ставит кнопку закрыть вопрос(false) потому что в нашем случае это не надо
                        true,           //создает кнопку список ответов(true)
                        true);              //Используем для установки лайков
                letsCodeBot.send(sendMessage);
            }
        }
    }
}
