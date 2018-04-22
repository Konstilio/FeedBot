package HansoftConnection;

import Shared.ISendBot;
import TelegramPolling.TelegramSendBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import se.hansoft.hpmsdk.EHPMSdkDebugMode;
import se.hansoft.hpmsdk.HPMSdkException;
import se.hansoft.hpmsdk.HPMSdkJavaException;
import se.hansoft.hpmsdk.HPMSdkSession;

import java.util.HashSet;

public class HansoftThread extends Thread {

    HPMSdkSession m_Session;
    long m_NextUpdate;
    long m_NextConnectionAttempt;
    HansoftCallback m_Callback = null;
    boolean m_bBrokenConnection;

    HansoftConnectionSettings m_ConnectionSettings;
    ISendBot m_SendBot = null;

    public HansoftThread(HansoftConnectionSettings _Settings)
    {
        m_Session = null;
        m_NextUpdate = 0;
        m_NextConnectionAttempt = 0;
        m_Callback = new HansoftCallback(this);
        m_bBrokenConnection = false;

        m_ConnectionSettings = _Settings;

    }

    public void setSendBot(ISendBot _SendBot) {
        this.m_SendBot = _SendBot;
    }
    boolean initConnection()
    {
        if (m_Session != null)
            return true;

        long currentTime = System.currentTimeMillis();
        if (currentTime > m_NextConnectionAttempt)
        {
            m_NextConnectionAttempt = 0;

            EHPMSdkDebugMode debugMode = EHPMSdkDebugMode.Debug; // Change to EHPMSdkDebugMode.Debug to get memory leak info and debug output. Note that this is expensive.

            try
            {
                // You should change these parameters to match your development server and the SDK account you have created. For more information see SDK documentation.
                m_Session = HPMSdkSession.SessionOpen
                    (
                        m_ConnectionSettings.m_Host
                        , m_ConnectionSettings.m_Port
                        , m_ConnectionSettings.m_Database
                        , m_ConnectionSettings.m_SDK
                        , m_ConnectionSettings.m_SDKPassword
                        , m_Callback
                        , null
                        , true
                        , debugMode
                        , 0
                        , ""
                        , "./HansoftSDK/Win"
                        , null
                    )
                ;
            }
            catch (HPMSdkException _Error)
            {
                System.out.println("SessionOpen 1 failed with error: " + _Error.ErrorAsStr() + "\r\n");
                return false;
            }
            catch (HPMSdkJavaException _Error)
            {
                System.out.println("SessionOpen 2 failed with error: " + _Error.ErrorAsStr() + "\r\n");
                return false;
            }

            System.out.println("Successfully opened session.\r\n");
            m_bBrokenConnection = false;

            return true;
        }
        return false;
    }

    void update()
    {
        if (initConnection())
        {
            if (m_bBrokenConnection) {
                System.out.println("Connection is broken");
                m_Callback = null; // #TODO_Boards: Do we need this ?
            } else {
                try {
                    m_Callback.Update();
                } catch (HPMSdkException _Error) {
                    System.out.println("update HPMSdkException Error: " + _Error.ErrorAsStr());
                } catch (HPMSdkJavaException _Error) {
                    System.out.println("update HPMSdkJavaException Error: " + _Error.ErrorAsStr());
                }
            }
        }
    }

    public void onNewsFeed(HansoftAction _Action) {
        if (m_SendBot != null)
            m_SendBot.sendAction(_Action);
    }

    public HPMSdkSession getSession() {
        return m_Session;
    }

    public void run()
    {
        while (!Thread.interrupted())
        {
            update();
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                System.out.print("Hansoft thread interupted");
                return;
            }
        }
    }
}
