import HansoftConnection.HansoftConnectionSettings;
import HansoftConnection.HansoftThread;

public class HansoftSimpleMain {

    public static void main(String[] args) {

        HansoftConnectionSettings Settings = new HansoftConnectionSettings();
        Settings.m_Host = "localhost";
        Settings.m_Port = 50255;
        Settings.m_Database = "Dev_24_04";
        Settings.m_SDK = "SDK";
        Settings.m_SDKPassword = "hpmadm";

        HansoftThread Hansoft = new HansoftThread(Settings);
        Hansoft.start();
    }
}
