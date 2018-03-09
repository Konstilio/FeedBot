package Shared;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class SharedState {

    private HashSet<Long> m_Chats = new LinkedHashSet<>();
    private String m_Token;

    public SharedState(String _Token) {
        m_Token = _Token;
    }

    public String getToken() {
        return m_Token;
    }

    public synchronized void addChat(Long _chatID) {
        m_Chats.add(_chatID);
    }

    public synchronized void removeChat(Long _chatID) {
        m_Chats.remove(_chatID);
    }

    // #TODO: Improve it somehow
    public synchronized HashSet<Long> getChats() {
        return new LinkedHashSet<>(m_Chats);
    }
}
