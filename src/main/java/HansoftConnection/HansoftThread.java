package HansoftConnection;

import se.hansoft.hpmsdk.EHPMDataHistoryClientOrigin;
import se.hansoft.hpmsdk.EHPMError;
import se.hansoft.hpmsdk.EHPMSdkDebugMode;
import se.hansoft.hpmsdk.HPMChangeCallbackData_TaskCreateUnified;
import se.hansoft.hpmsdk.HPMProjectEnum;
import se.hansoft.hpmsdk.HPMProjectProperties;
import se.hansoft.hpmsdk.HPMResourceEnum;
import se.hansoft.hpmsdk.HPMResourceProperties;
import se.hansoft.hpmsdk.HPMSdkCallbacks;
import se.hansoft.hpmsdk.HPMSdkException;
import se.hansoft.hpmsdk.HPMSdkJavaException;
import se.hansoft.hpmsdk.HPMSdkSession;
import se.hansoft.hpmsdk.HPMTaskCreateUnified;
import se.hansoft.hpmsdk.HPMTaskCreateUnifiedEntry;
import se.hansoft.hpmsdk.HPMTaskCreateUnifiedReference;
import se.hansoft.hpmsdk.HPMTaskEnum;
import se.hansoft.hpmsdk.HPMTaskTimeZones;
import se.hansoft.hpmsdk.HPMTaskTimeZonesZone;
import se.hansoft.hpmsdk.HPMUniqueID;

public class HansoftThread extends Thread {

    HPMSdkSession m_Session;
    long m_NextUpdate;
    long m_NextConnectionAttempt;
    HansoftCallback m_Callback;
    boolean m_bBrokenConnection;

    String m_Host;
    Integer m_Port;
    String m_Database;
    String m_SDK;
    String m_SDKPassword;

    public HansoftThread(String _Host, Integer _Port, String _Database, String _SDK, String _Password)
    {
        m_Session = null;
        m_Callback = new HansoftCallback(this, m_Session);
        m_Callback.m_Program = this;
        m_NextUpdate = 0;
        m_NextConnectionAttempt = 0;
        m_bBrokenConnection = false;

        m_Host = _Host;
        m_Port = _Port;
        m_Database = _Database;
        m_SDK = _SDK;
        m_SDKPassword = _Password;
    }

    boolean initConnection()
    {
        if (m_Session != null)
            return true;

        long currentTime = System.currentTimeMillis();
        if (currentTime > m_NextConnectionAttempt)
        {
            m_NextConnectionAttempt = 0;

            EHPMSdkDebugMode debugMode = EHPMSdkDebugMode.Off; // Change to EHPMSdkDebugMode.Debug to get memory leak info and debug output. Note that this is expensive.

            try
            {
                // You should change these parameters to match your development server and the SDK account you have created. For more information see SDK documentation.
                m_Session = HPMSdkSession.SessionOpen(m_Host, m_Port, m_Database, m_SDK, m_SDKPassword, m_Callback
                        , null, true, debugMode, 0, "", "./HansoftSDK/Win", null);
            }
            catch (HPMSdkException _Error)
            {
                System.out.println("SessionOpen 1 failed with error: " + _Error.ErrorAsStr() + "\r\n");
                return false;
            }
            catch (HPMSdkJavaException _Error)
            {
                System.out.println("SessionOpen 2 failed with error: " + _Error.ErrorAsStr() + "\r\n");
                return false;
            }

            System.out.println("Successfully opened session.\r\n");
            m_bBrokenConnection = false;

            return true;
        }
        return false;
    }

