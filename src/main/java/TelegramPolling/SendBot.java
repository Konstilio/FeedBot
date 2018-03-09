package TelegramPolling;

import Shared.SharedState;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class SendBot extends TelegramLongPollingBot {

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
        System.out.println("SendBot should not recieve messages");
    }

    @Override
    public String getBotUsername() {
        return "";
    }
}
