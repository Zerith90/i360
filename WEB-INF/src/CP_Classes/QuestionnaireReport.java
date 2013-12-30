package CP_Classes;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voDepartment;
import CP_Classes.vo.voDivision;
import CP_Classes.vo.voGroup;
import CP_Classes.vo.votblAssignment;
import CP_Classes.vo.votblJobPosition;
import CP_Classes.vo.votblSurvey;
import util.Utils;
/**
 * 
 * Change Log
 * ==========
 *
 * Date        By				Method(s)            													Change(s) 
 * ============================================================================================================================================
 * 24/05/12	  Albert		   Constructor												Added new data members: groupName, deptName, 
 * 																						divName
 * 
 * 24/05/12	  Albert		setGroupName(String),getGroupName(),						- Added the methods to accommodate changing the identifier from ID 
 * 							setDivisionName(String),getDivisionName(),					  name
 * 							setDepartmentName(String),getDepartmentName(),				- One name might have more than one ID for example 2 different divisions
 * 								  														have different department/group but with the same name
 * 							 															to accommodate MOE group reports
 * 							 
 * 25/05/12   Liu Taichen   getRater(int, int, int , int, int)                            -Created method to enable retrieving of raters under a particular group
 * 
 * 12/06/12   Albert   		getRaterTar(int, int, int , int, int)                        Retrieve all rater based on target
 *
 *  09/07/12   Liu Taichen  getTarget(int, int, int, int, int)                            Created this method to retrieve targets under a round
 *
 * 17/07/2012	Liu Taichen	SurveyIDImpt												created this field and the getter and setter to store the importance survey ID selected 
 */

public class QuestionnaireReport
{
//	private Database db;
	private User_Jenty U;
	public int SurveyID;
	public int SurveyIDImpt;
	public int GroupID;
	public int TargetID;
	public int RaterID;
	public int JobPostID;
	public int RoundID;
	public int WaveID;
	public int DivisionID;
	public int DepartmentID;
	public Boolean AttachReport;
	public String divName;
	public String deptName;
	public String groupName;
	public int PageLoad;
	public String designationName; //Gwen Oh - 18/10/11: Added to store the designation
	
	public QuestionnaireReport() {
		//db = new Database();
		U = new User_Jenty();
		
		SurveyID = 0;
		GroupID = 0;
		RoundID = 0;
		TargetID = 0;
		RaterID = 0;
		PageLoad = 0;
		designationName = "";
	}
	
	/**
	 * Set group name
	 * @param groupName
	 */	
	public void setGroupName(String groupName){
		this.groupName = groupName;
	}
	/**
	 * Set department name
	 * @param deptName
	 */	
	public void setDepartmentName(String deptName){
		this.deptName = deptName;
	}
	/**
	 * Set division name
	 * @param divName
	 */	
	public void setDivisionName(String divName){
		this.divName = divName;
	}
	/**
	 * Get group name
	 * @return groupName
	 */	
	public String getGroupName(){
		return groupName;
	}
	/**
	 * Get department name
	 * @return deptName
	 */	
	public String getDepartmentName(){
		return deptName;
	}
	/**
	 * Get division name
	 * @return divName
	 */	
	public String getDivisionName(){
		return divName;
	}
	public void setSurveyID(int SurveyID) {
		this.SurveyID = SurveyID;
	}

	public int getSurveyID() {
		return SurveyID;
	}
	
	public void setJobPostID(int JobPostID) {
		this.JobPostID = JobPostID;
	}

	public int getJobPostID() {
		return JobPostID;
	}
	
	public void setDivisionID(int DivisionID) {
		this.DivisionID = DivisionID;
	}

	public int getDivisionID() {
		return DivisionID;
	}
	
	public void setDepartmentID(int DepartmentID) {
		this.DepartmentID = DepartmentID;
	}

	public int getDepartmentID() {
		return DepartmentID;
	}

	public void setGroupID(int GroupID) {
		this.GroupID = GroupID;
	}

	public int getGroupID() {
		return GroupID;
	}

	//Gwen Oh - 18/10/2011: Accessor methods for designationID
	public void setDesignationName(String desingationName) {
		this.designationName = desingationName;
	}
	
	public String getDesignationName() {
		return designationName;
	}
	
	public void setTargetID(int TargetID) {
		this.TargetID = TargetID;
	}

	public int getTargetID() {
		return TargetID;
	}
	
	public void setRaterID(int RaterID) {
		this.RaterID = RaterID;
	}

	public int getRaterID() {
		return RaterID;
	}
	
	public void setPageLoad(int PageLoad) {
		this.PageLoad = PageLoad;
	}

	public int getPageLoad() {
		return PageLoad;
	}
	
