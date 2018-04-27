package Shared;

import HansoftConnection.HansoftConnectionSettings;
import Slack.SlackIncomingWebhookSettings;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SharedSlackProperties {
    @JsonProperty("Hansoft")
    public HansoftConnectionSettings m_Hansoft;

    @JsonProperty("Slack")
    public SlackIncomingWebhookSettings m_Slack;
}
