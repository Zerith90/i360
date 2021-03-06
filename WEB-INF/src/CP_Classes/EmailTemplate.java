package CP_Classes;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import javax.mail.internet.MimeUtility;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voGroup;

/*
* Change Log
* ==========
*
* Date        By                Method(s)                         Change(s) 
* =====================================================================================================
* 19/09/11    Gwen Oh          	fillNomTemplate(),	              Replace "&lt;XXX&gt;" instead of replacing <XXX>
* 			   					fillTestTemplate(),
* 								fillPartTemplate()
* 13/07/2012	Liu Taichen		getEmailTemplate(int, int)		  enabled retrieving of send Individual Report email template					
*
* 13/07/2012	Liu Taichen		getEmailSubject(int, int)		  enabled retrieving of send Individual Report email template
*
* 13/07/2012	Liu Taichen		editTemplate(int, int, String, String)   enabled editing of send Individual Report email template
*
* 13/07/2012    Liu Taichen     fillEmailIndividualReport(int, int, int, String)    create this method to replace information in the emial content
*/
/**
 * Edited By Roger 13 June 2008
 * Change to include organization Id in sql insert
 */
public class EmailTemplate implements Serializable
{
	private String content;
	private Setting server = new Setting();
	private Database db = new Database();
	private Organization org;
	//private User UserX = new User();
	Create_Edit_Survey CE_Survey = new Create_Edit_Survey();
	
	/** 
	 *Add entry to tbl email
	 */
	/* Edited by xuehai, 23 Jun 2011. Add a new function with additional parameter:String Log. */
	public boolean addTotblEmail(String SenderEmail, String ReceiverEmail, String Header, String Content, int orgId) throws SQLException, Exception 
	{	
		return this.addTotblEmail(SenderEmail, ReceiverEmail, Header, Content, null, orgId);
	}
	/** 
	 *Add entry to tbl email
	 */
	/* Edited by xuehai, 23 Jun 2011. Add a new function with additional parameter:String Log. */
	public boolean addTotblEmail(String SenderEmail, String ReceiverEmail, String Header, String Content, String Log, int orgId) throws SQLException, Exception 
	{	
		//Edited by Roger 13 June 2008
		String command ="INSERT INTO tblEmail(SenderEmail, ReceiverEmail, Header, Content, Log, FKOrganization) VALUES('"+SenderEmail+"','"+ReceiverEmail+"','"+Header+"','"+Content+"', '"+ Log +"', "+orgId+")";
		
		Connection con = null;
		Statement st = null;


		boolean bIsAdded = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(command);
			if(iSuccess!=0)
			bIsAdded=true;

		}
		catch(Exception E)
		{
            System.err.println("EmailTemplate.java - addTotblEmail - " + E);
		}
		finally
        {

			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

        }
		
