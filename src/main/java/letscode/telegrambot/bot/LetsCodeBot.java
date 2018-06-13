package letscode.telegrambot.bot;

import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Component
@Slf4j
public class LetsCodeBot extends TelegramLongPollingBot {
    private static final String BOT_TOKEN = "BOT_TOKEN";
    private static final String BOT_NAME = "BOT_NAME";

    private final MessageService messageService;

    @Autowired
    public LetsCodeBot(MessageService messageService) {
        this.messageService = messageService;
    }

    public void onUpdateReceived(Update update) {
        BotApiMethod message = null;
        BotMessage botMessage = null;
        boolean isIncoming = update.hasMessage() && update.getMessage().hasText();

        if (isIncoming) {
            botMessage = messageService.saveIncoming(update.getMessage());

            message = new SendMessage()
                    .setChatId(botMessage.getChat().getId())
                    .setText(botMessage.getText());
        } else if (update.hasEditedMessage() && update.getEditedMessage().hasText()) {
            botMessage = messageService.updateMessageText(update);

            message = new EditMessageText()
                    .setMessageId(botMessage.getId())
                    .setChatId(botMessage.getChat().getId())
                    .setText(botMessage.getText());
        }

        if (message != null) {
            Message resultMessage = null;

            try {
                resultMessage = (Message) execute(message);
            } catch (TelegramApiException e) {
 //               log.error("Some shit happens during message sending :(", e);
            }

            if (isIncoming) {
                messageService.saveOutgoing(resultMessage, botMessage);
            }
        }
    }

    public String getBotUsername() {
        return System.getenv(BOT_NAME);
    }

    public String getBotToken() {
        return System.getenv(BOT_TOKEN);
    }
}
