package CP_Classes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voCompetency;
import CP_Classes.vo.voKeyBehaviour;
import CP_Classes.vo.votblAssignment;
import CP_Classes.vo.votblRelationHigh;
import CP_Classes.vo.votblScaleValue;
import CP_Classes.vo.votblSurveyRating;

import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.XTableChart;

/**
 * This class implements all the operations for Individual Report in Excel.
 * It implements OpenOffice API.
 */  

/*****
 * 
 * Edited By Roger 13 June 2008
 * Add additional orgId when calling sendMail
 *
 */
public class IndividualReport_NoCPR
{ 	
	private Calculation C;
	private Questionnaire Q;
	private OpenOffice OO;
	private GlobalFunc G;
	private SurveyResult SR;
	private RaterRelation RR;
	private ExcelQuestionnaire EQ;
	private MailHTMLStd EMAIL;
	private Setting ST;
	private RatingScale rscale;

	private Vector vGapSorted;	// this is to store the gap of each competency so does not need to reopen another resultset
	private Vector vGapUnsorted;
	private Vector vCompID;
	private Vector vCompName;
	
	// These 4 vectors below are for Development Map
	private Vector Q1 = new Vector();
	private Vector Q2 = new Vector();
	private Vector Q3 = new Vector();
	private Vector Q4 = new Vector();
	
	private int surveyID;
	private int targetID;
	private int iPastSurveyID;		// For Toyota combined report
	private	int iPastTargetLogin;	// For Toyota combined report
	private int iCancel = 0;		// If user cancelled the printing process. 0=Not cancelled, 1=Cancelled
	private String surveyInfo [];
	private int arrN []; //To print N (No of Raters) for Simplified report
	
	private final int BGCOLOR = 12632256;
	private final int ROWHEIGHT = 560;
	
	private XMultiComponentFactory xRemoteServiceManager = null;
	private XComponent xDoc = null;
	private XSpreadsheet xSpreadsheet0 = null;
	private XSpreadsheet xSpreadsheet = null;
	private XSpreadsheet xSpreadsheet2 = null;
    private String storeURL;
	
	private int row;
	private int column;
	private int startColumn;
	private int endColumn;
	private int iReportType; //1=Simplified Report "No Competencies charts", 2=Standard Report
	private int iNoCPR = 1;	 //0=CPR is chosen for survey, 1=No CPR chosen for survey
	private int totalColumn = 12;
	
	/**
	 * Creates a new intance of IndividualReport object.
	 */
	public IndividualReport_NoCPR()
	{
		ST 	= new Setting();
		C 	= new Calculation();
		Q 	= new Questionnaire();
		OO	= new OpenOffice();
		G	= new GlobalFunc();
		SR	= new SurveyResult();
		RR  = new RaterRelation();
		EQ  = new ExcelQuestionnaire();
		ST	= new Setting();
		EMAIL = new MailHTMLStd();
		rscale = new RatingScale();
		
		vGapSorted = new Vector();
		vGapUnsorted = new Vector();
		vCompID = new Vector();
		vCompName = new Vector();
		
		startColumn = 0;
		endColumn = 12;
	}
	
