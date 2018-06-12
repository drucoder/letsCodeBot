package letscode.telegrambot.repo;

import letscode.telegrambot.domain.BotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<BotMessage, Long> {
    BotMessage findByAnswerForId(Integer messageId);
}
