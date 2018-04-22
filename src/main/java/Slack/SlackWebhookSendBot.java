package Slack;

import com.github.seratch.jslack.*;
import com.github.seratch.jslack.api.webhook.*;

import HansoftConnection.HansoftAction;
import Shared.ISendBot;
import okhttp3.Response;

import java.io.IOException;

public class SlackWebhookSendBot implements ISendBot {

    public void setSettings(SlackIncomingWebhookSettings _Settings) {
        m_Settings = _Settings;
    }


    @Override
    public void sendAction(HansoftAction _Action) {
        String Message =  _Action.toHTML();
        System.out.println("SlackWebhookSendBot: Trying to send message:" + Message);

        Payload SlackPayload = Payload.builder()
                .channel(m_Settings.m_Channel)
                .username(m_Settings.m_UserName)
                .text(Message)
                .build();

        Slack SlackInstance = Slack.getInstance();

        try {
            Response response = SlackInstance.send(m_Settings.m_Url, SlackPayload);
            if (response.code() != 200 || !response.isSuccessful())
            {
                System.out.println("SlackWebhookSendBot response error: " + response.message());
            }
        } catch (IOException _Exc) {
            System.out.println("SlackWebhookSendBot send message error: " + _Exc.getMessage());
        }
    }

    private SlackIncomingWebhookSettings m_Settings;
}
