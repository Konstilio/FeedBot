import HansoftConnection.HansoftThread;
import Shared.SharedState;
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
public class TelegramFeedBotMain {

    public static void main(String[] args) {

        ApiContextInitializer.init();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TelegramFeedBotMain();
            }
        });
    }

    public TelegramFeedBotMain() {
        MainFrame Frame = new MainFrame();
        Frame.setUIHandler(
                new IUIHandler() {
                    public void start(String _BotToken, String _Host, Integer _Port
                            , String _Database, String _SDK, String _SDKPassword) {

                        SharedState State = new SharedState(_BotToken);
                        State.loadConfig();

                        LongPollingBot bot = new LongPollingBot();
                        bot.setSharedState(State);
                        m_BotsApi = new TelegramBotsApi();
                        try {
                            m_Session = m_BotsApi.registerBot(bot);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        m_Hansoft = new HansoftThread(_Host, _Port, _Database, _SDK, _SDKPassword);
                        m_Hansoft.setSharedState(State);
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
