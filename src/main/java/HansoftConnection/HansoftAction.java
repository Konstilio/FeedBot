package HansoftConnection;

import se.hansoft.hpmsdk.HPMUniqueID;

public class HansoftAction {

    public enum EAction {
        Change
        , Comment
        , CommentChange
        , Create
        , Delete
    };

    public static class Task {
        public String m_Name = "";
        public String m_Hyperlink = "";
        public int m_ID = -1;
        public boolean m_bShowName = true;

        public String toString() {
            StringBuilder Builder = new StringBuilder();
            Builder.append("Task (ID: ");
            Builder.append(m_ID);
            if (m_bShowName && !m_Name.isEmpty())
            {
                Builder.append(", Name: ");
                Builder.append(m_Name);
            }

            if (!m_Hyperlink.isEmpty()) {
                Builder.append('\n');
                Builder.append(m_Hyperlink);
            }

            Builder.append(')');
            return Builder.toString();
        }
    }

    private String m_User;
    private String m_ProjectName;
    private EAction m_Action = EAction.Change;
    private String m_FieldName = "";
    private boolean m_bUseOldValue = false;
    private String m_NewValue;
    private String m_OldValue;
    private Task m_Task;

    public void setAction(EAction _Action) {
        this.m_Action = _Action;
    }

    public void setUser(String _User) {
        this.m_User = _User;
    }

    public void setProjectName(String _ProjectName) {
        this.m_ProjectName = _ProjectName;
    }

    public void setFieldName(String _FieldName) {
        m_FieldName = _FieldName;
    }

    public void setUseOldValue(boolean _bUseOldValue) {
        this.m_bUseOldValue = _bUseOldValue;
    }

    public void setNewValue(String _NewValue) {
        this.m_NewValue = _NewValue;
    }

    public void setOldValue(String _OldValue) {
        this.m_OldValue = _OldValue;
    }


    public void setTask(Task _Task) {
        this.m_Task = _Task;
    }

    public String toHTML() {
        StringBuilder Builder = new StringBuilder();
        Builder.append("<i>");
        Builder.append(m_ProjectName);
        Builder.append("</i>\n");

        Builder.append("<b>");
        Builder.append(m_User);
        Builder.append("</b>");

        Builder.append(' ');
        Builder.append(ActionToString(m_Action));
        Builder.append(' ');
        Builder.append(m_FieldName);
        if (!m_FieldName.isEmpty())
            Builder.append(' ');
        Builder.append("on\n");

        Builder.append("<b>");
        Builder.append(m_Task.toString());
        Builder.append("</b>\n");

        if (m_bUseOldValue) {
            Builder.append(' ');
            Builder.append(ActionToOldValuePreffix(m_Action));
            Builder.append("<b>");
            Builder.append(m_OldValue);
            Builder.append("</b>\n");
        }

        Builder.append(' ');
        Builder.append(ActionToNewValuePreffix(m_Action));
        Builder.append("<b>");
        Builder.append(m_NewValue);
        Builder.append("</b>");
        return Builder.toString();
    }

    private static String ActionToString(EAction _Action) {
        switch (_Action) {
            case Change:
                return "changed";
            case CommentChange:
                return "edited comment";
            case Create:
                return "created";
            case Delete:
                return "deleted";
            case Comment:
                return "commented";
            default:
                return "";
        }
    }

    private static String ActionToNewValuePreffix(EAction _Action) {
        switch (_Action) {
            case Change:
            case CommentChange:
            case Create:
            case Delete:
                return "new value is ";
            default:
                return "";
        }
    }

    private static String ActionToOldValuePreffix(EAction _Action) {
        switch (_Action) {
            case Change:
            case CommentChange:
            case Create:
            case Delete:
                return "old value is ";
            default:
                return "";
        }
    }
}
