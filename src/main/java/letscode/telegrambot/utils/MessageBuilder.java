package letscode.telegrambot.utils;

import letscode.telegrambot.bot.LetsCodeBot;
import letscode.telegrambot.config.KeyboardReply;
import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.repo.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;

/**
 * Этот клас был создан из-за того, что у меня вывалилась ошибка цикла БИНОВ
 */
@Service
public class MessageBuilder {

    @Autowired
    LetsCodeBot letsCodeBot;

    @Autowired
    MessageRepo messageRepo;

    /**
     * Метод создает сообщения списком.
     * @param message - принятое сообщение из чата для распарсивания
     * @param botMessageList - список сообщений
     * @param isAnswer - так как этот метод используется для вывода сообщений вопросов и ответов,
     *                 то при формировании шапки, и кнопок используется эта булевая переменная
     */
    public void createMessageList
            (Message message, List<BotMessage> botMessageList, boolean isAnswer) {
        String getText, title;
        SendMessage sendMessage = new SendMessage();
        KeyboardReply keyboardReply = new KeyboardReply();
        LetsCodeBot letsCodeBot = new LetsCodeBot();

        for (BotMessage botMessage:botMessageList) {        //пробегаемся по списку botMessageList

            if (botMessage.getText().length() > 50) {       // если сообщение длиннее 50 символов режем его
                getText = botMessage.getText().substring(0, 50);
            } else {
                getText = botMessage.getText();
            }

            sendMessage.setChatId(message.getChatId());

            if (!isAnswer) {                                        //определяем, вопрос это или ответ.
                title = "<b>Вопрос #"+botMessage.getId()+":</b>\n"; //шапка для вопроса
            } else {
                title = "<b>Ответ #"+botMessage.getId()+":</b>\n";  //шапка для ответа.
            }

            sendMessage.setText(title +
                    "<code>"+getText+"</code>\n" +
                    "<b>Автор: "+botMessage.getFrom().getUserName()+"</b>");

            sendMessage.enableHtml(true);                           //устанавливаем HTML разметку в сообщение
            keyboardReply.addOpenButton(sendMessage,isAnswer);      // устанавливаем клавиатуру для сообщения
            letsCodeBot.send(sendMessage);                          // отправляем сообщение
        }
    }

    /**
     * Метод отображает вопрос полностью.
     * @param botMessage - вопрос из БД
     * @param callbackQuery - перехваченный update из чата, который приходит в случае нажатия InlineKeyboardReply
     * @param count - счетчик ответов.
     * @param isAnswer
     */
    public void detailedQuestions(
                                    BotMessage botMessage,
                                    CallbackQuery callbackQuery,
                                    long count,
                                    boolean isAnswer) {
        String botText, title;
        SendMessage sendMessage = new SendMessage();
        KeyboardReply keyboardReply = new KeyboardReply();
        LetsCodeBot letsCodeBot = new LetsCodeBot();
        sendMessage.setChatId(callbackQuery.getMessage().getChatId());
        sendMessage.enableHtml(true);

        if (isAnswer) {
            title = "<b>Ответ #" + botMessage.getId() + ":</b>\n";
        } else {
            title = "<b>Вопрос #" + botMessage.getId() + ":</b>\n";
        }
        boolean enableAnswer = count > 0;
        if (enableAnswer) {
            botText = title +
                    "<code>" + botMessage.getText() + "</code>\n" +
                    "<b>Автор: " + botMessage.getFrom().getUserName() + "    \uD83D\uDCAC: " + count + "</b>";
        } else {
            botText = title +
                    "<code>" + botMessage.getText() + "</code>\n" +
                    "<b>Автор: " + botMessage.getFrom().getUserName() + "    \uD83D\uDCAC: " + count + "</b>";
        }
        sendMessage.setText(botText);
        boolean isAuthor = callbackQuery.getFrom().getId().equals(botMessage.getFrom().getId()); // проверяем, автора сообщения и пользователя нажавшего кнопку
            // проверяем есть ли ответы на этот вопрос
            keyboardReply.addTwoLineKeyboard(sendMessage, isAuthor, enableAnswer, isAnswer);  //собираем клавиатуру
        letsCodeBot.send(sendMessage);
    }

    /** TODO переделать метод
     * Создает динамические сообщения для уведомления пользователей
     * @param callbackQuery
     * @param botText
     */
    public void createDynamicMessage(CallbackQuery callbackQuery, String botText) {
        SendMessage sendMessage = new SendMessage();
        LetsCodeBot letsCodeBot = new LetsCodeBot();
        sendMessage.setChatId(callbackQuery.getMessage().getChatId());
        sendMessage.enableHtml(true);
        sendMessage.setText(botText);
        letsCodeBot.send(sendMessage);
    }

}
