package letscode.telegrambot.service;

import letscode.telegrambot.domain.BotUser;
import letscode.telegrambot.repo.UserRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.User;

@Service
public class UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public BotUser saveIncoming(User user) {
        BotUser botUser = new BotUser();

        BeanUtils.copyProperties(user, botUser);
        botUser.setIsBot(user.getBot());

        return userRepo.save(botUser);
    }
}
