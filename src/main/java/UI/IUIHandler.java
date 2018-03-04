package UI;

public interface IUIHandler {

    public void start(String _BotToken, String _Host, Integer _Port
            , String _Database, String _SDK, String _SDKPassword);

    public void stop();

}
