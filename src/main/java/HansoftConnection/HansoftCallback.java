package HansoftConnection;

import se.hansoft.hpmsdk.*;

class HansoftCallback extends HPMSdkCallbacks {
    protected HansoftThread m_Program;

    HansoftCallback(HansoftThread _Program) {
        m_Program = _Program;
    }

     @Override
    public void On_ProcessError(EHPMError _Error)
    {
        System.out.println(HPMSdkSession.ErrorAsStr(_Error) + "\r\n");
        m_Program.m_bBrokenConnection = true;
    }

    @Override
    public void On_TaskChange(HPMChangeCallbackData_TaskChange _Data)
    {
        if (!IsAcceptedFiled(_Data.m_FieldChanged))
            return;

        try {

            if (!m_Program.getSession().TaskGetFullyCreated(_Data.m_TaskID))
                return;

            // #TODO: Refactor this
            StringBuilder Builder = new StringBuilder();
            Builder.append("<b>");
            Builder.append(GetUserName(_Data.m_ChangedByResourceID));
            Builder.append("</b>");
            Builder.append(' ');
            Builder.append(GetAction(_Data.m_FieldChanged));
            Builder.append("<b>");
            Builder.append(GetTaskText(_Data.m_TaskID, IsShowTaskName(_Data.m_FieldChanged)));
            Builder.append("</b>");
            Builder.append(' ');
            Builder.append(GetValuePrefix(_Data.m_FieldChanged));
            Builder.append("<b>");
            Builder.append(GetTaskData(_Data.m_TaskID, _Data.m_FieldChanged));
            Builder.append("</b>");

            m_Program.onNewsFeed(Builder.toString());

        } catch (HPMSdkException _Error) {
            System.out.println("HPMSdkException in On_TaskChange: " + _Error.ErrorAsStr());
        } catch (HPMSdkJavaException _Error) {
            System.out.println("HPMSdkJavaException in On_TaskChange: " + _Error.ErrorAsStr());
        }
    }

    private boolean IsAcceptedFiled(EHPMTaskField _Field)
    {
        switch(_Field)
        {
            case Status:
            case WorkflowStatus:
            //case Comment:
            case BacklogPriority:
            case SprintPriority:
            case Description:
                return true;
            default:
                return false;

        }
    }

    private boolean IsShowTaskName(EHPMTaskField _Field) {
        switch (_Field) {
            case Description:
            case Comment:
                return false;
            default:
                return true;
        }
    }

    private String GetUserName(HPMUniqueID _UsedID) throws HPMSdkException, HPMSdkJavaException
    {
        HPMResourceProperties UserInfo = m_Program.getSession().ResourceGetProperties(_UsedID);
        return UserInfo.m_Name;

    }

    private String GetAction(EHPMTaskField _Field) throws HPMSdkException, HPMSdkJavaException {
        if (_Field == EHPMTaskField.Comment)
            return "commented on ";

        return "changed " + GetFieldName(_Field) + " on ";
    }

    private String GetValuePrefix(EHPMTaskField _Field) {
        if (_Field == EHPMTaskField.Comment)
            return " ";

        return "new value is ";
    }

    private String GetFieldName(EHPMTaskField _Field) throws HPMSdkException, HPMSdkJavaException {
        switch(_Field) {
            default: {
                HPMTaskField TaskField = new HPMTaskField();
                TaskField.m_FieldID = _Field;

                HPMColumn Column = m_Program.getSession().UtilTaskFieldToColumn(TaskField);
                EHPMProjectDefaultColumn DefaultColumn  = EHPMProjectDefaultColumn.values()[Column.m_ColumnID];
                HPMUntranslatedString Untranslated = m_Program.getSession().UtilGetColumnName(DefaultColumn);
                return m_Program.getSession().LocalizationTranslateString(m_Program.getSession().LocalizationGetDefaultLanguage(), Untranslated);
            }
        }
    }

