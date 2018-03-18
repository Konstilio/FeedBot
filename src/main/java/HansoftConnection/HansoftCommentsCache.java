package HansoftConnection;

import javassist.tools.rmi.ObjectNotFoundException;
import se.hansoft.hpmsdk.HPMUniqueID;

import java.util.HashMap;

public class HansoftCommentsCache {
    private HashMap<HPMUniqueID, HashMap<Integer, String>> m_Comments = new HashMap<>();

    public boolean IsCommentExist(HPMUniqueID _TaskID, Integer _PostID) {
        HashMap<Integer, String> Comments = m_Comments.get(_TaskID);
        if (Comments == null)
            return false;

        return Comments.containsKey(_PostID);
    }

    public String GetComment(HPMUniqueID _TaskID, Integer _PostID){
        HashMap<Integer, String> Comments = m_Comments.get(_TaskID);
        if (Comments == null)
            return null;

        return Comments.get(_PostID);
    }

    public void SetComment(HPMUniqueID _TaskID, Integer _PostID, String _Comment) {
        HashMap<Integer, String> Comments = m_Comments.get(_TaskID);
        if (Comments == null) {
            HashMap<Integer, String> NewComments = new HashMap<>();
            NewComments.put(_PostID, _Comment);
            m_Comments.put(_TaskID, NewComments);
        }
        else {
            Comments.put(_PostID, _Comment);
        }
    }

    public void DeleteComment(HPMUniqueID _TaskID, Integer _PostID) {
        HashMap<Integer, String> Comments = m_Comments.get(_TaskID);
        if (Comments == null)
            return;

        Comments.remove(_PostID);
    }
}
