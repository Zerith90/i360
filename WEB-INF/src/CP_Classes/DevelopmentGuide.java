package CP_Classes;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.io.IOException;

import CP_Classes.common.ConnectionBean;
import CP_Classes.vo.voCompetency;
import CP_Classes.vo.votblSurvey;
import CP_Classes.vo.votblSurveyRating;
import CP_Classes.Translate;

import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheet;

/*****
 * 
 * Edited By Roger 13 June 2008
 * Add additional orgId when calling sendMail
 *
 * Change Log
 * ==========
 * 
 * Date        	By				Method(s)            		Change(s) 
 * ==========================================================================================================
 * 04/10/2011	Gwen Oh			-							Added lang and selectedTemplate variables and their accessor methods
 * 04/10/2011	Gwen Oh			AllSurvey(), 				1) Get translation of words from db instead of hardcoding it.
 * 								selTarget(),		   		This allows the flexibility of having more languages without needing to change the code.													 
 * 								selComp()					2) Get competency and keybehaviour info	according to the language selected
 * 
 * 18/07/2012	Albert			InsertParagraphDesc()		Add method to insert description in the development guide generation			   
 */
public class DevelopmentGuide
{

	private Setting ST;
	private Create_Edit_Survey CE_Survey;
	private MailHTMLStd EMAIL;
	private EventViewer EV;
	private OpenOffice OO;
	private Database db;
	//private Excel E;
	
	private String sDetail[] = new String[13];
 	private String itemName = "Report";
	
	private XMultiComponentFactory xRemoteServiceManager = null;
	private XComponent xDoc = null;
	private XSpreadsheet xSpreadsheet = null;
	private String storeURL;
	//Gwen Oh - 04/10/11: Add template and lang variables
	private String selectedTemplate = "";
	private int lang = 0;
	private boolean isEngResIncluded = false;
	private Translate T;
	
	private int Survey_ID = 0;
	private int Type  = 0;
	private int startColumn;
	private int endColumn;
	
	public DevelopmentGuide()
	{
		ST = new Setting();
		CE_Survey = new Create_Edit_Survey();
		EMAIL = new MailHTMLStd();
		EV = new EventViewer();
		OO	= new OpenOffice();
		//E = new Excel();
		db = new Database();
		T	= new Translate();
		T.populateHashtable();
	}
	
	/**
	 * Initialize all the processes dealing with Excel Application.
	 */
	public void InitializeExcel(String savedFileName, int lang) throws IOException, Exception 
	{	
		storeURL = "file:///" + ST.getOOReportPath() + savedFileName;
		/**
		 * xukun 03/06/2013 use lang to determine which template to be used
		 */
		switch(lang){
		case 0: selectedTemplate = "DevelopmentGuide Template.xls"; break;//eng
		case 1: selectedTemplate = "DevelopmentGuide Template.xls"; break;//indo
		case 2: selectedTemplate = "DevelopmentGuide Template.xls"; break;//thai
		case 3: selectedTemplate = "DevelopmentGuide Template_chinese(simplified).xls"; break;//chinese s
		case 4: selectedTemplate = "DevelopmentGuide Template_chinese(traditional).xls"; break;//chinese t
		case 5: selectedTemplate = "DevelopmentGuide Template.xls"; break;// korean
		default: selectedTemplate = "DevelopmentGuide Template.xls"; break;
		}
		String templateURL 	= "file:///" + ST.getOOReportTemplatePath() + selectedTemplate;
		
		xRemoteServiceManager = OO.getRemoteServiceManager("uno:socket,host=localhost,port=2002;urp;StarOffice.ServiceManager");
		
		xDoc = OO.openDoc(xRemoteServiceManager, templateURL);		
		xSpreadsheet = OO.getSheet(xDoc, "DEVELOPMENT GUIDE");
	}
	
	/**
	 *	Replace one string with another, this is used only if we are using template.
	 * 	@param String SurveyName
	 */
	public void ReplacementSurvey(String SurveyName) throws SQLException, IOException, Exception
	{
		String title="";
		
		title = T.tslt(lang, "Survey Name") + ":";
			
		try 
		{
			OO.findAndReplace(xSpreadsheet, "<survey1>", title);
			OO.findAndReplace(xSpreadsheet, "<survey2>", UnicodeHelper.getUnicodeStringAmp(SurveyName));
		}
		catch(IOException IE) 
		{
			System.err.println(IE.getMessage());
		}
	}
	
	/**
	 * 	Replace one string with another, this is used only if we are using template.
	 * 	@param String TargetName
	 */
	public void ReplacementTarget(String TargetName) throws SQLException, IOException, Exception
	{
		String title="";
		
		if (!TargetName.equals(""))
			title = T.tslt(lang, "Target Name") + ":";
			
		try 
		{
			OO.findAndReplace(xSpreadsheet, "<target1>", title);
			OO.findAndReplace(xSpreadsheet, "<target2>", TargetName);		
		}
		catch(IOException IE) 
		{
			System.err.println(IE.getMessage());
		}
	}
	
	public void InsertParagraphDesc() throws Exception{
		int row = 56;
		int col = 0;
		OO.insertRows(xSpreadsheet, col, col, row-1, row, 1, 1);
		String desc = "This development guide only recommends development resources for competencies that you were considered weak in. It acts as a library of resources for you to select from to craft your Individual Development Plan (IDP). Different types of development resources including activities, web resources, books, videos, audio books, training programs, etc. are recommended to fit different learning styles. If you are a keen reader, books will be a good source for your IDP. If you are more action oriented, activities or videos may fit you better. Generally, it is wise to select a few items from each category to formulate your IDP. There is really no need to read all the books, implement all the activities or view all the videos listed here. While every attempt has been made to ensure that the resources listed are current and up-to-date at the point of printing this guide, we are unable to guarantee its availability. However, most of the time, you will be able to access and acquire a large proportion of the materials listed in the guide.";
		OO.insertString(xSpreadsheet, desc, row, col);
		OO.mergeCells(xSpreadsheet, 0, 1, row, row);
		OO.wrapText(xSpreadsheet, 0, 1, row, row);
		OO.setRowHeight(xSpreadsheet, row,col, 560*OO.countTotalRow(desc, 90));
	}

	
	public void InsertParagraphDesc4() throws Exception{
		int row = 56;
		int col = 0;
		OO.insertRows(xSpreadsheet, col, col, row-1, row, 1, 1);
		OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
		OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
		String desc = "鐧煎睍鎸囧崡鍙渻閲濆皪鎮ㄥ皻寰呯櫦灞曠殑鑱疯兘鎺ㄨ枽鐧煎睍璩囨簮銆傚ソ姣斾竴鍊嬪湒鏇搁え渚涙偍寰炰腑閬告搰浠ョ敤渚嗘墦閫犳偍鐨勫�浜虹櫦灞曡▓鍔冿紙IDP锛夈�涓嶅悓绋鐨勭櫦灞曡硣婧愬寘鎷鐐哄垪琛紝缍茬怠璩囨簮锛岀浉闂滄浉绫嶏紝瑕栬伣璩囨簮锛屽煿瑷撹绋嬬瓑绛夋渻鎺ㄨ枽绲︽偍浠ョ鍚堟偍鐨勫缈掗ⅷ鏍笺�濡傛灉鎮ㄦ槸涓��鐔卞勘鐨勮畝鑰咃紝鏇哥睄鏈冩垚鐐烘偍鐨勫�浜虹櫦灞曡▓鍔冪殑濂借硣婧愩�濡傛灉鎮ㄦ洿浠ヨ鍕曠偤灏庡悜锛岃鍕曞垪琛ㄥ拰瑕栭牷璩囨簮鍓囨洿閬╁悎鎮ㄣ�閫氬父渚嗚瑳锛屾槑鏅虹殑閬告搰鐨勬槸寰炴瘡涓��鍒楀垾瑁℃寫閬镐竴浜涜硣婧愪締鎵撻�鑷繁鐨勫�浜虹櫦灞曡▓鍔冦�鎮ㄤ甫涓嶉渶瑕侀柋璁�叏閮ㄧ殑鏇哥睄鈥嬧�锛屽鏂芥墍鏈夌殑娲诲嫊鍜岀湅瀹屾墍鏈夌殑瑕栭牷銆傚剺绠℃垜鍊戝姫鍔涚⒑淇濇墍鏈夊湪琛ㄥ柈涓殑璩囨簮鐨勬檪鏁堟�锛屾垜鍊戜笉鑳戒繚璀夋偍閫欎簺璩囨簮鐨勫彲鐢ㄦ�銆備絾鏄禃澶у鏁告檪闁擄紝鎮ㄥ彲浠ヨí鍟忎甫涓旂嵅鍙栧ぇ閮ㄥ垎鍒楀湪鎸囧崡涓殑璩囨簮銆�";
		OO.insertString(xSpreadsheet, desc, row, col);
		OO.mergeCells(xSpreadsheet, 0, 1, row, row); 
		OO.wrapText(xSpreadsheet, 0, 1, row, row);
		OO.setRowHeight(xSpreadsheet, row,col, 560*OO.countTotalRow(desc, 90));
	}
	