    private String GetTaskData(HPMUniqueID _TaskID, EHPMTaskField _Field) throws HPMSdkException, HPMSdkJavaException {

        HPMUniqueID ProjectID = m_Program.getSession().TaskGetContainer(_TaskID);

        switch(_Field)
        {
            case Status:
            {
                EHPMTaskStatus Status = m_Program.getSession().TaskGetStatus(_TaskID);
                HPMUntranslatedString Untranslated = m_Program.getSession().UtilGetColumnDataItemFormatted(ProjectID, EHPMProjectDefaultColumn.ItemStatus, Status.getValue());
                return m_Program.getSession().LocalizationTranslateString(m_Program.getSession().LocalizationGetDefaultLanguage(), Untranslated);
            }
            case WorkflowStatus:
            {
                // #TODO: Cache?
                int WorflowID = m_Program.getSession().TaskGetWorkflow(_TaskID);
                int WorkflowStatus = m_Program.getSession().TaskGetWorkflowStatus(_TaskID);
                HPMUniqueID RealProjectID = m_Program.getSession().UtilGetRealProjectIDFromProjectID(ProjectID);
                HPMProjectWorkflowSettings Settings = m_Program.getSession().ProjectWorkflowGetSettings(RealProjectID, WorflowID);

                if (Settings.m_Properties.m_WorkflowType != EHPMWorkflowType.Workflow)
                    return "Unknown";

                for (HPMProjectWorkflowObject WfObj : Settings.m_WorkflowObjects)
                {
                    if (WfObj.m_ObjectType == EHPMProjectWorkflowObjectType.WorkflowStatus && WfObj.m_ObjectID == WorkflowStatus)
                        return m_Program.getSession().LocalizationTranslateString(m_Program.getSession().LocalizationGetDefaultLanguage(), WfObj.m_WorkflowStatus_Name);
                }

                return "Unknown";
            }
            case Comment:
                return "";
            case BacklogPriority:
            {
                EHPMTaskAgilePriorityCategory Priority = m_Program.getSession().TaskGetBacklogPriority(_TaskID);
                HPMUntranslatedString Untranslated = m_Program.getSession().UtilGetColumnDataItemFormatted(ProjectID, EHPMProjectDefaultColumn.BacklogPriority, Priority.getValue());
                return m_Program.getSession().LocalizationTranslateString(m_Program.getSession().LocalizationGetDefaultLanguage(), Untranslated);
            }
            case SprintPriority:
            {
                EHPMTaskAgilePriorityCategory Priority = m_Program.getSession().TaskGetSprintPriority(_TaskID);
                HPMUntranslatedString Untranslated = m_Program.getSession().UtilGetColumnDataItemFormatted(ProjectID, EHPMProjectDefaultColumn.SprintPriority, Priority.getValue());
                return m_Program.getSession().LocalizationTranslateString(m_Program.getSession().LocalizationGetDefaultLanguage(), Untranslated);
            }
            case Description:
                return m_Program.getSession().TaskGetDescription(_TaskID);
            default:
                return "";

        }
    }

    private String GetTaskText(HPMUniqueID _TaskID, boolean _bShowName) throws HPMSdkException, HPMSdkJavaException {
        HPMUniqueID ProjectID = m_Program.getSession().TaskGetContainer(_TaskID);
        HPMProjectProperties Properties = m_Program.getSession().ProjectGetProperties(ProjectID);
        int TaskUSerID = m_Program.getSession().TaskGetID(_TaskID);

        StringBuilder Builder = new StringBuilder();
        Builder.append("Task (ID: ");
        Builder.append(TaskUSerID);
        Builder.append(", Project: ");
        Builder.append(Properties.m_Name);
        if (_bShowName)
        {
            String Description = m_Program.getSession().TaskGetDescription(_TaskID);
            Builder.append(", Name: ");
            Builder.append(Description);
        }

        Builder.append(')');
        return Builder.toString();
    }
}
