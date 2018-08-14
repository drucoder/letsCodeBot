package letscode.telegrambot.bot;

import letscode.telegrambot.domain.BotMessage;
import letscode.telegrambot.domain.Buttons;
import letscode.telegrambot.service.ButtonService;
import letscode.telegrambot.service.MessageService;
import letscode.telegrambot.service.SendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
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
    private final ButtonService buttonService;
    private final SendService sendService;

    @Autowired
    public LetsCodeBot(MessageService messageService,
                       ButtonService buttonService,
                       SendService sendService) {
        this.messageService = messageService;
        this.buttonService = buttonService;
        this.sendService = sendService;
    }

    public void onUpdateReceived(Update update) {
        BotApiMethod message = null;
        BotMessage botMessage = null;

        boolean isIncoming = update.hasMessage() && update.getMessage().hasText();

        if (isIncoming) {
            Message receiveMessage = update.getMessage();

            boolean isStarting = receiveMessage.getText()
                    .toLowerCase()
                    .equals("/start");

            if (isStarting) {
                send(sendService.createMessageBody(receiveMessage,
                        "Привет, " + receiveMessage.getFrom().getFirstName() + "!!!"));
            }

            boolean isBottomButtonsPressed = Buttons.of(receiveMessage.getText()) != null &&
                    !update.hasCallbackQuery();

            if (isBottomButtonsPressed) {
                buttonService.executeCommand(update);
            }

            boolean isQuestions = receiveMessage.getText().startsWith("?"); //Проверка вопроса на сохранение в БД

            if (isQuestions) {
                messageService.saveIncoming(receiveMessage);
                send(sendService
                        .createMessageBody(receiveMessage,
                                "Ваш вопрос был сохранён, вы можете его найти в разделе мои вопросы."));
            }

            boolean isAnswer = (receiveMessage.getReplyToMessage() != null);
            if (isAnswer) {
                messageService.saveIncoming(receiveMessage);
            }

        } else if (update.hasEditedMessage() && update.getEditedMessage().hasText()) {
            botMessage = messageService.updateMessageText(update);

            message = new EditMessageText()
                    .setMessageId(Math.toIntExact(botMessage.getId()))
                    .setChatId(botMessage.getChat().getId())
                    .setText(botMessage.getMessageText());
        } else if (update.hasCallbackQuery()) { //Проверяем является ли сообщение InlineKeyboardReply командой

            buttonService.executeCommand(update);
        }

        if (message != null) {
            Message resultMessage = null;

            try {
                resultMessage = (Message) execute(message);
            } catch (TelegramApiException e) {
                log.error("Some shit happens during message sending :(", e);
            }

            if (isIncoming) {
                messageService.saveOutgoing(resultMessage, botMessage);
            }
        }

        boolean imageQuestions = (update.getMessage().getPhoto() != null    //проверяем содержит ли вопрос пикчу.
                && update.getMessage().getCaption().startsWith("?"));

        if (imageQuestions) {
            messageService.saveIncoming(update.getMessage());
        }
    }

    /**
     * Отправляем сообщения
     *
     * @param sendMessage
     */
    public void send(SendMessage sendMessage) {

        try {
            execute(sendMessage); // Call method to send the message
        } catch (TelegramApiException e) {
            log.error("Some shit happens during message sending :(", e);
        }
    }

    public void sendImage(SendPhoto msg) {
        try {
            sendPhoto(msg);
        } catch (TelegramApiException e) {
            log.error("Some shit happens during sending image message:(", e + ")");
        }
    }

    public String getBotUsername() {
        return System.getenv(BOT_NAME);
    }

    public String getBotToken() {
        return System.getenv(BOT_TOKEN);
    }
}
