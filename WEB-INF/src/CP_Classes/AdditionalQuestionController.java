package CP_Classes;

import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import CP_Classes.common.ConnectionBean;
/*
 * Change Log
 * ===================================================================================================================
 * 	  Date	  Changed By			Method(s)										Change(s)
 * ===================================================================================================================
 * 25/05/2012	Albert		updateAnswerHeader(),saveAnswerHeader(),			replace the apostrophe in the string
 * 							updateQuestionnareHeader,saveQuestionnareHeader(),	with double apostrophe using 
 * 							saveAnswer(),updateAnswer(),						util.Utils.SQLFixer(String). This is done to 
 * 							saveQuestion,updateQuestion()						tackle SQL error when user input contains apostrophe.
 * 
 * 15/08/2012   Lingtong    saveAnswer(),updateAnswer()                         Modified methods so it can insert entry date automatically
 * 
 * 22/05/2013   xukun		deleteQn, deleteAns                                 When delete qn, delete its ans as well
 */
public class AdditionalQuestionController {	
	
	public AdditionalQuestionController()
	{	
	}
	
	public String escapeInvalidChars(String s)
	{
		String validString = s.replace("'", "''");
		
		return validString;
	}
	
	public void updateAnswerHeader(int fkSurveyId, String header)
	{
		Connection con = null;
		Statement st = null;
		String query ="Update tbl_AdditionalQuestionAnsHeader set AddQnAnsHeaderContent='"+util.Utils.SQLFixer(header)+"' where FKSurveyID ='"+fkSurveyId+"'";
		
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
			
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - updateAnswerHeader - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
	}
	
	public void saveAnswerHeader(int fkSurveyId, String header)
	{
		Connection con = null;
		Statement st = null;
		String query ="Insert into tbl_AdditionalQuestionAnsHeader (FKSurveyID, AddQnAnsHeaderContent) VALUES ('"+fkSurveyId+"', '"+util.Utils.SQLFixer(header)+"')";
		//System.out.println(query);
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
			
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - saveAnswerHeader - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		
	}
	/*
	public void saveEntryDate(String Date){
		Connection con = null;
		Statement st = null;
		String query = "Insert into tbl_AdditionalQnAns (EntryDate) VALUES ('"+util.Utils.SQLFixer(Date)+"')";
	            try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
			
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - saveAnswerHeader - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
	
	} */
	
