import HansoftConnection.HansoftConnectionSettings;
import HansoftConnection.HansoftThread;
import Shared.ISendBot;
import Shared.SharedSlackProperties;
import Slack.SlackWebhookSendBot;
import TelegramPolling.TelegramSendBot;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class SlackFeedBotMain {

    public static void main(String[] args) {
        ObjectMapper Mapper = new ObjectMapper();

        try {
            SharedSlackProperties Properties = Mapper.readValue(new File("slackConfig.json"), SharedSlackProperties.class);

            System.out.println(Properties.m_Hansoft);
            System.out.println(Properties.m_Slack);

            m_Hansoft = new HansoftThread(Properties.m_Hansoft);
            SlackWebhookSendBot Bot = new SlackWebhookSendBot();
            Bot.setSettings(Properties.m_Slack);
            m_Hansoft.setSendBot(Bot);
            m_Hansoft.start();

        } catch (JsonGenerationException e) {
            System.out.println("read SharedSlackProperties JsonGenerationException exception");
            e.printStackTrace();
        } catch (JsonMappingException e) {
            System.out.println("read SharedSlackProperties JsonMappingException exception");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("read SharedSlackProperties IOException exception");
            e.printStackTrace();
        }
    }

    static HansoftThread m_Hansoft;
}
