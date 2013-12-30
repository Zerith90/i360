package CP_Classes;

// Change by Santoso (2008-10-27)
// organize imports, remove unnecessary imports
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.text.*;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voCluster;
import CP_Classes.vo.voCompetency;
import CP_Classes.vo.voImportance;
import CP_Classes.vo.voKeyBehaviour;
import CP_Classes.vo.voRatingResult;
import CP_Classes.vo.voRatingTask;
import CP_Classes.vo.voUser;
import CP_Classes.vo.votblSurveyRating;

import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.XTableChart;

/**
 * This class implements all the operations for Group Report in Excel. It
 * implements OpenOffice API.
 * 
 * Change Log ==========
 * 
 * Date By Method(s) Change(s)
 * ==================================================
 * ============================
 * ============================================================== 13/10/11 Gwen
 * Oh - 1) Added selectedUsers array 2) Added setter method, setSelectedUsers()
 * 17/10/11 Gwen Oh getCPCPR(),totalRater(), Check if request is for subgroup or
 * group report printBlindSpotAnalysis(), and change the SQL statements
 * accordingly. LevelOfAgreement(), AvgLevelOfAgreement(),
 * getTotRatersCompleted(), getTotalAllTargetResults(), getAllResults(),
 * totalTargetBasedScore(), printTargetRank(), getTotalTarget(), getKBCPCPR(),
 * getKBCPSelf()
 * 
 * 14/05/12 Albert Constructor Added new data
 * members:groupSectionList,divisionIDList, departmentIDList
 * 
 * 29/05/12 Albert InitializeSurvey(int,int,Vector<Integer>,Vector<Integer>)
 * Create override methods to accommodate the changes made
 * generateReport(int,int,Vector<Integer>,<Integer>,int,String,int)
 * Report(int,int,Vector<Integer>,Vector<Integer>,int,String,int)
 * getGroupName(Vector<Integer>),getDeptName(Vector<Integer>)
 * 
 * 05/07/2012 Liu Taichen InsertRatingScale() created this method to insert the
 * explanation of rating scales into the group report
 * 
 * 17/07/2012 Liu Taichen InsertRatingScaleList() print the rating scales in the
 * individual report as a list
 * 
 * 17/07/2012 Liu Taichen Report(int, int, int, String, int, use
 * InsertRatingScaleList() instead of InsertRatingScale() String, String,
 * String, String, int, String) 22/05/2013 Sherman Tan getCPNonSelf(int,String)
 * query the DB with the right rater code (See rater's Code in the i360
 * Functional specs under "tblAvgMean" table. Commented off some of the methods
 * that are already not in use.(eg. functions with "Toyota" in it)
 * 
 * 23/05/2013 Sherman Tan generateChart(int,Vector,int,int,int,int added an
 * additional parameter reqWeightedAverage to consider the average
 * int,int,int,int,int,int,int) all subgroups instead of the average of all
 * raters
 */

public class GroupReportSPF

{
	private Calculation				C;
	private Questionnaire			Q;
	private OpenOffice				OO;
	private RatingScale				rscale;
	private GlobalFunc				G;
	private Setting					ST;										// Declaration
																				// of
																				// new
																				// object
																				// of
																				// class
																				// Setting.
	private User_Jenty				U;											// Declaration
																				// of
																				// new
																				// object
																				// of
																				// class
																				// User.
	private EventViewer				EV;										// Declaration
																				// of
																				// new
																				// object
																				// of
																				// class
																				// EventViewer.
	private AssignTarget_Rater		assign;

	private XMultiComponentFactory	xRemoteServiceManager	= null;
	private XComponent				xDoc					= null;
	private XSpreadsheet			xSpreadsheet			= null;
	private XSpreadsheet			xSpreadsheet2			= null;
//	private XSpreadsheet			xSpreadsheet			= null;
	private XSpreadsheet			xSpreadsheet4			= null;
	private XSpreadsheet			xSpreadsheet5			= null;
	private XSpreadsheet			xSpreadsheet6			= null;
	private XSpreadsheet			xSpreadsheet7			= null;
	
	private String					storeURL;

	private final int				BGCOLOR					= 12632256;
	private final int				ROWHEIGHT				= 560;

	// ---global variable---
	private String					surveyInfo[];
	private int						surveyID;
	private int						groupSection;
	private int						deptID;									// department
																				// ID
	private int						divID;										// division
																				// ID
	private String[]				selectedUsers			= null;			// Gwen
																				// Oh
																				// -
																				// 13/10/2011:
																				// Stores
																				// the
																				// selected
																				// users
																				// for
																				// subgroup
																				// report

	private int						arrN[];									// To
																				// print
																				// N
																				// (No
																				// of
																				// Raters)
																				// for
																				// Simplified
																				// report

	private int						iReportType;								// 1=Simplified
																				// Report
																				// "No Competencies charts",
																				// 2=Standard
																				// Report
	int								startColumn				= 0;
	int								endColumn				= 0;
	int								row						= 0;
	int								column					= 0;
	private int						groupRankingTableRow	= 0;

	private boolean					hasCP					= false;			// true
																				// if
																				// CP
																				// is
																				// chosen
	private boolean					hasCPR					= false;			// true
																				// if
																				// CPR
																				// is
																				// chosen
	private boolean					hasFPR					= false;			// true
																				// if
																				// FPR
																				// is
																				// chosen
	private boolean					reqWeightedAverage		= false;

	private Vector					vGap;										// this
																				// is
																				// to
																				// store
																				// the
																				// gap
																				// of
																				// each
																				// competency
																				// so
																				// does
																				// not
																				// need
																				// to
																				// reopen
																				// another
																				// resultset
	private Vector					vCompDetails;
	private Vector					vRatingTask;
	private Vector					vCP;
	private Vector					vCPR;
	private Vector					vCompID;
	private Vector					vCPValues;									// add
																				// to
																				// store
																				// CP
																				// values
																				// of
																				// each
																				// competency
																				// for
																				// sorting
																				// ,
																				// Mark
																				// Oei
																				// 22
																				// April
																				// 2010

	private HashMap					CPCPRMap;
	private HashMap					CompIDGapMap;
	private final int				BGCOLORCLUSTER			= 16774400;
	private int						iPastSurveyID;								// For
	private Vector					comparisonHashMapVector;																			// Toyota
	private Vector					comparisonGapTableVector;
	private Vector					comparisonCPTableVector;
	private Vector					comparisonClusterRankTableVector;	
	private Vector					 totalScores = new Vector();																			// combined
																				// report

	// Denise 16/12/2009 exGR = 0. Include Group Ranking. Otherwise, exclude
	// Group Ranking
	private int						exGR;

	DecimalFormat					df						= new DecimalFormat(
																	"#.00");

	/**
	 * Creates a new intance of GroupReport object.
	 */
	public GroupReportSPF() {

		ST = new Setting();
		C = new Calculation();
		Q = new Questionnaire();
		U = new User_Jenty();
		EV = new EventViewer();
		OO = new OpenOffice();
		G = new GlobalFunc();
		rscale = new RatingScale();
		assign = new AssignTarget_Rater();

		vGap = new Vector();
		CPCPRMap = new HashMap();
		CompIDGapMap = new HashMap();
		vCompDetails = new Vector();
		vRatingTask = new Vector();
		vCP = new Vector();
		vCPR = new Vector();
		vCPValues = new Vector(); // add to store CP values of each competency
									// for sorting , Mark Oei 22 April 2010

		hasCP = false;
		hasCPR = false;
		hasFPR = false;

		startColumn = 0;
		endColumn = 12;
	}

	/*************************** START - INITIALISATION ***************************************/

	/**
	 * Initialize all the processes dealing with Excel Application.
	 * 
	 * @param savedFileName
	 * @throws IOException
	 * @throws Exception
	 */
	public void InitializeExcel(String savedFileName) throws IOException,
			Exception {
		System.out.println("2. Excel Initialisation Starts");
		storeURL = "file:///" + ST.getOOReportPath() + savedFileName;
		String templateURL = "file:///" + ST.getOOReportTemplatePath();
		// Denise 16/12/2009
		// Denise 06/01/2009 combine the groupReport template and group report
		// template_noRank so just need to use one template
		// exGR = 0. Use the Group Report template. Otherwise, use the Group
		// Report Template_NoRank
		// if (exGR ==0)
		templateURL = templateURL + "Group Report Template_SPF.xls";
		// else templateURL = templateURL + "Group Report Template_NoRank.xls";
		
		xRemoteServiceManager = OO
				.getRemoteServiceManager("uno:socket,host=localhost,port=2002;urp;StarOffice.ServiceManager");
		xDoc = OO.openDoc(xRemoteServiceManager,templateURL);

		// save as the template into a new file first. This is to avoid the
		// template being used.
		OO.storeDocComponent(xRemoteServiceManager,xDoc,storeURL);
		OO.closeDoc(xDoc);

		// open up the saved file and modify from there
		xDoc = OO.openDoc(xRemoteServiceManager,storeURL);
		xSpreadsheet = OO.getSheet(xDoc,"Group Report");
		xSpreadsheet2 = OO.getSheet(xDoc,"Sheet2");
		//xSpreadsheet = OO.getSheet(xDoc,"Sheet3");
		xSpreadsheet4 = OO.getSheet(xDoc,"Group Ranking Table");
		xSpreadsheet5 = OO.getSheet(xDoc,"Cluster Ranking Table");
		xSpreadsheet6 = OO.getSheet(xDoc,"Raw Data");
		xSpreadsheet7 = OO.getSheet(xDoc,"Group Raw Data");
		comparisonHashMapVector = new Vector();
		comparisonGapTableVector = new Vector();
		comparisonCPTableVector = new Vector();
		comparisonClusterRankTableVector = new Vector();
		
		System.out.println("END OF INTIALISATION");
	}

	/*************************** START - SURVEY INITIALISATION ********************************/
	/**
	 * Initializes all processes dealing with Survey
	 * 
	 * @param int SurveyID
	 * @param int DeptID PKDepartment
	 * @param int DivID PKDivision
	 */
	public void InitializeSurvey(int surveyID, int groupSection, int deptID,
			int divID) throws SQLException, IOException {
		System.out.println("1. Survey Initialisation Starts");

		this.surveyID = surveyID;
		this.groupSection = groupSection;
		this.deptID = deptID;
		this.divID = divID;

		surveyInfo = new String[7];
		surveyInfo = SurveyInfo();

		// Initialise array for arrN
		arrN = new int[73 * 10 * 6]; // size = max 73 competencies * max 10 KBs
										// * max 6 Rating

	}

	public void InitializeSurvey(int surveyID, Vector<Integer> groupSection,
			Vector<Integer> deptID, int divID) throws SQLException, IOException {
		System.out.println("1. Survey Initialisation Starts");

		this.surveyID = surveyID;
		this.divID = divID;
		if (groupSection != null)
			this.groupSection = groupSection.elementAt(0);
		if (deptID != null)
			this.deptID = deptID.elementAt(0);
		surveyInfo = new String[7];
		surveyInfo = SurveyInfo();

		// Initialise array for arrN
		arrN = new int[73 * 10 * 6]; // size = max 73 competencies * max 10 KBs
										// * max 6 Rating

	}

	public String[] SurveyInfo() throws SQLException {
		String[] info = new String[7];

		String query = "SELECT tblSurvey.LevelOfSurvey, tblJobPosition.JobPosition, tblSurvey.AnalysisDate, ";
		query = query
				+ "tblOrganization.NameSequence, tblSurvey.SurveyName, tblOrganization.OrganizationName, tblOrganization.OrganizationLogo FROM tblSurvey INNER JOIN ";
		query = query
				+ "tblJobPosition ON tblSurvey.JobPositionID = tblJobPosition.JobPositionID INNER JOIN ";
		query = query
				+ "tblOrganization ON tblSurvey.FKOrganization = tblOrganization.PKOrganization ";
		query = query + "WHERE tblSurvey.SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				for (int i = 0; i < 7; i++)
					info[i] = rs.getString(i + 1);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - SurveyInfo - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return info;

	}

	/**
	 * Retrieves the group name based on groupID
	 * 
	 * @return String GroupName
	 */
	public String getGroupName() throws SQLException {
		String info = "";

		String query = "SELECT * from [Group] where PKGroup = " + groupSection;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				info = rs.getString("GroupName");

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getGroupName - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return info;
	}

