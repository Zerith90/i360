package CP_Classes;

import java.sql.*;
import java.util.*;
import java.math.*;
import util.Utils;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voCompetency;
import CP_Classes.vo.voEthnic;
import CP_Classes.vo.voGroup;
import CP_Classes.vo.voUser;
import CP_Classes.vo.votblAssignment;
import CP_Classes.vo.votblSurvey;

/** 
 * Change Log
 * ==========
 * Date        By				Method(s)            																Change(s) 
 * ===================================================================================================================================================
 * 29/05/12	  Albert	CalculateStatus(int,int,int,Vector<Integer>,Vector<Integer>,int),						Added override methods to accommodate
 * 						TargetID(int,int,Vector<Integer>,Vector<Integer>,String),								the changes made
 * 						TargetID(int,int,Vector<Integer>,Vector<Integer>)
 * 						checkCalculationStatusComplete(int,int,int,Vector<Integer>,Vector<Integer>),
 * 						isAllRaterRated(int,Vector<Integer>,int), isAllRated(int,Vector<Integer>, int)
 * 
 */

/**
 * This class implements all results retrieval process of the 360 Feedback
 * Survey. Last Updated By: Chun Pong Last Updated On: 24 Jun 2008
 */
public class SurveyResult {
	/**
	 * Declaration of new object of class Database. This object is declared
	 * private, which is to make sure that it is only accessible within this
	 * class.
	 */
	private Database db;

	/**
	 * Declaration of new object of class User. This object is declared private,
	 * which is to make sure that it is only accessible within this class.
	 */
	private Calculation C;

	/**
	 * Declaration of new object of class Calculation. This object is declared
	 * private, which is to make sure that it is only accessible within this
	 * class.
	 */
	private User_Jenty U;

	/**
	 * Declaration of new object of class EventViewer.
	 */
	private EventViewer EV;

	/**
	 * Bean Variable to store SurveyID.
	 */
	public int SurveyID;

	/**
	 * Bean Variable to store SurveyLevel.
	 */
	public int SurveyLevel;

	/**
	 * Bean Variable to store GroupID.
	 */
	public int GroupID;

	/**
	 * Bean Variable to store DeptID.
	 */
	public int DeptID;

	/**
	 * Bean Variable to store DivID.
	 */
	public int DivID;

	/**
	 * Bean Variable to store TargetID.
	 */
	public int TargetID;

	/**
	 * Bean Variable to store RaterID.
	 */
	public int RaterID;

	/**
	 * Bean Variable to store assignmentID of a rater. From assignmentID, we can
	 * retrieve SurveyID, targetID, rater status, etc.
	 */
	public int AssignmentID;

	/**
	 * Bean Variable for sorting purposes. Total Array depends on total
	 * SortType. 0 = ASC 1 = DESC
	 */
	private int Toggle; // 0=asc, 1=desc

	/**
	 * Bean Variable to store the Sorting type, such as sort by CompetencyName,
	 * Definition, Origin.
	 */
	public int SortType;

	public Vector vDiv = null;

	public Vector vGroup;

	public Vector vDept;

	/**
	 * Creates a new instance of SurveyResult Object.
	 */
	public SurveyResult() {
		db = new Database();
		C = new Calculation();
		EV = new EventViewer();
		U = new User_Jenty();

		// added in by Jenty on 21st March at 6.11 PM
		vGroup = new Vector();
		vDept = new Vector();
		DeptID = -1;
		GroupID = -1;

		Toggle = 0;

		SortType = 1;

	}

	/**
	 * Retrieves the Assignment Information.
	 */
	public String[] assignmentInfo(int assignmentID) {
		String info[] = new String[3];

		String query = "SELECT tblSurvey.SurveyName, tblOrganization.NameSequence, [User].FamilyName, [User].GivenName, ";
		query = query
				+ "User_1.FamilyName AS RaterFamily, User_1.GivenName AS RaterGiven ";
		query = query + "FROM tblAssignment INNER JOIN ";
		query = query
				+ "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query = query
				+ "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
		query = query
				+ "[User] User_1 ON tblAssignment.RaterLoginID = User_1.PKUser ";
		query = query + "INNER JOIN tblOrganization ON ";
		query = query
				+ "tblSurvey.FKOrganization = tblOrganization.PKOrganization ";
		query = query + "WHERE tblAssignment.AssignmentID = " + assignmentID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				info[0] = rs.getString("SurveyName");

				int seq = rs.getInt("NameSequence");
				String targetFamily = rs.getString("FamilyName");
				String targetGiven = rs.getString("GivenName");
				String raterFamily = rs.getString("RaterFamily");
				String raterGiven = rs.getString("RaterGiven");

				if (seq == 0) {
					info[1] = targetFamily + " " + targetGiven;
					info[2] = raterFamily + " " + raterGiven;
				} else {
					info[1] = targetGiven + " " + targetFamily;
					info[2] = raterGiven + " " + raterFamily;
				}
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - assignmentInfo - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return info;
	}

