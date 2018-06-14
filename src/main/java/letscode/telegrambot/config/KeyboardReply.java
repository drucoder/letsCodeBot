package letscode.telegrambot.config;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardReply {

    /**
     * Клавиатура с 4мя кнопками <ul>
     *     <li><b>База Ответов</b> - Выводит сообщением все закрытые вопросы пользователей</li>
     *     <li><b>Мои вопросы</b> - Выводит сообщениями все вопросы текущего пользователя</li>
     *     <li><b>Список вопросов</b> - Выводит сообщениеми все открытые вопросы пользователей</li>
     *     <li><b>Помощь</b> - FAQ</li>
     * </ul>
     *
     * @param sendMessage - <code>SendMessage sendMessage</code> добавляем в sendMessage клавиатуру
     */
    public void setBottomButtons(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список рядов клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первый ряд клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первый ряд клавиатуры
        keyboardFirstRow.add(new KeyboardButton("База Ответов📚"));
        keyboardFirstRow.add(new KeyboardButton("Мои вопросы📒"));

        // Второй ряд клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во второй ряд клавиатуры
        keyboardSecondRow.add(new KeyboardButton("Список вопросов 📝"));
        keyboardSecondRow.add(new KeyboardButton("Помощь 💬"));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    /**
     * Метод устанавливает в SendMessage <code>sendMessage.setReplyMarkup(markupInline);</code>
     * кнопки в теле сообщения формирует за счет переменной isAnswer
     * @param sendMessage - тело сообщения
     * @param isAnswer - булевая переменная от которой зависит какая будет кнопка "Открыть ответ" - true
     *                 "Открыть вопрос" - false.
     */
    public void addOpenButton(SendMessage sendMessage, boolean isAnswer) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        if (!isAnswer) {
            InlineKeyboardButton button = btn.setText("Открыть вопрос").setCallbackData("getQuest");
            rowInline.add(button);
        } else {
            InlineKeyboardButton button = btn.setText("Открыть ответ").setCallbackData("getAnswer");
            rowInline.add(button);
        }

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);
    }

    /**
     * Метод создает клавиатуру для просмотра вопроса.
     * @param sendMessage - тело сообщения
     * @param isAuthor - если переменная true, то добавляет кнопку закрыть вопрос.
     * @param enableAnswer - если переменная true, добавляет кнопку посмотреть ответы на вопрос.
     * @param isAnswer - если сообщение является ответом то добавляем кнопки Like-Dislike
     */
    public void addTwoLineKeyboard(SendMessage sendMessage, boolean isAuthor, boolean enableAnswer, boolean isAnswer) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        if (isAuthor && !isAnswer) {
                InlineKeyboardButton btnDone = new InlineKeyboardButton();
                InlineKeyboardButton buttonDone = btnDone.setText("Закрыть вопрос").setCallbackData("setDone");
                rowInline.add(buttonDone);

            }

            if (enableAnswer) {
                InlineKeyboardButton btnListAnswer = new InlineKeyboardButton();
                InlineKeyboardButton buttonAnswerList = btnListAnswer.setText("Ответы").setCallbackData("getAnswerList");
                rowInline.add(buttonAnswerList);

            }

        if (isAnswer) {
            InlineKeyboardButton btnLike = new InlineKeyboardButton();
            InlineKeyboardButton buttonLike = btnLike.setText("👍").setCallbackData("setLike");
            rowInline.add(buttonLike);
            InlineKeyboardButton btnDislike = new InlineKeyboardButton();
            InlineKeyboardButton buttonDisLike = btnDislike.setText("👎").setCallbackData("setDislike");
            rowInline.add(buttonDisLike);

        }
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

    }
}
