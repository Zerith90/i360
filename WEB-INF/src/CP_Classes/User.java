package CP_Classes;

import java.sql.*;
import java.util.Vector;

import util.Utils;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voDepartment;
import CP_Classes.vo.voDivision;
import CP_Classes.vo.voGroup;
import CP_Classes.vo.voUser;
import CP_Classes.vo.voUserType;
import CP_Classes.vo.votblUserRelation;

/**
 * This java file will do all the SQL for [User] database
 */
/**
 * 
 * Change Log
 * ==========
 *
 * Date        By				Method(s)            					Change(s) 
 * ================================================================================================
 * 30/05/12	  Albert		   getUserDetailWithRound(int,int)		To include round in the array
 * 
 * 30/05/12	  Albert		   getRoundList(int)					Get all distinct rounds based on surveyID
 * 
 * 01/06/12   Albert		   addRecord -> addRecordImport			Change method name and parameter to accommodate importing user with
 * 							   editRecord -> editRecordImport		the field round
 * 
 * 01/06/12   Albert		addRecord, editRecord					Added override methods to accommodate adding or editing user with round field
 * 
 * 01/06/12   Albert		   search_User_WithRelation				Change method so that it selects round and set it to voUser also
 * 
 * 01/06/12   Albert			constructor for data member Toggle	Increment initialization of array size 
 * 
 * 11/06/13   Xukun			add in officeTel, handphone, remark		
 * 								mobile provider
 */
public class User
{
/**
 * Declaration
 */
	private Database db;
	private MailHTMLStd Email;
	private Setting server;
	private EmailTemplate template;
	private EventViewer ev;
	private Create_Edit_Survey CE_Survey;
	private AssignTarget_Rater ATR;
	
	private String sDetail[] = new String[13];
 	private String itemName = "User";
	
	public int SortType;
	
	private int selUser = 0;
	private String LoginName=" ";
	
	private int Toggle [];	// 0=asc, 1=desc
	
	private String SQLstatement = "";

	/******
	 *
	 * Edited by Roger - 12 June 2008
	 * Add one more FKOrganization to table
	 * 
	 *****/	
	int defaultOrgID = 1; //need an organization id to send email now. 
 
	public User()
	{
		db = new Database();
		Email = new MailHTMLStd();
		server = new Setting();
		template = new EmailTemplate();
		ev = new EventViewer();
		CE_Survey = new Create_Edit_Survey();
		
		Toggle = new int [12];
		
		for(int i=0; i<12; i++)
		{
			Toggle[i] = 0;
		}
		SortType = 1;
		//System.out.println(server.getCompanySetting());
	}
	
	public static void main(String args[]) throws Exception
	{
		User a = new User();
		
		a.copyOrgStruc();
		//System.out.println("OK");		
	}
	
	/**
	 * User detail for Nomination Report
	 * @param SurveyID
	 * @param NameSequence
	 * @return
	 * @throws SQLException
	 */
	public Vector getUserDetail_RptNomination(int SurveyID, int NameSequence)throws SQLException
	{
		//db.openDB();
		User_Jenty uJ = new User_Jenty();
		int nameSequence = uJ.NameSequence_BySurvey(SurveyID);

		//Change SQL string to get Supervisor ID from tblAssignment instead of tblUserRelation
		//Mark Oei 28 April 2010
		String sSQL = "SELECT [User].*, Division.DivisionName AS DivisionName, Department.DepartmentName AS DepartmentName, [Group].GroupName AS GroupName,"; 
		sSQL = sSQL + " User_Sup.FamilyName AS SupFamilyName, User_Sup.GivenName AS SupGivenName, User_Sup.Designation AS SupDesignation,";
		sSQL = sSQL + " Department_Sup.DepartmentName AS SupDepartmentName, tblUserRelation.User2, tblAssignment.RaterLoginID,";
		sSQL = sSQL + " tblAssignment.RaterCode";
		sSQL = sSQL + " FROM Department Department_Sup";
		sSQL = sSQL + " INNER JOIN [User] User_Sup ON Department_Sup.PKDepartment = User_Sup.FKDepartment";
//		sSQL = sSQL + " INNER JOIN tblUserRelation ON User_Sup.PKUser = tblUserRelation.User2";
		sSQL = sSQL + " INNER JOIN tblAssignment ON User_Sup.PKUser = tblAssignment.RaterLoginID";
		sSQL = sSQL + " AND tblAssignment.SurveyID=" + SurveyID + " AND (tblAssignment.RaterCode Like 'SUP%' OR tblAssignment.RaterCode LIKE 'SELF%')";
		sSQL = sSQL + " INNER JOIN [User]";
		sSQL = sSQL + " INNER JOIN Division ON [User].FKDivision = Division.PKDivision";
		sSQL = sSQL + " INNER JOIN Department ON [User].FKDepartment = Department.PKDepartment";
		sSQL = sSQL + " INNER JOIN [tblUserRelation] ON [tblUserRelation].User1 = [User].PKUser";
//		sSQL = sSQL + " INNER JOIN [Group] ON [User].Group_Section = [Group].PKGroup ON tblUserRelation.User1 = [User].PKUser ";
		sSQL = sSQL + " INNER JOIN [Group] ON [User].Group_Section = [Group].PKGroup ON tblAssignment.TargetLoginID = [User].PKUser";
//		sSQL = sSQL + " WHERE ([User].PKUser IN (SELECT * FROM (SELECT DISTINCT TargetLoginID FROM tblAssignment WHERE SurveyID = "+SurveyID+") DERIVEDTBL))";
		sSQL = sSQL + " WHERE (tblAssignment.TargetLoginID = [User].PKUser)";
		//add sort by Ratercode
		//Mark Oei 28 April 2010
		sSQL = sSQL + " ORDER BY Division.DivisionName, Department.DepartmentName, [Group].GroupName, [User].Designation, [User].GivenName, [User].FamilyName, RaterCode";

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sSQL);
        	
            while(rs.next())
            {
            	voUser vo = new voUser();
            	vo.setPKUser(rs.getInt("PKUser"));
            	//get the values from database for validation
            	//Mark Oei 28 April 2010
            	int usr2 = rs.getInt("User2");
            	int raterLogin = rs.getInt("RaterLoginID");
            	String raterCode = rs.getString("RaterCode");
            	vo.setDivisionName(rs.getString("DivisionName"));
            	vo.setDepartmentName(rs.getString("DepartmentName"));
            	vo.setGroupName(rs.getString("GroupName"));
            	vo.setDesignation(rs.getString("Designation"));
            	vo.setIDNumber(rs.getString("IDNumber"));
            	vo.setFamilyName(rs.getString("FamilyName"));
            	vo.setGivenName(rs.getString("GivenName"));
            	//code change to add (Default) wording behind supervisor name
            	//and a '-' to supervisor name when target is self
            	//Mark Oei 28 April 2010
            	if (usr2 == raterLogin && raterCode.substring(0,3).equals("SUP")){
            		if (nameSequence==0){
            			vo.setSupervisorFamilyName(rs.getString("SupFamilyName"));
            			vo.setSupervisorGivenName(rs.getString("SupGivenName") + " (Default)");
            		} else {
            			vo.setSupervisorFamilyName(rs.getString("SupFamilyName") + " (Default)");
            			vo.setSupervisorGivenName(rs.getString("SupGivenName"));
            		}
            	} else {
            		//Check raterCode for SELF, if found, insert a '-'
            		if (raterCode.equals("SELF")) {
            			vo.setSupervisorFamilyName("-");
            			vo.setSupervisorGivenName(" ");            			
            		} else {
            			vo.setSupervisorFamilyName(rs.getString("SupFamilyName"));
            			vo.setSupervisorGivenName(rs.getString("SupGivenName"));
            		}
            	}
            		
        		vo.setSupervisorDesignation(rs.getString("SupDesignation"));
        		vo.setSupervisorDepartmentName(rs.getString("SupDepartmentName"));
        		vo.setRaterCode(rs.getString("RaterCode"));
    			v.add(vo);
            	
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - getUserDetail_RptNomination - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return v;
	}
	
/*------------------------------------------------end user detail for Nomination Report--------------------------------------*/
	
