package letscode.telegrambot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.TelegramBotsApi;

@Configuration
public class TelegramBotConfig {
    @Bean
    @ConditionalOnMissingBean(TelegramBotsApi.class)
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi();
    }
}
