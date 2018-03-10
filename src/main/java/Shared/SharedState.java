package Shared;

import java.io.*;
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

    public void addChat(Long _chatID) {

        synchronized (this) {
            m_Chats.add(_chatID);
        }
        saveConfig();
    }

    public void removeChat(Long _chatID) {

        synchronized (this) {
            m_Chats.remove(_chatID);
        }
        saveConfig();
    }

    // #TODO: Improve it somehow
    public synchronized HashSet<Long> getChats() {
        return new LinkedHashSet<>(m_Chats);
    }

    private void saveConfig() {
        try {
            FileOutputStream fos = new FileOutputStream("chats.config");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(m_Chats);
            oos.close();
            fos.close();
        } catch (IOException _e) {
            System.out.println("chats save config failed");
            _e.printStackTrace();
        }
    }

    public void loadConfig() {
        try {
            FileInputStream fis = new FileInputStream("chats.config");
            ObjectInputStream ois = new ObjectInputStream(fis);
            synchronized (this) {
                m_Chats = (LinkedHashSet)ois.readObject();
            }
        } catch (IOException _e) {
            System.out.println("chats load config failed");
            _e.printStackTrace();
        } catch (ClassNotFoundException _e) {
            System.out.println("chats load config failed");
            _e.printStackTrace();
        }
    }
}
