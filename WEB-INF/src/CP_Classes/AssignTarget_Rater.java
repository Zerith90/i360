package CP_Classes;

import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.text.*;
import java.lang.String;

import com.caucho.quercus.lib.spl.Serializable;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voCompetency;
import CP_Classes.vo.voUser;
import CP_Classes.vo.voDepartment;
import CP_Classes.vo.votblAssignment;
import CP_Classes.vo.votblSurvey;
import CP_Classes.vo.votblSurveyRelationSpecific;

/**
 * This class implements all the operations for Target Rater table in the database.
 */

/**
 * 
 * Change Log
 * ==========
 *
 * Date        By				Method(s)            								Change(s) 
 * =========================================================================================================================================
					 
 * 06/06/12   Liu Taichen       editAssignment(int, String, int)                -Created method to edit of rater relation
 * 
 * 06/06/12   Liu Taichen       getAssignment(int)                         -Created method to retrieve details of an assignment
 * 
 * 25/06/12	  Albert			getAllRaterDepartment()						-created to get all distinct departments of raters
 * 								getTotalRaters()							- created to retrieve total number of raters based on department
 * 								getTotalRatersIncomplete()					- created to retrieve total number of raters that havent completed the survey based on department
 *
 * 27/07/2012 Liu Taichen       RaterCode()									enabled setting rater code for addtional raters
 */
public class AssignTarget_Rater implements java.io.Serializable
{
	/**
	 * Declaration of classes 
	 */
	 
	//private Database db;
	private Setting server;
	private EventViewer ev;
	private Create_Edit_Survey user;
	private User UserList;
	private RaterRelation RtRel;
	private GlobalFunc GFunc; 
	
	/**
	 * Declaration of Variables
	 */
	 
	private String sDetail[] = new String[13];
	private String targetDetail[] = new String[13];
	private String raterDetail[] = new String[13];
 	private String itemName = "Assign";
 	private static int TargetList[];
	
	private int round = 1;
	private int wave = 1;
	private int GroupID = 0;
	private int TargetID = 0;
	private int selAssID = 0;
	private int selTargetID = 0;
	private int NameSequence = 0;
	private int selRaterID = 0;
	private int DeptID = 0;
	private int DivID = 0;
	private int SelectedGroup = 0;
	private int SelectedDept = 0;
	private int SelectedDiv = 0;
	
	public int SortType;
	
	private int Toggle [];	// 0=asc, 1=desc

	/**
	 * Creates a new intance of Create Edit Survey object.
	 */
	 
	public AssignTarget_Rater()
	{
		//db = new Database();
		server = new Setting();
		ev = new EventViewer();
		user = new Create_Edit_Survey();
		UserList = new User();
		RtRel= new RaterRelation();
		GFunc = new GlobalFunc();
		
		Toggle = new int [6];
		
		for(int i=0; i<6; i++)
		{
			Toggle[i] = 0;
		}
		SortType = 1;
	}
	
	public static void main (String[] args)throws SQLException, Exception
	{
		AssignTarget_Rater Rpt = new AssignTarget_Rater();
		//Rpt.FixRaterCode(438, 1);
		//RaterRelation a = new RaterRelation();
		//System.out.println(a.getSpecificID(0));
		//Rpt.insertRaters_SELFandSup(438, 4);
		
		Rpt.FixResultProblem();
		
		//Rpt.FixRaterCode(442, 1);
		
		//Rpt.insertRaters_SELFandSup(438);
		//System.out.println("Supervisor completed = " + Rpt.getNomSupStatus(415, 104));
		//System.out.println("RelHighID = " + Rpt.getRelationHighID(67));
		
		/*	
		TargetList = Rpt.getTargetList(415);
		
		for(int i=0; TargetList[i] != 0; i++)
		{
			System.out.println(TargetList[i]);
		}
		*/	
	}

