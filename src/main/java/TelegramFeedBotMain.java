import HansoftConnection.HansoftConnectionSettings;
import HansoftConnection.HansoftThread;
import Shared.ISendBot;
import Shared.SharedTelegramState;
import TelegramPolling.LongPollingBot;
import javax.swing.*;

import TelegramPolling.TelegramSendBot;
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

                        SharedTelegramState State = new SharedTelegramState(_BotToken);
                        State.loadConfig();

                        LongPollingBot bot = new LongPollingBot();
                        bot.setSharedState(State);
                        m_BotsApi = new TelegramBotsApi();
                        try {
                            m_Session = m_BotsApi.registerBot(bot);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        HansoftConnectionSettings Settings = new HansoftConnectionSettings();
                        Settings.m_Host = _Host;
                        Settings.m_Port = _Port;
                        Settings.m_Database = _Database;
                        Settings.m_SDK = _SDK;
                        Settings.m_SDKPassword = _SDKPassword;

                        m_Hansoft = new HansoftThread(Settings);
                        ISendBot Bot = new TelegramSendBot(State);
                        m_Hansoft.setSendBot(Bot);
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
