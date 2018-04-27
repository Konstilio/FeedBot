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

    @Override
    public String toString() {
        return "["
                + "\n\tHost = " + m_Host
                + "\n\tPort = " + m_Port
                + "\n\tDatabase = " + m_Database
                + "\n\tSDK = " + m_SDK
                + "\n\tSDKPassword = " + m_SDKPassword
                + "\n]";
    }
}
