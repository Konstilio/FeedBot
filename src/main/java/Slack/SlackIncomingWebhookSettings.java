package Slack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SlackIncomingWebhookSettings {

    @JsonProperty("Url")
    public String m_Url;

    @JsonProperty("Channel")
    public String m_Channel;

    @JsonProperty("UserName")
    public String m_UserName;
}
