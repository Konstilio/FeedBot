package Slack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SlackIncomingWebhookSettings {

    @JsonProperty("Url")
    public String m_Url;

    @JsonProperty("Channel")
    public String m_Channel;

    @JsonProperty("UserName")
    public String m_UserName;

    @Override
    public String toString() {
        return "["
                + "\n\tUrl = " + m_Url
                + "\n\tChannel = " + m_Channel
                + "\n\tUserName = " + m_UserName
                + "\n]";
    }
}