	//find organisation ID given PKUser ID
		/* @param int PKUser ID
		 */
		//This is to help filter recommended activities more specificly
	
	public int UserIDtoOrgID(int PKUser){
		int OrgID = 0;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs_comp = null;
		
		String Query = "SELECT FKOrganization FROM [User] WHERE (PKUser =" + PKUser + ")";
		
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs_comp=st.executeQuery(Query);
			
			OrgID = rs_comp.getInt("FKOrganization");
		    }catch(Exception ex){
			System.out.println("DevelopmentGuide.java - AllSurvey - "+ex.getMessage());
		    }finally{
			ConnectionBean.closeRset(rs_comp); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		
		return OrgID;
	}
	
	/**
	 * @author xukun
	 * @param con
	 * @param st
	 * @param Sql_Resource
	 * @param startRow
	 * @param row
	 * @param col
	 * @return
	 * @throws Exception
	 */
	public int retrieveRDARes(Connection con, Statement st, String Sql_Resource, int startRow, int row, int col) throws Exception{
		ResultSet rs_Res = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs_Res=st.executeQuery(Sql_Resource);
			while(rs_Res.next())	
			{	
				String resource = null;
				switch(lang){
				case 0: resource = rs_Res.getString("Resource"); break;
				case 1: resource = rs_Res.getString("Resource1"); break;
				case 2: resource = rs_Res.getString("Resource2"); break;
				case 3: resource = rs_Res.getString("Resource5"); break;
				case 4: resource = rs_Res.getString("Resource4"); break;
				case 5: resource = rs_Res.getString("Resource3"); break;
				default: resource = rs_Res.getString("Resource");
				}	
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
				}
				if(resource == null && getEngResInclude()){
					resource = rs_Res.getString("Resource");
				
				}
				if(resource != null){
					OO.insertString(xSpreadsheet, "-", row, col);
					OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(resource), row, col+1);
					OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);			// To wrap text
					OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
					row++;
				}
			}
			

		}catch(Exception ex){
			System.out.println("DevelopmentGuide.java - AllSurvey - "+ex.getMessage());
		}finally{
			ConnectionBean.closeRset(rs_Res); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		OO.setFontSize(xSpreadsheet, col, col, startRow, row, 18);
		OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 1, 2);	// Horizontal, Center
		OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 2, 1);	// Vertical, Top
		OO.setFontSize(xSpreadsheet, col+1, col+1, startRow, row, 12);
		
		row = row + 3;
		return row;
	}
	
	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ START: FROM EXISTING SURVEY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	/* 	@param int SurveyID
	 *	@param int PKUser	To insert into tblEvent
	 *	@param String filename
	 */
	public boolean AllSurvey(int SurveyID, int PKUser, String fileName) throws SQLException, IOException, Exception
	{
		Connection con = null;
		Statement st = null;
		ResultSet rs_comp = null;

		int CompID = 0;
		int OrgID = UserIDtoOrgID(PKUser);
		int row = 57;	// row
		int col = 0;	// column
		int startRow = 0;
		row++;
		String compName = "";
		String compDef = "";
		String Survey_Name="";
		String orgName = "";
		
		boolean IsNull = false;
			
		votblSurvey voSurvey = CE_Survey.getSurveyDetail(SurveyID);
	
			
		Survey_Name = voSurvey.getSurveyName();
		orgName = voSurvey.getOrganizationName();
		
		InitializeExcel(fileName, lang);
		ReplacementSurvey(Survey_Name);
		ReplacementTarget("");
		//InsertParagraphDesc();
		
		String Sql = "SELECT * FROM Competency a INNER JOIN tblSurveyCompetency b ON a.PKCompetency = b.CompetencyID";
		Sql = Sql +" WHERE (b.SurveyID = "+SurveyID+") AND (a.IsSystemGenerated = 1) OR (b.SurveyID = "+SurveyID+") AND (a.IsSystemGenerated = 0) ";
		Sql = Sql +" ORDER BY isSystemGenerated, CompetencyName";
		
		System.out.println("SurveyID = " + SurveyID);
		
		System.out.println(Sql);
		
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs_comp=st.executeQuery(Sql);
			
			//Gwen Oh - 04/10/11: Translate words used in the template
			String strCompetency = T.tslt(lang, "Competency");
			String strDefinition = T.tslt(lang, "Definition");
			String strKB = T.tslt(lang, "Key Behaviours");
			String strRecommendedActivities = T.tslt(lang,"Recommended Development Activities");
			String strRecommendedResources = T.tslt(lang,"Recommended Development Resources");
			String strBooks = T.tslt(lang,"Books");
			String strWeb = T.tslt(lang, "Web Resources");
			String strTrainingResources = T.tslt(lang, "Training Resources");
			String strAVResources = T.tslt(lang,"AV Resources");
			
			while(rs_comp.next())	
			{
				/**
				 * Modified By Yiping
				 * Date 5/1/2012
				 * Reason: when the chosen language is English, it queries column CompetencyName0, which doesn't exist
				 */
				CompID = rs_comp.getInt("PKCompetency");
				switch(lang){
				case 0: compName = rs_comp.getString("CompetencyName"); compDef = rs_comp.getString("CompetencyDefinition");break;
				case 1: compName = rs_comp.getString("CompetencyName1"); compDef = rs_comp.getString("CompetencyDefinition1");break;
				case 2: compName = rs_comp.getString("CompetencyName2"); compDef = rs_comp.getString("CompetencyDefinition2");break;
				case 3: compName = rs_comp.getString("CompetencyName5"); compDef = rs_comp.getString("CompetencyDefinition5");break;
				case 4: compName = rs_comp.getString("CompetencyName4"); compDef = rs_comp.getString("CompetencyDefinition4");break;
				case 5: compName = rs_comp.getString("CompetencyName3"); compDef = rs_comp.getString("CompetencyDefinition3");break;
				default: compName = rs_comp.getString("CompetencyName"); compDef = rs_comp.getString("CompetencyDefinition");}
				
				//No translation available
				if (compName == null)
					compName = rs_comp.getString("CompetencyName");
				if (compDef == null)
					compDef = rs_comp.getString("CompetencyDefinition");
				//End modification
				//TODO
					startRow = row;	// Capture row no of the first row with alphabets
					//Modified By Yiping : support chinese character : 09/01/2012
					if(lang==4||lang==3){
						OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
						OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
					}
				OO.insertString(xSpreadsheet, strCompetency + ": ", row, col);
				
				OO.setFontBold(xSpreadsheet, col, col, row, row);
				OO.setCellAllignment(xSpreadsheet, col, col, row, row, 1, 1);	// Horizontal, Left
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
				}
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(compName), row, col+1);
				OO.setCellAllignment(xSpreadsheet, col+1, col+1, row, row, 1, 1);	// Horizontal, Left
				OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
				//Added by Alvis on 01-Sep-09 to ensure name of competency aligns to the left
				OO.decreaseIndent(xSpreadsheet, col+1, col+1, row, row);//decreaseIndent is used instead of align left because of a weird bug where the first competency name will not align left properly
				OO.decreaseIndent(xSpreadsheet, col+1, col+1, row, row);
				row = row + 2;
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
				}
				OO.insertString(xSpreadsheet, strDefinition + ": ", row, col);
				
				OO.setFontBold(xSpreadsheet, col, col, row, row);
				OO.setCellAllignment(xSpreadsheet, col, col, row, row, 1, 1);	// Horizontal, Left
				OO.setCellAllignment(xSpreadsheet, col, col, row, row, 2, 1);	// Vertical, Top
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
				}
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(compDef), row, col+1);
				OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);
				OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
				row = row + 2;
				
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
				}
				OO.insertString(xSpreadsheet, strKB + ": ", row, col);
				
				OO.setFontBold(xSpreadsheet, col, col, row, row);
				OO.setFontSize(xSpreadsheet, col, col+1, startRow, row, 12);
				row = row + 2;
				startRow = row;
				
				//if(levelOfSurvey == 1)
				//
					String keyBehavName = "";
					
					String Sql_KeyBehav = "SELECT * FROM KeyBehaviour WHERE FKCompetency ="+CompID+" ORDER BY KeyBehaviour";
					/*
					db.openDB();
					Statement stmt2 = db.con.createStatement();
					ResultSet rs_key = stmt2.executeQuery(Sql_KeyBehav);
					*/
					Connection con1 = null;
					Statement st1 = null;
					ResultSet rs_key = null;
					try{
						con1=ConnectionBean.getConnection();
						st1=con1.createStatement();
						rs_key=st1.executeQuery(Sql_KeyBehav);
						while(rs_key.next())	
						{	
							
							switch(lang){
							case 0: keyBehavName = rs_key.getString("KeyBehaviour"); break;
							case 1: keyBehavName = rs_key.getString("KeyBehaviour1"); break;
							case 2: keyBehavName = rs_key.getString("KeyBehaviour2"); break;
							case 3: keyBehavName = rs_key.getString("KeyBehaviour5"); break;
							case 4: keyBehavName = rs_key.getString("KeyBehaviour4"); break;
							case 5: keyBehavName = rs_key.getString("KeyBehaviour3"); break;
							default: keyBehavName = rs_key.getString("KeyBehaviour");}
							if (keyBehavName == null)
								keyBehavName = rs_key.getString("KeyBehaviour");
							OO.insertString(xSpreadsheet, "-", row, col);	
							//Modified By Yiping : support chinese character : 09/01/2012
							if(lang==4||lang==3){
								OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
								OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
							}
							OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(keyBehavName), row, col+1);
							OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);			// To wrap text
							OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
							row++;
						}
						

					}catch(Exception ex){
						System.out.println("DevelopmentGuide.java - AllSurvey - "+ex.getMessage());
					}finally{
						ConnectionBean.closeRset(rs_key); //Close ResultSet
						ConnectionBean.closeStmt(st1); //Close statement
						ConnectionBean.close(con1); //Close connection
					}
					
					
					OO.setFontSize(xSpreadsheet, col, col, startRow, row, 18);
					OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 1, 2);	// Horizontal, Center
					OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 2, 1);	// Vertical, Top
					OO.setFontSize(xSpreadsheet, col+1, col+1, startRow, row, 12);
				//}
				/*
				else if(levelOfSurvey == 0)
				{
					if (ST.LangVer == 1)
						OO.insertString(xSpreadsheet, "None(Key Behaviours are not selected).", row, col+1);
					else if (ST.LangVer == 2)
						OO.insertString(xSpreadsheet, "Tidak ada(Tidak ada Perilaku Kunci yang dipilih).", row, col+1);
					
					OO.setFontSize(xSpreadsheet, col+1, col+1, row, row, 12);
					row = row + 2;
				}
				*/
				OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
				row++;
				
				// ******************************** DEVELOPMENT ACTIVITIES ********************************
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,22);
				}
				OO.insertString(xSpreadsheet, strRecommendedActivities, row, col+1);
				
				OO.setFontSize(xSpreadsheet, col+1, col+1, row, row, 22);
				row = row + 3;
				startRow = row;
				
				String Sql_Activities = "SELECT * FROM tblDRA WHERE CompetencyID = "+CompID;
				System.out.println(Sql_Activities);
				
				/*
				db.openDB();
				Statement stmt3 = db.con.createStatement();
				ResultSet rs_Act = stmt3.executeQuery(Sql_Activities);
				*/
				ResultSet rs_Act = null;
				try{
					con1=ConnectionBean.getConnection();
					st1=con1.createStatement();
					rs_Act=st1.executeQuery(Sql_Activities);
					while(rs_Act.next())	
					{	
						// xukun 03/06/2013 need standardize the numbering
						String draName = null;
						switch(lang){
						case 0: draName = rs_Act.getString("DRAStatement"); break;
						case 1: draName = rs_Act.getString("DRAStatement1"); break;
						case 2: draName = rs_Act.getString("DRAStatement2"); break;
						case 3: draName = rs_Act.getString("DRAStatement5"); break;
						case 4: draName = rs_Act.getString("DRAStatement4"); break;
						case 5: draName = rs_Act.getString("DRAStatement3"); break;
						default: draName = rs_Act.getString("DRAStatement");
						}
						
						if(draName == null  && getEngResInclude())
							draName = rs_Act.getString("DRAStatement");
						if(draName != null){
							if(lang == 4 || lang == 3){
								OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
								OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
							}
							OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(draName), row, col+1);
							
							OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);			// To wrap text
							OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
							row++;
						}
					}

				}catch(Exception ex){
					System.out.println("DevelopmentGuide.java - AllSurvey - "+ex.getMessage());
				}finally{
					ConnectionBean.closeRset(rs_Act); //Close ResultSet
					ConnectionBean.closeStmt(st1); //Close statement
					ConnectionBean.close(con1); //Close connection
				}
				
				
			
				
				OO.setFontSize(xSpreadsheet, col, col, startRow, row, 18);
				OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 1, 2);	// Horizontal, Center
				OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 2, 1);	// Vertical, Top
				OO.setFontSize(xSpreadsheet, col+1, col+1, startRow, row, 12);
					
				OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
				row++;
				
				// ******************************* DEVELOPMENT RESOURCES *********************************
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,22);
				}
				OO.insertString(xSpreadsheet, strRecommendedResources, row, col+1);
				
				OO.setFontSize(xSpreadsheet, col+1, col+1, row, row, 22);
				row = row + 2;
				
				// ----------------------------- BOOKS -----------------------------
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
				}
				OO.insertString(xSpreadsheet, strBooks, row, col);
				
				OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
				row = row + 2;
				startRow = row;
				
				String Sql_Resource1 = "SELECT * FROM tblDRARes WHERE ResType = 1 AND CompetencyID = "+CompID;
				/*
				db.openDB();
				Statement stmt4 = db.con.createStatement();
				ResultSet rs_Res1 = stmt4.executeQuery(Sql_Resource1);
				*/
				row = retrieveRDARes(con1, st1,Sql_Resource1, startRow, row, col);
				
				// ----------------------------- WEB RESOURCES -----------------------------
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
				}
				OO.insertString(xSpreadsheet, strWeb, row, col);
				
				OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
				row = row + 2;
				startRow = row;
				//TODO
				String Sql_Resource2 = "SELECT * FROM tblDRARes WHERE ResType = 2 AND CompetencyID = "+CompID;
				/*
				db.openDB();
				Statement stmt5 = db.con.createStatement();
				ResultSet rs_Res2 = stmt5.executeQuery(Sql_Resource2);
				*/
				row = retrieveRDARes(con1, st1,Sql_Resource2, startRow, row, col);
				
				// ----------------------------- TRAINING RESOURCES -----------------------------
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
				}
				OO.insertString(xSpreadsheet, strTrainingResources, row, col);
				
				OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
				row = row + 2;
				startRow = row;
				
				String Sql_Resource3 = "SELECT * FROM tblDRARes WHERE ResType = 3 AND CompetencyID = "+CompID;
				/*
				db.openDB();
				Statement stmt6 = db.con.createStatement();
				ResultSet rs_Res3 = stmt6.executeQuery(Sql_Resource3);
				*/
				row = retrieveRDARes(con1, st1,Sql_Resource3, startRow, row, col);
				
				// ----------------------------- AV RESOURCES -----------------------------
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
				}
				OO.insertString(xSpreadsheet, strAVResources, row, col);
				
				OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
				row = row + 2;
				startRow = row;
				
				String Sql_Resource4 = "SELECT * FROM tblDRARes WHERE ResType = 4 AND CompetencyID = "+CompID;
				/*
				db.openDB();
				ResultSet rs_Res4 = stmt6.executeQuery(Sql_Resource4);
				*/
				row = retrieveRDARes(con1, st1,Sql_Resource4, startRow, row, col);
				
				OO.setFontSize(xSpreadsheet, col, col, startRow, row, 18);
				OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 1, 2);	// Horizontal, Center
				OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 2, 1);	// Vertical, Top
				OO.setFontSize(xSpreadsheet, col+1, col+1, startRow, row, 12);
				OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
				
				row++;
			}
			
			
		}catch(Exception ex){
			System.out.println("DevelopmentGuide.java - AllSurvey - "+ex.getMessage());
		}finally{
			ConnectionBean.closeRset(rs_comp); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection

		}

		Date timestamp = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
		String temp = dFormat.format(timestamp);
		OO.insertHeaderFooter(xDoc, orgName, UnicodeHelper.getUnicodeStringAmp(Survey_Name), "Date of printing: " + temp + "\n" + "Copyright 锟�3-Sixty Profiler锟�is a product of Pacific Century Consulting Pte Ltd.");
			
		sDetail = CE_Survey.getUserDetail(PKUser);
		EV.addRecord("Insert", itemName, "Development Guide for Survey "+Survey_Name, sDetail[2], sDetail[11], sDetail[10]);
		
		try {			
			OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);
			OO.closeDoc(xDoc);	
		}catch (SQLException SE) {
			System.out.println("a " + SE.getMessage());
		}catch (Exception E) {
			System.out.println("b " + E.getMessage());
		}

		return IsNull;		
	}
	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ END: FROM EXISTING SURVEY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	
	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ START: FOR SELECTED TARGET @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	/*	@param int SurveyID
	 *	@param int TargetID
	 *	@param int PKUser	To insert into tblEvent
	 *	@param String filenam
	 */
	public boolean SelTarget(int SurveyID, int TargetID, int PKUser, String fileName) throws SQLException, IOException, Exception
	{
		int count = 0;
		int CompID=0;
		int row = 57;
		int col = 0;
		int startRow = 0;
		int levelOfSurvey=0;
		float MinGap =0;
		
		/**Change(s): Added method calls to retrieve hasCPOnly and Minimum Pass Score for this survey.
		 * Reason(s): To check whether the particular survey has only Competency Required (CP) as its Rating Task and to generate development guide using Minimum Pass Score retrieved.
		 * Updated By: Kian Hwee
		 * Updated On: 3 March 2010
		 **/
		// Variables used for handling generation of DG for Surveys with no CPR, Desmond 27 Jan 2010
		//boolean hasNoCPR = hasNoCPR (SurveyID); // boolean variable that records whether if current survey has CPR as a rating task
		boolean hasCPOnly = hasCPOnly(SurveyID);
		//System.out.println("hasNoCPR = "+hasNoCPR);
		System.out.println("hasCPOnly = "+hasCPOnly);
		//float minPassScore = 3;
		//commented off as minPassScore will not be used. MinGap will be used instead, Mark	20 May 2010
		//double minPassScore = CE_Survey.getMinPassScore(SurveyID); // Minimum Passing Score, any CP scores below this considered as a Development Area
		//System.out.println("Minimum Pass Score: "+minPassScore);
		String Survey_Name="";
		String TargetName = "";
		String orgName = "";
		
		boolean IsNull = false;
		
		votblSurvey voSurvey = CE_Survey.getSurveyDetail(SurveyID);

		Survey_Name = db.SQLFixer(voSurvey.getSurveyName());
		levelOfSurvey = voSurvey.getLevelOfSurvey();
		MinGap = voSurvey.getMIN_Gap();
		orgName = voSurvey.getOrganizationName();
	
		/*
		 * 
		 * Change for the moe importance survey
		 * hardcode the mingap to be 4.0
		 */
		
		//MinGap = (float) 4.0;
		
		/*
		 * edit by xukun 
		 * 
		 */
		String minGapQuery = "Select * from tblSurvey where SurveyID="+SurveyID;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			con = ConnectionBean.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(minGapQuery);
			if(rs.next()){
				MinGap = rs.getInt("MIN_Gap");
			}
		}catch(Exception ex){
			System.out.println("DevelopmentGuide.java - SelTarget -  "+ex.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		
				
		String [] TDetail = new String [14];
		String query1 ="SELECT * FROM [User] b, tblOrganization c";
		query1 = query1+ " WHERE b.FKOrganization = c.PKOrganization AND PKUser = "+TargetID;
	

		con = null;
		st = null;
		rs = null;
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(query1);
			
			if(rs.next())
			{
				TDetail = CE_Survey.getUserDetail_ADV(TargetID);
				TargetName = TDetail[0]+" "+TDetail[1];
			}			
			

		}catch(Exception ex){
			System.out.println("DevelopmentGuide.java - SelTarget -  "+ex.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		/*
		ResultSet rs_Target = db.getRecord(query1);		
		if(rs_Target.next())
		{
			TDetail = CE_Survey.getUserDetail_ADV(TargetID);
			TargetName = TDetail[0]+" "+TDetail[1];
		}			
		
		rs_Target.close();
		*/
		
		InitializeExcel(fileName, lang);
		ReplacementSurvey(Survey_Name);
		ReplacementTarget(TargetName);
		if(lang == 3 || lang == 4)
		InsertParagraphDesc4();
		else
		InsertParagraphDesc();	
		String Sql ="";
		
		/**Change(s): Replace the previous boolean variable hasNoCPR with 
		 * boolean variable hasCPOnly.
		 * Reason(s): To check whether the particular survey has only 
		 * Competency Required (CP) as its Rating Task before generate
		 * Development Guide
		 * Updated By: Kian Hwee
		 * Updated On: 3 March 2010
		 **/
		// Logic for handling generation of DG for Surveys with no CPR, Desmond 27 Jan 2010
		if (hasCPOnly)
		{
			//Modified query string to get an average mean of the competencies that has key behaviours as well
			//Mark Oei 20 May 2010
			// Query tblAvgMean for CP Scores (indicated by Type = 1) that are below Minimum Passing Score
			//Sql = "SELECT * FROM tblAvgMean am";
			//Sql = Sql+" INNER JOIN Competency c ON am.CompetencyID = c.PKCompetency";
			//Sql = Sql+" WHERE am.SurveyID = " + SurveyID + " AND am.Type = 1 AND am.TargetLoginID = " + TargetID + " AND AvgMean <= " + minPassScore;
			Sql = "SELECT *"; 
			Sql+= " FROM (SELECT tblAvgMean.CompetencyID, ROUND(AVG(tblAvgMean.AvgMean),2) AS Gap,";
			Sql += " Competency.CompetencyName, Competency.CompetencyName1,Competency.CompetencyName2,Competency.CompetencyName3,Competency.CompetencyName4,Competency.CompetencyName5, " +
					"Competency.CompetencyDefinition,Competency.CompetencyDefinition1,Competency.CompetencyDefinition2,Competency.CompetencyDefinition3,Competency.CompetencyDefinition4,Competency.CompetencyDefinition5 FROM tblAvgMean"; 
			Sql += " INNER JOIN Competency ON tblAvgMean.CompetencyID = Competency.PKCompetency";
			Sql += " WHERE (tblAvgMean.SurveyID = "+SurveyID+") AND (tblAvgMean.TargetLoginID = "+TargetID+") AND (tblAvgMean.Type = 1)";
			Sql += " GROUP BY tblAvgMean.CompetencyID, Competency.CompetencyName, Competency.CompetencyName1,Competency.CompetencyName2,Competency.CompetencyName3,Competency.CompetencyName4,Competency.CompetencyName5," +
					"Competency.CompetencyDefinition, Competency.CompetencyDefinition1,Competency.CompetencyDefinition2,Competency.CompetencyDefinition3,Competency.CompetencyDefinition4,Competency.CompetencyDefinition5)"; 
			Sql += " DERIVEDTBL WHERE (Gap <= "+MinGap+")";
			System.out.println("hasCPOnly - SQL:"+Sql);
		}
		else{
			if(levelOfSurvey == 1)
			{
				//round the Gap value to 2 decimal places as in Individual Report
				//Mark Oei 20 May 2010
				Sql = "SELECT* FROM "+
						"(SELECT tblGap.CompetencyID, ROUND(AVG(tblGap.Gap),2) AS Gap, Competency.CompetencyName,  Competency.CompetencyName1,Competency.CompetencyName2,Competency.CompetencyName3,Competency.CompetencyName4,Competency.CompetencyName5, " +
						"Competency.CompetencyDefinition,Competency.CompetencyDefinition1,Competency.CompetencyDefinition2,Competency.CompetencyDefinition3,Competency.CompetencyDefinition4,Competency.CompetencyDefinition5 "+
						"FROM tblGap INNER JOIN Competency ON tblGap.CompetencyID = Competency.PKCompetency "+
						"WHERE (tblGap.SurveyID = "+SurveyID+") AND (tblGap.TargetLoginID = "+TargetID+") "+
						"GROUP BY tblGap.CompetencyID, Competency.CompetencyName,  Competency.CompetencyName1,Competency.CompetencyName2,Competency.CompetencyName3,Competency.CompetencyName4,Competency.CompetencyName5, " +
						"Competency.CompetencyDefinition, Competency.CompetencyDefinition1,Competency.CompetencyDefinition2,Competency.CompetencyDefinition3,Competency.CompetencyDefinition4,Competency.CompetencyDefinition5) DERIVEDTBL "+
					  "WHERE (Gap <= "+MinGap+")";
						System.out.println("Level Survey = 1 - SQL:"+Sql);
				
			}
			else if(levelOfSurvey == 0)
			{
				Sql = "SELECT * FROM tblGap INNER JOIN Competency ON tblGap.CompetencyID = Competency.PKCompetency"; 
				Sql = Sql+" INNER JOIN [User] ON tblGap.TargetLoginID = [User].PKUser";
				Sql = Sql+" WHERE (tblGap.SurveyID = "+SurveyID+") AND (tblGap.TargetLoginID = "+TargetID+") AND Gap <= "+MinGap;
				System.out.println("Level Survey = 0 - SQL:"+Sql);
				
			}
		}
		try{
			con=ConnectionBean.getConnection();
			st=con.createStatement();
			rs=st.executeQuery(Sql);

			//Gwen Oh - 04/10/11: Translate words used in the template
			String strCompetency = T.tslt(lang, "Competency");
			String strDefinition = T.tslt(lang, "Definition");
			String strKB = T.tslt(lang, "Key Behaviours");
			String strRecommendedActivities = T.tslt(lang,"Recommended Development Activities");
			String strRecommendedResources = T.tslt(lang,"Recommended Development Resources");
			String strBooks = T.tslt(lang,"Books");
			String strWeb = T.tslt(lang, "Web Resources");
			String strTrainingResources = T.tslt(lang, "Training Resources");
			String strAVResources = T.tslt(lang,"AV Resources");
			
			while(rs.next())	
			{	
				count ++;
				CompID = rs.getInt("CompetencyID");
				//Modified by Albert (19 July 2012)
				//Fix retrieving from Competency0 column which does not exist
				String compName = "";
				String compDef = "";
				/*if (lang!=0) {
					compName = rs.getString("CompetencyName" + lang);
					compDef = rs.getString("CompetencyDefinition" + lang);
					}else{
							compName = rs.getString("CompetencyName");
							compDef = rs.getString("CompetencyDefinition");
					}*/
				switch(lang){
				case 0: compName = rs.getString("CompetencyName"); rs.getString("CompetencyDefinition");break;
				case 1: compName = rs.getString("CompetencyName1"); rs.getString("CompetencyDefinition1");break;
				case 2: compName = rs.getString("CompetencyName2"); rs.getString("CompetencyDefinition2");break;
				case 3: compName = rs.getString("CompetencyName5"); rs.getString("CompetencyDefinition5");break;
				case 4: compName = rs.getString("CompetencyName4"); rs.getString("CompetencyDefinition4");break;
				case 5: compName = rs.getString("CompetencyName3"); rs.getString("CompetencyDefinition3");break;
				default: compName = rs.getString("CompetencyName"); rs.getString("CompetencyDefinition");}
					//No translation available
				if (compName == null)
					compName = rs.getString("CompetencyName");
				if (compDef == null)
					compDef = rs.getString("CompetencyDefinition");
				//End modification
				
				startRow = row;	// Capture row no of the first row with alphabets
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
				}
				OO.insertString(xSpreadsheet, strCompetency + ": ", row, col);
				
				OO.setFontBold(xSpreadsheet, col, col, row, row);
				OO.setCellAllignment(xSpreadsheet, col, col, row, row, 1, 1);	// Horizontal, Left
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
				}
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(compName).trim(), row, col+1);				
				//Added by Alvis on 08-Sep-09 to ensure name of competency aligns to the left
				OO.wrapText(xSpreadsheet, col+1, col+1, row, row); //added by Alvis to wrap and align text to left
				OO.decreaseIndent(xSpreadsheet, col+1, col+1, row, row);//decreaseIndent is used instead of align left because of a weird bug where the first competency name will not align left properly
				OO.decreaseIndent(xSpreadsheet, col+1, col+1, row, row);
				row = row + 2;
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
				}
				OO.insertString(xSpreadsheet, strDefinition + ": ", row, col);
				
				OO.setFontBold(xSpreadsheet, col, col, row, row);
				OO.setCellAllignment(xSpreadsheet, col, col, row, row, 1, 1);	// Horizontal, Left
				OO.setCellAllignment(xSpreadsheet, col, col, row, row, 2, 1);	// Vertical, Top
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
				}
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(compDef), row, col+1);
				OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);
				OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
				row = row + 2;
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
				}
				OO.insertString(xSpreadsheet, strKB + ": ", row, col);
				
				OO.setFontBold(xSpreadsheet, col, col, row, row);
				OO.setFontSize(xSpreadsheet, col, col+1, startRow, row, 12);
				row = row + 2;
				startRow = row;
				
				//if(levelOfSurvey == 1)
				//{
					String keyBehavName = "";
					
					String Sql_KeyBehav = "SELECT * FROM KeyBehaviour WHERE FKCompetency ="+CompID+" ORDER BY KeyBehaviour";
					/*
					db.openDB();
					Statement stmt2 = db.con.createStatement();
					ResultSet rs_key = stmt2.executeQuery(Sql_KeyBehav);
					*/
					Connection con1 = null;
					Statement st1 = null;
					ResultSet rs1 = null;
					try{
						con1=ConnectionBean.getConnection();
						st1=con1.createStatement();
						rs1=st1.executeQuery(Sql_KeyBehav);
						while(rs1.next())
						{	
							// 05/06/2013 xukun
							switch(lang){
							case 0: keyBehavName = rs1.getString("KeyBehaviour");break;
							case 1: keyBehavName = rs1.getString("KeyBehaviour1");break;
							case 2: keyBehavName = rs1.getString("KeyBehaviour2");break;
							case 3: keyBehavName = rs1.getString("KeyBehaviour5");break;
							case 4: keyBehavName = rs1.getString("KeyBehaviour4");break;
							case 5: keyBehavName = rs1.getString("KeyBehaviour3");break;
							default: keyBehavName = rs1.getString("KeyBehaviour");}
							if (keyBehavName == null)
								keyBehavName = rs1.getString("KeyBehaviour");
							OO.insertString(xSpreadsheet, "-", row, col);	
							//Modified By Yiping : support chinese character : 09/01/2012
							if(lang==4||lang==3){
								OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
								OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
							}
							OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(keyBehavName), row, col+1);
							OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);			// To wrap text
							OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
							row++;
						}
					

					}catch(Exception ex){
						System.out.println("DevelopmentGuide.java - SelTarget -  "+ex.getMessage());
					}finally{
						ConnectionBean.closeRset(rs1); //Close ResultSet
						ConnectionBean.closeStmt(st1); //Close statement
						ConnectionBean.close(con1); //Close connection
					}
					
					
					
					OO.setFontSize(xSpreadsheet, col, col, startRow, row, 18);
					OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 1, 2);	// Horizontal, Center
					OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 2, 1);	// Vertical, Top
					OO.setFontSize(xSpreadsheet, col+1, col+1, startRow, row, 12);
				//}
				/*
				else if(levelOfSurvey == 0)
				{
					if (ST.LangVer == 1)
						OO.insertString(xSpreadsheet, "None(Key Behaviours are not selected).", row, col+1);
					else if (ST.LangVer == 2)
						OO.insertString(xSpreadsheet, "Tidak ada(Tidak ada Perilaku Kunci yang dipilih).", row, col+1);
						
					OO.setFontSize(xSpreadsheet, col+1, col+1, row, row, 12);
					row = row + 2;
				}
				*/
				
				OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
				row++;
				
				// ******************************** DEVELOPMENT ACTIVITIES ********************************
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,22);
				}
				OO.insertString(xSpreadsheet, strRecommendedActivities, row, col+1);
				
				OO.setFontSize(xSpreadsheet, col+1, col+1, row, row, 22);
				row = row + 3;
				startRow = row;
				
				String Sql_Activities = "SELECT * FROM tblDRA WHERE CompetencyID = "+CompID;
				/*
				db.openDB();
				Statement stmt3 = db.con.createStatement();
				ResultSet rs_Act = stmt3.executeQuery(Sql_Activities);
				*/
				try{
					con1=ConnectionBean.getConnection();
					st1=con1.createStatement();
					rs1=st1.executeQuery(Sql_Activities);
					while(rs1.next())	
					{	
						String draName = rs1.getString("DRAStatement");
						
						OO.insertString(xSpreadsheet, "-", row, col);
						//Modified By Yiping : support chinese character : 09/01/2012
						if(lang==4||lang==3){
							OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
							OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
						}
						OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(draName), row, col+1);
						OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);			// To wrap text
						OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
						row++;
					}
				

				}catch(Exception ex){
					System.out.println("DevelopmentGuide.java - SelTarget -  "+ex.getMessage());
				}finally{
					ConnectionBean.closeRset(rs1); //Close ResultSet
					ConnectionBean.closeStmt(st1); //Close statement
					ConnectionBean.close(con1); //Close connection
				}
				
				
				
				OO.setFontSize(xSpreadsheet, col, col, startRow, row, 18);
				OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 1, 2);	// Horizontal, Center
				OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 2, 1);	// Vertical, Top
				OO.setFontSize(xSpreadsheet, col+1, col+1, startRow, row, 12);
					
				OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
				row++;
				
				// ******************************** DEVELOPMENT RESOURCES *********************************
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,22);
				}
				OO.insertString(xSpreadsheet, strRecommendedResources, row, col+1);
				
				OO.setFontSize(xSpreadsheet, col+1, col+1, row, row, 22);
				row = row + 2;

				// ----------------------------- BOOKS -----------------------------
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
				}
				OO.insertString(xSpreadsheet, strBooks, row, col);
				
				OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
				row = row + 2;
				startRow = row;
				
				String Sql_Resource1 = "SELECT * FROM tblDRARes WHERE ResType = 1 AND CompetencyID = "+CompID;
				/*
				db.openDB();
				Statement stmt4 = db.con.createStatement();
				ResultSet rs_Res1 = stmt4.executeQuery(Sql_Resource1);
				*/
				
				row = retrieveRDARes(con1, st1,Sql_Resource1, startRow, row, col);
				
				// ----------------------------- WEB RESOURCES -----------------------------
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
				}
				OO.insertString(xSpreadsheet, strWeb, row, col);
				
				OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
				row = row + 2;
				startRow = row;
				
				String Sql_Resource2 = "SELECT * FROM tblDRARes WHERE ResType = 2 AND CompetencyID = "+CompID;
				/*
				db.openDB();
				Statement stmt5 = db.con.createStatement();
				ResultSet rs_Res2 = stmt5.executeQuery(Sql_Resource2);
				*/
				row = retrieveRDARes(con1, st1,Sql_Resource2, startRow, row, col);
				
				// ----------------------------- TRAINING RESOURCES -----------------------------
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
				}
				OO.insertString(xSpreadsheet, strTrainingResources, row, col);
				
				OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
				row = row + 2;
				startRow = row;
				
				String Sql_Resource3 = "SELECT * FROM tblDRARes WHERE ResType = 3 AND CompetencyID = "+CompID;
				/*
				db.openDB();
				Statement stmt6 = db.con.createStatement();
				ResultSet rs_Res3 = stmt6.executeQuery(Sql_Resource3);
				*/
				row = retrieveRDARes(con1, st1,Sql_Resource3, startRow, row, col);
				
				// ----------------------------- AV RESOURCES -----------------------------
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
				}
				OO.insertString(xSpreadsheet, strAVResources, row, col);
				
				OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
				row = row + 2;
				startRow = row;
				
				String Sql_Resource4 = "SELECT * FROM tblDRARes WHERE ResType = 4 AND CompetencyID = "+CompID;
				/*
				db.openDB();
				ResultSet rs_Res4 = stmt6.executeQuery(Sql_Resource4);
				*/
				row = retrieveRDARes(con1, st1,Sql_Resource4, startRow, row, col);
			}
			
		}catch(Exception ex){
			System.out.println("DevelopmentGuide.java - SelTarget -  "+ex.getMessage());
		}finally{
			ConnectionBean.closeRset(rs); //Close ResultSet
			ConnectionBean.closeStmt(st); //Close statement
			ConnectionBean.close(con); //Close connection
		}
		
		Date timestamp = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
		String temp = dFormat.format(timestamp);
		String targetDetail [] = CE_Survey.getUserDetail_ADV(TargetID);
		OO.insertHeaderFooter(xDoc, orgName, UnicodeHelper.getUnicodeStringAmp(Survey_Name) + "\n" + targetDetail[0] + " " + targetDetail[1], "Date of printing: " + temp + "\n" + "Copyright 锟�3-Sixty Profiler锟�is a product of Pacific Century Consulting Pte Ltd.");
		
		sDetail = CE_Survey.getUserDetail(PKUser);
		EV.addRecord("Insert", itemName, "Development Guide for Survey "+Survey_Name, sDetail[2], sDetail[11], sDetail[10]);
		
		try {
			OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);
			OO.closeDoc(xDoc);	
		}catch (SQLException SE) {
			System.out.println("a " + SE.getMessage());
		}catch (Exception E) {
			System.out.println("b " + E.getMessage());
		}
        if(count != 0){
        	return true;
        }
        else{
		return false;
		}
	}
	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ END: FOR SELECTED TARGET @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	
	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ START: FOR SELECTED COMPETENCIES @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public boolean SelComp(String [] Comp, int PKUser, String fileName) throws SQLException, IOException, Exception
	{
		System.out.println("START: FOR SELECTED COMPETENCIES");
		
		int CompID = 0;
		int row = 57;
		int col = 0;
		int startRow = 0;
		int OrgID = UserIDtoOrgID(PKUser);
		
		System.out.println("OrganizationID = " + OrgID);
		
		String Survey_Name = "";
		String compName = "";
		String compDef = "";
		
		boolean IsNull = false;
		
		if (lang == 4) InitializeExcel(fileName, lang);
		else InitializeExcel(fileName, lang);
		ReplacementSurvey("");
		ReplacementTarget("");
		
		//if (lang == 4 || lang == 3) InsertParagraphDesc4();
		//else InsertParagraphDesc();
		//Gwen Oh - 04/10/11: Translate words used in the template
		String strCompetency = T.tslt(lang, "Competency");
		String strDefinition = T.tslt(lang, "Definition");
		String strKB = T.tslt(lang, "Key Behaviours");
		String strRecommendedActivities = T.tslt(lang,"Recommended Development Activities");
		String strRecommendedResources = T.tslt(lang,"Recommended Development Resources");
		String strBooks = T.tslt(lang,"Books");
		String strWeb = T.tslt(lang, "Web Resources");
		String strTrainingResources = T.tslt(lang, "Training Resources");
		String strAVResources = T.tslt(lang,"AV Resources");
		
		for(int i=0; i<Comp.length;i++)
		{
			
			String Sql = "SELECT * FROM Competency INNER JOIN tblOrganization ON Competency.FKOrganizationID = tblOrganization.PKOrganization WHERE PKCompetency = '"+Comp[i]+"'";
			
			System.out.println(Sql);
			
			Connection con = null;
			Statement st = null;
			ResultSet rs = null;
			//voCompetency vo = new voCompetency();
			boolean bExist = false;
			
	  	   	try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(Sql);
			
				if(rs.next())	
				{
					CompID = rs.getInt("PKCompetency");
					switch(lang){
					case 0: compName = rs.getString("CompetencyName"); compDef = rs.getString("CompetencyDefinition");break;
					case 1: compName = rs.getString("CompetencyName1"); compDef = rs.getString("CompetencyDefinition1");break;
					case 2: compName = rs.getString("CompetencyName2"); compDef = rs.getString("CompetencyDefinition2");break;
					case 3: compName = rs.getString("CompetencyName5"); compDef = rs.getString("CompetencyDefinition5");break;
					case 4: compName = rs.getString("CompetencyName4"); compDef = rs.getString("CompetencyDefinition4");break;
					case 5: compName = rs.getString("CompetencyName3"); compDef = rs.getString("CompetencyDefinition3");break;
					default: compName = rs.getString("CompetencyName"); compDef = rs.getString("CompetencyDefinition");}
					//No translation available
					if (compName == null)
						compName = rs.getString("CompetencyName");
					if (compDef == null)
						compDef = rs.getString("CompetencyDefinition");
					
					bExist = true;
				}
			}catch(Exception ex){
				System.out.println("DevelopmentGuide.java - selComp - "+ex.getMessage());
			}
			finally{
				ConnectionBean.closeRset(rs); //Close ResultSet
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
			
			if(bExist) {
				startRow = row;
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
				}
				OO.insertString(xSpreadsheet, strCompetency + ": ", row, col);
				
				OO.setFontBold(xSpreadsheet, col, col, row, row);
				OO.setCellAllignment(xSpreadsheet, col, col, row, row, 1, 1);	// Horizontal, Left
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
				}
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(compName), row, col+1);
				OO.setCellAllignment(xSpreadsheet, col+1, col+1, row, row, 1, 1);	// Horizontal, Left
				//Added by Alvis on 08-Sep-09 to ensure name of competency aligns to the left
				OO.wrapText(xSpreadsheet, col+1, col+1, row, row); //added by Alvis to wrap and align text to left
				OO.decreaseIndent(xSpreadsheet, col+1, col+1, row, row);//decreaseIndent is used instead of align left because of a weird bug where the first competency name will not align left properly
				OO.decreaseIndent(xSpreadsheet, col+1, col+1, row, row);
				row = row + 2;
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
				}
				OO.insertString(xSpreadsheet, strDefinition + ": ", row, col);
				
				OO.setFontBold(xSpreadsheet, col, col, row, row);
				OO.setCellAllignment(xSpreadsheet, col, col, row, row, 1, 1);	// Horizontal, Left
				OO.setCellAllignment(xSpreadsheet, col, col, row, row, 2, 1);	// Vertical, Top
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
				}
				OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(compDef), row, col+1);
				OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);
				OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
				row = row + 2;
				//Modified By Yiping : support chinese character : 09/01/2012
				if(lang==4||lang==3){
					OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
					OO.setFontHeight(xSpreadsheet, col, col, row, row,12);
				}
				OO.insertString(xSpreadsheet, strKB + ": ", row, col);
				
				OO.setFontBold(xSpreadsheet, col, col, row, row);
				OO.setFontSize(xSpreadsheet, col, col+1, startRow, row, 12);
				row = row + 2;
				startRow = row;
				
				String keyBehavName = "";
				String Sql_KeyBehav = "SELECT * FROM KeyBehaviour WHERE FKCompetency ="+CompID+" ORDER BY KeyBehaviour";
				
				try{
		  		   con=ConnectionBean.getConnection();
		  		   st=con.createStatement();
		  		   rs=st.executeQuery(Sql_KeyBehav);
					
					while(rs.next())	
					{	
						switch(lang){
						case 0: keyBehavName = rs.getString("KeyBehaviour"); break;
						case 1: keyBehavName = rs.getString("KeyBehaviour1"); break;
						case 2: keyBehavName = rs.getString("KeyBehaviour2"); break;
						case 3: keyBehavName = rs.getString("KeyBehaviour5"); break;
						case 4: keyBehavName = rs.getString("KeyBehaviour4"); break;
						case 5: keyBehavName = rs.getString("KeyBehaviour3"); break;
						default: keyBehavName = rs.getString("KeyBehaviour");}
						if (keyBehavName == null)
							keyBehavName = rs.getString("KeyBehaviour");
						
						OO.insertString(xSpreadsheet, "-", row, col);				
						//Modified By Yiping : support chinese character : 09/01/2012
						if(lang==4||lang==3){
							OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
							OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
						}
						//Modified By Yiping : support chinese character : 09/01/2012
						if(lang==4||lang==3){
							OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
							OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
						}
						OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(keyBehavName), row, col+1);
						OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);			// To wrap text
						OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
						row++;
					}
				}catch(Exception ex){
					System.out.println("DevelopmentGuide.java - selComp - "+ex.getMessage());
				}
				finally{
					ConnectionBean.closeRset(rs); //Close ResultSet
					ConnectionBean.closeStmt(st); //Close statement
					ConnectionBean.close(con); //Close connection
				}
				
				OO.setFontSize(xSpreadsheet, col, col, startRow, row, 18);
				OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 1, 2);	// Horizontal, Center
				OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 2, 1);	// Vertical, Top
				OO.setFontSize(xSpreadsheet, col+1, col+1, startRow, row, 12);				
			}
				
	  	   	

			OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
			row++;
				
			// ******************************** DEVELOPMENT ACTIVITIES ********************************
			//Modified By Yiping : support chinese character : 09/01/2012
			if(lang==4||lang==3){
				OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
				OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,22);
			}
			OO.insertString(xSpreadsheet, strRecommendedActivities, row, col+1);
			
			OO.setFontSize(xSpreadsheet, col+1, col+1, row, row, 22);
			row = row + 3;
			startRow = row;
			
			String Sql_Activities = "SELECT * FROM tblDRA WHERE CompetencyID = "+CompID;
		    System.out.println(Sql_Activities);
			
	  	   	try{
	  	   		con=ConnectionBean.getConnection();
	  		   	st=con.createStatement();
	  		   	rs=st.executeQuery(Sql_Activities);
				while(rs.next())	
				{	
					String draName = null;
					switch(lang){
					case 0: draName = rs.getString("DRAStatement"); break;
					case 1: draName = rs.getString("DRAStatement1"); break;
					case 2: draName = rs.getString("DRAStatement2"); break;
					case 3: draName = rs.getString("DRAStatement5"); break;
					case 4: draName = rs.getString("DRAStatement4"); break;
					case 5: draName = rs.getString("DRAStatement3"); break;
					default: draName = rs.getString("DRAStatement");
					}
					
					if(draName == null && getEngResInclude())
						draName = rs.getString("DRAStatement");
		
					OO.insertString(xSpreadsheet, "-", row, col);
					//Modified By Yiping : support chinese character : 09/01/2012
					if(lang==4||lang==3){
						OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
						OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,12);
					}
					OO.insertString(xSpreadsheet, UnicodeHelper.getUnicodeStringAmp(draName), row, col+1);
					OO.mergeCells(xSpreadsheet, col+1, col+1, row, row);			// To wrap text
					OO.wrapText(xSpreadsheet, col+1, col+1, row, row);//added by Alvis to wrap and align text to left
					row++;
				}
	  	   	
	  	   	}catch(Exception ex){
				System.out.println("DevelopmentGuide.java - selComp - "+ex.getMessage());
			}
			finally{
				ConnectionBean.closeRset(rs); //Close ResultSet
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
			
			OO.setFontSize(xSpreadsheet, col, col, startRow, row, 18);
			OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 1, 2);	// Horizontal, Center
			OO.setCellAllignment(xSpreadsheet, col, col, startRow, row, 2, 1);	// Vertical, Top
			OO.setFontSize(xSpreadsheet, col+1, col+1, startRow, row, 12);
				
			OO.insertPageBreak(xSpreadsheet, startColumn, endColumn, row);
			row++;
			
			// ******************************** DEVELOPMENT RESOURCES *********************************
			//Modified By Yiping : support chinese character : 09/01/2012
			if(lang==4||lang==3){
				OO.setFontType(xSpreadsheet, col+1, col+1, row, row,"Microsoft JhengHei");
				OO.setFontHeight(xSpreadsheet, col+1, col+1, row, row,22);
			}
			OO.insertString(xSpreadsheet, strRecommendedResources, row, col+1);
			
			OO.setFontSize(xSpreadsheet, col+1, col+1, row, row, 22);
			row = row + 2;
				
				
				// ----------------------------- BOOKS -----------------------------
			//Modified By Yiping : support chinese character : 09/01/2012
			if(lang==4||lang==3){
				OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
				OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
			}
			OO.insertString(xSpreadsheet, strBooks, row, col);
			
			OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
			row = row + 2;
			startRow = row;
			
			String Sql_Resource1 = "SELECT * FROM tblDRARes WHERE ResType = 1 AND CompetencyID = "+CompID;
			try{
	  	   		con=ConnectionBean.getConnection();
	  		   	st=con.createStatement();
				row = retrieveRDARes(con, st,Sql_Resource1, startRow, row, col);
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			
			// ----------------------------- WEB RESOURCES -----------------------------
			//Modified By Yiping : support chinese character : 09/01/2012
			if(lang==4||lang==3){
				OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
				OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
			}
			OO.insertString(xSpreadsheet, strWeb, row, col);
			
			OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
			row = row + 2;
			startRow = row;
			
			String Sql_Resource2 = "SELECT * FROM tblDRARes WHERE ResType = 2 AND CompetencyID = "+CompID;
		
			try{
	  	   		con=ConnectionBean.getConnection();
	  		   	st=con.createStatement();
				row = retrieveRDARes(con, st,Sql_Resource2, startRow, row, col);
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			
			// ----------------------------- TRAINING RESOURCES -----------------------------
			//Modified By Yiping : support chinese character : 09/01/2012
			if(lang==4||lang==3){
				OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
				OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
			}
			OO.insertString(xSpreadsheet, strTrainingResources, row, col);
			
			OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
			row = row + 2;
			startRow = row;
			
			String Sql_Resource3 = "SELECT * FROM tblDRARes WHERE ResType = 3 AND CompetencyID = "+CompID;
	
			try{
	  	   		con=ConnectionBean.getConnection();
	  		   	st=con.createStatement();
				row = retrieveRDARes(con, st,Sql_Resource3, startRow, row, col);
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			
			// ----------------------------- AV RESOURCES -----------------------------
			//Modified By Yiping : support chinese character : 09/01/2012
			if(lang==4||lang==3){
				OO.setFontType(xSpreadsheet, col, col, row, row,"Microsoft JhengHei");
				OO.setFontHeight(xSpreadsheet, col, col, row, row,18);
			}
			OO.insertString(xSpreadsheet, strAVResources, row, col); // Change Resource category from "In-house Resource" to "AV Resource", Desmond 10 May 2011	
			
			OO.setFontSize(xSpreadsheet, col, col, row, row, 18);
			row = row + 2;
			startRow = row;
			
			String Sql_Resource4 = "SELECT * FROM tblDRARes WHERE ResType = 4 AND CompetencyID = "+CompID;
		
			try{
	  	   		con=ConnectionBean.getConnection();
	  		   	st=con.createStatement();
				row = retrieveRDARes(con, st,Sql_Resource4, startRow, row, col);
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
										
		sDetail = CE_Survey.getUserDetail(PKUser);
		Date timestamp = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
		String temp = dFormat.format(timestamp);
		OO.insertHeaderFooter(xDoc, sDetail[10], "", "Date of printing: " + temp + "\n" + "Copyright 锟�3-Sixty Profiler锟�is a product of Pacific Century Consulting Pte Ltd.");
		
		EV.addRecord("Insert", itemName, "Development Guide for Survey "+Survey_Name, sDetail[2], sDetail[11], sDetail[10]);
		
		try {			
			OO.storeDocComponent(xRemoteServiceManager, xDoc, storeURL);
			OO.closeDoc(xDoc);	
		}catch (SQLException SE) {
			System.out.println("a " + SE.getMessage());
		}catch (Exception E) {
			System.out.println("b " + E.getMessage());
		}

		return IsNull;		
	}
	
	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ END: FOR SELECTED COMPETENCIES @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	/**
	 * Send generated development guide through email
	 * @param sTargetName
	 * @param sSurveyName
	 * @param sFilename
	 * @author Maruli
	 */
	public void sendDevelopmentGuide(String sTargetName, String sSurveyName, String sEmail, String sFilename, int surveyId)
	{
		//Edited By Roger 13 June 2008
		//Get Org ID From SurveyID
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
		
		
		String sHeader = "DEVELOPMENT GUIDE OF " + sTargetName + " FOR " + sSurveyName;
		
		try {
			//Edited By Roger 13 June 2008
			EMAIL.sendMail_with_Attachment(ST.getAdminEmail(), sEmail, sHeader, "", sFilename, orgId);
		}
		catch(Exception E)
		{
			System.out.println("a " + E.getMessage());
		}
	}
	
	public void setSurvey_ID(int Survey_ID) 
	{
		this.Survey_ID = Survey_ID;
	}

	public int getSurvey_ID() 
	{
		return Survey_ID;
	}
	
	public void setType(int Type) 
	{
		this.Type = Type;
	}

	public int getType() 
	{
		return Type;
	}
	
	/*
	 * Change(s) : Added language and template accessor methods
	 * Reason(s) : Cater to different languages
	 * Updated By: Gwen Oh
	 * Updated On: 04 Oct 2011
	 */
	public void setLang(int aLang) {
		lang = aLang;
	}
	
	public void setEngResInclude(boolean isInclude){
		if(lang != 0){// only valid when the language choose is not english
			isEngResIncluded = isInclude;
		}
	}
	
	public int getLang() {
		return lang;
	}
	
	public boolean getEngResInclude(){
		return isEngResIncluded;
	}
	
	public void setSelectedTemplate(String aTemplate) {
		selectedTemplate = aTemplate;
	}
	
	public String getSelectedTemplate() {
		return selectedTemplate;
	}
	
	/**
	 * Check if a particulat survey has only CP as its Rating Task
	 * @author Kian Hwee
	 * @since v1.3.12.62 (03 Mar 2010)
	 * @param int SurveyID
	 * @return return true if survey has only CP as its Rating Task, false if CP doesn't exists or is not the only Rating Task
	 **/
	public boolean hasCPOnly(int SurveyID){
		
		String query="SELECT tblRatingTask.RatingCode FROM tblSurveyRating INNER JOIN tblRatingTask ";
		query = query +"ON tblSurveyRating.RatingTaskID=tblRatingTask.RatingTaskID ";
		query = query +"WHERE tblSurveyRating.SurveyID="+SurveyID;

		Connection con = null;
		Statement st=null;
		ResultSet rs = null;
		boolean hasCPOnly=false;
		try{
	  		   con=ConnectionBean.getConnection();
	  		   st=con.createStatement();
	  		   rs=st.executeQuery(query);
	  		 
	  		   Vector vRatingTask = new Vector();
	  		   while(rs.next())
	  		   {
	  			   vRatingTask.add(rs.getString("RatingCode"));
	  		   }
	  		   System.out.println("returnedRows: "+vRatingTask.size());
	  		      
	  		   if(vRatingTask.size()==1)
	  		   {   
	  			   if(vRatingTask.get(0).toString().equalsIgnoreCase("CP"))
	  			   {
	  				   hasCPOnly=true;
	  			   }
	  		   }
				
	  	   	}catch(Exception ex){
				System.out.println("DevelopmentGuide.java/Method hasCPOnly - RatingTask - "+ex.getMessage());
			}
			finally{
				ConnectionBean.closeRset(rs); //Close ResultSet
				ConnectionBean.closeStmt(st); //Close statement
				ConnectionBean.close(con); //Close connection
			}
		return hasCPOnly;
	}//end hasCPOnly
	
	
	public static void main (String[] args)throws SQLException, Exception
	{
		DevelopmentGuide Rpt = new DevelopmentGuide();
		//boolean flag = false;
		String Comp [] = new String [3];
		Comp[0] = "3";
		Comp[1] = "4";
		Comp[2] = "5";
		
		Rpt.AllSurvey(466, 6403, "DevelopmentGuide1.XLS") ;
		//Rpt.SelTarget(466, 6622, 6404, "Development Guide LOCAL.xls");
		Rpt.SelComp(Comp, 101, "DevelopmentGuideComp.xls");
		//flag = Rpt.SelTarget(405,105,5,"a.xls") ;
		System.out.println("DONE");
	}
}