	/**
	 * Retrieves the survey details and stores in an array.
	 */
	public String [] SurveyInfo() throws SQLException
	{
		String [] info = new String[9];
		
		String query = "SELECT tblSurvey.LevelOfSurvey, tblJobPosition.JobPosition, tblSurvey.AnalysisDate, ";
		query = query + "[User].FamilyName, [User].GivenName, tblOrganization.NameSequence, tblSurvey.SurveyName, ";
		query = query + "tblOrganization.OrganizationName, tblOrganization.OrganizationLogo FROM ";
		query = query + "tblSurvey INNER JOIN tblJobPosition ON ";
		query = query + "tblSurvey.JobPositionID = tblJobPosition.JobPositionID INNER JOIN ";
		query = query + "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
		query = query + "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
		query = query + "tblOrganization ON tblSurvey.FKOrganization = tblOrganization.PKOrganization ";
		query = query + "WHERE tblSurvey.SurveyID = " + surveyID;
		query = query + " AND tblAssignment.TargetLoginID = " + targetID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		
  		   if(rs.next()) {
  			   for(int i=0; i<9; i++) {
  				   info[i] = rs.getString(i+1);		
  				   
  			   }
  		   }
			
  	   	}catch(Exception ex){
			System.out.println("IndividualReport.java - SurveyInfo - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return info;
	}	
	
	/**
	 * 	Retrieves the survey details and stores in an array (For Toyota combined report)
	 *
	 *	@return Array String	Survey Details (Group, Dept, Job Pos., Rater Name etc...)
	 */
	public String [] SurveyInfoToyota() throws SQLException
	{
		String [] info = new String[11];
		
		String query = "SELECT Surv.LevelOfSurvey, JobPos.JobPosition, [User].IDNumber, [User].FamilyName, [User].GivenName, ";
		query = query + "Org.NameSequence, Surv.SurveyName, Org.OrganizationName, Dept.DepartmentName, Grp.GroupName, Surv.JobPositionID ";
		query = query + "FROM tblSurvey Surv INNER JOIN ";
		query = query + "tblJobPosition JobPos ON Surv.JobPositionID = JobPos.JobPositionID INNER JOIN ";
		query = query + "tblAssignment Assign ON Surv.SurveyID = Assign.SurveyID INNER JOIN ";
		query = query + "[User] ON Assign.TargetLoginID = [User].PKUser INNER JOIN ";
		query = query + "tblOrganization Org ON Surv.FKOrganization = Org.PKOrganization INNER JOIN ";
		query = query + "tblConsultingCompany Comp ON Surv.FKCompanyID = Comp.CompanyID AND [User].FKCompanyID = Comp.CompanyID AND ";
		query = query + "Org.FKCompanyID = Comp.CompanyID INNER JOIN [Group] Grp ON [User].Group_Section = Grp.PKGroup INNER JOIN ";
		query = query + "Department Dept ON [User].FKDepartment = Dept.PKDepartment AND Org.PKOrganization = Dept.FKOrganization ";
		query = query + "WHERE Surv.SurveyID = " + surveyID;
		query = query + " AND Assign.TargetLoginID = " + targetID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		
  		   if(rs.next()) {
		
		
  			   for(int i=0; i<11; i++)
  				   info[i] = rs.getString(i+1);										
		
  		   }
			
  	   	}catch(Exception ex){
			System.out.println("IndividualReport.java - SurveyInfoToyota - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return info;
	}
	
	/**
	 * Initializes all processes dealing with Survey.
	 */
	public void InitializeSurvey(int surveyID, int targetID, String fileName) throws SQLException, IOException
	{
		//System.out.println("Initialize Survey");
		
		column = 0;
		this.surveyID = surveyID;
		this.targetID = targetID;
		
		surveyInfo = new String [9];
		surveyInfo = SurveyInfo();
		
		//System.out.println("Initialize Survey Completed");
	}	
	
	/**
	 * 	Initializes all processes dealing with Survey. (For Toyota combined report)
	 */
	public void InitializeSurveyToyota(int surveyID, int targetID, String fileName) throws SQLException, IOException
	{
		column = 0;
		this.surveyID = surveyID;
		this.targetID = targetID;
		
		surveyInfo = new String [10];
		surveyInfo = SurveyInfoToyota();		
	}
	
	/**
	 * Get the username based on the name sequence.
	 */
	public String UserName() 
	{
		String name = "";
		
		int nameSeq = Integer.parseInt(surveyInfo[5]);	//0=familyname first

		String familyName = surveyInfo[3];
		String GivenName = surveyInfo[4];
				
		if(nameSeq == 0)
			name = familyName + " " + GivenName;
		else
			name = GivenName + " " + familyName;
			
		return name;		
	}
	
	/**
	 * Retrieves competencies under the surveyID.
	 */
	public Vector CompetencyByName() throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		Vector v = new Vector();
		
		if(surveyLevel == 0) {
			query = query + "SELECT tblSurveyCompetency.CompetencyID, Competency.CompetencyName, ";
			query = query + "CompetencyDefinition FROM tblSurveyCompetency INNER JOIN Competency ON ";
			query = query + "tblSurveyCompetency.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblSurveyCompetency.SurveyID = " + surveyID;
			query = query + " ORDER BY Competency.CompetencyName";
			
		} else {
			
			query = query + "SELECT DISTINCT tblSurveyBehaviour.CompetencyID, Competency.CompetencyName, ";
			query = query + "Competency.CompetencyDefinition FROM Competency INNER JOIN ";
			query = query + "tblSurveyBehaviour ON Competency.PKCompetency = tblSurveyBehaviour.CompetencyID ";
			query = query + "AND Competency.PKCompetency = tblSurveyBehaviour.CompetencyID ";
			query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID;
			query = query + " ORDER BY Competency.CompetencyName";
		}


		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		
  		   while(rs.next()) {
  			   voCompetency vo = new voCompetency();
  			   vo.setCompetencyID(rs.getInt("CompetencyID"));
  			   vo.setCompetencyName(rs.getString("CompetencyName"));
  			   vo.setCompetencyDefinition(rs.getString("CompetencyDefinition"));
  			   v.add(vo);
		
  		   }
			
  	   	}catch(Exception ex){
			System.out.println("IndividualReport.java - CompetencyByName - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}

	/**
	 * 	Retrieves competencies under the surveyID.
	 *	@param int iOrder	0 = Ascending, 1 = Descending
	 *
	 *	@return ResultSet Competency
	 */
	public Vector Competency(int iOrder) throws SQLException
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);

		Vector v = new Vector();
		if(surveyLevel == 0) {
			query = query + "SELECT tblSurveyCompetency.CompetencyID, Competency.CompetencyName, ";
			query = query + "CompetencyDefinition FROM tblSurveyCompetency INNER JOIN Competency ON ";
			query = query + "tblSurveyCompetency.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblSurveyCompetency.SurveyID = " + surveyID;
			
			//Changed by HA  07/07/08 Order should be by Competency Name instead of CompetencyID
			if (iOrder == 0)
				query = query + " ORDER BY Competency.CompetencyName";
			else
				query = query + " ORDER BY Competency.CompetencyName DESC";
			
		} else {
			
			query = query + "SELECT DISTINCT tblSurveyBehaviour.CompetencyID, Competency.CompetencyName, ";
			query = query + "Competency.CompetencyDefinition FROM Competency INNER JOIN ";
			query = query + "tblSurveyBehaviour ON Competency.PKCompetency = tblSurveyBehaviour.CompetencyID ";
			query = query + "AND Competency.PKCompetency = tblSurveyBehaviour.CompetencyID ";
			query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID;
			
			//Changed by Ha 02/07/08 Order by CompetencyName instead of CompetencyID
			//Problem with old query: It is ordered by competency ID while the respective
			//value is ordered by Competency name. Therefore, name and value do not match
			if (iOrder == 0)
				query = query + " ORDER BY Competency.CompetencyName";
			else
				query = query + " ORDER BY Competency.CompetencyName DESC";
			
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		
  		   while(rs.next()) {
  			   voCompetency vo = new voCompetency();
  			   vo.setCompetencyID(rs.getInt("CompetencyID"));
  			   vo.setCompetencyName(rs.getString("CompetencyName"));
  			   vo.setCompetencyDefinition(rs.getString("CompetencyDefinition"));
  			   v.add(vo);
		
  		   }

  	   	}catch(Exception ex){
			System.out.println("IndividualReport.java - Competency - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	/**
	 * Retrieves Key Behaviour lists based on CompetencyID.
	 */
	public Vector KBList(int compID) throws SQLException 
	{
		String query = "SELECT DISTINCT tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour ";
		query = query + "FROM tblSurveyBehaviour INNER JOIN KeyBehaviour ON ";
		query = query + "tblSurveyBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
		query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID + " AND ";
		query = query + "tblSurveyBehaviour.CompetencyID = " + compID;
		query = query + " ORDER BY tblSurveyBehaviour.KeyBehaviourID";

		Vector v = new Vector();
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		
  		   while(rs.next()) {
  			   voKeyBehaviour vo = new voKeyBehaviour();
  			   vo.setKeyBehaviourID(rs.getInt("KeyBehaviourID"));
  			   vo.setKeyBehaviour(rs.getString("KeyBehaviour"));
  			   v.add(vo);
		
  		   }
			
  	   	}catch(Exception ex){
			System.out.println("IndividualReport.java - KBLIst - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
	
		return v;
	}
	
	/**
	 * Retrieve all the rating task assigned to the specific survey.
	 */
	public Vector RatingTask()  throws SQLException 
	{
		// Changed by Ha 27/05/08: add keyword DISTINCT into the query
		String query = "SELECT DISTINCT tblSurveyRating.RatingTaskID, tblRatingTask.RatingCode, ";
		query = query + "tblSurveyRating.RatingTaskName FROM tblSurveyRating INNER JOIN ";
		query = query + "tblRatingTask ON tblSurveyRating.RatingTaskID = tblRatingTask.RatingTaskID ";			
		query = query + "WHERE tblSurveyRating.SurveyID = " + surveyID;
		query = query + " and tblRatingTask.RatingCode in('CP', 'CPR', 'FPR')";
		query = query + " ORDER BY tblSurveyRating.RatingTaskID";
		
		Vector v = new Vector();
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		
  		   while(rs.next()) {
  			  votblSurveyRating vo = new votblSurveyRating();
  			  vo.setRatingTaskID(rs.getInt("RatingTaskID"));
  			  vo.setRatingCode(rs.getString("RatingCode"));
  			  vo.setRatingTaskName(rs.getString("RatingTaskName"));
  			  v.add(vo);
  		   }
			
  	   	}catch(Exception ex){
			System.out.println("IndividualReport.java - RatingTask - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}

		return v;
	}
	
	/**
	 * Get the maximum scale, which is to be used in the alignment process.
	 */
	public int MaxScale() throws SQLException 
	{	
		int total = 0;
		
		String query = "SELECT MAX(tblScale.ScaleRange) AS Result FROM ";
		query = query + "tblScale INNER JOIN tblSurveyRating ON ";
		query = query + "tblScale.ScaleID = tblSurveyRating.ScaleID WHERE ";
		query = query + "tblSurveyRating.SurveyID = " + surveyID;
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
			if(rs.next())
				total = rs.getInt(1);

  		}catch(Exception ex){
			System.out.println("IndividualReport.java - MaxScale - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	/**
	 * Count the total competencies in the particular survey.
	 */
	public int totalCompetency() throws SQLException {
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		
		int total = 0;
	
		if(surveyLevel == 0) {
			query = query + "SELECT  COUNT(CompetencyID) AS Total FROM tblSurveyCompetency ";
			query = query + "WHERE SurveyID = " + surveyID;
		}else {
			query = query + "SELECT COUNT(DISTINCT CompetencyID) AS Total FROM ";
			query = query + "tblSurveyBehaviour WHERE SurveyID = " + surveyID;
		}
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalCompetency - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}

	/**by Hemilda 23/09/2008
	 * Count total Others for the particular survey and target.
	 * for KB level
	 * To calculate number of others rater  for each rating task of each KB
	 */	
	public int totalOth1(int iRatingTaskID, int iCompetencyID) throws SQLException 
	{	
		int total = 0;
		SurveyResult SR = new SurveyResult();
		Calculation cal = new Calculation();
		String query = "select max(table1.Cnt)AS Total ";
		query = query + " From( ";
		query = query + " SELECT     COUNT(tblAssignment.RaterCode) AS Cnt,tblResultBehaviour.KeyBehaviourID ";
		query = query + " FROM         tblAssignment INNER JOIN ";
		query = query + " tblResultBehaviour ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID INNER JOIN ";
		query = query + " KeyBehaviour ON tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
		query = query + " WHERE     (tblAssignment.SurveyID =  " + surveyID + ") AND (tblAssignment.TargetLoginID = "+targetID+") " ;
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND RaterCode LIKE 'OTH%' and RaterStatus in(1,2,4)";
		else
			query = query + " AND RaterCode LIKE 'OTH%' and RaterStatus in(1,2,4,5)";
		query = query + "  AND (tblResultBehaviour.RatingTaskID = "+iRatingTaskID+")and (KeyBehaviour.FKCompetency = "+iCompetencyID +") ";
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND (tblResultBehaviour.Result <> 0)";   
		query = query + "  group by tblResultBehaviour.KeyBehaviourID ";
		query = query + "  ) table1 ";


		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalOth - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
//System.out.println(">>>>>>>>> "+query);	
		return total;

	}
	
	/**
	 * Count total Others for the particular survey and target.
	 * Code edited by Ha adding RatingTask and Competency to method signature
	 * To calculate number of others rater  for each rating task of each competency
	 */
	public int totalOth(int iRatingTaskID, int iCompetencyID) throws SQLException 
	{	
		int total = 0;
		SurveyResult SR = new SurveyResult();
		Calculation cal = new Calculation();
		//Query changed by Ha 07/07/08 to calculate number of others rated for this target
		//exlcluded who put NA in their questionnaire if the survey is NA_Excluded
		String query = "SELECT COUNT(RaterCode) AS Total FROM tblAssignment ";
		query = query + " INNER JOIN  tblResultCompetency ON tblAssignment.AssignmentID = tblResultCompetency.AssignmentID ";
		query = query + "WHERE SurveyID = " + surveyID + " AND TargetLoginID = " + targetID;
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND RaterCode LIKE 'OTH%' and RaterStatus in(1,2,4)";
		else
			query = query + " AND RaterCode LIKE 'OTH%' and RaterStatus in(1,2,4,5)";
		query = query + " AND tblResultCompetency.RatingTaskID = "+iRatingTaskID;
		query = query + " AND tblResultCompetency.CompetencyID = "+iCompetencyID;
		if (cal.NAIncluded(surveyID)==0)
			query = query + "AND tblResultCompetency.Result <> 0";
		

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalOth - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
//System.out.println(">>>>>>>>> "+query);	
		return total;

	}
	
	/**by Hemilda date 23/09/2008
	 * Count total Others for the particular survey and target.
	 * for KB level
	 * To calculate number of others rater  for each rating task of each competency
	 */	
	public int totalOth(int iRatingTaskID, int iCompetencyID,int iKBId) throws SQLException 
	{	
		int total = 0;
		SurveyResult SR = new SurveyResult();
		Calculation cal = new Calculation();

		String query = "SELECT     COUNT(tblAssignment.RaterCode) AS Total ";
		query = query + " FROM         tblAssignment INNER JOIN ";
		query = query + "  tblResultBehaviour ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID ";
		query = query + "  WHERE     (tblAssignment.SurveyID =  "+surveyID+") AND (tblAssignment.TargetLoginID ="+targetID+")";
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND RaterCode LIKE 'OTH%' and RaterStatus in(1,2,4)";
		else
			query = query + " AND RaterCode LIKE 'OTH%' and RaterStatus in(1,2,4,5)";
		query = query + " AND (tblResultBehaviour.RatingTaskID ="+iRatingTaskID+") AND (tblResultBehaviour.KeyBehaviourID = "+iKBId+") ";
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND (tblResultBehaviour.Result <> 0)";                      

		

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalOth - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
//System.out.println(">>>>>>>>> "+query);	
		return total;

	}
	/** by Hemilda 23/09/2008
	 * Count total Supervisors for the particular survey and target.
	 * for KB level
	 * To calculate number of supervisor rater  for each rating task of each KB
	 */
	public int totalSup1(int iRatingTaskID, int iCompetencyID) throws SQLException 
	{	
		int total = 0;
		SurveyResult SR = new SurveyResult();
		Calculation cal = new Calculation();
		String query = "select max(table1.Cnt)AS Total ";
		query = query + " From( ";
		query = query + " SELECT     COUNT(tblAssignment.RaterCode) AS Cnt,tblResultBehaviour.KeyBehaviourID ";
		query = query + " FROM         tblAssignment INNER JOIN ";
		query = query + " tblResultBehaviour ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID INNER JOIN ";
		query = query + " KeyBehaviour ON tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
		query = query + " WHERE     (tblAssignment.SurveyID =  " + surveyID + ") AND (tblAssignment.TargetLoginID = "+targetID+") " ;
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND RaterCode LIKE 'SUP%' and RaterStatus in(1,2,4)";
		else
			query = query + " AND RaterCode LIKE 'SUP%' and RaterStatus in(1,2,4,5)";
		query = query + "  AND (tblResultBehaviour.RatingTaskID = "+iRatingTaskID+")and (KeyBehaviour.FKCompetency = "+iCompetencyID +") ";
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND (tblResultBehaviour.Result <> 0)";   
		query = query + "  group by tblResultBehaviour.KeyBehaviourID ";
		query = query + "  ) table1 ";


		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalSup - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
//System.out.println(">>>>>>>>> "+query);	
		return total;

	}
	/**by Hemilda 23/09/2008
	 * Count total Supervisors for the particular survey and target.
	 *KB level
	 * To calculate number of supervisor rater  for each rating task of each competency
	 */
	public int totalSup(int iRatingTaskID, int iCompetencyID,int iKBId) throws SQLException 
	{
		int total = 0;
		Calculation cal  = new Calculation();

		String query = "SELECT COUNT(RaterCode) AS Total FROM tblAssignment ";
		query = query + " INNER JOIN tblResultBehaviour ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID ";
		query = query + "WHERE SurveyID = " + surveyID + " AND TargetLoginID = " + targetID;
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND RaterCode LIKE 'SUP%' and RaterStatus in(1,2,4)";
		else
			query = query + "AND RaterCode LIKE 'SUP%' and RaterStatus in(1,2,4,5)";
		query = query + " AND tblResultBehaviour.RatingTaskID = "+iRatingTaskID;
		query = query + " AND tblResultBehaviour.KeyBehaviourID = "+iKBId;
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND tblResultBehaviour.Result <> 0";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalSup - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	/**
	 * Count total Supervisors for the particular survey and target.
	 * Code edited by Ha adding RatingTask and Competency to method signature
	 * To calculate number of supervisor rater  for each rating task of each competency
	 */
	public int totalSup(int iRatingTaskID, int iCompetencyID) throws SQLException 
	{
		int total = 0;
		Calculation cal  = new Calculation();
		//Query changed by Ha 07/07/08 to calculate number of supervisor rated for this target
		//exlcluded who put NA in their questionnaire if survey is NA_Excluded
		String query = "SELECT COUNT(RaterCode) AS Total FROM tblAssignment ";
		query = query + " INNER JOIN tblResultCompetency ON tblAssignment.AssignmentID = tblResultCompetency.AssignmentID ";
		query = query + "WHERE SurveyID = " + surveyID + " AND TargetLoginID = " + targetID;
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND RaterCode LIKE 'SUP%' and RaterStatus in(1,2,4)";
		else
			query = query + "AND RaterCode LIKE 'SUP%' and RaterStatus in(1,2,4,5)";
		query = query + " AND tblResultCompetency.RatingTaskID = "+iRatingTaskID;
		query = query + " AND tblResultCompetency.CompetencyID = "+iCompetencyID;
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND tblResultCompetency.Result <> 0";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalSup - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	/** by Hemilda 23/09/2008
	 * Count total Self for the particular survey and target.
	 * KB level
	 * To calculate number of SELF rater  for each rating task of each KB
	 */
	public int totalSelf1(int iRatingTaskID, int iCompetencyID) throws SQLException 
	{	
		int total = 0;
		SurveyResult SR = new SurveyResult();
		Calculation cal = new Calculation();
		String query = "select max(table1.Cnt)AS Total ";
		query = query + " From( ";
		query = query + " SELECT     COUNT(tblAssignment.RaterCode) AS Cnt,tblResultBehaviour.KeyBehaviourID ";
		query = query + " FROM         tblAssignment INNER JOIN ";
		query = query + " tblResultBehaviour ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID INNER JOIN ";
		query = query + " KeyBehaviour ON tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
		query = query + " WHERE     (tblAssignment.SurveyID =  " + surveyID + ") AND (tblAssignment.TargetLoginID = "+targetID+") " ;
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND RaterCode LIKE 'SELF' and RaterStatus in(1,2,4)";
		else
			query = query + " AND RaterCode LIKE 'SELF' and RaterStatus in(1,2,4,5)";
		query = query + "  AND (tblResultBehaviour.RatingTaskID = "+iRatingTaskID+")and (KeyBehaviour.FKCompetency = "+iCompetencyID +") ";
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND (tblResultBehaviour.Result <> 0)";   
		query = query + "  group by tblResultBehaviour.KeyBehaviourID ";
		query = query + "  ) table1 ";


		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalSelf - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
//System.out.println(">>>>>>>>> "+query);	
		return total;

	}
	
	/**
	 * Count total Self for the particular survey and target.
	 * for KB level
	 * To calculate number of SELF rater  for each rating task of each competency
	 */
	public int totalSelf(int iRatingTaskID, int iCompetencyID,int iKBId) throws SQLException 
	{	
		int total = 0;
		Calculation cal = new Calculation();

		String query = "SELECT COUNT(RaterCode) AS Total FROM tblAssignment INNER JOIN tblResultBehaviour";
		query = query + " ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID ";
		query = query + "WHERE SurveyID = " + surveyID + " AND TargetLoginID = " + targetID;
		if (cal.NAIncluded(surveyID) ==0)
			query = query + " AND RaterCode = 'SELF' and RaterStatus in(1,2,4)";
		else
			query = query + " AND RaterCode = 'SELF' and RaterStatus in(1,2,4,5)";
		query = query + " AND tblResultBehaviour.RatingTaskID  =  "+iRatingTaskID;
		query = query + " AND tblResultBehaviour.KeyBehaviourID = "+iKBId;
		
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND tblResultBehaviour.Result <>0";
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalSelf - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	/**
	 * Count total Self for the particular survey and target.
	 * Code edited by Ha 07/07/08 add Rating task ID and Competency ID to method signature
	 * To calculate number of SELF rater  for each rating task of each competency
	 */
	public int totalSelf(int iRatingTaskID, int iCompetencyID) throws SQLException 
	{	
		int total = 0;
		Calculation cal = new Calculation();
		//Query changed by Ha 07/07/08 to calculate number of SELF rated for this target
		//exlcluded who put NA in their questionnaire if survey is NA_Excluded
		String query = "SELECT COUNT(RaterCode) AS Total FROM tblAssignment INNER JOIN tblResultCompetency";
		query = query + " ON tblAssignment.AssignmentID = tblResultCompetency.AssignmentID ";
		query = query + "WHERE SurveyID = " + surveyID + " AND TargetLoginID = " + targetID;
		if (cal.NAIncluded(surveyID) ==0)
			query = query + " AND RaterCode = 'SELF' and RaterStatus in(1,2,4)";
		else
			query = query + " AND RaterCode = 'SELF' and RaterStatus in(1,2,4,5)";
		query = query + " AND tblResultCompetency.RatingTaskID  =  "+iRatingTaskID;
		query = query + " AND tblResultCompetency.CompetencyID  = "+iCompetencyID;
		
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND tblResultCompetency.Result <>0";
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalSelf - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	/**
	 * Count total groups for the particular survey and target.
	 */	
	public int totalGroup() throws SQLException 
	{	
		int total = 0;
		
		String query = "SELECT COUNT(DISTINCT tblAssignment.RTRelation) AS TotalGroup ";
		query = query + "FROM tblAssignment INNER JOIN tblSurveyRating ON ";
		query = query + "tblAssignment.SurveyID = tblSurveyRating.SurveyID INNER JOIN ";
		query = query + "tblRatingTask ON tblSurveyRating.RatingTaskID = tblRatingTask.RatingTaskID ";
		query = query + "WHERE tblAssignment.SurveyID = " + surveyID + " AND ";
		query = query + "tblAssignment.TargetLoginID = " + targetID + " AND tblRatingTask.RatingCode = 'CP'";
		query = query + " and tblAssignment.RaterStatus in(1,2,4)";
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalGroup - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	/**
	 * Count total other rating tasks besides CP for the particular survey.
	 */
	public int totalOtherRT() throws SQLException 
	{
		int total = 0;

		String query = "SELECT COUNT(tblRatingTask.RatingCode) AS TotalRT ";
		query = query + "FROM tblSurveyRating INNER JOIN tblRatingTask ON ";
		query = query + "tblSurveyRating.RatingTaskID = tblRatingTask.RatingTaskID ";
		query = query + "WHERE tblSurveyRating.SurveyID = " + surveyID;
		query = query + " AND (tblRatingTask.RatingCode = 'CPR' or tblRatingTask.RatingCode = 'FPR')";
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalOtherRT - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}

		
		return total;
	}
	
	/**
	 * Retrieves the competency score for all.
	 * This is only applied in KB Level Analysis.
	 * The score is stored in table tblTrimmedMean
	 */
	public double CompTrimmedMeanforAll(int RTID, int compID) throws SQLException 
	{
		double Result = 0;
		String query = "";
		int reliabilityIndex = C. ReliabilityCheck(surveyID);
		
		if(reliabilityIndex == 0) {
			query = query + "SELECT CompetencyID, Type, round(TrimmedMean, 2) AS Result FROM tblTrimmedMean ";
			query += "WHERE SurveyID = " + surveyID;
			query += " AND TargetLoginID = " + targetID + " AND RatingTaskID = " + RTID + " and CompetencyID = " + compID;
			query += " ORDER BY CompetencyID";
		} else {
			query = "select RatingTaskID, CompetencyID, cast(AVG(AvgMean) as numeric(38,2)) as Result from tblAvgMean ";
			query = query + "where SurveyID = " + surveyID;
			query = query + " AND TargetLoginID = " + targetID;
			query = query + " and Type = 1";
			query += " AND RatingTaskID = "+ RTID + " AND CompetencyID = " + compID;
			query = query + " group by CompetencyID, RatingTaskID";
		}
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			Result = rs.getDouble("Result");

  		}catch(Exception ex){
			System.out.println("IndividualReport.java - CompTrimmedMeanForAll - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		
		return Result;
	}	
	
	/**
	 * Retrieves the average mean of KB for a specific competency.
	 * This is only applied in KB Level Analysis.
	 */
	public Vector KBMean(int RTID, int compID) throws SQLException 
	{
		String query = "SELECT CompetencyID, Type, CAST(AVG(AvgMean) AS numeric(38, 2)) AS Result ";
		query = query + "FROM tblAvgMean WHERE SurveyID = " + surveyID + " AND TargetLoginID = " + targetID;
		query = query + " AND CompetencyID = " + compID + " and RatingTaskID = " + RTID;
		query = query + " GROUP BY CompetencyID, Type ORDER BY Type";
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector v = new Vector();
		
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - KBMean - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}	
	
	/**
	 * Retrieve the average or trimmed mean result based on competency and key behaviour for each group.
	 */
	public Vector MeanResult(int RTID, int compID, int KBID) throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C. ReliabilityCheck(surveyID);
		
		String tblName = "tblAvgMean";
		String result = "AvgMean";
	
		if(reliabilityCheck == 0) {
			tblName = "tblTrimmedMean";
			result = "TrimmedMean";
		}
		// Changed by Ha 27/05/08: add keyword DISTINCT
		if(surveyLevel == 0) {
			query = query + "SELECT DISTINCT " + tblName + ".CompetencyID, ";
			query = query + tblName + ".Type, " + tblName + "." + result;
			query = query + " as Result FROM " + tblName;
			query = query + " WHERE " + tblName + ".SurveyID = " + surveyID + " AND ";
			query = query + tblName + ".TargetLoginID = " + targetID;
			query = query + " AND " + tblName + ".RatingTaskID = " + RTID;
			query = query + " AND " + tblName + ".CompetencyID = " + compID;
			query = query + " ORDER BY " + tblName + ".Type";
		} else {
			query = query + "SELECT DISTINCT CompetencyID, Type, AvgMean as Result, KeyBehaviourID ";
			query = query + "FROM tblAvgMean WHERE SurveyID = " + surveyID + " AND ";
			query = query + "TargetLoginID = " + targetID + " AND RatingTaskID = " + RTID;
			query = query + " AND CompetencyID = " + compID + " AND KeyBehaviourID = " + KBID;
			query = query + " ORDER BY Type";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector v = new Vector();
		
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		  
  		   while(rs.next()) {  			    
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);	 			
	 			arr[1] = rs.getString(2);	 			
	 			arr[2] = rs.getString(3);	 			
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - MEanResult - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}

		return v;
	}
	
	/**
	 * Retrieves the individual level of agreement from tblLevelOfAgreement based on Competency and Key Behaviour.
	 * KBID = 0 if it is Competency Level Analysis.
	 */
	public double LevelOfAgreement(int compID, int KBID) throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		double LOA = -1;
		
		if(surveyLevel == 0) {
		
			query = query + "select * from tblLevelOfAgreement where SurveyID = " + surveyID;
			query = query + " and TargetLoginID = " + targetID + " and CompetencyID = " + compID;
			
		}else {
			query = query + "select * from tblLevelOfAgreement where SurveyID = " + surveyID;
			query = query + " and TargetLoginID = " + targetID + " and CompetencyID = " + compID;
			query = query + " and KeyBehaviourID = " + KBID;
		}
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
  			   LOA = rs.getDouble("LevelOfAgreement");


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - LevelOfAgreement - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
	
		return LOA;
	}
	
	/**
	 * Calculate the average of individual level of agreement for each competency.
	 * This is only apply for KB Level Analysis.
	 */
	public double AvgLevelOfAgreement(int compID, int noOfRaters) throws SQLException 
	{	
		String query = "";
		int iBase = C.getLOABase(noOfRaters);
		double LOA = -1;
		int iMaxScale = rscale.getMaxScale(surveyID); //Get Maximum Scale of this survey
		
		/*query = query + "SELECT tblResultBehaviour.RatingTaskID, KeyBehaviour.FKCompetency, ";
		query = query + "cast((100-(stDev(tblResultBehaviour.Result * 10 / " + iMaxScale + ") * " + iBase + ")) AS numeric(38, 2)) AS LOA ";
		query = query + "FROM tblAssignment INNER JOIN tblResultBehaviour ON ";
		query = query + "tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID INNER JOIN ";
		query = query + "tblRatingTask ON tblResultBehaviour.RatingTaskID = tblRatingTask.RatingTaskID ";
		query = query + "INNER JOIN KeyBehaviour ON ";
		query = query + "tblResultBehaviour.KeyBehaviourrID = KeyBehaviour.PKKeyBehaviour ";
		query = query + "WHERE tblAssignment.SurveyID = " + surveyID + " AND ";
		query = query + "tblAssignment.TargetLoginID = " + targetID + " AND ";
		query = query + "tblAssignment.RaterStatus IN (1, 2, 4) AND KeyBehaviour.FKCompetency = " + compID;
		query = query + " AND tblAssignment.RaterCode <> 'SELF' AND tblRatingTask.RatingCode = 'CP' ";
		query = query + "GROUP BY tblResultBehaviour.RatingTaskID, KeyBehaviour.FKCompetency";		
		*/
		//Edit by Roger 24 July 2008.
		//The base use in this calculation is wrong. Different competencies have different number of raters used to make
		// the calculation, depending on whether the survey is exclude NA and if the rater entered NA
		query = query + "SELECT tblAvgMeanByRater.RatingTaskID, tblAvgMeanByRater.CompetencyID, count(*) as numOfRaters, ";
		query = query + "stDev(tblAvgMeanByRater.AvgMean * 10 / " + iMaxScale + ") AS LOA ";
		query = query + "FROM tblAssignment INNER JOIN " ;
        query = query + "tblAvgMeanByRater ON tblAssignment.AssignmentID = tblAvgMeanByRater.AssignmentID INNER JOIN ";
        query = query + "tblRatingTask ON tblAvgMeanByRater.RatingTaskID = tblRatingTask.RatingTaskID ";
        query = query + "WHERE tblAssignment.SurveyID = " + surveyID + " AND ";
		query = query + "tblAssignment.TargetLoginID = " + targetID + " AND ";
		query = query + "tblAssignment.RaterStatus IN (1, 2, 4) AND tblAvgMeanByRater.CompetencyID = " + compID;
		query = query + " AND tblAssignment.RaterCode <> 'SELF' AND tblRatingTask.RatingCode = 'CP' ";
		query = query + "GROUP BY tblAvgMeanByRater.RatingTaskID, tblAvgMeanByRater.CompetencyID";		
		
     
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next()) {
  			   //Edit by Roger 24 July 2008
  			   //Shift the calculation of LOA from Query to here. It is because the number of raters is get from the query
  			   //and we need to use the getLOABase formula seperately
  			   LOA = 100-rs.getDouble("LOA")*C.getLOABase(rs.getInt("numOfRaters"));
  			   BigDecimal bd = new BigDecimal(LOA);
  			   bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP); //round to 2 decimal place
  			   LOA = bd.doubleValue();
  		   }


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - AvgLevelOfAgreement - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return LOA;
	}	
	
	/**
	 * Retrieves the gap results from tblGap based on competency and key behaviour.
	 * KBID = 0 if it is Competency Level Analysis.
	 */
	public double Gap(int compID, int KBID) throws SQLException 
	{
		String query = "";
		double gap = -1;
		
		query = query + "SELECT Gap FROM tblGap WHERE SurveyID = " + surveyID;
		query = query + " AND TargetLoginID = " + targetID + " AND CompetencyID = " + compID;
		query = query + " and KeyBehaviourID = " + KBID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		 if(rs.next())
 			gap = rs.getDouble(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - Gap - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return gap;
	}
	
	/**
	 * Retrieves the gap results from tblGap based on competency and key behaviour.
	 * KBID = 0 if it is Competency Level Analysis.
	 */
	public double GapToyota(int compID) throws SQLException 
	{
		String query = "";
		double gap = -1;
				
		query = query + "SELECT Gap FROM tblGap WHERE SurveyID = " + surveyID;
		query = query + " AND TargetLoginID = " + targetID + " AND CompetencyID = " + compID;
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		 if(rs.next())
 			gap = rs.getDouble(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - GapToyota - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return gap;
	}
	
	/**
	 * Retrieves the importance result based on competency and key behaviour id.
	 * KBID = 0 if it is Competency Level Analysis.
	 */
	public Vector Importance(int compID, int KBID) throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C. ReliabilityCheck(surveyID);
		
		String tblName = "tblAvgMean";
		String result = "AvgMean";
	
		if(reliabilityCheck == 0) {
			tblName = "tblTrimmedMean";
			result = "TrimmedMean";
		}
		
		
		//try {			
		if(surveyLevel == 0) {
			query = query + "SELECT tblRatingTask.RatingCode, ";
			query = query + "tblSurveyRating.RatingTaskName, " + tblName + "." + result + " as Result ";
			query = query + "FROM " + tblName + " INNER JOIN tblRatingTask ON ";
			query = query + tblName + ".RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "INNER JOIN tblSurveyRating ON ";
			query = query + "tblRatingTask.RatingTaskID = tblSurveyRating.RatingTaskID AND ";
			query = query + tblName + ".SurveyID = tblSurveyRating.SurveyID ";                      
			query = query + "WHERE " + tblName + ".SurveyID = " + surveyID + " AND ";
			query = query + tblName + ".TargetLoginID = " + targetID + " AND " + tblName + ".Type = 1 AND ";
			query = query + tblName + ".CompetencyID = " + compID + " AND ";
			query = query + "(tblRatingTask.RatingCode = 'IN' OR tblRatingTask.RatingCode = 'IF')";
		} else {
			query = query + "SELECT tblRatingTask.RatingCode, tblSurveyRating.RatingTaskName, ";
			query = query + "tblAvgMean.AvgMean AS Result FROM tblAvgMean INNER JOIN ";
			query = query + "tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "INNER JOIN tblSurveyRating ON ";
			query = query + "tblRatingTask.RatingTaskID = tblSurveyRating.RatingTaskID AND ";
			query = query + "tblAvgMean.SurveyID = tblSurveyRating.SurveyID ";                      				
			query = query + "WHERE tblAvgMean.SurveyID = " + surveyID + " AND ";
			query = query + "tblAvgMean.TargetLoginID = " + targetID + " AND ";
			query = query + "tblAvgMean.CompetencyID = " + compID + " AND ";
			query = query + "tblAvgMean.KeyBehaviourID = " + KBID + " AND tblAvgMean.Type = 1 ";
			query = query + "AND (tblRatingTask.RatingCode = 'IN' OR tblRatingTask.RatingCode = 'IF')";
		}				
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector v = new Vector();
		
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - Importance - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
	
		return v;
	}
	
	/**
	 * Calculate the average of importance for each competency.
	 * This is only apply for KB Level Analysis.
	 */
	public Vector AvgImportance(int compID) throws SQLException 
	{
		String query = "";
				
		//try {			
		query = query + "SELECT tblRatingTask.RatingCode, tblSurveyRating.RatingTaskName, ";
		query = query + "cast(avg(tblAvgMean.AvgMean) as numeric(38,2)) AS Result FROM tblAvgMean ";
		query = query + "INNER JOIN tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
		query = query + "INNER JOIN tblSurveyRating ON ";
		query = query + "tblRatingTask.RatingTaskID = tblSurveyRating.RatingTaskID AND ";
		query = query + "tblAvgMean.SurveyID = tblSurveyRating.SurveyID ";
		query = query + "WHERE tblAvgMean.SurveyID = " + surveyID;
		query = query + " AND tblAvgMean.TargetLoginID = " + targetID;
		query = query + " AND tblAvgMean.CompetencyID = " + compID;
		query = query + " AND tblAvgMean.Type = 1 AND ";
		query = query + "(tblRatingTask.RatingCode = 'IN' OR tblRatingTask.RatingCode = 'IF') ";
		query = query + "group by tblRatingTask.RatingTaskID,tblRatingTask.RatingCode, ";
		query = query + "tblSurveyRating.RatingTaskName";

	
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector v = new Vector();
		
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - AvgImportance - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}	
	
	/**
	 * Calculate the average of gap for each competency.
	 * This is only apply for KB Level Analysis.
	 */
	public double getAvgGap(int compID) throws SQLException 
	{
		double gap = 0;
		
		
		String query = "Select cast(AVG(Gap) as numeric(38,2)) from tblGap where SurveyID = " + surveyID;
		query = query + " AND TargetLoginID = " + targetID + " and CompetencyID = " + compID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		 if(rs.next())
 			gap = rs.getDouble(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getAvgGap - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return gap;
	}
	
	/**
	 * Retrieves the minimum and maximum gap, which was set when create/edit survey.
	 */
	public double [] getMinMaxGap() throws SQLException 
	{
		double gap [] = new double [2];
		
		String query = "Select MIN_gap, MAX_Gap from tblSurvey where SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next()) {
  			   gap[0] = rs.getDouble(1);
  			   gap[1] = rs.getDouble(2);
  		   }


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getMinMaxGap - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return gap;
	}	
	
	/**
	 * Retrieves all target results based on the reliability check.
	 */
	public Vector getAllTargetsResults() throws SQLException 
	{
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		
		String query = "";
		
		int reliabilityCheck = C. ReliabilityCheck(surveyID);

		//try {		
		if(reliabilityCheck == 0) {			
			query = query + "SELECT tblTrimmedMean.CompetencyID, Competency.CompetencyName, ";
			query = query + "cast(AVG(tblTrimmedMean.TrimmedMean) as numeric(38,2)) AS Result ";
			query = query + "FROM tblRatingTask INNER JOIN tblTrimmedMean ON ";
			query = query + "tblRatingTask.RatingTaskID = tblTrimmedMean.RatingTaskID INNER JOIN ";
			query = query + "Competency ON tblTrimmedMean.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblTrimmedMean.SurveyID = " + surveyID;
			query = query + " AND tblTrimmedMean.TargetLoginID <> " + targetID;
			query = query + " AND tblTrimmedMean.Type = 1 AND tblRatingTask.RatingCode = 'CP' ";
			query = query + "GROUP BY tblTrimmedMean.CompetencyID, Competency.CompetencyName ";
			query = query + "order by Competency.CompetencyName";
		} else {
			if(surveyLevel == 0) {
				query = query + "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, ";
				query = query + "cast(AVG(tblAvgMean.AvgMean) as numeric(38,2)) AS Result ";
				query = query + "FROM tblAvgMean INNER JOIN tblRatingTask ON ";
				query = query + "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID + " AND ";
				query = query + "tblAvgMean.TargetLoginID <> " + targetID + " AND tblAvgMean.Type = 1 AND ";
				query = query + "tblRatingTask.RatingCode = 'CP' ";
				query = query + "GROUP BY tblAvgMean.CompetencyID, Competency.CompetencyName ";
				query = query + "order by Competency.CompetencyName";
				
			} else {
				
				query = query + "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, ";
				query = query + "CAST(AVG(tblAvgMean.AvgMean) AS numeric(38, 2)) AS Result ";
				query = query + "FROM tblRatingTask INNER JOIN tblAvgMean ON ";
				query = query + "tblRatingTask.RatingTaskID = tblAvgMean.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID + " AND ";
				query = query + "tblAvgMean.TargetLoginID <> " + targetID + " AND tblAvgMean.Type = 1 AND ";
				query = query + "tblRatingTask.RatingCode = 'CP' ";
				query = query + "GROUP BY tblAvgMean.CompetencyID, Competency.CompetencyName ";
				query = query + "order by Competency.CompetencyName";
			}

		}
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector v = new Vector();
		
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - KBMean - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}		
	
	/**
	 * Retrieves the results under that particular rating code.
	 */
	public Vector CPCPR(String RTCode) throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C. ReliabilityCheck(surveyID);
		
		if(reliabilityCheck == 0) 
		{			
			query = "SELECT tblTrimmedMean.CompetencyID, Competency.CompetencyName, tblTrimmedMean.TrimmedMean as Result ";
			query = query + "FROM tblTrimmedMean INNER JOIN tblRatingTask ON ";
			query = query + "tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "INNER JOIN Competency ON ";
			query = query + "tblTrimmedMean.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblTrimmedMean.SurveyID = " + surveyID + " AND ";
			query = query + "tblTrimmedMean.TargetLoginID = " + targetID + " AND tblTrimmedMean.Type = 1 AND ";
			query = query + "tblRatingTask.RatingCode = '" + RTCode + "' ";
			query = query + "ORDER BY Competency.CompetencyName";
		} 
		else 
		{
			if(surveyLevel == 0) 
			{
				query = "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, tblAvgMean.AvgMean as Result ";
				query = query + "FROM tblAvgMean INNER JOIN tblRatingTask ON ";
				query = query + "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID + " AND ";
				query = query + "tblAvgMean.TargetLoginID = " + targetID + " AND tblAvgMean.Type = 1 AND ";
				query = query + "tblRatingTask.RatingCode = '" + RTCode + "' ORDER BY Competency.CompetencyName";
			} 
			else 
			{
				query = "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, ";
				query = query + "CAST(AVG(tblAvgMean.AvgMean) AS numeric(38, 2)) AS Result ";
				query = query + "FROM tblRatingTask INNER JOIN tblAvgMean ON ";
				query = query + "tblRatingTask.RatingTaskID = tblAvgMean.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID + " AND ";
				query = query + "tblAvgMean.TargetLoginID = " + targetID + " AND tblAvgMean.Type = 1 AND ";
				query = query + "tblRatingTask.RatingCode = '" + RTCode + "' GROUP BY tblAvgMean.CompetencyID, ";
				query = query + "Competency.CompetencyName order by Competency.CompetencyName";
			}
		}
		//System.out.println("cpcpr "+query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector v = new Vector();
		
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - CPCPR - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
//System.out.println(query);
		return v;
	}	

	/**
	 * Calculate CP Score, break down to Specific Relation of OTH
	 * @param RTCode
	 * @return CP score
	 * @throws SQLException
	 * @author Maruli
	 */
	public double CPCPRAllianz(int iRTCode, int iCompID, String sRelHigh, String sRelSpec) throws SQLException 
	{
		String query = "";
		double dCP = 0;
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C. ReliabilityCheck(surveyID);
		
		if(reliabilityCheck == 0) 
		{			
			/*query = query + "SELECT tblTrimmedMean.CompetencyID, Competency.CompetencyName, tblTrimmedMean.TrimmedMean as Result ";
			query = query + "FROM tblTrimmedMean INNER JOIN tblRatingTask ON ";
			query = query + "tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "INNER JOIN Competency ON ";
			query = query + "tblTrimmedMean.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblTrimmedMean.SurveyID = " + surveyID + " AND ";
			query = query + "tblTrimmedMean.TargetLoginID = " + targetID + " AND tblTrimmedMean.Type = 1 AND ";
			query = query + "tblRatingTask.RatingCode = '" + RTCode + "' ";
			query = query + "ORDER BY Competency.CompetencyName";*/
		} 
		else 
		{
			if(surveyLevel == 0) 
			{
				
			} 
			else 
			{			
				query = "SELECT FKCompetency, CAST(CAST(SUM(AvgMean) AS float) / COUNT(FKCompetency) AS numeric(38, 2)) AS Result ";
				query = query + "FROM (SELECT RB.RatingTaskID, KB.FKCompetency, RB.KeyBehaviourID, "; 
				query = query + "CAST(CAST(SUM(RB.Result) AS float) / COUNT(RB.Result) AS numeric(38, 2)) AS AvgMean, RS.RelationSpecific ";
				query = query + "FROM tblAssignment ASG INNER JOIN ";
				query = query + "tblResultBehaviour RB ON ASG.AssignmentID = RB.AssignmentID INNER JOIN ";
				query = query + "KeyBehaviour KB ON RB.KeyBehaviourID = KB.PKKeyBehaviour LEFT OUTER JOIN ";
				query = query + "tblRelationSpecific RS ON ASG.RTSpecific = RS.SpecificID ";
				query = query + "WHERE (ASG.SurveyID = "+surveyID+") AND (ASG.TargetLoginID = "+targetID+") AND (KB.FKCompetency = "+iCompID+") ";
				query = query + "AND (ASG.RaterStatus IN (1, 2, 4)) AND (ASG.RaterCode LIKE '"+sRelHigh+"') AND (RB.Result <> 0) ";
				query = query + "GROUP BY RB.RatingTaskID, KB.FKCompetency, RB.KeyBehaviourID, RS.RelationSpecific ";
				query = query + "HAVING (RB.RatingTaskID = "+iRTCode+") ";
				
				if(sRelSpec != "")
					query = query + "AND (RS.RelationSpecific = '"+sRelSpec+"') ";
				
				query = query + ") DERIVEDTBL GROUP BY FKCompetency";
			}
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   if(rs.next())
  			   dCP = rs.getDouble("Result");
  		   
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - CPCPRAllianz - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return dCP;
	}
	
	
	/**
	 * Retrieves the results under that particular rating code sorted by CompetencyID
	 */
	public Vector CPCPRSortedID(String RTCode) throws SQLException 
	{
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		
		String query = "";
		
		int reliabilityCheck = C. ReliabilityCheck(surveyID);

		if(reliabilityCheck == 0) {
			query = "SELECT tblTrimmedMean.CompetencyID, Competency.CompetencyName, tblTrimmedMean.TrimmedMean as Result ";
			query = query + "FROM tblTrimmedMean INNER JOIN tblRatingTask ON ";
			query = query + "tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "INNER JOIN Competency ON ";
			query = query + "tblTrimmedMean.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblTrimmedMean.SurveyID = " + surveyID + " AND ";
			query = query + "tblTrimmedMean.TargetLoginID = " + targetID + " AND tblTrimmedMean.Type = 1 AND ";
			query = query + "tblRatingTask.RatingCode = '" + RTCode + "' ";
			query = query + "ORDER BY tblTrimmedMean.CompetencyID";
		} else {
			if(surveyLevel == 0) {
				query = "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, tblAvgMean.AvgMean as Result ";
				query = query + "FROM tblAvgMean INNER JOIN tblRatingTask ON ";
				query = query + "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID + " AND ";
				query = query + "tblAvgMean.TargetLoginID = " + targetID + " AND tblAvgMean.Type = 1 AND ";
				query = query + "tblRatingTask.RatingCode = '" + RTCode + "' ORDER BY tblAvgMean.CompetencyID";
				
			} else {
				query = "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, ";
				query = query + "CAST(AVG(tblAvgMean.AvgMean) AS numeric(38, 2)) AS Result ";
				query = query + "FROM tblRatingTask INNER JOIN tblAvgMean ON ";
				query = query + "tblRatingTask.RatingTaskID = tblAvgMean.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID + " AND ";
				query = query + "tblAvgMean.TargetLoginID = " + targetID + " AND tblAvgMean.Type = 1 AND ";
				query = query + "tblRatingTask.RatingCode = '" + RTCode + "' GROUP BY tblAvgMean.CompetencyID, ";
				query = query + "Competency.CompetencyName ORDER BY tblAvgMean.CompetencyID";
			}
		}

		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector v = new Vector();
		
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - CPCPRSorted - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;	
	}	
	
	
	/**
	 * 	Retrieves past survey results under that particular rating code
	 *	This function is used only for Toyota combined report
	 */
	public Vector PastCP(String RTCode) throws SQLException 
	{
		Vector v = new Vector();
		
		String query = "";
		boolean bPastSurveyExist = false;
		
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C. ReliabilityCheck(surveyID);

		/*
		 *	Check whether there are any existing survey in the same Job Position as chosen survey
		 */
		query = "SELECT MAX(tblSurvey.SurveyID) AS SurveyID ";
		query = query + "FROM tblSurvey INNER JOIN tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID ";
		query = query + "WHERE (JobPositionID = " + surveyInfo[10] +") ";
		query = query + "AND tblSurvey.SurveyID < " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		int ID = 0;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
	
			if(rs.next()) {
	 			ID = rs.getInt(1);
			}
		}catch(Exception ex){
			System.out.println("IndividualReport.java - PastCP - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
			
		
		if (ID !=0)
		{
			//rsPastSurvey.next();
			iPastSurveyID = ID;

			if (iPastSurveyID > 0)
			{
				query = "SELECT DISTINCT TargetLoginID FROM tblAssignment WHERE SurveyID = " + iPastSurveyID +
				//Added by Jenty on 26-Sept-06
				//To solve the mising previous CP
				" and TargetLoginID = " + targetID; 

				try{
					con=ConnectionBean.getConnection();
					st=con.createStatement();
					rs=st.executeQuery(query);
					if(rs.next()) {
						iPastTargetLogin = targetID;
						bPastSurveyExist = true;
					}
				}catch(Exception ex){
					System.out.println("IndividualReport.java - PastCP - "+ex.getMessage());
				}
				finally{
					ConnectionBean.closeRset(rs); //Close ResultSet
					ConnectionBean.closeStmt(st); //Close statement
					ConnectionBean.close(con); //Close connection
				}
			
			}
						
		}
		// ~~~ END Past Survey Exist ~~~
		
		if (bPastSurveyExist == true)
		{
			if(reliabilityCheck == 0)
			{
				query = "SELECT tblTrimmedMean.CompetencyID, Competency.CompetencyName, tblTrimmedMean.TrimmedMean as Result ";
				query = query + "FROM tblTrimmedMean INNER JOIN tblRatingTask ON ";
				query = query + "tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query + "tblTrimmedMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblTrimmedMean.SurveyID = " + iPastSurveyID + " AND ";
				query = query + "tblTrimmedMean.TargetLoginID = " + iPastTargetLogin + " AND tblTrimmedMean.Type = 1 AND ";
				query = query + "tblRatingTask.RatingCode = '" + RTCode + "' ";
				query = query + "ORDER BY Competency.PKCompetency";
			}
			else
			{
				if(surveyLevel == 0)
				{
					query = "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, tblAvgMean.AvgMean as Result ";
					query = query + "FROM tblAvgMean INNER JOIN tblRatingTask ON ";
					query = query + "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
					query = query + "INNER JOIN Competency ON ";
					query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
					query = query + "WHERE tblAvgMean.SurveyID = " + iPastSurveyID + " AND ";
					query = query + "tblAvgMean.TargetLoginID = " + iPastTargetLogin + " AND tblAvgMean.Type = 1 AND ";
					query = query + "tblRatingTask.RatingCode = '" + RTCode + "' ORDER BY Competency.PKCompetency";
				}
				else
				{
					query = "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, ";
					query = query + "CAST(AVG(tblAvgMean.AvgMean) AS numeric(38, 2)) AS Result ";
					query = query + "FROM tblRatingTask INNER JOIN tblAvgMean ON ";
					query = query + "tblRatingTask.RatingTaskID = tblAvgMean.RatingTaskID ";
					query = query + "INNER JOIN Competency ON ";
					query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
					query = query + "WHERE tblAvgMean.SurveyID = " + iPastSurveyID + " AND ";
					query = query + "tblAvgMean.TargetLoginID = " + iPastTargetLogin + " AND tblAvgMean.Type = 1 AND ";
					query = query + "tblRatingTask.RatingCode = '" + RTCode + "' GROUP BY tblAvgMean.CompetencyID, ";
					query = query + "Competency.CompetencyName ORDER BY Competency.PKCompetency";
				}
			}
			
	  	   	try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(query);
			
	  		   while(rs.next()) {
		 			String [] arr = new String[3];
		 			arr[0] = rs.getString(1);
		 			arr[1] = rs.getString(2);
		 			arr[2] = rs.getString(3);
		 			v.add(arr);
	  		   }
	  		}catch(Exception ex){
				System.out.println("IndividualReport.java - PastCP - "+ex.getMessage());
			}
			finally{
				ConnectionBean.closeRset(rs); //Close ResultSet
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
			
		}	//	bPastSurveyExist

		return v;
	}	
	
	
	/**
	 * Retrieve the average or trimmed mean result based on competency and key behaviour for each group.
	 */
	public Vector MeanResultToyota(int RTID, int compID, int KBID) throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C. ReliabilityCheck(surveyID);

		String tblName = "tblAvgMean";
		String result = "AvgMean";
	
		if(reliabilityCheck == 0) {
			tblName = "tblTrimmedMean";
			result = "TrimmedMean";
		}
		
		if(surveyLevel == 0) {
			query = query + "SELECT " + tblName + ".CompetencyID, ";
			query = query + tblName + ".Type, " + tblName + "." + result;
			query = query + " as Result FROM " + tblName;
			query = query + " WHERE " + tblName + ".SurveyID = " + iPastSurveyID + " AND ";
			query = query + tblName + ".TargetLoginID = " + iPastTargetLogin;
			query = query + " AND " + tblName + ".RatingTaskID = " + RTID;
			query = query + " AND " + tblName + ".CompetencyID = " + compID;
			query = query + " AND " + tblName + ".Type = 1";
			
		} else {
			//Maruli
			query = query + "SELECT CompetencyID, Type, AVG(" + result + ") as Result ";
			query = query + "FROM " + tblName + " WHERE SurveyID = " + iPastSurveyID + " AND ";
			query = query + "TargetLoginID = " + iPastTargetLogin + " AND RatingTaskID = " + RTID;
			query = query + " AND CompetencyID = " + compID;
			query = query + " AND Type = 1";
			query = query + " GROUP BY CompetencyID, " + result;
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		   
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - MeanResultToyota - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	
	/**
	 * Retrieves target gaps based on the low, high value, and the type.
	 * Type: 1 = Strength
	 *		 2 = Meet Expectation
	 *		 3 = Developmental Area
	 */
	public Vector getTargetGap(double low, double high, int type) throws SQLException 
	{
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		String query = "";
		String filter = "";

		if(surveyLevel == 0) {
			switch (type) {
				case 1 : filter = " and tblGap.Gap >= " + low;
				 	break;
				case 2 : filter = " and tblGap.Gap > " + low + " and tblGap.Gap < " + high;
				 	break;
				case 3 : filter = " and tblGap.Gap <= " + low;
				 	break;		 
			}
	
			query = query + "SELECT tblGap.CompetencyID, Competency.CompetencyName, cast(tblGap.Gap as numeric(38,2)) as Result ";
			query = query + "FROM tblGap INNER JOIN Competency ON tblGap.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblGap.SurveyID = " + surveyID + " AND tblGap.TargetLoginID = " + targetID;
			query = query + filter;
			query = query + " ORDER BY Competency.CompetencyName";
		} else {
			switch (type) {
				case 1 : filter = " having CAST(AVG(tblGap.Gap) AS numeric(38, 2))  >= " + low;
				 	break;
				case 2 : filter = " having CAST(AVG(tblGap.Gap) AS numeric(38, 2))  > " + low + " and CAST(AVG(tblGap.Gap) AS numeric(38, 2)) < " + high;
				 	break;
				case 3 : filter = " having CAST(AVG(tblGap.Gap) AS numeric(38, 2))  <= " + low;
				 	break;		 
			}
	
			query = query + "SELECT tblGap.CompetencyID, Competency.CompetencyName, ";
			query = query + "CAST(AVG(tblGap.Gap) AS numeric(38, 2)) AS Result ";
			query = query + "FROM tblGap INNER JOIN Competency ON ";
			query = query + "tblGap.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblGap.SurveyID = " + surveyID;
			query = query + " AND tblGap.TargetLoginID = " + targetID;
			query = query + " GROUP BY tblGap.CompetencyID, Competency.CompetencyName ";
			query = query + filter;
			query = query + " ORDER BY Competency.CompetencyName";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		   
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getTargetGap - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}	
	
	
	/**
	 * Retrieves rater results for each competency to calculate percentile in Normative Report.
	 */
	public Vector getOtherTargetResults(int ID, int compID) throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int reliabilityCheck = C. ReliabilityCheck(surveyID);

		if(reliabilityCheck == 0) 
		{			
			query = query + "SELECT tblTrimmedMean.CompetencyID, Competency.CompetencyName, tblTrimmedMean.TrimmedMean as Result ";
			query = query + "FROM tblTrimmedMean INNER JOIN tblRatingTask ON ";
			query = query + "tblTrimmedMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query = query + "INNER JOIN Competency ON ";
			query = query + "tblTrimmedMean.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblTrimmedMean.SurveyID = " + surveyID + " AND ";
			query = query + "tblTrimmedMean.TargetLoginID = " + ID + " AND tblTrimmedMean.Type = 1 AND ";
			query = query + "tblRatingTask.RatingCode = 'CP' and tblTrimmedMean.CompetencyID = " + compID;				
		} else {
			if(surveyLevel == 0) 
			{
				query = query + "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, tblAvgMean.AvgMean as Result ";
				query = query + "FROM tblAvgMean INNER JOIN tblRatingTask ON ";
				query = query + "tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID + " AND ";
				query = query + "tblAvgMean.TargetLoginID = " + ID + " AND tblAvgMean.Type = 1 AND ";
				query = query + "tblRatingTask.RatingCode = 'CP' and tblAvgMean.CompetencyID = " + compID;
				
			} else 
			{	
				query = query + "SELECT tblAvgMean.CompetencyID, Competency.CompetencyName, ";
				query = query + "CAST(AVG(tblAvgMean.AvgMean) AS numeric(38, 2)) AS Result ";
				query = query + "FROM tblRatingTask INNER JOIN tblAvgMean ON ";
				query = query + "tblRatingTask.RatingTaskID = tblAvgMean.RatingTaskID ";
				query = query + "INNER JOIN Competency ON ";
				query = query + "tblAvgMean.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblAvgMean.SurveyID = " + surveyID + " AND ";
				query = query + "tblAvgMean.TargetLoginID = " + ID + " AND tblAvgMean.Type = 1 AND ";
				query = query + "tblRatingTask.RatingCode = 'CP' and tblAvgMean.CompetencyID = " + compID;
				query = query + " GROUP BY tblAvgMean.CompetencyID, Competency.CompetencyName";
			}
		}
		//System.out.println("query1 "+query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		   
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getOtherTargetResults - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}

		return v;
	}	
	
	
	/**
	  * Get total targets which status is completed included hinself
	  */
	public int TotalTargetsIncluded() throws SQLException 
	{
		int total = 0;
		
		String query = "SELECT count(DISTINCT tblAssignment.TargetLoginID) as total FROM tblAssignment INNER JOIN ";
		query = query + "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
		query = query + "[User] User_1 ON [User].Group_Section = User_1.Group_Section AND ";
		query = query + "tblAssignment.TargetLoginID = User_1.PKUser WHERE ";
		query = query + "tblAssignment.SurveyID = " + surveyID;
		query = query + " AND tblAssignment.RaterStatus <> 0";
		query += " AND tblAssignment.TargetLoginID IN (SELECT TargetLoginID FROM tblAssignment ";
		query += "WHERE SurveyID = " + surveyID + " AND RaterStatus <> 0 AND RaterCode <> 'SELF')";
		//System.out.println("total Target :"+query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   if(rs.next())
  			   total = rs.getInt(1);
  		   
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalTargetsIncluded - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	
	/**
	  * Get total targets which status is completed
	  */
	public int TotalTargets() throws SQLException 
	{
		int total = 0;
		String query = "SELECT count(DISTINCT tblAssignment.TargetLoginID) as total FROM tblAssignment INNER JOIN ";
		query = query + "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
		query = query + "[User] User_1 ON [User].Group_Section = User_1.Group_Section AND ";
		query = query + "tblAssignment.TargetLoginID = User_1.PKUser WHERE ";
		query = query + "tblAssignment.SurveyID = " + surveyID;
		query = query + " AND tblAssignment.RaterStatus <> 0 AND tblAssignment.TargetLoginID <> " + targetID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   if(rs.next())
  			   total = rs.getInt(1);
  		   
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalTargets - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}					
	
	/**
	 * Retrieves all the comments input upon fill in the questionnaire.
	 * @param raterCode
	 * @param compID
	 * @param KBID
	 * @return
	 * @throws SQLException
	 */
	public Vector getComments(String raterCode, int compID, int KBID) throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		
		if(surveyLevel == 0) {
			query = query + "SELECT Competency.CompetencyName, tblComment.Comment, PKCompetency ";
			query = query + "FROM tblAssignment INNER JOIN tblComment ON ";
			query = query + "tblAssignment.AssignmentID = tblComment.AssignmentID INNER JOIN ";
			query = query + "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
			query = query + "Competency ON tblComment.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblAssignment.SurveyID = " + surveyID;
			query = query + " AND tblAssignment.TargetLoginID = " + targetID;
			query = query + " AND tblAssignment.RaterCode LIKE '" + raterCode + "'";
			query = query + " AND Competency.PKCompetency = " + compID;
			query = query + " ORDER BY tblComment.Comment";
		} else {
			query = query + "SELECT Competency.CompetencyName, tblComment.Comment, KeyBehaviour.KeyBehaviour ";
			query = query + "FROM tblAssignment INNER JOIN tblComment ON ";
			query = query + "tblAssignment.AssignmentID = tblComment.AssignmentID INNER JOIN ";
			query = query + "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
			query = query + "Competency ON tblComment.CompetencyID = Competency.PKCompetency ";
			query = query + "INNER JOIN KeyBehaviour ON ";
			query = query + "tblComment.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query + "WHERE tblAssignment.SurveyID = " + surveyID;
			query = query + " AND tblAssignment.TargetLoginID = " + targetID;
			query = query + " AND tblAssignment.RaterCode LIKE '" + raterCode + "'";
			query = query + " AND Competency.PKCompetency = " + compID;
			query = query + " AND KeyBehaviour.PKKeyBehaviour = " + KBID;
			query = query + " ORDER BY tblComment.Comment";												
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		   
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getComments - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}	
	
	/**
	 * Get comments. Allianz does not want to break down into KB.
	 * @param raterCode
	 * @param compID
	 * @return
	 * @throws SQLException
	 */
	public Vector getCommentsAllianz(String raterCode, int compID) throws SQLException 
	{
		String query = "SELECT Competency.CompetencyName, tblComment.Comment ";
		query = query + "FROM tblAssignment INNER JOIN tblComment ON ";
		query = query + "tblAssignment.AssignmentID = tblComment.AssignmentID INNER JOIN ";
		query = query + "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
		query = query + "Competency ON tblComment.CompetencyID = Competency.PKCompetency ";
		query = query + "WHERE tblAssignment.SurveyID = " + surveyID;
		query = query + " AND tblAssignment.TargetLoginID = " + targetID;
		query = query + " AND tblAssignment.RaterCode LIKE '" + raterCode + "'";
		query = query + " AND Competency.PKCompetency = " + compID;
		query = query + " ORDER BY tblComment.Comment";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		   
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getCommentsAllianz - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	/**
	 * Get target gap.
	 */
	public Vector getTargetGap() throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		
		if(surveyLevel == 0) {
			query = query + "SELECT CompetencyID, Competency.CompetencyName, Gap FROM tblGap ";
			query += "INNER JOIN Competency ON tblGap.CompetencyID = Competency.PKCompetency WHERE ";
			query += "SurveyID = " + surveyID + " AND TargetLoginID = " + targetID;
			query += " ORDER BY CompetencyID";
		} else {
			query = query + "SELECT CompetencyID, Competency.CompetencyName, CAST(AVG(Gap) AS numeric(38, 2)) AS Gap ";
			query += "FROM tblGap INNER JOIN Competency ON tblGap.CompetencyID = Competency.PKCompetency WHERE SurveyID = " + surveyID;
			query += " AND TargetLoginID = " + targetID;
			query += " GROUP BY CompetencyID, Competency.CompetencyName ORDER BY Competency.CompetencyName, tblGap.CompetencyID";										
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		   
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getTargetGap - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	
	/**
	 * Get target gap.
	 */
	public Vector getTargetGapToyota(String sGapQuery) throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		
		if(surveyLevel == 0) {
			query = query + "SELECT CompetencyID, Competency.CompetencyName, Gap FROM tblGap ";
			query += "INNER JOIN Competency ON tblGap.CompetencyID = Competency.PKCompetency WHERE ";
			query += "SurveyID = " + surveyID + " AND TargetLoginID = " + targetID + " ";
			query += sGapQuery;
		} else {
			query = query + "SELECT CompetencyID, Competency.CompetencyName, CAST(AVG(Gap) AS numeric(38, 2)) AS Gap ";
			query += "FROM tblGap INNER JOIN Competency ON tblGap.CompetencyID = Competency.PKCompetency WHERE SurveyID = " + surveyID;
			query += " AND TargetLoginID = " + targetID;
			query += sGapQuery;
			query += " GROUP BY CompetencyID, Competency.CompetencyName ORDER BY Competency.CompetencyName, tblGap.CompetencyID";
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		   
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getTargetGapToyota - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	
	public Vector getOtherTargetGap(int ID, int compID) throws SQLException 
	{
		String query = "";
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
	
		if(surveyLevel == 0) {
			query = query + "SELECT CompetencyID, Competency.CompetencyName, Gap FROM tblGap ";
			query += "INNER JOIN Competency ON tblGap.CompetencyID = Competency.PKCompetency WHERE ";
			query += "SurveyID = " + surveyID + " AND TargetLoginID = " + ID + " and CompetencyID = " + compID;
			query += " ORDER BY CompetencyID";
		} else {
			query = query + "SELECT CompetencyID, Competency.CompetencyName, CAST(AVG(Gap) AS numeric(38, 2)) AS Gap ";
			query += "FROM tblGap INNER JOIN Competency ON tblGap.CompetencyID = Competency.PKCompetency WHERE SurveyID = " + surveyID;
			query += " AND TargetLoginID = " + ID + " and CompetencyID = " + compID;;
			query += " GROUP BY CompetencyID, Competency.CompetencyName ORDER BY Competency.CompetencyName, tblGap.CompetencyID";										
		}
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		   
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()) {
	 			String [] arr = new String[3];
	 			arr[0] = rs.getString(1);
	 			arr[1] = rs.getString(2);
	 			arr[2] = rs.getString(3);
	 			v.add(arr);
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getOtherTargetGap - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}	
	
	
	/**
	  * Get all Targets' ID which status is completed
	  */
	public Vector TargetsID() {
		
		String query = "SELECT DISTINCT tblAssignment.TargetLoginID FROM tblAssignment INNER JOIN ";
		query = query + "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
		query = query + "[User] User_1 ON [User].Group_Section = User_1.Group_Section AND ";
		query = query + "tblAssignment.TargetLoginID = User_1.PKUser WHERE ";
		query = query + "tblAssignment.SurveyID = " + surveyID;
		query = query + " AND tblAssignment.RaterStatus <> 0 AND tblAssignment.TargetLoginID <> " + targetID;
		
		// added in so that target who only completed by SELF does not counted as complete.
		query += " AND RaterCode <> 'SELF' ";
		
		query = query + " ORDER BY tblAssignment.TargetLoginID";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Vector v = new Vector();
		   
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()) {
	 			v.add(new Integer(rs.getInt(1)));
  		   }
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getOtherTargetGap - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	/***************************************** SPREADSHEET ********************************************************/
	
	/**
	 * Initialize all the processes dealing with Excel Application.
	 */
	public void InitializeExcel(String savedFileName) throws IOException, Exception 
	{	
		//System.out.println("2. Excel Initialisation Starts");
		
		storeURL 	= "file:///" + ST.getOOReportPath() + savedFileName;
		String templateURL 	= "file:///" + ST.getOOReportTemplatePath() + "Individual Report Template.xls";

		xRemoteServiceManager = OO.getRemoteServiceManager("uno:socket,host=localhost,port=2002;urp;StarOffice.ServiceManager");
		xDoc = OO.openDoc(xRemoteServiceManager, templateURL);

		//save as the template into a new file first. This is to avoid the template being used.		
		OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);
		OO.closeDoc(xDoc);

		//open up the saved file and modify from there
		xDoc = OO.openDoc(xRemoteServiceManager, storeURL);
		xSpreadsheet = OO.getSheet(xDoc, "Sheet1");
		xSpreadsheet2 = OO.getSheet(xDoc, "Sheet2");

		//System.out.println("2. Excel Initialisation Completed");
	}
	
	
	/**
	 * 	Initialize all the processes dealing with Excel Application. (For Toyota combined report)
	 */
	public void InitializeExcelToyota(String savedFileName) throws IOException, Exception 
	{	
		//System.out.println("2. Excel Initialisation Starts");
		
		storeURL 	= "file:///" + ST.getOOReportPath() + savedFileName;
		String templateURL 	= "file:///" + ST.getOOReportTemplatePath() + "Individual Report Template Combined.xls";
		
		xRemoteServiceManager = OO.getRemoteServiceManager("uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager");
		xDoc = OO.openDoc(xRemoteServiceManager, templateURL);
		
		//save as the template into a new file first. This is to avoid the template being used.		
		OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);		
		OO.closeDoc(xDoc);
		
		//open up the saved file and modify from there
		xDoc = OO.openDoc(xRemoteServiceManager, storeURL);
		xSpreadsheet = OO.getSheet(xDoc, "Individual Report");
		xSpreadsheet2 = OO.getSheet(xDoc, "Sheet2");
		
	//	System.out.println("2. Excel Initialisation Completed");
	}
	
	/**
	 * Initialize excel. For both customised and standard report
	 * @param sSavedFileName	Name of the file when prompted to save
	 * @param sTemplateName		Name of the template of the excel file
	 * @throws IOException
	 * @throws Exception
	 */
	public void InitializeExcel(String sSavedFileName, String sTemplateName) throws IOException, Exception
	{
		//System.out.println("2. Excel Initialisation Starts");
		
		storeURL 	= "file:///" + ST.getOOReportPath() + sSavedFileName;
		String templateURL 	= "file:///" + ST.getOOReportTemplatePath() + sTemplateName;
		
		System.out.println(storeURL);
		System.out.println(templateURL);
		
		xRemoteServiceManager = OO.getRemoteServiceManager("uno:socket,host=localhost,port=2002;urp;StarOffice.ServiceManager");
		
		xDoc = OO.openDoc(xRemoteServiceManager, templateURL);
		
		
		//save as the template into a new file first. This is to avoid the template being used.		
		OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);		
		OO.closeDoc(xDoc);
		
		//open up the saved file and modify from there
		xDoc = OO.openDoc(xRemoteServiceManager, storeURL);
		xSpreadsheet = OO.getSheet(xDoc, "Individual Report");
		xSpreadsheet2 = OO.getSheet(xDoc, "Sheet2");
		
	//	System.out.println("2. Excel Initialisation Completed");
	}
	
	public void InitializeExcelDevMap(String sSavedFileName, String sTemplateName) throws IOException, Exception
	{
		//System.out.println("2. Excel Initialisation Starts");
		
		storeURL 	= "file:///" + ST.getOOReportPath() + sSavedFileName;
		String templateURL 	= "file:///" + ST.getOOReportTemplatePath() + sTemplateName;
		
		xRemoteServiceManager = OO.getRemoteServiceManager("uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager");
		xDoc = OO.openDoc(xRemoteServiceManager, templateURL);
		
		//save as the template into a new file first. This is to avoid the template being used.		
		OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);		
		OO.closeDoc(xDoc);
		
		//open up the saved file and modify from there
		xDoc = OO.openDoc(xRemoteServiceManager, storeURL);
		
		xSpreadsheet0 = OO.getSheet(xDoc, "Cover");
		xSpreadsheet = OO.getSheet(xDoc, "Dev Map");
		xSpreadsheet2 = OO.getSheet(xDoc, "Quadrant Details");
	}
	
	/**
	 * Replace words with <> tags with another word
	 * @throws Exception
	 * @throws IOException
	 */
	public void Replacement() throws Exception, IOException
	{
		//System.out.println("3. Replacement Starts");
		//System.out.println("1OK");
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		String after;
		//System.out.println("2OK");	
		
		if(surveyLevel == 0) {		
			after = "(Competency Level)";
			if (ST.LangVer == 2)
				after = "(Tingkat Kompetensi)";
		
		} else {		
			after = "(Key Behaviour Level)";
			if (ST.LangVer == 2)
				after = "(Tingkat Perilaku Kunci)";
		}
		
		OO.findAndReplace(xSpreadsheet, "<Comp/KB Level>", after);
		OO.findAndReplace(xSpreadsheet, "<Job Position>", surveyInfo[1]);
			
		if (ST.LangVer == 1)
			OO.findAndReplace(xSpreadsheet, "<Target Name:>", "Target Name: " + UserName());
		else if (ST.LangVer == 2)
			OO.findAndReplace(xSpreadsheet, "<Target Name:>", "Nama Target: " + UserName());
		
		//System.out.println("3. Replacement Completed");
	}
	
	public void replacementDevelopmentMap() throws Exception, IOException
	{
		//System.out.println("3. Replacement Starts");
		
		OO.findAndReplace(xSpreadsheet0, "<target>", UserName());
		
		if (ST.LangVer == 1)
		{
			OO.findAndReplace(xSpreadsheet, "<competency>", "Competency");
			OO.findAndReplace(xSpreadsheet, "<behaviour>", "Key Behaviour");
			OO.findAndReplace(xSpreadsheet, "<positive>", "Positive Gap");
			OO.findAndReplace(xSpreadsheet, "<negative>", "Negative Gap");
		}
		else if (ST.LangVer == 2)
		{
			OO.findAndReplace(xSpreadsheet, "<competency>", "Kompetensi");
			OO.findAndReplace(xSpreadsheet, "<behaviour>", "Perilaku Kunci");
			OO.findAndReplace(xSpreadsheet, "<positive>", "Selisih Positif");
			OO.findAndReplace(xSpreadsheet, "<negative>", "Selisih Negatif");
		}
	}
	
	/**
	 * Replace words with <> tags with another word (Toyota)
	 * @throws Exception
	 * @throws IOException
	 */
	public void ReplacementToyota() throws Exception, IOException{
		
		//System.out.println("3. Replacement Starts");
		
		OO.findAndReplace(xSpreadsheet, "<Name>", UserName());
		OO.findAndReplace(xSpreadsheet, "<Job Title>", surveyInfo[1].toUpperCase());
		OO.findAndReplace(xSpreadsheet, "<EmpID>", surveyInfo[2]);
		OO.findAndReplace(xSpreadsheet, "<Company Name>", surveyInfo[7].toUpperCase());
		OO.findAndReplace(xSpreadsheet, "<Department>", surveyInfo[8].toUpperCase());
		OO.findAndReplace(xSpreadsheet, "<Group>", surveyInfo[9].toUpperCase());
		
		//System.out.println("3. Replacement Completed");
	}
	
	/**
	 * Replace words with <> tags with another word (Allianz)
	 * @throws Exception
	 * @throws IOException
	 */
	public void ReplacementAllianz() throws Exception, IOException
	{
		//System.out.println("3. Replacement Starts");
		OO.findAndReplace(xSpreadsheet, "<Target Name:>", "Assessee: " + UserName());
		//System.out.println("3. Replacement Completed");
	}
	
	/**
	 * Write CP versus CPR results to excel.
	 */
	public void InsertCPvsCPR() throws SQLException, IOException, Exception {
		
		//System.out.println("4. CP Versus CPR Starts");
		
		int iNoOfRT 	= 0;
		String RTCode 	= "";
		Vector vComp = Competency(0);
		
		Vector RT 	= RatingTask();
		Vector CP 	= new Vector();
		Vector CPR 	= new Vector();
		int type 		= 1;		// 1=cpr, 2=fpr
		int total_RT = RT.size();
		
		int [] address = OO.findString(xSpreadsheet, "<CP versus CPR Graph>");
		
		column = address[0];
		row = address[1];
		
		OO.findAndReplace(xSpreadsheet, "<CP versus CPR Graph>", "");		
		//OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+18, 18, 1);
		
		for(int i=0; i<RT.size(); i++) {
			votblSurveyRating vo = (votblSurveyRating)RT.elementAt(i);
			
			RTCode 	=  vo.getRatingCode();
		
			//System.out.println(RTCode);
			
			if(RTCode.equals("CP") || RTCode.equals("CPR") || RTCode.equals("FPR")) {
				if(RTCode.equals("CP"))
					CP = CPCPR(RTCode);
				else {
					CPR = CPCPR(RTCode);
					if(RTCode.equals("CPR")) {
						type = 1;
						iNoCPR = 0;	// CPR is chosen in this survey
					} else {
						type = 2;
					}
				}
			}
			iNoOfRT++;
		}
				
		//Chart Source Data
		//Vector vComp = CompetencyByName();
//		int total = totalCompetency();		// 1 for all		
//		String title = "Current Proficiency Vs Required Proficiency";
		
		int r = row;
		int c = 0;
		
		OO.insertString(xSpreadsheet2, "CP", r, c+1);
		if (total_RT > 1)
		{
			if(type == 1)
				OO.insertString(xSpreadsheet2, "CPR", r, c+2);
			else {
				OO.insertString(xSpreadsheet2, "FPR", r, c+2);
	//			title = "Current Proficiency Vs Future Required Proficiency";
			}
		}	
		r++;
		
		vGapSorted.clear();
		vGapUnsorted.clear();
		vCompID.clear();
		
		double dCP = 0;
		double dCPR = 0;
		
		for(int i=0; i<vComp.size(); i++) {
			voCompetency voComp = (voCompetency)vComp.elementAt(i);
			int compID 		= voComp.getCompetencyID();
			String statement = voComp.getCompetencyName();		
			String compName = UnicodeHelper.getUnicodeStringAmp(statement);
		
						
			dCP 	= 0;
			dCPR 	= 0;
			
			if(CP.size() != 0 && i<CP.size()) {
				String arr [] = (String[])CP.elementAt(i);
				dCP = Double.parseDouble(arr[2]);
			}
			
			if(CPR.size() != 0 && i<CPR.size()) {
				
				String arr [] = (String[])CPR.elementAt(i);
				dCPR = Double.parseDouble(arr[2]);
			}
			
			double gap = Math.round((dCP - dCPR) * 100.0) / 100.0;
			
			vCompID.add(new Integer(compID));
			vGapSorted.add(new String[]{compName, Double.toString(gap)});
			vGapUnsorted.add(new String[]{compName, Double.toString(gap)});
			
			OO.insertString(xSpreadsheet2, compName, r, c);
			OO.insertNumeric(xSpreadsheet2, dCP, r, c+1);
			if (total_RT > 1)
				OO.insertNumeric(xSpreadsheet2, dCPR, r, c+2);
			
			r++;
		}
		
		
//		String xAxis = "Competencies";
//		String yAxis = "Rating";
//		if (ST.LangVer == 2) {		
//			xAxis = "Kompetensi";
//			yAxis = "Penilaian";
//		}
		
		/* rianto
		//draw chart
		OO.setFontSize(8);
		//XTableChart xtablechart = OO.getChart(xSpreadsheet, xSpreadsheet2, 0, c+2, row, r-1, "Executive", 13000, 9000, row, 1);
		XTableChart xtablechart = OO.getChart(xSpreadsheet, xSpreadsheet2, 0, c+2, row, r-1, "Executive", 15500, 11500, row, 1);
		xtablechart = OO.setChartTitle(xtablechart, title);	
		OO.setChartProperties(xtablechart, false, true, true, true, true);
		xtablechart = OO.setAxes(xtablechart, xAxis, yAxis, 10, 1, 4500, 0);
		
		//need to change to LineDiagram and set the scale of xAxis, and also the width of the line
		OO.changeChartType("com.sun.star.chart.LineDiagram", xtablechart);
		*/
		
		if (total_RT > 1)
			OO.setSourceData(xSpreadsheet, xSpreadsheet2, 0, 0, c+2, row, r-1);
		else
			OO.setSourceData(xSpreadsheet, xSpreadsheet2, 0, 0, c+1, row, r-1);
		
		//System.out.println("4. CP Versus CPR Completed");
	}
	
	
	/**
	 * Write CP versus CPR results to excel.
	 */
	public void InsertCPvsCPRToyota() throws SQLException, IOException, Exception {
		
		//System.out.println("4. CP Versus CPR Starts");	
		
		//int RTID 		= 0;
		//String RTName 	= "";
		String RTCode 	= "";
		
		Vector RT 	= RatingTask();
		Vector CP 	= null;
		Vector CPR 	= null;
		Vector PastCP = null;
		int type 		= 1;		// 1=cpr, 2=fpr
		
		for(int i=0; i<RT.size(); i++) {
			votblSurveyRating vo = (votblSurveyRating)RT.elementAt(i);
			
			RTCode 	=  vo.getRatingCode();
		
			if(RTCode.equals("CP") || RTCode.equals("CPR") || RTCode.equals("FPR")) {
				if(RTCode.equals("CP")) {
					CP = CPCPRSortedID(RTCode);
					PastCP = PastCP(RTCode);
				}
				else {
					CPR = CPCPRSortedID(RTCode);
					if(RTCode.equals("CPR"))
						type = 1;
					else
						type = 2;
				}
			}
		}
		
		int [] address = OO.findString(xSpreadsheet, "<Chart Title>");
		
		column = address[0];
		row = address[1];
		row++;
		
		//Chart Source Data
		Vector vComp = Competency(0);
//		int total = totalCompetency();		// 1 for all		
		String title = "Current Proficiency Vs Required Proficiency";
		
		int r = row+2;
		int c = 0;
		
		OO.insertString(xSpreadsheet2, "CP", r, c+1);
		if(type == 1)
			OO.insertString(xSpreadsheet2, "CPR", r, c+2);
		else {
			OO.insertString(xSpreadsheet2, "FPR", r, c+2);
			title = "Current Proficiency Vs Future Required Proficiency";
		}
		OO.insertString(xSpreadsheet2, "Prev. CP", r, c+3);
		r++;
		
		OO.findAndReplace(xSpreadsheet, "<Chart Title>", title);
		
		/**
		 * Added by Jenty 6 Oct 06
		 * 
		 * Previously it didn't clear the vector, hence it will return the same gap if we generate multiple reports
		 */		
		vGapUnsorted.clear();
		
		vGapSorted.clear();
		vCompID.clear();
		
		double dCP = 0;
		double dCPR = 0;
		double dPastCP = 0;
		
		for(int i=0; i<vComp.size(); i++) {
			voCompetency voComp = (voCompetency)vComp.elementAt(i);
			
			int compID 		= voComp.getCompetencyID();
			String statement = voComp.getCompetencyName();
			
			String compName = UnicodeHelper.getUnicodeStringAmp(statement);
							
			dCP 	= 0;
			dCPR 	= 0;
			dPastCP = 0;
			
			if(CP.size() != 0 && i<CP.size()) {
				String arr [] = (String[])CP.elementAt(i);
				dCP = Double.parseDouble(arr[2]);
			}
			
			if(CPR.size() != 0 && i<CPR.size()) {
				String arr [] = (String[])CPR.elementAt(i);
				dCPR = Double.parseDouble(arr[2]);
			}
			
			if(PastCP.size() != 0 && i<PastCP.size()) {
				String arr [] = (String[])PastCP.elementAt(i);
				dPastCP = Double.parseDouble(arr[2]);
			}
			double gap = Math.round((dCP - dCPR) * 100.0) / 100.0;
			
			vCompID.add(new Integer(compID));
			vGapSorted.add(new String[]{compName, Double.toString(gap)});
			vGapUnsorted.add(new String[]{compName, Double.toString(gap)});
			
			//OO.insertString(xSpreadsheet2, compName, r, c);
			OO.insertNumeric(xSpreadsheet2, dCP, r, c+1);
			OO.insertNumeric(xSpreadsheet2, dCPR, r, c+2);
			OO.insertNumeric(xSpreadsheet2, dPastCP, r, c+3);
			
			r++;
		}
		
		//System.out.println("4. CP Versus CPR Completed");	
	}
	
	
	/**
	 * Write target gap results to excel worksheet.
	 */
	
	//****Added by Tracy 26 aug 08************************
	//Add dynamic title to Individual report
	public void InsertGapTitle(int surveyID) throws SQLException, IOException, Exception
	{
		//System.out.println("5.1 Gap Title Insertion Starts");
		
		int [] address = OO.findString(xSpreadsheet, "<Gap Title>");
		
		OO.findAndReplace(xSpreadsheet, "<Gap Title>", "");		
		
		column = address[0];
		row = address[1];
		int i=0;
		Vector RTaskID= new Vector();
	    Vector RTaskName=new Vector();
		
		//Get Rating from database according to s urvey ID
		String query = "SELECT a.RatingTaskID as RTaskID, b.RatingTask as RTaskName FROM tblSurveyRating a ";
		query += "INNER JOIN tblRatingTask b ON a.RatingTaskID=b.RatingTaskID  WHERE a.SurveyID = "+ surveyID;

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
		
		//Check CPR or FPR
		String pType="";
		String CPR="";
		String CP="";
		String FPR="";
		
		for (int n=0; RTaskID.size()-1>=n; n++ ) {
			if (((Integer)RTaskID.elementAt(n)).intValue()==1) {
				CP=RTaskName.elementAt(n).toString();
			}else if (((Integer)RTaskID.elementAt(n)).intValue()==2){
				CPR=RTaskName.elementAt(n).toString();
				pType="C";
			}else if (((Integer)RTaskID.elementAt(n)).intValue()==3){
				FPR=RTaskName.elementAt(n).toString();
				pType="F";
			}
		}
		//changed by Hemilda 15/09/2009 change word add (All) and make it fit the width of column
		String title = "";
		if (pType.equals("C"))
			title= "Gap = " + CPR + " (All) minus " + CP + " (All) : Strengths and Development Areas Report";
		else if (pType.equals("F"))
			title= "Gap = " + FPR + " (All) minus " + CP + " (All) : Strengths and Development Areas Report";
		
		//Insert title to excel file
		OO.insertString(xSpreadsheet, title, row, 0);	
		OO.mergeCells(xSpreadsheet, startColumn, endColumn, row, row);
		OO.setRowHeight(xSpreadsheet, row, 1, ROWHEIGHT*OO.countTotalRow(title, 90));				

		} catch (Exception E) {
			System.err.println("SurveyResult.java - GroupSection - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
	}
	//***End Tracy edit 26 aug 08****************************
	
	public void InsertGap() throws SQLException, IOException, Exception 
	{
		//System.out.println("5.2 Gap Insertion Starts");

		int [] address = OO.findString(xSpreadsheet, "<Gap>");
		
		column = address[0];
		row = address[1];
		int c = 0;
		
		vGapSorted = G.sorting(vGapSorted, 1);
		
		OO.findAndReplace(xSpreadsheet, "<Gap>", "");		
		
		double MinMaxGap [] = getMinMaxGap();
		
		double low = MinMaxGap[0];
		double high = MinMaxGap[1];
		//int type = 2;	// 1 is >=, 2 is >x>, 3 is <
		
		if (iNoCPR == 0)	// If no CPR is chosen in this survey
		{
			//ResultSet Gap = null;
			String title = "COMPETENCY";
	
			if (ST.LangVer == 2)
				title = "KOMPETENSI";
			
			OO.insertString(xSpreadsheet, title, row, c);		
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
	
			row++;
			OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+2, 2, 1);
			
			int startBorder = row;
			
			if (ST.LangVer == 1){
				OO.insertString(xSpreadsheet, "STRENGTH", row, c);
				OO.insertString(xSpreadsheet, "Gap >= " + high, row, 10);
			}
			else if (ST.LangVer == 2){
				OO.insertString(xSpreadsheet, "KEKUATAN", row, c);
				OO.insertString(xSpreadsheet, "Selisih >= " + high, row, 10);
			}
			
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, endColumn-1, row, row, BGCOLOR);					
			row++;
			
			for(int i=0; i<vGapSorted.size(); i++) {
				double gap = Double.valueOf(((String [])vGapSorted.elementAt(i))[1]).doubleValue();
				
				if(gap >= high) {
					String compName = ((String [])vGapSorted.elementAt(i))[0];
					
					OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+1, 1, 1);			
					
					OO.insertString(xSpreadsheet, compName, row, c);
					OO.insertNumeric(xSpreadsheet, gap, row, 10);
					row++;	
				}
			}
						
			row++;
			int endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, endColumn-1, startBorder, endBorder, false, false, true, true, true, true);
			
			startBorder = endBorder + 1;		
			row++;	
			OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+2, 2, 1);
				
			if (ST.LangVer == 1){
				OO.insertString(xSpreadsheet, "MEET EXPECTATIONS", row, c);
				OO.insertString(xSpreadsheet, low + " < Gap < " + high, row, 10);
			}
			else if (ST.LangVer == 2){			
				OO.insertString(xSpreadsheet, "MEMENUHI PENGHARAPAN", row, c);
				OO.insertString(xSpreadsheet, low + " < Selisih < " + high, row, 10);
			}
			
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, endColumn-1, row, row, BGCOLOR);
			row++;
			
			for(int i=0; i<vGapSorted.size(); i++) {
				double gap = Double.valueOf(((String [])vGapSorted.elementAt(i))[1]).doubleValue();
				
				if(gap < high && gap > low) {
					String compName = ((String [])vGapSorted.elementAt(i))[0];				
					
					OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+1, 1, 1);
					
					OO.insertString(xSpreadsheet, compName, row, c);
					OO.insertNumeric(xSpreadsheet, gap, row, 10);
					row++;
				}
			}
			
			row++;
			endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, endColumn-1, startBorder, endBorder, false, false, true, true, true, true);
			
			startBorder = endBorder + 1;		
			row++;
			
			OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+2, 2, 1);					
			
			if (ST.LangVer == 1){
				OO.insertString(xSpreadsheet, "DEVELOPMENTAL AREA", row, c);
				OO.insertString(xSpreadsheet, "Gap <= " + low, row, 10);
			}
			else if (ST.LangVer == 2){
				OO.insertString(xSpreadsheet, "AREA PERKEMBANGAN", row, c);
				OO.insertString(xSpreadsheet, "Selisih <= " + low, row, 10);
			}
			
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, endColumn-1, row, row, BGCOLOR);
			
			row++;
			
			for(int i=0; i<vGapSorted.size(); i++) {
				double gap = Double.valueOf(((String [])vGapSorted.elementAt(i))[1]).doubleValue();
				
				if(gap <= low) {
					String compName = ((String [])vGapSorted.elementAt(i))[0];
					
					OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+1, 1, 1);			
					
					OO.insertString(xSpreadsheet, compName, row, c);
					OO.insertNumeric(xSpreadsheet, gap, row, 10);
					row++;	
				}
			}
			
			endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, endColumn-1, startBorder, endBorder, false, false, true, true, true, true);
		} else {
			// Delete the rows with Gap Table description from the report
			OO.deleteRows(xSpreadsheet, 0, 12, 96, 110, 12, 1);
		}
		
		//System.out.println("5. Gap Completed");
	}
	
	
	/**
	 * Write target gap results to excel worksheet. (For Toyota combined report)
	 */
	public void InsertGapToyota() throws SQLException, IOException, Exception 
	{
		//System.out.println("5. Gap Insertion Starts");
		
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		
		row = 8;
		column = 23;
		startColumn = 23;
		endColumn = 34;
		
		Vector tempGapVector = new Vector();
		double MinMaxGap [] = getMinMaxGap();

		double low = MinMaxGap[0];
		double high = MinMaxGap[1];
		//int type = 2;	// 1 is >=, 2 is >x>, 3 is <
			
		//ResultSet Gap = null;
		String title = "COMPETENCY";
		
		OO.insertString(xSpreadsheet, title, row, column);		
		OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
		OO.setTableBorder(xSpreadsheet, startColumn, endColumn, row, row, false, false, true, true, true, true);
		row++;
		
		int startBorder = row;
		
		if (surveyLevel == 0)
		{
			OO.insertString(xSpreadsheet, "STRENGTH", row, column);
			OO.insertString(xSpreadsheet, "Gap >= " + high, row, column+10);
		
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);					
			row++;
			
			Vector rsGapValue = null;
			rsGapValue = getTargetGapToyota("AND tblGap.Gap >= " + high + " ORDER BY tblGap.Gap DESC");
		
			for(int i=0; i<rsGapValue.size(); i++)
			{
				String [] arr = (String[])rsGapValue.elementAt(i);
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(arr[1]), row, column);
				OO.insertNumeric(xSpreadsheet, Double.parseDouble(arr[2]), row, column+10);
				row++;	
			}
			
