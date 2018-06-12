package letscode.telegrambot.repo;

import letscode.telegrambot.domain.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<BotUser, Integer> {
}
