package HansoftConnection;

import se.hansoft.hpmsdk.*;

class HansoftCallback extends HPMSdkCallbacks {
    protected HansoftThread m_Program;
    protected HansoftCommentsCache m_CommentsCache = new HansoftCommentsCache();
    private boolean m_bSyncComments = true;

    HansoftCallback(HansoftThread _Program) {
        m_Program = _Program;
    }

    // Called from HansoftThreadLoop
    public void Update() throws HPMSdkException, HPMSdkJavaException {

        if (!m_bSyncComments)
            return;

        HPMProjectEnum Projects = m_Program.getSession().ProjectEnum();
        for(HPMUniqueID ProjectID : Projects.m_Projects) {
            HPMTaskEnum Tasks = m_Program.getSession().TaskEnum(ProjectID);
            for(HPMUniqueID TaskID : Tasks.m_Tasks) {
                HPMTaskCommentEnum Comments = m_Program.getSession().TaskEnumComments(TaskID);
                for (int PostID: Comments.m_Comments) {
                    String Comment = m_Program.getSession().TaskGetComment(TaskID, PostID).m_MessageText;
                    m_CommentsCache.SetComment(TaskID, PostID, Comment);
                }
            }
        }
        m_bSyncComments = false;
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

            HansoftAction.Task Task = new HansoftAction.Task();
            Task.m_ID = m_Program.getSession().TaskGetID(_Data.m_TaskID);
            Task.m_Name = m_Program.getSession().TaskGetDescription(_Data.m_TaskID);
            String URLSuffix = "Task/" + _Data.m_TaskID.toString();
            Task.m_Hyperlink = m_Program.getSession().UtilGetHansoftURL(URLSuffix);
            Task.m_bShowName = IsShowTaskName(_Data.m_FieldChanged);

            HansoftAction Action = new HansoftAction();
            Action.setTask(Task);
            Action.setUser(GetUserName(_Data.m_ChangedByResourceID));
            Action.setAction(HansoftAction.EAction.Change);

            HPMUniqueID ProjectID = m_Program.getSession().TaskGetContainer(_Data.m_TaskID);
            HPMUniqueID RealProjectID = m_Program.getSession().UtilGetRealProjectIDFromProjectID(ProjectID);
            HPMProjectProperties Properties = m_Program.getSession().ProjectGetProperties(RealProjectID);
            Action.setProjectName(Properties.m_Name);
            Action.setFieldName(GetFieldName(_Data.m_FieldChanged));
            Action.setNewValue(GetTaskData(_Data.m_TaskID, _Data.m_FieldChanged));

            m_Program.onNewsFeed(Action);

        } catch (HPMSdkException _Error) {
            System.out.println("HPMSdkException in On_TaskChange: " + _Error.ErrorAsStr());
        } catch (HPMSdkJavaException _Error) {
            System.out.println("HPMSdkJavaException in On_TaskChange: " + _Error.ErrorAsStr());
        }
    }

    @Override
    public void On_TaskCommentPosted(HPMChangeCallbackData_TaskCommentPosted _Data) {

        try {

            if (!m_Program.getSession().TaskGetFullyCreated(_Data.m_TaskID))
                return;

            HansoftAction.Task Task = new HansoftAction.Task();
            Task.m_ID = m_Program.getSession().TaskGetID(_Data.m_TaskID);
            Task.m_Name = m_Program.getSession().TaskGetDescription(_Data.m_TaskID);
            String URLSuffix = "Task/" + _Data.m_TaskID.toString();
            Task.m_Hyperlink = m_Program.getSession().UtilGetHansoftURL(URLSuffix);
            Task.m_bShowName = true;

            HansoftAction Action = new HansoftAction();
            Action.setTask(Task);
            Action.setUser(GetUserName(_Data.m_ChangedByResourceID));

            String OldComment = m_CommentsCache.GetComment(_Data.m_TaskID, _Data.m_PostID);

            if (OldComment != null) {
                Action.setAction(HansoftAction.EAction.CommentChange);
                Action.setOldValue(OldComment);
                Action.setUseOldValue(true);
            } else {
                Action.setAction(HansoftAction.EAction.Comment);
            }

            HPMUniqueID ProjectID = m_Program.getSession().TaskGetContainer(_Data.m_TaskID);
            HPMUniqueID RealProjectID = m_Program.getSession().UtilGetRealProjectIDFromProjectID(ProjectID);
            HPMProjectProperties Properties = m_Program.getSession().ProjectGetProperties(RealProjectID);
            Action.setProjectName(Properties.m_Name);

            String NewComment = m_Program.getSession().TaskGetComment(_Data.m_TaskID, _Data.m_PostID).m_MessageText;
            m_CommentsCache.SetComment(_Data.m_TaskID, _Data.m_PostID, NewComment);
            Action.setNewValue(NewComment);

            m_Program.onNewsFeed(Action);

        } catch (HPMSdkException _Error) {
            System.out.println("HPMSdkException in On_TaskCommentPosted: " + _Error.ErrorAsStr());
        } catch (HPMSdkJavaException _Error) {
            System.out.println("HPMSdkJavaException in On_TaskCommentPosted: " + _Error.ErrorAsStr());
        }
    }

    @Override
    public void On_TaskDeleteComment(HPMChangeCallbackData_TaskDeleteComment _Data) {
        // #TODO: Maybe create action
        m_CommentsCache.DeleteComment(_Data.m_TaskID, _Data.m_PostID);
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
}