			row++;
			int endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startBorder, endBorder, false, false, true, true, true, true);
	
			startBorder = endBorder + 1;
			row++;
			
			OO.insertString(xSpreadsheet, "MEET EXPECTATIONS", row, column);
			OO.insertString(xSpreadsheet, low + " < Gap < " + high, row, column+10);
			
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);						
			row++;
			
			rsGapValue = getTargetGapToyota("AND tblGap.Gap > " + low + " AND tblGap.Gap < " + high + " ORDER BY tblGap.Gap DESC");
			
			for(int i=0; i<rsGapValue.size(); i++)
			{
				String [] arr = (String[])rsGapValue.elementAt(i);
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(arr[1]), row, column);
				OO.insertNumeric(xSpreadsheet, Double.parseDouble(arr[2]), row, column+10);
				row++;	
			}
			
			row++;
			endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startBorder, endBorder, false, false, true, true, true, true);
										
			startBorder = endBorder + 1;		
			row++;
			
			OO.insertString(xSpreadsheet, "DEVELOPMENTAL AREA", row, column);
			OO.insertString(xSpreadsheet, "Gap <= " + low, row, column+10);
						
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);
			
			row++;
			
			rsGapValue = getTargetGapToyota("AND tblGap.Gap <= " + low + " ORDER BY tblGap.Gap");
			
			for(int i=0; i<rsGapValue.size(); i++)
			{
				String [] arr = (String[])rsGapValue.elementAt(i);
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(arr[1]), row, column);
				OO.insertNumeric(xSpreadsheet, Double.parseDouble(arr[2]), row, column+10);
				row++;	
			}
			
			endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startBorder, endBorder, false, false, true, true, true, true);
		}	// SurveyLevel == 0
		else
		{
			double dGap = 0;
			String sCompName = "";
						
			OO.insertString(xSpreadsheet, "STRENGTH", row, column);
			OO.insertString(xSpreadsheet, "Gap >= " + high, row, column+10);
		
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);					
			row++;
			
			tempGapVector = sortGap(vGapSorted, 0);
			
			for(int a=0; a<tempGapVector.size(); a++)
			{
				dGap = Double.valueOf(((String [])tempGapVector.elementAt(a))[1]).doubleValue();
				sCompName = ((String [])tempGapVector.elementAt(a))[0];
				
				if (dGap >= high)
				{
					OO.insertString(xSpreadsheet, sCompName, row, column);
					OO.insertNumeric(xSpreadsheet, dGap, row, column+10);
					row++;	
				}
			}
			
			row++;
			int endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startBorder, endBorder, false, false, true, true, true, true);
	
			startBorder = endBorder + 1;
			row++;
			
			OO.insertString(xSpreadsheet, "MEET EXPECTATIONS", row, column);
			OO.insertString(xSpreadsheet, low + " < Gap < " + high, row, column+10);
			
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);						
			row++;
			
			
			for(int b=0; b<tempGapVector.size(); b++)
			{
				dGap = Double.valueOf(((String [])tempGapVector.elementAt(b))[1]).doubleValue();
				sCompName = ((String [])tempGapVector.elementAt(b))[0];
				
				if (dGap > low && dGap < high)
				{
					OO.insertString(xSpreadsheet, sCompName, row, column);
					OO.insertNumeric(xSpreadsheet, dGap, row, column+10);
					row++;	
				}
			}
			
			row++;
			endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startBorder, endBorder, false, false, true, true, true, true);
	
			startBorder = endBorder + 1;
			row++;
			
			OO.insertString(xSpreadsheet, "DEVELOPMENTAL AREA", row, column);
			OO.insertString(xSpreadsheet, "Gap <= " + low, row, column+10);
						
			OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);				
			row++;
			
			for(int d=0; d<tempGapVector.size(); d++)
			{
				dGap = Double.valueOf(((String [])tempGapVector.elementAt(d))[1]).doubleValue();
				sCompName = ((String [])tempGapVector.elementAt(d))[0];
				
				if (dGap <= low)
				{
					OO.insertString(xSpreadsheet, sCompName, row, column);
					OO.insertNumeric(xSpreadsheet, dGap, row, column+10);
					row++;	
				}
			}
			
			endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startBorder, endBorder, false, false, true, true, true, true);
		}	// SurveyLevel == 1
		
		//System.out.println("5. Gap Completed");
	}
	
	/*
	  * Sort the gap.
	  *	@param int type		0 = DESC, 1 = ASC
	 */
	public Vector sortGap(Vector vGapLocal, int type) throws SQLException, Exception 
	{
		//System.out.println("Sort vGapLocal.size() = " + vGapLocal.size());
		Vector vLocal = (Vector) vGapLocal.clone();
		Vector vSorted = new Vector();
		double max  = 0; //highest score
		double temp = 0; //temp score
		int curr  = 0; //curr highest element
		
		while(!vLocal.isEmpty()) {
			max = Double.valueOf(((String [])vLocal.elementAt(0))[1]).doubleValue();
			curr = 0;
			
			// do sorting here
			for(int t=1; t<vLocal.size(); t++) {
				temp = Double.valueOf(((String [])vLocal.elementAt(t))[1]).doubleValue();
				
				if(type == 0) {
					if(temp > max) {    
						max = temp;
						curr = t;
					}	
				} else {
					if(temp < max) {    
						max = temp;
						curr = t;
					}	
				}				
			}
			
			String info [] = {((String [])vLocal.elementAt(curr))[0], ((String [])vLocal.elementAt(curr))[1]};
			vSorted.add(info);
			
			vLocal.removeElementAt(curr);
		}
		
		return vSorted;
	}


	/**
	 * Write Normative results on excel worksheet.
	 */
	public void InsertNormative() throws SQLException, IOException, Exception 
	{
		// Added by Tracy 01 Sep 08**************************************
		// Insert CP Rating into "Normative"
		int [] CPAddress= OO.findString(xSpreadsheet, "<CP>");
		String RTaskName="";
		
		
		column = CPAddress[0];
		row = CPAddress[1];
		
//		Get CP Rating from database according to survey ID
		String query = "SELECT b.RatingTask as RTaskName FROM tblSurveyRating a ";
		query += "INNER JOIN tblRatingTask b ON a.RatingTaskID=b.RatingTaskID  WHERE a.SurveyID = "+ surveyID + " AND a.RatingTaskID=1";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			if(rs.next()) {
				RTaskName= rs.getString("RTaskName");
			}
		} catch (Exception E) {
			System.err.println("SurveyResult.java - GroupSection - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection
		}
			
		OO.findAndReplace(xSpreadsheet, "<CP>", RTaskName);
		//End Edit by Tracy 01 Sep 08******************************
		int [] address = OO.findString(xSpreadsheet, "<Normative>");
		
		column = address[0];
		row = address[1];
		
		OO.findAndReplace(xSpreadsheet, "<Normative>", "");
		//OO.insertRows(xSpreadsheet, startColumn, endColumn, row, row+26, 26, 1);
			
		
		Vector allTargets 		= null;
		Vector targetResult 	= CPCPR("CP");		
		Vector otherTarget 		= null;
		int totalTargets 		= TotalTargetsIncluded();		// include SELF		
		
		int total 	= totalCompetency();
		int tot 	= 0;
		int compID 	= 0;
		
		//Initialise array for arrN
		arrN = new int[total * 10 * 6]; //size = total competency * max 10 KBs * max 6 Rating
		
		//double [] normative = new double[total];
		//String comp [] 		= new String[total];
		double normative = 0;
		String comp		 = "";
				
		int r = row;
		
		//by Hemilda 15/09/2008 remove the competencies 0 at chart normative report
		
		for(int i=0; i<targetResult.size(); i++) {		// particular target's result by All
			String [] arr = (String[])targetResult.elementAt(i);
			
			allTargets = null;
			allTargets = TargetsID();
			
			tot = 0;		// 1 for the target himself
			normative = 0;
			
			compID 			= Integer.parseInt(arr[0]);
			comp	 		= arr[1];
			double target 	= Double.parseDouble(arr[2]);
			
			for(int j=0; j<allTargets.size(); j++) {
				
				int iTargetLoginID = ((Integer)allTargets.elementAt(j)).intValue();
				otherTarget = getOtherTargetResults(iTargetLoginID, compID);
				//Changed by HA 07/07/08
				//The result returned is a vector with only one element because 
				//it stores value of each Target, therefore should not have 
				// j < otherTarget.size()
				if(otherTarget.size() != 0 ) {
					//Changed by Ha 07/07/08 should only retrieve the first element
					String [] arrOther = (String[])otherTarget.elementAt(0);
					double all = Double.parseDouble(arrOther[2]);
					//if (compID == 870 )
					//{
					//System.out.println(target + " "+all);
					//}
					if(target >= all){
						tot++;
						if (compID == 870 )
						{
						System.out.println(target + " "+all+ " add "+tot);
						}
					}else{
						if (compID == 870 )
						{
						System.out.println(target + " "+all+ " not add");
						}
					}
				}				
			}	
			
			//to round up to 2 decimal points
			//int twodec = 100 - ((int)((double)tot/(double)totalTargets * 100 * 100));
			//System.out.println("compID "+compID);
			if (compID == 870 )
			{
			System.out.println("tot "+tot);
			System.out.println("totalTargets "+totalTargets);
			}
			tot = totalTargets - tot;
			if (compID == 870 )
			{
			System.out.println("tot "+tot);
			}
			if (totalTargets != 0) {
				if(tot == 1)
					normative = 100;
				else{
					normative =  Math.round((100 - ((double)tot/(double)totalTargets * 100.0)));
					if (compID == 870 )
					{
				//System.out.println(comp + "-----" + (double)tot/(double)totalTargets);
				//System.out.println(comp + "-----" + (double)tot/(double)totalTargets * 100.0);

				//System.out.println(comp + "-----" + (100 - ((double)tot/(double)totalTargets * 100.0)));
				//System.out.println(comp + "-----" +  Math.round(66.66666677));
				//System.out.println(comp + "-----" + normative);
					}
				
				}
			}
			else
				normative = 100;
			if (compID == 870 )
			{
			System.out.println(comp + "-----" + normative);
			}
				
				OO.insertString(xSpreadsheet2, UnicodeHelper.getUnicodeStringAmp(comp), r, 0);
				OO.insertNumeric(xSpreadsheet2, normative, r, 1);
				
				r++;
			
			
		}
		
		//rianto
		//int height = 2000 * (r - row);
		//if((r - row) > 6)
			//height = 14000;

		
		String title = "Normative Report for " + UserName() + " vs The Target Group " + surveyInfo[1];
		if(ST.LangVer == 2)
			title = "Laporan Normative untuk " + UserName() + " vs " + surveyInfo[1];
		
		/* rianto
		//draw chart
		OO.setFontSize(8);
		//XTableChart xtablechart = OO.getChart(xSpreadsheet, xSpreadsheet2, 0, 1, row, r-1, "Normative", 13000, height, row, 1);
		XTableChart xtablechart = OO.getChart(xSpreadsheet, xSpreadsheet2, 0, 1, row, r-1, "Normative", 16000, height + 1500, row, 1);
		xtablechart = OO.setChartTitle(xtablechart, title);
		xtablechart = OO.setAxes(xtablechart, "Competencies", "Results (%)", 100, 10, 4500, 2000);
		OO.setChartProperties(xtablechart, false, true, true, true, true);
		*/
		
		XTableChart xtablechart = OO.getChartByIndex(xSpreadsheet, 1);
		xtablechart = OO.setChartTitle(xtablechart, title);
        
		
		//OO.setSourceData(xSpreadsheet, xSpreadsheet2, 1, 0, 1, row+1, r-1);
		// Changed by Ha 26/05/08 change parameters passing to the following method
		OO.setSourceData(xSpreadsheet, xSpreadsheet2, 1, 0, 1, row, r);
		
		//Set back to Bar Chart Horizontal, because system automatically set back to Bar Chart Vertical.
		OO.setChartProperties(xtablechart, true, true, true, true, true);
		
	//	System.out.println("6. Normative Completed");
	}
	
	
	/**
	 * Write competency report to excel.
	 */
	
	//Added by Tracy 01 Sep 08**********************************
	public void InsertCompGap(int surveyID) throws SQLException, Exception {
		//System.out.println("6.1 Competency Gap Insertion Starts");
		
		int i=0;
		Vector RTaskID= new Vector();
	    Vector RTaskName=new Vector();
		
		//Get Rating from database according to survey ID
		String query = "SELECT a.RatingTaskID as RTaskID, b.RatingTask as RTaskName FROM tblSurveyRating a ";
		query += "INNER JOIN tblRatingTask b ON a.RatingTaskID=b.RatingTaskID  WHERE a.SurveyID = "+ surveyID;

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
		
		//Check CPR or FPR
		String pType="";
		String CPR="";
		String FPR="";
		
		for (int n=0; RTaskID.size()-1>=n; n++ ) {
			if (((Integer)RTaskID.elementAt(n)).intValue()==1) {
				//CP=RTaskName.elementAt(n).toString();
			}else if (((Integer)RTaskID.elementAt(n)).intValue()==2){
				CPR=RTaskName.elementAt(n).toString();
				pType="C";
			}else if (((Integer)RTaskID.elementAt(n)).intValue()==3){
				FPR=RTaskName.elementAt(n).toString();
				pType="F";
			}
		}
		
		String RPTitle = "";
		if (pType.equals("C"))
			RPTitle= CPR ;
		else if (pType.equals("F"))
			RPTitle= FPR ;
		
		//Insert title to excel file
		OO.findAndReplace(xSpreadsheet, "<CompRP>", RPTitle);		
			

		} catch (Exception E) {
			System.err.println("SurveyResult.java - GroupSection - " + E);
		} finally {
			ConnectionBean.closeRset(rs); // Close ResultSet
			ConnectionBean.closeStmt(st); // Close statement
			ConnectionBean.close(con); // Close connection

		}
	}
	// End add by Tracy 01 Sep 08*******************************
	
	private void mergeAndCenter(XSpreadsheet xSpreadsheet, int startRow, int endRow) throws Exception {
		OO.mergeCells(xSpreadsheet, 0, 0, startRow, endRow);
		OO.setCellAllignment(xSpreadsheet, 0, 0, startRow, startRow, 2, 2);
	}
	
	/**
	 * Added by Santoso : 16 Oct 2008
	 * To prepare the cells to write the number of rater
	 * 
	 * @return array of row position for writing nr. of rater starting from bottom to top
	 * @throws Exception 
	 */
	private int[] prepareCells(XSpreadsheet xSpreadsheet, int startingRow, int totalRater) throws Exception {
		int[] result = new int[5];
		OO.setFontSize(xSpreadsheet, 0, 0, startingRow, startingRow + 11, 12);
		if (totalRater > 4) {
 			result[0] = startingRow + 2;
			result[1] = startingRow + 4;
			mergeAndCenter(xSpreadsheet, result[1], result[1]+1);
			result[2] = startingRow + 7;
			result[3] = startingRow + 9;
			
			result[4] = startingRow + 11;
			mergeAndCenter(xSpreadsheet, result[4], result[4]+1);
		} else {
			// at least there is 4
			result[0] = startingRow + 2;
			mergeAndCenter(xSpreadsheet, result[0], result[0]+1);
			result[1] = startingRow + 5;
			mergeAndCenter(xSpreadsheet, result[1], result[1]+1);
			result[2] = startingRow + 8;
			mergeAndCenter(xSpreadsheet, result[2], result[2]+1);
			result[3] = startingRow + 11;
			mergeAndCenter(xSpreadsheet, result[3], result[3]+1);
		}
		return result;
	}
	
	private int findRatingIdx(String name, String[] rating, double[] result, int[] totalRater, String[] newRating, 
				double[] newResult, int[] newTotalRater, int idx, List ratingProcessed) {
		int ratingIdx = -1;
		for (int i = 0; i < rating.length; i++) {
			if (rating[i] != null && rating[i].equals(name)) {
				ratingIdx = i;
				break;
			}
		}
		if (ratingIdx != -1) {
			newRating[idx] = rating[ratingIdx];
			newResult[idx] = result[ratingIdx];
			newTotalRater[idx++] = totalRater[ratingIdx];
			ratingProcessed.add(new Integer(ratingIdx));
		}
		return idx;
	}
	
	/**
	 * Added by Santoso
	 * Sort Rating and Result array
	 * The order is CP(ALL), CP(SELF), CP(OTHERS), CP(SUPERVISORS), CPR(ALL)
	 * @param params array containing Rating and Result
	 */
	private Object[] sortRatingResult(Object[] params) {
		String[] rating = (String[]) params[0];
		double[] result = (double[]) params[1];
		int[] totalRater = (int[]) params[2];
		String[] newRating = new String[rating.length];
		double[] newResult = new double[result.length];
		int[] newTotalRater = new int[totalRater.length];
		
		int idx = 0;
		List ratingProcessed = new ArrayList();
		// we do it in a simple way, hardcoding the rating name here
		idx = findRatingIdx("CP(All)", rating, result, totalRater, newRating, newResult, newTotalRater, idx, ratingProcessed);
		idx = findRatingIdx("CP(Self)", rating, result, totalRater, newRating, newResult, newTotalRater, idx, ratingProcessed);
		idx = findRatingIdx("CP(Others)", rating, result, totalRater, newRating, newResult, newTotalRater, idx, ratingProcessed);
		idx = findRatingIdx("CP(Supervisors)", rating, result, totalRater, newRating, newResult, newTotalRater, idx, ratingProcessed);
		idx = findRatingIdx("CPR(All)", rating, result, totalRater, newRating, newResult, newTotalRater, idx, ratingProcessed);
		
		// do we have some rating not inserted yet?
		if (ratingProcessed.size() < rating.length) {
			for (int i = 0; i < rating.length; i++) {
				if (ratingProcessed.contains(new Integer(i))) {
					continue;
				}
				newRating[idx] = rating[i];
				newResult[idx] = result[i];
				newTotalRater[idx++] = totalRater[i];
			}
		}
		
		return new Object[] {newRating, newResult, newTotalRater};
	}
	
	/**by Santoso 2008/10/29
	 * Count total rater for the particular survey and target.
	 * for KB level
	 * To calculate number of others rater  for each rating task of each KB
	 */	
	public int totalRater(int iRatingTaskID, int iCompetencyID, String raterCode) throws SQLException 
	{	
		int total = 0;
		SurveyResult SR = new SurveyResult();
		Calculation cal = new Calculation();
		String query = "select count(*) AS Total ";
		query = query + " From( ";
		query = query + " SELECT DISTINCT tblAssignment.RaterCode";
		query = query + " FROM         tblAssignment INNER JOIN ";
		query = query + " tblResultBehaviour ON tblAssignment.AssignmentID = tblResultBehaviour.AssignmentID INNER JOIN ";
		query = query + " KeyBehaviour ON tblResultBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
		query = query + " WHERE     (tblAssignment.SurveyID =  " + surveyID + ") AND (tblAssignment.TargetLoginID = "+targetID+") " ;
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND RaterCode LIKE '"+raterCode+"' and RaterStatus in(1,2,4)";
		else
			query = query + " AND RaterCode LIKE '"+raterCode+"' and RaterStatus in(1,2,4,5)";
		query = query + "  AND (tblResultBehaviour.RatingTaskID = "+iRatingTaskID+")and (KeyBehaviour.FKCompetency = "+iCompetencyID +") ";
		if (cal.NAIncluded(surveyID)==0)
			query = query + " AND (tblResultBehaviour.Result <> 0)";
		query = query + "  ) table1 ";


		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  		   if(rs.next())
	 			total = rs.getInt(1);


  		}catch(Exception ex){
			System.out.println("IndividualReport.java - totalRater - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return total;

	}
	
	public void InsertCompetency(int reportType) throws SQLException, Exception {

		int iN = 0; //To be used as counter for arrN
				
	//	System.out.println("7. Competencies Starts");
		
		int [] address = OO.findString(xSpreadsheet, "<Report>");
		
		column = address[0];
		row = address[1];

		OO.findAndReplace(xSpreadsheet, "<Report>", "");
						
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		String level 	= "Competency";
		if (ST.LangVer == 2) 
			level = "Kompetensi";
		
		int totalOtherRT = totalOtherRT();
		int total 		= totalGroup() + totalOtherRT + 1;		// 1 for all
		
	
		// int rowTotal 	= row + 1;
        // Changed by Ha 27/05/08: size of array from total --> total + 1
		// String [] Rating = new String [total+1];
		// double [] Result = new double [total+1];
		// Change by Santoso 22/10/08 : Rating, Result now fixed to 5 : CP(All), CP(Sups), CP(Oth), CP(Self), CPR(All)
		// we can hardcode the string here anyway, since it is also hardcoded belows
		String [] Rating = new String [4 + totalOtherRT];
		double [] Result = new double [4 + totalOtherRT];
		int[] totalRater = new int[4 + totalOtherRT];
		// initialize Rating --> CP only
		Rating[0] = "CP(All)";
		Rating[1] = "CP(Supervisors)";
		Rating[2] = "CP(Others)";
		Rating[3] = "CP(Self)";
		
		int maxScale = MaxScale();
		
		int count = 0; // to count total chart for each page, max = 2;
		int r1 = 1;
		int add = 13/total;
		
		int totalOth = 0;
		int totalSup = 0;				
		int totalSelf = 0;
		int totalAll = 0;
	
		Vector vComp = CompetencyByName();
		
		OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);

		if (ST.LangVer == 1)
			OO.insertString(xSpreadsheet, level + " Report", row, 0);
		else if (ST.LangVer == 2)
			OO.insertString(xSpreadsheet, "Laporan " + level, row, 0);
		
		OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row); 
		row += 2;
		
		if(surveyLevel == 0) {

			int startRow = row;	// for border
			int endRow = row;
			
			for(int i=0; i<vComp.size(); i++) {
				
				
				
				voCompetency voComp = (voCompetency)vComp.elementAt(i);
				
				int compID 		= voComp.getCompetencyID();
				String statement = voComp.getCompetencyName();
				String desc = voComp.getCompetencyDefinition();
				
			
				int RTID = 0;			
				int KBID = 0;

				startRow = row;
				
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(statement), row, 0);
				OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
				OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);							
				row++;
				
				r1 = row;
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(desc), row, 0);
				OO.mergeCells(xSpreadsheet, startColumn, endColumn, row, row);
				OO.mergeCells(xSpreadsheet, startColumn, endColumn, row, row);
				OO.setRowHeight(xSpreadsheet, row, 1, ROWHEIGHT*OO.countTotalRow(desc, 90));
				row++;

				String RTCode = "";
				
				Vector RT = RatingTask();
				int r = 0;
				
				// Change by Santoso : Allignment of n number with the bar of the graph 
				// was : rowTotal = row + 11;
				int[] rowPos = prepareCells(xSpreadsheet, row, totalRater.length);
				boolean hasCPRFPR = false;
				for(int j=0; j<RT.size(); j++) {
					votblSurveyRating vo = (votblSurveyRating)RT.elementAt(j);
					
					RTID = vo.getRatingTaskID();
					RTCode 	=  vo.getRatingCode();
					
				
					Vector result = MeanResult(RTID, compID, KBID);
					
					
					if(RTCode.equals("CP")) 
					{
						//Changed by Ha 09/07/08 to calculate total rater for each competency for each rating task
						totalOth = totalOth(1,compID);
						totalSup = totalSup(1, compID);						
						totalSelf = totalSelf(1, compID);
						totalAll = totalOth + totalSup;
						
						for(int k=0; k<result.size(); k++) 
						{							
							String [] arrOther = (String[])result.elementAt(k);
							
							int type = Integer.parseInt(arrOther[1]);
							String t = "";
							switch(type) 
							{
								case 1 : t = "All";
								
										 arrN[iN] = totalAll;
										 iN++;
										 totalRater[0] = totalAll;
										 break;
								case 2 : t = "Supervisors";
										 arrN[iN] = totalSup;
										 iN++;
										 totalRater[1] = totalSup;
									 	 break;
								case 3 : t = "Others";
										 arrN[iN] = totalOth;
										 iN++;
										 totalRater[2] = totalOth;
										 break;		 		 	
								case 4 : t = "Self";
										 arrN[iN] = totalSelf;
										 iN++;
										 totalRater[3] = totalSelf;
										 break;
							}

							//Should not insert a "\n". Will push col into 2 rows. Printing will go haywire (Maruli)
							//Rating[r] = RTCode + "\n(" + t + ")";
							Rating[type-1] = RTCode + "(" + t + ")";
							if(iReportType == 1)
								Rating[type-1] = Rating[r].replaceAll("\n", " ");
							
							Result[type-1] = Double.parseDouble(arrOther[2]);						
						}
					}
					else if(RTCode.equals("CPR") || RTCode.equals("FPR"))
					{
						//Changed by Ha 26/06/08 should not have j < result.size in the condition
						//Problem with old condition: value were not displayed correctly
						if (result.size()  > 0)
						{						
							hasCPRFPR = true;
							String [] arrOther = (String[])result.elementAt(0);
							
							//Should not insert a "\n". Will push col into 2 rows. Printing will go haywire (Maruli)
							//Rating[r] = RTCode + "\n(All)";
							
							// Change by Santoso 22/10/08 : Rating order and value already initialized above (also the Result)
							// we need to set the RTCode and the result at the appropriate position as already defined above
							Rating[4] = RTCode + "(All)";
							
							if(iReportType == 1)
								Rating[4] = Rating[4].replaceAll("\n", " ");
							
							Result[4] = Double.parseDouble(arrOther[2]);
							if (RTCode.equals("CPR"))
								totalAll = totalSup(2,compID) + totalOth(2,compID);
							else if (RTCode.equals("FPR"))
								totalAll = totalSup(3,compID) + totalOth(3,compID);
							arrN[iN] = totalAll;
							iN++;
							totalRater[4] = totalAll;	
						}
					}
				}//while RT


				// Add by Santoso (2008-10-19)
				// Sort Rating (and also Result), the order is CP(ALL), CP(SELF), CP(OTHERS), CP(SUPERVISOR), CPR(ALL)
				Object[] params = new Object[] {Rating, Result, totalRater};
				params = sortRatingResult(params);
				Rating = (String[]) params[0];
				Result = (double[]) params[1];
				totalRater = (int[]) params[2];
				
				// add by Santoso
				// moved all OO.insertNumeric above to here
