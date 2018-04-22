package HansoftConnection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HansoftConnectionSettings {

    @JsonProperty("host")
    public String m_Host;

    @JsonProperty("port")
    public Integer m_Port;

    @JsonProperty("Database")
    public String m_Database;

    @JsonProperty("SDK")
    public String m_SDK;

    @JsonProperty("SDKPassword")
    public String m_SDKPassword;
}
