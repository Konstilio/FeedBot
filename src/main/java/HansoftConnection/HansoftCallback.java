package HansoftConnection;

import se.hansoft.hpmsdk.*;

class HansoftCallback extends HPMSdkCallbacks {
    HansoftThread m_Program;
    HPMSdkSession m_Session;

    HansoftCallback(HansoftThread _Program, HPMSdkSession _Session) {
        m_Program = _Program;
        m_Session = _Session;
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

            if (!m_Session.TaskGetFullyCreated(_Data.m_TaskID))
                return;

            HPMUniqueID ProjectID = m_Session.TaskGetContainer(_Data.m_TaskID);

            String UserName = GetUserName(_Data.m_ChangedByResourceID);

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
            case Comment:
            case BacklogPriority:
            case SprintPriority:
            case Description:
                return true;
            default:
                return false;

        }
    }

    private String GetUserName(HPMUniqueID _UsedID) throws HPMSdkException, HPMSdkJavaException
    {
        HPMResourceProperties UserInfo = m_Session.ResourceGetProperties(_UsedID);
        return UserInfo.m_Name;

    }

    private String GetFieldName(EHPMTaskField _Field) throws HPMSdkException, HPMSdkJavaException {
        switch(_Field) {
            default: {
                HPMTaskField TaskField = new HPMTaskField();
                TaskField.m_FieldID = _Field;

                HPMColumn Column = m_Session.UtilTaskFieldToColumn(TaskField);
                HPMUntranslatedString Untranslated = m_Session.UtilGetColumnName(EHPMProjectDefaultColumn.Status);
                return m_Session.LocalizationTranslateString(m_Session.LocalizationGetDefaultLanguage(), Untranslated);
            }
        }
    }

    private String GetTaskData(HPMUniqueID _TaskID, EHPMTaskField _Field) throws HPMSdkException, HPMSdkJavaException {
        switch(_Field)
        {
            case Status:
            {
                EHPMTaskStatus Status = m_Session.TaskGetStatus(_TaskID);
                switch (Status)
                {
                    case NoStatus:
                        return "";
                    case Blocked:
                        return "Blocked";
                    case NotDone:
                        return "Not Done";
                    case Deleted:
                        return "To be deleted";
                    case Completed:
                        return "Completed";
                    case InProgress:
                        return "In Progress";
                    case NewVersionOfSDKRequired:
                    {
                        System.out.println("GetTaskData EHPMTaskStatus: new Version of SDK required");
                        return "";
                    }
                    default:
                        return "";
                }
            }
            case WorkflowStatus:
            case Comment:
            case BacklogPriority:
            case SprintPriority:
            case Description:
                return "";
            default:
                return "";

        }
    }
}
