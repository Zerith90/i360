/*
 * Author: Dai Yong
 * June 2013
 */
package Coach;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;
import CP_Classes.ConsultingCompany;
import CP_Classes.EventViewer;
import CP_Classes.Organization;
import CP_Classes.User;
import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voCoach;
import CP_Classes.vo.voCoachDate;
import CP_Classes.vo.voCoachSession;
import CP_Classes.vo.voCoachSessionDate;
import CP_Classes.vo.voCoachSlot;
import CP_Classes.vo.voCoachUserAssignment;
import CP_Classes.vo.voCoachVenue;
import CP_Classes.vo.voUser;
import CP_Classes.vo.votblConsultingCompany;
import CP_Classes.vo.votblOrganization;
import CP_Classes.vo.votblSurvey;

public class SessionSetup {
	
//	Start of EventViewer
	private EventViewer ev=new EventViewer();
	private int UserPK;
	private voUser userDetials;
	private votblConsultingCompany companyDetail;
	private votblOrganization votblOrganizationDetail;
	private Vector<voCoachDate> coachDates=new Vector<voCoachDate>();
		
	public int getUserPK() {
		return UserPK;
	}
	public void setUserPK(int userPK) {
		UserPK = userPK;
		User user=new User();
		ConsultingCompany consultingCompany=new ConsultingCompany();
		Organization organization=new Organization();
		userDetials=user.getUserInfo(UserPK);	
		companyDetail=consultingCompany.getConsultingCompany(userDetials.getFKCompanyID());
		votblOrganizationDetail=organization.getOrganization(userDetials.getFKOrganization());
	}
//	End of EventViewer
	
	
	int selectedOrganisation=0;
	int selectedSlotGroup=0;
	int selectedSlot=0;
	int selectedDayGroup=0;
	int selectedSession=0;
	int sessionPK=0;
	String sessionName="";
	int selectDateToEdit=0;
	int selectUser=0;
	int selectedUserAssignment=0;
	int selectedSurvey=0;
	int selectedCoach=0;
	int selectedVenue=0;
	int selectedDate=0;
	
	public int SortType;
	private int Toggle [];	// 0=ascending, 1=descending
	
	public int getSelectedUserAssignment() {
		return selectedUserAssignment;
	}

	public void setSelectedUserAssignment(int selectedUserAssignment) {
		this.selectedUserAssignment = selectedUserAssignment;
	}

	Vector<Integer> selectedCoaches=new Vector<Integer>();
	public SessionSetup() {
		super();
		Toggle = new int [5];

		for(int i=0; i<5; i++)
			Toggle[i] = 0;

		SortType = 1;
	}
	
	public String getUserNamebyID(int id){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		String userName="";
		String query="SELECT * from [User] where [User].PKUser="+id;
		//System.out.println(query);
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next())
			{	
				userName=rs.getString("LoginName");
				break;
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getUserNamebyID - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return userName;
	}
	
	public Vector<voUser> getUsersbyOrganizationID(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voUser> users=new Vector<voUser>();
		
		String query="SELECT * from [User] where [User].FKOrganization="+this.getSelectedOrganisation();
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				voUser vo = new voUser();
				vo.setPKUser(rs.getInt("PKUser"));
				vo.setLoginName(rs.getString("LoginName"));
				vo.setFamilyName(rs.getString("FamilyName"));
				vo.setGivenName(rs.getString("GivenName"));
				users.add(vo);
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getUsersbyOrganizationID - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return users;
	}
	public  voCoachUserAssignment getSelectedvoUserAssignment(int CoachUserAssignmentPK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		voCoachUserAssignment vo=new voCoachUserAssignment();
		
		String query="SELECT * from CoachAssignment where AssignmentPK="+CoachUserAssignmentPK;
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				vo.setAssignmentPK(rs.getInt("AssignmentPK"));
				vo.setStatus(rs.getInt("Status"));
				vo.setUserFK(rs.getInt("UserFK"));
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getSelectedvoUserAssignment - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return vo;
	}
	public Vector<voUser> getUsersbyOrganizationIDandSurveyID(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		//get surveyID using session Name
		Vector<voUser> users=new Vector<voUser>();
		
		String query="SELECT * ";
		query=query+" FROM tblAssignment INNER JOIN";
		query=query+" [User] ON [User].PKUser = tblAssignment.RaterLoginID";
		query=query+" WHERE (tblAssignment.SurveyID = "+this.getSurveyIDbySessionID()+") AND (tblAssignment.RaterCode = 'SELF')";
		query=query+" order by FamilyName";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				voUser vo = new voUser();
				vo.setPKUser(rs.getInt("PKUser"));
				vo.setLoginName(rs.getString("LoginName"));
				vo.setFamilyName(rs.getString("FamilyName"));
				vo.setGivenName(rs.getString("GivenName"));
				users.add(vo);
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getUsersbyOrganizationIDandSurveyID - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return users;
	}
	
	public int getSurveyIDbySessionID(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		int SurveyID=0;
		
		String query="SELECT * from CoachAssignment where SessionFK="+this.selectedSession;
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				SurveyID=rs.getInt("SurveyFK");
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getSurveyIDbySessionID - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return SurveyID;
	}
	
