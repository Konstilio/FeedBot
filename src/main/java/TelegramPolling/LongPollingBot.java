package TelegramPolling;

import Shared.SharedTelegramState;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class LongPollingBot extends TelegramLongPollingBot {

    private SharedTelegramState m_State;

    public void setSharedState(SharedTelegramState _State) {
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
