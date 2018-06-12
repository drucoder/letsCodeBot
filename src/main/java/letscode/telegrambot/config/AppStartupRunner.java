package letscode.telegrambot.config;

import letscode.telegrambot.bot.LetsCodeBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramBotsApi;

@Component
public class AppStartupRunner implements ApplicationRunner {
    private final TelegramBotsApi telegramBotsApi;
    private final LetsCodeBot letsCodeBot;

    @Autowired
    public AppStartupRunner(TelegramBotsApi telegramBotsApi, LetsCodeBot letsCodeBot) {
        this.telegramBotsApi = telegramBotsApi;
        this.letsCodeBot = letsCodeBot;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        telegramBotsApi.registerBot(letsCodeBot);
    }
}
