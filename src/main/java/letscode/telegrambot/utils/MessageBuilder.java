package letscode.telegrambot.utils;

import letscode.telegrambot.bot.LetsCodeBot;
import letscode.telegrambot.config.KeyboardReply;
import letscode.telegrambot.domain.BotMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;

@Component
public class MessageBuilder {

    @Autowired
    LetsCodeBot letsCodeBot;

    public void createQuestions
            (Message message, List<BotMessage> botMessageList) {
        String getText;
        SendMessage sendMessage = new SendMessage();
        KeyboardReply keyboardReply = new KeyboardReply();
        LetsCodeBot letsCodeBot = new LetsCodeBot();
        for (BotMessage botMessage:botMessageList) {
            if (botMessage.getText().length() > 50) {
                getText = botMessage.getText().substring(0, 50);
            } else {
                getText = botMessage.getText();
            }
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("<code>"+getText+"</code>\n" +
                    "<b>Автор: "+botMessage.getFrom().getUserName()+"</b>");
            sendMessage.enableHtml(true);
            keyboardReply.addOpenButton(sendMessage);
            letsCodeBot.send(sendMessage);
        }
    }

    public void detailedQuestions(BotMessage message, CallbackQuery callbackQuery) {
        SendMessage sendMessage = new SendMessage();
        KeyboardReply keyboardReply = new KeyboardReply();
        LetsCodeBot letsCodeBot = new LetsCodeBot();
        sendMessage.setChatId(callbackQuery.getMessage().getChatId());
        sendMessage.enableHtml(true);
        sendMessage.setText("<code>"+message.getText()+"</code>\n" +
                "<b>Автор: "+message.getFrom().getUserName()+"</b>");
        if (callbackQuery.getFrom().getId().equals(message.getFrom().getId())) {
            keyboardReply.addButtonDone(sendMessage);
        }
        letsCodeBot.send(sendMessage);
    }


}
