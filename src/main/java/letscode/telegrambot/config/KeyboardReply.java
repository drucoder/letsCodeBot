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

    public void addOpenButton(SendMessage sendMessage) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();

        InlineKeyboardButton button = btn.setText("Открыть вопрос").setCallbackData("getQuest");

        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);
    }

    public void addButtonDone(SendMessage sendMessage) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();

        InlineKeyboardButton button = btn.setText("Закрыть вопрос").setCallbackData("setDone");

        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);
    }
}
