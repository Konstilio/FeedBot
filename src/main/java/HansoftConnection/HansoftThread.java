package HansoftConnection;

import Shared.SharedState;
import TelegramPolling.SendBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import se.hansoft.hpmsdk.EHPMDataHistoryClientOrigin;
import se.hansoft.hpmsdk.EHPMError;
import se.hansoft.hpmsdk.EHPMSdkDebugMode;
import se.hansoft.hpmsdk.HPMChangeCallbackData_TaskCreateUnified;
import se.hansoft.hpmsdk.HPMProjectEnum;
import se.hansoft.hpmsdk.HPMProjectProperties;
import se.hansoft.hpmsdk.HPMResourceEnum;
import se.hansoft.hpmsdk.HPMResourceProperties;
import se.hansoft.hpmsdk.HPMSdkCallbacks;
import se.hansoft.hpmsdk.HPMSdkException;
import se.hansoft.hpmsdk.HPMSdkJavaException;
import se.hansoft.hpmsdk.HPMSdkSession;
import se.hansoft.hpmsdk.HPMTaskCreateUnified;
import se.hansoft.hpmsdk.HPMTaskCreateUnifiedEntry;
import se.hansoft.hpmsdk.HPMTaskCreateUnifiedReference;
import se.hansoft.hpmsdk.HPMTaskEnum;
import se.hansoft.hpmsdk.HPMTaskTimeZones;
import se.hansoft.hpmsdk.HPMTaskTimeZonesZone;
import se.hansoft.hpmsdk.HPMUniqueID;

import java.util.HashSet;

public class HansoftThread extends Thread {

    HPMSdkSession m_Session;
    long m_NextUpdate;
    long m_NextConnectionAttempt;
    HansoftCallback m_Callback = null;
    boolean m_bBrokenConnection;

    String m_Host;
    Integer m_Port;
    String m_Database;
    String m_SDK;
    String m_SDKPassword;

    SharedState m_State;
    SendBot m_SendBot;

    public HansoftThread(String _Host, Integer _Port, String _Database, String _SDK, String _Password)
    {
        m_Session = null;
        m_NextUpdate = 0;
        m_NextConnectionAttempt = 0;
        m_Callback = new HansoftCallback(this);
        m_bBrokenConnection = false;

        m_Host = _Host;
        m_Port = _Port;
        m_Database = _Database;
        m_SDK = _SDK;
        m_SDKPassword = _Password;

        m_SendBot = new SendBot();
    }

    public void setSharedState(SharedState _State) {
        m_State = _State;
        m_SendBot.setSharedState(m_State);
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
                m_Session = HPMSdkSession.SessionOpen(m_Host, m_Port, m_Database, m_SDK, m_SDKPassword, m_Callback
                        , null, true, debugMode, 0, "", "./HansoftSDK/Win", null);
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
        if (m_State == null)
        {
            System.out.println("onNewsFeed: State is not set");
            return;
        }

        HashSet<Long> Chats = m_State.getChats();
        if (Chats.isEmpty())
            return;

        String message = _Action.toHTML();
        System.out.println("onNewsFeed: Trying to send message:" + message);

        for( Long ChatID : Chats) {
            SendMessage Message = new SendMessage().setChatId(ChatID).setText(message).enableHtml(true);
            try {
                m_SendBot.execute(Message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
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