    void update()
    {
        if (initConnection())
        {
            try
            {
                if (m_bBrokenConnection)
                    return;
                else
                {
                    // Check our stuff
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > m_NextUpdate)
                    {
                        // Find administrator resource

                        HPMResourceEnum Resources = m_Session.ResourceEnum();

                        HPMUniqueID AdminResourceUID = new HPMUniqueID();
                        String ResourceToFind = "Administrator";
                        for (HPMUniqueID ResourceUID : Resources.m_Resources)
                        {
                            if (AdminResourceUID.IsValid())
                                break;

                            HPMResourceProperties ResourceInfo = m_Session.ResourceGetProperties(ResourceUID);

                            if (ResourceInfo.m_Name.equals(ResourceToFind))
                            {
                                AdminResourceUID = ResourceUID;
                            }
                        }

                        if (AdminResourceUID.IsValid())
                        {
                            // Enumerate projects
                            HPMProjectEnum Projects = m_Session.ProjectEnum();
                            // Loop through projects
                            for (HPMUniqueID ProjectUID : Projects.m_Projects)
                            {
                                // Enumerate tasks
                                HPMTaskEnum Tasks = m_Session.TaskEnum(ProjectUID);
                                HPMProjectProperties ProjectProp = m_Session.ProjectGetProperties(ProjectUID);

                                HPMUniqueID OurTaskID = new HPMUniqueID();
                                String OurTaskDesc = "HPM SDK Simple Sample Task";
                                for (HPMUniqueID Task : Tasks.m_Tasks)
                                {
                                    if (OurTaskID.IsValid())
                                        break;
                                    String Description = m_Session.TaskGetDescription(Task);
                                    if (Description.equals(OurTaskDesc))
                                    {
                                        OurTaskID = Task;
                                    }
                                }

                                // Impersonate resource so it looks like this resource made the changes.
                                // The string in the third argument will be shown in the "Change originates from" column in the change history
                                m_Session.ResourceImpersonate(AdminResourceUID, EHPMDataHistoryClientOrigin.CustomSDK, m_Session.LocalizationCreateUntranslatedStringFromString("Task updated from Sample_SimpleJava"));

                                if (!OurTaskID.IsValid())
                                {
                                    // No old task was found, create a new one
                                    HPMTaskCreateUnified CreateData = new HPMTaskCreateUnified();
                                    HPMTaskCreateUnifiedEntry Entry = new HPMTaskCreateUnifiedEntry();

                                    // Set previous to -1 to make it the top task.
                                    HPMTaskCreateUnifiedReference PrevRefID = new HPMTaskCreateUnifiedReference();
                                    PrevRefID.m_RefID = new HPMUniqueID();
                                    HPMTaskCreateUnifiedReference PrevWorkPrioRefID = new HPMTaskCreateUnifiedReference();
                                    PrevWorkPrioRefID.m_RefID = new HPMUniqueID(-2);

                                    Entry.m_LocalID = new HPMUniqueID(1);
                                    Entry.m_PreviousRefID = PrevRefID;
                                    Entry.m_PreviousWorkPrioRefID = PrevWorkPrioRefID;
                                    CreateData.m_Tasks.add(Entry);

                                    HPMChangeCallbackData_TaskCreateUnified TaskCreateReturn = m_Session.TaskCreateUnifiedBlock(ProjectUID, CreateData);

                                    if (TaskCreateReturn.m_Tasks.size() == 1)
                                    {
                                        // The returned is a task ref in the project container. We need the task id not the reference id.
                                        HPMUniqueID OurTaskRefID = TaskCreateReturn.m_Tasks.get(0).m_TaskRefID;
                                        OurTaskID = m_Session.TaskRefGetTask(OurTaskRefID);
                                        m_Session.TaskSetDescription(OurTaskID, OurTaskDesc);
                                        // When we set fully created the task becomes visible to users.
                                        m_Session.TaskSetFullyCreated(OurTaskID);
                                        System.out.println("Successfully created task for project: " + ProjectProp.m_Name + "\r\n");
                                    }
                                    else
                                        System.out.println("The wrong number of tasks were created, aborting\r\n");
                                }

                                if (OurTaskID.IsValid())
                                {
                                    // Set to todays date
                                    HPMTaskTimeZones Zones = new HPMTaskTimeZones();
                                    HPMTaskTimeZonesZone Zone = new HPMTaskTimeZonesZone();
                                    long TruncTimeSeconds = currentTime / 1000;
                                    TruncTimeSeconds = TruncTimeSeconds * 1000000;
                                    Zone.m_Start = TruncTimeSeconds; // We must align the time on whole days
                                    Zone.m_End = Zone.m_Start; // When the end is the same as the start the task is one day long.
                                    Zones.m_Zones.add(Zone);
                                    m_Session.TaskSetTimeZones(OurTaskID, Zones, false);
                                    System.out.println("Successfully updated task for project: " + ProjectProp.m_Name + "\r\n");
                                }
                            }
                        }
                        else
                            System.out.println("No administrator user was found, aborting.\r\n");

                        m_NextUpdate = currentTime + 10000; // Check every 10 seconds
                    }
                }
            }
            catch (HPMSdkException _Error)
            {
                System.out.println("Exception in processing loop: " + _Error.ErrorAsStr() + "\r\n");
            }
            catch (HPMSdkJavaException _Error)
            {
                System.out.println("Exception in processing loop: " + _Error.ErrorAsStr() + "\r\n");
            }
        }
    }

    public void onNewsFeed(String message) {

    }

    public void run()
    {
        while (!Thread.interrupted())
        {
            update();
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                System.out.print("Hansoft thread interupted");
                return;
            }
        }
    }
}
