/*
 * Author: Dai Yong
 * June 2013
 */
package Coach;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import CP_Classes.ConsultingCompany;
import CP_Classes.EventViewer;
import CP_Classes.Organization;
import CP_Classes.User;
import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voCoachDate;
import CP_Classes.vo.voUser;
import CP_Classes.vo.votblConsultingCompany;
import CP_Classes.vo.votblOrganization;

public class CoachDate {
//	Start of EventViewer
	private EventViewer ev=new EventViewer();
	private int UserPK;
	private voUser userDetials;
	private votblConsultingCompany companyDetail;
	private votblOrganization votblOrganizationDetail;
		
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
	
	
	public CoachDate(){

	}
	public voCoachDate getSelectedDate(int PKCoachDate){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		voCoachDate vo = new voCoachDate();
		String query="Select * from CoachDate where PKCoachDate="+PKCoachDate;
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				vo.setPK(rs.getInt("PKCoachDate"));
				vo.setFKCoachDateGroup(rs.getInt("FKCoachDateGroup"));
				vo.setDate(rs.getString("Date"));
			}
		}
		catch(Exception E) 
		{
			System.err.println("CoachDate.java - getSelectedDate - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return vo;
	}
	public Vector<voCoachDate> getAllDate(int FKCoachDateGroup){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		String query="Select * from CoachDate where FKCoachDateGroup="+FKCoachDateGroup;
		//System.out.println("check sql"+query);
		Vector<voCoachDate> v=new Vector<voCoachDate>();
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				voCoachDate vo = new voCoachDate();
				vo.setPK(rs.getInt("PKCoachDate"));
				vo.setFKCoachDateGroup(rs.getInt("FKCoachDateGroup"));
				vo.setDate(rs.getString("Date"));
				v.add(vo);
			}
			//System.out.println("check date size.java"+v.size());
		}
		catch(Exception E) 
		{
			System.err.println("CoachDate.java - getAllDate - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return v;
	}

	
	public boolean addDate(int FKCoachDateGroup, String Date){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean suc=false;
		String query = "INSERT INTO CoachDate (FKCoachDateGroup,Date) VALUES ('"+ FKCoachDateGroup+"' ,'" + Date +"')";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0)
				suc=true;
			ev.addRecord("Insert", "Add Coaching Date", "Add Coaching Date to Coaching Period, Coaching Period PK:"+FKCoachDateGroup, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());
		
		}
		catch(Exception E) 
		{
			System.err.println("CoachDate.java - addDate - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return suc;
	}
	public boolean deleteDate(int PKCoachDate) throws SQLException, Exception
	{
		
		String sql = "DELETE FROM CoachDate WHERE PKCoachDate = " + PKCoachDate;
		Connection con = null;
		Statement st = null;
        boolean bIsDeleted = false;
			
			try
			{
				con=ConnectionBean.getConnection();
				st=con.createStatement();
				int iSuccess = st.executeUpdate(sql);
				if(iSuccess!=0){
					bIsDeleted=true;
					ev.addRecord("Delete", "Delete Coaching Date", "Delete Coaching Date From Coaching Period, Coaching Date PK:"+PKCoachDate, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
				}
				
				
			} 
			catch (Exception E)
			{
				System.err.println("CoachDate.java - deleteDate - " + E);
				
			}

			finally
			{

				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection


			}
		
	
		return bIsDeleted;
	}
	

	public boolean updateDate(int PKCoachDate,String Date)throws SQLException, Exception{
	
		String sql = "Update CoachDate Set Date = '"+ Date +"' where PKCoachDate = " + PKCoachDate;
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
					ev.addRecord("Update", "Update Coaching Date", "Update Coaching Date, Coaching Date PK:"+PKCoachDate, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
				}
	  		
			} 
			catch (Exception E)
			{
				System.err.println("CoachDate.java - updateDate - " + E);
				
			}

			finally
			{
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		return bIsUpdated;
		
	}
}
