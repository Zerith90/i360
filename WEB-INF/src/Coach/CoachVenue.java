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
import CP_Classes.vo.voCoachVenue;
import CP_Classes.vo.voUser;
import CP_Classes.vo.votblConsultingCompany;
import CP_Classes.vo.votblOrganization;

public class CoachVenue {
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
	
	public CoachVenue(){

	}
	
	public Vector<voCoachVenue> getAllCoachVenue(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		Vector<voCoachVenue> CoachVenues=new Vector<voCoachVenue>();
		String query="Select * from CoachVenue order by Venue1";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				voCoachVenue vo = new voCoachVenue();
				vo.setVenuePK(rs.getInt("VenuePK"));
				vo.setVenue1(rs.getString("Venue1"));
				vo.setVenue2(rs.getString("Venue2"));
				vo.setVenue3(rs.getString("Venue3"));
				CoachVenues.add(vo);
			}
		}
		catch(Exception E) 
		{
			System.err.println("CoachVenue.java - getAllCoachVenue - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return CoachVenues;
	}

	public voCoachVenue getSelectedCoachVenue(int VenuePK){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		voCoachVenue vo = new voCoachVenue();
		String query="Select * from CoachVenue where VenuePK="+VenuePK;
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query);

			while(rs.next())
			{	
				vo.setVenuePK(rs.getInt("VenuePK"));
				vo.setVenue1(rs.getString("Venue1"));
				vo.setVenue2(rs.getString("Venue2"));
				vo.setVenue3(rs.getString("Venue3"));
			}
		}
		catch(Exception E) 
		{
			System.err.println("CoachVenue.java - getSelectedCoachVenue - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return vo;
	}

	
	public boolean addCoachVenue(String Venue1, String Venue2, String Venue3){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		boolean suc=false;
		String query = "INSERT INTO CoachVenue (Venue1,Venue2,Venue3) VALUES ('"+ Venue1+"' ,'" + Venue2 +"','"+Venue3+"')";
		try
		{ 

			con=ConnectionBean.getConnection();
			st=con.createStatement();
			int iSuccess=st.executeUpdate(query);
			if(iSuccess!=0){
				suc=true;
				ev.addRecord("Insert", "Insert Coaching Venue", "Insert Coaching Venue, Coaching Address Line 1:"+Venue1, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
			}
		
		}
		catch(Exception E) 
		{
			System.err.println("CoachVenue.java - addCoachVenue - " + E);
		}
		finally
		{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		return suc;
	}
	public boolean deleteCoachVenue(int VenuePK) throws SQLException, Exception
	{
		
		String sql = "DELETE FROM CoachVenue WHERE VenuePK = " + VenuePK;
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
					ev.addRecord("Delete", "Delete Coaching Venue", "Delete Coaching Venue, Coaching Venue PK:"+VenuePK, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
				}
	  		
			} 
			catch (Exception E)
			{
				System.err.println("CoachVenue.java - deleteCoachVenue - " + E);
				
			}

			finally
			{

				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection


			}
		
	
		return bIsDeleted;
	}
	

	public boolean updateVenue(int VenuePK, String Venue1, String Venue2, String Venue3)throws SQLException, Exception{
	
		String sql = "Update CoachVenue Set Venue1 = '" + Venue1 +"', Venue2 = '" + Venue2 +"', Venue3 = '" + Venue3 +"' where VenuePK = " + VenuePK;
		//System.out.println(sql);
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
					ev.addRecord("Update", "Update Coaching Venue", "Delete Coaching Venue, Coaching Venue PK:"+VenuePK, userDetials.getLoginName(), companyDetail.getCompanyName(), votblOrganizationDetail.getOrganizationName());					
				}
	  		
			} 
			catch (Exception E)
			{
				System.err.println("CoachVenue.java - updateVenue - " + E);
				
			}

			finally
			{

				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection


			}
		
		return bIsUpdated;
	}

}