	public Vector<votblSurvey> getSurveybyOrganizationID(int FKOrganization){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<votblSurvey> surveys=new Vector<votblSurvey>();
		
		String query="SELECT * from tblSurvey where FKOrganization="+FKOrganization+" order by SurveyName";
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				votblSurvey vo = new votblSurvey();
				vo.setSurveyID(rs.getInt("SurveyID"));
				vo.setSurveyName(rs.getString("SurveyName"));
				surveys.add(vo);
			}
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getSurveybyOrganizationID - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return surveys;
		
	}
	public boolean updateUser(){
		
		int UserPK=0;
		String sql = "Update CoachAssignment Set UserFK = "+ this.selectUser +" where AssignmentPK = " + this.selectedUserAssignment;
		
		if(this.selectUser==-1){
			sql = "Update CoachAssignment Set UserFK = NULL where AssignmentPK = " + this.selectedUserAssignment;
			UserPK=this.getUserIDinAssignment(this.selectedUserAssignment);
		}
		Connection con = null;
		Statement st = null;
		 boolean bIsUpdated = false;
			try
			{
				con=ConnectionBean.getConnection();
				st=con.createStatement();
				int iSuccess = st.executeUpdate(sql);
				if(iSuccess!=0){
					bIsUpdated=true;
					Calendar today = Calendar.getInstance();
					if(this.getSessionCutOffDate(this.selectedUserAssignment)!=null){
						Calendar getDate =Calendar.getInstance();
						getDate.setTime(this.getSessionCutOffDate(this.selectedUserAssignment));
//						System.out.println("today"+today);
						if(today.after(getDate)){
							//System.out.println("sign up after cut-off");
							try {
								if(this.selectUser==-1){
//									System.out.println("this.getUserIDinAssignment(this.selectedUserAssignment)"+this.getUserIDinAssignment(this.selectedUserAssignment));
									AssignmentChangeEmail.unSignAssignment(this.selectedUserAssignment, UserPK);
								}else{
									
									AssignmentChangeEmail.signAssignment(this.selectedUserAssignment,this.selectUser);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							//System.out.println("sign up before cut-off");
						}
					}
					ev.addRecord("Update", "Update Candidate of User Assignment by Admin", "Update Candidate of User Assignment, AssignmentPK:"+this.selectedUserAssignment+" to user PK:"+this.selectUser, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
				}
			} 
			catch (Exception E)
			{
				System.err.println("SessionSetup.java - updateUser - " + E);
			}
			finally
			{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		return bIsUpdated;
	}
	public int getMaxSessionNumber(int PKCoachSession){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		int max=0;
		
		String query="SELECT * from CoachSession where PKCoachSession="+PKCoachSession;
//		System.out.println("getMaxSessionNumber: "+PKCoachSession);
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				max=rs.getInt("SessionMax");
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getMaxSessionNumber - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
//		System.out.println("getMaxSessionNumber: max:"+max);
		return max;
		
	}
	public boolean reachMax(int userPK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		int count=0;
		boolean reachMax=false;
		int maxNumber=this.getMaxSessionNumber(this.selectedSession);
		Vector<voCoachUserAssignment> allUserAssignment=this.getUserAssignment();
		for(int i=0;i<allUserAssignment.size();i++){
			int UserAssignmentPK=((voCoachUserAssignment)allUserAssignment.elementAt(i)).getAssignmentPK();
			String query="SELECT * from CoachAssignment where UserFK="+userPK+" and AssignmentPK="+UserAssignmentPK+" and SessionFK="+this.selectedSession;
			try
			{ 

				con=ConnectionBean.getConnection();
				st=con.createStatement();
				rs=st.executeQuery(query);
				
				while(rs.next()){
					//System.out.println("reach maxfound, SQL: "+query);
					count++;
				}				
			}
			catch(Exception E) 
			{
				System.err.println("SessionSetup.java - reachMax - " + E);
			}
			finally
			{
				ConnectionBean.closeRset(rs); //Close ResultSet
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		}
//		System.out.println("Count:"+count);
//		System.out.println("max:"+maxNumber);
		
		if(count>=maxNumber){
			reachMax=true;
//			System.out.println("reach max sessions");
		}
		
		return reachMax;
	}
	
	public boolean checkUserHasSigned(int UserAssignmentPK,int userPK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		
		boolean exist=false;
		String query="SELECT * from CoachAssignment where UserFK="+userPK+" and AssignmentPK="+UserAssignmentPK;
		//System.out.println(query);
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			if(rs.next())
				exist=true;
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - checkUserHasSigned - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return exist;
		
	}
	public boolean checkThatUserHasSigned(int UserAssignmentPK,int userPK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		int UserFK=-1;
		boolean signUp=false;
		String query="SELECT * from CoachAssignment where AssignmentPK="+UserAssignmentPK;
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				UserFK=rs.getInt("UserFK");
			}
			if(userPK==UserFK){
				signUp=true;
			}
			
			
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - checkUserHasSigned - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return signUp;
		
	}
	
	public boolean candidateSignUp(int UserAssignmentPK,int userPK){
		String sql = "Update CoachAssignment Set UserFK = "+ userPK +" where AssignmentPK = " + UserAssignmentPK;
		Connection con = null;
		Statement st = null;

		boolean bIsUpdated = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if(iSuccess!=0){
				bIsUpdated=true;
				Calendar today = Calendar.getInstance();
				//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				//					System.out.println("today:"+sdf.format(today.getTime()));
				//					System.out.println("get date:"+this.getSessionCutOffDate(UserAssignmentPK));

				if(this.getSessionCutOffDate(UserAssignmentPK)!=null){
					Calendar getDate =Calendar.getInstance();
					getDate.setTime(this.getSessionCutOffDate(UserAssignmentPK));
//					System.out.println("today"+today);
					if(today.after(getDate)){
						//						System.out.println("sign up after cut-off");
						try {
							AssignmentChangeEmail.signAssignment(UserAssignmentPK,userPK);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						//System.out.println("sign up before cut-off");
					}
				}
				try {
					ev.addRecord("Update", "Sign Coaching Assignment by Candidate", "Sign Coaching User Assignment, UserAssignmentPK:"+UserAssignmentPK+" by User PK:"+userPK, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());
				} catch (Exception e) {
					e.printStackTrace();
				}					
			}
		} 
		catch (Exception E)
		{
			System.err.println("SessionSetup.java - candidateSignUP - " + E);

		}
		finally
		{
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}

		return bIsUpdated;
	}
	public boolean candidateUnSign(int UserAssignmentPK,int userPK){
		String sql = "Update CoachAssignment Set UserFK = "+ null +" where AssignmentPK = " + UserAssignmentPK;
		Connection con = null;
		Statement st = null;
			
		 boolean bIsUpdated = false;
			try
			{
				con=ConnectionBean.getConnection();
				st=con.createStatement();
				int iSuccess = st.executeUpdate(sql);
				if(iSuccess!=0){
					bIsUpdated=true;
					Calendar today = Calendar.getInstance();
//					SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
//					System.out.println("today:"+sdf.format(today.getTime()));
//					System.out.println("get date:"+this.getSessionCutOffDate(UserAssignmentPK));
					if(this.getSessionCutOffDate(UserAssignmentPK)!=null){
					Calendar getDate =Calendar.getInstance();
					getDate.setTime(this.getSessionCutOffDate(UserAssignmentPK));
					if(today.after(getDate)){
//						System.out.println("sign up after cut-off");
						AssignmentChangeEmail.unSignAssignment(UserAssignmentPK,userPK);
					}else{
//						System.out.println("sign up before cut-off");
					}
					}
					ev.addRecord("Update", "Unsign Coaching Assignment by Candidate", "UnSign Coaching User Assignment, UserAssignmentPK:"+UserAssignmentPK+" by User PK:"+userPK, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
				}
			} 
			catch (Exception E)
			{
				System.err.println("SessionSetup.java - unSignByCandidate - " + E);
				
			}
			finally
			{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		
		return bIsUpdated;
		
	}
	
	
	public void closeSlot(int AssignmentPK){
		String sql = "Update CoachAssignment Set Status = "+ 0 +" where AssignmentPK = " + AssignmentPK;
		Connection con = null;
		Statement st = null;
			
			try
			{

				con=ConnectionBean.getConnection();
				st=con.createStatement();
				st.executeUpdate(sql);
				
	  		
			} 
			catch (Exception E)
			{
				System.err.println("SessionSetup.java - closeSlot - " + E);
				
			}

			finally
			{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		
	}
	public void openSlot(int AssignmentPK){
		String sql = "Update CoachAssignment Set Status = "+ 1 +" where AssignmentPK = " + AssignmentPK;
		Connection con = null;
		Statement st = null;
			
			try
			{

				con=ConnectionBean.getConnection();
				st=con.createStatement();
				st.executeUpdate(sql);
	  		
			} 
			catch (Exception E)
			{
				System.err.println("SessionSetup.java - openSlot - " + E);
				
			}

			finally
			{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		
	}
	public boolean deleteUserAssignment(int AssignmentPK){
//		System.out.println("entered");
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean suc=false;
		String query = "delete from CoachAssignment where AssignmentPK = " + AssignmentPK;
//		System.out.println(query);
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0){
				suc=true;
				Calendar today = Calendar.getInstance();
//				SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
//				System.out.println("today:"+sdf.format(today.getTime()));
//				System.out.println("get date:"+this.getSessionCutOffDate(UserAssignmentPK));
				if(this.getSessionCutOffDate(AssignmentPK)!=null){
				Calendar getDate =Calendar.getInstance();
				getDate.setTime(this.getSessionCutOffDate(AssignmentPK));
				if(today.after(getDate)){
//					System.out.println("sign up after cut-off");
//					System.out.println("unsign by admin after cut off");
					AssignmentChangeEmail.unSignAssignment(AssignmentPK,this.getUserIDinAssignment(AssignmentPK));
				}else{
//					System.out.println("sign up before cut-off");
				}
				}
				ev.addRecord("Delete", "Delete Coaching User Assignment", "Delete Coaching User Assignment with PK:"+AssignmentPK, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
			}
		
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - deleteUserAssignment - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return suc;
		
	}
	public int getUserIDinAssignment(int AssignmentPK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		int UserPK=0;
		String query="SELECT * FROM CoachAssignment where AssignmentPK = " + AssignmentPK;
//		System.out.println(query);
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				UserPK=rs.getInt("UserFK");
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getUserIDinAssignment - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return UserPK;
	}
	
	
	public Vector<voCoachSession> getSessionAllNames(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachSession> sessions=new Vector<voCoachSession>();
		String query="SELECT DISTINCT CoachSession.SessionName,CoachSession.SessionCutOffDate,CoachSession.SessionDescription, CoachSession.PKCoachSession,CoachSession.SessionMax FROM CoachAssignment INNER JOIN CoachSession ON CoachSession.PKCoachSession = CoachAssignment.SessionFK";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				voCoachSession vo = new voCoachSession();
				vo.setPK(rs.getInt("PKCoachSession"));
				vo.setName(rs.getString("SessionName"));
				vo.setDescription(rs.getString("SessionDescription"));
				vo.setSessionMax(rs.getInt("SessionMax"));
				vo.setCloseDate(rs.getDate("SessionCutOffDate"));
				sessions.add(vo);
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getSessionName - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return sessions;
		
	}
	
	public Vector<voCoachSession> getSessionAllNamesByORGID(int orgID){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachSession> sessions=new Vector<voCoachSession>();
		//only choose the open slot
		String query="SELECT DISTINCT CoachSession.SessionName, CoachSession.PKCoachSession FROM CoachAssignment INNER JOIN CoachSession ON CoachSession.PKCoachSession = CoachAssignment.SessionFK where OrgFK="+orgID+" and CoachAssignment.Status="+1;
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				voCoachSession vo = new voCoachSession();
				vo.setPK(rs.getInt("PKCoachSession"));
				vo.setName(rs.getString("SessionName"));
				sessions.add(vo);
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getSessionName - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return sessions;
		
	}
	
	public boolean validateUser(int orgID, int userID){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean allow=false;
		
		//only choose the open slot
		String query="SELECT     [User].PKUser";
				query=query+" FROM tblAssignment INNER JOIN";
				query=query+" [User] ON [User].PKUser = tblAssignment.RaterLoginID";
				query=query+" WHERE(tblAssignment.RaterCode = 'SELF') AND ([User].FKOrganization = "+orgID+")";
				//System.out.println(query);
				try
		{ 

					con=ConnectionBean.getConnection();
					st=con.createStatement();
					rs=st.executeQuery(query);
					while(rs.next())
					{	
						//System.out.println("user in db:"+rs.getInt("PKUser"));
						if(rs.getInt("PKUser")==userID){
							allow=true;
							//System.out.println("at "+i+" found user");
						}
					}

		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - validateUser - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}

		return allow;
		
		
	}
	public boolean validateUserInSideBar(int orgID, int userID){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean allow=false;
		
		
		
		//only choose the open slot
		String query="SELECT DISTINCT [User].PKUser";
				query=query+" FROM tblAssignment INNER JOIN";
				query=query+" [User] ON [User].PKUser = tblAssignment.RaterLoginID INNER JOIN CoachAssignment on [User].FKOrganization=CoachAssignment.OrgFK";
				query=query+" WHERE(tblAssignment.RaterCode = 'SELF') AND (tblAssignment.SurveyID=CoachAssignment.SurveyFK) AND ([User].FKOrganization = "+orgID+")";
				System.out.println(query);
				try
		{ 

					con=ConnectionBean.getConnection();
					st=con.createStatement();
					rs=st.executeQuery(query);
					while(rs.next())
					{	
						//System.out.println("user in db:"+rs.getInt("PKUser"));
						if(rs.getInt("PKUser")==userID){
							allow=true;
							//System.out.println("at "+i+" found user");
						}
					}

		}
		catch(Exception E) 
		{
			System.err.println("validateUserInSideBar.java - validateUser - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}

		return allow;
		
		
	}
	public Vector<voCoachSession> getSessionAllNamesUsedbyCandidate(int orgID){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachSession> sessions=new Vector<voCoachSession>();
		//only choose the open slot
		String query="SELECT DISTINCT CoachSession.SessionName, CoachSession.PKCoachSession";
		query=query+" FROM tblAssignment INNER JOIN";
		query=query+" [User] ON [User].PKUser = tblAssignment.RaterLoginID INNER JOIN";
		query=query+" CoachAssignment ON tblAssignment.SurveyID = CoachAssignment.SurveyFK INNER JOIN";
		query=query+" CoachSession ON CoachSession.PKCoachSession = CoachAssignment.SessionFK";
		query=query+" WHERE(tblAssignment.RaterCode = 'SELF') AND ([User].FKOrganization = "+orgID+")";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				voCoachSession vo = new voCoachSession();
				vo.setPK(rs.getInt("PKCoachSession"));
				vo.setName(rs.getString("SessionName"));
				sessions.add(vo);
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getSessionName - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return sessions;
		
	}
	public Vector<voCoachUserAssignment> getUserAssignment(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachUserAssignment> userAssignments=new Vector<voCoachUserAssignment>();
		String query="SELECT CoachAssignment.AssignmentPK, CoachAssignment.Status,CoachAssignment.VenueFK, CoachSession.SessionName,CoachDate.Date, Coach.CoachName, Coach.Link, Coach.UploadFile, tblOrganization.OrganizationName, CoachAssignment.OrgFK,"; 
		query=query+ " CoachSlot.StartingTime, CoachSlot.EndingTime, CoachAssignment.UserFK";
		query=query+" FROM  CoachAssignment INNER JOIN";
		query=query+" CoachSession ON CoachSession.PKCoachSession = CoachAssignment.SessionFK INNER JOIN";
		query=query+" CoachDate ON CoachDate.PKCoachDate = CoachAssignment.DateFK INNER JOIN";
		query=query+" CoachSlot ON CoachSlot.CoachSlotPK = CoachAssignment.SlotFK INNER JOIN";
		query=query+" Coach ON Coach.PKCoach = CoachAssignment.CoachFK INNER JOIN";
		query=query+" tblOrganization ON tblOrganization.PKOrganization = CoachAssignment.OrgFK";
		query=query+" WHERE (CoachAssignment.SessionFK = "+this.getSelectedSession()+")";
		query=query+" ORDER BY ";
		//System.out.println(query);
		
		if(SortType == 1)
			query = query + " CoachAssignment.DateFK";
		else if(SortType == 2)
			query = query + " StartingTime";
		else if(SortType == 3)
			query = query + " CoachName";
		else if(SortType == 4)
			query = query + "Status";
		else if(SortType == 5)
			query = query + "EndingTime";
		
		if(Toggle[SortType - 1] == 1)
			query = query + " DESC";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
                 
				voCoachUserAssignment vo = new voCoachUserAssignment();
				vo.setAssignmentPK(rs.getInt("AssignmentPK"));
				vo.setCoachName(rs.getString("CoachName"));
				vo.setDate(rs.getString("Date"));
				vo.setEndingTime(rs.getInt("EndingTime"));
				vo.setOrganizationName(rs.getString("OrganizationName"));
				vo.setStartingTime(rs.getInt("StartingTime"));
				vo.setStatus(rs.getInt("Status"));
				vo.setUserFK(rs.getInt("UserFK"));
				vo.setOrgID(rs.getInt("OrgFK"));
				vo.setCoachLink(rs.getString("Link"));
				vo.setCoachFileName(rs.getString("UploadFile"));
				vo.setVenueFK(rs.getInt("VenueFK"));
//				vo.setSessionVenue(rs.getString("SessionVenue"));
				userAssignments.add(vo);
				
				//set for later use
				this.setSelectedOrganisation(rs.getInt("OrgFK"));
			}
			
			//System.out.println("userAssignments size"+userAssignments);
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getUserAssignment - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return userAssignments;
		
	}
	public Vector<voCoachUserAssignment> getUserAssignment(int datePK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachUserAssignment> userAssignments=new Vector<voCoachUserAssignment>();
		String query="SELECT CoachAssignment.AssignmentPK, CoachAssignment.Status,CoachAssignment.VenueFK, CoachSession.SessionName,CoachDate.Date, Coach.CoachName, Coach.Link, Coach.UploadFile, tblOrganization.OrganizationName, CoachAssignment.OrgFK,"; 
		query=query+ " CoachSlot.StartingTime, CoachSlot.EndingTime, CoachAssignment.UserFK";
		query=query+" FROM  CoachAssignment INNER JOIN";
		query=query+" CoachSession ON CoachSession.PKCoachSession = CoachAssignment.SessionFK INNER JOIN";
		query=query+" CoachDate ON CoachDate.PKCoachDate = CoachAssignment.DateFK INNER JOIN";
		query=query+" CoachSlot ON CoachSlot.CoachSlotPK = CoachAssignment.SlotFK INNER JOIN";
		query=query+" Coach ON Coach.PKCoach = CoachAssignment.CoachFK INNER JOIN";
		query=query+" tblOrganization ON tblOrganization.PKOrganization = CoachAssignment.OrgFK";
		query=query+" WHERE (CoachAssignment.SessionFK = "+this.getSelectedSession()+" and PKCoachDate="+datePK+")";
		query=query+" ORDER BY ";
		//System.out.println(query);
		
		if(SortType == 1)
			query = query + " CoachAssignment.DateFK";
		else if(SortType == 2)
			query = query + " StartingTime";
		else if(SortType == 3)
			query = query + " CoachName";
		else if(SortType == 4)
			query = query + "Status";
		else if(SortType == 5)
			query = query + "EndingTime";
		
		if(Toggle[SortType - 1] == 1)
			query = query + " DESC";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
                 
				voCoachUserAssignment vo = new voCoachUserAssignment();
				vo.setAssignmentPK(rs.getInt("AssignmentPK"));
				vo.setCoachName(rs.getString("CoachName"));
				vo.setDate(rs.getString("Date"));
				vo.setEndingTime(rs.getInt("EndingTime"));
				vo.setOrganizationName(rs.getString("OrganizationName"));
				vo.setStartingTime(rs.getInt("StartingTime"));
				vo.setStatus(rs.getInt("Status"));
				vo.setUserFK(rs.getInt("UserFK"));
				vo.setOrgID(rs.getInt("OrgFK"));
				vo.setCoachLink(rs.getString("Link"));
				vo.setCoachFileName(rs.getString("UploadFile"));
				vo.setVenueFK(rs.getInt("VenueFK"));
//				vo.setSessionVenue(rs.getString("SessionVenue"));
				userAssignments.add(vo);
				
				//set for later use
				this.setSelectedOrganisation(rs.getInt("OrgFK"));
			}
			
			//System.out.println("userAssignments size"+userAssignments);
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getUserAssignment - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return userAssignments;
		
	}
	public Vector<voCoachUserAssignment> getUserAssignmentBYCandidate(){
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachUserAssignment> userAssignments=new Vector<voCoachUserAssignment>();
		String query="SELECT CoachAssignment.AssignmentPK,CoachAssignment.Status,CoachAssignment.VenueFK, CoachAssignment.Status, CoachSession.SessionName,CoachDate.Date, Coach.CoachName, Coach.Link, Coach.UploadFile, tblOrganization.OrganizationName, CoachAssignment.OrgFK,"; 
		query=query+ " CoachSlot.StartingTime, CoachSlot.EndingTime, CoachAssignment.UserFK";
		query=query+" FROM  CoachAssignment INNER JOIN";
		query=query+" CoachSession ON CoachSession.PKCoachSession = CoachAssignment.SessionFK INNER JOIN";
		query=query+" CoachDate ON CoachDate.PKCoachDate = CoachAssignment.DateFK INNER JOIN";
		query=query+" CoachSlot ON CoachSlot.CoachSlotPK = CoachAssignment.SlotFK INNER JOIN";
		query=query+" Coach ON Coach.PKCoach = CoachAssignment.CoachFK INNER JOIN";
		query=query+" tblOrganization ON tblOrganization.PKOrganization = CoachAssignment.OrgFK";
		query=query+" WHERE (CoachAssignment.SessionFK = "+this.getSelectedSession()+" and Status=1 )";
		query=query+" ORDER BY ";
		//System.out.println(query);
		
		if(SortType == 1)
			query = query + " Date";
		else if(SortType == 2)
			query = query + " StartingTime";
		else if(SortType == 3)
			query = query + " CoachName";
		else if(SortType == 4)
			query = query + "Status";
		else if(SortType == 5)
			query = query + "EndingTime";
		
		if(Toggle[SortType - 1] == 1)
			query = query + " DESC";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
                 
				voCoachUserAssignment vo = new voCoachUserAssignment();
				vo.setAssignmentPK(rs.getInt("AssignmentPK"));
				vo.setCoachName(rs.getString("CoachName"));
				vo.setDate(rs.getString("Date"));
				vo.setEndingTime(rs.getInt("EndingTime"));
				vo.setOrganizationName(rs.getString("OrganizationName"));
				vo.setStartingTime(rs.getInt("StartingTime"));
				vo.setStatus(rs.getInt("Status"));
				vo.setUserFK(rs.getInt("UserFK"));
				vo.setOrgID(rs.getInt("OrgFK"));
				vo.setCoachLink(rs.getString("Link"));
				vo.setCoachFileName(rs.getString("UploadFile"));
				vo.setVenueFK(rs.getInt("VenueFK"));
//				vo.setSessionVenue(rs.getString("SessionVenue"));
				userAssignments.add(vo);
				//set for later use
				this.setSelectedOrganisation(rs.getInt("OrgFK"));
			}		
			//System.out.println("userAssignments size"+userAssignments);
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getUserAssignmentBYCandidate - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return userAssignments;
		
	}
public Vector<voCoachUserAssignment> getUserAssignmentBYCandidate(int datePK){
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachUserAssignment> userAssignments=new Vector<voCoachUserAssignment>();
		String query="SELECT CoachAssignment.AssignmentPK,CoachAssignment.Status,CoachAssignment.VenueFK, CoachAssignment.Status, CoachSession.SessionName,CoachDate.Date,CoachDate.PKCoachDate, Coach.CoachName, Coach.Link, Coach.UploadFile, tblOrganization.OrganizationName, CoachAssignment.OrgFK,"; 
		query=query+ " CoachSlot.StartingTime, CoachSlot.EndingTime, CoachAssignment.UserFK";
		query=query+" FROM  CoachAssignment INNER JOIN";
		query=query+" CoachSession ON CoachSession.PKCoachSession = CoachAssignment.SessionFK INNER JOIN";
		query=query+" CoachDate ON CoachDate.PKCoachDate = CoachAssignment.DateFK INNER JOIN";
		query=query+" CoachSlot ON CoachSlot.CoachSlotPK = CoachAssignment.SlotFK INNER JOIN";
		query=query+" Coach ON Coach.PKCoach = CoachAssignment.CoachFK INNER JOIN";
		query=query+" tblOrganization ON tblOrganization.PKOrganization = CoachAssignment.OrgFK";
		query=query+" WHERE (CoachAssignment.SessionFK = "+this.getSelectedSession()+" and Status=1 and PKCoachDate="+datePK+")";
		query=query+" ORDER BY ";
		//System.out.println(query);
		
		if(SortType == 1)
			query = query + " Date";
		else if(SortType == 2)
			query = query + " StartingTime";
		else if(SortType == 3)
			query = query + " CoachName";
		else if(SortType == 4)
			query = query + "Status";
		else if(SortType == 5)
			query = query + "EndingTime";
		
		if(Toggle[SortType - 1] == 1)
			query = query + " DESC";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
                 
				voCoachUserAssignment vo = new voCoachUserAssignment();
				vo.setAssignmentPK(rs.getInt("AssignmentPK"));
				vo.setCoachName(rs.getString("CoachName"));
				vo.setDate(rs.getString("Date"));
				vo.setEndingTime(rs.getInt("EndingTime"));
				vo.setOrganizationName(rs.getString("OrganizationName"));
				vo.setStartingTime(rs.getInt("StartingTime"));
				vo.setStatus(rs.getInt("Status"));
				vo.setUserFK(rs.getInt("UserFK"));
				vo.setOrgID(rs.getInt("OrgFK"));
				vo.setCoachLink(rs.getString("Link"));
				vo.setCoachFileName(rs.getString("UploadFile"));
				vo.setVenueFK(rs.getInt("VenueFK"));
//				vo.setSessionVenue(rs.getString("SessionVenue"));
				userAssignments.add(vo);
				//set for later use
				this.setSelectedOrganisation(rs.getInt("OrgFK"));
			}		
			//System.out.println("userAssignments size"+userAssignments.size());
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getUserAssignmentBYCandidate - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return userAssignments;
		
	}
	public Vector<voCoachDate> getUserAssignmentDatesBYCandidate(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachDate> assingmentDates=new Vector<voCoachDate>();
		String query="SELECT distinct CoachDate.Date,CoachDate.PKCoachDate";
		query=query+" FROM  CoachAssignment INNER JOIN";
		query=query+" CoachDate ON CoachDate.PKCoachDate = CoachAssignment.DateFK ";
		query=query+" WHERE CoachAssignment.SessionFK = "+this.getSelectedSession();
		query=query+" ORDER BY CoachDate.Date";
//		System.out.println(query);
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
                 
				voCoachDate vo = new voCoachDate();
				vo.setPK(rs.getInt("PKCoachDate"));
				vo.setDate(rs.getString("Date"));
				assingmentDates.add(vo);
			}		
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getUserAssignmentDatesBYCandidate - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return assingmentDates;
		
	}
	public class voCoachUserAssignmentComparable implements Comparator<voCoachUserAssignment>{
		@Override
		public int compare(voCoachUserAssignment o1, voCoachUserAssignment o2) {
			System.out.println("Compare: "+o1.getCal().getTime()+" with "+o2.getCal().getTime()+":"+o1.getCal().getTime().compareTo(o2.getCal().getTime()));
		return o1.getCal().getTime().compareTo(o2.getCal().getTime());
		}
	} 
	public void generateScheduleForm(){
		System.out.println("called");
		//use sessionPK to get all the session
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachSessionDate> sessionsDates=new Vector<voCoachSessionDate>();
		String query="Select * from CoachSessionDate where SessionFK="+this.getSessionPK();
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				voCoachSessionDate vo = new voCoachSessionDate();
				vo.setPK(rs.getInt("PK"));
				vo.setCoachFK(rs.getInt("CoachFK"));
				vo.setDateFK(rs.getInt("DateFK"));
				vo.setSessionFK(rs.getInt("SessionFK"));
				sessionsDates.add(vo);
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - generateScheduleForm1 - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		//System.out.println("sessionsDates size"+sessionsDates.size());
		//use slot Group PK to get all the slot group
		Connection con2 = null;
		Statement st2 = null;
		ResultSet rs2 = null;
		System.out.println("called 2");
		Vector<voCoachSlot> slots=new Vector<voCoachSlot>();
		String query2="Select * from CoachSlot where FKCoachSlotGroup="+this.getSelectedSlotGroup();
		try
		{ 

			con2=ConnectionBean.getConnection();
			st2=con2.createStatement();
			rs2=st2.executeQuery(query2);

			while(rs2.next())
			{	
				voCoachSlot vo = new voCoachSlot();
				vo.setPK(rs2.getInt("CoachSlotPK"));
				vo.setFKCoachSlotGroup(rs2.getInt("FKCoachSlotGroup"));
				vo.setStartingtime(rs2.getInt("StartingTime"));
				vo.setEndingtime(rs2.getInt("EndingTime"));
				slots.add(vo);
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - generateScheduleForm2 - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs2); //Close ResultSet
			ConnectionBean.closeStmt(st2); //Close statement
			ConnectionBean.close(con2); //Close connection
		}
		
		//System.out.println("slots size"+slots.size());
		System.out.println("called3");
		//two for loop to get all the combinations
		int slotPK;
		Connection con3 = null;
		Statement st3 = null;
		String query3;
//		System.out.println("slot.size+"+slots.size());
//		System.out.println("sessionsDates.size+"+sessionsDates.size());
		for(int j=0;j<sessionsDates.size();j++){
			//get venueFK
			int venueFK=this.getSessionVenueFK(sessionsDates.elementAt(j));
			//System.out.println("venue FK:"+venueFK);
			
			for(int i=0;i<slots.size();i++){
				slotPK=slots.elementAt(i).getPK();
				if(venueFK==0){
					query3="INSERT INTO CoachAssignment (DateFK,SlotFK,CoachFK,OrgFK,Status,SessionFK,SurveyFK) VALUES ("+ sessionsDates.elementAt(j).getDateFK()+" ," + slotPK +","+sessionsDates.elementAt(j).getCoachFK()+" ," + this.getSelectedOrganisation() +" ," + 1 +"," + this.getSessionPK() +"," + this.selectedSurvey +")";
				}
				else{
					query3="INSERT INTO CoachAssignment (DateFK,SlotFK,CoachFK,OrgFK,Status,SessionFK,SurveyFK,VenueFK) VALUES ("+ sessionsDates.elementAt(j).getDateFK()+" ," + slotPK +","+sessionsDates.elementAt(j).getCoachFK()+" ," + this.getSelectedOrganisation() +" ," + 1 +"," + this.getSessionPK() +"," + this.selectedSurvey +","+venueFK+")";
				}
//				System.out.println("Check Query 3"+query3);
				try
				{ 
					con3=ConnectionBean.getConnection();
					st3=con3.createStatement();
					st3.executeUpdate(query3);
				}
				catch(Exception E) 
				{
					System.err.println("SessionSetup.java - generateScheduleForm3 - " + E);
				}
				finally
				{
					ConnectionBean.closeStmt(st3); //Close statement
					ConnectionBean.close(con3); //Close connection
				}
			}
		}
	}
	public int getSessionVenueFK(voCoachSessionDate sessionDate){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		int venueFK=0;
		int sessionFK=sessionDate.getSessionFK();
		int dateFK=sessionDate.getDateFK();
		String query = "SELECT * from CoachSessionDateVenue WHERE FKSession =  "+sessionFK+" and FKDate="+dateFK;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				venueFK=rs.getInt("FKVenue");
				break;
			}
		}catch(SQLException SE)
		{
			System.err.println("SessionSetup.java - getSessionVenueFK - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return venueFK;
	}
	
	public SessionSetup(int selectedOrganisation, int selectedSlotGroup,
			int selectedDayGroup, Vector<Integer> selectedCoaches) {
		super();
		this.selectedOrganisation = selectedOrganisation;
		this.selectedSlotGroup = selectedSlotGroup;
		this.selectedDayGroup = selectedDayGroup;
		this.selectedCoaches = selectedCoaches;
	}
	public Vector<voCoach> getCoachBySessionIDandDateID(int dateID){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoach> coaches=new Vector<voCoach>();
		String query="Select * from CoachSessionDate where SessionFK="+this.getSessionPK()+" and DateFK="+dateID;
//		System.out.println(query);
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{
				int coachFK=rs.getInt("CoachFK");
				voCoach vo = new voCoach();
				vo=getVoCoachbyCoachID(coachFK);
				coaches.add(vo);
			}
			//System.out.println("===================");
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getCoachBySessionIDandDateID - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return coaches;
		
	}
	
	public voCoach getVoCoachbyCoachID(int coachID){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		voCoach vo = new voCoach();
		
		String query="Select * from Coach where PKCoach="+coachID;
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			

			while(rs.next())
			{	
				
				vo.setPk(rs.getInt("PKCoach"));
				vo.setLink(rs.getString("Link"));
				vo.setCoachName(rs.getString("CoachName"));				
//				System.out.println("Coach name:"+rs.getString("CoachName"));
				break;
				
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getVoCoachbyCoachID - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return vo;
	}
	public Vector<voCoachSession> getAllSession(){
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachSession> sessions=new Vector<voCoachSession>();
		String query="Select * from CoachSession";
		
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next())
			{	
				
				voCoachSession vo = new voCoachSession();
				vo.setPK(rs.getInt("PKCoachSession"));
				vo.setName(rs.getString("SessionName"));
				vo.setDescription(rs.getString("SessionDescription"));
				vo.setSessionMax(rs.getInt("SessionMax"));
				vo.setCloseDate(rs.getDate("SessionCutOffDate"));
				sessions.add(vo);
				
				
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getSelectedCoach - " + E);
			E.printStackTrace();
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return sessions;
	}
	public boolean addSessionCoach(int DateFK, int CoachFK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean suc=false;
		int sessionFK=this.getSessionPK();
		String query = "INSERT INTO CoachSessionDate (SessionFK,DateFK,CoachFK) VALUES ("+ sessionFK+" ," + DateFK +","+CoachFK+")";
//		System.out.println(query);
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
				suc=true;
		
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - addSessionCoach - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return suc;
	}
	public boolean deleteSessionCoach(int DateFK, int CoachFK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean suc=false;
		String query = "delete from CoachSessionDate where SessionFK="+this.getSessionPK()+" and DateFK="+DateFK+" and CoachFK="+CoachFK;
//		System.out.println(query);
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
				suc=true;
		
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - addSessionCoach - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return suc;
	}
	public boolean addSession(String SessionName, String SessionDescription, int SessionMax, Date cutOffDate){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean suc=false;
//		System.out.println("cutOffDate:"+cutOffDate);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		
//		System.out.println("Formatted cutOffDate:"+sdf.format(cutOffDate));
		String query = "INSERT INTO CoachSession (SessionName,SessionDescription,SessionMax,SessionCutOffDate) VALUES ('"+ SessionName+"' ,'" + SessionDescription +"',"+SessionMax+",'" + sdf.format(cutOffDate) +"')";
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0){
				suc=true;
//				ev.addRecord("Insert", "Insert Coaching Session", "Insert Coaching Session, Coaching Session Name:"+SessionName, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
			}
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - addSession - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return suc;
	}
	
	public int getSessionPKbyNameAndDes(String SessionName, String SessionDescription){
		String query = "SELECT * from CoachSession WHERE SessionName = '" + SessionName+"' and SessionDescription='"+SessionDescription+"'";
		//System.out.println(query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		int PK=0;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				PK=rs.getInt("PKCoachSession");
				break;
			}
		}catch(SQLException SE)
		{
			System.err.println("SessionSetup.java - getSessionPKbyNameAndDes - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return PK;
	}
	public boolean updateUserAssignmentVenue(int userAssignmentPK, int venueFK){
		String sql = "UPDATE CoachAssignment SET VenueFK = '" + venueFK +"' WHERE AssignmentPK = " +userAssignmentPK;
		Connection con = null;
		Statement st = null;
        boolean bIsUpdated = false;
			try
			{
				con=ConnectionBean.getConnection();
				st=con.createStatement();
				int iSuccess = st.executeUpdate(sql);
				if(iSuccess!=0){
					bIsUpdated=true;
					ev.addRecord("Update", "Update Coaching User Assignment's Venue", "Update Coaching User Assignment's Venue of AssignmentPK:"+userAssignmentPK+" to Venue PK:"+venueFK, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
				}
			} 
			catch (Exception E)
			{
				System.err.println("SessionSetup.java - updateUserAssignmentVenue - " + E);
				
			}
			finally
			{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		
		return bIsUpdated;
	}
	public boolean updateUserAssignmentCoach(int userAssignmentPK, int coachFK){
		String sql = "UPDATE CoachAssignment SET CoachFK = '" + coachFK +"' WHERE AssignmentPK = " +userAssignmentPK;
		Connection con = null;
		Statement st = null;
        boolean bIsUpdated = false;
			try
			{
				con=ConnectionBean.getConnection();
				st=con.createStatement();
				int iSuccess = st.executeUpdate(sql);
				if(iSuccess!=0){
					bIsUpdated=true;
					ev.addRecord("Update", "Update Coaching User Assignment's Coach", "Update Coaching User Assignment's Coach of AssignmentPK:"+userAssignmentPK+" to Coach PK:"+coachFK, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
				}
			} 
			catch (Exception E)
			{
				System.err.println("SessionSetup.java - updateUserAssignmentCoach - " + E);
				
			}
			finally
			{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		
		return bIsUpdated;
	}
	public voCoachVenue getSessionVenue(int DateFK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String query = "SELECT * from CoachSessionDateVenue WHERE FKSession =  "+ this.getSessionPK()+" and FKDate="+DateFK;
		voCoachVenue vo = new voCoachVenue();
		int venueFK=0;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				venueFK=rs.getInt("FKVenue");
				break;
			}
			CoachVenue coachVenue=new CoachVenue();
			vo=coachVenue.getSelectedCoachVenue(venueFK);
		}catch(SQLException SE)
		{
			System.err.println("SessionSetup.java - getSessionVenue - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return vo;
	}
	public boolean CheckSessionVenueExist(int DateFK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean exist=false;
		String query = "SELECT * from CoachSessionDateVenue WHERE FKSession =  "+ this.getSessionPK()+" and FKDate="+DateFK;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				exist=true;
				break;
			}
		}catch(SQLException SE)
		{
			System.err.println("SessionSetup.java - CheckSessionVenueExist - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return exist;
	}
	public boolean addSessionVenue(int DateFK, int FKVenue){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean suc=false;
		String query = "INSERT INTO CoachSessionDateVenue (FKSession,FKDate,FKVenue) VALUES ('"+ this.getSessionPK()+"' ,'" + DateFK +"',"+FKVenue+")";
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
				suc=true;
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - addSessionVenue - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return suc;
	}
	public boolean updateSessionVenue(int DateFK, int FKVenue){
		String sql = "UPDATE CoachSessionDateVenue SET FKVenue = '" + FKVenue +"' WHERE FKSession = " + this.getSessionPK()+" and FKDate="+DateFK;
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
			catch (Exception E)
			{
				System.err.println("SessionSetup.java - updateSessionVenue - " + E);
				
			}
			finally
			{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		
		return bIsUpdated;
	}
	public boolean updateSessionCutOffDate(Date cutOffDate, int PKCoachSession){
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		String sql = "UPDATE CoachSession SET SessionCutOffDate = '" + sdf.format(cutOffDate) +"' WHERE PKCoachSession = " + this.getSelectedSession();
//		System.out.println("sql"+sql);
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
			catch (Exception E)
			{
				System.err.println("SessionSetup.java - updateSessionCutOffDate - " + E);
				
			}
			finally
			{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		
		return bIsUpdated;
	}
	public boolean deleteSessionVenue(int DateFK, int FKVenue){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean suc=false;
		String query = "delete from CoachSessionDateVenue where FKSession="+this.getSessionPK()+" and FKDate="+DateFK+" and FKVenue="+FKVenue;
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
				suc=true;
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - deleteSessionVenue - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return suc;
	}
	public Date getSessionCutOffDate(int assignmentPK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String query = "SELECT * FROM CoachSession INNER JOIN ";
        		query=query+" CoachAssignment ON CoachAssignment.SessionFK = CoachSession.PKCoachSession";
        		query=query+" WHERE (CoachAssignment.AssignmentPK = "+assignmentPK+")";
		Date SessionCutOffDate=new Date();
		//System.out.println("query:"+query);
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				SessionCutOffDate=rs.getDate("SessionCutOffDate");
//				System.out.println("SessionCutOffDate"+SessionCutOffDate);
				break;
			}
		}catch(SQLException SE)
		{
			System.err.println("SessionSetup.java - getSessionCloseDate - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return SessionCutOffDate;
	}
	public Date getSessionCutOffDateBySessionID(int PKCoachSession){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String query = "SELECT * FROM CoachSession where PKCoachSession="+PKCoachSession; 
        		
		Date SessionCutOffDate=new Date();
		//System.out.println("query:"+query);
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				SessionCutOffDate=rs.getDate("SessionCutOffDate");
				//System.out.println("SessionCutOffDate"+SessionCutOffDate);
				break;
			}
		}catch(SQLException SE)
		{
			System.err.println("SessionSetup.java - getSessionCloseDate - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return SessionCutOffDate;
	}
	public boolean CheckOrgHasSession(int id){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		String query="SELECT * from CoachAssignment where OrgFK="+id;
		boolean has=false;
		//System.out.println(query);
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next())
			{	
				has=true;
				break;
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - CheckOrgHasSession - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return has;
	}
	public boolean AddSlotToUserAssignment(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean suc=false;
		String query;
		int orgID=this.getOrgPKForAddSlotToUserAssignment(this.getSelectedSession());
		int SurveyID=this.getSurveyPKForAddSlotToUserAssignment(this.getSelectedSession());
		if(this.getSelectedCoach()==0&&this.getSelectedVenue()==0){
			 query="INSERT INTO CoachAssignment (DateFK,SlotFK,OrgFK,Status,SessionFK,SurveyFK) VALUES ("+ this.getSelectedDate()+" ," + this.getSelectedSlot() +" ," + orgID +" ," + 1 +"," + this.getSelectedSession() +"," + SurveyID +")";
		}else if(this.getSelectedCoach()==0&&this.getSelectedVenue()!=0){
			 query="INSERT INTO CoachAssignment (DateFK,SlotFK,OrgFK,Status,SessionFK,SurveyFK,VenueFK) VALUES ("+ this.getSelectedDate()+" ," + this.getSelectedSlot() +" ," + orgID +" ," + 1 +"," + this.getSelectedSession() +"," + SurveyID +"," + this.getSelectedVenue() +")";
		}else if(this.getSelectedCoach()!=0&&this.getSelectedVenue()==0){
			 query="INSERT INTO CoachAssignment (DateFK,SlotFK,CoachFK,OrgFK,Status,SessionFK,SurveyFK) VALUES ("+ this.getSelectedDate()+" ," + this.getSelectedSlot() +","+this.getSelectedCoach()+" ," + orgID +" ," + 1 +"," + this.getSelectedSession() +"," + SurveyID +")";
		}else{
			 query="INSERT INTO CoachAssignment (DateFK,SlotFK,CoachFK,OrgFK,Status,SessionFK,SurveyFK,VenueFK) VALUES ("+ this.getSelectedDate()+" ," + this.getSelectedSlot() +","+this.getSelectedCoach()+" ," + orgID +" ," + 1 +"," + this.getSelectedSession() +"," + SurveyID +"," + this.getSelectedVenue() +")";
		}
//        System.out.println(query);
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
				suc=true;
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - AddSlotToUserAssignment - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return suc;
	}
	public int getOrgPKForAddSlotToUserAssignment(int SessionID){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		String query="SELECT * from CoachAssignment where SessionFK="+SessionID;
		int orgPK=0;
		//System.out.println(query);
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next())
			{	
				orgPK=rs.getInt("OrgFK");
				break;
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getOrgPKForAddSlotToUserAssignment - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return orgPK;
	}
	public int getSurveyPKForAddSlotToUserAssignment(int SessionID){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		String query="SELECT * from CoachAssignment where SessionFK="+SessionID;
		int surveyPK=0;
		//System.out.println(query);
		try
		{ 
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next())
			{	
				surveyPK=rs.getInt("SurveyFK");
				break;
			}
			
		}
		catch(Exception E) 
		{
			System.err.println("SessionSetup.java - getSurveyPKForAddSlotToUserAssignment - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return surveyPK;
	}
	public int getSelectedOrganisation() {
		return selectedOrganisation;
	}
	public void setSelectedOrganisation(int selectedOrganisation) {
		this.selectedOrganisation = selectedOrganisation;
	}
	public int getSelectedSlotGroup() {
		return selectedSlotGroup;
	}
	public void setSelectedSlotGroup(int selectedSlotGroup) {
		this.selectedSlotGroup = selectedSlotGroup;
	}
	public int getSelectedDayGroup() {
		return selectedDayGroup;
	}
	public void setSelectedDayGroup(int selectedDayGroup) {
		this.selectedDayGroup = selectedDayGroup;
	}
	public Vector<Integer> getSelectedCoaches() {
		return selectedCoaches;
	}
	public void setSelectedCoaches(Vector<Integer> selectedCoaches) {
		this.selectedCoaches = selectedCoaches;
	}
	public int getSessionPK() {
		return sessionPK;
	}
	public void setSessionPK(int sessionPK) {
		this.sessionPK = sessionPK;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public int getSelectDateToEdit() {
		return selectDateToEdit;
	}
	public void setSelectDateToEdit(int selectDateToEdit) {
		this.selectDateToEdit = selectDateToEdit;
	}
	public int getSelectedSession() {
		return selectedSession;
	}
	public void setSelectedSession(int selectedSession) {
		this.selectedSession = selectedSession;
		this.coachDates=this.getUserAssignmentDatesBYCandidate();
	}
	public int getSelectUser() {
		return selectUser;
	}
	public void setSelectUser(int selectUser) {
		//System.out.println("Set");
		this.selectUser = selectUser;
	}
	public int getSelectedSurvey() {
		return selectedSurvey;
	}
	public void setSelectedSurvey(int selectedSurvey) {
		this.selectedSurvey = selectedSurvey;
	}
	public int getSelectedCoach() {
		return selectedCoach;
	}
	public void setSelectedCoach(int selectedCoach) {
		this.selectedCoach = selectedCoach;
	}
	public void setToggle(int toggle) {
		Toggle[SortType - 1] = toggle;
	}
	public int getToggle() {
		return Toggle [SortType - 1];
	}	
	public void setSortType(int SortType) {
		this.SortType = SortType;
	}
	public int getSortType() {
		return SortType;
	}
	public int getSelectedVenue() {
		return selectedVenue;
	}
	public void setSelectedVenue(int selectedVenue) {
		this.selectedVenue = selectedVenue;
	}
	public int getSelectedDate() {
		return selectedDate;
	}
	public void setSelectedDate(int selectedDate) {
		this.selectedDate = selectedDate;
	}
	public Vector<voCoachDate> getCoachDates() {
		return coachDates;
	}
	public void setCoachDates(Vector<voCoachDate> coachDates) {
		this.coachDates = coachDates;
	}
	public int getSelectedSlot() {
		return selectedSlot;
	}
	public void setSelectedSlot(int selectedSlot) {
		this.selectedSlot = selectedSlot;
	}
	
}