//				if(iReportType == 2) {
//					for (int idx = 0; idx < totalRater.length; idx++) {
//					 	// OO.insertNumeric(xSpreadsheet, totalAll, rowTotal, 0);
//						OO.insertNumeric(xSpreadsheet, totalRater[idx], rowPos[idx], 0);	
//					}
//				}
				
				row++;		//start draw chart from here
				OO.setFontSize(12);
				
				drawChart(Rating, Result, totalRater, rowPos, maxScale);
				column = 9;		//write the importance n gap
				int rtemp = row;
						
				Vector vImportance = Importance(compID, KBID);

				for(int j=0; j<vImportance.size(); j++) {
					String [] arr = (String[]) vImportance.elementAt(j);
					String task = arr[1];
					double taskResult = Double.parseDouble(arr[2]);
					
					OO.insertString(xSpreadsheet, task + ": " + taskResult, rtemp, column);
					OO.mergeCells(xSpreadsheet, column, endColumn, rtemp, rtemp+1);
					OO.setCellAllignment(xSpreadsheet, column, endColumn, rtemp, rtemp+1, 2, 1);
					
					rtemp += 3;
				}
				
				double gap = 0;
				if (hasCPRFPR) {
					gap = getAvgGap(compID);
					 // If CPR is chosen in this survey
					{
						if (ST.LangVer == 1)
							OO.insertString(xSpreadsheet, " Gap = " + gap, rtemp, column);
						else if (ST.LangVer == 2)
							OO.insertString(xSpreadsheet, "Selisih = " + gap, rtemp, column);	
												
						
						OO.mergeCells(xSpreadsheet, column, endColumn, rtemp, rtemp+1);
					
						OO.setCellAllignment(xSpreadsheet, column, endColumn, rtemp, rtemp+1, 2, 1);	
					}				
				}
				rtemp+=3;
							
				double LOA = LevelOfAgreement(compID, KBID);
				if (ST.LangVer == 1)
					OO.insertString(xSpreadsheet, "Level Of Agreement: \n" + LOA + "%", rtemp, column);
				else if (ST.LangVer == 2)
					OO.insertString(xSpreadsheet, "Tingkat Persetujuan: \n" + LOA + "%", rtemp, column);
				
				OO.mergeCells(xSpreadsheet, column, endColumn, rtemp, rtemp+1);
				OO.setCellAllignment(xSpreadsheet, column, endColumn, rtemp, rtemp+1, 2, 1);
													
				column = 0;
				count++;
				
				if(count == 2) {
					count = 0;
					row += 17;					
					OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
				} else {
					column = 0;					
					row += 16;						
				}
				
				endRow = row-1;
				//comp name and definition
				OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startRow, startRow+1, 
    							false, false, true, true, true, true);
    			
    			//total sup n others
				
    			OO.setTableBorder(xSpreadsheet, startColumn, startColumn, startRow+2, endRow, 
    							false, false, true, true, true, true);
			
    			//chart
				
    			OO.setTableBorder(xSpreadsheet, startColumn+1, 8, startRow+2, endRow, 
    							false, false, true, true, true, true);
    			OO.setTableBorder(xSpreadsheet, 9, endColumn, startRow+2, endRow, 
    							false, false, true, true, true, true);
    											    			OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, startRow+2, endRow, 1, 2);
				OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, startRow+2, endRow, 1, 2);
    						
			}// while Comp
			
		
			
		} else {	//kb level
			
			int start = 0;
			int startRow = row;	// for border
			int endRow = row;						
			
			for(int i=0; i<vComp.size(); i++)
			{					
				//rowTotal = row + 1;
				// Add by Santoso : reinitialize array per loop
				// reinitialize the array each loop (otherwise it will use the previous value)
				totalRater = new int[totalRater.length];
				Result = new double[Result.length];
				// Reset only rating[4] since Rating[0]..[3] always have the same value
				if (Rating.length > 4) {
					Rating[4] = "";
				}
				
				start = 0;
				int RTID = 0;
						
				int KBID = 0;
				String KB = "";
			
				voCompetency voComp = (voCompetency)vComp.elementAt(i);
				
				int compID = voComp.getCompetencyID();

				String statement = voComp.getCompetencyName();
				String desc = voComp.getCompetencyDefinition();
				
				startRow = row;
				
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(statement), row, column);
				OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
				OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);			
				row++;
				
				r1 = row;
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(desc), row, column);
				OO.mergeCells(xSpreadsheet, startColumn, endColumn, row, row);
				OO.setRowHeight(xSpreadsheet, row, 1, ROWHEIGHT*OO.countTotalRow(desc, 90));
						
				row++;
				start++;

				String RTCode = "";
			
				Vector RT = RatingTask();
			
				// Change by Santoso : Allignment of n number with the bar of the graph 
				// was : rowTotal = row + 11;
				int[] rowPos = prepareCells(xSpreadsheet, row, totalRater.length);
				boolean hasCPRFPR = false;
				//Print out the "N" in other section for simplified report
				
				for(int j=0; j<RT.size(); j++) {
						votblSurveyRating vo = (votblSurveyRating)RT.elementAt(j);
					
						RTID = vo.getRatingTaskID();
						RTCode 	=  vo.getRatingCode();
						
						Vector result = null;
	
						if(RTCode.equals("CP")) {
							result = KBMean(RTID, compID);
							//by Hemilda 23/09/2008 get the total sup, oth, self, all by competency
//							totalOth =totalOth1(RTID, compID);
//							totalSup = totalSup1(RTID, compID);						
//							totalSelf = totalSelf1(RTID, compID);
//							
							// Change by Santoso 2008/10/29 
							totalOth = totalRater(RTID, compID, "OTH%");
							totalSup = totalRater(RTID, compID, "SUP%");
							totalSelf = totalRater(RTID, compID, "SELF");
							totalAll = totalOth + totalSup;
							
							for(int k=0; k<result.size(); k++) {
								
								String [] arr = (String[])result.elementAt(k);
								
								int type = Integer.parseInt(arr[1]);
								String t = "";
								switch(type) 
								{
									case 1 : t = "All";
									
											 arrN[iN] = totalAll;
											 iN++;
											 totalRater[0] = totalAll;
											 break;
									case 2 : t = "Supervisors";
											 arrN[iN] = totalSup;
											 iN++;
											 totalRater[1] = totalSup;
										 	 break;
									case 3 : t = "Others";
											 arrN[iN] = totalOth;
											 iN++;
											 totalRater[2] = totalOth;
											 break;		 		 	
									case 4 : t = "Self";
											 arrN[iN] = totalSelf;
											 iN++;
											 totalRater[3] = totalSelf;
											 break;
								}

								//Should not insert a "\n". Will push col into 2 rows. Printing will go haywire (Maruli)
								//Rating[r] = RTCode + "\n(" + t + ")";
								// Change by Santoso 22/10/08 : Rating order and value already initialized above (also the Result)
								// we need to set the RTCode and the result at the appropriate position as already defined above
								Rating[type-1] = RTCode + "(" + t + ")";
								if(iReportType == 1)
									Rating[type-1] = Rating[type-1].replaceAll("\n", " ");
							
								if(type == 1)
									Result[type-1] = CompTrimmedMeanforAll(RTID, compID);
								else
									Result[type-1] = Double.parseDouble(arr[2]);
							}
						}else if(RTCode.equals("CPR") || RTCode.equals("FPR")){
							// need to keep track whether CPR/FPR is included in the survey or not to keep Gap from printed if no CPR/FPR
							hasCPRFPR = true;
							// Change by Santoso 2008/10/29 
							totalOth = totalRater(RTID, compID, "OTH%");
							totalSup = totalRater(RTID, compID, "SUP%");
							totalAll = totalOth + totalSup;
							arrN[iN] = totalAll;
							iN++;
							//Should not insert a "\n". Will push col into 2 rows. Printing will go haywire (Maruli)
							//Rating[r] = RTCode + "\n(All)";
							// Change by Santoso 22/10/08 : Rating order and value already initialized above (also the Result)
							// we need to set the RTCode and the result at the appropriate position as already defined above
							Rating[4] = RTCode + "(All)";
							if(iReportType == 1)
								Rating[4] = Rating[4].replaceAll("\n", " ");
							
							totalRater[4] = totalAll;
							Result[4] = CompTrimmedMeanforAll(RTID, compID);
						}				
					
					}//while RT
				
					row++;
					
					// add by Santoso
					// moved all OO.insertNumeric above to here
