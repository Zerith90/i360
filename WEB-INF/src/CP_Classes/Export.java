package CP_Classes;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.io.File;
import java.io.IOException;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voUser;
import CP_Classes.vo.voUserDemographic;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.WritableFont;
import jxl.write.WritableCellFormat;
import jxl.write.Label;
import jxl.write.WriteException;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.WritableCell;

/**
 * 
 * Change Log
 * ==========
 *
 * Date        By				Method(s)            					Change(s) 
 * ================================================================================================
 * 21/06/12    liu Taichen		exportCluster()					        Created this method to allow exporting of Clusters
 */

public class Export
{
	/**
	 * Declaration of classes 
	 */
	 
	//private Database db;
	private Setting server;
	private EventViewer ev;
	private Create_Edit_Survey user;
	private DemographicEntry demographic;
	private RaterRelation RTRel;
	private int iSurveyID;
	private int iOrgID;
	private int selectedSession;
	/**
	 * Declaration of Variables
	 */
	
	private String sDetail[] = new String[13];
	private	String sHeader[] = new String[0];
	private	String sSubHeader[] = new String[0];
	
	private Label label;
	private WritableSheet writesheet;
	private WritableSheet writesheet1;
	private WritableCellFormat cellBOLD;
	private WritableFont fontBold, fontFace;
	private WritableWorkbook workbook;
	private WritableCellFormat cellBOLD_Border;
	private	WritableCellFormat bordersData1, bordersData2, bordersData_LeftAlign;
	private WritableCellFormat No_Borders, No_Borders_ctrAll,No_Borders_ctrAll_Bold, No_Borders_NoBold ;
	private File inputWorkBook, outputWorkBook;
	
	/**
	 * Creates a new intance of Create Edit Survey object.
	 */
	
	public Export()
	{
		//db = new Database();
		server = new Setting();
		ev = new EventViewer();
		user = new Create_Edit_Survey();
		demographic = new DemographicEntry();
		RTRel = new RaterRelation();
	}
	
	/*--------------------------------------To initialize the workbook and borders -------------------------------*/
	
