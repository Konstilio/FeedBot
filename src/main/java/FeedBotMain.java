import HansoftConnection.HansoftThread;
import TelegramPolling.LongPollingBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by OleksandrKonstantinov on 2/26/18.
 */
public class FeedBotMain {

    public static void main(String[] args) {

        String cwd = System.getProperty("user.dir");
        System.out.println("Current working directory : " + cwd);

        ApiContextInitializer.init();

        LongPollingBot bot = new LongPollingBot();
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId("447198168")
                .setText("Hi");
        try {
            bot.execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        HansoftThread HPMFeeder = new HansoftThread();
        HPMFeeder.start();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new LongPollingBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