//					if(iReportType == 2) {
//						for (int idx = 0; idx < totalRater.length; idx++) {
//						 	// OO.insertNumeric(xSpreadsheet, totalAll, rowTotal, 0);
//							OO.insertNumeric(xSpreadsheet, totalRater[idx], rowPos[idx], 0);	
//						}
//					}
					
					drawChart(Rating, Result, totalRater, rowPos, maxScale);						
									
					column = 9;
					r1 = row;
							
					Vector Importance = AvgImportance(compID);

					for(int j=0; j<Importance.size(); j++) {
						String [] arr = (String[])Importance.elementAt(j);
						
						String task = arr[1];
						double taskResult = Double.parseDouble(arr[2]);
						
						OO.insertString(xSpreadsheet, task + ": " + taskResult, r1, column);
						OO.mergeCells(xSpreadsheet, column, endColumn, r1, r1+1);														
						r1 += 3;
					}
					
					double gap = 0;
					if (hasCPRFPR) {
						int element = vCompID.indexOf(new Integer(compID));
						gap = Double.valueOf(((String [])vGapUnsorted.elementAt(element))[1]).doubleValue();
						//System.out.println(gap + "----" + compID + " --- " + element);
						if (iNoCPR == 0)	// If CPR is chosen in this survey
						{
							if (ST.LangVer == 1)
								OO.insertString(xSpreadsheet, "Gap = " + gap, r1, column);							
							else if (ST.LangVer == 2)
								OO.insertString(xSpreadsheet, "Selisih = " + gap, r1, column);	
							
							OO.mergeCells(xSpreadsheet, column, endColumn, r1, r1+1);
						}
					}
					r1 += 3;
			
					double LOA = AvgLevelOfAgreement(compID, totalAll);
					//System.out.println(LOA + "----" + compID );
					if (ST.LangVer == 1)
						OO.insertString(xSpreadsheet, "Level Of Agreement: \n" + LOA + "%", r1, column);	
					else if (ST.LangVer == 2)
						OO.insertString(xSpreadsheet, "Tingkat Persetujuan: \n" + LOA + "%", r1, column);	
					OO.mergeCells(xSpreadsheet, column, endColumn, r1, r1+1);									
					r1 += 3;
									
					count++;
					column = 0;
					if(count == 2) {
						count = 0;
						row += 15;						
						OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
					} else {					
						row += 15;						
					}
					
					endRow = row-1;

					//comp name and definition
					OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startRow, startRow+1, 
	    							false, false, true, true, true, true);
	    			
	    			//total sup n others				
	    			OO.setTableBorder(xSpreadsheet, startColumn, startColumn, startRow+2, endRow, 
	    							false, false, true, true, true, true);
	    			//chart
	    			OO.setTableBorder(xSpreadsheet, startColumn+1, 8, startRow+2, endRow, 
	    							false, false, true, true, true, true);	
	    			OO.setTableBorder(xSpreadsheet, 9, endColumn, startRow+2, endRow, 
	    							false, false, true, true, true, true);
	    											
	    			OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, startRow+2, endRow, 1, 2);
					
					
					// KB LEVEL //				
					
					//(30-Sep-05) Rianto: Replaced with Simplified report with no competencies & KB charts
					//if(reportType == 2) {			// only if standard report, simplified report no need for KB
			
					Vector KBList = KBList(compID);	
					
					for(int j=0; j<KBList.size(); j++) {
						voKeyBehaviour voKB = (voKeyBehaviour)KBList.elementAt(j);
						KBID = voKB.getKeyBehaviourID();
						KB = voKB.getKeyBehaviour();
						
						startRow = row;
						r1 = row;
					
						OO.insertString(xSpreadsheet, start + ". " + UnicodeHelper.getUnicodeStringAmp(KB), row, 0);
						OO.mergeCells(xSpreadsheet, startColumn, endColumn, row, row);
						OO.setRowHeight(xSpreadsheet, row, 0, ROWHEIGHT*OO.countTotalRow(KB, 90));
						
						row += 2;
						start++;

						// Change by Santoso : Allignment of n number with the bar of the graph 
						// was : rowTotal = row + 11;
						rowPos = prepareCells(xSpreadsheet, row, totalRater.length);
					    totalRater = new int[4 + totalOtherRT]; // reset totalRater
					    Result = new double[4 + totalOtherRT]; // reset Result
						RT = RatingTask();
						// initialize cpr/fpr flag
						hasCPRFPR = false;
						for(int k=0; k<RT.size(); k++) {
							votblSurveyRating vo = (votblSurveyRating)RT.elementAt(k);
							RTID = vo.getRatingTaskID();
							RTCode 	=  vo.getRatingCode();
					
							Vector result = MeanResult(RTID, compID, KBID);
							
							if(RTCode.equals("CP")) {
								//Comment off by Ha 02/07/08 should not re-set the r to 0
								//It will print out the KB CP, CPR, FPR incorrecly
								//r = 0;
								
								//by Hemilda 23/09/2008 to get total oth,sup,self,all for each kb
								totalOth =totalOth(RTID, compID, KBID);
								totalSup = totalSup(RTID, compID, KBID);						
								totalSelf = totalSelf(RTID, compID, KBID);
								totalAll = totalOth + totalSup;
								for(int l=0; l<result.size(); l++) {
									String [] arr = (String[])result.elementAt(l);
									
									int type = Integer.parseInt(arr[1]);
									String t = "";
					
									switch(type) {
										case 1 : t = "All";	
												 arrN[iN] = totalAll;
												 iN++;
												 totalRater[0] = totalAll;
												 break;
										case 2 : t = "Supervisors";
												 arrN[iN] = totalSup;
												 iN++;
												 totalRater[1] = totalSup;
											 	break;
										case 3 : t = "Others";
												 arrN[iN] = totalOth;
												 iN++;
												 totalRater[2] = totalOth;
												 break;	 	
										case 4 : t = "Self";
												 arrN[iN] = totalSelf;
												 iN++;
												 totalRater[3] = totalSelf;
												 break;
									}
									
									//Should not insert a "\n". Will push col into 2 rows. Printing will go haywire (Maruli)
									//Rating[r] = RTCode + "\n(" + t + ")";
									
									// Change by Santoso 22/10/08 : Rating order and value already initialized above (also the Result)
									// we need to set the RTCode and the result at the appropriate position as already defined above
									Rating[type-1] = RTCode + "(" + t + ")";
									
									if(iReportType == 1)
										Rating[type-1] = Rating[type-1].replaceAll("\n", " ");
						
									Result[type-1] = Double.parseDouble(arr[2]);	;

								}
							}else if(RTCode.equals("CPR") || RTCode.equals("FPR")){
								//Comment off by Ha 02/07/08 should not re-set the r to 0
								//It will print out the KB CP, CPR, FPR incorrecly
								
								//r = 0;
								// Change by Santoso : 2008-10-19
								// Do we need to check k< result.size? 
								// k refers to index of RatingTask and result refers to the MeanResult
								if(result.size() != 0) { // && k<result.size()) {
									// Change by Santoso : 2008-10-19
									// Wrong value seems to be retrieved (sup's value --> result[2]
									// Retrieve the correct value for All in result[0]
									// String [] arr = (String[])result.elementAt(k);
									String [] arr = (String[])result.elementAt(0);
									hasCPRFPR = true;
									//Should not insert a "\n". Will push col into 2 rows. Printing will go haywire (Maruli)
									//Rating[r] = RTCode + "\n(All)";
									// Change by Santoso 22/10/08 : Rating order and value already initialized above (also the Result)
									// we need to set the RTCode and the result at the appropriate position as already defined above									
									Rating[4] = RTCode + "(All)";
									if(iReportType == 1)
										Rating[4] = Rating[4].replaceAll("\n", " ");
									
									// Change by Santoso
									// Fix CPR(All), we need to find it again (instead of using the current totalAll value)
									totalOth =totalOth(RTID, compID, KBID);
									totalSup = totalSup(RTID, compID, KBID);						
									totalAll = totalOth + totalSup;
									totalRater[4] = totalAll;
									Result[4] = Double.parseDouble(arr[2]);
								}					
							}
							
						}//while RT
		
						row++;
						
						// add by Santoso
						// moved all OO.insertNumeric above to here
//						if(iReportType == 2) {
//							for (int idx = 0; idx < totalRater.length; idx++) {
//							 	// OO.insertNumeric(xSpreadsheet, totalAll, rowTotal, 0);
//								OO.insertNumeric(xSpreadsheet, totalRater[idx], rowPos[idx], 0);	
//							}
//						}
						
						drawChart(Rating, Result, totalRater, rowPos, maxScale);
						column = 9;							
						r1 = row;
					
						Vector vImportance = Importance(compID, KBID);

						for(int k=0; k<vImportance.size(); k++) {
							String [] arr = (String[]) vImportance.elementAt(k);
							String task = arr[1];
							double taskResult = Double.parseDouble(arr[2]);
							
							
							arrN[iN] = totalAll;
							iN++;
							OO.insertString(xSpreadsheet, task + ": " + taskResult, r1, column);
							OO.mergeCells(xSpreadsheet, column, endColumn, r1, r1+1);
							r1 += 3;
						}

						if (hasCPRFPR) {
							gap = Gap(compID, KBID);
							gap = Math.round(gap * 100.0) / 100.0;
						
							if (iNoCPR == 0)	// If CPR is chosen in this survey
							{
								if (ST.LangVer == 1)
									OO.insertString(xSpreadsheet, "Gap = " + gap, r1, column);
								else if (ST.LangVer == 2)
									OO.insertString(xSpreadsheet, "Selisih = " + gap, r1, column);	
								
								OO.mergeCells(xSpreadsheet, column, endColumn, r1, r1+1);
							}
						}
						r1 += 3;
					
						LOA = LevelOfAgreement(compID, KBID);
						if (ST.LangVer == 1)
							OO.insertString(xSpreadsheet, "Level Of Agreement: \n" + LOA + "%", r1, column);
						else if (ST.LangVer == 2)
							OO.insertString(xSpreadsheet, "Tingkat Persetujuan: \n" + LOA + "%", r1, column);
												
						OO.mergeCells(xSpreadsheet, column, endColumn, r1, r1+1);																				
						r1 += 3;							
					
						count++;
						column = 0;
						if(count == 2) {
							count = 0;								
							row += 16;
							OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
							
						} else								
							row += 16;
						
						endRow = row - 1;
						
						//comp name and definition
						OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startRow, startRow+1, 
		    							false, false, true, true, true, true);
		    			
		    			//total sup n others				
		    			OO.setTableBorder(xSpreadsheet, startColumn, startColumn, startRow+2, endRow, 
		    							false, false, true, true, true, true);
		    			//chart
		    			OO.setTableBorder(xSpreadsheet, startColumn+1, 8, startRow+2, endRow, 
		    							false, false, true, true, true, true);	
		    			OO.setTableBorder(xSpreadsheet, 9, endColumn, startRow+2, endRow, 
		    							false, false, true, true, true, true);
		    											
		    			OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, startRow+2, endRow, 1, 2);
				}//while KBList
				//}	// end if of standard version
			}// while Comp
		}
	}
	
	
	/**
	 * Write competency report to excel.
	 */
	public void InsertCompetencyToyota() throws SQLException, Exception 
	{	
		//System.out.println("7. Competencies Starts");
		
		column = 0;
		row = 32;
		startColumn = 0;
		endColumn = 4;
		
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		int total 		= totalGroup() + totalOtherRT() + 1;		// 1 for all
		//int total 		= totalGroup() + totalOtherRT() + 2; // For TMT problematic rep, COMMENT OFF (Maruli)
		
		String [] Rating = new String [total];
		double [] Result = new double [total];
		
		int iCompCount = 1; // Counter to count the no of competencies
		int iCompRow = 32;	// Counter to store Row of <Comp Name X>
		int iCompCol = 0;	// Counter to count Column of <Comp Name X>
		//int iCompRow = 60;	// Counter to store Row of <Comp Name X>
		//int iCompCol = 18;	// Counter to count Column of <Comp Name X>
		
		//int totalOth = totalOth();
		//int totalSup = totalSup();
		//int totalSelf = totalSelf();
		//int totalAll = totalOth + totalSup;
		
		//ResultSet competency = Competency(1);
		Vector vComp = Competency(0);
		
		if(surveyLevel == 0) {

			for(int i=0; i<vComp.size(); i++) {
				voCompetency voComp = (voCompetency)vComp.elementAt(i);
				
				int compID 		= voComp.getCompetencyID();
				String statement = voComp.getCompetencyName();
				String desc = voComp.getCompetencyDefinition();
				
				int RTID = 0;			
				int KBID = 0;
				
				
				String RTCode = "";
				
				Vector RT = RatingTask();
				int r = 0;
			
				for(int j=0; j<RT.size(); j++) {
					votblSurveyRating vo = (votblSurveyRating)RT.elementAt(j);
					
					RTID = vo.getRatingTaskID();
					RTCode 	=  vo.getRatingCode();
					
					Vector result = MeanResult(RTID, compID, KBID);
					
					if(RTCode.equals("CP")) {
						for(int l=0; l<result.size(); l++) {
							String [] arr = (String[])result.elementAt(l);
							
							int type = Integer.parseInt(arr[1]);
							String t = "";
							
							switch(type) {
								case 1 : t = "All";
										 break;
								case 2 : t = "Supervisors";
									 	 break;
								case 3 : t = "Others";
										 break;
								case 4 : t = "Self";
										 break;
							}
							
							Rating[r] = RTCode + " (" + t + ")";
							Result[r++] = Double.parseDouble(arr[2]);
						}
					} else if(RTCode.equals("CPR") || RTCode.equals("FPR")) {

						if(result.size() != 0 && j < result.size()) {
							String [] arr = (String[])result.elementAt(j);
							
							int type = Integer.parseInt(arr[1]);
							Rating[r] = RTCode + " (All)";
							Result[r++] = Double.parseDouble(arr[2]);
						}
					}

				}//while RT
				
				r = row;
				
				// Insert Score
				for(int j=0; j<Rating.length; j++) 
				{	
					if (Rating[j].equals("CPR (All)"))
						r = row + 5;
					else if (Rating[j].equals("CP (All)"))
						r = row + 3;
					else if (Rating[j].equals("CP (Self)"))
						r = row + 2;
					else if (Rating[j].equals("CP (Supervisors)"))
						r = row + 1;
					else if (Rating[j].equals("CP (Others)"))
						r = row;
					
					OO.insertString(xSpreadsheet2, Rating[j], r, column);
					OO.insertNumeric(xSpreadsheet2, Result[j], r, column+1);
				}
				
				int element = vCompID.indexOf(new Integer(compID));
				double gap = Double.valueOf(((String [])vGapUnsorted.elementAt(element))[1]).doubleValue();					
				
				OO.insertString(xSpreadsheet, " Gap = " + gap, iCompRow, iCompCol);
				
    			row = row + 7;
    			iCompCount++;
    			iCompCol = iCompCol + 6;
    			//iCompCol = iCompCol - 6;
    			
    			/*
    			 *	After 4 competencies name are printed, increase rows to print the next 4 competencies
    			 *	At the same time reduce column to 0 to start printing from Left to Right again
    			 */
    			 
    			if (iCompCount == 5 || iCompCount == 9)
    			{
    				iCompCol = 0;
    				iCompRow = iCompRow + 14;
    			}
    			/*
    			if (iCompCount == 5 || iCompCount == 9)
    			{
    				iCompCol = 18;
    				iCompRow = iCompRow - 14;
    			}
    			*/
			}// while Comp
			
				
		}else {	//kb level
			
			for(int i=0; i<vComp.size(); i++) {
				voCompetency voComp = (voCompetency)vComp.elementAt(i);
				
				int r = 0;
				int RTID = 0;
				int compID = voComp.getCompetencyID();								
				String RTCode = "";
			
				Vector RT = RatingTask();

				for(int j=0; j<RT.size(); j++) {
					votblSurveyRating vo = (votblSurveyRating)RT.elementAt(j);
					
					RTID = vo.getRatingTaskID();
					RTCode 	=  vo.getRatingCode();

					Vector result = null;

					if(RTCode.equals("CP")) {
						result = KBMean(RTID, compID);

						for(int k=0; k<result.size(); k++) {
							String [] arr = (String[])result.elementAt(k);
							
							int type = Integer.parseInt(arr[1]);
							String t = "";

							switch(type) {
									case 1 : t = "All";
											 break;
									case 2 : t = "Supervisors";
										 	 break;
									case 3 : t = "Others";
											 break;		 		 	
									case 4 : t = "Self";
											 break;
								}

								Rating[r] = RTCode + " (" + t + ")";
								
								if(type == 1)
									Result[r++] = CompTrimmedMeanforAll(RTID, compID);
								else
									Result[r++] = Double.parseDouble(arr[2]);
																																
							}
						}else if(RTCode.equals("CPR") || RTCode.equals("FPR")) {
																
								Rating[r] = RTCode + " (All)";								
								Result[r++] = CompTrimmedMeanforAll(RTID, compID);				
						}				
				
					}//while RT
					
					r = row;

					// Insert Score
					for(int j=0; j<Rating.length; j++) {
						
						if (Rating[j].equals("CPR (All)"))
							r = row + 5;
						else if (Rating[j].equals("CP (All)"))
							r = row + 3;
						else if (Rating[j].equals("CP (Self)"))
							r = row + 2;
						else if (Rating[j].equals("CP (Supervisors)"))
							r = row + 1;
						else if (Rating[j].equals("CP (Others)"))
							r = row;

						OO.insertString(xSpreadsheet2, Rating[j], r, column);
						OO.insertNumeric(xSpreadsheet2, Result[j], r, column+1);
						//r++;
					}
										
					int element = vCompID.indexOf(new Integer(compID));
					double gap = Double.valueOf(((String [])vGapUnsorted.elementAt(element))[1]).doubleValue();
					
					OO.insertString(xSpreadsheet, " Gap = " + gap, iCompRow, iCompCol);
					
	    			row = row + 7;
	    			iCompCount++;
	    			//Uncommented by Jenty on 27-Sept-06
	    			//Otherwise the gap did not show properly
	    			iCompCol = iCompCol + 6;
	    			//iCompCol = iCompCol - 6;
	    			
	    			/*
	    			 *	After 4 competencies name are printed, increase rows to print the next 4 competencies
	    			 *	At the same time reduce column to 0 to start printing from Left to Right again
	    			 */
	    			if (iCompCount == 5 || iCompCount == 9)
	    			{
	    				iCompCol = 0;
	    				iCompRow = iCompRow + 14;
	    				//iCompCol = 18;
	    				//iCompRow = iCompRow - 14;
	    			}
			}// while Comp
		}
		
		Vector vResult = getCompAvg(surveyID, vCompID, "CP");
		
		int r = 36;
		
		for(int v=0;v<vResult.size();v++)
		{
			double dAvgCPPos = Double.parseDouble((String)vResult.elementAt(v));
			
			OO.insertString(xSpreadsheet2, "Avg. CP of Position", r, column);
			OO.insertNumeric(xSpreadsheet2, dAvgCPPos, r, column+1);

			r = r + 7;
		}
	}
	
	/**
	 * Draw bar chart for competency report.
	 */
	public void drawChart(String Rating [], double Result [], int[] totalRater, int[] rowPos, int maxScale) throws IOException, Exception 
	{
		//iReportType = 1 (Simplified Report "No Comp Chart"), 2 (Standard Report)
		int r = row;
		int c = 0;
		
		
		if(iReportType == 1)
		{
			//Print heading for "N" and align 
			OO.insertString(xSpreadsheet, "N", r, c+5);
			OO.setCellAllignment(xSpreadsheet, c+5, c+5, r, r, 1, 3);
		}
		
		for(int i=0; i<Rating.length; i++) {
			r++;
			if (Rating[i] != null) {
				OO.insertString(xSpreadsheet, Rating[i], r, c+2);
				OO.mergeCells(xSpreadsheet, c+2, c+3, r, r);

				OO.insertNumeric(xSpreadsheet, Result[i], r, c+4);
				// change by Santoso 
				// use totalRater (already sorted) instead of arrN
				//OO.insertNumeric(xSpreadsheet, arrN[i], r, c+5);
				OO.insertNumeric(xSpreadsheet, totalRater[i], r, c+5);	
			}
		}
		
		r = row; //reset
		if(iReportType == 2) 
		{
			for (int idx = 0; idx < totalRater.length; idx++) {
			 	// OO.insertNumeric(xSpreadsheet, totalAll, rowTotal, 0);
				OO.insertNumeric(xSpreadsheet, totalRater[idx], rowPos[idx], 0);	
			}
			for(int i=0; i<Rating.length; i++) {
			//Added by Ha 25/06/08 to get rid of "0" line in the report			
				if (Rating[i]!=null)
				{	
					OO.insertString(xSpreadsheet2, Rating[i], r, c);
					OO.insertNumeric(xSpreadsheet2, Result[i], r, c+1);
					r++;
				}			
			}
			
			//draw chart
			XTableChart xtablechart = OO.getChart(xSpreadsheet, xSpreadsheet2, c, c+1, row-1, r-1, Integer.toString(row), 10000, 7800, row, 2);
			OO.setFontSize(8);
			xtablechart = OO.setChartTitle(xtablechart, "");
			// (22-08-06) Axes should all be 0 degree right? Otherwise, slanted score seems funny (Maruli)
			//xtablechart = OO.setAxes(xtablechart, "", "Scores (%)", maxScale, 1, 0, 4500);
			xtablechart = OO.setAxes(xtablechart, "", "Scores (%)", maxScale, 1, 0, 0);
			OO.setChartProperties(xtablechart, false, true, false, true, true);
			OO.showLegend(xtablechart, false);
		}//end iReportType
	}
	
	/**
	 * Write comments on excel.
	 */
	public void InsertComments() throws SQLException, IOException, Exception 
	{
		row++;
		
		int surveyLevel = Integer.parseInt(surveyInfo[0]);
		column = 0;
		
		if (ST.LangVer == 1)
			OO.insertString(xSpreadsheet, "Narrative Comments", row, column);
		else if (ST.LangVer == 2)
			OO.insertString(xSpreadsheet, "Komentar Naratif", row, column);
		
		OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
		
			
		row += 2;
		
		Vector vComp = CompetencyByName();
		
		int startBorder = 1;
		int endBorder = 1;
		int selfIncluded = Q.SelfCommentIncluded(surveyID);
		int column = 0;
		
		//added by Ping Yang on 11/08/08, check raters assigned
		boolean blnSupIncluded = Q.SupCommentIncluded(surveyID, targetID);
		boolean blnOthIncluded = Q.OthCommentIncluded(surveyID, targetID);
		
		if(surveyLevel == 0) {
	
			int count = 0;
			
			startBorder = row;
			for(int i=0; i<vComp.size(); i++) {
				voCompetency voComp = (voCompetency)vComp.elementAt(i);
				
				int compID 		= voComp.getCompetencyID();
				String statement = voComp.getCompetencyName();
					
				
				count++;
										
				
				OO.insertString(xSpreadsheet, Integer.toString(count) + ".", row, column);								
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(statement), row, column+1);
				OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
				OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);
									
				int KBID = 0;
				int start = 0;
				row++;
				
				if(selfIncluded == 1) {
					Vector selfComments = getComments("SELF", compID, KBID);
					
					if(selfComments != null) {
						boolean blnSelfCommentExists  = false;//Added by ping yang on 31/7/08 to get rid of extra '-'s
						for(int j=0; j<selfComments.size(); j++) {
							String [] arr = (String[])selfComments.elementAt(j);
							if(start == 0) {
								if (ST.LangVer == 1)
									OO.insertString(xSpreadsheet, "Self", row, column+1);
								else if (ST.LangVer == 2)
									OO.insertString(xSpreadsheet, "Diri Sendiri", row, column+1);
								
								OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
								OO.setFontItalic(xSpreadsheet, startColumn, endColumn, row, row);
																								
								row++;
								start++;
							}
									
							String comment = arr[1];
						
							if(!comment.trim().equals("")){//Added by ping yang on 31/7/08 to get rid of extra '-'s
								OO.insertString(xSpreadsheet, "- " + UnicodeHelper.getUnicodeStringAmp(comment), row, column+1);
								OO.mergeCells(xSpreadsheet, column+1, endColumn, row, row);
								OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(comment, 85));
								OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
								row++;
								blnSelfCommentExists = true;
							}	
						}
						if(!blnSelfCommentExists){//Added by ping yang on 31/7/08 to get rid of extra '-'s
							OO.insertString(xSpreadsheet, "-", row, column+1);	
							row++;
						}
					}else {										
						start = 0;					
						row++;
					}
					row++;
				}
				//Added by Ha 23/06/08 reset the value start to print the header of comment correctly
				start = 0;					 					
				Vector supComments = getComments("SUP%", compID, KBID);
				Vector othComments = getComments("OTH%", compID, KBID);
				if(blnSupIncluded){//added by Ping Yang on 11/08/08, check raters assigned
					boolean blnSupCommentExists  = false;//Added by ping yang on 31/7/08 to get rid of extra '-'s
					
					for(int j=0; j<supComments.size(); j++) {
						String [] arr = (String[])supComments.elementAt(j);
						if(start == 0) {
							
							OO.insertString(xSpreadsheet, "Supervisors", row, column+1);
							OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
							OO.setFontItalic(xSpreadsheet, startColumn, endColumn, row, row);
							
							row++;
							start++;
						}
								
						String comment = arr[1];
						if(!comment.trim().equals("")){//Added by ping yang on 31/7/08 to get rid of extra '-'s
							OO.insertString(xSpreadsheet, "- " + UnicodeHelper.getUnicodeStringAmp(comment), row, column+1);			
							OO.mergeCells(xSpreadsheet, column+1, endColumn, row, row);
							OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(comment, 85));
							OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
							row++;
							blnSupCommentExists = true;
						}
						
					}
					if(!blnSupCommentExists){//Added by ping yang on 31/7/08 to get rid of extra '-'s
						OO.insertString(xSpreadsheet, "-", row, column+1);	
						row++;
					}
					
					start = 0;					
					row++;
				}// end if(blnSupIncluded)
				
				if(blnOthIncluded){//added by Ping Yang on 11/08/08, check raters assigned
					boolean blnOthCommentExists  = false;//Added by ping yang on 31/7/08 to get rid of extra '-'s
					
					for(int j=0; j<othComments.size(); j++) {
						String [] arr = (String[])othComments.elementAt(j);				
						
						if(start == 0) {
							if (ST.LangVer == 1)
								OO.insertString(xSpreadsheet, "Others", row, column+1);					
							else if (ST.LangVer == 2)
								OO.insertString(xSpreadsheet, "Lainnya", row, column+1);
								
							OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
							OO.setFontItalic(xSpreadsheet, startColumn, endColumn, row, row);
								
							start++;
							row++;
						}	
												
						String comment = arr[1];
						
						if(!comment.trim().equals("")){//Added by ping yang on 31/7/08 to get rid of extra '-'s
							OO.insertString(xSpreadsheet, "- " + UnicodeHelper.getUnicodeStringAmp(comment), row, column+1);			
							OO.mergeCells(xSpreadsheet, column+1, endColumn, row, row);
								OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(comment, 85));
								OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
							row++;
							blnOthCommentExists = true;
						}
					}
					if(!blnOthCommentExists){//Added by ping yang on 31/7/08 to get rid of extra '-'s
						OO.insertString(xSpreadsheet, "-", row, column+1);	
						row++;
					}
						
					row++;
				}//end if(blnOthIncluded)
				endBorder = row;
				OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startBorder, endBorder, 
    							false, false, true, true, true, true);								
				row++;
				startBorder = row;
			}
		
		} else {						
			int count = 0;
			
			startBorder = row;
			for(int i=0; i<vComp.size(); i++) {
				voCompetency voComp = (voCompetency)vComp.elementAt(i);
				count++;
				int compID = voComp.getCompetencyID();
				String statement = voComp.getCompetencyName();							
				
				OO.insertString(xSpreadsheet, Integer.toString(count) + ".", row, column);
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(statement), row, column+1);												
				OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
				OO.setBGColor(xSpreadsheet, startColumn, endColumn, row, row, BGCOLOR);
				
				row++;
											
				Vector KBList = KBList(compID);	
				
				for(int j=0; j<KBList.size(); j++) {
					voKeyBehaviour voKB = (voKeyBehaviour)KBList.elementAt(j);
					int KBID = voKB.getKeyBehaviourID();
					String KB = voKB.getKeyBehaviour();
					
					OO.insertString(xSpreadsheet, "-", row, column);
					OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(KB), row, column+1);
					OO.mergeCells(xSpreadsheet, column+1, endColumn, row, row);
					OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(KB, 85));
					OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
					
					Vector supComments = getComments("SUP%", compID, KBID);
					Vector othComments = getComments("OTH%", compID, KBID);
									
					int start = 0;	
					row++;
					
					if(blnSupIncluded){//added by Ping Yang on 11/08/08, check raters assigned
						boolean blnSupCommentExist = false;//Added by ping yang on 31/7/08 to get rid of extra '-'s
					
						if(start == 0) {
							OO.insertString(xSpreadsheet, "Supervisors", row, column+1);	
							OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
							OO.setFontItalic(xSpreadsheet, startColumn, endColumn, row, row);
							
							row++;
							start++;
						}
									
	
							
						for(int k=0; k<supComments.size(); k++) {
							String [] arr = (String[])supComments.elementAt(k);
							
							String comment = arr[1];
							if(!comment.trim().equals("")){//Added by ping yang on 31/7/08 to get rid of extra '-'s
								OO.insertString(xSpreadsheet, "- " + UnicodeHelper.getUnicodeStringAmp(comment), row, column+1);
								OO.mergeCells(xSpreadsheet, column+1, endColumn, row, row);
								OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(comment, 85));
								OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
								row++;
								blnSupCommentExist = true;
							}
							
	
						}// end while sup comments
						
						if(!blnSupCommentExist) {//Added by ping yang on 31/7/08 to get rid of extra '-'s
							OO.insertString(xSpreadsheet, "-", row, column+1);
							row++;
						}
						
						start = 0;					
						
						row++;
					}// end if(blnSupIncluded){
					
					if(blnOthIncluded){//added by Ping Yang on 11/08/08, check raters assigned
						if(start == 0) {
							if (ST.LangVer == 1)
								OO.insertString(xSpreadsheet, "Others", row, column+1);	
							else if (ST.LangVer == 2)
								OO.insertString(xSpreadsheet, "Lainnya", row, column+1);
							
							OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
							OO.setFontItalic(xSpreadsheet, startColumn, endColumn, row, row);
							
							start++;
							row++;
						}					
						boolean blnOthCommentExist = false;	//Added by ping yang on 31/7/08 to get rid of extra '-'s
						for(int k=0; k<othComments.size(); k++) {
							String [] arr = (String[])othComments.elementAt(k);
							String comment = arr[1];
							
							if(!comment.trim().equals("")){
								OO.insertString(xSpreadsheet, "- " + UnicodeHelper.getUnicodeStringAmp(comment), row, column+1);
								OO.mergeCells(xSpreadsheet, column+1, endColumn, row, row);
								OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(comment, 85));
								OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
							
								row++;	
								blnOthCommentExist = true;
							}															
						}
						
						if(!blnOthCommentExist) {//Added by ping yang on 31/7/08 to get rid of extra '-'s
							OO.insertString(xSpreadsheet, "-", row, column+1);
							row++;
						}
						
						start = 0;
						row++;
					}//end if(blnOthIncluded)
					
					if(selfIncluded == 1) {
						Vector selfComments = getComments("SELF", compID, KBID);

						boolean blnSelfCommentExist = false;//Added by ping yang on 31/7/08 to get rid of extra '-'s
						if(start == 0) {
							if (ST.LangVer == 1)
								OO.insertString(xSpreadsheet, "Self", row, column+1);
							else if (ST.LangVer == 2)
								OO.insertString(xSpreadsheet, "Diri Sendiri", row, column+1);
							
							OO.setFontBold(xSpreadsheet, startColumn, endColumn, row, row);
							OO.setFontItalic(xSpreadsheet, startColumn, endColumn, row, row);
																							
							row++;
							start++;
						}
						for(int k=0; k<selfComments.size(); k++) {
							String [] arr = (String[])selfComments.elementAt(k);				
							String comment = arr[1];
							if(!comment.trim().equals("")){//Added by ping yang on 31/7/08 to get rid of extra '-'s
								OO.insertString(xSpreadsheet, "- " + UnicodeHelper.getUnicodeStringAmp(comment), row, column+1);
								OO.mergeCells(xSpreadsheet, column+1, endColumn, row, row);
								OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(comment, 85));
								OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
								row++;
								blnSelfCommentExist = true;//Added by ping yang on 31/7/08 to get rid of extra '-'s
							}
						}
						if(!blnSelfCommentExist){//Added by ping yang on 31/7/08 to get rid of extra '-'s
							OO.insertString(xSpreadsheet, "-", row, column+1);	
							row++;
						}
						row++;
					}
					
					
				} // kb
								
				endBorder = row;
				OO.setTableBorder(xSpreadsheet, startColumn, endColumn, startBorder, endBorder,
    							false, false, true, true, true, true);							
				row++;
				startBorder = row;
			}
			
		}
	}
	
	/*********************** GENERATES ALL REPORTS ************************************************************/
	
	public int NameSequence(int orgID) throws Exception {
		
		String query = "Select NameSequence from tblOrganization where PKOrganization = " + orgID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		int iNameSeq = 0;
		
  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(query);
  		
  	   		if(rs.next()) {
  	   			iNameSeq = rs.getInt(1);
  	   		}
			
  	   	}catch(Exception ex){
			System.out.println("IndividualReport.java - NameSequence - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return iNameSeq;
	}
	
	/**
	 * Retrieves all targets under the particular division, department or group.
	 * Group = 0: all targets all groups
	 * @param surveyID
	 * @param divID
	 * @param deptID
	 * @param groupID
	 * @param orgID
	 * @return Vector
	 * @throws Exception
	 */
	public Vector AllTargets(int surveyID, int divID, int deptID, int groupID, int orgID) throws Exception
	{
		int nameSeq = NameSequence(orgID);
		
		String query = "SELECT DISTINCT Asg.SurveyID, S.SurveyName, Asg.TargetLoginID, ";
		
		if(nameSeq == 0)
			query += "U.FamilyName + ' ' + U.GivenName AS FullName ";
		else
			query += "U.GivenName + ' ' +  U.FamilyName as FullName ";
			
		query += "FROM [User] U INNER JOIN tblAssignment Asg ON U.PKUser = Asg.TargetLoginID INNER JOIN tblSurvey S ON Asg.SurveyID = S.SurveyID ";
		query += "WHERE Asg.SurveyID = " + surveyID;
	
		if(divID > 0)
			query += " AND Asg.FKTargetDivision = " + divID;
		
		if(deptID > 0)
			query += " AND Asg.FKTargetDepartment = " + deptID;
		
		if(groupID > 0)
			query += " AND Asg.FKTargetGroup = " + groupID;
		
		query = query + " ORDER BY FullName";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector vTargets = new Vector();
		
  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(query);
		
			while(rs.next()) 
			{	
				String surveyName = rs.getString("SurveyName").trim();
				String targetID = rs.getString("TargetLoginID");
				String name = rs.getString("FullName").trim();
				
				String [] info = {surveyName, targetID, name};
				
				vTargets.add(info);
			}
		
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - AllTargets - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return vTargets;
	}
	
	/**
	 * Get the competency avg mean for CP and CPR.
	 * The table to store Competency Level and KB Level Result are not the same.
	 *
	 * For Competency Level:
	 *		ReliabilityIndex is stored in tblAvgMean
	 *		TrimmedMean is stored in tblTrimmedMean
	 *
	 * For KBLevel, both are stored in tblAvgMean
	 *
	 * This is a little bit messy, wrong design previously.
	 *
	 */
	public Vector getCompAvg(int surveyID, Vector vCompID, String rtCode) throws SQLException
	{
		int surveyLevel 		= C.LevelOfSurvey(surveyID);
		int reliabilityCheck 	= C.ReliabilityCheck(surveyID); // 0=trimmed mean
		
		String query 		= "";
		String tableName 	= "";
		String columnName 	= "";
		
		Vector vCompCP 	= new Vector();
		Vector vScore 	= new Vector();
		Vector vResult = new Vector();
		
		if(surveyLevel == 1) {	//KB Level
			
			query = "SELECT tblAvgMean.CompetencyID, ROUND(AVG(tblAvgMean.AvgMean), 2) AS AvgMean ";
			query += "FROM tblAvgMean INNER JOIN tblRatingTask ON tblAvgMean.RatingTaskID = tblRatingTask.RatingTaskID ";
			query += "WHERE tblAvgMean.SurveyID = " + surveyID;
			query += " AND (tblAvgMean.Type = 1) AND (tblRatingTask.RatingCode = '" + rtCode + "') ";
			query += " and CompetencyID IN (";
			
			for(int i=0; i<vCompID.size(); i++) {
				if(i != 0)
					query += ",";
				
				query += vCompID.elementAt(i);
			}
			
			query += ")";
			query += " GROUP BY tblAvgMean.CompetencyID";
			
		} else {	// Competency Level
			
			if(reliabilityCheck == 0) {
				tableName = "tblTrimmedMean";
				columnName = "TrimmedMean";
			}
			else {
				tableName = "tblAvgMean";
				columnName = "AvgMean";
			}
				
			query = "SELECT " + tableName + ".CompetencyID, ROUND(AVG(" + tableName + "." + columnName + "), 2) AS AvgMean ";
			query += "FROM " + tableName + " INNER JOIN tblRatingTask ON ";
			query += tableName + ".RatingTaskID = tblRatingTask.RatingTaskID WHERE ";
			query += tableName + ".Type = 1 AND " + tableName + ".SurveyID = " + surveyID;
			query += " AND tblRatingTask.RatingCode = 'CP' ";
			
			query += " and CompetencyID IN (";
			
			for(int i=0; i<vCompID.size(); i++) {
				if(i != 0)
					query += ",";
				
				query += vCompID.elementAt(i);
			}
			
			query += ") ";
			
			query += "GROUP BY " + tableName + ".CompetencyID";
		}
		
		//if(db.con == null)
			//db.openDB();
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		
  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(query);
		
	  	   	while(rs.next()) {			
				String fkComp 	= rs.getString("CompetencyID");
				String score	= rs.getString("AvgMean");
				
				vCompCP.add(fkComp);
				vScore.add(score);
			}
			
		
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getCompAvg - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		
		//copy all the score into the correct order
		for(int i=0; i<vCompID.size(); i++) 
		{	
			String score = "0";
			
			String sCompScore = (String)vCompID.elementAt(i).toString();
			int element = vCompCP.indexOf(sCompScore);
			
			if(element != -1)
				score = (String)vScore.elementAt(element);
				
			vResult.add(score);
		}
		
		return vResult;
	}
	
	/**
	 * Overall Competency Ratings (Allianz)
	 * @param iSurvey
	 * @throws IOException
	 * @throws Exception
	 */
	public void AllianzOverall() throws IOException, Exception
	{
		//System.out.println("4. Overall Report");
		int [] address = OO.findString(xSpreadsheet, "<overall>");
		
		column = address[0];
		row = address[1];
		
		OO.findAndReplace(xSpreadsheet, "<overall>", "");
		
		insertRatingScale(column, row);
		overallGraph();
	}

	public Vector getRatingScaleDescending(int RatingScaleID) {
	
		Vector v = new Vector();
		
		String query = "SELECT * FROM tblScaleValue WHERE ScaleID = " + RatingScaleID;
		query = query + " ORDER BY LowValue DESC, HighValue DESC";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            while(rs.next())
            {
            	votblScaleValue vo = new votblScaleValue();
            	vo.setHighValue(rs.getInt("HighValue"));
            	vo.setLowValue(rs.getInt("LowValue"));
            	vo.setScaleDescription(rs.getString("ScaleDescription"));
            	v.add(vo);
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("IndividualReport.java - getRatingScaleDescending - " + E);
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
	 * Insert Rating Scale (Allianz)
	 * @param iCol
	 * @param iRow
	 * @param iSurveyID
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 * @author Maruli
	 */
	public void insertRatingScale(int iCol, int iRow) throws SQLException, IOException, Exception 
	{
		//System.out.println("	- Printing Rating Scales");
		
		int count = 0;
		int maxScale = EQ.maxScale(surveyID) + 1;
		int totalCells = totalColumn / maxScale;
		int totalMerge = 0;		// total cells to be merged after rounding
		double merge = 0;		// total cells to be merged before rounding
					
		Vector RT = EQ.SurveyRating(surveyID);		
		
		for(int j=0; j<RT.size(); j++) {
			
			votblSurveyRating vo = (votblSurveyRating)RT.elementAt(j);
			count++;
			
			int scaleID = vo.getScaleID();
			int c = 1;
			int r = iRow;
			int start = 0;
			
			Vector RS = getRatingScaleDescending(scaleID);
			
			for(int i=0; i<RS.size(); i++)
			{
				votblScaleValue voScale = (votblScaleValue)RS.elementAt(i);
				int low = voScale.getLowValue();
				int high = voScale.getHighValue();
				String desc = voScale.getScaleDescription();
				
				OO.insertString(xSpreadsheet, desc, iRow, column+1);	// add in scale description
				OO.setFontBold(xSpreadsheet, column+1, column+1, iRow, iRow);
				OO.setCellAllignment(xSpreadsheet, column+1, column+1, iRow, iRow, 1, 2);
				OO.setCellAllignment(xSpreadsheet, column+1, column+1, iRow, iRow, 2, 2);
				
				r = iRow + 1;
				c = column;
				
				start = c; // start merge cell
				String temp = "";

				while(low <= high) {						
					if(low > 1)
						temp += "    ";
					temp = temp + Integer.toString(low);

					low++;						
				}
				
				OO.insertString(xSpreadsheet, temp.trim(), r, c+1);	// add in rating scale value
				OO.setCellAllignment(xSpreadsheet, c+1, c+1, r, r, 1, 2);
				
				int to = start+totalCells-1;	// merge cell for rating scale value
				OO.mergeCells(xSpreadsheet, start+1, to+1, r, r);
				OO.setTableBorder(xSpreadsheet, start+1, to+1, r, r, true, true, true, true, true, true);
				OO.setFontSize(xSpreadsheet, start+1, to+1, r, r, 10);
				
				OO.mergeCells(xSpreadsheet, start+1, to+1, iRow, iRow);	// merge cell for rating scale description
				OO.setTableBorder(xSpreadsheet, start+1, to+1, iRow, iRow, true, true, true, true, true, true);
				OO.setBGColor(xSpreadsheet, start+1, to+1, iRow, iRow, BGCOLOR);
				
				merge = (double)desc.length() / (double)(totalCells * 2);				
				
				BigDecimal BD = new BigDecimal(merge);
				BD.setScale(0, BD.ROUND_UP);
				BigInteger BI = BD.toBigInteger();
				totalMerge = BI.intValue() + 1;
				
				column = to + 1;
			}
			
			OO.setRowHeight(xSpreadsheet, iRow, start+1, (300 * totalMerge));
			
			row = r + 3;					
			column = 0;				
		}	
	}
	
	/**
	 * Calculate CP score. OTHERS are further break down into relation specific (Allianz)
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 * @author Maruli
	 */
	public void overallGraph() throws SQLException, IOException, Exception
	{
		//System.out.println("	- Working on overall graph");
		int iNoOfRT 	= 0;
		String RTCode 	= "";
			
		Vector RT 	= RatingTask();
		Vector CP 	= null;
		Vector CPR 	= null;
		int type 		= 1;
		
		for(int j=0; j<RT.size(); j++) {
			votblSurveyRating vo = (votblSurveyRating)RT.elementAt(j);
			
			RTCode 	=  vo.getRatingCode();
		
			if(RTCode.equals("CP") || RTCode.equals("CPR") || RTCode.equals("FPR")) {
				if(RTCode.equals("CP"))
					CP = CPCPR(RTCode);
				else {
					CPR = CPCPR(RTCode);
					if(RTCode.equals("CPR")) {
						type = 1;
						iNoCPR = 0;	// CPR is chosen in this survey
					} else {
						type = 2;
					}
				}
			}
			iNoOfRT++;
		}
		
		vCompID.clear();
		vCompName.clear();
		
		Vector vComp = CompetencyByName();
		
		int iLocalR = 1;
		int iLocalC = 0;
		
		double dCPSelf = 0;
		double dCPSup = 0;
		double dCPPeer = 0;
		double dCPDR = 0;
		double dCPBIP = 0;
		//double dCPR = 0;
			
		for(int i=0; i<vComp.size(); i++) {
			voCompetency voComp = (voCompetency)vComp.elementAt(i);
			
			int compID 		= voComp.getCompetencyID();
			String compName = UnicodeHelper.getUnicodeStringAmp(voComp.getCompetencyName());
							
			dCPSelf = CPCPRAllianz(1, compID, "SELF", "");
			dCPSup = CPCPRAllianz(1, compID, "SUP%", "");
			dCPPeer = CPCPRAllianz(1, compID, "OTH%", "Peer");
			dCPDR = CPCPRAllianz(1, compID, "OTH%", "Direct Report");
			dCPBIP = CPCPRAllianz(1, compID, "OTH%", "BIP Project Champion");
			//dCPR 	= 0;
			
//			if(CP != null && CP.next())
//				dCP = CP.getDouble("Result");
						
//			if(CPR != null && CPR.next())
//				dCPR = CPR.getDouble("Result");
				
			//System.out.println(dCP);
			
			vCompID.add(new Integer(compID));
			vCompName.add(new String(compName));
			
			OO.insertString(xSpreadsheet2, compName, iLocalR, iLocalC);
			OO.insertNumeric(xSpreadsheet2, dCPDR, iLocalR, iLocalC+1);
			OO.insertNumeric(xSpreadsheet2, dCPPeer, iLocalR, iLocalC+2);
			OO.insertNumeric(xSpreadsheet2, dCPBIP, iLocalR, iLocalC+3);
			OO.insertNumeric(xSpreadsheet2, dCPSup, iLocalR, iLocalC+4);
			OO.insertNumeric(xSpreadsheet2, dCPSelf, iLocalR, iLocalC+5);
			//OO.insertNumeric(xSpreadsheet2, dCPR, iLocalR, iLocalC+2);
			
			iLocalR++;
		}
		
		row = iLocalR + 2;
		OO.setSourceData(xSpreadsheet, xSpreadsheet2, 0, iLocalC, iLocalC+5, 0, iLocalR-1);
	}
	
	/**
	 * Generate competency report (Allianz)
	 * @throws IOException
	 * @throws Exception
	 */
	public void AllianzCompReport() throws IOException, Exception
	{
		//System.out.println("5. Printing Competency Report");
		
		int [] address = OO.findString(xSpreadsheet, "<competency>");
		
		column = address[0];
		row = address[1];
		
		OO.findAndReplace(xSpreadsheet, "<competency>", "");
		
		int iCompID = 0;
		String sCompName = "";
		
		for(int i=0; i<vCompID.size(); i++) 
		{
			iCompID = Integer.parseInt( ((String)vCompID.elementAt(i).toString()) );
			sCompName = (String)vCompName.elementAt(i).toString();
			
			//OO.insertRows(xSpreadsheet, 0, 1, row, row+1, 1, 1);
			
			OO.insertString(xSpreadsheet, sCompName.toUpperCase(), row, 0);
			OO.setFontBold(xSpreadsheet, column, column + 3, row, row);
			OO.setFontSize(xSpreadsheet, startColumn, endColumn, row, row, 12);
			
			row = row + 2;
			//OO.insertRows(xSpreadsheet, 0, 1, row, row+2, 2, 1);
			
			OO.setFontSize(xSpreadsheet, startColumn, endColumn, row, row, 10);
			insertRatingScale(column, row);
	
			//OO.insertRows(xSpreadsheet, 0, 1, row, row+19, 17, 1);
			drawCompetencyChartAllianz(iCompID, sCompName);
			printKBAllianz(iCompID);
		}
	}
	
	/**
	 * Draw competency charts (Allianz)
	 * @param iCompID
	 * @param sCompName
	 * @throws IOException
	 * @throws Exception
	 * @author Maruli
	 */
	public void drawCompetencyChartAllianz(int iCompID, String sCompName) throws IOException, Exception
	{
		int iKBID = 0;
		int iLocalC = 0;
		int startRow = row;
		int iLocalR = row;
		int iLocCounter = 1;
		int iMaxScale = MaxScale();
		
		double dCPSelf = 0;
		double dCPSup = 0;
		double dCPPeer = 0;
		double dCPDR = 0;
		double dCPBIP = 0;
		
		String sKB = "";
		
		Vector KBList = KBList(iCompID);
		
		OO.insertString(xSpreadsheet2, "Direct Report", iLocalR, iLocalC+1);
		OO.insertString(xSpreadsheet2, "Peer", iLocalR, iLocalC+2);
		OO.insertString(xSpreadsheet2, "BIP Champion", iLocalR, iLocalC+3);
		OO.insertString(xSpreadsheet2, "Supervisors", iLocalR, iLocalC+4);
		OO.insertString(xSpreadsheet2, "Self", iLocalR, iLocalC+5);
		
		iLocalR++;
		
		for(int j=0; j<KBList.size(); j++) {
			voKeyBehaviour voKB = (voKeyBehaviour)KBList.elementAt(j);
			iKBID = voKB.getKeyBehaviourID();
			sKB = voKB.getKeyBehaviour();
			
			dCPSelf = getAvgBehvScoreAllianz(iKBID, "SELF", "");
			dCPSup = getAvgBehvScoreAllianz(iKBID, "SUP%", "");
			dCPPeer = getAvgBehvScoreAllianz(iKBID, "OTH%", "Peer");
			dCPDR = getAvgBehvScoreAllianz(iKBID, "OTH%", "Direct Report");
			dCPBIP = getAvgBehvScoreAllianz(iKBID, "OTH%", "BIP Project Champion");
			
			OO.insertString(xSpreadsheet2, "Q"+iLocCounter, iLocalR, iLocalC);
			OO.insertNumeric(xSpreadsheet2, dCPDR, iLocalR, iLocalC+1);
			OO.insertNumeric(xSpreadsheet2, dCPPeer, iLocalR, iLocalC+2);
			OO.insertNumeric(xSpreadsheet2, dCPBIP, iLocalR, iLocalC+3);
			OO.insertNumeric(xSpreadsheet2, dCPSup, iLocalR, iLocalC+4);
			OO.insertNumeric(xSpreadsheet2, dCPSelf, iLocalR, iLocalC+5);
			
			iLocalR++;
			iLocCounter++;
		}
		
		// Draw chart
		XTableChart xtablechart = OO.getChart(xSpreadsheet, xSpreadsheet2, iLocalC, iLocalC+5, startRow, iLocalR-1, 
												Integer.toString(row), 15050, 9000, startRow, 1);
		xtablechart = OO.setChartTitle(xtablechart, "");
		xtablechart = OO.setAxes(xtablechart, "Competency", "", iMaxScale, 1, 0, 0);
		OO.setChartProperties(xtablechart, false, false, true, false, true);
		OO.setLegendPosition(xtablechart, 4);
		OO.changeChartType("com.sun.star.chart.LineDiagram", xtablechart);
		//OO.changeChartLineColours(1);
	}
	
	/**
	 * Print out KB under each competency graph (Allianz)
	 * @param iCompID
	 * @throws IOException
	 * @throws Exception
	 */
	public void printKBAllianz(int iCompID) throws IOException, Exception
	{
		int iLocalC = 1;
		row = row + 18;
		int iStartRow = row;
		int iLocCounter = 1;
		
		int iStartCol = 1;
		int iEndCol = 10;
		String sKB = "";
		
		Vector KBList = KBList(iCompID);
				
		for(int j=0; j<KBList.size(); j++) {
			voKeyBehaviour voKB = (voKeyBehaviour)KBList.elementAt(j);
			sKB = voKB.getKeyBehaviour();
			
			OO.mergeCells(xSpreadsheet, 2, iEndCol, row, row);
			OO.insertString(xSpreadsheet, "Q"+iLocCounter, row, iLocalC);
			OO.insertString(xSpreadsheet, sKB, row, 2);
			
			OO.setRowHeight(xSpreadsheet, row, 1, 510*OO.countTotalRow(sKB, 92));
			OO.setTableBorder(xSpreadsheet, iStartCol, iEndCol, row, row, true, true, true, true, true, true);

			row++;
			iLocCounter++;
		}
		
		OO.setFontSize(xSpreadsheet, startColumn, endColumn, iStartRow, row, 10);
		OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
	}
	
	
	/**
	 * Get CP for competency graphs (Allianz)
	 * @param iKBID
	 * @param sRelHigh
	 * @param sRelSpec
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public double getAvgBehvScoreAllianz(int iKBID, String sRelHigh, String sRelSpec) throws IOException, Exception
	{
		double dAvgCP = 0; 

		String query = "SELECT RB.RatingTaskID, KB.FKCompetency, RB.KeyBehaviourID, CAST(CAST(SUM(RB.Result) AS float) / COUNT(RB.Result) AS numeric(38, 2)) ";
		query = query + "AS Result, RS.RelationSpecific ";
		query = query + "FROM tblAssignment ASG INNER JOIN ";
		query = query + "tblResultBehaviour RB ON ASG.AssignmentID = RB.AssignmentID INNER JOIN ";
		query = query + "KeyBehaviour KB ON RB.KeyBehaviourID = KB.PKKeyBehaviour LEFT OUTER JOIN ";
		query = query + "tblRelationSpecific RS ON ASG.RTSpecific = RS.SpecificID ";
		query = query + "WHERE (ASG.SurveyID = "+surveyID+") AND (ASG.TargetLoginID = "+targetID+") AND ";
		query = query + "(ASG.RaterStatus IN (1, 2, 4)) AND (ASG.RaterCode LIKE '"+sRelHigh+"') AND (RB.Result <> 0) ";
		query = query + "GROUP BY RB.RatingTaskID, KB.FKCompetency, RB.KeyBehaviourID, RS.RelationSpecific ";
		query = query + "HAVING (RB.RatingTaskID = 1) AND (RB.KeyBehaviourID = "+iKBID+") ";
		
		if(sRelSpec != "")
			query = query + "AND (RS.RelationSpecific = '"+sRelSpec+"') ";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
		
  			if(rs.next())
  				dAvgCP = rs.getDouble("Result");
  			
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getAvgBehvScoreAllianz - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		
		return dAvgCP;
	}
	
	/**
	 * Print out comments (Allianz)
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 */
	public void InsertCommentsAllianz() throws SQLException, IOException, Exception
	{
		//System.out.println("6. Printing Comments");
		
		int startBorder = 1;
		int endBorder = 1;
		int selfIncluded = Q.SelfCommentIncluded(surveyID);
		int column = 0;
		int iCompID = 0;
		int count = 0;
		int iLocEndCol = 10;
		String sCompName = "";
		
		row++;
		
		OO.insertString(xSpreadsheet, "FEEDBACK COMMENTS", row, column);								
		OO.setFontBold(xSpreadsheet, column, column+1, row, row);
		OO.setBGColor(xSpreadsheet, column, column+10, row, row, BGCOLOR);
		OO.setFontSize(xSpreadsheet, column, column+10, row, row, 14);
		
		row = row + 2;
		startBorder = row;
		OO.setFontSize(xSpreadsheet, column, iLocEndCol, row, row, 12);
		
		for(int i=0; i<vCompID.size(); i++)
		{	
			count++;
			iCompID = Integer.parseInt( ((String)vCompID.elementAt(i).toString()) );
			sCompName = (String)vCompName.elementAt(i).toString();
			
			OO.insertString(xSpreadsheet, Integer.toString(count) + ".", row, column);								
			OO.insertString(xSpreadsheet, sCompName, row, column+1);
			OO.setFontBold(xSpreadsheet, startColumn, iLocEndCol, row, row);
			OO.setBGColor(xSpreadsheet, startColumn, iLocEndCol, row, row, BGCOLOR);
								
			int start = 0;
			row++;
			
			if(selfIncluded == 1) 
			{
				Vector selfComments = getCommentsAllianz("SELF", iCompID);
				
				for(int j=0; j<selfComments.size(); j++) {
					String [] arr = (String[])selfComments.elementAt(j);
					if(start == 0) 
					{
						row++;
						OO.insertString(xSpreadsheet, "Self", row, column+1);
						
						OO.setFontBold(xSpreadsheet, startColumn, iLocEndCol, row, row);
						OO.setFontItalic(xSpreadsheet, startColumn, iLocEndCol, row, row);
																						
						row++;
						start++;
					}
					
					String comment = (arr[1]).trim();
				
					OO.insertString(xSpreadsheet, "-", row, column);
					OO.setCellAllignment(xSpreadsheet, column, column, row, row, 1, 3);
					
					OO.mergeCells(xSpreadsheet, column+1, iLocEndCol, row, row);
					OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(comment), row, column+1);
					OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(comment, 85));
					OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
					row++;		
				}
			}
			
			Vector supComments = getCommentsAllianz("SUP%", iCompID);
			Vector othComments = getCommentsAllianz("OTH%", iCompID);
			
			start = 0;
			
			for(int j=0; j<supComments.size(); j++) {
				String [] arr = (String[])supComments.elementAt(j);
			
				if(start == 0) 
				{
					row++;
					OO.insertString(xSpreadsheet, "Supervisors", row, column+1);
					OO.setFontBold(xSpreadsheet, startColumn, iLocEndCol, row, row);
					OO.setFontItalic(xSpreadsheet, startColumn, iLocEndCol, row, row);
					
					row++;
					start++;
				}
				
				String comment = (arr[1]).trim();
				OO.insertString(xSpreadsheet, "-", row, column);
				OO.setCellAllignment(xSpreadsheet, column, column, row, row, 1, 3);
				
				OO.mergeCells(xSpreadsheet, column+1, iLocEndCol, row, row);
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(comment), row, column+1);
				OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(comment, 85));
				OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
				row++;
			}
			
			start = 0;
			
			for(int j=0; j<othComments.size(); j++) {
				String [] arr = (String[])othComments.elementAt(j);					
				if(start == 0) 
				{
					row++;
					OO.insertString(xSpreadsheet, "Others (Peers, Direct Reports)", row, column+1);
					OO.setFontBold(xSpreadsheet, startColumn, iLocEndCol, row, row);
					OO.setFontItalic(xSpreadsheet, startColumn, iLocEndCol, row, row);
						
					start++;
					row++;
				}	
										
				String comment = (arr[1]).trim();
				OO.insertString(xSpreadsheet, "-", row, column);
				OO.setCellAllignment(xSpreadsheet, column, column, row, row, 1, 3);
				
				OO.mergeCells(xSpreadsheet, column+1, iLocEndCol, row, row);
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(comment), row, column+1);
				OO.setRowHeight(xSpreadsheet, row, column+1, ROWHEIGHT*OO.countTotalRow(comment, 85));
				OO.setCellAllignment(xSpreadsheet, startColumn, startColumn, row, row, 2, 1);
				row++;
			}
				
			row++;

			endBorder = row;
			OO.setTableBorder(xSpreadsheet, startColumn, iLocEndCol, startBorder, endBorder, 
							false, false, true, true, true, true);								
			row++;
			
			//Insert page break for each competency comments
			OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
			row++;
			startBorder = row;
		}
	}
	
	/**
	 * Draw the development map
	 * @param iSurvey
	 * @param iTarget
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 * @see IndividualReport1#reportDevelopmentMap(int, int, String)
	 * @author Maruli
	 */	
	public void drawDevelopmentMap() throws SQLException, IOException, Exception
	{
		/*	  	   	   	   +ve
		 *     	       Q3 	|   Q4
		 *    (-ve) --------|-------- (+ve)
		 *             Q1 	|   Q2
		 *				   -ve
		 *    
		 *  Quadrant classification
		 *  Q1.	Competency gap above 0 with all KB gap above 0
		 *	Q2.	Competency gap above 0 with one or more KB gap below 0
		 *	Q3.	Competency gap below 0 with one or more KB gap above 0
		 *	Q4.	Competency gap below 0 with all KB gap below 0
		 */
		//System.out.println("4. Drawing Development Map grid");
		
		double dCompGap = 0;
		double dBehvGap = 0;
		
		int iQ1 = 0;
		int iQ2 = 0;
		int iQ3 = 0;
		int iQ4 = 0;
		
		Q1.removeAllElements();
		Q2.removeAllElements();
		Q3.removeAllElements();
		Q4.removeAllElements();
		
		//double dMinGap = 0;
		//double [] arrGap = getMinMaxGap();		
		//dMinGap = arrGap[0];
		
		// PROCESSING SECTION
		int iCompID = 0;
		int iCompRank = 0;
		String sComp = "";
		
		Vector rsComp = SR.getCompList(this.surveyID);
		Vector rsBehv = null;
		
		for(int i=0; i<rsComp.size(); i++) 
		{
			voCompetency vo = (voCompetency)rsComp.elementAt(i);
			iQ1 = 0;
			iQ2 = 0;
			iQ3 = 0;
			iQ4 = 0;
			
			iCompID = vo.getCompetencyID();
			sComp = vo.getCompetencyName();
			iCompRank = vo.getRank();
			
			// Get Competency's Gap
			dCompGap = SR.getCompAvgGapDevMap(this.surveyID, this.targetID, iCompID);
			
			if(dCompGap < 0)	// Negative competency
			{
				// Q1 or Q2
				rsBehv = SR.getBehaviourGapDevMap(this.surveyID, this.targetID, iCompID);
				
				for(int j=0; j<rsBehv.size(); j++)
				{
					
					String [] arr = (String []) rsBehv.elementAt(j);
					dBehvGap = Double.parseDouble(arr[0]);
					
					if(dBehvGap < 0)					
						iQ1++;
					else
					{
						// Since 1 KB is already +ve (Q2), break the loop
						iQ2++;
						break;
					}
				}
				
				if(iQ2 > 0)
					Q2.add(new String [] { Integer.toString(iCompID), sComp, Double.toString(dCompGap), 
											Integer.toString(iCompRank) });
				else
					Q1.add(new String [] { Integer.toString(iCompID), sComp, Double.toString(dCompGap), 
											Integer.toString(iCompRank) });
			}			
			else	// Positive competency
			{
				// Q3 or Q4
				rsBehv = SR.getBehaviourGapDevMap(this.surveyID, this.targetID, iCompID);
				
				for(int j=0; j<rsBehv.size(); j++)
				{
					
					String [] arr = (String []) rsBehv.elementAt(j);
					
					dBehvGap = Double.parseDouble(arr[0]);
					
					if(dBehvGap < 0)
					{
						// Since 1 KB is already -ve (Q3), break the loop
						iQ3++;
						break;
					}
					else
						iQ4++;
				}
				
				if(iQ3 > 0)
					Q3.add(new String [] { Integer.toString(iCompID), sComp, Double.toString(dCompGap),
											Integer.toString(iCompRank) });
				else
					Q4.add(new String [] { Integer.toString(iCompID), sComp, Double.toString(dCompGap),
											Integer.toString(iCompRank) });
			}
		}
		
		// END OF PROCESSING SECTION
		
		// DRAWING SECTION		
		int i = 0;	// For loop
		int iBigTotRow = 0;
		int iSmallTotRow = 0;
		String sBiggerQuad = "";
		String sSmallerQuad = "";
		String sBigQuadReplacement = "";
		String sSmallQuadReplacement = "";
		
		// Draw the upper part of the map (Q3 & Q4)
		// We need to know how many rows to insert.
		// Find out which one has more elements in it (Q3 or Q4). Store in iTotRow		
		if(Q3.size() > Q4.size())
		{
			iBigTotRow = Q3.size();	// Q3 is bigger
			iSmallTotRow = Q4.size();
			sBiggerQuad = "<q3>";
			sSmallerQuad = "<q4>";
			sBigQuadReplacement = "Q3 - STRENGTHEN";
			sSmallQuadReplacement = "Q4 - SUSTAIN";
		}
		else if(Q4.size() > Q3.size())
		{
			iBigTotRow = Q4.size();	// Q4 is bigger
			iSmallTotRow = Q3.size();
			sBiggerQuad = "<q4>";
			sSmallerQuad = "<q3>";
			sBigQuadReplacement = "Q4 - SUSTAIN";
			sSmallQuadReplacement = "Q3 - STRENGTHEN";
		}
		else	// Same size
		{
			iBigTotRow = Q4.size();	// Equal size, whichever can be used
			iSmallTotRow = Q3.size();
			sBiggerQuad = "<q4>";
			sSmallerQuad = "<q3>";
			sBigQuadReplacement = "Q4 - SUSTAIN";
			sSmallQuadReplacement = "Q3 - STRENGTHEN";
		}
		
		int [] address = OO.findString(xSpreadsheet, sBiggerQuad);
		
		column = address[0];
		row = address[1];
		
		OO.findAndReplace(xSpreadsheet, sBiggerQuad, sBigQuadReplacement);
		row = row + 2;
		int iInitRow = row;
		
		for(i=0; i<iBigTotRow; i++)
		{
			OO.insertRows(xSpreadsheet, startColumn, endColumn, row-1, row, 1, 1);
			
			if(sBiggerQuad.equals("<q4>"))
				OO.insertString(xSpreadsheet, ((String [])Q4.elementAt(i))[1], row, column);
			else
				OO.insertString(xSpreadsheet, ((String [])Q3.elementAt(i))[1], row, column);
			
			row++;
		}
		
		address = OO.findString(xSpreadsheet, sSmallerQuad);
		
		column = address[0];
		row = address[1];
		
		OO.findAndReplace(xSpreadsheet, sSmallerQuad, sSmallQuadReplacement);
		
		for(i=0; i<iSmallTotRow; i++)
		{
			if(sSmallerQuad.equals("<q4>"))
				OO.insertString(xSpreadsheet, ((String [])Q4.elementAt(i))[1], iInitRow, column);
			else
				OO.insertString(xSpreadsheet, ((String [])Q3.elementAt(i))[1], iInitRow, column);
			
			iInitRow++;
		}
		
		// Draw the bottom part of the map (Q1 & Q2)
		if(Q1.size() > Q2.size())
		{
			iBigTotRow = Q1.size();	// Q1 is bigger
			iSmallTotRow = Q2.size();
			sBiggerQuad = "<q1>";
			sSmallerQuad = "<q2>";
			sBigQuadReplacement = "Q1 - ACQUIRE";
			sSmallQuadReplacement = "Q2 - INVEST";
		}
		else if(Q2.size() > Q1.size())
		{
			iBigTotRow = Q2.size();	// Q2 is bigger
			iSmallTotRow = Q1.size();
			sBiggerQuad = "<q2>";
			sSmallerQuad = "<q1>";
			sBigQuadReplacement = "Q2 - INVEST";
			sSmallQuadReplacement = "Q1 - ACQUIRE";
		}
		else	// Same size
		{
			iBigTotRow = Q2.size();	// Equal size, whichever can be used
			iSmallTotRow = Q1.size();
			sBiggerQuad = "<q2>";
			sSmallerQuad = "<q1>";
			sBigQuadReplacement = "Q2 - INVEST";
			sSmallQuadReplacement = "Q1 - ACQUIRE";
		}
		
		address = OO.findString(xSpreadsheet, sBiggerQuad);
		
		column = address[0];
		row = address[1];
		
		OO.findAndReplace(xSpreadsheet, sBiggerQuad, sBigQuadReplacement);
		row = row - 1;
		iInitRow = row;
		
		for(i=0; i<iBigTotRow; i++)
		{
			OO.insertRows(xSpreadsheet, column, column, row, row+1, 1, 1);
			
			if(sBiggerQuad.equals("<q2>"))
				OO.insertString(xSpreadsheet, ((String [])Q2.elementAt(i))[1], row, column);
			else
				OO.insertString(xSpreadsheet, ((String [])Q1.elementAt(i))[1], row, column);
			
			row++;
		}
		
		address = OO.findString(xSpreadsheet, sSmallerQuad);
		
		column = address[0];
		row = address[1];
		
		OO.findAndReplace(xSpreadsheet, sSmallerQuad, sSmallQuadReplacement);
		//row = row - 2;	// Go up 2 spaces
		
		for(i=0; i<iSmallTotRow; i++)
		{
			if(sSmallerQuad.equals("<q2>"))
				OO.insertString(xSpreadsheet, ((String [])Q2.elementAt(i))[1], iInitRow, column);
			else
				OO.insertString(xSpreadsheet, ((String [])Q1.elementAt(i))[1], iInitRow, column);
			
			iInitRow++;
		}
	}
	
	/**
	 * Write the Quadrant description and table heading
	 * @param iQuadrant
	 * @param bBreakDown
	 * @throws Exception
	 * @see IndividualReport1#populateQuadrantDetail(boolean)
	 */
	public void writeQuadrantData(int iQuadrant, boolean bBreakDown) throws Exception
	{
		column = 0;
		startColumn = 0;
		endColumn = 0;
		
		String sTitle = "";
		String sDesc = "";
		
		switch(iQuadrant)
		{
			case 1 :
				sTitle = "QUADRANT 1 - ACQUIRE";
				sDesc = "This quadrant is typified by competencies that have negative gaps and with all key behaviours rated with negative gaps."
					+ " " + "Raters generally think that the target does not meet expectations in these competencies."
					+ " " + "The target would need to acquire these competencies through substantive development efforts.";
				break;
				
			case 2 :
				sTitle = "QUADRANT 2 - INVEST";
				sDesc = "This quadrant is typified by competencies that have negative gaps but with some key behaviours rated with positive gaps."
					+ " " + "Although the target is rated as not meeting the required expectations for these competencies, raters do think that there are certain behaviours that are considered as meeting or exceeding expectations."
					+ " " + "As such, the target only need to invest developmental efforts in those behaviours that are rated with negative gaps to improve the overall proficiency of the competencies.";
				break;
			
			case 3 :
				sTitle = "QUADRANT 3 - STRENGTHEN";
				sDesc = "This quadrant is typified by competencies that have positive gaps but with some key behaviours rated with negative gaps."
					+ " " + "As such, raters generally think that the target has met expectations in these competencies but there are behaviours that are considered as below requirements."
					+ " " +	"The target could strengthen these competencies by working on those behaviours that are rated with negative gaps.";
				break;
				
			case 4 :
				sTitle = "QUADRANT 4 - SUSTAIN";
				sDesc = "This quadrant is typified by competencies that have positive gaps and with all key behaviours rated with positive gaps."
					+ " " + "The target is seen to meet or exceed expectations for all these competencies."
					+ " " + "The target only need to sustain proficiency in these competencies."
					+ " " + "No developmental intervention is necessary in this quadrant.";
				break;
		}
		
		OO.insertString(xSpreadsheet2, sTitle, row, column);
		OO.mergeCells(xSpreadsheet2, startColumn, startColumn+1, row, row);
		OO.setFontBold(xSpreadsheet2, startColumn, startColumn+1, row, row);
		row++;
		
		OO.mergeCells(xSpreadsheet2, startColumn, startColumn+6, row, row);
		OO.insertString(xSpreadsheet2, sDesc, row, column);
		OO.setRowHeight(xSpreadsheet2, row, 1, ROWHEIGHT*OO.countTotalRow(sDesc, 110));
		
		row += 2;
		OO.insertString(xSpreadsheet2, "Ranked Order", row, column);
		OO.mergeCells(xSpreadsheet2, column, column, row, row+1);
		OO.setFontBold(xSpreadsheet2, column, column, row, row+1);
		OO.setCellAllignment(xSpreadsheet2, column, column, row, row+1, 1, 2);
		OO.setCellAllignment(xSpreadsheet2, column, column, row, row+1, 2, 2);
		column++;
		
		OO.insertString(xSpreadsheet2, "Competency", row, column);
		OO.mergeCells(xSpreadsheet2, column, column, row, row+1);
		OO.setFontBold(xSpreadsheet2, column, column, row, row+1);
		OO.setCellAllignment(xSpreadsheet2, column, column, row, row+1, 1, 2);
		OO.setCellAllignment(xSpreadsheet2, column, column, row, row+1, 2, 2);
		column++;
		
		int iCategory = RR.getTotalRelation(this.surveyID, bBreakDown);

		OO.insertString(xSpreadsheet2, "Current Proficiency", row, column);
		// Calculate total no of categories and merge the cell
		OO.mergeCells(xSpreadsheet2, column, column + (iCategory-1), row, row);
		OO.setFontBold(xSpreadsheet2, column, column + (iCategory-1), row, row+1);
		OO.setCellAllignment(xSpreadsheet2, column, column, row, row+1, 1, 2);
		
		int iGapCol = column + iCategory;
		OO.insertString(xSpreadsheet2, "Gap", row, iGapCol);
		OO.mergeCells(xSpreadsheet2, iGapCol, iGapCol, row, row+1);
		OO.setFontBold(xSpreadsheet2, iGapCol, iGapCol, row, row+1);
		OO.setCellAllignment(xSpreadsheet2, iGapCol, iGapCol, row, row+1, 1, 2);
		OO.setCellAllignment(xSpreadsheet2, iGapCol, iGapCol, row, row+1, 2, 2);
		
		endColumn = iGapCol;
		OO.setTableBorder(xSpreadsheet2, startColumn, endColumn, row, row+1, true, true, true, true, true, true);
		
		row++;
		
		if(bBreakDown)
			writeBreakDownQuadrantScore(iQuadrant);
		else
			writeQuadrantScore(iQuadrant);
		
		OO.insertPageBreak(xSpreadsheet2, 0, 1, row);
	}
	
	/**
	 * Main function to write the Quadrant Details (Sheet 2)
	 * @param bBreakDown
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 * @author Maruli
	 * @see IndividualReport1#reportDevelopmentMap(int, int, String, boolean)
	 */
	public void populateQuadrantDetail(boolean bBreakDown) throws Exception
	{
	//	System.out.println("5. Populating Quadrant details");
		
		row = 0;
		
		writeQuadrantData(1, bBreakDown);
		writeQuadrantData(2, bBreakDown);
		writeQuadrantData(3, bBreakDown);
		writeQuadrantData(4, bBreakDown);
	}
	
	/**
	 * Write the Competency, KB, CP and Gap score (Category Relation High)
	 * @param iQuadrant
	 * @throws Exception
	 * @author Maruli
	 * @see IndividualReport1#writeQuadrantData(int, boolean)
	 */
	public void writeQuadrantScore(int iQuadrant) throws Exception
	{
		int iCol = 2;		// To iterate through each group of CP
		
		Vector vLocal = null;
		Vector rsBehv = null;
		Vector rsRelation = null;
		
		double dCompCP = 0;
		double dBehvCP = 0;
		double dCompGap = 0;
		double dBehvGap = 0;
		int iCompID = 0;
		int iBehvID = 0;
		int iCompRank = 0;
		String sComp = "";
		String sBehv = "";
		
		switch (iQuadrant)
		{
			case 1 : vLocal = (Vector) Q1.clone();
					 break;
			case 2 : vLocal = (Vector) Q2.clone();
					 break;
			case 3 : vLocal = (Vector) Q3.clone();
					 break;
			case 4 : vLocal = (Vector) Q4.clone();
					 break;
		}
		
		//vLocal = G.sortVector(vLocal, 1);
		
		for(int a=0; a<vLocal.size(); a++)
		{
			if(a == 0)
			{
				String sRelation = "";
				
				OO.insertString(xSpreadsheet2, "Self", row, iCol);
				OO.setFontBold(xSpreadsheet2, iCol, iCol, row, row);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 2, 2);
				iCol++;
				
				rsRelation = RR.getRelationHigh(this.surveyID);
				
				for(int i=0; i<rsRelation.size(); i++)
				{
					votblRelationHigh vo = (votblRelationHigh)rsRelation.elementAt(i);
					sRelation = vo.getRelationHigh();
					OO.insertString(xSpreadsheet2, sRelation, row, iCol);
					OO.setFontBold(xSpreadsheet2, iCol, iCol, row, row);
					OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
					OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 2, 2);
					iCol++;
				}
				
				row++;
			}
			
			iCol = 0;
			
			iCompID = Integer.parseInt( ((String [])vLocal.elementAt(a))[0] );
			sComp = ((String [])vLocal.elementAt(a))[1];
			dCompGap = Double.parseDouble( ((String [])vLocal.elementAt(a))[2] );
			iCompRank = Integer.parseInt( ((String [])vLocal.elementAt(a))[3] ); 
			
			OO.insertNumeric(xSpreadsheet2, iCompRank, row, iCol);
			OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
			OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
			iCol++;
			
			OO.insertString(xSpreadsheet2, sComp, row, iCol);
			OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
			iCol++;
			
			// SELF CP goes first
			dCompCP = SR.getAvgCPComp(this.surveyID, this.targetID, 4, iCompID);
			OO.insertNumeric(xSpreadsheet2, dCompCP, row, iCol);
			OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
			OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
			iCol++;
			
			int iRelID = 0;
			
			rsRelation = RR.getRelationHigh(this.surveyID);
			for(int i=0; i<rsRelation.size(); i++)
			{
				votblRelationHigh vo = (votblRelationHigh)rsRelation.elementAt(i);
				
				iRelID = vo.getRTRelation();
				
				// In tblAvgMean SUP = 2 but in tblRelation SUP = 1. OTH is not affected (Both 3)
				if(iRelID == 1)
					iRelID = 2;
				
				dCompCP = SR.getAvgCPComp(this.surveyID, this.targetID, iRelID, iCompID);
				OO.insertNumeric(xSpreadsheet2, dCompCP, row, iCol);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
				OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
				iCol++;
			}
			
			OO.insertNumeric(xSpreadsheet2, dCompGap, row, iCol);
			OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
			OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
			
			OO.setTableBorder(xSpreadsheet2, startColumn, endColumn, row, row, true, true, true, true, true, true);
			
			row++;
			iCol = 1;
			
			// Get Behaviour's gap
			rsBehv = SR.getBehaviourGapDevMap(this.surveyID, this.targetID, iCompID);
			
			for(int j=0; j<rsBehv.size(); j++)
			{
				
				String [] arr = (String []) rsBehv.elementAt(j);
				
				
				iCol = 1;
				iBehvID = Integer.parseInt(arr[2]);
				sBehv = arr[1];
				dBehvGap = Double.parseDouble(arr[0]);
				
				OO.insertString(xSpreadsheet2, sBehv, row, iCol);
				iCol++;
				// SELF CP goes first
				dBehvCP = SR.getAvgCPKB(this.surveyID, this.targetID, 4, iBehvID);
				OO.insertNumeric(xSpreadsheet2, dBehvCP, row, iCol);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
				iCol++;
				
				rsRelation = RR.getRelationHigh(this.surveyID);
				
				for(int i=0; i<rsRelation.size(); i++)
				{
					votblRelationHigh vo = (votblRelationHigh)rsRelation.elementAt(i);
					iRelID = vo.getRTRelation();
					
					// In tblAvgMean SUP = 2 but in tblRelation SUP = 1. OTH is not affected (Both 3)
					if(iRelID == 1)
						iRelID = 2;
					
					dBehvCP = SR.getAvgCPKB(this.surveyID, this.targetID, iRelID, iBehvID);
					OO.insertNumeric(xSpreadsheet2, dBehvCP, row, iCol);
					OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
					iCol++;
				}
				
				OO.insertNumeric(xSpreadsheet2, dBehvGap, row, iCol);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
				
				OO.setTableBorder(xSpreadsheet2, startColumn, endColumn, row, row, true, true, true, true, true, true);
				row++;
			}
		}
		
		// If there is no competency in that particular Quadrant, insert relation and leave 2 blank spaces
		if(vLocal.size() == 0)
		{
			String sRelation = "";
			
			OO.insertString(xSpreadsheet2, "Self", row, iCol);
			OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
			iCol++;
			
			rsRelation = RR.getRelationHigh(this.surveyID);
			
			for(int i=0; i<rsRelation.size(); i++)
			{
				votblRelationHigh vo = (votblRelationHigh)rsRelation.elementAt(i);
				
				sRelation = vo.getRelationHigh();
				OO.insertString(xSpreadsheet2, sRelation, row, iCol);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
				iCol++;
			}
			
			row++;
			OO.insertString(xSpreadsheet2, "There are no competencies under this Quadrant", row, 0);
			OO.mergeCells(xSpreadsheet2, 0, endColumn, row, row);
			row += 2;
		}
	}
	
	/**
	 * Write the Competency, KB, CP and Gap score (Category Relation High & Specific)
	 * @param iQuadrant
	 * @throws Exception
	 * @author Maruli
	 * @see IndividualReport1#writeQuadrantData(int, boolean)
	 */
	public void writeBreakDownQuadrantScore(int iQuadrant) throws Exception
	{
		int iCol = 2;		// To iterate through each group of CP
		
		Vector vLocal = null;
		Vector rsBehv = null;
		Vector rsRelationHigh = null;
		Vector rsRelationSpec = null;
		
		double dCompCP = 0;
		double dBehvCP = 0;
		double dCompGap = 0;
		double dBehvGap = 0;
		int iCompID = 0;
		int iBehvID = 0;
		int iCompRank = 0;
		String sComp = "";
		String sBehv = "";
		String sRelation = "";
		int iRelHigh = 0;
		
		switch (iQuadrant)
		{
			case 1 : vLocal = (Vector) Q1.clone();
					 break;
			case 2 : vLocal = (Vector) Q2.clone();
					 break;
			case 3 : vLocal = (Vector) Q3.clone();
					 break;
			case 4 : vLocal = (Vector) Q4.clone();
					 break;
		}
		
		//vLocal = G.sortVector(vLocal, 1);
		
		for(int a=0; a<vLocal.size(); a++)
		{
			if(a == 0)
			{
				OO.insertString(xSpreadsheet2, "Self", row, iCol);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 2, 2);
				iCol++;
				
				rsRelationHigh = RR.getRelationHigh(this.surveyID);
				
				for(int i=0; i<rsRelationHigh.size(); i++)
				{
					votblRelationHigh vo = (votblRelationHigh)rsRelationHigh.elementAt(i);
					
					iRelHigh = vo.getRTRelation();
					
					rsRelationSpec = RR.getRelationSpecific(this.surveyID, iRelHigh);
					
					for(int j=0; j<rsRelationSpec.size(); j++)
					{
						votblAssignment voTbl = (votblAssignment)rsRelationSpec.elementAt(j);
						sRelation = voTbl.getRelationSpecific();
						OO.insertString(xSpreadsheet2, sRelation, row, iCol);
						OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
						OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 2, 2);
						iCol++;
					}
				}
				
				row++;
			}
			
			iCol = 0;
			
			iCompID = Integer.parseInt( ((String [])vLocal.elementAt(a))[0] );
			sComp = ((String [])vLocal.elementAt(a))[1];
			dCompGap = Double.parseDouble( ((String [])vLocal.elementAt(a))[2] );
			iCompRank = Integer.parseInt( ((String [])vLocal.elementAt(a))[3] );
				
			OO.insertNumeric(xSpreadsheet2, iCompRank, row, iCol);
			OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
			OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
			iCol++;
			
			OO.insertString(xSpreadsheet2, sComp, row, iCol);
			OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
			iCol++;
			
			// SELF CP goes first
			dCompCP = SR.getAvgCPCompBreakDown(this.surveyID, this.targetID, 2, 0, iCompID);
			OO.insertNumeric(xSpreadsheet2, dCompCP, row, iCol);
			OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
			OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
			iCol++;
			
			rsRelationHigh = RR.getRelationHigh(this.surveyID);
			
			for(int i=0; i<rsRelationHigh.size(); i++)
			{
				votblRelationHigh vo = (votblRelationHigh)rsRelationHigh.elementAt(i);
				
				iRelHigh = vo.getRTRelation();
				
				rsRelationSpec = RR.getRelationSpecific(this.surveyID, iRelHigh);
				
				for(int j=0; j<rsRelationSpec.size(); j++)
				{
					votblAssignment voTbl = (votblAssignment)rsRelationSpec.elementAt(j);
					
					dCompCP = SR.getAvgCPCompBreakDown(this.surveyID, this.targetID, iRelHigh, 
								voTbl.getRTSpecific(), iCompID);
					OO.insertNumeric(xSpreadsheet2, dCompCP, row, iCol);
					OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
					OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
					iCol++;
				}
			}
						
			OO.insertNumeric(xSpreadsheet2, dCompGap, row, iCol);
			OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
			OO.setBGColor(xSpreadsheet2, iCol, iCol, row, row, BGCOLOR);
			
			OO.setTableBorder(xSpreadsheet2, startColumn, endColumn, row, row, true, true, true, true, true, true);
			
			row++;
			iCol = 1;
			
			// Get Behaviour's gap
			
			rsBehv = SR.getBehaviourGapDevMap(this.surveyID, this.targetID, iCompID);
			
			for(int j=0; j<rsBehv.size(); j++)
			{
				
				String [] arr = (String []) rsBehv.elementAt(j);
				
				
				iCol = 1;
				iBehvID = Integer.parseInt(arr[2]);
				sBehv = arr[1];
				dBehvGap = Double.parseDouble(arr[0]);;
				
				OO.insertString(xSpreadsheet2, sBehv, row, iCol);
				iCol++;
				// SELF CP goes first
				dBehvCP = SR.getAvgCPKBBreakDown(this.surveyID, this.targetID, 2, 0, iBehvID);
				OO.insertNumeric(xSpreadsheet2, dBehvCP, row, iCol);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
				iCol++;
				
				rsRelationHigh = RR.getRelationHigh(this.surveyID);
				
				for(int i=0; i<rsRelationHigh.size(); i++)
				{
					votblRelationHigh vo = (votblRelationHigh)rsRelationHigh.elementAt(i);
					
					iRelHigh = vo.getRTRelation();
					
					rsRelationSpec = RR.getRelationSpecific(this.surveyID, iRelHigh);
					
					for(int k=0; k<rsRelationSpec.size(); k++)
					{
						votblAssignment voTbl = (votblAssignment)rsRelationSpec.elementAt(k);
						
						dBehvCP = SR.getAvgCPKBBreakDown(this.surveyID, this.targetID, iRelHigh, 
									voTbl.getRTSpecific(), iBehvID);
						OO.insertNumeric(xSpreadsheet2, dBehvCP, row, iCol);
						OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
						iCol++;
					}
				}
								
				OO.insertNumeric(xSpreadsheet2, dBehvGap, row, iCol);
				OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
				
				OO.setTableBorder(xSpreadsheet2, startColumn, endColumn, row, row, true, true, true, true, true, true);
				row++;
			}
		}
		
		// If there is no competency in that particular Quadrant, insert relation and leave 2 blank spaces
		if(vLocal.size() == 0)
		{	
			OO.insertString(xSpreadsheet2, "Self", row, iCol);
			OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
			iCol++;
			
			rsRelationHigh = RR.getRelationHigh(this.surveyID);
			
			for(int i=0; i<rsRelationHigh.size(); i++)
			{
				votblRelationHigh vo = (votblRelationHigh)rsRelationHigh.elementAt(i);
				
				iRelHigh = vo.getRTRelation();
				
				rsRelationSpec = RR.getRelationSpecific(this.surveyID, iRelHigh);
				
				for(int j=0; j<rsRelationSpec.size(); j++)
				{
					votblAssignment voTbl = (votblAssignment)rsRelationSpec.elementAt(j);
					sRelation = voTbl.getRelationSpecific();
					
					OO.insertString(xSpreadsheet2, sRelation, row, iCol);
					OO.setCellAllignment(xSpreadsheet2, iCol, iCol, row, row, 1, 2);
					iCol++;
				}
			}
			
			row++;
			OO.insertString(xSpreadsheet2, "There are no competencies under this Quadrant", row, 0);
			OO.mergeCells(xSpreadsheet2, 0, endColumn, row, row);
			row += 2;
		}
	}
	
	public int getCompetencyRank(int iCompetency) throws SQLException
	{
		int iRank = 0;
		
		String sSQL = "SELECT CompetencyRank FROM tblSurveyCompetency WHERE CompetencyID = " + iCompetency;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(sSQL);
		
  		   if(rs.next())
  			   iRank = rs.getInt("CompetencyRank");
 		
  			
  		}catch(Exception ex){
			System.out.println("IndividualReport.java - getCompetencyRank - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		
		return iRank;
	}
	
	/**
	 * Development Map Report (Main function)
	 * @param surveyID
	 * @param targetID
	 * @param fileName
	 * @param bBreakDown - True=Breakdown OTH into sub category
	 * @author Maruli
	 */
	public void reportDevelopmentMap(int surveyID, int targetID, String fileName, boolean bBreakDown)
	{
		try {
			
		//	System.out.println("Development Map Generation Starts");
			
			InitializeExcelDevMap(fileName, "Development Map Template.xls");
			InitializeSurvey(surveyID, targetID, fileName);
			replacementDevelopmentMap();
			
			drawDevelopmentMap();
			populateQuadrantDetail(bBreakDown);
			
			Date timestamp = new Date();
			SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
			String temp = dFormat.format(timestamp);
			
			OO.insertHeaderFooter(xDoc, "", surveyInfo[6] + "\nTarget: " + UserName() + "\n", 
					"Date of Printing: " + temp + "\n" + "Copyright � 3-Sixty Profiler� is a product of Pacific Century Consulting Pte Ltd.", 1, 3);			
			
		//	System.out.println("Development Map Generation Completed");
			
		}catch (SQLException SE) {
			System.out.println("a " + SE.getMessage());
		}catch (Exception E) {
			System.out.println("b " + E.getMessage());
		} finally {      
			
			try {			
				OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);
				OO.closeDoc(xDoc);	
			}catch (SQLException SE) {
				System.out.println("a " + SE.getMessage());
			}catch (IOException IO) {
				System.err.println(IO);
			}catch (Exception E) {
				System.out.println("b " + E.getMessage());
			}
   		}
	}
	
	/**
	 * Print Individual Report
	 * @param surveyID
	 * @param targetID
	 * @param pkUser
	 * @param fileName
	 * @param type - 1=Simplified(No charts), 2=Standard
	 * @author Jenty - Edited by Maruli
	 */
	//Edited printing report with Normation optional by  Tracy 01 Sep 08
	public void Report(int surveyID, int targetID, int pkUser, String fileName, int type, String chkNormative) 
	// End add by Tracy 01 Sep 08
	{	
		//System.out.println(surveyID+ " "+targetID+ " "+pkUser+ " "+ fileName+ " "+ type+ " "+chkNormative);

		try {
			/*
			 * We need to re-initialize the start and end column.
			 * If we need to send reports through email, the next time we tried to generate report,
			 * the start and end column won't be initialised by constructor anymore.
			 * 
			 * If there is a part which substract/add these 2 variables, the code is going to have problem.
			 * Possibly array out of bound will happen.
			 */
			startColumn = 0;
			endColumn = 12;
			
			//System.out.println(surveyID + "\n" + targetID + "\n" + pkUser + "\n" + type);
			//System.out.println("1. Individual Report Generation Starts");
			iReportType = type; //Set report type
			
			//Edited printing ind report with Normative option
			// By Tracy 01 Sep 08****
			if (chkNormative=="") {
				InitializeExcel(fileName, "Individual Report Template_No_Normative_noCPR.xls");
				System.out.println("non normative");
			}else {
				InitializeExcel(fileName, "Individual Report Template_noCPR.xls");
				System.out.println(" normative");
			}
			// End edit by Tracy *********
			
			InitializeSurvey(surveyID, targetID, fileName);
			Replacement();
			
			InsertCPvsCPR();
			//***Added by Tracy 26 Aug 08************
			//Add dynamic Rating task title based on CPR or FPR
			InsertGapTitle(surveyID);
			//*******End Tracy add*******************
			
			InsertGap();
			//Edited printing ind report with Normative option
			// by Tracy 01 Sep 08***
			System.out.println("chkNormative "+chkNormative);
			if (chkNormative!=""){
				InsertNormative();
				System.out.println("normative");
			}else{
				// by Hemilda 23/08/2008 for not include normative hasn't define the array, always got error
				int total 	= totalCompetency();
				//Initialise array for arrN
				arrN = new int[total * 10 * 6]; //size = total competency * max 10 KBs * max 6 Rating

			}
			//End edit by Tracy 01 Sep 08***
			
			// Added by Tracy 01 Sep 08************************
			//Page 6: Ind Profile report- Insert dynamic competency in Gap
			InsertCompGap(surveyID);
			// End add by Tracy 01 Sep 08**********************
			
			InsertCompetency(type);

			int included = Q.commentIncluded(surveyID);
			//Added by Ha 23/06/09 to insert self commnent as well
			int selfIncluded = Q.SelfCommentIncluded(surveyID);
			if(included == 1||selfIncluded==1)
				InsertComments();
			
			if(surveyInfo[8] != "") //Org Logo
			{
				File F = new File(ST.getOOLogoPath()+ surveyInfo[8]); //directory where the file supposed to be stored
				if(F.exists())
					OO.replaceLogo(xSpreadsheet, xDoc, "<Logo>", ST.getOOLogoPath()+ surveyInfo[8]);
				else
					OO.replaceLogo(xSpreadsheet, xDoc, "<Logo>", "");
			}
			
			Date timestamp = new Date();
			SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
			String temp = dFormat.format(timestamp);
			
			OO.insertHeaderFooter(xDoc, surveyInfo[1], surveyInfo[6] + "\n" + UserName() + "\n", 
					"Date of printing: " + temp + "\n" + "Copyright � 3-Sixty Profiler� is a product of Pacific Century Consulting Pte Ltd.");			
			
			//System.out.println("Individual Report Generation Completed");
			
		}catch (SQLException SE) {
			System.out.println("a " + SE.getMessage());
		}catch (Exception E) {
			System.out.println("b " + E.getMessage());
			E.printStackTrace();
		} finally {      
				
			try {
				OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);
				OO.closeDoc(xDoc);	
			}catch (SQLException SE) {
				System.out.println("a " + SE.getMessage());
			}catch (IOException IO) {
				System.err.println(IO);
			}catch (Exception E) {
				System.out.println("b " + E.getMessage());
			}
   		}
	}
	
	
	/**
	 * Print out the customised version report for Toyota
	 * @param surveyID
	 * @param targetID
	 * @param pkUser
	 * @param fileName
	 * @author Maruli
	 */
	public void ReportToyota(int surveyID, int targetID, int pkUser, String fileName) 
	{	
		try {
			
			//System.out.println("1. Individual Report Generation Starts");
			
			InitializeExcel(fileName, "Individual Report Template Combined.xls");
			InitializeSurveyToyota(surveyID, targetID, fileName);
			ReplacementToyota();
			InsertCPvsCPRToyota();
			InsertGapToyota();
			InsertCompetencyToyota();
			
			//System.out.println("Individual Report Generation Completed");
			
		}catch (SQLException SE) {
			System.out.println("a " + SE.getMessage());
		}catch (Exception E) {
			System.out.println("b " + E.getMessage());
		} finally {      
			
			try {
				OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);
				OO.closeDoc(xDoc);	
			}catch (SQLException SE) {
				//System.out.println("a " + SE.getMessage());
				System.out.println("a " + SE);
			}catch (IOException IO) {
				System.err.println(IO);
			}catch (Exception E) {
				//System.out.println("b " + E.getMessage());
				System.out.println("b " + E);
			}
   		}
	}
	
	
	/**
	 * Customised report for Allianz
	 * @param iSurveyID
	 * @param iTarget
	 * @param iPKUser
	 * @param sFilename
	 * @author Maruli
	 */
	public void ReportAllianz(int iSurveyID, int iTarget, int iPKUser, String sFilename)
	{
		try {
			
			//System.out.println("1. Individual Report Generation Starts");
			
			InitializeExcel(sFilename, "Individual Report Template Allianz.xls");
			InitializeSurvey(iSurveyID, iTarget, sFilename);
			ReplacementAllianz();
			AllianzOverall();
			AllianzCompReport();
			InsertCommentsAllianz();
			
			OO.insertHeaderFooter(xDoc, "", "Assessee: "+UserName(), "");
			//System.out.println("Individual Report Generation Completed");
			
		}catch (SQLException SE) {
			System.out.println("a " + SE.getMessage());
			
		}catch (Exception E) {
			System.out.println("b " + E.getMessage());
			
		} finally {
			
			try {
				OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);
				OO.closeDoc(xDoc);
			} catch (SQLException SE) {
				System.out.println("a " + SE.getMessage());
			} catch (IOException IO) {
				System.err.println(IO);
			} catch (Exception E) {
				System.out.println("b " + E.getMessage());
			}
   		}
	}
	
	/**
	 * Send generated individual report through email
	 * @param sTargetName
	 * @param sSurveyName
	 * @param sFilename
	 * @author Maruli
	 */
	public void sendIndividualReport(String sTargetName, String sSurveyName, String sEmail, String sFilename, int surveyId)
	{
		String sHeader = "INDIVIDUAL REPORT OF " + sTargetName + " FOR " + sSurveyName;
		
		try {
			//Edited By Roger 13 June 2008
			EMAIL.sendMail_with_Attachment(ST.getAdminEmail(), sEmail, sHeader, "", sFilename, getOrgId(surveyId));
		}
		catch(Exception E)
		{
			System.out.println("a " + E.getMessage());
		}
	}
	
	/**
	 * Send generated development map through email
	 * @param sTargetName
	 * @param sSurveyName
	 * @param sFilename
	 * @author Maruli
	 */
	public void sendDevelopmentMap(String sTargetName, String sSurveyName, String sEmail, String sFilename, int surveyId)
	{
		String sHeader = "DEVELOPMENT MAP OF " + sTargetName + " FOR " + sSurveyName;
		
		try {
			//Edited By Roger 13 June 2008
			EMAIL.sendMail_with_Attachment(ST.getAdminEmail(), sEmail, sHeader, "", sFilename, getOrgId(surveyId));
		}
		catch(Exception E)
		{
			System.out.println("a " + E.getMessage());
		}
	}
	
	public void setCancelPrint(int iVar) {
		iCancel = iVar;
	}
	
	public int getCancelPrint() {
		return iCancel;
	}
	
	//Edited By Roger 13 June 2008
	//Get Org ID From SurveyID
	public int getOrgId(int surveyId) {
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;	
		int orgId = 0;
		try {
			String sql = "SELECT FKOrganization FROM tblSurvey WHERE SurveyID=" + surveyId;

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
			if (rs.next()) {
				orgId = rs.getInt("FKOrganization");
			}
		} catch (Exception e) {
			
		} finally {	
	    	ConnectionBean.closeRset(rs); //Close ResultSet
	    	ConnectionBean.closeStmt(st); //Close statement
	    	ConnectionBean.close(con); //Close connection
		}		
		return orgId;
	}
	

	public static void main (String [] args) throws IOException, Exception
	{	
		
		IndividualReport_NoCPR IR = new IndividualReport_NoCPR();
		
		int surveyID = 438;
		int targetID = 2328;//6636
		//System.out.println("TEST");
		long past = System.currentTimeMillis();
		// Commented by Tracy 01 Sep 08 IR.Report(489, 7711, 2, "Individual Report (Test).xls", 2);	
		long now = System.currentTimeMillis();
		
		IR.Report(498,6611,6404,"IndividualReport220908153454.xls",2,"");
		//System.out.println("Time taken: " + (past-now)/1000);
		/*
		int surveyID = 459;
		int targetID = 2410;
		IR.ReportToyota(surveyID, targetID, 124, "IndividualReportToyota.xls");
		*/
		/*
		int surveyID = 458;
		int targetID = 6497;
		IR.ReportAllianz(surveyID, targetID, 6450, "Individual Report Allianz.xls");
		*/
		
		//int surveyID = 466;
		//int targetID = 6612;
		//int surveyID = 462;
		//int targetID = 1076;
		//IR.reportDevelopmentMap(surveyID, targetID,"Development Map.xls", true);
		
		//int surveyID = 459;
		//int targetID = 2105;
		//int pkUser = 1;
		//int targetID [] = {7155,7156,7160,7162,7165,7170,7171,7172,7174,7175,7177,7180,7181,7182,7187,7191,7192,7199,7200,7205,7211,7214,7220,7221,7223,7226,7229,7230,7231,7233};
		//String file_name [] = {"Natcha Teinwutichai.xls", "Nawanan Chalard.xls", "Nopparat Pornrattanapitak.xls", "Nuchanart Pitchayanan.xls", "Ornsuda Pornpattrapong.xls", "Panya Kitcharoenkankul.xls", "Paparnin Lertpaitoonpan.xls", "Paranee Leelasettakul.xls", "Patanee Chokdeepanich.xls", "Patcharin Jingkaojai.xls", "Paweenuch Sutthirat.xls", "Pijika Wongwiwat.xls", "Pinnapa Nakham.xls", "Piyada Soontornchaiya.xls", "Prasarn Gritsanawarun.xls", "Rungmit Chunhengpan.xls","Rungrawee Selananda.xls", "Saowanee Jarusruangchai.xls", "Saroch Pornputtkul.xls", "Siriporn Saeteaw.xls", "Somsaluay Suwanprasop.xls", "Sukanya Niyomthamakit.xls", "Surakit Chunharotrit.xls", "Surat Pornputtichai.xls", "Sutisophan Chuaywongyart.xls", "Thanarat Somphol.xls", "Usaporn Preechawud.xls", "Vithita Prasopakarakit.xls", "Vuthiphan Vuthiphan.xls", "Wannaporn Sukhonpun.xls"};

		/*int targetID [] = {2394};
		String file_name [] = {"WEERAYUT RASSADONVIJIT.xls"};
		*/
		//for(int i=0; i<targetID.length; i++)
			//IR.Report(surveyID, targetID[i], 6559, file_name[i], 2);
		
	
		
		//System.exit(1);
	} 
}