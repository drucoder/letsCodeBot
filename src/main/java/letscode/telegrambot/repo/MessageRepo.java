package letscode.telegrambot.repo;

import letscode.telegrambot.domain.BotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<BotMessage, Long> {

    /**
     * Метод ищет ответ по ID
     * @param messageId - Integer id - ответа который мы ищем.
     * @return - возвращает BotMessage
     */
    BotMessage findByAnswerForId(Integer messageId);

    /**Тупой поиск сообщения по ID
     * @param messageId - ID запрашиваемого сообщения
     * @return - возвращает запись из бд с соответственным ID
     */
    BotMessage findById(Integer messageId);

    /**Список сообщений у которых статус выполнения false, и AnswerFor = null,
     * для поиска всех не закрытых вопросов
     * @return
     */
    List<BotMessage> findAllByDoneIsFalseAndAnswerForNull();

    /**
     * Счетчик Оветов
     * @param botMessage - принимает сообщение и ищет все сообщения в базе у которых колонка
     *                   AnswerFor == botMessage
     * @return возвращает long
     */
    long countAllByAnswerFor(BotMessage botMessage);

    /**
     * Поиск всех ответов
     * @param botMessage - Ищет все сообщения в которых колонка AnswerFor == botMessage
     * @return - возвращает список сообщений.
     */
    List<BotMessage> findAllByAnswerFor(BotMessage botMessage);

}