	/**
	 * Retrieves list of rounds based on the surveyID.
	 *
	 * @param surveyID
	 * @return Vector<Integer> roundList
	 */
	 public Vector<Integer> getRoundList(int surveyID) throws SQLException, Exception{
		 Vector<Integer> v = new Vector<Integer>();
			
		String Sql = "SELECT distinct [User].Round ";
		Sql += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		Sql += "WHERE tblAssignment.SurveyID = " + surveyID + " ORDER BY [User].Round";			
			
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
  	   	try{
  	   		con=ConnectionBean.getConnection();
	  		st=con.createStatement();
	  		rs=st.executeQuery(Sql);
	  	   	while(rs.next()) {
	  	   		if (rs.getString("Round")==null) continue;
	  		    v.add(rs.getInt("Round"));
	  	   	}
				
  	   	}catch(Exception ex){
			System.err.println("User.java - getRoundList - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
			
		return v;
	}
	
	/*------------------------------------------------end user detail for Nomination Report--------------------------------------*/
	
	/**
	 * Retrieves User ID if exist.
	 *
	 * Parameter: String UserTypeName, int ApplicationType (1=cProfiler, 2=3-Sixty Profiler)
	 *
	 */
	 public int getPKUserType(String UserTypeName, int ApplicationType) throws SQLException, Exception
	{
		int PKUserType=0;
		//db.openDB();
		
		//Get the PKUserType
		String query = "SELECT * FROM UserType WHERE UserTypeName = '"+ UserTypeName + "' AND ApplicationType = " + ApplicationType;
		//ResultSet rs = db.getRecord(query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try
        {          

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
                        
            if(rs.next())
    			PKUserType = rs.getInt("PKUserType");
       
        }
        catch(Exception E) 
        {
            System.err.println("User.java - getPKUserType - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		return PKUserType;
	}
	
	 public int getPKUserType(int iPKUser) throws SQLException, Exception
	 {
		 int iUserType = 0;
		// db.openDB();
		 
		 String sSQL = "SELECT FKUserType360 FROM [User] WHERE PKUser = " + iPKUser;
		// ResultSet rs = db.getRecord(sSQL);
		 
		 Connection con = null;
		 Statement st = null;
		 ResultSet rs = null;

		 try
		 {          

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sSQL);
	                        

	   		 if(rs.next())
	   			 iUserType = rs.getInt("FKUserType360");
	       
	        }
	        catch(Exception E) 
	        {
	            System.err.println("User.java - getPKUserType - " + E);
	        }
	        finally
	        {
	        	ConnectionBean.closeRset(rs); //Close ResultSet
	        	ConnectionBean.closeStmt(st); //Close statement
	        	ConnectionBean.close(con); //Close connection	
	        }
		 
		 
		 
		
		 return iUserType;
	 }
	 

	 
	/**
	 * Retrieves User ID if exist.
	 */
	public int checkUserExist(int FKDepartment, int FKDivision, int FKUserType, String FamilyName,String GivenName, 
							String LoginName, String Designation, String IDNumber, int Group_Section,String Password, 
							int IsEnabled, int FKCompanyID, int FKOrganization) throws SQLException, Exception
	{
		int iPKUser = 0;
		
		String command = "SELECT * FROM [User] WHERE FKDepartment = " + FKDepartment + " and FKDivision = " + FKDivision;
		command = command + " and FKUserType360 = " + FKUserType + " and FamilyName = '" + FamilyName + "' ";
		command = command + " and GivenName = '" + GivenName + "' and LoginName = '" + LoginName + "' ";
		command = command + " and Designation = '" + Designation + "' and IDNumber = '" + IDNumber + "' ";
		command = command + " and Group_Section = " + Group_Section + " and Password = '" + Password + "' ";
		command = command + " and IsEnabled = " + IsEnabled + " and FKCompanyID = " + FKCompanyID + " and FKOrganization = " + FKOrganization;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try
        {          

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(command);
                        
        	if(rs.next())
    			iPKUser = rs.getInt("PKUser");
	    		
        }
        catch(Exception E) 
        {
            System.err.println("User.java - checkUserExist - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
	      
		return iPKUser;
	}
	
	 public int checkPossibleSameUser(String FamilyName, String GivenName, int FKCompanyID, int FKOrganization) throws SQLException, Exception
	 {
		 int iPKUser = 0;
		 String command = "Select * from [User] where FamilyName = '" + FamilyName +"'";
		 command += " and GivenName = '" + GivenName +"'";
		 command += "and FKCompanyID = " + FKCompanyID + " and FKOrganization = " + FKOrganization;
		 
		 Connection con = null;
		 Statement st = null;
		 ResultSet rs = null;
		 
		 try{
			 con = ConnectionBean.getConnection();
			 st = con.createStatement();
			 rs = st.executeQuery(command);
			 
			 if(rs.next())
				 iPKUser = rs.getInt("PKUser");
		 }catch(Exception E) 
        {
            System.err.println("User.java - checkPossibleSameUser - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		return iPKUser;
	 }

	/**
	 * Retrieves User ID if exist.
	 */
	public int checkUserExist(String LoginName, int FKCompanyID, int FKOrganization) throws SQLException, Exception
	{
		int iPKUser = 0;
		
		String command = "SELECT * FROM [User] WHERE LoginName = '" + LoginName + "' ";
		command = command + "and FKCompanyID = " + FKCompanyID + " and FKOrganization = " + FKOrganization;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try
        {          

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(command);
                        
            if(rs.next())
    			iPKUser = rs.getInt("PKUser");
    		
        }
        catch(Exception E) 
        {
            System.err.println("User.java - checkUserExist - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
        

	
		return iPKUser;
	}
	
	/**
	 * Get UserRelationID.
	 *
	 */
	public int getUserRelationID(int PKUser1, int PKUser2, int RelationType) throws SQLException, Exception
	{
		//RelationType:
		//1 = Direct Supervisor
		int UserRelationID = 0;
		//db.openDB();
		
		String query = "SELECT * FROM tblUserRelation WHERE User1 = "+ PKUser1 + " AND User2="+PKUser2 + " AND RelationType = " + RelationType;
		//ResultSet rs = db.getRecord(query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try
        {          

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
            
            if(rs.next())
    			UserRelationID = rs.getInt("UserRelationID");          
          
    	 }
        catch(Exception E) 
        {
            System.err.println("User.java - getUserRelationID - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
        
		

		return UserRelationID;	
	}
	
	/**Add in by Su See
	 * Get ResultSet
	 * Parameters: PKUser2, SurveyID
	 * This mtd will retrieve resultset of relationType based on PKUser2 and SurveyID
	 */
	public votblUserRelation getUserRelation(int PKUser2, int SurveyID) throws SQLException, Exception
	{
		//db.openDB();
		
		String query = "SELECT tblUserRelation.RelationType, tblUserRelation.User1";
		query += " FROM tblAssignment INNER JOIN tblUserRelation ON tblAssignment.TargetLoginID = tblUserRelation.User1";
		query += " WHERE (tblUserRelation.User2 =" + PKUser2 + ") AND (tblAssignment.SurveyID = " + SurveyID + ")";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		votblUserRelation vo = new votblUserRelation();
		
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs.next())
            {
            	vo.setRelationType(rs.getInt("RelationType"));
            	vo.setUser1(rs.getInt("User1"));
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - getUserRelation- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return vo;
	}
	
	/**
	 * Get Supervisor PKUser.
	 *
	 *	@param (int PKUser1)
	 *
	 */
	public int getSupPKUser(int PKUser1) throws SQLException, Exception
	{
		//RelationType:
		//1 = Direct Supervisor
		int PKUser2 = 0;
		//db.openDB();
		
		String query = "SELECT * FROM tblUserRelation WHERE User1 = "+ PKUser1 + " AND RelationType = 1";
		//ResultSet rs = db.getRecord(query);
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try
        {          

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
            
            if(rs.next())
    			PKUser2 = rs.getInt("User2");
    		
    	 }
        catch(Exception E) 
        {
            System.err.println("User.java - checkUserExist - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
        
		
		//if(rs.next())
		//	PKUser2 = rs.getInt("User2");
		
		
		return PKUser2;	
	}
	
	
	/**
	 * List out User's specific Relation.
	 *
	 * Parameters:
	 *		int PKUser2, int RelationType
	 *
	 */
	public int[] listSpecificRelation(int PKUser2, int RelationType)
	{
		//RelationType:
		//1 = Direct Supervisor
		//Database db1 = new Database();
		
		//db.openDB();
		//db1.openDB();
		
		int sResult[] = new int[0];
		
		//Get the record count and set the Array
		String query = "SELECT count(*) as recordcount FROM tblUserRelation WHERE User2 = "+ PKUser2 + " AND RelationType = " + RelationType;
		/*
		ResultSet rs = db.getRecord(query);
		if(rs.next())
			sResult = new int[rs.getInt("recordcount")];
		else
			return sResult;
		rs.close();
		*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		boolean exist = false;
		try
        {          

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
            
            if(rs.next()) {
    			sResult = new int[rs.getInt("recordcount")];
    			exist = true;
            }
    	 }
        catch(Exception E) 
        {
            System.err.println("User.java - listSpecificRelation - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
        
		if(exist) {
			query = "SELECT DISTINCT User1 FROM tblUserRelation WHERE User2 = "+ PKUser2 + " AND RelationType = " + RelationType;
			
			/*
			ResultSet rs1 = db.getRecord(query);
			
			int i=0;
			while(rs1.next())
			{
				//Loop to get all the DR's PKUser
				sResult[i] = rs1.getInt("User1");
				i++;
			}
			
			rs1.close();
			*/
		
			try
	        {          
	
				con=ConnectionBean.getConnection();
				st=con.createStatement();
				rs=st.executeQuery(query);
	            
	            int i=0;
	    		while(rs.next())
	    		{
	    			//Loop to get all the DR's PKUser
	    			sResult[i] = rs.getInt("User1");
	    			i++;
	    		}
	    	 }
	        catch(Exception E) 
	        {
	            System.err.println("User.java - listSpecificRelation - " + E);
	        }
	        finally
	        {
	        	ConnectionBean.closeRset(rs); //Close ResultSet
	        	ConnectionBean.closeStmt(st); //Close statement
	        	ConnectionBean.close(con); //Close connection
	        }
		}
		
		return sResult;
	}
	
	/**
	 * DELETE User's Relation.
	 *
	 * Parameters:
	 *		int PKUser
	 *
	 */
	public boolean deleteRelation(int PKUser) throws SQLException, Exception
	{
		//db.openDB();

		//PreparedStatement ps;
		String query = "DELETE tblUserRelation WHERE (User1 = " + PKUser + ") OR (User2 = " + PKUser + ")";
		//ps = db.con.prepareStatement(query);
		//ps.execute();	
		Connection con = null;
		Statement st = null;
	
		boolean bIsDeleted = false;
 		
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(query);
			if(iSuccess!=0)
				bIsDeleted=true;

		} 
        catch(Exception E) 
        {
            System.err.println("JobFunction.java - deleteRelation - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
        return bIsDeleted;
		
	}
	
	/**
	  * Similar to updateNoRelation(int PKUser1). Only difference is it receives another additional parameter PrevState to determine whether to revert to the previous user relation
	  * @param PKUser1 - Determine which user to update
	  * @param PrevState - false (set relation to empty), true (no change and remain as the existing state)
	  * @author Sebastian
	  * @since v.1.3.12.77 (05 July 2010)
	**/
	public void updateNoRelation(int PKUser1, boolean PrevState) throws SQLException, Exception
	{
		boolean exist = false;
		
		String query = "SELECT * FROM tblUserRelation WHERE User1 = "+ PKUser1; // + " AND User2="+PKUser1;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs.next())
            {
            	exist = true;
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - updateNoRelation- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
       
		if(exist)
		{
			//Record existed, continue updating
			//System.out.println("RECORD EXISTED");
			
			if (!PrevState)	//don't revert back to previous relation, but set relation to empty
			{
				query = "UPDATE tblUserRelation SET User2 = " +PKUser1+ ", RelationType=0 WHERE (User1 = " + PKUser1 + ")"; //" AND User2="+PKUser1 + ")";
				boolean bIsUpdated = false;
				try	
				{

					con=ConnectionBean.getConnection();
					st=con.createStatement();
					int iSuccess = st.executeUpdate(query);
					if(iSuccess!=0)
						bIsUpdated=true;

				}
					
				catch(Exception E)
				{
			        System.err.println("User.java - updateNoRelation- " + E);
				}
				
				finally
		    	{
					ConnectionBean.closeStmt(st); //Close statement
					ConnectionBean.close(con); //Close connection
		    	}
			}
		}
		else
		{
			//Record Does not exist, insert record instead of updating
			//System.out.println("RECORD DOES NOT EXIST");
			insertRelation(PKUser1, PKUser1, 0);
		}	
	}
	
	/**
	 * Update User's No Relation.
	 *
	 * Parameters:
	 *		int PKUser1
	 *
	 */

	public void updateNoRelation(int PKUser1) throws SQLException, Exception
	{
		boolean exist = false;
		
		String query = "SELECT * FROM tblUserRelation WHERE User1 = "+ PKUser1; // + " AND User2="+PKUser1;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs.next())
            {
            	exist = true;
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - updateNoRelation- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
       
		if(exist)
		{
			//Record existed, continue updating
			//System.out.println("RECORD EXISTED");
			
			query = "UPDATE tblUserRelation SET User2 = " +PKUser1+ ", RelationType=0 WHERE (User1 = " + PKUser1 + ")"; //" AND User2="+PKUser1 + ")";
			boolean bIsUpdated = false;
			try	
			{

				con=ConnectionBean.getConnection();
				st=con.createStatement();
				int iSuccess = st.executeUpdate(query);
				if(iSuccess!=0)
					bIsUpdated=true;

			}
				
			catch(Exception E)
			{
		        System.err.println("User.java - updateNoRelation- " + E);
			}
			
			finally
	    	{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
	    	}
		}
		else
		{
			//Record Does not exist, insert record instead of updating
			//System.out.println("RECORD DOES NOT EXIST");
			insertRelation(PKUser1, PKUser1, 0);
		}	
	}
	
	/**
	 * Update User's Relation.
	 *
	 * Parameters:
	 *		int PKUser1, int PKUser2
	 *
	 */
	public void updateRelation(int PKUser1, int PKUser2) throws SQLException, Exception
	{
		
		boolean exist = false;
		
		String query = "SELECT * FROM tblUserRelation WHERE User1 = "+ PKUser1; // + " AND User2="+PKUser1;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs.next())
            {
            	exist = true;
            }
            
        }
        catch(Exception E) 
        {
        
            System.err.println("User.java - updateRelation- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
       
		if(exist)
		{

			//Record existed, continue updating
			//System.out.println("RECORD EXISTED");
			query = "UPDATE tblUserRelation SET User2 = " +PKUser2+ ", RelationType = 1 WHERE (User1 = " + PKUser1 + ")";
			boolean bIsUpdated = false;
			try	
			{

				con=ConnectionBean.getConnection();
				st=con.createStatement();
				int iSuccess = st.executeUpdate(query);
				if(iSuccess!=0)
					bIsUpdated=true;

			}
				
			catch(Exception E)
			{
		        System.err.println("User.java - updateRelation- " + E);
			}
			
			finally
	    	{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
	    	}
		}
		else
		{
			//Record Does not exist, insert record instead of updating
			//System.out.println("RECORD DOES NOT EXIST");
			insertRelation(PKUser1, PKUser2);
		}	
		
	}
	
	/**
	 * Update User's Relation.
	 *
	 * Parameters:
	 *		int PKUser1, int PKUser2, int iRelationType
	 *
	 */
	public void updateRelation(int PKUser1, int PKUser2, int iRelationType) throws SQLException, Exception
	{
		
		String query = "SELECT * FROM tblUserRelation WHERE User1 = "+ PKUser1;
		boolean exist = false;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs.next())
            {
            	exist = true;
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - updateRelation- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		if(exist)
		{
			//Record existed, continue updating
			//System.out.println("RECORD EXISTED");
			
			query = "UPDATE tblUserRelation SET User2 = " +PKUser2+ ", RelationType = " + iRelationType + " WHERE (User1 = " + PKUser1 + ")";
			
			boolean bIsUpdated = false;
			try	
			{

				con=ConnectionBean.getConnection();
				st=con.createStatement();
				int iSuccess = st.executeUpdate(query);
				if(iSuccess!=0)
					bIsUpdated=true;

			}
				
			catch(Exception E)
			{
		        System.err.println("User.java - updateRelation- " + E);
			}
						
			finally
	    	{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
	    	}
		}
		else
		{
			//Record Does not exist, insert record instead of updating
			//System.out.println("RECORD DOES NOT EXIST");
			insertRelation(PKUser1, PKUser2, iRelationType);
		}	
		
	}
	
	/**
	 * Insert User's Relation.
	 *
	 * Parameters:
	 *		int PKUser1, int PKUser2
	 *
	 */
	public boolean insertRelation(int PKUser1, int PKUser2) throws SQLException, Exception
	{
		String query = "INSERT INTO tblUserRelation (User1, User2, RelationType) VALUES(" +PKUser1+ ","+PKUser2+ ", 1)";
		
		Connection con = null;
		Statement st = null;
		
		boolean bIsAdded = false;
		try	
		{

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(query);
			if(iSuccess!=0)
				bIsAdded=true;

		}
			
		catch(Exception E)
		{
	        System.err.println("User.java - insertRelation- " + E);
		}
		
		finally
    	{
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
    	}
		
		return bIsAdded;
	}
	
	/**
	 * Insert User's Relation.
	 *
	 * Parameters:
	 *		int PKUser1, int PKUser2, int iRelationType
	 *
	 */
	public boolean insertRelation(int PKUser1, int PKUser2, int iRelationType) throws SQLException, Exception
	{
		String query = "INSERT INTO tblUserRelation (User1, User2, RelationType) VALUES(" +PKUser1+ ","+PKUser2+ "," + iRelationType + ")";
		Connection con = null;
		Statement st = null;
		
		boolean bIsAdded = false;
		try	
		{

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(query);
			if(iSuccess!=0)
				bIsAdded=true;

		}
			
		catch(Exception E)
		{
	        System.err.println("User.java - insertRelation- " + E);
		}
		
		finally
    	{
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
    	}
		
		return bIsAdded;
	}
	
	/**
	 * Insert User's Relation.
	 *
	 * Search for the newly added User and get the PKUser, then insert into tblUserRelation
	 *
	 * Parameters:
	 *		String LoginIDUser1, int PKUser2
	 *
	 */
	public void insertRelation(String LoginIDUser1, int PKUser2) throws SQLException, Exception
	{
		//Get the newly added User's PKUser
		String query = "SELECT TOP 1 * FROM [User] WHERE LoginName = '" + LoginIDUser1 + "' ORDER BY [User].PKUser DESC";
		
		int iPKUser = 0;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs.next())
            {
            	iPKUser = rs.getInt("PKUser");
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - insertRelation- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
		if(iPKUser != 0)
		{
			insertRelation(iPKUser, PKUser2);
			//System.out.println("Relation inserted, LoginID=" + rs1.getString("LoginName"));
		}
	}
	
	/**
	 * Insert User's Relation.
	 *
	 * Search for the newly added User and get the PKUser, then insert into tblUserRelation
	 *
	 * Parameters:
	 *		String LoginIDUser1, int PKUser2, int iRelationType
	 *
	 */
	public void insertRelation(String LoginIDUser1, int PKUser2, int iRelationType) throws SQLException, Exception
	{
	
		//Get the newly added User's PKUser
		String query = "SELECT TOP 1 * FROM [User] WHERE LoginName = '" + LoginIDUser1 + "' ORDER BY [User].PKUser DESC";
		
		int iPKUser = 0;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs.next())
            {
            	iPKUser = rs.getInt("PKUser");
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - insertRelation- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		if(iPKUser != 0)
		{
			insertRelation(iPKUser, PKUser2, iRelationType);
			//System.out.println("Relation inserted, LoginID=" + rs1.getString("LoginName"));
		}
	}
	
	/**
	 * Insert User's No Relation.
	 *
	 * Search for the newly added User and get the PKUser, then insert into tblUserRelation with RelationType = 0 (No Relation)
	 *
	 * Parameters:
	 *		String LoginIDUser1, int iRelationType
	 *
	 */
	public void insertNoRelation(String LoginIDUser1) throws SQLException, Exception
	{
		
		//Get the newly added User's PKUser
		String query = "SELECT TOP 1 * FROM [User] WHERE LoginName = '" + LoginIDUser1 + "' ORDER BY [User].PKUser DESC";
		
		int iPKUser = 0;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs.next())
            {
            	iPKUser = rs.getInt("PKUser");
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - insertNoRelation- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		if(iPKUser != 0)
		{

			insertRelation(iPKUser, iPKUser, 0);
			//System.out.println("Relation inserted, LoginID=" + rs1.getString("LoginName"));
		}
	}
	
	/**
	 * Add a new record to the [User] database without round.
	 *
	 */
	public boolean addRecord(int FKDepartment, int FKDivision, int FKUserType, String FamilyName,String GivenName, 
							String LoginName, String Designation, String IDNumber, int Group_Section,String Password, 
							int IsEnabled, int FKCompanyID, int FKOrganization, String Email, String offTel, String handphone,
							String mobileProvider, String remark, int Admin)  throws SQLException, Exception
	{
		boolean bIsAdded=false;
		Connection con = null;
		Statement st = null;

		String query = "INSERT INTO [User] (FKDepartment, FKDivision, FKUserType360, FamilyName, GivenName, LoginName, " +
				"Designation, IDNumber, Group_Section, Password, IsEnabled, FKCompanyID, FKOrganization, Email, OfficeTel, " +
				" Handphone, MobileProvider, Remarks) VALUES(" 
				+FKDepartment+ ","+FKDivision+ "," +FKUserType+ ",'" +FamilyName+ "','" +GivenName+ "','" +LoginName+ "','" 
				+Designation+ "','" +IDNumber+ "'," +Group_Section+ ",'" +Password+ "'," +IsEnabled+ "," +FKCompanyID+ "," +
				FKOrganization+", '"+Email+"','"+ offTel +"','"+handphone+"','"+mobileProvider +"','"+remark +"');";
		
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			
			if(iSuccess!=0)
				bIsAdded=true;

			
		}
		catch(Exception E)
		{
            System.err.println("User.java - addRecord- " + E);
		}
		finally
        {
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

        }
		
		sDetail = CE_Survey.getUserDetail(Admin);
		ev.addRecord("Insert", itemName, LoginName, sDetail[2], sDetail[11], sDetail[10]);
			
		return bIsAdded;
	}
	
	/**
	 * Add a new record to the [User] database with round.
	 *
	 */
	public boolean addRecord(int FKDepartment, int FKDivision, int FKUserType, String FamilyName,String GivenName, 
							String LoginName, String Designation, String IDNumber, int Group_Section,String Password, 
							int IsEnabled, int FKCompanyID, int FKOrganization, String Email, String offTel,
							String handphone, String mobileProvider, String remark, int Admin, int round)  throws SQLException, Exception
	{
		boolean bIsAdded=false;
		Connection con = null;
		Statement st = null;

		String query = "INSERT INTO [User] (FKDepartment, FKDivision, FKUserType360, FamilyName, GivenName," +
				" LoginName, Designation, IDNumber, Group_Section, Password, IsEnabled, FKCompanyID, FKOrganization, " +
				"Email, OfficeTel, Handphone, MobileProvider, Remarks, Round) VALUES(" +FKDepartment+ ","+FKDivision+ "," +
				FKUserType+ ",'" +FamilyName+ "','" +GivenName+ "','" +LoginName+ "','" +Designation+ "','" +IDNumber+ "'," +
				Group_Section+ ",'" +Password+ "'," +IsEnabled+ "," +FKCompanyID+ "," +FKOrganization+", '"+Email+"','" 
				+ offTel +"','" + handphone +"','"+mobileProvider+"','"+remark+"',"+round+");";
		
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			
			if(iSuccess!=0)
				bIsAdded=true;

			
		}
		catch(Exception E)
		{
            System.err.println("User.java - addRecord- " + E);
		}
		finally
        {
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

        }
		
		sDetail = CE_Survey.getUserDetail(Admin);
		ev.addRecord("Insert", itemName, LoginName, sDetail[2], sDetail[11], sDetail[10]);
			
		return bIsAdded;
	}
		
	/**
	 * Add a new record to the [User] database, Function without Admin details & Meant for Import User).
	 *
	 */
	public boolean addRecordImport(int FKDepartment, int FKDivision, int FKUserType, String FamilyName,String GivenName, 
							String LoginName, String Designation, String IDNumber, int Group_Section,String Password, 
							int IsEnabled, int FKCompanyID, int FKOrganization, String Email, String round, String officeTel,
							String handphone, String mobileProvider, String remark) throws SQLException, Exception
	{
		boolean bIsAdded=false;
		Connection con = null;
		Statement st = null;
		String query="";
		query = "INSERT INTO [User] (FKDepartment, FKDivision, FKUserType360, FamilyName, GivenName, LoginName, Designation, IDNumber, Group_Section," +
				" Password, IsEnabled, FKCompanyID, FKOrganization, Email, Round, OfficeTel, Handphone, MobileProvider, Remarks) VALUES(";
		query += FKDepartment+ ","+FKDivision+ "," +FKUserType+ ",'" +FamilyName+"','" +GivenName+ "','" +LoginName+ "','" +Designation+ "','" +IDNumber+ "'," +Group_Section+ 
				",'" +Password+ "'," +IsEnabled+ "," +FKCompanyID+ "," +FKOrganization+", '"+Email+"'," +round+",";
		if(officeTel != null){
			query += "'"+officeTel +"',";
		}else{
			query += "NULL,";
		}
		if(handphone != null){
			query += "'"+handphone +"',";
		}else{
			query += "NULL,";
		}
		if(mobileProvider != null){
			query += "'"+mobileProvider +"',";
		}else{
			query += "NULL,";
		}
		if(remark != null){
			query += "'"+remark +"');";
		}else{
			query += "NULL);";
		}
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			
			if(iSuccess!=0)
				bIsAdded=true;

		}
		catch(Exception E)
		{
            System.err.println("User.java - addRecordImport- " + E);
		}
		finally
        {
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

        }
		
		ev.addRecord("Insert", itemName, LoginName, "Import User", "Import User", "Import User");
	
		return bIsAdded;
	}
	
	
	/**
	 * Edit user's data
	 * @param PKUser
	 * @param FKDepartment
	 * @param FKDivision
	 * @param FKUserType
	 * @param FamilyName
	 * @param GivenName
	 * @param LoginName
	 * @param Designation
	 * @param IDNumber
	 * @param Group_Section
	 * @param IsEnabled
	 * @param Email
	 * @param Admin
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean editRecord(int PKUser, int FKDepartment, int FKDivision, int FKUserType, String FamilyName, String GivenName,
							String LoginName, String Designation, String IDNumber, int Group_Section, int IsEnabled,
							String Email, String offTel, String handphone, String mobileProvider, String remark, int Admin) throws SQLException, Exception
	{
		String OldName = "";
		String command = "SELECT * FROM [User] WHERE PKUser  = "+ Admin ;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        	
            if(rs.next())
            {
            	OldName = rs.getString("LoginName");
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - editRecord- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }

		String query = "UPDATE [User] SET FKDepartment= " +FKDepartment+ ",";
		query = query+ "FKDivision= "+FKDivision+ ", FKUserType360= " +FKUserType+ ", FamilyName= '" +FamilyName+ "',";
		query = query+ " GivenName= '" +GivenName+ "',LoginName = '" +LoginName+ "', Designation= '" +Designation+ "',";
		query = query+ " IDNumber= '" +IDNumber+ "', Group_Section= " +Group_Section+",";
		query = query+ " OfficeTel= '" + offTel +"', Handphone= '"+handphone+"', ";
		query = query+ " Remarks= '" + remark +"', MobileProvider= '"+ mobileProvider+"', ";
		query = query+ " IsEnabled= " +IsEnabled+ ", Email = '"+Email+"', Round = NULL WHERE PKUser=" +PKUser;

		boolean bIsUpdated = false;
        
		try	
		{

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(query);
			if(iSuccess!=0)
				bIsUpdated=true;

	
		}
			
		catch(Exception E)
		{
	        System.err.println("USer.java - editRecord- " + E);
		}
		
		finally
    	{
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection


    	}
		
		
		sDetail = CE_Survey.getUserDetail(PKUser);
		ev.addRecord("Update", itemName, "("+OldName+") - ("+LoginName+")", sDetail[2], sDetail[11], sDetail[10]);
		
		return bIsUpdated;
	}
	
	/**
	 * Edit user's data
	 * @param PKUser
	 * @param FKDepartment
	 * @param FKDivision
	 * @param FKUserType
	 * @param FamilyName
	 * @param GivenName
	 * @param LoginName
	 * @param Designation
	 * @param IDNumber
	 * @param Group_Section
	 * @param IsEnabled
	 * @param Email
	 * @param Admin
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean editRecord(int PKUser, int FKDepartment, int FKDivision, int FKUserType, String FamilyName, String GivenName,
							String LoginName, String Designation, String IDNumber, int Group_Section, int IsEnabled,
							String Email, String offTel, String handphone, String mobileProvider, String remark, int Admin, int round) throws SQLException, Exception
	{
		String OldName = "";
		String command = "SELECT * FROM [User] WHERE PKUser  = "+ Admin ;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        	
            if(rs.next())
            {
            	OldName = rs.getString("LoginName");
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - editRecord- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }

		String query = "UPDATE [User] SET FKDepartment= " +FKDepartment+ ",";
		query = query+ "FKDivision= "+FKDivision+ ", FKUserType360= " +FKUserType+ ", FamilyName= '" +FamilyName+ "',";
		query = query+ " GivenName= '" +GivenName+ "',LoginName = '" +LoginName+ "', Designation= '" +Designation+ "', Round= " +round+ ",";
		query = query+ " OfficeTel= '" + offTel +"', Handphone= '"+handphone+"', ";
		query = query+ " Remarks= '" + remark +"', MobileProvider= '"+ mobileProvider+"', ";
		query = query+ " IDNumber= '" +IDNumber+ "', Group_Section= " +Group_Section+",";
		query = query+ " IsEnabled= " +IsEnabled+ ", Email = '"+Email+"' WHERE PKUser=" +PKUser;

		boolean bIsUpdated = false;
        
		try	
		{

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(query);
			if(iSuccess!=0)
				bIsUpdated=true;

	
		}
			
		catch(Exception E)
		{
	        System.err.println("USer.java - editRecord- " + E);
		}
		
		finally
    	{
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection


    	}
		
		
		sDetail = CE_Survey.getUserDetail(PKUser);
		ev.addRecord("Update", itemName, "("+OldName+") - ("+LoginName+")", sDetail[2], sDetail[11], sDetail[10]);
		
		return bIsUpdated;
	}
	
	/**
	 * Edit user's record with password (for SA)
	 * @param PKUser
	 * @param FKDepartment
	 * @param FKDivision
	 * @param FKUserType
	 * @param FamilyName
	 * @param GivenName
	 * @param LoginName
	 * @param Designation
	 * @param IDNumber
	 * @param Group_Section
	 * @param IsEnabled
	 * @param Email
	 * @param Admin
	 * @param Password
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean editRecord(int PKUser, int FKDepartment, int FKDivision, int FKUserType, String FamilyName, String GivenName,
							String LoginName, String Designation, String IDNumber, int Group_Section, int IsEnabled,
							String Email, String offTel, String handphone, String mobileProvider, String remark, int Admin, String Password) throws SQLException, Exception
	{
		String OldName = "";
		String command = "SELECT * FROM [User] WHERE PKUser  = "+ Admin ;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        	
            if(rs.next())
            {
            	OldName = rs.getString("LoginName");
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - editRecord- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
		String query = "UPDATE [User] SET FKDepartment= " +FKDepartment+ ",";
		query = query+ "FKDivision= "+FKDivision+ ", FKUserType360= " +FKUserType+ ", FamilyName= '" +FamilyName+ "',";
		query = query+ " GivenName= '" +GivenName+ "',LoginName = '" +LoginName+ "', Designation= '" +Designation+ "',";
		query = query+ " IDNumber= '" +IDNumber+ "', Group_Section= " +Group_Section+", Password = '" +Password+ "',";
		query = query+ " OfficeTel= '" + offTel +"', Handphone= '"+handphone+"', ";
		query = query+ " Remarks= '" + remark +"', MobileProvider= '"+ mobileProvider+"', ";
		query = query+ " IsEnabled= " +IsEnabled+ ", Email = '"+Email+"', Round = NULL WHERE PKUser=" +PKUser;

		boolean bIsUpdated = false;
        
		try	
		{

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(query);
			if(iSuccess!=0)
				bIsUpdated=true;

	
		}
			
		catch(Exception E)
		{
	        System.err.println("USer.java - editRecord- " + E);
		}
		
		finally
    	{
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection


    	}
		
		sDetail = CE_Survey.getUserDetail(PKUser);
		ev.addRecord("Update", itemName, "("+OldName+") - ("+LoginName+")", sDetail[2], sDetail[11], sDetail[10]);
	
		return bIsUpdated;
	}
	
	/**
	 * Edit user's record with password (for SA)
	 * @param PKUser
	 * @param FKDepartment
	 * @param FKDivision
	 * @param FKUserType
	 * @param FamilyName
	 * @param GivenName
	 * @param LoginName
	 * @param Designation
	 * @param IDNumber
	 * @param Group_Section
	 * @param IsEnabled
	 * @param Email
	 * @param Admin
	 * @param Password
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean editRecord(int PKUser, int FKDepartment, int FKDivision, int FKUserType, String FamilyName, String GivenName,
							String LoginName, String Designation, String IDNumber, int Group_Section, int IsEnabled,
							String Email, String offTel, String handphone, String mobileProvider, String remark, int Admin, String Password, int round) throws SQLException, Exception
	{
		String OldName = "";
		String command = "SELECT * FROM [User] WHERE PKUser  = "+ Admin ;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        	
            if(rs.next())
            {
            	OldName = rs.getString("LoginName");
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - editRecord- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
		String query = "UPDATE [User] SET FKDepartment= " +FKDepartment+ ",";
		query = query+ "FKDivision= "+FKDivision+ ", FKUserType360= " +FKUserType+ ", FamilyName= '" +FamilyName+ "',";
		query = query+ " GivenName= '" +GivenName+ "',LoginName = '" +LoginName+ "', Designation= '" +Designation+ "', Round= " +round+ ",";
		query = query+ " IDNumber= '" +IDNumber+ "', Group_Section= " +Group_Section+", Password = '" +Password+ "',";
		query = query+ " OfficeTel= '" + offTel +"', Handphone= '"+handphone+"', ";
		query = query+ " Remarks= '" + remark +"', MobileProvider= '"+ mobileProvider+"', ";
		query = query+ " IsEnabled= " +IsEnabled+ ", Email = '"+Email+"' WHERE PKUser=" +PKUser;

		boolean bIsUpdated = false;
        
		try	
		{

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(query);
			if(iSuccess!=0)
				bIsUpdated=true;

	
		}
			
		catch(Exception E)
		{
	        System.err.println("USer.java - editRecord- " + E);
		}
		
		finally
    	{
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection


    	}
		
		sDetail = CE_Survey.getUserDetail(PKUser);
		ev.addRecord("Update", itemName, "("+OldName+") - ("+LoginName+")", sDetail[2], sDetail[11], sDetail[10]);
	
		return bIsUpdated;
	}
	
	/**
	 * Edit record from [User] database, Function without Admin details & Meant for Import User).
	 * @throws Exception 
	 * @throws SQLException 
	 *
	 */ 
	public boolean editRecordImport(int PKUser, int FKDepartment, int FKDivision, int FKUserType, String FamilyName, 
							String GivenName, String LoginName, String Designation, String IDNumber, int Group_Section,
							int IsEnabled, String Email, String round, String officeTel,
							String handphone, String mobileProvider, String remark) throws SQLException, Exception
	{		
		String query = "UPDATE [User] SET FKDepartment= " +FKDepartment+ ",";
		query = query+ "FKDivision= "+FKDivision+ ", FKUserType360= " +FKUserType+ ", FamilyName= '" +FamilyName+ "',";
		query = query+ " GivenName= '" +GivenName+ "',LoginName = '" +LoginName+ "', Designation= '" +Designation+ "',";
		query = query+ " IDNumber= '" +IDNumber+ "', Group_Section= " +Group_Section+", Round= " +round+",";
		if(officeTel != null){
			query += "OfficeTel='"+officeTel+"',";
		}
		if(handphone != null){
			query += "Handphone='"+handphone+"',";
		}
		if(mobileProvider != null){
			query += "MobileProvider='"+mobileProvider+"',";
		}
		if(remark != null){
			query += "Remarks='"+remark+"',";
		}

		query = query+ " IsEnabled= " +IsEnabled+ ", Email = '"+Email+"' WHERE PKUser=" +PKUser;
		boolean bIsUpdated = false;
		Connection con = null;
		Statement st = null;
		
		try	
		{

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(query);
			if(iSuccess!=0)
				bIsUpdated=true;

	
		}
			
		catch(Exception E)
		{
	        System.err.println("USer.java - editRecordImport- " + E);
		}
		
		finally
    	{
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection


    	}
		
		ev.addRecord("Update", itemName, LoginName, "Import User", "Import User", "Import User");
	
		return bIsUpdated;
	}
	
	public boolean deleteRecord(int PKUser, int Admin) throws SQLException, Exception
	{
		ATR = new AssignTarget_Rater();
		
		String OldName = "";
		String command = "SELECT * FROM [User] WHERE PKUser  = "+ Admin ;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(command);
        	
            if(rs.next())
            {
            	OldName = rs.getString("LoginName");
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - deleteRecord- " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		deleteRelation(PKUser);//Delete tblUserRelation that tight to the User
		ATR.delTargetAndRater(PKUser); //Delete All Target and Rater's records of PKUser
		
		
		String sql = "Delete from [User] where PKUser = " + PKUser;
		
		boolean bIsDeleted = false;
		try	
		{

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if(iSuccess!=0)
				bIsDeleted=true;

	
		}
			
		catch(Exception E)
		{
	        System.err.println("USer.java - deleteRecord- " + E);
		}
		
		finally
    	{
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection


    	}
		
		
		sDetail = CE_Survey.getUserDetail(PKUser);
		ev.addRecord("Delete", itemName, OldName, sDetail[2], sDetail[11], sDetail[10]);

		return bIsDeleted;
	}
	
	public Vector search_User_WithRelation(String FKDepartment, String FKDivision, String FKUserType, String FamilyName, 
							String GivenName, String LoginName, String Designation, String IDNumber, String Group_Section,
							String IsEnabled, int FKCompanyID, int FKOrganization, int SortType, String User2FamilyName) throws SQLException, Exception
	{
		
		Vector v = new Vector();
		
		/*String sSQL = "SELECT a.PKUser, a.FamilyName, a.GivenName, a.LoginName, a.Designation, a.IDNumber, a.Password, a.IsEnabled, a.Round, ";
		sSQL = sSQL + "e.GroupName, b.DivisionName, c.DepartmentName, d.UserTypeName, User2.FamilyName AS User2FamilyName, ";
		sSQL = sSQL + "User2.GivenName AS User2GivenName, User2.LoginName AS User2LoginName, tblUserRelation.RelationType, tblOrganization.NameSequence ";
		sSQL = sSQL + "FROM [User] a INNER JOIN [Group] e ON a.Group_Section = e.PKGroup INNER JOIN ";
        sSQL = sSQL + "Division b ON a.FKDivision = b.PKDivision INNER JOIN ";
        sSQL = sSQL + "Department c ON a.FKDepartment = c.PKDepartment INNER JOIN ";
        sSQL = sSQL + "UserType d ON a.FKUserType360 = d.PKUserType INNER JOIN ";
        sSQL = sSQL + "tblUserRelation ON a.PKUser = tblUserRelation.User1 INNER JOIN ";
        sSQL = sSQL + "[User] User2 ON tblUserRelation.User2 = User2.PKUser INNER JOIN ";
        sSQL = sSQL + "tblOrganization ON a.FKOrganization = tblOrganization.PKOrganization ";
		sSQL = sSQL + "WHERE (a.FKUserType360 <> 1) AND (a.FKCompanyID = "+FKCompanyID+") AND (a.FKOrganization = " +FKOrganization + ") ";
*/
		String sSQL = "SELECT a.PKUser, a.FamilyName, a.GivenName, a.LoginName, a.Designation, a.IDNumber, a.Password, a.IsEnabled, a.Round, ";
		sSQL = sSQL + "e.GroupName, b.DivisionName, c.DepartmentName, d.UserTypeName, ";
		sSQL = sSQL + "tblOrganization.NameSequence ";
		sSQL = sSQL + "FROM [User] a INNER JOIN [Group] e ON a.Group_Section = e.PKGroup INNER JOIN ";
        sSQL = sSQL + "Division b ON a.FKDivision = b.PKDivision INNER JOIN ";
        sSQL = sSQL + "Department c ON a.FKDepartment = c.PKDepartment INNER JOIN ";
        sSQL = sSQL + "UserType d ON a.FKUserType360 = d.PKUserType INNER JOIN ";
        sSQL = sSQL + "tblOrganization ON a.FKOrganization = tblOrganization.PKOrganization ";
		sSQL = sSQL + "WHERE (a.FKUserType360 <> 1) AND (a.FKCompanyID = "+FKCompanyID+") AND (a.FKOrganization = " +FKOrganization + ") ";

		if(FKDepartment != null  && !FKDepartment.equals("0") && !FKDepartment.equals(""))
		sSQL = sSQL + " AND a.FKDepartment ="+FKDepartment;
		
		if(FKDivision != null && !FKDivision.equals("0") && !FKDivision.equals(""))
		sSQL = sSQL + " AND a.FKDivision =" +FKDivision;
		
		if(FKUserType != null && FKUserType.length() != 0)
		sSQL = sSQL + " AND a.FKUserType360 =" +FKUserType;
		
		if(FamilyName != null && FamilyName.length() != 0)
		sSQL = sSQL + " AND a.FamilyName LIKE '" +FamilyName+ "%'";
		
		if(GivenName != null && GivenName.length() != 0)
		sSQL = sSQL + " AND a.GivenName LIKE '%" +GivenName+ "%'";
		
		if(LoginName != null && LoginName.length() != 0)
		sSQL = sSQL + " AND a.LoginName LIKE '%" +LoginName+ "%'";
		
		if(Designation != null && Designation.length() != 0)
		sSQL = sSQL + " AND a.Designation LIKE '" +Designation+ "%'";

		if(IDNumber != null && IDNumber.length() != 0)
		sSQL = sSQL + " AND a.IDNumber LIKE '" +IDNumber+ "%'";
		
		if(Group_Section != null && !Group_Section.equals("0") && !Group_Section.equals(""))
		sSQL = sSQL + " AND a.Group_Section LIKE '" +Group_Section+ "%'";
		
		if(IsEnabled != null && IsEnabled.length() != 0)
		sSQL = sSQL + " AND a.IsEnabled =" +IsEnabled;
		
		if(User2FamilyName != null && User2FamilyName.length() != 0)
		sSQL = sSQL + " AND User2.FamilyName LIKE '%" +User2FamilyName + "%'";
		
		//set_SQLstatement(sSQL);
		
		
		sSQL = sSQL + " ORDER BY ";
		
		if(SortType == 1)
			sSQL = sSQL + "a.LoginName ";
		else if(SortType == 2)
			sSQL = sSQL + "a.FamilyName ";
		else if(SortType == 3)
			sSQL = sSQL + "a.GivenName ";
		else if(SortType == 4)
			sSQL = sSQL + "a.Designation ";
		else if(SortType == 5)
			sSQL = sSQL + "DivisionName ";
		else if(SortType == 6)
			sSQL = sSQL + "DepartmentName ";
		else if(SortType == 7)
			sSQL = sSQL + "GroupName ";
		else if(SortType == 8)
			sSQL = sSQL + "UserTypeName ";
		else if(SortType == 9)
			sSQL = sSQL + "a.IsEnabled ";
		else if(SortType == 10)
			sSQL = sSQL + "RelationType ";
		else if(SortType == 11)
			sSQL = sSQL + "a.Round";
		
		if(Toggle[SortType - 1] == 1)
			sSQL = sSQL + " DESC ";		
		
		if(SortType == 10)
			sSQL = sSQL + ", User2.FamilyName";
		
		//System.out.println(sSQL);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sSQL);
                  
            while(rs.next())
            {
            	voUser vo = new voUser();
            	vo.setPKUser(rs.getInt("PKUser"));
        		vo.setFamilyName(rs.getString("FamilyName"));
        		vo.setGivenName(rs.getString("GivenName"));
     
        		vo.setLoginName(rs.getString("LoginName"));
        		vo.setDesignation(rs.getString("Designation"));
        		vo.setIDNumber(rs.getString("IDNumber"));
        		vo.setPassword(rs.getString("Password"));
        		vo.setIsEnabled(rs.getInt("IsEnabled"));
        		vo.setGroupName(rs.getString("GroupName"));
        		vo.setDivisionName(rs.getString("DivisionName"));
        		vo.setDepartmentName(rs.getString("DepartmentName"));
        		vo.setUserTypeName(rs.getString("UserTypeName"));
        		if(rs.getString("Round") == null)
        			vo.setRound(-1);
        		else vo.setRound(rs.getInt("Round"));
        		
        	/*	String supfirst = rs.getString("User2FamilyName");
        		String suplast = rs.getString("User2GivenName");
        		String suplogin = rs.getString("User2LoginName"); 
        		String Supervisor =  supfirst+ ", " + suplast + " (" + suplogin + ")";
        		
        		int NameSeq = vo.getNameSequence();
        		
        		if(NameSeq == 1)
        		{
        			String buffer = FamilyName;
        			FamilyName = GivenName;
        			GivenName = buffer;
        			
        			Supervisor =  suplast+ ", " + supfirst + " (" + suplogin + ")";
        		}
        		
        		vo.setSupervisorName(Supervisor);
        		
        		int RelationType = rs.getInt("RelationType");
        		vo.setRelationType(RelationType);
        		*/
        		v.add(vo);
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - search_user_WithRelation - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		return v;
	}
	
	public Vector search_User(String FKDepartment, String FKDivision, String FKUserType, String FamilyName, String GivenName,
							String LoginName, String Designation, String IDNumber, String Group_Section, String IsEnabled,
							int FKCompanyID, int FKOrganization, int SortType) throws SQLException, Exception
	{
		String sSQL;
		
		sSQL = "SELECT * FROM [User] a, Division b, Department c, UserType d, [Group] e ";
		sSQL = sSQL + "WHERE a.FKUserType360 != 1 ";
		sSQL = sSQL + "AND e.PKGroup = a.Group_Section ";
		sSQL = sSQL + "AND b.PKDivision = a.FKDivision ";
		sSQL = sSQL + "AND c.PKDepartment = a.FKDepartment ";
		sSQL = sSQL + "AND d.PKUserType = a.FKUserType360 ";
		sSQL = sSQL + "AND a.FKCompanyID = "+FKCompanyID+" ";
		sSQL = sSQL + "AND a.FKOrganization = "+FKOrganization+"";
		//sSQL = sSQL +"INNER JOIN tblOrganization ON a.FKOrganization = tblOrganization.PKOrganization AND b.FKOrganization = tblOrganization.PKOrganization AND ";
		//sSQL = sSQL +"c.FKOrganization = tblOrganization.PKOrganization";
		
		if(FKDepartment != null && !FKDepartment.equals("0") && !FKDepartment.equals(""))
		sSQL = sSQL + " AND a.FKDepartment ="+FKDepartment;
		
		if(FKDivision != null && !FKDivision.equals("0") && !FKDivision.equals(""))
		sSQL = sSQL + " AND a.FKDivision =" +FKDivision;
		
		if(FKUserType != null && FKUserType.length() != 0)
		sSQL = sSQL + " AND a.FKUserType360 =" +FKUserType;
		
		if(FamilyName != null && FamilyName.length() != 0)
		sSQL = sSQL + " AND a.FamilyName LIKE '" +FamilyName+ "%'";
		
		if(GivenName != null && GivenName.length() != 0)
		sSQL = sSQL + " AND a.GivenName LIKE '%" +GivenName+ "%'";
		
		if(LoginName != null && LoginName.length() != 0)
		sSQL = sSQL + " AND a.LoginName LIKE '%" +LoginName+ "%'";
		
		if(Designation != null && Designation.length() != 0)
		sSQL = sSQL + " AND a.Designation LIKE '" +Designation+ "%'";

		if(IDNumber != null && IDNumber.length() != 0)
		sSQL = sSQL + " AND a.IDNumber LIKE '" +IDNumber+ "%'";
		
		if(Group_Section != null && !Group_Section.equals("0") && !Group_Section.equals(""))
		sSQL = sSQL + " AND a.Group_Section LIKE '" +Group_Section+ "%'";
		
		if(IsEnabled != null && IsEnabled.length() != 0)
		sSQL = sSQL + " AND a.IsEnabled =" +IsEnabled;
		
		sSQL = sSQL + " ORDER BY ";
		
		if(SortType == 1)
			sSQL = sSQL + "LoginName";
		else if(SortType == 2)
			sSQL = sSQL + "FamilyName";
		else if(SortType == 3)
			sSQL = sSQL + "GivenName";
		else if(SortType == 4)
			sSQL = sSQL + "Designation";
		else if(SortType == 5)
			sSQL = sSQL + "DivisionName";
		else if(SortType == 6)
			sSQL = sSQL + "DepartmentName";
		else if(SortType == 7)
			sSQL = sSQL + "GroupName";
		else if(SortType == 8)
			sSQL = sSQL + "UserTypeName";
		else if(SortType == 9)
			sSQL = sSQL + "IsEnabled";
		
		if(Toggle[SortType - 1] == 1)
			sSQL = sSQL + " DESC";			
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sSQL);
                  
            while(rs.next())
            {
            	voUser vo = new voUser();
            	vo.setPKUser(rs.getInt("PKUser"));
        		vo.setFamilyName(rs.getString("FamilyName"));
        		vo.setGivenName(rs.getString("GivenName"));
        		vo.setLoginName(rs.getString("LoginName"));
        		vo.setDesignation(rs.getString("Designation"));
        		vo.setIDNumber(rs.getString("IDNumber"));
        		vo.setPassword(rs.getString("Password"));
        		vo.setIsEnabled(rs.getInt("IsEnabled"));
        		vo.setGroupName(rs.getString("GroupName"));
        		vo.setDivisionName(rs.getString("DivisionName"));
        		vo.setDepartmentName(rs.getString("DepartmentName"));
        		vo.setUserTypeName(rs.getString("UserTypeName"));
        	
        		v.add(vo);
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - search_user - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		return v;
	}
	
	public voUser checkUser(String FKDepartment, String FKDivision, String FKUserType, String FamilyName, String GivenName,
								String LoginName, String Designation, String IDNumber, String Group_Section, String IsEnabled,
								int FKCompanyID, int FKOrganization, int SortType) throws SQLException, Exception
	{
		String sSQL;
		
		sSQL = "SELECT * FROM [User] a, Division b, Department c, UserType d, [Group] e ";
		sSQL = sSQL + "WHERE a.FKUserType360 != 1 ";
		sSQL = sSQL + "AND e.PKGroup = a.Group_Section ";
		sSQL = sSQL + "AND b.PKDivision = a.FKDivision ";
		sSQL = sSQL + "AND c.PKDepartment = a.FKDepartment ";
		sSQL = sSQL + "AND d.PKUserType = a.FKUserType360 ";
		sSQL = sSQL + "AND a.FKCompanyID = "+FKCompanyID+" ";
		sSQL = sSQL + "AND a.FKOrganization = "+FKOrganization+"";
	
		if(FKDepartment != null && !FKDepartment.equals("0") && !FKDepartment.equals(""))
		sSQL = sSQL + " AND a.FKDepartment ="+FKDepartment;
		
		if(FKDivision != null && !FKDivision.equals("0") && !FKDivision.equals(""))
		sSQL = sSQL + " AND a.FKDivision =" +FKDivision;
		
		if(FKUserType != null && FKUserType.length() != 0)
		sSQL = sSQL + " AND a.FKUserType360 =" +FKUserType;
		
		if(FamilyName != null && FamilyName.length() != 0)
		sSQL = sSQL + " AND a.FamilyName = '" +FamilyName+ "'";
		
		if(GivenName != null && GivenName.length() != 0)
		sSQL = sSQL + " AND a.GivenName = '" +GivenName+ "'";
		
		if(LoginName != null && LoginName.length() != 0)
		sSQL = sSQL + " AND a.LoginName = '" +LoginName+ "'";
		
		if(Designation != null && Designation.length() != 0)
		sSQL = sSQL + " AND a.Designation = '" +Designation+ "'";
		
		if(IDNumber != null && IDNumber.length() != 0)
		sSQL = sSQL + " AND a.IDNumber = '" +IDNumber+ "'";
		
		if(Group_Section != null && !Group_Section.equals("0") && !Group_Section.equals(""))
		sSQL = sSQL + " AND a.Group_Section = '" +Group_Section+ "'";
		
		if(IsEnabled != null && IsEnabled.length() != 0)
		sSQL = sSQL + " AND a.IsEnabled =" +IsEnabled;
		
		sSQL = sSQL + " ORDER BY ";
		
		if(SortType == 1)
		sSQL = sSQL + "LoginName";
		else if(SortType == 2)
		sSQL = sSQL + "FamilyName";
		else if(SortType == 3)
		sSQL = sSQL + "GivenName";
		else if(SortType == 4)
		sSQL = sSQL + "Designation";
		else if(SortType == 5)
		sSQL = sSQL + "DivisionName";
		else if(SortType == 6)
		sSQL = sSQL + "DepartmentName";
		else if(SortType == 7)
		sSQL = sSQL + "GroupName";
		else if(SortType == 8)
		sSQL = sSQL + "UserTypeName";
		else if(SortType == 9)
		sSQL = sSQL + "IsEnabled";
		
		if(Toggle[SortType - 1] == 1)
		sSQL = sSQL + " DESC";			
		
		voUser vo = new voUser();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sSQL);
                  
            while(rs.next())
            {
            	
            	vo.setPKUser(rs.getInt("PKUser"));
        		vo.setFamilyName(rs.getString("FamilyName"));
        		vo.setGivenName(rs.getString("GivenName"));
        		vo.setLoginName(rs.getString("LoginName"));
        		vo.setDesignation(rs.getString("Designation"));
        		vo.setIDNumber(rs.getString("IDNumber"));
        		vo.setPassword(rs.getString("Password"));
        		vo.setIsEnabled(rs.getInt("IsEnabled"));
        		vo.setGroupName(rs.getString("GroupName"));
        		vo.setDivisionName(rs.getString("DivisionName"));
        		vo.setDepartmentName(rs.getString("DepartmentName"));
        		vo.setUserTypeName(rs.getString("UserTypeName"));
        	
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - check_user - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		return vo;
		
	}
	
	public Vector search_UserWholeDB(String FKDepartment, String FKDivision, String FKUserType, String FamilyName, 
							String GivenName, String LoginName, String Designation, String IDNumber, String Group_Section,
							String IsEnabled, int SortType) throws SQLException, Exception
	{
		String sSQL;
		
		sSQL = "SELECT * FROM [User] a, Division b, Department c, UserType d, [Group] e WHERE a.FKUserType360 != 1 AND e.PKGroup = a.Group_Section AND b.PKDivision = a.FKDivision AND c.PKDepartment = a.FKDepartment AND d.PKUserType = a.FKUserType360";
		
		if(FKDepartment != null  && !FKDepartment.equals("0"))
		sSQL = sSQL + " AND a.FKDepartment ="+FKDepartment;
		
		if(FKDivision != null && !FKDivision.equals("0"))
		sSQL = sSQL + " AND a.FKDivision =" +FKDivision;
		
		if(FKUserType != null && FKUserType.length() != 0)
		sSQL = sSQL + " AND a.FKUserType360 =" +FKUserType;
		
		if(FamilyName != null && FamilyName.length() != 0)
		sSQL = sSQL + " AND a.FamilyName = '" +FamilyName+ "'";
		
		if(GivenName != null && GivenName.length() != 0)
		sSQL = sSQL + " AND a.GivenName = '%" +GivenName+ "%'";
		
		if(LoginName != null && LoginName.length() != 0)
		sSQL = sSQL + " AND a.LoginName = '" +LoginName+ "'";
		
		if(Designation != null && Designation.length() != 0)
		sSQL = sSQL + " AND a.Designation = '" +Designation+ "'";

		if(IDNumber != null && IDNumber.length() != 0)
		sSQL = sSQL + " AND a.IDNumber = '" +IDNumber+ "'";
		
		if(Group_Section != null && !Group_Section.equals("0"))
		sSQL = sSQL + " AND a.Group_Section = '" +Group_Section+ "'";
		
		if(IsEnabled != null && IsEnabled.length() != 0)
		sSQL = sSQL + " AND a.IsEnabled =" +IsEnabled;
		
		sSQL = sSQL + " ORDER BY ";
		
		if(SortType == 1)
			sSQL = sSQL + "LoginName";
		else if(SortType == 2)
			sSQL = sSQL + "FamilyName";
		else if(SortType == 3)
			sSQL = sSQL + "GivenName";
		else if(SortType == 4)
			sSQL = sSQL + "Designation";
		else if(SortType == 5)
			sSQL = sSQL + "DivisionName";
		else if(SortType == 6)
			sSQL = sSQL + "DepartmentName";
		else if(SortType == 7)
			sSQL = sSQL + "GroupName";
		else if(SortType == 8)
			sSQL = sSQL + "UserTypeName";
		else if(SortType == 9)
			sSQL = sSQL + "IsEnabled";
		
		if(Toggle[SortType - 1] == 1)
			sSQL = sSQL + " DESC";			
		
		Vector v = new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sSQL);
                  
            while(rs.next())
            {
            	voUser vo = new voUser();
            	vo.setPKUser(rs.getInt("PKUser"));
        		vo.setFamilyName(rs.getString("FamilyName"));
        		vo.setGivenName(rs.getString("GivenName"));
        		vo.setLoginName(rs.getString("LoginName"));
        		vo.setDesignation(rs.getString("Designation"));
        		vo.setIDNumber(rs.getString("IDNumber"));
        		vo.setPassword(rs.getString("Password"));
        		vo.setIsEnabled(rs.getInt("IsEnabled"));
        		vo.setGroupName(rs.getString("GroupName"));
        		vo.setDivisionName(rs.getString("DivisionName"));
        		vo.setDepartmentName(rs.getString("DepartmentName"));
        		vo.setUserTypeName(rs.getString("UserTypeName"));
        		
        		v.add(vo);
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - search_userWholeDB - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		return v;
	}
	
	/*-------------------------------------User detail--------------------------------------------*/
	public String[] getUserDetail(int PKUser, int NameSequence)throws SQLException
	{
		//db.openDB();
		String sDetail[] = new String[18];
		
		String sql = "SELECT * ";
		sql += "FROM Department INNER JOIN [User] ON Department.PKDepartment = [User].FKDepartment INNER JOIN ";
		sql += "tblOrganization ON [User].FKOrganization = tblOrganization.PKOrganization INNER JOIN ";
		sql += "Division ON [User].FKDivision = Division.PKDivision INNER JOIN [Group] ON ";
		sql += "[User].Group_Section = [Group].PKGroup INNER JOIN UserType ON ";
		sql += "[User].FKUserType = UserType.PKUserType INNER JOIN tblConsultingCompany ON ";
		sql += "[User].FKCompanyID = tblConsultingCompany.CompanyID WHERE [User].PKUser = " + PKUser;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		
	  	try 
        {          

	  		con=ConnectionBean.getConnection();
	  		st=con.createStatement();
	  		rs=st.executeQuery(sql);

           
            if(rs.next())
    	    {
    	    	/* User */
    	    	String OrgName = rs.getString("OrganizationName");
    			String FamilyName = rs.getString("FamilyName");
    			String OtherName = rs.getString("GivenName");
    			
    			if(NameSequence == 0)
    			{
    			   	sDetail[0] = FamilyName;
    	    		sDetail[1] = OtherName;
    	    	}	
    	    	else if(NameSequence == 1)
    	    	{
    	    		sDetail[0] = OtherName;
    	    		sDetail[1] = FamilyName;
    	    	}
    	    		    	
    	    	sDetail[2] = rs.getString("LoginName");
    	    	sDetail[3] = rs.getString("Designation");
    	    	sDetail[4] = rs.getString("IDNumber");
    	    	sDetail[12] = rs.getString("Password");	
    	    	sDetail[5] = rs.getString("IsEnabled");
    	    	sDetail[13] = rs.getString("Email");
    	  		sDetail[6] = rs.getString("DepartmentName");
    	  		sDetail[7] = rs.getString("DivisionName");
    	  		sDetail[8] = rs.getString("UserTypeName");
    	  		sDetail[9] = rs.getString("GroupName");	
    	  		sDetail[10] = OrgName;
    	  		sDetail[11] = rs.getString("CompanyName");
    	  		sDetail[14] = rs.getString("OfficeTel");
    	  		sDetail[15] = rs.getString("Handphone");
    	  		sDetail[16] = rs.getString("MobileProvider");
    	  		sDetail[17] = rs.getString("Remarks");
    	  	}
            
      		
        }
        catch(Exception E) 
        {
            
            System.err.println("User.java - getUserDetail - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection


        }

        return sDetail;
	}
	
	public String[] getUserDetailWithRound(int PKUser, int NameSequence)throws SQLException
	{
		//db.openDB();
		String sDetail[] = new String[19];
		
		String sql = "SELECT * ";
		sql += "FROM Department INNER JOIN [User] ON Department.PKDepartment = [User].FKDepartment INNER JOIN ";
		sql += "tblOrganization ON [User].FKOrganization = tblOrganization.PKOrganization INNER JOIN ";
		sql += "Division ON [User].FKDivision = Division.PKDivision INNER JOIN [Group] ON ";
		sql += "[User].Group_Section = [Group].PKGroup INNER JOIN UserType ON ";
		sql += "[User].FKUserType = UserType.PKUserType INNER JOIN tblConsultingCompany ON ";
		sql += "[User].FKCompanyID = tblConsultingCompany.CompanyID INNER JOIN " +
				"tblAssignment on [User].PKUser = tblAssignment.RaterLoginID WHERE [User].PKUser = " + PKUser;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		//System.out.println(sql);

	  	try 
        {          

	  		con=ConnectionBean.getConnection();
	  		st=con.createStatement();
	  		rs=st.executeQuery(sql);

           
            if(rs.next())
    	    {
    	    	/* User */
    	    	String OrgName = rs.getString("OrganizationName");
    			String FamilyName = rs.getString("FamilyName");
    			String OtherName = rs.getString("GivenName");
    			
    			if(NameSequence == 0)
    			{
    			   	sDetail[0] = FamilyName;
    	    		sDetail[1] = OtherName;
    	    	}	
    	    	else if(NameSequence == 1)
    	    	{
    	    		sDetail[0] = OtherName;
    	    		sDetail[1] = FamilyName;
    	    	}
    	    		    	
    	    	sDetail[2] = rs.getString("LoginName");
    	    	sDetail[3] = rs.getString("Designation");
    	    	sDetail[4] = rs.getString("IDNumber");
    	    	sDetail[12] = rs.getString("Password");	
    	    	sDetail[5] = rs.getString("IsEnabled");
    	    	sDetail[13] = rs.getString("Email");
    	  		sDetail[6] = rs.getString("DepartmentName");
    	  		sDetail[7] = rs.getString("DivisionName");
    	  		sDetail[8] = rs.getString("UserTypeName");
    	  		sDetail[9] = rs.getString("GroupName");	
    	  		sDetail[10] = OrgName;
    	  		sDetail[11] = rs.getString("CompanyName");
    	  		sDetail[14] = rs.getString("Round");
    	  		sDetail[15] = rs.getString("OfficeTel");
    	  		sDetail[16] = rs.getString("Handphone");
    	  		sDetail[17] = rs.getString("MobileProvider");
    	  		sDetail[18] = rs.getString("Remarks");
    	  		
    	  	}
            
      		
        }
        catch(Exception E) 
        {
            
            System.err.println("User.java - getUserDetailWithRound - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection


        }

        return sDetail;
	}
	
	public String[] getUserDetailWithRound(int PKUser) throws SQLException {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		String sDetail[] = new String[21];
		String sql = "SELECT * FROM [User] a, Department b, Division c, UserType d, [Group] e, tblOrganization f, tblConsultingCompany g";
		sql = sql
				+ " WHERE a.FKDepartment = b.PKDepartment AND a.FKDivision= c.PKDivision AND a.FKUserType360 = d.PKUserType";
		sql = sql + " AND a.Group_Section = e.PKGroup AND a.PKUser = " + PKUser;
		sql = sql
				+ " AND a.FKOrganization = f.PKOrganization AND a.FKCompanyID = g.CompanyID";

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);

			if (rs != null && rs.next()) {
				/* User */

				sDetail[0] = rs.getString("FamilyName");
				sDetail[1] = rs.getString("GivenName");
				sDetail[2] = rs.getString("LoginName");
				sDetail[3] = rs.getString("Designation");
				sDetail[4] = rs.getString("IDNumber");
				sDetail[5] = rs.getString("IsEnabled");
				sDetail[12] = rs.getString("Email");
				sDetail[6] = rs.getString("DepartmentName");
				sDetail[7] = rs.getString("DivisionName");
				sDetail[8] = rs.getString("UserTypeName");
				sDetail[9] = rs.getString("GroupName");
				sDetail[10] = rs.getString("OrganizationName");
				sDetail[11] = rs.getString("CompanyName");
				/*
				 * Change : include the id into the details
				 * Reason : because many duplicate name "NA"
				 * Add by : Johanes
				 * Add on : 26/10/2009
				 */	
				sDetail[13] = rs.getString("FKDepartment");
				sDetail[14] = rs.getString("FKDivision");
				sDetail[15] = rs.getString("Group_section");
				sDetail[16] = rs.getString("Round");
				sDetail[17] = rs.getString("OfficeTel");
				sDetail[18] = rs.getString("Handphone");
				sDetail[19] = rs.getString("MobileProvider");
				sDetail[20] = rs.getString("Remarks");
			}

		} catch (Exception E) {

			System.err.println("Create_Edit_Survey .java - getUserDetail - "
					+ E);
		} finally {

			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return sDetail;
	}
	
	/*	Get User's Foreign Key Detail
	 *	(FKDivision, FKDepartment and FKGroup)
	 */
	public int[] getUserFKDetail(int PKUser)throws SQLException
	{
		int iDetail[] = new int[3];
		
		String sql = "SELECT FKDivision, FKDepartment, Group_Section FROM [User] WHERE (PKUser = " + PKUser + ")";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);

			if(rs.next())
		    {
		    	/* User */
		    		    	
		    	iDetail[0] = rs.getInt("FKDivision");
		    	iDetail[1] = rs.getInt("FKDepartment");
		    	iDetail[2] = rs.getInt("Group_Section");	
		  	}
			
        }
        catch(Exception E) 
        {
            System.err.println("User.java - getUserFKDetail - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		

	  	return iDetail;
	}
	/*------------------------------------------------end user detail--------------------------------------*/
	
	public void ForgotPass(int PKUser, String email, int FKOrganization)throws SQLException, Exception
	{
		int NameSequence=0;
		String sql = "SELECT * FROM tblOrganization WHERE PKOrganization = "+FKOrganization;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);

			if(rs.next())
		    {
		    	NameSequence = rs.getInt("NameSequence");
		  	}
			
        }
        catch(Exception E) 
        {
            System.err.println("User.java - ForgotPAss - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
	
		String UserDetail[] = new String[14];
		
		UserDetail = getUserDetail(PKUser,NameSequence);
		
		String nama = UserDetail[0]+" "+UserDetail[1];
		
		String content = template.ForgotPass_temp(nama, UserDetail[12]);
		Email.sendMail(server.getAdminEmail(), email, "Forgot Password", content, defaultOrgID);	
	}	
	
	
	public boolean ChangePass(int PKUser, String Password)throws SQLException, Exception
	{
		String OldName = "";
		String command = "SELECT * FROM [User] WHERE PKUser  = "+ PKUser;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(command);

			if(rs.next())
		    {
		    	OldName = rs.getString("LoginName");
		  	}
			
        }
        catch(Exception E) 
        {
            System.err.println("User.java - ChangePass - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
        
		String query = "UPDATE [User] SET Password ='"+Password+"' WHERE PKUser=" +PKUser;
		
		boolean bIsUpdated = false;
     
		try	
		{

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess = st.executeUpdate(query);
			if(iSuccess!=0)
			bIsUpdated=true;

	
		}
			
		catch(Exception E)
		{
	        System.err.println("USer.java - changePasee- " + E);
		}
		
		finally
    	{
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection


    	}
		
		sDetail = CE_Survey.getUserDetail(PKUser);
		ev.addRecord("Update", itemName, "("+OldName+") - ("+OldName+")", sDetail[2], sDetail[11], sDetail[10]);
	
		return bIsUpdated;
		
	}	
	
	
	/**
	 * This function will check User table & tblAssignment and update the Division, Department & Group according to the latest organisation's structure
	 * Users will only be updated if ALL the 3 fields (Division, Department & Group) have a valid relationship
	 * @param iOrgID
	 * @throws SQLException
	 * @throws Exception
	 * @author Maruli
	 * @return Array ErrorLog
	 */
	public String [] linkUserDivDepGrp(int iOrgID, int iErrorCounter, String [] arError) throws SQLException, Exception
	{
		String arErrorLog[] = arError;
		int iErr = iErrorCounter;
		int iGroup = 0, iDepartment = 0, iDivision = 0;
		String sGroup = "";
		String sDepartment = "";
//		
//		int iProcessed = 0;
//		int [] aiupdateCounts;
//		boolean bError = false;
		
		String sSQL = "SELECT DISTINCT [User].Group_Section, [Group].GroupName FROM [User] INNER JOIN [Group] ON [User].Group_Section = [Group].PKGroup ";
		sSQL = sSQL + "WHERE [User].FKOrganization = " + iOrgID;
		
		Vector vGrp = new Vector();
    	Vector vDept = new Vector();
    	Vector vDiv = new Vector();
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sSQL);
        	
            while(rs.next())
            {
            	voGroup vo = new voGroup();
            	vo.setGroupName(rs.getString("GroupName"));
            	vo.setPKGroup(rs.getInt("Group_Section"));
            	vGrp.add(vo);
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - linkUserDivDepGrp - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
        if(vGrp.size() != 0) {
        
			for(int i=0; i<vGrp.size(); i++)
			{
				voGroup vo = (voGroup)vGrp.elementAt(i);
				// Get all the Groups from User table for update in the latter part
				iGroup = vo.getPKGroup();
				sGroup = vo.getGroupName();
				
				sSQL = "SELECT [Group].FKDepartment, Department.DepartmentName FROM [Group] INNER JOIN Department ON [Group].FKDepartment = Department.PKDepartment ";
				sSQL = sSQL + "WHERE [Group].PKGroup = " + iGroup;
				
				try
		        {          
		        	con=ConnectionBean.getConnection();
		        	st=con.createStatement();
		        	rs=st.executeQuery(sSQL);
		        	
		            while(rs.next())
		            {
		            	voDepartment voDept = new voDepartment();
		            	
		            	voDept.setPKDepartment(rs.getInt("FKDepartment"));
		            	voDept.setDepartmentName(rs.getString("DepartmentName"));
		            	vDept.add(voDept);
		            }
		            
		        }
		        catch(Exception E) 
		        {
		            System.err.println("User.java - linkUserDivDepGrp - " + E);
		        }
		        finally
		        {
			        ConnectionBean.closeRset(rs); //Close ResultSet
			        ConnectionBean.closeStmt(st); //Close statement
			        ConnectionBean.close(con); //Close connection
		        }
			}
			
			if(vDept.size() != 0) {
				
				
				for(int i=0; i<vDept.size(); i++) {
					
					voDepartment voDept = (voDepartment)vDept.elementAt(i);
					
					// Get the updated DepartmentID based on iGroup
					iDepartment = voDept.getPKDepartment();
					sDepartment = voDept.getDepartmentName();
					
					sSQL = "SELECT FKDivision FROM Department WHERE PKDepartment = " + iDepartment;
					
					try
			        {          
			        	con=ConnectionBean.getConnection();
			        	st=con.createStatement();
			        	rs=st.executeQuery(sSQL);
			        	
			            while(rs.next())
			            {
			            	voDivision voDiv = new voDivision();
			            	voDiv.setPKDivision(rs.getInt("FKDivision"));
			            	
			            	vDiv.add(voDiv);
			            }
			            
			        }
			        catch(Exception E) 
			        {
			            System.err.println("User.java - linkUserDivDepGrp - " + E);
			        }
			        finally
			        {
				        ConnectionBean.closeRset(rs); //Close ResultSet
				        ConnectionBean.closeStmt(st); //Close statement
				        ConnectionBean.close(con); //Close connection
			        }
					
				}
			
				for(int i=0; i<vDiv.size(); i++) {
					
					voDivision voDiv = (voDivision)vDiv.elementAt(i);
					
					// Get the updated DivisionID based on updated DepartmentID
					iDivision = voDiv.getPKDivision();
					
					
						
	//					db.con.setAutoCommit(false);
	//					stmt.clearBatch();
						
						// Since Group -> Department -> Division are valid. Update users
					sSQL = "UPDATE [User] SET ";
					sSQL = sSQL + "FKDivision = " + iDivision;
					sSQL = sSQL + ", FKDepartment = " + iDepartment;
					sSQL = sSQL + " WHERE Group_Section = " + iGroup;
	//					stmt.addBatch(sSQL);
						
	//					sSQL = "UPDATE tblAssignment SET ";
	//					sSQL = sSQL + "FKTargetDivision = " + iDivision;
	//					sSQL = sSQL + " FKTargetDepartment = " + iDepartment;
	//					sSQL = sSQL + " WHERE FKTargetGroup = " + iGroup;
	//					stmt.addBatch(sSQL);
						
	//					aiupdateCounts = stmt.executeBatch();
	//					
	//					for (int i = 0; i < aiupdateCounts.length; i++)
	//					{
	//						iProcessed = aiupdateCounts[i];
	//						if( iProcessed > 0 || iProcessed == -2)
	//						{
	//							// statement was successful
	//						}
	//						else
	//						{
	//						    // error on statement
	//						    bError = true;
	//						    break;
	//						}
	//					} // end for
	//
	//				    if( bError ) 
	//				    { 
	//				    	//db.con.rollback(); 
	//				    }
	//				    else 
	//				    { 
	//				    	db.con.commit(); 
	//				    }
						
					boolean bIsUpdated = false;
			        
					try	
					{

						con=ConnectionBean.getConnection();
						st=con.createStatement();
						int iSuccess = st.executeUpdate(sSQL);
						if(iSuccess!=0)
							bIsUpdated=true;

				
					}
						
					catch(Exception E)
					{
				        System.err.println("User.java - linkUserDivDepGrp - " + E);
					}
					
					finally
			    	{
					ConnectionBean.closeStmt(st); //Close statement
					ConnectionBean.close(con); //Close connection


			    	}
					
				}
				
				
			
			}
			else 
			{
				// This department has no relationship with any division yet. Skip updating
				arErrorLog[iErr] = "DEPARTMENT <"+sDepartment+"> does not have a relationship with any Division yet. All users under this department will not be updated";
				iErr++;
			}
		}
		else
		{
			// This group has no relationship with any department yet. Skip updating
			arErrorLog[iErr] = "GROUP <"+sGroup+"> does not have a relationship with any Departments yet. All users under this group will not be updated.";
			iErr++;
		}
		

		return arErrorLog;
	}
	
	
	/**
	  * Retrieve user name from [User].
	  */
	public int UserName(int OrgID)
	{
		String query = "";
						
		query = query + "select NameSequence from tblOrganization ";
		query = query + " WHERE PKOrganization = " + OrgID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		int iNameSequence = 0;
        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs.next())
            {	
            	iNameSequence = rs.getInt(1);
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - UserName - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return iNameSequence;

	}
	
	/**
	 * get the supervisor info
	 * @param iPKUser
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public voUser getSupervisorInfo(int iPKUser) throws SQLException, Exception
	{
		String sSQL = "SELECT User_1.*, tblUserRelation.RelationType AS RelationType FROM tblUserRelation INNER JOIN [User] ON tblUserRelation.User1 = [User].PKUser INNER JOIN [User] User_1 ON tblUserRelation.User2 = User_1.PKUser WHERE ([User].PKUser = " + iPKUser + ")";
		voUser vo = new voUser();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sSQL);
                  
            if(rs.next())
            {
            	vo.setPKUser(rs.getInt("PKUser"));
    			String Supervisor = rs.getString("FamilyName") + ", " + rs.getString("GivenName") + " (" + rs.getString("LoginName");
    			
    			vo.setSupervisorName(Supervisor);
    			vo.setRelationType(rs.getInt("RelationType"));
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - getSupervisorInfo - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }

		return vo;
	}

	public void set_selectedUser(int selUser) 
	{
		this.selUser = selUser;
	}

	public int get_selectedUser() 
	{
		return selUser;
	}
	
	public void set_LoginName(String LoginName) 
	{
		this.LoginName = LoginName;
	}

	public String get_LoginName() 
	{
		return LoginName;
	}
	
	/**
	 * Store Bean Variable Sort Type.
	 */
	public void setSortType(int SortType) {
		this.SortType = SortType;
	}

	/**
	 * Get Bean Variable SortType.
	 */
	public int getSortType() {
		return SortType;
	}
	
	
	/**
	 * Store Bean Variable toggle either 1 or 0.
	 */	
	public void setToggle(int toggle) {
		Toggle[SortType - 1] = toggle;
	}

	/**
	 * Get Bean Variable toggle.
	 */
	public int getToggle() {
		return Toggle [SortType - 1];
	}	
	
	/**
	 *	Store for SQL statement
	 */
	public void set_SQLstatement(String value) 
	{
		this.SQLstatement = value;
	}
	
	/**
	 *	Get for SQL statement
	 */
	public String get_SQLstatement() 
	{
		return SQLstatement;
	}
	
	//copy FKDepartment, FKDivision, Group_Section  from [User] 
	//to FKTargetDivision, FKTargetDepartment, FKTargetGroup in tblAssignment
	public boolean copyOrgStruc() throws SQLException, Exception{
		/*String sql = "SELECT DISTINCT  [User].PKUser, [User].FKDepartment, [User].FKDivision, [User].Group_Section, " +
					 "tblAssignment.TargetLoginID FROM [User] INNER JOIN " +
					 "tblAssignment ON [User].PKUser = tblAssignment.TargetLoginID";
		*/
		String sql = "SELECT [User].PKUser, [User].FKDepartment, [User].FKDivision, [User].Group_Section FROM [User] ";

		Vector v = new Vector();

        
        boolean bIsUpdated = false;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
        	
            while(rs.next())
            {
            	voUser vo = new voUser();
            	vo.setTargetLoginID(rs.getInt("PKUser"));
            	vo.setGroup_Section(rs.getInt("Group_Section"));
            	vo.setFKDivision(rs.getInt("FKDivision"));
            	vo.setFKDepartment(rs.getInt("FKDepartment"));
            	
    			v.add(vo);
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("User.java - copyOrcStruc - " + E);
        }
        finally
        {
	        ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }

        
		for(int i=0; i<v.size(); i++) {
			
			voUser vo = (voUser)v.elementAt(i);
			
			int TargetLoginID = vo.getTargetLoginID();
			int groupID = vo.getGroup_Section();
			int divID = vo.getFKDivision();
			int deptID = vo.getFKDepartment();
			
			String query = "UPDATE tblAssignment SET " +
					"FKTargetDivision = "+divID+", FKTargetDepartment ="+deptID+", FKTargetGroup = " + groupID + 
					" WHERE TargetLoginID = " + TargetLoginID;
			
			
			try	
			{

				con=ConnectionBean.getConnection();
				st=con.createStatement();
				int iSuccess = st.executeUpdate(query);
				if(iSuccess!=0)
				bIsUpdated=true;

		
			}
				
			catch(Exception E)
			{
		        System.err.println("User.java - CopyOrgStruc- " + E);
			}
			
			finally
	    	{
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection


	    	}
			
		}
		
		return bIsUpdated;
	}
	
	/**
	 * GET AllUsers
	 * 
	 * @return
	 * @author James
	 */
	public Vector getAllUsers(){
		/*
		 * Filter for PKUserType = 9 removed as this is not used
		 * Mark Oei 09 Mar 2010
		 */
		String sSql="SELECT * FROM UserType WHERE ApplicationType = 2 AND PKUserType != 1";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
	
		Vector v = new Vector();
		
	    
	    try
	    {          
	    	con=ConnectionBean.getConnection();
	    	st=con.createStatement();
	    	rs=st.executeQuery(sSql);
	
	                    
	        while(rs.next())
	        {
	        	voUserType vo = new voUserType();
	        	vo.setPKUserType(rs.getInt("PKUserType"));
	        	vo.setUserTypeName(rs.getString("UserTypeName"));
	        	vo.setApplicationType(rs.getInt("ApplicationType"));
	    		v.add(vo);
	        }
	       
	    }
	    catch(Exception E) 
	    {
	        System.err.println("User.java - getAllUser- " + E);
	    }
	    finally
	    {
	
	    	ConnectionBean.closeRset(rs); //Close ResultSet
	    	ConnectionBean.closeStmt(st); //Close statement
	    	ConnectionBean.close(con); //Close connection
	
	    	
	    }

    
	    return v;
	
	}
	
	/**
	 * GET User Info
	 * 
	 * @return
	 * @author Yuni
	 */
	public voUser getUserInfo(int iPKUser){
		String sSql= "SELECT * FROM [User] WHERE PKUser = " + iPKUser;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
		voUser vo = new voUser();
		
	    try
	    {          
	    	con=ConnectionBean.getConnection();
	    	st=con.createStatement();
	    	rs=st.executeQuery(sSql);
	
	                    
	        if(rs.next())
	        {
	        	vo.setPKUser(rs.getInt("PKUser"));
	        	vo.setGivenName(rs.getString("GivenName"));
	        	vo.setFamilyName(rs.getString("FamilyName"));
	        	vo.setFKCompanyID(rs.getInt("FKCompanyID"));
	        	vo.setFKOrganization(rs.getInt("FKOrganization"));
	        	vo.setLoginName(rs.getString("LoginName"));
	        	vo.setPassword(rs.getString("Password"));
	        	vo.setEmail(rs.getString("Email"));
				String sSup =  rs.getString("FamilyName")  + ", " + rs.getString("GivenName") + " (" + rs.getString("LoginName") + ")";

				vo.setSupervisorName(sSup);
	        }
	       
	    }
	    catch(Exception E) 
	    {
	        System.err.println("User.java - getUserInfo - " + E);
	    }
	    finally
	    {
	
	    	ConnectionBean.closeRset(rs); //Close ResultSet
	    	ConnectionBean.closeStmt(st); //Close statement
	    	ConnectionBean.close(con); //Close connection
	
	    }

	    return vo;
	
	}
	
	public voUser getUserPassInfo(String sLoginID, String sEmail, int iFKOrganisation){
		String query = "SELECT * FROM [User] WHERE FKOrganization = " + iFKOrganisation + " AND ";
		
		
		if(!sLoginID.equals("")) {
			query += "[User].LoginName = '" + Utils.SQLFixer(sLoginID.trim()) + "' ";
			
			if(!sEmail.equals(""))
				query += "AND [User].Email LIKE '%" + Utils.SQLFixer(sEmail.trim()) + "%'";
		
		} else {
			if(!sEmail.equals(""))
				query += "[User].Email LIKE '%" + Utils.SQLFixer(sEmail.trim()) + "%'";
		}
		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
		voUser vo = null;
		
	    try
	    {          
	    	con=ConnectionBean.getConnection();
	    	st=con.createStatement();
	    	rs=st.executeQuery(query);
	
	       
	        if(rs.next())
	        {
	        	 vo = new voUser();
	        	 vo.setPKUser(rs.getInt("PKUser"));
	        	 vo.setFKOrganization(rs.getInt("FKOrganization"));
	        	 vo.setEmail(rs.getString("Email"));
	        }
	       
	    }
	    catch(Exception E) 
	    {
	        System.err.println("User.java - getUserPassInfo - " + E);
	    }
	    finally
	    {
	
	    	ConnectionBean.closeRset(rs); //Close ResultSet
	    	ConnectionBean.closeStmt(st); //Close statement
	    	ConnectionBean.close(con); //Close connection
	
	    }

	    return vo;
	
	}

	//Edited by junwei on 5 of March for checking login id and organization id existence on User.jsp
	public boolean checkLoginIdExist(String sLoginID, int sFKOrganizationID){
		String query = "SELECT * FROM [User] Where LoginName='" + sLoginID +"' AND" +
				" FKOrganization=" + sFKOrganizationID + "";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
		{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			if(rs.next())
			{
				return true;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		return false;
	}
	
	/**
	 * 
	 * @return checking login id existence on User.jsp for edit
	 * @author Hemilda 
	 * added on 06/06/2008
	 */
	public boolean checkLoginIdExist_Edit(String sLoginID, int sFKOrganizationID,int PKUser){
		String query = "SELECT * FROM [User] Where LoginName='" + sLoginID +"' AND" +
				" FKOrganization=" + sFKOrganizationID + " AND PKUser <> "+PKUser;
		//System.out.println(query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
		{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			if(rs.next())
			{
				return true;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		return false;
	}
	
	/**
	 * Check if user is assign to survey
	 * 
	 * @return true if user is assign else, false
	 * @author Junwei 
	 * added on March 4 2008 
	 */
	public boolean isUserAssign(int selectedID){
		
		String query = "SELECT * FROM tblAssignment ta WHERE ta.TargetLoginID = " + selectedID + 
				" OR ta.RaterLoginID = " + selectedID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
		{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			if(rs.next())
			{
				return true;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		return false;
	}

	/* Method Name : chkAdmin
	 * Check if user is Admin
	 * @param isAdmin, userType
	 * @return true if user is admin else, false
	 * @author Mark Oei
	 * since v.1.3.12.63 09 Mar 2010 
	 */
	public boolean chkAdmin(int selectedID){
		
		boolean isAdmin = true;
		int userType = 0;
		String query = "SELECT * FROM [User] WHERE PKUser = " + selectedID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try
		{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			if(rs.next())
			{
				userType = rs.getInt("FKUserType360");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		//System.out.println(selectedID + " " + isAdmin);
		if (userType == 6)
			isAdmin = true;
		else
			isAdmin = false;
		
		return isAdmin;
	}	// End of chkAdmin method
	

	/**
	  * Retrieve sa PK based on user login name
	  * @param LoginName - Specify the loginName to reference
	  * @return int - Return 0 if LoginName is not sa. Return PK of sa if loginName is sa
	  * @author Sebastian
	  * @since v.1.3.12.86 (29 July 2010)
	**/
	public int getSAPKUser(String LoginName) {
		int iPKUser = 0;
		
		String command = "SELECT PKUser FROM [User] " +
				"WHERE LoginName = '" + LoginName + "' " +
						"AND FKUserType = 1";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try
        {          

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(command);
                        
            if(rs.next())
    			iPKUser = rs.getInt("PKUser");
    		
        }
        catch(Exception E) 
        {
            System.err.println("User.java - checkUserExist - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
        
		return iPKUser;
	}
	
}