	public void write(int exportType) throws SQLException, Exception
	{
		String sFileName;
		String sSheet1 = "Sheet1"; //default
		String sSheet2 = "Sheet2"; //default
		
		/* Switch Function for multiple exportType */
		// Activate when there's multiple exportType
		switch(exportType)
		{
			case 1	: //Export User
				sFileName = "ExportUser.xls";
				//Removed the codes for creating the spreadsheet that allows deletion of user in the export user file. As there is no methods for deleting user therefore remove the function for deleting user, Sebastian 29 July 2010

				break;
			case 2	: //Export Target
				sFileName = "ExportTarget.xls";
				break;
			case 3	: //Export Assignment
				sFileName = "ExportAssignment.xls";
				break;
			case 4 : //Export Competency
				sFileName = "ExportCompetency.xls";
				break;
			case 5 : //Export KB
				sFileName = "ExportBehaviour.xls";
				break;
			case 6 : //Export DA
				sFileName = "ExportDevelopmentActivities.xls";
				//Renamed the sheet names for Export Development Activities to display the sheets in the same format as the import excel template, Sebastian 29 July 2010
				sSheet1 = "Development Activities";
				sSheet2 = "Development Activities (Delete)";
				break;
			case 7 : //Export DR
				sFileName = "ExportDevelopmentResources.xls";
				//Renamed the sheet names for Export Development Resources to display the sheets in the same format as the import excel template, Sebastian 29 July 2010
				sSheet1 = "Development Resources";
				sSheet2 = "Development Resources (Delete)";
				break;
			case 8 : //Export Div
				sFileName = "ExportDivision.xls";
				break;
			case 9 : //Export Dept
				sFileName = "ExportDepartment.xls";
				break;
			case 10 : //Export Grp
				sFileName = "ExportGroup.xls";
				break;
			case 11 : //Export Cluster
				sFileName = "ExportCluster.xls";
				break;
			case 12 : //Export Cluster
				sFileName = this.getCoachingStatusFileName();
				break;
			default	: //Default = Export User
				sFileName = "ExportUser.xls";
		}
		
		String output = server.getReport_Path()+"\\" + sFileName;
		outputWorkBook = new File(output);
		
		inputWorkBook = new File(server.getReport_Path_Template() + "\\HeaderFooter.xls");
		Workbook inputFile = Workbook.getWorkbook(inputWorkBook);
		
		workbook = Workbook.createWorkbook(outputWorkBook, inputFile);
			
		writesheet = workbook.getSheet(0);
		writesheet.setName(sSheet1); 
		writesheet1 = workbook.getSheet(1);
		writesheet1.setName(sSheet2);
		
		fontFace = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD);
		fontBold = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD); 
		
		cellBOLD = new WritableCellFormat(fontBold); 
		
		cellBOLD_Border = new WritableCellFormat(fontBold); 
		cellBOLD_Border.setBorder(Border.ALL, BorderLineStyle.THIN);
		cellBOLD_Border.setAlignment(Alignment.CENTRE);
		
		//Added code to make the colour of the column headers in the export files to match closely the headers of the import excel template, Sebastian 29 July 2010
		cellBOLD_Border.setBackground(jxl.format.Colour.ICE_BLUE); 
		cellBOLD_Border.setWrap(true);
		
		bordersData1 = new WritableCellFormat();
		bordersData1.setBorder(Border.ALL, BorderLineStyle.THIN);
		bordersData1.setAlignment(Alignment.CENTRE);
					
		bordersData2 = new WritableCellFormat(fontFace);
		bordersData2.setBorder(Border.ALL, BorderLineStyle.THIN);
		bordersData2.setWrap(true);
		
		bordersData_LeftAlign = new WritableCellFormat();
		bordersData_LeftAlign.setBorder(Border.ALL, BorderLineStyle.THIN);
		bordersData_LeftAlign.setAlignment(Alignment.LEFT);
		
		//Added code to set wrapping of text in the content cells as some cells contents have very long strings/value, Sebastian 29 July 2010
		bordersData_LeftAlign.setWrap(true);
		
		No_Borders_ctrAll = new WritableCellFormat(fontFace);
		No_Borders_ctrAll.setBorder(Border.NONE, BorderLineStyle.NONE);
		No_Borders_ctrAll.setAlignment(Alignment.CENTRE);
		
		No_Borders_ctrAll_Bold = new WritableCellFormat(fontBold);
		No_Borders_ctrAll_Bold.setBorder(Border.NONE, BorderLineStyle.NONE);
		No_Borders_ctrAll_Bold.setAlignment(Alignment.CENTRE);
		
		No_Borders = new WritableCellFormat(fontBold);
		No_Borders.setBorder(Border.NONE, BorderLineStyle.NONE);
		
		No_Borders_NoBold = new WritableCellFormat(fontFace);
		No_Borders_NoBold.setBorder(Border.NONE, BorderLineStyle.NONE);
		No_Borders_NoBold.setWrap(true);
		
		//Removed codes that add the columns in the 2nd spreadsheet (To Delete) in user export file, since there is no method for deleting, Sebastian 29 July 2010
		
		//Include columns in the 2nd spreadsheet necessary to perform delete for the Development Activities and Development Resources exports, Sebastian 29 July 2010
		if (exportType == 6) //exporting development activties
		{
			Label label = new Label(0, 0, "CompetencyName", cellBOLD_Border); 
			writesheet1.addCell(label); 
			label = new Label(1, 0, "DRAStatement", cellBOLD_Border); 
			writesheet1.addCell(label); 
		}
		else if (exportType == 7) //exporting development resources
		{
			Label label = new Label(0, 0, "CompetencyName", cellBOLD_Border); 
			writesheet1.addCell(label); 
			label = new Label(1, 0, "Resource", cellBOLD_Border); 
			writesheet1.addCell(label); 
			label = new Label(2, 0, "ResType", cellBOLD_Border); 
			writesheet1.addCell(label); 
		}
	}
	
	/*--------------------------------------To initialize the headings -------------------------------*/	

	public void Header(int exportType) 
		throws IOException, WriteException, SQLException, Exception
	{	
		/* Switch Function for multiple exportType */
		switch(exportType)
		{
			case 1	: // Export User
				sHeader = new String[13];
				sSubHeader = new String[5]; //Headers for Demographic Details
				
				sHeader[0] = "FKUserType360";
				sHeader[1] = "FamilyName";
				sHeader[2] = "GivenName";
				sHeader[3] = "LoginName";
				sHeader[4] = "SupervisorLoginName";
				sHeader[5] = "Designation";
				sHeader[6] = "IDNumber";
				sHeader[7] = "FKDepartment";
				sHeader[8] = "FKDivision";
				sHeader[9] = "Group_Section";
				sHeader[10] = "IsEnabled";
				sHeader[11] = "Email";
				sHeader[12] = "Password";
				
				sSubHeader[0] = "JobFunctionName";
				sSubHeader[1] = "JobLevelName";
				sSubHeader[2] = "GenderDesc";
				sSubHeader[3] = "EthnicDesc";
				sSubHeader[4] = "LocationName";
				break;
				
			case 2	: //Export Target
				sHeader = new String[2];
				sSubHeader = new String [0]; // Reset to prevent report from printing unwanted columns
				
				sHeader[0] = "SurveyName";
				sHeader[1] = "TargetLoginID";
				break;
				
			case 3	: //Export Assignment
				sHeader = new String[4];
				sSubHeader = new String[1]; //Headers for Rater Relation
				
				sHeader[0] = "SurveyName";
				sHeader[1] = "RTRelation";
				sHeader[2] = "RaterLoginID";
				sHeader[3] = "TargetLoginID";
				
				sSubHeader[0] = "RTSpecific";
				break;
				
			case 4  : // Export Competency
				sHeader = new String [3];
				sSubHeader = new String [0]; // Reset to prevent report from printing unwanted columns
				
				sHeader[0] = "CompetencyName";
				sHeader[1] = "CompetencyDefinition";
				sHeader[2] = "IsSystemGenerated";
				break;
				
			case 5 : // Export KB
				sHeader = new String [4];
				sSubHeader = new String [0]; // Reset to prevent report from printing unwanted columns
				
				sHeader[0] = "CompetencyName";
				sHeader[1] = "KeyBehaviour";
				sHeader[2] = "KBLevel";
				sHeader[3] = "IsSystemGenerated";
				break;
				
			case 6 : // Export Development Activities
				sHeader = new String [3];
				sSubHeader = new String [0]; // Reset to prevent report from printing unwanted columns
				
				sHeader[0] = "CompetencyName";
				sHeader[1] = "DRAStatement";
				sHeader[2] = "IsSystemGenerated";
				break;
				
			case 7 : // Export Development Resources
				sHeader = new String [2];
				sSubHeader = new String [2]; // Res Type
				
				sHeader[0] = "CompetencyName";
				sHeader[1] = "Resource";
				sSubHeader[0] = "ResType";
				sSubHeader[1] = "IsSystemGenerated";
				break;
				
			case 8 : // Export Division
				sHeader = new String [2];
				sSubHeader = new String [0]; // Reset to prevent report from printing unwanted columns
				
				sHeader[0] = "DivisionName";
				sHeader[1] = "DivisionCode";
				break;
				
			case 9 : // Export Department
				sHeader = new String [3];
				sSubHeader = new String [0]; // Reset to prevent report from printing unwanted columns
				
				sHeader[0] = "DivisionName";
				sHeader[1] = "DepartmentName";
				sHeader[2] = "DepartmentCode";
				break;
				
			case 10 : // Export Group / Section
				
				sHeader = new String[3];
				sSubHeader = new String [0]; // Reset to prevent report from printing unwanted columns
				
				sHeader[0] = "DivisionName";
				sHeader[1] = "DepartmentName";
				sHeader[2] = "GroupName";
				
				
				
				break;
			case 11: //export Cluster
				sHeader = new String[2];
				sSubHeader = new String[0];
						
				sHeader[0] = "Clustername";
				sHeader[1] = "FKOrganization";
				break;
			case 12 : // Export Coaching Booking Status Report
				
				sHeader = new String[6];
				sSubHeader = new String [0]; // Reset to prevent report from printing unwanted columns
				
				sHeader[0] = "Date";
				sHeader[1] = "Starting Time";
				sHeader[2] = "Ending Time";
				sHeader[3] = "Coach Name";
				sHeader[4] = "Venue";
				sHeader[5] = "Book By";
				break;
		}

		int row_header = 0;
		if(exportType==12){
			//for Coaching Status Report
			row_header=8;
		}
		
		//Print Main Header
		for(int i=0; i<sHeader.length; i++)
		{
			label = new Label(i, row_header, sHeader[i], cellBOLD_Border); 
			writesheet.addCell(label); 
			
		}
		
		//Print Sub Header (For Demographic details)
		for(int j=0; j<sSubHeader.length; j++)
		{
			label = new Label(j + sHeader.length, row_header, sSubHeader[j], cellBOLD_Border); 
			writesheet.addCell(label); 
		}
		
		/*Date timestamp = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
		String temp = dFormat.format(timestamp);
		//System.out.println(temp);
		writesheet.setHeader("", "", "Pacific Century Consulting Pte Ltd.");
		writesheet.setFooter("Date of printing: " + temp +  "\n" + "Copyright � 3-Sixty Profiler� is a product of Pacific Century Consulting Pte Ltd.", "", "Page &P of &N");
		*/
	}
	
	/**
	 *	ExportType :
	 *  1 => Export User.
	 *	2 => Export Targets Only.
	 *	3 => Export Complete Assignments (Targets, Raters and details).
	 *  4 => Export Competency.
	 *  5 => Export KB.
	 *  6 => Export Development Activities.
	 *  7 => Export Development Resources.
	 *  8 => Export Division.
	 *  9 => Export Department.
	 *  10 => Export Group/Section.
	 *  
	 *  Note: 	For ExportType 2 and 3, You have to setSurveyID() first before using this function.
	 *			For the rest of the ExportType, You have to setOrgID() first before using this function.
	 *
	 * @param exportType	Integer to determine export type
	 * @param PKUser		Integer to store User to be recorded in Event viewer
	 * @throws IOException
	 * @throws WriteException
	 * @throws SQLException
	 * @throws Exception
	 */
	public void export(int exportType, int PKUser) 
		throws IOException, WriteException, SQLException, Exception
	{
		write(exportType);
		Header(exportType);
		
		switch(exportType)
		{
			case 1 : //Export User
				exportUser();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export User", "Export User", sDetail[2], sDetail[11], sDetail[10]);
				break;
				
			case 2 : //Export Targets Only
				exportTarget();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Target", "Export Target Only", sDetail[2], sDetail[11], sDetail[10]);
				break;
				
			case 3 : //Export Assignment
				exportAssignment();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Assignment", "Export Assignment", sDetail[2], sDetail[11], sDetail[10]);
				break;
			
			case 4 : //Export Competency
				exportCompetency();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Competency", "Export Competency", sDetail[2], sDetail[11], sDetail[10]);
				break;
				
			case 5 : //Export KB
				exportBehaviour();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Behaviour", "Export Behaviour", sDetail[2], sDetail[11], sDetail[10]);
				break;
				
			case 6 : //Export Development Activities
				exportDA();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Development Activities", "Export Development Activities", sDetail[2], sDetail[11], sDetail[10]);
				break;
				
			case 7 : //Export Development Resources
				exportDR();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Development Resources", "Export Development Resources", sDetail[2], sDetail[11], sDetail[10]);
				break;
				
			case 8 : //Export Division
				exportDivision();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Division", "Export Division", sDetail[2], sDetail[11], sDetail[10]);
				break;
				
			case 9 : //Export Department
				exportDepartment();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Department", "Export Department", sDetail[2], sDetail[11], sDetail[10]);
				break;
				
			case 10 : //Export Group / Section
				exportGroup();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Group / Section", "Export Group / Section", sDetail[2], sDetail[11], sDetail[10]);
				break;
			case 11 :
				exportCluster();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Cluster", "Export Cluster", sDetail[2], sDetail[11], sDetail[10]);
				break;
			case 12:
				exportCoachingBookingStatus();
				sDetail = user.getUserDetail(PKUser);
				ev.addRecord("EXPORT", "Export Coach Booking Status Report", "Export Cluster", sDetail[2], sDetail[11], sDetail[10]);
				break;
			default:
				exportType = 1;
		}
		
		System.out.println("Export done successfully");
		
		//Call autoFormatWritesheet() method here in order to auto-format the sheets created, Sebastian 29 July 2010
		if(exportType!=12){
			autoFormatWritesheet(writesheet,0); 
		}else{
			autoFormatWritesheet(writesheet,9); 
		}
		
		//if 2nd spread sheet have columns inside
		if (!writesheet1.getName().equals("Sheet2"))
		{
			autoFormatWritesheet(writesheet1,0);
		}
		
		workbook.write();
		workbook.close(); 
	}
	
	/**
	  * Auto format the rows and cols of the Write Sheet to fit the text in each cell
	  * 
	  * @param WritableSheet writesheet - writesheet to format
	  * @author Sebastian
	  * @since v.1.3.12.88 (29 July 2010)
	**/
	
	public void autoFormatWritesheet(WritableSheet writesheet, int startingRow)
	{
		int iColWidth = 0;
		int iCurrColWidth = 0;
		
		WritableCell cell;
		
		
		for (int i=0;i<writesheet.getColumns();i++)
		{
			for (int j=startingRow;j<writesheet.getRows();j++)
			{
				cell = writesheet.getWritableCell(i, j);
				iCurrColWidth = cell.getContents().length();
				
				if (iColWidth < iCurrColWidth)
				{
					iColWidth = iCurrColWidth;
				}
			}
			
			if (iColWidth > 50)
			{
				iColWidth = 50;
			}
			
			writesheet.setColumnView(i, Math.round((float)(iColWidth*1.40)));
			iColWidth = 0;
		}
		writesheet.setColumnView(1, Math.round((float)(7*1.40)));
		writesheet.setColumnView(2, Math.round((float)(7*1.40)));
	}
	public void exportCoachingBookingStatus() throws IOException, WriteException, SQLException, Exception {
		String query="SELECT  CoachDate.Date, CoachSlot.StartingTime, CoachSlot.EndingTime,";
		query=query+" Coach.CoachName,CoachVenue.Venue1, tblOrganization.OrganizationName, tblOrganization.OrganizationCode, CoachAssignment.UserFK,";
		query=query+" tblConsultingCompany.CompanyName, CoachSession.SessionName, tblSurvey.SurveyName";
		query=query+" FROM  CoachAssignment INNER JOIN";
		query=query+" CoachSession ON CoachSession.PKCoachSession = CoachAssignment.SessionFK INNER JOIN";
		query=query+" CoachDate ON CoachDate.PKCoachDate = CoachAssignment.DateFK INNER JOIN";
		query=query+" CoachSlot ON CoachSlot.CoachSlotPK = CoachAssignment.SlotFK INNER JOIN";
		query=query+" Coach ON Coach.PKCoach = CoachAssignment.CoachFK INNER JOIN";
		query=query+" tblOrganization ON tblOrganization.PKOrganization = CoachAssignment.OrgFK INNER JOIN";
		query=query+" CoachVenue ON CoachVenue.VenuePK = CoachAssignment.VenueFK INNER JOIN";
		query=query+" tblConsultingCompany on tblOrganization.FKCompanyID=tblConsultingCompany.CompanyID  INNER JOIN";
		query=query+" tblSurvey ON tblSurvey.SurveyID = CoachAssignment.SurveyFK";
		query=query+" WHERE (CoachAssignment.SessionFK = "+this.getSelectedSession()+")";
		query=query+" ORDER BY Date";
       	System.out.println(query);
       	String tile="Coach Booking Status Report";
       	String companyName="";
       	String organizationName="";
       	String surveyName="";
      
       	int row_data = 9;

    	Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(query);
			while(rs.next())	
			{
				organizationName=rs.getString("OrganizationName");
				companyName=rs.getString("CompanyName");
				surveyName=rs.getString("SurveyName");
	           	for(int i=0; i<sHeader.length; i++)
				{	
	           		String sData = "";
	           		if(sHeader[i].equalsIgnoreCase("Date")){
	           			String date=rs.getString("Date").substring(0, 10);
						Date dateString = new SimpleDateFormat("yyyy-MM-dd").parse(date);
						SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yyyy");
						sData=sdf.format(dateString);
	           		}else if(sHeader[i].equalsIgnoreCase("Starting Time")){
	           			sData=Integer.toString(rs.getInt("StartingTime"));
	           			if(sData.length()==3)
	           				sData="0"+sData;
	           		}else if(sHeader[i].equalsIgnoreCase("Ending Time")){
	           			sData=Integer.toString(rs.getInt("EndingTime"));
	           			if(sData.length()==3)
	           				sData="0"+sData;
	           		}
	           		else if(sHeader[i].equalsIgnoreCase("Coach Name")){
	           			sData=rs.getString("CoachName");
	           			
	           		}
	           		else if(sHeader[i].equalsIgnoreCase("Venue")){
	           			sData=rs.getString("Venue1");
	           		}
	           		else if(sHeader[i].equalsIgnoreCase("Book By")){
	           			int userPK=rs.getInt("UserFK");
	           			User user=new User();
	           			voUser userDetials=user.getUserInfo(userPK);	
	           			sData=userDetials.getFamilyName()+" "+userDetials.getGivenName();
	           		}
	           		
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label); 
					//System.out.println(rs.getString(sHeader[i]));
				}
				row_data++;
			}
		 	//write the survey information
	       	label = new Label(0, 0, tile, cellBOLD); 
	       	writesheet.addCell(label);
	       	label = new Label(0, 2, "Company:", cellBOLD);
	       	writesheet.addCell(label);
	       	label = new Label(1, 2, companyName, cellBOLD);
	       	writesheet.addCell(label);
	       	label = new Label(0, 4, "Organisation:", cellBOLD);
	       	writesheet.addCell(label);
	       	label = new Label(1, 4, organizationName, cellBOLD);
	       	writesheet.addCell(label);
	     	label = new Label(0, 6, "Survey Name:", cellBOLD);
	     	writesheet.addCell(label);
	       	label = new Label(1, 6, surveyName, cellBOLD); 
			writesheet.addCell(label); 
			// End of writing  survey information
			
		
		}catch(Exception ex){
	   		System.out.println("Export.java - exportCoachingBookingStatus - "+ex.getMessage());
	   	}
	   	finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
	   	}
	}
	public String getOrganizationCodeBySurveyID() throws IOException, WriteException, SQLException, Exception {
		String query=" SELECT  tblOrganization.OrganizationCode";
		query=query+" FROM  CoachAssignment  INNER JOIN";
		query=query+" tblOrganization ON tblOrganization.PKOrganization = CoachAssignment.OrgFK ";
		query=query+" WHERE CoachAssignment.SessionFK = "+this.getSelectedSession();
     
       	String organizationCode="";

    	Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(query);
			while(rs.next())	
			{
				organizationCode=rs.getString("OrganizationCode");
				
			}
		
		}catch(Exception ex){
	   		System.out.println("Export.java - exportOrganizationCodeBySurveyID - "+ex.getMessage());
	   	}
	   	finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
	   	}
  	   	return organizationCode;
	}
	public String getCoachingStatusFileName() throws WriteException, IOException, SQLException, Exception{
		String organizationCode=this.getOrganizationCodeBySurveyID();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
		String fileGeneratedTime=sdf.format(date);
		return "Coach Booking Status for " +organizationCode+" as at "+fileGeneratedTime+".xls";
	}
	
	
	/* This function is to export List of User with all the details
	 * Note: You have to setOrgID() first before using this function
	 *
	 * @see setOrgID(int iTemp) 
	 *
	 */
	public void exportUser() 
		throws IOException, WriteException, SQLException, Exception
	{
		/*
		String sql = "SELECT UserType.UserTypeName AS FKUserType360, [User].FamilyName, [User].GivenName, [User].LoginName, [User].Password, User_Sup.LoginName AS SupervisorLoginName, "; 
       	sql = sql + "[User].Designation, [User].IDNumber, Department.DepartmentName AS FKDepartment, Division.DivisionName AS FKDivision, [Group].GroupName AS Group_Section, [User].IsEnabled, [User].Email, ";
       	sql = sql + "JobFunction.JobFunctionName AS JobFunction, JobLevel.JobLevelName AS JobLevel, Gender.GenderDesc AS Gender, Ethnic.EthnicDesc AS EthnicGroup, Location.LocationName AS Location ";
		sql = sql + "FROM tblUserRelation INNER JOIN [User] User_Sup ON tblUserRelation.User2 = User_Sup.PKUser INNER JOIN ";
		sql = sql + "[User] INNER JOIN UserType ON [User].FKUserType360 = UserType.PKUserType ON tblUserRelation.User1 = [User].PKUser INNER JOIN ";
       	sql = sql + "Department ON [User].FKDepartment = Department.PKDepartment INNER JOIN ";
       	sql = sql + "Division ON [User].FKDivision = Division.PKDivision INNER JOIN ";
		sql = sql + "[Group] ON [User].Group_Section = [Group].PKGroup INNER JOIN ";
		sql = sql + "UserDemographic ON [User].PKUser = UserDemographic.FKUser INNER JOIN ";
      	sql = sql + "JobFunction ON UserDemographic.FKJobFunction = JobFunction.PKJobFunction INNER JOIN ";
   		sql = sql + "JobLevel ON UserDemographic.FKJobLevel = JobLevel.PKJobLevel INNER JOIN ";
    	sql = sql + "Gender ON UserDemographic.FKGender = Gender.PKGender INNER JOIN ";
		sql = sql + "Ethnic ON UserDemographic.FKEthnic = Ethnic.PKEthnic INNER JOIN ";
		sql = sql + "Location ON UserDemographic.FKLocation = Location.PKLocation ";
		sql = sql + "WHERE ([User].LoginName <> 'sa') AND ([User].FKOrganization = " + FKOrganizationID + ") AND ([User].FKCompanyID = " + FKCompanyID + ")";
        */
        
        String sql = "SELECT UserType.UserTypeName AS FKUserType360, [User].PKUser, [User].FamilyName, [User].GivenName, [User].LoginName, [User].Password, ";
        sql = sql + "User_Sup.LoginName AS SupervisorLoginName, [User].Designation, [User].IDNumber, Department.DepartmentName AS FKDepartment, ";
        sql = sql + "Division.DivisionName AS FKDivision, [Group].GroupName AS Group_Section, [User].IsEnabled, [User].Email ";
		sql = sql + "FROM tblUserRelation INNER JOIN ";
        sql = sql + "[User] User_Sup ON tblUserRelation.User2 = User_Sup.PKUser INNER JOIN ";
        sql = sql + "[User] INNER JOIN ";
        sql = sql + "UserType ON [User].FKUserType360 = UserType.PKUserType ON tblUserRelation.User1 = [User].PKUser INNER JOIN ";
        sql = sql + "Department ON [User].FKDepartment = Department.PKDepartment INNER JOIN ";
        sql = sql + "Division ON [User].FKDivision = Division.PKDivision INNER JOIN ";
        sql = sql + "[Group] ON [User].Group_Section = [Group].PKGroup ";
		sql = sql + "WHERE ([User].LoginName <> 'sa') AND ([User].FKOrganization = " + getOrgID() + ")";
       	
       	int row_data = 1;

       	Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{
				int PKUser= rs.getInt("PKUser");
	          // 	System.out.println("Exporting PKUser = " + PKUser);
	           	
	           	for(int i=0; i<sHeader.length; i++)
				{
					String sData = "";
					if(rs.getString(sHeader[i]) != null)
					{
						sData = rs.getString(sHeader[i]).trim();
					}
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label); 
					
				}
				//Print Demographic details
				Vector rsDemographic = demographic.getUserDemographicDetail(PKUser);

				for(int i=0; i<rsDemographic.size(); i++)	
				{
					voUserDemographic voUD = (voUserDemographic)rsDemographic.elementAt(i);
					for(int j=0; j<sSubHeader.length; j++)
					{
						String value = "";
						if(sSubHeader[j].equals("JobFunctionName")){
							value = voUD.getJobFunction();
						} else if(sSubHeader[j].equals("JobLevelName")){
							value = voUD.getJobLevel();
						} else if(sSubHeader[j].equals("GenderDesc")){
							value = voUD.getGender();
						} else if(sSubHeader[j].equals("EthnicDesc")){
							value = voUD.getEthnic();
						} else if(sSubHeader[j].equals("LocationName")){
							value = voUD.getLocation();
						} 
						
						label = new Label(j + sHeader.length, row_data, value.trim(), bordersData_LeftAlign); 
						writesheet.addCell(label); 
					}
				}
				//END Print Demographic details
				
				//Check if there is no demographic return. If no demographic, fill the respective cells with the right format but empty, Sebastian 29 July 2010
				if (rsDemographic.size() == 0)
				{
					for(int j=0; j<sSubHeader.length; j++)
					{
						label = new Label(j + sHeader.length, row_data, "", bordersData_LeftAlign); 
						writesheet.addCell(label); 
					}
				}
				
				row_data++;
			}
  	   	}catch(Exception ex){
  	   		System.out.println("Export.java - ExportUser - "+ex.getMessage());
  	   	}
  	   	finally{
  			ConnectionBean.closeRset(rs); //Close ResultSet
  			ConnectionBean.closeStmt(st); //Close statement
  			ConnectionBean.close(con); //Close connection
  	   	}
	}

	/* This function is to export List of Target
	 * Note: You have to setSurveyID() first before using this function
	 *
	 * @see setSurveyID(int iTemp) 
	 *
	 */
	public void exportTarget() throws IOException, WriteException, SQLException, Exception {
        String sql = "SELECT DISTINCT tblSurvey.SurveyName, [User].LoginName AS TargetLoginID ";
		sql = sql + "FROM tblAssignment INNER JOIN ";
        sql = sql + "[User] ON tblAssignment.TargetLoginID = [User].PKUser INNER JOIN ";
        sql = sql + "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID ";
		sql = sql + "WHERE (tblAssignment.SurveyID = " + getSurveyID() + ") ";
       	
       	int row_data = 1;

    	Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{
	           	for(int i=0; i<sHeader.length; i++)
				{
					label = new Label(i, row_data, rs.getString(sHeader[i]).trim(), bordersData_LeftAlign); 
					writesheet.addCell(label); 
					//System.out.println(rs.getString(sHeader[i]));
				}
				row_data++;
			}
		
		}catch(Exception ex){
	   		System.out.println("Export.java - ExportTarget - "+ex.getMessage());
	   	}
	   	finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
	   	}
	}
	
	
	/* This function is to export Assignment by SurveyID
	 * Note: You have to setSurveyID() first before using this function
	 *
	 * @see setSurveyID(int iTemp) 
	 *
	 */
	public void exportAssignment() throws IOException, WriteException, SQLException, Exception {
            
		String sql = "SELECT tblSurvey.SurveyName AS SurveyName, tblRelationHigh.RelationHigh AS RTRelation, ";
        sql = sql + "tblAssignment.RTSpecific, User_Rater.LoginName AS RaterLoginID, ";
        sql = sql + "User_Target.LoginName AS TargetLoginID FROM [User] User_Rater INNER JOIN ";
		sql = sql + "tblRelationHigh INNER JOIN tblAssignment INNER JOIN ";
     	sql = sql + "tblSurvey ON tblAssignment.SurveyID = tblSurvey.SurveyID ON ";
     	sql = sql + "tblRelationHigh.RelationID = tblAssignment.RTRelation INNER JOIN ";
    	sql = sql + "[User] User_Target ON tblAssignment.TargetLoginID = User_Target.PKUser ON ";
    	sql = sql + "User_Rater.PKUser = tblAssignment.RaterLoginID ";                      
		sql = sql + "WHERE (tblAssignment.SurveyID = " + getSurveyID() + ") ";
       		
       	int row_data = 1;

    	Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{
				int iRelSpecID = rs.getInt("RTSpecific");
				
	           	for(int i=0; i<sHeader.length; i++)
				{
					label = new Label(i, row_data, rs.getString(sHeader[i]).trim(), bordersData_LeftAlign); 
					writesheet.addCell(label); 
					//System.out.println(rs.getString(sHeader[i]));
				}
					
				//Print Relation Specific
				String rsRTSpecific = RTRel.getRelSpec(iRelSpecID);
				for(int j=0; j<sSubHeader.length; j++)
				{
					label = new Label(j + sHeader.length, row_data, rsRTSpecific.trim(), bordersData_LeftAlign); 
					writesheet.addCell(label); 
				}
				//END Print Relation Specific
				
				row_data++;
			}
  	   	}catch(Exception ex){
  	   		System.out.println("Export.java - ExportAssignment - "+ex.getMessage());
      	}
  	   	finally{
  			ConnectionBean.closeRset(rs); //Close ResultSet
  			ConnectionBean.closeStmt(st); //Close statement
  			ConnectionBean.close(con); //Close connection
  	   	}
	}
	
	/**
	 * Export Cluster in System Library
	 * @throws IOException
	 * @throws WriteException
	 * @throws SQLException
	 * @throws Exception
	 */
	public void exportCluster() throws IOException, WriteException, SQLException, Exception
	{
		int row_data = 1;
		
        String sql = "SELECT * FROM Cluster WHERE FKOrganization IN (1, " + getOrgID() + ") ORDER BY ClusterName ";

        Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{	
		       	for(int i=0; i<sHeader.length; i++)
				{
					String sData = "";
					if(rs.getString(sHeader[i]) != null)
					{
						sData = UnicodeHelper.getUnicodeStringAmp(rs.getString(sHeader[i]).trim());
					}
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label); 
				}
				
				row_data++;
			}
  		}catch(Exception ex){
  	   		System.out.println("Export.java - ExportCompetency - "+ex.getMessage());
      	}
  	   	finally{
  			ConnectionBean.closeRset(rs); //Close ResultSet
  			ConnectionBean.closeStmt(st); //Close statement
  			ConnectionBean.close(con); //Close connection
  	   	}
	}
	
	/**
	 * Export Competencies in System Library
	 * @throws IOException
	 * @throws WriteException
	 * @throws SQLException
	 * @throws Exception
	 */
	public void exportCompetency() throws IOException, WriteException, SQLException, Exception
	{
		int row_data = 1;
		
        String sql = "SELECT * FROM Competency WHERE FKOrganizationID IN (1, " + getOrgID() + ") ORDER BY CompetencyName ";

        Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{	
		       	for(int i=0; i<sHeader.length; i++)
				{
					String sData = "";
					if(rs.getString(sHeader[i]) != null)
					{
						sData = UnicodeHelper.getUnicodeStringAmp(rs.getString(sHeader[i]).trim());
					}
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label); 
				}
				
				row_data++;
			}
  		}catch(Exception ex){
  	   		System.out.println("Export.java - ExportCompetency - "+ex.getMessage());
      	}
  	   	finally{
  			ConnectionBean.closeRset(rs); //Close ResultSet
  			ConnectionBean.closeStmt(st); //Close statement
  			ConnectionBean.close(con); //Close connection
  	   	}
	}
	
	
	/**
	 * Export KB from System Library
	 * @throws IOException
	 * @throws WriteException
	 * @throws SQLException
	 * @throws Exception
	 */
	public void exportBehaviour() throws IOException, WriteException, SQLException, Exception
	{
		int row_data = 1;
		
		String sql = "SELECT Comp.CompetencyName, KB.KeyBehaviour, KB.KBLevel, KB.IsSystemGenerated ";
		sql = sql + "FROM  Competency Comp INNER JOIN KeyBehaviour KB ON Comp.PKCompetency = KB.FKCompetency ";
		sql = sql + "WHERE KB.FKOrganizationID IN (1, " + getOrgID() + ") ORDER BY Comp.CompetencyName, KB.KeyBehaviour";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{	
	           	for(int i=0; i<sHeader.length; i++)
				{
					String sData = "";
					if(rs.getString(sHeader[i]) != null)
					{
						sData = UnicodeHelper.getUnicodeStringAmp(rs.getString(sHeader[i]).trim());
					}
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label); 
				}
				
				row_data++;
			}
		}catch(Exception ex){
		   		System.out.println("Export.java - ExportBehhaviour - "+ex.getMessage());
	  	}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
	   	}
	}
	
	/**
	 * Export Development Activities
	 * @throws IOException
	 * @throws WriteException
	 * @throws SQLException
	 * @throws Exception
	 */
	public void exportDA() throws IOException, WriteException, SQLException, Exception
	{
		int row_data = 1;
		
		String sql = "SELECT Comp.CompetencyName, DRA.DRAStatement, DRA.IsSystemGenerated ";
		sql = sql + "FROM  tblDRA DRA INNER JOIN Competency Comp ON DRA.CompetencyID = Comp.PKCompetency ";
		sql = sql + "WHERE DRA.FKOrganizationID IN (1, " + getOrgID() + ") ORDER BY Comp.CompetencyName, DRA.DRAStatement";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{		
	           	for(int i=0; i<sHeader.length; i++)
				{
					String sData = "";
					if(rs.getString(sHeader[i]) != null)
					{
						sData = UnicodeHelper.getUnicodeStringAmp(rs.getString(sHeader[i]).trim());
					}
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label); 
				}
				
				row_data++;
			}
  	   	}catch(Exception ex){
  	   		System.out.println("Export.java - ExportDA - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
	 	}
	}
	
	/**
	 * Export Development Resources
	 * @throws IOException
	 * @throws WriteException
	 * @throws SQLException
	 * @throws Exception
	 */
	public void exportDR() throws IOException, WriteException, SQLException, Exception
	{
		int row_data = 1;
		int iResType = 0;
		String sResType = "";
		String sData = "";
		
		String sql = "SELECT Comp.CompetencyName, DRes.Resource, DRes.ResType, DRes.IsSystemGenerated ";
		sql = sql + "FROM tblDRARes DRes INNER JOIN Competency Comp ON DRes.CompetencyID = Comp.PKCompetency ";
		sql = sql + "WHERE DRes.FKOrganizationID IN (1, " + getOrgID() + ") ORDER BY Comp.CompetencyName, DRes.ResType, DRes.Resource";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{	
		       	for(int i=0; i<sHeader.length; i++)
				{
					if(rs.getString(sHeader[i]) != null)
						sData = UnicodeHelper.getUnicodeStringAmp(rs.getString(sHeader[i]).trim());
					
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label); 
				}
				
		       	iResType = rs.getInt("ResType");
		       	switch (iResType)
		       	{
		       		case 0 : sResType = "";
		       				 break;
		       		case 1 : sResType = "Book";
		       				 break;
		       		case 2 : sResType = "Web Resource";
		       				 break;
		       		case 3 : sResType = "Training Course";
		       				 break;
		       		case 4 : sResType = "AV Resource"; // Change Resource category from "In-house Resource" to "AV Resource", Desmond 10 May 2011
		       				 break;
		       	}
		       	
		       	for(int j=0; j<sSubHeader.length; j++)
		       	{
		       		if(rs.getString(sSubHeader[j]) != null)
		       			sData = rs.getString(sSubHeader[j]);
		       		
		       		if(j == 0)	// Resource Type
		       			label = new Label(sHeader.length, row_data, sResType, bordersData_LeftAlign); 
		       		else		// System Generated ?
		       			label = new Label(sHeader.length + 1, row_data, sData, bordersData_LeftAlign);
		
		       		writesheet.addCell(label);
		       	}
		      
				row_data++;
			}
		
		}catch(Exception ex){
	   		System.out.println("Export.java - ExportDR - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
	}
	
	/**
	 * Export Division
	 * @throws IOException
	 * @throws WriteException
	 * @throws SQLException
	 * @throws Exception
	 */
	public void exportDivision() throws IOException, WriteException, SQLException, Exception
	{
		int row_data = 1;
		String sData = "";
		
		String sql = "SELECT * FROM [Division] WHERE FKOrganization = " + getOrgID() + " ORDER BY DivisionName";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

  	   	try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{
		       	for(int i=0; i<sHeader.length; i++)
				{
					if(rs.getString(sHeader[i]) != null)
						sData = rs.getString(sHeader[i]).trim();
					
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label);
					writesheet.setColumnView(i,30); 
				}
				
				row_data++;
  	   		}
  	   	}catch(Exception ex){
	   		System.out.println("Export.java - ExportDivision - "+ex.getMessage());
		}
		finally{
		ConnectionBean.closeRset(rs); //Close ResultSet
		ConnectionBean.closeStmt(st); //Close statement
		ConnectionBean.close(con); //Close connection
		}
	}
	
	/**
	 * Export Department
	 * @throws IOException
	 * @throws WriteException
	 * @throws SQLException
	 * @throws Exception
	 */
	public void exportDepartment() throws IOException, WriteException, SQLException, Exception
	{
		int row_data = 1;
		String sData = "";
        
		String sql = "SELECT Div.DivisionName, Dept.DepartmentName, Dept.DepartmentCode ";
		sql = sql + "FROM Department Dept INNER JOIN Division Div ON Dept.FKDivision = Div.PKDivision ";
		sql = sql + "WHERE Dept.FKOrganization = " + getOrgID() +" ORDER BY Div.DivisionName, Dept.DepartmentName";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{
	           	for(int i=0; i<sHeader.length; i++)
				{
					if(rs.getString(sHeader[i]) != null)
						sData = rs.getString(sHeader[i]).trim();
					
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label);
					writesheet.setColumnView(i,30);
				}
				
				row_data++;
			}
		
		}catch(Exception ex){
			System.out.println("Export.java - ExportDepartment - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
	}
	
	/**
	 * Export Group
	 * @throws IOException
	 * @throws WriteException
	 * @throws SQLException
	 * @throws Exception
	 */
	
	public void exportGroup() throws IOException, WriteException, SQLException, Exception
	{
		int row_data = 1;
		String sData = "";
        
		String sql = "SELECT Div.DivisionName, Dept.DepartmentName, [Group].GroupName ";
		sql = sql + "FROM [Division] Div INNER JOIN Department Dept ON Div.PKDivision = Dept.FKDivision ";
		sql = sql + "INNER JOIN [Group] ON Dept.PKDepartment = [Group].FKDepartment ";
		sql = sql + "WHERE [Group].FKOrganization = " + getOrgID() + " ORDER BY Div.DivisionName, Dept.DepartmentName, [Group].GroupName";
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try{
  	   		con=ConnectionBean.getConnection();
  	   		st=con.createStatement();
  	   		rs=st.executeQuery(sql);
			while(rs.next())	
			{
	           	for(int i=0; i<sHeader.length; i++)
				{
					if(rs.getString(sHeader[i]) != null)
						sData = rs.getString(sHeader[i]).trim();
					
					label = new Label(i, row_data, sData, bordersData_LeftAlign); 
					writesheet.addCell(label);
					writesheet.setColumnView(i,30);
				}
				
				row_data++;
			}
		}catch(Exception ex){
			System.out.println("Export.java - ExportGroup - "+ex.getMessage());
		}
		finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
	}
	
	/* This function is to set Public SurveyID value to be used by methods in Export.java
	 * 
	 * @param iSurveyID		The integer to pass to Public integer SurveyID
	 *
	 */
	public void setSurveyID(int iTemp)
	{
		iSurveyID = iTemp;
	}
	
	/* This function is to get Public SurveyID value
	 * 
	 * @return iSurveyID	The integer to pass to the caller method
	 *
	 */
	public int getSurveyID()
	{
		return iSurveyID;
	}
	
	/* This function is to set OrgID value to be used by methods in Export.java
	 * 
	 * @param iSurveyID		The integer to pass to Public integer SurveyID
	 *
	 */
	public void setOrgID(int iTemp)
	{
		iOrgID = iTemp;
	}
	
	/* This function is to get Public OrgID value
	 * 
	 * @return iOrgID	The integer to pass to the caller method
	 *
	 */
	public int getOrgID()
	{
		return iOrgID;
	}
	
	public static void main (String[] args)throws SQLException, Exception
	{
		Export exp = new Export();
		
		exp.setSurveyID(445);
		exp.setOrgID(36);
		exp.export(10, 2);
		
		//exp.export(1, 6403);
		//System.out.println(exp.getSurveyID());
	}

	public int getSelectedSession() {
		return selectedSession;
	}

	public void setSelectedSession(int selectedSession) {
		this.selectedSession = selectedSession;
	}

	
	
}	