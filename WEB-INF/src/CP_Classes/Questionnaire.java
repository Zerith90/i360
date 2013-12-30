package CP_Classes;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import java.util.Date;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voCompetency;
import CP_Classes.vo.voKeyBehaviour;
import CP_Classes.vo.voRatingTask;
import CP_Classes.vo.voUser;

/**
 * This class implements all the operations for Questionnaire, which is to be used in System Libraries, Survey, and Report..
 */

/**
 * 
 * Change Log
 * ==========
 *
 * Date        By				Method(s)            								Change(s) 
 * =========================================================================================================================================
					 
 * 29/06/12   Liu Taichen       isCompletedCompetency(int, int)             -Created method to get the completion status of a competency
 * 
 * 31/07/12   Albert       		addComment                     				Modified method so it can insert entry date automatically
 * 38/05/13	  Xukun				getRatingTasks							return all the RatingTask this survey using
 */
public class Questionnaire
{
	/**
	 * Declaration of new object of class Database. This object is declared private, which is to make sure that it is only accessible within this class Competency.
	 */
	private Database db;
	
	/**
	 * Declaration of new object of class Calculation. This object is declared private, which is to make sure that it is only accessible within this class Competency.
	 */
	private Calculation C;
	
	/**
	 * Declaration of new object of class User. This object is declared private, which is to make sure that it is only accessible within this class Competency.
	 */
	private User_Jenty U;
	
	/**
	 * Bean Variable to store Job Position.
	 */
	public String JobPost;
	
	/**
	 * Bean Variable to store the name of the target.
	 */
	public String Name;
	
	/**
	 * Bean Variable to store total competency for the particular questionnaire.
	 */
	public int TotalComp;
	
	/**
	 * Bean Variable to store the current total competency for the particular questionnaire.
	 * This variable will increase/decrease everytime the rater move next /previous.
	 */
	public int TotalCurrComp;
	
	/**
	 * Bean Variable to store the current competencyID for the particular questionnaire.
	 */
	public int CurrID;
	
	/**
	 * Bean Variable to store whether the particular rating scale has been checked.
	 */
	public int Checked;

	/**
	 * Bean Variable to store all the rating tasks for the particular questionnaire.
	 */
	public int RT[];	//rating task
	
	/**
	 * Bean Variable to store all the rating scales for the particular questionnaire.
	 */
	public int RS[];	// rating scale
	
	/**
	 * Bean Variable to store the assignmentID of the particular rater.
	 */
	public int AssignmentID;
	
	/**
	 * Bean Variable to store the future job ID for the particular questionnaire.
	 */
	public String FutureJob;
	
	/**
	 * Bean Variable to store the timeframe ID for the particular questionnaire.
	 */
	public String TimeFrame;
	
	/**
	 * Bean Variable to store the survey level for the particular questionnaire.
	 */
	public int surveyLevel;
	

	//add by Denise 14/12/2009
	/**
	 * Bean Variable to store the HideNA option for the particular questionnaire.
	 */
	public int HideNA;
	
	/**
	 * Bean Variable to determine the starting for the particular questionnaire, 
	 * so that it can be directed to the first question that has not been completed.
	 */
	public int StartID;


	public Questionnaire() {
		db = new Database();
		C = new Calculation();
		U = new User_Jenty();
		
		TotalCurrComp = 0;
		TotalComp = 0;
		Checked = 0;
		StartID = 0;
		
		HideNA = 0;
	}


	/**
	  * Retrieve the Competency or Key Behaviour List for a particular Survey.
	  * **NOT IN USE FOR CURRENT QUESTIONNAIRE**
	  */
	/*public ResultSet CompetencyOrKBList(int surveyID) {
		
		int surveyLevel = C.LevelOfSurvey(surveyID);
		String query = "";
		
		try {
			if(surveyLevel == 0) {
				query = query + "SELECT tblSurveyCompetency.CompetencyID, Competency.CompetencyDefinition ";
				query = query + "FROM tblSurveyCompetency INNER JOIN Competency ON ";
				query = query + "tblSurveyCompetency.CompetencyID = Competency.PKCompetency ";
				query = query + "WHERE tblSurveyCompetency.SurveyID = " + surveyID;
				query = query + " ORDER BY tblSurveyCompetency.CompetencyID, Competency.CompetencyDefinition";
			}
			else {
				query = query + "SELECT tblSurveyBehaviour.CompetencyID, Competency.CompetencyDefinition, ";
				query = query + "tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour FROM ";
				query = query + "tblSurveyBehaviour INNER JOIN Competency ON ";
				query = query + "tblSurveyBehaviour.CompetencyID = Competency.PKCompetency INNER JOIN ";
				query = query + "KeyBehaviour ON tblSurveyBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
				query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID;
				query = query + " ORDER BY tblSurveyBehaviour.CompetencyID, Competency.CompetencyDefinition, ";
				query = query + "tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour";
			}
			
			db.openDB();
			Statement stmt = db.con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			return rs;
		} catch (SQLException SE) {
			System.err.println(SE.getMessage());
		}
		return null;
	}*/
	
