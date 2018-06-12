package letscode.telegrambot.repo;

import letscode.telegrambot.domain.BotChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepo extends JpaRepository<BotChat, Long> {
}