	/**
	 * This method retrieve the details of all distinct departments of raters
	 * @param iSurveyID - ID of the survey
	 * @throws SQLException
	 * @throws Exception
	 * @author Albert
	 * Created on: 25 June 2012
	 */
	public Vector getAllRaterDepartment(int iSurveyID) throws SQLException, Exception{
		Vector dept = new Vector();
		
		String sql = "SELECT Distinct PKDepartment,DepartmentName FROM tblAssignment INNER JOIN [User] ON tblAssignment.RaterLoginID = [User].PKUser INNER JOIN Department ON Department.PKDepartment = [User].FKDepartment";
		sql += " WHERE tblAssignment.SurveyID = " +iSurveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				while(rs.next()){
					voDepartment vo = new voDepartment();
					vo.setPKDepartment(rs.getInt("PKDepartment"));
					vo.setDepartmentName(rs.getString("DepartmentName"));
					dept.add(vo);
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAllRaterDepartment - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return dept;
	}

	/**
	 * This returns number of total raters based on a department
	 * @param iSurveyID - ID of the survey
	 * @param iDepartmentID - Id of the department
	 * @throws SQLException
	 * @throws Exception
	 * @author Albert
	 * Created on: 25 June 2012
	 */
	public double getTotalRaters(int iSurveyID, int iDepartmentID) throws SQLException, Exception{
		double total = 0.0;
		
		String sql = "SELECT [User].FKDepartment, Count(tblAssignment.RaterStatus) AS Total FROM tblAssignment INNER JOIN [User] ON tbl.Assignment.RaterLoginID = [User].PKUser";
		sql += " WHERE tblAssignment.SurveyID = " +iSurveyID+" AND [User].FKDepartment = "+iDepartmentID;
		sql += " GROUP BY [User].FKDepartment";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				if(rs.next()){
					total = rs.getDouble("Total");
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTotalRaters - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	/**
	 * This returns number of total raters that havent completed the survey based on a department
	 * @param iSurveyID - ID of the survey
	 * @param iDepartmentID - Id of the department
	 * @throws SQLException
	 * @throws Exception
	 * @author Albert
	 * Created on: 25 June 2012
	 */
	public double getTotalRatersIncomplete(int iSurveyID, int iDepartmentID) throws SQLException, Exception{
		double total = 0.0;
		
		String sql = "SELECT [User].FKDepartment, Count(tblAssignment.RaterStatus) AS Total FROM tblAssignment INNER JOIN [User] ON tbl.Assignment.RaterLoginID = [User].PKUser";
		sql += " WHERE tblAssignment.SurveyID = " +iSurveyID+" AND [User].FKDepartment = "+iDepartmentID+" AND tblAssignment.RaterStatus = 0";
		sql += " GROUP BY [User].FKDepartment";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				if(rs.next()){
					total = rs.getDouble("Total");
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTotalRaters - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	/**
	 * This method retrieve the details of all distinct departments of raters
	 * @param iSurveyID - ID of the survey
	 * @param iRound - round
	 * @throws SQLException
	 * @throws Exception
	 * @author Albert
	 * Created on: 25 June 2012
	 */	
	public Vector getAllRaterDepartment(int iSurveyID, int iRound) throws SQLException, Exception{
		Vector dept = new Vector();
		
		String sql = "SELECT Distinct PKDepartment,DepartmentName FROM tblAssignment INNER JOIN [User] ON tblAssignment.RaterLoginID = [User].PKUser INNER JOIN Department ON Department.PKDepartment = [User].FKDepartment";
		sql += " WHERE tblAssignment.SurveyID = " +iSurveyID+ " AND [User].Round = "+iRound;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				while(rs.next()){
					voDepartment vo = new voDepartment();
					vo.setPKDepartment(rs.getInt("PKDepartment"));
					vo.setDepartmentName(rs.getString("DepartmentName"));
					dept.add(vo);
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAllRaterDepartment - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return dept;
	}
	
	/**
	 * This returns number of total raters based on a department
	 * @param iSurveyID - ID of the survey
	 * @param iDepartmentID - Id of the department
	 * @param iRound - round 
	 * @throws SQLException
	 * @throws Exception
	 * @author Albert
	 * Created on: 25 June 2012
	 */
	public double getTotalRaters(int iSurveyID, int iDepartmentID, int iRound) throws SQLException, Exception{
		double total = 0.0;
		
		String sql = "SELECT [User].FKDepartment, Count(tblAssignment.RaterStatus) AS Total FROM tblAssignment INNER JOIN [User] ON tbl.Assignment.RaterLoginID = [User].PKUser";
		sql += " WHERE tblAssignment.SurveyID = " +iSurveyID+" AND [User].FKDepartment = "+iDepartmentID+" AND [User].Round = "+iRound;
		sql += " GROUP BY [User].FKDepartment";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				if(rs.next()){
					total = rs.getDouble("Total");
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTotalRaters - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	/**
	 * This returns number of total raters that havent completed the survey based on a department
	 * @param iSurveyID - ID of the survey
	 * @param iDepartmentID - Id of the department
	 * @param iRound - round 
	 * @throws SQLException
	 * @throws Exception
	 * @author Albert
	 * Created on: 25 June 2012
	 */
	public double getTotalRatersIncomplete(int iSurveyID, int iDepartmentID, int iRound) throws SQLException, Exception{
		double total = 0.0;
		
		String sql = "SELECT [User].FKDepartment, Count(tblAssignment.RaterStatus) AS Total FROM tblAssignment INNER JOIN [User] ON tbl.Assignment.RaterLoginID = [User].PKUser";
		sql += " WHERE tblAssignment.SurveyID = " +iSurveyID+" AND [User].FKDepartment = "+iDepartmentID+" AND tblAssignment.RaterStatus = 0 AND [User].Round = "+iRound;
		sql += " GROUP BY [User].FKDepartment";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				if(rs.next()){
					total = rs.getDouble("Total");
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTotalRaters - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return total;
	}
	
	/**
	 * This method retrieve the details of an assignment
	 * @param AssignmentID - ID of the assignment
	 * @throws SQLException
	 * @throws Exception
	 * @author Liu Taichen
	 * Created on: 6 June 2012
	 */
	
	public votblAssignment getAssignment(int assignmentID){
		votblAssignment vo = new votblAssignment();
		
		
		String sql = "SELECT * FROM tblAssignment WHERE AssignmentID = " +assignmentID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				if(rs.next()){
					vo.setAssignmentID(rs.getInt("AssignmentID"));
				    vo.setSurveyID(rs.getInt("SurveyID"));
				    vo.setTargetLoginID(rs.getInt("TargetLoginID"));
				    vo.setRaterLoginID(rs.getInt("RaterLoginID"));
				    vo.setRTRelation(rs.getInt("RTRelation"));
			        vo.setRTSpecific(rs.getInt("RTSpecific"));
			        vo.setFKTargetDivision(rs.getInt("FKTargetDivision"));
			        vo.setFKTargetDepartment(rs.getInt("FKTargetDepartment"));
			        vo.setFKTargetGroup(rs.getInt("FKTargetGroup"));
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAssignment - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		
		return vo;
	}
	
	
	
	
	/** Get AssignmentID
	*/
	
	
	public int getAssignmentID(int SurveyID, int PKRater, int PKTarget) throws SQLException, Exception 
	{
		/*
		String rel = "SELECT * FROM tblAssignment WHERE SurveyID = " +SurveyID;
		ResultSet rs = db.getRecord(rel);		
		if(rs.next())
			RTRelation  = rs.getInt("RelationID");
		*/	
		
		String rel = "SELECT * FROM tblAssignment WHERE SurveyID = " +SurveyID + " AND TargetLoginID = " + PKTarget + " AND RaterLoginID = " + PKRater;
		/*
		ResultSet rs = db.getRecord(rel);		
		if(rs.next())
			return(rs.getInt("AssignmentID"));
		rs.close();
		*/
		
		int iAssignmentID = 0;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(rel);
			
				if(rs.next())
					iAssignmentID = rs.getInt("AssignmentID");
			
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAssignmentID - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return iAssignmentID;
	}
	
	/*	Import function to insert data into tblAssignment
	 *
	 *	@param int SurveyID
	 *	@param int RelationHighID
	 *	@param int RelationSpecificID
	 *	@param String RaterCode
	 *	@param int PKRater
	 *	@param int PKTarget
	 */
	public boolean ImportAddRecord(int SurveyID, int RTRelation, int RTSpecific, String RaterCode, int RaterID, int TargetID) throws SQLException, Exception 
	{
		int iDetail[] = new int[3];
		iDetail = UserList.getUserFKDetail(TargetID);
			
		String sql = "INSERT INTO tblAssignment (SurveyID, RTRelation, RTSpecific, RaterCode, RaterLoginID, TargetLoginID, FKTargetDivision, FKTargetDepartment, FKTargetGroup) ";
		sql = sql + "VALUES ("+ SurveyID +", "+ RTRelation +", "+ RTSpecific +", '" + RaterCode + "', "+ RaterID +", "+ TargetID +", "+ iDetail[0] +", "+ iDetail[1] +", "+ iDetail[2] +")";
		/*
		db.openDB();
		PreparedStatement ps = db.con.prepareStatement(sql);
		ps.executeUpdate();
		*/
		Connection con = null;
		Statement st = null;
	
		boolean bIsAdded=false;
  	   try{

  		con=ConnectionBean.getConnection();
  		st=con.createStatement();
  		int iSuccess=st.executeUpdate(sql);
  		if(iSuccess!=0)
  		bIsAdded=true;

		
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - ImportAddRecord - "+ex.getMessage());
		}
		finally{
		
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		//To synchronise all Div, Dept & Group in tblAssignment under specific TargetLoginID
		FixResultProblem(TargetID);
		return bIsAdded;
	}
		
	/** Include SELF & DIRECT SUPERVISOR as Target's Raters for all Surveys
	*
	*	Parameters - int SurveyID
	*
	*/
	public void insertRaters_SELFandSup(int SurveyID) throws SQLException, Exception 
	{
		TargetList = getTargetList(SurveyID);
		
		int counter = 0;
		for(int i=0; TargetList[i] != 0; i++)
		{
			//INSERT RATER (SELF)
			addRater(SurveyID, TargetList[i], TargetList[i], 2, 0, "SELF");
			
			//INSERT RATER (SUPERVISOR)
			int SupPKUser = UserList.getSupPKUser(TargetList[i]);
			if (SupPKUser != 0)
			{
				//RTSpecific = 70 only for TOYOTA, As this function is for TOYOTA only
				
				//System.out.println("CODE = " + RaterCode(SurveyID, 1, 70, TargetList[i]));
				addRater(SurveyID, TargetList[i], SupPKUser, 1, 70, RaterCode(SurveyID, 1, 70, TargetList[i]));
			}
			else
			{
				
				System.out.println("NO Supervisor found for User (" + TargetList[i] + ")");
			}
				
			counter++;
		}
		
		System.out.println("TOTAL " + counter + " RECORDS ADDED");
		
	}
		
	/** Get Target List
	*
	*	Parameters - int SurveyID
	*
	*/
	public int[] getTargetList(int SurveyID) throws SQLException, Exception 
	{
		String command = "SELECT DISTINCT (SELECT DISTINCT COUNT(*) AS totRec FROM tblAssignment) AS TotRec, TargetLoginID FROM tblAssignment WHERE SurveyID = " + SurveyID;
		
		int TargetID[] = new int[10];
		int i = 0;
		/*
		ResultSet rs1 = db.getRecord(command);
		if(rs1.next())
		{
			int TotRec = rs1.getInt("TotRec");
			TargetID = new int[TotRec]; 
			
			TargetID[i] = rs1.getInt("TargetLoginID");
			//System.out.println(TargetID[i]);
			i++;
		}
		
		while(rs1.next())
		{
			TargetID[i] = rs1.getInt("TargetLoginID");
			//System.out.println(TargetID[i]);
			i++;
		}
		
		rs1.close();
		db.closeDB();
		*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
  	   try{


  		 con=ConnectionBean.getConnection();
  		 st=con.createStatement();
  		 rs=st.executeQuery(command);

  		if(rs.next())
		{
			int TotRec = rs.getInt("TotRec");
			TargetID = new int[TotRec]; 
			
			TargetID[i] = rs.getInt("TargetLoginID");
			//System.out.println(TargetID[i]);
			i++;
		}
		
		while(rs.next())
		{
			TargetID[i] = rs.getInt("TargetLoginID");
			//System.out.println(TargetID[i]);
			i++;
		}

		
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTargetList - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		return TargetID;	
	}
	
	/** Get Total Raters
	*
	*	Parameters - int SurveyID, int TargetLoginID
	*
	*/
	
	public int getTotRaters(int SurveyID, int TargetLoginID) throws SQLException, Exception 
	{
		int TotRaters = 0;
		
		String command = "SELECT COUNT(*) AS TotRater FROM tblAssignment WHERE (SurveyID = " + SurveyID + ") ";
		command = command + "AND (TargetLoginID = " + TargetLoginID + ")";
		/*
		ResultSet rs1 = db.getRecord(command);
		if(rs1.next())
			TotRaters = rs1.getInt("TotRater");
		
		rs1.close();
		db.closeDB();
		*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command);

  		   if(rs.next())
  			   TotRaters = rs.getInt("TotRater");
		

		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTotRaters - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return TotRaters;
		
	}
	
	/** Get Total Completed Raters Excluding Self
	*
	* @param SurveyID		Integer
	* @param TargetLoginID	Integer
	*
	* @return TotRatersCompleted		Integer
	*
	*/
	public int getTotRatersCompleted(int SurveyID, int TargetLoginID) throws SQLException, Exception 
	{
		int TotRatersCompleted = 0;
		
		String command = "SELECT COUNT(*) AS TotRatersCompleted FROM tblAssignment WHERE (SurveyID = " + SurveyID + ") ";
		command = command + "AND (TargetLoginID = " + TargetLoginID + ") ";
		command = command + "AND RaterLoginID <> " + TargetLoginID + " AND RaterStatus = 1";
		/*
		ResultSet rs1 = db.getRecord(command);
		if(rs1.next())
			TotRatersCompleted = rs1.getInt("TotRatersCompleted");
		
		rs1.close();
		db.closeDB();
		*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command);

  		   if(rs.next())
  			   TotRatersCompleted = rs.getInt("TotRatersCompleted");
	
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTotRatersCompleted - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return TotRatersCompleted;
		
	}
	
	
	/** Get Total Completed Raters Excluding Self
	*
	* @param SurveyID		int
	* @param TargetLoginID	int
	* @param GroupSect		int
	* @param DeptID			int
	* @param DivID			int
	*
	* @return TotRatersCompleted		Integer
	*
	*/
	public int getTotRatersCompleted(int SurveyID, int GroupSect, int DeptID, int DivID) throws SQLException, Exception 
	{
		int TotRatersCompleted = 0;
		
		String command = "SELECT COUNT(*) as TotRatersCompleted FROM tblAssignment INNER JOIN ";
		command = command + "[User] ON tblAssignment.TargetLoginID = [User].PKUser ";
		command = command + "WHERE (tblAssignment.SurveyID = " + SurveyID + ") AND (tblAssignment.RaterStatus = 1) ";
		command = command + "AND (tblAssignment.RaterCode NOT LIKE '%self%') ";
		
		if (GroupSect != 0)
			command = command + "AND (tblAssignment.FKTargetGroup = '" + GroupSect + "') ";
			
		if (DeptID != 0)
			command = command + "AND (tblAssignment.FKTargetDepartment = " + DeptID + ") ";

		if (DivID != 0)
			command = command + "AND (tblAssignment.FKTargetDivision = " + DivID + ")";
		/*
		ResultSet rs1 = db.getRecord(command);
		if(rs1.next())
			TotRatersCompleted = rs1.getInt("TotRatersCompleted");
		
		rs1.close();
		db.closeDB();
		*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(command);

  	   		if(rs.next())
  	   			TotRatersCompleted = rs.getInt("TotRatersCompleted");
		
		

  	   	}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTotRatersCompleted - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		return TotRatersCompleted;
	}
	
	
	/** Get Supervisor Nomination complete Status
	*	As long as one target have not have 6 raters, reminder email still will be sent
	*	Parameters - int SurveyID, int SupID
	*
	*/
	public int getNomSupStatus(int SurveyID, int SupID) throws SQLException, Exception 
	{
		int CompleteStatus = 1; //0=incomplete, 1=complete
		int counter = 0;
		
		// Get Target list under this Supervisor
		Vector vSupTargetList = getSupTargetList(SurveyID, SupID);
		
		//while(rs1.next())
		for(int i=0;i<vSupTargetList.size();i++)
		{
			votblAssignment vo=(votblAssignment)vSupTargetList.elementAt(i);
			counter++;
			int TargetLoginID = vo.getTargetLoginID();
			//System.out.println("TargetLoginID = " + TargetLoginID);
			//System.out.println("Total Raters = " + getTotRaters(SurveyID, TargetLoginID));
			
			// Loop the target list and get completion status (rater = 6)
			if(getTotRaters(SurveyID, TargetLoginID) != 6)
			{
				CompleteStatus = 0;
				//System.out.println("CompleteStatus = " + CompleteStatus);
				break;
			}

		}
		
		if (counter == 0)
			CompleteStatus = 0;
			
		//System.out.println("FINAL CompleteStatus = " + CompleteStatus);
		//db.closeDB();
		return CompleteStatus;
		
	}
	
	/** Get Supervisor's Target List (Direct Reports list who are participating the survey)
	*
	*	Parameters - int SurveyID, int SupID
	*
	*/
	
	public Vector getSupTargetList(int SurveyID, int SupID) throws SQLException, Exception 
	{
		Vector v=new Vector();
		
		// Get Target list under this Supervisor
		String command = "SELECT DISTINCT tblAssignment.TargetLoginID FROM tblAssignment INNER JOIN ";
		command = command + "tblUserRelation ON tblAssignment.TargetLoginID = tblUserRelation.User1 ";
		command = command + "WHERE (tblUserRelation.User2 = " + SupID + ") AND (tblAssignment.SurveyID = " + SurveyID + ")";
		/*
		ResultSet rs1 = db.getRecord(command);
		*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
  	   try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command);

  		   while(rs.next()){
  			   votblAssignment vo=new votblAssignment();
  			   vo.setTargetLoginID(rs.getInt("TargetLoginID"));
  			   v.add(vo);
  			}
			

		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getSupTargetList - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	public Vector<Integer> getAllRound(int surveyID){
		Vector<Integer> rounds = new Vector<Integer>();
		String sql= "select distinct round from tblAssignment where SurveyID = " +surveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(sql);

  		   while(rs.next())
  			    rounds.add(rs.getInt("round"));
			

		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAllRound - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return rounds;
	}
	
	public Vector<Integer> getAllWave(int surveyID){
		Vector<Integer> waves = new Vector<Integer>();
		String sql = "select distinct wave from tblAssignment where SurveyID = " + surveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			
			while(rs.next())
				waves.add(rs.getInt("wave"));
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAllWave - "+ex.getMessage());
		}finally{
			ConnectionBean.closeRset(rs);
			ConnectionBean.closeStmt(st);
			ConnectionBean.close(con);
		}
		return waves;
	}
	
	public int getNewRound(int surveyID){
		String sql = "select max(round) as round from tblAssignment where SurveyID = "+surveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			
			if(rs.next())
				return rs.getInt("round")+1;
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getNewRound - " + ex.getMessage());
		}finally{
			ConnectionBean.closeRset(rs);
			ConnectionBean.closeStmt(st);
			ConnectionBean.close(con);
		}
		return 1;
	}
	
	public int getNewWave(int surveyID){
		String sql = "select max(wave) as wave from tblAssignment where SurveyID = "+surveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			
			if(rs.next())
				return rs.getInt("wave")+1;
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getNewWave - " + ex.getMessage());
		}finally{
			ConnectionBean.closeRset(rs);
			ConnectionBean.closeStmt(st);
			ConnectionBean.close(con);
		}
		return 1;
	}
	
	public int getRound(int targetID){
		int round = 1;
		String sql = "select round from tblAssignment where TargetLoginID = " + targetID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()){
				round = rs.getInt("round");
			}	
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getRoundAndWave - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return round;
	}
	
	/** Get Relation High ID based on Relation Specific ID
	*	Parameters - int RelSpecID
	*/
	public int getRelationHighID(int RelSpecID) throws SQLException, Exception 
	{
		int RelHighID = 0;
		/*
		*Change(s): Use class SurveyRelationSpecific to manage relation specifics
		*Reason(s): To associate relation specific to survey instead of organization
		*Updated by: Liu Taichen
		*Updated on: 5 June 2012
		*/
		// Get Relation High ID based on Relation Specific ID
		String command = "SELECT RelationID from tblSurveyRelationSpecific WHERE SpecificID = " + RelSpecID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
		try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command);

  		   if(rs.next())
  			   RelHighID = rs.getInt("RelationID");
			

		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getRelationHighID - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return RelHighID;
	}
	
	/** Get AssignmentID
	*/
	public int getAssignmentID(int SurveyID, int PKTarget) throws SQLException, Exception 
	{
		int iAssignmentID = 0;
		
		String rel = "SELECT * FROM tblAssignment WHERE SurveyID = " +SurveyID + " AND TargetLoginID = " + PKTarget;
		/*
		ResultSet rs = db.getRecord(rel);		
		if(rs.next())
			iAssignmentID = rs.getInt("AssignmentID");
		
		rs.close();*/
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(rel);
  	   		if(rs.next())
  	   			iAssignmentID = rs.getInt("AssignmentID");
		

  	   	}catch(Exception ex){
  	   		System.out.println("AssignTarget_Rater.java - getAssignmentID - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return iAssignmentID;
	}
	
	
	/*------------------------------------------start Target Menu-----------------------------------------------*/
	
    /**
     * This method adds a Target for the given Survey into the database. It will also automatically
     * add the Target as a Rater or his/her superior if the isAutoSelf and isAutoSup parameters are True.
     * 
     * NOTE: Add operation is captured into Event Log
     * 
     * @param iSurveyID - The Survey ID
     * @param iPKTarget - The Target ID
     * @param iPKUser 	- The User ID of the User who add the Target
     * @return A boolean which indicates if the add operation is successful
     * @throws SQLException
     * @throws Exception
     * Change Log
     * ==========
     * >> 26 Jun 2008 By Chun Pong --> Replaced getSpecificID() with getDirectSupSpecificID()
     *                             --> Renamed TargerLoginID to iPKTarget and change type to int
     *                             --> Replaced Selecy Query with getSurveyDetail()
     *                             --> Updated Insert Query from a Statement object to a PreparedStatement
     *                             --> Updated Event Logging
     *                             --> Remove old DB code
     * >> 19 Jun 2008 By DeZ       --> Added automatic adding of Self and direct superior as rater 
     * >> 20 May 2008 By Ha        --> Return Type change from void to boolean
     */
	public boolean addTarget(int iSurveyID, int iPKTarget, int iPKUser) throws SQLException, Exception {
		String sInsertQuery = "INSERT INTO tblAssignment (SurveyID, TargetloginID, " +
							  "FKTargetDivision, FKTargetDepartment, FKTargetGroup) VALUES (?, ?, ?, ?, ?)";
		
		//Added boolean to indicate if the add operation is successful
		//By: Ha
		//Date: 20 May 2008
		boolean isAdded = false;  
		                
        //Connection, Statement and 
		Connection con = null;	
		//Replaced the exisiting Statement object with a PreparedStatement
		//By: Chun Pong
		//Date: 26 May 2008
		PreparedStatement psInsert = null;
		ResultSet rs = null;
		
		//User FK Details
		int iDetail[] = new int[3];
		
		//Remove Select query to retrieve Survey Name with a votblSurvey object
		//By: Chun Pong
		//Date: 26 May 2008
		votblSurvey voSurvey = null;
		
                //Added boolean to indicate if the Auto add Self and Superior rater options
		//By: DeZ
		//Date: 19 Jun 2008
                boolean isAutoSelf = false;
                boolean isAutoSup = false;
                
		//Start of Try-Catch
		try{
			//Get DB Connection
			con = ConnectionBean.getConnection();
			
			//Get Survey Details
			//By: Chun Pong
			//Date: 26 May 2008
			voSurvey = user.getSurveyDetail(iSurveyID);
			
			//Get User FK Details
			iDetail = UserList.getUserFKDetail(iPKTarget);
			
			//Initial psInsert and set its parameters
			//By: Chun Pong
			//Date: 26 May 2008
			psInsert = con.prepareStatement(sInsertQuery);
			//Set Prepared Statement Parameters
			psInsert.setInt(1, iSurveyID);
			psInsert.setInt(2, iPKTarget);
			psInsert.setInt(3, iDetail[0]);
			psInsert.setInt(4, iDetail[1]);
			psInsert.setInt(5, iDetail[2]);
			
	  		psInsert.executeUpdate();
			
	  		//Shifted from after the excution of psSelect
			//By: DeZ
			//Date: 19 Jun 2008
	  		isAdded = true;
	  		
	  		//Old code for fixing FK problems, no longer needed
			//By: DeZ
			//Date: 19 Jun 2008
	  		//FixResultProblem(Integer.parseInt(TargetLoginID));
	  		
                        //Added to retrieve current survey advanced settings for automatic assignment of rater
			//By: DeZ
			//Date: 01 July 2008
                        isAutoSelf = voSurvey.getAutoSelf();
                        isAutoSup = voSurvey.getAutoSup();
                        
            //Add Rater(Self) if isAutoSelf is true
	  		//By: DeZ
			//Date: 19 Jun 2008
            if(isAutoSelf) {                
                addRater(iSurveyID, iPKTarget, iPKTarget, 2, 0, "SELF");
                System.out.println("AssignTarget_Rater.java - addTarget(s params) - Added Self [ID:" + 
                				   iPKTarget + "] as Rater");
            } //End of Add Rater(Self)
            
            //Add Rater(Superior) if isAutoSup is true
	  		//By: DeZ
			//Date: 19 Jun 2008
            //INSERT RATER (SUPERVISOR) if enabled (true)
            if(isAutoSup) {
            	//Get Superior ID
                int iSupPKUser = UserList.getSupPKUser(iPKTarget);
                int iDirectSupSpecificID = RtRel.getDirectSupSpecificID(voSurvey.getFKCompanyID(), 
                														voSurvey.getFKOrganization());
                String sRaterCode = RaterCode(iSurveyID, 1, iDirectSupSpecificID, iPKTarget);
                
                //Superior Exists Check
                if (iSupPKUser != 0) {
                    addRater(iSurveyID, iPKTarget, iSupPKUser, 1, iDirectSupSpecificID, sRaterCode, 
                    	     iPKTarget);
                    System.out.println("AssignTarget_Rater.java - addTarget(s params) - Added Superior [ID:" + 
         				   			   iPKTarget + "] as Rater for Target [ID:" + iPKUser + "]");
                } else {
                    System.out.println("AssignTarget_Rater.java - addTarget(s params) - No Superior found for Target [ID:" + iPKTarget + "]");
                } //End of Superior Exists Check
            } //End of Add Rater(Superior)
		}catch(Exception e){
			System.err.println("AssignTarget_Rater.java - addTarget(s params) - " + e.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closePStmt(psInsert); //Close statement
			ConnectionBean.close(con); //Close connection
			
			//Updated Event Log message
			//By: Chun Pong
			//Date: 26 Jun 2008
			String sEventLogMessage = "Add Target [ID:" + iPKTarget + "] ";
			if(isAutoSelf && !isAutoSup)
				sEventLogMessage += "with Auto Self ";
			else if(!isAutoSelf && isAutoSup)
				sEventLogMessage += "with Auto Superior ";
			else if(isAutoSelf && isAutoSup)
				sEventLogMessage += "with Auto Self and Auto Superior ";
			
			if(isAdded)				
				sEventLogMessage += "Successfully";
			else
				sEventLogMessage += "Failed";
			
			//Get the User Information of the user who perform the add target operation 
			voUser voUsr = UserList.getUserInfo(iPKUser);	
			//Event Logging
			ev.addRecord("Add Target", "Add Target - 3 Params", sEventLogMessage, voUsr.getLoginName(), voSurvey.getCompanyName(), voSurvey.getOrganizationName());
		} //End of Try-Catch
		return isAdded;
	} //End of addTarget() - 3 params
	
	
	public boolean addTarget(int iSurveyID, int iPKTarget, int iPKUser, int round) throws SQLException, Exception {
		String sInsertQuery = "INSERT INTO tblAssignment (SurveyID, TargetloginID, " +
							  "FKTargetDivision, FKTargetDepartment, FKTargetGroup, round) VALUES (?, ?, ?, ?, ?, ?)";
		
		//Added boolean to indicate if the add operation is successful
		//By: Ha
		//Date: 20 May 2008
		boolean isAdded = false;  
		                
        //Connection, Statement and 
		Connection con = null;	
		//Replaced the exisiting Statement object with a PreparedStatement
		//By: Chun Pong
		//Date: 26 May 2008
		PreparedStatement psInsert = null;
		ResultSet rs = null;
		
		//User FK Details
		int iDetail[] = new int[3];
		
		//Remove Select query to retrieve Survey Name with a votblSurvey object
		//By: Chun Pong
		//Date: 26 May 2008
		votblSurvey voSurvey = null;
		
                //Added boolean to indicate if the Auto add Self and Superior rater options
		//By: DeZ
		//Date: 19 Jun 2008
                boolean isAutoSelf = false;
                boolean isAutoSup = false;
                
		//Start of Try-Catch
		try{
			//Get DB Connection
			con = ConnectionBean.getConnection();
			
			//Get Survey Details
			//By: Chun Pong
			//Date: 26 May 2008
			voSurvey = user.getSurveyDetail(iSurveyID);
			
			//Get User FK Details
			iDetail = UserList.getUserFKDetail(iPKTarget);
			
			//Initial psInsert and set its parameters
			//By: Chun Pong
			//Date: 26 May 2008
			psInsert = con.prepareStatement(sInsertQuery);
			//Set Prepared Statement Parameters
			psInsert.setInt(1, iSurveyID);
			psInsert.setInt(2, iPKTarget);
			psInsert.setInt(3, iDetail[0]);
			psInsert.setInt(4, iDetail[1]);
			psInsert.setInt(5, iDetail[2]);
			psInsert.setInt(6, round);
	  		psInsert.executeUpdate();
			
	  		//Shifted from after the excution of psSelect
			//By: DeZ
			//Date: 19 Jun 2008
	  		isAdded = true;
	  		
	  		//Old code for fixing FK problems, no longer needed
			//By: DeZ
			//Date: 19 Jun 2008
	  		//FixResultProblem(Integer.parseInt(TargetLoginID));
	  		
                        //Added to retrieve current survey advanced settings for automatic assignment of rater
			//By: DeZ
			//Date: 01 July 2008
                        isAutoSelf = voSurvey.getAutoSelf();
                        isAutoSup = voSurvey.getAutoSup();
                        
            //Add Rater(Self) if isAutoSelf is true
	  		//By: DeZ
			//Date: 19 Jun 2008
            if(isAutoSelf) {                
                addRater(iSurveyID, iPKTarget, iPKTarget, 2, 0, "SELF");
                System.out.println("AssignTarget_Rater.java - addTarget(s params) - Added Self [ID:" + 
                				   iPKTarget + "] as Rater");
            } //End of Add Rater(Self)
            
            //Add Rater(Superior) if isAutoSup is true
	  		//By: DeZ
			//Date: 19 Jun 2008
            //INSERT RATER (SUPERVISOR) if enabled (true)
            if(isAutoSup) {
            	//Get Superior ID
                int iSupPKUser = UserList.getSupPKUser(iPKTarget);
                int iDirectSupSpecificID = RtRel.getDirectSupSpecificID(voSurvey.getFKCompanyID(), 
                														voSurvey.getFKOrganization());
                String sRaterCode = RaterCode(iSurveyID, 1, iDirectSupSpecificID, iPKTarget);
                
                //Superior Exists Check
                if (iSupPKUser != 0) {
                    addRater(iSurveyID, iPKTarget, iSupPKUser, 1, iDirectSupSpecificID, sRaterCode, 
                    	     iPKTarget);
                    System.out.println("AssignTarget_Rater.java - addTarget(s params) - Added Superior [ID:" + 
         				   			   iPKTarget + "] as Rater for Target [ID:" + iPKUser + "]");
                } else {
                    System.out.println("AssignTarget_Rater.java - addTarget(s params) - No Superior found for Target [ID:" + iPKTarget + "]");
                } //End of Superior Exists Check
            } //End of Add Rater(Superior)
		}catch(Exception e){
			System.err.println("AssignTarget_Rater.java - addTarget(s params) - " + e.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closePStmt(psInsert); //Close statement
			ConnectionBean.close(con); //Close connection
			
			//Updated Event Log message
			//By: Chun Pong
			//Date: 26 Jun 2008
			String sEventLogMessage = "Add Target [ID:" + iPKTarget + "] ";
			if(isAutoSelf && !isAutoSup)
				sEventLogMessage += "with Auto Self ";
			else if(!isAutoSelf && isAutoSup)
				sEventLogMessage += "with Auto Superior ";
			else if(isAutoSelf && isAutoSup)
				sEventLogMessage += "with Auto Self and Auto Superior ";
			
			if(isAdded)				
				sEventLogMessage += "Successfully";
			else
				sEventLogMessage += "Failed";
			
			//Get the User Information of the user who perform the add target operation 
			voUser voUsr = UserList.getUserInfo(iPKUser);	
			//Event Logging
			ev.addRecord("Add Target", "Add Target - 3 Params", sEventLogMessage, voUsr.getLoginName(), voSurvey.getCompanyName(), voSurvey.getOrganizationName());
		} //End of Try-Catch
		return isAdded;
	} //End of addTarget() - 3 params
	
    /**
     * This method adds a Target for the given Survey into the database. 
     * @param iSurveyID - The Survey ID
     * @param iPKTarget - The Target ID
     * @return A boolean which indicates if the add operation is successful
     * @throws SQLException
     * @throws Exception
     * 
     * Change Log
     * ==========
     * >> 26 Jun 2008 By Chun Pong --> Clear up code
     * >> 19 Jun 2008 By DeZ       --> Return Type change from void to boolean  
     */
	public boolean addTarget(int iSurveyID, int iPKTarget) throws SQLException, Exception {	
		String sInsertQuery = "INSERT INTO tblAssignment (SurveyID, TargetloginID, " +
		  					  "FKTargetDivision, FKTargetDepartment, FKTargetGroup) VALUES (?, ?, ?, ?, ?)";
		
		//Added boolean to indicate if the add operation is successful
		//By: Ha
		//Date: 20 May 2008
		boolean isAdded = false;
		
		//Connection, Statement and 
		Connection con = null;	
		//Replaced the exisiting Statement object with a PreparedStatement
		//By: Chun Pong
		//Date: 26 May 2008
		PreparedStatement psInsert = null;
		ResultSet rs = null;
		
		//User FK Details
		int iDetail[] = new int[3];
		
		//Start of Try-Catch
		try{
		//Get DB Connection
		con = ConnectionBean.getConnection();
		
		//Get User FK Details
		iDetail = UserList.getUserFKDetail(iPKTarget);
		
		//Initial psInsert and set its parameters
		//By: Chun Pong
		//Date: 26 May 2008
		psInsert = con.prepareStatement(sInsertQuery);
		//Set Prepared Statement Parameters
		psInsert.setInt(1, iSurveyID);
		psInsert.setInt(2, iPKTarget);
		psInsert.setInt(3, iDetail[0]);
		psInsert.setInt(4, iDetail[1]);
		psInsert.setInt(5, iDetail[2]);
		
		psInsert.executeUpdate();
		
		//Shifted from after the excution of psSelect
		//By: DeZ
		//Date: 19 Jun 2008
		isAdded = true;
		
		//Old code for fixing FK problems, no longer needed
		//By: DeZ
		//Date: 19 Jun 2008
		//FixResultProblem(Integer.parseInt(TargetLoginID));
		}catch(Exception e){
			System.err.println("AssignTarget_Rater.java - addTarget(2 params) - " + e.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closePStmt(psInsert); //Close statement
			ConnectionBean.close(con); //Close connection
		} //End of Try-Catch
		return isAdded;
	} //End of addTarget() - 2 Params
	
	/** Delete Target
	*
	*	Parameters - int SurveyID, String TargetLoginID, int PKUser
	*
	*	Record will be deleted from tblAssignment
	*
	*	Event viewer will capture this event.
	*/
	
	public boolean delTarget(int SurveyID, int TargetLoginID, int PKUser) throws SQLException, Exception 
	{
		boolean bIsDeleted=false;
		
		String OldName = "";
		String command = "SELECT * FROM tblSurvey WHERE SurveyID ="+SurveyID;
	
		/*
		ResultSet rs1 = db.getRecord(command);
		if(rs1.next())
			OldName = rs1.getString("SurveyName");
		
		rs1.close();
	     db.openDB();*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps =null;
  	   
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(command);
			
			if(rs.next())
				OldName = rs.getString("SurveyName");
		
   		}catch(Exception ex){
   			System.out.println("AssignTarget_Rater.java - delTarget - "+ex.getMessage());
   		}
   		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
   		
		String sql = "DELETE FROM tblAssignment WHERE TargetLoginID= "+TargetLoginID+" AND SurveyID ="+SurveyID;
		
		//PreparedStatement ps = db.con.prepareStatement(sql);
		//ps.execute();
		//db.closeDB();
		try{
	  		 ps=con.prepareStatement(sql);
	  		 
	  		int iSuccess = ps.executeUpdate();
	  		
			if(iSuccess!=0)
				bIsDeleted=true;
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - delTarget - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closePStmt(ps);
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		sDetail = user.getUserDetail(PKUser);
		targetDetail = user.getUserDetail(TargetLoginID);
		try {
			ev.addRecord("Delete", itemName,  targetDetail[0]+", "+targetDetail[1]+"(Target) for Survey "+OldName, sDetail[2], sDetail[11], sDetail[10]);
		} catch(SQLException SE) {}
		return bIsDeleted;
		
	}
	
	/** Delete Target and Rater
	*
	*	Parameters - String int PKUser
	*
	*	Record of the PKUser will be deleted from tblAssignment
	*/
	public boolean delTargetAndRater(int PKUser) throws SQLException, Exception 
	{  boolean bIsDeleted =false ;
		//db.openDB();
		String sql = "DELETE FROM tblAssignment WHERE (RaterLoginID = " + PKUser + ") OR (TargetLoginID = " + PKUser + ")";
		Connection con = null;
		PreparedStatement ps =null;
  	   try{
  		   con=ConnectionBean.getConnection();
  		   ps=con.prepareStatement(sql);
  		   int iSuccess=ps.executeUpdate();
  		   
		if(iSuccess!=0)
  			 bIsDeleted=true;
		
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - delTarget - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closePStmt(ps); 
		ConnectionBean.close(con); //Close connection
		}
		
		/*
		PreparedStatement ps = db.con.prepareStatement(sql);
		ps.execute();
		db.closeDB();
		*/
		
		System.out.println("Rater and Target's records of PKUser=" + PKUser + ", has been deleted");
		return bIsDeleted;
	}
	
	/** Get Target's Division name under specific SurveyID
	*	@param int SurveyID
	*	@param int TargetID
	*	
	*	@return	String	DivisionName
	*/
	public String getTargetDivision(int SurveyID, int TargetID) throws SQLException, Exception 
	{
		String DivisionName = "";
		String command = "SELECT Division.DivisionName FROM tblSurvey INNER JOIN ";
      	command += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
       	command += "Division ON tblAssignment.FKTargetDivision = Division.PKDivision ";
		command += "WHERE (tblSurvey.SurveyID = " + SurveyID + ") AND (tblAssignment.TargetLoginID = " + TargetID + ")";
		/*
		ResultSet rs1 = db.getRecord(command);
		if(rs1.next())
			DivisionName = rs1.getString("DivisionName");
		
		rs1.close();*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		 st=con.createStatement();
  		 rs=st.executeQuery(command);
  		if(rs.next())
			DivisionName = rs.getString("DivisionName");
		
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTargetDivision - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		return DivisionName;
	}
	
	/**	Delete Target's results/scores from tblAvgMean
	 *	@param int SurveyID
	 *	@param int TargetID
	 */
	public boolean delTargetsResult(int SurveyID, int TargetLoginID) throws SQLException, Exception 
	{
		boolean bIsDeleted=false;
		String sql = "DELETE FROM tblAvgMean WHERE TargetLoginID= "+TargetLoginID+" AND SurveyID ="+SurveyID;
		/*
		PreparedStatement ps = db.con.prepareStatement(sql);
		ps.execute();
		db.closeDB();*/
		Connection con = null;
		PreparedStatement ps =null;
  	   	
		try{
  		   	con=ConnectionBean.getConnection();
  		   	ps=con.prepareStatement(sql);
  		   	int iSuccess=ps.executeUpdate();

			if(iSuccess!=0)
				 bIsDeleted=true;
		
  	   	}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - delTargetsResult - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closePStmt(ps); 
			ConnectionBean.close(con); //Close connection
		}
		return bIsDeleted;
	}
	
	
	/** Get Target's Department name under specific SurveyID
	*	@param int SurveyID
	*	@param int TargetID
	*	
	*	@return	String	DepartmentName
	*/
	public String getTargetDepartment(int SurveyID, int TargetID) throws SQLException, Exception 
	{
		String DepartmentName = "";
		String command = "SELECT Department.DepartmentName FROM tblSurvey INNER JOIN ";
      	command += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
       	command += "Department ON tblAssignment.FKTargetDepartment = Department.PKDepartment ";
		command += "WHERE (tblSurvey.SurveyID = " + SurveyID + ") AND (tblAssignment.TargetLoginID = " + TargetID + ")";
		/*
		ResultSet rs1 = db.getRecord(command);
		if(rs1.next())
			DepartmentName = rs1.getString("DepartmentName");
		
		rs1.close();
		*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		 st=con.createStatement();
  		 rs=st.executeQuery(command);
  		if(rs.next())
			DepartmentName = rs.getString("DepartmentName");
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTargetDepartment - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		return DepartmentName;
	}
	
	/** Get Target's Group name under specific SurveyID
	*	@param int SurveyID
	*	@param int TargetID
	*	
	*	@return	String	GroupName
	*/
	public String getTargetGroup(int SurveyID, int TargetID) throws SQLException, Exception 
	{
		String GroupName = "";
		String command = "SELECT [Group].GroupName FROM tblSurvey INNER JOIN ";
      	command += "tblAssignment ON tblSurvey.SurveyID = tblAssignment.SurveyID INNER JOIN ";
       	command += "[Group] ON tblAssignment.FKTargetGroup = [Group].PKGroup ";
		command += "WHERE (tblSurvey.SurveyID = " + SurveyID + ") AND (tblAssignment.TargetLoginID = " + TargetID + ")";
		/*
		ResultSet rs1 = db.getRecord(command);
		if(rs1.next())
			GroupName = rs1.getString("GroupName");
		
		rs1.close();
		*/
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		 st=con.createStatement();
  		 rs=st.executeQuery(command);
  		if(rs.next())
			GroupName = rs.getString("GroupName");
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java -getTargetGroup - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		
		return GroupName;
	}
	
	/*------------------------------------------end Target Menu-----------------------------------------------*/
	
	
	/*------------------------------------------start Rater Menu-----------------------------------------------*/
	
	/** Fix Existing tblAssignment constant changing group report result problem 
	 *
	 *	This function is to insert "ALL" existing FKTargetDivision, FKTargetDepartment, FKTargetGroup data from
	 *	tblUser to tblAssignment
	 *	
	 */
	public void FixResultProblem() throws SQLException, Exception 
	{
		//db.openDB();
		String command;
		String sql;
		
		command = "SELECT DISTINCT TargetLoginID FROM tblAssignment WHERE TargetLoginID <> 0 ORDER BY TargetLoginID";
			
		//ResultSet rs = db.getRecord(command);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps=null;
		
  	   try{
  		   con=ConnectionBean.getConnection();
  		 st=con.createStatement();
  		 rs=st.executeQuery(command);
  		while(rs.next())
		{
			int iDetail[] = new int[3];
			iDetail = UserList.getUserFKDetail(rs.getInt("TargetLoginID"));
			
			if(iDetail[0] != 0 && iDetail[1] != 0 && iDetail[2] != 0)
			{
				//Update
				sql = "UPDATE tblAssignment SET FKTargetDivision = " + iDetail[0] + ", FKTargetDepartment = ";
				sql = sql + iDetail[1] + ", FKTargetGroup = " + iDetail[2] + " WHERE TargetLoginID = " + rs.getInt("TargetLoginID");
				ps = con.prepareStatement(sql);
				ps.executeUpdate();
				System.out.println("UPDATED FOR Target: " + rs.getInt("TargetLoginID") + ", 1:" + iDetail[0] + ", 2:" + iDetail[1] + ", 3:" + iDetail[2]);
			}
		}
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - FixResultProblem - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closePStmt(ps);
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
	
	}
	
	/** Fix Existing tblAssignment constant changing group report result problem 
	 *
	 *	This function is to update "ALL" existing FKTargetDivision, FKTargetDepartment, FKTargetGroup data from
	 *	tblUser to tblAssignment under specific "TargetID"
	 *	
	 */
	public void FixResultProblem(int TargetID) throws SQLException, Exception 
	{
		//db.openDB();
		String sql;
		Connection con = null;
		Statement st = null;
		PreparedStatement ps=null;

		int iDetail[] = new int[3];
		iDetail = UserList.getUserFKDetail(TargetID);
		
		if(iDetail[0] != 0 && iDetail[1] != 0 && iDetail[2] != 0)
		{
			try{
				con=ConnectionBean.getConnection();
				st=con.createStatement();

				//Update
				sql = "UPDATE tblAssignment SET FKTargetDivision = " + iDetail[0] + ", FKTargetDepartment = ";
				sql = sql + iDetail[1] + ", FKTargetGroup = " + iDetail[2] + " WHERE TargetLoginID = " + TargetID;
				ps = con.prepareStatement(sql);
				ps.executeUpdate();
				System.out.println("UPDATED FOR Target: " + TargetID + ", 1:" + iDetail[0] + ", 2:" + iDetail[1] + ", 3:" + iDetail[2]);
			
			}catch(Exception ex){
				System.out.println("AssignTarget_Rater.java - FixResultProblem - "+ex.getMessage());
			}
			finally{
				ConnectionBean.closePStmt(ps);
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		}
	}
		
	/** Fix Existing tblAssignment RaterCode
	 *	
	 *	Type:
	 *	0. Reorder all the Rater Code number (For SUP and OTH only)
	 *	1. Reorder all the Rater Code number (For SUP and OTH only) where RTRelation = 0
	 */
	public void FixRaterCode(int SurveyID, int iType) throws SQLException, Exception 
	{
		//db.openDB();
		String command;
		String sql;

		int iRTRelation;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps=null;
		
		if(iType == 0)
			command = "SELECT * FROM tblAssignment WHERE (SurveyID = " + SurveyID + ") ORDER BY TargetLoginID, RaterLoginID";
		else
			command = "SELECT * FROM tblAssignment WHERE (SurveyID = " + SurveyID + ")  AND (RTRelation = 0) ORDER BY RTSpecific, TargetLoginID, RaterLoginID";
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(command);
			while(rs.next())
			{
				int AssignmentID = rs.getInt("AssignmentID");
				int RTRelation = rs.getInt("RTRelation");
				int RTSpecific = rs.getInt("RTSpecific");
				String RaterCode = rs.getString("RaterCode");
				int TargetLoginID = rs.getInt("TargetLoginID");
				String sRaterCode = "";
				
				if(RTRelation != 0)
				{
					if (RaterCode.length() >= 3)
					{
						if(RaterCode.substring(0, 3).equals("SUP") || RaterCode.substring(0, 3).equals("OTH"))
						{
							
							sRaterCode = RaterCode(SurveyID, RTRelation, RTSpecific, TargetLoginID);
							
							//Update
							sql = "UPDATE tblAssignment SET RaterCode = '" + sRaterCode + "' WHERE AssignmentID = " + AssignmentID;
							ps = con.prepareStatement(sql);
							ps.executeUpdate();	
							System.out.println("UPDATED FOR " + RTRelation + ", " + RTSpecific + ", " + TargetLoginID);
						}
					}
				}
				
				if(iType == 1)
				{
					
					iRTRelation = getRelationHighID(RTSpecific);
					sql = "UPDATE tblAssignment SET RTRelation = '" + iRTRelation + "' WHERE AssignmentID = " + AssignmentID;
					ps = con.prepareStatement(sql);
					ps.executeUpdate();	
					System.out.println("UPDATED RTRelation FOR " + iRTRelation + ", " + RTSpecific + ", " + TargetLoginID);
					
					System.out.println("RaterCode = " + RaterCode + " Length = " + RaterCode.length());
					if (RaterCode.length() >= 3)
					{
						if(RaterCode.substring(0, 3).equals("SUP") || RaterCode.substring(0, 3).equals("OTH"))
						{
							
							sRaterCode = RaterCode(SurveyID, iRTRelation, RTSpecific, TargetLoginID);
							
							//Update
							sql = "UPDATE tblAssignment SET RaterCode = '" + sRaterCode + "' WHERE AssignmentID = " + AssignmentID;
							ps = con.prepareStatement(sql);
							ps.executeUpdate();	
							System.out.println("UPDATED FOR " + RTRelation + ", " + RTSpecific + ", " + TargetLoginID);
						}
					}
				}
				//System.out.println(sRaterCode);
			}
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - FixResultProblem - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closePStmt(ps);
			ConnectionBean.closeRset(rs);
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
	
	}
	public boolean checkRaterCodeGenre(String oldCode, String newCode){
		if(newCode.substring(0,3).equals(oldCode.substring(0,3))){
			return true;
		}
		return false;
	}
	public boolean updateOthersRaterCode(String RaterCode, int surveyID, int targetID){
		// retrieve outdated data
		TreeMap<Integer, String> assignIDCodeMap = new TreeMap<Integer, String>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = "select * from tblAssignment where SurveyID = "+surveyID+
				" and TargetLoginID = "+targetID +" and RaterCode like '%"+RaterCode.substring(0,3)+"%' and RaterCode > '"+RaterCode+"' order by RaterCode";
		
		System.out.println(sql);
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				assignIDCodeMap.put(rs.getInt("AssignmentID"), rs.getString("RaterCode"));
			}
		}catch(Exception e){
			System.out.println("AssignTarget_Rater.java - updateOthersRaterCode - "+e.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		// udpate outdated data
		for(Map.Entry<Integer, String> entry : assignIDCodeMap.entrySet()){
			
			int codeNum = Integer.parseInt(entry.getValue().substring(entry.getValue().length()-3,entry.getValue().length())) - 1;
			String codeNumStr = String.format("%03d", codeNum);
			String newRaterCode = entry.getValue().substring(0,3)+codeNumStr;
			//PEER000 is longer than others
			if(entry.getValue().length() > 6)
				newRaterCode = entry.getValue().substring(0,4)+codeNumStr;
			sql = "update tblAssignment set RaterCode ='" + newRaterCode+"' where AssignmentID = "+entry.getKey();
			try{
				con = ConnectionBean.getConnection();
				st = con.createStatement();
				int num = st.executeUpdate(sql);
				if(num == 0){
					return false;
				}
			}catch(Exception e){
				System.out.println("AssignTarget_Rater.java - updateOthersRaterCode - "+e.getMessage());
			}finally{
				ConnectionBean.closeRset(rs); //Close ResultSet
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		}
		return true;
	}
	
	public String getRaterCode(int assignmentID){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = "select * from [tblAssignment] where AssignmentID = " + assignmentID;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()){
				return rs.getString("RaterCode");
			}
		}catch(Exception e){
			System.out.println("AssignTarget_Rater.java - getRaterCode - "+e.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return null;
	}
	/* 
	*	Assignment of Rater Code
	*	
	*	@param - int SurveyID, int RTRelation, int RTSpecific, int TargetLoginID
	*	Example - SUP 001, SUP 002,OTH 001...
	*/
	public String RaterCode(int SurveyID, int RTRelation, int RTSpecific, int TargetLoginID) throws SQLException, Exception 
	{
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
		int Code =0;
		String RaterCode ="";
		
		if(RTSpecific != 0)
		{
			/*
			*Change(s): Use table tblSurveyRelationSpecific to manage relation specifics
			*Reason(s): To associate relation specific to survey instead of organization
			*Updated by: Liu Taichen
			*Updated on: 5 June 2012
			*/
			String rel = "SELECT * FROM tblSurveyRelationSpecific WHERE SpecificID = " +RTSpecific;
			try{
				con=ConnectionBean.getConnection();
				st=con.createStatement();
				rs=st.executeQuery(rel);
				if(rs.next())
					RTRelation  = rs.getInt("RelationID");
				
			}catch(Exception ex){
				System.out.println("AssignTarget_Rater.java - RaterCode1 - "+ex.getMessage());
			}finally{

				ConnectionBean.closeRset(rs); //Close ResultSet
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection

	
			}
	
		}

		String command = "SELECT * FROM tblAssignment WHERE RTRelation = " +RTRelation+" AND SurveyID ="+SurveyID;
		command = command + " AND TargetLoginID = "+TargetLoginID+" ORDER BY RaterCode";
		ResultSet rs_rel =null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs_rel=st.executeQuery(command);

		while(rs_rel.next())
		{
			String db_RaterCode = rs_rel.getString("RaterCode");
			
			//SELF
			if(db_RaterCode.length() > 4)
			{
				int db_Code = Integer.parseInt(db_RaterCode.substring(4));
				// PEER000 is longer than other i.e. SUP SUB
				if(db_RaterCode.length() == 6){
					db_Code = Integer.parseInt(db_RaterCode.substring(3));
				}

				if(db_Code > Code)
					Code = db_Code;
			}
			else
				Code = 0;
		}
		}catch(Exception ex){
			
		System.out.println("AssignTarget_Rater.java - RaterCode2 - "+ex.getMessage()+command);
		ex.printStackTrace();
		}finally{

		ConnectionBean.closeRset(rs_rel); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection

		}
		
		Code = Code + 1;
		
		if(RTRelation == 1)
		{
			if(Code < 10)
				RaterCode = "SUP00"+Code;
			else if(Code < 100)					
				RaterCode = "SUP0"+Code;
			else if(Code > 99)	
				RaterCode = "SUP"+Code;
		}
		else if(RTRelation == 2)
		{
			RaterCode = "SELF";
		}
		else if(RTRelation == 3)
		{
			if(Code < 10)
				RaterCode = "OTH00"+Code;
			else if(Code < 100)					
				RaterCode = "OTH0"+Code;
			else if(Code > 99)	
				RaterCode = "OTH"+Code;
		}
		
		// Add logic for display of Rater Codes for Subordinates and Peers, added by Desmond 21 Oct 09
		else if(RTRelation == 4)
		{
			// Rater Relation is Subordinate
			if(Code < 10)
				RaterCode = "SUB00"+Code;
			else if(Code < 100)					
				RaterCode = "SUB0"+Code;
			else if(Code > 99)	
				RaterCode = "SUB"+Code;
		}
		else if(RTRelation == 5)
		{
			// Rater Relation is Peer
			// TO DO - May need to change codes from PEER to PR
			if(Code < 10)
				RaterCode = "PEER00"+Code;
			else if(Code < 100)					
				RaterCode = "PEER0"+Code;
			else if(Code > 99)	
				RaterCode = "PEER"+Code;
		}
		else if(RTRelation == 6)
		{
			// Rater Relation is Additional Raters
			// TO DO - May need to change codes from PEER to PR
			if(Code < 10)
				RaterCode = "ADD00"+Code;
			else if(Code < 100)					
				RaterCode = "ADD0"+Code;
			else if(Code > 99)	
				RaterCode = "ADD"+Code;
		}
		
		else if(RTRelation == 7)
		{
			// Rater Relation is Direct Reports
			// TO DO - May need to change codes from PEER to PR
			if(Code < 10)
				RaterCode = "DIR00"+Code;
			else if(Code < 100)					
				RaterCode = "DIR0"+Code;
			else if(Code > 99)	
				RaterCode = "DIR"+Code;
		}
		else if(RTRelation == 8)
		{
			// Rater Relation is InDirect Reports
			// TO DO - May need to change codes from PEER to PR
			if(Code < 10)
				RaterCode = "IDR00"+Code;
			else if(Code < 100)					
				RaterCode = "IDR0"+Code;
			else if(Code > 99)	
				RaterCode = "IDR"+Code;
		}
		else if(RTRelation == 9)
		{
			// Rater Relation is Peer
			// TO DO - May need to change codes from PEER to PR
			if(Code < 10)
				RaterCode = "BOSS00"+Code;
			else if(Code < 100)					
				RaterCode = "BOSS0"+Code;
			else if(Code > 99)	
				RaterCode = "BOSS"+Code;
		}
		
		return RaterCode;


	}
	
	/** Add Rater into a survey
	*
	*	Parameters - int SurveyID, int TargetLoginID, int RaterLoginID, int RTRelation, 
	*					int RTSpecific, String RaterCode
	*
	*	Record will be stored into tblAssignment. If the record already existed then only updating needed.
	*
	*/
	
	public boolean addRater(int SurveyID, int TargetLoginID, int RaterLoginID, int RTRelation, 
							int RTSpecific, String RaterCode) throws SQLException, Exception {
		
		boolean isAdded = false;
		int iSQLQueryStatus = 0;
		
		String sSelectQuery = "SELECT * FROM tblAssignment WHERE SurveyID = "+ SurveyID + " " +
							  "AND TargetLoginID = " + TargetLoginID + " AND RaterLoginID = " + RaterLoginID;
		String sSelectJunkQuery = "SELECT * FROM tblAssignment WHERE SurveyID = " + SurveyID + " " +
		  						  "AND TargetLoginID = " + TargetLoginID + " AND RaterLoginID = 0";
		String sUpdateQuery = "";
		String sInsertQuery = "";
		
		Connection con = null;
		PreparedStatement psSelect = null;
		PreparedStatement psJunkRow = null;
		PreparedStatement psInsertUpdate = null;
		ResultSet rsSelect = null;
		ResultSet rsJunkRow = null;
		
		try{
			con = ConnectionBean.getConnection();
			psSelect = con.prepareStatement(sSelectQuery);
			rsSelect = psSelect.executeQuery();
			psJunkRow = con.prepareStatement(sSelectJunkQuery);
			rsJunkRow = psJunkRow.executeQuery();
			
			//addTarget() will generate entry inside tblAssignment which RaterLoginID is 0, 
			//the following code is required to remove this junk row
			//By: Chun Pong
			//Date: 09 Jul 2008
			if(!rsSelect.next() && rsJunkRow.next()) {
				sUpdateQuery = "UPDATE tblAssignment SET RaterLoginID = " + RaterLoginID + ", RTRelation = " + 
							   RTRelation + ", RTSpecific = " + RTSpecific + ", RaterCode = '" + RaterCode + "' " +
							   "WHERE SurveyID = " + SurveyID + " AND TargetLoginID= " + TargetLoginID + 
							   " AND RaterLoginID = 0";
				psInsertUpdate = con.prepareStatement(sUpdateQuery);
				iSQLQueryStatus = psInsertUpdate.executeUpdate();	
				if(iSQLQueryStatus != 0)
					isAdded = true;
			} else {
				int iDetail[] = UserList.getUserFKDetail(TargetLoginID);
				
				sInsertQuery = "INSERT INTO tblAssignment (SurveyID, TargetLoginID,RaterLoginID,RTRelation,RTSpecific,RaterCode, FKTargetDivision, FKTargetDepartment, FKTargetGroup) VALUES (" + SurveyID + ", " + TargetLoginID + ", " + RaterLoginID + ", " + RTRelation + ", " + RTSpecific + ", '" + RaterCode + "'" + ", " + iDetail[0] + ", " + iDetail[1] + ", " + iDetail[2] + ")";
				psInsertUpdate = con.prepareStatement(sInsertQuery);
				iSQLQueryStatus = psInsertUpdate.executeUpdate();	
				if(iSQLQueryStatus != 0)
					isAdded = true;
				
				//Old code -> To be commented off
				//To synchronise all Div, Dept & Group in tblAssignment under specific TargetLoginID
				//FixResultProblem(TargetLoginID);
			}		
		}catch(Exception ex){			
			System.out.println("AssignTarget_Rater.java - addRater - "+ex.getMessage());
		}finally{
	        ConnectionBean.closePStmt(psSelect);
	        ConnectionBean.closePStmt(psJunkRow);
	        ConnectionBean.closePStmt(psInsertUpdate);
			ConnectionBean.closeRset(rsSelect); //Close ResultSet
			ConnectionBean.closeRset(rsJunkRow); //Close ResultSet
			ConnectionBean.close(con); //Close connection
		}
		return isAdded;
	}
	
	
	/** Add Rater into a survey
	*
	*	Parameters - int SurveyID, int TargetLoginID, int RaterLoginID, int RTRelation, 
	*					int RTSpecific, String RaterCode
	*
	*	Record will be stored into tblAssignment. If the record already existed then only updating needed.
	*
	*/
	
	public boolean addRater(int SurveyID, int TargetLoginID, int RaterLoginID, int RTRelation, 
							int RTSpecific, String RaterCode, int round, int wave) throws SQLException, Exception {
		
		boolean isAdded = false;
		int iSQLQueryStatus = 0;
		
		String sSelectQuery = "SELECT * FROM tblAssignment WHERE SurveyID = "+ SurveyID + " " +
							  "AND TargetLoginID = " + TargetLoginID + " AND RaterLoginID = " + RaterLoginID;
		String sSelectJunkQuery = "SELECT * FROM tblAssignment WHERE SurveyID = " + SurveyID + " " +
		  						  "AND TargetLoginID = " + TargetLoginID + " AND RaterLoginID = 0";
		String sUpdateQuery = "";
		String sInsertQuery = "";
		
		Connection con = null;
		PreparedStatement psSelect = null;
		PreparedStatement psJunkRow = null;
		PreparedStatement psInsertUpdate = null;
		ResultSet rsSelect = null;
		ResultSet rsJunkRow = null;
		
		try{
			con = ConnectionBean.getConnection();
			psSelect = con.prepareStatement(sSelectQuery);
			rsSelect = psSelect.executeQuery();
			psJunkRow = con.prepareStatement(sSelectJunkQuery);
			rsJunkRow = psJunkRow.executeQuery();
			
			//addTarget() will generate entry inside tblAssignment which RaterLoginID is 0, 
			//the following code is required to remove this junk row
			//By: Chun Pong
			//Date: 09 Jul 2008
			if(!rsSelect.next() && rsJunkRow.next()) {
				sUpdateQuery = "UPDATE tblAssignment SET RaterLoginID = " + RaterLoginID + ", RTRelation = " + 
							   RTRelation + ", RTSpecific = " + RTSpecific + ", RaterCode = '" + RaterCode + "', " +
							   "Round = " + round +", Wave = "+ wave + " " +
							   "WHERE SurveyID = " + SurveyID + " AND TargetLoginID= " + TargetLoginID + 
							   " AND RaterLoginID = 0";
				psInsertUpdate = con.prepareStatement(sUpdateQuery);
				iSQLQueryStatus = psInsertUpdate.executeUpdate();	
				if(iSQLQueryStatus != 0)
					isAdded = true;
			} else {
				int iDetail[] = UserList.getUserFKDetail(TargetLoginID);
				
				sInsertQuery = "INSERT INTO tblAssignment (SurveyID, TargetLoginID,RaterLoginID,RTRelation,RTSpecific,RaterCode, FKTargetDivision, FKTargetDepartment, FKTargetGroup, round, wave) " +
						"VALUES (" + SurveyID + ", " + TargetLoginID + ", " + RaterLoginID + ", " + RTRelation + ", " + RTSpecific + ", '" + RaterCode + "'" + ", " + iDetail[0] + ", " + iDetail[1] + ", "
						+ iDetail[2] + ","+round+","+wave+")";
				psInsertUpdate = con.prepareStatement(sInsertQuery);
				iSQLQueryStatus = psInsertUpdate.executeUpdate();	
				if(iSQLQueryStatus != 0)
					isAdded = true;
				
				//Old code -> To be commented off
				//To synchronise all Div, Dept & Group in tblAssignment under specific TargetLoginID
				//FixResultProblem(TargetLoginID);
			}		
		}catch(Exception ex){			
			System.out.println("AssignTarget_Rater.java - addRater - "+ex.getMessage());
		}finally{
	        ConnectionBean.closePStmt(psSelect);
	        ConnectionBean.closePStmt(psJunkRow);
	        ConnectionBean.closePStmt(psInsertUpdate);
			ConnectionBean.closeRset(rsSelect); //Close ResultSet
			ConnectionBean.closeRset(rsJunkRow); //Close ResultSet
			ConnectionBean.close(con); //Close connection
		}
		return isAdded;
	}
	
	
	/** Add Rater into a survey
	*
	*	Parameters - int SurveyID, int TargetLoginID, int RaterLoginID, int RTRelation, 
	*					int RTSpecific, String RaterCode, int PKUser
	*
	*	Record will be stored into tblAssignment. If the record already exit then only updating needed.
	*
	*	Event viewer will capture this event.
	*/
	
	public boolean addRater(int SurveyID, int TargetLoginID, int RaterLoginID, int RTRelation, 
						int RTSpecific, String RaterCode, int PKUser) throws SQLException, Exception 
	{
		boolean bIsAdded=false;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps =null;
		/* 19-Jan-04 Function modified by Rianto, commented one are old codes */
		String OldName = "";
		String command = "SELECT * FROM tblSurvey WHERE SurveyID ="+SurveyID;
		//ResultSet rs1 = db.getRecord(command);
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(command);
		if(rs.next())
			OldName = rs.getString("SurveyName");
		}catch(Exception ex){
			
			System.out.println("AssignTarget_Rater.java - addRater - "+ex.getMessage());
			}finally{
	        ConnectionBean.closePStmt(ps);
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		String sql=" ";
		
		//String sSQL = "SELECT * FROM tblAssignment WHERE SurveyID= "+ SurveyID+" AND TargetLoginID= "+TargetLoginID;
		String sSQL = "SELECT * FROM tblAssignment WHERE SurveyID= "+ SurveyID+" AND TargetLoginID= "+TargetLoginID + " AND RaterLoginID = 0";
		
		//db.openDB();
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sSQL);
			if(rs.next())
			{//	db_RaterID = rs.getInt("RaterLoginID");
			
			//if(db_RaterID == 0)	
				sql = "UPDATE tblAssignment SET RaterLoginID ="+RaterLoginID+", RTRelation ="+RTRelation+", RTSpecific= "+RTSpecific+", RaterCode= '"+RaterCode+"' " +
						" WHERE SurveyID= "+ SurveyID+" AND TargetLoginID= "+TargetLoginID + " AND RaterLoginID = 0";
			
				ps = con.prepareStatement(sql);
				int iSuccess=ps.executeUpdate();	
				if(iSuccess!=0){
					bIsAdded=true;
				}
			}
			else
			{
				int iDetail[] = new int[3];
				iDetail = UserList.getUserFKDetail(TargetLoginID);
				int targetRound = getRound(TargetLoginID);
				sql = "INSERT INTO tblAssignment (SurveyID, TargetLoginID,RaterLoginID,RTRelation,RTSpecific,RaterCode, FKTargetDivision, FKTargetDepartment, FKTargetGroup, round) VALUES ("
				+ SurveyID+", "+TargetLoginID+", "+RaterLoginID+", "+RTRelation+", "+RTSpecific+", '"+RaterCode+"'" + ", "+ iDetail[0] +", "+ iDetail[1] +", "+ iDetail[2] +", " + targetRound  +")";
				System.out.println(sql);
				//db.openDB();
				 ps = con.prepareStatement(sql);
				int iSuccess=ps.executeUpdate();	
				if(iSuccess!=0){
					bIsAdded=true;
				}
			
				//To synchronise all Div, Dept & Group in tblAssignment under specific TargetLoginID
				FixResultProblem(TargetLoginID);
			}
			
		}catch(Exception ex){
			
			System.out.println("AssignTarget_Rater.java - addRater - "+ex.getMessage());
			}finally{
	        ConnectionBean.closePStmt(ps);
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		
		//System.out.println(sql);
		
		sDetail = user.getUserDetail(PKUser);
		raterDetail = user.getUserDetail(RaterLoginID);
		targetDetail = user.getUserDetail(TargetLoginID);
		
		ev.addRecord("Insert", itemName, raterDetail[0]+", "+raterDetail[1]+"(Rater) for "+targetDetail[0]+", "+targetDetail[1]+"(Target) for Survey "+OldName, sDetail[2], sDetail[11], sDetail[10]);
		return bIsAdded;
	}
	
	public boolean editRater(int TargetID, int assignID, int RTRelation,
			int RTSpecific, String RaterCode, int round, int wave) throws SQLException, Exception {
		boolean isUpdated=false;
		Connection con = null;
		PreparedStatement ps =null;

		String sql=" ";

		//db.openDB();
		try{
			int iDetail[] = UserList.getUserFKDetail(TargetID);
			con=ConnectionBean.getConnection();
			sql = "Update tblAssignment set RTRelation = "+RTRelation+", RTSpecific = "+RTSpecific+", RaterCode = '"+RaterCode+"', round = "+round+", wave = "+wave+", " +
					"FKTargetDivision ="+iDetail[0]+", FKTargetDepartment ="+iDetail[1]+", FKTargetGroup ="+iDetail[2]+"  where AssignmentID = " + assignID;
			
			//db.openDB();
			ps = con.prepareStatement(sql);
			int iSuccess=ps.executeUpdate();	
			if(iSuccess!=0){
				isUpdated=true;
			}
			

		}catch(Exception ex){
			
			System.out.println("AssignTarget_Rater.java - editRater(6) - "+ex.getMessage());
		}finally{
	        ConnectionBean.closePStmt(ps);
			ConnectionBean.close(con); //Close connection
		}
		return isUpdated;
	}

	/**
	 * This method edit the rater relation of a rater
	 * @param AssignmentID - ID of the assignment
	 * @param PKUser - ID of the user performing the action
	 * @param RaterCode - the code of the rater relation
	 * @throws SQLException
	 * @throws Exception
	 * @author Liu Taichen
	 * Created on: 6 June 2012
	 */
	
	public boolean editAssignment ( int AssignmentID, String RaterCode, int PKUser) throws SQLException, Exception
	{
		//process the ratercode
		AssignTarget_Rater atr = new AssignTarget_Rater();
		votblAssignment assignment = atr.getAssignment(AssignmentID);
		SurveyRelationSpecific srs = new SurveyRelationSpecific();
		
		int oldRelHigh = assignment.getRTRelation();
		int oldRelSpec = assignment.getRTSpecific();
		int ID = Integer.parseInt(RaterCode.substring(4));
		String relation = RaterCode.substring(0,4);
		String sql = "";
	
		if(relation.equals("High")){
			//if there is change
			System.out.println("enter "+relation);
			System.out.println("enter1 "+ID);
			if(ID != oldRelHigh){
				String Code = atr.RaterCode(assignment.getSurveyID(), ID, 0, assignment.getTargetLoginID());
				System.out.println("CODE is "+Code);
				sql = "Update tblAssignment Set RTRelation = " + ID + ", RTSpecific = 0 , RaterCode = '" + Code +"' where AssignmentID = " + AssignmentID;
			}
			//if there is no change
			else{
				return false;
			}
		}
		else{
			//if there is change
			if(ID != oldRelSpec){
				int relHigh = srs.getSpecific(ID).getRelationID();
				if(relHigh != oldRelHigh){
				String Code = atr.RaterCode(assignment.getSurveyID(), relHigh, ID, assignment.getTargetLoginID());
				
				sql = "Update tblAssignment set RTRelation = " + relHigh + ", RTSpecific = " + ID + ", RaterCode = '" + Code +"' where AssignmentID = " + AssignmentID;
				}
				else{
					sql = "Update tblAssignment set RTSpecific = " + ID + " where AssignmentID = " + AssignmentID;
				}
				
			}
			//if there is no change
			else{
				return false;
			}
			
			
		}
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps=null;

		boolean bIsDeleted=false;
		String OldName = "";
		int raterloginid=assignment.getRaterLoginID();
		int targetloginid=assignment.getTargetLoginID();
		int SurveyID=assignment.getSurveyID();
		


				
		con=ConnectionBean.getConnection();
		st=con.createStatement();
		try{
			con=ConnectionBean.getConnection();
			ps=con.prepareStatement(sql);
			int iSuccess=ps.executeUpdate();
			if(iSuccess!=0){
				bIsDeleted=true;
			}
			
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - editAssignment - "+ex.getMessage());
		}finally{

			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection


		}
		
		sDetail = user.getUserDetail(PKUser);
		raterDetail = user.getUserDetail(raterloginid);
		targetDetail = user.getUserDetail(targetloginid);
		
		ev.addRecord("Edit", itemName, raterDetail[0]+", "+raterDetail[1]+"(Rater) for "+targetDetail[0]+", "+targetDetail[1]+"(Target) for Survey "+OldName, sDetail[2], sDetail[11], sDetail[10]);
		
	return bIsDeleted;
		
	}
	
	/** Delete Rater into a survey
	*
	*	Parameters - int SurveyID, int TargetLoginID, int RaterLoginID, int RTRelation, 
	*					int RTSpecific, String RaterCode, int PKUser
	*
	*	Record will be stored into tblAssignment. If the record already exit then only updating needed.
	*
	*	Event viewer will capture this event.
	*/
	
	public boolean delRater(int AssignmentID, int PKUser) throws SQLException, Exception 
	{
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps=null;

		boolean bIsDeleted=false;
		String OldName = "";
		int raterloginid=0;
		int targetloginid=0;
		int SurveyID=0;
		int TargetID=0;
		
		
		String command = "SELECT * FROM tblAssignment a, tblSurvey b WHERE a.SurveyID = b.SurveyID AND AssignmentID ="+AssignmentID;
		
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(command);
			if(rs.next())
			{
				raterloginid = rs.getInt("RaterLoginID");
				targetloginid = rs.getInt("TargetLoginID");
				OldName = rs.getString("SurveyName");	
			}
			
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - delRater - "+ex.getMessage());
		}finally{

			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection


		}
		
		/*if 	getallowDeleteFunc = true -->capture deletion record
		 */
		
		if(server.getallowDeleteFunc ())
		{
			int RTRelation = 0;
			int RTSpecific = 0;
			String RaterCode = "";
			int RaterStatus = 0;
			int RaterID = 0;
			TargetID = 0;
			SurveyID= 0;
			float ReliabilityScore=0f;
			String DeadLineSubmission="";
			String sql="";
							
			SimpleDateFormat formatter_db2= new SimpleDateFormat ("yyyy/MM/dd");
			
			String query ="SELECT * FROM tblAssignment WHERE AssignmentID = "+AssignmentID;
			try{
				con=ConnectionBean.getConnection();
				st=con.createStatement();
				rs=st.executeQuery(query);
				
				if(rs.next())
				{
					SurveyID = rs.getInt("SurveyID");
					RTRelation = rs.getInt("RTRelation");
					RTSpecific = rs.getInt("RTSpecific");
					RaterCode = rs.getString("RaterCode");
					RaterStatus = rs.getInt("RaterStatus");
					ReliabilityScore = rs.getFloat("Reliability");
					Date db_DeadLineSubmission = rs.getDate("DeadLineSubmission");
					RaterID = rs.getInt("RaterLoginID");
					TargetID = rs.getInt("TargetLoginID");

					if(db_DeadLineSubmission == null)
					{
						sql = "INSERT INTO tblDeletedAssignment (SurveyID, RTRelation, RTSpecific, RaterCode, RaterStatus, ReliabilityScore, TargetLoginID,RaterLoginID)";
						sql = sql +" VALUES ("+ SurveyID+", "+RTRelation+", "+RTSpecific+", '"+RaterCode+"', "+RaterStatus+", "+ReliabilityScore+", "+TargetID+", "+RaterID+")";
					}	
					else
					{
						DeadLineSubmission = formatter_db2.format(db_DeadLineSubmission);
						sql = "INSERT INTO tblDeletedAssignment (SurveyID, RTRelation, RTSpecific, RaterCode, RaterStatus, ReliabilityScore, DeadLineSubmission, TargetLoginID,RaterLoginID)";
						sql = sql +" VALUES ("+ SurveyID+", "+RTRelation+", "+RTSpecific+", '"+RaterCode+"', "+RaterStatus+", "+ReliabilityScore+", '"+DeadLineSubmission+"', "+TargetID+", "+RaterID+")";
					}
				}
				 ps= con.prepareStatement(sql);
				 ps.executeUpdate();	
					
				
			}catch(Exception ex){
				System.out.println("AssignTarget_Rater.java - delRater - "+ex.getMessage());
			}finally{
				ConnectionBean.closePStmt(ps);
				ConnectionBean.closeRset(rs); //Close ResultSet
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection


			}
		
		}		
		String sql1="";
//	**************Edited by Tracy 14 Aug 08: Check number of assignment per target
// Delete the assignment if the target has more than 1 assignment
// Update RaterCode to NULL and RaterLoginID=0 when target has only 1 assignment
		String cmd=" SELECT * FROM tblAssignment WHERE SurveyID=" + SurveyID + " AND TargetLoginID=" + TargetID;
		int count=0;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(cmd);
			while(rs.next())
			{
				count++;
			}
		
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - delRater - "+ex.getMessage());
		}finally{

			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}
		if (count>1){
			sql1 = "DELETE FROM tblAssignment WHERE AssignmentID = "+AssignmentID;
		}else{
			sql1 = "UPDATE tblAssignment SET RaterCode= NULL, RaterLoginID= 0 WHERE AssignmentID = "+AssignmentID;
		}
//********************************
//***********Commented by Tracy 14 Aug 08
		//db.openDB();
		//sql1 = "DELETE FROM tblAssignment WHERE AssignmentID = "+AssignmentID;
		//PreparedStatement ps1 = db.con.prepareStatement(sql1);
		//ps1.execute();
		//db.closeDB();
//*******************************************
		con=ConnectionBean.getConnection();
		st=con.createStatement();
		try{
			con=ConnectionBean.getConnection();
			ps=con.prepareStatement(sql1);
			int iSuccess=ps.executeUpdate();
			if(iSuccess!=0){
				bIsDeleted=true;
			}
			
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - delRater - "+ex.getMessage());
		}finally{

			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection


		}
		
		sDetail = user.getUserDetail(PKUser);
		raterDetail = user.getUserDetail(raterloginid);
		targetDetail = user.getUserDetail(targetloginid);
		
		ev.addRecord("Delete", itemName, raterDetail[0]+", "+raterDetail[1]+"(Rater) for "+targetDetail[0]+", "+targetDetail[1]+"(Target) for Survey "+OldName, sDetail[2], sDetail[11], sDetail[10]);
		
	return bIsDeleted;
	}
		
    /**
     * Add survey assignment between a target and a rater who is the target's a direct superior 
     * @param iSurveyID  	          - The file name of the Excel file containing the data
     * @param iTargetID 	          - The Company ID
     * @return A boolean to indicate if the addition was successful
     * 
     */
	public boolean addSupRater(int iSurveyID, int iTargetID) throws SQLException, Exception {
		int iSupPKUser = -1;		
		int iDirectSupSpecificID = -1;
		String sRaterCode = "";
		votblSurvey voSurvey = null;
		
		//Get SurveyID
		voSurvey = user.getSurveyDetail(iSurveyID);
		
		//Get Superior PKUser ID
		iSupPKUser = UserList.getSupPKUser(iTargetID);
				
		//Superior Exists Check
		if(iSupPKUser <= 0) {
			System.out.println("AssignTarget_Rater.java - addSupRater() - No Superior Found for User [ID:" +
							   iTargetID + "]");
			return true;
		} else {
			//Get Direct Superior ID
			iDirectSupSpecificID = RtRel.getDirectSupSpecificID(voSurvey.getFKCompanyID(), voSurvey.getFKOrganization());
			//Get Rater Code
			sRaterCode = RaterCode(iSurveyID, 1, iDirectSupSpecificID, iTargetID);
			//Add Superior Check
			if(addRater(iSurveyID, iTargetID, iSupPKUser, 1, iDirectSupSpecificID, sRaterCode)) {
				System.out.println("AssignTarget_Rater.java - addSupRater() - Added Superior [ID:" + iSupPKUser +
							   	   "]as Rater for User [ID:" + iTargetID + "] Successfully");
				return true;
			} else {
				System.out.println("AssignTarget_Rater.java - addSupRater() - Added Superior [ID:" + iSupPKUser +
					   	   		   "]as Rater for User [ID:" + iTargetID + "] Failed");
				return false;
			} //End Add Superior Check		
		}//End of Superior Exists Check		
	} //End of addSupRater()
	
	/*------------------------------------------end Rater Menu-----------------------------------------------*/

	
	/* ---To extract total no of Group/section assignment in the survey*/
	
	public void setRound(int round){
		this.round = round;
	}
	
	public int getRound(){
		return round;
	}
	
	public void setWave(int wave){
		this.wave = wave;
	}
	
	public int getWave(){
		return wave;
	}

	public void setGroupID(int GroupID) 
	{
		this.GroupID = GroupID;
	}

	public int getGroupID() 
	{
		return GroupID;
	}
	
	public void setDeptID (int DeptID) 
	{
		this.DeptID = DeptID;
	}
 
	public int getDeptID() 
	{
		return DeptID;
	}
	
	public void setDivID(int DivID) 
	{
		this.DivID = DivID;
	}

	public int getDivID() 
	{
		return DivID;
	}
	
	public void setTargetID (int TargetID) 
	{
		this.TargetID = TargetID;
	}

	public int getTargetID() 
	{
		return TargetID;
	}
	
	public void set_selectedTargetID(int selTargetID) 
	{
		this.selTargetID = selTargetID;
	}

	public int get_selectedTargetID() 
	{
		return selTargetID;
	}
	
	public void set_selectedRaterID(int selRaterID) 
	{
		this.selRaterID = selRaterID;
	}

	public int get_selectedRaterID() 
	{
		return selRaterID;
	}
	
	public void set_NameSequence(int NameSequence) 
	{
		this.NameSequence = NameSequence;
	}

	public int get_NameSequence() 
	{
		return NameSequence;
	}
	
	public void set_selectedAssID(int selAssID) 
	{
		this.selAssID = selAssID;
	}

	public int get_selectedAssID() 
	{
		return selAssID;
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
	 * Set Bean Variable SelectedGroup.
	 */
	public void setSelectedGroup(int SelectedGroup) {
		this.SelectedGroup = SelectedGroup;
	}
	
	/**
	 * Get Bean Variable SelectedGroup.
	 */
	public int getSelectedGroup() {
		return SelectedGroup;
	}
	
	/**
	 * Set Bean Variable SelectedDept.
	 */
	public void setSelectedDept(int SelectedDept) {
		this.SelectedDept = SelectedDept;
	}
	
	/**
	 * Get Bean Variable SelectedDept.
	 */
	public int getSelectedDept() {
		return SelectedDept;
	}
	
	/**
	 * Set Bean Variable SelectedDiv.
	 */
	public void setSelectedDiv(int SelectedDiv) {
		this.SelectedDiv = SelectedDiv;
	}
	
	/**
	 * Get Bean Variable SelectedDiv.
	 */
	public int getSelectedDiv() {
		return SelectedDiv;
	}
	
	/*Added by Su Lingtong
	 *Date: 11/9/12
	 *The target status may have already been modified.
	 *Ask admin if he/she wants to update assignment status to make it consistent with target status.
	 */
	
    public boolean iftargetassignmodified(int TargetID){
    	boolean modified = false;
    	
    	String command1 = "SELECT * FROM [User] " + "INNER JOIN tblAssignment ON [User].PKUser = tblAssignment.TargetLoginID ";
		command1 = command1 + "WHERE ([User].PKUser = "+TargetID+") AND ([User].FKDepartment <> tblAssignment.FKTargetDepartment) OR";
		command1 = command1 + "([User].PKUser = "+TargetID+") AND ([User].FKDivision <> tblAssignment.FKTargetDivision) OR";
		command1 = command1 + "([User].PKUser = "+TargetID+") AND ([User].Group_Section <> tblAssignment.FKTargetGroup) ";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		   try{
			   con=ConnectionBean.getConnection();
			   st=con.createStatement();
			   rs=st.executeQuery(command1);
			
			   while(rs.next()) {
				   modified = true;
			   }
				
		   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		   
    	return modified;
    }
	
	public boolean ifanytargetmodified(int SurveyID, int TargetID){
		boolean ifanytargetmodified = false;
		
		String command1 = "SELECT * FROM [User] " + "INNER JOIN tblAssignment ON [User].PKUser = tblAssignment.TargetLoginID ";
		command1 = command1 + "WHERE ([User].PKUser = "+TargetID+") AND ([User].FKDepartment <> tblAssignment.FKTargetDepartment) AND (tblAssignment.SurveyID = " +SurveyID +  ") OR";
		command1 = command1 + "([User].PKUser = "+TargetID+") AND ([User].FKDivision <> tblAssignment.FKTargetDivision) AND (tblAssignment.SurveyID = "+SurveyID+ ")  OR";
		command1 = command1 + "([User].PKUser = "+TargetID+") AND ([User].Group_Section <> tblAssignment.FKTargetGroup) AND (tblAssignment.SurveyID = "+SurveyID+ ")";
		
		//System.out.print(command1);
		
		//String sql = "SELECT FKDepartment, FKDivision, Group_Section FROM [User]";
		//sql = sql + "WHERE (PKUser  = "+ iTargetID+ ")";		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		   try{
			   con=ConnectionBean.getConnection();
			   st=con.createStatement();
			   rs=st.executeQuery(command1);
			
			   while(rs.next()) {
				   ifanytargetmodified = true;
			   }
				
		   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		
		return ifanytargetmodified;
	}
	
	/*Added by Su Lingtong
	 *Date: 18/9/12
	 *The target status given under a specific category may have already been modified.
	 *Ask admin if he/she wants to update assignment status to make it consistent with target status.
	 */
	
	public boolean iftargetmodified (int SurveyID, int iTargetID, int dep, int div, int grp) throws SQLException, Exception
	{
		boolean iftargetmodified = false;
		
		String command1 = "SELECT * FROM [User] " +
				          "INNER JOIN tblAssignment ON [User].PKUser = tblAssignment.TargetLoginID ";
		command1 = command1 + "WHERE ([User].PKUser = "+iTargetID+") AND ([User].FKDepartment <> tblAssignment.FKTargetDepartment) AND (tblAssignment.SurveyID = " +SurveyID + ") AND (tblAssignment.FKTargetDepartment = " + dep + ") OR";
		command1 = command1 + "([User].PKUser = "+iTargetID+") AND ([User].FKDivision <> tblAssignment.FKTargetDivision) AND (tblAssignment.SurveyID = "+SurveyID+ ") AND (tblAssignment.FKTargetDivision = " + div + ") OR";
		command1 = command1 + "([User].PKUser = "+iTargetID+") AND ([User].Group_Section <> tblAssignment.FKTargetGroup) AND (tblAssignment.SurveyID = "+SurveyID+ ") AND (tblAssignment.FKTargetGroup = " + grp + ")";
		
		//System.out.print(command1);
		
		//String sql = "SELECT FKDepartment, FKDivision, Group_Section FROM [User]";
		//sql = sql + "WHERE (PKUser  = "+ iTargetID+ ")";		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	  	   try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command1);
	  		
	  		   while(rs.next()) {
	  			   iftargetmodified = true;
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		
	  	   //if (ifanytargetmodified) System.out.println("Modified!!! True!!!\n");
	  	   //if (!ifanytargetmodified) System.out.println("not modified!!! False!!!\n");
		return iftargetmodified;
	}
	
	
	public boolean ifassignmodified (int SurveyID, int iTargetID, int assignmentid) throws SQLException, Exception
	{
		boolean ifanytargetmodified = false;
		
		String command1 = "SELECT * FROM [User] " +
				          "INNER JOIN tblAssignment ON [User].PKUser = tblAssignment.TargetLoginID ";
		command1 = command1 + "WHERE ([User].PKUser = "+iTargetID+") AND ([User].FKDepartment <> tblAssignment.FKTargetDepartment) AND (tblAssignment.AssignmentID = " +assignmentid + ") OR";
		command1 = command1 + "([User].PKUser = "+iTargetID+") AND ([User].FKDivision <> tblAssignment.FKTargetDivision) AND (tblAssignment.AssignmentID = "+assignmentid+ ")OR";
		command1 = command1 + "([User].PKUser = "+iTargetID+") AND ([User].Group_Section <> tblAssignment.FKTargetGroup) AND (tblAssignment.AssignmentID = "+assignmentid+ ")";
		
		//System.out.print(command1);
		
		//String sql = "SELECT FKDepartment, FKDivision, Group_Section FROM [User]";
		//sql = sql + "WHERE (PKUser  = "+ iTargetID+ ")";		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	  	   try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command1);
	  		
	  		   while(rs.next()) {
	  			   ifanytargetmodified = true;
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		
	  	   //if (ifanytargetmodified) System.out.println("Modified!!! True!!!\n");
	  	   //if (!ifanytargetmodified) System.out.println("not modified!!! False!!!\n");
		return ifanytargetmodified;
	}
	
	
	public int howmanychanged (int SurveyID, int iTargetID){
		int count  =0;
		
		String command1 = "SELECT * FROM [User] " +
				          "INNER JOIN tblAssignment ON [User].PKUser = tblAssignment.TargetLoginID ";
		command1 = command1 + "WHERE ([User].PKUser = "+iTargetID+") AND ([User].FKDepartment <> tblAssignment.FKTargetDepartment) AND (tblAssignment.SurveyID = " +SurveyID + ") OR";
		command1 = command1 + "([User].PKUser = "+iTargetID+") AND ([User].FKDivision <> tblAssignment.FKTargetDivision) AND (tblAssignment.SurveyID = "+SurveyID+ ")OR";
		command1 = command1 + "([User].PKUser = "+iTargetID+") AND ([User].Group_Section <> tblAssignment.FKTargetGroup) AND (tblAssignment.SurveyID = "+SurveyID+ ")";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		   try{
			   con=ConnectionBean.getConnection();
			   st=con.createStatement();
			   rs=st.executeQuery(command1);
			
			   while(rs.next()) {
				   count++;
			   }
				
		   }catch(Exception ex){
				System.out.println("howmanychanged - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}		
		return count;
		
	}
	
	
	public String TargetName(int TargetID){
		String FullName = "";
		String FamilyName = "";
		String GivenName = "";
		
        String command = "SELECT * FROM [User] WHERE PKUser = "+TargetID;
		
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   FamilyName = rs.getString("FamilyName");
	  			   GivenName = rs.getString("GivenName");
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
        
        FullName = FamilyName + " "+GivenName;
		return FullName;
	}
	
	public String TargetDiv(int TargetID){
		int Div = 0;
		String DivisionName = "";
		
        String command = "SELECT * FROM [User] WHERE PKUser = "+TargetID;
		
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   Div = rs.getInt("FKDivision");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
        
        command = "SELECT * FROM Division WHERE PKDivision = "+Div;

        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   DivisionName = rs.getString("DivisionName");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}      
        
        
        return DivisionName;
	}

	
	public String TargetGrp(int TargetID){
		int grp = 0;
		String groupName = "";
		
        String command = "SELECT * FROM [User] WHERE PKUser = "+TargetID;
		
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   grp = rs.getInt("Group_Section");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
        
        command = "SELECT * FROM [Group] WHERE PKGroup = "+grp;

        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   groupName = rs.getString("GroupName");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}      
        
        
        return groupName;
	}

	
	
	public String TargetDep(int TargetID){
		int Dep = 0;
		String DepartmentName = "";
		
        String command = "SELECT * FROM [User] WHERE PKUser = "+TargetID;
		
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   Dep= rs.getInt("FKDepartment");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
        
        command = "SELECT * FROM Department WHERE PKDepartment = "+Dep;

        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   DepartmentName = rs.getString("DepartmentName");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}      
        
        
        return DepartmentName;
	}
	
	
	public int CountAss(int TargetID, int SurveyID){
		int count = 0;
		
        String command = "SELECT COUNT(*) AS Expr1 FROM tblAssignment WHERE TargetLoginID = "+TargetID +"AND SurveyID = "+ SurveyID;
		
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   count = rs.getInt("Expr1");
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		
		
		return count;
	}
	
	public Vector[] GetAssignment(int TargetID, int SurveyID){
		Vector[] v = new Vector[100];
		
		int assignmentid = 0;
		String rater = "";
		String div = "";
		String dep = "";
		String grp = "";
		String date = "";
		int i= 0;
		
        String command = "SELECT * FROM tblAssignment WHERE TargetLoginID = "+TargetID +"AND SurveyID = "+ SurveyID;
		
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			  
	  			   
	  			   assignmentid = rs.getInt("AssignmentID");
	  			   rater = TargetName(rs.getInt("RaterLoginID"));
	  			   div = FindDiv(rs.getInt("FKTargetDivision"));
	  			   //System.out.println("division = "+ division);
	  			   dep = FindDep(rs.getInt("FKTargetDepartment"));
	  			   grp = FindGrp(rs.getInt("FKTargetGroup"));
	  			   date = rs.getString("EntryDate");
	  			   if(ifassignmodified(SurveyID, TargetID, assignmentid)){
	  			   v[i] = new Vector();
	  			   v[i].addElement(assignmentid);
	  			   v[i].addElement(rater);
	  			   v[i].addElement(div);
	  			   v[i].addElement(dep);
	  			   v[i].addElement(grp);
	  			   v[i].addElement(date);
	  			   i++;
	  			   }
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
                      
        
        return v;
	}
	
	public String FindDiv (int div){
		String command = "SELECT * FROM Division WHERE PKDivision = "+div;
        String DivisionName = "";
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   DivisionName = rs.getString("DivisionName");	  			   
	  		   }
	  		   
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}      
        
          return DivisionName;
	}
	
	
	public String FindGrp (int grp){
		
		String command = "SELECT * FROM [Group] WHERE PKGroup = "+grp;
        String groupName = "";
		Connection con = null;
        Statement st = null;
        ResultSet rs = null;
		
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   groupName = rs.getString("GroupName");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}      
        
        return groupName;        
	}
	
	public String FindDep (int dep){
		
		 String command = "SELECT * FROM Department WHERE PKDepartment = "+dep;
		 String DepartmentName = "";

		    Connection con = null;
	        Statement st = null;
	        ResultSet rs = null;
	        try{
		  		   con=ConnectionBean.getConnection();
		  		   st=con.createStatement();
		  		   rs=st.executeQuery(command);
		  		
		  		   while(rs.next()) {
		  			   DepartmentName = rs.getString("DepartmentName");	  			   
		  		   }
					
		  	   }catch(Exception ex){
					System.out.println("ifanymodified - "+ex.getMessage());
				}
				finally{
				ConnectionBean.closeRset(rs);//Close ResultSet
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
				}              
	        
	        return DepartmentName;
	}
	
	public void updateassignment(int assignmentid) throws SQLException, Exception
	{		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String updateopt = " IN ";
		
		//if (option == 1) updateopt = " = ";
		
		int TargetID = 0;
		String command = "SELECT * FROM tblAssignment WHERE AssignmentID = " + assignmentid;
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   TargetID = rs.getInt("TargetLoginID");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		
		
		int Div = 0;
		String DivisionName = "";
		
        command = "SELECT * FROM [User] WHERE PKUser = "+TargetID;
        
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   Div = rs.getInt("FKDivision");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
        
        int Dep = 0;
		String DepartmentName = "";
		
        command = "SELECT * FROM [User] WHERE PKUser = "+TargetID;

        
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   Dep= rs.getInt("FKDepartment");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
        
        int grp = 0;
		String groupName = "";
		
        command = "SELECT * FROM [User] WHERE PKUser = "+TargetID;
		

        
        try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(command);
	  		
	  		   while(rs.next()) {
	  			   grp = rs.getInt("Group_Section");	  			   
	  		   }
				
	  	   }catch(Exception ex){
				System.out.println("ifanymodified - "+ex.getMessage());
			}
			finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		
        
		String sql = "UPDATE tblAssignment SET FKTargetDivision="+Div+ ", FKTargetDepartment = "+ Dep + ", FKTargetGroup ="+ grp +" WHERE AssignmentID= "+assignmentid;
		
        
		try {
		    st = con.createStatement();
		    int updateCount = st.executeUpdate(sql);
		    System.out.println("updatecount = "+ updateCount);
		    // updateCount contains the number of updated rows
		} catch (SQLException e) {
		}finally{
			ConnectionBean.closeRset(rs);//Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
			}
		
	}
	
	/** Get Target and Rater ID
	*	@param int SurveyID
	*	
	*	@return	Vector
	*/
	public Vector getTargetRater(int SurveyID) throws SQLException, Exception 
	{
		Vector v = new Vector();
		
		String Sql = "SELECT * FROM [User] INNER JOIN tblSurvey a INNER JOIN ";
        Sql = Sql + "  tblAssignment ON a.SurveyID = tblAssignment.SurveyID ON [User].PKUser = tblAssignment.RaterLoginID INNER JOIN";
        Sql = Sql + " tblOrganization ON a.FKOrganization = tblOrganization.PKOrganization";
		Sql = Sql + " WHERE (a.SurveyID = "+SurveyID+")";
		Sql = Sql + " ORDER BY TargetLoginID";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(Sql);
  		
  		   while(rs.next()) {
  			   voUser vo = new voUser();
  			   vo.setNameSequence(rs.getInt("NameSequence"));
  			   vo.setTargetLoginID(rs.getInt("TargetLoginID"));
  			   vo.setRaterLoginID(rs.getInt("RaterLoginID"));
  			   vo.setRaterCode(rs.getString("RaterCode"));
  			   vo.setSurveyName(rs.getString("SurveyName"));
  			   vo.setRaterStatus(rs.getInt("RaterStatus"));
  			   v.add(vo);
  		   }
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTargetRater - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	
		
	public Vector getTargetAssignmentIDs(int SurveyID, int iTargetID) throws SQLException, Exception 
	{
		Vector v = new Vector();
		
		String command1 = "SELECT * FROM tblSurveyRating a, tblSurvey b, tblAssignment c";
		command1 = command1+" WHERE b.SurveyID = c.SurveyID AND a.SurveyID = b.SurveyID AND a.SurveyID = "+SurveyID+" AND TargetLoginID= "+iTargetID;
		
		String command = command1 +" ORDER BY c.RaterCode";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command);
  		
  		   while(rs.next()) {
  			   votblAssignment vo = new votblAssignment();
  			   vo.setAssignmentID(rs.getInt("AssignmentID"));
  			   v.add(vo);
  		   }
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTargetAssignmentID - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	public Vector getRaterAssignmentIDs(int SurveyID, int iRaterLoginID) throws SQLException, Exception 
	{
		Vector v = new Vector();
		
		String command1 = "SELECT * FROM tblSurveyRating a, tblSurvey b, tblAssignment c WHERE b.SurveyID = c.SurveyID AND a.SurveyID = b.SurveyID AND a.SurveyID = "+SurveyID;
		
		String command = command1 +" AND c.RaterLoginID = "+iRaterLoginID+" ORDER BY c.RaterLoginID, c.AssignmentID";									
		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command);
  		
  		   while(rs.next()) {
  			   votblAssignment vo = new votblAssignment();
  			   vo.setAssignmentID(rs.getInt("AssignmentID"));
  			   v.add(vo);
  		   }
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getRaterAssignmentID - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	public Vector getSurveyDetail(int SurveyID, int iTargetID) throws SQLException, Exception 
	{
		Vector v = new Vector();
		
		String command1 = "SELECT * FROM tblSurveyRating a, tblSurvey b, tblAssignment c";
		command1 = command1+" WHERE b.SurveyID = c.SurveyID AND a.SurveyID = b.SurveyID AND a.SurveyID = "+SurveyID+" AND TargetLoginID= "+iTargetID;
		
		command1 = command1 +" ORDER BY a.RatingTaskID , c.RTRelation ";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command1);
  		
  		   while(rs.next()) {
  			   votblAssignment vo = new votblAssignment();
  			   vo.setRatingTaskID(rs.getInt("RatingTaskID"));
  			   vo.setRatingTaskName(rs.getString("RatingTaskName"));
  			   vo.setLevelOfSurvey(rs.getInt("LevelOfSurvey"));
  			   vo.setAssignmentID(rs.getInt("AssignmentID"));
  			   v.add(vo);
  		   }
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getSurveyDetail - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	public Vector getSurveyDetail(int SurveyID) throws SQLException, Exception 
	{
		Vector v = new Vector();
		
		String command1 = "SELECT * FROM tblSurveyRating a, tblSurvey b, tblAssignment c WHERE b.SurveyID = c.SurveyID AND a.SurveyID = b.SurveyID AND a.SurveyID = "+SurveyID;
		
		command1 = command1 +" ORDER BY a.RatingTaskID , c.RTRelation ";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command1);
  		
  		   while(rs.next()) {
  			   votblAssignment vo = new votblAssignment();
  			   vo.setRatingTaskID(rs.getInt("RatingTaskID"));
  			   vo.setRatingTaskName(rs.getString("RatingTaskName"));
  			   vo.setLevelOfSurvey(rs.getInt("LevelOfSurvey"));
  			   vo.setAssignmentID(rs.getInt("AssignmentID"));
  			   v.add(vo);
  		   }
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getSurveyDetail - "+ex.getMessage());
			ex.printStackTrace();
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	/**
	 * Retrieves all Competencies based on AssignmentID
	 * 
	 * @param compID
	 * @param OrgID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Vector getCompetencies(int AssignmentID) throws SQLException, Exception {
		Vector v=new Vector();
		
		/*String resultSQL = "SELECT * FROM tblAssignment INNER JOIN tblResultCompetency ON tblAssignment.AssignmentID = tblResultCompetency.AssignmentID ";
		resultSQL = resultSQL +" INNER JOIN Competency ON tblResultCompetency.CompetencyID = Competency.PKCompetency";
		resultSQL = resultSQL +" WHERE (tblAssignment.AssignmentID = "+AssignmentID+")";*/
		
		//New query created by Ha 19/06/08 to get all the competency that been assigned to the provided assignment
		//Problem with old query: return empty if not raters rated for that target
		String resultSQL = "SELECT Competency.PKCompetency, Competency.CompetencyName, Competency.CompetencyDefinition ";
		resultSQL += "FROM Competency INNER JOIN"+
                      " tblSurveyCompetency ON Competency.PKCompetency = tblSurveyCompetency.CompetencyID ";
		resultSQL += "INNER JOIN" +
                      " tblAssignment ON tblSurveyCompetency.SurveyID = tblAssignment.SurveyID";
		resultSQL += " WHERE (tblAssignment.AssignmentID = "+AssignmentID +" )";
		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(resultSQL);
			while(rs.next()){
				/*By Hemilda Date 06/06/2008
				 add competencyid set to vector, because the result never show in the report, the id of competency always 0
				*/
				voCompetency vo=new voCompetency();
				vo.setCompetencyDefinition(rs.getString("CompetencyDefinition"));
				vo.setCompetencyName(rs.getString("CompetencyName"));
				//Changed by Ha 19/06/08 get PKCompetency instead of get CompetencyID
				vo.setCompetencyID(rs.getInt("PKCompetency"));
				//vo.setCompetencyID(rs.getInt("CompetencyId"));
				v.add(vo);
			
			}

		}catch(SQLException SE)
		{
			System.out.println("AssignTarget_Rater.java - getCompetencies - "+SE.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}

		return v;
	}
	
	/**
	 * get KBResult 
	 * @param iAssignmentID
	 * @param iKBID
	 * @param iRTID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public double getKBResult(int iAssignmentID, int iKBID, int iRTID) throws SQLException, Exception {
		
		String query = "SELECT * FROM tblResultBehaviour a, tblAssignment b WHERE a.AssignmentID = b.AssignmentID AND a.AssignmentID = "+iAssignmentID;
		query = query + " AND KeyBehaviourID = "+iKBID+" AND RatingTaskID = "+ iRTID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		/*db.openDB();
		Statement stmt = db.con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		if(rs.next())
			compID = rs.getInt("PKCompetency");*/
		
		double result = -1;
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

            if(rs.next())
            {
            	result = rs.getDouble("Result");
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("AssignTarget_Rater.java - getKBResult - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection

        }	
		return result;
	}
	
	/**
	 * get KBResult 
	 * @param iAssignmentID
	 * @param iKBID
	 * @param iRTID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public double getCompResult(int iAssignmentID, int iCompID, int iRTID) throws SQLException, Exception {
		
		String query = "SELECT * FROM tblResultCompetency a, tblAssignment b WHERE a.AssignmentID = b.AssignmentID AND a.AssignmentID = "+iAssignmentID;
		query = query + " AND CompetencyID = "+iCompID+" AND RatingTaskID = "+iRTID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		/*db.openDB();
		Statement stmt = db.con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		if(rs.next())
			compID = rs.getInt("PKCompetency");*/
		
		double result = -1;
		try
        {          
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

            if(rs.next())
            {
            	result = rs.getDouble("Result");
            	System.out.println("the result is "+result);
            }
            
        }
        catch(Exception E) 
        {
            System.err.println("AssignTarget_Rater.java - getCompResult - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection

        }	
		return result;
	}
	
	public Vector getAssignmentDetail(int iAssignmentID) throws SQLException, Exception 
	{
		Vector v = new Vector();
		
		String command1 = "SELECT * FROM tblAssignment WHERE AssignmentID="+iAssignmentID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command1);
  		
  		   while(rs.next()) {
  			   votblAssignment vo = new votblAssignment();
  			   vo.setRaterCode(rs.getString("RaterCode"));
  			   vo.setRTRelation(rs.getInt("RTRelation"));
  			   vo.setRTSpecific(rs.getInt("RTSpecific"));
  			   vo.setWave(rs.getInt("Wave"));
  			   vo.setRound(rs.getInt("Round"));
  			   v.add(vo);
  		   }
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAssignmentDetail - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	public Vector getAssignmentDetail(int iSurveyID, int iTargetID) throws SQLException, Exception 
	{
		Vector v = new Vector();
		
		String command1 = "SELECT * FROM tblSurveyRating a, tblSurvey b, tblAssignment c WHERE b.SurveyID = c.SurveyID AND a.SurveyID = b.SurveyID AND a.SurveyID = "+iSurveyID+" AND TargetLoginID= "+iTargetID;
		String command = command1 +" ORDER BY c.RaterCode";			
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command);
  		
  		   while(rs.next()) {
  			   votblAssignment vo = new votblAssignment();
  			   vo.setRaterCode(rs.getString("RaterCode"));
  			   vo.setAssignmentID(rs.getInt("AssignmentID"));
  			   v.add(vo);
  		   }
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAssignmentDetail - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	/** Get Target Detail
	*	@param int SurveyID
	*	
	*	@return	Vector
	*/
	public Vector getTargetDetail(int SurveyID, int RaterLoginID) throws SQLException, Exception 
	{
		Vector v = new Vector();
		
		String command1 = "SELECT * FROM tblSurveyRating a, tblSurvey b, tblAssignment c, tblOrganization d WHERE b.FKOrganization = d.PKOrganization AND b.SurveyID = c.SurveyID AND a.SurveyID = b.SurveyID AND a.SurveyID = "+SurveyID;
		String command = command1 +" AND c.RaterLoginID = "+RaterLoginID+" ORDER BY c.RaterLoginID, c.AssignmentID";
		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(command);
  		
  		   while(rs.next()) {
  			   voUser vo = new voUser();
  			   vo.setNameSequence(rs.getInt("NameSequence"));
  			   vo.setTargetLoginID(rs.getInt("TargetLoginID"));
  		
  			   v.add(vo);
  		   }
			
  	   }catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTargetDetail - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		
		
		return v;
	}
	
	public Vector getAllSurveyTargets(int SurveyID) throws SQLException, Exception 
	{
		Vector v = new Vector();
		
		String Sql = "SELECT distinct tblSurvey.SurveyName, tblAssignment.TargetLoginID, tblOrganization.NameSequence ";
		Sql += "FROM tblAssignment INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		Sql += "tblOrganization ON tblSurvey.FKOrganization = tblOrganization.PKOrganization ";
		Sql += "WHERE tblAssignment.SurveyID = " + SurveyID + " ORDER BY tblAssignment.TargetLoginID";			
		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(Sql);
  		
  	   		while(rs.next()) {
  			   voUser vo = new voUser();
  			   vo.setNameSequence(rs.getInt("NameSequence"));
  			   vo.setTargetLoginID(rs.getInt("TargetLoginID"));
  			   vo.setSurveyName(rs.getString("SurveyName"));
  			   v.add(vo);
  	   		}
			
  	   	}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAllSurveyTargets - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return v;
	}
	
	public Vector getAssignments (int iSurveyID, int iUserType, int iDiv, int iDept, int iGroup, int iFKUser) {
		Vector v = new Vector();
		
		String query ="SELECT * FROM tblAssignment a, [User] b, [Group] c ";
		query = query + "WHERE a.TargetLoginID = b.PKUser AND a.FKTargetGroup = c.PKGroup AND SurveyID="+ iSurveyID;
		
		/* Nomination module */			
		if(iUserType == 4)
		{	//User is Participant or Rater
			
			//Get list of Direct Reports of the user then Convert into String for SQL "IN" function to filter and show only DRs
			
			//String sSQL = "SELECT * FROM tblUserRelation WHERE (User1 = " + logchk.getPKUser() + ") AND (RelationType = 0)";		
			//ResultSet rsCheck = db.getRecord(sSQL);
			
			//Check whether the participant has no Relation, if No Relation, Participant can nominate anybody as target		
			//if (!rsCheck.next())
			//{
				String sArrUser = GFunc.putArrayListToString(UserList.listSpecificRelation(iFKUser, 1));
				if (sArrUser.equals(""))
					query = query + " AND (PKUser IN (''))";
				else
					query = query + " AND (PKUser IN (" + sArrUser + "))";
			//}
		}
		else
		{
		

			if(iDiv != 0 && iDiv != -1) 
					query = query + " AND FKTargetDivision = " + iDiv;
			
			
			if(iDept != 0 && iDept != -1)
					query = query + " AND FKTargetDepartment = " + iDept;
		
			if(iGroup != 0 && iGroup != -1)
					query = query + " AND FKTargetGroup = " + iGroup;
		
		}
		/* END Nomination module */
			
		query = query + " ORDER BY GroupName, PKGroup, RTRelation, RaterCode, LoginName, AssignmentID, RaterLoginID";
		
		//System.out.println(query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   System.out.println(" query "+query);
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()){
  			   votblAssignment vo=new votblAssignment();
  			   vo.setAssignmentID(rs.getInt("AssignmentID"));
  			   vo.setRTRelation(rs.getInt("RTRelation"));
  			   vo.setRaterCode(rs.getString("RaterCode"));
  			   vo.setRaterStatus(rs.getInt("RaterStatus"));
  			   vo.setRaterLoginID(rs.getInt("RaterLoginID"));
  			   vo.setTargetLoginID(rs.getInt("TargetLoginID"));
  			   vo.setLoginName(rs.getString("LoginName"));
  			   vo.setFKGroup(rs.getInt("PKGroup"));
  			   vo.setGroupName(rs.getString("GroupName"));
  				
  			   v.add(vo);
  		   }
			

		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAssignments - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		
		return v;
	}
	
	public Vector getTargetAssignments (int iSurveyID, int iUserType, int iDiv, int iDept, int iGroup, int iFKUser) {
		Vector v = new Vector();
		
		String query ="SELECT * FROM tblAssignment a, [User] b, [Group] c ";
		query = query + "WHERE a.TargetLoginID = b.PKUser AND a.FKTargetGroup = c.PKGroup AND SurveyID="+ iSurveyID;
		
		/* Nomination module */			
		if(iUserType == 4)
		{	//User is Participant or Rater
			
			//Get list of Direct Reports of the user then Convert into String for SQL "IN" function to filter and show only DRs
			
			//String sSQL = "SELECT * FROM tblUserRelation WHERE (User1 = " + logchk.getPKUser() + ") AND (RelationType = 0)";		
			//ResultSet rsCheck = db.getRecord(sSQL);
			
			//Check whether the participant has no Relation, if No Relation, Participant can nominate anybody as target		
			//if (!rsCheck.next())
			//{
				String sArrUser = GFunc.putArrayListToString(UserList.listSpecificRelation(iFKUser, 1));
				if (sArrUser.equals(""))
					query = query + " AND (PKUser IN (''))";
				else
					query = query + " AND (PKUser IN (" + sArrUser + "))";
			//}
		}
		else
		{
		

			if(iDiv != 0 && iDiv != -1) 
					query = query + " AND FKTargetDivision = " + iDiv;
			
			
			if(iDept != 0 && iDept != -1)
					query = query + " AND FKTargetDepartment = " + iDept;
		
			if(iGroup != 0 && iGroup != -1)
					query = query + " AND FKTargetGroup = " + iGroup;
		
		}
		/* END Nomination module */
			
		query = query + " ORDER BY GroupName, PKGroup, LoginName, AssignmentID, RaterLoginID";
		
		//System.out.println(query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
  	   	try{
  		   con=ConnectionBean.getConnection();
  		   st=con.createStatement();
  		   rs=st.executeQuery(query);
  		   
  		   while(rs.next()){
  			   votblAssignment vo=new votblAssignment();
  			   vo.setAssignmentID(rs.getInt("AssignmentID"));
  			   vo.setRTRelation(rs.getInt("RTRelation"));
  			   vo.setRaterCode(rs.getString("RaterCode"));
  			   vo.setRaterStatus(rs.getInt("RaterStatus"));
  			   vo.setRaterLoginID(rs.getInt("RaterLoginID"));
  			   vo.setTargetLoginID(rs.getInt("TargetLoginID"));
  			   vo.setLoginName(rs.getString("LoginName"));
  			   vo.setFKGroup(rs.getInt("PKGroup"));
  			   vo.setGroupName(rs.getString("GroupName"));
  				
  			   v.add(vo);
  		   }
			

		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAssignments - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		
		return v;
	}
	
	public Vector getUserList(int iSurveyID, int iFKUser, int iOrgID, int iDivID, int iDeptID, int iGroupID, int iUserType) {
		String query = "SELECT * FROM [User] WHERE (PKUser NOT IN (SELECT TargetLoginID FROM tblAssignment WHERE SurveyID= "+iSurveyID+")) AND FKUserType360 != 1 ";
		
		if(iGroupID != 0)
		{
			query = query + "AND Group_Section = "+iGroupID;
		}
		if(iDeptID != 0)
		{
			query = query + "AND FKDepartment = "+iDeptID;
		}
		if(iDivID != 0)
		{
			query = query + "AND FKDivision = "+iDivID;
		}
		
		query = query + "AND FKOrganization = "+ iOrgID +" ORDER BY ";
		
		if(getSortType() == 1)
			query = query + "FamilyName";
		else if(getSortType() == 2)
			query = query + "GivenName";
		else if(getSortType() == 3)
			query = query + "LoginName";

		if(getToggle() == 1)
			query = query + " DESC";
				
		
		
		/* Nomination module */			
		if(iUserType == 4)
		{	//User is Participant or Rater
			boolean bExist = existRelation(iFKUser);
			//Check whether the participant has no Relation, if No Relation, Participant can nominate anybody as target		
			if (!bExist)
			{
				//Get list of Direct Reports of the user then Convert into String for SQL "IN" function to filter and show only DRs
				String sArrUser = GFunc.putArrayListToString(UserList.listSpecificRelation(iFKUser, 1));
				if (sArrUser.equals(""))
					query = query + " AND (PKUser IN (''))";
				else
					query = query + " AND (PKUser IN (" + sArrUser + "))";
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

  	   		while(rs.next()){
  	   			voUser vo = new voUser();
  	   			vo.setPKUser(rs.getInt("PKUser"));
  	   			vo.setFamilyName(rs.getString("FamilyName"));
  	   			vo.setGivenName(rs.getString("GivenName"));
  	   			vo.setLoginName(rs.getString("LoginName"));
  	   			v.add(vo);
  	   		}
			

  	   	}catch(Exception ex){
  	   		System.out.println("AssignTarget_Rater.java - getUserList - "+ex.getMessage());
  	   	}
  	   	finally{
  	   		ConnectionBean.closeRset(rs); //Close ResultSet
  	   		ConnectionBean.closeStmt(st); //Close statement
  	   		ConnectionBean.close(con); //Close connection
  	   	}
		 
		return v;
		
		
	}
	
	public boolean existRelation(int iFKUser) {
		String query = "SELECT * FROM tblUserRelation WHERE (User1 = " + iFKUser + ") AND (RelationType = 0)";
		
//		System.out.println(query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
		boolean bExist = false;
  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(query);

  	   		if(rs.next()){
  	   			bExist = true;
  	   		}
			

		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - existRelation - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return bExist;
	}
	
	public Vector getUserList(int iSurveyID, int iSelectedTargetID, int iOrgID, int iDivID, int iDeptID, int iGroupID) {
		
		String sql = "SELECT * FROM [User] WHERE (PKUser NOT IN (SELECT RaterLoginID FROM tblAssignment WHERE TargetLoginID = "+iSelectedTargetID+" AND SurveyID= "+iSurveyID+")) AND FKUserType360 != 1 ";
		
		if(iGroupID != 0)
		{
		 	sql = sql + "AND Group_Section = "+iGroupID ;
		 	
		}
		if(iDeptID != 0)
		{
		 	sql = sql + "AND FKDepartment = "+iDeptID;
		 	
		}
		if(iDivID != 0)
		{
		 	sql = sql + "AND FKDivision = "+iDivID;
		 	
		}
		sql = sql + "AND FKOrganization = "+iOrgID+" ORDER BY ";
		
		// Changed by Ha 21/05/08 switch GivenName and FamilyName
		if(getSortType() == 1)
			sql = sql + "GivenName";
		else if(getSortType() == 2)
			sql = sql + "FamilyName";
		else if(getSortType() == 3)
			sql = sql + "LoginName";

		if(getToggle() == 1)
			sql = sql + " DESC";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
		Vector v = new Vector();
  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);

  	   		while(rs.next()){
  	   			v.add(new Integer(rs.getInt("PKUser")));
  	   		}
			

  	   	}catch(Exception ex){
  	   		System.out.println("AssignTarget_Rater.java - getUserList - "+ex.getMessage());
  	   	}
  	   	finally{
  	   		ConnectionBean.closeRset(rs); //Close ResultSet
  	   		ConnectionBean.closeStmt(st); //Close statement
  	   		ConnectionBean.close(con); //Close connection
  	   	}
		
		return v;
		
	}
	/**
	 * @author Clement
	 * @see i360_Pool/SendNominationEmail.jsp
	 * @return Vector of voUser objects which contain user information :
	 * PKUser, FamilyName, GivenName
	 * @since 9-jan-2008
	 */
	public Vector getUserList(int iNameSequence, int surveyOrg, int company, String selSurvey){
		String query ="SELECT DISTINCT  [User].PKUser, [User].GivenName AS GivenName, [User].FamilyName AS FamilyName, [User].FKOrganization ";
		query = query + "FROM tblAssignment INNER JOIN tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID INNER JOIN ";
		query = query + "[User] INNER JOIN tblUserRelation ON [User].PKUser = tblUserRelation.User2 ON tblAssignment.TargetLoginID = tblUserRelation.User1 ";
		query = query + "WHERE ";

		if(surveyOrg != 0)
			query = query+"[User].FKOrganization = "+surveyOrg;
		else
			query = query+"[User].FKCompanyID = "+company;
		
		if(iNameSequence == 0)
			query = query + " AND (tblSurvey.SurveyID = " + selSurvey + ") ORDER BY [User].FamilyName, [User].GivenName";
		else
			query = query + " AND (tblSurvey.SurveyID = " + selSurvey + ") ORDER BY [User].GivenName, [User].FamilyName";
		
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();
		
		try {
			/*
			db.openDB();
			Statement stmt = db.getCon().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			*/
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next()) {
				int iPKUser = rs.getInt("PKUser");
				String iGivenName = rs.getString("GivenName");
				String iFamilyName = rs.getString("FamilyName");
				
				voUser vo = new voUser();
				
				vo.setPKUser(iPKUser);
				vo.setGivenName(iGivenName);
				vo.setFamilyName(iFamilyName);
				
				v.add(vo);
			}
		} catch (SQLException SE) {
            System.err.println("AssignTarget_Rater.java - getUserList(int,int,int,String) " + SE);
        }finally{
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
        return v;
	}
	
	/**
	 * @author Clement
	 * @see i360_Pool/SendPartEmail.jsp
	 * @return Vector of voUser objects which contain user information :
	 * PKUser, FamilyName, GivenName
	 * @since 9-jan-2008
	 */
	public Vector getUserList(int iSurveyID){
		String query = "SELECT DISTINCT PKUser, FamilyName, GivenName FROM tblAssignment INNER JOIN [User] ON tblAssignment.RaterLoginID = [User].PKUser WHERE ";
		query = query + "(tblAssignment.SurveyID = " + iSurveyID + ") AND (tblAssignment.RaterLoginID <> 0) AND (tblAssignment.RaterStatus = 0) ";
		//Edited by Xuehai, 07 Jun 2011. Changed the order by columns.
		//query = query + "ORDER BY [User].GivenName, [User].FamilyName";
		query = query + "ORDER BY [User].FamilyName, [User].GivenName";
		//End of edited.
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Vector v = new Vector();
		
		try {
			/*
			db.openDB();
			Statement stmt = db.getCon().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			*/
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next()) {
				int iPKUser = rs.getInt("PKUser");
				String iFamilyName = rs.getString("FamilyName");
				String iGivenName = rs.getString("GivenName");
				
				voUser vo = new voUser();
				
				vo.setPKUser(iPKUser);
				vo.setFamilyName(iFamilyName);
				vo.setGivenName(iGivenName);
				
				v.add(vo);
			}
		} catch (SQLException SE) {
            System.err.println("AssignTarget_Rater.java - getUserList(int) " + SE);
        }finally{
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
        return v;
	}
	
	/** Get Total number of Targets for a particular survey
	*
	* @param SurveyID int
	*
	* @return TotTargets int
	* 
	* @author Desmond
	* @since v1.3.12.59 (13 Jan 2010)
	*
	*/
	public int getTotTargetsBySurveyID(int SurveyID) throws SQLException, Exception 
	{
		int TotTargets = 0;
		
		String command = "SELECT COUNT(DISTINCT (TargetLoginID)) AS TotTargets FROM [tblAssignment] WHERE SurveyID = " + SurveyID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	
  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(command);

  	   		if(rs.next())
  	   			TotTargets = rs.getInt("TotTargets");
		
		

  	   	}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getTotTargetsBySurveyID - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
		return TotTargets;
	} // End getTotTargetsBySurveyID()

/*
public int getPrelimQnAns(int prelimQnID, int assignID) throws SQLException, Exception{
	Vector dept = new Vector();
	
	String sql = "SELECT * FROM tbl_PrelimQnAns where FKPrelimQnID = "+prelimQnID+" and FKAssignmentID = " + assignID;
	Connection con = null;
	Statement st = null;
	ResultSet rs = null;
	try{
		con=ConnectionBean.getConnection();
		st=con.createStatement();
		rs=st.executeQuery(sql);
		
			while(rs.next()){
				rs.get()
			}
		      
	}catch(Exception ex){
		System.out.println("AssignTarget_Rater.java - getAllRaterDepartment - "+ex.getMessage());
	}
	finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
	}
	
	return dept;
}*/

	public Vector<String> getAddQnAns(int addQnID, int assignID) throws Exception{
		Vector<String> result = new Vector<String>();
		String sql = "SELECT * FROM tbl_AdditionalQnAns where FKAddQnID = "+addQnID+" and FKAssignmentID = " + assignID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				while(rs.next()){
					result.add(rs.getString("Answer"));
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAddQnAns - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return result;
	}
	
	public Vector<Integer>getAddQn(int surveyID) throws Exception{
		Vector<Integer> result = new Vector<Integer>();
		String sql = "SELECT * FROM tbl_AdditionalQn where FKSurveyID = "+surveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				while(rs.next()){
					result.add(rs.getInt("AddQnID"));
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAddQn - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return result;
	}
	
	public String getAddQnTitle(int qnID) throws Exception{
		String result = null;
		String sql = "SELECT * FROM tbl_AdditionalQn where AddQnID = "+qnID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				while(rs.next()){
					return rs.getString("Question");
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getAddQn - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return result;
	}

	
	public Vector<String> getPrelimQnOptions(int prelimQnID) throws Exception{
		Vector<String> result = new Vector<String>();
		String sql = "SELECT * FROM tbl_PrelimQn a  inner join tbl_PrelimQnRatingScale b" +
				" on a.PrelimRatingScaleID = b.PrelimRatingScaleID where PrelimQnID = "+prelimQnID +
				" order by RatingSequence";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				while(rs.next()){
					result.add(rs.getString("RatingScale"));
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getPrelimQnOptions - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return result;
	}
	
	public String getPrelimQnAns(int prelimQnID, int assignID) throws Exception{
		String result = new String();
		String sql = "SELECT * FROM tbl_PrelimQnAns where FKPrelimQnID = "+prelimQnID+" and FKAssignmentID = " + assignID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				while(rs.next()){
					return rs.getString("PrelimAnswer");
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getPrelimQnAns - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return result;
	}
	
	public Vector<Integer>getPrelimQn(int surveyID) throws Exception{
		Vector<Integer> result = new Vector<Integer>();
		String sql = "SELECT * FROM tbl_PrelimQn where FKSurveyID = "+surveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				while(rs.next()){
					result.add(rs.getInt("PrelimQnID"));
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getPrelimQn - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return result;
	}
	
	public String[] getAssignmentDetail(int targetID, int raterID, int NameSequence)throws SQLException
	{
		//db.openDB();
		String sDetail[] = new String[20];
		
		String sql = "SELECT * ";
		sql += "FROM Department INNER JOIN [User] ON Department.PKDepartment = [User].FKDepartment INNER JOIN ";
		sql += "tblOrganization ON [User].FKOrganization = tblOrganization.PKOrganization INNER JOIN ";
		sql += "Division ON [User].FKDivision = Division.PKDivision INNER JOIN [Group] ON ";
		sql += "[User].Group_Section = [Group].PKGroup INNER JOIN UserType ON ";
		sql += "[User].FKUserType = UserType.PKUserType INNER JOIN tblConsultingCompany ON ";
		sql += "[User].FKCompanyID = tblConsultingCompany.CompanyID INNER JOIN " +
				"tblAssignment on [User].PKUser = tblAssignment.RaterLoginID WHERE tblAssignment.RaterLoginID = "+ raterID +" and " +
						"TargetLoginID = " + targetID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		System.out.println(sql);

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
    	  		sDetail[19] = Integer.toString(rs.getInt("Wave"));
    	  		
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
	
	public String getPrelimQnTitle(int qnID) throws Exception{
		String result = null;
		String sql = "SELECT * FROM tbl_PrelimQn where PrelimQnID = "+qnID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			
				while(rs.next()){
					return rs.getString("Question");
				}
			      
		}catch(Exception ex){
			System.out.println("AssignTarget_Rater.java - getPrelimQnTitle - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		return result;
	}
}