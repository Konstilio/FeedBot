package HansoftConnection;

import Formatting.HTMLFormatter;
import Formatting.ITextFormatter;
import Formatting.SlackFormatter;
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

        public String Format(ITextFormatter _Formatter) {
            StringBuilder Builder = new StringBuilder();

            if (_Formatter.SupportsHyperlinking() && !m_Hyperlink.isEmpty())
            {
                Builder.append(_Formatter.OpenTag());
                Builder.append(m_Hyperlink);
                Builder.append(_Formatter.HyperlinkSeparator());
            }

            Builder.append("Task (ID: ");
            Builder.append(m_ID);
            if (m_bShowName && !m_Name.isEmpty())
            {
                Builder.append(", Name: ");
                Builder.append(m_Name);
            }

            if (!_Formatter.SupportsHyperlinking() && !m_Hyperlink.isEmpty()) {
                Builder.append('\n');
                Builder.append(m_Hyperlink);
            }

            Builder.append(')');

            if (_Formatter.SupportsHyperlinking() && !m_Hyperlink.isEmpty())
            {
                Builder.append(_Formatter.CloseTag());
            }

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
        return Format(new HTMLFormatter());
    }

    public String toSlackMessage() {
        return Format(new SlackFormatter());
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

    private String Format(ITextFormatter _Formatter) {
        StringBuilder Builder = new StringBuilder();
        Builder.append(_Formatter.MakeItalic(m_ProjectName));
        Builder.append(_Formatter.NewLine());

        Builder.append(_Formatter.MakeBold(m_User));

        Builder.append(' ');
        Builder.append(ActionToString(m_Action));
        Builder.append(' ');
        Builder.append(m_FieldName);
        if (!m_FieldName.isEmpty())
            Builder.append(' ');
        Builder.append("on");
        Builder.append(_Formatter.NewLine());

        Builder.append(_Formatter.MakeBold(m_Task.Format(_Formatter)));
        Builder.append(_Formatter.NewLine());

        if (m_bUseOldValue) {
            Builder.append(' ');
            Builder.append(ActionToOldValuePreffix(m_Action));
            Builder.append(_Formatter.MakeBold(m_OldValue));
            Builder.append(_Formatter.NewLine());
        }

        Builder.append(' ');
        Builder.append(ActionToNewValuePreffix(m_Action));
        Builder.append(_Formatter.MakeBold(m_NewValue));
        return Builder.toString();
    }
}
