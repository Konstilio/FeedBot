import HansoftConnection.HansoftThread;
import TelegramPolling.LongPollingBot;
import javax.swing.*;

import UI.IUIHandler;
import UI.MainFrame;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

/**
 * Created by OleksandrKonstantinov on 2/26/18.
 */
public class FeedBotMain{

    public static void main(String[] args) {

        ApiContextInitializer.init();

//        LongPollingBot bot = new LongPollingBot();
//        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
//                .setChatId("447198168")
//                .setText("Hi");
//        try {
//            bot.execute(message); // Call method to send the message
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FeedBotMain();
            }
        });
    }

    public FeedBotMain() {
        MainFrame Frame = new MainFrame();
        Frame.setStartHandler(
                new IUIHandler() {
                    public void start(String _BotToken, String _Host, Integer _Port
                            , String _Database, String _SDK, String _SDKPassword) {
                        LongPollingBot Bot = new LongPollingBot();
                        Bot.setToken(_BotToken);
                        m_BotsApi = new TelegramBotsApi();
                        try {
                            m_Session = m_BotsApi.registerBot(new LongPollingBot());
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        m_Hansoft = new HansoftThread(_Host, _Port, _Database, _SDK, _SDKPassword);
                        m_Hansoft.start();
                    }

                    public void stop() {
                        m_Session.stop();
                        m_Session = null;

                        m_Hansoft.interrupt();
                        m_Hansoft = null;
                    }
                }
        );
    }

    private BotSession m_Session = null;
    private HansoftThread m_Hansoft = null;
    TelegramBotsApi m_BotsApi = null;
}
