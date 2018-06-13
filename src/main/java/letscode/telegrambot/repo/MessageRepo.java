package letscode.telegrambot.repo;

import letscode.telegrambot.domain.BotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<BotMessage, Long> {
    BotMessage findByAnswerForId(Integer messageId);
    BotMessage findById(Integer messageId);
    List<BotMessage> findAllByDoneIsFalse();
    long countAllByAnswerFor(BotMessage botMessage);
}
