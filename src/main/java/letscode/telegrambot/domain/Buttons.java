package letscode.telegrambot.domain;

import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.soap.Addressing;

/**
 * Список кнопок
 */
public enum Buttons {

    KNOWLEDGE_BASE(0,"База Ответов📚"),
    MY_QUESTIONS_LIST(1,"Мои вопросы📒"),
    QUESTIONS_LIST(2,"Список вопросов 📝"),
    HELP(3,"Помощь 💬"),
    OPEN_QUESTIONS(4,"getQuest");

    private final int code;
    private final String buttonCommands;


    public static Buttons get(String buttonComands) {
        for (Buttons c: Buttons.values()) {
            if (buttonComands.equals(c.getButtonCommands())) {
                return c;
            }
        }
        return null;
    }

    Buttons(int code, String buttonCommands) {
        this.buttonCommands = buttonCommands;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getButtonCommands() {
        return buttonCommands;
    }

    @Override
    public String toString() {
        return "Commands{" +
                "code=" + code +
                ", operator='" + buttonCommands + '\'' +
                '}';
    }
}
