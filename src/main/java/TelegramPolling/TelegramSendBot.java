package TelegramPolling;

import Shared.ISendBot;
import Shared.SharedState;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class TelegramSendBot extends TelegramLongPollingBot implements ISendBot{

    private SharedState m_State;

    public void setSharedState(SharedState _State) {
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
