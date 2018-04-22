package TelegramPolling;

import HansoftConnection.HansoftAction;
import Shared.ISendBot;
import Shared.SharedTelegramState;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashSet;

public class TelegramSendBot extends TelegramLongPollingBot implements ISendBot{

    private SharedTelegramState m_State;

    public TelegramSendBot(SharedTelegramState _State)
    {
        m_State = _State;
    }

    @Override
    public void sendAction(HansoftAction _Action) {
        HashSet<Long> Chats = m_State.getChats();
        if (Chats.isEmpty())
            return;

        String message = _Action.toHTML();
        System.out.println("TelegramSendBot: Trying to send message:" + message);

        for( Long ChatID : Chats) {
            SendMessage Message = new SendMessage().setChatId(ChatID).setText(message).enableHtml(true);
            try {
                execute(Message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSharedState(SharedTelegramState _State) {
        m_State = _State;
    }

    @Override
    public String getBotToken() {
        return m_State.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("TelegramSendBot should not recieve messages");
    }

    @Override
    public String getBotUsername() {
        return "";
    }
}