	public Vector getJobPost(int compID, int orgID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT tblJobPosition.JobPositionID , ";
			query = query + "tblJobPosition.* FROM tblJobPosition INNER JOIN ";
			query = query + "tblSurvey ON tblJobPosition.JobPositionID = tblSurvey.JobPositionID ";
			query = query + "WHERE tblJobPosition.FKOrganization = " + orgID;
			query = query + " ORDER BY tblJobPosition.JobPosition";
		
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				votblJobPosition vo=new votblJobPosition();
				
				vo.setFKOrganization(rs.getInt("FKOrganization"));
				vo.setJobPositionID(rs.getInt("JobPositionID"));
				vo.setJobPosition(rs.getString("JobPosition"));
				v.add(vo);
			}
			/*
			db.openDB();
			Statement stmt = db.con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			return rs;
			*/
			
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getJobPost - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
	}
	
	/**
	 * Retrieve divisions based on survey
	 * @param surveyID
	 * @return vector
	 */	
	public Vector getDivision(int surveyID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT Division.PKDivision, Division.DivisionName FROM Division INNER JOIN ";
           	query += "tblAssignment ON Division.PKDivision = tblAssignment.FKTargetDivision WHERE ";
           	query += "(tblAssignment.SurveyID = " + surveyID + ") ORDER BY Division.DivisionName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				voDivision vo=new voDivision();
				
				vo.setPKDivision(rs.getInt("PKDivision"));
				vo.setDivisionName(rs.getString("DivisionName"));
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getDivision - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
		
	}
	
	/**
	 * Retrieve divisions with unique division name based on survey and organization
	 * @param surveyID
	 * @param orgID
	 * @return vector
	 */
	public Vector getDivision(int surveyID, int orgID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT Division.PKDivision, Division.DivisionName FROM Division INNER JOIN ";
           	query += "tblAssignment ON Division.PKDivision = tblAssignment.FKTargetDivision WHERE ";
           	query += "(tblAssignment.SurveyID = " + surveyID + ") ";
           	query += "AND Division.FKOrganization =" + orgID + " ";
           	query += "ORDER BY Division.DivisionName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				voDivision vo=new voDivision();
				vo.setPKDivision(rs.getInt("PKDivision"));
				vo.setDivisionName(rs.getString("DivisionName"));
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getDivision - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
		
	}
	
	public String getDivisionName(int divID){
		String name="";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DivisionName FROM [Division] WHERE PKDivision = "+divID;
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			if(rs.next()){
				name = rs.getString("DivisionName");
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getDivisionName - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return name;
	}

	public Vector getDepartment(int surveyID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT Department.PKDepartment, Department.DepartmentName, Department.Location ";
			query = query + "FROM tblAssignment INNER JOIN Department ON ";
			query = query + "tblAssignment.FKTargetDepartment = Department.PKDepartment ";
			query = query + "WHERE (tblAssignment.SurveyID = " + surveyID + ") ";
			query = query + "ORDER BY Department.DepartmentName ";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				voDepartment vo=new voDepartment();
				
				vo.setPKDepartment(rs.getInt("PKDepartment"));
				vo.setDepartmentName(rs.getString("DepartmentName"));
				vo.setLocation(rs.getString("Location"));
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getDepartment - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return v;
	}
	
	
	/* Added by Su See
	 * Retrieve all depts based on survey and division
	 */
	public Vector getDepartment(int surveyID, int divID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT Department.PKDepartment, Department.DepartmentName, Department.Location ";
			query = query + "FROM tblAssignment INNER JOIN Department ON ";
			query = query + "tblAssignment.FKTargetDepartment = Department.PKDepartment ";
			query = query + "WHERE (tblAssignment.SurveyID = " + surveyID + ") ";
			
			if(divID > 0)
			{
				query = query + "AND Department.FKDivision = " + divID + " ";
			}
			
			query = query + "ORDER BY Department.DepartmentName ";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				voDepartment vo=new voDepartment();
				
				vo.setPKDepartment(rs.getInt("PKDepartment"));
				vo.setDepartmentName(rs.getString("DepartmentName"));
				vo.setLocation(rs.getString("Location"));
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getDepartment - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return v;
	}
	
	public String getDepartmentName(int deptID){
		String name="";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DepartmentName FROM [Department] WHERE PKDepartment = "+deptID;
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			if(rs.next()){
				name = rs.getString("DepartmentName");
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getDepartmentName - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return name;
	}
	
	public Vector getSurvey(int jobPost) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT tblSurvey.* FROM tblJobPosition INNER JOIN ";
			query = query + "tblSurvey ON tblJobPosition.JobPositionID = tblSurvey.JobPositionID ";
			query = query + "WHERE tblJobPosition.JobPositionID = " + jobPost;
			query = query + " ORDER BY tblSurvey.SurveyName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				votblSurvey vo=new votblSurvey();
				
				vo.setSurveyName(rs.getString("SurveyName"));
				vo.setSurveyID(rs.getInt("SurveyID"));
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getSurvey - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
		
		
	}
	
	public Vector getSurvey(int compID, int orgID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT * FROM tblSurvey a, tblOrganization b WHERE a.FKOrganization = b.PKOrganization ";
           //changed query made by Ha 03/06/08
			if (orgID != 0)
				query = query + " AND a.FKOrganization = " + orgID;
			else
				query = query + "	AND a.FKCompanyID = " + compID;
		/*String query = "Select * from tblSurvey ";
			query = query + "where FKOrganization = " + orgID + " and FKCompanyID = " + compID;*/
			query = query + " order by SurveyName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblSurvey vo=new votblSurvey();
				vo.setSurveyName(rs.getString("SurveyName"));
				vo.setSurveyID(rs.getInt("SurveyID"));
				
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getSurvey - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
		
		
	}
	public Vector getSurveys(int orgID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT * FROM tblSurvey a, tblOrganization b WHERE a.FKOrganization = b.PKOrganization ";
           //changed query made by Ha 03/06/08
			if (orgID != 0)
				query = query + " AND a.FKOrganization = " + orgID;
	
		/*String query = "Select * from tblSurvey ";
			query = query + "where FKOrganization = " + orgID + " and FKCompanyID = " + compID;*/
			query = query + " order by SurveyName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblSurvey vo=new votblSurvey();
				vo.setSurveyName(rs.getString("SurveyName"));
				vo.setSurveyID(rs.getInt("SurveyID"));
				
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getSurvey - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
		
		
	}
	public Vector getGroup(int surveyID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT [Group].PKGroup, [Group].GroupName FROM tblAssignment INNER JOIN ";
          	query += "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
           	query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
           	query += "(tblAssignment.SurveyID = " + surveyID + ") ORDER BY [Group].GroupName ";

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				voGroup vo=new voGroup();
				vo.setPKGroup(rs.getInt("PKGroup"));
				vo.setGroupName(rs.getString("GroupName"));
			
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getGroup - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
		
	}
	
	/**
	 * Retrieve groups based on survey and department
	 * @param surveyID
	 * @param divID
	 * @param deptID
	 * @return
	 * @author Su See
	 */
	public Vector getGroup(int surveyID, int divID, int deptID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT Division.PKDivision, Division.DivisionName, Department.PKDepartment, Department.DepartmentName, [Group].PKGroup, [Group].GroupName ";
          	query += "FROM [Group] INNER JOIN ";
           	query += "Department ON [Group].FKDepartment = Department.PKDepartment INNER JOIN ";
            query += "Division ON Department.FKDivision = Division.PKDivision AND Department.FKDivision = Division.PKDivision INNER JOIN ";
            query += "tblAssignment ON [Group].PKGroup = tblAssignment.FKTargetGroup AND Department.PKDepartment = tblAssignment.FKTargetDepartment AND ";
            query += "Division.PKDivision = tblAssignment.FKTargetDivision INNER JOIN " ;
            query += "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID " ;
            query += "WHERE (tblAssignment.SurveyID = " + surveyID + ") ";
            	
           	if(deptID > 0)
           	{
           		query += "AND Department.PKDepartment = " + deptID + " ";
           	}
           	
           	if(divID > 0)
           	{
           		query += "AND Division.PKDivision =" + divID + " ";
           	}
           	
           	query += "ORDER BY [Group].GroupName ";

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				voGroup vo=new voGroup();
				
				vo.setPKGroup(rs.getInt("PKGroup"));
				vo.setGroupName(rs.getString("GroupName"));
				vo.setPKDivision(rs.getInt("PKDivision"));
				vo.setDivisionName(rs.getString("DivisionName"));
				vo.setPKDepartment(rs.getInt("PKDepartment"));
				vo.setDepartmentName(rs.getString("DepartmentName"));
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getGroup - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;

	}
	
	public String getGroupName(int groupID){
		String name="";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT GroupName FROM [Group] WHERE PKGroup = "+groupID;
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			if(rs.next()){
				name = rs.getString("GroupName");
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getGroupName - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return name;
	}

	/** 
	 * Retrieve designation based on survey, division, department, group
	 * @param surveyID
	 * @param divID
	 * @param deptID
	 * @return
	 * @author Gwen Oh
	 */
	public Vector getDesignation(int surveyID, int divID, int deptID, int groupID) {
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
            String query = "SELECT DISTINCT [User].Designation FROM [User] WHERE pkuser in (" +
            		"SELECT TargetLoginID FROM tblAssignment WHERE SurveyID = " + surveyID;
            	
           	if(deptID > 0)
           	{
           		query += "AND FKTargetDepartment = " + deptID + " ";
           	}
           	
           	if(divID > 0)
           	{
           		query += "AND FKTargetDivision =" + divID + " ";
           	}
           	
           	if (groupID > 0)
           		query += "AND FKTargetGroup =" + groupID + " ";
           		
           	query += ")" + " ORDER BY Designation";

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				v.add(rs.getString("Designation"));
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getDesignation - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;

	}
	
	public Vector getTarget(int surveyID, int group)
	{
		int seq = U.NameSequence_BySurvey(surveyID);
		
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT tblAssignment.TargetLoginID, ";
			
			if(seq == 0)
				query += " [User].FamilyName + ' ' + [User].GivenName as FullName ";
			else
				query += " [User].GivenName + ' ' +  [User].FamilyName as FullName ";
				
			query = query + "FROM tblAssignment INNER JOIN ";
			query = query + "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
			query = query + "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
			query = query + "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
			
			if(group >0)
				query = query + "tblSurvey.SurveyID = " + surveyID + " AND tblAssignment.FKTargetGroup = " + group;
			else
				query = query + "tblSurvey.SurveyID = " + surveyID;
				
			query = query + " ORDER BY FullName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblAssignment vo=new votblAssignment();
				
				vo.setFullName(rs.getString("FullName"));
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getTarget - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;	
	}
	
	public int getWaveID()
	{
		return WaveID;
	}
	
	public void setWaveID(int wave)
	{
		WaveID = wave;
	}
	
	public Vector getWave()
	{
		
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT wave ";	
			query = query + "FROM tblAssignment inner join [user] a on RaterLoginID = a.PKUser ";
			query = query + " where TargetLoginID = "+TargetID;
			query = query + " and SurveyID = " + SurveyID;
			query = query + " and a.Round = " + RoundID;
			query = query + " and Wave IS NOT NULL";
			query = query + " ORDER BY wave";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next()){
				//votblAssignment vo=new votblAssignment();
				//vo.setWave(rs.getInt("wave"));
				//v.add(vo);
				v.add(rs.getInt("wave"));
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getWave - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;	
	}
	
	public Vector getRound()
	{
		
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT round from tblAssignment where SurveyID = " + SurveyID;	
			query = query + " ORDER BY round";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next()){
				//votblAssignment vo=new votblAssignment();
				//vo.setWave(rs.getInt("wave"));
				//v.add(vo);
				v.add(rs.getInt("round"));
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getRound - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;	
	}
	
	
	/**
	 * Retrieve all targets' IDs under the particular survey, div, dept and group
	 * @param surveyID
	 * @param divID
	 * @param deptID
	 * @param groupSection
	 * @return ResultSet
	 */
	public Vector getTarget(int surveyID, int divID, int deptID, int groupSection)
	{
		
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			int seq = U.NameSequence_BySurvey(surveyID);
			String query = "SELECT DISTINCT tblAssignment.TargetLoginID, ";
			
			if(seq == 0)
				query += "[User].FamilyName + ' ' + [User].GivenName as FullName ";
			else
				query += "[User].GivenName + ' ' +  [User].FamilyName as FullName ";
				
			query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
			query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
            query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = " + surveyID + ")";
		
			if(groupSection > 0)
				query += " AND [Group].PKGroup = " + groupSection;
			
			if(deptID > 0)
				query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";
			
			if(divID > 0)
				query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";
		
			query = query + " ORDER BY FullName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblAssignment vo=new votblAssignment();
				vo.setFullName(rs.getString("FullName"));
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getTarget - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;

	
	}
	
	/*
	 * Change(s) :Created method to enable retrieving of raters under a particular group
	 * Reason(s) : To enable sending of email to specific group of raters
	 * Updated By: Liu Taichen
	 * Updated On: 25 May 2012
	 */
	/**
	 * Method called in SendPartEmail.jsp to retrieve all raters IDs under the particular survey, division, department and group
	 * @param surveyID - Specify the survey to reference
	 * @param divID - Specify the ID of the division to be referenced
	 * @param deptID - Specify the ID of the department
	 * @param groupID - Specify the ID of the group
	 * 
	 */
	public Vector getRater(int surveyID, int divID, int deptID, int groupID, int targetID, int raterID){
		Vector v = new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	 	
		try {
			int seq = U.NameSequence_BySurvey(surveyID);
			String query = "SELECT DISTINCT tblAssignment.RaterLoginID, ";
			
			if(seq == 0)
				query += "[User].FamilyName + ' ' + [User].GivenName as FullName ";
			else
				query += "[User].GivenName + ' ' +  [User].FamilyName as FullName ";
				
			query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.RaterLoginID = [User].PKUser ";
			query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
            query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = " + surveyID + ")";
		
			if(groupID > 0)
				query += " AND [Group].PKGroup = " + groupID;
			
			if(deptID > 0)
				query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";
			
			if(divID > 0)
				query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";
			
			if(targetID > 0)
				query += " AND (tblAssignment.TargetLoginID = " + targetID + ")";
			
			if(raterID > 0)
				query += " AND (tblAssignment.RaterLoginID = " + raterID + ")";
		
			query = query + " ORDER BY FullName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblAssignment vo=new votblAssignment();
				vo.setFullName(rs.getString("FullName"));
				vo.setRaterLoginID(rs.getInt("RaterLoginID"));
				
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getTarget - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
	}
	
	/**
	 * Method called in SendPartEmail.jsp to retrieve all target IDs under the particular survey, division, department and group and round
	 * @param surveyID - Specify the survey to reference
	 * @param divID - Specify the ID of the division to be referenced
	 * @param deptID - Specify the ID of the department
	 * @param groupID - Specify the ID of the group
	 * @param round - the round number
	 * @author Liu Taichen
	 */
	public Vector getTarget(int surveyID, int divID, int deptID, int groupSection, int round)
	{
		
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			int seq = U.NameSequence_BySurvey(surveyID);
			String query = "SELECT DISTINCT tblAssignment.TargetLoginID, ";
			
			if(seq == 0)
				query += "[User].FamilyName + ' ' + [User].GivenName as FullName ";
			else
				query += "[User].GivenName + ' ' +  [User].FamilyName as FullName ";
				
			query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
			query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
            query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = " + surveyID + ")";
		
			if(groupSection > 0)
				query += " AND [Group].PKGroup = " + groupSection;
			
			if(deptID > 0)
				query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";
			
			if(divID > 0)
				query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";
			if(round > 0){
				query += " AND ([User].Round = " + round + ")";
			}
		
			query = query + " ORDER BY FullName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblAssignment vo=new votblAssignment();
				vo.setFullName(rs.getString("FullName"));
				vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getTarget - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;

	
	}
	
	/**
	 * Method called in SendPartEmail.jsp to retrieve all raters IDs under the particular survey, division, department and group
	 * @param surveyID - Specify the survey to reference
	 * @param divID - Specify the ID of the division to be referenced
	 * @param deptID - Specify the ID of the department
	 * @param groupID - Specify the ID of the group
	 * 
	 */
	public Vector getRaterTar(int surveyID, int divID, int deptID, int groupID,  int roundID,int targetID, int waveID){
		Vector v = new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			int seq = U.NameSequence_BySurvey(surveyID);
			String query = "SELECT DISTINCT tblAssignment.RaterLoginID, ";
			
			if(seq == 0)
				query += "[User].FamilyName + ' ' + [User].GivenName as FullName ";
			else
				query += "[User].GivenName + ' ' +  [User].FamilyName as FullName ";
				
			query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.RaterLoginID = [User].PKUser ";
			query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
            query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = " + surveyID + ")";
		
			if(groupID > 0)
				query += " AND [Group].PKGroup = " + groupID;
			
			if(deptID > 0)
				query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";
			
			if(divID > 0)
				query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";
			
			if(roundID > 0)
				query += " AND ([user].Round = " + roundID + ")";
			
			if(targetID > 0)
				query += " AND (tblAssignment.TargetLoginID = " + targetID + ")";
			
			if(waveID > 0)
				query += "AND (tblAssignment.wave = " + waveID + ")";
		
			query = query + " ORDER BY FullName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblAssignment vo=new votblAssignment();
				vo.setFullName(rs.getString("FullName"));
				vo.setRaterLoginID(rs.getInt("RaterLoginID"));
				
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getRaterTar - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
	}
	/**
	 * Method called in SendPartEmail.jsp to retrieve all raters IDs under the particular survey, division, department and group
	 * @param surveyID - Specify the survey to reference
	 * @param divID - Specify the ID of the division to be referenced
	 * @param deptID - Specify the ID of the department
	 * @param groupID - Specify the ID of the group
	 * 
	 */
	public Vector getRater(int surveyID, int divID, int deptID, int groupID){
		Vector v = new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			int seq = U.NameSequence_BySurvey(surveyID);
			String query = "SELECT DISTINCT tblAssignment.RaterLoginID, ";
			
			if(seq == 0)
				query += "[User].FamilyName + ' ' + [User].GivenName as FullName ";
			else
				query += "[User].GivenName + ' ' +  [User].FamilyName as FullName ";
				
			query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.RaterLoginID = [User].PKUser ";
			query += "INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
            query += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE (tblSurvey.SurveyID = " + surveyID + ")";
		
			if(groupID > 0)
				query += " AND [Group].PKGroup = " + groupID;
			
			if(deptID > 0)
				query += " AND (tblAssignment.FKTargetDepartment = " + deptID + ")";
			
			if(divID > 0)
				query += " AND (tblAssignment.FKTargetDivision = " + divID + ")";
		
			query = query + " ORDER BY FullName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblAssignment vo=new votblAssignment();
				vo.setFullName(rs.getString("FullName"));
				vo.setRaterLoginID(rs.getInt("RaterLoginID"));
				
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getTarget - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;
	}

	public Vector getRater(int surveyID, int group, int targetLoginID)
	{
		int seq = U.NameSequence_BySurvey(surveyID);
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT tblAssignment.RaterLoginID, ";
			if(seq == 0)
				query += " [User].FamilyName + ' ' + [User].GivenName as FullName ";
			else
				query += " [User].GivenName + ' ' +  [User].FamilyName as FullName ";
				
			query = query + "FROM tblAssignment INNER JOIN ";
			query = query + "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
			query = query + "[User] ON tblAssignment.RaterLoginID = [User].PKUser INNER JOIN ";
			query = query + "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
			query = query + "tblSurvey.SurveyID = " + surveyID;	
			query = query + " AND tblAssignment.TargetLoginID = " + targetLoginID;
			query = query + " ORDER BY FullName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblAssignment vo=new votblAssignment();
			
				vo.setFullName(rs.getString("FullName"));
				vo.setRaterLoginID(rs.getInt("RaterLoginID"));
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getRater - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;

		
	}
	
	/**
	  * Retrieve the rater's full name.
	  * @param SurveyID - Specify which survey to reference for rater's full name
	  * @param raterLoginID - Specify which rater to reference
	  * @author Sebastian
	  * @since v.1.3.12.81 (26 July 2010)
	**/
	public String getRaterFullName(int surveyID, int raterLoginID)
	{
		int seq = U.NameSequence(surveyID);
		String fullName = "";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT ";
			if(seq == 0)
				query += "[User].FamilyName + ' ' + [User].GivenName as FullName ";
			else
				query += "[User].GivenName + ' ' +  [User].FamilyName as FullName ";
				
			query = query + "FROM [User] ";
			query = query + "WHERE [User].PKUser = " + raterLoginID + " ";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			if(rs.next()){
				fullName = rs.getString("FullName");
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getRater - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return fullName;

		
	}
	
	/**
	  * To get the list of raters based on SurveyID. Used for selecting rater to get Targets Questionnaires by rater
	  * @param int surveyID - Specify which survey to reference for rater
	  * @author Sebastian
	  * @since v.1.3.12.81 (26 July 2010)
	**/
	public Vector getRater(int surveyID)
	{
		int seq = U.NameSequence(surveyID);
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String query = "SELECT DISTINCT tblAssignment.RaterLoginID, ";
			if(seq == 0)
				query += " [User].FamilyName + ' ' + [User].GivenName as FullName ";
			else
				query += " [User].GivenName + ' ' +  [User].FamilyName as FullName ";
				
			query = query + "FROM tblAssignment INNER JOIN ";
			query = query + "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
			query = query + "[User] ON tblAssignment.RaterLoginID = [User].PKUser INNER JOIN ";
			query = query + "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup WHERE ";
			query = query + "tblSurvey.SurveyID = " + surveyID;
			query = query + " ORDER BY FullName";
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()){
				votblAssignment vo=new votblAssignment();
			
				vo.setFullName(rs.getString("FullName"));
				vo.setRaterLoginID(rs.getInt("RaterLoginID"));
				v.add(vo);
			}
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - getRater - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return v;

		
	}
	
	/**
	  * To get a list of ALL targets of a specific survey that the specific rater need to rate, 
	  * 	including those targets that rater has already rated.
	  * @param int SurveyID - Specify which survey to reference for targets
	  * @param int RaterID - Specify which rater to reference for targets
	  * @author Sebastian
	  * @since v.1.3.12.81 (26 July 2010)
	**/
	// Updated method name to reflect actual feature of this method, Desmond 10 August 2010
	public Vector getAllSurveyTargetsByRater(int surveyID, int raterID)
	{		
		int seq = U.NameSequence_BySurvey(surveyID);
		
		String query = "SELECT TargetLoginID, ";
		
		// Added codes for handling name sequence, Desmond 10 August 2010
		if(seq == 0) {
			query += " [User].FamilyName + ' ' + [User].GivenName as FullName ";
		} else {
			query += " [User].GivenName + ' ' +  [User].FamilyName as FullName ";
		}
		
		query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser " + 
			"WHERE SurveyID = " + surveyID + " AND RaterLoginID = " + raterID;
	
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector v = new Vector();

	  	try 
        {          

	  		con=ConnectionBean.getConnection();
	  		st=con.createStatement();
	  		rs=st.executeQuery(query);
	  		
	  		while(rs.next()) {
	  			String[] tmp = new String[2];
	  			tmp[0] = rs.getString(1);
	  			tmp[1] = rs.getString(2);
	  			
				v.add(tmp);
	  		}
		
        }
        catch(Exception E) 
        {
            
            System.err.println("ExcelQuestionnaire.java - getAllSurveyTargetsByRater - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
        return v;
	} // End getAllSurveyTargetsByRater()
	
	/**
	  * Retrieves only the list of targets for a specific survey that the specified rater has NOT rated yet
	  * @param int SurveyID - Specify which survey to reference for targets
	  * @param int RaterID - Specify which rater to reference for targets
	  * @author Desmond
	  * @since v.1.3.12.89 (10 August 2010)
	**/
	public Vector getSurveyTargetsByRater(int surveyID, int raterID)
	{		
		int seq = U.NameSequence_BySurvey(surveyID);
		
		String query = "SELECT TargetLoginID, ";
		
		// Added codes for handling name sequence, Desmond 10 August 2010
		if(seq == 0) {
			query += " [User].FamilyName + ' ' + [User].GivenName as FullName ";
		} else {
			query += " [User].GivenName + ' ' +  [User].FamilyName as FullName ";
		}
		
		query += "FROM tblAssignment INNER JOIN [User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		query += "WHERE SurveyID = " + surveyID + " AND RaterLoginID = " + raterID + " AND RaterStatus = 0";
	
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector v = new Vector();

	  	try 
       {          

	  		con=ConnectionBean.getConnection();
	  		st=con.createStatement();
	  		rs=st.executeQuery(query);
	  		
	  		while(rs.next()) {
	  			String[] tmp = new String[2];
	  			tmp[0] = rs.getString(1);
	  			tmp[1] = rs.getString(2);
	  			
				v.add(tmp);
	  		}
		
       }
       catch(Exception E) 
       {
           
           System.err.println("ExcelQuestionnaire.java - GetSurveyTargetsByRater - " + E);
       }
       finally
       {
       	ConnectionBean.closeRset(rs); //Close ResultSet
       	ConnectionBean.closeStmt(st); //Close statement
       	ConnectionBean.close(con); //Close connection
       }
		
       return v;
	} // End getSurveyTargetsByRater()
	
	/**
	  * To generate a list of questionnaires that the rater need to rate and and place them all in a zip archive
	  * @param int surveyID - Specify which survey to reference for targets
	  * @param int raterID - Specify which rater to reference for targets
	  * @param int pkUser - Specify the user of the system
	  * @return String - return the full file path name of the zip file when it is created sucessfully created. Return empty if it is created unsuccessfully.
	  * @author Sebastian
	  * @since v.1.3.12.81 (26 July 2010)
	**/
	// Updated method name to reflect actual feature of this method, Desmond 10 August 2010
	public String generateQuestionnairesByRaterZipped(int surveyID, int raterID, int pkUser)
	{
		ExcelQuestionnaire EQ = new ExcelQuestionnaire();
		
		//get the targets that the this rater needs to rate
		// Updated method call to newly renamed method getAllSurveyTargetsByRater(), Desmond 10 August 2010
		Vector targets = getAllSurveyTargetsByRater(surveyID, raterID);

		Date timeStamp = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("ddMMyyHHmmss");
		String temp = dFormat.format(timeStamp);
		
		String raterName = "";
		String input_file_name = "";
		String output_file_name = "";
		
		String file_name_1 = "Questionnaire" + temp;
		String file_name_2 = ".xls";
		String targetsDetails[] = null;
		
		//To get the name of the rater for later append the rater name to the filename of each questionnaire
		raterName = getRaterFullName(surveyID, raterID);
		
		Vector vfilenames = new Vector();
		
		//Create the questionnaires files and store them into the report directory
		for (int i=0; i<targets.size(); i++)
		{
			targetsDetails = (String[]) targets.get(i);
			
			input_file_name = file_name_1 + "_" + targetsDetails[1].toLowerCase() + "_" + raterName.toLowerCase() + file_name_2; //e.g. QuestionnaireReport01012010_TargetLoginName_RaterLoginName.xls
			output_file_name = "Questionnaire" + "_" + targetsDetails[1].toLowerCase() + "_" + raterName.toLowerCase() + file_name_2; //Output filename when user download e.g. Questionnaire_TargetLoginName_RaterLoginName.xls
			
			EQ.QuestionnaireReport(surveyID, Integer.parseInt(targetsDetails[0]), raterID, pkUser, input_file_name);
			
			//file_name[0] = input filename, file_name[1] = output filename
			String []filenames = new String[2]; 
			filenames[0] = input_file_name;
			filenames[1] = output_file_name;
			
			vfilenames.add(filenames);
		}
		
		Utils utils = new Utils();
		
		//generate zip archive of the questionnaire created
		return utils.zipArchive("Questionnaires" + temp + ".zip", vfilenames);
	} // End generateQuestionnairesByRaterZipped()
	
	/**
	  * To generate a list of questionnaires that the rater need to rate
	  * @param int surveyID - Specify which survey to reference for targets
	  * @param int raterID - Specify which rater to reference for targets
	  * @param int pkUser - Specify the user of the system
	  * @return Vector - return pairs of filenames (i.e. Input Filename and Output Filename) for all questionnaires that rater needs to complete
	  * @author Desmond
	  * @since v.1.3.12.89 (10 August 2010)
	**/
	public Vector generateQuestionnairesByRater(int surveyID, int raterID, int pkUser)
	{
		ExcelQuestionnaire EQ = new ExcelQuestionnaire();
		
		//get the targets that the this rater needs to rate
		Vector targets = getSurveyTargetsByRater(surveyID, raterID);

		Date timeStamp = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("ddMMyyHHmmss");
		String temp = dFormat.format(timeStamp);
		
		String raterName = "";
		String input_file_name = "";
		String output_file_name = "";
		
		String file_name_1 = "Questionnaire" + temp;
		String file_name_2 = ".xls";
		String targetsDetails[] = null;
		
		//To get the name of the rater for later append the rater name to the filename of each questionnaire
		raterName = getRaterFullName(surveyID, raterID);
		
		Vector vfilenames = new Vector();
		
		//Create the questionnaires files and store them into the report directory
		for (int i=0; i<targets.size(); i++)
		{
			targetsDetails = (String[]) targets.get(i);
			
			input_file_name = file_name_1 + "_" + targetsDetails[1].toLowerCase() + "_" + raterName.toLowerCase() + file_name_2; //e.g. QuestionnaireReport01012010_TargetLoginName_RaterLoginName.xls
			output_file_name = "Questionnaire" + "_" + targetsDetails[1].toLowerCase() + "_" + raterName.toLowerCase() + file_name_2; //Output filename when user download e.g. Questionnaire_TargetLoginName_RaterLoginName.xls
			
			EQ.QuestionnaireReport(surveyID, Integer.parseInt(targetsDetails[0]), raterID, pkUser, input_file_name);
			
			//file_name[0] = input filename, file_name[1] = output filename
			String []filenames = new String[2]; 
			filenames[0] = input_file_name;
			filenames[1] = output_file_name;
			
			vfilenames.add(filenames);
		}
		
		return vfilenames;
	} // End generateQuestionnairesByRater()
	
	public String[] resetSurvey(int surveyID)
	{
		
		Vector v=new Vector();
		Connection con = null;
		Statement st = null;
		
		String[] error = new String[1];
		try {
			String query = "delete from tblAvgmean where surveyID = " +surveyID +";";
			query += "update tblAssignment set calculationstatus=0 where surveyID = " + surveyID;
			
		 	          

			  		con=ConnectionBean.getConnection();
			  		st=con.createStatement();
			  		st.executeQuery(query);
		} catch (SQLException SE) {
			System.err.println("QuestionnaireReport.java - resetSurvey - "+SE.getMessage());
			error[0] = SE.getMessage();
		}finally{
			
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		return error;

		
	}
	
	public void refresh() {
		setGroupID(0);
		setTargetID(0);
		setRaterID(0);
	}

	public int getRoundID() {
		return RoundID;
	}

	public void setRoundID(int roundID) {
		RoundID = roundID;
	}

	public Boolean getAttachReport() {
		return AttachReport;
	}

	public void setAttachReport(Boolean attachReport) {
		AttachReport = attachReport;
	}

	public int getSurveyIDImpt() {
		return SurveyIDImpt;
	}

	public void setSurveyIDImpt(int surveyIDImpt) {
		SurveyIDImpt = surveyIDImpt;
	}
}