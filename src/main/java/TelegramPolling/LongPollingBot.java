package TelegramPolling;

import Shared.SharedState;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;

public class LongPollingBot extends TelegramLongPollingBot {

    private SharedState m_State;

    public void setSharedState(SharedState _State) {
        m_State = _State;
    }

    public String getBotToken() {
        return m_State.getToken();
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String Text = update.getMessage().getText();
            Long ChatID = update.getMessage().getChatId();
            if (Text.equals("/start")) {
                System.out.println("Recieved /start from " + ChatID.toString());
                m_State.addChat(ChatID);
            }
            else if (Text.equals("/end")) {
                System.out.println("Recieved /end from " + ChatID.toString());
                m_State.removeChat(ChatID);
            }
        }
    }

    public String getBotUsername() {
        return "HPMTestFeedBot";
    }

}
