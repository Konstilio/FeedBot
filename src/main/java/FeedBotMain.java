import TelegramPolling.LongPollingBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by OleksandrKonstantinov on 2/26/18.
 */
public class FeedBotMain {

    public static void main(String[] args) {

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new LongPollingBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