	/**
	 * Retrieves the group name based on groupID
	 * 
	 * @return String GroupName
	 */
	public String getGroupName(Vector<Integer> groupSection)
			throws SQLException {
		String info = "";
		String query = "SELECT * from [Group] where PKGroup = "
				+ groupSection.elementAt(0);
		for (int i = 1; i < groupSection.size(); i++) {
			query += " OR PKGroup = " + groupSection.elementAt(i);
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				info = rs.getString("GroupName");
			}
			while (rs.next()) {
				if (!(info.startsWith(rs.getString("GroupName")))) {
					info = "All";
					break;
				}
			}

		} catch (Exception ex) {
			System.out
					.println("GroupReport.java - getGroupName(Vector<Integer>) - "
							+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return info;
	}

	/**
	 * Retrieves the department name based on deptID
	 * 
	 * @return String DepartmentName
	 */
	public String getDeptName() throws SQLException {
		String info = "";

		String query = "SELECT * from Department where PKDepartment = "
				+ deptID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				info = rs.getString("DepartmentName");

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getDeptName - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return info;
	}

	public String getDeptName(Vector<Integer> deptID) throws SQLException {
		String info = "";
		String query = "SELECT * from Department where PKDepartment = "
				+ deptID.elementAt(0);

		for (int i = 1; i < deptID.size(); i++) {
			query += " OR PKDepartment = " + deptID.elementAt(i);
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				info = rs.getString("DepartmentName");
			while (rs.next()) {
				if (!(info.startsWith(rs.getString("DepartmentName")))) {
					info = "All";
					break;
				}
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getDeptName - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return info;
	}

	/**
	 * Retrieves the division name based on divID
	 * 
	 * @return String DivisionName
	 */
	public String getDivName() throws SQLException {
		String info = "";

		String query = "SELECT * from Division where PKDivision = " + divID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				info = rs.getString("DivisionName");

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getDivName - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return info;
	}

	/**
	 * Retrieves all the job level based by on the survey.
	 */
	public String getJobLevel(int FKSurvey) throws SQLException, IOException,
			Exception {
		String sJobLevel = "";

		String query = "SELECT DISTINCT tblJobPosition.JobLevelName FROM tblSurvey INNER JOIN ";
		query += "tblJobPosition ON tblSurvey.JobPositionID = tblJobPosition.JobPositionID ";
		query += "WHERE (tblSurvey.SurveyID = " + FKSurvey + ")";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				sJobLevel = rs.getString("JobLevelName");

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getJobLevel - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return sJobLevel;
	}
	/****************************** END - SURVEY INITIALISATION ***************************/

	/************************ START - CP/CPR ****************************************/
	/**
	 * print CP and CPR
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 * @see generateReport()
	 */
	public void printCPvsCPR() throws SQLException, IOException, Exception {
		System.out.println("4. Generating CP Versus CPR");

		int[] address = OO.findString(xSpreadsheet,"<CP versus CPR Graph>");

		column = address[0];
		row = address[1];

		// OO.findAndReplace(xSpreadsheet, "<CP versus CPR Graph>", "");
		// Denise
		OO.deleteRows(xSpreadsheet,0,10,row,row + 1,1,0);

		// check if CP exists
		if (hasCP) {
			vCP = getCPCPR("CP");
		}
System.out.println("CP: "  + vCP.size() + hasCP);
		// check if either CPR or FPR exists.
		if (hasCPR) {

			vCPR = getCPCPR("CPR");
		} else if (hasFPR) {

			vCPR = getCPCPR("FPR");
		}

		drawLineChart();

	}

	/**
	 * get rating task which match CP, CPR or FPR.
	 * 
	 * @return vector of voSurveyRating
	 * @throws SQLException
	 * @see generateReport()
	 */
	public Vector getRatingTask() throws SQLException {
		Vector v = new Vector();

		String query = "";

		query = query
				+ "SELECT tblSurveyRating.RatingTaskID, tblRatingTask.RatingCode, ";
		query = query
				+ "tblSurveyRating.RatingTaskName FROM tblSurveyRating INNER JOIN ";
		query = query
				+ "tblRatingTask ON tblSurveyRating.RatingTaskID = tblRatingTask.RatingTaskID ";
		query = query + "WHERE tblSurveyRating.SurveyID = " + surveyID;
		query = query + " and tblRatingTask.RatingCode in('CP', 'CPR', 'FPR')";
		query = query + " ORDER BY tblSurveyRating.RatingTaskID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				voRatingTask vo = new voRatingTask();
				vo.setRatingTaskID(rs.getInt("RatingTaskID"));
				vo.setRatingCode(rs.getString("RatingCode"));
				vo.setRatingTaskName(rs.getString("RatingTaskName"));
				v.add(vo);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getRatingTask - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/**
	 * check whether hasCP, hasCPR, hasFPR exist or not.
	 * 
	 * @see generateReport()
	 */
	public void checkCPCPR() {

		for (int i = 0; i < vRatingTask.size(); i++) {
			voRatingTask vo = (voRatingTask) vRatingTask.elementAt(i);
			String RTCode = vo.getRatingCode();

			if (RTCode.equals("CP"))
				hasCP = true;

			if (RTCode.equals("CPR"))
				hasCPR = true;

			if (RTCode.equals("FPR"))
				hasFPR = true;
		}

	}

	/**
	 * Retrieves the results under that particular rating code
	 * 
	 * @param String
	 *            RTCode Rating Task Code
	 * 
	 * @return Vector CPCPR
	 * @see printCPvsCPR()
	 */
	public Vector getCPCPR(String RTCode) throws SQLException {
		String query = "";

		int reliabilityCheck = C.ReliabilityCheck(surveyID);
		int average = 1;
		if (reqWeightedAverage = true) {
			average = 10;
		} else {
			average = 1;
		}
		if (reliabilityCheck == 0) // trimmed mean
		{
			query = query
					+ "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
			query += "ROUND(AVG(tblTrimmedMean.TrimmedMean) ,2) AS Result FROM ";
			query += "tblTrimmedMean INNER JOIN Competency ON ";
			query += "tblTrimmedMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query += "tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query += "[User] ON [User].PKUser = tblTrimmedMean.TargetLoginID ";
			query += "WHERE tblTrimmedMean.SurveyID = " + surveyID;
			query += " AND tblTrimmedMean.Type = 1 AND tblRatingTask.RatingCode = '"
					+ RTCode + "' AND ";
			// Gwen Oh - 13/10/2011: Added DISTINCT keyword to the SELECT
			// statement
			query += "tblTrimmedMean.TargetLoginID IN (SELECT DISTINCT TargetLoginID FROM tblAssignment INNER JOIN ";
			query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
			query += "WHERE SurveyID = " + surveyID
					+ " AND RaterCode <> 'SELF' AND ";
			query += "RaterStatus IN (1, 2, 4) ";
			/*
			 * Change(s) : Check if the report to be generated is a subgroup
			 * report Reason(s) : To cater to subgroup report where only certain
			 * users are selected Updated By: Gwen Oh Updated On: 13 Oct 2011
			 */
			if (selectedUsers != null) {
				System.out.println(" SELECTED USER");
				query += "AND (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "TargetLoginID = " + selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += "))";
			} else {
				System.out.println(" SELECTED USER NOT");
				if (divID != 0)
					query = query + " AND tblAssignment.FKTargetDivision = "
							+ divID;

				if (deptID != 0)
					query = query + " AND tblAssignment.FKTargetDepartment = "
							+ deptID;

				if (groupSection != 0)
					query = query + " AND tblAssignment.FKTargetGroup = "
							+ groupSection;

				query += ") ";
			}
			// Added by Ha 21/06/08 GROUP BY SurveyID
			query += " GROUP BY tblTrimmedMean.SurveyID, Competency.PKCompetency, Competency.CompetencyName ";
			query += "ORDER BY Competency.CompetencyName";

		} else {
			query = "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
			query += "ROUND(AVG(tblAvgMean.AvgMean),2) AS Result FROM ";
			query += "tblAvgMean INNER JOIN Competency ON ";
			query += "tblAvgMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query += "tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query += "[User] ON [User].PKUser = tblAvgMean.TargetLoginID ";
			query += "WHERE tblAvgMean.SurveyID = " + surveyID;
			query += " AND tblAvgMean.Type = " + average
					+ " AND tblRatingTask.RatingCode = '" + RTCode + "' AND ";

			if (selectedUsers != null) {
				
				query += "tblAvgMean.TargetLoginID IN (SELECT DISTINCT TargetLoginID FROM tblAssignment WHERE (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "TargetLoginID = " + selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += ") AND RaterCode <> 'SELF' AND RaterStatus IN (1, 2, 4))";
			} else {
				
				// Gwen Oh - 13/10/2011: Added DISTINCT keyword to the SELECT
				// statement
				query += "tblAvgMean.TargetLoginID IN (SELECT DISTINCT TargetLoginID FROM tblAssignment INNER JOIN ";
				query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
				query += "WHERE SurveyID = " + surveyID
						+ " AND RaterCode <> 'SELF' AND ";
				query += "RaterStatus IN (1, 2, 4) ";

				if (divID != 0)
					query = query + " AND tblAssignment.FKTargetDivision = "
							+ divID;

				if (deptID != 0)
					query = query + " AND tblAssignment.FKTargetDepartment = "
							+ deptID;

				if (groupSection != 0)
					query = query + " AND tblAssignment.FKTargetGroup = "
							+ groupSection;

				query += ") ";
			}
			// Added by Ha 21/06/08 GROUP BY SurveyID
			query += " GROUP BY tblAvgMean.SurveyID, Competency.PKCompetency, Competency.CompetencyName ";
			query += "ORDER BY Competency.CompetencyName";

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
				voRatingResult vo = new voRatingResult();
				vo.setCompetencyID(rs.getInt("CompetencyID"));
				vo.setCompetencyName(rs.getString("CompetencyName"));
				vo.setResult(Math.round(rs.getDouble("Result")*100)/100.00);
		
				v.add(vo);

			}
		} catch (Exception ex) {
			System.out.println("GroupReport.java - getCPCPR - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/**
	 * Get CP(All) values by Target
	 * 
	 * @return Vector
	 * 
	 * @author: Mark Oei
	 * @since v1.3.12.70 22 April 2010
	 */
	public Vector getCP() throws Exception {
		int type = 1;
		if (reqWeightedAverage == true) {
			type = 10;
		}
		String[] surveyInfo = SurveyInfo();
	
		String query = "select FamilyName, GivenName, tblAvgMean.TargetLoginID, RatingTaskID, CompetencyID, ";
		query += " ROUND(AVG(tblAvgMean.AvgMean), 2)as Result";
		query += " from [tblAvgMean], [User]  ";
		query += " where tblAvgMean.SurveyID = " + surveyID + " and Type = " + type
				+ " and RatingTaskID = 1 and tblAvgMean.TargetLoginID = PKUser";
	//	if (divID != 0)
		//	query = query + " AND tblAssignment.FKTargetDivision = "
					//+ divID;

		if (deptID != 0)
			query = query + " AND FKDepartment = "
					+ deptID;

	//	if (groupSection != 0)
			//query = query + " AND tblAssignment.FKTargetGroup = "
				//	+ groupSection;

		
		query += " group by FamilyName, GivenName, tblAvgMean.TargetLoginID, RatingTaskID, CompetencyID order by tblAvgMean.TargetLoginID Asc, Result Desc";

		Vector v = new Vector();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
		
			while (rs.next()) {
				String[] arr = new String[3];
				if (Integer.parseInt(surveyInfo[3]) == 0)
					arr[0] = rs.getString("FamilyName").trim() + " "
							+ rs.getString("GivenName").trim();
				else
					arr[0] = rs.getString("GivenName").trim() + " "
							+ rs.getString("FamilyName").trim();
		
				arr[1] = rs.getString("TargetLoginID");
			
				arr[2] = rs.getString("Result");
			
				v.add(arr);
			}
			
		} catch (Exception ex) {
			System.out.println("Error in GroupReport.java - getCP - "
					+ ex.getMessage() + " " + ex);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	} // End of getCP()

	/**
	 * Draw line chart for CPCPR or CPFPR.
	 * 
	 * @see printCPvsCPR()
	 */
	public void drawLineChart() throws IOException, Exception {
		XSpreadsheet xSpreadsheet2 = OO.getSheet(xDoc,"Sheet2");

		int r = row;
		int c = 0;

		int totalCol = 2; // 1 for comp, 2 with CP

		int total = totalCompetency(); // 1 for all

		String title = "Current Proficiency";
		c = 1;
		OO.insertString(xSpreadsheet2,"CP",r,c);
		c++;

		if (hasCPR)
			OO.insertString(xSpreadsheet2,"CPR",r,c);
		else if (hasFPR)
			OO.insertString(xSpreadsheet2,"FPR",r,c);
		// else
		// OO.insertString(xSpreadsheet2, "CPR", r, c);

		r++;
		vCPValues.clear(); // clear the object, Mark Oei 22 April 2010

		for (int i = 0; i < total; i++) {

			c = 0;
			int compID = 0;
			String compName = " ";
			double dCP = 0;
			double dCPR = 0;

			voRatingResult voCP = (voRatingResult) vCP.elementAt(i);

			if (voCP != null) {

				compID = voCP.getCompetencyID();

				compName = UnicodeHelper.getUnicodeStringAmp(voCP
						.getCompetencyName());

				dCP = voCP.getResult();

				vCPValues.add(new String[]{compName, Double.toString(dCP)}); // add
																				// compName
																				// and
																				// cp
																				// values
																				// to
																				// object,
																				// Mark
																				// Oei
																				// 2010
			}
			// System.out.println("Competency Name "+compName);
			int compLocation = sortCompetency(compName);
			OO.insertString(xSpreadsheet2,compName,r + compLocation,c);
			c++;

			OO.insertNumeric(xSpreadsheet2,dCP,r + compLocation,c);
			c++;
			HashMap<String,Double> hash = new HashMap<String,Double>();
			hash.put(compName,dCP);
			comparisonCPTableVector.add(hash);
			if (hasCPR || hasFPR) {
				voRatingResult voCPR = (voRatingResult) vCPR.elementAt(i);

				if (voCPR != null)
					dCPR = voCPR.getResult();
				else
					dCPR = 0;

				// Denise 22/12/2009 To not print the CPR value if CPR does not
				// exist
				totalCol = 3;
				title = "Current Proficiency Vs Current Proficiency Required";
				OO.insertNumeric(xSpreadsheet2,dCPR,r,c);

				c++;

			} else
				// if no CPR/FPR is chosen, set the CPR as 0
				dCPR = 0;

			double gap = Math.round((dCP - dCPR) * 100.0) / 100.0;

			double[] dArrTemp = new double[2];
			dArrTemp[0] = dCP;
			dArrTemp[1] = dCPR;

			CPCPRMap.put(new Integer(compID),(double[]) dArrTemp);
			vGap.add(new String[]{compName, Double.toString(gap)});
			CompIDGapMap.put(new Integer(compID),Double.toString(gap));

		}
		r += total;
		// Denise 06/01/2010 //dynamically change the description about
		// "CP versus CPR"
		if (hasCPR || hasFPR)
			OO.findAndReplace(xSpreadsheet,"<Graph Description>",
					"Current Proficiency versus the Required Proficiency");
		else
			// CPR are all 0s
			OO.findAndReplace(xSpreadsheet,"<Graph Description>",
					"Current Proficiency");

		// Denise 06/01/2009 dynamically change <ranking report> line in the
		// second page

		if (exGR == 1) // unless exclude group ranking report option is selected
		{
			int[] address = OO.findString(xSpreadsheet,"<Ranking Report>");
			OO.deleteRows(xSpreadsheet,0,10,address[1],address[1] + 1,1,0); // delete
																			// the
																			// whole
																			// row
			OO.insertRows(xSpreadsheet,0,10,address[1],address[1] + 1,1,0); // insert
																			// again
																			// to
																			// prevent
																			// any
																			// affect
																			// to
																			// the
																			// pages
																			// below
		} else
			OO.findAndReplace(xSpreadsheet,"<Ranking Report>",
					"Group Ranking Report");

		// Denise 22/12/2009 to print the title of the chart
		XTableChart xtableChart = OO.getChartByIndex(xSpreadsheet,0);
		xtableChart = OO.setChartTitle(xtableChart,title);
		OO.showLegend(xtableChart,true); // added to set chart legend to true,
											// Mark Oei 25 Mar 2010
		OO.setSourceData(xSpreadsheet,xSpreadsheet2,0,c - totalCol,c - 1,row,
				r - 1);
		// Denise 06/01/2009 to change the scale of yAxis
		int maxScale = getMaxScale();
		xtableChart = OO.setAxes(xtableChart,"","",maxScale,1,6000,0); // rotate
																		// 60
																		// degree
																		// vertically
		OO.drawGridLines(xtableChart,0); // draw both vertical and horizontal
											// gridlines, Mark Oei 25 Mar 2010
		OO.changeChartType("com.sun.star.chart.LineDiagram",xtableChart);

		total = total + row - 1;
		row--;

	}

	/**
	 * Count the total competencies in the particular survey
	 * 
	 * @return int TotalComp Total Competency
	 * @throws SQLException
	 * 
	 * @see drawChart()
	 */
	public int totalCompetency() throws SQLException {
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);

		int total = 0;

		if (surveyLevel == 0) {

			query = query
					+ "SELECT  COUNT(CompetencyID) AS Total FROM tblSurveyCompetency ";
			query = query + "WHERE SurveyID = " + surveyID;

		} else {
			query = query
					+ "SELECT COUNT(DISTINCT CompetencyID) AS Total FROM ";
			query = query + "tblSurveyBehaviour WHERE SurveyID = " + surveyID;
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				total = rs.getInt(1);

			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - totalCompetency - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return total;

	}

	/************************* END CP/CPR ****************************************************/

	// ****Added by Tracy 26 aug 08*************
	// ************Print Rating Title based on CPR or FPR
	public void printGapTitle(int surveyID) throws SQLException, IOException,
			Exception {
		System.out.println("5.1 Gap Title Insertion Starts");

		int[] address = OO.findString(xSpreadsheet,"<Gap Title>");

		OO.findAndReplace(xSpreadsheet,"<Gap Title>","");

		column = address[0];
		row = address[1];
		int i = 0;
		Vector RTaskID = new Vector();
		Vector RTaskName = new Vector();

		// added to get lower and upper limit of CP for display in section
		// description
		// Mark Oei 22 April 2010
		double MinMaxGap[] = getMinMaxGap();
		double low = MinMaxGap[0];
		double high = MinMaxGap[1];

		// Get Rating from database according to s urvey ID
		String query = "SELECT a.RatingTaskID as RTaskID, b.RatingTask as RTaskName FROM tblSurveyRating a ";
		query += "INNER JOIN tblRatingTask b ON a.RatingTaskID=b.RatingTaskID  WHERE a.SurveyID = "
				+ surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				RTaskID.add(i,new Integer(rs.getInt("RTaskID")));
				RTaskName.add(i,new String(rs.getString("RTaskName")));
				i++;
			}

			// Check CPR or FPR
			String pType = "";
			String CPR = "";
			String CP = "";
			String FPR = "";

			for (int n = 0; RTaskID.size() - 1 >= n; n++) {
				if (((Integer) RTaskID.elementAt(n)).intValue() == 1) {
					CP = RTaskName.elementAt(n).toString();
				} else if (((Integer) RTaskID.elementAt(n)).intValue() == 2) {
					CPR = RTaskName.elementAt(n).toString();
					pType = "C";
				} else if (((Integer) RTaskID.elementAt(n)).intValue() == 3) {
					FPR = RTaskName.elementAt(n).toString();
					pType = "F";
				}
			}

			String title = "";
			String info = ""; // add new variable to store string for display in
								// section description, Mark Oei 22 April 2010
			if (pType.equals("C")) {
				// changed by Hemilda 15/09/2008 change the word and make it fit
				// with the width of column
				// changed from CPR - CP to CP - CPR
				// Mark Oei 25 Mar 2010
				// added to display the information
				// Mark Oei 22 April 2010
				info = "The table below indicates the Target's strengths and areas of development. ";
				info += "For competencies where the gap is positive, these are the Target's strengths. ";
				info += "For competencies where the gap is negative, these are areas where the Target requires development.";
				title = "Gap = " + CP + " (All) minus " + CPR + " (All)"; // :
																			// Strengths
																			// and
																			// Development
																			// Areas
																			// Report";
			} else if (pType.equals("F")) {
				info = "The table below indicates the Target's strengths and areas of development. ";
				info += "For competencies where the gap is positive, these are the Target's strengths. ";
				info += "For competencies where the gap is negative, these are areas where the Target requires development.";
				title = "Gap = " + FPR + " (All) minus " + CP + " (All)"; // :
																			// Strengths
																			// and
																			// Development
																			// Areas
																			// Report";
			} else {
				info = "The table below indicates the Target's strengths and areas of development. ";
				info += "For competencies where the CP is higher than " + high;
				info += ", these are the Target's strengths. For competencies where the CP is lower than "
						+ low;
				info += ", these are areas where the Target requires development.";
				title = "CP = " + CP + " (All)";
			}
			// Insert title to excel file
			OO.insertString(xSpreadsheet,info,row - 2,0); // added to display
															// info
															// programmatically,
															// Mark Oei 22 April
															// 2010
			OO.insertString(xSpreadsheet,title,row,0);
			OO.mergeCells(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setRowHeight(xSpreadsheet,row - 2,1,
					ROWHEIGHT * OO.countTotalRow(info,90)); // added to allow
															// auto-increment of
															// row height, Mark
															// Oei 22 April 2010
			OO.setRowHeight(xSpreadsheet,row,1,
					ROWHEIGHT * OO.countTotalRow(title,90));

		} catch (Exception E) {
			System.err.println("SurveyResult.java - GroupSection - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
	}

	// **********End Tracy Add 26 aug 08

	/************************** START GAP ****************************************************/
	/**
	 * print gap
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 * 
	 * @see generateReport()
	 */
	public void printGap() throws SQLException, IOException, Exception {

		System.out.println("5. Gap Insertion Starts");
		// Added to instantiate a local variable called xSpreadsheet2 to make it
		// common with IndividualReport.java
		// Mark Oei 25 Mar 2010
		XSpreadsheet xSpreadsheet2 = OO.getSheet(xDoc,"Sheet2");

		int[] address = OO.findString(xSpreadsheet,"<Gap>");

		column = address[0];
		row = address[1];
		int c = 0;
		// Added to define columns where CP, CPR, Gap are inserted in
		// spreadsheet
		// Mark Oei 25 Mar 2010
		int gapCol = 11;
		int cprCol = 10;
		int cpCol = 9;
		int[] cpAddress;
		double cpValue = 0.0;
		double cprValue = 0.0;

		vGap = G.sorting(vGap,1);

		OO.findAndReplace(xSpreadsheet,"<Gap>","");

		double MinMaxGap[] = getMinMaxGap();

		double low = MinMaxGap[0];
		double high = MinMaxGap[1];

		if (hasCPR || hasFPR) // If CPR or FPR is chosen in this survey
		{
			String title = "COMPETENCY";

			if (ST.LangVer == 2)
				title = "KOMPETENSI";

			OO.insertString(xSpreadsheet,title,row,c);
			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);

			row++;
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 2,2,1);

			int startBorder = row;
			// Insert 2 new labels CP and CPR before Gap
			// Mark Oei 25 Mar 2010
			if (ST.LangVer == 1) {
				OO.insertString(xSpreadsheet,
						"STRENGTH ( Gap >= " + high + " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
				OO.insertString(xSpreadsheet,"CPR",row,cprCol);
				OO.insertString(xSpreadsheet,"Gap",row,gapCol);

			} else if (ST.LangVer == 2) {
				OO.insertString(xSpreadsheet,"KEKUATAN ( Selisih >= " + high
						+ " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
				OO.insertString(xSpreadsheet,"CPR",row,cprCol);
				OO.insertString(xSpreadsheet,"Selisih",row,gapCol);
			}

			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setCellAllignment(xSpreadsheet,cpCol,gapCol,row,row,1,2);
			OO.setBGColor(xSpreadsheet,startColumn,endColumn - 1,row,row,
					BGCOLOR);
			row++;

			for (int i = 0; i < vGap.size(); i++) {
				double gap = Double.valueOf(((String[]) vGap.elementAt(i))[1])
						.doubleValue();

				if (gap >= high) {
					String compName = ((String[]) vGap.elementAt(i))[0];

					OO.insertRows(xSpreadsheet,startColumn,endColumn,row,
							row + 1,1,1);

					OO.insertString(xSpreadsheet,compName,row,c);
					OO.insertNumeric(xSpreadsheet,gap,row,gapCol);
					// Insert CP and CPR values next to Gap
					// Mark Oei 25 Mar 2010
					cpAddress = OO.findString(xSpreadsheet2,compName);
					cpValue = OO.getCellValue(xSpreadsheet2,cpAddress[1],
							cpAddress[0] + 1);
					cprValue = OO.getCellValue(xSpreadsheet2,cpAddress[1],
							cpAddress[0] + 2);
					OO.insertNumeric(xSpreadsheet,cpValue,row,cpCol);
					OO.insertNumeric(xSpreadsheet,cprValue,row,cprCol);
					row++;
				}
			}

			row++;
			int endBorder = row;
			OO.setTableBorder(xSpreadsheet,startColumn,endColumn - 1,
					startBorder,endBorder,false,false,true,true,true,true);
			// Add border lines from cpCol to gapCol
			// Mark Oei 25 Mar 2010
			OO.setTableBorder(xSpreadsheet,cpCol,endColumn - 1,startBorder,
					endBorder,true,false,true,true,false,false);

			startBorder = endBorder + 1;
			row++;
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 2,2,1);

			// Insert 2 new labels CP and CPR before Gap
			// Mark Oei 25 Mar 2010
			if (ST.LangVer == 1) {
				OO.insertString(xSpreadsheet,"MEET EXPECTATIONS ( " + low
						+ " < Gap < " + high + " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
				OO.insertString(xSpreadsheet,"CPR",row,cprCol);
				OO.insertString(xSpreadsheet,"Gap",row,gapCol);
			} else if (ST.LangVer == 2) {
				OO.insertString(xSpreadsheet,"MEMENUHI PENGHARAPAN ( " + low
						+ " < Selisih < " + high + " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
				OO.insertString(xSpreadsheet,"CPR",row,cprCol);
				OO.insertString(xSpreadsheet,"Selisih",row,gapCol);
			}

			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setCellAllignment(xSpreadsheet,cpCol,gapCol,row,row,1,2);
			OO.setBGColor(xSpreadsheet,startColumn,endColumn - 1,row,row,
					BGCOLOR);
			row++;

			for (int i = 0; i < vGap.size(); i++) {
				double gap = Double.valueOf(((String[]) vGap.elementAt(i))[1])
						.doubleValue();

				if (gap < high && gap > low) {
					String compName = ((String[]) vGap.elementAt(i))[0];

					OO.insertRows(xSpreadsheet,startColumn,endColumn,row,
							row + 1,1,1);

					OO.insertString(xSpreadsheet,compName,row,c);
					OO.insertNumeric(xSpreadsheet,gap,row,gapCol);
					// Insert CP and CPR values next to Gap
					// Mark Oei 25 Mar 2010
					cpAddress = OO.findString(xSpreadsheet2,compName);
					cpValue = OO.getCellValue(xSpreadsheet2,cpAddress[1],
							cpAddress[0] + 1);
					cprValue = OO.getCellValue(xSpreadsheet2,cpAddress[1],
							cpAddress[0] + 2);
					OO.insertNumeric(xSpreadsheet,cpValue,row,cpCol);
					OO.insertNumeric(xSpreadsheet,cprValue,row,cprCol);
					row++;
				}
			}

			row++;
			endBorder = row;
			OO.setTableBorder(xSpreadsheet,startColumn,endColumn - 1,
					startBorder,endBorder,false,false,true,true,true,true);
			// Add border lines from cpCol to gapCol
			// Mark Oei 25 Mar 2010
			OO.setTableBorder(xSpreadsheet,cpCol,endColumn - 1,startBorder,
					endBorder,true,false,true,true,false,false);

			startBorder = endBorder + 1;
			row++;

			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 2,2,1);

			// Insert 2 new labels CP and CPR before Gap
			// Mark Oei 25 Mar 2010
			if (ST.LangVer == 1) {
				OO.insertString(xSpreadsheet,"DEVELOPMENTAL AREA ( Gap <= "
						+ low + " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
				OO.insertString(xSpreadsheet,"CPR",row,cprCol);
				OO.insertString(xSpreadsheet,"Gap",row,gapCol);
			} else if (ST.LangVer == 2) {
				OO.insertString(xSpreadsheet,"AREA PERKEMBANGAN ( Selisih <= "
						+ low + " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
				OO.insertString(xSpreadsheet,"CPR",row,cprCol);
				OO.insertString(xSpreadsheet,"Selisih",row,gapCol);
			}

			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setCellAllignment(xSpreadsheet,cpCol,gapCol,row,row,1,2);
			OO.setBGColor(xSpreadsheet,startColumn,endColumn - 1,row,row,
					BGCOLOR);

			row++;

			for (int i = 0; i < vGap.size(); i++) {
				double gap = Double.valueOf(((String[]) vGap.elementAt(i))[1])
						.doubleValue();

				if (gap <= low) {
					String compName = ((String[]) vGap.elementAt(i))[0];

					OO.insertRows(xSpreadsheet,startColumn,endColumn,row,
							row + 1,1,1);

					OO.insertString(xSpreadsheet,compName,row,c);
					OO.insertNumeric(xSpreadsheet,gap,row,gapCol);
					// Insert CP and CPR values next to Gap
					// Mark Oei 25 Mar 2010
					cpAddress = OO.findString(xSpreadsheet2,compName);
					cpValue = OO.getCellValue(xSpreadsheet2,cpAddress[1],
							cpAddress[0] + 1);
					cprValue = OO.getCellValue(xSpreadsheet2,cpAddress[1],
							cpAddress[0] + 2);
					OO.insertNumeric(xSpreadsheet,cpValue,row,cpCol);
					OO.insertNumeric(xSpreadsheet,cprValue,row,cprCol);
					row++;
				}
			}

			endBorder = row;
			OO.setTableBorder(xSpreadsheet,startColumn,endColumn - 1,
					startBorder,endBorder,false,false,true,true,true,true);
			// Add border lines from cpCol to gapCol
			// Mark Oei 25 Mar 2010
			OO.setTableBorder(xSpreadsheet,cpCol,endColumn - 1,startBorder,
					endBorder,true,false,true,true,false,false);
		} else {
			/****************************************************************************************/
			// Delete the rows with Gap Table description from the report
			// Denise
		
			// Commented off to prevent the deletion of the rows as CP will be
			// displayed
			// Mark Oei 22 April 2010
			// OO.deleteRows(xSpreadsheet, 0, 12, 89, 102, 14, 0);

			vCPValues = G.sorting(vCPValues,1); // added to sort CPValues, Mark
												// Oei 22 April 2010
			String title = "COMPETENCY";

			if (ST.LangVer == 2)
				title = "KOMPETENSI";

			OO.insertString(xSpreadsheet,title,row,c);
			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);

			row++;
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 2,2,1);

			int startBorder = row;
			cpCol = 11;

			if (ST.LangVer == 1) {
				OO.insertString(xSpreadsheet,"STRENGTH ( CP >= " + high + " )",
						row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
			} else if (ST.LangVer == 2) {
				OO.insertString(xSpreadsheet,"KEKUATAN ( CP >= " + high + " )",
						row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
			}

			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			// OO.setFontSize(xSpreadsheet, cpCol-1, cpCol, row, row, 12);
			OO.setCellAllignment(xSpreadsheet,cpCol - 1,cpCol,row,row,1,2);
			OO.setBGColor(xSpreadsheet,startColumn,endColumn - 1,row,row,
					BGCOLOR);
			row++;
		
			for (int i = 0; i < vCPValues.size(); i++) {
				double cpValues = Double.valueOf(
						((String[]) vCPValues.elementAt(i))[1]).doubleValue();

				if (cpValues >= high) {
					String compName = ((String[]) vCPValues.elementAt(i))[0];

					OO.insertRows(xSpreadsheet,startColumn,endColumn,row,
							row + 1,1,1);
					
					OO.insertString(xSpreadsheet,compName,row,c);
					OO.insertNumeric(xSpreadsheet,cpValues,row,cpCol);
					HashMap<String,Double> hash = new HashMap<String,Double>();
					hash.put(compName,cpValues);
					comparisonGapTableVector.add(hash);
					// OO.setFontSize(xSpreadsheet, cpCol-1, cpCol, row, row,
					// 12);
					row++;
				}
			}

			row++;
			int endBorder = row;
			OO.setTableBorder(xSpreadsheet,startColumn,endColumn - 1,
					startBorder,endBorder,false,false,true,true,true,true);

			startBorder = endBorder + 1;
			row++;
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 2,2,1);

			if (ST.LangVer == 1) {
				OO.insertString(xSpreadsheet,"MEET EXPECTATIONS ( " + low
						+ " < CP < " + high + " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
			} else if (ST.LangVer == 2) {
				OO.insertString(xSpreadsheet,"MEMENUHI PENGHARAPAN ( " + low
						+ " < CP < " + high + " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
			}

			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			// OO.setFontSize(xSpreadsheet, cpCol-1, cpCol, row, row, 12);
			OO.setCellAllignment(xSpreadsheet,cpCol - 1,cpCol,row,row,1,2);
			OO.setBGColor(xSpreadsheet,startColumn,endColumn - 1,row,row,
					BGCOLOR);
			row++;

			for (int i = 0; i < vCPValues.size(); i++) {
				double cpValues = Double.valueOf(
						((String[]) vCPValues.elementAt(i))[1]).doubleValue();

				if (cpValues < high && cpValues > low) {
					String compName = ((String[]) vCPValues.elementAt(i))[0];

					OO.insertRows(xSpreadsheet,startColumn,endColumn,row,
							row + 1,1,1);

					OO.insertString(xSpreadsheet,compName,row,c);
					OO.insertNumeric(xSpreadsheet,cpValues,row,cpCol);
					// OO.setFontSize(xSpreadsheet, cpCol-1, cpCol, row, row,
					// 12);
					HashMap<String,Double> hash = new HashMap<String,Double>();
					hash.put(compName,cpValues);
					comparisonGapTableVector.add(hash);
					row++;
				}
			}

			row++;
			endBorder = row;
			OO.setTableBorder(xSpreadsheet,startColumn,endColumn - 1,
					startBorder,endBorder,false,false,true,true,true,true);

			startBorder = endBorder + 1;
			row++;

			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 2,2,1);

			if (ST.LangVer == 1) {
				OO.insertString(xSpreadsheet,"DEVELOPMENTAL AREA ( Gap <= "
						+ low + " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
			} else if (ST.LangVer == 2) {
				OO.insertString(xSpreadsheet,"AREA PERKEMBANGAN ( Gap <= "
						+ low + " )",row,c);
				OO.insertString(xSpreadsheet,"CP",row,cpCol);
			}

			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			// OO.setFontSize(xSpreadsheet, cpCol-1, cpCol, row, row, 12);
			OO.setCellAllignment(xSpreadsheet,cpCol - 1,cpCol,row,row,1,2);
			OO.setBGColor(xSpreadsheet,startColumn,endColumn - 1,row,row,
					BGCOLOR);

			row++;

			for (int i = 0; i < vCPValues.size(); i++) {
				double cpValues = Double.valueOf(
						((String[]) vCPValues.elementAt(i))[1]).doubleValue();

				if (cpValues <= low) {
					String compName = ((String[]) vCPValues.elementAt(i))[0];

					OO.insertRows(xSpreadsheet,startColumn,endColumn,row,
							row + 1,1,1);

					OO.insertString(xSpreadsheet,compName,row,c);
					OO.insertNumeric(xSpreadsheet,cpValues,row,cpCol);
					// OO.setFontSize(xSpreadsheet, cpCol-1, cpCol, row, row,
					// 12);
					HashMap<String,Double> hash = new HashMap<String,Double>();
					hash.put(compName,cpValues);
					comparisonGapTableVector.add(hash);
					row++;
				}
			}

			endBorder = row;
			OO.setTableBorder(xSpreadsheet,startColumn,endColumn - 1,
					startBorder,endBorder,false,false,true,true,true,true);
		}

		System.out.println("5.CP/Gap insertion completed");
	}

	/**
	 * get minimum and maximum gap.
	 * 
	 * @return min and max gap in an array
	 * @throws SQLException
	 * @see printGap()
	 */
	public double[] getMinMaxGap() throws SQLException {
		double gap[] = new double[2];

		String query = "Select MIN_gap, MAX_Gap from tblSurvey where SurveyID = "
				+ surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				gap[0] = rs.getDouble(1);
				gap[1] = rs.getDouble(2);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getMinMaxGap - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return gap;
	}

	/**************************** END GAP ****************************************************/

	/*************************** START COMPETENCY ********************************************/
	/**
	 * This print the competency chart
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 * @see generateReport()
	 */

	// Added by Tracy 01 Sep 08**********************************
	public void printCompGap(int surveyID) throws SQLException, Exception {
		System.out.println("6.1 Competency Gap Insertion Starts");

		int i = 0;
		Vector RTaskID = new Vector();
		Vector RTaskName = new Vector();

		// Get Rating from database according to survey ID
		String query = "SELECT a.RatingTaskID as RTaskID, b.RatingTask as RTaskName FROM tblSurveyRating a ";
		query += "INNER JOIN tblRatingTask b ON a.RatingTaskID=b.RatingTaskID  WHERE a.SurveyID = "
				+ surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				RTaskID.add(i,new Integer(rs.getInt("RTaskID")));
				RTaskName.add(i,new String(rs.getString("RTaskName")));
				i++;
			}

			// Check CPR or FPR
			String pType = "";
			String CPR = "";
			String FPR = "";

			for (int n = 0; RTaskID.size() - 1 >= n; n++) {
				if (((Integer) RTaskID.elementAt(n)).intValue() == 1) {
					// CP=RTaskName.elementAt(n).toString();
				} else if (((Integer) RTaskID.elementAt(n)).intValue() == 2) {
					CPR = RTaskName.elementAt(n).toString();
					pType = "C";
				} else if (((Integer) RTaskID.elementAt(n)).intValue() == 3) {
					FPR = RTaskName.elementAt(n).toString();
					pType = "F";
				}
			}

			String RPTitle = "";
			if (pType.equals("C"))
				RPTitle = CPR;
			else if (pType.equals("F"))
				RPTitle = FPR;

			// Insert title to excel file
			OO.findAndReplace(xSpreadsheet,"<CompRP>",RPTitle);

		} catch (Exception E) {
			System.err.println("SurveyResult.java - GroupSection - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
	}
	// End add by Tracy 01 Sep 08*******************************

	public void printCompetency() throws SQLException, IOException, Exception {
		System.out.println("6. Competency/Key Behaviour Report");

		int[] address = OO.findString(xSpreadsheet,"<Report>");

		column = address[0];
		row = address[1];
		// OO.insertPageBreak(xSpreadsheet, 0, 10, row-1);

		OO.findAndReplace(xSpreadsheet,"<Report>","");

		// Denise 23/12/2009 to delete the four remainder rows
		// OO.deleteRows(xSpreadsheet, 0, 10, row, row+4, 4, 1);
		int surveyLevel = Integer.parseInt(surveyInfo[0]);

		int totalOth = 0;
		int totalSup = 0;
		int totalSelf = 0;

		int totalAll = totalOth + totalSup;// get the total number of raters
											// exclude self.(ALL)
		int total = 0;// to determine the number of variables in y-axis.

		// to determine the y legend in charts
		if (totalSelf > 0)
			total = 1 + getTotalOtherRaters() + 1; // 1(self) + other raters +
													// 1(all)
		else
			total = 0 + getTotalOtherRaters() + 1; // 0(self) + other raters +
													// 1(all)

		total = 1 + 1 + 4;// 1 self, 1 All, 4:superior,peers,direct, and
							// indirect
		/*
		 * First page of Competency rank will print out "Competency Report" and
		 * a blank after that This will cause the page break to go haywire. So
		 * we have to reduce the insertion of rows by 2 iInitial will keep track
		 * of the first instance of generation of Competency Report
		 */
		// chart related variable
		int iN = 0; // To be used as counter for arrN
		int iInitial = 0;
		int count = 0; // to count total chart for each page, max = 2;
		// int rowTotal = row + 1;

		int add = 13 / total;

		String level = "Competency";
		if (ST.LangVer == 2)
			level = "Kompetensi";

		row--;
		if (ST.LangVer == 1)
			OO.insertString(xSpreadsheet,level + " Report",row,0);
		else if (ST.LangVer == 2)
			OO.insertString(xSpreadsheet,"Laporan " + level,row,0);

		OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
		OO.setRowHeight(xSpreadsheet,row,1,
				ROWHEIGHT * OO.countTotalRow("Competency Report",90));
		// OO.setRowHeight(xSpreadsheet, row, 1, 1020); //Denise 07/01/2009
		// increas distance between "Competency Report and charts
		OO.setCellAllignment(xSpreadsheet,startColumn,endColumn,row,row + 1,2,1);

		row++;

		int endRow = row;
		int[] ID = {0, 0};

		int noGraph = 0;
		for (int i = 0; i < vCompDetails.size(); i++) {
			if (i != 0) {
				count = ID[1];
			}
			// int compID =
			// ((voCompetency)(vCompDetails.elementAt(i))).getCompetencyID();
			// int totalOth = getTotalRaters("OTH%");
			// int totalSup = getTotalRaters("SUP%");
			// int totalSelf = getTotalRaters("SELF");

			// Insert Competency Graph
			ID = generateChart(0,null,0,iInitial,i,endRow,total,iN,totalAll,
					add,totalSelf,count,vCompDetails.size());
			noGraph++;

			int compID = ID[0];
			if (surveyLevel == 1) {

				Vector vKBDetails = getKBList(ID[0]);
				for (int j = 0; j < vKBDetails.size(); j++) {
					count = ID[1];
					ID = generateChart(1,vKBDetails,compID,iInitial,j,endRow,
							total,iN,totalAll,add,totalSelf,count,
							vKBDetails.size());
					noGraph++;
					iInitial++;
				}

			}

			iInitial++;
		} // while Comp
		if (noGraph % 2 == 1)
			OO.insertPageBreak(xSpreadsheet,0,10,row);
		System.out.println("6.End of printing competency");
	}

	/**
	 * Add by Santoso (2008-10-27) Print the value of total raters on the left
	 * column of the graph
	 * 
	 * @param totalRater
	 * @param startRow
	 * @param totalData
	 * @throws Exception
	 */
	private void printNumeric(int[] totalRater, int startRow, int totalData)
			throws Exception {
		// if not a simplified report, print the numeric (since it has charts)
		if (iReportType != 2) {
			return;
		}
		int[] rowPos = new int[totalData];
	
		if (totalData == 2) {
			// Change order so that n value appears in the following order
			// CP(All) and CP(Self), Desmond 12 Jan 10
			rowPos[1] = startRow + 10;
			rowPos[0] = startRow + 4;
		} else if (totalData == 3) {
			// Change order so that n value appears in the following order CPR,
			// CP(All) and CP(Self), Desmond 12 Jan 10
			rowPos[1] = startRow + 11;
			rowPos[0] = startRow + 7;
			rowPos[2] = startRow + 3;
		} else { // total data = 6 (all, sup, peer, dir, idr, self)
			rowPos[0] = startRow + 2;
			rowPos[1] = startRow + 4;
			rowPos[2] = startRow + 6;
			rowPos[3] = startRow + 8;
			rowPos[4] = startRow + 10;
			rowPos[5] = startRow + 12;
		}

		for (int i = 0; i < totalData; i++) {
			OO.insertNumeric(xSpreadsheet,totalRater[i],rowPos[i],0);
		}
	}

	/**
	 * Retrieves the average current proficiency rating at a Competency level
	 * per individual
	 * 
	 * @param targetID
	 * @param compID
	 * @param RTID
	 * @param type
	 * @param KBID
	 * @return
	 * 
	 * @author Chun Yeong
	 * @since v1.3.12.109 //29 Jun 2011
	 */
	public Vector getIndividualMeanResult(int targetID, int compID, int RTID,
			int type, int KBID) {
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C.ReliabilityCheck(surveyID);

		String tblName = "tblAvgMean";
		String result = "AvgMean";
		if (type == 1 && reqWeightedAverage == true) {
			type = 10;
		}
		if (reliabilityCheck == 0) {
			tblName = "tblTrimmedMean";
			result = "TrimmedMean";
		}
		// Changed by Ha 27/05/08: add keyword DISTINCT
		if (surveyLevel == 0) {
			query = query + "SELECT DISTINCT " + tblName + ".CompetencyID, ";
			query = query + tblName + ".Type, " + tblName + "." + result;
			query = query + " as Result FROM " + tblName;
			query = query + " WHERE " + tblName + ".SurveyID = " + surveyID
					+ " AND ";
			query = query + tblName + ".TargetLoginID = " + targetID;
			query = query + " AND " + tblName + ".RatingTaskID = " + RTID;
			query = query + " AND " + tblName + ".CompetencyID = " + compID;
			query = query + " AND " + tblName + ".Type = " + type;
		} else {
			query = query
					+ "SELECT DISTINCT CompetencyID, Type, AvgMean as Result, KeyBehaviourID ";
			query = query + "FROM tblAvgMean WHERE SurveyID = " + surveyID
					+ " AND ";
			query = query + "TargetLoginID = " + targetID
					+ " AND RatingTaskID = " + RTID;
			query = query + " AND CompetencyID = " + compID
					+ " AND KeyBehaviourID = " + KBID;
			query = query + " AND Type = " + type;
			query = query + " ORDER BY Type";
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
				String[] arr = new String[3];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);
				v.add(arr);
			}
		} catch (Exception ex) {
			System.out.println("IndividualReport.java - MEanResult - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/**
	 * Retrieves the average current proficiency rating at a KB level per
	 * individual
	 * 
	 * @param targetID
	 * @param compID
	 * @param RTID
	 * @param type
	 * @return
	 * 
	 * @author Chun Yeong
	 * @since v1.3.12.109 //29 Jun 2011
	 */
	public Vector getIndividualKBMean(int targetID, int compID, int RTID,
			int type) {
		String query = "";
		query = "SELECT CompetencyID, Type, CAST(AVG(AvgMean) AS numeric(38, 2)) AS Result FROM tblAvgMean ";
		query += "WHERE SurveyID = " + surveyID + " AND TargetLoginID = "
				+ targetID;
		query += " AND CompetencyID = " + compID + " and RatingTaskID = "
				+ RTID + " AND Type = " + type;
		query += " GROUP BY CompetencyID, Type ORDER BY Type";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String[] arr = new String[3];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = rs.getString(3);
				v.add(arr);
			}
		} catch (Exception ex) {
			System.out.println("GroupReport.java - getCPCPRIndividual - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/**
	 * Writes Overall Blind Spot Analysis.
	 * 
	 * @throws Exception
	 * 
	 * @author Chun Yeong
	 * @since v1.3.12.109 //29 Jun 2011
	 */
	public void printBlindSpotAnalysis() throws Exception {

		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		Vector vKBDetails = null;
		Vector temp = new Vector();
		Vector vTargets = null;
		// Gwen Oh - 17/10/2011: Only get selected users for subgroup report
		if (selectedUsers == null)
			vTargets = getAllTargets();
		else {
			vTargets = new Vector(selectedUsers.length);
			for (int i = 0; i < selectedUsers.length; i++) {
				
				vTargets.add(new Integer(selectedUsers[i]));
			}
		}
		int maxScale = getMaxScale();

		String compName = "";
		int compID = 0;
		int targetID = 0;
		int countPos = 0;
		int countNeg = 0;
		int countNone = 0;

		int KBID = 0;
		String KBName = "";

		double othersValue = 0.0;
		double selfValue = 0.0;

		// Variables for printing excel

		int[] address = OO.findString(xSpreadsheet,"<BlindSpotsAnalysis>");
		OO.findAndReplace(xSpreadsheet,"<BlindSpotsAnalysis>","");
		column = address[0];
		row = address[1];
		int posCol = 6;
		int negCol = 8;
		int noneCol = 10;
		int compCount = 1;

		int startBorder = row;

		String title = "";
		if (ST.LangVer == 1) {
			title = "OVERALL BLIND SPOT ANALYSIS";
		} else if (ST.LangVer == 2) {
			title = "ANALISIS BLIND SPOT SECARA MENYELURUH";
		}

		OO.insertString(xSpreadsheet,title,row,column);
		OO.setRowHeight(xSpreadsheet,row,0,ROWHEIGHT*OO.countTotalRow(title,30));
		OO.setFontBold(xSpreadsheet,column,endColumn,row,row);
		OO.setFontSize(xSpreadsheet,column,endColumn,row,row,16);
		OO.mergeCells(xSpreadsheet,column,posCol + 6,row,row);

		address = OO.findString(xSpreadsheet,"<desc>");
		OO.findAndReplace(xSpreadsheet,"<desc>","");
		column = address[0];
		row = address[1];

		// \u2264 writes 'Smaller than or Equals to' or <=
		// \u003E writes 'Greater than' or >
		String desc = "";
		if (ST.LangVer == 1) {
			desc = "The table shows the number of people with positive blind spots (i.e. "
					+ "4.5"
				
					+ " \u2264 Other's Rating \u003E Self Rating) "
					+ "and negative blind spots (i.e. "
					+ maxScale
					/ (2 * 1.0)
					+ " \u2264 Self Rating \u003E Other's Rating).";
		} else if (ST.LangVer == 2) {
			desc = "Tabel ini menunjukkan jumlah orang yang memiliki blind spot positif. (yaitu "
					+ "4.5"
					+ " \u2264 Penilaian Orang Lain \u003E Penilaian Diri Sendiri) "
					+ "dan blind spot negative (yaitu "
					+ maxScale
					/ (2 * 1.0)
					+ " \u2264 Penilaian Diri Sendiri \u003E Penilaian Orang Lain).";
		}

		OO.insertString(xSpreadsheet,desc,row,column);
		OO.setFontType(xSpreadsheet,column,endColumn,row,row,"Times New Roman");
		OO.setFontSize(xSpreadsheet,column,endColumn,row,row,12);
		OO.mergeCells(xSpreadsheet,column,endColumn,row,row);

		address = OO.findString(xSpreadsheet,"<BlindSpotsTable>");
		OO.findAndReplace(xSpreadsheet,"<BlindSpotsTable>","");
		column = address[0];
		row = address[1];

		OO.insertRows(xSpreadsheet,column,endColumn,row + 1,row + 2,1,1);
		if (ST.LangVer == 1) {
			OO.insertString(xSpreadsheet,"Positive Blind Spots",row,posCol);
			OO.insertString(xSpreadsheet,"Negative Blind Spots",row,negCol);
			OO.insertString(xSpreadsheet,"No Blind Spots",row,noneCol);
		} else if (ST.LangVer == 2) {
			OO.insertString(xSpreadsheet,"Blind Spot Positif",row,posCol);
			OO.insertString(xSpreadsheet,"Blind Spot Negatif",row,negCol);
			OO.insertString(xSpreadsheet,"Tidak memiliki blind spot",row,
					noneCol);
		}

		OO.mergeCells(xSpreadsheet,posCol,negCol - 1,row,row);
		OO.mergeCells(xSpreadsheet,negCol,noneCol - 1,row,row);
		OO.mergeCells(xSpreadsheet,noneCol,endColumn - 1,row,row);

		OO.setFontBold(xSpreadsheet,column,endColumn,row,row);
		OO.setFontType(xSpreadsheet,column,endColumn,row,row,"Times New Roman");
		OO.setFontSize(xSpreadsheet,posCol,noneCol,row,row,12);
		OO.setCellAllignment(xSpreadsheet,posCol,noneCol,row,row,1,2);
		OO.setTableBorder(xSpreadsheet,column,posCol - 1,row,row,false,false,
				false,false,false,true);
		OO.setTableBorder(xSpreadsheet,posCol,negCol - 1,row,row,false,false,
				true,true,true,true);
		OO.setTableBorder(xSpreadsheet,negCol,noneCol - 1,row,row,false,false,
				true,true,true,true);
		OO.setTableBorder(xSpreadsheet,noneCol,endColumn - 1,row,row,false,
				false,true,true,true,true);

		row++;
		for (int i = 0; i < vCompDetails.size(); i++) {
			countPos = 0;
			countNeg = 0;
			countNone = 0;
			voCompetency voComp = (voCompetency) vCompDetails.elementAt(i);
			compID = voComp.getCompetencyID();
			compName = voComp.getCompetencyName();

			String[] arr = null;

			for (int k = 0; k < vTargets.size(); k++) {
				
				if(selectedUsers!=null){
				targetID = ((Integer) vTargets.elementAt(k)).intValue();
				}else{
					String[] st = (String[])vTargets.elementAt(k);
					targetID=Integer.parseInt(st[0]);
				}
				if (surveyLevel == 0) { // Competency level
					// Get CP, Others value
					temp = this.getIndividualMeanResult(targetID,compID,1,1,0);
					if (temp.size() != 0) {
						arr = (String[]) temp.elementAt(0);
						othersValue = Double.parseDouble(arr[2]);
					} else {
						othersValue = 0.0;
					}

					// Get CP, Self value
					temp = this.getIndividualMeanResult(targetID,compID,1,4,0);
					if (temp.size() != 0) {
						arr = (String[]) temp.elementAt(0);
						selfValue = Double.parseDouble(arr[2]);
					} else {
						selfValue = 0.0;
					}

				} else { // KB level
					// Get CP, Others value
					temp = this.getIndividualKBMean(targetID,compID,1,1);
					if (temp.size() != 0) {
						arr = (String[]) temp.elementAt(0);
						othersValue = Double.parseDouble(arr[2]);
					} else {
						othersValue = 0.0;
					}

					// Get CP, Self value
					temp = this.getIndividualKBMean(targetID,compID,1,4);
					if (temp.size() != 0) {
						arr = (String[]) temp.elementAt(0);
						selfValue = Double.parseDouble(arr[2]);
					} else {
						selfValue = 0.0;
					}
				}

				// 3 conditions: 1) others must be more than (maxScale/2) ** FOr
				// SPF, more than 4.5
				// 2) both others and self results are not the same
				// 3) others must be more than self
				if (othersValue >= (9 / (2 * 1.0)) && (othersValue > selfValue)
						&& (othersValue != selfValue))
					countPos++;
				// 3 conditions: 1) self must be more than (maxScale/2)
				// 2) both others and self results are not the same
				// 3) self must be more than others
				else if (othersValue >= (9 / (2 * 1.0))
						&& selfValue >= (maxScale / (2 * 1.0))
						&& (selfValue > othersValue)
						&& (othersValue != selfValue))
					countNeg++;

				else if (othersValue >= (9 / (2 * 1.0)))
					countNone++;
			} // End loop, target's list

			// Write onto Excel sheet
			// compCount, compName, countPos, countNeg, countNone

			/****************************************************************************
			 * Check if line exceeds new page. If yes, insert page break * This
			 * section is similar to BOTTOM checking before printing key
			 * behavior. *
			 ****************************************************************************/
			// To cater to if the competency name is starting on a new page
			// And reset the variables: startBorder, lastPageRowCount and
			// currentPageHeight
			// Check height and insert pagebreak where necessary
			int pageHeightLimit = 22272;// Page limit is 22272
			int currentPageHeight = 1076;

			// calculate the height of the table that is being added.
			for (int k = startBorder; k <= row; k++) {
				currentPageHeight += OO.getRowHeight(xSpreadsheet,k,column);
			}

			currentPageHeight += 1076;

			if (currentPageHeight > pageHeightLimit
					|| ((pageHeightLimit - currentPageHeight) <= 3000)) {// adding
																			// the
																			// table
																			// will
																			// exceed
																			// a
																			// single
																			// page,
																			// insert
																			// page
																			// break

				OO.insertRows(xSpreadsheet,column,endColumn,row,row + 2,2,1);
				// Draw the border
				OO.setTableBorder(xSpreadsheet,column,endColumn - 1,row - 1,
						row - 1,false,false,false,false,false,true);
				// Insert page break
				OO.insertPageBreak(xSpreadsheet,column,endColumn,row);
				// to move the table two lines down
				row += 2;
				// reset values
				startBorder = row;
				currentPageHeight = 0;

				OO.insertRows(xSpreadsheet,column,endColumn,row + 1,row + 2,1,1);

				// Insert 2 new labels CP and CPR before Gap and set font size
				// to 12
				if (ST.LangVer == 1) {
					OO.insertString(xSpreadsheet,"Positive Blind Spots",row,
							posCol);
					OO.insertString(xSpreadsheet,"Negative Blind Spots",row,
							negCol);
					OO.insertString(xSpreadsheet,"No Blind Spots",row,noneCol);
				} else if (ST.LangVer == 2) {
					OO.insertString(xSpreadsheet,"Blind Spot Positif",row,
							posCol);
					OO.insertString(xSpreadsheet,"Blind Spot Negatif",row,
							negCol);
					OO.insertString(xSpreadsheet,"Tidak memiliki blind spot",
							row,noneCol);
				}
				OO.mergeCells(xSpreadsheet,posCol,negCol - 1,row,row);
				OO.mergeCells(xSpreadsheet,negCol,noneCol - 1,row,row);
				OO.mergeCells(xSpreadsheet,noneCol,endColumn - 1,row,row);

				OO.setFontBold(xSpreadsheet,column,endColumn,row,row);
				OO.setFontType(xSpreadsheet,column,endColumn,row,row,
						"Times New Roman");
				OO.setFontSize(xSpreadsheet,posCol,noneCol,row,row,12);
				OO.setCellAllignment(xSpreadsheet,posCol,noneCol,row,row,1,2);
				OO.setTableBorder(xSpreadsheet,column,posCol - 1,row,row,false,
						false,false,false,false,true);
				OO.setTableBorder(xSpreadsheet,posCol,negCol - 1,row,row,false,
						false,true,true,true,true);
				OO.setTableBorder(xSpreadsheet,negCol,noneCol - 1,row,row,
						false,false,true,true,true,true);
				OO.setTableBorder(xSpreadsheet,noneCol,endColumn - 1,row,row,
						false,false,true,true,true,true);
				row++;
			} // End of checking for page limit

			OO.insertRows(xSpreadsheet,column,endColumn,row + 1,row + 2,1,1);
			// Insert Competency Name
			OO.insertString(xSpreadsheet,compCount + ". " + compName,row,column);
			OO.mergeCells(xSpreadsheet,column,posCol - 1,row,row);

			// Insert Others and Self values
			OO.insertNumeric(xSpreadsheet,countPos,row,posCol);
			OO.mergeCells(xSpreadsheet,posCol,negCol - 1,row,row);

			OO.insertNumeric(xSpreadsheet,countNeg,row,negCol);
			OO.mergeCells(xSpreadsheet,negCol,noneCol - 1,row,row);

			OO.insertNumeric(xSpreadsheet,countNone,row,noneCol);
			OO.mergeCells(xSpreadsheet,noneCol,endColumn - 1,row,row);
			OO.setFontType(xSpreadsheet,column,endColumn,row,row,
					"Times New Roman");
			OO.setFontSize(xSpreadsheet,column,endColumn,row,row,12);

			// Centralize and draw border for the data
			OO.setCellAllignment(xSpreadsheet,posCol,endColumn - 1,row,row,1,2);

			// Color competency
			OO.setBGColor(xSpreadsheet,column,endColumn - 1,row,row,BGCOLOR);

			// Increment Competency count
			compCount++;

			// Add border lines
			OO.setTableBorder(xSpreadsheet,column,posCol - 1,row,row,false,
					false,true,true,true,true);
			OO.setTableBorder(xSpreadsheet,posCol,negCol - 1,row,row,false,
					false,true,true,true,true);
			OO.setTableBorder(xSpreadsheet,negCol,noneCol - 1,row,row,false,
					false,true,true,true,true);
			OO.setTableBorder(xSpreadsheet,noneCol,endColumn - 1,row,row,false,
					false,true,true,true,true);

			row++;

			if (surveyLevel == 1) {
				// Get KB List
				vKBDetails = getKBList(compID);
		
				// vKBDetails.size());
				for (int j = 0; j < vKBDetails.size(); j++) {
					countPos = 0;
					countNeg = 0;
					countNone = 0;
					voKeyBehaviour voKB = (voKeyBehaviour) vKBDetails
							.elementAt(j);

					KBID = voKB.getKeyBehaviourID();
					KBName = voKB.getKeyBehaviour();

					for (int k = 0; k < vTargets.size(); k++) {
						if(selectedUsers!=null){
							targetID = ((Integer) vTargets.elementAt(k)).intValue();
							}else{
								String[] st = (String[])vTargets.elementAt(k);
								targetID=Integer.parseInt(st[0]);
							}

						// Get CP, Others value
						temp = this.getIndividualMeanResult(targetID,compID,1,
								1,KBID);
						if (temp.size() != 0) {
							arr = (String[]) temp.elementAt(0);
							othersValue = Double.parseDouble(arr[2]);
						} else {
							othersValue = 0.0;
						}

						// Get CP, Self value
						temp = this.getIndividualMeanResult(targetID,compID,1,
								4,KBID);
						if (temp.size() != 0) {
							arr = (String[]) temp.elementAt(0);
							selfValue = Double.parseDouble(arr[2]);
						} else {
							selfValue = 0.0;
						}

						if (othersValue > selfValue)
							countPos++;

						else if (othersValue < selfValue)
							countNeg++;

						else
							countNone++;
					}

					// Write onto Excel sheet
					// KBName, countPos, countNeg, countNone

					/*************************************************************************
					 * Check if line exceeds new page. If yes, insert page break
					 * * This section is similar to ABOVE checking before
					 * printing competency. *
					 *************************************************************************/
					// To cater to if the Key Behavior name is starting on a new
					// page
					// And reset the variables: startBorder, lastPageRowCount
					// and currentPageHeight
					// Check height and insert pagebreak where necessary
					pageHeightLimit = 22272;// Page limit is 22272
					currentPageHeight = 1076;

					// calculate the height of the table that is being added.
					for (int k = startBorder; k <= row; k++) {
						currentPageHeight += OO.getRowHeight(xSpreadsheet,k,
								column);
					}

					currentPageHeight += 1076;

					if (currentPageHeight > pageHeightLimit) {// adding the
																// table will
																// exceed a
																// single page,
																// insert page
																// break

						OO.insertRows(xSpreadsheet,column,endColumn,row,
								row + 2,2,1);
						// Draw the border
						OO.setTableBorder(xSpreadsheet,column,endColumn - 1,
								row - 1,row - 1,false,false,false,false,false,
								true);
						// Insert page break
						OO.insertPageBreak(xSpreadsheet,column,endColumn,row);
						// to move the table two lines down
						row += 2;
						// reset values
						startBorder = row;
						currentPageHeight = 0;

						OO.insertRows(xSpreadsheet,column,endColumn,row + 1,
								row + 2,1,1);
						if (ST.LangVer == 1) {
							OO.insertString(xSpreadsheet,
									"Positive Blind Spots",row,posCol);
							OO.insertString(xSpreadsheet,
									"Negative Blind Spots",row,negCol);
							OO.insertString(xSpreadsheet,"No Blind Spots",row,
									noneCol);
						} else if (ST.LangVer == 2) {
							OO.insertString(xSpreadsheet,"Blind Spot Positif",
									row,posCol);
							OO.insertString(xSpreadsheet,"Blind Spot Negatif",
									row,negCol);
							OO.insertString(xSpreadsheet,
									"Tidak memiliki blind spot",row,noneCol);
						}

						OO.mergeCells(xSpreadsheet,posCol,negCol - 1,row,row);
						OO.mergeCells(xSpreadsheet,negCol,noneCol - 1,row,row);
						OO.mergeCells(xSpreadsheet,noneCol,endColumn - 1,row,
								row);

						// TODO missing indonesian translation

						OO.setFontBold(xSpreadsheet,column,endColumn,row,row);
						OO.setFontType(xSpreadsheet,column,endColumn,row,row,
								"Times New Roman");
						OO.setFontSize(xSpreadsheet,posCol,noneCol,row,row,12);
						OO.setCellAllignment(xSpreadsheet,posCol,noneCol,row,
								row,1,2);
						OO.setTableBorder(xSpreadsheet,column,posCol - 1,row,
								row,false,false,false,false,false,true);
						OO.setTableBorder(xSpreadsheet,posCol,negCol - 1,row,
								row,false,false,true,true,true,true);
						OO.setTableBorder(xSpreadsheet,negCol,noneCol - 1,row,
								row,false,false,true,true,true,true);
						OO.setTableBorder(xSpreadsheet,noneCol,endColumn - 1,
								row,row,false,false,true,true,true,true);

						row++;
					} // End of checking for page limit

					OO.insertRows(xSpreadsheet,column,endColumn,row + 1,
							row + 2,1,1);
					// Insert Key behavior Name
					OO.insertString(xSpreadsheet,
							"KB: " + UnicodeHelper.getUnicodeStringAmp(KBName),
							row,column);
					OO.mergeCells(xSpreadsheet,column,posCol - 1,row,row);

					// Insert Others and Self values
					OO.insertNumeric(xSpreadsheet,countPos,row,posCol);
					OO.mergeCells(xSpreadsheet,posCol,negCol - 1,row,row);

					OO.insertNumeric(xSpreadsheet,countNeg,row,negCol);
					OO.mergeCells(xSpreadsheet,negCol,noneCol - 1,row,row);

					OO.insertNumeric(xSpreadsheet,countNone,row,noneCol);
					OO.mergeCells(xSpreadsheet,noneCol,endColumn - 1,row,row);

					OO.setFontType(xSpreadsheet,column,endColumn,row,row,
							"Times New Roman");
					OO.setFontSize(xSpreadsheet,column,endColumn,row,row,12);

					// Centralize the data
					OO.setCellAllignment(xSpreadsheet,posCol,endColumn - 1,row,
							row,1,2);

					// Draw borders
					OO.setTableBorder(xSpreadsheet,column,posCol - 1,row,row,
							false,false,true,true,false,false);
					OO.setTableBorder(xSpreadsheet,posCol,negCol - 1,row,row,
							false,false,true,true,false,false);
					OO.setTableBorder(xSpreadsheet,negCol,noneCol - 1,row,row,
							false,false,true,true,false,false);
					OO.setTableBorder(xSpreadsheet,noneCol,endColumn - 1,row,
							row,false,false,true,true,false,false);

					row++;
				} // End loop, for KB List

				OO.insertRows(xSpreadsheet,column,endColumn,row + 1,row + 2,1,1);
				// Add border lines
				OO.setTableBorder(xSpreadsheet,column,endColumn - 1,row,row,
						false,false,true,true,false,true);
				OO.setTableBorder(xSpreadsheet,posCol,negCol - 1,row,row,false,
						false,true,true,false,false);
				OO.setTableBorder(xSpreadsheet,negCol,noneCol - 1,row,row,
						false,false,true,true,false,false);
				OO.setTableBorder(xSpreadsheet,noneCol,endColumn - 1,row,row,
						false,false,true,true,false,false);
				row++;
			} // End if, surveyLevel == 1
		} // End loop, for Competency List
	}

	/**
	 * CHART TEMPLATE type = 0 to generate competency chart type = 1 to generate
	 * KB chart.
	 * 
	 * @param type
	 * @param iInitial
	 * @param i
	 * @param startRow
	 * @param endRow
	 * @param r1
	 * @param total
	 * @param iN
	 * @param totalAll
	 * @param add
	 * @param totalSelf
	 * @param count
	 * @throws Exception
	 */
	public int[] generateChart(int type, Vector v, int iFKComp, int iInitial,
			int i, int endRow, int total, int iN, int totalAll, int add,
			int totalSelf, int count, int size) throws Exception {
	
		// ", totalSelf = " + totalSelf); // To Remove, Desmond 17 Nov 09
		// --common
		// int RTID = 0;

		if (count == 0 && iInitial != 0) // Denise 07/01/2009 to insert two row
											// at the beginning of the page
		{
			OO.insertRows(xSpreadsheet,0,10,row,row + 2,2,1);
			row += 2;
		}
		int KBID = 0;
		int ID[] = {0, 0};

		int r = 0;
		int rowTotal = 0;
		String sGap = "";
		double dCPScore = -1;
		double dCPRScore = -1;
		int startRow = row;
		// int r1 = 1;

		// competency variable
		int iCompID = iFKComp;
		String sCompName = "";
		String sCompDef = "";

		// KB variable
		String sKB = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		// Print Competency Name and Definition

		if (type == 0) {
			// OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+20,
			// 20, 1);
			// if(iInitial == 0 || iInitial == 1)
			// OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+18,
			// 18, 1);
			// else
			// Denise 23/12/2009 insert rows 18 for all charts
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 18,18,1);

			voCompetency voComp = (voCompetency) vCompDetails.elementAt(i);
			iCompID = voComp.getCompetencyID();
			ID[0] = iCompID;
			sCompName = voComp.getCompetencyName();
			sCompDef = voComp.getCompetencyDefinition();

			startRow = row;

			OO.insertString(xSpreadsheet,
					UnicodeHelper.getUnicodeStringAmp(sCompName),row,0);
			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setBGColor(xSpreadsheet,startColumn,endColumn,row,row,BGCOLOR);
			row++;

			// r1 = row;
			OO.insertString(xSpreadsheet,
					UnicodeHelper.getUnicodeStringAmp(sCompDef),row,0);
			OO.mergeCells(xSpreadsheet,startColumn,endColumn,row,row);
			// Denise 06/01/2009 set alignment to the top
			OO.setCellAllignment(xSpreadsheet,column,column,row,row,5,1);
			OO.setRowHeight(xSpreadsheet,row,1,
					ROWHEIGHT * OO.countTotalRow(sCompDef,95) + 280);
			row++;

			r = 0;

			rowTotal = row + 11;
		}

		// Print KB Statements

		if (type == 1) {
			// Denise 23/12/2009 insert 18 rows instead of 19 to fit the page
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 18,18,1);
			voKeyBehaviour vo = (voKeyBehaviour) v.elementAt(i);
			KBID = vo.getKeyBehaviourID();
			ID[0] = KBID;
			sKB = vo.getKeyBehaviour();

			startRow = row;
			// r1 = row;

			int no = i + 1;
			OO.insertString(xSpreadsheet,
					no + ". " + UnicodeHelper.getUnicodeStringAmp(sKB),row,0);
			OO.mergeCells(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setRowHeight(xSpreadsheet,row,0,
					ROWHEIGHT * OO.countTotalRow(sKB,90));

			row += 2;

			rowTotal = row + 11;

			r = 0;
		}

		// Generate the actual bar charts

		String[] Rating = new String[total];
		double[] Result = new double[total];
		// Change by Santoso (2008-10-27)
		// totalRater array is used to keep the total rater value which will be
		// printed later
		int[] totalRater = new int[total];
		for (int j = 0; j < vRatingTask.size(); j++) {
			voRatingTask voSurvey = (voRatingTask) vRatingTask.elementAt(j);

			// RTID = voSurvey.getRatingTaskID();
			String RTCode = voSurvey.getRatingCode();

			// Common variable
			double dSupScore = -1;
			double dPeerScore = -1;
			double dDirScore = -1;
			double dIdrScore = -1;
			double dSelfScore = -1;
			// for weighted Average
			double totalScoreFromCatergories = 0;
			double totalCatergory = 0;
			// Comp variable
			double[] CPCPRScore = {-1, -1};

			// For Competency bar chart
			if (type == 0) {
				CPCPRScore = (double[]) CPCPRMap.get(new Integer(iCompID));
		
				dCPScore = CPCPRScore[0];
		
				dCPRScore = CPCPRScore[1];

				// If Survey Level is KB then change KBID to -1 to so as to run
				// new query for retrieving data
				if (surveyLevel == 1) {
					
					// // To remove, Desmond 17 Nov 09
					KBID = -1;
				}

			}

			if (RTCode.equals("CP")) {

				if (type == 1) {
				
					dCPScore = getKBCPCPR(iCompID,KBID,RTCode);
				}

				if (dCPScore != -1) {
					Rating[r] = RTCode + " (ALL)";
					
					Result[r] = dCPScore;

					/*
					 * Change(s) : Included new categories Peers and
					 * Subordinates when calculating total number of raters
					 * Reason(s) : Cater for the splitting of Peers and
					 * Subordinates from the category Others Updated By: Desmond
					 * Updated On: 17 Nov 09 Previous Change(s): Change to using
					 * totalRater to retrieve the total rater value, Santoso
					 * (2008-10-29)
					 */
					int totalSup = totalRater("SUP%",1,iCompID,KBID);
					int totalPeer = totalRater("PEER%",1,iCompID,KBID);
					int totalSub = totalRater("SUB%",1,iCompID,KBID);
					int totalDir = totalRater("DIR%",1,iCompID,KBID);
					int totalIdr = totalRater("IDR%",1,iCompID,KBID);
					int totalOth = totalRater("OTH%",1,iCompID,KBID);

					totalAll = totalSup + totalPeer + totalSub + totalOth
							+ totalDir + totalIdr;
					totalRater[r++] = totalAll;

					arrN[iN] = totalAll;
					iN++;
					// Change by Santoso (2008-10-27)
					// the total rater will be printed later below

				}
				int totalSup = totalRater("SUP%",1,iCompID,KBID);
				if (totalSup != 0) {

					if (type == 0)
						dSupScore = getCPNonSelf(iCompID,"%SUP%");

					if (dSupScore != -1) {
						Rating[r] = RTCode + " (SUPERIOR)";
						Result[r] = dSupScore;
						totalScoreFromCatergories += dSupScore;
						totalCatergory++;
						totalRater[r++] = totalSup;
						arrN[iN] = totalSup;
						iN++;

					}
				}
				int totalPeer = totalRater("PEER%",1,iCompID,KBID);
				if (totalPeer != 0) {

					if (type == 0)
						dPeerScore = getCPNonSelf(iCompID,"%PEER%");
					// else only comp level (SPF)
					// dSupScore = getKBCPSelf(iCompID, KBID);

					if (dPeerScore != -1) {
						Rating[r] = RTCode + " (PEER)";
						Result[r] = dPeerScore;
						totalScoreFromCatergories += dPeerScore;
						totalCatergory++;
						totalRater[r++] = totalPeer;
						arrN[iN] = totalPeer;
						iN++;

					}
				}
				int totalDir = totalRater("DIR%",1,iCompID,KBID);
				if (totalDir != 0) {

					if (type == 0)
						dDirScore = getCPNonSelf(iCompID,"%DIR%");
					// else only comp level (SPF)
					// dSupScore = getKBCPSelf(iCompID, KBID);

					if (dDirScore != -1) {
						Rating[r] = RTCode + " (DIRECT)";
						Result[r] = dDirScore;
						totalScoreFromCatergories += dDirScore;
						totalCatergory++;
						totalRater[r++] = totalDir;
						arrN[iN] = totalDir;
						iN++;

					}
				}
				int totalIdr = totalRater("IDR%",1,iCompID,KBID);
				if (totalIdr != 0) {

					if (type == 0)
						dIdrScore = getCPNonSelf(iCompID,"%IDR%");
					// else only comp level (SPF)
					// dSupScore = getKBCPSelf(iCompID, KBID);

					if (dIdrScore != -1) {
						Rating[r] = RTCode + " (INDIRECT)";
						Result[r] = dIdrScore;
						totalScoreFromCatergories += dIdrScore;
						totalCatergory++;
						totalRater[r++] = totalIdr;
						arrN[iN] = totalIdr;
						iN++;

					}
				}
				// Change by Santoso (2008-10-29)
				// use totalRater to retrieve the total rater value
				totalSelf = totalRater("SELF",1,iCompID,KBID);
				if (totalSelf != 0) {

					if (type == 0)
						dSelfScore = getCPSelf(iCompID);
					else
						dSelfScore = getKBCPSelf(iCompID,KBID);

					if (dSelfScore != -1) {
						Rating[r] = RTCode + " (SELF)";
						// Change by Santoso (2008-10-29)
						// keep the total self in the totalRater array to be
						// printed later
						Result[r] = dSelfScore;
						totalRater[r++] = totalSelf;

						arrN[iN] = totalSelf;
						iN++;
						// Change by Santoso (2008-10-27)
						// the total rater will be printed below
						// if(iReportType == 2)
						// OO.insertNumeric(xSpreadsheet, totalSelf, rowTotal+2,
						// 0);
						// rowTotal -= add;
					}
				}
				// Sherman - 23/05/2013 check if weighted average is requested.
				/*
				if (reqWeightedAverage == true) {
					DecimalFormat df = new DecimalFormat("0.##");
					Rating[0] = RTCode + " (ALL)";
			
					String weightedAverageResult = df
							.format(totalScoreFromCatergories / totalCatergory);
					Double average = Double.valueOf(weightedAverageResult);
					Result[0] = average;
				} else {
					Rating[0] = RTCode + " (ALL)";
				}
				*/
			} else if (RTCode.equals("CPR") || RTCode.equals("FPR")) {

				if (type == 1)
					dCPRScore = getKBCPCPR(iCompID,KBID,RTCode);

				if (dCPRScore != -1) {

					Rating[r] = RTCode + " (ALL)";
					// Change by Santoso (2008-10-29)
					// keep the total CPR/FPR in the totalRater array to be
					// printed later
					Result[r] = dCPRScore;

					/*
					 * Change(s) : Included new categories Peers and
					 * Subordinates when calculating total number of raters
					 * Reason(s) : Cater for the splitting of Peers and
					 * Subordinates from the category Others Updated By: Desmond
					 * Updated On: 17 Nov 09 Previous Change(s): Change to using
					 * totalRater to retrieve the total rater value, Santoso
					 * (2008-10-29)
					 */
					if (RTCode.equals("CPR")) {
						totalAll = totalRater("SUP%",2,iCompID,KBID)
								+ totalRater("PEER%",2,iCompID,KBID)
								+ totalRater("SUB%",2,iCompID,KBID)
								+ totalRater("OTH%",2,iCompID,KBID);
					} else if (RTCode.equals("FPR")) {
						totalAll = totalRater("SUP%",3,iCompID,KBID)
								+ totalRater("PEER%",3,iCompID,KBID)
								+ totalRater("SUB%",3,iCompID,KBID)
								+ totalRater("OTH%",3,iCompID,KBID);
					}

					totalRater[r++] = totalAll;
					arrN[iN] = totalAll;
					iN++;

					// Change by Santoso (2008-10-27)
					// the total rater will be printed below
					// if(iReportType == 2)
					//
					// OO.insertNumeric(xSpreadsheet, totalAll, rowTotal+4, 0);
					// rowTotal -= add;
				}
			}
		}// while RT

		// Add by Santoso(2008-10-27) to print out the total rater
		printNumeric(totalRater,row,r);

		row++;

		int maxScale = getMaxScale();

		// start draw chart from here
		
		drawChart(Rating,Result,1,maxScale,iCompID);

		column = 9; // write the importance n gap
		int rtemp = row;

		voImportance voImp = getImportance(iCompID,KBID);

		if (voImp != null) {
			String task = voImp.getRatingTask();

			if (task != null) {
				double taskResult = voImp.getResult();

				OO.insertString(xSpreadsheet,task + ": " + taskResult,rtemp,
						column);
				OO.mergeCells(xSpreadsheet,column,endColumn,rtemp,rtemp + 1);
				OO.setCellAllignment(xSpreadsheet,column,endColumn,rtemp,
						rtemp + 1,2,1);

				rtemp += 3;
			}
		}

		if (hasCPR || hasFPR) // If CPR is chosen in this survey
		{

			if (type == 0) {
				sGap = (String) CompIDGapMap.get(new Integer(iCompID));
			} else {
				sGap = Double.toString(Math
						.round((dCPScore - dCPRScore) * 100.0) / 100.0);
			}

			if (ST.LangVer == 1)
				OO.insertString(xSpreadsheet,"Gap = " + sGap,rtemp,column);
			else if (ST.LangVer == 2)
				OO.insertString(xSpreadsheet,"Selisih = " + sGap,rtemp,column);

			OO.mergeCells(xSpreadsheet,column,endColumn,rtemp,rtemp + 1);
			OO.setCellAllignment(xSpreadsheet,column,endColumn,rtemp,rtemp + 1,
					2,1);
		}
		rtemp += 3;

		double LOA = 0;

		if (surveyLevel == 0 || KBID != 0)
			LOA = LevelOfAgreement(iCompID,KBID);
		else
			LOA = AvgLevelOfAgreement(iCompID);

		if (ST.LangVer == 1)
			OO.insertString(xSpreadsheet,"Level Of Agreement: " + LOA + "%",
					rtemp,column);
		else if (ST.LangVer == 2)
			OO.insertString(xSpreadsheet,"Tingkat Persetujuan: " + LOA + "%",
					rtemp,column);

		OO.mergeCells(xSpreadsheet,column,endColumn,rtemp,rtemp + 1);
		OO.setCellAllignment(xSpreadsheet,column,endColumn,rtemp,rtemp + 1,2,1);

		column = 0;
		count++;

		// Denise 23/12/2009 increase the row by 15 for all charts
		// if(iInitial == 0 || iInitial == 1)
		row += 15;
		// else
		// row += 16;

		if (count == 2) {
			count = 0;
			OO.insertPageBreak(xSpreadsheet,startColumn,endColumn,row);
		}
		ID[1] = count;

		endRow = row - 1;
		// comp name and definition
		OO.setTableBorder(xSpreadsheet,startColumn,endColumn,startRow,
				startRow + 1,false,false,true,true,true,true);
		// total sup n others
		OO.setTableBorder(xSpreadsheet,startColumn,startColumn,startRow + 2,
				endRow,false,false,true,true,true,true);

		// chart
		OO.setTableBorder(xSpreadsheet,startColumn + 1,8,startRow + 2,endRow,
				false,false,true,true,true,true);
		OO.setTableBorder(xSpreadsheet,9,endColumn,startRow + 2,endRow,false,
				false,true,true,true,true);

		OO.setCellAllignment(xSpreadsheet,startColumn,startColumn,startRow + 2,
				endRow,1,2);

		return ID;
	}

	/**
	 * Retrieves Key Behaviour lists based on CompetencyID
	 * 
	 * @param compID
	 * @return
	 * @throws SQLException
	 */
	public Vector getKBList(int compID) throws SQLException {
		String query = "";

		Vector v = new Vector();

		query = query
				+ "SELECT DISTINCT tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour ";
		query = query + "FROM tblSurveyBehaviour INNER JOIN KeyBehaviour ON ";
		query = query
				+ "tblSurveyBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
		query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID
				+ " AND ";
		query = query + "tblSurveyBehaviour.CompetencyID = " + compID;
		query = query + " ORDER BY tblSurveyBehaviour.KeyBehaviourID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				voKeyBehaviour vo = new voKeyBehaviour();
				vo.setKeyBehaviourID(rs.getInt("KeyBehaviourID"));
				vo.setKeyBehaviour(rs.getString("KeyBehaviour"));
				v.add(vo);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getKBList - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/**
	 * Draw bar chart for competency report.
	 */
	public void drawChart(String Rating[], double Result[], int type,
			int maxScale,int iFKComp) throws IOException, Exception {
		// System.out.println("draw open");
		XSpreadsheet xSpreadsheet2 = OO.getSheet(xDoc,"Sheet2");
		int r = row;
		int c = 0;

		if (iReportType == 1) {
			// Print heading for "N" and align
			OO.insertString(xSpreadsheet,"N",r,c + 5);
			OO.setCellAllignment(xSpreadsheet,c + 5,c + 5,r,r,1,1);
		}

		/*
		 * Change(s) : Commented away old method of drawing bar charts and
		 * implemented a more simplistic way of drawing the bar charts in the
		 * correct order Reason(s) : Order of bar charts or data (simplified
		 * report) are in the wrong order, should be CPR(All) followed by
		 * CP(All) then CP(Self) at the bottom Updated By: Desmond Updated On:
		 * 12 Jan 2010
		 */

		// Indexes used fore re-ordering results and ratings
		int CPRindx = -1;
		int CPAllIndx = -1;
		int CPSupIndx = -1;
		int CPPeerIndx = -1;
		int CPDirIndx = -1;
		int CPIdrIndx = -1;
		int CPSelfIndx = -1;

		// Get indexes required for re-ordering the results and ratings
		for (int i = 0; i < Rating.length; i++) {
			if (Rating[i].equalsIgnoreCase("CPR (All)")) {
				CPRindx = i;
			}
			if (Rating[i].equalsIgnoreCase("CP (All)")) {
				CPAllIndx = i;
			}
			if (Rating[i].equalsIgnoreCase("CP (Superior)")) {
				CPSupIndx = i;
			}
			if (Rating[i].equalsIgnoreCase("CP (Peer)")) {
				CPPeerIndx = i;
			}
			if (Rating[i].equalsIgnoreCase("CP (Direct)")) {
				CPDirIndx = i;
			}
			if (Rating[i].equalsIgnoreCase("CP (Indirect)")) {
				CPIdrIndx = i;
			}
			if (Rating[i].equalsIgnoreCase("CP (Self)")) {
				CPSelfIndx = i;
			}
		}

		/*
		 * //OLD method - inserting data for remaining rating tasks, ordering
		 * wrong for(int i=Rating.length-1; i>-1; i--) // Reverse order so that
		 * CPR appears on top, Desmond 18 Nov 09 { if (Rating[i] !=null) { r++;
		 * OO.insertString(xSpreadsheet, Rating[i], r, c+2);
		 * OO.mergeCells(xSpreadsheet, c+2, c+3, r, r);
		 * 
		 * OO.insertNumeric(xSpreadsheet, Result[i], r, c+4);
		 * OO.insertNumeric(xSpreadsheet, arrN[i], r, c+5); } }
	
		 */
		// Insert CPR(All) Rating and Result if it exists
		if (CPRindx != -1) {
			r++;
			OO.insertString(xSpreadsheet,Rating[CPRindx],r,c + 2);
			OO.mergeCells(xSpreadsheet,c + 2,c + 3,r,r);

			OO.insertNumeric(xSpreadsheet,Result[CPRindx],r,c + 4);
			OO.insertNumeric(xSpreadsheet,arrN[CPRindx],r,c + 5);
		}

		// Insert CP(All) value if it exists
		if (CPAllIndx != -1) {
			r++;
			OO.insertString(xSpreadsheet,Rating[CPAllIndx],r,c + 2);
			OO.mergeCells(xSpreadsheet,c + 2,c + 3,r,r);
			
			OO.insertNumeric(xSpreadsheet,Result[CPAllIndx],r,c + 4);
			OO.insertNumeric(xSpreadsheet,arrN[CPAllIndx],r,c + 5);
		}
		if (CPSupIndx != -1) {
			r++;
			OO.insertString(xSpreadsheet,Rating[CPSupIndx],r,c + 2);
			OO.mergeCells(xSpreadsheet,c + 2,c + 3,r,r);
			
			OO.insertNumeric(xSpreadsheet,Result[CPSupIndx],r,c + 4);
			OO.insertNumeric(xSpreadsheet,arrN[CPSupIndx],r,c + 5);
		}
		if (CPPeerIndx != -1) {
			r++;
			OO.insertString(xSpreadsheet,Rating[CPPeerIndx],r,c + 2);
			OO.mergeCells(xSpreadsheet,c + 2,c + 3,r,r);

			OO.insertNumeric(xSpreadsheet,Result[CPPeerIndx],r,c + 4);
			OO.insertNumeric(xSpreadsheet,arrN[CPPeerIndx],r,c + 5);
		}
		if (CPDirIndx != -1) {
			r++;
			OO.insertString(xSpreadsheet,Rating[CPDirIndx],r,c + 2);
			OO.mergeCells(xSpreadsheet,c + 2,c + 3,r,r);

			OO.insertNumeric(xSpreadsheet,Result[CPDirIndx],r,c + 4);
			OO.insertNumeric(xSpreadsheet,arrN[CPDirIndx],r,c + 5);
		}
		if (CPIdrIndx != -1) {
			r++;
			OO.insertString(xSpreadsheet,Rating[CPIdrIndx],r,c + 2);
			OO.mergeCells(xSpreadsheet,c + 2,c + 3,r,r);

			OO.insertNumeric(xSpreadsheet,Result[CPIdrIndx],r,c + 4);
			OO.insertNumeric(xSpreadsheet,arrN[CPIdrIndx],r,c + 5);
		}

		// Insert CP(Self) Rating and Result if it exists
		if (CPSelfIndx != -1) {
			r++;
			OO.insertString(xSpreadsheet,Rating[CPSelfIndx],r,c + 2);
			OO.mergeCells(xSpreadsheet,c + 2,c + 3,r,r);

			OO.insertNumeric(xSpreadsheet,Result[CPSelfIndx],r,c + 4);
			OO.insertNumeric(xSpreadsheet,arrN[CPSelfIndx],r,c + 5);
		}

		r = row; // reset
		if (iReportType == 2) // Standard Report (i.e. includes bar charts)
		{

			// New way of drawing bar charts for rating tasks

			// Insert CPR(All) Rating and Result if it exists
			if (CPRindx != -1) {
				OO.insertString(xSpreadsheet2,Rating[CPRindx],r,c);
				OO.insertNumeric(xSpreadsheet2,Result[CPRindx],r,c + 1);
				r++;
			}

			// Insert CP(All) Rating and Result if it exists
			if (CPAllIndx != -1) {
				OO.insertString(xSpreadsheet2,Rating[CPAllIndx],r,c);
				OO.insertNumeric(xSpreadsheet2,Result[CPAllIndx],r,c + 1);
				HashMap<Integer,Double> comparisonHashMap = new HashMap<Integer,Double>();
				comparisonHashMap.put(iFKComp,Result[CPAllIndx]);
				
				comparisonHashMapVector.add(comparisonHashMap);
				r++;
			}

			if (CPSupIndx != -1) {
				OO.insertString(xSpreadsheet2,Rating[CPSupIndx],r,c);
				OO.insertNumeric(xSpreadsheet2,Result[CPSupIndx],r,c + 1);
				r++;
			}
			if (CPPeerIndx != -1) {
				OO.insertString(xSpreadsheet2,Rating[CPPeerIndx],r,c);
				OO.insertNumeric(xSpreadsheet2,Result[CPPeerIndx],r,c + 1);
				r++;
			}
			if (CPDirIndx != -1) {
				OO.insertString(xSpreadsheet2,Rating[CPDirIndx],r,c);
				OO.insertNumeric(xSpreadsheet2,Result[CPDirIndx],r,c + 1);
				r++;
			}
			if (CPIdrIndx != -1) {
				OO.insertString(xSpreadsheet2,Rating[CPIdrIndx],r,c);
				OO.insertNumeric(xSpreadsheet2,Result[CPIdrIndx],r,c + 1);
				r++;
			}

			// Insert CP(Self) Rating and Result if it exists
			if (CPSelfIndx != -1) {
				OO.insertString(xSpreadsheet2,Rating[CPSelfIndx],r,c);
				OO.insertNumeric(xSpreadsheet2,Result[CPSelfIndx],r,c + 1);
				r++;
			}

			long then = System.currentTimeMillis();
			long now = System.currentTimeMillis();

			XTableChart xtablechart = OO.getChart(xSpreadsheet,xSpreadsheet2,c,
					c + 1,row - 1,r - 1,Integer.toString(row),9000,7800,row,2);
			OO.setFontSize(8);
			long now1 = System.currentTimeMillis();
			now = System.currentTimeMillis();
			xtablechart = OO.setChartTitle(xtablechart,"");

			now1 = System.currentTimeMillis();
		
			// /System.out.println("MaxScale---" + maxScale);
			xtablechart = OO.setAxes(xtablechart,"","",maxScale,1,0,0,0); // Remove
																			// X-Axis
																			// label,
																			// Desmond
																			// 18
																			// Nov
																			// 09
			now = System.currentTimeMillis();
			// System.out.println("SEt axis: " + (now - now1) / 1000);
			OO.drawGridLines(xtablechart,0); // draw the horiztonal and vertical
												// gridlines for CP/CPR
												// Bargraphs, Mark Oei 25 Mar
												// 2010
			OO.setChartProperties(xtablechart,false,true,false,true,true); // display
																			// only
																			// vertical
																			// gridlines,
																			// Mark
																			// Oei
																			// 25
																			// Mar
																			// 2010
			OO.showLegend(xtablechart,false);
			now = System.currentTimeMillis();
			// System.out.println( "Time : " + (now - then)/1000 );
		}

		// System.out.println("draw - close");
	}

	/**
	 * get CP score for self
	 * 
	 * @param iFKComp
	 * @return
	 * @throws SQLException
	 * @see generateChart
	 */
	public double getCPSelf(int iFKComp) throws SQLException {
		String query = "";

		int reliabilityCheck = C.ReliabilityCheck(surveyID);

		double dScore = -1;

		if (reliabilityCheck == 0) // trimmed mean
		{
			query = query
					+ "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
			query += "ROUND(AVG(tblTrimmedMean.TrimmedMean), 2) AS Result FROM ";
			query += "tblTrimmedMean INNER JOIN Competency ON ";
			query += "tblTrimmedMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query += "tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query += "[User] ON [User].PKUser = tblTrimmedMean.TargetLoginID ";
			query += "WHERE tblTrimmedMean.SurveyID = " + surveyID;
			query += " AND tblTrimmedMean.Type = 4 AND tblRatingTask.RatingCode = 'CP' AND ";
			query += "tblTrimmedMean.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment INNER JOIN ";
			query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
			query += "WHERE SurveyID = " + surveyID
					+ " AND Competency.PKCompetency = " + iFKComp
					+ " AND RaterCode = 'SELF' AND ";
			query += "RaterStatus IN (1, 2, 4) ";

			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;

			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;

			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;

			query += ") ";

			query += " GROUP BY Competency.PKCompetency, Competency.CompetencyName ";
			query += "ORDER BY Competency.CompetencyName";

		} else {
			query = "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
			query += "ROUND(AVG(tblAvgMean.AvgMean), 2) AS Result FROM ";
			query += "tblAvgMean INNER JOIN Competency ON ";
			query += "tblAvgMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query += "tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query += "[User] ON [User].PKUser = tblAvgMean.TargetLoginID ";
			query += "WHERE tblAvgMean.SurveyID = " + surveyID;
			query += " AND tblAvgMean.Type = 4 AND tblRatingTask.RatingCode = 'CP' AND ";
			query += "tblAvgMean.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment INNER JOIN ";
			query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
			query += "WHERE SurveyID = " + surveyID
					+ " AND Competency.PKCompetency = " + iFKComp
					+ " AND RaterCode = 'SELF' AND ";
			query += "RaterStatus IN (1, 2, 4) ";

			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;

			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;

			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;

			query += ") ";

			query += " GROUP BY Competency.PKCompetency, Competency.CompetencyName ";
			query += "ORDER BY Competency.CompetencyName";

		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				dScore = rs.getDouble("Result");
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getCPSelf - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return dScore;
	}

	/**
	 * get CP score for self
	 * 
	 * @param iFKComp
	 * @return
	 * @throws SQLException
	 * @see generateChart
	 */
	public double getCPNonSelf(int iFKComp, String raterCode)
			throws SQLException {
		String query = "";

		int reliabilityCheck = C.ReliabilityCheck(surveyID);

		double dScore = -1;
		int tblAvgMeanType = 0;
		String addTypeZero = "";
		if (raterCode.equalsIgnoreCase("%SUP%")) {
			tblAvgMeanType = 2;
		} else if (raterCode.equalsIgnoreCase("SELF")) {
			tblAvgMeanType = 4;
		} else if (raterCode.equalsIgnoreCase("%PEER%")) {
			tblAvgMeanType = 6;
		} else if (raterCode.equalsIgnoreCase("%DIR%")) {
			tblAvgMeanType = 8;

		} else if (raterCode.equalsIgnoreCase("%IDR%")) {
			tblAvgMeanType = 9;

		} else if (raterCode.equalsIgnoreCase("ALL")) {
			tblAvgMeanType = 10;

		}
		if (reliabilityCheck == 0) // trimmed mean
		{
			query = query
					+ "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
			query += "ROUND(AVG(tblTrimmedMean.TrimmedMean), 2) AS Result FROM ";
			query += "tblTrimmedMean INNER JOIN Competency ON ";
			query += "tblTrimmedMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query += "tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query += "[User] ON [User].PKUser = tblTrimmedMean.TargetLoginID ";
			query += "WHERE tblTrimmedMean.SurveyID = " + surveyID;
			query += " AND tblTrimmedMean.Type = " + tblAvgMeanType
					+ " AND tblRatingTask.RatingCode = 'CP' AND ";
			query += "tblTrimmedMean.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment INNER JOIN ";
			query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
			query += "WHERE SurveyID = " + surveyID
					+ " AND Competency.PKCompetency = " + iFKComp
					+ " AND RaterCode LIKE '" + raterCode + "' AND ";
			query += "RaterStatus IN (1, 2, 4) ";

			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;

			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;

			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;

			query += ") ";

			query += " GROUP BY Competency.PKCompetency, Competency.CompetencyName ";
			query += "ORDER BY Competency.CompetencyName";

		} else {
			query = "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
			query += "ROUND(AVG(tblAvgMean.AvgMean), 2) AS Result FROM ";
			query += "tblAvgMean INNER JOIN Competency ON ";
			query += "tblAvgMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query += "tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
			query += "[User] ON [User].PKUser = tblAvgMean.TargetLoginID ";
			query += "WHERE tblAvgMean.SurveyID = " + surveyID;
			query += " AND tblAvgMean.Type = " + tblAvgMeanType
					+ " AND tblRatingTask.RatingCode = 'CP' AND ";
			query += "tblAvgMean.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment INNER JOIN ";
			query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
			query += "WHERE SurveyID = " + surveyID
					+ " AND Competency.PKCompetency = " + iFKComp
					+ " AND RaterCode LIKE '" + raterCode + "' AND ";
			query += "RaterStatus IN (1, 2, 4) ";

			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;

			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;

			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;

			query += ") ";

			query += " GROUP BY Competency.PKCompetency, Competency.CompetencyName ";
			query += "ORDER BY Competency.CompetencyName";

		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		// System.out.println("QUERY:\n"+query);
		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				dScore = Math.round(rs.getDouble("Result")*100)/100.00;
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getCPSelf - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return dScore;
	}

	/**
	 * Retrieves the KB results under that particular rating code
	 * 
	 * @param String
	 *            RTCode Rating Task Code
	 * @param iKBID
	 * 
	 * @return CP/CPR score
	 * @see generateChart()
	 */
	public double getKBCPCPR(int iCompID, int iKBID, String RTCode)
			throws SQLException {
		String query = "";
		int type = 1;
		if (reqWeightedAverage == true) {
			type = 10;
		}
		double dScore = -1;

		query = "SELECT ROUND(AVG(tblAvgMean.AvgMean), 2) AS Result, competencyID FROM ";
		query += "tblAvgMean INNER JOIN ";
		query += "tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
		query += "[User] ON [User].PKUser = tblAvgMean.TargetLoginID ";
		query += "WHERE tblAvgMean.SurveyID = " + surveyID;
		query += " AND CompetencyID = " + iCompID + " AND KeyBehaviourID = "
				+ iKBID
				+ " AND tblAvgMean.Type = " + type + " AND tblRatingTask.RatingCode = '"
				+ RTCode + "' AND ";
		query += "tblAvgMean.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment INNER JOIN ";
		query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
		query += "WHERE SurveyID = " + surveyID
				+ " AND RaterCode <> 'SELF' AND ";
		query += "RaterStatus IN (1,2, 4) ";
		/*
		 * Change(s) : Check if the report to be generated is a subgroup report
		 * Reason(s) : To cater to subgroup report where only certain users are
		 * selected Updated By: Gwen Oh Updated On: 13 Oct 2011
		 */
		if (selectedUsers != null) {
			query += "AND (";
			for (int i = 0; i < selectedUsers.length; i++) {
				query += "TargetLoginID = " + selectedUsers[i];
				if (i != (selectedUsers.length - 1))
					query += " OR ";
			}
			query += "))";
		} else {
			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;

			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;

			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;

			query += ") ";
		}
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				dScore = Math.round(rs.getDouble("Result")*100)/100.00;
			}
		

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getKBCPCPR - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return dScore;
	}

	/**
	 * Retrieve the value for CP Self at KB Level
	 * 
	 * @param iKBID
	 * @return CP Score at KB Level
	 * @throws SQLException
	 * @see generateChart()
	 */
	public double getKBCPSelf(int iCompID, int iKBID) throws SQLException {
		String query = "";
		int type = 1;
		if (reqWeightedAverage == true) {
			type = 10;
		}
		double dScore = -1;

		query = "SELECT ROUND(AVG(tblAvgMean.AvgMean), 2) AS Result FROM ";
		query += "tblAvgMean INNER JOIN ";
		query += "tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
		query += "[User] ON [User].PKUser = tblAvgMean.TargetLoginID ";
		query += "WHERE tblAvgMean.SurveyID = " + surveyID;
		query += " AND CompetencyID = "
				+ iCompID
				+ " AND KeyBehaviourID = "
				+ iKBID
				+ " AND tblAvgMean.Type = 4 AND tblRatingTask.RatingCode = 'CP' AND ";
		query += "tblAvgMean.TargetLoginID IN (SELECT DISTINCT TargetLoginID FROM tblAssignment INNER JOIN ";
		query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
		query += "WHERE SurveyID = " + surveyID
				+ " AND RaterCode = 'SELF' AND ";
		query += "RaterStatus IN (1, 2, 4) ";
		/*
		 * Change(s) : Check if the report to be generated is a subgroup report
		 * Reason(s) : To cater to subgroup report where only certain users are
		 * selected Updated By: Gwen Oh Updated On: 13 Oct 2011
		 */
		if (selectedUsers != null) {
			query += "AND (";
			for (int i = 0; i < selectedUsers.length; i++) {
				query += "TargetLoginID = " + selectedUsers[i];
				if (i != (selectedUsers.length - 1))
					query += " OR ";
			}
			query += "))";
		} else {
			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;

			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;

			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;

			query += ") ";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				dScore = rs.getDouble("Result");
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getKBCPSelf - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return dScore;
	}

	/**
	 * Count total raters in that particular group.
	 * 
	 * @param int group GroupID (1=ALL, 2=SUP, 3=OTH, 4=SELF)
	 * 
	 * @return int TotalRaters Total no. of raters
	 */
	public int getTotalRaters(int group) throws SQLException {
		String query = "";
		String filter = "";
		int total = 0;

		switch (group) { // 1 for all, 4 for self
			case 1 :
				filter = "tblAssignment.RaterCode <> 'SELF'";
				break;
			case 2 :
				filter = "tblAssignment.RaterCode LIKE 'SUP%'";
				break;
			case 3 :
				filter = "tblAssignment.RaterCode LIKE 'OTH%'";
				break;
			case 4 :
				filter = "tblAssignment.RaterCode = 'SELF'";
				break;
		}

		query = "SELECT COUNT(tblAssignment.RaterLoginID) AS Total FROM ";
		query = query + "tblAssignment INNER JOIN [User] ON ";
		query = query + "tblAssignment.TargetLoginID = [User].PKUser ";
		query = query + "WHERE tblAssignment.SurveyID = " + surveyID + " AND ";
		query = query + "tblAssignment.RaterStatus IN (1, 2, 4)";
		query = query + " AND " + filter;

		if (divID != 0)
			query = query + " AND tblAssignment.FKTargetDivision = " + divID;

		if (deptID != 0)
			query = query + " AND tblAssignment.FKTargetDepartment = " + deptID;

		if (groupSection != 0)
			query = query + " AND tblAssignment.FKTargetGroup = "
					+ groupSection;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				total = rs.getInt(1);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTotalRaters - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return total;

	}

	/**
	 * Add by Santoso 2008/10/29 Count total rater for the particular survey for
	 * competency level
	 */
	public int totalRater(String raterCode, int iRatingTaskID,
			int iCompetencyID, int KBID) throws SQLException {
		int total = 0;
		SurveyResult SR = new SurveyResult();
		Calculation cal = new Calculation();
		String query = "select count(*) AS Total ";
		query = query + " From( ";

		if (KBID != 0) {
			// For KB Level surveys
			query = query
					+ " SELECT DISTINCT tblAssignment.RaterCode, tblAssignment.TargetLoginID";
			query = query + " FROM         tblAssignment INNER JOIN ";
			query = query
					+ " tblResultBehaviour ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID INNER JOIN ";
			query = query
					+ " KeyBehaviour ON tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query
					+ "INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
			query = query + " WHERE (tblAssignment.SurveyID =  " + surveyID
					+ ") ";
			/*
			 * Change(s) : Check if the report to be generated is a subgroup
			 * report Reason(s) : Only need to get raters for selected target
			 * users Updated By: Gwen Oh Updated On: 13 Oct 2011
			 */
			if (selectedUsers != null) {
				query += "AND (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "tblAssignment.TargetLoginID = "
							+ selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += ")";
			}

			if (cal.NAIncluded(surveyID) == 0) {
				query = query + " AND RaterCode LIKE '" + raterCode
						+ "' and RaterStatus in(1,2,4)";
				query = query + " AND (tblResultBehaviour.Result <> 0)";
			} else
				query = query + " AND RaterCode LIKE '" + raterCode
						+ "' and RaterStatus in(1,2,4,5)";

			// If it is a subgroup report, selected users are already filtered
			// by grp, divison and dept
			if (selectedUsers == null) {
				if (divID != 0)
					query = query + " AND tblAssignment.FKTargetDivision = "
							+ divID;

				if (deptID != 0)
					query = query + " AND tblAssignment.FKTargetDepartment = "
							+ deptID;

				if (groupSection != 0)
					query = query + " AND tblAssignment.FKTargetGroup = "
							+ groupSection;

			}
			query = query + " AND (tblResultBehaviour.RatingTaskID = "
					+ iRatingTaskID + ") ";

			// Skip this if KBID is -1 which indicates we are calculating total
			// raters for Competency bar charts of a KB Level Survey, Desmond 17
			// Nov 09
			if (KBID != -1) {
				query = query + " AND (KeyBehaviour.PKKeyBehaviour = " + KBID
						+ ") ";
			}

		} else {

			// Competency Level
			query = query
					+ " SELECT DISTINCT tblAssignment.RaterCode, tblAssignment.TargetLoginID";
			query = query + " FROM tblAssignment INNER JOIN ";
			query = query
					+ " tblResultCompetency ON tblAssignment.AssignmentID = tblResultCompetency.AssignmentID INNER JOIN ";
			query = query
					+ " [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
			query = query + " WHERE (tblAssignment.SurveyID =  " + surveyID
					+ ") ";
			/*
			 * Change(s) : Check if the report to be generated is a subgroup
			 * report Reason(s) : Only need to get raters for selected target
			 * users Updated By: Gwen Oh Updated On: 13 Oct 2011
			 */
			if (selectedUsers != null) {
				query += "AND (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "tblAssignment.TargetLoginID = "
							+ selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += ")";
			}
			query = query + " AND RaterCode LIKE '" + raterCode + "'";
			if (cal.NAIncluded(surveyID) == 0) {
				query = query + " AND RaterStatus in(1,2,4)";
				query = query + " AND (tblResultCompetency.Result <> 0)";
			} else {
				query = query + " AND RaterStatus in(1,2,4,5)";
			}
			// If it is a subgroup report, selected users are already filtered
			// by grp, divison and dept
			if (selectedUsers == null) {
				if (divID != 0)
					query = query + " AND tblAssignment.FKTargetDivision = "
							+ divID;

				if (deptID != 0)
					query = query + " AND tblAssignment.FKTargetDepartment = "
							+ deptID;

				if (groupSection != 0)
					query = query + " AND tblAssignment.FKTargetGroup = "
							+ groupSection;
			}

			query = query + " AND (tblResultCompetency.RatingTaskID = "
					+ iRatingTaskID + ") ";
			query = query + " AND (tblResultCompetency.CompetencyID = "
					+ iCompetencyID + ") ";
		}
		query = query + "  ) table1 ";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				total = rs.getInt(1);

		} catch (Exception ex) {
			System.out.println("IndividualReport.java - totalRater - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		return total;

	}

	/**
	 * Returns total number of raters specified in the raterType This method is
	 * a fix for getTotalRaters (table tblResultBehaviour is used in the query)
	 * 
	 * @param sRaterType
	 * @param iRatingTaskID
	 * @param iCompetencyID
	 * @return
	 * @throws SQLException
	 */
	public int getTotalRaters1(String sRaterType, int iRatingTaskID,
			int iCompetencyID) throws SQLException {
		StringBuilder query = new StringBuilder();
		int total = 0;
		Calculation cal = new Calculation();
		query.append("select max(table1.Cnt)AS Total  From( ")
				.append("SELECT COUNT(tblAssignment.RaterCode) AS Cnt,tblResultBehaviour.KeyBehaviourID ")
				.append("FROM tblAssignment INNER JOIN  tblResultBehaviour ")
				.append("ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID ")
				.append("INNER JOIN  KeyBehaviour ON tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ")
				.append("INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ")
				.append("WHERE (tblAssignment.SurveyID =").append(surveyID)
				.append(") ").append("AND RaterCode LIKE '").append(sRaterType)
				.append("' ");
		if (divID != 0)
			query.append(" AND tblAssignment.FKTargetDivision = ")
					.append(divID);

		if (deptID != 0)
			query.append(" AND tblAssignment.FKTargetDepartment = ").append(
					deptID);

		if (groupSection != 0)
			query.append(" AND tblAssignment.FKTargetGroup = ").append(
					groupSection);

		if (cal.NAIncluded(surveyID) == 0) {
			query.append(" AND tblAssignment.RaterStatus IN (1,2,4) ");
			query.append(" AND (tblResultBehaviour.Result <> 0) ");
		} else {
			query.append(" AND tblAssignment.RaterStatus IN (1,2,4,5) ");
		}
		query.append(" AND (tblResultBehaviour.RatingTaskID = ")
				.append(iRatingTaskID)
				.append(") ")
				.append("and (KeyBehaviour.FKCompetency = ")
				.append(iCompetencyID)
				.append(") ")
				.append("group by tblResultBehaviour.KeyBehaviourID   ) table1");

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query.toString());

			if (rs.next()) {
				total = rs.getInt(1);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTotalRaters - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		// System.out.println(query);
		return total;
	}

	/**
	 * get the total number of raters. specify the rater type in the parameter
	 * 
	 * @param RaterType
	 * @return
	 * @throws SQLException
	 * @see printCompetency edit by Ha 07/07/08 add Rating task and CompetencyId
	 *      to method signature To calculate total raters for each rating task
	 *      of each Competency
	 */
	public int getTotalRaters(String sRaterType, int iRatingTaskID,
			int iCompetencyID) throws SQLException {
		String query = "";
		int total = 0;
		Calculation cal = new Calculation();
		// Query changed by HA 07/07/08 so the total rater can be calculated
		// correctly
		//
		query = query
				+ "SELECT COUNT(tblAssignment.RaterCode) AS Total FROM tblAssignment INNER JOIN ";
		query = query
				+ "[User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query = query
				+ " INNER JOIN tblResultCompetency ON tblAssignment.AssignmentID = tblResultCompetency.AssignmentID ";

		query = query + "WHERE tblAssignment.SurveyID = " + surveyID;

		if (divID != 0)
			query = query + " AND tblAssignment.FKTargetDivision = " + divID;

		if (deptID != 0)
			query = query + " AND tblAssignment.FKTargetDepartment = " + deptID;

		if (groupSection != 0)
			query = query + " AND tblAssignment.FKTargetGroup = "
					+ groupSection;

		query = query + " AND tblAssignment.RaterCode LIKE '" + sRaterType
				+ "'";
		if (cal.NAIncluded(surveyID) == 0) {
			query = query + " AND tblAssignment.RaterStatus IN (1,2,4)";
			query = query + " AND tblResultCompetency.Result <> 0 ";
		} else {
			query = query + " AND tblAssignment.RaterStatus IN (1,2,4,5)";
		}
		query = query + " AND tblResultCompetency.RatingTaskID = "
				+ iRatingTaskID;
		query = query + " AND tblResultCompetency.CompetencyID = "
				+ iCompetencyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				total = rs.getInt(1);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTotalRaters - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		// System.out.println(query);
		return total;
	}

	/**
	 * Count total other rating tasks besides CP for the particular survey
	 * 
	 * @return int total Total no. of RatingCode in tblRatingTask
	 * @see printCompetency
	 */
	public int getTotalOtherRaters() throws SQLException {
		String query = "";
		int total = 0;

		query = query
				+ "SELECT COUNT(distinct(tblRatingTask.RatingCode)) AS TotalRT ";
		query = query + "FROM tblSurveyRating INNER JOIN tblRatingTask ON ";
		query = query
				+ "tblSurveyRating.RatingTaskID = tblRatingTask.RatingTaskID ";
		query = query + "WHERE tblSurveyRating.SurveyID = " + surveyID;
		query = query
				+ " AND (tblRatingTask.RatingCode = 'CPR' or tblRatingTask.RatingCode = 'FPR')";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				total = rs.getInt(1);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTotalOtherRaters - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return total;
	}

	/**
	 * Get the maximum rating scale for alignment purposes.
	 * 
	 * @return int MaxScale
	 * @throws SQLException
	 */
	public int getMaxScale() throws SQLException {
		String query = "";
		int total = 0;

		query = query + "SELECT MAX(tblScale.ScaleRange) AS Result FROM ";
		query = query + "tblScale INNER JOIN tblSurveyRating ON ";
		query = query + "tblScale.ScaleID = tblSurveyRating.ScaleID WHERE ";
		query = query + "tblSurveyRating.SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				total = rs.getInt(1);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getMaxScale - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return total;
	}

	public voImportance getImportance(int compID, int KBID) throws SQLException {
		voImportance vo = new voImportance();

		String query = "";

		int reliabilityCheck = C.ReliabilityCheck(surveyID);

		if (reliabilityCheck == 0 && KBID == 0) // trimmed mean
		{
			query = query
					+ "SELECT ROUND(AVG(tblTrimmedMean.TrimmedMean), 2) AS Result, RatingTask ";
			query = query + "FROM tblTrimmedMean INNER JOIN ";
			query = query
					+ "tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "WHERE tblTrimmedMean.CompetencyID = " + compID;
			// Added by Ha 21/07/08 TrimmMean.Type = 1 to calculate the average
			// CI in the group report correctly
			// Problem with old query: calculate the average mean of CI using
			// wrong value
			query = query
					+ " AND (tblRatingTask.RatingCode = 'IF' or tblRatingTask.RatingCode = 'IN') AND (tblTrimmedMean.SurveyID = "
					+ surveyID
					+ ") AND tblTrimmedMean.Type = 1 AND (tblTrimmedMean.TargetLoginID IN ";
			query = query
					+ "(SELECT TargetLoginID FROM tblAssignment INNER JOIN ";
			query = query
					+ "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
			query = query + "WHERE SurveyID = " + surveyID
					+ " AND RaterCode <> 'SELF' AND RaterStatus IN (1, 2, 4) ";

			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;

			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;

			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;

			query += "))  Group BY tblRatingTask.RatingTask";
		} else {

			query = query
					+ "SELECT ROUND(AVG(tblAvgMean.AvgMean),2) AS Result, RatingTask ";
			query = query + "FROM tblAvgMean INNER JOIN ";
			query = query
					+ "tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "WHERE tblAvgMean.CompetencyID = " + compID;

			if (KBID != 0)
				query = query + " AND tblAvgMean.KeyBehaviourID = " + KBID;
			// Added by Ha 21/07/08 tblAvgMean.Type = 1 to calculate the average
			// IN of group correctly
			query = query
					+ " AND (tblRatingTask.RatingCode = 'IF' or tblRatingTask.RatingCode = 'IN') AND (tblAvgMean.SurveyID = "
					+ surveyID
					+ ") AND tblAvgMean.Type = 1 AND (tblAvgMean.TargetLoginID IN ";
			query = query
					+ "(SELECT TargetLoginID FROM tblAssignment INNER JOIN ";
			query = query
					+ "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
			query = query + "WHERE SurveyID = " + surveyID
					+ " AND RaterCode <> 'SELF' AND RaterStatus IN (1, 2, 4) ";

			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;

			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;

			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;

			query += "))  Group BY tblRatingTask.RatingTask";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				// Added Rounding by Ha 07/07/08, sometimes the Round in SQL
				// does not work
				vo.setRatingTask(rs.getString("RatingTask"));
				double result = rs.getDouble("Result");
				BigDecimal bd = new BigDecimal(result);
				bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
				result = bd.doubleValue();
				vo.setResult(result);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getImportance - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return vo;
	}

	/**
	 * Retrieves the group level of agreement from tblLevelOfAgreement based on
	 * Competency and Key Behaviour. KBID = 0 if it is Competency Level
	 * Analysis. type = 0 Competency Level type = 1 KB Level
	 * 
	 * @param int compID CompetencyID
	 * @param int KBID KeyBehaviourID
	 * 
	 * @return double LevelofAgreement
	 * 
	 * @see getLOABase(int iNoOfRaters)
	 * @see MaxScale(int surveyID)
	 */
	/*
	 * public double LevelOfAgreement(int compID, int KBID) throws SQLException,
	 * Exception { String query = "";
	 * 
	 * //double LOA = 0;
	 * 
	 * double LOA = 100; //Default 100%
	 * 
	 * 
	 * query = query +
	 * "SELECT ROUND(AVG(tblLevelOfAgreement.LevelOfAgreement), 2) AS LOA ";
	 * query = query + "FROM tblLevelOfAgreement INNER JOIN "; query = query +
	 * "tblRatingTask ON tblLevelOfAgreement.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN "
	 * ; query = query +
	 * "[User] ON [User].PKUser = tblLevelOfAgreement.TargetLoginID "; query =
	 * query + " WHERE tblLevelOfAgreement.CompetencyID = " + compID;
	 * 
	 * if(KBID != 0) query = query +
	 * " AND tblLevelOfAgreement.KeyBehaviourID = " + KBID;
	 * 
	 * query = query +
	 * " AND (tblRatingTask.RatingCode = 'CP') AND (tblLevelOfAgreement.SurveyID = "
	 * +surveyID+") AND (tblLevelOfAgreement.TargetLoginID IN "; query = query +
	 * "(SELECT TargetLoginID FROM tblAssignment INNER JOIN "; query = query +
	 * "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID "; query = query +
	 * "WHERE SurveyID = " +surveyID+
	 * " AND RaterCode <> 'SELF' AND RaterStatus IN (1, 2, 4) ";
	 * 
	 * 
	 * if(divID != 0) query = query + " AND tblAssignment.FKTargetDivision = " +
	 * divID;
	 * 
	 * if(deptID != 0) query = query +
	 * " AND tblAssignment.FKTargetDepartment = " + deptID;
	 * 
	 * if(groupSection != 0) query = query +
	 * " AND tblAssignment.FKTargetGroup = " + groupSection;
	 * 
	 * query += "))";
	 * 
	 * if(db.con == null) db.openDB();
	 * 
	 * Statement stmt = db.con.createStatement(); ResultSet rs =
	 * stmt.executeQuery(query);
	 * 
	 * if(rs.next()) LOA = rs.getDouble("LOA");
	 * 
	 * 
	 * rs.close(); stmt.close(); return LOA; }
	 */

	/**
	 * Retrieves the group level of agreement from tblLevelOfAgreement based on
	 * Competency and Key Behaviour. KBID = 0 if it is Competency Level
	 * Analysis.
	 * 
	 * @param int compID CompetencyID
	 * @param int KBID KeyBehaviourID
	 * 
	 * @return double LevelofAgreement
	 * 
	 * @see getLOABase(int iNoOfRaters)
	 * @see MaxScale(int surveyID)
	 * @see stdDev(Vector v)
	 * 
	 *      Modified by Jenty 20 Oct 06 to fix the LOA for KB LOA for group must
	 *      be the average of the LOA for each individual, cannot take from
	 *      raters' input
	 */
	public double LevelOfAgreement(int compID, int KBID) throws SQLException,
			Exception {

		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int type = 1;
		if (reqWeightedAverage == true)
			type = 10;
		/*
		 * Change(s) : Change queries to retrieve set of CP(All) scores without
		 * performing any calculations; Created a new java method, stdDev(), for
		 * use with formula for calculating LOA; Reorganized codes so that its
		 * more logically structured Reason(s) : Change in methodology for
		 * calculating group LOA Updated By: Desmond Updated On: 13 Jan 2010
		 * Previous Update: Denise 21/12/2009, for surveyLevel == 1, the
		 * competency graph is drawn as when surveyLevel = 0
		 */

		// Get the total number of Targets in this survey
		int iNoOfTargets = 0;
		if (selectedUsers != null)
			iNoOfTargets = selectedUsers.length;
		else
			iNoOfTargets = assign.getTotTargetsBySurveyID(surveyID);

		// Update the base use for calculation according to number of targets
		// instead of raters
		int iBase = C.getLOABase(iNoOfTargets);
		// System.out.println("Total Number of Targets = " + iNoOfTargets +
		// ", iBase = " + iBase + ", Level = " + surveyLevel );

		// Find out how many point scale are the ratings for this survey
		int iMaxScale = rscale.getMaxScale(surveyID);

		double LOA = 100; // Default 100%

		if (surveyLevel == 0) {
			// For Competency Level Survey - Only Competency Graphs are
			// displayed
			query = query + "SELECT AvgMean as Result";
			query = query + " FROM [tblAvgMean]";
			query = query + " WHERE SurveyID = " + surveyID
					+ " AND RatingTaskID = 1 AND Type = " + type;
			query = query + " AND CompetencyID = " + compID;

		} // End of Competency Level Survey
		else if (surveyLevel == 1 && KBID == -1) {
			// For Competency Level Survey and Competency Level Graph of a KB
			// Level Survey
			query = query
					+ "SELECT cast(AVG(AvgMean) as numeric(38,2)) as Result";
			query = query + " FROM [tblAvgMean]";
			query = query + " WHERE SurveyID = " + surveyID
					+ " AND RatingTaskID = 1 AND Type = " + type;
			query = query + " AND CompetencyID = " + compID;

		} // End of Competency Level Graph of a KB Level Survey
		else {
			// For KB Level Survey - KB Level Graphs

			query = query + "SELECT AvgMean as Result";
			query = query + " FROM [tblAvgMean]";
			query = query + " WHERE SurveyID = " + surveyID
					+ " AND RatingTaskID = 1 AND Type = " + type;
			query = query + " AND CompetencyID = " + compID
					+ " AND KeyBehaviourID = " + KBID;

		} // End KB Level Graph for KB Level Survey

		/*
		 * Change(s) : Check if the report to be generated is a subgroup report
		 * Reason(s) : Only need to get the avgMean result for selected users
		 * Updated By: Gwen Oh Updated On: 13 Oct 2011
		 */
		if (selectedUsers != null) {
			query += " AND (";
			for (int i = 0; i < selectedUsers.length; i++) {
				query += "TargetLoginID = " + selectedUsers[i];
				if (i != (selectedUsers.length - 1))
					query += " OR ";
			}
			query += ")";
		}

		if (surveyLevel == 1 && KBID == -1) {
			query = query + " Group By TargetLoginID";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			Vector v = new Vector(); // Vector for storing data, Desmond 13 Jan
										// 2010

			// Filter if more than 1 rater, get LOA from calculation, else if 1
			// Rater, LOA = 100%
			if (iNoOfTargets > 1) {

				// Used for rounding purposes, indicates keeping 2 decimal
				// places, Desmond 13 Jan 2010
				int precision = 100;

				// Retrieve all the values and store them as Doubles in a vector
				while (rs.next()) {
					v.add(Double.valueOf(rs.getDouble("Result")));
				} // End while

				// Run these codes only if there are results
				if (v.size() > 0) {

					// Calculate LOA
					LOA = 100 - (((this.stdDev(v) * 10) / iMaxScale) * iBase);

					// Round LOA off to 2 decimal places
					LOA = Math.floor(LOA * precision + .5) / precision;

				} // End if

			} // End if

		} catch (Exception ex) {
			System.out.println("GroupReport.java - LevelOfAgreement - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return LOA;

	} // End of LevelOfAgreement()

	/**
	 * Calculate the average of group level of agreement for each competency.
	 * This is only apply for KB Level Analysis.
	 * 
	 * @param compID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	/*
	 * public double AvgLevelOfAgreement(int compID) throws SQLException,
	 * Exception { String query = ""; //double LOA = -1; //Get No of Raters int
	 * iNoOfRaters = getTotRatersCompleted(surveyID, groupSection, deptID,
	 * divID); int iBase = C.getLOABase(iNoOfRaters); double LOA = 100;
	 * //Default 100%
	 * 
	 * int iMaxScale = rscale.getMaxScale(surveyID); //Get Maximum Scale of this
	 * survey
	 * 
	 * //commented off by yuni query = query +
	 * "SELECT tblResultBehaviour.RatingTaskID, KeyBehaviour.FKCompetency, ";
	 * query = query + "CAST(100 - (STDEV(tblResultBehaviour.Result * 10 / " +
	 * iMaxScale + ") * " + iBase + ") AS numeric(38, 2)) AS LOA "; query =
	 * query + "FROM tblAssignment INNER JOIN tblResultBehaviour ON "; query =
	 * query +
	 * "tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID INNER JOIN "
	 * ; query = query +
	 * "tblRatingTask ON tblResultBehaviour.RatingTaskID = tblRatingTask.RatingTaskID "
	 * ; query = query + "INNER JOIN KeyBehaviour ON "; query = query +
	 * "tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour "; query
	 * = query +
	 * "INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
	 * query = query + "WHERE tblAssignment.SurveyID = " + surveyID + " AND ";
	 * query = query +
	 * "tblAssignment.RaterStatus IN (1, 2, 4) AND KeyBehaviour.FKCompetency = "
	 * + compID; query = query +
	 * " AND tblAssignment.RaterCode <> 'SELF' AND tblRatingTask.RatingCode = 'CP'"
	 * ;
	 * 
	 * 
	 * if(divID != 0) query = query + " AND tblAssignment.FKTargetDivision = " +
	 * divID; //query = query + " AND [User].FKDivision = " + divID; if(deptID
	 * != 0) query = query + " AND tblAssignment.FKTargetDepartment = " +
	 * deptID; //query = query + " AND [User].FKDepartment = " + deptID;
	 * if(groupSection != 0) query = query +
	 * " AND tblAssignment.FKTargetGroup = " + groupSection; //query = query +
	 * " AND [User].Group_Section = " + groupSection;
	 * 
	 * 
	 * query = query +
	 * " GROUP BY tblResultBehaviour.RatingTaskID, KeyBehaviour.FKCompetency";
	 * 
	 * if(db.con == null) db.openDB(); Statement stmt =
	 * db.con.createStatement(); ResultSet rs = stmt.executeQuery(query);
	 * 
	 * //Filter if more than 1 rater, get LOA from calculation, else if 1 Rater,
	 * LOA = 100% if(iNoOfRaters > 1) if(rs.next()) LOA = rs.getDouble("LOA");
	 * 
	 * return LOA; }
	 */

	public double AvgLevelOfAgreement(int compID) throws SQLException,
			Exception {
		// Change by Santoso (2008-10-28)
		// no need to initialize query here
		// String query = "";
		// double LOA = -1;
		// Get No of Raters
		int iNoOfRaters = getTotRatersCompleted(surveyID,groupSection,deptID,
				divID);
		int iBase = C.getLOABase(iNoOfRaters);
		double avg = 100;
		double LOA = 0; // Default 100%

		int iMaxScale = rscale.getMaxScale(surveyID); // Get Maximum Scale of
														// this survey

		// Change by Santoso (2008-10-28)
		// Update query similar to the one in individual report
		String query;
		query = "SELECT tblAvgMeanByRater.RatingTaskID, tblAvgMeanByRater.CompetencyID, tblAssignment.TargetLoginID, count(*) as numOfRaters, ";
		query = query + "stDev(tblAvgMeanByRater.AvgMean * 10 / " + iMaxScale
				+ ") AS LOA ";
		query = query + "FROM tblAssignment INNER JOIN ";
		query = query
				+ "tblAvgMeanByRater ON tblAssignment.AssignmentID = tblAvgMeanByRater.AssignmentID INNER JOIN ";
		query = query
				+ "tblRatingTask ON tblAvgMeanByRater.RatingTaskID = tblRatingTask.RatingTaskID ";
		query += " INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query = query + "WHERE tblAssignment.SurveyID = " + surveyID + " AND ";
		query = query
				+ "tblAssignment.RaterStatus IN (1, 2, 4) AND tblAvgMeanByRater.CompetencyID = "
				+ compID;
		query = query
				+ " AND tblAssignment.RaterCode <> 'SELF' AND tblRatingTask.RatingCode = 'CP' ";
		/*
		 * Change(s) : Check if the report to be generated is a subgroup report
		 * Reason(s) : To cater to subgroup report where only certain users are
		 * selected Updated By: Gwen Oh Updated On: 13 Oct 2011
		 */
		if (selectedUsers != null) {
			query += "AND ";
			for (int i = 0; i < selectedUsers.length; i++) {
				query += "tblAssignment.TargetLoginID = " + selectedUsers[i];
				if (i != (selectedUsers.length - 1))
					query += " OR ";
			}
			query = query + ") ";
		} else {
			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;
			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;
			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;
		}
		query = query
				+ "GROUP BY tblAvgMeanByRater.RatingTaskID, tblAvgMeanByRater.CompetencyID, tblAssignment.TargetLoginID";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			// Filter if more than 1 rater, get LOA from calculation, else if 1
			// Rater, LOA = 100%
			if (iNoOfRaters > 1) {
				double sum = 0;
				int count = 0;

				while (rs.next()) {
					// Change by Santoso (2008-10-29) : adapt the code from
					// IndividualReport
					LOA = 100 - rs.getDouble("LOA")
							* C.getLOABase(rs.getInt("numOfRaters"));
					BigDecimal bd = new BigDecimal(LOA);
					bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP); // round to 2
																	// decimal
																	// place
					LOA = bd.doubleValue();
					sum = sum + LOA;
					count++;
					// System.out.println(LOA +"--------" +
					// rs.getInt("TargetLoginID"));
				}

				avg = sum / count;
				avg = Math.round(avg * 100) / 100.0;

			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - AvgLevelOfAgreement - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return avg;
	}

	// not in use
	public double AvgLevelOfAgreement(int compID, int noOfRaters)
			throws SQLException {
		String query = "";
		int iBase = C.getLOABase(noOfRaters);
		double LOA = -1;
		int iMaxScale = rscale.getMaxScale(surveyID); // Get Maximum Scale of
														// this survey

		query += "SELECT DISTINCT ";
		query += "tblAssignment.TargetLoginID, tblAvgMeanByRater.RatingTaskID, tblAvgMeanByRater.CompetencyID, ";
		query += "CAST(100 - STDEV(tblAvgMeanByRater.AvgMean * 10 / "
				+ iMaxScale
				+ ") * "
				+ iBase
				+ " AS numeric(38, 2)) AS LOA, [User].LoginName, [User].GivenName ";
		query += "FROM tblAssignment INNER JOIN ";
		query += "tblAvgMeanByRater ON tblAssignment.AssignmentID = tblAvgMeanByRater.AssignmentID INNER JOIN ";
		query += "tblRatingTask ON tblAvgMeanByRater.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
		query += "[User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query += "WHERE (tblAssignment.SurveyID = "
				+ surveyID
				+ ") AND (tblAssignment.RaterStatus IN (1, 2, 4)) AND (tblAvgMeanByRater.CompetencyID = "
				+ compID + ") AND ";
		query += "(tblAssignment.RaterCode <> 'SELF') AND (tblRatingTask.RatingCode = 'CP') ";
		query += "GROUP BY tblAvgMeanByRater.RatingTaskID, tblAvgMeanByRater.CompetencyID, tblAssignment.TargetLoginID, [User].LoginName, [User].GivenName ";

		double sum = 0;
		double avg = 0;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				LOA = rs.getDouble("LOA");
				sum = sum + LOA;
			}

			avg = sum / noOfRaters;

		} catch (Exception ex) {
			System.out.println("GroupReport.java - AvgLevelOfAgreement - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return avg;
	}
	/**
	 * get the total numbers completed for a particular survey
	 * 
	 * @param SurveyID
	 * @param GroupSect
	 * @param DeptID
	 * @param DivID
	 * @return integer total
	 * @throws SQLException
	 * @throws Exception
	 * @see AvgLevelOfAgreement()
	 */
	public int getTotRatersCompleted(int SurveyID, int GroupSect, int DeptID,
			int DivID) throws SQLException, Exception {
		int TotRatersCompleted = 0;

		String command = "SELECT COUNT(*) as TotRatersCompleted FROM tblAssignment ";
		command = command
				+ "INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		command = command + "WHERE (tblAssignment.SurveyID = " + SurveyID
				+ ") AND (tblAssignment.RaterStatus IN (1,2,4)) ";
		command = command + "AND (tblAssignment.RaterCode NOT LIKE '%self%') ";
		/*
		 * Change(s) : Check if the report to be generated is a subgroup report
		 * Reason(s) : Only need to get the raters for selected users Updated
		 * By: Gwen Oh Updated On: 13 Oct 2011
		 */
		if (selectedUsers != null) {
			command += "AND (";
			for (int i = 0; i < selectedUsers.length; i++) {
				command += "TargetLoginID = " + selectedUsers[i];
				if (i != (selectedUsers.length - 1))
					command += " OR ";
			}
			command += ") ";
		} else {
			if (GroupSect != 0)
				command = command + "AND (tblAssignment.FKTargetGroup = '"
						+ GroupSect + "') ";

			if (DeptID != 0)
				command = command + "AND (tblAssignment.FKTargetDepartment = "
						+ DeptID + ") ";

			if (DivID != 0)
				command = command + "AND (tblAssignment.FKTargetDivision = "
						+ DivID + ")";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(command);

			if (rs.next())
				TotRatersCompleted = rs.getInt("TotRatersCompleted");

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTotRatersCompleted - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return TotRatersCompleted;
	}

	/************************* END COMPETENCY *************************************************************/

	/************************** COMMENTS ****************************************************************/
	/**
	 * Write comments on excel.
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 * @see generateReport
	 */
	public void printComments() throws SQLException, IOException, Exception {
		System.out.println("      - Insert Comments");

		// check for the survery level
		int surveyLevel = Integer.parseInt(surveyInfo[0]);

		column = 0;

		OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 2,2,1);
		row++;

		if (ST.LangVer == 1)
			OO.insertString(xSpreadsheet,"Narrative Comments",row,column);
		else if (ST.LangVer == 2)
			OO.insertString(xSpreadsheet,"Komentar Naratif",row,column);
		OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
		row += 2;

		int startBorder = row;
		int endBorder = 1;
		int selfIncluded = Q.SelfCommentIncluded(surveyID);
		int column = 0;

		int count = 0;

		int borderArr[] = new int[2];

		for (int i = 0; i < vCompDetails.size(); i++) {

			voCompetency voComp = (voCompetency) vCompDetails.elementAt(i);
			count++;
			int compID = voComp.getCompetencyID();
			String statement = voComp.getCompetencyName();

			if (i == 0) {
				OO.insertPageBreak(xSpreadsheet,startColumn,endColumn,row - 3);
			}

			// OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+1,
			// 1, 1);
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 5,1,1);

			OO.insertString(xSpreadsheet,Integer.toString(count) + ".",row,
					column);

			OO.insertString(xSpreadsheet,
					UnicodeHelper.getUnicodeStringAmp(statement),row,column + 1);
			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setBGColor(xSpreadsheet,startColumn,endColumn,row,row,BGCOLOR);

			int KBID = 0;

			if (surveyLevel == 0) {

				borderArr = generateComments(compID,KBID,selfIncluded,
						startBorder,endBorder);

				startBorder = borderArr[0];
				endBorder = borderArr[1];

				// Added by Tracy 01 Sep 08
				// Adjust rows in "Narrative comments" part so that this part
				// does not
				// overlap with "Group Distribution report"
				OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 1,1,
						1);
				// End Tracy add 01 Sep 08**

			} else if (surveyLevel == 1) {
				Vector vKBDetails = getKBList(compID);
				row++;

				for (int j = 0; j < vKBDetails.size(); j++) {
					voKeyBehaviour voKB = (voKeyBehaviour) vKBDetails
							.elementAt(j);

					KBID = voKB.getKeyBehaviourID();
					String KB = voKB.getKeyBehaviour();

					OO.insertRows(xSpreadsheet,startColumn,endColumn,row,
							row + 1,1,1);
					OO.insertString(xSpreadsheet,"-",row,column);
					OO.insertString(xSpreadsheet,
							UnicodeHelper.getUnicodeStringAmp(KB),row,
							column + 1);
					OO.mergeCells(xSpreadsheet,column + 1,endColumn,row,row);
					OO.setRowHeight(xSpreadsheet,row,column + 1,
							ROWHEIGHT * OO.countTotalRow(KB,100));
					OO.setCellAllignment(xSpreadsheet,startColumn,startColumn,
							row,row,2,1);

					// int isThai = KB.indexOf("&#");
					borderArr = generateComments(compID,KBID,selfIncluded,
							startBorder,endBorder);
					startBorder = borderArr[0];
					endBorder = borderArr[1];
				}

			}

			/*
			 * if(i == (vCompDetails.size() -1)) {
			 * OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row); }
			 */

		}
		System.out.println("End of inserting comment");
	}

	public int[] generateComments(int compID, int KBID, int selfIncluded,
			int startBorder, int endBorder) throws Exception {

		int start = 0;
		int chartArr[] = {0, 0};
		row++;

		int countRecord = 0;

		/*
		 * =========================SELF==========================================
		 * ============
		 */

		if (start == 0) {
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 1,1,1);

			if (ST.LangVer == 1)
				OO.insertString(xSpreadsheet,"Self",row,column + 1);
			else if (ST.LangVer == 2)
				OO.insertString(xSpreadsheet,"Diri Sendiri",row,column + 1);

			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setFontItalic(xSpreadsheet,startColumn,endColumn,row,row);

			row++;
			start++;
		}

		if (selfIncluded == 1) {
			Vector selfComments = getComments("SELF",compID,KBID);

			if (selfComments != null) {
				for (int i = 0; i < selfComments.size(); i++) {

					String[] arr = (String[]) selfComments.elementAt(i);
					String comment = arr[1].trim();

					OO.insertRows(xSpreadsheet,startColumn,endColumn,row,
							row + 1,1,1);

					/*
					 * String [] comments = comment.split("\n");
					 * 
					 * for(int m=0; m<comments.length; m++) { comments[m] =
					 * comments[m].trim(); if(m!=0) row = row+1;
					 * 
					 * OO.insertString(xSpreadsheet, "- " +
					 * UnicodeHelper.getUnicodeStringAmp(comments[m]), row,
					 * column+1); OO.mergeCells(xSpreadsheet, column+1,
					 * endColumn, row, row); OO.setRowHeight(xSpreadsheet, row,
					 * column+1, ROWHEIGHT*OO.countTotalRow(comment, 100));
					 * 
					 * }
					 */
					OO.insertString(xSpreadsheet,
							"- " + UnicodeHelper.getUnicodeStringAmp(comment),
							row,column + 1);
					OO.mergeCells(xSpreadsheet,column + 1,endColumn,row,row);
					OO.setRowHeight(xSpreadsheet,row,column + 1,
							ROWHEIGHT * OO.countTotalRow(comment,100));

					OO.setCellAllignment(xSpreadsheet,startColumn,startColumn,
							row,row,2,1);

					row++;
					countRecord++;
				}// while(selfComments.next())

			}
		}// if(selfIncluded)*/

		if (countRecord == 0) {
			OO.insertString(xSpreadsheet,"- No comments given",row,column + 1);
			row++;
		}

		start = 0;
		countRecord = 0;

		if (start == 0) {

			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 1,1,1);

			OO.insertString(xSpreadsheet,"Supervisors",row,column + 1);
			OO.mergeCells(xSpreadsheet,column + 1,endColumn,row,row);
			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setFontItalic(xSpreadsheet,startColumn,endColumn,row,row);

			row++;
			start++;
		}

		Vector supComments = getComments("SUP%",compID,KBID);
		Vector othComments = getComments("OTH%",compID,KBID);

		/*
		 * ================================== SUPERVISOR
		 * ========================================
		 */

		if (supComments != null) {

			for (int i = 0; i < supComments.size(); i++) {

				String[] arr = (String[]) supComments.elementAt(i);
				String comment = arr[1].trim();

				OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 1,1,
						1);

				/*
				 * String [] comments = comment.split("\n");
				 * 
				 * for(int m=0; m<comments.length; m++) { comments[m] =
				 * comments[m].trim(); if(m!=0) row = row+1;
				 * 
				 * OO.insertString(xSpreadsheet, "- " +
				 * UnicodeHelper.getUnicodeStringAmp(comments[m]), row,
				 * column+1); OO.mergeCells(xSpreadsheet, column+1, endColumn,
				 * row, row); OO.setRowHeight(xSpreadsheet, row, column+1,
				 * ROWHEIGHT*OO.countTotalRow(comment, 100));
				 * 
				 * }
				 */

				// String comment = selfComments.getString("Comment");

				OO.insertString(xSpreadsheet,
						"- " + UnicodeHelper.getUnicodeStringAmp(comment),row,
						column + 1);
				OO.mergeCells(xSpreadsheet,column + 1,endColumn,row,row);
				OO.setRowHeight(xSpreadsheet,row,column + 1,
						ROWHEIGHT * OO.countTotalRow(comment,100));

				OO.setCellAllignment(xSpreadsheet,startColumn,startColumn,row,
						row,2,1);

				row++;
				countRecord++;

			}

		}

		if (countRecord == 0) {
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 1,1,1);
			OO.insertString(xSpreadsheet,"- No comments given",row,column + 1);
			row++;
		}

		start = 0;
		countRecord = 0;

		/*
		 * ================================== OTHERS
		 * ========================================
		 */

		if (start == 0) {
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 1,1,1);
			if (ST.LangVer == 1)
				OO.insertString(xSpreadsheet,"Others",row,column + 1);
			else if (ST.LangVer == 2)
				OO.insertString(xSpreadsheet,"Lainnya",row,column + 1);

			OO.mergeCells(xSpreadsheet,column + 1,endColumn,row,row);
			OO.setFontBold(xSpreadsheet,startColumn,endColumn,row,row);
			OO.setFontItalic(xSpreadsheet,startColumn,endColumn,row,row);

			start++;
			row++;
		}

		if (othComments != null) {
			for (int i = 0; i < othComments.size(); i++) {

				String[] arr = (String[]) othComments.elementAt(i);
				String comment = arr[1].trim();

				OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 1,1,
						1);
				/*
				 * String [] comments = comment.split("\n");
				 * 
				 * for(int m=0; m<comments.length; m++) { comments[m] =
				 * comments[m].trim(); if(m!=0) row = row+1;
				 * 
				 * OO.insertString(xSpreadsheet, "- " +
				 * UnicodeHelper.getUnicodeStringAmp(comments[m]), row,
				 * column+1); OO.mergeCells(xSpreadsheet, column+1, endColumn,
				 * row, row); OO.setRowHeight(xSpreadsheet, row, column+1,
				 * ROWHEIGHT*OO.countTotalRow(comment, 100));
				 * 
				 * }
				 */

				OO.insertString(xSpreadsheet,
						"- " + UnicodeHelper.getUnicodeStringAmp(comment),row,
						column + 1);
				OO.mergeCells(xSpreadsheet,column + 1,endColumn,row,row);
				OO.setRowHeight(xSpreadsheet,row,column + 1,
						ROWHEIGHT * OO.countTotalRow(comment,100));

				OO.setCellAllignment(xSpreadsheet,startColumn,startColumn,row,
						row,2,1);

				row++;
				countRecord++;

			}

		}

		if (countRecord == 0) {
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 1,1,1);
			OO.insertString(xSpreadsheet,"- No comments given",row,column + 1);
			row++;
		}

		OO.insertRows(xSpreadsheet,startColumn,endColumn,row,row + 1,1,1);
		endBorder = row;
		chartArr[1] = endBorder;

		OO.setTableBorder(xSpreadsheet,startColumn,endColumn,startBorder,
				endBorder,false,false,true,true,true,true);
		row++;
		startBorder = row;

		chartArr[0] = startBorder;

		return chartArr;
	}

	/**
	 * 
	 * Retrieves all the comments input upon fill in the questionnaire
	 * 
	 * @param String
	 *            raterCode
	 * @param int compID CompetencyID
	 * @param int KBID KeyBehaviourID
	 * @return
	 * @throws SQLException
	 * 
	 * @see printComments
	 */
	public Vector getComments(String raterCode, int compID, int KBID)
			throws SQLException {
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);

		if (surveyLevel == 0) {
			query = query
					+ "SELECT Competency.CompetencyName, tblComment.Comment ";
			query = query + "FROM tblAssignment INNER JOIN tblComment ON ";
			query = query
					+ "tblAssignment.AssignmentID = tblComment.AssignmentID INNER JOIN ";
			query = query
					+ "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
			query = query
					+ "Competency ON tblComment.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblAssignment.SurveyID = " + surveyID;
			query = query + " AND tblAssignment.RaterCode LIKE '" + raterCode
					+ "'";

			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;
			// query = query + " AND [User].FKDivision = " + divID;
			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;
			// query = query + " AND [User].FKDepartment = " + deptID;
			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;
			// query = query + " AND [User].Group_Section = " + groupSection;

			query = query + " AND Competency.PKCompetency = " + compID;
			query = query + " ORDER BY tblComment.Comment";
		} else {
			query = query
					+ "SELECT Competency.CompetencyName, tblComment.Comment ";
			query = query + "FROM tblAssignment INNER JOIN tblComment ON ";
			query = query
					+ "tblAssignment.AssignmentID = tblComment.AssignmentID INNER JOIN ";
			query = query
					+ "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
			query = query
					+ "Competency ON tblComment.CompetencyID = Competency.PKCompetency ";
			query = query + "INNER JOIN KeyBehaviour ON ";
			query = query
					+ "tblComment.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query + "WHERE tblAssignment.SurveyID = " + surveyID;
			query = query + " AND tblAssignment.RaterCode LIKE '" + raterCode
					+ "'";

			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;
			// query = query + " AND [User].FKDivision = " + divID;
			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;
			// query = query + " AND [User].FKDepartment = " + deptID;
			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;
			// query = query + " AND [User].Group_Section = " + groupSection;

			query = query + " AND Competency.PKCompetency = " + compID;
			query = query + " AND KeyBehaviour.PKKeyBehaviour = " + KBID;
			query = query + " ORDER BY tblComment.Comment";
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
				String[] arr = new String[2];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				v.add(arr);

			}
		} catch (Exception ex) {
			System.out.println("GroupReport.java - getComments - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/************************** end COMMENTS **************************************************************************/

	/*********************** START COMPETENCY DISTRIBUTION REPORT ***************************************/
	/**
	 * Add by Santoso (2008-10-28)
	 * 
	 * Returns rating scale of the given surveyID
	 */
	private int getRatingScale(int surveyID) {
		String query = "select MAX(ScaleRange) from tblScale INNER JOIN tblSurveyRating "
				+ "ON tblScale.ScaleID=tblSurveyRating.ScaleID "
				+ "WHERE surveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		int result = 10; // default set to 10
		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.err.println("GroupReport.java - getRatingScale - " + e);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		return result;
	}
	/**
	 * Write Group Competency Distribution Report
	 */
	public void printTotalTargets() throws IOException, Exception {
		System.out.println("7. Group Competency Distribution Report");

		// Added by Tracy 01 Sep 08**********************************
		// int [] titleAddress= OO.findString(xSpreadsheet, "<for CP>");
		String RTaskName = "";

		// Get CP Rating from database according to survey ID
		String query = "SELECT b.RatingTask as RTaskName FROM tblSurveyRating a ";
		query += "INNER JOIN tblRatingTask b ON a.RatingTaskID=b.RatingTaskID  WHERE a.SurveyID = "
				+ surveyID + " AND a.RatingTaskID=1";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				RTaskName = rs.getString("RTaskName");
			}

		} catch (Exception E) {
			System.err.println("SurveyResult.java - GroupSection - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		OO.findAndReplace(xSpreadsheet,"<for CP>","for " + RTaskName);
		// System.out.println("RTASK NAME: " + RTaskName);
		// End add by Tracy 01 Sep 08******************************

		int[] address = OO.findString(xSpreadsheet,
				"<Total Feedback Recipients>");

		OO.findAndReplace(xSpreadsheet,"<Total Feedback Recipients>"," ");

		column = address[0];
		row = address[1];

		column = 1;
		int count = 0;

		for (int a = 0; a < vCompDetails.size(); a++) {
			if (count == 0 && a != 0) // Denise 07/01/2009 to insert two row at
										// the beginning of the page
			{
				OO.insertRows(xSpreadsheet,0,10,row,row + 2,2,1);
				row += 2;
			}

			// OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+15,
			// 15, 0);
			OO.insertRows(xSpreadsheet,startColumn,endColumn,row + 1,row + 16,
					15,0);

			int c = 0;

			voCompetency voComp = (voCompetency) vCompDetails.elementAt(a);
			int compID = voComp.getCompetencyID();
			String compName = voComp.getCompetencyName();

			int total = 0;

			// Get total number of Targets in this survey
			// 2 or more targets with same ratings are considered as 1
			total = getTotalAllTargetResults(compID);

			int targets[] = new int[total];
			int result[] = new int[total];

			Vector totalTargets = null;

			// Get the ratings of all the target in this survey
			totalTargets = getAllResults(compID);

			// Count total no of targets for each ratings
			for (int i = 0; i < totalTargets.size(); i++) {
				result[c] = ((Integer) totalTargets.elementAt(i)).intValue();
				targets[c] = totalTargetBasedScore(compID,result[c]);
				c++;
			}

			// Change by Santoso (2008-10-28)
			// Add end of scale if needed
			int ratingScale = getRatingScale(surveyID);
			if (result[c - 1] < ratingScale) {
				int[] temp = new int[total];
				System.arraycopy(targets,0,temp,0,targets.length);
				targets = temp;
				targets[targets.length - 1] = 0;
				temp = new int[total];
				System.arraycopy(result,0,temp,0,result.length);
				result = temp;
				result[result.length - 1] = ratingScale;
			}

			OO.insertString(xSpreadsheet,
					UnicodeHelper.getUnicodeStringAmp(compName),row,column);
			OO.setFontBold(xSpreadsheet,column,column,row,row);
			OO.setFontSize(xSpreadsheet,column,column,row,row,12);
			row = row + 2;

			long now = System.currentTimeMillis();
			drawTotalTargets(result,targets);
			long then = System.currentTimeMillis();

			// System.out.println("Comp Report : " + (then-now)/1000);

			count++;

			row = row + 13;

			if (count == 2 && a != (vCompDetails.size() - 1)) {
				count = 0;
				// Insert page break each time after printed 2 charts
				OO.insertPageBreak(xSpreadsheet,startColumn,endColumn,row);
			}

		}
		System.out.println("7. End of Group COmpetency Distribution Report");
	}

	/**
	 * Draw line chart for total targets on each rating.
	 */
	public void drawTotalTargets(int result[], int targets[])
			throws IOException, Exception {
		int r = row;
		int c = 2;
		int total = result.length;

		XSpreadsheet xSpreadsheet2 = OO.getSheet(xDoc,"Sheet2");

		// Change by Santoso (2008-11-12)
		// For open office 2.4.1 : reverse the data
		// OO.insertNumeric(xSpreadsheet2, 0, r, c);
		// OO.insertNumeric(xSpreadsheet2, 0, r, c+1);
		// r++;

		int maxTotal = 0;// this is to set the maximum height of Y Axis

		/*
		 * Change(s) : Reverse the order of the elements retrived from the
		 * result and targets arrays Change(s) : Added the codes to check and
		 * plot to graph if there are targets that received rating value NA
		 * Reason(s) : To reverse the X axis order and display the ratings in
		 * the report in the order from 0 to n Updated By: Sebastian Updated On:
		 * 01 July 2010
		 */
		for (int i = 0; i < total; i++) {
			// At the beginning of the record, check if have targets that
			// received rating value NA
			if (i == 0) {
				// if there are targets that received ratings value NA, insert
				// the number of raters that rated NA
				if (result[i] == 0 && targets[i] != 0) {
					OO.insertNumeric(xSpreadsheet2,result[i],r,c);
					OO.insertNumeric(xSpreadsheet2,targets[i],r,c + 1);
				}
				// if don't have targets that received ratings value NA, add
				// default x and y points (0,0)
				else {
					// plot the default rating 0 on graph
					OO.insertNumeric(xSpreadsheet2,0,r,c);
					OO.insertNumeric(xSpreadsheet2,0,r,c + 1);

					r++;

					// plot the current target rating on graph
					OO.insertNumeric(xSpreadsheet2,result[i],r,c);
					OO.insertNumeric(xSpreadsheet2,targets[i],r,c + 1);
				}
			} else {
				OO.insertNumeric(xSpreadsheet2,result[i],r,c);
				OO.insertNumeric(xSpreadsheet2,targets[i],r,c + 1);
			}

			if (maxTotal < targets[i])
				maxTotal = targets[i];

			r++;

		}

		// Change by Santoso (2008-11-12)
		// For open office 2.4.1 : reverse the data
		// OO.insertNumeric(xSpreadsheet2, 0, r, c);
		// OO.insertNumeric(xSpreadsheet2, 0, r, c+1);

		String sXAxis = "Ratings";
		String sYAxis = "Number of Targets";

		if (ST.LangVer == 2) {
			sXAxis = "Nilai";
			sYAxis = "Jumlah Target";
		}

		// draw chart
		// XTableChart xtablechart = OO.getChart(xSpreadsheet, xSpreadsheet2, c,
		// c+1, row-1, r-1, "Distribution"+(row-1), 10000, 7000, row-1, c);
		// Change by Santoso (2008-11-12)
		// For open office 2.4.1 : reverse the data
		XTableChart xtablechart = OO
				.getChart(xSpreadsheet,xSpreadsheet2,c,c + 1,row - 1,r,
						"Distribution" + (row - 1),10000,7000,row - 1,c);
		OO.setFontSize(8);
		xtablechart = OO.setChartTitle(xtablechart,"");
		xtablechart = OO.setAxes(xtablechart,sXAxis,sYAxis,maxTotal,1,0,0,0);

		// OO.setAxes(xtablechart, sXAxis, sYAxis, true, false, false, 0, 0);
		// xtablechart = OO.setAxes(xtablechart, sXAxis, sYAxis, maxTotal, 1,
		// false);
		// OO.setChartProperties(xtablechart, false, true, false, true, true);
		OO.setChartProperties(xtablechart,true,true,true,true,true);
		OO.showLegend(xtablechart,false);

		OO.drawGridLines(xtablechart,0); // draw gridlines for Target graphs,
											// Mark Oei 25 Mar 2010
		// need to change to LineDiagram and set the scale of xAxis, and also
		// the width of the line
		OO.changeChartType("com.sun.star.chart.LineDiagram",xtablechart);
	}

	/**
	 * Retrieves the total number of targets and ratings based on competencyID
	 * 
	 * @param int compID CompetencyID
	 * 
	 * @return int TotalAllTarget
	 */
	public int getTotalAllTargetResults(int compID) throws SQLException {
		int totalTarget = 0;

		String query = "";
		int groupType = 1;
		if (reqWeightedAverage == true) {
			groupType = 10;
		}
		int reliability = C.ReliabilityCheck(surveyID);

		if (reliability == 0) {
			/*
			 * query =
			 * "SELECT COUNT(*) AS Total FROM (SELECT DISTINCT CAST(AVG(tblTrimmedMean.TrimmedMean) AS int) AS Average "
			 * ; query +=
			 * "FROM tblTrimmedMean INNER JOIN tblAssignment ON tblTrimmedMean.TargetLoginID = tblAssignment.TargetLoginID "
			 * ; query +=
			 * "INNER JOIN tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID "
			 * ; query += "WHERE tblTrimmedMean.SurveyID = " + surveyID +
			 * " AND tblTrimmedMean.Type = 1 "; query +=
			 * "AND tblTrimmedMean.CompetencyID = " + compID +
			 * " AND tblAssignment.RaterCode <> 'SELF' AND tblRatingTask.RatingCode = 'CP' "
			 * ; query += " GROUP BY tblTrimmedMean.TargetLoginID ";
			 * 
			 * if(divID != 0) query += ", tblAssignment.FKTargetDivision ";
			 * if(deptID != 0) query += ", tblAssignment.FKTargetDepartment ";
			 * if(groupSection != 0) query += ", tblAssignment.FKTargetGroup ";
			 * 
			 * if(divID != 0 || deptID != 0 || groupSection != 0) query +=
			 * "HAVING (";
			 * 
			 * if(divID != 0) { query += " tblAssignment.FKTargetDivision = " +
			 * divID;
			 * 
			 * if(deptID != 0 || groupSection != 0) query += " AND "; }
			 * if(deptID != 0) { query += " tblAssignment.FKTargetDepartment = "
			 * + deptID;
			 * 
			 * if(groupSection != 0) query += " AND "; } if(groupSection != 0)
			 * query += " tblAssignment.FKTargetGroup = " + groupSection;
			 * 
			 * if(divID != 0 || deptID != 0 || groupSection != 0) query += ")";
			 * query += ") DERIVEDTBL";
			 */
			// Change by Santoso (2008-10-29)
			// add round
			query = "SELECT count(distinct CAST(ROUND(tblTrimmedMean.TrimmedMean, 0) AS int)) AS Total ";
			query += "FROM tblTrimmedMean INNER JOIN ";
			query += "tblAssignment ON tblTrimmedMean.SurveyID = tblAssignment.SurveyID AND ";
			query += "tblTrimmedMean.TargetLoginID = tblAssignment.TargetLoginID ";
			query += "INNER JOIN tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query += "WHERE tblTrimmedMean.SurveyID = " + surveyID;
			query += " AND tblTrimmedMean.Type = 1 AND tblRatingTask.RatingCode = 'CP' AND ";
			query += "tblTrimmedMean.CompetencyID = " + compID
					+ " AND RaterCode <> 'SELF'";
			/*
			 * Change(s) : Check if the report to be generated is a subgroup
			 * report Reason(s) : Only need to get the data for selected users
			 * Updated By: Gwen Oh Updated On: 17 Oct 2011
			 */
			if (selectedUsers != null) {
				query += " AND (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "tblTrimmedMean.TargetLoginID = "
							+ selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += ")";
			}
			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;
			// query = query + " AND [User].FKDivision = " + divID;
			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;
			// query = query + " AND [User].FKDepartment = " + deptID;
			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;
			// query = query + " AND [User].Group_Section = " + groupSection;
		} else {
			// Changed by Ha 09/07/08 from CAST to CAST (ROUND) to round to
			// nearest number
			// Changed by Santoso 2008/10/29, the query is fine but sometimes
			// Use double round to have the correct rounded value because avg
			// might produce value like 9.499999
			query = "SELECT COUNT(*) AS Total FROM (SELECT DISTINCT CAST (ROUND(ROUND(AVG(tblAvgMean.AvgMean),2),0) as int) AS Average ";
			query += "FROM tblAvgMean INNER JOIN tblAssignment ON tblAvgMean.TargetLoginID = tblAssignment.TargetLoginID ";
			query += "INNER JOIN tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query += "WHERE tblAvgMean.SurveyID = " + surveyID
					+ " AND tblAvgMean.Type =  " + groupType;
			query += " AND tblAvgMean.CompetencyID = "
					+ compID
					+ " AND tblAssignment.RaterCode <> 'SELF' AND tblRatingTask.RatingCode = 'CP'";
			/*
			 * Change(s) : Check if the report to be generated is a subgroup
			 * report Reason(s) : Only need to get the data for selected users
			 * Updated By: Gwen Oh Updated On: 17 Oct 2011
			 */
			if (selectedUsers != null) {
				query += " AND (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "tblAvgMean.TargetLoginID = " + selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += ")";
			}
			query += " GROUP BY tblAvgMean.TargetLoginID ";

			if (divID != 0)
				query += ", tblAssignment.FKTargetDivision ";
			if (deptID != 0)
				query += ", tblAssignment.FKTargetDepartment ";
			if (groupSection != 0)
				query += ", tblAssignment.FKTargetGroup ";

			if (divID != 0 || deptID != 0 || groupSection != 0)
				query += "HAVING (";

			if (divID != 0) {
				query += " tblAssignment.FKTargetDivision = " + divID;

				if (deptID != 0 || groupSection != 0)
					query += " AND ";
			}
			if (deptID != 0) {
				query += " tblAssignment.FKTargetDepartment = " + deptID;

				if (groupSection != 0)
					query += " AND ";
			}
			if (groupSection != 0)
				query += " tblAssignment.FKTargetGroup = " + groupSection;

			if (divID != 0 || deptID != 0 || groupSection != 0)
				query += ")";
			query += ") DERIVEDTBL";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				totalTarget = rs.getInt(1);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTotalAllTargetResults - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		return totalTarget;
	}

	/**
	 * Retrieves all distinct result from particular survey and competencyid
	 * 
	 * @param int compID CompetencyID
	 * 
	 * @return ResultSet AllResults
	 */
	public Vector getAllResults(int compID) throws SQLException {
		int reliability = C.ReliabilityCheck(surveyID);
		int groupType = 1;
		if (reqWeightedAverage == true) {
			groupType = 10;
		}
		String query = "";

		if (reliability == 0) {
			/*
			 * query =
			 * "SELECT DISTINCT CAST(AVG(tblTrimmedMean.TrimmedMean) AS int) AS Result FROM tblTrimmedMean "
			 * ; query +=
			 * "INNER JOIN tblAssignment ON tblTrimmedMean.TargetLoginID = tblAssignment.TargetLoginID "
			 * ; query +=
			 * "INNER JOIN tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID "
			 * ; query += "WHERE tblTrimmedMean.SurveyID = " + surveyID +
			 * " AND tblTrimmedMean.Type = 1 "; query +=
			 * "AND tblRatingTask.RatingCode = 'CP' AND tblTrimmedMean.CompetencyID = "
			 * + compID + " AND tblAssignment.RaterCode <> 'SELF'"; query +=
			 * " GROUP BY tblTrimmedMean.CompetencyID, tblTrimmedMean.TargetLoginID "
			 * ;
			 * 
			 * if(divID != 0) query += ", tblAssignment.FKTargetDivision ";
			 * if(deptID != 0) query += ", tblAssignment.FKTargetDepartment ";
			 * if(groupSection != 0) query += ", tblAssignment.FKTargetGroup ";
			 * 
			 * if(divID != 0 || deptID != 0 || groupSection != 0) query +=
			 * "HAVING (";
			 * 
			 * if(divID != 0) { query += " tblAssignment.FKTargetDivision = " +
			 * divID;
			 * 
			 * if(deptID != 0 || groupSection != 0) query += " AND "; }
			 * if(deptID != 0) { query += " tblAssignment.FKTargetDepartment = "
			 * + deptID;
			 * 
			 * if(groupSection != 0) query += " AND "; } if(groupSection != 0)
			 * query += " tblAssignment.FKTargetGroup = " + groupSection;
			 * 
			 * if(divID != 0 || deptID != 0 || groupSection != 0) query += ")";
			 */
			// Change by santoso (2008-10-29): round the trimmed mean before
			// casting
			query = "SELECT distinct CAST(ROUND(tblTrimmedMean.TrimmedMean,0) AS int) AS Result ";
			query += "FROM tblTrimmedMean INNER JOIN ";
			query += "tblAssignment ON tblTrimmedMean.SurveyID = tblAssignment.SurveyID AND ";
			query += "tblTrimmedMean.TargetLoginID = tblAssignment.TargetLoginID ";
			query += "INNER JOIN tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query += "WHERE tblTrimmedMean.SurveyID = " + surveyID;
			query += " AND tblTrimmedMean.Type = 1 AND tblRatingTask.RatingCode = 'CP' AND ";
			query += "tblTrimmedMean.CompetencyID = " + compID
					+ " and RaterCode <> 'SELF'";
			/*
			 * Change(s) : Check if the report to be generated is a subgroup
			 * report Reason(s) : Only need to get the data for selected users
			 * Updated By: Gwen Oh Updated On: 13 Oct 2011
			 */
			if (selectedUsers != null) {
				query += " AND (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "tblTrimmedMean.TargetLoginID = "
							+ selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += ")";
			}
			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;
			// query = query + " AND [User].FKDivision = " + divID;
			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;
			// query = query + " AND [User].FKDepartment = " + deptID;
			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;
			// query = query + " AND [User].Group_Section = " + groupSection;

			// Change by santoso (2008-10-29): round the trimmed mean before
			// casting
			query += " GROUP BY CAST(Round(tblTrimmedMean.TrimmedMean, 0) AS int)";

		} else {
			// Changed by Ha 09/07/08 from CAST to CAST (ROUND) to round to
			// nearest number
			// Changed by Santoso 2008/10/29, the query is fine but sometimes
			// Use double round to have the correct rounded value because avg
			// might produce value like 9.499999
			query = "SELECT DISTINCT CAST(ROUND(ROUND(AVG(tblAvgMean.AvgMean),2),0) as int) AS Result FROM tblAvgMean ";
			query += "INNER JOIN tblAssignment ON tblAvgMean.TargetLoginID = tblAssignment.TargetLoginID ";
			query += "INNER JOIN tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query += "WHERE tblAvgMean.SurveyID = " + surveyID
					+ " AND tblAvgMean.Type = " + groupType;
			query += " AND tblRatingTask.RatingCode = 'CP' AND tblAvgMean.CompetencyID = "
					+ compID + " AND tblAssignment.RaterCode <> 'SELF'";
			/*
			 * Change(s) : Check if the report to be generated is a subgroup
			 * report Reason(s) : Only need to get the data for selected users
			 * Updated By: Gwen Oh Updated On: 13 Oct 2011
			 */
			if (selectedUsers != null) {
				query += " AND (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "tblAvgMean.TargetLoginID = " + selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += ")";
			}
			query += " GROUP BY tblAvgMean.CompetencyID, tblAvgMean.TargetLoginID ";

			if (divID != 0)
				query += ", tblAssignment.FKTargetDivision ";
			if (deptID != 0)
				query += ", tblAssignment.FKTargetDepartment ";
			if (groupSection != 0)
				query += ", tblAssignment.FKTargetGroup ";

			if (divID != 0 || deptID != 0 || groupSection != 0)
				query += "HAVING (";

			if (divID != 0) {
				query += " tblAssignment.FKTargetDivision = " + divID;

				if (deptID != 0 || groupSection != 0)
					query += " AND ";
			}
			if (deptID != 0) {
				query += " tblAssignment.FKTargetDepartment = " + deptID;

				if (groupSection != 0)
					query += " AND ";
			}
			if (groupSection != 0)
				query += " tblAssignment.FKTargetGroup = " + groupSection;

			if (divID != 0 || deptID != 0 || groupSection != 0)
				query += ")";
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

				v.add(new Integer(rs.getInt(1)));
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getAllResults - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/**
	 * Retrieves total target based on the score
	 * 
	 * @param int compID CompetencyID
	 * @param int score
	 * 
	 * @return int TargetBasedScore
	 */
	public int totalTargetBasedScore(int compID, int score) throws SQLException {
		int reliability = C.ReliabilityCheck(surveyID);
		int iTotalTarget = 0;
		String query = "";

		if (reliability == 0) {
			/*
			 * query = "SELECT COUNT(*) AS Total FROM "; query +=
			 * "(SELECT tblTrimmedMean.TargetLoginID FROM tblTrimmedMean INNER JOIN "
			 * ; query +=
			 * "tblAssignment ON tblTrimmedMean.TargetLoginID = tblAssignment.TargetLoginID "
			 * ; query +=
			 * "INNER JOIN tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID "
			 * ; query += "WHERE tblTrimmedMean.SurveyID = " + surveyID +
			 * " AND tblRatingTask.RatingCode = 'CP' "; query +=
			 * "AND tblTrimmedMean.CompetencyID = " + compID +
			 * " AND tblTrimmedMean.Type = 1 "; query +=
			 * "GROUP BY tblTrimmedMean.TargetLoginID ";
			 * 
			 * if(divID != 0) query += ", tblAssignment.FKTargetDivision ";
			 * if(deptID != 0) query += ", tblAssignment.FKTargetDepartment ";
			 * if(groupSection != 0) query += ", tblAssignment.FKTargetGroup ";
			 * 
			 * query +=
			 * "HAVING (CAST(AVG(tblTrimmedMean.TrimmedMean) AS int) = " + score
			 * + ") ";
			 * 
			 * if(divID != 0) query += "AND tblAssignment.FKTargetDivision = " +
			 * divID; if(deptID != 0) query +=
			 * "AND tblAssignment.FKTargetDepartment = " + deptID;
			 * if(groupSection != 0) query +=
			 * "AND tblAssignment.FKTargetGroup = " + groupSection;
			 * 
			 * query += ") DERIVEDTBL";
			 */

			query = "SELECT COUNT(distinct tblTrimmedMean.TargetLoginID) AS Total FROM tblTrimmedMean INNER JOIN ";
			query += "tblAssignment ON tblTrimmedMean.SurveyID = tblAssignment.SurveyID AND ";
			query += "tblTrimmedMean.TargetLoginID = tblAssignment.TargetLoginID ";
			query += "INNER JOIN tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query += "WHERE tblTrimmedMean.SurveyID = " + surveyID;
			query += " AND tblTrimmedMean.Type = 1 AND tblRatingTask.RatingCode = 'CP' AND ";
			query += "tblTrimmedMean.CompetencyID = " + compID
					+ " and RaterCode <> 'SELF'";
			/*
			 * Change(s) : Check if the report to be generated is a subgroup
			 * report Reason(s) : Only need to get the data for selected users
			 * Updated By: Gwen Oh Updated On: 13 Oct 2011
			 */
			if (selectedUsers != null) {
				query += " AND (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "tblTrimmedMean.TargetLoginID = "
							+ selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += ")";
			}
			if (divID != 0)
				query = query + " AND tblAssignment.FKTargetDivision = "
						+ divID;

			if (deptID != 0)
				query = query + " AND tblAssignment.FKTargetDepartment = "
						+ deptID;

			if (groupSection != 0)
				query = query + " AND tblAssignment.FKTargetGroup = "
						+ groupSection;

			// Change by Santoso (2008-10-29) : add round
			query += " GROUP BY CAST(ROUND(tblTrimmedMean.TrimmedMean, 0) AS int) ";
			query += " HAVING (CAST(ROUND(tblTrimmedMean.TrimmedMean, 0) AS int) = "
					+ score + ") ";

		} else {
			query = "SELECT COUNT(*) AS Total FROM ";
			query += "(SELECT tblAvgMean.TargetLoginID FROM tblAvgMean INNER JOIN ";
			query += "tblAssignment ON tblAvgMean.TargetLoginID = tblAssignment.TargetLoginID ";
			query += "INNER JOIN tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query += "WHERE tblAvgMean.SurveyID = " + surveyID
					+ " AND tblRatingTask.RatingCode = 'CP' ";
			query += "AND tblAvgMean.CompetencyID = " + compID
					+ " AND tblAvgMean.Type = 1 ";
			/*
			 * Change(s) : Check if the report to be generated is a subgroup
			 * report Reason(s) : Only need to get the data for selected users
			 * Updated By: Gwen Oh Updated On: 13 Oct 2011
			 */
			if (selectedUsers != null) {
				query += " AND (";
				for (int i = 0; i < selectedUsers.length; i++) {
					query += "tblAvgMean.TargetLoginID = " + selectedUsers[i];
					if (i != (selectedUsers.length - 1))
						query += " OR ";
				}
				query += ")";
			}
			query += "GROUP BY tblAvgMean.TargetLoginID ";

			if (divID != 0)
				query += ", tblAssignment.FKTargetDivision ";
			if (deptID != 0)
				query += ", tblAssignment.FKTargetDepartment ";
			if (groupSection != 0)
				query += ", tblAssignment.FKTargetGroup ";
			// Changed by Ha 09/07/08 from CAST to CAST (ROUND) to round to
			// nearest number
			// Changed by Santoso 2008/10/29, the query is fine but sometimes
			// Use double round to have the correct rounded value because avg
			// might produce value like 9.499999
			query += "HAVING (CAST(ROUND(ROUND(AVG(tblAvgMean.AvgMean),2),0) AS int) = "
					+ score + ") ";

			if (divID != 0)
				query += "AND tblAssignment.FKTargetDivision = " + divID;
			if (deptID != 0)
				query += "AND tblAssignment.FKTargetDepartment = " + deptID;
			if (groupSection != 0)
				query += "AND tblAssignment.FKTargetGroup = " + groupSection;

			query += ") DERIVEDTBL";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				iTotalTarget = rs.getInt(1);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getAllResults - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return iTotalTarget;
	}
	/***************************** END DISTRIBUTION REPORT ************************************************/

	/******************************* TARGET RANK ***********************************************/
	/**
	 * Writes target rank to excel.
	 */
	public void printTargetRank() throws SQLException, IOException, Exception {
		if (exGR != 1) {
			if (hasCPR && exGR != 1) // If CPR is chosen in this survey
										// Denise 06/01/2009 if group ranking
										// not excluded
			{
				System.out.println("8. Group Ranking Report");

				// Added to perform a search for Report Title and insert the
				// information
				// Mark Oei 22 April 2010
				int[] address2 = OO.findString(xSpreadsheet,
						"<Group Ranking Report Title>");
				String info = "This report lists the Targets in ranked order based on the aggregate of all ";
				info += "their competency gaps. The report is in descending order starting with the strongest target.";

				int r = address2[1];
				OO.insertString(xSpreadsheet,info,r + 1,0);

				// Added by Tracy 01 Sep 08***********************
				// Add heading "GROUP RANKING TABLE" before the graph
				OO.findAndReplace(xSpreadsheet,"<Table title>",
						"GROUP RANKING TABLE");

				// End add by Tracy 01 Sep 08**********************
				int maxScale = getMaxScale();

				int[] address = OO.findString(xSpreadsheet,"<Group Rank>");

				column = address[0];
				row = address[1];

				OO.findAndReplace(xSpreadsheet,"<Group Rank>"," ");

				int total = getTotalTarget();

				HashMap NameMap = new HashMap();

				// 1. get all the completed raters.
				Vector rsRank = null;
				// Gwen Oh - 17/10/2011: Only get selected users for subgroup
				// report
				if (selectedUsers == null)
					rsRank = getAllTargets();
				else {
					rsRank = new Vector(selectedUsers.length);
					for (int i = 0; i < selectedUsers.length; i++) {
						rsRank.add(new Integer(selectedUsers[i]));
					}
				}
				
				// 2. get all the gap and target name
				Vector vGap = new Vector();
				

				for (int j = 0; j < rsRank.size(); j++) {
					int fkUser=0;
					if(selectedUsers!=null){
						 fkUser = ((Integer) rsRank.elementAt(j)).intValue();
					}else{
						String[] st= (String[])rsRank.elementAt(j);
						 fkUser =Integer.parseInt(st[0]);
					}
						
					voUser vo = getTargetGap(fkUser);
					double dGap = vo.getGap();
					String sName = vo.getName();

					NameMap.put(new Integer(fkUser),sName);

					vGap.add(new String[]{Integer.toString(fkUser),
							Double.toString(dGap)});

				}

				Vector vSorted = G.sorting(vGap,1);

				int startBorder = row;
				String sName = "Name";
				String sGapScore = "Gap Score";

				if (ST.LangVer == 2) {
					sName = "Nama";
					sGapScore = "Nilai Selisih";
				}

				// Change to shift GapScore by 2 columns to the right
				// Mark Oei 25 Mar 2010
				OO.insertString(xSpreadsheet,"S/N",row,column);
				OO.insertString(xSpreadsheet,sName,row,column + 2);
				OO.insertString(xSpreadsheet,sGapScore,row,column + 9);
				// OO.setFontBold(xSpreadsheet, startColumn, column+9, row,
				// row);
				OO.setCellAllignment(xSpreadsheet,column + 9,column + 9,row,
						row,1,2);
				OO.setBGColor(xSpreadsheet,startColumn,column + 10,row,row,
						BGCOLOR);

				row++;
				int i = 0;
				double gap[] = new double[total];
				String target[] = new String[total];

				for (int j = 0; j < vSorted.size(); j++) {

					int user = Integer.parseInt(((String[]) vSorted
							.elementAt(j))[0]);
					target[i] = (String) NameMap.get(new Integer(user));
					gap[i] = Double.valueOf(
							((String[]) vSorted.elementAt(j))[1]).doubleValue();
					// Added checking by Ha 01/07/08 to get rid of null value in
					// the report
					if (target[i] != null) {
						OO.insertNumeric(xSpreadsheet,i + 1,row,column);
						OO.insertString(xSpreadsheet,target[i],row,column + 2);
						OO.insertNumeric(xSpreadsheet,gap[i],row,column + 9);
						i++;
						row++;
					}
				}

				int endBorder = row - 1;

				OO.setTableBorder(xSpreadsheet,0,10,startBorder,endBorder,
						false,false,true,true,true,true);

				row++;
				// System.out.println("testing " + r + " " + row);
				drawChartRank(target,gap,2,maxScale);
			} else {
				// Codes added to get CPScores and plot the table and chart
				// Mark Oei 22 April 2010
				System.out.println("8. Group Ranking Report");
				// int [] address2 = OO.findString(xSpreadsheet4,
				// "<Group Ranking Report Title>");
				int startRow = 0;
				String info = "This report lists the Targets in ranked order based on the aggregate of ";
				info += "all their CP (All) scores. The report is in descending order starting with the strongest target.";

				/*
				 * Group ranking table header
				 */
				OO.insertString(xSpreadsheet4,"GROUP RANKING TABLE",startRow,0);
				OO.setFontSize(xSpreadsheet4,0,11,startRow,startRow,16);
				OO.setFontBold(xSpreadsheet4,0,11,startRow,startRow);
				startRow += 2;

				/*
				 * Group ranking table Information
				 */
				OO.mergeCells(xSpreadsheet4,0,11,startRow,startRow);
				OO.insertString(xSpreadsheet4,info,startRow,0);
				OO.setRowHeight(xSpreadsheet4,startRow,0,
						ROWHEIGHT * OO.countTotalRow(info,105));
				startRow += 2;
				int maxScale = getMaxScale();
				// End of copying

				Vector cpAll = new Vector(); // create an object to store the
												// CP(All) values
				vCPValues.clear(); // clear the previous values and reuse the
									// object
				cpAll.clear(); // clear all values from the object
				cpAll = getCP(); // get the CP(All) from database

				int totalTarget = getTotalTarget();

				int currentElement = 0;
				int count = 0;
				double sum = 0.0;
				String lastTarget[] = (String[]) cpAll.lastElement(); // get the
																		// last
																		// target
				String currTarget[] = (String[]) cpAll.firstElement(); // get
																		// the
																		// first
																		// target
				String arr[] = new String[currTarget.length];
				System.out.println("targets: " + totalTarget + "cp: " + cpAll.size());
				for (int targetLoop = 0; targetLoop < totalTarget; targetLoop++) {
					sum = 0.0; // for adding up all the different CP(All)
					count = 0; // for determing the number of CP(All) to add
					
					System.out.println("targets: in  " + currentElement);
					if(currentElement>= cpAll.size()){
						break;
					}
					currTarget = (String[]) cpAll.elementAt(currentElement); // get
																				// the
																				// current
																				// target

					
					for (int calcLoop = 0; calcLoop < cpAll.size(); calcLoop++) {
						arr = (String[]) cpAll.elementAt(calcLoop);
						if (currTarget[1].equals(arr[1])) {
							sum = sum + Double.parseDouble(arr[2]);
							count++;
						}
					}
					
					//sum = Math.round(sum * 100) / 100.0; // format values to 2
													// decimal places
					// System.out.println("testing " + currTarget[1] + " " +
					// arr[1] + " " + count + " " + targetLoop);
					vCPValues.add(new String[]{currTarget[0],
							Double.toString(sum)});
					currentElement += count;
					
					// System.out.println(currentElement + " Current Target " +
					// currTarget[0] + " CP " + avg );
				}

				maxScale = count * maxScale; // set the maxScale based on number
												// of competencies
				vCPValues = G.sorting(vCPValues,1); // sorting the CP (All)
													// values
				
				int startBorder = row;
				String sName = "Name";
				String sCPScore = "CP Score";

				if (ST.LangVer == 2) {
					sName = "Nama";
					sCPScore = "Selisih";
				}
				int tableStart = startRow;

				OO.insertString(xSpreadsheet4,"S/N",startRow,column);
				OO.insertString(xSpreadsheet4,sName,startRow,column + 2);
				OO.mergeCells(xSpreadsheet4,10,11,startRow,startRow);
				OO.insertString(xSpreadsheet4,sCPScore,startRow,column + 10);
				OO.setFontBold(xSpreadsheet4,startColumn,column + 10,startRow,
						startRow);
				OO.setCellAllignment(xSpreadsheet4,column + 9,column + 10,
						startRow,startRow,1,2);
				OO.setBGColor(xSpreadsheet4,startColumn,column + 10,startRow,
						startRow,BGCOLOR);
				
				startRow++;
				int i = 0;
				String[] target = new String[totalTarget];
				double cpOverall[] = new double[totalTarget];

				for (int j = 0; i < vCPValues.size(); j++) {
					arr = (String[]) vCPValues.elementAt(i);
					target[i] = arr[0];
					cpOverall[i] = Double.valueOf(
							((String[]) vCPValues.elementAt(i))[1])
							.doubleValue();
					if (target[i] != null) {
						OO.mergeCells(xSpreadsheet4,10,11,startRow,startRow);
						OO.insertNumeric(xSpreadsheet4,i + 1,startRow,column);
						OO.insertString(xSpreadsheet4,target[i],startRow,
								column + 2);
						OO.insertNumeric(xSpreadsheet4,cpOverall[i],startRow,
								column + 10);
						HashMap<String,Double> total = new HashMap<String,Double>();
						total.put(target[i],cpOverall[i]);
						totalScores.add(total);
						OO.setCellAllignment(xSpreadsheet4,10,11,startRow,
								startRow,2,1);
					}
					i++;
					startRow++;
				}

				int endBorder = startRow - 1;

				OO.setTableBorder(xSpreadsheet4,0,11,tableStart,startRow - 1,
						false,false,true,true,true,true);

				startRow++;

				drawChartRank(target,cpOverall,2,maxScale);
				groupRankingTableRow = startRow;
				
			}
		} else {
			System.out.println("9. Removing Group Ranking Report"); // (No
																	// CPR)");

			int[] address = OO.findString(xSpreadsheet,
					"<Group Ranking Report Title>");

			column = address[0];
			row = address[1];

			OO.deleteChart(xSpreadsheet,"Object 2");
			OO.deleteRows(xSpreadsheet,0,12,row - 6,row + 46,52,1);
		}

	}

	/**
	 * Count total records in tblGap for the particular survey, group,
	 * department, and division.
	 * 
	 * @return int TotalTarget
	 */
	public int getTotalTarget() throws SQLException {
		int total = 0;

		String query = "SELECT COUNT(DISTINCT tblAssignment.TargetLoginID) AS Total ";
		query += "FROM         tblAssignment INNER JOIN ";
		query += "[User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query += "WHERE     tblAssignment.SurveyID =  " + surveyID;
		/*
		 * Change(s) : Check if the report to be generated is a subgroup report
		 * Reason(s) : To cater to subgroup report where only certain users are
		 * selected Updated By: Gwen Oh Updated On: 17 Oct 2011
		 */
		if (selectedUsers != null) {
			query += " AND (";
			for (int i = 0; i < selectedUsers.length; i++) {
				query += "tblAssignment.TargetLoginID = " + selectedUsers[i];
				if (i != (selectedUsers.length - 1))
					query += " OR ";
			}
			query += ")";
		}
		if (divID != 0)
			query = query + " AND tblAssignment.FKTargetDivision = " + divID;

		if (deptID != 0)
			query = query + " AND tblAssignment.FKTargetDepartment = " + deptID;

		if (groupSection != 0)
			query = query + " AND tblAssignment.FKTargetGroup = "
					+ groupSection;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next())
				total = rs.getInt(1);

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTotalTarget - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return total;
	}

	/**
	 * Retrieves all targets under that particular survey.
	 * 
	 * @return ResultSet All targets under a survey
	 */
	public Vector getAllTargets() throws SQLException {
		String query = "SELECT DISTINCT tblAssignment.TargetLoginID,givenName,familyname FROM tblAssignment INNER JOIN ";
		query += "[User] ON tblAssignment.TargetLoginID = [User].PKUser WHERE ";
		query += "tblAssignment.SurveyID = " + surveyID ;

		if (divID != 0)
			query = query + " AND tblAssignment.FKTargetDivision = " + divID;

		if (deptID != 0)
			query = query + " AND tblAssignment.FKTargetDepartment = " + deptID;

		if (groupSection != 0)
			query = query + " AND tblAssignment.FKTargetGroup = "
					+ groupSection;

		query += " ORDER BY tblAssignment.TargetLoginID ";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String[] info = new String[2];
				info[0]=String.valueOf(rs.getInt(1));
				info[1]=rs.getString("familyname") + " " + rs.getString("givenName");
				v.add(info);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getAllTargets - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/**
	 * Add by Santoso (2008-10-31) : copied from IndividualReport.CPCPR
	 * Retrieves the results under that particular rating code.
	 */
	private Vector getCPCPR(String RTCode, int targetID) throws SQLException {
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C.ReliabilityCheck(surveyID);
		int average = 1;
		if (reqWeightedAverage = true) {
			average = 10;
		} else {
			average = 1;
		}
		if (reliabilityCheck == 0) { // Change Competency.CompetencyID to
										// Competency.PKCompetency as
										// CompetencyID does not
										// exist in Competency table, Mark Oei
										// 22 April 2010
			query = "SELECT tblTrimmedMean.CompetencyID, Competency.CompetencyName, tblTrimmedMean.TrimmedMean as Result ";
			query = query + "FROM tblTrimmedMean INNER JOIN tblRatingTask ON ";
			query = query
					+ "tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "INNER JOIN Competency ON ";
			query = query
					+ "tblTrimmedMean.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblTrimmedMean.SurveyID = " + surveyID
					+ " AND ";
			query = query + "tblTrimmedMean.TargetLoginID = " + targetID
					+ " AND tblTrimmedMean.Type = 1 AND ";
			query = query + "tblRatingTask.RatingCode = '" + RTCode + "' ";
			query = query + "ORDER BY Competency.PKCompetency"; // CompetencyID";
		} else {
			if (surveyLevel == 0) {
				query = "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, tblAvgMean.AvgMean as Result ";
				query = query + "FROM tblAvgMean INNER JOIN tblRatingTask ON ";
				query = query
						+ "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query
						+ "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID
						+ " AND ";
				query = query + "tblAvgMean.TargetLoginID = " + targetID
						+ " AND tblAvgMean.Type = " + average + " AND ";
				query = query + "tblRatingTask.RatingCode = '" + RTCode
						+ "' ORDER BY Competency.PKCompetency";
			} else {
				query = "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, ";
				query = query
						+ "CAST(AVG(tblAvgMean.AvgMean) AS numeric(38, 2)) AS Result ";
				query = query + "FROM tblRatingTask INNER JOIN tblAvgMean ON ";
				query = query
						+ "tblRatingTask.RatingTaskID = tblAvgMean.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query
						+ "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID
						+ " AND ";
				query = query + "tblAvgMean.TargetLoginID = " + targetID
						+ " AND tblAvgMean.Type = " + average + " AND ";
				query = query + "tblRatingTask.RatingCode = '" + RTCode
						+ "' GROUP BY tblAvgMean.CompetencyID, ";
				query = query + "Competency.CompetencyName";
			}
		}
		// System.out.println("cpcpr "+query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String[] arr = new String[3];
				arr[0] = rs.getString(1);
				arr[1] = rs.getString(2);
				arr[2] = Double.toString(Math.round(rs.getDouble(3)*100)/100.00);
				
				v.add(arr);
			}
		} catch (Exception ex) {
			System.out.println("GroupReport.java - CPCPR - " + ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		return v;
	}

	/**
	 * Add by Santoso (2008-10-31) To calculate the Gap value that belongs to
	 * the specified targetLoginID
	 * 
	 * @param targetLoginID
	 * @return
	 * @throws SQLException
	 */
	private double getGap(int targetLoginID) throws SQLException {
		// System.out.println("4. CP Versus CPR Starts");
		List competencyList = getCompetency(0);
		List ratingTaskList = getRatingTask();
		List CP = null, CPR = null;
		// get CP & CPR value
		for (Iterator iteratorRating = ratingTaskList.iterator(); iteratorRating
				.hasNext();) {
			voRatingTask ratingTask = (voRatingTask) iteratorRating.next();
			String RTCode = ratingTask.getRatingCode();
			if (RTCode.equals("CP") || RTCode.equals("CPR")
					|| RTCode.equals("FPR")) {
				if (RTCode.equals("CP"))
					CP = getCPCPR(RTCode,targetLoginID);
				else {
					CPR = getCPCPR(RTCode,targetLoginID);
				}
			}
		}

		double gap = 0d;
		for (int i = 0; i < competencyList.size(); i++) {
			double dCP = 0;
			double dCPR = 0;
			voCompetency competency = (voCompetency) competencyList.get(i);

			if (CP.size() != 0 && i < CP.size()) {
				String arr[] = (String[]) CP.get(i);
				dCP = Double.parseDouble(arr[2]);
			}

			if (CPR.size() != 0 && i < CPR.size()) {
				String arr[] = (String[]) CPR.get(i);
				dCPR = Double.parseDouble(arr[2]);
			}

			gap += Math.round((dCP - dCPR) * 100.0) / 100.0;
		}
		return gap;
	}

	/**
	 * Get the gap username based on the name sequence
	 * 
	 * @param int TargetID
	 * 
	 * @return String Username
	 */
	public voUser getTargetGap(int targetID) throws SQLException {
		String query = "";
		String name = "";

		int nameSeq = Integer.parseInt(surveyInfo[3]); // 0=familyname first
		// Changed by HA 08/07/08 change from Average Gap to Sum Gap
		// Changed by Santoso (2008-10-31)
		// don't retrieve the value from tblGap (for competency)
		// just get user's full name
		query += "SELECT [User].PKUser, [User].FamilyName, [User].GivenName "
				+ "FROM [User] WHERE [User].PKUser = " + targetID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		voUser vo = new voUser();

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				// Change by Santoso (2008-10-31)
				// retrive gap in getGap
				double dGap = getGap(targetID);
				vo.setGap(dGap);

				String family = rs.getString("FamilyName");
				String given = rs.getString("GivenName");

				if (nameSeq == 0)
					name = family + " " + given;
				else
					name = given + " " + family;

				vo.setName(name);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTargetGap - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return vo;
	}

	/**
	 * Draw Group Ranking Report chart
	 */
	public void drawChartRank(String Rating[], double Result[], int type,
			int maxScale) throws IOException, Exception {
		int r = row;
		int c = 0;

		// Initialised values for bar graph and gridlines colours
		// Mark Oei 25 Mar 2010
		int colColor = 10066431; // violet colour
		int gridLineColor = 0; // black colour
		int fontColor = 16777215; // white colour

		// Added to find original row where Group Ranking Report chart to be
		// displayed
		// Mark Oei 25 Mar 2010
		// Commented off findString by "This report .." as different information
		// will be inserted depending
		// on CP Score or GapScore, Mark Oei 22 April 2010
		// int [] address = OO.findString(xSpreadsheet,
		// "This report lists the Targets in ranked order based on the aggregate of all their competency gaps");
		int[] address = OO.findString(xSpreadsheet,
				"<Group Ranking Report Title>");
		OO.findAndReplace(xSpreadsheet,"<Group Ranking Report Title>",
				"Group Ranking Report");
		int originalRow = address[1];

		// Remove the old chart
		// Mark Oei 25 Mar 2010
		OO.deleteChart(xSpreadsheet,"Object 2");

		XSpreadsheet xSpreadsheet2 = OO.getSheet(xDoc,"Sheet2");
		OO.insertString(xSpreadsheet2,"Targets",r,c);
		OO.insertString(xSpreadsheet2,"Gap",r,c + 1);
		r++;

		// Store the values in reverse order for plotting
		// Mark Oei 25 Mar 2010
		int endRow = r + Rating.length - 1;
		for (int i = Rating.length - 1; i >= 0; i--) {
			// Added checking null by Ha 01/07/08 to get rid of "0" row in the
			// chart
			if (Rating[i] != null) {
				OO.insertString(xSpreadsheet2,Rating[i],endRow,c);
				OO.insertNumeric(xSpreadsheet2,Result[i],endRow,c + 1);
				endRow--;
				r++;
			}
		}

		// Denise
		// XTableChart xtableChart = OO.getChartByIndex(xSpreadsheet, 1);
		// OO.showLegend(xtableChart, false);
		// Added to create a new chart for Group Ranking Report
		// Mark Oei 25 Mar 2010
		XTableChart xtablechart = OO.getChart(xSpreadsheet,xSpreadsheet2,c,
				c + 1,row,r - 1,Integer.toString(row),16000,16800,
				originalRow + 4,c);
		if (!(hasCPR && exGR != 1)) // set scales for CPScore chart, Mark Oei 22
									// April 2010
			OO.setAxes(xtablechart,"","",maxScale,(maxScale / 5),0,0,0);
		xtablechart = OO.setChartTitle(xtablechart,"");
		OO.setBarColor(xtablechart,colColor);
		OO.drawGridLines(xtablechart,gridLineColor); // draw both vertical and
														// horizontal lines,
														// Mark Oei 25 Mar 2010
		OO.setFontSize(8);
		OO.setChartProperties(xtablechart,false,true,false,false,true); // hide
																		// the
																		// horizontal
																		// lines,
																		// Mark
																		// Oei
																		// 25
																		// Mar
																		// 2010
		OO.showLegend(xtablechart,false);

		// Commented off
		// Mark Oei 25 Mar 2010
		// OO.setSourceData(xSpreadsheet, xSpreadsheet2, 1, c, c+1, row, r-1);

		row++;
	}

	/********************************* END TARGET RANK **********************************************/

	/****************************** REPLACEMENT ***************************************************/
	/**
	 * Replace one string with another, this is used only if we are using
	 * template.
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	public void Replacement() throws Exception, IOException {
		// job position
		System.out.println("3. Replacement Starts");
		OO.findAndReplace(xSpreadsheet,"<Job Position>",surveyInfo[1]);
		int[] addressDepartment = OO.findString(xSpreadsheet,
				"<Department>");
		if(deptID==218){
			OO.findAndReplace(xSpreadsheet, "<Department>", " - Staff Department");
			
		}else if(deptID==219){
			OO.findAndReplace(xSpreadsheet, "<Department>", " - Specialist Units");
			
		} else if(deptID==220){
			OO.findAndReplace(xSpreadsheet, "<Department>", " - Land Divisions");
			
		} else{
			OO.findAndReplace(xSpreadsheet, "<Department>", "");
		}
		
		String[] dates = getSurveyDates();
		int dateStart = dates[0].indexOf(" ");
		int dateEnd = dates[1].indexOf(" ");
		
		dates[0]=dates[0].substring(0,dateStart);
		dates[1]=dates[1].substring(0,dateEnd);
		System.out.println("Dates:" + dates[0]+" " +  dates[1]);
		OO.findAndReplace(xSpreadsheet,"<DateStart>",dates[0]);
		OO.findAndReplace(xSpreadsheet,"<DateEnd>",dates[1]);
	}

	/**
	 * Replace one string with another, this is used only if we are using
	 * template.
	 */
	/*
	 * public void ReplacementToyota(String sDiv, String sDept, String sGrp,
	 * String sJobLvl) throws Exception, IOException {
	 * System.out.println("3. Replacement Starts");
	 * 
	 * OO.findAndReplace(xSpreadsheet, "<Position>", sJobLvl.toUpperCase());
	 * OO.findAndReplace(xSpreadsheet, "<Job Title>",
	 * surveyInfo[1].toUpperCase()); OO.findAndReplace(xSpreadsheet,
	 * "<Division>", sDiv.toUpperCase()); OO.findAndReplace(xSpreadsheet,
	 * "<Department>", sDept.toUpperCase()); OO.findAndReplace(xSpreadsheet,
	 * "<Section>", sGrp.toUpperCase()); OO.findAndReplace(xSpreadsheet2,
	 * "<Job Position>", surveyInfo[1].toUpperCase()); }
	 */

	/************************** END OF REPLACEMENT ******************************************/

	/************************ START REPORT GENERATION ************************************************************/
	/**
	 * Method to call all report generation method.
	 * 
	 * @param surveyID
	 * @param groupSection
	 * @param deptID
	 * @param divID
	 * @param pkUser
	 * @param fileName
	 * @param type
	 * @throws SQLException
	 * @throws Exception
	 * @throws IOException
	 * @see Report()
	 */
	public void generateReport(int surveyID, int groupSection, int deptID,
			int divID, int pkUser, String fileName, int type)
			throws SQLException, Exception, IOException {
		try {
			vGap.clear();
			CPCPRMap.clear();
			CompIDGapMap.clear();
			vCompDetails.clear();
			vRatingTask.clear();
			vCP.clear();
			vCPR.clear();

			// Denise 28/12/2009 reinitialize hasCP, hasCPR, hasFPR value
			hasCP = false;
			hasCPR = false;
			hasFPR = false;

			InitializeExcel(fileName);

			Replacement();

			vCompDetails = getCompetency(0);
			vRatingTask = getRatingTask();
			iReportType = type;
			// to check for CP / CPR and assign value to variables.
			checkCPCPR();

			InsertLogo(); // Denise 07/01/2009 insert logo after checking for
							// hasCPR
			printCPvsCPR();

			// Denise 23/12/2009 to print the group report profile page
			InsertProfileLegend();
			// Added by Tracy 26 aug 08******************
			// Print Rating title based on CPR or FPR*********
			printGapTitle(surveyID);
			// End Tracy add 26 aug 08***************

			try {
				printGap();
			} catch (Exception e) {
				System.out.println("Error2 " + e.getMessage());
			}
			try {
				printCompGap(surveyID);
			} catch (Exception e) {
				System.out.println("Error3 " + e.getMessage());
			}
			try {
				printCompetency();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error4a " + e.getMessage());
			}

			int included = Q.commentIncluded(surveyID);
			// Added by Ha 23/06/08 to print out the self comment
			int selfIncluded = Q.SelfCommentIncluded(surveyID);
			// by Hemilda 10/10/2008 remove the comments part
			// if(included == 1||selfIncluded==1)
			// try {printComments();} catch (Exception
			// e){System.out.println("Print comment "+e.getMessage());}

			try {
				printTotalTargets();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error4b " + e.getMessage());
			}

			// Added by Chun Yeong 29 Jun 2011 ***********
			// To write overall blind spots analysis
			// try {printBlindSpotAnalysis();} catch (Exception
			// e){System.out.println("Error4.5 "+e.getMessage());}
			printBlindSpotAnalysis();
			// End Chun Yeong 29 Jun 2011 *************

			// Denise 16/12/2009. exGR == 0 Printout the Group ranking
			// if (exGR==0) //Denise 06/01/2009 check for exclude group report
			// ranking in the printTargetRank() method
			try {
				printTargetRank();
			} catch (Exception e) {
				System.out.println("Error5 " + e.getMessage());
			}

			Date timestamp = new Date();
			SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
			String temp = dFormat.format(timestamp);
			System.out.println("Insert Header Footer");
			// changed copyright symbol to \u00a9 and registered symbol to
			// \u00AE by Chun Yeong 1 Jul 2011
			OO.insertHeaderFooter(
					xDoc,
					surveyInfo[1],
					surveyInfo[4],
					"Date of printing: "
							+ temp
							+ "\n"
							+ "Copyright \u00a9 3-Sixty Profiler\u00AE is a product of Pacific Century Consulting Pte Ltd.");

			// reset selected users
			selectedUsers = null;

			System.out
					.println("================ Group Report Completed ================");

		} catch (SQLException SE) {
			System.out.println("1Store Document");
			OO.storeDocComponent(xRemoteServiceManager,xDoc,storeURL);
			System.out.println("SQL " + SE.getMessage());
			OO.closeDoc(xDoc);
		} catch (Exception E) {
			System.out.println("2Store Document");
			OO.storeDocComponent(xRemoteServiceManager,xDoc,storeURL);
			System.out.println("DD " + E.getMessage());
			OO.closeDoc(xDoc);
		} finally {

			try {
				System.out.println("Store Document");
				OO.storeDocComponent(xRemoteServiceManager,xDoc,storeURL);
				System.out.println("CLosing OO");
				OO.closeDoc(xDoc);
				System.out.println("OO Closed");
			} catch (SQLException SE) {
				System.out.println("a " + SE.getMessage());
			} catch (Exception E) {
				System.out.println("b " + E.getMessage());
			}
		}
	}

	public void generateReport(int surveyID, Vector<Integer> groupSection,
			Vector<Integer> deptID, int divID, int pkUser, String fileName,
			int type) throws SQLException, Exception, IOException {
		try {
			vGap.clear();
			CPCPRMap.clear();
			CompIDGapMap.clear();
			vCompDetails.clear();
			vRatingTask.clear();
			vCP.clear();
			vCPR.clear();

			// Denise 28/12/2009 reinitialize hasCP, hasCPR, hasFPR value
			hasCP = false;
			hasCPR = false;
			hasFPR = false;

			InitializeExcel(fileName);

			Replacement();

			vCompDetails = getCompetency(0);
			vRatingTask = getRatingTask();
			iReportType = type;
			// to check for CP / CPR and assign value to variables.
			checkCPCPR();

			InsertLogo(); // Denise 07/01/2009 insert logo after checking for
							// hasCPR
			printCPvsCPR();
			InsertRatingScaleList();
			// Denise 23/12/2009 to print the group report profile page
			InsertProfileLegend();
			// Added by Tracy 26 aug 08******************
			// Print Rating title based on CPR or FPR*********
			printGapTitle(surveyID);
			// End Tracy add 26 aug 08***************
			
			try {
				printGap();
			} catch (Exception e) {
				System.out.println("Error2 " + e.getMessage());
			}
			try {
				printCompGap(surveyID);
			} catch (Exception e) {
				System.out.println("Error3 " + e.getMessage());
			}
			try {
				InsertStrDistributionTable();
			} catch (Exception e) {
				System.out.println("hahaha " + e.getMessage());
			}
			
			try {
				printCompetency();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error4a " + e.getMessage());
			}
			
			int included = Q.commentIncluded(surveyID);
		
			// Added by Ha 23/06/08 to print out the self comment
			int selfIncluded = Q.SelfCommentIncluded(surveyID);
		
			// by Hemilda 10/10/2008 remove the comments part
			// if(included == 1||selfIncluded==1)
			// try {printComments();} catch (Exception
			// e){System.out.println("Print comment "+e.getMessage());}

			try {
				InsertSuveyOverview(surveyID);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error4b " + e.getMessage());
			}

			// Added by Chun Yeong 29 Jun 2011 ***********
			// To write overall blind spots analysis
			// try {printBlindSpotAnalysis();} catch (Exception
			// e){System.out.println("Error4.5 "+e.getMessage());}
			
			try{
			printBlindSpotAnalysis();
			}catch(Exception e){
				System.out.println("GroupReport.java - printBlindSpotAnalysis : " + e);
			}
			// End Chun Yeong 29 Jun 2011 *************

			// Denise 16/12/2009. exGR == 0 Printout the Group ranking
			// if (exGR==0) //Denise 06/01/2009 check for exclude group report
			// ranking in the printTargetRank() method
			try {
			
				printTargetRank();
				
			} catch (Exception e) {
				System.out.println("Error5 " + e.getMessage());
				e.printStackTrace();
			}
			
			insertCompetencyRankTable();
			
			
			
			try {
				
				printRawData();

			} catch (Exception e) {
				System.out.println("Error5 " + e.getMessage());
			}

			Date timestamp = new Date();
			SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
			String temp = dFormat.format(timestamp);

			System.out.println("Insert Header Footer");

			// changed copyright symbol to \u00a9 and registered symbol to
			// \u00AE by Chun Yeong 1 Jul 2011
			
			int departmentID =0;
			if (deptID.size()==1){
				departmentID = deptID.elementAt(0);
				departmentID= this.deptID;
			}
			String departmentName = "";
			if(departmentID == 218){
				departmentName="Staff Department";
				
			}else if(departmentID==219){
				departmentName="Specialist Units";
				
			} else if(departmentID==220){
				departmentName="Land Divisions";
				
			} else{
				departmentName="Group Report";
			}
			OO.insertHeaderFooter(
					xDoc,
					surveyInfo[1],
					surveyInfo[4] + "\n" + departmentName,
					"Date of printing: "
							+ temp
							+ "\n"
							+ "Copyright \u00a9 3-Sixty Profiler\u00AE is a product of Pacific Century Consulting Pte Ltd.");

			// reset selected users
			selectedUsers = null;

			System.out
					.println("================ Group Report Completed ================");
			
			
		} catch (SQLException SE) {
			System.out.println("1Store Document");
			OO.storeDocComponent(xRemoteServiceManager,xDoc,storeURL);
			System.out.println("SQL " + SE.getMessage());
			OO.closeDoc(xDoc);
		} catch (Exception E) {
			System.out.println("2Store Document");
			
			OO.storeDocComponent(xRemoteServiceManager,xDoc,storeURL);
			System.out.println("DD " + E.getMessage());
			E.printStackTrace();
			OO.closeDoc(xDoc);
		} finally {

			try {
				System.out.println("Store Document");
				OO.storeDocComponent(xRemoteServiceManager,xDoc,storeURL);
				System.out.println("CLosing OO");
				OO.closeDoc(xDoc);
				System.out.println("OO Closed");
			} catch (SQLException SE) {
				System.out.println("a " + SE.getMessage());
			} catch (Exception E) {
				System.out.println("b " + E.getMessage());
			}
		}
	}

	/**
	 * get competency
	 * 
	 * @param iOrder
	 * @return
	 * @throws SQLException
	 * @see generateReport()
	 */
	public Vector getCompetency(int iOrder) throws SQLException {
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		Vector v = new Vector();

		if (surveyLevel == 0) {
			query = query
					+ "SELECT tblSurveyCompetency.CompetencyID, Competency.CompetencyName, ";
			query = query
					+ "CompetencyDefinition FROM tblSurveyCompetency INNER JOIN Competency ON ";
			query = query
					+ "tblSurveyCompetency.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblSurveyCompetency.SurveyID = " + surveyID;

			if (iOrder == 0)
				query = query + " ORDER BY tblSurveyCompetency.CompetencyID";
			else
				query = query
						+ " ORDER BY tblSurveyCompetency.CompetencyID DESC";

		} else {

			query = query
					+ "SELECT DISTINCT tblSurveyBehaviour.CompetencyID, Competency.CompetencyName, ";
			query = query
					+ "Competency.CompetencyDefinition FROM Competency INNER JOIN ";
			query = query
					+ "tblSurveyBehaviour ON Competency.PKCompetency = tblSurveyBehaviour.CompetencyID ";
			query = query
					+ "AND Competency.PKCompetency = tblSurveyBehaviour.CompetencyID ";
			query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID;

			if (iOrder == 0)
				query = query + " ORDER BY tblSurveyBehaviour.CompetencyID";
			else
				query = query
						+ " ORDER BY tblSurveyBehaviour.CompetencyID DESC";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				int compID = rs.getInt("CompetencyID");
				String compName = UnicodeHelper.getUnicodeStringAmp(rs
						.getString("CompetencyName").trim());
				String compDef = UnicodeHelper.getUnicodeStringAmp(rs
						.getString("CompetencyDefinition").trim());

				voCompetency vo = new voCompetency();
				vo.setCompetencyID(compID);
				vo.setCompetencyName(compName);
				vo.setCompetencyDefinition(compDef);
				v.add(vo);
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getCompetency - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}

	/**
	 * Insert Logo
	 */
	public void InsertLogo() throws IOException, Exception {
		if (surveyInfo[6] != "") // Org Logo
		{
			File F = new File(ST.getOOLogoPath() + surveyInfo[6]); // directory
																	// where the
																	// file
																	// supposed
																	// to be
																	// stored

			// System.out.println(ST.getOOLogoPath()+ surveyInfo[6]);
			if (F.exists())
				OO.replaceLogo(xSpreadsheet,xDoc,"<Logo>",ST.getOOLogoPath()
						+ surveyInfo[6]);
			else
				OO.replaceLogo(xSpreadsheet,xDoc,"<Logo>","");

			// Commented off the check as Group Report will be available with CP
			// Score
			// Mark Oei 22 April 2010
			// if (this.hasCPR && exGR!=1) //Denise 07/01/2009 insert logo
			// before group Ranking chart
			if (exGR != 1) {
				if (F.exists())
					OO.replaceLogo(xSpreadsheet,xDoc,"<LogoGRPosition>",
							ST.getOOLogoPath() + surveyInfo[6]);
				else
					OO.replaceLogo(xSpreadsheet,xDoc,"<LogoGRPosition>","");
			}
		}
	}
	/************************ END REPORT GENERATION ***************************************************/

	/************************** START - JSP METHODS ******************************/

	public String Report(int surveyID, int groupSection, int deptID, int divID,
			int pkUser, String fileName, int type) throws SQLException,
			Exception, IOException {
		System.out.println("----Group Report Generation Starts----");

		InitializeSurvey(surveyID,groupSection,deptID,divID);

		String jobName = surveyInfo[1].replaceAll("/","-");

		String save = jobName + " (" + surveyInfo[4] + ")";

		generateReport(surveyID,groupSection,deptID,divID,pkUser,fileName,type);

		// ---adding event viewer
		String[] UserInfo = U.getUserDetail(pkUser);

		try {
			String groupSect = "All";
			String deptName = "All";
			String divName = "All";

			if (groupSection != 0)
				groupSect = getGroupName();
			if (deptID != 0)
				deptName = getDeptName();
			if (divID != 0)
				divName = getDivName();

			String temp = surveyInfo[4] + "(S); " + groupSect + "(G); "
					+ deptName + "(Dept); " + divName + "(Div)";

			EV.addRecord("Print","Rater Result",temp,UserInfo[2],UserInfo[11],
					UserInfo[10]);

		} catch (SQLException SE) {
			System.out.println(SE.getMessage());
		}

		return save;

	}

	public String Report(int surveyID, Vector<Integer> groupSection,
			Vector<Integer> deptID, int divID, int pkUser, String fileName,
			int type, int weightedAverage) throws SQLException, Exception,
			IOException {
		System.out.println("----Group Report Generation Starts----");

		InitializeSurvey(surveyID,groupSection,deptID,divID);

		String jobName = surveyInfo[1].replaceAll("/","-");

		String save = jobName + " (" + surveyInfo[4] + ")";
		
		if (weightedAverage == 1) {
			reqWeightedAverage = true;
		} else {
			reqWeightedAverage = false;
		}
		System.out.println("HI");
		generateReport(surveyID,groupSection,deptID,divID,pkUser,fileName,type);

		// ---adding event viewer
		String[] UserInfo = U.getUserDetail(pkUser);

		try {
			String groupSect = "All";
			String deptName = "All";
			String divName = "All";

			if (groupSection != null)
				groupSect = getGroupName(groupSection);
			if (deptID != null)
				deptName = getDeptName(deptID);
			if (divID != 0)
				divName = getDivName();

			String temp = surveyInfo[4] + "(S); " + groupSect + "(G); "
					+ deptName + "(Dept); " + divName + "(Div)";

			EV.addRecord("Print","Rater Result",temp,UserInfo[2],UserInfo[11],
					UserInfo[10]);

		} catch (SQLException SE) {
			System.out.println(SE.getMessage());
		}

		return save;

	}

	/*********************** END - JSP FUNCTIONS *************************/

	/**
	 * Get the username based on targetID
	 * 
	 * @param int TargetID
	 * 
	 * @return String Username
	 */
	public String UserNameToyota(int targetID) throws SQLException {
		String query = "";
		String name = "";

		int nameSeq = Integer.parseInt(surveyInfo[5]); // 0=familyname first

		query = "SELECT FamilyName, GivenName FROM [User] WHERE PKUser = "
				+ targetID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				String family = rs.getString("FamilyName");
				String given = rs.getString("GivenName");

				if (nameSeq == 0)
					name = family + " " + given;
				else
					name = given + " " + family;
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - UserNameToyota - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		return name;
	}

	/**
	 * Get user's department based user id
	 * 
	 * @param int TargetID
	 * 
	 * @return String DepartmentName
	 */
	public String UserDepartment(int targetID) throws SQLException {
		String query = "";
		String sDepartment = "";

		query = "SELECT D.DepartmentCode AS DepartmentName FROM [User] U INNER JOIN Department D ON U.FKDepartment = D.PKDepartment ";
		query = query + "WHERE U.PKUser = " + targetID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next()) {
				sDepartment = rs.getString("DepartmentName");
			}

		} catch (Exception ex) {
			System.out.println("GroupReport.java - UserDepartment - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		return sDepartment;
	}

	public double PastCP(String RTCode) throws SQLException {
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C.ReliabilityCheck(surveyID);
		double dPastCP = 0;

		boolean bPastSurveyExist = false;

		/*
		 * Check whether there are any existing survey in the same Job Position
		 * as chosen survey
		 */
		query = "SELECT MAX(tblSurvey.SurveyID) AS SurveyID ";
		query = query
				+ "FROM tblSurvey INNER JOIN tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID ";
		query = query + "WHERE (JobPositionID = " + surveyInfo[10] + ") ";
		query = query + "AND tblSurvey.SurveyID < " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (rs.next() != false) {
				// rsPastSurvey.next();
				iPastSurveyID = rs.getInt("SurveyID");

				if (iPastSurveyID > 0) {
					query = "SELECT DISTINCT TargetLoginID FROM tblAssignment WHERE SurveyID = "
							+ iPastSurveyID;

					// Statement stmtID = db.con.createStatement();
					// ResultSet rsPastTarget = stmtID.executeQuery(query);
					// rsPastTarget.next();
				}

				bPastSurveyExist = true;
			}
			// ~~~ END Past Survey Exist ~~~

		} catch (Exception ex) {
			System.out
					.println("GroupReport.java - PastCP - " + ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		if (bPastSurveyExist == true) {
			if (surveyLevel == 0) {
				if (reliabilityCheck == 0) // trimmed mean
				{
					query = "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
					query += "ROUND(AVG(tblTrimmedMean.TrimmedMean), 2) AS Result FROM ";
					query += "tblTrimmedMean INNER JOIN Competency ON ";
					query += "tblTrimmedMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
					query += "tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
					query += "[User] ON [User].PKUser = tblTrimmedMean.TargetLoginID ";
					query += "WHERE tblTrimmedMean.SurveyID = " + iPastSurveyID;
					query += " AND tblTrimmedMean.Type = 1 AND tblRatingTask.RatingCode = '"
							+ RTCode + "' AND ";
					query += "tblTrimmedMean.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment INNER JOIN ";
					query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
					query += "WHERE SurveyID = " + surveyID
							+ " AND RaterCode <> 'SELF' AND ";
					query += "RaterStatus IN (1, 2, 4) ";

					if (divID != 0)
						query = query
								+ " AND tblAssignment.FKTargetDivision = "
								+ divID;
					// query = query + " AND [User].FKDivision = " + divID;
					if (deptID != 0)
						query = query
								+ " AND tblAssignment.FKTargetDepartment = "
								+ deptID;
					// query = query + " AND [User].FKDepartment = " + deptID;
					if (groupSection != 0)
						query = query + " AND tblAssignment.FKTargetGroup = "
								+ groupSection;
					// query = query + " AND [User].Group_Section = " +
					// groupSection;

					query += ") ";

					// if(deptID != 0)
					// query = query + " AND [User].FKDepartment = " + deptID;
					// if(divID != 0)
					// query = query + " AND [User].FKDivision = " + divID;
					// if(groupSection != 0)
					// query = query + " AND [User].Group_Section = " +
					// groupSection;

					query += " GROUP BY Competency.PKCompetency, Competency.CompetencyName ";
					query += "ORDER BY Competency.PKCompetency";

				} else {
					query = "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
					query += "ROUND(AVG(tblAvgMean.AvgMean), 2) AS Result FROM ";
					query += "tblAvgMean INNER JOIN Competency ON ";
					query += "tblAvgMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
					query += "tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID INNER JOIN ";
					query += "[User] ON [User].PKUser = tblAvgMean.TargetLoginID ";
					query += "WHERE tblAvgMean.SurveyID = " + surveyID;
					query += " AND tblAvgMean.Type = 1 AND tblRatingTask.RatingCode = '"
							+ RTCode + "' AND ";
					query += "tblAvgMean.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment INNER JOIN ";
					query += "[USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
					query += "WHERE SurveyID = " + iPastSurveyID
							+ " AND RaterCode <> 'SELF' AND ";
					query += "RaterStatus IN (1, 2, 4) ";

					if (divID != 0)
						query = query
								+ " AND tblAssignment.FKTargetDivision = "
								+ divID;
					// query = query + " AND [User].FKDivision = " + divID;
					if (deptID != 0)
						query = query
								+ " AND tblAssignment.FKTargetDepartment = "
								+ deptID;
					// query = query + " AND [User].FKDepartment = " + deptID;
					if (groupSection != 0)
						query = query + " AND tblAssignment.FKTargetGroup = "
								+ groupSection;
					// query = query + " AND [User].Group_Section = " +
					// groupSection;

					query += ") ";

					// if(deptID != 0)
					// query = query + " AND [User].FKDepartment = " + deptID;
					// if(divID != 0)
					// query = query + " AND [User].FKDivision = " + divID;
					// if(groupSection != 0)
					// query = query + " AND [User].Group_Section = " +
					// groupSection;

					query += " GROUP BY Competency.PKCompetency, Competency.CompetencyName ";
					query += "ORDER BY Competency.PKCompetency";

				}
			} else {
				if (reliabilityCheck == 1) {
					query = "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
					query += "ROUND(AVG(tblAvgMean.AvgMean), 2) AS Result ";
					query += "FROM tblAvgMean INNER JOIN Competency ON ";
					query += "tblAvgMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
					query += "tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
					query += " inner join [User] on [User].PKUser = tblAvgMean.TargetLoginID ";
					query += "WHERE tblAvgMean.SurveyID = " + surveyID;
					query += " AND tblAvgMean.Type = 1 AND tblRatingTask.RatingCode = '"
							+ RTCode + "'";
					query += " AND tblAvgMean.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment ";
					query += "INNER JOIN [USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
					query += "WHERE SurveyID = " + iPastSurveyID
							+ " AND RaterCode <> 'SELF' AND ";
					query += "RaterStatus IN (1, 2, 4) ";

					if (divID != 0)
						query = query
								+ " AND tblAssignment.FKTargetDivision = "
								+ divID;
					// query = query + " AND [User].FKDivision = " + divID;
					if (deptID != 0)
						query = query
								+ " AND tblAssignment.FKTargetDepartment = "
								+ deptID;
					// query = query + " AND [User].FKDepartment = " + deptID;
					if (groupSection != 0)
						query = query + " AND tblAssignment.FKTargetGroup = "
								+ groupSection;
					// query = query + " AND [User].Group_Section = " +
					// groupSection;

					query += ") ";

					// if(deptID != 0)
					// query = query + " AND [User].FKDepartment = " + deptID;
					// if(divID != 0)
					// query = query + " AND [User].FKDivision = " + divID;
					// if(groupSection != 0)
					// query = query + " AND [User].Group_Section = " +
					// groupSection;

					query += " GROUP BY Competency.PKCompetency, Competency.CompetencyName ";
					query += "ORDER BY Competency.PKCompetency";

				} else {
					query = "SELECT Competency.PKCompetency AS CompetencyID, Competency.CompetencyName, ";
					query += "ROUND(AVG(tblTrimmedMean.TrimmedMean), 2) AS Result ";
					query += "FROM tblTrimmedMean INNER JOIN Competency ON ";
					query += "tblTrimmedMean.CompetencyID = Competency.PKCompetency INNER JOIN ";
					query += "tblRatingTask ON tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
					query += " inner join [User] on [User].PKUser = tblTrimmedMean.TargetLoginID ";
					query += "WHERE tblTrimmedMean.SurveyID = " + surveyID;
					query += " AND tblTrimmedMean.Type = 1 AND tblRatingTask.RatingCode = '"
							+ RTCode + "'";
					query += " AND tblTrimmedMean.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment ";
					query += "INNER JOIN [USER] ON [USER].PKUser = tblAssignment.TargetLoginID ";
					query += "WHERE SurveyID = " + iPastSurveyID
							+ " AND RaterCode <> 'SELF' AND ";
					query += "RaterStatus IN (1, 2, 4) ";

					if (divID != 0)
						query = query
								+ " AND tblAssignment.FKTargetDivision = "
								+ divID;
					// query = query + " AND [User].FKDivision = " + divID;
					if (deptID != 0)
						query = query
								+ " AND tblAssignment.FKTargetDepartment = "
								+ deptID;
					// query = query + " AND [User].FKDepartment = " + deptID;
					if (groupSection != 0)
						query = query + " AND tblAssignment.FKTargetGroup = "
								+ groupSection;
					// query = query + " AND [User].Group_Section = " +
					// groupSection;

					query += ") ";

					// if(deptID != 0)
					// query = query + " AND [User].FKDepartment = " + deptID;
					// if(divID != 0)
					// query = query + " AND [User].FKDivision = " + divID;
					// if(groupSection != 0)
					// query = query + " AND [User].Group_Section = " +
					// groupSection;

					query += " GROUP BY Competency.PKCompetency, Competency.CompetencyName ";
					query += "ORDER BY Competency.PKCompetency";
				}
			}

			try {
				con = ConnectionBean.getConnection();
				st = con.createStatement();
				rs = st.executeQuery(query);

				if (rs.next()) {
					dPastCP = rs.getDouble("Result");
				}
				// ~~~ END Past Survey Exist ~~~

			} catch (Exception ex) {
				System.out.println("GroupReport.java - PastCP - "
						+ ex.getMessage());
			} finally {
				ConnectionBean.closeRset(rs); // Close ResultSet
				ConnectionBean.closeStmt(st); // Close statement
				ConnectionBean.close(con); // Close connection
			}

		} // bPastSurveyExist

		return dPastCP;
	}

	/**
	 * setExGroupRanking
	 * 
	 * @ param value
	 * 
	 * @author Denise by 14/12/09
	 * 
	 *         set the Exclude Group Ranking of the Group report
	 * */
	public void setExGroupRanking(int value) {
		exGR = value;
	}

	/**
	 * InsertProfileLegend
	 * 
	 * @author Denise by 14/12/09
	 * 
	 *         to insert Group report profile
	 * */
	public void InsertProfileLegend() throws Exception {

	
		int[] grpReport = OO.findString(xSpreadsheet,"<GROUP COMPETENCY PROFILE REPORT>"); 
		// find address of definition to delete	;
		
		int row = grpReport[1];
		
		
		if(deptID == 218){
			OO.findAndReplace(xSpreadsheet,"<DepartmentName>","LG PLUS Group's (Staff Department)");
		
		}else if(deptID==219){
			OO.findAndReplace(xSpreadsheet,"<DepartmentName>","LG PLUS Group's (Specialists)");
			int[] qw = OO.findString(xSpreadsheet,"<DepartmentName>"); 
			
		} else if(deptID==220){
			OO.findAndReplace(xSpreadsheet,"<DepartmentName>","LG PLUS Group's (Land Divisions)");
		
		} else{
			OO.findAndReplace(xSpreadsheet,"<DepartmentName>","LG PLUS Group's");
		
		}/*
		int[] addressCRP = OO.findString(xSpreadsheet,"<CPR>");
		int[] addressGAP = OO.findString(xSpreadsheet,"<GAP_DEF>");
		int[] addressOTHER = OO.findString(xSpreadsheet,"<OTHERS>");
		int[] addressIMP = OO.findString(xSpreadsheet,"<IMPORTANCE");
		int[] addressSUP = OO.findString(xSpreadsheet,"<CP_SUP>");
		// int [] addressPEER = OO.findString(xSpreadsheet, "<CP_PEER>");
		// int [] addressDIR = OO.findString(xSpreadsheet, "<CP_DIR>");
		// int [] addressIDR = OO.findString(xSpreadsheet, "<CP_IDR>");

		OO.findAndReplace(xSpreadsheet,"<CPR>","");
		OO.findAndReplace(xSpreadsheet,"<GAP_DEF>","");
		OO.findAndReplace(xSpreadsheet,"<OTHERS>","");
		OO.findAndReplace(xSpreadsheet,"<IMPORTANCE>","");
		OO.findAndReplace(xSpreadsheet,"<CP_SUP>","");
		// OO.findAndReplace(xSpreadsheet, "<CP_PEER>", "");
		// OO.findAndReplace(xSpreadsheet, "<CP_DIR>", "");
		// OO.findAndReplace(xSpreadsheet, "<CP_IDR>", "");

		int rowCPR = addressCRP[1];
		int rowGAP = addressGAP[1];
		int rowOTHER = addressOTHER[1];
		int rowImp = addressIMP[1];
		int rowSup = addressSUP[1];
		// int rowPeer = addressPEER[1];
		// int rowDir = addressDIR[1];
		// int rowIdr = addressIDR[1];

		char order = 'a';
		int totalDef = 8;

		int row = rowCPR + 1;

		// delete CPR and GAP if does not have CPR rating task
		if (!hasCPR) {
			totalDef = totalDef - 2;
			OO.deleteRows(xSpreadsheet,0,10,rowCPR,rowCPR + 4,4,1);
			OO.deleteRows(xSpreadsheet,0,10,rowGAP - 4,rowGAP - 1,3,1);
			rowOTHER -= 4;
			rowImp -= 4;
			rowSup -= 4;
		}

		if (!isImp) {// delete Importance
			totalDef = totalDef - 1;
			OO.deleteRows(xSpreadsheet,0,10,rowImp,rowImp + 3,3,1);
			rowImp = 0;
		}
		if (hide_others) {// delete others
			totalDef = totalDef - 1;
			OO.deleteRows(xSpreadsheet,0,10,rowOTHER,rowOTHER + 4,4,1);
			rowOTHER = 0;
		}

		if (hide_sup) { // Denise 06/01/2009 Delete Superior
			totalDef = totalDef - 1;
			OO.deleteRows(xSpreadsheet,0,10,rowSup,rowSup + 3,3,1);
			rowSup = 0;
		}
		/*
		 * if (hide_peer){ //Denise 06/01/2009 Delete Superior totalDef =
		 * totalDef - 1; OO.deleteRows(xSpreadsheet, 0, 10, rowPeer, rowPeer+3,
		 * 3, 1); rowPeer = 0; } if (hide_dir){ //Denise 06/01/2009 Delete
		 * Superior totalDef = totalDef - 1; OO.deleteRows(xSpreadsheet, 0, 10,
		 * rowDir, rowDir+3, 3, 1); rowDir = 0; } if (hide_idr){ //Denise
		 * 06/01/2009 Delete Superior totalDef = totalDef - 1;
		 * OO.deleteRows(xSpreadsheet, 0, 10, rowIdr, rowIdr+3, 3, 1); rowIdr =
		 * 0; }
		 */
/*
		for (int i = 0; i < totalDef; i++) // insert number ordering
		{
			OO.insertString(xSpreadsheet,order++ + "",row,column);
			if (hasCPR) {
				if (i == 0 || i == 1 || row == rowOTHER + 1)
					row += 4;
				else
					row += 3;
			} else {
				if (i == 0 || row == rowOTHER + 1)
					row += 4;
				else
					row += 3;
			}
		}*/
	}

	/**
	 * Calculates the Standard Deviation for a set of Double values
	 * 
	 * @author Desmond
	 * @since v1.3.12.59 (13 Jan 2010)
	 * @param Vector
	 *            containing values (in Double) to perform standard deviation on
	 * @return Standard deviation value in double
	 * */
	public double stdDev(Vector data) {
		// sd is sqrt of sum of (values-mean) squared divided by n - 1
		// Calculate the mean
		double mean = 0;
		final int n = data.size();

		if (n < 2) {
			return Double.NaN;
		}

		for (int i = 0; i < n; i++) {
			mean += ((Double) data.get(i)).doubleValue();
		}

		mean /= n;

		// calculate the sum of squares
		double sum = 0;
		for (int i = 0; i < n; i++) {
			final double v = ((Double) data.get(i)).doubleValue() - mean;
			sum += v * v;
		}

		// Change to ( n - 1 ) to n if you have complete data instead of a
		// sample.
		return Math.sqrt(sum / (n - 1));

	} // End stdDev()

	// Gwen Oh - 13/10/2011: Add method to set the users for subgroup report
	public void setSelectedUsers(String[] users) {
		selectedUsers = users;
	}

	// *****************MAIN************************************************/

	public static void main(String[] args) throws SQLException, Exception {
		GroupReportSPF IR = new GroupReportSPF();

		int surveyID = 475;
		int groupSection = 0;
		int deptID = 0;
		int divID = 0;

		long now = System.currentTimeMillis();
		// new java.util.Date(t1)

		// IR.Report(surveyID, groupSection, deptID, divID, 6404,
		// "GroupReport(PacRim Staff).xls", 2); //type = 1 (simple), 2 (full)
		// IR.ReportToyota(surveyID, groupSection, deptID, divID, 101,
		// "GroupReportToyota.xls");
		long then = System.currentTimeMillis();

		// IR.Report(568, groupSection, deptID, divID, 6403,
		// "GroupReport(TEST).xls", 1); //type = 1 (simple), 2 (full)
		IR.Report(487,509,66,1,6403,"Report11.xls",1);
		// System.out.println((then-now) / 1000);
		System.exit(1);

	}

	/*
	 * This method insert the rating scale into the report
	 * 
	 * @author: Liu Taichen created on: 3/July/2012
	 */
	public void InsertRatingScale() {
		int iRow = row;
		int iColumn = column;

		System.out.println("printing rating scale.");
		// row and colume specify where the rating scale is to be inserted/
		try {

			row = 56;
			column = 1;
			ExcelQuestionnaire eq = new ExcelQuestionnaire();

			int totalColumn = 12;
			int maxScale = eq.maxScale(surveyID) + 1;

			// int totalCells = totalColumn / maxScale;
			int totalCells = 2;
			int totalMerge = 0; // total cells to be merged after rounding
			double merge = 0; // total cells to be merged before rounding
			Vector v = eq.SurveyRating(surveyID);
			int count = 0;

			int[] scale = new int[2];
			scale[0] = 0;
			scale[1] = 0;

			try {
				OO.insertRows(xSpreadsheet,0,totalColumn,row,row + 1,1,1);
			} catch (Exception e) {
				System.out.println("it's here");
			}
			OO.insertString(xSpreadsheet,"Rating Scales used in this survey:",
					row,column);
			OO.setFontBold(xSpreadsheet,column,column,row,row);
			OO.mergeCells(xSpreadsheet,column,column + 4,row,row + 1);

			row++;
			OO.insertRows(xSpreadsheet,0,totalColumn,row,row + 1,1,1);
			OO.insertRows(xSpreadsheet,0,totalColumn,row,row + 1,1,1);

			for (int i = 0; i < 1; i++) {
				votblSurveyRating vo = (votblSurveyRating) v.elementAt(i);
				count++;

				boolean hideNA = Q.getHideNAOption(surveyID);
				String code = vo.getRatingCode();
				String ratingTask = vo.getRatingTaskName();
				int scaleID = vo.getScaleID();
				// Denise 29/12/2009 insert row here
				// OO.insertRows(xSpreadsheet, 0, 24, row, row+1, 1, 1);
				// OO.insertString(xSpreadsheet, count + ". " + ratingTask, row,
				// column);
				// OO.setFontBold(xSpreadsheet, column, column, row, row);

				// String statement = eq.RatingStatement(code);

				row++;
				OO.insertRows(xSpreadsheet,0,totalColumn,row,row + 1,1,1);
				scale[1] += 1;

				// OO.insertString(xSpreadsheet, statement, row, column+1);
				OO.mergeCells(xSpreadsheet,column + 1,totalColumn,row,row);

				// add rating scale
				row = row + 2;
				OO.insertRows(xSpreadsheet,1,1,row,row + 2,2,1);
				scale[1] += 2;

				int c = 1;
				int r = row;
				int to = c;

				Vector RS = Q.getRatingScale(scaleID);

				// OO.insertRows(xSpreadsheet, 0, 24, row, row+3, 3, 1);
				for (int j = 0; j < RS.size(); j++) {
					String[] sRS = new String[3];

					sRS = (String[]) RS.elementAt(j);

					int low = Integer.parseInt(sRS[0]);
					int high = Integer.parseInt(sRS[1]);
					String desc = sRS[2];
					// Denise 29/12/2009 to hide NA if required
					if (!(hideNA && (desc.equalsIgnoreCase("NA")
							|| desc.equalsIgnoreCase("N/A")
							|| desc.equals("Not applicable")
							|| desc.contains("NA") || desc.contains("N/A")
							|| desc.contains("Not applicable") || desc
								.contains("Not Applicable")))) {

						if (column + totalCells > totalColumn) {
							row += 2;
							column = 1;
							OO.insertRows(xSpreadsheet,0,totalColumn,row,
									row + 3,3,1);

							row += 1;
							
						}
						OO.insertString(xSpreadsheet,desc,row,column); // add in
																		// scale
																		// description
						OO.setCellAllignment(xSpreadsheet,column,column,row,
								row,1,2);
						OO.setCellAllignment(xSpreadsheet,column,column,row,
								row,2,2);

						r = row + 1;
						c = column;

						int start = c; // start merge cell
						String temp = "";

						while (low <= high) {
							if (low > 1)
								temp += "    ";
							temp = temp + Integer.toString(low);

							low++;
						}

						OO.insertString(xSpreadsheet,temp,r,c); // add in rating
																// scale value
						OO.setCellAllignment(xSpreadsheet,c,c,r,r,1,2);

						to = start + totalCells - 1; // merge cell for rating
														// scale value

						OO.mergeCells(xSpreadsheet,start,to,r,r);
						OO.setTableBorder(xSpreadsheet,start,to,r,r,true,true,
								true,true,true,true);

						OO.mergeCells(xSpreadsheet,start,to,row,row); // merge
																		// cell
																		// for
																		// rating
																		// scale
																		// description
						OO.setTableBorder(xSpreadsheet,start,to,row,row,true,
								true,true,true,true,true);
						OO.setBGColor(xSpreadsheet,start,to,row,row,BGCOLOR);

						merge = (double) desc.trim().length()
								/ (double) (totalCells);

						BigDecimal BD = new BigDecimal(merge);
						BD.setScale(0,BD.ROUND_UP);
						BigInteger BI = BD.toBigInteger();
						totalMerge = BI.intValue() + 1;

						OO.setRowHeight(xSpreadsheet,row,start,
								(150 * totalMerge));

						column = to + 1;
					}// end if to insert Rating scale
				}
				row = r + 2;
				// OO.insertRows(xSpreadsheet, 1, 1, row, row+3, 3, 1);
				scale[1] += 2;
				column = 0;
			}

			OO.insertPageBreak(xSpreadsheet,1,12,row);
		} catch (Exception e) {
			System.out.println(e);
		}
		row = iRow;
		column = iColumn;

	}
	/*
	 * This method insert the rating scale into the individual report
	 * 
	 * @author: Liu Taichen created on: 17/July/2012 modified by: Albert (18
	 * July 2012) changes: add new paragraph and dynamic placement of rating
	 * scale following the template
	 */
	public void InsertRatingScaleList() throws Exception {
		int address[] = OO.findString(xSpreadsheet,"<Rating Scale>");
		int CR = 0;
		OO.findAndReplace(xSpreadsheet,"<Rating Scale>","");
		int iColumn = address[0];
		int iRow = address[1];
		System.out.println("Printing Rating Scale.");
		// row and colume specify where the rating scale is to be inserted/
		try {

			// row = 88;
			// column = 0;
			ExcelQuestionnaire eq = new ExcelQuestionnaire();

			int totalColumn = 12;
			int maxScale = eq.maxScale(surveyID) + 1;

			Vector v = eq.SurveyRating(surveyID);
			int count = 0;

			try {
				OO.insertRows(xSpreadsheet,0,totalColumn,iRow,iRow + 1,1,1);
			} catch (Exception e) {
				System.out.println("it's here");
			}

			for (int i = 0; i < 1; i++) {
				votblSurveyRating vo = (votblSurveyRating) v.elementAt(i);
				count++;

				boolean hideNA = Q.getHideNAOption(surveyID);
				String code = vo.getRatingCode();
				String ratingTask = vo.getRatingTaskName();
				int scaleID = vo.getScaleID();

				Vector RS = Q.getRatingScale(scaleID);
				CR = RS.size() - 1;
				int tempIRow = iRow;
				int ratingScale = 0;
				// OO.mergeCells(xSpreadsheet, startColumn, totalColumn, iRow,
				// iRow );

				for (int j = 0; j < RS.size(); j++) {
					String[] sRS = new String[3];

					sRS = (String[]) RS.elementAt(j);

					int low = Integer.parseInt(sRS[0]);
					int high = Integer.parseInt(sRS[1]);
					String desc = sRS[2];
					// Denise 29/12/2009 to hide NA if required
					if (!(hideNA && (desc.equalsIgnoreCase("NA")
							|| desc.equalsIgnoreCase("N/A")
							|| desc.equals("Not applicable")
							|| desc.contains("NA") || desc.contains("N/A")
							|| desc.contains("Not applicable") || desc
								.contains("Not Applicable")))) {

						iRow += 1;
						column = 0;
						OO.insertRows(xSpreadsheet,0,totalColumn,iRow,iRow + 1,
								1,1);
						String temp = "";
						boolean firstTime = true;
						if (low == high) {
							temp += Integer.toString(low);
						} else {
							while (low <= high) {
								if (firstTime) {
									temp += Integer.toString(low) + " to ";
									firstTime = false;
								} else {
									if (low == high)
										temp += Integer.toString(low);
									low++;
								}
							}
						}
						ratingScale = high;
						OO.insertString(xSpreadsheet,
								(temp + "  -  " + desc).trim(),iRow,iColumn); // add
																				// in
																				// scale
																				// description
						OO.setCellAllignment(xSpreadsheet,iColumn,totalColumn,
								iRow,iRow,2,2);
						// OO.setCellAllignment(xSpreadsheet, column,
						// totalColumn, row, row, 2, 2);
						OO.mergeCells(xSpreadsheet,0,totalColumn,iRow,iRow);
					}// end if to insert Rating scale
				}
				String paragraph = "All the graphs in this report with a "
						+ ratingScale
						+ " point scale use the following rating scale description: ";
				OO.insertString(xSpreadsheet,paragraph,tempIRow,startColumn);
			}
			OO.findAndReplace(xSpreadsheet,"<CR>",Integer.toString(CR));
			// OO.insertPageBreak(xSpreadsheet, 1, 12, row + 2);
		} catch (Exception e) {
			System.out.println(e);
		}
		// row = iRow;
		// column = iColumn;
	}
	public void insertCompetencyRankTable() {
		Vector vComp = null;
		int row = 0;
		/*
		 * get clusters
		 */
		try {
			// OO.setFontSize(xSpreadsheet4, 0, 11, row, 1500, 12);
			// OO.setFontType(xSpreadsheet4, 0, 11, row, 1500,
			// "Times new Roman");
			Vector vClust = ClusterByName();
			for (int i = 0; i < vClust.size(); i++) {
				voCluster voClust = (voCluster) vClust.elementAt(i);
				String clusterName = voClust.getClusterName();
				int clusterID = voClust.getClusterID();
				/*
				 * Put Self under the sheet with the group ranking table as it
				 * has only one column
				 */
				if (clusterName.equalsIgnoreCase("SELF")) {
				
					int startRow = groupRankingTableRow;
					int column = 0;
					String info = "This Group Ranking Table lists the LG PLUS Feedback Recipients in ranked order based on their CP (All) scores ";
					info += "for each cluster and competency. The ranking is in descending order starting with the strongest LG PLUS Feedback Recipient.";
					/*
					 * Table must be on another page
					 */
					OO.insertPageBreak(xSpreadsheet4,startColumn,endColumn,
							startRow);

					OO.insertString(xSpreadsheet4,
							"GROUP RANKING TABLE BY CLUSTER AND COMPETENCY",
							startRow,column);
					OO.setFontBold(xSpreadsheet4,0,11,startRow,startRow);
					OO.setFontSize(xSpreadsheet4,0,11,startRow,startRow,16);
					startRow += 2;
					OO.mergeCells(xSpreadsheet4,column,column + 11,startRow,
							startRow + 1);

					OO.insertString(xSpreadsheet4,info,startRow,column);
					OO.setRowHeight(xSpreadsheet4,startRow,0,1120);
					startRow += 3;

					vComp = ClusterCompetencyByName(clusterID);
					voCompetency v1 = (voCompetency) vComp.elementAt(0);
					String competencyName = v1.getCompetencyName();

					int compID = v1.getCompetencyID();
					int startOfTable = startRow;

					OO.mergeCells(xSpreadsheet4,column,column + 1,startRow,
							startRow);
					OO.insertString(xSpreadsheet4,clusterName,startRow,column);
					OO.setCellAllignment(xSpreadsheet4,0,1,startRow,startRow,1,
							2);
					OO.setBGColor(xSpreadsheet4,0,1,startRow,startRow,
							BGCOLORCLUSTER);

					OO.mergeCells(xSpreadsheet4,column + 2,column + 11,
							startRow,startRow);
					OO.insertString(xSpreadsheet4,competencyName,startRow,
							column + 2);
					startRow++;

					OO.mergeCells(xSpreadsheet4,column,column + 1,startRow,
							startRow);
					OO.insertString(xSpreadsheet4,"Ranked",startRow,column);
					OO.setCellAllignment(xSpreadsheet4,0,1,startRow,startRow,1,
							2);
					OO.mergeCells(xSpreadsheet4,column + 2,column + 9,startRow,
							startRow);
					OO.insertString(xSpreadsheet4,"Name",startRow,column + 2);
					OO.mergeCells(xSpreadsheet4,column + 10,column + 11,
							startRow,startRow);
					OO.insertString(xSpreadsheet4,"CP Score",startRow,
							column + 10);

					OO.setBGColor(xSpreadsheet4,0,11,startRow,startRow,BGCOLOR);
				//	OO.setTableBorder(xSpreadsheet4,0,11,startRow,startRow,
					//		true,true,true,true,true,true);
					OO.setCellAllignment(xSpreadsheet4,10,11,startRow,startRow,
							1,2);

					startRow++;

					Vector rankingTable = getCPScoreRanking(compID,surveyID);

					rankingTable = sortClusterCompetency(rankingTable);
					/*
					 * Each element returns: [0] = Individual's Name [1] =
					 * CP(All) Scores [2] = targetID
					 */

					for (int k = 0; k < rankingTable.size(); k++) {
						String[] individualScorePair = (String[]) rankingTable
								.elementAt(k);

						int target = Integer.parseInt(individualScorePair[2]);
						OO.mergeCells(xSpreadsheet4,0,1,startRow,startRow);
						OO.insertNumeric(xSpreadsheet4,(k + 1),startRow,0);
						OO.setCellAllignment(xSpreadsheet4,0,1,startRow,
								startRow,1,2);
						/*
						 * Blank out Name of target that  is not the user of this
						 * individual Report
						 */
						OO.mergeCells(xSpreadsheet4,2,4,startRow,startRow);
						OO.insertString(xSpreadsheet4,individualScorePair[0],
								startRow,2);
						OO.insertNumeric(xSpreadsheet4,Double.parseDouble(individualScorePair[1]),
								startRow,10);
						
						OO.mergeCells(xSpreadsheet4,column + 10,column + 11,
								startRow,startRow);
						OO.setCellAllignment(xSpreadsheet4,10,11,startRow,
								startRow,1,2);

						startRow++;
						
						String[] comparison = new String[3];
						comparison[0]= individualScorePair[0];
						comparison[1]=individualScorePair[1];
						comparison[2] = competencyName;
						
						comparisonClusterRankTableVector.add(comparison);

					}
					OO.setTableBorder(xSpreadsheet4,0,11,startOfTable,
							startRow - 1,false,false,true,true,true,true);

				} else {
					OO.mergeCells(xSpreadsheet5,0,2,row,row);
					OO.insertString(xSpreadsheet5,clusterName,row,0);
					OO.setBGColor(xSpreadsheet5,0,2,row,row,BGCOLORCLUSTER);
					/*
					 * Return a vector with the Competencies in the particular
					 * cluster.
					 */
					vComp = ClusterCompetencyByName(clusterID);
					vComp = sortClusterCompetencyOrder(vComp);

					/*
					 * For use later to fill in the 1st row of the next cluster
					 * with people.
					 */
					int numberOfPeople = 0;

					int competencyStartCol = 3;
					int startRow = row;
					/*
					 * Start of running through the competencies
					 */
					Vector clusterScores = new Vector();
					for (int j = 0; j < vComp.size(); j++) {
						/*
						 * Within a cluster, iterate through the vector to get
						 * the data out for each competency
						 * 
						 * @listing = the row which will be running based on the
						 * number of people participating in the survey.
						 */

						int listing = startRow;
						voCompetency v1 = (voCompetency) vComp.elementAt(j);
						String competencyName = v1.getCompetencyName();
						int compID = v1.getCompetencyID();
						/*
						 * Insert the header - Name and CPScore
						 */
						OO.mergeCells(xSpreadsheet5,competencyStartCol,
								competencyStartCol + 1,listing,listing);
						OO.insertString(xSpreadsheet5,competencyName,listing,
								competencyStartCol);
					
						OO.setFontBold(xSpreadsheet5,competencyStartCol,
								competencyStartCol + 1,listing,listing);
						OO.setCellAllignment(xSpreadsheet5,competencyStartCol,
								competencyStartCol + 1,listing,listing,1,2);
						listing++;
						OO.insertString(xSpreadsheet5,"Name",listing,
								competencyStartCol);
						OO.insertString(xSpreadsheet5,"CP Score",listing,
								competencyStartCol + 1);
						OO.setBGColor(xSpreadsheet5,competencyStartCol,
								competencyStartCol + 1,listing,listing,BGCOLOR);
						listing++;
						/*
						 * Query the database to get the CP(All) scores for the
						 * particular Competency for everyone in descending
						 * order
						 */

						Vector rankingTable = getCPScoreRanking(compID,surveyID);
						rankingTable = sortClusterCompetency(rankingTable);
						/*
						 * Each element returns: [0] = Individual's Name [1] =
						 * CP(All) Scores [2] = targetID
						 */
						numberOfPeople = rankingTable.size();

						for (int k = 0; k < rankingTable.size(); k++) {
							String[] individualScorePair = (String[]) rankingTable
									.elementAt(k);

							int target = Integer
									.parseInt(individualScorePair[2]);
							/*
							 * Blank out Name is target is not the user of this
							 * individual Report
							 */
						
							OO.insertString(xSpreadsheet5,
									individualScorePair[0],listing,
									competencyStartCol);

							OO.insertNumeric(xSpreadsheet5,
									Math.round(Double.parseDouble(individualScorePair[1])*100.00)/100.00,listing,
									competencyStartCol + 1);

							listing++;
							
							String[] comparison = new String[3];
							comparison[0]= individualScorePair[0];
							comparison[1]=individualScorePair[1];
							comparison[2] = competencyName;
							
							comparisonClusterRankTableVector.add(comparison);
							
							boolean isExist = false;
							int index = 0;
							double updatedScore = Double.parseDouble(individualScorePair[1]);
							for (int m = 0; m < clusterScores.size(); m++) {
								String[] existingIndividualScorePair = (String[]) clusterScores
										.elementAt(m);
								if (existingIndividualScorePair[0].equalsIgnoreCase(individualScorePair[0])) {
									isExist = true;
									index = m;
									updatedScore += Double.parseDouble(existingIndividualScorePair[1]);
									updatedScore = Math.round(updatedScore*100.00)/100.00;
								}
							}
							String[] updateRecord = new String[3];
							updateRecord[0] = individualScorePair[0];
							updateRecord[1] = String.valueOf(updatedScore);
							updateRecord[2] = individualScorePair[2];
							if (isExist == true) {
								clusterScores.set(index,updateRecord);
							} else {
								clusterScores.add(individualScorePair);
							}
						}
						competencyStartCol += 2;
					}
					/*
					 * Insert the values for the clusters.
					 * 
					 * 
					 */					
					
					
					OO.insertString(xSpreadsheet5,"Name",startRow + 1,1);
					OO.insertString(xSpreadsheet5,"CP Score",startRow + 1,2);
					OO.setBGColor(xSpreadsheet5,0,1 + 1,startRow + 1,
							startRow + 1,BGCOLOR);
					int insertClusterScores = startRow + 2;
					clusterScores = sortClusterCompetency(clusterScores);
					for (int p = 0; p < clusterScores.size(); p++) {
						OO.insertString(xSpreadsheet5,(p + 1) + ". ",
								insertClusterScores,0);
						String[] clusterScoreTable = (String[]) clusterScores
								.elementAt(p);
						int target = Integer.parseInt(clusterScoreTable[2]);

						OO.insertString(xSpreadsheet5,clusterScoreTable[0],
								insertClusterScores,1);
						OO.insertNumeric(xSpreadsheet5,Math.round((Double.parseDouble(clusterScoreTable[1])/ vComp.size())*100.00)/100.00,insertClusterScores,2);

						insertClusterScores++;
					}
					row += numberOfPeople;
					/*
					 * end column will be "minus 2" due to the fact that it adds 2 at the end of each competency
					 */
					OO.setTableBorder(xSpreadsheet5,0,competencyStartCol -1,startRow,row+1,true,false,true,true,true,true);
					OO.setTableBorder(xSpreadsheet5,0,competencyStartCol -1,startRow,startRow,true,false,true,true,true,true);

					
					row += 3;
				}
			}

		} catch (Exception e) {
			System.out
					.println("insertCompetencyRankTable - SPFGrpReport.java"
							+ e);
		}
	}

	public Vector sortClusterCompetency(Vector v) {
		Vector sorted = new Vector();
		int size = v.size();
		while (sorted.size() < size) {
			double maxValue = 0;
			String targetName = "";
			int index = 0;
			for (int i = 0; i < v.size(); i++) {
				String[] current = (String[]) v.elementAt(i);
				double score = Double.parseDouble(current[1]);
				if (score > maxValue) {
					maxValue = score;
					targetName = current[0];
					index = i;
				}
			}

			sorted.add(v.get(index));
			v.remove(index);
		}
		return sorted;
	}
	public Vector getCPScoreRanking(int compID, int surveyID) {
		Vector v = new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String query = "Select FamilyName,GivenName ,round(AvgMean,2) as result ,targetLoginID from tblAvgMean a";
		query += "inner join Competency b on CompetencyID=PKCompetency ";
		query += "inner join [User] on targetLoginID = PKUser where CompetencyID ="
				+ compID + " and SurveyID=" + surveyID + " and TYPE =10";
		if (divID != 0)
			query = query + " AND FKTargetDivision = "
					+ divID;

		if (deptID != 0)
			query = query + " AND FKDepartment = "
					+ deptID;

	
		query += "  order by AvgMean DESC   ";
		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs != null && rs.next()) {
				String[] nameScorePair = new String[3];
				nameScorePair[0] = rs.getString("FamilyName") + " "	+ rs.getString("GivenName");
				nameScorePair[1] = Double.toString((Math.round(rs.getDouble("result") * 100.0) / 100.00));
				nameScorePair[2] = rs.getString("targetLoginID");
			
				v.add(nameScorePair);
			}
			
		} catch (Exception E) {
			System.err
					.println("Groupreport.java - getCPScoreRanking - "
							+ E);
		} finally {
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
		return v;
	}
	public Vector ClusterByName() throws SQLException {
		String query = "";
		Vector v = new Vector();

		query = query
				+ "SELECT tblSurveyCluster.ClusterID, Cluster.ClusterName ";
		query = query + "FROM tblSurveyCluster INNER JOIN Cluster ON ";
		query = query + "tblSurveyCluster.ClusterID = Cluster.PKCluster ";
		query = query + "WHERE tblSurveyCluster.SurveyID = " + surveyID;
		query = query + " ORDER BY tblSurveyCluster.ClusterID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				voCluster vo = new voCluster();
				vo.setClusterID(rs.getInt("ClusterID"));
				vo.setClusterName(rs.getString("ClusterName"));
				v.add(vo);
			}

		} catch (Exception ex) {
			System.out.println("SPFIndividualReport.java - ClusterByName - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}
	public Vector ClusterCompetencyByName(int clusterID) throws SQLException {
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		Vector v = new Vector();

		if (surveyLevel == 0) {
			query = query
					+ "SELECT Cluster.ClusterName, tblSurveyCompetency.CompetencyID, Competency.CompetencyName, ";
			query = query
					+ "CompetencyDefinition FROM tblSurveyCompetency INNER JOIN Competency ON ";
			query = query
					+ "tblSurveyCompetency.CompetencyID = Competency.PKCompetency INNER JOIN Cluster ON Cluster.PKCluster = tblSurveyCompetency.ClusterID ";
			query = query + "WHERE tblSurveyCompetency.SurveyID = " + surveyID
					+ " AND tblSurveyCompetency.ClusterID = " + clusterID;
			query = query
					+ " ORDER BY Cluster.ClusterName, Competency.CompetencyName";

		} else {

			query = query
					+ "SELECT DISTINCT Cluster.ClusterName, tblSurveyBehaviour.CompetencyID, Competency.CompetencyName, ";
			query = query
					+ "Competency.CompetencyDefinition FROM Competency INNER JOIN ";
			query = query
					+ "tblSurveyBehaviour ON Competency.PKCompetency = tblSurveyBehaviour.CompetencyID ";
			query = query
					+ "INNER JOIN Cluster ON Cluster.PKCluster = tblSurveyBehaviour.ClusterID ";
			query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID
					+ "AND tblSurveyBehaviour.ClusterID = " + clusterID;
			query = query
					+ " ORDER BY Cluster.ClusterName, Competency.CompetencyName";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				voCompetency vo = new voCompetency();
				int compId = rs.getInt("CompetencyID");
				String compName = rs.getString("CompetencyName");
				String compDef = rs.getString("CompetencyDefinition");

				if (compName.equals("Performing as a Team")) {
					int tempId = compId;
					String tempName = compName;
					String tempDef = compDef;
					if (rs.next()) {
						vo.setCompetencyID(rs.getInt("CompetencyID"));
						vo.setCompetencyName(rs.getString("CompetencyName"));
						vo.setCompetencyDefinition(rs
								.getString("CompetencyDefinition"));
					}
					voCompetency vo1 = new voCompetency();
					vo1.setCompetencyID(tempId);
					vo1.setCompetencyName(tempName);
					vo1.setCompetencyDefinition(tempDef);
					v.add(vo);
					v.add(vo1);

					break;
				}

				if (compName.equals("Communicating Effectively")) {
					int tempId = compId; // this is for communicating
											// effectively
					String tempName = compName;
					String tempDef = compDef;
					voCompetency vo1 = new voCompetency();
					voCompetency vo2 = new voCompetency();
					vo1.setCompetencyID(tempId);
					vo1.setCompetencyName(tempName);
					vo1.setCompetencyDefinition(tempDef);
					if (rs.next()) {
						compId = rs.getInt("CompetencyID"); // for building
															// partnerships
						compName = rs.getString("CompetencyName");
						compDef = rs.getString("CompetencyDefinition");
						vo2.setCompetencyID(compId);
						vo2.setCompetencyName(compName);
						vo2.setCompetencyDefinition(compDef);
					}
					if (rs.next()) {
						vo.setCompetencyID(rs.getInt("CompetencyID")); // for
																		// using
																		// heartskills
						vo.setCompetencyName(rs.getString("CompetencyName"));
						vo.setCompetencyDefinition(rs
								.getString("CompetencyDefinition"));
						v.add(vo);
					}
					v.add(vo1);
					v.add(vo2);
					break;
				}

				vo.setCompetencyID(compId);
				vo.setCompetencyName(compName);
				vo.setCompetencyDefinition(compDef);
				v.add(vo);

			}

		} catch (Exception ex) {
			System.out
					.println("SPFIndividualReport.java - ClusterCompetencyByName - "
							+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}
	public Vector sortClusterCompetencyOrder(Vector vComp) {
		Vector v = new Vector(3);
		HashMap<Integer, voCompetency> hm1 = new HashMap<Integer, voCompetency>();

		for (int i = 0; i < vComp.size(); i++) {

			voCompetency voComp = (voCompetency) vComp.elementAt(i);
			String competency = voComp.getCompetencyName();

			if (competency.equalsIgnoreCase("Personal mastery")
					|| competency.equalsIgnoreCase("Planning and Prioritising")
					|| competency.equalsIgnoreCase("Solving Problems")
					|| competency
							.equalsIgnoreCase("Unleashing People Potential")
					|| competency.equalsIgnoreCase("Using HeartSkills")) {
				hm1.put(1,voComp);
			} else if (competency
					.equalsIgnoreCase("Focusing on Mission and Vision")
					|| competency.equalsIgnoreCase("Systems Thinking")
					|| competency.equalsIgnoreCase("Performing as a Team")
					|| competency.equalsIgnoreCase("Communicating Effectively")) {
				hm1.put(2,voComp);
			} else if (competency.equalsIgnoreCase("Facilitating Change")
					|| competency.equalsIgnoreCase("Creating and Innovating")
					|| competency.equalsIgnoreCase("Building Partnerships")) {
				hm1.put(3,voComp);
			}
		}
		for (int j = 0; j < vComp.size(); j++) {
			voCompetency v1 = hm1.get(j + 1);
			v.add(v1);
		}

		return v;
	}
	public int sortCompetency(String comp) {

		int compLocation = 0;

		if (comp.equalsIgnoreCase("Personal Mastery")) {
			compLocation = 0;
		} else if (comp.equalsIgnoreCase("Planning and Prioritising")) {
			compLocation = 1;
		} else if (comp.equalsIgnoreCase("Focusing on Mission and Vision")) {
			compLocation = 2;
		} else if (comp.equalsIgnoreCase("Facilitating Change")) {
			compLocation = 3;
		} else if (comp.equalsIgnoreCase("Solving Problems")) {
			compLocation = 4;
		} else if (comp.equalsIgnoreCase("Systems Thinking")) {
			compLocation = 5;
		} else if (comp.equalsIgnoreCase("Creating and Innovating")) {
			compLocation = 6;
		} else if (comp.equalsIgnoreCase("Unleashing People Potential")) {
			compLocation = 7;
		} else if (comp.equalsIgnoreCase("Performing as a Team")) {
			compLocation = 8;
		} else if (comp.equalsIgnoreCase("Using Heartskills")) {
			compLocation = 9;
		} else if (comp.equalsIgnoreCase("Communicating Effectively")) {
			compLocation = 10;
		} else if (comp.equalsIgnoreCase("Building Partnerships")) {
			compLocation = 11;
		}

		return compLocation;

	}

	public void InsertStrDistributionTable() throws Exception {

		// find address of definition to delete
		int[] address = OO.findString(xSpreadsheet,"<DISTRIBUTION TABLE>");

		// int [] addressPEER = OO.findString(xSpreadsheet, "<CP_PEER>");
		// int [] addressDIR = OO.findString(xSpreadsheet, "<CP_DIR>");
		// int [] addressIDR = OO.findString(xSpreadsheet, "<CP_IDR>");

		OO.findAndReplace(xSpreadsheet,"<DISTRIBUTION TABLE>","");

		int row = address[1];

		vCPValues = G.sorting(vCPValues,1); // added to sort CPValues, Mark
		// Oei 16 April 2010
		

		Vector v = ClusterByName();
		int tableStart =row;
		for (int i = 0; i < v.size(); i++) {
			voCluster vCluster = (voCluster) v.elementAt(i);
			String clusterName = vCluster.getClusterName();
		
			int clusterID = vCluster.getClusterID();
			int clusterNameRow = row;
			double totalCP = 0;
			double totalGCP = 0;
			// Allow dynamic translation, Chun Yeong 1 Aug 2011
			OO.mergeCells(xSpreadsheet,0,8,row,row);
			OO.insertString(xSpreadsheet,clusterName,row,0);
			OO.setFontBold(xSpreadsheet,0,8,row,row);
	
		
			int clusterNameRows = row;
			// OO.insertString(xSpreadsheet, "CP", row, cpCol);
			if (clusterName.equalsIgnoreCase("SELF")){
				OO.insertString(xSpreadsheet,"S",row,9);
				OO.insertString(xSpreadsheet,"ME",row,10);
				OO.insertString(xSpreadsheet,"DA",row,11);
				OO.setCellAllignment(xSpreadsheet,9,11,row,row,1,2);
				OO.setFontBold(xSpreadsheet,8,115,row,row);
				//OO.setTableBorder(xSpreadsheet,0,11,clusterNameRows,clusterNameRows+1,false,false,true,true,true,true);
				//OO.setBGColor(xSpreadsheet,0,8,row,row,BGCOLORCLUSTER);
			}

			OO.insertRows(xSpreadsheet, startColumn, endColumn, row,
					row + 1, 1, 1);
			row++;

			/*
			 * Get all the competency based on this cluster.
			 */
			Vector vComp = ClusterCompetencyByName(clusterID);
			/*
			 * Sort the competency in order of SPF Competencies
			 */
			vComp = sortClusterCompetencyOrder(vComp);
			int numOfComp = 0;

			for (int k = 0; k < vComp.size(); k++) {
				double cpValues = 0;
				int strength=0;
				int meetExpectations=0;
				int devAreas =0;

				OO.insertRows(xSpreadsheet, startColumn, endColumn, row,
						row + 1, 1, 1);
				voCompetency voComp = (voCompetency) vComp.elementAt(k);

				int compID = voComp.getCompetencyID();

				String compName = voComp.getCompetencyName();
				Vector scores = getCompetencyDistribution(compID);
			
				for(int a=0;a<scores.size();a++){
					double score = (double) scores.elementAt(a);
						if(score>5.1){
							strength++;
							
						}else if(score>=4.5 && score<5.1){
							meetExpectations++;
						} else{
							devAreas++;
						}
				}
				
			
					OO.mergeCells(xSpreadsheet,0,7,row,row);
					OO.insertString(xSpreadsheet,compName.toString(),row,0);
					OO.setRowHeight(xSpreadsheet,row,1,	ROWHEIGHT* OO.countTotalRow(compName.toString(),	90));
					
					OO.insertNumeric(xSpreadsheet,strength,row,9);
					OO.insertNumeric(xSpreadsheet,meetExpectations,row,10);
					OO.insertNumeric(xSpreadsheet,devAreas,row,11);
					OO.setFontNormal(xSpreadsheet,0,11,row,row);
					OO.setFontSize(xSpreadsheet,9,11,row,row,12);
					OO.setCellAllignment(xSpreadsheet,9,11,row,row,1,2);
					// Insert GCP values next to Gap, Chun Yeong 29 Jun
					// 2011
					

				
					row++;
				
				}
			OO.setBGColor(xSpreadsheet,0,11,clusterNameRow,clusterNameRow,BGCOLORCLUSTER);
			OO.setTableBorder(xSpreadsheet,startColumn,11,
					clusterNameRow,clusterNameRow,false,false,true,true,true,true);
			OO.setTableBorder(xSpreadsheet,startColumn,11,
					tableStart,tableStart,true,true,true,true,true,true);
			OO.insertRows(xSpreadsheet, startColumn, endColumn, row,
					row + 1, 1, 1);
				//row++;
			}
		OO.setTableBorder(xSpreadsheet,startColumn,11,
				tableStart,row,false,false,true,true,true,true);
		OO.setTableBorder(xSpreadsheet,9,11,
				tableStart,row,true,false,true,true,true,true);

	

	}
	public String[] sortClusterCompetencyOrder(String clusterName) {
		String[] v = new String[3];

		if (clusterName.equalsIgnoreCase("SELF")) {
			v[0] = "Personal Mastery";
		} else if (clusterName.equalsIgnoreCase("ACTION")) {
			v[0] = "Planning and Prioritising";
			v[1] = "Focusing on Mission and Vision";
			v[2] = "Facilitating Change";
		} else if (clusterName.equalsIgnoreCase("DELIBERATIONS")) {
			v[0] = "Solving Problems";
			v[1] = "Systems Thinking";
			v[2] = "Creating and Innovating";
		} else if (clusterName.equalsIgnoreCase("EMPLOYEES")) {
			v[0] = "Unleashing People Potential";
			v[1] = "Performing as a Team";
		} else if (clusterName.equalsIgnoreCase("RELATIONSHIP BUILDING")) {
			v[0] = "Using Heartskills";
			v[1] = "Communicating Effectively";
			v[2] = "Building Partnerships";
		}

		return v;
	}
	
	public Vector getCompetencyDistribution(int compID) throws SQLException {
		String query = "";
		Vector v = new Vector();
		int type =1;
		if(reqWeightedAverage ==true){
			type=10;
		}
		query = "SELECT * ";
		query = query + "FROM tblAvgMean inner join [user] on TargetLoginID=PKUser where surveyID =  "+ surveyID;
		query = query + " and type= " + type + " and competencyID = " + compID;
		if(deptID!=0){
			query+=" and FKDepartment = "+ deptID;
		}
		



		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				double result = rs.getDouble("AvgMean");
				v.add(result);
			}

		} catch (Exception ex) {
			System.out.println("GroupReportSPF.java - getCompetencyDistribution - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}
	
	public void printRawData(){
		int row =0;
		
		try{
		Vector allTargets = getAllTargets();
	
		Vector totalScore = new Vector();
		for(int i=0; i<allTargets.size();i++){
			Vector allCompetency = getCompetency(0);
			String[] info = (String[])allTargets.elementAt(i);
			
			OO.insertString(xSpreadsheet6,info[1],row,0);
			
			row+=3;
			int topRow = row;
			
			int numberOfRaters=0;
			/*
			 * Competecncy
			 */
			for(int j=0;j<allCompetency.size();j++){
				voCompetency vo = (voCompetency) allCompetency.elementAt(j);
				String competencyName = vo.getCompetencyName();
				int compID = vo.getCompetencyID();
				OO.insertString(xSpreadsheet6,competencyName,row,0);
				Vector breakDown = getBreakDown(Integer.parseInt(info[0]),compID);
				numberOfRaters = breakDown.size();
				/*
				 * Rater
				 */
				for(int k=0;k<breakDown.size();k++){
					String[] scores = (String[])breakDown.elementAt(k);
					if(j==0){
						OO.insertString(xSpreadsheet6,scores[1],row-1,k+1);
						
					}
					OO.insertNumeric(xSpreadsheet6,Double.parseDouble(scores[2]),row,k+1);
				}
				row++;
			}
			numberOfRaters++;
			Vector groupScore = getTotalNumberOfGroups(Integer.parseInt(info[0]));
			for(int l=0; l<groupScore.size();l++){
				int group = (Integer) groupScore.elementAt(l);
				Vector groupCP = getGroupCPAll(group,Integer.parseInt(info[0]));
				int topRowG=topRow;
				for(int m=0;m<groupCP.size();m++){
					double score = (Double) groupCP.elementAt(m);
					OO.insertNumeric(xSpreadsheet6,score,topRowG,numberOfRaters+1);
					if(m==0){
						if(group==2){
						OO.insertString(xSpreadsheet6,"Superior",topRowG-1,numberOfRaters+1);
						} else if(group==4){
							OO.insertString(xSpreadsheet6,"SELF",topRowG-1,numberOfRaters+1);
						} else if(group==6){
							OO.insertString(xSpreadsheet6,"PEERS",topRowG-1,numberOfRaters+1);
						} else if(group==8){
							OO.insertString(xSpreadsheet6,"DIRECT",topRowG-1,numberOfRaters+1);
						} else if(group==9){
							OO.insertString(xSpreadsheet6,"INDIRECT",topRowG-1,numberOfRaters+1);
						} 
					}
						
					
					topRowG++;
				}
				numberOfRaters++;
			}
			
			Vector dataBaseCPAll = getCPAllRaw(Integer.parseInt(info[0]));
			for(int l=0; l<dataBaseCPAll.size();l++){
				String[] infom = (String[])dataBaseCPAll.elementAt(l);
				
				OO.insertNumeric(xSpreadsheet6,Double.parseDouble(infom[1]),topRow,numberOfRaters+1);
				if(l==0){
					OO.insertString(xSpreadsheet6,"CP(All)",topRow-1,numberOfRaters+1);
				}
				topRow++;
			}
			
			row++;
			int groupStartRow = 2;
			/*
			 * insert for that particular person name
			 */
			OO.insertString(xSpreadsheet7,info[1]+ "\n CP(ALL)",groupStartRow-1,i*2+1);
			OO.setFontSize(xSpreadsheet7,i+1,i+1,groupStartRow-1,groupStartRow-1,8);
			OO.wrapText(xSpreadsheet7,i+1,i+1,groupStartRow-1,groupStartRow-1);
			
			/*
			 * list all the competency's cpALL
			 */
			for(int l=0; l<dataBaseCPAll.size();l++){
				String[] infom = (String[])dataBaseCPAll.elementAt(l);
				/*
				 * 
				 */
				if(i==0){
					/*
					 *insert Competency's name at column 0
					 */
					OO.insertString(xSpreadsheet7,infom[2],groupStartRow,0);
					Vector cpAll= getCPCPR("CP");
					
					int cpAllRow =2;
					/*
					 * Add in the CPAll for each mastery
					 */
					int compareClusterGraphRow =2;
					int compareGapRow =2;
					int compareCPGraphRow =2;
					for(int t=0; t<cpAll.size();t++){
						
						voRatingResult vo = (voRatingResult)cpAll.elementAt(t);
						if(vo.getCompetencyName().equalsIgnoreCase(infom[2])){
							if(l==0){
							OO.insertString(xSpreadsheet7,"CP(All)" ,cpAllRow-1,allTargets.size()*2+1);
							OO.insertString(xSpreadsheet7,"Comparison With ClusterCompetencyGraph" ,cpAllRow-1,allTargets.size()*2+2);
							OO.insertString(xSpreadsheet7,"Comparison With GapTable" ,cpAllRow-1,allTargets.size()*2+3);
							OO.insertString(xSpreadsheet7,"Comparison With CPGraph" ,cpAllRow-1,allTargets.size()*2+4);
							}
							OO.wrapText(xSpreadsheet7,allTargets.size()*2+2,allTargets.size()*2+4,cpAllRow-1,cpAllRow-1);
								
							OO.insertNumeric(xSpreadsheet7,vo.getResult(),groupStartRow,allTargets.size()*2+1);
						
							
						}
					
						if(l==0){
							
							for(int s=0; s<comparisonHashMapVector.size();s++){
								HashMap<Integer,Double> hash = (HashMap<Integer,Double>)comparisonHashMapVector.elementAt(s);
								
								if(hash.containsKey(vo.getCompetencyID())){
									
									double scoreResultFromGraph = hash.get(vo.getCompetencyID());
									if(scoreResultFromGraph!=vo.getResult()){
										OO.insertString(xSpreadsheet7,"False" ,compareClusterGraphRow,allTargets.size()*2+2);
									}else{
										OO.insertString(xSpreadsheet7,"True" ,compareClusterGraphRow,allTargets.size()*2+2);
									}
									compareClusterGraphRow++;
								}
								
							}
						}
						if(l==0){
							
							for(int s=0; s<comparisonGapTableVector.size();s++){
								HashMap<String,Double> hash = (HashMap<String,Double>)comparisonGapTableVector.elementAt(s);
								
								if(hash.containsKey(vo.getCompetencyName())){
									
									double scoreResultFromGraph = hash.get(vo.getCompetencyName());
									if(scoreResultFromGraph!=vo.getResult()){
										OO.insertString(xSpreadsheet7,"False" ,compareGapRow,allTargets.size()*2+3);
									}else{
										OO.insertString(xSpreadsheet7,"True" ,compareGapRow,allTargets.size()*2+3);
									}
									compareGapRow++;
								}
								
							}
						}
						if(l==0){
							
							for(int s=0; s<comparisonCPTableVector.size();s++){
								HashMap<String,Double> hash = (HashMap<String,Double>)comparisonCPTableVector.elementAt(s);
								
								if(hash.containsKey(vo.getCompetencyName())){
									
									double scoreResultFromGraph = hash.get(vo.getCompetencyName());
									if(scoreResultFromGraph!=vo.getResult()){
										OO.insertString(xSpreadsheet7,"False" ,compareCPGraphRow,allTargets.size()*2+4);
									}else{
										OO.insertString(xSpreadsheet7,"True" ,compareCPGraphRow,allTargets.size()*2+4);
									}
									compareCPGraphRow++;
								}
								
							}
						}
					
					}
				}
				/*
				 * Insert the CP All score for that person for that competency
				 */
				OO.insertNumeric(xSpreadsheet7,Double.parseDouble(infom[1]),groupStartRow,i*2+1);
				boolean isExist=false;
				int index=0;
				double score =0;
				for(int d=0;d<totalScore.size();d++){
					String[] ha = (String[]) totalScore.elementAt(d);
					if(ha[0].equalsIgnoreCase(info[1])){
						isExist=true;
						index=d;
						score=Double.parseDouble(ha[1]);
						
					}
				}
				if(isExist==true){
					String[] la = new String[2];
					la[0]=info[1];
					double updatedScore= score+= Double.parseDouble(infom[1]);
					la[1]=String.valueOf(updatedScore);
					totalScore.set(index,la);
					
				}else{
					String[] la = new String[2];
					la[0]=info[1];
					la[1]=infom[1];
					totalScore.add(la);
				}
				/*
				 * COmparison check with clusters ranking table
				 */
				for(int z=0;z<comparisonClusterRankTableVector.size();z++){
					String[] check =(String[])comparisonClusterRankTableVector.elementAt(z);
					if(check[2].equalsIgnoreCase(infom[2]) && check[0].equalsIgnoreCase(info[1])){
						if( Math.round(Double.parseDouble(infom[1])*100.00)/100.00 == Double.parseDouble(check[1])){
							
							OO.insertString(xSpreadsheet7,"True" ,groupStartRow,i*2+2);
						}else{
							OO.insertString(xSpreadsheet7,"False" ,groupStartRow,i*2+2);
						}
					}
				}
				
			
				groupStartRow++;
				
			}
			
			for(int a=0;a<totalScore.size();a++){
				String[] totalSco =(String [] ) totalScore.elementAt(a);
				OO.insertNumeric(xSpreadsheet7,Double.parseDouble(totalSco[1]) ,groupStartRow+1,(a*2)+1);
				boolean isExist = false;
				double score = 0;
				
					
			}
	
		
		}
	
		
		}catch (Exception e){
			System.out.println("GroupReport.java - printRawData : "+ e);
			e.printStackTrace();
		}
	}
	public Vector getBreakDown(int targetLoginId, int compID) throws SQLException {
		String query = "";
		Vector v = new Vector();
		query+= "SELECT GivenName,familyname,RaterCode,result";
		  query+=" FROM tblResultCompetency inner join tblAssignment on"; 
		  query+=" tblAssignment.AssignmentID=tblResultCompetency.AssignmentID Inner join [user] on TargetLoginID=PKUser";
		   query+=" where CompetencyID = "+ compID+"  and TargetLoginID=" + targetLoginId;
		   query+= "order by RaterCode";
		  



		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String[] info =new String[3];
				info[0] = rs.getString("familyname")+ " "+ rs.getString("GivenName");
				info[1] = rs.getString("RaterCode");
				info[2] = String.valueOf(rs.getDouble("result"));
				v.add(info);
			}

		} catch (Exception ex) {
			System.out.println("GroupReportSPF.java - getBreakDown - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}
	public Vector getCPAllRaw(int targetLoginId) throws SQLException {
		String query = "";
		Vector v = new Vector();
		query+= "select CompetencyID,round(AvgMean,2) as result, competencyName from tblAvgMean inner join competency on competencyID = pkcompetency where SurveyID=" + surveyID ;
				
		if(targetLoginId!=0){
			query+=" and TargetLoginID=" + targetLoginId;
		}

		query+= "  and TYPE =10";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next()) {
				String[] info =new String[3];
				info[0] = String.valueOf(rs.getInt("CompetencyID"));
				info[1] = String.valueOf(rs.getDouble("result"));
				info[2] = rs.getString("competencyName");
				
				v.add(info);
			}

		} catch (Exception ex) {
			System.out.println("GroupReportSPF.java - getBreakDown - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return v;
	}
	public Vector getTotalNumberOfGroups(int surveyID) throws SQLException {
		Vector total = new Vector();

		String query = "select distinct type as result from tblAvgMean where surveyID="
				+ surveyID + " and Type<>1 and Type<>10 and type<>4  order by result";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next())
				total.add(rs.getInt("result"));

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTotalTarget - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return total;
	}
	public Vector getGroupCPAll(int type,int targetID) throws SQLException {
		Vector total = new Vector();

		String query = " select CompetencyID,AvgMean from tblAvgMean where TargetLoginID=" + targetID+" and type=" + type + " order by competencyID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			while (rs.next())
				total.add(rs.getDouble("AvgMean"));

		} catch (Exception ex) {
			System.out.println("GroupReport.java - getTotalTarget - "
					+ ex.getMessage());
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}

		return total;
	}
	public void InsertSuveyOverview(int surveyID) {
		try {
			// insert the additional questions header
			int[] address = OO.findString(xSpreadsheet, "<SurveyOverview>");

			column = address[0];
			int tableRow = address[1];
			int startRow = tableRow;
			OO.findAndReplace(xSpreadsheet, "<SurveyOverview>", "");
			// OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);

			
			OO.mergeCells(xSpreadsheet, column, column + 2, tableRow, tableRow + 1);
			OO.insertString(xSpreadsheet,
					 "Relationship", tableRow, column);
			// OO.setFontSize(xSpreadsheet, startColumn, endColumn, tableRow, tableRow,
			// 16);
			

			OO.mergeCells(xSpreadsheet, column + 3, column + 6, tableRow, tableRow);
			OO.insertString(xSpreadsheet,
					 "Questionnaire", tableRow,
					column + 3);

			

		
			tableRow++;
			System.out.println("Col : "+ column);
			System.out.println("tableRow : "+ tableRow);

			OO.mergeCells(xSpreadsheet, column + 3, column + 4, tableRow, tableRow);
			OO.insertString(xSpreadsheet,"Distributed", tableRow,column + 3);
			OO.mergeCells(xSpreadsheet, column + 5, column + 6, tableRow, tableRow);
			OO.insertString(xSpreadsheet, "Received", tableRow, column + 5);

			

		

			/*
			 * Calculate the number of groups that rated on this person
			 */
			tableRow++;

			Vector numberOfGroupsOfUsers = getTotalNumberOfGroups(surveyID);
			
			String raterCode = "";
			String groupName = "";
			int self = -1;
			for (int i = 0; i < numberOfGroupsOfUsers.size(); i++) {

				int type = (Integer) numberOfGroupsOfUsers.elementAt(i);
				if (type == 2) {
					raterCode = "SUP%";
					groupName = "Superior";
				} else if (type == 4) {
					raterCode = "SELF";
					groupName = "Self";
					self = 1;
					continue;
				} else if (type == 6) {
					raterCode = "PEER%";
					groupName = "Peers";
				} else if (type == 8) {
					raterCode = "DIR%";
					groupName = "Direct";
				} else if (type == 9) {
					raterCode = "IDR%";
					groupName = "Indirect";
				}
				int[] distribution = getSurveyRaterStatus(surveyID, raterCode);
				System.out.println(distribution.length+ "haha");
				if (distribution[0] != 0) {
					OO.mergeCells(xSpreadsheet, column, column + 2, tableRow, tableRow);
					OO.insertString(xSpreadsheet, groupName, tableRow, column);
					OO.mergeCells(xSpreadsheet, column + 3, column + 4, tableRow,
							tableRow);
					OO.insertString(xSpreadsheet,
							String.valueOf(distribution[0]), tableRow, column + 3);
					OO.mergeCells(xSpreadsheet, column + 5, column + 6, tableRow,
							tableRow);
					OO.insertString(xSpreadsheet,
							String.valueOf(distribution[1]), tableRow, column + 5);
					

				} else {
					/*
					 * handle in the event that the individual is not rated by a
					 * particular group of raters
					 */
					OO.mergeCells(xSpreadsheet, column, column + 2, tableRow, tableRow);
					OO.insertString(xSpreadsheet, groupName, tableRow, column);
					OO.mergeCells(xSpreadsheet, column + 3, column + 4, tableRow,
							tableRow);
					OO.insertString(xSpreadsheet, "0", tableRow, column + 3);
					OO.mergeCells(xSpreadsheet, column + 5, column + 6, tableRow,
							tableRow);
					OO.insertString(xSpreadsheet, "0", tableRow, column + 5);
				
				}

				tableRow++;
			}
			if (self != -1) {
				OO.mergeCells(xSpreadsheet, column, column + 2, tableRow, tableRow);
				OO.insertString(xSpreadsheet, "Self", tableRow, column);
				OO.mergeCells(xSpreadsheet, column + 3, column + 4, tableRow, tableRow);
				OO.insertString(xSpreadsheet, "1", tableRow, column + 3);
				OO.mergeCells(xSpreadsheet, column + 5, column + 6, tableRow, tableRow);
				OO.insertString(xSpreadsheet, "1", tableRow, column + 5);
				OO.mergeCells(xSpreadsheet, column + 7, column + 10, tableRow, tableRow);
				OO.insertString(xSpreadsheet, "N.A.", tableRow, column + 7);
			}
			int endRow = tableRow;
			
			OO.setCellAllignment(xSpreadsheet, 0, 11, startRow - 1, endRow-1, 1,
					2);
			OO.setFontSize(xSpreadsheet, 0, 11, startRow - 1, endRow-1, 12);
			OO.setTableBorder(xSpreadsheet, 0, 6, startRow, endRow-1, true,
					true, true, true, true, true);

		} catch (Exception e) {

		}
	}
	public int[] getSurveyRaterStatus(int surveyID, String raterCode) {
		{
			int[] results = new int[2];
			Connection con = null;
			Statement st = null;
			ResultSet rs = null;
			String query = "SELECT count(ratercode) as Distribution, count(raterStatus) as Rated from tblAssignment where surveyID=" + surveyID+ " and raterCode like '"+ raterCode+"'";
			query+= " and raterStatus =1";

			try {

				con = ConnectionBean.getConnection();
				st = con.createStatement();
				rs = st.executeQuery(query);

				while (rs != null && rs.next()) {
					results[0] = rs.getInt("Distribution");
					results[1] = rs.getInt("Rated");
				}

			} catch (Exception E) {
				System.err
						.println("PrelimQuestionController.java - updatePrelimQnHeader - "
								+ E);
			} finally {
				ConnectionBean.closeStmt(st); // Close statement
				ConnectionBean.close(con); // Close connection
			}
			return results;
		}

	}
	/*
	 * Search fro start Date and end date of survey
	 */
	public String[] getSurveyDates() {
			
			String[] results = new String[2];
			Connection con = null;
			Statement st = null;
			ResultSet rs = null;
			String query = "select dateopened,deadlinesubmission from tblsurvey where surveyID=" + surveyID;

			try {

				con = ConnectionBean.getConnection();
				st = con.createStatement();
				rs = st.executeQuery(query);

				while (rs != null && rs.next()) {
					results[0] = rs.getString("dateopened");
					results[1] = rs.getString("deadlinesubmission");
				}

			} catch (Exception E) {
				System.err
						.println("PrelimQuestionController.java - updatePrelimQnHeader - "
								+ E);
			} finally {
				ConnectionBean.closeStmt(st); // Close statement
				ConnectionBean.close(con); // Close connection
			}
			return results;
	}

	
}
