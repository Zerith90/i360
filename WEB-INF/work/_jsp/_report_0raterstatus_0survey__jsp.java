/*
 * JSP generated by Resin Professional 4.0.36 (built Fri, 26 Apr 2013 03:33:09 PDT)
 */

package _jsp;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.sql.*;
import java.io.*;
import java.lang.String;
import java.util.*;
import CP_Classes.vo.*;

public class _report_0raterstatus_0survey__jsp extends com.caucho.jsp.JavaPage
{
  private static final java.util.HashMap<String,java.lang.reflect.Method> _jsp_functionMap = new java.util.HashMap<String,java.lang.reflect.Method>();
  private boolean _caucho_isDead;
  private boolean _caucho_isNotModified;
  private com.caucho.jsp.PageManager _jsp_pageManager;
  
  public void
  _jspService(javax.servlet.http.HttpServletRequest request,
              javax.servlet.http.HttpServletResponse response)
    throws java.io.IOException, javax.servlet.ServletException
  {
    javax.servlet.http.HttpSession session = request.getSession(true);
    com.caucho.server.webapp.WebApp _jsp_application = _caucho_getApplication();
    com.caucho.jsp.PageContextImpl pageContext = _jsp_pageManager.allocatePageContext(this, _jsp_application, request, response, null, session, 8192, true, false);

    TagState _jsp_state = null;

    try {
      _jspService(request, response, pageContext, _jsp_application, session, _jsp_state);
    } catch (java.lang.Throwable _jsp_e) {
      pageContext.handlePageException(_jsp_e);
    } finally {
      _jsp_pageManager.freePageContext(pageContext);
    }
  }
  
