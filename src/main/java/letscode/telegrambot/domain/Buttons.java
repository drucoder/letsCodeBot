package letscode.telegrambot.domain;

/**
 * Список кнопок
 */
public enum Buttons {

    KNOWLEDGE_BASE("База Ответов📚"),
    MY_QUESTIONS_LIST("Мои вопросы📒"),
    QUESTIONS_LIST("Список вопросов 📝"),
    HELP("Помощь 💬"),
    OPEN_QUESTIONS("getQuest"),
    SET_DONE("setDone"),
    GET_ANSWER_LIST("getAnswerList"),
    GET_ANSWER("getAnswer"),
    SET_LIKE("setLike"),
    SET_DISLIKE("setDislike");

    private final String buttonCommands;


    public static Buttons get(String buttonComands) {
        for (Buttons c: Buttons.values()) {
            if (buttonComands.equals(c.getButtonCommands())) {
                return c;
            }
        }
        return null;
    }

    Buttons(String buttonCommands) {
        this.buttonCommands = buttonCommands;
        }

    public String getButtonCommands() {
        return buttonCommands;
    }

}