	public String getAnswerHeader(int fkSurveyId)
	{
		String header = "";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String query="SELECT * FROM tbl_AdditionalQuestionAnsHeader where fkSurveyID='"+fkSurveyId+"'";
		
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
		
			while(rs != null && rs.next())
			{
				header= rs.getString("AddQnAnsHeaderContent");
			}
		}
        catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - getAnswerHeader - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		return header;
	}

	public void updateQuestionnaireHeader(int fkSurveyId, String header)
	{
		Connection con = null;
		Statement st = null;
		String query ="Update tbl_QuestionnaireHeader set QnsHeaderContent='"+util.Utils.SQLFixer(header)+"' where FKSurveyID ='"+fkSurveyId+"'";
		
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
			
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - updateQuestionnaireHeader - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
	}
	
	public void saveQuestionnaireHeader(int fkSurveyId, String header)
	{
		Connection con = null;
		Statement st = null;
		String query ="Insert into tbl_QuestionnaireHeader (FKSurveyID, QnsHeaderContent) VALUES ('"+fkSurveyId+"', '"+util.Utils.SQLFixer(header)+"')";
		//System.out.println(query);
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
			
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - saveQuestionnaireHeader - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		
	}
	
	public String getQuestionnaireHeader(int fkSurveyId)
	{
		String header = "";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String query="SELECT * FROM tbl_QuestionnaireHeader where fkSurveyID='"+fkSurveyId+"'";
		
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
		
			while(rs != null && rs.next())
			{
				header= rs.getString("QnsHeaderContent");
			}
		}
        catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - getQuestionnaireHeader - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		return header;
	}
	
	public Vector<String> getReportAnswers(String raterCategory, int fkAddQnId, int targetID)
	{
		Vector<String> ansV = new Vector<String>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		String query="SELECT * FROM tbl_AdditionalQnAns " +
		"INNER JOIN tblAssignment on tbl_AdditionalQnAns.FKAssignmentID = tblAssignment.AssignmentID " +
		"WHERE FKAddQnID ='"+fkAddQnId+"' AND tblAssignment.TargetLoginID='"+targetID+"' AND RaterStatus='1' AND tblAssignment.RaterCode LIKE '"+raterCategory+"'";
		
			try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
		
			while(rs != null && rs.next())
			{
				ansV.add(rs.getString("Answer"));
			}
		}
        catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - getReportAnswers - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		return ansV;
	}
	
	public Vector<AdditionalQuestionAns> getAnswers(int fkAddQnId, int fkAssignmentId, int fkRaterId)
	{
		Vector<AdditionalQuestionAns> v = new Vector<AdditionalQuestionAns>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String query ="Select * from tbl_AdditionalQnAns where FKAddQnID='"+fkAddQnId+"' and FKAssignmentId='"+fkAssignmentId+"'  Order by AddQnAnsID";
		
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
		
			while(rs != null && rs.next())
			{
				AdditionalQuestionAns aqa = new AdditionalQuestionAns(rs.getInt("AddQnAnsID"), rs.getInt("FKAddQnID"), rs.getString("Answer"), rs.getInt("FKAssignmentID"), rs.getInt("FKRaterID"));
				v.add(aqa);
			}
		}
        catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - getAnswers - " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		
		return v;
		
	}
	

	public void saveAnswer(int fkAddQnID, String answer, int assignmentID, int raterID, String Date)
	{
		
		Connection con = null;
		Statement st = null;
		//System.out.println("addqnid is "+fkAddQnID);
		String query ="Insert into tbl_AdditionalQnAns (fkAddQnID, Answer, FKAssignmentID, FKRaterID, EntryDate) VALUES ('"+fkAddQnID+"', N'"+util.Utils.SQLFixer(answer)+"','"+assignmentID+"', '"+raterID+"' , '"+Date+"')";
		//System.out.println(query);
		//System.out.println(query);
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
						
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - saveAnswer - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
	}
	
	public void updateAnswer(int addQnAnsID, int fkAddQnID, String answer, int assignmentID, int raterID, String Date)
	{
		Connection con = null;
		Statement st = null;
        //System.out.println("answer =" + answer);
		//System.out.println("addqnid is "+fkAddQnID);
		String query ="Update tbl_AdditionalQnAns set Answer= N'"+util.Utils.SQLFixer(answer)+"', EntryDate='"+Date+"' where AddQnAnsID ='"+addQnAnsID+"'";
		//System.out.println(query);
		//When user update answer, the entry date is updated as well.
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
			
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - updateAnswer - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
	}

	public void saveQuestion(int fkSurveyID, String question)
	{
		Connection con = null;
		Statement st = null;
		String query ="Insert into tbl_AdditionalQn (FKSurveyID, Question) VALUES ('"+fkSurveyID+"', '"+util.Utils.SQLFixer(question)+"')";
		//System.out.println(query);
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
			
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - saveQuestion - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
	}
	
	public void updateQuestion(int addQnID, int fkSurveyID, String question)
	{
		Connection con = null;
		Statement st = null;
		String query ="Update tbl_AdditionalQn set FKSurveyID= '"+fkSurveyID+"', Question='"+util.Utils.SQLFixer(question)+"' where AddQnID ='"+addQnID+"'";
		
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
			
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - updateQuestion - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
	}
	
	public void deleteQuestion(int addQnID)
	{
		
		Connection con = null;
		Statement st = null;
		String query ="delete from tbl_AdditionalQn where AddQnID ='"+addQnID+"'";
		
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			st.executeUpdate(query);
			
		}catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - deleteQuestion - " + E);
        }
        finally
        {
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		// remove all ans belong to this question
		//deleteAns(addQnID);
	}
	
	public void deleteAns(int addQnID){
		Connection con = null;
		Statement st = null;
		String query = "delete from tbl_AdditionalQnAns where FKAddQnID ='"+addQnID+"'";
		
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			st.executeUpdate(query);
		}catch(Exception E){
			System.err.println("AdditionalQuestionController.java - deleteAns -" + E);
		}finally{
			ConnectionBean.closeStmt(st);//Close statement
			ConnectionBean.close(con);//Close connection
		}
	}
	
	public Vector<AdditionalQuestion> getQuestions(int fkSurveyId)
	{
		Vector<AdditionalQuestion> v = new Vector<AdditionalQuestion>();
		

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String query ="Select * from tbl_AdditionalQn where FKSurveyID="+fkSurveyId+"Order by AddQnID";
		
		try{
			
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);
		
			while(rs != null && rs.next())
			{
				AdditionalQuestion aq = new AdditionalQuestion(rs.getInt("AddQnID"), rs.getInt("FKSurveyID"), rs.getString("Question"));
				v.add(aq);
			}
		}
        catch(Exception E) 
        {
            System.err.println("AdditionalQuestionController.java - getQuestions- " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }
		
		
		return v;
		
	}
	
	public boolean checkHavingAns(int addQnID){
		int count = 0;
		String sql = "SELECT count(*) from tbl_AdditionalQn inner join tbl_AdditionalQnAns on " +
				"AddQnID = FKAddQnID where " +
				"AddQnID = "+addQnID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(sql);
			while(rs.next()){
				count = rs.getInt(1);
			}
			
		}catch(Exception E) 
        {
            System.out.println("AdditionalQuestionController.java - checkHavingAns- " + E);
        }
        finally
        {
        	ConnectionBean.closeRset(rs); //Close ResultSet
        	ConnectionBean.closeStmt(st); //Close statement
        	ConnectionBean.close(con); //Close connection
        }	
		
		if(count == 0)
			return false;
		return true;
	}

}