		return bIsAdded;
	}

	/** 
	 *	Subject for Delete entry to tbl email
	 */
	public boolean delFromtblEmail(int EmailID) throws SQLException, Exception 
	{
		
		String sql = "DELETE FROM tblEmail WHERE EmailID= " + EmailID;
       
       	Connection con = null;
		Statement st = null;
	
		boolean bIsDeleted = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(sql);
			if(iSuccess!=0)
			bIsDeleted=true;
	
		}
		catch(Exception E)
		{
	       System.err.println("EmailTemplate.java - delFromtblEmail - " + E);
		}
		finally
		{
	
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
	
		}
		
		return bIsDeleted;
	}
	
	/** 
	 *	Delete entry to tbl email
	 */
	public String ForgotPass_temp(String nama, String password)
	{
		content = "<br /><br />Dear "+nama+",<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Your password: "+password+"";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "You may login as usual with the above password. ";
		content = content + "This is a system generated email. No reply is needed. ";
		content = content + "For any enquiries, please contact "+server.getEmail_Hyperlink()+"";
		content = content +"<br />";
		content = content +"<br />";
		content = content + server.getServerName() + "<br />";
		content = content + "Pacific Century Consulting Pte Ltd";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Optimal Performance through People Technology";
		content = content + "<br />www.pcc.com.sg";
		
		return content;
	}
	
	/** 
	 *	Subject for 1 day before Date opened email send to admin to set the survey status to 'Open'
	 */
	public String Survey_Open_Subject()
	{
		content = "REMINDER TO ADMINISTRATOR TO OPEN SURVEY";
		return content;
	}
	
	/** 
	 *	1 day before Date opened email send to admin to set the survey status to 'Open'
	 *
	 *	@param nama - Name of the email receiver
	 *	@param OpenedDate - Survey Opened Date
	 *	@param OrgName - Organisation name
	 *	@param JobPosition - Job Position
	 *	@param SurveyName - Survey Name
	 *	@param SurveyStatus - Survey Status
	 *	@return content
	 */
	public String Survey_Open(String nama, String OpenedDate, String OrgName, String JobPosition, String SurveyName, String SurveyStatus)
	{
		content = "<br /><br />Dear "+nama+",<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "We noted that you have created a survey with Not Commissioned or Closed status and it is due to open. ";
		content = content + "Please set the survey status to Open. Below are the survey details:";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "\t	锟絅ame: "+SurveyName+"<br />"; 
		content = content + "\t	锟絁ob Position: "+JobPosition+"<br />"; 
		content = content + "\t	锟絆rganisation: "+OrgName+"<br />";
		content = content + "\t	锟絆pened Date: "+OpenedDate+"<br />";
		content = content + "\t	锟絊tatus: "+SurveyStatus+"<br />";
		content = content +"<br />";
		content = content +"<br />";
		
		content = content + Footer();
		
		if(server.getCompanySetting() == 3)
		{
			/* TOYOTA SETTING */
			content = "<br /><br />Dear "+nama+",<br />";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "We noted that you have created a survey with Not Commissioned or Closed status and it is due to open. ";
			content = content + "Please set the survey status to 'Open'. Below are the survey details:";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Name: "+SurveyName+"<br />"; 
			content = content + "Job Position: "+JobPosition+"<br />"; 
			content = content + "Organisation: "+OrgName+"<br />";
			content = content + "Opened Date: "+OpenedDate+"<br />";
			content = content + "Status: "+SurveyStatus+"<br />";
			content = content +"<br />";
			content = content +"<br />";
			
			content = content + Footer();
			/* END TOYOTA SETTING */
		}
		
		return content;
	}
	
	/**
	 * Subject for Upon kick off survey email will be sent to the targets and raters.
	 */
	public String Survey_Open_Participant_Subject(String Deadline)
	{
		// Change Email Subject Field to generic one, Desmond 09 Oct 09
		content = "NOTIFICATION TO RATERS TO COMPLETE 3-SIXTY FEEDBACK QUESTIONNAIRES";
		//content = "Launch of Multi-Source Feedback for participants of India CDC";
		
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
			// Change Email Subject Field to generic one, Desmond 09 Oct 09
			content = "NOTIFICATION TO RATERS TO COMPLETE 3-SIXTY QUESTIONNAIRES ("+Deadline+")";
		/* END TOYOTA SETTING */
		else if (server.getCompanySetting() == 1)
			// Change Email Subject Field to generic one, Desmond 09 Oct 09
			content = "NOTIFICATION TO RATERS TO COMPLETE 3-SIXTY FEEDBACK QUESTIONNAIRES";
			//content = "Launch of Multi-Source Feedback for participants of India CDC";
		
		return content;
	}
	
	/**
	 * TMT need to customise the subject to show the job position name in the email subject
	 * @param Deadline
	 * @param SurveyName
	 * @return
	 */
	public String Survey_Open_Participant_Subject(String Deadline, String sPosition)
	{
		// Change Email Subject Field to generic one, Desmond 09 Oct 09
		content = "NOTIFICATION TO RATERS TO COMPLETE 3-SIXTY FEEDBACK QUESTIONNAIRES";
		//content = "Launch of Multi-Source Feedback for participants of India CDC";
		
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
			// Change Email Subject Field to generic one, Desmond 09 Oct 09
			content = "NOTIFICATION TO RATERS TO COMPLETE 3-SIXTY QUESTIONNAIRES ("+Deadline+") for Targets in " +sPosition;
		/* END TOYOTA SETTING */
		else if (server.getCompanySetting() == 1)
			// Change Email Subject Field to generic one, Desmond 09 Oct 09
			content = "NOTIFICATION TO RATERS TO COMPLETE 3-SIXTY FEEDBACK QUESTIONNAIRES";
			//content = "Launch of Multi-Source Feedback for participants of India CDC";
		
		return content;
	}
	
	/**
	 * Email Subject Upon kick off of survey (will be sent to SELF only)
	 * @param Deadline
	 * @return Subject
	 */
	public String Survey_Open_Self_Subject(String Deadline)
	{
		content = "Launch of Multi-Source Feedback for India CDC (self-assessment)";
		
		return content;
	}
	
	/**
	 * 	Upon kick off survey email will be sent to the targets and raters.
	 *
	 *	@param RaterName - Rater Name
	 *	@param Deadline - Survey Deadline
	 * 	@param AssignmentID - Assignment ID
	 *	@return content
	 */ 
	public String Survey_Open_Participant(String RaterName, String Deadline, int AssignmentID)throws SQLException, Exception
	{
		int counter = 1;
				
		content = "<br /><br />Dear "+ RaterName +",<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "You have been chosen to participate in the 3-Sixty Profiler Exercise as a Rater. You can ";
		content = content + "now start to complete the Questionnaire(s) for whom you have been assigned to provide ";
		content = content + "feedback. Below is the list of Target(s) assigned to you:";
		content = content +"<br />";
		content = content +"<br />";
			
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
		{
			content = "<br /><br />Dear "+ RaterName +",<br />";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"You are invited to provide feedback to colleagues whom their superiors have nominated ";
			content = content +"you to be the raters.";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"Please complete the 3-Sixty Questionnaire(s) for each of your colleague by " + Deadline + " :<br />";
		}
		/* END TOYOTA SETTING */
		
		String command = "SELECT * FROM tblAssignment WHERE AssignmentID = "+AssignmentID;
		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        	
        	while(rs.next())
    		{
    			String [] TargetDetail = new String[14];
    			int TargetID = rs.getInt("TargetLoginID");

    			TargetDetail = CE_Survey.getUserDetail_ADV(TargetID);
    			String fullname = TargetDetail[0]+" "+TargetDetail[1];
    			
    			//content = content + "\t"+counter+". "+fullname+"<br />";
    			
    			/* TOYOTA SETTING */
    			if(server.getCompanySetting() == 1)
    			{
    				content = content + "\t"+counter+". "+fullname+"<br />";
    				counter++;
    			}
    			/* END TOYOTA SETTING */
    			
    		}
            
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java -  Survey_Open_Participant- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }

       
		
		/*
		content = content +"\n";
		content = content +"\n";
		content = content + "Please support in the timely completion of this process by submitting all duly ";
		content = content + "completed questionnaires by "+Deadline;
		content = content +"\n";
		content = content +"\n";
		*/
		
		/* TOYOTA SETTING */
		/*
		if(server.getCompanySetting() == 3)
		{
			content = content +"\n";
			content = content +"\n";
			content = content + "Please support in the timely completion of this process by submitting all duly ";
			content = content + "completed questionnaires by "+Deadline;
			content = content +"\n";
			content = content +"\n";
			content = content + "Your feedback will be kept confidential and will serve as inputs for identifying key strengths ";
			content = content + "and improvement needs for the Target.";
			content = content +"\n";
			content = content +"\n";
		}
		*/
		/* END TOYOTA SETTING */
				
		content = content + Footer_Participant();
		
		return content;
	}
	
   /**
	* 	Subject for Reminder email will be sent to the raters.
	*/
	public String Participant_Reminder_Subject()
	{
		content = "REMINDER TO RATERS TO COMPLETE 3-SIXTY FEEDBACK QUESTIONNAIRES";
		
		if(server.getCompanySetting() == 3)
			content = "REMINDER TO RATERS TO COMPLETE 3-SIXTY QUESTIONNAIRES";
			
		return content;
	}
	
	/**
	 * Subject for Reminder email will be sent to the raters (With Job Position name)
	 * @param sPosition
	 * @return
	 */
	public String Participant_Reminder_Subject(String sPosition)
	{
		content = "REMINDER TO RATERS TO COMPLETE 3-SIXTY FEEDBACK QUESTIONNAIRES";
		
		if(server.getCompanySetting() == 3)
			content = "REMINDER TO RATERS TO COMPLETE 3-SIXTY QUESTIONNAIRES for Targets in " +sPosition;
			
		return content;
	}
	
   /**
	* 	Reminder email will be sent to the raters.
	*
	*	@param RaterName - Rater Name
	*	@param Deadline - Survey Deadline
	* 	@param AssignmentID - Assignment ID
	*	@return content
	*/ 

	public String Participant_Reminder(String RaterName, String Deadline, int AssignmentID)throws SQLException, Exception
	{
		int counter=1;
				
		content = "<br /><br />Dear "+ RaterName +",<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "We noted that you have not completed the 3-Sixty Feedback Questionnaire(s) for self and those whom ";
		content = content + "you have been assigned to provide feedback. Below is the list of Target(s) assigned to you:";
		content = content +"<br />";
		content = content +"<br />";
			
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
		{
			content = "<br /><br />Dear "+ RaterName +",<br />";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "We noted that you have not completed the 3-Sixty Feedback Questionnaire(s) for:";
			content = content +"<br />";
			content = content +"<br />";
		}
		/* END TOYOTA SETTING */
		
		String command = "SELECT * FROM tblAssignment WHERE AssignmentID = "+AssignmentID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        	
    		while(rs.next())
    		{
    			String [] TargetDetail = new String[14];
    			int TargetID = rs.getInt("TargetLoginID");

    			TargetDetail = CE_Survey.getUserDetail_ADV(TargetID);
    			String fullname = TargetDetail[0]+" "+TargetDetail[1];
    			
    			content = content + "\t"+counter+". "+fullname+"<br />";
    			counter++;
    		}
            
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java -  Participant_Reminder - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
		if(server.getCompanySetting() == 3)
		{
			/* TOYOTA SETTING */
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Please support us by completing all the 360 questionnaires by "+Deadline;
			content = content +"<br />";
			content = content +"<br />";
			/* END TOYOTA SETTING */
		}
		else
		{
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Please support in the timely completion of this process by submitting all duly ";
			content = content + "completed questionnaires by "+Deadline;
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Your feedback will be kept confidential and will serve as inputs for identifying key strengths ";
			content = content + "and improvement needs for the Target.";
			content = content +"<br />";
			content = content +"<br />";
		}
		
		content = content + Footer_Participant();

		return content;
	}
	
	/**
	 * Subject for Upon completion of survey email will be sent to admin.
	 */
	public String Participant_Completion_Subject()
	{
		content = "NOTIFICATION TO ADMINISTRATOR TO PROCESS SURVEY RESULT";
		return content;
	}
	
	/**
	 * 	Upon completion of survey email will be sent to admin.
	 *
	 *	@param AdminName - Admin Name
	 *	@param OpenedDate - Survey Opened Date
	 *	@param Deadline - Survey Deadline
	 *	@param SurveyName - Survey Name
	 *	@return content
	 */
	public String Participant_Completion(String AdminName, String SurveyName, String OpenedDate, String Deadline)throws SQLException, Exception
	{
		content = "<br /><br />Dear "+ AdminName +",<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Please be informed that all the participant(s) have completed their Questionnaire(s) for the ";
		content = content + "specific survey. Below are the survey details:";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "\t	锟�Survey Name: "+ SurveyName +"<br />";
		content = content + "\t	锟�Opened Date: "+ OpenedDate +"<br />";
		content = content + "\t	锟�Deadline: "+ Deadline +"<br />";
		content = content +"<br />";
		content = content +"<br />";
		
		content = content + Footer();

		return content;
	}
	
	/**
	 *	Subject for Email template to remind admin to close the survey
	 */
	public String Survey_Closed_Subject()
	{
		content = "REMINDER TO ADMINISTRATOR TO CLOSE THE SURVEY";
		return content;
	}
	
	/**
	 *	Email template to remind admin to close the survey
	 *
	 *	@param nama - Name of the email receiver
	 *	@param OpenedDate - Survey Opened Date
	 *	@param OrgName - Organisation name
	 *	@param JobPosition - Job Position
	 *	@param SurveyName - Survey Name
	 *	@param SurveyStatus - Survey Status
	 *	@return content
	 */
	public String Survey_Closed(String nama, String OpenedDate, String OrgName, String JobPosition, String SurveyName, String SurveyStatus)
	{
		
		content = "<br /><br />Dear "+nama+",<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "We noted that you have created a survey with Not Commissioned or Open status and it is due to close. ";
		content = content + "Please set the survey status to Closed. Below are the survey details:";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "\t	锟�Name: "+SurveyName+"<br />"; 
		content = content + "\t	锟�Job Position: "+JobPosition+"<br />"; 
		content = content + "\t	锟�Organisation: "+OrgName+"<br />";
		content = content + "\t	锟�Opened Date: "+OpenedDate+"<br />";
		content = content + "\t	锟�Status: "+SurveyStatus+"<br />";
		content = content +"<br />";
		content = content +"<br />";
		
		content = content + Footer();
		

		return content;
	}
	
	/**
	 *	Subject for Email template to remind admin about the incomplete raters
	 */
	public String Incomplete_Raters_Subject()
	{
		content = "NOTIFICATION TO ADMINISTRATOR ABOUT INCOMPLETE RATERS";
		
		return content;
	}
	
	/**
	 *	Email template to remind admin about the incomplete raters
	 *
	 *	@param Admin_nama - Admin Name
	 *	@param OpenedDate - Survey Opened Date
	 *	@param Deadline - Survey Deadline
	 *	@param Deadline - Incomplete Raters name
	 *	@return content
	 */
	public String Incomplete_Raters(String Admin_nama, String SurveyName, String OpenedDate, String Deadline, int [] Raters)throws SQLException, Exception
	{
		String rater_name="";		
		String [] RaterDetail = new String[14];
		content = "<br /><br />Dear "+Admin_nama +",<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "We noted that you have created a survey and it is due to close today.";
		content = content + "However, some of the raters have not yet completed their survey.";
		content = content + "Below are the survey details:";
		content = content + "<br />";
		content = content + "<br />";
		content = content + "\t	锟�Name: "+SurveyName+"<br />"; 
		content = content + "\t	锟�Opened Date: "+OpenedDate+"<br />";
		content = content + "\t	锟�Deadline: "+Deadline+"<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Below is the list of rater(s) who has/have not yet completed their questionnaire(s):";
		content = content +"<br />";
		content = content +"<br />";
		
		for(int i = 0; i< Raters.length; i++)
		{
			if(Raters[i] != 0)
			{
				RaterDetail = CE_Survey.getUserDetail_ADV(Raters[i]);
				rater_name = RaterDetail[0]+" "+RaterDetail[1];
			
				//content = content + "\t	锟�"+rater_name+"<br />";
				content = content + "	锟� "+rater_name+"<br />";
			}
		}
		content = content +"<br />";
		content = content +"<br />";
		content = content + Footer();

		return content;
	}
	
	
	/**
	 *	Subject for Email Notification To Supervisor To Nominate Panel Of Raters For Their Direct Reports
	 */
	public String Sup_Nominate_Subject()
	{
		content = "NOTIFICATION TO SUPERVISOR TO NOMINATE PANEL OF RATERS FOR THEIR DIRECT REPORTS";
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
			content = "NOTIFICATION TO SUPERIOR TO NOMINATE RATERS FOR YOUR DIRECT REPORTS";	
		/* END TOYOTA SETTING */
		
		return content;
	}
	
	/**
	 *	Email template Notification To Supervisor To Nominate Panel Of Raters For Their Direct Reports
	 *
	 *	@param RaterName - Rater Name
	 *	@return content
	 */
	public String Sup_Nominate(int SupID, int SurveyID, String NominationClosedDate)throws SQLException, Exception
	{
		int counter = 1;
		String [] targetDetail = new String[14];
		String [] supDetail = new String[14];
		
		supDetail = CE_Survey.getUserDetail_ADV(SupID);
		String supName = supDetail[0]+", "+supDetail[1];

		content = "<br /><br />Dear "+supName+",";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Please nominate panel of Raters for your Direct Report(s) by "+NominationClosedDate+":<br /><br />";
		
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
		{
			content = "<br /><br />Dear "+supName+",";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"As you are a superior and your subordinates are assigned to participate in the 360 degree ";
			content = content +"feedback survey, therefore, this email aims to notify you that you need to nominate Raters ";
			content = content +"for your listed direct reports.";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"Please nominate 4 Raters (2 Peers and 2 subordinates) for each of your ";
			content = content +"subordinates by "+NominationClosedDate+":<br /><br />";		
		}
		
		String command = "SELECT DISTINCT tblAssignment.TargetLoginID FROM tblUserRelation INNER JOIN ";
        command = command +"tblAssignment ON tblUserRelation.User1 = tblAssignment.TargetLoginID ";
		command = command +"WHERE (tblUserRelation.User2 = "+SupID+") AND (tblAssignment.SurveyID = "+SurveyID+")";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        	
        	while(rs.next())
    		{
    			int targetID = rs.getInt("TargetLoginID");
    			targetDetail = CE_Survey.getUserDetail_ADV(targetID);
    			String targetName = targetDetail[0]+", "+targetDetail[1];
    			
    			content = content + "\t"+counter+". "+targetName+"<br />";
    			counter++;
    		
    		}	
            
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java -  Sup_Nominate - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
				
		content = content +"<br />";
		content = content +"<br />";
		
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)		
			content = content + Footer_Supervisor();
		else
			content = content + Footer_Participant();
		/* END TOYOTA SETTING */
		
		return content;
	}
	
	/**
	 *	Subject for Email Notification To Supervisor To Nominate Panel Of Raters For Their Direct Reports
	 */
	 
	public String Sup_Nominate_Reminder_Subject()
	{
		content = "REMINDER TO SUPERVISOR TO NOMINATE THE PANEL OF RATERS FOR THEIR DIRECT REPORTS";
		
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
			content = "REMINDER TO SUPERIOR TO NOMINATE RATERS FOR YOUR DIRECT REPORTS";
		
		
		return content;
	}
	
	/**
	 *	Email template Notification To Supervisor To Nominate Panel Of Raters For Their Direct Reports
	 *
	 *	@param RaterName - Rater Name
	 *	@return content
	 */
	 
	public String Sup_Nominate_Reminder(int SupID, int SurveyID, String NominationClosedDate)throws SQLException, Exception
	{
		int counter = 1;
		String [] targetDetail = new String[14];
		String [] supDetail = new String[14];
		
		supDetail = CE_Survey.getUserDetail_ADV(SupID);
		String supName = supDetail[0]+", "+supDetail[1];

		content = "<br /><br />Dear "+supName+",";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "We noted that you have not nominated the panel of Raters for your Direct Report(s):<br /><br />";		
		
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
		{
			content = "<br /><br />Dear "+supName+",";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "We noted that you have not nominated the 4 Raters for your Subordinate(s):<br /><br />";
		}
		/* END TOYOTA SETTING */
		
		String command = "SELECT DISTINCT tblAssignment.TargetLoginID FROM tblUserRelation INNER JOIN ";
        command = command +"tblAssignment ON tblUserRelation.User1 = tblAssignment.TargetLoginID ";
		command = command +"WHERE (tblUserRelation.User2 = "+SupID+") AND (tblAssignment.SurveyID = "+SurveyID+")";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        
    		while(rs.next())
    		{
    			int targetID = rs.getInt("TargetLoginID");
    			targetDetail = CE_Survey.getUserDetail_ADV(targetID);
    			String targetName = targetDetail[0]+", "+targetDetail[1];
    			
    			content = content + "\t"+counter+". "+targetName+"<br />";
    			counter++;
    		}			
    		
            
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java -  Sup_Nominate_Reminder - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		if(server.getCompanySetting() == 3)
		{
			/* TOYOTA SETTING */
			content = content +"<br />";
			content = content +"<br />";
			content = content +"It is critical that you perform this step in order to allow the Raters sufficient time to ";
			content = content +"complete the 3-Sixty Feedback Questionnaires. The 3-Sixty Feedback process cannot proceed ";
			content = content +"until you've nominated the Raters for your Direct Report(s).";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"Please support in the timely completion of this process by nominating the panel of Raters ";
			content = content +"by "+NominationClosedDate;	
			content = content +"<br />";
			content = content +"<br />";			
			content = content + Footer_Supervisor();
			/* END TOYOTA SETTING */
		}
		else
		{
			content = content +"<br />";
			content = content +"<br />";
			content = content +"It is critical that you perform this step in order to allow the Raters sufficient time to ";
			content = content +"complete the 3-Sixty Feedback Questionnaires. The 3-Sixty Feedback exercise cannot ";
			content = content +"proceed until you have nominated the panel of Raters for your Direct Report(s).";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"Please support in the timely completion of this process by nominating the panel of Raters ";
			content = content +"by "+NominationClosedDate;	
			content = content +"<br />";
			content = content +"<br />";			
			content = content + Footer_Participant();
		}
		
		return content;
	}
	
	/**
	 *	Subject for Email Notification To Admin regarding nomination end date
	 */
	 
	public String NominationEnd_Subject()
	{
		content = "NOTIFICATION TO ADMINISTRATOR REGARDING NOMINATION END DATE";
		return content;
	}
	
	/**
	 *	Email Notification To Admin regarding nomination end date
	 */
	public String NominationEnd(String nama, String NominationEndDate, String OrgName, String JobPosition, String SurveyName, String SurveyStatus)
	{
		content = "<br /><br />Dear "+nama+",";
		content = content +"<br />";
		content = content +"<br />";
		content = content +"Please be notified that the nomination period for the below survey has ended:";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "\t	锟�Name: "+SurveyName+"<br />"; 
		content = content + "\t	锟�Job Position: "+JobPosition+"<br />"; 
		content = content + "\t	锟�Organisation: "+OrgName+"<br />";
		content = content + "\t	锟�Nomination End Date: "+NominationEndDate+"<br />";
		content = content + "\t	锟�Status: "+SurveyStatus+"<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + Footer();
		
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
		{
			content = "<br /><br />Dear "+nama+",";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"Please be notified that the nomination period for the survey below has ended:";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Name: "+SurveyName+"<br />"; 
			content = content + "Job Position: "+JobPosition+"<br />"; 
			content = content + "Organisation: "+OrgName+"<br />";
			content = content + "Nomination End Date: "+NominationEndDate+"<br />";
			content = content + "Status: "+SurveyStatus+"<br />";
			content = content +"<br />";
			content = content +"<br />";
			content = content + Footer();
		}
		/* END TOYOTA SETTING */

		return content;	
	}
		
	/**
	 * Footer for all the email.
	 */
	public String Footer()
	{
		content = "You can access the 3-Sixty Profiler&reg Online System by clicking on the URL below and ";
		content = content + "logon to the system using your userid and password. ";
		content = content +"<br />";
		content = content +"<br />";
		content = content + server.getWebsite_Hyperlink();
		content = content +"<br />";
		content = content +"<br />";
		content = content + "For assistance, please contact the PCC Helpdesk at phone: +65 68960080 (Mon to Fri from ";
		content = content + "9.00 am to 6.00 pm) or email to: "+server.getEmail_Hyperlink();
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Thank you.";
		content = content +"<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Regards,<br />";
		content = content + "3-Sixty Profiler&reg Administrator";

		return content;
	}
	
	/**
	 * 	Footer for all the participant emails.
	 *
	 *	@param Username
	 *	@param Password
	 */
	public String Footer_Participant()
	{
		content = "You can access the 3-Sixty Profiler<sup>&reg;</sup> System by clicking on the URL below ";
		content = content + "using your username and password. ";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "<Website>";
		content = content +"<br />";
		content = content +"Username: <Username><br />";
		content = content +"Password: <Password><br />";
		content = content +"<br />";
		content = content + "For assistance, please contact the PCC Helpdesk at phone: +65 68960080 (Mon to Fri from ";
		content = content + "9.00 am to 6.00 pm) or email to: <Admin Email>";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Thank you.";
		content = content +"<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Regards,<br />";
		content = content + "3-Sixty Profiler<sup>&reg;</sup> Administrator<br />";
		content = content + "Pacific Century Consulting Pte Ltd";
		
		/* TOYOTA SETTING */
		if(server.getCompanySetting() == 3)
		{
			content = "You can access the 360 Online System via the TA Website and logon to the system using your user id ";
			content = content +"and password to complete the questionnaire(s) provided below.";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "<Website>";
			content = content +"<br />";
			content = content +"Username: <Username><br />";
			content = content +"Password: <Password><br />";
			content = content +"<br />";
			content = content +"After logon to the 360 Online System, click at 'Rater's To-Do list' icon on the left ";
			content = content +"then answer the question of your demographic after finishing them, click OK button to ";
			content = content +"proceed.";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"Then the system will show your colleagues names in which you are required to do the ";
			content = content +"360 degree questionnaire for. Click on the survey name, then the questions will appear. ";
			content = content +"Please answer to all of the questions and repeat for all of your listed colleagues.";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"You can find more details on Questionnaire completion process and user guides from our ";
			content = content +"TA Website.";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"For assistance, please contact 3-Sixty Online Team ";
			content = content +"at phone: 0-2386-2830, 0-2386-1159-60 ";
			content = content +"or email to: <Admin Email>.";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Thank you.";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Regards,<br />";
			content = content + "3-Sixty Administrator<br />";
			content = content + "Toyota Academy Thailand";
		}
		/* END TOYOTA SETTING */
		
		return content;
	}
	
	/**
	 * 	Footer for all the Surpervisors emails.
	 *
	 *	@param Username
	 *	@param Password
	 */
	public String Footer_Supervisor()
	{	
		content = "You may logon to the system by clicking on the URL below and using your username and password to nominate the Raters as provided below: ";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "<Website>";
		content = content +"<br />";
		content = content +"Username: <UserName><br />";
		content = content +"Password: <Password><br />";
		content = content +"<br />";
		content = content +"After you logon to the 3-Sixty Profiler<sup>&reg;</sup> System, click on \"Nominate Rater\" and a list of all your subordinate(s) will appear.";
		content = content +" Please ensure that you nominate raters for all your subordinates.";
		content = content +"<br />";
		content = content +"<br />";
		content = content +"You can find more details on the nomination process and step-by-step user guides from the Help menu.";
		content = content +"<br />";
		content = content +"<br />";
		content = content +"For assistance, please contact the PCC Helpdesk at phone: +65 68960080 (Mon to Fri from 9.00 am to 6.00 pm) or email to: <Admin Email>";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Thank you.";
		content = content +"<br />";
		content = content +"<br />";
		content = content +"<br />";
		content = content + "Regards,<br />";
		content = content + "3-Sixty Profiler<sup>&reg;</sup> Administrator<br />";
		content = content + "Pacific Century Consulting Pte Ltd";
	
		if(server.getCompanySetting() == 3)
		{
			content = "You can access the 360 Online System via the TA Website and click on the icon of ";
			content = content + "'360 Degree Competency Assessment Online' in the section of TA Plus to logon to ";
			content = content + "the system using your user id and password to nominate the Raters as provided below.";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "<Website>";
			content = content +"<br />";
			content = content +"Username: <Username><br />";
			content = content +"Password: <Password><br />";
			content = content +"<br />";
			content = content +"After logon to the 360 Online System, click at 'Nominate Rater' then your listed ";
			content = content +"subordinate name list will appear. Please nominate all the raters for them so that ";
			content = content +"your subordinates would know who they need to do the 360 questionnaire for.";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"You can find more details on nomination process and user guides from our TA Website.";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"For assistance, please contact 3-Sixty Online Team ";
			content = content +"at phone: 0-2386-2830, 0-2386-1159-60 ";
			content = content +"or email to: <Admin Email>. ";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Thank you.";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Regards,<br />";
			content = content + "3-Sixty Administrator<br />";
			content = content + "Toyota Academy Thailand";
		}

		return content;
	}
	
	/**
	 * Retrieves the email template which assigned under that organisation.
	 * @param int FKOrg	FKOrganisation
	 * @param int iOpt	Template Option: 0=Nom Email, 1=Nom Reminder Email, 2=Participant Email, 3=Participant Reminder Email
	 *
	 * @return String EmailTemplate
	 */
	public String getEmailTemplate(int FKOrg, int iOpt) throws SQLException, Exception {
		
		String query = "";
		
		String field = "EmailNom"; //Nomination Email
		if(iOpt == 1)
			field = "EmailNomRemind"; //Nomination Reminder Email
		else if(iOpt == 2)
			field = "EmailPart"; //Participant Email
		else if(iOpt == 3)
			field = "EmailPartRemind"; //Participant Reminder Email
		else if(iOpt == 4)
			field = "EmailIndividualReport"; //Send Individual Report Email
		else if(iOpt == 5)
			field = "EmailSelf";
		else if(iOpt == 6)
			field = "EmailCoachNotification";
		else if(iOpt == 7)
			field = "EmailCoachReminder";
		
		query = query + "SELECT " + field + " FROM tblOrganization WHERE PKOrganization = " + FKOrg;	

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		String sEmailTemplate = "";

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        
    		if(rs.next())
    		{
    			sEmailTemplate = rs.getString(1);
    		}			
            
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java - getEmailTemplate - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return sEmailTemplate;
	}
	

	/**
	 * Retrieves the email subject which assigned under that organisation.
	 * @param int FKOrg	FKOrganisation
	 * @param int iOpt	Template Option: 0=Nom Email, 1=Nom Reminder Email, 2=Participant Email, 3=Participant Reminder Email
	 * @author Johanes
	 * @return String EmailSubject
	 */
	public String getEmailSubject(int FKOrg, int iOpt) throws SQLException, Exception {
		
		String query = "";
		
		String field = "EmailNomSubject"; //Nomination Email Subject
		if(iOpt == 1)
			field = "EmailNomRemindSubject"; //Nomination Reminder Email Subject
		else if(iOpt == 2)
			field = "EmailPartSubject"; //Participant Email Subject
		else if(iOpt == 3)
			field = "EmailPartRemindSubject"; //Participant Reminder Email Subject
		else if(iOpt == 4)
			field = "EmailIndividualReportSubject"; //Send Individual Report Email
		else if(iOpt == 5)
			field = "EmailSelf";
		else if(iOpt == 6)
			field = "EmailCoachNotificationSubject";
		else if(iOpt == 7)
			field = "EmailCoachReminderSubject";
	
		query = query + "SELECT " + field + " FROM tblOrganization WHERE PKOrganization = " + FKOrg;	
		
		System.out.println(query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		String sEmailTemplateSubject = "";
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        
    		if(rs.next())
    		{
    			sEmailTemplateSubject = rs.getString(1);
    		}			
            
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java - getEmailTemplate - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return sEmailTemplateSubject;
	}
	
	/**
	 * Update email template.
	 * @param int FKOrg	FKOrganisation
	 * @param int iOpt	Template Option: 0=Nom Email, 1=Nom Reminder Email, 2=Participant Email, 3=Participant Reminder Email
	 * @param String template	Email Template
	 * 
	 * @return void
	 */
	public boolean editTemplate(int FKOrg, int iOpt, String template, String subject) throws SQLException, Exception {
		
		template = fixText(template.trim());   
		template = db.SQLFixer(template.trim());
		subject = fixText(subject.trim());	
		subject = db.SQLFixer(subject.trim());
				/*
 * Change : Update subject column in table organization
 * Reason : need to update the subject too
 * Add by : Johanes
 * Add on : 9/11/2009
 */	
		String field = "EmailNom";
		String fieldSubject = "EmailNomSubject";
		if(iOpt == 1){
			field = "EmailNomRemind";
			fieldSubject = "EmailNomRemindSubject";
		}
		else if(iOpt == 2){
			field = "EmailPart";
			fieldSubject = "EmailPartSubject";
		}
		else if(iOpt == 3){
			field = "EmailPartRemind";
			fieldSubject = "EmailPartRemindSubject";
		}
		else if(iOpt == 4){
			field = "EmailIndividualReport"; //Send Individual Report Email
		    fieldSubject = "EmailIndividualReportSubject";
		}
		else if(iOpt == 5)
			field = "EmailSelf";
		else if(iOpt == 6){
			field = "EmailCoachNotification";
			fieldSubject = "EmailCoachNotificationSubject";
		}
		else if(iOpt == 7){
			field = "EmailCoachReminder"; //Send Individual Report Email
		    fieldSubject = "EmailCoachReminderSubject";
		}
		
		String sql = "";
		
		//Edited by Xuehai 02 Jun 2011. Changing sql for saving Chinese characters. 
		//sql = "Update tblOrganization set " + field + " = '" + template + "'," + fieldSubject + " = '" + subject + "' where PKOrganization = " + FKOrg;
		sql = "Update tblOrganization set " + field + " = N'" + template + "'," + fieldSubject + " = N'" + subject + "' where PKOrganization = " + FKOrg;
		//End of Edit
		Connection con = null;
		Statement st = null;
		boolean bIsUpdated = false;
        
		try	
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if(iSuccess!=0)
			bIsUpdated=true;
		}
			
		catch(Exception E)
		{
	        System.err.println("EmailTemplate.java - editTemplate - " + E);
		}
		
		finally
	   	{
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

	    }
		
		return bIsUpdated;
	}
	
	/**
	 *	Default Email template
	 *
	 * 	@param int iOpt	Template Option: 0=Nom Email, 1=Nom Reminder Email, 2=Participant Email, 3=Participant Reminder Email, 4=SELF Assessment Email
	 *
	 *	@return content
	 */
	/* Edited by Xuehai, 20 Jun 2011. Changed '<br />' to '<br />', because the textarea has been replace by FCKEditor. */
	public String getDefaultTemplate(int iOpt)throws SQLException, Exception
	{		
		if(iOpt == 0) //Nomination Email
		{
			content = "<br /><br />Dear <Rater Name>,";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Please nominate the panel of Raters for each of the following subordinate(s) by <Nomination Closed Date>:<br /><br />";
			
			
			/* TOYOTA SETTING */
			if(server.getCompanySetting() == 3)
			{
				content = "<br /><br />Dear <Rater Name>,";
				content = content +"<br />";
				content = content +"<br />";
				content = content +"As you are a superior and your subordinates are assigned to participate in the 360 degree ";
				content = content +"feedback survey, therefore, this email aims to notify you that you need to nominate Raters ";
				content = content +"for your listed direct reports.";
				content = content +"<br />";
				content = content +"<br />";
				content = content +"Please nominate 4 Raters (2 Peers and 2 subordinates) for each of your ";
				content = content +"subordinates by <Nomination Closed Date> :<br /><br />";
			
			} 
			content = content + "<Targets List><br /><br /><br />";

			if(server.getCompanySetting() != 3)
			{
				content = content +"It is critical that you perform this step in order to give the Raters sufficient time to ";
				content = content +"complete the 3-Sixty Profiler<sup>&reg;</sup> Feedback Questionnaires. The 3-Sixty Profiler<sup>&reg;</sup> Feedback process cannot proceed ";
				content = content +"until you have nominated the Raters for your subordinate(s).";
				content = content +"<br />";
				content = content +"<br />";
				content = content +"Please support in the timely completion of this process by completing this nomination ";
				content = content +"by <Nomination Closed Date>.";	
				content = content +"<br />";
				content = content +"<br />";
			}
			content = content + Footer_Supervisor();
		}
		else if(iOpt == 1) //Nomination Reminder Email
		{
			content = "<br /><br />Dear <Rater Name>,";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "We noted that you have not nominated the Raters for the following subordinate(s):<br /><br />";		
			
			/* TOYOTA SETTING */
			if(server.getCompanySetting() == 3)
			{
				content = "<br /><br />Dear <Supervisor Name>,";
				content = content +"<br />";
				content = content +"<br />";
				content = content + "We noted that you have not nominated the 4 Raters for your Subordinate(s):<br /><br />";
			}
			/* END TOYOTA SETTING */
			
			content = content + "<Targets List><br /><br /><br />";

			content = content +"It is critical that you perform this step in order to give the Raters sufficient time to ";
			content = content +"complete the 3-Sixty Profiler<sup>&reg;</sup> Feedback Questionnaires. The 3-Sixty Profiler<sup>&reg;</sup> Feedback process cannot proceed ";
			content = content +"until you have nominated the Raters for your subordinate(s).";
			content = content +"<br />";
			content = content +"<br />";
			content = content +"Please support in the timely completion of this process by completing this nomination ";
			content = content +"by <Nomination Closed Date>.";	
			content = content +"<br />";
			content = content +"<br />";
			
			content = content + Footer_Supervisor();
		}
		else if(iOpt == 2) //Participant Email
		{
			content = "<br /><br />Dear <Rater Name>,<br />";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "You have been chosen to participate in the 3-Sixty Profiler<sup>&reg;</sup> Feedback Exercise as a Rater. You can ";
			content = content + "start to complete the Questionnaire(s) for whom you have been assigned to provide ";
			content = content + "feedback. Below is the list of Target(s) assigned to you:";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "<Targets List><br />";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Please support in the timely completion of this process by submitting all duly ";
			content = content + "completed questionnaires by <Deadline>";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "Your feedback will be kept confidential and will serve as inputs for identifying key strengths ";
			content = content + "and improvement needs for the Target.";
			content = content +"<br />";
			content = content +"<br />";
				
			/* TOYOTA SETTING */
			if(server.getCompanySetting() == 3)
			{
				content = "<br /><br />Dear <Rater Name>,<br />";
				content = content +"<br />";
				content = content +"<br />";
				content = content +"You are invited to provide feedback to colleagues whom their superiors have nominated ";
				content = content +"you to be the raters.";
				content = content +"<br />";
				content = content +"<br />";
				content = content +"Please complete the 3-Sixty Questionnaire(s) for each of your colleague by <Deadline>.<br /><br />";
			}
			
			/* END TOYOTA SETTING */
			content = content + Footer_Participant();
		}
		else if(iOpt == 3) //Participant Reminder Email
		{
			content = "<br /><br />Dear <Rater Name>,<br />";
			content = content +"<br />";
			content = content +"<br />";
			content = content + "We noted that you have not completed the 3-Sixty Profiler<sup>&reg;</sup> Feedback Questionnaire(s) for the ";
			content = content + "following list of Target(s) assigned to you:";
			content = content +"<br />";
			content = content +"<br />";
				
			/* TOYOTA SETTING */
			if(server.getCompanySetting() == 3)
			{
				content = "<br /><br />Dear <Rater Name><br />";
				content = content +"<br />";
				content = content +"<br />";
				content = content + "We noted that you have not completed the 3-Sixty Feedback Questionnaire(s) for:";
				content = content +"<br />";
				content = content +"<br />";
			}
			/* END TOYOTA SETTING */
			
			content = content + "<Targets List><br />";
			
			if(server.getCompanySetting() == 3)
			{
				/* TOYOTA SETTING */
				content = content +"<br />";
				content = content +"<br />";
				content = content + "Please support us by completing all the 360 questionnaires by <Deadline>.";
				content = content +"<br />";
				content = content +"<br />";
				/* END TOYOTA SETTING */
			}
			else
			{
				content = content +"<br />";
				content = content +"<br />";
				content = content + "Please support in the timely completion of this process by submitting all duly ";
				content = content + "completed questionnaires by <Deadline>";
				content = content +"<br />";
				content = content +"<br />";
				content = content + "Your feedback will be kept confidential and will serve as inputs for identifying key strengths ";
				content = content + "and improvement needs for the Target.";
				content = content +"<br />";
				content = content +"<br />";
			}
			content = content + Footer_Participant();
		}
		else if(iOpt == 6){ //coaching  notification Email
			
			content="";
			content = content +"<p>Dear<Rater Name>;,<br /> <br />";
			content = content +"Please logon to the system to book your timeslot with the coach for the 1-to-1 Feedback Interpretation Session.";
			content = content +" Do take note that it is on a first come first served basis so please proceed to book as soon as possible. There are limited slots per coach per day.<br /> <br />";
			content = content +"You can access the 3-Sixty Profiler<sup>&reg;</sup> System by clicking on the URL below using your username and password.";
			content = content +" During this period, it will be available online 7 x 23 hours (with the exception of 1 hour of maintenance close to midnight) and can be accessed using any internet connection with most browsers including IE, Firefox, Google Chrome and Safari.<br />";
			content = content +"<br /> URL: <a href=\"http://119.73.212.178/i360_Pool_SVNJSP/index.jsp?candidate=SPF\">http://119.73.212.178/i360_Pool_SVNJSP/index.jsp?candidate=SPF</a><br />";
			content = content +"Username: <Username><br /> Password: <Password><br /> <br />";
			content = content +"Please proceed to book as soon as possible as the slots are limited. The first cut off date for booking is <strong>05/07/2013. ";
			content = content +"</strong>We would appreciate your support by doing your booking by that date so that we can do the necessary logistics preparations i.e. provide your coach with your report so that they can prepare for the 1-to-1 Feedback Interpretation Session.<br /> <br />";
			content = content +"For assistance, please contact the PCC Helpdesk at phone: +65 68960080 (Mon to Fri from 9.00 am to 6.00 pm) or email to: <strong><a ";
			content = content +"href=\"mailto:3SixtyAdmin@pcc.com.sg\">3SixtyAdmin@pcc.com.sg</a> </strong>with a screenshot of the error encountered. Thank you.<br /> <br />";
			content = content +"<em>Note: If you encounter a message informing you that the email might be a phishing message and may be potentially unsafe, do not worry.";
			content = content +" When there are URL links in the email, this message tends to appear. Simply click to Enable and you will be able to see and access the URL links. </em><br />";
			content = content +"<br /> Regards,<br /> 3-Sixty Profiler<sup>&reg;</sup> Administrator<br /> Pacific Century Consulting Pte Ltd</p>";
		}
		else if(iOpt == 7){ //coaching  reminder Email
			
			content="";
			content = content +"	<p>Dear <Rater Name>;,</p>";
			content = content +"	<p>The deadline for the submission of the coach booking is drawing near. Please logon to the system to book your timeslot with the coach for the 1-to-1 Feedback Interpretation Session. Do take note that it is on a first come first served basis so please proceed to book as soon as possible so that the coach may proceed to prepare for your coaching session.<br />";
			content = content +"	<br />";
			content = content +"	You can access the 3-Sixty Profiler<sup>&reg;</sup> System by clicking on the URL below using your username and password. During this period, it will be available online 7 x 23 hours (with the exception of 1 hour of maintenance close to midnight) and can be accessed using any internet connection with most browsers including IE, Firefox, Google Chrome and Safari.<br />";
			content = content +"	<br />";
			content = content +"	URL: <a href=\"http://119.73.212.178/i360_Pool_SVNJSP/index.jsp?candidate=SPF\">http://119.73.212.178/i360_Pool_SVNJSP/index.jsp?candidate=SPF</a><br />";
			content = content +"	Username: <Username><br />";
			content = content +"	Password: <Password><br />";
			content = content +"	<br />";
			content = content +"	Please proceed to book as soon as possible as the slots are limited. The first cut off date for booking is <strong>05/07/2013. </strong>We would appreciate your support by doing your booking by that date so that we can do the necessary logistics preparations i.e. provide your coach with your report so that they can prepare for the 1-to-1 Feedback Interpretation Session.<br />";
			content = content +"	<br />";
			content = content +"For assistance, please contact the PCC Helpdesk at phone: +65 68960080 (Mon to Fri from 9.00 am to 6.00 pm) or email to: <strong><a href=\"mailto:3SixtyAdmin@pcc.com.sg\">3SixtyAdmin@pcc.com.sg</a> </strong>with a screenshot of the error encountered. Thank you.<br />";
			content = content +"<br />";
			content = content +"<em>Note: If you encounter a message informing you that the email might be a phishing message and may be potentially unsafe, do not worry. When there are URL links in the email, this message tends to appear. Simply click to Enable and you will be able to see and access the URL links. </em><br />";
			content = content +"<br />";
			content = content +"Regards,<br />";
			content = content +"3-Sixty Profiler<sup>&reg;</sup> Administrator<br />";
			content = content +"Pacific Century Consulting Pte Ltd</p>";

		}
		else //SELF Notification Email
		{
			content = content + "<br /><br />";
			content = content + "Welcome to the Multi-Source Feedback (MSF) exercise as part of the \"Managing People/Self\" module of the Asia Future Leader Program - 2005/06.";
			content = content + "<br /><br />";
			content = content + "You are required to assess yourself by responding to a series of behavioural statements selected from the Allianz Asia Core Competency Model, ";
			content = content + "a set of critical behaviours that make for successful leadership within ALLIANZ and the Asian context.";
			content = content + "<br /><br />";
			content = content + "As mentioned in our earlier communications, the purpose of this exercise is to help you identify and understand your emerging and established ";
			content = content + "competency strengths, which will be shared with you by an assigned development coach during the AFL program.";
			content = content + "<br /><br />";
			content = content + "You are now ready to access the Allianz MSF on-line questionnaire for your self-assessment by clicking on the URL below and logon to the system using your user-id and password.";
			content = content + "<br /><br />";
			content = content + "<Website><br /><br />";
			content = content + "Username: <Username><br />";
			content = content + "Password: <Password><br /><br />";
			content = content + "Please complete the MSF on-line questionnaire by 29 March 2006 at the latest.";
			content = content + "<br /><br />";
			content = content + "For technical assistance, please contact the PCC Helpdesk at phone: +65 6896 0080 (Mon to Fri from 9.00 am to 6.00 pm) or email to: 3SixtyAdmin@pcc.com.sg";
			content = content + "<br /><br />";
			content = content + "If you have any other questions pertaining to the MSF and its process, please contact Karina Kuhlmann from HR Asia at phone: + 65 6297 4367 or e-mail to: Karina.Kuhlmann@allianz.com.sg.";
			content = content + "<br /><br />";
			content = content + "Regards,<br />";
			content = content + "3-Sixty Administrator";
		}
		
		return content;
	}
	
	/**
	 *	Fill Nomination Email template
	 *
	 * 	@param 	int iOpt		Template Option: 0=Nom Email, 1=Nom Reminder Email
	 *	@param	SurvID			int SurveyID
	 *	@param	SupName			Supervisors's name
	 *	@param	NomClosedDate	Nomination Closed Date
	 *	@param	TargetList		List of Targets
	 *
	 *	@return content
	 */
	public String fillNomTemplate(int iOpt, int SurveyID, int SupID, String NomClosedDate)throws SQLException, Exception
	{
		org = new Organization();
		
		int counter = 1;
		String content = "";
		String sTargetList="";
		String [] targetDetail = new String[14];
		String [] supDetail = new String[14];
		
		supDetail = CE_Survey.getUserDetail_ADV(SupID);
		
		String supName = supDetail[0]+", "+supDetail[1];
		
		String command = "SELECT DISTINCT tblAssignment.TargetLoginID FROM tblUserRelation INNER JOIN ";
        command = command +"tblAssignment ON tblUserRelation.User1 = tblAssignment.TargetLoginID ";
		command = command +"WHERE (tblUserRelation.User2 = "+SupID+") AND (tblAssignment.SurveyID = "+SurveyID+")";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        
        	while(rs.next())
    		{
    			int targetID = rs.getInt("TargetLoginID");
    			targetDetail = CE_Survey.getUserDetail_ADV(targetID);
    			
    			String targetName = targetDetail[0]+", "+targetDetail[1];
    			
    			sTargetList = sTargetList + "\t"+counter+". "+targetName+"<br />";
    			counter++;
    		}
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java - fillNomTemplate - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
	
		//Get template from database and store into content
		content = org.getEmailTemplate_SurvID(SurveyID, iOpt);
		
		//Replace the tags "<>" in the template with the data
		/*
		 * Change(s) : Replace "&lt;XXX&gt;" instead of replacing <XXX>
		 * Reason(s) : Non-html tags are encoded as "&lt;" and "&gt;"
		 * Updated By: Gwen Oh
		 * Updated On: 19 Sep 2011
		 */
		content = content.replaceAll("&lt;Rater Name&gt;", supName);
		content = content.replaceAll("&lt;Nomination Closed Date&gt;", NomClosedDate);
		content = content.replaceAll("&lt;Targets List&gt;", sTargetList);
		content = content.replaceAll("&lt;Website&gt;", server.getWebsite_Hyperlink());
		content = content.replaceAll("&lt;UserName&gt;", supDetail[2]);
		content = content.replaceAll("&lt;Username&gt;", supDetail[2]);
		content = content.replaceAll("&lt;Password&gt;", supDetail[12]);
		content = content.replaceAll("&lt;Admin Email&gt;", server.getAdminEmail());
		
		return content;
	}
	
	/**
	 *	Fill Nomination Email template that is used for filling test email
	 *
	 *	@param 	Content			Email Template to fill
	 *	@param  PkOrg			Organisation ID to reference for survey with rater
	 *
	 *	@author Sebastian
	 *	@since  v.1.3.12.80 (21 July 2010)
	 *
	 *	@return content
	 */
	public String fillTestTemplate(String Content, int PkOrg)throws SQLException, Exception
	{
		int z = 0;
		int SurveyID = 0;
		int RaterID = 0;
		int counter = 1;
		String Deadline = "";
		String NominationClosedDate ="";
		String sTargetList = "";
		String [] RaterDetail = new String[20];
		SimpleDateFormat formatter= new SimpleDateFormat ("dd-MM-yyyy");

		
		String query = "SELECT tblAssignment.SurveyID, tblAssignment.RaterLoginID, tblSurvey.DeadlineSubmission FROM tblAssignment " +
		"INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID WHERE ";
		
		query += "tblSurvey.FKOrganization = " + PkOrg;
		query += "ORDER BY AssignmentID DESC"; //sort by the most recent rater inputting to a survey in the org or coy


		int TargetID = 0;
		String sFullname = "";
		String [] TargetDetail = new String[14];
		z = 0;
		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
        	if (rs.next()) //if have recent survey with raters assigned in it, take the very first rater as the reference
        	{
        		SurveyID = rs.getInt(1); //get Survey ID
        		RaterID = rs.getInt(2); //get Rater Login ID
        		Deadline = formatter.format(rs.getDate(3)); //get the Deadline submission of the survey
        		
        		//Look for the Normination Date
        		query = "SELECT DISTINCT(NominationEndDate) FROM tblAssignment " +
        				"INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID " +
        				"INNER JOIN tblUserRelation ON tblAssignment.TargetLoginID = tblUserRelation.User1 " +
        				"WHERE tblAssignment.SurveyID = " + SurveyID;

            	rs=st.executeQuery(query);
            	
            	if (rs.next())
            	{
            		NominationClosedDate = formatter.format(rs.getDate(1)); //get the normiation end date
            	}
        		//Look for list of targets in this survey that this rater have to rate
        		query = "SELECT tblAssignment.TargetLoginID " +
        				"FROM tblAssignment " +
        				"WHERE tblAssignment.SurveyID = " + SurveyID + " " +
        				"AND tblAssignment.RaterLoginID = " + RaterID;
        		
            	rs=st.executeQuery(query);
            	
            	while(rs.next())
        		{
            		TargetID = rs.getInt(1); //get the target Login ID
        			
        			//System.out.println(AssignmentID + ". Getting user details for Target: " + TargetID);
        			TargetDetail = CE_Survey.getUserDetail_ADV(TargetID);
        			sFullname = TargetDetail[0]+" "+TargetDetail[1];
        			
        			sTargetList = sTargetList + "\t"+counter+". "+sFullname+"<br />";
        			counter++;
        		}
        	}
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java - fillPartTestTemplate - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }

	
		//Get template from database and store into content
		RaterDetail = CE_Survey.getUserDetail_ADV(RaterID);
		
		//Replace the tags "<>" in the template with the data
		/*
		 * Change(s) : Replace "&lt;XXX&gt;" instead of replacing <XXX>
		 * Reason(s) : Non-html tags are encoded as "&lt;" and "&gt;"
		 * Updated By: Gwen Oh
		 * Updated On: 19 Sep 2011
		 */
		Content = Content.replaceAll("&lt;Rater Name&gt;", RaterDetail[0]+" "+RaterDetail[1]);
		Content = Content.replaceAll("&lt;Nomination Closed Date&gt;", NominationClosedDate);
		Content = Content.replaceAll("&lt;Deadline&gt;", Deadline);
		Content = Content.replaceAll("&lt;Targets List&gt;", sTargetList);
		Content = Content.replaceAll("&lt;Website&gt;", server.getWebsite_Hyperlink());
		Content = Content.replaceAll("&lt;Username&gt;", RaterDetail[2]);
		Content = Content.replaceAll("&lt;UserName&gt;", RaterDetail[2]);
		Content = Content.replaceAll("&lt;Password&gt;", RaterDetail[12]);
		Content = Content.replaceAll("&lt;Admin Email&gt;", server.getAdminEmail());
		
		return Content;
	}
	
	/**
	 *	Fill Email Individual Report Template
	 *
	 *	@param 	int iOpt		1=Nom Reminder, 2=Participant, 3=Participant Reminder, 4=Email Individual Report
	 *	@param	SurvID			int SurveyID
	 * 	@param	TargeID		    the PKUser of the target
	 *	@param	Deadline		Survey's deadline
	 *	@Author Liu Taichen
	 *
	 *	@return content
	 */
	public String fillEmailIndividualReport(int iOpt, int SurvID, int iTargetID, String Deadline)throws SQLException, Exception
	{
	
		org = new Organization();
	
		String sFullname = "";
		String [] TargetDetail = new String[16];
		

        TargetDetail = CE_Survey.getUserDetail(iTargetID);
		//Get template from database and store into content
		content = org.getEmailTemplate_SurvID(SurvID, iOpt);
		
        
		
		content = content.replaceAll("&lt;Target Name&gt;", TargetDetail[0]+ " " + TargetDetail[1] );
		content = content.replaceAll("&lt;Deadline&gt;", Deadline);	
		content = content.replaceAll("&lt;Website&gt;", server.getWebsite_Hyperlink());
		content = content.replaceAll("&lt;Admin Email&gt;", server.getAdminEmail());
		
		return content;
	}
	
	/**
	 *	Fill Nomination Email template
	 *
	 *	@param 	int iOpt		1=Nom Reminder, 2=Participant, 3=Participant Reminder, 4=SELF
	 *	@param	SurvID			int SurveyID
	 * 	@param	RaterName		Supervisors's name
	 *	@param	Deadline		Survey's deadline
	 *	@param	TargetList		List of Targets
	 *
	 *	@return content
	 */
	public String fillPartTemplate(int iOpt, int SurvID, int iRaterID, String RaterName, String Deadline, int [] AssignmentID)throws SQLException, Exception
	{
		int z = 0;
		int RaterID=0;
		int counter = 1;
		String sTargetList = "";
		String [] RaterDetail = new String[20];
		
		org = new Organization();

		String command = "SELECT * FROM tblAssignment WHERE AssignmentID IN (";
		
		command = command + "SELECT d.AssignmentID FROM tblSurvey a, tblOrganization b, tblJobPosition c, tblAssignment d, [User] e ";
		command = command + "WHERE a.FKOrganization = b.PKOrganization AND a.JobPositionID = c.JobPositionID ";
		command = command + "AND a.SurveyID = d.SurveyID AND d.RaterLoginID = e.PKUser ";
		command = command + "AND a.SurveyStatus = 1 AND d.RaterStatus = 0 AND TargetLoginID <> 0 AND (a.SurveyID = " + SurvID + ") ";
		
		if(iRaterID != 0)
			command = command + "AND (d.RaterLoginID = " + iRaterID + ") ";
		
		/*for(z=0; z<AssignmentID.length; z++)
		{
			// Since we initialise AssignmentID to be 20 earlier on, the loop will be repeated 20 times even though there isn't any AssignmentID anymore
			// Therefore, when AssignmentID is 0, increase z to be the length of array in order to break the for loop above
			if(AssignmentID[z] != 0)
			{
				if(z == AssignmentID.length - 1 || AssignmentID[z+1] == 0)
					command = command + AssignmentID[z];
				else
					command = command + AssignmentID[z] + ", ";
			}
			else
				z = AssignmentID.length;
		}*/

		command = command + ") ORDER BY RaterLoginID ";
		
		int TargetID = 0;
		String sFullname = "";
		String [] TargetDetail = new String[14];
		z = 0;
		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        
        	while(rs.next())
    		{
    			RaterID = rs.getInt("RaterLoginID");
    			TargetID = rs.getInt("TargetLoginID");
    			
    			TargetDetail = CE_Survey.getUserDetail_ADV(TargetID);
    			sFullname = TargetDetail[0]+" "+TargetDetail[1];
    			
    			sTargetList = sTargetList + "\t"+counter+". "+sFullname+"<br />";
    			counter++;
    			z++;
    		}
    		
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java - filPartTemplate - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }

	
		//Get template from database and store into content
		content = org.getEmailTemplate_SurvID(SurvID, iOpt);
		RaterDetail = CE_Survey.getUserDetail_ADV(RaterID);

		//Replace the tags "<>" in the template with the data
		/*
		 * Change(s) : Replace "&lt;XXX&gt;" instead of replacing <XXX>
		 * Reason(s) : Non-html tags are encoded as "&lt;" and "&gt;"
		 * Updated By: Gwen Oh
		 * Updated On: 19 Sep 2011
		 */
		content = content.replaceAll("&lt;Rater Name&gt;", RaterName);
		content = content.replaceAll("&lt;Deadline&gt;", Deadline);
		content = content.replaceAll("&lt;Targets List&gt;", sTargetList);
		content = content.replaceAll("&lt;Website&gt;", server.getWebsite_Hyperlink());
		content = content.replaceAll("&lt;Username&gt;", RaterDetail[2]);
		content = content.replaceAll("&lt;Password&gt;", RaterDetail[12]);
		content = content.replaceAll("&lt;Admin Email&gt;", server.getAdminEmail());
		
		return content;
	}
	
	
	public String fillPartTemplate(int iOpt, int SurvID, String RaterName, String Deadline, int [] AssignmentID)throws SQLException, Exception
	{
		int z = 0;
		int RaterID=0;
		int counter = 1;
		String sTargetList = "";
		String [] RaterDetail = new String[20];
		
		org = new Organization();

		//String command = "SELECT * FROM tblAssignment WHERE AssignmentID = "+AssignmentID;
		String command = "SELECT * FROM tblAssignment WHERE AssignmentID IN (";
		
		command = command + "SELECT d.AssignmentID FROM tblSurvey a, tblOrganization b, tblJobPosition c, tblAssignment d, [User] e ";
		command = command + "WHERE a.FKOrganization = b.PKOrganization AND a.JobPositionID = c.JobPositionID ";
		command = command + "AND a.SurveyID = d.SurveyID AND d.RaterLoginID = e.PKUser ";
		command = command + "AND a.SurveyStatus = 1 AND d.RaterStatus = 0 AND TargetLoginID <> 0 AND (a.SurveyID = " + SurvID + ") ";
		
		if(RaterID != 0)
			command = command + "AND (d.RaterLoginID = " + RaterID + ") ";
		
		command = command + ") ORDER BY d.RaterLoginID";
		
		/*for(z=0; z<AssignmentID.length; z++)
		{
			// Since we initialise AssignmentID to be 20 earlier on, the loop will be repeated 20 times even though there isn't any AssignmentID anymore
			// Therefore, when AssignmentID is 0, increase z to be the length of array in order to break the for loop above
			if(AssignmentID[z] != 0)
			{
				if(z == AssignmentID.length - 1 || AssignmentID[z+1] == 0)
					command = command + AssignmentID[z];
				else
					command = command + AssignmentID[z] + ", ";
			}
			else
				z = AssignmentID.length;
		}*/
			
		command = command + ")";
		
		int TargetID = 0;
		String sFullname = "";
		String [] TargetDetail = new String[14];
		z = 0;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        
        	while(rs.next())
    		{
    			RaterID = rs.getInt("RaterLoginID");
    			TargetID = rs.getInt("TargetLoginID");
    			
    			//System.out.println(AssignmentID + ". Getting user details for Target: " + TargetID);
    			TargetDetail = CE_Survey.getUserDetail_ADV(TargetID);
    			sFullname = TargetDetail[0]+" "+TargetDetail[1];
    			
    			sTargetList = sTargetList + "\t"+counter+". "+sFullname+"<br />";
    			counter++;
    			z++;
    		}
    		
        }
        catch(Exception E) 
        {
            System.err.println("EmailTemplate.java - filPartTemplate - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
		
		//Get template from database and store into content
		content = org.getEmailTemplate_SurvID(SurvID, iOpt);
		
		RaterDetail = CE_Survey.getUserDetail_ADV(RaterID);

		//Replace the tags "<>" in the template with the data
		/*
		 * Change(s) : Replace "&lt;XXX&gt;" instead of replacing <XXX>
		 * Reason(s) : Non-html tags are encoded as "&lt;" and "&gt;"
		 * Updated By: Gwen Oh
		 * Updated On: 19 Sep 2011
		 */
		content = content.replaceAll("&lt;Rater Name&gt;", RaterName);
		content = content.replaceAll("&lt;Deadline&gt;", Deadline);
		content = content.replaceAll("&lt;Targets List&gt;", sTargetList);
		content = content.replaceAll("&lt;Website&gt;", server.getWebsite_Hyperlink());
		content = content.replaceAll("&lt;Username&gt;", RaterDetail[2]);
		content = content.replaceAll("&lt;Password&gt;", RaterDetail[12]);
		content = content.replaceAll("&lt;Admin Email&gt;", server.getAdminEmail());
		
		return content;
	}
	
	public String getMessage()
	{
		String stemp = "EmailTemplate.java";
		return stemp;
	}
	
	//Added by Roger 26 June 2008
	//This method is to replace all strange unicode characters
	//(for example bullets to a unicode that browser can display
	/**
	 *	@param 	String text	
	 *	@return processed text
	 */	
	public String fixText(String text) {
		Hashtable ht = new Hashtable();
		//add more if necessary
		ht.put(new Integer(8216),"'");
		ht.put(new Integer(8217),"'");
		ht.put(new Integer(8226),"&#8226;");
		ht.put(new Integer(8211),"-");
		ht.put(new Integer(8220),"\"");
		ht.put(new Integer(8221),"\"");
		ht.put(new Integer(174),"&reg;"); //Denise 05/01/2009 to store the register trademark notation to the database
	
		String tempStr = "";
		for (int i=0; i<text.length(); i++) {
			char c = text.charAt(i);
			int unicode = c;
			String m = (String)ht.get(new Integer(unicode));
			if (m != null) {
				tempStr += m;
			} else {
				tempStr += c;
			}
		}		
		return tempStr;
		
	}
	
	/**
	  * To send a test email to the specified user email address 
	  * @param PkUser - Specify which user id to find the user's email address to send the test email
	  * @param Subject - Subject of the email
	  * @param Template - Content of the email
	  * @author Sebastian
	  * @since v.1.3.12.80 (21 July 2010)
	**/
	public boolean SendTestEmail(int PkUser, int PkOrg, String Subject, String Template)
	{
		boolean result = false;
		MailHTMLStd email = new MailHTMLStd();
		String userDetail[] = new String[13];
		String filledTemplate = "";
		
		try {
			userDetail = CE_Survey.getUserDetail(PkUser); //get the details of the user based on user id
			String to = userDetail[12]; //get the email address of the user
			
			//if email address is not empty, send test email
			if (!to.equals(""))
			{
				//fill the tags <> in the template with proper content
				filledTemplate = fillTestTemplate(Template, PkOrg);
				
				result = email.sendMail(server.getAdminEmail(), to, Subject, filledTemplate, 1);
			}
			//if email address is empty, fail to send
			else
			{
				result = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			result = false;
		}
		return result;
	}

	public static void main (String [] args)throws SQLException, Exception
	{
		EmailTemplate RC = new EmailTemplate();
		//System.out.println(RC.fillNomTemplate(0, 438, 2686, "27/11/80"));
		//System.out.println(RC.fillPartTemplate(3, 438, "YUTHANA KOTCHAHIRUN", "27/11/80", 4811));
		//String temp = RC.Footer();
		//System.out.println(temp);
		//	RC.Survey_Open_Participant("Wandy", "25/03/2004");
	
	}

}