  private void
  _jspService(javax.servlet.http.HttpServletRequest request,
              javax.servlet.http.HttpServletResponse response,
              com.caucho.jsp.PageContextImpl pageContext,
              javax.servlet.ServletContext application,
              javax.servlet.http.HttpSession session,
              TagState _jsp_state)
    throws Throwable
  {
    javax.servlet.jsp.JspWriter out = pageContext.getOut();
    final javax.el.ELContext _jsp_env = pageContext.getELContext();
    javax.servlet.ServletConfig config = getServletConfig();
    javax.servlet.Servlet page = this;
    javax.servlet.jsp.tagext.JspTag _jsp_parent_tag = null;
    com.caucho.jsp.PageContextImpl _jsp_parentContext = pageContext;
    response.setContentType("text/html");
    response.setCharacterEncoding("utf-8");

    out.write(_jsp_string0, 0, _jsp_string0.length);
    CP_Classes.Login logchk;
    synchronized (pageContext.getSession()) {
      logchk = (CP_Classes.Login) pageContext.getSession().getAttribute("logchk");
      if (logchk == null) {
        logchk = new CP_Classes.Login();
        pageContext.getSession().setAttribute("logchk", logchk);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.User user;
    synchronized (pageContext.getSession()) {
      user = (CP_Classes.User) pageContext.getSession().getAttribute("user");
      if (user == null) {
        user = new CP_Classes.User();
        pageContext.getSession().setAttribute("user", user);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Report_ListOfRatersStatus_Survey Rpt5;
    synchronized (pageContext.getSession()) {
      Rpt5 = (CP_Classes.Report_ListOfRatersStatus_Survey) pageContext.getSession().getAttribute("Rpt5");
      if (Rpt5 == null) {
        Rpt5 = new CP_Classes.Report_ListOfRatersStatus_Survey();
        pageContext.getSession().setAttribute("Rpt5", Rpt5);
      }
    }
    out.write(_jsp_string3, 0, _jsp_string3.length);
    CP_Classes.Setting setting;
    synchronized (pageContext.getSession()) {
      setting = (CP_Classes.Setting) pageContext.getSession().getAttribute("setting");
      if (setting == null) {
        setting = new CP_Classes.Setting();
        pageContext.getSession().setAttribute("setting", setting);
      }
    }
    out.write(_jsp_string3, 0, _jsp_string3.length);
    CP_Classes.Create_Edit_Survey CE_Survey;
    synchronized (pageContext.getSession()) {
      CE_Survey = (CP_Classes.Create_Edit_Survey) pageContext.getSession().getAttribute("CE_Survey");
      if (CE_Survey == null) {
        CE_Survey = new CP_Classes.Create_Edit_Survey();
        pageContext.getSession().setAttribute("CE_Survey", CE_Survey);
      }
    }
    out.write(_jsp_string3, 0, _jsp_string3.length);
    CP_Classes.Translate trans;
    synchronized (pageContext.getSession()) {
      trans = (CP_Classes.Translate) pageContext.getSession().getAttribute("trans");
      if (trans == null) {
        trans = new CP_Classes.Translate();
        pageContext.getSession().setAttribute("trans", trans);
      }
    }
    out.write(_jsp_string3, 0, _jsp_string3.length);
     	// added to check whether organisation is a consulting company
// Mark Oei 09 Mar 2010 
    out.write(_jsp_string3, 0, _jsp_string3.length);
    CP_Classes.Organization Org;
    synchronized (pageContext.getSession()) {
      Org = (CP_Classes.Organization) pageContext.getSession().getAttribute("Org");
      if (Org == null) {
        Org = new CP_Classes.Organization();
        pageContext.getSession().setAttribute("Org", Org);
      }
    }
    out.write(_jsp_string4, 0, _jsp_string4.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string5, 0, _jsp_string5.length);
    


String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {
    out.write(_jsp_string6, 0, _jsp_string6.length);
      } 
  else 
  { 

if(request.getParameter("proceed") != null)
{ 	
	int var2 = new Integer(request.getParameter("selOrg")).intValue();
	CE_Survey.set_survOrg(var2);
	logchk.setOrg(var2);
	logchk.setRound(-2);
	logchk.setSelfSurvey(0);
}
if(request.getParameter("round") != null)
{ 
	int survID = new Integer(request.getParameter("selSurvey")).intValue();
	logchk.setSelfSurvey(survID);
	logchk.setRound(-2);
}
if(request.getParameter("btnPreview") != null)
{
        // Changed by DeZ, 21.07.08, Add new feature to display or hide unreliable status
        String showUnreliable = request.getParameter("showUnreliable");
        if( showUnreliable == null || !showUnreliable.equalsIgnoreCase("show") ) showUnreliable = "hide";
    
	int SurveyID = new Integer(request.getParameter("selSurvey")).intValue();
	/*	 Change(s)	: Added type
		 Reason(s)	: To filter report based on round
		 By			: Albert
		 Date		: 30/5/2012
	*/
	if (SurveyID==0){ 
    out.write(_jsp_string7, 0, _jsp_string7.length);
    } else{
	int type = new Integer(request.getParameter("reportRound")).intValue();
	logchk.setRound(type);
	
	if (type==-1) Rpt5.AllRaters(SurveyID, logchk.getPKUser(), showUnreliable); //type == -1 -> without round
	else if(type==-2) Rpt5.AllRatersWithRound(SurveyID, logchk.getPKUser(),type, showUnreliable); //type == -2 -> all rounds
	else Rpt5.AllRatersWithRound(SurveyID, logchk.getPKUser(), type, showUnreliable);
			
	//read the file name.
	String file_name = "List Of Raters Status Surveys.xls";		
	String output = setting.getReport_Path() + "\\"+file_name;
	File f = new File (output);

	
	//set the content type(can be excel/word/powerpoint etc..)
	response.reset();
	response.setContentType ("application/xls");
	//set the header and also the Name by which user will be prompted to save
	response.addHeader ("Content-Disposition", "attachment;filename=\"List Of Raters Status Surveys.xls\"");
		
	//get the file name
	String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
	//OPen an input stream to the file and post the file contents thru the 
	//servlet output stream to the client m/c
	
		InputStream in = new FileInputStream(f);
		ServletOutputStream outs = response.getOutputStream();
		
		
		int bit = 256;
		int i = 0;


    		try {
        			while ((bit) >= 0) {
        				bit = in.read();
        				outs.write(bit);
        			}
        			//System.out.println("" +bit);

            		} catch (IOException ioe) {
            			ioe.printStackTrace(System.out);
            		}
            //		System.out.println( "\n" + i + " bytes sent.");
            //		System.out.println( "\n" + f.length() + " bytes sent.");
            		outs.flush();
            		outs.close();
            		in.close();	
	}//end else if(surveyID==0)
}

    out.write(_jsp_string8, 0, _jsp_string8.length);
    out.print((trans.tslt("List of Raters' Status for Specific Survey")));
    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print((trans.tslt("Organisation")));
    out.write(_jsp_string10, 0, _jsp_string10.length);
    
// Added to check whether organisation is also a consulting company
// if yes, will display a dropdown list of organisation managed by this company
// else, it will display the current organisation only
// Mark Oei 09 Mar 2010
	String [] UserDetail = new String[14];
	UserDetail = CE_Survey.getUserDetail(logchk.getPKUser());
	boolean isConsulting = true;
	isConsulting = Org.isConsulting(UserDetail[10]); // check whether organisation is a consulting company 
	if (isConsulting){ 
    out.write(_jsp_string11, 0, _jsp_string11.length);
    out.print((trans.tslt("All")));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    
		Vector vOrg = logchk.getOrgList(logchk.getCompany());

		for(int i=0; i<vOrg.size(); i++)
		{
			votblOrganization vo = (votblOrganization)vOrg.elementAt(i);
			int PKOrg = vo.getPKOrganization();
			String OrgName = vo.getOrganizationName();

			if(logchk.getOrg() == PKOrg)
			{ 
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((PKOrg));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((OrgName));
    out.write(_jsp_string15, 0, _jsp_string15.length);
     } else { 
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((PKOrg));
    out.write('>');
    out.print((OrgName));
    out.write(_jsp_string15, 0, _jsp_string15.length);
    	}	
		} 
	} else { 
    out.write(_jsp_string16, 0, _jsp_string16.length);
    out.print((logchk.getSelfOrg()));
    out.write('>');
    out.print((UserDetail[10]));
    out.write(_jsp_string12, 0, _jsp_string12.length);
     } // End of isConsulting 
    out.write(_jsp_string17, 0, _jsp_string17.length);
    out.print((trans.tslt("Survey")));
    out.write(_jsp_string18, 0, _jsp_string18.length);
    out.print((trans.tslt("Please select one")));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    
	boolean anyRecord=false;
	// Changed by Ha 23/05/08: calling method getSurveys passing different parameters
	Vector vS=CE_Survey.getSurveys(logchk.getCompany(),logchk.getOrg());
	for(int i=0;i<vS.size();i++){
	votblSurvey vo=(votblSurvey)vS.elementAt(i);
		anyRecord=true;
		int Surv_ID = vo.getSurveyID();
		String Surv_Name = vo.getSurveyName();
		if(logchk.getSelfSurvey() == Surv_ID)
			{ 
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((Surv_ID));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((Surv_Name));
    out.write(_jsp_string15, 0, _jsp_string15.length);
     } else { 
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((Surv_ID));
    out.write('>');
    out.print((Surv_Name));
    out.write(_jsp_string15, 0, _jsp_string15.length);
    	}
	}
    out.write(_jsp_string20, 0, _jsp_string20.length);
    out.print((trans.tslt("Round")));
    out.write(_jsp_string21, 0, _jsp_string21.length);
    out.print((trans.tslt("All")));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    
		Vector<Integer> roundList = user.getRoundList(logchk.getSelfSurvey());

		for(int i=0; i<roundList.size(); i++)
		{
			int roundNo = roundList.elementAt(i);
			if(logchk.getRound() == roundNo)
			{ 
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((roundNo));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((roundNo));
    out.write(_jsp_string15, 0, _jsp_string15.length);
     } else { 
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((roundNo));
    out.write('>');
    out.print((roundNo));
    out.write(_jsp_string15, 0, _jsp_string15.length);
    	}	
		}//end for loop
		
    out.write(_jsp_string22, 0, _jsp_string22.length);
    out.print((trans.tslt("Without Round")));
    out.write(_jsp_string23, 0, _jsp_string23.length);
     // Added by DeZ, 21.07.08, Add new feature to display or hide unreliable status 
    out.write(_jsp_string24, 0, _jsp_string24.length);
    out.print((trans.tslt("Show Unreliable")));
    out.write(_jsp_string25, 0, _jsp_string25.length);
     // End of added by DeZ, 21.07.08, Add new feature to display or hide unreliable status 
    out.write(_jsp_string26, 0, _jsp_string26.length);
    	if(anyRecord)
   	 	{
    out.write(_jsp_string3, 0, _jsp_string3.length);
     if(!logchk.getCompanyName().equalsIgnoreCase("demo") || logchk.getUserType() == 1) {

    out.write(_jsp_string27, 0, _jsp_string27.length);
    out.print((trans.tslt("Preview")));
    out.write(_jsp_string28, 0, _jsp_string28.length);
     } else {

    out.write(_jsp_string27, 0, _jsp_string27.length);
    out.print((trans.tslt("Preview")));
    out.write(_jsp_string29, 0, _jsp_string29.length);
    
} 
    out.write(_jsp_string30, 0, _jsp_string30.length);
    }
    out.write(_jsp_string31, 0, _jsp_string31.length);
    	}	
    out.write(_jsp_string32, 0, _jsp_string32.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string33, 0, _jsp_string33.length);
    
	Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);

    out.write(_jsp_string34, 0, _jsp_string34.length);
     // Denise 05/01/2010 update new email address 
    out.write(_jsp_string35, 0, _jsp_string35.length);
    out.print((year));
    out.write(_jsp_string36, 0, _jsp_string36.length);
  }

  private com.caucho.make.DependencyContainer _caucho_depends
    = new com.caucho.make.DependencyContainer();

  public java.util.ArrayList<com.caucho.vfs.Dependency> _caucho_getDependList()
  {
    return _caucho_depends.getDependencies();
  }

  public void _caucho_addDepend(com.caucho.vfs.PersistentDependency depend)
  {
    super._caucho_addDepend(depend);
    _caucho_depends.add(depend);
  }

  protected void _caucho_setNeverModified(boolean isNotModified)
  {
    _caucho_isNotModified = true;
  }

  public boolean _caucho_isModified()
  {
    if (_caucho_isDead)
      return true;

    if (_caucho_isNotModified)
      return false;

    if (com.caucho.server.util.CauchoSystem.getVersionId() != -7791540776389363938L)
      return true;

    return _caucho_depends.isModified();
  }

  public long _caucho_lastModified()
  {
    return 0;
  }

  public void destroy()
  {
      _caucho_isDead = true;
      super.destroy();
    TagState tagState;
  }

  public void init(com.caucho.vfs.Path appDir)
    throws javax.servlet.ServletException
  {
    com.caucho.vfs.Path resinHome = com.caucho.server.util.CauchoSystem.getResinHome();
    com.caucho.vfs.MergePath mergePath = new com.caucho.vfs.MergePath();
    mergePath.addMergePath(appDir);
    mergePath.addMergePath(resinHome);
    com.caucho.loader.DynamicClassLoader loader;
    loader = (com.caucho.loader.DynamicClassLoader) getClass().getClassLoader();
    String resourcePath = loader.getResourcePathSpecificFirst();
    mergePath.addClassPath(resourcePath);
    com.caucho.vfs.Depend depend;
    depend = new com.caucho.vfs.Depend(appDir.lookup("Report_RaterStatus_Survey.jsp"), -1735192317381582026L, false);
    _caucho_depends.add(depend);
    depend = new com.caucho.vfs.Depend(appDir.lookup("Footer.jsp"), -6500873733011061252L, false);
    _caucho_depends.add(depend);
  }

  final static class TagState {

    void release()
    {
    }
  }

  public java.util.HashMap<String,java.lang.reflect.Method> _caucho_getFunctionMap()
  {
    return _jsp_functionMap;
  }

  public void caucho_init(ServletConfig config)
  {
    try {
      com.caucho.server.webapp.WebApp webApp
        = (com.caucho.server.webapp.WebApp) config.getServletContext();
      init(config);
      if (com.caucho.jsp.JspManager.getCheckInterval() >= 0)
        _caucho_depends.setCheckInterval(com.caucho.jsp.JspManager.getCheckInterval());
      _jsp_pageManager = webApp.getJspApplicationContext().getPageManager();
      com.caucho.jsp.TaglibManager manager = webApp.getJspApplicationContext().getTaglibManager();
      com.caucho.jsp.PageContextImpl pageContext = new com.caucho.jsp.InitPageContextImpl(webApp, this);
    } catch (Exception e) {
      throw com.caucho.config.ConfigException.create(e);
    }
  }

  private final static char []_jsp_string21;
  private final static char []_jsp_string22;
  private final static char []_jsp_string15;
  private final static char []_jsp_string11;
  private final static char []_jsp_string16;
  private final static char []_jsp_string30;
  private final static char []_jsp_string5;
  private final static char []_jsp_string12;
  private final static char []_jsp_string10;
  private final static char []_jsp_string36;
  private final static char []_jsp_string13;
  private final static char []_jsp_string24;
  private final static char []_jsp_string14;
  private final static char []_jsp_string25;
  private final static char []_jsp_string35;
  private final static char []_jsp_string26;
  private final static char []_jsp_string27;
  private final static char []_jsp_string20;
  private final static char []_jsp_string34;
  private final static char []_jsp_string29;
  private final static char []_jsp_string28;
  private final static char []_jsp_string7;
  private final static char []_jsp_string23;
  private final static char []_jsp_string8;
  private final static char []_jsp_string3;
  private final static char []_jsp_string1;
  private final static char []_jsp_string19;
  private final static char []_jsp_string9;
  private final static char []_jsp_string6;
  private final static char []_jsp_string2;
  private final static char []_jsp_string0;
  private final static char []_jsp_string31;
  private final static char []_jsp_string32;
  private final static char []_jsp_string18;
  private final static char []_jsp_string4;
  private final static char []_jsp_string17;
  private final static char []_jsp_string33;
  static {
    _jsp_string21 = ":</font></b></td>\r\n		<td width=\"228\" style=\"border-style: none; border-width: medium\">\r\n		<p align=\"left\">		\r\n		<select size=\"1\" name=\"reportRound\">\r\n		<option value=\"-2\" selected>".toCharArray();
    _jsp_string22 = "\r\n		<option value=\"-1\">".toCharArray();
    _jsp_string15 = "</option>\r\n			".toCharArray();
    _jsp_string11 = "\r\n		<select size=\"1\" name=\"selOrg\" onchange=\"proceed(this.form,this.form.selOrg)\">\r\n		<option value=\"0\" selected>".toCharArray();
    _jsp_string16 = "\r\n		<select size=\"1\" name=\"selOrg\" onchange=\"proceed(this.form,this.form.selOrg)\">\r\n		<option value=".toCharArray();
    _jsp_string30 = "		\r\n		".toCharArray();
    _jsp_string5 = "\r\n</head>\r\n<SCRIPT LANGUAGE=\"JavaScript\">\r\nfunction proceed(form,field){\r\n	form.action=\"Report_RaterStatus_Survey.jsp?round=0&proceed=\"+field.value;\r\n	form.method=\"post\";\r\n	form.submit();\r\n}\r\nfunction populateRound(form,field){\r\n	form.action=\"Report_RaterStatus_Survey.jsp?round=\"+field.value;\r\n	form.method=\"post\";\r\n	form.submit();\r\n}\r\n</script>\r\n\r\n<body>\r\n".toCharArray();
    _jsp_string12 = "</option>\r\n	".toCharArray();
    _jsp_string10 = ":</font></b></td>\r\n		<td width=\"283\" height=\"25\" colspan=\"2\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\"> <font size=\"2\">\r\n\r\n".toCharArray();
    _jsp_string36 = " Pacific Century Consulting Pte Ltd. All Rights Reserved.\r\n		</font>\r\n		</td>\r\n		\r\n	</tr>\r\n		\r\n</table>\r\n\r\n		</tr>\r\n</table>\r\n</body>\r\n</html>".toCharArray();
    _jsp_string13 = "\r\n				<option value=".toCharArray();
    _jsp_string24 = "\r\n	<tr>\r\n		<td width=\"117\" align=\"center\" height=\"25\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n		<td width=\"283\" height=\"25\" colspan=\"2\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"117\" align=\"center\" height=\"25\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">\r\n                <p align=\"left\"><b><font face=\"Arial\" size=\"2\">&nbsp;<label for=\"showUnreliable\">".toCharArray();
    _jsp_string14 = " selected>".toCharArray();
    _jsp_string25 = ":</label></font></b></td>\r\n		<td width=\"283\" height=\"25\" colspan=\"2\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\"> <font size=\"2\">\r\n        <input id=\"showUnreliable\" name=\"showUnreliable\" type=\"checkbox\" value=\"show\">\r\n    	</td>\r\n	</tr>\r\n    ".toCharArray();
    _jsp_string35 = "\r\n		<td align=\"center\" height=\"5\" valign=\"top\"><font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"Login.jsp\">Home</a>&nbsp;| <a color=\"navy\" face=\"Arial\">&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"mailto:3SixtyAdmin@pcc.com.sg?subject=Regarding:\">Contact \r\n		Us</a><a color=\"navy\" face=\"Arial\" href=\"termofuse.jsp\" target=\"_blank\"><span style=\"color: #000080; text-decoration: none\"> | Terms of Use </span></a>| <span style=\"color: #000080; text-decoration: none\"><a style=\"TEXT-DECORATION: none; color:navy;\" href=\"http://www.pcc.com.sg/\" target=\"_blank\">PCC Website</a></span></font></td></tr><tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\">\r\n   \r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		<font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;Copyright &copy; ".toCharArray();
    _jsp_string26 = "\r\n	<tr>\r\n		<td width=\"399\" align=\"center\" colspan=\"3\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"116\" align=\"center\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n		<td width=\"213\" align=\"center\" style=\"border-style: none; border-width: medium\">&nbsp; </td>\r\n		<td width=\"100\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">\r\n		<p align=\"left\">\r\n   	".toCharArray();
    _jsp_string27 = "   		\r\n		<input type=\"Submit\" value=\"".toCharArray();
    _jsp_string20 = "\r\n</select></td>\r\n	</tr>\r\n	<!-- Added a new filter Round (Added by Albert 31/5/12) -->\r\n	<tr>\r\n		<td width=\"117\" align=\"center\" height=\"25\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n		<td width=\"851\" height=\"25\" colspan=\"2\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"117\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">\r\n		<b><font face=\"Arial\" size=\"2\">&nbsp;".toCharArray();
    _jsp_string34 = "\r\n\r\n<table border=\"0\" width=\"610\" height=\"26\" id=\"table1\">\r\n\r\n	<tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\">\r\n		".toCharArray();
    _jsp_string29 = "\" name=\"btnPreview\" style=\"float: left\" disabled>\r\n".toCharArray();
    _jsp_string28 = "\" name=\"btnPreview\" style=\"float: left\">\r\n".toCharArray();
    _jsp_string7 = "\r\n	<script>alert(\"Please select a survey\")</script>\r\n	".toCharArray();
    _jsp_string23 = "</option>\r\n		</select>\r\n</td>\r\n		<td width=\"154\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\"> </td>\r\n	</tr>\r\n	<!-- end of addition of filter by round -->\r\n    ".toCharArray();
    _jsp_string8 = "\r\n<form name=\"Rpt_RaterStatus_Survey\" action=\"Report_RaterStatus_Survey.jsp\" method=\"post\">\r\n<table border=\"0\" width=\"49%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td> <font size=\"2\">\r\n   \r\n    	<b>\r\n		<font face=\"Arial\" size=\"2\" color=\"#000080\">".toCharArray();
    _jsp_string3 = "\r\n".toCharArray();
    _jsp_string1 = "      \r\n".toCharArray();
    _jsp_string19 = "</option>\r\n".toCharArray();
    _jsp_string9 = "</font></b></td>\r\n	</tr>\r\n	<tr>\r\n		<td>&nbsp;</td>\r\n	</tr>\r\n</table>\r\n<table border=\"2\" width=\"483\" cellspacing=\"0\" cellpadding=\"0\" bordercolor=\"#3399FF\" bgcolor=\"#FFFFCC\">\r\n		<tr>\r\n		<td width=\"117\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: solid; border-top-width: 1px; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n		<td width=\"242\" style=\"border-left-style: none; border-left-width: medium; border-right-style: none; border-right-width: medium; border-top-style: solid; border-top-width: 1px; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n		<td width=\"100\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: solid; border-top-width: 1px; border-bottom-style: none; border-bottom-width: medium\">&nbsp; </td>\r\n	</tr>\r\n		<tr>\r\n		<td width=\"117\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">\r\n		<b><font face=\"Arial\" size=\"2\">&nbsp;".toCharArray();
    _jsp_string6 = " <font size=\"2\">\r\n   \r\n	<script>\r\n	parent.location.href = \"index.jsp\";\r\n	</script>\r\n".toCharArray();
    _jsp_string2 = "             \r\n".toCharArray();
    _jsp_string0 = "   \r\n".toCharArray();
    _jsp_string31 = "\r\n		\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"116\" align=\"center\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: solid; border-bottom-width: 1px\">&nbsp; </td>\r\n		<td width=\"213\" align=\"center\" style=\"border-left-style: none; border-left-width: medium; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: solid; border-bottom-width: 1px\">&nbsp; </td>\r\n		<td width=\"100\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: solid; border-bottom-width: 1px\">&nbsp;\r\n		</td>\r\n	</tr>\r\n</table>\r\n</form>\r\n".toCharArray();
    _jsp_string32 = "\r\n<table border=\"0\" width=\"610\" height=\"26\">\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n		</tr>\r\n	\r\n	<tr>\r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n	\r\n\r\n\r\n".toCharArray();
    _jsp_string18 = ":</font></b></td>\r\n		<td width=\"283\" height=\"25\" colspan=\"2\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\"> <font size=\"2\">\r\n   \r\n    	<select size=\"1\" name=\"selSurvey\" onChange=\"populateRound(this.form,this.form.selSurvey)\">\r\n    	<option value=\"0\" selected>".toCharArray();
    _jsp_string4 = "\r\n\r\n<html>\r\n<head>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n\r\n".toCharArray();
    _jsp_string17 = "\r\n</select><font size=\"2\">&nbsp; </font>\r\n</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"117\" align=\"center\" height=\"25\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n		<td width=\"283\" height=\"25\" colspan=\"2\" style=\"border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">&nbsp;\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"117\" align=\"center\" height=\"25\" style=\"border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium\">\r\n		<p align=\"left\"><b><font face=\"Arial\" size=\"2\">&nbsp;".toCharArray();
    _jsp_string33 = "\r\n\r\n".toCharArray();
  }
}