	/**
	 * Retrieve the Survey Information from tblSurvey to be used in Rater's Data
	 * Entry.
	 */
	public votblSurvey SurveyInfo(int surveyID) {

		votblSurvey vo = new votblSurvey();
		String query = "Select * from tblSurvey where SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {

				vo.setSurveyName(rs.getString("SurveyName"));
				vo.setLevelOfSurvey(rs.getInt("LevelOfSurvey"));
				vo.setSurveyStatus(rs.getInt("SurveyStatus"));
				vo.setOpenedDate(rs.getDate("DateOpened"));
				vo.setDeadlineSubmissionDate(rs.getDate("DeadlineSubmission"));
				// Added Auto Assign Self and Superior
				// By: Chun Pong
				// Date: 24 Jun 2008
				// Reason: To provide the Auto Assignment information to all
				// methods
				vo.setAutoSelf(rs.getBoolean("AutoAssignSelf"));
				vo.setAutoSup(rs.getBoolean("AutoAssignSuperior"));
			}

		} catch (Exception ex) {
			System.out.println("SurveyResult.java - SurveyInfo - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return vo;
	}

	/**
	 * Retrieve the Survey Information from tblSurvey based on SurveyName &
	 * OrgID.
	 */
	public votblSurvey SurveyInfo(String SurveyName, int OrgID) {

		votblSurvey vo = new votblSurvey();

		String query = "Select * from tblSurvey where SurveyName = '"
				+ SurveyName + "' AND FKOrganization = " + OrgID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				vo.setSurveyID(rs.getInt("SurveyID"));
				vo.setSurveyName(rs.getString("SurveyName"));
				vo.setLevelOfSurvey(rs.getInt("LevelOfSurvey"));
				// Added Auto Assign Self and Superior
				// By: Chun Pong
				// Date: 24 Jun 2008
				// Reason: To provide the Auto Assignment information to all
				// methods
				vo.setAutoSelf(rs.getBoolean("AutoAssignSelf"));
				vo.setAutoSup(rs.getBoolean("AutoAssignSuperior"));
			}

		} catch (Exception ex) {
			System.out.println("SurveyResult.java - SurveyInfo - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return vo;
	}

	/**
	 * Retrieve the Survey Information from tblSurvey based on SurveyName, OrgID
	 * & CompID
	 */
	public votblSurvey SurveyInfo(String SurveyName, int CompID, int OrgID) {

		String query = "Select * from tblSurvey where SurveyName = '"
				+ SurveyName + "' AND FKOrganization = " + OrgID
				+ " AND FKCompanyID = " + CompID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		votblSurvey vo = new votblSurvey();
		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {

				vo.setSurveyID(rs.getInt("SurveyID"));
				vo.setSurveyName(rs.getString("SurveyName"));
				// Added Auto Assign Self and Superior
				// By: Chun Pong
				// Date: 24 Jun 2008
				// Reason: To provide the Auto Assignment information to all
				// methods
				vo.setAutoSelf(rs.getBoolean("AutoAssignSelf"));
				vo.setAutoSup(rs.getBoolean("AutoAssignSuperior"));
			}

		} catch (Exception E) {
			System.err.println("Group.java - getGroup - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return vo;

	}

	/**
	 * Retrieves the Company Name.
	 */
	public String CompanyName(int compID) {
		String compName = "";

		String query = "Select * from tblConsultingCompany where CompanyID = "
				+ compID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				compName = rs.getString(2);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - CompanyName - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return compName;
	}

	/**
	 * Retrieves the Organization Name.
	 */
	public String OrganizationName(int orgID) {
		String compName = "";

		String query = "Select * from tblOrganization where PKOrganization = "
				+ orgID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs != null && rs.next()) {
				compName = rs.getString(3);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - OrganizationName - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return compName;
	}

	/**
	 * Retrieves the Survey Name.
	 */
	public String SurveyName(int surveyID) {
		String sSurveyName = "";

		String query = "Select * from tblSurvey where SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				sSurveyName = rs.getString(2);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - SurveyName - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return sSurveyName;
	}

	/**
	 * Retrieves the Survey Level in String.
	 */
	public String SurveyLevel(int surveyID) {
		String sSurveyLevel = "";

		String query = "Select * from tblSurvey where SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		int id = 0;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				id = rs.getInt("LevelOfSurvey");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - SurveyLevel - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		if (id == 0)
			sSurveyLevel = "Comptency Level";
		else
			sSurveyLevel = "Key Behaviour Level";

		return sSurveyLevel;
	}

	/**
	 * Retrieves the Username based on the sequence.
	 */
	public String UserName(int orgID, int pkUser) {
		String query = "";
		int nameSequence = U.NameSequence(orgID);

		if (nameSequence == 0) {
			query = query + "select FamilyName + ' ' + GivenName from [User] ";
			query = query + " WHERE PKUser = " + pkUser;
		} else {
			query = query + "select GivenName + ' ' + FamilyName from [User] ";
			query = query + " WHERE PKUser = " + pkUser;
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		String sUserName = "";

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				sUserName = rs.getString(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - UserName - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return sUserName;
	}

	/**
	 * Retrieve all the group section available under the specific Survey based
	 * on rater and target ID that have been assigned to the survey.
	 */
	public voGroup GroupSection(int surveyID) {

		/*
		 * String query = "SELECT DISTINCT [Group].PKGroup, [Group].GroupName
		 * FROM [Group] INNER JOIN "; query = query + "[User] ON [Group].PKGroup
		 * = [User].Group_Section INNER JOIN tblAssignment ON "; query = query +
		 * "[User].PKUser = tblAssignment.TargetLoginID INNER JOIN tblSurvey ON "
		 * ; query = query + "tblAssignment.SurveyID = dbo.tblSurvey.SurveyID
		 * WHERE tblSurvey.SurveyID = " + surveyID;
		 */

		String query = "SELECT DISTINCT [Group].PKGroup, [Group].GroupName FROM [Group] INNER JOIN ";
		query += "tblAssignment ON [Group].PKGroup = tblAssignment.FKTargetGroup WHERE ";
		query += "(tblAssignment.SurveyID = " + surveyID
				+ ") ORDER BY [Group].GroupName";

		voGroup vo = new voGroup();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		String sUserName = "";

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				vo.setPKGroup(rs.getInt(1));
				vo.setGroupName(rs.getString(2));
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - GroupSection - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return vo;
	}

	/**
	 * Retrieve target's ID based on AssignmentID.
	 */
	public int TargetID(int assignmentID) {
		int id = 0;

		String query = "SELECT * from tblAssignment where AssignmentID = "
				+ assignmentID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				id = rs.getInt("TargetLoginID");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return id;
	}

	/**
	 * Retrieve all targets' IDs under the particular survey and group.
	 */
	public Vector TargetID(int surveyID, int groupSection) {

		String query = "SELECT DISTINCT tblAssignment.TargetLoginID, [User].GivenName, [User].FamilyName AS Name ";
		query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
		query += "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
		query += "(tblSurvey.SurveyID = " + surveyID
				+ ") AND ([Group].PKGroup = " + groupSection + ")";

		/*
		 * String query = "SELECT DISTINCT tblAssignment.TargetLoginID,
		 * [User].GivenName, [User].FamilyName "; query = query + "AS Name FROM
		 * [Group] INNER JOIN [User] ON [Group].PKGroup = [User].Group_Section
		 * "; query = query + "INNER JOIN tblAssignment ON [User].PKUser =
		 * tblAssignment.TargetLoginID INNER JOIN "; query = query + "tblSurvey
		 * ON dbo.tblAssignment.SurveyID = dbo.tblSurvey.SurveyID WHERE "; query
		 * = query + "tblSurvey.SurveyID = " + surveyID +
		 * " AND [Group].PKGroup = " + groupSection;
		 */
		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				voUser vo = new voUser();
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				vo.setGivenName(rs.getString("GivenName"));
				vo.setFamilyName(rs.getString("Name"));

				v.add(vo);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;

	}

	/**
	 * Retrieve all targets' IDs under the particular survey and group.
	 */
	public Vector TargetID(int surveyID, int deptID, int groupSection) {

		String query = "SELECT DISTINCT tblAssignment.TargetLoginID, [User].GivenName, [User].FamilyName AS Name ";
		query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = "
				+ surveyID + ")";
		/*
		 * String query = "SELECT DISTINCT tblAssignment.TargetLoginID,
		 * [User].GivenName, [User].FamilyName "; query = query + "AS Name FROM
		 * [Group] INNER JOIN [User] ON [Group].PKGroup = [User].Group_Section
		 * "; query = query + "INNER JOIN tblAssignment ON [User].PKUser =
		 * tblAssignment.TargetLoginID INNER JOIN "; query = query + "tblSurvey
		 * ON dbo.tblAssignment.SurveyID = dbo.tblSurvey.SurveyID WHERE "; query
		 * = query + "tblSurvey.SurveyID = " + surveyID;
		 */

		if (groupSection > 0)
			query += " AND [Group].PKGroup = " + groupSection;

		if (deptID > 0)
			query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";
		// query += " AND ([User].FKDepartment = " + deptID + ")";

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				voUser vo = new voUser();
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				vo.setGivenName(rs.getString("GivenName"));
				vo.setFamilyName(rs.getString("Name"));

				v.add(vo);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * 
	 * @param targetLoginId
	 * @param surveyID
	 * @param divID
	 * @param deptID
	 * @param groupSection
	 * @return boolean result for update status
	 * @author Thant Thura Myo
	 * @since 14 Jan 2008
	 * @lastUpdate 14 Jan 2008;
	 * @see SurveyResult.jsp
	 */
	public boolean CalculateStatus(int targetLoginId, int surveyID, int divID,
			int deptID, int groupSection, int iAdminCalc) {
		boolean bResult = false;
		int counter = 0;
		
		// retrieve the Assignment ID for a particular target login ID.
		// Comment updated on 1/4/2008 by Yun
		String query = "SELECT tblAssignment.AssignmentID ";
		query += "FROM tblAssignment ";
		query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = "
				+ surveyID + ")";
		query += " And tblAssignment.targetLoginID= " + targetLoginId;
		query += " AND tblAssignment.calculationStatus=0 ";
		query += " AND tblAssignment.RaterStatus=1 ";
		if (groupSection > 0)
			query += " AND [Group].PKGroup = " + groupSection;

		if (deptID > 0)
			query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";

		if (divID > 0)
			query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();

			rs = st.executeQuery(query);

			while (rs.next()) {
				Integer iAssignmentID = new Integer(rs.getInt("AssignmentID"));
				
				v.add(iAssignmentID);

			}
			System.out
					.println("Size of vector in CalculateStatus: " + v.size() + query);
		} catch (SQLException E) {
			System.err.println("SurveyResult.java -  CalculateStatus- " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		/********************************
		 * Updated 15 jan 2008
		 * 
		 * @author Thant Thura Myo Calculate survey result
		 *         ******************************
		 */
		// call the calculation methods
		// Comment updated on 21/4/2008 by Yuni

		/*
		 * Change(s) : Seperate the AvgMean calculation and the Reliability and
		 * GAP calculation into 2 loops instead of one so they are done
		 * consecutively (AvgMean for all survey assignments than Reliability
		 * and GAP calculation for all survey assignments. Reason(s) :
		 * Calculation of AvgMean for all assignments must be done first due to
		 * the dependency of records in tblAvgMean and tblAvgMeanByRater used
		 * for Calculation of Reliability. Updated By: Sebastian Updated On: 29
		 * June 2010
		 */
		for (int i = 0; i < v.size(); i++) {
			int iAssignment = ((Integer) v.elementAt(i)).intValue();
			try {
				// add message to identify progress of Timer Calculation
				
				this.calculateAvgMean(iAssignment, 0);
			} catch (SQLException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		if(v.size() > 0){
			int assignmentID =((Integer)v.elementAt(0)).intValue();
			int roundNo = getRoundNo(assignmentID);
			C.calculateWeightedAverage(surveyID,assignmentID);
		}
		// Added a loop to call the method calculateReliabilityAndGAP() after
		// calulating AvgMean for all assignments in survey
		// Sebastian 29 June 2010
		for (int i = 0; i < v.size(); i++) {
			int iAssignment = ((Integer) v.elementAt(i)).intValue();
			try {
				// add message to identify progress of Timer Calculation
				System.out
						.println("Calculate Reliability and GAP for Assignment : "
								+ iAssignment);
				calculateReliabilityAndGAP(iAssignment, 0);
			} catch (SQLException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		// Add by Santoso (2008-11-12)
		// After checking the reliability, check for NA rater
		for (int i = 0; i < v.size(); i++) {
			int iAssignment = ((Integer) v.elementAt(i)).intValue();
			// add message to identify progress of status Calculation
			System.out
					.println("Check NA rater for Assignment : " + iAssignment);
			C.checkNARater(surveyID, targetLoginId, iAssignment);
		}

		PreparedStatement ps = null;
		con = null;
		try {
			// set the calculation status to 1
			// Comment updated on 1/4/2008 by Yun
			// Changed by Ha 26/06/08 to update calculation status to 1
			// Problem with old query: calculation status depends on admin
			// calculation status, which is not the case
			// String query2 =
			// "Update tblAssignment Set CalculationStatus= "+iAdminCalc+" where AssignmentID=?";
			String updateQuery = "Update tblAssignment Set CalculationStatus = 1 where AssignmentID = ?";
			con = ConnectionBean.getConnection();
			ps = con.prepareStatement(updateQuery);
			Iterator iter = v.iterator();

			while (iter.hasNext()) {
				Integer iAssign = (Integer) iter.next();
				int iAssignment = iAssign.intValue();

				ps.setInt(1, iAssignment);
				int iResult = ps.executeUpdate();
				if (iResult != 0) {
					counter++;
				}
			}

		} catch (SQLException E) {
			System.err.println("SurveyResult.java -  CalculateStatus-Insert "
					+ E);
		} catch (Exception ex) {
			System.err
					.println("SurveyResult.java -  CalculateStatus-Insert Exception "
							+ ex);
		} finally {
			ConnectionBean.closePStmt(ps);// close prepare statement
			ConnectionBean.close(con); // Close connection

		}

		// set the admin calculation status to 1 for a particular target ID
		// Comment updated on 1/4/2008 by Yun
		if (counter == v.size()) {
			bResult = true;
			// Changed by Ha 26/06/08 to update the admin calculation/system
			// calculation status correcly
			// Problem with old statment: always admin calculations status
			updateAdminStatus(targetLoginId, surveyID, iAdminCalc);
		}

		return bResult;
	}

	public boolean CalculateStatus(int targetLoginId, int surveyID, int divID,
			Vector<Integer> deptID, Vector<Integer> groupSection, int iAdminCalc) {
		boolean bResult = false;
		int counter = 0;

		// retrieve the Assignment ID for a particular target login ID.
		// Comment updated on 1/4/2008 by Yun
		String query = "SELECT tblAssignment.AssignmentID ";
		query += "FROM tblAssignment ";
		query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = "
				+ surveyID + ")";
		query += " And tblAssignment.targetLoginID= " + targetLoginId;
		query += " AND tblAssignment.calculationStatus=0 ";
		query += " AND tblAssignment.RaterStatus=1 ";
		if (groupSection != null && groupSection.elementAt(0) > 0) {
			if (groupSection.size() == 1)
				query += "AND [Group].PKGroup = " + groupSection.elementAt(0)
						+ " ";
			else {
				for (int i = 0; i < groupSection.size(); i++) {
					if (groupSection.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query + "AND ([Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
						else if (i == (groupSection.size() - 1))
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + ") ";
						else
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
					}
				}
			}
		}

		if (deptID != null && deptID.elementAt(0) > 0) {
			if (deptID.size() == 1)
				query += "AND tblAssignment.FKTargetDepartment = "
						+ deptID.elementAt(0) + " ";
			else {
				for (int i = 0; i < deptID.size(); i++) {
					if (deptID.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query
									+ "AND (tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + " ";
						else if (i == (deptID.size() - 1))
							query = query
									+ "OR tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + ") ";
						else
							query = query
									+ "OR tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + " ";
					}
				}
			}
		}

		if (divID > 0)
			query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();

			rs = st.executeQuery(query);

			while (rs.next()) {
				Integer iAssignmentID = new Integer(rs.getInt("AssignmentID"));

				v.add(iAssignmentID);

			}
		} catch (SQLException E) {
			System.err.println("SurveyResult.java -  CalculateStatus- " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		/********************************
		 * Updated 15 jan 2008
		 * 
		 * @author Thant Thura Myo Calculate survey result
		 *         ******************************
		 */
		// call the calculation methods
		// Comment updated on 21/4/2008 by Yuni

		/*
		 * Change(s) : Seperate the AvgMean calculation and the Reliability and
		 * GAP calculation into 2 loops instead of one so they are done
		 * consecutively (AvgMean for all survey assignments than Reliability
		 * and GAP calculation for all survey assignments. Reason(s) :
		 * Calculation of AvgMean for all assignments must be done first due to
		 * the dependency of records in tblAvgMean and tblAvgMeanByRater used
		 * for Calculation of Reliability. Updated By: Sebastian Updated On: 29
		 * June 2010
		 */
		for (int i = 0; i < v.size(); i++) {
			int iAssignment = ((Integer) v.elementAt(i)).intValue();
			try {
				// add message to identify progress of Timer Calculation
			
				this.calculateAvgMean(iAssignment, 0);
			} catch (SQLException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		// Added a loop to call the method calculateReliabilityAndGAP() after
		// calulating AvgMean for all assignments in survey
		// Sebastian 29 June 2010
		for (int i = 0; i < v.size(); i++) {
			int iAssignment = ((Integer) v.elementAt(i)).intValue();
			try {
				// add message to identify progress of Timer Calculation
				calculateReliabilityAndGAP(iAssignment, 0);
			} catch (SQLException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		// Add by Santoso (2008-11-12)
		// After checking the reliability, check for NA rater
		for (int i = 0; i < v.size(); i++) {
			int iAssignment = ((Integer) v.elementAt(i)).intValue();
			// add message to identify progress of status Calculation
			C.checkNARater(surveyID, targetLoginId, iAssignment);
		}

		PreparedStatement ps = null;
		con = null;
		try {
			// set the calculation status to 1
			// Comment updated on 1/4/2008 by Yun
			// Changed by Ha 26/06/08 to update calculation status to 1
			// Problem with old query: calculation status depends on admin
			// calculation status, which is not the case
			// String query2 =
			// "Update tblAssignment Set CalculationStatus= "+iAdminCalc+" where AssignmentID=?";
			String updateQuery = "Update tblAssignment Set CalculationStatus = 1 where AssignmentID = ?";
			con = ConnectionBean.getConnection();
			ps = con.prepareStatement(updateQuery);
			Iterator iter = v.iterator();

			while (iter.hasNext()) {
				Integer iAssign = (Integer) iter.next();
				int iAssignment = iAssign.intValue();

				ps.setInt(1, iAssignment);
				int iResult = ps.executeUpdate();
				if (iResult != 0) {
					counter++;
				}
			}

		} catch (SQLException E) {
			System.err.println("SurveyResult.java -  CalculateStatus-Insert "
					+ E);
		} catch (Exception ex) {
			System.err
					.println("SurveyResult.java -  CalculateStatus-Insert Exception "
							+ ex);
		} finally {
			ConnectionBean.closePStmt(ps);// close prepare statement
			ConnectionBean.close(con); // Close connection

		}

		// set the admin calculation status to 1 for a particular target ID
		// Comment updated on 1/4/2008 by Yun
		if (counter == v.size()) {
			bResult = true;
			// Changed by Ha 26/06/08 to update the admin calculation/system
			// calculation status correcly
			// Problem with old statment: always admin calculations status
			updateAdminStatus(targetLoginId, surveyID, iAdminCalc);
		}

		return bResult;
	}

	/**
	 * updateAdminStatus
	 * 
	 * @param iTargetLoginID
	 *            , iSurveyID, iAdminCalc
	 * @return boolean
	 * 
	 * @author throw Exception: SQLException, Exception edit code by Ha 26/05/08
	 *         add iAdminCalc to method signature
	 */
	public boolean updateAdminStatus(int iTargetLoginID, int iSurveyID,
			int iAdminCalc) {
		Connection con = null;
		Statement st = null;
		// Changed by Ha 25/06/08 to set AdminCalcStatus to Admin/Sys correcly
		// Problem with old query: only set to 1
		String sql = "UPDATE tblAssignment SET AdminCalcStatus = " + iAdminCalc
				+ " WHERE TargetLoginID = " + iTargetLoginID
				+ " AND SurveyID = " + iSurveyID;
		/*
		 * PreparedStatement ps = db.con.prepareStatement(sql);
		 * ps.executeUpdate();
		 */
		boolean bIsUpdated = false;

		try {
			// bIsUpdated = sqlMethod.bUpdate(sql);

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if (iSuccess != 0)
				bIsUpdated = true;

		}

		catch (Exception E) {
			System.err.println("SurveyResult.java - updateAdminStatus- " + E);
		}

		finally {

			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return bIsUpdated;

	}
	public int getRoundNo(int iAssignmentID) {
		Connection con = null;
		Statement st = null;
		// Changed by Ha 25/06/08 to set AdminCalcStatus to Admin/Sys correcly
		// Problem with old query: only set to 1
		String sql = "Select round from tblAssignment where assignmentID =" + iAssignmentID;
		/*
		 * PreparedStatement ps = db.con.prepareStatement(sql);
		 * ps.executeUpdate();
		 */
		
		int round=1;// default round is 1.
		try {
			// bIsUpdated = sqlMethod.bUpdate(sql);

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			round = rs.getInt("round");

		}

		catch (Exception E) {
			System.err.println("SurveyResult.java - getRoundNo " + E);
		}

		finally {

			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return round;

	}

	/**
	 * Date Created 11 Mar 2008
	 * 
	 * @author thant thura myo
	 * @see SurveyResult.jsp
	 */
	public boolean updateAdminStatusSystemCalc(int iTargetLoginID,
			int iSurveyID, int divID, int deptID, int groupSection) {

		// check the Calculation status whether it has been all calculated
		boolean isComplete = checkCalculationStatusComplete(iTargetLoginID,
				iSurveyID, divID, deptID, groupSection);
		Connection con = null;
		Statement st = null;

		String sql = "UPDATE tblAssignment SET AdminCalcStatus = 0 WHERE TargetLoginID = "
				+ iTargetLoginID + " AND SurveyID = " + iSurveyID;
		/*
		 * PreparedStatement ps = db.con.prepareStatement(sql);
		 * ps.executeUpdate();
		 */
		boolean bIsUpdated = false;

		if (isComplete) {
			try {
				// bIsUpdated = sqlMethod.bUpdate(sql);

				con = ConnectionBean.getConnection();
				st = con.createStatement();
				int iSuccess = st.executeUpdate(sql);
				if (iSuccess != 0)
					bIsUpdated = true;

			}

			catch (Exception E) {
				System.err
						.println("SurveyResult.java - updateAdminStatusSystemCalc- "
								+ E);
			}

			finally {

				ConnectionBean.closeStmt(st); // Close statement
				ConnectionBean.close(con); // Close connection

			}
		}

		return bIsUpdated;

	}

	/**
	 * check whether all calculation status are complete(1) for specific
	 * surveyID and targetIDLOgin ID
	 * 
	 * @param targetLoginID
	 * @param surveyID2
	 * @return true if all calculation are complete, false if not complete
	 *         edited by Ha 26/06/08 change private->public to call in jsp page
	 */
	public boolean checkCalculationStatusComplete(int targetLoginID,
			int surveyID, int divID, int deptID, int groupSection) {
		boolean isCalculated = false;
		String query = "SELECT  tblSurvey.SurveyID, tblAssignment.TargetLoginID, tblAssignment.AssignmentID, tblAssignment.CalculationStatus "
				+ " FROM  tblAssignment INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN"
				+ " [Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup "
				+ "WHERE (tblSurvey.SurveyID = "
				+ surveyID
				+ ") AND (tblAssignment.TargetLoginID = " + targetLoginID + ")";

		if (groupSection > 0)
			query += " AND [Group].PKGroup = " + groupSection;

		if (deptID > 0)
			query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";

		if (divID > 0)
			query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();

			rs = st.executeQuery(query);

			A: while (rs.next()) {
				int iCalc = rs.getInt("CalculationStatus");
				if (iCalc == 1) {
					isCalculated = true;

				} else {
					isCalculated = false;
					break A;
				}

			}
		} catch (SQLException E) {
			System.err
					.println("SurveyResult.java -  cneckCalculationStatusComplete- "
							+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return isCalculated;
	}

	public boolean checkCalculationStatusComplete(int targetLoginID,
			int surveyID, int divID, Vector<Integer> deptID,
			Vector<Integer> groupSection) {
		boolean isCalculated = false;
		String query = "SELECT  tblSurvey.SurveyID, tblAssignment.TargetLoginID, tblAssignment.AssignmentID, tblAssignment.CalculationStatus "
				+ " FROM  tblAssignment INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN"
				+ " [Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup "
				+ "WHERE (tblSurvey.SurveyID = "
				+ surveyID
				+ ") AND (tblAssignment.TargetLoginID = " + targetLoginID + ")";

		if (groupSection != null && groupSection.elementAt(0) > 0) {
			if (groupSection.size() == 1)
				query += "AND [Group].PKGroup = " + groupSection.elementAt(0)
						+ " ";
			else {
				for (int i = 0; i < groupSection.size(); i++) {
					if (groupSection.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query + "AND ([Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
						else if (i == (groupSection.size() - 1))
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + ") ";
						else
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
					}
				}
			}
		}

		if (deptID != null && deptID.elementAt(0) > 0) {
			if (deptID.size() == 1)
				query += "AND tblAssignment.FKTargetDepartment = "
						+ deptID.elementAt(0) + " ";
			else {
				for (int i = 0; i < deptID.size(); i++) {
					if (deptID.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query
									+ "AND (tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + " ";
						else if (i == (deptID.size() - 1))
							query = query
									+ "OR tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + ") ";
						else
							query = query
									+ "OR tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + " ";
					}
				}
			}
		}

		if (divID > 0)
			query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();

			rs = st.executeQuery(query);

			A: while (rs.next()) {
				int iCalc = rs.getInt("CalculationStatus");
				if (iCalc == 1) {
					isCalculated = true;

				} else {
					isCalculated = false;
					break A;
				}

			}
		} catch (SQLException E) {
			System.err
					.println("SurveyResult.java -  cneckCalculationStatusComplete- "
							+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return isCalculated;
	}

	/**
	 * Retrieve all targets' IDs under the particular survey, div, dept and
	 * group
	 * 
	 * @param surveyID
	 * @param divID
	 * @param deptID
	 * @param groupSection
	 * @return ResultSet
	 */

	public Vector TargetID(int surveyID, int divID, int deptID, int groupSection) {

		String query = "SELECT DISTINCT tblAssignment.TargetLoginID, [User].GivenName, [User].FamilyName AS Name, tblAssignment.FKTargetGroup, tblAssignment.AdminCalcStatus  ";
		query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = "
				+ surveyID + ")";

		if (groupSection > 0)
			query += " AND [Group].PKGroup = " + groupSection;

		if (deptID > 0)
			query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";

		if (divID > 0)
			query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";

		// query += " ORDER BY [User].GivenName, [User].FamilyName";
		query += " ORDER BY ";

		if (SortType == 0)
			query = query + "[User].GivenName";
		else if (SortType == 1)
			query = query + "[User].FamilyName";
		else if (SortType == 2)
			query = query + " tblAssignment.AdminCalcStatus ";
		if (Toggle == 1)
			query = query + " DESC";

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();

			rs = st.executeQuery(query);

			while (rs.next()) {
				voUser vo = new voUser();
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				vo.setGivenName(rs.getString("GivenName"));
				vo.setFamilyName(rs.getString("Name"));
				vo.setFKGroup(rs.getInt("FKTargetGroup"));
				v.add(vo);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	public Vector TargetID(int surveyID, int divID, Vector<Integer> deptID,
			Vector<Integer> groupSection, String designation) {

		String query = "SELECT DISTINCT tblAssignment.TargetLoginID, [User].GivenName, [User].FamilyName AS Name, tblAssignment.FKTargetGroup, tblAssignment.AdminCalcStatus  ";
		query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = "
				+ surveyID + ")";

		if (groupSection != null && groupSection.elementAt(0) > 0) {
			if (groupSection.size() == 1)
				query += "AND [Group].PKGroup = " + groupSection.elementAt(0)
						+ " ";
			else {
				for (int i = 0; i < groupSection.size(); i++) {
					if (groupSection.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query + "AND ([Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
						else if (i == (groupSection.size() - 1))
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + ") ";
						else
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
					}
				}
			}
		}

		if (deptID != null && deptID.elementAt(0) > 0) {
			if (deptID.size() == 1)
				query += "AND tblAssignment.FKTargetDepartment = "
						+ deptID.elementAt(0) + " ";
			else {
				for (int i = 0; i < deptID.size(); i++) {
					if (deptID.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query
									+ "AND (tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + " ";
						else if (i == (deptID.size() - 1))
							query = query
									+ "OR tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + ") ";
						else
							query = query
									+ "OR tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + " ";
					}
				}
			}
		}

		if (divID > 0)
			query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";

		if (!designation.equals(""))
			query += " AND ([User].Designation = '" + designation + "')";

		// query += " ORDER BY [User].GivenName, [User].FamilyName";
		query += " ORDER BY ";

		if (SortType == 0)
			query = query + "[User].GivenName";
		else if (SortType == 1)
			query = query + "[User].FamilyName";
		else if (SortType == 2)
			query = query + " tblAssignment.AdminCalcStatus ";
		if (Toggle == 1)
			query = query + " DESC";

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();

			rs = st.executeQuery(query);

			while (rs.next()) {
				voUser vo = new voUser();
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				vo.setGivenName(rs.getString("GivenName"));
				vo.setFamilyName(rs.getString("Name"));
				vo.setFKGroup(rs.getInt("FKTargetGroup"));
				v.add(vo);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	public Vector TargetID(int surveyID, int divID, Vector<Integer> deptID,
			Vector<Integer> groupSection) {

		String query = "SELECT DISTINCT tblAssignment.TargetLoginID, [User].GivenName, [User].FamilyName AS Name, tblAssignment.FKTargetGroup, tblAssignment.AdminCalcStatus  ";
		query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = "
				+ surveyID + ")";

		if (groupSection != null && groupSection.elementAt(0) > 0) {
			if (groupSection.size() == 1)
				query += "AND [Group].PKGroup = " + groupSection.elementAt(0)
						+ " ";
			else {
				for (int i = 0; i < groupSection.size(); i++) {
					if (groupSection.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query + "AND ([Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
						else if (i == (groupSection.size() - 1))
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + ") ";
						else
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
					}
				}
			}
		}

		if (deptID != null && deptID.elementAt(0) > 0) {
			if (deptID.size() == 1)
				query += "AND tblAssignment.FKTargetDepartment = "
						+ deptID.elementAt(0) + " ";
			else {
				for (int i = 0; i < deptID.size(); i++) {
					if (deptID.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query
									+ "AND (tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + " ";
						else if (i == (deptID.size() - 1))
							query = query
									+ "OR tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + ") ";
						else
							query = query
									+ "OR tblAssignment.FKTargetDepartment = "
									+ deptID.elementAt(i) + " ";
					}
				}
			}
		}

		if (divID > 0)
			query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";

		// query += " ORDER BY [User].GivenName, [User].FamilyName";
		query += " ORDER BY ";

		if (SortType == 0)
			query = query + "[User].GivenName";
		else if (SortType == 1)
			query = query + "[User].FamilyName";
		else if (SortType == 2)
			query = query + " tblAssignment.AdminCalcStatus ";
		if (Toggle == 1)
			query = query + " DESC";

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();

			rs = st.executeQuery(query);

			while (rs.next()) {
				voUser vo = new voUser();
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				vo.setGivenName(rs.getString("GivenName"));
				vo.setFamilyName(rs.getString("Name"));
				vo.setFKGroup(rs.getInt("FKTargetGroup"));
				v.add(vo);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	// Gwen Oh - 20/10/2011
	/**
	 * Retrieve all targets' IDs under the particular survey, div, dept, group
	 * and designation
	 * 
	 * @param surveyID
	 * @param divID
	 * @param deptID
	 * @param groupSection
	 * @param designation
	 * @return ResultSet
	 */
	public Vector TargetID(int surveyID, int divID, int deptID,
			int groupSection, String designation) {

		String query = "SELECT DISTINCT tblAssignment.TargetLoginID, [User].GivenName, [User].FamilyName AS Name, tblAssignment.FKTargetGroup, tblAssignment.AdminCalcStatus  ";
		query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = "
				+ surveyID + ")";

		if (groupSection > 0)
			query += " AND [Group].PKGroup = " + groupSection;

		if (deptID > 0)
			query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";

		if (divID > 0)
			query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";

		if (!designation.equals(""))
			query += " AND ([User].Designation = '" + designation + "')";

		// query += " ORDER BY [User].GivenName, [User].FamilyName";
		query += " ORDER BY ";

		if (SortType == 0)
			query = query + "[User].GivenName";
		else if (SortType == 1)
			query = query + "[User].FamilyName";
		else if (SortType == 2)
			query = query + " tblAssignment.AdminCalcStatus ";
		if (Toggle == 1)
			query = query + " DESC";

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();

			rs = st.executeQuery(query);

			while (rs.next()) {
				voUser vo = new voUser();
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				vo.setGivenName(rs.getString("GivenName"));
				vo.setFamilyName(rs.getString("Name"));
				vo.setFKGroup(rs.getInt("FKTargetGroup"));
				v.add(vo);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Retrieve all raters' IDs under particular survey, group, and target ID.
	 */
	public Vector RaterID(int surveyID, int groupSection, int targetID) {

		/*
		 * String query = "SELECT DISTINCT tblAssignment.RaterLoginID "; query =
		 * query + "FROM [Group] INNER JOIN [User] ON [Group].PKGroup =
		 * [User].Group_Section "; query = query + "INNER JOIN tblAssignment ON
		 * [User].PKUser = tblAssignment.TargetLoginID INNER JOIN "; query =
		 * query + "tblSurvey ON dbo.tblAssignment.SurveyID =
		 * dbo.tblSurvey.SurveyID WHERE "; query = query + "tblSurvey.SurveyID =
		 * " + surveyID + " AND [Group].PKGroup = " + groupSection; query =
		 * query + " and tblAssignment.TargetLoginID <> 0 AND
		 * tblAssignment.TargetLoginID = " + targetID;
		 */

		String query = "SELECT DISTINCT tblAssignment.RaterLoginID,[Group].GroupName,[Group].PKGroup,"
				+ " tblAssignment.RTRelation,tblAssignment.RaterCode,tblAssignment.AssignmentID FROM tblSurvey INNER JOIN ";
		query += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
		query += "(tblSurvey.SurveyID = " + surveyID + ")  AND ";

		if (groupSection != 0)
			query += "([Group].PKGroup = " + groupSection + ") AND ";

		query += "(tblAssignment.TargetLoginID = " + targetID
				+ ") AND (tblAssignment.RaterLoginID <> 0) ";
		query += "Order by GroupName, PKGroup, RTRelation, RaterCode,  AssignmentID, RaterLoginID ";
		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				voUser vo = new voUser();
				vo.setRaterLoginID(rs.getInt("RaterLoginID"));

				v.add(vo);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - RaterID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Retrieve the rater's name and return as array. Array[0] = Given Name.
	 * Array[1] = Family Name. This is necessary because of name sequence which
	 * is different for each organization.
	 */
	public String[] RatersName(int raterID) {
		String name[] = new String[2];

		String query = "Select GivenName, FamilyName from [User] where PKUser = "
				+ raterID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				name[0] = rs.getString(1);
				name[1] = rs.getString(2);

			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - RatersName- " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return name;
	}

	/**
	 * Check the rater's status. Status: 0 = Incomplete 1 = Completed 2 =
	 * Unreliable 3 = Excluded 4 = Included
	 */
	public int RatersStatus(int assignmentID) {

		String query = "Select RaterStatus from tblAssignment where AssignmentID = "
				+ assignmentID;

		int iRaterStatus = 0;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				iRaterStatus = rs.getInt(1);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - RaterStatus - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return iRaterStatus;
	}

	/**
	 * 
	 * @author James Wu
	 * @since 10 Jan 2008
	 * @param assignmentID
	 * @return calculation status
	 * 
	 *         This method is used to get the status of calculation
	 * 
	 */
	public int CalculationStatus(int assignmentID) {

		String query = "Select CalculationStatus from tblAssignment where AssignmentID = "
				+ assignmentID;

		int iCalculationStatus = 0;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				iCalculationStatus = rs.getInt(1);
			}
		} catch (Exception E) {
			System.out.println("SurveyResult.java - CalculationStatus - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return iCalculationStatus;
	}

	/**
	 * @author James Wu
	 * @since 10 Jan 2008
	 * @param assignmentID
	 * @return adminCalcStatus
	 * 
	 */

	public int AdminCalcStatus(int assignmentID) {

		String query = "Select AdminCalcStatus from tblAssignment where AssignmentID = "
				+ assignmentID;

		int iAdminCalcStatus = 0;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				iAdminCalcStatus = rs.getInt(1);
			}
		} catch (Exception E) {
			System.out.println("SurveyResult.java - AdminCalcStatus - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return iAdminCalcStatus;
	}

	public boolean isAllCalculated(int surveyID, int groupSection, int targetID) {

		String query = "SELECT tblAssignment.AdminCalcStatus, tblAssignment.CalculationStatus, tblAssignment.RaterStatus FROM tblSurvey INNER JOIN ";
		query += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
		query += "(tblSurvey.SurveyID = " + surveyID
				+ ") AND ([Group].PKGroup = " + groupSection + ") AND ";
		query += "(tblAssignment.TargetLoginID = " + targetID
				+ ") AND (tblAssignment.RaterLoginID <> 0)";

		boolean bIsCalculated = true;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			int count = 0;
			int temp = 0;
			while (rs.next()) {
				// int iRaterStatus=rs.getInt("RaterStatus");
				int iCalculationStatus = rs.getInt("CalculationStatus");

				if (iCalculationStatus == 1) {
					count++;
				}
				temp++;
			}
			if (temp != count) {
				bIsCalculated = false;
			}

		} catch (Exception E) {
			System.out.println("SurveyResult.java - isAllCalculated - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return bIsCalculated;
	}

	public int TotalRaterCalculated(int surveyID, int groupSection, int targetID) {

		String query = "SELECT COUNT(tblAssignment.AssignmentID) AS Total FROM tblSurvey INNER JOIN ";
		query += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
		query += "(tblSurvey.SurveyID = " + surveyID
				+ ") AND ([Group].PKGroup = " + groupSection + ") AND ";
		query += "(tblAssignment.TargetLoginID = "
				+ targetID
				+ ") AND (tblAssignment.RaterLoginID <> 0) AND CalculationStatus = 1 And RaterStatus = 1";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		int count = 0;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				// int iRaterStatus=rs.getInt("RaterStatus");
				count = rs.getInt("Total");
			}

		} catch (Exception E) {
			System.out.println("SurveyResult.java - TotalRaterCalculated - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return count;
	}

	/**
	 * isAllRaterRated
	 * 
	 * @param surveyID
	 *            , groupSection, targetID
	 * @return boolean true if all rater have completed questionnaire false if
	 *         not
	 * @author Ha by 24/06/08 throw Exception: SQLException, Exception
	 * */
	public boolean isAllRaterRated(int surveyID, int groupSection, int targetID) {
		String query = "SELECT tblAssignment.AdminCalcStatus, tblAssignment.CalculationStatus, tblAssignment.RaterStatus FROM tblSurvey INNER JOIN ";
		query += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
		query += "(tblSurvey.SurveyID = " + surveyID + " ) ";
		if (groupSection != 0)
			query += "AND ([Group].PKGroup = " + groupSection + ")";
		query += " AND (tblAssignment.TargetLoginID = " + targetID
				+ ") AND (tblAssignment.RaterLoginID <> 0)";
		boolean bIsRated = true;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			int iUnRated = 0;
			while (rs.next()) {
				int iRaterStatus = rs.getInt("RaterStatus");
				if (iRaterStatus == 0) {
					iUnRated++;
				}
			}

			if (iUnRated > 0) {
				bIsRated = false;
			}
		} catch (Exception E) {
			System.out.println("SurveyResult.java - isAllRated - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		// System.out.println("Checked result : Rated Status is : " + bIsRated);

		return bIsRated;
	}

	/**
	 * isAllRaterRated
	 * 
	 * @param surveyID
	 *            , groupSection, targetID
	 * @return boolean true if all rater have completed questionnaire false if
	 *         not
	 * @author Ha by 24/06/08 throw Exception: SQLException, Exception
	 * */
	public boolean isAllRaterRated(int surveyID, Vector<Integer> groupSection,
			int targetID) {
		String query = "SELECT tblAssignment.AdminCalcStatus, tblAssignment.CalculationStatus, tblAssignment.RaterStatus FROM tblSurvey INNER JOIN ";
		query += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
		query += "(tblSurvey.SurveyID = " + surveyID + " ) ";

		if (groupSection != null) {
			if (groupSection.size() == 1)
				query += "AND [Group].PKGroup = " + groupSection.elementAt(0)
						+ " ";
			else {
				for (int i = 0; i < groupSection.size(); i++) {
					if (groupSection.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query + "AND ([Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
						else if (i == (groupSection.size() - 1))
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + ") ";
						else
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
					}
				}
			}
		}

		query += " AND (tblAssignment.TargetLoginID = " + targetID
				+ ") AND (tblAssignment.RaterLoginID <> 0)";
		boolean bIsRated = true;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			int iUnRated = 0;
			while (rs.next()) {
				int iRaterStatus = rs.getInt("RaterStatus");
				if (iRaterStatus == 0) {
					iUnRated++;
				}
			}

			if (iUnRated > 0) {
				bIsRated = false;
			}
		} catch (Exception E) {
			System.out.println("SurveyResult.java - isAllRated - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		// System.out.println("Checked result : Rated Status is : " + bIsRated);

		return bIsRated;
	}

	public boolean isAllRated(int surveyID, int groupSection, int targetID) {

		String query = "SELECT tblAssignment.AdminCalcStatus, tblAssignment.CalculationStatus, tblAssignment.RaterStatus FROM tblSurvey INNER JOIN ";
		query += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
		query += "(tblSurvey.SurveyID = " + surveyID + " ) ";
		if (groupSection != 0)
			query += "AND ([Group].PKGroup = " + groupSection + ")";
		query += " AND (tblAssignment.TargetLoginID = " + targetID
				+ ") AND (tblAssignment.RaterLoginID <> 0)";

		boolean bIsRated = true;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			int iUnRated = 0;
			int temp = 0;

			while (rs.next()) {
				int iRaterStatus = rs.getInt("RaterStatus");
				// int iCalculationStatus=rs.getInt("CalculationStatus");

				if (iRaterStatus != 1) {
					iUnRated++;

				}

				temp++;
			}

			if (iUnRated == temp) {
				bIsRated = false;
			}
		} catch (Exception E) {
			System.out.println("SurveyResult.java - isAllRated - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		// System.out.println("Checked result : Rated Status is : " + bIsRated);

		return bIsRated;
	}

	public boolean isAllRated(int surveyID, Vector<Integer> groupSection,
			int targetID) {

		String query = "SELECT tblAssignment.AdminCalcStatus, tblAssignment.CalculationStatus, tblAssignment.RaterStatus FROM tblSurvey INNER JOIN ";
		query += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
		query += "(tblSurvey.SurveyID = " + surveyID + " ) ";

		if (groupSection != null) {
			if (groupSection.size() == 1)
				query += "AND [Group].PKGroup = " + groupSection.elementAt(0)
						+ " ";
			else {
				for (int i = 0; i < groupSection.size(); i++) {
					if (groupSection.elementAt(i) == 0)
						break;
					else {
						if (i == 0)
							query = query + "AND ([Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
						else if (i == (groupSection.size() - 1))
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + ") ";
						else
							query = query + "OR [Group].PKGroup = "
									+ groupSection.elementAt(i) + " ";
					}
				}
			}
		}

		query += " AND (tblAssignment.TargetLoginID = " + targetID
				+ ") AND (tblAssignment.RaterLoginID <> 0)";

		boolean bIsRated = true;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			int iUnRated = 0;
			int temp = 0;

			while (rs.next()) {
				int iRaterStatus = rs.getInt("RaterStatus");
				// int iCalculationStatus=rs.getInt("CalculationStatus");

				if (iRaterStatus != 1) {
					iUnRated++;

				}

				temp++;
			}

			if (iUnRated == temp) {
				bIsRated = false;
			}
		} catch (Exception E) {
			System.out.println("SurveyResult.java - isAllRated - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		// System.out.println("Checked result : Rated Status is : " + bIsRated);

		return bIsRated;
	}

	public int TotalRaterCompleted(int surveyID, int groupSection, int targetID) {

		String query = "SELECT COUNT(tblAssignment.AssignmentID) FROM tblSurvey INNER JOIN ";
		query += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
		query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
		query += "(tblSurvey.SurveyID = " + surveyID
				+ ") AND ([Group].PKGroup = " + groupSection + ") AND ";
		query += "(tblAssignment.TargetLoginID = " + targetID
				+ ") AND (tblAssignment.RaterLoginID <> 0) AND RaterStatus = 1";

		int iCount = 0;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				iCount = rs.getInt(1);
			}

		} catch (Exception E) {
			System.out.println("SurveyResult.java - TotalRaterCompleted- " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		// System.out.println("Checked result : Rated Status is : " + bIsRated);
		return iCount;
	}

	/**
	 * Retrieve the rater's result, which input in the questionnaire.
	 */
	public Vector RaterResult(int assignmentID) {
		String query = "";
		int surveyLevel = C.SurveyLevelByAssignmentID(assignmentID);

		if (surveyLevel == 0) {
			query = query
					+ "SELECT Competency.CompetencyName, tblRatingTask.RatingCode, ";
			query = query
					+ "tblResultCompetency.Result, tblResultCompetency.AssignmentID ";
			query = query
					+ "FROM tblResultCompetency INNER JOIN Competency ON ";
			query = query
					+ "tblResultCompetency.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query = query
					+ "tblRatingTask ON tblResultCompetency.RatingTaskID =tblRatingTask.RatingTaskID ";
			query = query + "WHERE tblResultCompetency.AssignmentID = "
					+ assignmentID;
			query = query
					+ " order by tblResultCompetency.CompetencyID, tblResultCompetency.RatingTaskID";

		} else {
			query = query + "SELECT Competency.CompetencyName, ";
			query = query
					+ "tblRatingTask.RatingCode, tblResultBehaviour.Result, KeyBehaviour.KeyBehaviour ";
			query = query
					+ "FROM KeyBehaviour KeyBehaviour INNER JOIN tblResultBehaviour ON ";
			query = query
					+ "KeyBehaviour.PKKeyBehaviour = tblResultBehaviour.KeyBehaviourID INNER JOIN ";
			query = query
					+ "Competency Competency ON KeyBehaviour.FKCompetency = Competency.PKCompetency ";
			query = query + "INNER JOIN tblRatingTask tblRatingTask ON ";
			query = query
					+ "tblResultBehaviour.RatingTaskID = tblRatingTask.RatingTaskID WHERE ";
			query = query + "tblResultBehaviour.AssignmentID = " + assignmentID;
			query = query
					+ " order by Competency.PKCompetency, KeyBehaviour.PKKeyBehaviour, ";
			query = query + "tblRatingTask.RatingTaskID";
		}

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String[] arr = new String[4];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);
				arr[3] = rs.getString(4);
				v.add(v);
			}

		} catch (Exception E) {
			System.out.println("SurveyResult.java - RaterResult- " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;

	}

	/**
	 * Get total Competency for the particular survey.
	 */
	public int TotalCompetency(int surveyID) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);

		if (surveyLevel == 0) {
			query = query + "select count(*) from tblSurveyCompetency ";
			query = query + "where SurveyID = " + surveyID;
		} else {
			query = query
					+ "select count(distinct CompetencyID) from tblSurveyBehaviour ";
			query = query + "where SurveyID = " + surveyID;
		}

		int iTotal = 0;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				iTotal = rs.getInt(1);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - RaterStatus - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return iTotal;
	}

	/**
	 * Get total Key Behaviour for particular survey.
	 */
	public Vector TotalKB(int surveyID) {
		String query = "";

		query = query
				+ "select CompetencyID, count(KeyBehaviourID) from tblSurveyBehaviour ";
		query = query + "where SurveyID = " + surveyID
				+ " group by CompetencyID ";
		query = query + "order by CompetencyID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {

				v.add(new int[] { rs.getInt("CompetencyID"), rs.getInt(2) });

			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TotalKB- " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/**
	 * Get the total Rating Tasks used in the survey.
	 */
	public int TotalRT(int surveyID) {
		String query = "";

		int iTotalRT = 0;

		query = query + "select count(*) from tblSurveyRating ";
		query = query + "where SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				iTotalRT = rs.getInt(1);
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - TotalRT - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return iTotalRT;
	}

	/**
	 * Return the Rating Codes assigned to survey.
	 */
	public String[] RatingCode(int surveyID) {
		String query = "";
		int totalRT = TotalRT(surveyID);

		String ratingCode[] = new String[totalRT];

		query = query
				+ "SELECT DISTINCT tblRatingTask.RatingCode FROM tblSurveyRating INNER JOIN ";
		query = query
				+ "tblRatingTask ON tblSurveyRating.RatingTaskID = tblRatingTask.RatingTaskID ";
		query = query + "WHERE tblSurveyRating.SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		int i = 0;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next())
				ratingCode[i++] = rs.getString(1);
		} catch (Exception E) {
			System.err.println("SurveyResult.java - Ratingcode - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return ratingCode;

	}

	/**
	 * Get the total rater codes for particular survey and target.
	 */
	public int TotalRaterCode(int surveyID, int targetID) {
		String query = "";

		int iTotal = 0;

		query = query
				+ "select count(RaterCode) from tblAssignment where SurveyID = "
				+ surveyID;
		query = query + " and TargetLoginID = " + targetID
				+ " and RaterStatus <> 3";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				iTotal = rs.getInt(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TotalRatingCode - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return iTotal;
	}

	/**
	 * Get total rater code based on group (SUP or OTHER or SELF).
	 */
	public int TotalRaterCodeSpecific(int surveyID, int targetID, String code) {
		String query = "";
		int total = 0;

		query = query
				+ "select count(RaterCode) from tblAssignment where SurveyID = "
				+ surveyID;
		query = query + " and TargetLoginID = " + targetID;
		query = query + " and RaterCode like '" + code
				+ "' and RaterStatus <> 3";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				total = rs.getInt(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TotalRaterCodeSpecific - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return total;
	}

	/**
	 * Get all the rater codes of the particular survey and target.
	 */
	public Vector RaterCode(int surveyID, int targetID) {
		String query = "";

		query = "";
		query = query
				+ "SELECT tblAssignment.AssignmentID, tblAssignment.RaterCode, [User].GivenName, [User].FamilyName ";
		query = query
				+ "FROM tblAssignment INNER JOIN [User] ON tblAssignment.RaterLoginID = [User].PKUser ";
		query = query + "WHERE tblAssignment.SurveyID = " + surveyID + " AND ";
		query = query + "tblAssignment.TargetLoginID = " + targetID
				+ " AND tblAssignment.RaterStatus <> 3 ";
		query = query
				+ "ORDER BY tblAssignment.RaterCode, tblAssignment.AssignmentID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = new String[4];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);
				arr[3] = rs.getString(4);

				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - RaterCode - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Get the Competency or Key Behaviour List for the particular Survey.
	 */
	public Vector CompOrKBList(int surveyID) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);

		if (surveyLevel == 0) {
			query = query
					+ "SELECT Competency.PKCompetency, Competency.CompetencyName FROM Competency ";
			query = query + "INNER JOIN tblSurveyCompetency ON ";
			query = query
					+ "Competency.PKCompetency = tblSurveyCompetency.CompetencyID WHERE ";
			query = query + "tblSurveyCompetency.SurveyID = " + surveyID
					+ " ORDER BY Competency.PKCompetency";
		} else {
			query = query
					+ "SELECT tblSurveyBehaviour.CompetencyID, Competency.CompetencyName, ";
			query = query
					+ "tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour FROM Competency ";
			query = query + "INNER JOIN tblSurveyBehaviour ON ";
			query = query
					+ "Competency.PKCompetency = tblSurveyBehaviour.CompetencyID INNER JOIN ";
			query = query
					+ "KeyBehaviour ON tblSurveyBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID
					+ " ORDER BY ";
			query = query
					+ "tblSurveyBehaviour.CompetencyID, tblSurveyBehaviour.KeyBehaviourID";

		}
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = null;

				if (surveyLevel == 0) {
					arr = new String[2];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);

				} else {
					arr = new String[4];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);

				}

				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - CompOrKBList - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Retrieve the Target result of each rater to be displayed in Process
	 * Result. The data retrieved is a raw data input by rater. Tables:
	 * tblResultCompetency and tblResultBehaviour.
	 */
	public Vector TargetResult(int surveyID, int targetID, String raterCode) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);

		if (surveyLevel == 0) {

			query = query
					+ "SELECT tblResultCompetency.RatingTaskID, tblResultCompetency.CompetencyID, ";
			query = query
					+ "Competency.CompetencyName, tblAssignment.RaterCode, cast(tblResultCompetency.Result as float) as Result FROM tblAssignment ";
			query = query + "INNER JOIN tblResultCompetency ON ";
			query = query
					+ "tblAssignment.AssignmentID = tblResultCompetency.AssignmentID INNER JOIN ";
			query = query
					+ "Competency ON tblResultCompetency.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblAssignment.TargetLoginID = " + targetID;
			query = query + " AND tblAssignment.SurveyID = " + surveyID;
			query = query + " AND tblAssignment.RaterCode like '" + raterCode
					+ "' ";
			query = query
					+ "ORDER BY tblResultCompetency.RatingTaskID, tblResultCompetency.CompetencyID, ";
			query = query + "tblAssignment.RaterCode";
		} else {
			query = query
					+ "SELECT tblResultBehaviour.RatingTaskID, Competency.PKCompetency, ";
			query = query
					+ "Competency.CompetencyName, tblAssignment.RaterCode, cast(tblResultBehaviour.Result as float) as Result, ";

			query = query
					+ "tblResultBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour ";
			query = query
					+ " FROM tblAssignment INNER JOIN tblResultBehaviour ON ";
			query = query
					+ "tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID ";
			query = query + "INNER JOIN KeyBehaviour ON ";
			query = query
					+ "tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query + "INNER JOIN Competency ON ";
			query = query
					+ "KeyBehaviour.FKCompetency = Competency.PKCompetency WHERE ";
			query = query + "tblAssignment.SurveyID = " + surveyID + " AND ";
			query = query + "tblAssignment.TargetLoginID = " + targetID
					+ " AND ";
			query = query + "tblAssignment.RaterCode LIKE '" + raterCode + "' ";
			query = query + "and tblAssignment.RaterStatus <> 3 ";
			query = query + "ORDER BY tblResultBehaviour.RatingTaskID, ";
			query = query
					+ "KeyBehaviour.FKCompetency, tblResultBehaviour.KeyBehaviourID, ";
			query = query + "tblAssignment.RaterCode";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = null;

				if (surveyLevel == 0) {
					arr = new String[4];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);

				} else {
					arr = new String[6];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);
					arr[4] = rs.getString(5);
					arr[5] = rs.getString(6);
				}
				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetResult - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Retrieve the target average mean from tblAvgMean based on the group. This
	 * is used if the ReliabilityIndex chosen is Average Mean.
	 */
	public Vector getTargetAvgMean(int surveyID, int targetID, int group) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);

		if (surveyLevel == 0) {
			query = query
					+ "SELECT tblAvgMean.RatingTaskID, Competency.PKCompetency, ";
			query = query
					+ "Competency.CompetencyName, round(tblAvgMean.AvgMean, 2) AS Result ";
			query = query + "FROM tblAvgMean INNER JOIN tblRatingTask ON ";
			query = query
					+ "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query = query
					+ "Competency ON tblAvgMean.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblAvgMean.Type = " + group
					+ " AND tblAvgMean.SurveyID = " + surveyID;
			query = query + " AND tblAvgMean.TargetLoginID = " + targetID;
			query = query
					+ " ORDER BY tblAvgMean.RatingTaskID, tblAvgMean.CompetencyID";
		} else {
			query = query
					+ "SELECT tblAvgMean.RatingTaskID, Competency.PKCompetency, ";
			query = query
					+ "Competency.CompetencyName, round(tblAvgMean.AvgMean, 2) AS Result, KeyBehaviour.PKKeyBehaviour, KeyBehaviour.KeyBehaviour ";
			query = query + " FROM tblAvgMean INNER JOIN tblRatingTask ON ";
			query = query
					+ "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query = query
					+ "Competency ON tblAvgMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query = query
					+ "KeyBehaviour ON tblAvgMean.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query + "WHERE tblAvgMean.Type = " + group
					+ " AND tblAvgMean.SurveyID = " + surveyID;
			query = query + " AND tblAvgMean.TargetLoginID = " + targetID;
			query = query
					+ " ORDER BY tblAvgMean.RatingTaskID, tblAvgMean.CompetencyID, KeyBehaviour.PKKeyBehaviour";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = null;

				if (surveyLevel == 0) {
					arr = new String[4];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);

				} else {
					arr = new String[5];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);
					arr[4] = rs.getString(5);
				}
				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getTargetAvgMean - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;

	}

	/**
	 * Get the TrimmedMean Result of all raters based on the group. This is to
	 * be used if the ReliabilityCheck chosen is Trimmed Mean. For KB Level
	 * analysis, this method returns the average mean of results from all raters
	 * under each KB. TrimmedMean has to be on Competency, this will be done in
	 * TrimmedMeanAll method. Group: 1 = ALL Excludes SELF 2 = SUP 3 = OTHER 4 =
	 * SELF
	 */
	public Vector getTargetTrimmedMean(int surveyID, int targetID, int group) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);

		if (surveyLevel == 0) {
			query = query
					+ "SELECT tblTrimmedMean.RatingTaskID, Competency.CompetencyName, cast(tblTrimmedMean.TrimmedMean as numeric(38,2)) AS Result ";
			query = query + "FROM tblTrimmedMean INNER JOIN tblRatingTask ON ";
			query = query
					+ "tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query = query
					+ "Competency ON tblTrimmedMean.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblTrimmedMean.Type = " + group
					+ " AND tblTrimmedMean.SurveyID = " + surveyID;
			query = query + " AND tblTrimmedMean.TargetLoginID = " + targetID;
			query = query
					+ " ORDER BY tblTrimmedMean.RatingTaskID, tblTrimmedMean.CompetencyID";
		} else {
			// for all average up all raters result under each KB, then trim.
			query = query
					+ "SELECT tblAvgMean.RatingTaskID, Competency.CompetencyName, ";
			query = query
					+ " cast(tblAvgMean.AvgMean as numeric(38,2)) AS Result, KeyBehaviour.KeyBehaviour FROM ";
			query = query
					+ "KeyBehaviour INNER JOIN tblAvgMean INNER JOIN Competency ON ";
			query = query
					+ "tblAvgMean.CompetencyID = Competency.PKCompetency ON ";
			query = query
					+ "KeyBehaviour.PKKeyBehaviour = tblAvgMean.KeyBehaviourID WHERE ";
			query = query + "tblAvgMean.SurveyID = " + surveyID
					+ " AND tblAvgMean.TargetLoginID = " + targetID;
			query = query + " AND tblAvgMean.Type = " + group;
			query = query
					+ " order by tblAvgMean.RatingTaskID, Competency.PKCompetency,KeyBehaviour.PKKeyBehaviour";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = null;

				if (surveyLevel == 0) {
					arr = new String[3];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);

				} else {
					arr = new String[4];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);
				}
				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getTargetTrimmedMean - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * This method is to trim the result of group ALL to be displayed in Process
	 * Result. It takes the avg mean of all raters under each KB, then trim.
	 * This method is to be used only under Key Behaviour Level Analysis.
	 */
	public Vector TrimmedMeanAll(int surveyID, int targetID) {
		String query = "";

		query = query
				+ "Select TrimmedMean as Result from tblTrimmedMean where SurveyID = "
				+ surveyID;
		query = query + " and TargetLoginID = " + targetID + " and Type = 1 ";
		query = query + " order by RatingTaskID, CompetencyID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {

				v.add(new Double(rs.getDouble("Result")));
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TrimmedMeanAll - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Retrieves the Average Mean from the tblAvgMean based on each Competency.
	 * Group: 1 = ALL Excludes SELF 2 = SUP 3 = OTHER 4 = SELF
	 */
	public Vector getAvgMean(int surveyID, int targetID, int group) {

		String query = "select RatingTaskID, CompetencyID, cast(AVG(AvgMean) as numeric(38,2)) as Result from tblAvgMean ";
		query = query + "where SurveyID = " + surveyID;
		query = query + " AND TargetLoginID = " + targetID;
		query = query + " and Type = " + group;
		query = query + " group by CompetencyID, RatingTaskID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = new String[3];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);

				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getAvgMean - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Get average CP of Competency based on RelationHigh (Non breakdown group)
	 * 
	 * @param surveyID
	 * @param targetID
	 * @param group
	 * @param iCompetency
	 * @return avg CP
	 * @author Maruli
	 * @see IndividualReport#writeQuadrantScore(int)
	 */
	public double getAvgCPComp(int surveyID, int targetID, int group,
			int iCompetency) {

		double Result = 0;
		String query = "SELECT CAST(AVG(AvgMean) AS numeric(38, 2)) AS Result FROM tblAvgMean "
				+ "WHERE SurveyID = "
				+ surveyID
				+ " AND TargetLoginID = "
				+ targetID
				+ " AND Type = "
				+ group
				+ " AND RatingTaskID = 1 AND CompetencyID = "
				+ iCompetency
				+ " GROUP BY CompetencyID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				Result = rs.getDouble("Result");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getAvgCPComp - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return Result;
	}

	/**
	 * Get average CP of Competency based on High & Specific Relation
	 * 
	 * @param surveyID
	 * @param targetID
	 * @param iRelHigh
	 * @param iRelLow
	 * @param iCompetency
	 * @return avg CP
	 * @author Maruli
	 * @see IndividualReport#writeBreakDownQuadrantScore(int)
	 */
	public double getAvgCPCompBreakDown(int surveyID, int targetID,
			int iRelHigh, int iRelLow, int iCompetency) {

		double Result = 0;

		String query = "SELECT CAST(AVG(RESULTBHV.Result) AS numeric(38, 2)) AS Result, COMP.PKCompetency "
				+ "FROM Competency COMP INNER JOIN KeyBehaviour KB ON COMP.PKCompetency = KB.FKCompetency INNER JOIN "
				+ "tblAssignment ASSIGN INNER JOIN tblSurvey SURVEY ON ASSIGN.SurveyID = SURVEY.SurveyID INNER JOIN "
				+ "tblResultBehaviour RESULTBHV ON ASSIGN.AssignmentID = RESULTBHV.AssignmentID ON "
				+ "KB.PKKeyBehaviour = RESULTBHV.KeyBehaviourID "
				+ "WHERE SURVEY.SurveyID = "
				+ surveyID
				+ " AND RESULTBHV.RatingTaskID = 1 AND ASSIGN.RTRelation = "
				+ iRelHigh
				+ " AND ASSIGN.RTSpecific = "
				+ iRelLow
				+ " AND ASSIGN.TargetLoginID = "
				+ targetID
				+ " GROUP BY COMP.PKCompetency HAVING COMP.PKCompetency = "
				+ iCompetency;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				Result = rs.getDouble("Result");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getAvgCPCompBreakDown - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return Result;
	}

	/**
	 * Get average CP of KB based on RelationHigh (Non breakdown group)
	 * 
	 * @param surveyID
	 * @param targetID
	 * @param group
	 * @param iKB
	 * @return avg CP
	 * @author Maruli
	 * @see IndividualReport#writeQuadrantScore(int)
	 */
	public double getAvgCPKB(int surveyID, int targetID, int group, int iKB) {
		double Result = 0;

		String query = "SELECT KeyBehaviourID, CAST(AVG(AvgMean) AS numeric(38,2)) AS Result FROM tblAvgMean "
				+ "WHERE SurveyID = "
				+ surveyID
				+ " AND Type = "
				+ group
				+ " AND (RatingTaskID = 1) "
				+ "AND TargetLoginID = "
				+ targetID
				+ " AND KeyBehaviourID = "
				+ iKB
				+ " GROUP BY KeyBehaviourID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				Result = rs.getDouble("Result");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getAvgCPKB - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return Result;
	}

	/**
	 * Get average CP of KB based on High & Specific Relation
	 * 
	 * @param surveyID
	 * @param targetID
	 * @param iRelHigh
	 * @param iRelLow
	 * @param iKB
	 * @return avg CP
	 * @author Maruli
	 * @see IndividualReport#writeBreakDownQuadrantScore(int)
	 */
	public double getAvgCPKBBreakDown(int surveyID, int targetID, int iRelHigh,
			int iRelLow, int iKB) {
		double Result = 0;
		String query = "SELECT CAST(AVG(RESULTBHV.Result) AS numeric(38, 2)) AS Result, RESULTBHV.KeyBehaviourID "
				+ "FROM tblAssignment ASSIGN INNER JOIN "
				+ "tblResultBehaviour RESULTBHV ON ASSIGN.AssignmentID = RESULTBHV.AssignmentID "
				+ "WHERE RESULTBHV.RatingTaskID = 1 AND ASSIGN.RTRelation = "
				+ iRelHigh
				+ " AND ASSIGN.RTSpecific = "
				+ iRelLow
				+ " AND ASSIGN.TargetLoginID = "
				+ targetID
				+ " AND ASSIGN.SurveyID = "
				+ surveyID
				+ " GROUP BY RESULTBHV.KeyBehaviourID HAVING RESULTBHV.KeyBehaviourID = "
				+ iKB;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				Result = rs.getDouble("Result");

		} catch (Exception E) {
			System.err
					.println("SurveyResult.java - getAvgCPKBBreakdown - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return Result;

	}

	/**
	 * Check the existance of the particular RaterCode. Returns: 0 = NOT Exist 1
	 * = Exist
	 * 
	 */
	public int checkRaterGroup(int surveyID, int targetID, String code) {

		int exist = 0;

		String query = "select * from tblAssignment where SurveyID = "
				+ surveyID;
		query = query + " and TargetLoginID = " + targetID
				+ " and RaterCode like '" + code + "'";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				exist = 1;

		} catch (Exception E) {
			System.err.println("SurveyResult.java - checkRaterGroup- " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return exist;
	}

	/**
	 * Retrieves the average mean from tblAvgMean/tblTrimmedMean based on each
	 * Competency to be displayed in target gap.
	 */
	public Vector getAvgMeanForGap(int surveyID, int targetID) {

		String query = "";
		int reliability = C.ReliabilityCheck(surveyID);

		if (reliability == 1) {
			query = "SELECT tblAvgMean.RatingTaskID, CompetencyID, CAST(AVG(AvgMean) AS numeric(38,2)) AS Result FROM tblAvgMean";
			query += " INNER JOIN tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID";
			query += " WHERE SurveyID = " + surveyID + " AND TargetLoginID = "
					+ targetID + " AND Type = 1";
			query += " AND (tblRatingTask.RatingCode IN ('CP', 'CPR', 'FPR'))";
			query += " GROUP BY CompetencyID, tblAvgMean.RatingTaskID";
			query += " ORDER BY CompetencyID, tblAvgMean.RatingTaskID";
		} else {
			query = "SELECT tblTrimmedMean.RatingTaskID, CompetencyID, TrimmedMean AS Result FROM tblTrimmedMean ";
			query += "INNER JOIN tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query += "WHERE SurveyID = " + surveyID;
			query += " AND TargetLoginID = " + targetID + " AND Type = 1 ";

			// add in to filter, this reflect toyota_idp_report and
			// summaryreport.
			query += " AND (tblRatingTask.RatingCode IN ('CP', 'CPR', 'FPR')) ";
			query += " ORDER BY CompetencyID, tblTrimmedMean.RatingTaskID";
		}
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = new String[3];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);

				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getAvgMeanForGap - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * ********************************* CHECK RELIABILITY
	 * ***********************************************
	 */

	/**
	 * Retrieve the reliability results of each rater from tblAssignment.
	 */
	public Vector getReliability(int surveyID, int targetID) {

		String query = "SELECT tblAssignment.AssignmentID, tblAssignment.RaterCode, ";
		query = query + "tblAssignment.RaterStatus FROM tblAssignment WHERE ";
		query = query + "tblAssignment.SurveyID = " + surveyID + " AND ";
		query = query + "tblAssignment.TargetLoginID = " + targetID + " AND ";
		query = query + "tblAssignment.RaterStatus IN (1, 2, 4) ";
		query = query + "ORDER BY tblAssignment.RaterCode";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				votblAssignment vo = new votblAssignment();
				vo.setAssignmentID(rs.getInt(1));
				vo.setRaterCode(rs.getString(2));
				vo.setRaterStatus(rs.getInt(3));

				v.add(vo);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getReliability - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Retrieve the individual rater input for display purpose.
	 */
	public Vector IndividualResult(int assignmentID) {
		String query = "";
		int surveyLevel = C.SurveyLevelByAssignmentID(assignmentID);

		if (surveyLevel == 0) {
			query = query
					+ "SELECT tblResultCompetency.RatingTaskID, Competency.CompetencyName, ";
			query = query
					+ "tblResultCompetency.Result FROM Competency INNER JOIN tblResultCompetency ON ";
			query = query
					+ "Competency.PKCompetency = tblResultCompetency.CompetencyID INNER JOIN ";
			query = query
					+ "tblAssignment ON tblResultCompetency.AssignmentID = tblAssignment.AssignmentID INNER JOIN ";
			query = query
					+ "tblRatingTask ON tblResultCompetency.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "WHERE tblResultCompetency.AssignmentID = "
					+ assignmentID + " AND ";
			query = query + "tblAssignment.RaterStatus IN (1,2,4) ORDER BY ";
			query = query
					+ "tblResultCompetency.RatingTaskID, Competency.CompetencyName";
		} else {
			query = query
					+ "SELECT tblResultBehaviour.RatingTaskID, Competency.CompetencyName, ";
			query = query
					+ "tblResultBehaviour.Result, KeyBehaviour.KeyBehaviour FROM tblResultBehaviour INNER JOIN ";
			query = query
					+ "KeyBehaviour ON tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour INNER JOIN ";
			query = query
					+ "Competency ON KeyBehaviour.FKCompetency = Competency.PKCompetency INNER JOIN ";
			query = query
					+ "tblRatingTask ON dbo.tblResultBehaviour.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query
					+ "INNER JOIN tblAssignment ON tblResultBehaviour.AssignmentID = tblAssignment.AssignmentID ";
			query = query + "WHERE tblResultBehaviour.AssignmentID = "
					+ assignmentID;
			query = query
					+ "ORDER BY tblResultBehaviour.RatingTaskID, Competency.CompetencyName, ";
			query = query + "KeyBehaviour.KeyBehaviour";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = null;

				if (surveyLevel == 0) {
					arr = new String[3];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);

				} else {
					arr = new String[4];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);
				}
				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - IndividualResult - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;

	}

	/**
	 * *************************************** BEGINNING TARGET GAP
	 * ***************************************************
	 */

	/**
	 * Retrieves the trimmedMean/AvgMean results on each Competency to be
	 * displayed in target gap.
	 */
	public Vector TargetGapDisplayed(int surveyID, int targetID) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);
		int reliabilityCheck = C.ReliabilityCheck(surveyID); // 0=trimmed
		// mean

		String tableName = "";
		String columnName = "";

		if (surveyLevel == 0) {
			if (reliabilityCheck == 0) {
				tableName = "tblTrimmedMean";
				columnName = "TrimmedMean";
			} else {
				tableName = "tblAvgMean";
				columnName = "AvgMean";
			}

			query = query + "SELECT " + tableName
					+ ".RatingTaskID, Competency.CompetencyName, ";
			query = query + tableName + "." + columnName + " AS Result FROM "
					+ tableName + " INNER JOIN ";
			query = query + "tblRatingTask ON " + tableName
					+ ".RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "INNER JOIN Competency ON " + tableName
					+ ".CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE " + tableName + ".Type = 1 AND " + tableName
					+ ".SurveyID = " + surveyID;
			query = query + " AND " + tableName + ".TargetLoginID = "
					+ targetID;
			query = query
					+ " and tblRatingTask.RatingCode IN ('CP', 'CPR', 'FPR')";
			query = query + " ORDER BY " + tableName + ".CompetencyID, "
					+ tableName + ".RatingTaskID";

		} else {
			/*
			 * query = query + "SELECT tblAvgMean.RatingTaskID,
			 * Competency.CompetencyName, "; query = query +
			 * "KeyBehaviour.KeyBehaviour, tblAvgMean.AvgMean AS Result FROM ";
			 * query = query + "KeyBehaviour INNER JOIN tblAvgMean INNER JOIN
			 * Competency ON "; query = query + "tblAvgMean.CompetencyID =
			 * Competency.PKCompetency ON "; query = query +
			 * "KeyBehaviour.PKKeyBehaviour = tblAvgMean.KeyBehaviourID WHERE ";
			 * query = query + "tblAvgMean.SurveyID = " + surveyID + " AND
			 * tblAvgMean.TargetLoginID = " + targetID; query = query + " AND
			 * tblAvgMean.Type = 1"; query = query + " order by
			 * Competency.PKCompetency,KeyBehaviour.PKKeyBehaviour,
			 * tblAvgMean.RatingTaskID";
			 */
			query = "SELECT tblAvgMean.RatingTaskID, Competency.CompetencyName, ";
			query += "tblAvgMean.AvgMean AS Result, KeyBehaviour.KeyBehaviour FROM tblAvgMean INNER JOIN Competency ON ";
			query += "tblAvgMean.CompetencyID = Competency.PKCompetency INNER JOIN KeyBehaviour ON ";
			query += "tblAvgMean.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query += "INNER JOIN tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID WHERE ";
			query += "tblAvgMean.SurveyID = " + surveyID;
			query += " AND tblAvgMean.TargetLoginID = " + targetID;
			query += " AND tblAvgMean.Type = 1 ";
			query += "AND (tblRatingTask.RatingCode IN ('CP', 'CPR', 'FPR')) ";
			query += "ORDER BY Competency.PKCompetency, ";
			query += "KeyBehaviour.PKKeyBehaviour, tblAvgMean.RatingTaskID";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {

				String arr[] = null;

				if (surveyLevel == 0) {
					arr = new String[3];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);

				} else {
					arr = new String[4];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);
				}
				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetGapDisplayed - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		return v;
	}

	/**
	 * Retrieves the target gaps from tblGap.
	 */
	public Vector getTargetGap(int surveyID, int targetID) {
		int surveyLevel = C.LevelOfSurvey(surveyID);
		String query = "";

		if (surveyLevel == 0) {
			query = query
					+ "SELECT CompetencyID, cast(Gap as numeric(38,2)) as Gap from tblGap ";
			query = query + "WHERE SurveyID = " + surveyID;
			query = query + " AND TargetLoginID = " + targetID;
			query = query + " ORDER BY CompetencyID";
		} else {
			query = query
					+ "SELECT CompetencyID, cast(Gap as numeric(38,2)) as Gap, KeyBehaviourID from tblGap ";
			query = query + "WHERE SurveyID = " + surveyID;
			query = query + " AND TargetLoginID = " + targetID;
			query = query + " ORDER BY CompetencyID, KeyBehaviourID";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = null;

				if (surveyLevel == 0) {
					arr = new String[2];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);

				} else {
					arr = new String[3];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);

				}
				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getTargetGap - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Get the total gap records from tblGap.
	 */
	public int getTotalGap(int surveyID, int targetID) {

		int total = 0;
		String query = "SELECT count(*) from tblGap ";
		query = query + "WHERE SurveyID = " + surveyID;
		query = query + " AND TargetLoginID = " + targetID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				total = rs.getInt(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getTotalGap - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return total;
	}

	/**
	 * Get the total summation of gap results in tblGap.
	 */
	public double getSumGap(int surveyID, int targetID) {
		double sum = 0;
		String query = "Select sum(Gap) from tblGap where SurveyID = "
				+ surveyID;
		query = query + " AND TargetLoginID = " + targetID;
		query = query + " group by CompetencyID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				sum = rs.getDouble(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSumGap - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return sum;
	}

	/**
	 * Get the average gap results in tblGap.
	 */
	public double getAvgGap(int surveyID, int targetID) {

		double gap = 0;

		String query = "SELECT CAST(AVG(Gap) AS numeric(38,2)) FROM tblGap WHERE SurveyID = "
				+ surveyID;
		query = query + " AND TargetLoginID = " + targetID;
		query = query + " GROUP BY CompetencyID ORDER BY CompetencyID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				gap = rs.getDouble(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getAvgGap - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return gap;

	}

	/**
	 * Get the average gap of specific competency
	 * 
	 * @param surveyID
	 * @param targetID
	 * @return competency gap value
	 * @author Maruli
	 * @see IndividualReport#drawDevelopmentMap(int, int)
	 */
	public double getCompAvgGapDevMap(int surveyID, int targetID, int iCompID) {
		double dGap = 0;

		String query = "SELECT CAST(AVG(Gap) AS numeric(38, 2)) AS Gap FROM tblGap "
				+ "WHERE SurveyID = "
				+ surveyID
				+ " AND TargetLoginID = "
				+ targetID + " AND CompetencyID = " + iCompID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				dGap = rs.getDouble("Gap");

		} catch (Exception E) {
			System.err
					.println("SurveyResult.java - getCompAvgGapDevMap - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return dGap;
	}

	/**
	 * Get the gap for each KB
	 * 
	 * @param iSurveyID
	 * @param iTargetID
	 * @param iCompID
	 * @return ResultSet
	 * @author Maruli
	 * @see IndividualReport#drawDevelopmentMap(int, int)
	 */
	public Vector getBehaviourGapDevMap(int iSurveyID, int iTargetID,
			int iCompID) {

		String query = "SELECT CAST(tblGap.Gap AS numeric(38, 2)) AS Gap, KeyBehaviour.KeyBehaviour, KeyBehaviour.PKKeyBehaviour "
				+ "FROM tblGap INNER JOIN KeyBehaviour ON tblGap.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour "
				+ "WHERE tblGap.SurveyID = "
				+ iSurveyID
				+ " AND tblGap.TargetLoginID = "
				+ iTargetID
				+ " "
				+ "AND tblGap.CompetencyID = " + iCompID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = new String[3];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);

				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getBehaviourGapDevMap - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * ************************************** END TARGET GAP
	 * *********************************************
	 */

	/**
	 * ****************** BEGINNING STANDARD DEVIATION (might not being used
	 * anymore) ********************
	 */

	/**
	 * Retrieve the Standard Deviation from tblStandardDeviation and display it
	 * properly in the Survey List.
	 */
	public Vector getIndividualStDev(int surveyID, int targetID) {
		String query = "";
		int surveyLevel = C.SurveyLevelByAssignmentID(surveyID);

		if (surveyLevel == 0) {
			query = query
					+ "SELECT tblStandardDeviation.CompetencyID, Competency.CompetencyName, ";
			query = query
					+ "tblStandardDeviation.RatingTaskID, tblStandardDeviation.StandardDeviation ";
			query = query
					+ "FROM Competency INNER JOIN tblStandardDeviation ON ";
			query = query
					+ "Competency.PKCompetency = tblStandardDeviation.CompetencyID ";
			query = query + "WHERE tblStandardDeviation.SurveyID = " + surveyID;
			query = query + " and tblStandardDeviation.TargetLoginID = "
					+ targetID;
			query = query
					+ " ORDER BY tblStandardDeviation.CompetencyID, tblStandardDeviation.KeyBehaviourID, ";
			query = query + "tblStandardDeviation.RatingTaskID";
		} else {
			query = query
					+ "SELECT tblStandardDeviation.CompetencyID, Competency.CompetencyName, ";
			query = query
					+ "tblStandardDeviation.RatingTaskID, tblStandardDeviation.StandardDeviation, ";
			query = query
					+ "tblStandardDeviation.KeyBehaviourID, KeyBehaviour.KeyBehaviour ";
			query = query + "FROM Competency INNER JOIN tblStandardDeviation ";
			query = query
					+ "ON Competency.PKCompetency = tblStandardDeviation.CompetencyID INNER JOIN ";
			query = query
					+ "KeyBehaviour ON tblStandardDeviation.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query + "WHERE tblStandardDeviation.SurveyID = " + surveyID
					+ " AND ";
			query = query + "tblStandardDeviation.TargetLoginID = " + targetID;
			query = query + " ORDER BY tblStandardDeviation.CompetencyID, ";
			query = query
					+ "tblStandardDeviation.KeyBehaviourID, tblStandardDeviation.RatingTaskID";
		}
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = null;

				if (surveyLevel == 0) {
					arr = new String[4];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);

				} else {
					arr = new String[6];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);
					arr[4] = rs.getString(5);
					arr[5] = rs.getString(6);

				}
				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getIndividualStDev - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	public Vector getIndividualAvgStDev(int surveyID, int targetID) {

		String query = "Select CompetencyID, RatingTaskID, AVG(StandardDeviation) from ";
		query = query + "tblStandardDeviation where SurveyID = " + surveyID
				+ " and ";
		query = query + "TargetLoginID = " + targetID;
		query = query + " group by RatingTaskID,CompetencyID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = new String[3];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);

				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getIndividualAvgStDev - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		return v;
	}

	public int getTotalIndividualDev(int surveyID, int targetID) {

		int total = 0;

		String query = "select count(distinct CompetencyID) * count(distinct RatingTaskID) ";
		query = query + "from tblStandardDeviation ";
		query = query + "where SurveyID = " + surveyID
				+ " and TargetLoginID = " + targetID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				total = rs.getInt(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getTotalIndividualDev - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return total;
	}

	/**
	 * ************************************ BEGINNING GROUP STANDARD
	 * DEVIATION******************************************
	 */

	public Vector getGroupAvgStDev(int surveyID, int groupSection) {

		String query = "Select CompetencyID, RatingTaskID, cast(AVG(StandardDeviation) as float) from ";
		query = query + "tblStandardDeviation where SurveyID = " + surveyID;
		query = query + " and GroupSectionID = " + groupSection;
		query = query + " group by RatingTaskID, CompetencyID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = new String[3];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);

				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getGroupAvgStDev - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		return v;
	}

	/**
	 * get total group
	 * 
	 * @param surveyID
	 * @param groupSection
	 * @return
	 */
	public int getTotalGroupDev(int surveyID, int groupSection) {

		int total = 0;

		String query = "select count(distinct CompetencyID) * count(distinct RatingTaskID) ";
		query = query + "from tblStandardDeviation where SurveyID = "
				+ surveyID;
		query = query + " and GroupSectionID = " + groupSection;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				total = rs.getInt(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getTotalGroupDev - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return total;
	}

	/**
	 * *************************************** END GROUP STANDARD
	 * DEVIATION******************************************
	 */

	/**
	 * *************************************** BEGINNING GROUP AVERAGE MEAN
	 * ******************************************
	 */

	public Vector getGroupAvgMean(int surveyID, int type, int groupSection) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);

		if (surveyLevel == 0) {
			query = query
					+ "SELECT tblSurveyAvgMean.CompetencyID, Competency.CompetencyName, ";
			query = query
					+ "tblSurveyAvgMean.RatingTaskID, tblSurveyAvgMean.SurveyAvgMean ";
			query = query + "FROM Competency INNER JOIN tblSurveyAvgMean ON ";
			query = query
					+ "Competency.PKCompetency = tblSurveyAvgMean.CompetencyID ";
			query = query + "WHERE tblSurveyAvgMean.SurveyID = " + surveyID;
			query = query + " and Type = " + type + " and GroupSection = "
					+ groupSection;
			query = query
					+ " ORDER BY tblSurveyAvgMean.CompetencyID, Competency.CompetencyName, ";
			query = query + "tblSurveyAvgMean.RatingTaskID";
		} else {
			query = query
					+ "SELECT tblSurveyAvgMean.CompetencyID, Competency.CompetencyName, ";
			query = query
					+ "tblSurveyAvgMean.KeyBehaviourID, KeyBehaviour.KeyBehaviour, ";
			query = query
					+ "tblSurveyAvgMean.RatingTaskID FROM Competency INNER JOIN ";
			query = query
					+ "KeyBehaviour ON Competency.PKCompetency = KeyBehaviour.FKCompetency ";
			query = query + "INNER JOIN tblSurveyAvgMean ON ";
			query = query
					+ "KeyBehaviour.PKKeyBehaviour = tblSurveyAvgMean.KeyBehaviourID AND ";
			query = query
					+ "Competency.PKCompetency = tblSurveyAvgMean.CompetencyID ";
			query = query + "WHERE tblSurveyAvgMean.SurveyID = " + surveyID;
			query = query + " and Type = " + type + " and GroupSection = "
					+ groupSection;
			;
			query = query
					+ "ORDER BY tblSurveyAvgMean.CompetencyID, tblSurveyAvgMean.KeyBehaviourID, ";
			query = query + "tblSurveyAvgMean.RatingTaskID";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = null;

				if (surveyLevel == 0) {
					arr = new String[4];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);

				} else {
					arr = new String[5];
					arr[0] = rs.getString(1);
					arr[1] = rs.getString(2);
					arr[2] = rs.getString(3);
					arr[3] = rs.getString(4);
					arr[4] = rs.getString(5);

				}
				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getGroupAvgMean - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * ********************************************* END GROUP AVERAGE MEAN
	 * ******************************************
	 */

	/**
	 * ******************************************* BEGINNING SURVEY GAP
	 * **********************************************
	 */

	public void calculateSurveyGap(int surveyID, int groupSection) {
		int pkComp = 0, pkKB = 0;
		double result1, result2;
		double gap = 0;

		String query = "select CompetencyID, KeyBehaviourID, RatingTaskID, SurveyAvgMean from tblSurveyAvgMean ";
		query = query + "where SurveyID = " + surveyID
				+ " and Type = 1 and GroupSection = " + groupSection;
		query = query + " order by CompetencyID, KeyBehaviourID, RatingTaskID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				pkComp = rs.getInt("CompetencyID");
				pkKB = rs.getInt("KeyBehaviourID");

				result1 = rs.getDouble("SurveyAvgMean");

				rs.next();
				result2 = rs.getDouble("SurveyAvgMean");

				gap = result1 - result2;

				String[] arr = new String[5];
				arr[0] = Integer.toString(pkComp);
				arr[1] = Integer.toString(pkKB);
				arr[2] = Double.toString(result1);
				arr[3] = Double.toString(result2);
				arr[4] = Double.toString(gap);

				v.add(arr);

				rs.next();
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - calculateSurveyGap - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		for (int i = 0; i < v.size(); i++) {
			String[] arr = (String[]) v.elementAt(i);
			pkComp = Integer.parseInt(arr[0]);
			pkKB = Integer.parseInt(arr[1]);
			gap = Integer.parseInt(arr[4]);

			WritetoSurveyGapDB(surveyID, pkComp, pkKB, gap, groupSection);

		}

	}

	public boolean WritetoSurveyGapDB(int surveyID, int pkComp, int pkKB,
			double gap, int groupSection) {

		String sql = "Insert into tblSurveyGap values (" + surveyID + ","
				+ pkComp;
		sql = sql + "," + pkKB + "," + gap + "," + groupSection + ")";

		Connection con = null;
		Statement st = null;

		boolean bIsAdded = false;
		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if (iSuccess != 0)
				bIsAdded = true;

		} catch (Exception E) {
			System.err.println("SurveyResult.java - WritetoSurveyGapDB - " + E);
		} finally {
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return bIsAdded;
	}

	public double getSurveyAvgGap(int surveyID, int groupSection) {

		String query = "Select AVG(SurveyGap) As SurveyAvgGap from tblSurveyGap where SurveyID = "
				+ surveyID;
		query = query + " and GroupSection = " + groupSection
				+ " group by CompetencyID";

		double gap = 0;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				gap = rs.getDouble(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSurveyAvgGap - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return gap;
	}

	/**
	 * Average from the Survey avgmean based on each Competency
	 */
	public Vector getSurveyAvgMean(int surveyID, int groupSection) {

		String query = "select RatingTaskID, CompetencyID, KeyBehaviourID, AVG(SurveyAvgMean) AS SurveyAvgResult from tblSurveyAvgMean ";
		query = query + "where SurveyID = " + surveyID + " and GroupSection = "
				+ groupSection;
		query = query + " group by RatingTaskID,CompetencyID, KeyBehaviourID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = new String[4];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);
				arr[3] = rs.getString(4);

				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSurveyAvgMean - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * ********************************************* END SURVEY GAP
	 * ***************************************************
	 */

	/**
	 * Get all Raters' ID which status is completed, excludes SELF and the Rater
	 * to be compared for Reliability.
	 */
	public Vector RatersID(int surveyID, int targetID, int raterID) {

		String query = "SELECT [User].PkUser, [User].GivenName, [User].FamilyName FROM tblAssignment INNER JOIN ";
		query = query
				+ "[User] ON tblAssignment.RaterLoginID = [User].PKUser WHERE ";
		query = query + "tblAssignment.SurveyID = " + surveyID + " AND ";
		query = query + "tblAssignment.RaterLoginID <> " + raterID + " AND ";
		query = query + "tblAssignment.TargetLoginID = " + targetID
				+ " AND tblAssignment.RaterStatus IN (1,2,4) ";
		query = query + "ORDER BY [User].PkUser";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String arr[] = new String[4];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);
				arr[3] = rs.getString(4);

				v.add(arr);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java -RatersID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Get all the targets' id for the particular Survey.
	 */
	public Vector TargetsID(int surveyID) {

		String query = "SELECT distinct(tblAssignment.TargetLoginID), ";
		query = query
				+ "[User].GivenName + ' ' + [User].FamilyName FROM tblAssignment ";
		query = query
				+ "INNER JOIN [User] ON dbo.tblAssignment.TargetLoginID = [User].PKUser ";
		query = query + "WHERE tblAssignment.SurveyID = " + surveyID;
		query = query + " ORDER BY tblAssignment.TargetLoginID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				voUser vo = new voUser();
				vo.setTargetLoginID(rs.getInt(1));
				vo.setFullName(rs.getString(2));
				v.add(vo);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java -TargetsID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Get average of KBs under each competency. Changed by Jenty on 14 Nov 06
	 * We don't need to calculate and get from raw score here. The score has
	 * been stored in tblAvgMeanByRater
	 */
	public double getCompScore(int assignmentID, int RTID, int compID) {
		double score = 0;

		/*
		 * String query = "SELECT tblAssignment.AssignmentID,
		 * tblResultBehaviour.RatingTaskID, "; query = query + "
		 * KeyBehaviour.FKCompetency, round(AVG(cast(tblResultBehaviour.Result
		 * as float)), 2) AS Result "; query = query + " FROM tblAssignment
		 * INNER JOIN tblResultBehaviour ON "; query = query + "
		 * tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID INNER
		 * JOIN "; query = query + " KeyBehaviour ON "; query = query + "
		 * tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
		 * query = query + " WHERE tblAssignment.AssignmentID = " +
		 * assignmentID; query = query +
		 * " AND tblResultBehaviour.RatingTaskID = " + RTID; query = query +
		 * " AND KeyBehaviour.FKCompetency = " + compID; query = query + " GROUP
		 * BY tblAssignment.AssignmentID, tblResultBehaviour.RatingTaskID,
		 * "; query = query + " KeyBehaviour.FKCompetency ORDER BY
		 * "; query = query + " tblResultBehaviour.RatingTaskID,
		 * KeyBehaviour.FKCompetency";
		 */
		String query = "SELECT AssignmentID, RatingTaskID, CompetencyID as FKCompetency, AvgMean AS Result "
				+ "FROM tblAvgMeanByRater WHERE AssignmentID = "
				+ assignmentID
				+ " AND RatingTaskID = "
				+ RTID
				+ " AND CompetencyID = "
				+ compID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				score = rs.getDouble("Result");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getCompScore - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return score;
	}

	/**
	 * Get the raterCode for the particular Rater.
	 */
	public String RaterCode(int assignmentID) {
		String sRaterCode = null;

		String query = "select RaterCode from tblAssignment where AssignmentID = "
				+ assignmentID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs != null && rs.next()) {
				sRaterCode = rs.getString("RaterCode");
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - RaterCode - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return sRaterCode;
	}

	public double getSurveyRank(int surveyID, int targetID) {
		double rank = 0;

		String query = "select SUM(Gap) FROM tblGap WHERE SurveyID = "
				+ surveyID;
		query = query + " and TargetLoginID = " + targetID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs != null && rs.next()) {
				rank = rs.getDouble(1);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSurveyRank - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return rank;
	}

	/**
	 * Update the status of rater from Process Result screen. Admin and
	 * SuperAdmin - Exclude is only available if the rater is Unreliable. .
	 * Include is only available if the rater has been excluded. Each time a
	 * rater is Included/Excluded, calculation has to be reinitiated. Type: 1 =
	 * Include 2 = Exclude
	 */
	public void IncludeExcludeRater(int assignmentID, int type, int pkUser,
			int RaterID) throws SQLException, Exception {
		excludeRater(assignmentID, type, RaterID);
		this.calculateAvgMean(assignmentID, 1);
		// Call the method to calculate Reliability and GAP after calculating
		// avgMean
		// Sebastian 29 June 2010
		calculateReliabilityAndGAP(assignmentID, 1);

		String include = "";
		if (type == 1)
			include = "Include";
		else
			include = "Exclude";

		try {
			String[] UserInfo = U.getUserDetail(pkUser);

			String[] info = assignmentInfo(assignmentID);

			String temp = info[0] + "(S); " + info[1] + "(T); " + info[2]
					+ "(R)";
			EV.addRecord(include, "Rater", temp, UserInfo[2], UserInfo[11],
					UserInfo[10]);
		} catch (SQLException SE) {
			System.out.println(SE.getMessage());
		} catch (Exception E) {
			System.out.println(E.getMessage());
		}

	}

	/**
	 * Update rater status to be Included.
	 */
	public boolean excludeRater(int assignmentID, int type, int RaterID) {
		// Changed by Ha 16/06/08 change value from 1 to 4
		int value = 4;
		if (type == 2)
			value = 3;

		String sql = "Update tblAssignment set RaterStatus = " + value;
		sql = sql + " where AssignmentID = " + assignmentID
				+ " AND RaterLoginID = " + RaterID;

		Connection con = null;
		Statement st = null;

		boolean bIsUpdated = false;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if (iSuccess != 0)
				bIsUpdated = true;

		}

		catch (Exception E) {
			System.err.println("SurveyResult.java - excludeRater- " + E);
		}

		finally {
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return bIsUpdated;
	}

	/**
	 * Update Survey status to close.
	 */
	public boolean CloseSurvey(int surveyID, int pkUser, String today)
			throws Exception {

		String sql = "Update tblSurvey set SurveyStatus = 2, AnalysisDate = '"
				+ today + "'";
		sql = sql + " where SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;

		boolean bIsUpdated = false;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if (iSuccess != 0)
				bIsUpdated = true;

		}

		catch (Exception E) {
		}

		finally {
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		String[] UserInfo = U.getUserDetail(pkUser);
		String surveyName = SurveyName(surveyID);
		try {

			EV.addRecord("Update", "Survey", "Close Survey (" + surveyName
					+ ")", UserInfo[2], UserInfo[11], UserInfo[10]);
		} catch (SQLException SE) {
			System.out.println(SE.getMessage());
		}

		return bIsUpdated;

	}

	/**
	 * Update Survey status to open.
	 */
	public boolean OpenSurvey(int surveyID, int pkUser) throws Exception {

		String sql = "Update tblSurvey set SurveyStatus = 1";
		sql = sql + " where SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;

		boolean bIsUpdated = false;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if (iSuccess != 0)
				bIsUpdated = true;

		}

		catch (Exception E) {
			System.err.println("SurveyResult.java - OpenSurvey - " + E);
		}

		finally {
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		String[] UserInfo = U.getUserDetail(pkUser);
		String surveyName = SurveyName(surveyID);
		try {

			EV.addRecord("Update", "Survey", "Re-Open Survey (" + surveyName
					+ ")", UserInfo[2], UserInfo[11], UserInfo[10]);
		} catch (SQLException SE) {
			System.out.println(SE.getMessage());
		}

		return bIsUpdated;

	}

	/**
	 * Get the total number of completed raters based on specific raterCode.
	 */
	public int TotalCompleted(int surveyID, int targetID, String raterCode) {
		int total = 0;

		String sql = "SELECT COUNT(RaterCode) AS Total FROM tblAssignment WHERE SurveyID = "
				+ surveyID;
		sql = sql + " AND TargetLoginID = " + targetID
				+ " AND RaterCode LIKE '" + raterCode + "' AND ";
		sql = sql + "RaterStatus IN (1, 2, 4)";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);

			if (rs.next()) {
				total = rs.getInt(1);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TotalCompleted - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return total;
	}

	/**
	 * Store bean variable SurveyID.
	 */
	public void setSurveyID(int SurveyID) {
		this.SurveyID = SurveyID;
	}

	/**
	 * Get bean variable surveyID.
	 */
	public int getSurveyID() {
		return SurveyID;
	}

	/**
	 * Store bean varibale GroupID.
	 */
	public void setGroupID(int GroupID) {
		this.GroupID = GroupID;
	}

	/**
	 * Get bean varibale GroupID.
	 */
	public int getGroupID() {
		return GroupID;
	}

	/**
	 * Store bean variable TargetID.
	 */
	public void setTargetID(int TargetID) {
		this.TargetID = TargetID;
	}

	/**
	 * Get bean variable TargetID.
	 */
	public int getTargetID() {
		return TargetID;
	}

	/**
	 * Store bean variable RaterID.
	 */
	public void setRaterID(int RaterID) {
		this.RaterID = RaterID;
	}

	/**
	 * Get bean variable RaterID.
	 */
	public int getRaterID() {
		return RaterID;
	}

	/**
	 * Store bean variable SurveyLevel.
	 */
	public void setSurveyLevel(int SurveyLevel) {
		this.SurveyLevel = SurveyLevel;
	}

	/**
	 * Get bean variable SurveyLevel.
	 */
	public int getSurveyLevel() {
		return SurveyLevel;
	}

	/**
	 * Store bean variable AssignmentID.
	 */
	public void setAssignmentID(int AssignmentID) {
		this.AssignmentID = AssignmentID;
	}

	/**
	 * Get bean variable AssignmentID.
	 */
	public int getAssignmentID() {
		return AssignmentID;
	}

	/**
	 * Store bean variable DeptID.
	 */
	public void setDeptID(int DeptID) {
		this.DeptID = DeptID;
	}

	/**
	 * Get bean variable DeptID.
	 */
	public int getDeptID() {
		return DeptID;
	}

	/**
	 * Store bean variable vDept.
	 */
	public void setvDept(Vector vDept) {
		this.vDept = vDept;
	}

	/**
	 * Get bean variable vDept.
	 */
	public Vector getvDept() {
		return vDept;
	}

	/**
	 * Store bean variable vGroup.
	 */
	public void setvGroup(Vector vGroup) {
		this.vGroup = vGroup;
	}

	/**
	 * Get bean variable vGroup.
	 */
	public Vector getvGroup() {
		return vGroup;
	}

	/**
	 * Added by Su See Get bean variable DivID.
	 */
	public int getDivID() {
		return DivID;
	}

	/**
	 * Added by Su See Set bean variable DivID.
	 */
	public void setDivID(int DivID) {
		this.DivID = DivID;
	}

	/**
	 * Store Bean Variable toggle either 1 or 0.
	 */
	public void setToggle(int toggle) {
		Toggle = toggle;
	}

	/**
	 * Get Bean Variable toggle.
	 */
	public int getToggle() {
		return Toggle;
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
	 * Added by Su See Get bean variable vDiv.
	 */
	public Vector getvDiv() {
		return vDiv;
	}

	/**
	 * Store bean variable vDiv.
	 */
	public void setvDiv(Vector vDiv) {
		this.vDiv = vDiv;
	}

	/**
	 * Set all the bean variables to 0.
	 */
	public void refreshBean() {
		this.AssignmentID = 0;
		this.SurveyID = 0;
		this.RaterID = 0;
		this.TargetID = 0;
		this.SurveyLevel = 0;
		this.GroupID = 0;
		this.DeptID = 0;
	}

	/**
	 * Check the result existence.
	 */
	public double IsResultExist(int assignmentID, int RTID, int ID) {
		int surveyLevel = C.SurveyLevelByAssignmentID(assignmentID);
		String query = "";
		double result = -1;

		if (surveyLevel == 0) {
			query = query + "SELECT Result from tblResultCompetency ";
			query = query + "WHERE AssignmentID = " + assignmentID;
			query = query + " AND RatingTaskID = " + RTID;
			query = query + " AND CompetencyID = " + ID;
		} else {
			query = query + "SELECT Result from tblResultBehaviour ";
			query = query + "WHERE AssignmentID = " + assignmentID;
			query = query + " AND RatingTaskID = " + RTID;
			query = query + " AND KeyBehaviourID = " + ID;

		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				result = rs.getDouble(1);
			}

		} catch (Exception E) {
			System.err.println("SurveyReuslt.java - IsResultExist - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return result;
	}

	/**
	 * Get all the rating task ID.
	 */
	public int[] RTID(int surveyID) {

		String query = "";
		int total = TotalRT(surveyID);
		int rt[] = new int[total];

		query = query + "select distinct[RatingTaskID] from tblSurveyRating ";
		query = query + "where SurveyID = " + surveyID
				+ " order by RatingTaskID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			int i = 0;
			while (rs.next())
				rt[i++] = rs.getInt(1);

		} catch (Exception E) {
			System.err.println("SurveyReuslt.java - RTID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return rt;
	}

	/**
	 * Get the group section of the particular target.
	 */
	public int GroupSection(int surveyID, int targetID) {
		int exist = 0;

		String query = "SELECT DISTINCT FKTargetGroup FROM tblAssignment WHERE ";
		query += "(TargetLoginID = " + targetID + ") AND (SurveyID = "
				+ surveyID + ")";
		/*
		 * String query = "SELECT DISTINCT [User].Group_Section FROM
		 * tblAssignment INNER JOIN "; query = query + "[User] ON
		 * tblAssignment.TargetLoginID = [User].PKUser "; query = query + "WHERE
		 * tblAssignment.SurveyID = " + surveyID; query = query + " AND
		 * tblAssignment.TargetLoginID = " + targetID;
		 */
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				exist = rs.getInt(1);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - GroupSection - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return exist;
	}

	/**
	 * ***************************** CHECK WHETHER THERE IS ANY RATER WHO HASN'T
	 * COMPLETED THE SURVEY *******
	 */

	/**
	 * To Check whether there is any rater who hasn't completed the
	 * questionnaire.
	 */
	public int IncompleteRaterExist(int surveyID) {
		int exist = 0;
		String query = "SELECT tblAssignment.* FROM tblAssignment INNER JOIN ";
		query = query
				+ "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID ";
		query = query + "where SurveyID = " + surveyID;
		query = query + " AND tblAssignment.RaterStatus = 0";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				exist = 1;

		} catch (Exception E) {
			System.err.println("SurveyResult.java - IncompleteRaterExist - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return exist;
	}

	/**
	 * *********************************** DELETION
	 * ******************************************************
	 */

	/**
	 * Delete an existing record from the tblAvgMean. The deletion process is to
	 * add in with new result that has been recalculated.
	 */
	public boolean DeletetblAvgMean(int surveyID, int targetID, int type) {

		String sql = "Delete from tblAvgMean where SurveyID = " + surveyID;
		sql = sql + " and TargetLoginID = " + targetID + " and Type = " + type;

		Connection con = null;
		Statement st = null;

		boolean bIsDeleted = false;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if (iSuccess != 0)
				bIsDeleted = true;

		}

		catch (Exception E) {
			System.err.println("SurveyResult.java - DeletetblAvgMean- " + E);
		}

		finally {
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		return bIsDeleted;
	}

	/**
	 * Delete an existing record from the tblGap. The deletion process is to add
	 * in with new result that has been recalculated.
	 */
	public boolean DeletetblGap(int surveyID, int targetID) {

		String sql = "Delete from tblGap where SurveyID = " + surveyID
				+ " and TargetLoginID = " + targetID;

		Connection con = null;
		Statement st = null;

		boolean bIsDeleted = false;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if (iSuccess != 0)
				bIsDeleted = true;

		}

		catch (Exception E) {
			System.err.println("SurveyResult.java - DeletetblGap- " + E);
		}

		finally {
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		return bIsDeleted;
	}

	/**
	 * Delete an existing record from the tblTrimmedMean. The deletion process
	 * is to add in with new result that has been recalculated.
	 */
	public boolean DeletetblTrimmedMean(int surveyID, int targetID, int type) {

		String sql = "Delete from tblTrimmedMean where SurveyID = " + surveyID;
		sql = sql + " and TargetLoginID = " + targetID + " and Type = " + type;

		Connection con = null;
		Statement st = null;

		boolean bIsDeleted = false;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if (iSuccess != 0)
				bIsDeleted = true;

		}

		catch (Exception E) {
			System.err
					.println("SurveyResult.java - DeletetblTrimmedMean- " + E);
		}

		finally {
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		return bIsDeleted;
	}

	/**
	 * Retrieves the assignment Information.
	 */
	public votblSurvey Info(int assignmentID) {

		String query = "select * from tblAssignment where AssignmentID = "
				+ assignmentID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		votblSurvey vo = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				vo = new votblSurvey();
				vo.setSurveyID(rs.getInt("SurveyID"));
				vo.setRaterCode(rs.getString("RaterCode"));
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));

			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - Info - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return vo;
	}

	/**
	 * Retrieves Competency List under a particular Survey.
	 */
	public Vector CompListSurvey(int surveyID) {

		String query = "SELECT Competency.PKCompetency, Competency.CompetencyName ";
		query = query + "FROM tblSurveyCompetency INNER JOIN Competency ON ";
		query = query
				+ "tblSurveyCompetency.CompetencyID = Competency.PKCompetency AND ";
		query = query
				+ "tblSurveyCompetency.CompetencyID = Competency.PKCompetency ";
		query = query + "WHERE tblSurveyCompetency.SurveyID = " + surveyID;
		query = query + " ORDER BY Competency.CompetencyName";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				voCompetency vo = new voCompetency();
				vo.setCompetencyName(rs.getString("CompetencyName"));
				vo.setPKCompetency(rs.getInt("PKCompetency"));

				v.add(vo);

			}

		} catch (SQLException SE) {
			System.out.println("SurveyResult.java - CompListSurvey - "
					+ SE.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Retrieves Competency List under a particular Survey. I created this
	 * function from CompListSurvey because there is a new column in
	 * tblSurveyCompetency (CompetencyRank). Altering the function above MAY
	 * render other functions useless. If every clients database are already
	 * changed, this function should be safe to use (need to add in option for
	 * the ORDER, CompName or CompRank).
	 * 
	 * @param surveyID
	 * @return
	 * @author Maruli
	 */
	public Vector getCompList(int surveyID) {

		String query = "SELECT Competency.PKCompetency, Competency.CompetencyName, tblSurveyCompetency.CompetencyRank ";
		query = query + "FROM tblSurveyCompetency INNER JOIN Competency ON ";
		query = query
				+ "tblSurveyCompetency.CompetencyID = Competency.PKCompetency AND ";
		query = query
				+ "tblSurveyCompetency.CompetencyID = Competency.PKCompetency ";
		query = query + "WHERE tblSurveyCompetency.SurveyID = " + surveyID;
		query = query + " ORDER BY tblSurveyCompetency.CompetencyRank";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				voCompetency vo = new voCompetency();
				vo.setRank(rs.getInt("CompetencyRank"));
				vo.setCompetencyName(rs.getString("CompetencyName"));
				vo.setPKCompetency(rs.getInt("PKCompetency"));

				v.add(vo);

			}

		} catch (SQLException SE) {
			System.out.println("SurveyResult.java - getCompList - "
					+ SE.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		return v;
	}

	// ----added by Tracy 08 sep 08-----------
	// get comment based on AssignmentID, competencyID and KeyBehaviourID
	public String getKBComment(int asgtID, int compID, int kbID) {

		// Vector v = new Vector();
		String comment = "";

		String query = "Select Comment from tblComment where CompetencyID = "
				+ compID + " and AssignmentID = " + asgtID
				+ " and KeyBehaviourID = " + kbID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				comment = rs.getString("Comment");
				// v.add(sComment);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getComment - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		return comment;
	}

	// added by tracy 08 sep 08---------
	// get Key Behaviour ID list-------
	public Vector getKeyBehaviourID(int surveyID, int compID) {
		Vector v = new Vector();
		// String query =
		// "SELECT KeyBehaviour.KeyBehaviour kb FROM tblComment INNER JOIN KeyBehaviour ON tblComment.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour where tblComment.CompetencyID = "+
		// compID;
		// query = query + " and tblComment.AssignmentID =" + asgtID +
		// " order by Comment";
		String query = "SELECT KeyBehaviourID as kb from tblSurveyBehaviour "
				+ " WHERE SurveyID=" + surveyID + " AND CompetencyID=" + compID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {

				// int sKBehaviourID = rs.getInt("kb");
				// v.add(new Integer(sKBehaviourID));
				v.add(new Integer(rs.getInt("kb")));
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getKeyBehaviour - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	// ---------end add by tracy 08 sep 08-----------

	/**
	 * Retrieves Comment given by a rater under each Competency.
	 */
	public Vector getComment(int asgtID, int compID) {

		Vector v = new Vector();
		String query = "Select * from tblComment where CompetencyID = "
				+ compID;
		query = query + " and AssignmentID = " + asgtID + " order by Comment";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String sComment = rs.getString("Comment");
				v.add(sComment);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getComment - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
		return v;
	}

	// ****added by Tracy 8 Sep 08
	// retrieve Key behaviour name based on competency and survey id

	public Vector getKeyBehaviour(int surveyID, int compID) {
		Vector v = new Vector();
		// String query =
		// "SELECT KeyBehaviour.KeyBehaviour kb FROM tblComment INNER JOIN KeyBehaviour ON tblComment.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour where tblComment.CompetencyID = "+
		// compID;
		// query = query + " and tblComment.AssignmentID =" + asgtID +
		// " order by Comment";
		String query = "SELECT kb.KeyBehaviour as kb from tblSurveyBehaviour "
				+ "sb INNER JOIN KeyBehaviour kb ON sb.KeyBehaviourID= "
				+ " kb.PKKeyBehaviour WHERE sb.SurveyID=" + surveyID
				+ " AND sb.CompetencyID=" + compID + "";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {

				String sKBehaviour = rs.getString("kb");
				v.add(sKBehaviour);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getKeyBehaviour - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	// *** end add by Tracy 08 sep 08

	// added by Roger 18 June 2008
	// include function to get keybehaviour name
	/**
	 * Retrieves Key behaviour name given by a rater under each Competency.
	 */
	public Vector getKeyBehaviourNames(int asgtID, int compID) {
		Vector v = new Vector();
		// String query =
		// "SELECT KeyBehaviour.KeyBehaviour kb FROM tblComment INNER JOIN KeyBehaviour ON tblComment.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour where tblComment.CompetencyID = "+
		// compID;
		// query = query + " and tblComment.AssignmentID =" + asgtID +
		// " order by Comment";
		String query = "SELECT  KeyBehaviour.KeyBehaviour AS kb FROM tblComment INNER JOIN  KeyBehaviour ON tblComment.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour WHERE  (tblComment.CompetencyID = "
				+ compID
				+ ") AND (tblComment.AssignmentID = "
				+ asgtID
				+ ") ORDER BY tblComment.Comment";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {

				String sComment = rs.getString("kb");
				v.add(sComment);
			}

		} catch (Exception E) {
			System.err
					.println("SurveyResult.java - getKeyBehaviourName - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	// added in by Jenty on 21st March on 6.13 PM
	/**
	 * Retrieves all departments under that particular survey.
	 */
	public Vector getSurveyDept(int surveyID) {

		Vector dept = new Vector();

		String sql2 = "SELECT DISTINCT Department.PKDepartment, Department.DepartmentName ";
		sql2 += "FROM tblAssignment INNER JOIN Department ON tblAssignment.FKTargetDepartment = ";
		sql2 += "Department.PKDepartment WHERE (tblAssignment.SurveyID = "
				+ surveyID + ") ";
		sql2 += "ORDER BY Department.DepartmentName ";

		/*
		 * String sql2 = "SELECT DISTINCT Department.PKDepartment,
		 * Department.DepartmentName "; sql2 += "FROM [User] INNER JOIN
		 * tblAssignment ON [User].PKUser = tblAssignment.TargetLoginID "; sql2
		 * += "INNER JOIN Department ON [User].FKDepartment =
		 * Department.PKDepartment "; sql2 += "WHERE tblAssignment.SurveyID = "
		 * + surveyID; sql2 += " ORDER BY Department.DepartmentName";
		 */

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql2);

			while (rs.next()) {
				String PKDept = rs.getString("PKDepartment");
				String deptName = rs.getString("DepartmentName");

				String[] info = { PKDept, deptName };

				dept.add(info);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSurveyDept - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return dept;

	}

	/**
	 * Retrieves all departments under that particular survey and division
	 * 
	 * @param surveyID
	 * @param division
	 * @return
	 * @author Su See
	 */
	public Vector getSurveyDept(int surveyID, int division) {

		Vector dept = new Vector();

		String sql2 = "SELECT DISTINCT Department.PKDepartment, Department.DepartmentName ";
		sql2 += "FROM tblAssignment INNER JOIN Department ON tblAssignment.FKTargetDepartment = ";
		sql2 += "Department.PKDepartment WHERE (tblAssignment.SurveyID = "
				+ surveyID + ") ";

		if (division > 0) {
			sql2 += "AND Department.FKDivision = " + division + " ";
		}
		sql2 += "ORDER BY Department.DepartmentName ";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql2);

			while (rs.next()) {
				String PKDept = rs.getString("PKDepartment");
				String deptName = rs.getString("DepartmentName");

				String[] info = { PKDept, deptName };

				dept.add(info);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSurveyDept - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return dept;

	}

	/**
	 * Retrieves all groups under that particular survey.
	 */
	public Vector getSurveyGroup(int surveyID) {

		Vector group = new Vector();

		String sql3 = "SELECT DISTINCT [Group].PKGroup, [Group].GroupName FROM [Group] INNER JOIN ";
		sql3 += "tblAssignment ON [Group].PKGroup = tblAssignment.FKTargetGroup WHERE ";
		sql3 += "(tblAssignment.SurveyID = " + surveyID
				+ ") ORDER BY [Group].GroupName";

		/*
		 * String sql3 = "SELECT DISTINCT [Group].PKGroup, [Group].GroupName
		 * FROM [User] INNER JOIN "; sql3 += "[Group] ON [User].Group_Section =
		 * [Group].PKGroup INNER JOIN tblAssignment ON "; sql3 += "[User].PKUser
		 * = tblAssignment.TargetLoginID WHERE tblAssignment.SurveyID = " +
		 * surveyID; sql3 += " ORDER BY [Group].GroupName";
		 */
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql3);

			while (rs.next()) {
				String PKGroup = rs.getString("PKGroup");
				String groupName = rs.getString("GroupName");

				String[] info = { PKGroup, groupName };

				group.add(info);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSurveyGroup - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return group;

	}

	/**
	 * Retrieves all divisions under that particular survey and organization
	 * 
	 * @param surveyID
	 * @param orgID
	 * @return Vector
	 * @author Su See
	 */
	public Vector getSurveyDiv(int surveyID, int orgID) {

		Vector div = new Vector();

		String sql2 = "SELECT DISTINCT Division.PKDivision, Division.DivisionName ";
		sql2 += "FROM tblAssignment INNER JOIN Division ON tblAssignment.FKTargetDivision = ";
		sql2 += "Division.PKDivision WHERE (tblAssignment.SurveyID = "
				+ surveyID + ") ";
		sql2 += "AND Division.FKOrganization = " + orgID + " ";
		sql2 += "ORDER BY Division.DivisionName ";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql2);

			while (rs.next()) {
				String PKDivision = rs.getString("PKDivision");
				String DivisionName = rs.getString("DivisionName");

				String[] info = { PKDivision, DivisionName };

				div.add(info);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSurveyDiv - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return div;

	}

	/**
	 * Retrieves all groups under that particular survey
	 * 
	 * @param surveyID
	 * @param deptID
	 * @return
	 * @author Su See
	 */
	public Vector getSurveyGroup(int surveyID, int deptID) {

		Vector group = new Vector();

		String sql3 = "SELECT DISTINCT [Group].PKGroup, [Group].GroupName FROM [Group] INNER JOIN ";
		sql3 += "tblAssignment ON [Group].PKGroup = tblAssignment.FKTargetGroup WHERE ";
		sql3 += "(tblAssignment.SurveyID = " + surveyID + ") ";

		if (deptID > 0) {
			sql3 += "AND [Group].FKDepartment = " + deptID + " ";
		}

		sql3 += "ORDER BY [Group].GroupName";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql3);

			while (rs.next()) {
				String PKGroup = rs.getString("PKGroup");
				String groupName = rs.getString("GroupName");

				String[] info = { PKGroup, groupName };

				group.add(info);
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSurveyGroup - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return group;
	}

	/**
	 * This is the method which calls all the calculation process in Calculation
	 * class. This is called from JSP once a rater finishs the questionnaire,
	 * include/exclude rater.
	 * 
	 * (Updated on 29 June 2010) Renamed method from Calculate to
	 * calculateAvgMean and the codes for calculation of Relaibility and GAP is
	 * moved to a new method calculateReliabilityAndGAP() To help faciliate in
	 * Calculating AvgMean of all assignments in the survey before calculating
	 * Reliability and GAP
	 * 
	 * @param int assignmentID - Assignment ID of the survey
	 * @param int include - 1 (Include Rater), 0 (Exclude Rater)
	 * 
	 * @author Sebastian
	 * @since v1.3.12.74 (29 June 2010)
	 */
	public void calculateAvgMean(int assignmentID, int include)
			throws SQLException, Exception {
		try {

			votblSurvey info = Info(assignmentID);

			if (info != null) {

				int surveyID = info.getSurveyID();
				String raterCode = info.getRaterCode();
				int targetID = info.getTargetLoginID();

				int group = 0;

				int surveyLevel = C.LevelOfSurvey(surveyID);

				if (raterCode.substring(0, 3).equals("SUP")) {
					group = 2;
				} else if (raterCode.substring(0, 3).equals("OTH")) {
					group = 3;
				} else if (raterCode.substring(0, 4).equals("SELF")) {
					group = 4;
				}
				// Add logic for calculation of Subordinates and Peers, added by
				// Desmond 21 Oct 09
				// 10 is assigned to weightedAverage
				else if (raterCode.substring(0, 3).equals("SUB")) {
					group = 5;
				} else if (raterCode.substring(0, 4).equals("PEER")) {
					group = 6;
				} else if (raterCode.substring(0, 3).equals("ADD")) {
					group = 7;
				} else if (raterCode.substring(0, 3).equals("DIR")) {
					group = 8;
				} else if (raterCode.substring(0, 3).equals("IDR")) {
					group = 9;
				}else if (raterCode.substring(0, 4).equals("BOSS")) {
					group = 11;
				}

			

				DeletetblAvgMean(surveyID, targetID, group);
				DeletetblAvgMean(surveyID, targetID, 1);

				// Calculate the competency result using averaging for each
				// rating task (CP, CPR, FP, FPR).
				// The group is specified here to minimise the processing.
				// The group is defined from the rater who completed the
				// questionnaire.
				// If rater is an OTH, then just calculate the avg mean for OTH
				// group.

				C.calculateAvgMean(surveyID, targetID, group, assignmentID);

				// ALL have to be recalculated everytime rater completed as it
				// will affect the ALL score.
				C.calculateAvgMean(surveyID, targetID, 1, assignmentID);
			
				// for
				// ALL

				if (surveyLevel == 1) {
					if (include == 0) {

						C.calculateRaterAvgMean(surveyID, assignmentID); // calculate
					}
				}
			}
		} catch (Exception SE) {
			System.err.println(SE.getMessage());
		}

	}

	/**
	 * Calculate the reliability and GAP of target/raters for the assignment To
	 * help faciliate in Calculating AvgMean of all assignments in the survey
	 * before calculating Reliability and GAP
	 * 
	 * @param int assignmentID - Assignment ID of the survey
	 * @param int include - 1 (Include Rater), 0 (Exclude Rater)
	 * @author Sebastian
	 * @since v1.3.12.74 (29 June 2010)
	 */
	public void calculateReliabilityAndGAP(int assignmentID, int include)
			throws SQLException, Exception {

		votblSurvey info = Info(assignmentID);

		if (info != null) {

			int surveyID = info.getSurveyID();
			String raterCode = info.getRaterCode();
			int targetID = info.getTargetLoginID();

			int reliabilityCheck = C.ReliabilityCheck(surveyID);
			int surveyLevel = C.LevelOfSurvey(surveyID);

			int group = 0;

			if (raterCode.substring(0, 3).equals("SUP")) {
				group = 2;
			} else if (raterCode.substring(0, 3).equals("OTH")) {
				group = 3;
			} else if (raterCode.substring(0, 4).equals("SELF")) {
				group = 4;
			}
			// Add logic for calculation of Subordinates and Peers, added by
			// Desmond 21 Oct 09
			else if (raterCode.substring(0, 3).equals("SUB")) {
				group = 5;
			} else if (raterCode.substring(0, 4).equals("PEER")) {
				group = 6;
			} else if (raterCode.substring(0, 3).equals("ADD")) {
				group = 7;
			}
			// average score for each competency for each rater by averaging the
			// KBs
			// TrimmedMean
			if (reliabilityCheck == 0) {
				DeletetblTrimmedMean(surveyID, targetID, 1);
				// Re-calculate trimmed mean of competency for ALL
				C.calculateTrimmedMean(surveyID, targetID, 1, assignmentID);

				if (surveyLevel == 0) { // competency level
					DeletetblTrimmedMean(surveyID, targetID, group);
					// For competency level, then must also re-calculate
					// score for that particular group
					// There is no need for KB Level to do this, because it
					// has already been calculated above
					// (C.calculateAvgMean(surveyID, targetID, group))

					C.calculateTrimmedMean(surveyID, targetID, group,
							assignmentID);
				}
			} else { // Average Mean
				// Perform the checking of reliability for raters.
				C.calculateReliability(surveyID, targetID, include,
						assignmentID);
			}

			DeletetblGap(surveyID, targetID);
			C.calculateGap(surveyID, targetID);

			C.IndividualLevelOfAgreement(surveyID, targetID, assignmentID);

			// There is no more need to calculate group LOA here as the
			// result can be directly retrieved from Individual LOA when
			// group report is generated
			// C.GroupLevelOfAgreement(surveyID, groupSection);
		}
	}

	/**
	 * Added in order to change the rater's input by group screen. Previous
	 * implementation has problem if the survey doesn't include NA into
	 * calculation
	 */
	public Vector getRatingTask(int surveyID) {

		Vector v = new Vector();

		String query = "SELECT tblSurveyRating.RatingTaskID, tblRatingTask.RatingCode, ";
		query += "tblSurveyRating.RatingTaskName FROM tblSurveyRating ";
		query += "INNER JOIN tblRatingTask ON tblSurveyRating.RatingTaskID = tblRatingTask.RatingTaskID ";
		query += "WHERE SurveyID = " + surveyID
				+ " ORDER BY tblSurveyRating.RatingTaskID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {

				v.add(new String[] { rs.getString("RatingTaskID"),
						rs.getString("RatingCode"),
						rs.getString("RatingTaskName") });
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getRatingTask - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Get the Key Behaviour List for the particular Survey.
	 */
	public Vector CompList(int surveyID) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);

		Vector v = new Vector();

		if (surveyLevel == 0) {
			query = query
					+ "SELECT Competency.PKCompetency, Competency.CompetencyName FROM Competency ";
			query = query + "INNER JOIN tblSurveyCompetency ON ";
			query = query
					+ "Competency.PKCompetency = tblSurveyCompetency.CompetencyID WHERE ";
			query = query + "tblSurveyCompetency.SurveyID = " + surveyID
					+ " ORDER BY Competency.PKCompetency";
		} else {
			query = query
					+ "SELECT distinct tblSurveyBehaviour.CompetencyID, Competency.CompetencyName FROM Competency ";
			query = query + "INNER JOIN tblSurveyBehaviour ON ";
			query = query
					+ "Competency.PKCompetency = tblSurveyBehaviour.CompetencyID INNER JOIN ";
			query = query
					+ "KeyBehaviour ON tblSurveyBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID
					+ " ORDER BY ";
			query = query + "tblSurveyBehaviour.CompetencyID";

		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next())
				v.add(new String[] { rs.getString(1), rs.getString(2) });

		} catch (Exception E) {
			System.err.println("SurveyResult.java - CompList - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;

	}

	/**
	 * Get the Key Behaviour List for the particular Survey.
	 */
	public Vector KBList(int surveyID, int FKComp) {
		String query = "";

		Vector v = new Vector();

		query = query
				+ "SELECT distinct tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour FROM Competency ";
		query = query + "INNER JOIN tblSurveyBehaviour ON ";
		query = query
				+ "Competency.PKCompetency = tblSurveyBehaviour.CompetencyID INNER JOIN ";
		query = query
				+ "KeyBehaviour ON tblSurveyBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
		query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID
				+ " and Competency.PKCompetency = " + FKComp + " ORDER BY ";
		query = query + "tblSurveyBehaviour.KeyBehaviourID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next())
				v.add(new String[] { rs.getString(1), rs.getString(2) });

		} catch (Exception E) {
			System.err.println("SurveyResult.java - KBList - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}

		return v;
	}

	/**
	 * Retrieves the Average Mean from the tblAvgMean based on each Competency.
	 * Group: 1 = ALL Excludes SELF 2 = SUP 3 = OTHER 4 = SELF
	 */
	public double getAvgMean(int surveyID, int targetID, int group, int fkComp,
			int rtid) {

		double result = 0;

		String query = "select RatingTaskID, CompetencyID, cast(AVG(AvgMean) as numeric(38,2)) as Result from tblAvgMean ";
		query = query + "where SurveyID = " + surveyID;
		query = query + " AND TargetLoginID = " + targetID
				+ " and CompetencyID = " + fkComp;
		query = query + " and Type = " + group + " and RatingTaskID = " + rtid;
		query = query + " group by CompetencyID, RatingTaskID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				result = rs.getDouble("Result");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getAvgMean - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return result;

	}

	/**
	 * This method is to trim the result of group ALL to be displayed in Process
	 * Result. It takes the avg mean of all raters under each KB, then trim.
	 * This method is to be used only under Key Behaviour Level Analysis.
	 */
	public double TrimmedMeanAll(int surveyID, int targetID, int fkComp,
			int rtid) {
		String query = "";

		double result = 0;
		query = query
				+ "Select TrimmedMean as Result from tblTrimmedMean where SurveyID = "
				+ surveyID;
		query = query + " and TargetLoginID = " + targetID
				+ " and Type = 1 and CompetencyID = " + fkComp;
		query += " and RatingTaskID = " + rtid;
		query = query + " order by RatingTaskID, CompetencyID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				result = rs.getDouble("Result");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TrimmedMeanAll - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return result;

	}

	/**
	 * Get the TrimmedMean Result of all raters based on the group. This is to
	 * be used if the ReliabilityCheck chosen is Trimmed Mean. For KB Level
	 * analysis, this method returns the average mean of results from all raters
	 * under each KB. TrimmedMean has to be on Competency, this will be done in
	 * TrimmedMeanAll method. Group: 1 = ALL Excludes SELF 2 = SUP 3 = OTHER 4 =
	 * SELF
	 */
	public double getTargetTrimmedMean(int surveyID, int targetID, int group,
			int FKComp, int FKKB, int rtid) {

		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);

		double result = 0;
		if (surveyLevel == 0) {
			query += "SELECT CAST(tblTrimmedMean.TrimmedMean AS numeric(38, 2)) AS Result FROM tblTrimmedMean ";
			query += "INNER JOIN tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query += "INNER JOIN Competency ON tblTrimmedMean.CompetencyID = Competency.PKCompetency ";
			query += "WHERE tblTrimmedMean.Type = " + group
					+ " AND tblTrimmedMean.SurveyID = " + surveyID;
			query += " AND tblTrimmedMean.TargetLoginID = " + targetID
					+ " AND ";
			query += "tblTrimmedMean.RatingTaskID = " + rtid;
			query += " AND tblTrimmedMean.CompetencyID = " + FKComp;
		} else {

			query = "SELECT CAST(tblAvgMean.AvgMean AS numeric(38, 2)) AS Result FROM KeyBehaviour INNER JOIN ";
			query += "tblAvgMean INNER JOIN Competency ON tblAvgMean.CompetencyID = Competency.PKCompetency ON ";
			query += "KeyBehaviour.PKKeyBehaviour = tblAvgMean.KeyBehaviourID WHERE ";
			query += "tblAvgMean.SurveyID = " + surveyID
					+ " AND tblAvgMean.TargetLoginID = " + targetID;
			query += " AND tblAvgMean.Type = " + group
					+ " AND tblAvgMean.RatingTaskID = " + rtid;
			query += " AND KeyBehaviour.PKKeyBehaviour = " + FKKB;
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				result = rs.getDouble("Result");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getTargetTrimmedMean - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return result;

	}

	/**
	 * Retrieve the Target result of each rater to be displayed in Process
	 * Result. The data retrieved is a raw data input by rater. Tables:
	 * tblResultCompetency and tblResultBehaviour.
	 */
	public double TargetResult(int surveyID, int targetID, String raterCode,
			int FKComp, int FKKB, int rtid) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);

		double result = 0;
		if (surveyLevel == 0) {

			query += "SELECT CAST(tblResultCompetency.Result AS float) AS Result FROM tblAssignment INNER JOIN ";
			query += "tblResultCompetency ON tblAssignment.AssignmentID = tblResultCompetency.AssignmentID ";
			query += "INNER JOIN Competency ON tblResultCompetency.CompetencyID = Competency.PKCompetency ";
			query += "WHERE tblAssignment.TargetLoginID = " + targetID;
			query += " AND tblAssignment.SurveyID = " + surveyID + " AND ";
			query += "tblAssignment.RaterCode LIKE '" + raterCode + "%' AND ";
			query += "tblResultCompetency.CompetencyID = " + FKComp;
			query += "and tblAssignment.RaterStatus <> 3";
			query += "and tblResultCompetency.RatingTaskID = " + rtid;
		} else {
			query += "SELECT CAST(tblResultBehaviour.Result AS float) AS Result FROM tblAssignment INNER JOIN ";
			query += "tblResultBehaviour ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID ";
			query += "INNER JOIN KeyBehaviour ON ";
			query += "tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour INNER JOIN ";
			query += "Competency ON KeyBehaviour.FKCompetency = Competency.PKCompetency ";
			query += "WHERE tblAssignment.TargetLoginID = " + targetID;
			query += " AND tblAssignment.SurveyID = " + surveyID;
			query += " AND tblAssignment.RaterCode LIKE '" + raterCode
					+ "%' AND ";
			query += "tblAssignment.RaterStatus <> 3 AND ";
			query += "tblResultBehaviour.KeyBehaviourID = " + FKKB;
			query += " AND tblResultBehaviour.RatingTaskID = " + rtid;
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				result = rs.getDouble("Result");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - TargetResult - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return result;

	}

	/**
	 * Retrieve the target average mean from tblAvgMean based on the group. This
	 * is used if the ReliabilityIndex chosen is Average Mean.
	 */
	public double getTargetAvgMean(int surveyID, int targetID, int group,
			int fkComp, int FKKB, int rtid) {
		String query = "";
		int surveyLevel = C.LevelOfSurvey(surveyID);
		double result = 0;

		if (surveyLevel == 0) {

			query = "SELECT ROUND(tblAvgMean.AvgMean,2) AS Result ";
			query = query + "FROM tblAvgMean INNER JOIN tblRatingTask ON ";
			query = query
					+ "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query = query
					+ "Competency ON tblAvgMean.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblAvgMean.Type = " + group
					+ " AND tblAvgMean.SurveyID = " + surveyID;
			query = query + " AND tblAvgMean.TargetLoginID = " + targetID;
			query += " and tblAvgMean.RatingTaskID = " + rtid;
			query += " AND Competency.PKCompetency = " + fkComp;

		} else {
			query = "SELECT tblAvgMean.RatingTaskID, Competency.PKCompetency, ";
			query = query
					+ "Competency.CompetencyName, KeyBehaviour.PKKeyBehaviour, KeyBehaviour.KeyBehaviour, ";
			query = query
					+ "round(tblAvgMean.AvgMean, 2) AS Result FROM tblAvgMean INNER JOIN tblRatingTask ON ";
			query = query
					+ "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query = query
					+ "Competency ON tblAvgMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query = query
					+ "KeyBehaviour ON tblAvgMean.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query + "WHERE tblAvgMean.Type = " + group
					+ " AND tblAvgMean.SurveyID = " + surveyID;
			query = query + " AND tblAvgMean.TargetLoginID = " + targetID;
			query += " and tblAvgMean.RatingTaskID = " + rtid;
			query += " and KeyBehaviour.PKKeyBehaviour = " + FKKB;
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				// Added rounding by Ha 07/07/08 sometimes round in SQL does not
				// work properly
				result = rs.getDouble("Result");
				BigDecimal bd = new BigDecimal(result);
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				result = bd.doubleValue();
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getTargetTrimmedMean - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return result;
	}

	/**
	 * getRaterID
	 * 
	 * @param AssignmentID
	 * @return integer ID of the rater
	 * @author Ha by 16/06/08
	 * 
	 * 
	 */

	public int getRaterID(int AssignmentID) {
		int raterID = 0;
		String query = "SELECT * FROM tblAssignment WHERE AssignmentID = "
				+ AssignmentID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				raterID = rs.getInt("RaterLoginID");

		} catch (Exception E) {
			System.err.println("SurveyResult.java - getRaterID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return raterID;
	}

	/**
	 * updateCalculationStatus
	 * 
	 * @param iTargetID
	 *            , iSurveyID, iStatus
	 * @return void update Calculation Status to iStatus
	 * @author Ha by 25/06/08 throw Exception: SQLException, Exception
	 * */

	public void updateCalculationStatus(int iTargetID, int iSurveyID,
			int iStatus) {

		String query = "UPDATE tblAssignment SET CalculationStatus =  "
				+ iStatus + " WHERE TargetLoginID = " + iTargetID;
		query += " AND SurveyID = " + iSurveyID;
		query += " AND RaterStatus in (1,2,4)";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			st.execute(query);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - updateCalculationStatus - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
	}

	/**
	 * updateStatusAssignment
	 * 
	 * @param iAssignment
	 *            , iAdminStatus
	 * @return void update AdminStatus to iAdminStatus given the AssignmentID
	 * @author Ha by 24/06/08
	 * 
	 * */

	public void updateStatusAssignment(int iAssignment, int iAdminStatus) {

		String query = "UPDATE tblAssignment SET AdminCalcStatus =  "
				+ iAdminStatus;
		query += " WHERE AssignmentID = " + iAssignment;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			st.execute(query);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - updateStatusAssignment - "
					+ E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
	}

	public int getSurveyID(int iAssignment) {
		int iSurveyID = 0;
		String query = "SELECT DISTINCT SurveyID FROM tblAssignment  WHERE AssignmentID = "
				+ iAssignment;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			if (rs.next())
				iSurveyID = rs.getInt("SurveyID");
		} catch (Exception E) {
			System.err.println("SurveyResult.java - getSurveyID - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		return iSurveyID;
	}

	/**
	 * getAssignment
	 * 
	 * @param iSurveyID
	 *            , iTargetID
	 * @return Vector list of assignment
	 * @author Ha by 24/06/08
	 * 
	 * */

	public Vector getAssignment(int iSurveyID, int iTargetID, int iCompleted) {
		Vector iAssignment = new Vector();
		String query = "SELECT * FROM tblAssignment  WHERE TargetLoginID = "
				+ iTargetID;
		query += " AND SurveyID = " + iSurveyID;
		if (iCompleted != 0)
			query += " AND RaterStatus in (1,2,4)";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				int iAssignmentID = rs.getInt("AssignmentID");
				iAssignment.add(new Integer(iAssignmentID));
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - getAssignment - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return iAssignment;
	}

	public static void main(String[] args) {
		SurveyResult SR = new SurveyResult();

		SR.CalculateStatus(6607, 489, 1, 66, 509, 1);
		SR.isAllRated(31, 0, 11);

		// Vector v = SR.getRatingTask(438);
		// int assignmentID = 12204;
		// int [] assignmentID = {12485, 10629, 11545, 12745, 11602, 12218,
		// 10377,
		// 11705, 12909, 13188, 13135, 13555, 13149, 12270, 12062,
		// 11302};

		// int [] assignmentID = {12382, 12300, 12461, 12476, 12196, 11399,
		// 11905, 12661, 12662,
		// 10514, 11873, 10203, 10217, 12626, 10356, 10421};
		/*
		 * int [] assignmentID = {6832};
		 * 
		 * int include = 0; for(int i=0; i<assignmentID.length; i++) try {
		 * SR.Calculate(assignmentID[i], include); System.out.println("FIXED + "
		 * + assignmentID[i]); } catch (SQLException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); System.err.println(e); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		// try {
		// SR.Calculate(8812, 0);
		// // SR.IncludeExcludeRater(6842, 2, 1);
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// System.out.println(e.getMessage());
		// e.printStackTrace();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// System.out.println(e.getMessage());
		// }
		// SR.CalculateStatus(101,468,0,0,0,0);
		
	}
}