	/**
	 * @author xukun
	 * @param surveyID
	 * @return list of all prelim question
	 * @throws SQLException
	 */
	public Vector<PrelimQuestion> getPrelimQuestion(int surveyID){
		Vector<PrelimQuestion> v = new Vector<PrelimQuestion>();
		String query = "";
		query = query+"SELECT * from tbl_PrelimQn where FKSurveyID="+surveyID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try{
			con = ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next()) {
				PrelimQuestion pq = new PrelimQuestion(rs.getInt("PrelimQnID"),rs.getInt("FKSurveyID"), rs.getString("Question"), rs.getInt("PrelimRatingScaleID"));
				v.add(pq);
			}
		}
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getPrelimQuestion - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		Collections.sort(v);
		return v;
	}
	//xukun change
	public int getPrelimQnOptionNum(int scaleID)throws Exception{
		int result = 0;
		String query = "";
		query = query+"SELECT count(*) from tbl_PrelimQnRatingScale where PrelimRatingScaleID="+scaleID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try{
			con = ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				result = rs.getInt(1);
			}
			
		}catch(Exception E){
			return -1;
		}finally{
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
		}
		return result;
	}
	
	/**
	 * @author xukun
	 * @param surveyID
	 * @return list of all additional questions
	 * @throws SQLException
	 */
	public Vector<AdditionalQuestion> getAdditionalQuestion(int surveyID){
		Vector<AdditionalQuestion> v = new Vector<AdditionalQuestion>();
		String query = "";
		query = query+"SELECT * from tbl_AdditionalQn where FKSurveyID="+surveyID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
	   try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

    		while(rs.next()) {
    			AdditionalQuestion adq = new AdditionalQuestion(rs.getInt("AddQnID"), rs.getInt("FKSurveyID"), rs.getString("Question"));
    			v.add(adq);
    		}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getAdditionalQuestion - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		Collections.sort(v);
		return v;
	}

	/**
	  * Retrieve the Competency or Key Behaviour List for a particular Survey.
	 * @throws SQLException 
	 * Change by Hemilda Date 13/08/2008
	 * Add competency name to show in questionnaire
	  */
	public Vector getCompetency(int surveyID) throws SQLException {
		Vector v = new Vector();
		
	//	int surveyLevel = C.LevelOfSurvey(surveyID);
		String query = "";
		
		//if(surveyLevel == 0) {
			query = query + "SELECT tblSurveyCompetency.CompetencyID, Competency.CompetencyDefinition , Competency.CompetencyName ";
			query = query + "FROM tblSurveyCompetency INNER JOIN Competency ON ";
			query = query + "tblSurveyCompetency.CompetencyID = Competency.PKCompetency ";
			query = query + "WHERE tblSurveyCompetency.SurveyID = " + surveyID;
			query = query + " ORDER BY tblSurveyCompetency.CompetencyID, Competency.CompetencyDefinition";
		//}
		/*else {
			query = query + "SELECT tblSurveyBehaviour.CompetencyID, Competency.CompetencyDefinition, ";
			query = query + "tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour FROM ";
			query = query + "tblSurveyBehaviour INNER JOIN Competency ON ";
			query = query + "tblSurveyBehaviour.CompetencyID = Competency.PKCompetency INNER JOIN ";
			query = query + "KeyBehaviour ON tblSurveyBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
			query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID;
			query = query + " ORDER BY tblSurveyBehaviour.CompetencyID, Competency.CompetencyDefinition, ";
			query = query + "tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour";
		}*/
		//System.out.println("get competency "+ query);
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

    		while(rs.next()) {
    			voCompetency vo = new voCompetency();
    			vo.setCompetencyID(rs.getInt("CompetencyID"));
    			vo.setCompetencyDefinition(rs.getString("CompetencyDefinition"));
    			vo.setCompetencyName(rs.getString("CompetencyName"));
    			v.add(vo);
    		}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getCompetency - " + E);
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
	  * Retrieve the Competency or Key Behaviour List for a particular Survey.
	 * @throws SQLException 
	  */
	public Vector getKBSurvey(int surveyID, int compID) throws SQLException {
		Vector v = new Vector();

		String query = "";
	
		query = query + "SELECT tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour FROM ";
		query = query + "tblSurveyBehaviour INNER JOIN ";
		query = query + "KeyBehaviour ON tblSurveyBehaviour.KeyBehaviourID = KeyBehaviour.PKKeyBehaviour ";
		query = query + "WHERE tblSurveyBehaviour.SurveyID = " + surveyID + " and tblSurveyBehaviour.CompetencyID = "+ compID;
		query = query + " ORDER BY ";
		query = query + "tblSurveyBehaviour.KeyBehaviourID, KeyBehaviour.KeyBehaviour";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
    		while(rs.next()) {
    			voKeyBehaviour voKB = new voKeyBehaviour();
    			voKB.setKeyBehaviourID(rs.getInt("KeyBehaviourID"));
    			voKB.setKeyBehaviour(rs.getString("KeyBehaviour"));
    			v.add(voKB);
    		}

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getKBSurvey - " + E);
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
	  * Retrieve the total Competencies / Key Behaviours for a particular Survey.
	  */
	public int TotalList(int surveyID, int type) {
	
		String query = "";
		int total = 0;
		
		if(type == 0) 
			query = query + "Select count(*) from tblSurveyCompetency where SurveyID = " + surveyID;
		else {
			query = query + "Select count(*) from tblSurveyBehaviour where SurveyID = " + surveyID;
		}

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        
    		if(rs != null && rs.next())
    	    {
    			total = rs.getInt(1);
    	  	}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - TotalList - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return total;
	}

	/**
	  * Retrieve the total Competencies / Key Behaviours for a particular Survey.
	  * **NOT IN USE FOR CURRENT QUESTIONNAIRE**
	  */
	public int TotalList(int surveyID) {
		int surveyLevel = C.LevelOfSurvey(surveyID);
		String query = "";
		int total = 0;
		
		
		if(surveyLevel == 0) 
			query = query + "Select count(*) from tblSurveyCompetency where SurveyID = " + surveyID;
		else {
			query = query + "Select count(*) from tblSurveyBehaviour where SurveyID = " + surveyID;
		}
								
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

			if(rs.next())
				total = rs.getInt(1);
			
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - TotalList - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
		return total;
	}


	/**
	  * This method returns the Key Behaviour Definition given the KB ID.
	  * **NOT IN USE FOR CURRENT QUESTIONNAIRE**
	  */
	public String getKB(int pkKB) {
		
		String query = "Select KeyBehaviour from KeyBehaviour where PKKeyBehaviour = " + pkKB;
		
		String sKB = "";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

			if(rs.next())
				sKB = rs.getString(1);
			
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getKB - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
			
		return sKB;
		
	}


/************************************************* RATING TASK ***************************************************/

	/**
	  * This method is used to retrieve the Rating Task and Rating Scale assigned to each survey.
	  */
	public Vector getSurveyRating(int surveyID) {
		Vector v = new Vector();
		
		String query = "Select * from tblSurveyRating where SurveyID = " + surveyID;
		query = query + " order by RatingTaskID";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

    		while(rs != null && rs.next())
    	    {
    			int [] id = new int[2];
    			id[0] = rs.getInt("RatingTaskID");		// store all the ratings ID in an array
    			id[1] = rs.getInt("ScaleID");
    			v.add(id);
    	  	}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getSurveyRating - " + E);
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
	 * Get the total rating tasks for the particular survey.
	 */
	public int getTotalSurveyRating(int surveyID) {
		int total = 0;
		
		String query = "Select count(*) from tblSurveyRating where SurveyID = " + surveyID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
           
    		if(rs != null && rs.next())
    	    {
    			total = rs.getInt(1);
    	  	}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getTotalSurveyRating - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return total;
	}

	/**
	 * Get the rating task name given the rating task ID.
	 * **NOT IN USE FOR CURRENT QUESTIONNAIRE**
	 */
	public String getRatingTask(int RatingTaskID) {
		String sRT = "";
		
		String query = "Select RatingTask from tblRatingTask where RatingTaskID = " + RatingTaskID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
           
    		if(rs != null && rs.next())
    	    {
    			sRT = rs.getString(1);
    	  	}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getRatingTasl - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return sRT;
	}
		
	
	/**
	 * Retrieves all the rating scale value given the specific scaleID.
	 */
	public Vector getRatingScale(int RatingScaleID) {
		Vector v = new Vector();
		
		String query = "Select * from tblScaleValue where ScaleID = " + RatingScaleID;
		query = query + " order by LowValue, HighValue";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try 
        {          
			con=ConnectionBean.getConnection();
	  		st=con.createStatement();
	  		rs=st.executeQuery(query);
           
    		while(rs != null && rs.next())
    	    {
    	    	String [] sRS = new String[3];
    	   
    	    	sRS[0] = Integer.toString(rs.getInt("LowValue"));
    	    	sRS[1] = Integer.toString(rs.getInt("HighValue"));
    	    	sRS[2] = rs.getString("ScaleDescription");
    	    	v.add(sRS);
    	  	}

        }
        catch(Exception E) 
        {
        	System.err.println("Questionnaire.java - getRatingscale - " + E);
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
	 * @author xukun
	 * @param surveyID
	 * @return
	 */
	public TreeMap<Integer, String> getRatingTasks(int surveyID){
		TreeMap<Integer, String> result = new TreeMap<Integer, String>(Collections.reverseOrder());
		String sql = "SELECT tblRatingTask.RatingTaskID, RatingTask from tblSurveyRating inner join tblRatingTask on " +
				"tblSurveyRating.RatingTaskID = tblRatingTask.RatingTaskID where SurveyID="+surveyID;
				
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try 
        {          
			con=ConnectionBean.getConnection();
	  		st=con.createStatement();
	  		rs=st.executeQuery(sql);
           
    		while(rs != null && rs.next())
    	    {
    	    	result.put(rs.getInt("RatingTaskID"), rs.getString("RatingTask"));
    	  	}

        }
        catch(Exception E) 
        {
        	System.err.println("Questionnaire.java - getRatingTasks - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		return result;
	}
	
	public boolean isKBHidden(int surveyID){
		boolean result = false;
		String sql = "SELECT hideKBDesc from tblSurvey where SurveyID = "+surveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try 
        {          
			con=ConnectionBean.getConnection();
	  		st=con.createStatement();
	  		rs=st.executeQuery(sql);
           
    		while(rs != null && rs.next())
    	    {
    	    	if(rs.getInt("hideKBDesc")==1){
    	    		result = true;
    	    	}
    	  	}

        }
        catch(Exception E) 
        {
        	System.err.println("Questionnaire.java - isKBHidden - " + E);
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
	 * Retrieves all the rating scale value given the specific scaleID.
	 * @param RatingScaleID
	 * @return
	 * @author Maruli
	 * **NOT IN USE FOR CURRENT QUESTIONNAIRE**
	 */
	/*public ResultSet getRatingScaleDescending(int RatingScaleID) {
		try {
			String query = "SELECT * FROM tblScaleValue WHERE ScaleID = " + RatingScaleID;
			query = query + " ORDER BY LowValue DESC, HighValue DESC";
			
			db.openDB();
			Statement stmt = db.con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			return rs;
		} catch (SQLException SE) {
			System.err.println(SE.getMessage());
		}
		return null;
	}*/
	
	/**
	 * Get total rating scale value under the particular scale.
	 */
	public int getTotalRS(int ScaleID) {
		int total = 0;
		String query = "Select count(*) from tblScaleValue where ScaleID = " + ScaleID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

    		if( rs!= null && rs.next()){
    			total = rs.getInt(1);		
    		}

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - gettotalRS- " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return total;
	}
	

	/**
	  * Get the AssignmentID based on SurveyID, TargetLoginID, and RaterLoginID assigned to the particular Survey.
	  */
	public int AssignmentID(int surveyID, int targetID, int raterID) {
		int iAssignmentID = 0;
		
		String query = "Select AssignmentID from tblAssignment where ";
		query = query + "SurveyID = " + surveyID + " and RaterLoginID = " + raterID;
		query = query + " and TargetLoginID = " + targetID;
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
	           
    		if(rs != null && rs.next())
    	    {
    	    	iAssignmentID = rs.getInt("AssignmentID");
    	  	}
           
        }
        catch(Exception E) 
        {
        	System.err.println("Questionnaire.java - AssignmentID - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }

		return iAssignmentID;
	}

/**************************************************** SURVEY INFO ***************************************************/

	/**
	 * Retrieves the survey information from tblSurvey.
	 * This is basically to get the Future Job and Time Frame ID
	 */
	public String getSurveyInfo(int SurveyID) {
		String sSurveyName = null;
		
		String query = "Select * from tblSurvey where SurveyID = " + SurveyID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
           
    		if(rs != null && rs.next())
    	    {
    			sSurveyName = rs.getString("SurveyName");
    	  	}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getSurveyInfo - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return sSurveyName;
	}
	
	/**
	 * Get the future job decription given the surveyID.
	 */
	public String FutureJob(int surveyID) {
		String sFutureJob = null;
		
		String query = "Select JobPosition from tblJobPosition ";
		query = query + "join tblSurvey on tblSurvey.JobFutureID = tblJobPosition.JobPositionID ";
		query = query + "where SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
    		if(rs != null && rs.next())
    	    {
    	    	sFutureJob = rs.getString("JobPosition");
    	  	}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - FutureJob - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
	
	
		return sFutureJob;
	}

	/**
	 * Get the time frame description given the SurveyID.
	 */
	public String TimeFrame(int SurveyID) {
		String sTimeFrame = null;
		
		String query = "Select tblTimeFrame.TimeFrame from tblTimeFrame JOIN ";
		query = query + "tblSurvey ON tblSurvey.TimeFrameID = tblTimeFrame.TimeFrameID ";
		query = query + "where SurveyID = " + SurveyID;
			
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
	           
    		if(rs != null && rs.next())
    	    {
    	    	sTimeFrame = rs.getString("TimeFrame");
    	  	}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - Time frame - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }

		return sTimeFrame;
	}


/*********************************************** ADD RESULTS *******************************************************/

	
	/**
	 * Add a new record to the database when Rater fill in the questionnaire.
	 * Competency Level Questionnaire will be added into tblResultCompetency.
	 * Key Behaviour Level Questionnaire will be added into tblResultBehaviour.
	 * @param AssignmentID
	 * @param CompOrKBID
	 * @param RatingTaskID
	 * @param Result
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean addResult(int AssignmentID, int CompOrKBID, int RatingTaskID, float Result) throws SQLException, Exception 
	{
		boolean bIsAdded = false;
		float exist = CheckOldResultExist(AssignmentID, CompOrKBID, RatingTaskID);
		
		int surveyLevel = C.SurveyLevelByAssignmentID(AssignmentID);
		String query = "";
		
		if(surveyLevel == 0) {
			query = query + "Insert into tblResultCompetency(AssignmentID, CompetencyID, RatingTaskID, Result, EntryDate) ";
			query = query + "values (" + AssignmentID + ", " + CompOrKBID + ", " + RatingTaskID;
			query = query + ", " + Result + ", getDate())";
		}
		else {
			query = query + "Insert into tblResultBehaviour(AssignmentID, KeyBehaviourID, RatingTaskID, Result, EntryDate) ";
			query = query + "values (" + AssignmentID + ", " + CompOrKBID + ", " + RatingTaskID + ", " + Result + ", getDate())";
		}
		
		if(exist < 0) {
			
			Connection con = null;
			Statement st = null;

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
	            System.err.println("Questionnaire.java - addResult - " + E);
	        }
	        finally
	        {
		        ConnectionBean.closeStmt(st); //Close statement
		        ConnectionBean.close(con); //Close connection
	        }
		}
		return bIsAdded;
	}
	
	/**
	 * Delete result from db.
	 * This is used in import questionnaire.
	 */
	public boolean deleteResult(int AssignmentID, int CompOrKBID, int RatingTaskID) throws SQLException, Exception {
		
		int surveyLevel = C.SurveyLevelByAssignmentID(AssignmentID);
		String query = "";
		
		if(surveyLevel == 0) {		
			query = "Delete from tblResultCompetency ";
			query +=" where AssignmentID = " + AssignmentID + " and CompetencyID = " + CompOrKBID;
			query +=" and RatingTaskID = " + RatingTaskID;
		}
		else {
			query = "Delete from tblResultBehaviour ";
			query +=" where AssignmentID = " + AssignmentID + " and KeyBehaviourID = " + CompOrKBID;
			query +=" and RatingTaskID = " + RatingTaskID;
		}
			
		Connection con = null;
		Statement st = null;


		boolean bIsDeleted = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
			bIsDeleted=true;

		} 
		catch(Exception E) 
        {
            System.err.println("Questionnaire.java - deleteResult - " + E);
        }
        finally
        {
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return bIsDeleted;
	}
	
	
	/**
	 * Delete comment from db.
	 * This is used in import questionnaire.
	 */
	public boolean deleteComment(int AssignmentID, int CompID, int KBID) throws SQLException, Exception 
	{
		String	query = "Delete from tblComment ";
		query +=" where AssignmentID = " + AssignmentID + " and CompetencyID = " + CompID;
		query += " and KeyBehaviourID = " + KBID;
		
		Connection con = null;
		Statement st = null;


		boolean bIsDeleted = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
			bIsDeleted=true;

		} 
		catch(Exception E) 
        {
            System.err.println("Questionnaire.java - deleteComment - " + E);
        }
        finally
        {
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return bIsDeleted;
	}
	
	/**
	  * Edit the new rating to the database when Rater importing the questionnaire.
	  * Competency Level Questionnaire will be added in to tblResultCompetency.
	  * Key Behaviour Level Questionnaire will be added in tblResultBehaviour.
	  */
	public boolean editResult(int AssignmentID, int CompOrKBID, int RatingTaskID, int Result) throws SQLException, Exception 
	{		
		int surveyLevel = C.SurveyLevelByAssignmentID(AssignmentID);
		String query = "";
	
		if(surveyLevel == 0) {		
			query = "Update tblResultCompetency set Result = " + Result + ", EntryDate=getDate()";
			query +=" where AssignmentID = " + AssignmentID + " and CompetencyID = " + CompOrKBID;
			query +=" and RatingTaskID = " + RatingTaskID;
		}
		else {
			query = "Update tblResultBehaviour set Result = " + Result + " , EntryDate=getDate()";
			query +=" where AssignmentID = " + AssignmentID + " and KeyBehaviourID = " + CompOrKBID;
			query +=" and RatingTaskID = " + RatingTaskID;
		}
			
		Connection con = null;
		Statement st = null;


		boolean bIsUpdated = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
			bIsUpdated=true;

		} 
		catch(Exception E) 
        {
            System.err.println("Questionnaire.java - editResult - " + E);
        }
        finally
        {
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return bIsUpdated;
	}
	
	/**
	 * Update rater status.
	 */
	public boolean SetRaterStatus(int assignmentID, int status) 
	{
		String query = "";
		
		query = query + "Update tblAssignment Set RaterStatus = " + status;
		query = query + " WHERE AssignmentID = " + assignmentID;

		
		Connection con = null;
		Statement st = null;


		boolean bIsUpdated = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
			bIsUpdated=true;

		} 
		catch(Exception E) 
        {
            System.err.println("Questionnaire.java - setRaterStatur - " + E);
        }
        finally
        {
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
        return bIsUpdated;
        
	}



/************************************************** OLD RESULTS ****************************************************/

	/**
	 * Retrieves the existing result that has been filled in before.
	 */
	public Vector QuestionnaireOldResult(int assignmentID) 
	{
		int surveyLevel = C.SurveyLevelByAssignmentID(assignmentID);
		String query = "";
			
		Vector v = new Vector();
		
		if(surveyLevel == 0) {		
			query = query + "select CompetencyID, RatingTaskID, Result from tblResultCompetency ";
			query = query + "where AssignmentID = " + assignmentID;
			query = query + " order by CompetencyID, RatingTaskID";
		}
		else {
			query = query + "SELECT tblResultBehaviour.AssignmentID, KeyBehaviour.FKCompetency, ";
			query = query + "tblResultBehaviour.KeyBehaviourID, tblResultBehaviour.RatingTaskID, ";
			query = query + "tblResultBehaviour.Result FROM KeyBehaviour INNER JOIN tblResultBehaviour ";
			query = query + "ON KeyBehaviour.PKKeyBehaviour = tblResultBehaviour.KeyBehaviourID ";
			query = query + "WHERE tblResultBehaviour.AssignmentID = " + assignmentID;
			query = query + " ORDER BY KeyBehaviour.FKCompetency, tblResultBehaviour.KeyBehaviourID, ";
			query = query + "tblResultBehaviour.RatingTaskID";
		}
			
		return v;
			
	}
	
	/**
	 * Count the total results that has been filled in.
	 * This is to check whether the rater has completed the questionnaire, so that we won't allow them to click FINISH.
	 */
	public int TotalResult(int assignmentID) {
		int surveyLevel = C.SurveyLevelByAssignmentID(assignmentID);
		String query = "";
		int total = 0;
		
		if(surveyLevel == 0)		
			query = query + "select count(*) from tblResultCompetency where AssignmentID = " + assignmentID;
		else 
			query = query + "select count(*) from tblResultBehaviour where AssignmentID = " + assignmentID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
           
            if(rs != null && rs.next())
            	total = rs.getInt(1);

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - TotalResult- " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return total;
	}
	
	
	/**
	 * Count the total results that has been filled in for each Competency.
	 * This is for display purpose, first open questionnaire will display the question that has not been answered.
	 * remove KBID
	 */
	public int TotalResult(int assignmentID, int compID) {
		int surveyLevel = C.SurveyLevelByAssignmentID(assignmentID);
		String query = "";
		int total = 0;	
		
		if(surveyLevel == 0) {
			query = query + "select count(*) from tblResultCompetency where AssignmentID = " + assignmentID;
			query = query + " and CompetencyID = " + compID + " group by CompetencyID";
		}
		else {
			/*query = query + "select count(*) from tblResultBehaviour where AssignmentID = " + assignmentID;
			query = query + " and KeyBehaviourID = " + KBID + " group by KeyBehaviourID";
			*/
			
			query += "SELECT COUNT(*) ";
			query += "FROM tblResultBehaviour INNER JOIN ";
			query += "tblAssignment ON tblResultBehaviour.AssignmentID = tblAssignment.AssignmentID INNER JOIN ";
			query += "tblSurveyBehaviour ON tblSurveyBehaviour.SurveyID = tblAssignment.SurveyID AND ";
			query += "tblSurveyBehaviour.KeyBehaviourID = tblResultBehaviour.KeyBehaviourID ";
			query += "WHERE (tblResultBehaviour.AssignmentID = "+assignmentID+") AND (tblSurveyBehaviour.CompetencyID = "+compID+")";
			query += " group by CompetencyID";
			
		}
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
            if(rs != null && rs.next())
            	total = rs.getInt(1);
            
      		rs.close();
      		rs = null;
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - TotalResult- " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return total;
	}
	
	/**
	 * Update the existing result if the rater change the rating. 
	 * @param assignmentID
	 * @param ID
	 * @param RatingTaskID
	 * @param Result
	 */
	public boolean updateOldResult(int assignmentID, int ID, int RatingTaskID, float Result) 
	{
		String query = "";
		int surveyLevel = C.SurveyLevelByAssignmentID(assignmentID);
		
		
		if(surveyLevel == 0) {
			query = query + "Update tblResultCompetency ";
			query = query + "SET Result = " + Result +", EntryDate = getDate()";
			query = query + " WHERE AssignmentID = " + assignmentID + " AND CompetencyID = " + ID;
			query = query + " AND RatingTaskID = " + RatingTaskID;
		}
		else {
			query = query + "Update tblResultBehaviour ";
			query = query + "SET Result = " + Result +", EntryDate = getDate()";
			query = query + " WHERE AssignmentID = " + assignmentID;
			query = query + " AND KeyBehaviourID = " + ID;
			query = query + " AND RatingTaskID = " + RatingTaskID;
		}
		
		Connection con = null;
		Statement st = null;


		boolean bIsUpdated = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
				bIsUpdated=true;

		}
		catch(Exception E)
		{
            System.err.println("Group.java - updateOldReuslt - " + E);
		}
		finally
        {

			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

        }
		
		return bIsUpdated;
		
	}
	
	
	/**
	 * Check whether the particular question has been answered previously.
	 * If yes, then it will return the Result, if no then we will need to add in the new record to the Database.
	 * @param assignmentID
	 * @param ID
	 * @param RatingTaskID
	 * @return Previous score value
	 */
	public float CheckOldResultExist(int assignmentID, int ID, int RatingTaskID) {
		int surveyLevel = C.SurveyLevelByAssignmentID(assignmentID);
		String query = "";
		
		float result = -1;
		
		if(surveyLevel == 0) {
			query = query + "select Result from tblResultCompetency ";
			query = query + " WHERE AssignmentID = " + assignmentID + " AND CompetencyID = " + ID;
			query = query + " AND RatingTaskID = " + RatingTaskID;
		}
		else {
			query = query + "SELECT Result from tblResultBehaviour ";
			query = query + " WHERE AssignmentID = " + assignmentID + " AND KeyBehaviourID = " + ID;
			query = query + " AND RatingTaskID = " + RatingTaskID;
		}
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

            if(rs.next())
				result = rs.getFloat(1);

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - checkOldresultExist - " + E);
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
	  * Retrieve user name from [User].
	  */
	public String UserName(int orgID, int pkUser) {
		String query = "";
		int nameSequence = U.NameSequence(orgID);
		String name = null;
		
		if(nameSequence == 0) {
			query = query + "select FamilyName + ' ' + GivenName from [User] ";
			query = query + " WHERE PKUser = " + pkUser;
		} else {
			query = query + "select GivenName + ' ' + FamilyName from [User] ";
			query = query + " WHERE PKUser = " + pkUser;
		}
				
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
	           
    		if(rs != null && rs.next())
    	    {
    	    	name = rs.getString(1);
    	  	}
        
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - UserName - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }


		return name;
	}




/************************************************** BEAN VARIABLES *************************************************/

	/**
	 * Store bean variable job position.
	 */
	public void setJobPost(String JobPost) {
		this.JobPost = JobPost;
	}

	/**
	 * Get bean variable job position.
	 */
	public String getJobPost() {
		return JobPost;
	}

	/**
	 * Store bean variable target name.
	 */
	public void setName(String Name) {
		this.Name = Name;
	}

	/**
	 * Get bean variable target name.
	 */
	public String getName() {
		return Name;
	}

	/**
	 * Store bean variable total competency.
	 */
	public void setTotalComp(int TotalComp) {
		this.TotalComp = TotalComp;
	}

	/**
	 * Get bean variable total competency.
	 */
	public int getTotalComp() {
		return TotalComp;
	}

	/**
	 * Store bean variable of current total competency.
	 */
	public void setTotalCurrComp(int TotalCurrComp) {
		this.TotalCurrComp = TotalCurrComp;
	}

	/**
	 * Get bean variable of current total competency.
	 */
	public int getTotalCurrComp() {
		return TotalCurrComp;
	}

	/**
	 * Store bean variable of the rating tasks ID.
	 */
	public void setRT(int RT[]) {
		this.RT = RT;
	}

	/**
	 * Get bean variable of the rating tasks ID.
	 */
	public int [] getRT() {
		return RT;
	}

	/**
	 * Store bean variable of the rating scales ID.
	 */
	public void setRS(int RS[]) {
		this.RS = RS;
	}

	/**
	 * Get bean variable of the rating scales ID.
	 */
	public int [] getRS() {
		return RS;
	}

	/**
	 * Store bean variable of the assignmentID.
	 */
	public void setAssignmentID(int AssignmentID) {
		this.AssignmentID = AssignmentID;
	}

	/**
	 * Get bean variable of the assignmentID.
	 */
	public int getAssignmentID() {
		return AssignmentID;
	}

	/**
	 * Store bean variable of the job future id.
	 */
	public void setFutureJob(String FutureJob) {
		this.FutureJob = FutureJob;
	}

	/**
	 * Get bean variable of the job future id.
	 */
	public String getFutureJob() {
		return FutureJob;
	}

	/**
	 * Store bean variable of the timeframe ID.
	 */
	public void setTimeFrame(String TimeFrame) {
		this.TimeFrame = TimeFrame;
	}

	/**
	 * Get bean variable of the timeframe ID.
	 */
	public String getTimeFrame() {
		return TimeFrame;
	}

	/**
	 * Store bean variable of the survey level.
	 */
	public void setSurveyLevel(int surveyLevel) {
			this.surveyLevel = surveyLevel;
	}

	/**
	 * Get bean variable of the survey level.
	 */
	public int getSurveyLevel() {
			return surveyLevel;
	}
	
	/**
	 * Store the checked status to bean variable.
	 */
	public void setChecked(int Checked) {
			this.Checked = Checked;
	}

	/**
	 * Get the checked status to bean variable.
	 */
	public int getChecked() {
			return Checked;
	}
	
	/**
	 * Store bean variable of the current competency ID.
	 */
	public void setCurrID(int CurrID) {
			this.CurrID = CurrID;
	}

	/**
	 * Get bean variable of the current competency ID.
	 */
	public int getCurrID() {
			return CurrID;
	}
	
	/**
	 * Store bean variable of the StartID.
	 */
	public void setStartID(int StartID) {
			this.StartID = StartID;
	}

	/**
	 * Get bean variable of the StartID.
	 */
	public int getStartID() {
			return StartID;
	}
	
	public int retrieveRaterID(int assignID){
		int raterID = -1;
		String sql = "Select RaterLoginID from tblAssignment where AssignmentID = "+assignID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				raterID = rs.getInt("RaterLoginID");
			}
		}catch(Exception e){
			System.err.println("Questionnair.java - retrieveRaterID - " + e);
		}finally{
			ConnectionBean.closeRset(rs);
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return raterID;
	}
	/**
	 * Add answer for additional questions into database
	 * @author xukun
	 * @param assignmentID
	 * @param QuestionID
	 * @param ans
	 * @param assignID
	 * @param rateID
	 * @return
	 */
	public boolean addAdditionalAns(int questionID, Vector<String> ans, int assignID) throws Exception{
		boolean result = false;
		//need to retrieve raterID 
		int raterID = retrieveRaterID(assignID);
		for(String entry : ans){
			String sql = "Insert into tbl_AdditionalQnAns (FKAddQnID, Answer, FKAssignmentID, FKRaterID, EntryDate)" +
					" values("+questionID+",'"+entry+"',"+assignID+","+raterID+",getDate()"+")";
			Connection con = null;
			Statement st = null;
			try{
				con = ConnectionBean.getConnection();
				st = con.createStatement();
				int iSuccess = st.executeUpdate(sql);
				if(iSuccess!=0)
					result = true;
			}catch(Exception e){
				System.err.println("Questionnaire.java - addAdditionalAns - " + e);
			}finally{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		}
		return result;
	}
	
	public boolean updateAdditionalAns(int questionID, Vector<String> ans, int assignID) throws Exception{
		boolean result = false;
		result =  removeAdditionalAns(questionID, assignID) && addAdditionalAns(questionID, ans, assignID);
		return result;
	}
	
	public boolean removeAdditionalAns(int questionID, int assignID) throws Exception{
		boolean result = false;
		int raterID = retrieveRaterID(assignID);
		String sql = "DELETE from tbl_AdditionalQnAns where FKAssignmentID= "+assignID+" and FKAddQnID= "+ questionID;
		Connection con = null;
		Statement st = null;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if(iSuccess!=0)
				result = true;
		}catch(Exception e){
			System.err.println("Questionnaire.java - removeAdditionalAns - " + e);
		}finally{
			ConnectionBean.closeStmt(st);
			ConnectionBean.close(con);
		}
		return result;
	}
	
	/**
	 * @author xukun
	 * @param questionID
	 * @param ans
	 * @param assignID
	 * @return
	 * @throws Exception
	 */
	public boolean addPrelimAns(int questionID, String ans, int assignID) throws Exception{
		boolean result = false;
		int raterID= retrieveRaterID(assignID);
		ans = ans.replaceAll(" ","");
		String sql = "Insert into tbl_PrelimQnAns (FKPrelimQnID, PrelimAnswer, FKAssignmentID, FKRaterID, EntryDate) " +
				"values("+ questionID+",'"+ans+"',"+assignID+","+raterID+",getDate()"+")";
		Connection con = null;
		Statement st = null;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if(iSuccess!=0)
				result = true;
		}catch(Exception e){
			System.err.println("Questionnaire.java - addPrelimAns - " + e);
		}finally{
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return result;
	}
	
	public boolean updatePrelimAns(int questionID, String ans, int assignID) throws Exception{
		boolean result = false;
		ans = ans.replaceAll(" ","");
		int raterID= retrieveRaterID(assignID);
		String sql = "Update tbl_PrelimQnAns  set prelimAnswer = '"+ans+"', EntryDate=getDate() where " +
				"FKPrelimQnID ="+questionID+" and FKAssignmentID="+assignID;
		Connection con = null;
		Statement st = null;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			int iSuccess = st.executeUpdate(sql);
			if(iSuccess == 1)
				result = true;
		}catch(Exception e){
			System.err.println("Questionnaire.java - updatePrelimAns - " + e);
		}finally{
			ConnectionBean.closeStmt(st);//Close statement
			ConnectionBean.close(con); //Close connection
		}
		return result;
	}
	
	/**
	 * @author xukun
	 * @param assignID
	 * @return
	 */
	public int getNumOfPrelimAns(int assignID){
		int result = -1;
		String sql = "SELECT count(distinct FKPrelimQnID) from tbl_PrelimQnAns where FKAssignmentID = "+assignID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs=st.executeQuery(sql);
			while(rs.next()){
				result = rs.getInt(1);
			}
		}catch(Exception e){
			System.err.println("Questionnaire.java - getNumOfPrelimAns - " + e);
		}finally{
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return result;
	}
	
	/**
	 * @author xukun
	 * @param assignID
	 * @return
	 */
	public int getNumOfAdditionalAns(int assignID){
		int result = -1;
		String sql = "SELECT count(distinct FKAddQnID) from tbl_AdditionalQnAns where FKAssignmentID = "+assignID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				result = rs.getInt(1);
			}
		}catch(Exception e){
			System.err.println("Questionnaire.java - getNumOfAdditionalAns - " + e);
		}finally{
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return result;
	}
	
	/**
	 * Add rater's comment to database.
	 */
	public boolean addComment(int assignmentID, int PKComp, int KBID, String comment) throws SQLException, Exception {											
		boolean b = false;
		
                // Changed by DeZ, 16.07.08,  Fixed problem where Import Questionaires gives blank narrative comments even though data is available
		String sql = "Insert into tblComment(AssignmentID, CompetencyID, KeyBehaviourID, Comment, EntryDate) values(" + assignmentID + ", " + PKComp + ", ";
		sql = sql + KBID + ", N'" + util.Utils.SQLFixer(comment) + "', getDate())";
		
		Connection con = null;
		Statement st = null;

		boolean bIsAdded = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(sql);
			if(iSuccess!=0)
			bIsAdded=true;

		}
		catch(Exception E)
		{
            System.err.println("Questionnaire.java - addComment - " + E);
		}
		finally
        {

			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

        }
       //Changed by Ha 30/05/08
		return bIsAdded;
	}
	
	/**
	 * Update rater's comment in the database.
	 */
	public boolean updateComment(int assignmentID, int PKComp, int KBID, String comment) throws SQLException, Exception {											
		boolean b = false;
		
        // Changed by DeZ, 16.07.08,  Fixed problem where Import Questionaires gives blank narrative comments even though data is available
		String sql = "Update tblComment set comment = N'" + util.Utils.SQLFixer(comment) + "', EntryDate=getDate() where ";		
		sql = sql + "assignmentID = " + assignmentID + " and CompetencyID = " + PKComp;
		sql = sql + " and KeyBehaviourID = " + KBID;
		
		Connection con = null;
		Statement st = null;


		boolean bIsUpdated = false;
		try
		{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(sql);
			if(iSuccess!=0)
			bIsUpdated=true;

		}
		catch(Exception E)
		{
            System.err.println("Questionnaire.java - updateComment - " + E);
		}
		finally
        {

			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

        }
        
		return bIsUpdated;
	}
	
	
	/**
	 * Check whether rater's comment has already in the database.
	 */
	public String checkCommentExist(int assignmentID, int PKComp, int KBID) throws SQLException, Exception {											
		String exist = null;
		
		String sql = "Select * from tblComment where ";		
		sql = sql + "AssignmentID = " + assignmentID + " and CompetencyID = " + PKComp;
		sql = sql + " and KeyBehaviourID = " + KBID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
        	
            if(rs.next())
    			exist = rs.getString("Comment");			

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - checkCommentExist - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		return exist; 
	}
	
	/**
	 * @author xukun
	 * @param prelimQnID
	 * @param assignID
	 * @return
	 */
	public boolean checkPrelimAnsExist(int prelimQnID, int assignID){
		boolean result = false;
		String sql = "SELECT * from tbl_PrelimQnAns where FKPrelimQnID="
				+prelimQnID+" and FKAssignmentID="+assignID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try{
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
        	
            if(rs.next())
    			result = true;	
            
		}catch(Exception E) 
        {
            System.err.println("Questionnaire.java - checkPrelimAnsExist - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		return result;
	}
	
	public boolean checkAdditionalAnsExist(int AdditionalQnID, int assignID){
		boolean result = false;
		String sql = "SELECT * from tbl_AdditionalQnAns where FKAddQnID="
				+AdditionalQnID+" and FKAssignmentID="+assignID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try{
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
        	
            if(rs.next())
    			result = true;	
            
		}catch(Exception E) 
        {
            System.err.println("Questionnaire.java - checkAdditionalAnsExist - " + E);
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
	 * Get CompetencyID based on Key Behaviour ID.
	 */
	public int CompetencyID(int KBID) throws SQLException, Exception {											
		int exist = 0;
		
		String sql = "Select * from KeyBehaviour where ";		
		sql = sql + "PKKeyBehaviour = " + KBID;
				
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);

    		if( rs!= null && rs.next())
    			exist = rs.getInt("FKCompetency");				

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - CompetencyID - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return exist; 
	}
	
	/**
	 * Check whether comment is included.
	 */
	public int commentIncluded(int surveyID) {											
		int exist = 0;
		
		String sql = "Select * from tblSurvey where ";		
		sql = sql + "SurveyID = " + surveyID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
           
            if(rs != null && rs.next())
				exist = rs.getInt("Comment_Included");	

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - commentIncluded - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
       
		return exist; 
	}
	
	/**
	 * Check whether Self comment is included.
	 */
	public int SelfCommentIncluded(int surveyID) {											
		int exist = 0;
		
		String sql = "Select * from tblSurvey where ";		
		sql = sql + "SurveyID = " + surveyID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
           
            if(rs != null && rs.next())
            	exist = rs.getInt("Self_Comment_Included");		

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - selfCommentIncluded - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return exist; 
	}
	
	
	/**
	 * Check if any 'Supervisor' was assigned to the survey for individual
	 * 
	 * @author Ping Yang
	 * Date 11/08/08
	 * 
	 * @param surveyID
	 * @param TargetLoginID
	 * @return blnExist
	 * @see IndividualReport.java
	 */
	
	public boolean SupCommentIncluded(int surveyID, int TargetLoginID){
		
		boolean exist = false;
		
		String sql = "SELECT COUNT(AssignmentID) As NumOfAssignment FROM tblAssignment where";		
		sql = sql + "(SurveyID = " + surveyID +")" ;
		sql = sql + "AND (RaterCode LIKE 'SUP%')" ;
		sql = sql + "AND (TargetLoginID = "+ TargetLoginID +")" ;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
           
            if(rs != null && rs.next())
            	if (rs.getInt("NumOfAssignment") > 0){
            		exist = true;
            	}

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - selfCommentIncluded - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		
		return exist;
	}
	
	/**
	 * Check if any 'Supervisor' was assigned to the survey for individual
	 * 
	 * @author Ping Yang
	 * Date 11/08/08
	 * 
	 * @param surveyID
	 * @param TargetLoginID
	 * @return blnExist
	 * @see IndividualReport.java
	 */
	
	public boolean categoryCommentIncluded(int surveyID, int TargetLoginID,String category){
		
		boolean exist = false;
		
		String sql = "SELECT COUNT(AssignmentID) As NumOfAssignment FROM tblAssignment where";		
		sql = sql + "(SurveyID = " + surveyID +")" ;
		sql = sql + "AND (RaterCode LIKE '" + category + "')" ;
		sql = sql + "AND (TargetLoginID = "+ TargetLoginID +")" ;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
           
            if(rs != null && rs.next())
            	if (rs.getInt("NumOfAssignment") > 0){
            		exist = true;
            	}

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - categoryCommentIncluded - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		
		return exist;
	}
	
	
	/**
	 * Check if any 'Supervisor' was assigned to the survey for whole survey
	 * 
	 * @author Ping Yang
	 * Date 11/08/08
	 * 
	 * @param surveyID
	 * @return blnExist
	 * @see GroupReport.java
	 */
	
	public boolean SupCommentIncluded(int surveyID){
		
		boolean exist = false;
		
		String sql = "SELECT COUNT(AssignmentID) As NumOfAssignment FROM tblAssignment where";		
		sql = sql + "(SurveyID = " + surveyID +")" ;
		sql = sql + "AND (RaterCode LIKE 'SUP%')" ;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
           
            if(rs != null && rs.next())
            	if (rs.getInt("NumOfAssignment") > 0){
            		exist = true;
            	}

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - selfCommentIncluded - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		
		return exist;
	}
	
	/**
	 * Check if any 'Others' was assigned to the survey for individual
	 * 
	 * @author Ping Yang
	 * Date 11/08/08
	 * 
	 * @param surveyID
	 * @param TargetLoginID
	 * @return blnExist
	 * @see IndividualReport.java
	 */
	
	public boolean OthCommentIncluded(int surveyID, int TargetLoginID){
		
		boolean exist = false;
		
		/*
		 * Change(s) : Change query
		 * Reason(s) : We must now consider Peers and Subordinates in RelationHigh as the orginial "Others" category
		 * Updated By: Desmond
		 * Updated On: 16 Nov 2009
		 */
		
		String sql = "SELECT COUNT(AssignmentID) As NumOfAssignment FROM tblAssignment where";		
		sql = sql + "(SurveyID = " + surveyID +")" ;
		sql = sql + "AND (RaterCode LIKE 'OTH%' OR RaterCode LIKE 'PEER%' OR RaterCode LIKE 'SUB%' )" ;
		sql = sql + "AND (TargetLoginID = "+ TargetLoginID +")" ;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
           
            if(rs != null && rs.next())
            	if (rs.getInt("NumOfAssignment") > 0){
            		exist = true;
            	}

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - selfCommentIncluded - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		
		return exist;
	}
	
	
	/**
	 * Check if any 'Others' was assigned to the survey for whole survey
	 * 
	 * @author Ping Yang
	 * Date 11/08/08
	 * 
	 * @param surveyID
	 * @return blnExist
	 * @see IndividualReport.java
	 */
	
	public boolean OthCommentIncluded(int surveyID){
		
		boolean exist = false;
		
		String sql = "SELECT COUNT(AssignmentID) As NumOfAssignment FROM tblAssignment where";		
		sql = sql + "(SurveyID = " + surveyID +")" ;
		sql = sql + "AND (RaterCode LIKE 'OTH%')" ;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
           
            if(rs != null && rs.next())
            	if (rs.getInt("NumOfAssignment") > 0){
            		System.out.println(sql);
            		exist = true;
            	}

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - selfCommentIncluded - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		
		return exist;
	}
	
		
	/**
	 * Check Rating Task Setup status.
	 * @param RTID
	 * @param surveyID
	 * @return 0 = Default, 1 = Show, 2 = Hide
	 */
	public int RTSetupStatus(int RSID, int surveyID) {											
		int exist = 0;
		
		String sql = "Select * from tblSurveyRating where ";		
		sql = sql + "SurveyID = " + surveyID + " and RatingTaskID = " + RSID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);

            if(rs.next())
				exist = rs.getInt("AdminSetup");	
    	
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getSetupStatus - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return exist; 
	}
	
	
	/**
	 * Check Rating Score for the particular competency and rating task
	 * @param RTID
	 * @param surveyID
	 * @param pkComp
	 * @return Score value
	 */
	public float RTScore(int RTID, int surveyID, int pkComp) {											
		float fixedScore = 0;
		
		String sql = "Select * from tblRatingSetup where ";		
		sql = sql + "SurveyID = " + surveyID + " and RatingTaskID = " + RTID + " and CompetencyID = " + pkComp;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);

            if(rs.next())
				fixedScore = rs.getFloat("Score");		
    	
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - RTScore - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		
		return fixedScore; 
	}	
	
	/**
	 * Check Rating Score for the particular key behaviour and rating task
	 * @param RTID
	 * @param surveyID
	 * @param pkBehv
	 * @return Score value
	 */
	public float RTScoreBehv(int RTID, int surveyID, int pkBehv) {
		float fixedScore = 0;
		
		String Sql = "";
		
		Sql = "Select * from tblRatingSetup where ";		
		Sql = Sql + "SurveyID = " + surveyID + " and RatingTaskID = " + RTID + " and ";
		Sql = Sql + "CompetencyID = (SELECT FKCompetency FROM KeyBehaviour WHERE PKKeyBehaviour = "+pkBehv +")";
				
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(Sql);

            if(rs.next())
				fixedScore = rs.getFloat("Score");		
    	
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - RTScorebehv - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return fixedScore; 
	}
	
	
	/**
	 * Get survey id given assignment id.
	 */
	public int getSurveyID(int assignmentID) {											
		int exist = 0;
		
		String sql = "Select * from tblAssignment where AssignmentID = " + assignmentID;
				
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);
			if(rs.next())
				exist = rs.getInt("SurveyID");				
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getSurveyID - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return exist; 
	}	
	
	
	/**
	 * Check whether the particular rater has complete all the ratings.
	 */
	public boolean checkSurveyCompleted(int assignmentID){
		return checkQuestionnaireCompleted(assignmentID)+
				checkPrelimCompleted(assignmentID)+
				checkAdditionalCompleted(assignmentID)== 3;		
	}
	
	public int checkQuestionnaireCompleted(int assignmentID) {						
		
		int surveyID = getSurveyID(assignmentID);
		
		int totalRatingTask = getTotalSurveyRating(surveyID);
		int totalAll = TotalList(surveyID);
		int totalCompleted = TotalResult(assignmentID);
		
		//need to compare whether it is the same question
		if(totalCompleted == (totalAll * totalRatingTask))
			return 1;
		else
			return 0;
	}
	
	public int checkPrelimCompleted(int assignmentID){
		int surveyID = getSurveyID(assignmentID);
		int totalPrelimQn = getPrelimQuestion(surveyID).size();
		int completedPrelimQn = getNumOfPrelimAns(assignmentID);
		if(totalPrelimQn <= completedPrelimQn){
			return compareQnSet("tbl_PrelimQn", surveyID, assignmentID);
		}else{
			return 0;
		}
	}
	
	public int checkAdditionalCompleted(int assignmentID){
		int surveyID = getSurveyID(assignmentID);
		int totalAddiQn = getAdditionalQuestion(surveyID).size();
		int completedAddiQn = getNumOfAdditionalAns(assignmentID); 
		if(totalAddiQn <= completedAddiQn){
			return compareQnSet("tbl_AdditionalQn", surveyID, assignmentID);
		}else{
			return 0;
		}
	}
	
	public int compareQnSet(String tableName, int surveyID, int assignID){
		Vector<Integer> currentQnSet = new Vector<Integer>();
		Vector<Integer> ansedQnSet = new Vector<Integer>();
		String qnSql = "SELECT * from "+tableName+" where FKSurveyID="+surveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(qnSql);
			while(rs.next()){
				currentQnSet.add(rs.getInt(1));
			}
		}catch(Exception E) 
        {
            System.err.println("Questionnaire.java - compareQnSet - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		String ansSql = "SELECT * from "+tableName+"Ans where FKAssignmentID= " + assignID;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(ansSql);
			while(rs.next()){
				ansedQnSet.add(rs.getInt(2));
			}
		}catch(Exception E) 
        {
            System.err.println("Questionnaire.java - compareQnSet - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
		for(int i :currentQnSet){
			if(!ansedQnSet.contains(i)){
				System.err.println("xukun"+i);
				return 0;
			}
		}
		return 1;
	}
	
	/**
	 * Check whether there is any comment input into the questionnaire.
	 */
	public int checkCommentInput(int assignmentID) {											
		int exist = 0;
		
		String sql = "Select * from tblComment where AssignmentID = " + assignmentID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(sql);

            if(rs != null && rs.next())
				exist = 1;		
    		
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - checkCommentInput - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return exist; 
		
	}		
	
	public int [] getSurveyRatingScale(int surveyID) {
		int arr[] = {-1,-1,-1,-1,-1};
		
		String query = "Select DISTINCT ScaleID from tblSurveyRating where SurveyID = " + surveyID;
		query = query + " order by ScaleID";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
           
            int i=0;
    		while(rs != null && rs.next())
    	    {
    			arr[i] = rs.getInt("ScaleID");
    			//System.out.println(arr[i]);
				i++;
    	  	}
            
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getSurveyRatingScale - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		
		return arr;
	}

	public Vector getSurveyRatingTask(int iSurveyID, int iRSID) {
		Vector v = new Vector();
		
		String query = "Select tblSurveyRating.RatingTaskID, tblSurveyRating.RatingTaskName, RatingCode from tblSurveyRating INNER JOIN tblRatingTask ON ";
		query += "tblSurveyRating.RatingTaskID = tblRatingTask.RatingTaskID where SurveyID = " + iSurveyID;
		query = query + " and ScaleID = " + iRSID + " order by tblSurveyRating.RatingTaskID DESC";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

            while(rs != null && rs.next()) {
				voRatingTask vo = new voRatingTask();
				vo.setRatingTaskID(rs.getInt("RatingTaskID"));
				vo.setRatingTaskName(rs.getString("RatingTaskName"));
				vo.setRatingCode(rs.getString("RatingCode"));
				v.add(vo);
			}
    		
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getSurveyRatingTask- " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
		return v;

	}

	public boolean hasCompleted(int assignmentID, int KBID, int size) {
		boolean hasTotal = false;
		String query = "Select COUNT(*) as Total from tblResultBehaviour ";
		query += "where KeyBehaviourID = " + KBID + " AND AssignmentID = " + assignmentID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

    		if( rs!= null && rs.next()){
    			if(size == rs.getInt("Total"))
					hasTotal = true;			
    		}

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - hasCompleted- " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return hasTotal;	
	}
	
	
	/**
	 * This method is used to see whether a rating of competency of a particular assignment is completed
	 * @param assignmentID: the ID of the assignment
	 * @param competencyID: the ID of the competency
	 * @throws SQLException
	 * @throws Exception
	 * @author Liu Taichen
	 * Created on: 29 June 2012
	 */
	
	public boolean isCompletedCompetency(int assignmentID, int competencyID) {
		boolean isCompleted = false;
		String query = "Select COUNT(*) as Total from tblResultCompetency ";
		query += "where CompetencyID = " + competencyID + " AND AssignmentID = " + assignmentID;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {          
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);

    		if( rs!= null && rs.next()){
    			if(1 == rs.getInt("Total"))
					isCompleted = true;			
    		}

        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - isCompletedCompetency- " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        
		return isCompleted;	
	}
	
	
	
	/**
	 * getHideNAOption
	 * 
	 * @param surveyID
	 * @author Denise by 14/12/09
	 * 
	 * get the hide NA  option of one particular survey
	 * */
	
	public boolean getHideNAOption(int surveyID)
	{		
		String query = "Select * from tblSurvey ";
		query += "where SurveyID = " + surveyID;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

        try
        {      		
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
        	if(rs != null && rs.next())
        	{
        		if (rs.getInt("HideNA") == 1) 
        			return true;
        		else return false;
        	}
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getSurveyHideNAOption - " + E);
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
	 * retrive rater's information according to assignmentID of the questionnaire
	 * @param assignmentID
	 * @return voUser
	 * 
	 * Qiao Li 05 Jan 2010
	 */
	public voUser getRaterInfo(int assignmentID)
	{		
		String query = "SELECT * FROM [User] ";
		query += "INNER JOIN tblAssignment ON [User].PKUser = tblAssignment.RaterLoginID ";
		query += "WHERE tblAssignment.AssignmentID = " + assignmentID;
		//System.out.println(query);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		voUser rater = new voUser();

        try
        {      		
        	con=ConnectionBean.getConnection();
        	st=con.createStatement();
        	rs=st.executeQuery(query);
        	
        	if(rs != null & rs.next())
        	{
        		
        		rater.setPKUser(rs.getInt("PKUser"));
        		rater.setFamilyName(rs.getString("FamilyName"));
        		rater.setGivenName(rs.getString("GivenName"));
        		rater.setLoginName(rs.getString("LoginName"));
        	}
        	
        }
        catch(Exception E) 
        {
            System.err.println("Questionnaire.java - getRaterInfo - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
	        ConnectionBean.closeStmt(st); //Close statement
	        ConnectionBean.close(con); //Close connection
        }
        return rater;
	}
}