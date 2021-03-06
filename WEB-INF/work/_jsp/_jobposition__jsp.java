/*
 * JSP generated by Resin Professional 4.0.36 (built Fri, 26 Apr 2013 03:33:09 PDT)
 */

package _jsp;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.lang.String;
import CP_Classes.vo.votblJobPosition;
import CP_Classes.vo.votblOrganization;

public class _jobposition__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.JobPosition JP;
    synchronized (pageContext.getSession()) {
      JP = (CP_Classes.JobPosition) pageContext.getSession().getAttribute("JP");
      if (JP == null) {
        JP = new CP_Classes.JobPosition();
        pageContext.getSession().setAttribute("JP", JP);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
     	// added to check whether organisation is a consulting company
// Mark Oei 09 Mar 2010 
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Organization Org;
    synchronized (pageContext.getSession()) {
      Org = (CP_Classes.Organization) pageContext.getSession().getAttribute("Org");
      if (Org == null) {
        Org = new CP_Classes.Organization();
        pageContext.getSession().setAttribute("Org", Org);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Create_Edit_Survey CE_Survey;
    synchronized (pageContext.getSession()) {
      CE_Survey = (CP_Classes.Create_Edit_Survey) pageContext.getSession().getAttribute("CE_Survey");
      if (CE_Survey == null) {
        CE_Survey = new CP_Classes.Create_Edit_Survey();
        pageContext.getSession().setAttribute("CE_Survey", CE_Survey);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Translate trans;
    synchronized (pageContext.getSession()) {
      trans = (CP_Classes.Translate) pageContext.getSession().getAttribute("trans");
      if (trans == null) {
        trans = new CP_Classes.Translate();
        pageContext.getSession().setAttribute("trans", trans);
      }
    }
    out.write(_jsp_string3, 0, _jsp_string3.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string4, 0, _jsp_string4.length);
    out.print((trans.tslt("Delete Job Position")));
    out.write(_jsp_string5, 0, _jsp_string5.length);
    out.print((trans.tslt("Edit Job Position")));
    out.write(_jsp_string6, 0, _jsp_string6.length);
    out.print((trans.tslt("Please enter Job Position")));
    out.write(_jsp_string7, 0, _jsp_string7.length);
    out.print((trans.tslt("Add Job Position")));
    out.write(_jsp_string8, 0, _jsp_string8.length);
    out.print((trans.tslt("Please enter Job Position")));
    out.write(_jsp_string9, 0, _jsp_string9.length);
    


String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {
    out.write(_jsp_string10, 0, _jsp_string10.length);
      } 
  else 
  { 

if(request.getParameter("proceed") != null)
{ 
	int PKOrg = new Integer(request.getParameter("proceed")).intValue();
 	logchk.setOrg(PKOrg);
}

/*-------------------------------------------------------------------end login modification 1--------------------------------------*/

if(request.getParameter("Delete") != null)
{

	int JobPosition_ID = new Integer(request.getParameter("JobPosition_ID")).intValue();
	
	boolean bIsDeleted = JP.deleteRecord(JobPosition_ID,logchk.getOrg(), logchk.getPKUser());

	if(bIsDeleted) {

    out.write(_jsp_string11, 0, _jsp_string11.length);
    
	} 
	//Changed by HA 16/06/08 pop out message when deleting action cannot be done
	else
	{
    out.write(_jsp_string12, 0, _jsp_string12.length);
     	}

}

if(request.getParameter("Edit") != null)
{
	int JobPosition_ID = new Integer(request.getParameter("JobPosition_ID")).intValue();

	String txtJobPosition = request.getParameter("txtJobPosition");
	
	int iPKJobPosition = JP.getPKJobPosition(txtJobPosition, logchk.getOrg());
	if(iPKJobPosition != 0 && iPKJobPosition != JobPosition_ID) {

    out.write(_jsp_string13, 0, _jsp_string13.length);
    	
	
	} else {
	
		boolean bIsEdited = JP.editRecord(JobPosition_ID, txtJobPosition,logchk.getOrg(), logchk.getPKUser());
	
		if(bIsEdited) {

    out.write(_jsp_string14, 0, _jsp_string14.length);
    
		}  else {

    out.write(_jsp_string15, 0, _jsp_string15.length);
    		
		}
	}
}

if(request.getParameter("Add") != null)
{
	String txtJobPosition = request.getParameter("txtJobPosition");
	
	boolean bExist = JP.existRecord(txtJobPosition, logchk.getOrg());
	
	if(!bExist) {
		boolean bIsAdded = JP.addRecord(txtJobPosition,logchk.getOrg(), logchk.getPKUser());
	
		if(bIsAdded) {

    out.write(_jsp_string16, 0, _jsp_string16.length);
    
		} 
	} else {

    out.write(_jsp_string13, 0, _jsp_string13.length);
    		
	}
}

    out.write(_jsp_string17, 0, _jsp_string17.length);
    
/*------------------------------------------------------------start: LOgin modification 2------------------------------------------*/

    out.write(_jsp_string18, 0, _jsp_string18.length);
    out.print(( trans.tslt("Job Position") ));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print(( trans.tslt("To Add, click on the Add button") ));
    out.write(_jsp_string20, 0, _jsp_string20.length);
    out.print(( trans.tslt("To Edit, click on the relevant radio button and click on the Edit button") ));
    out.write(_jsp_string20, 0, _jsp_string20.length);
    out.print(( trans.tslt("To Delete, click on the relevant radio button and click on the Delete button") ));
    out.write(_jsp_string21, 0, _jsp_string21.length);
    out.print(( trans.tslt("Organisations") ));
    out.write(_jsp_string22, 0, _jsp_string22.length);
    
// Added to check whether organisation is also a consulting company
// if yes, will display a dropdown list of organisation managed by this company
// else, it will display the current organisation only
// Mark Oei 09 Mar 2010
	String [] UserDetail = new String[14];
	UserDetail = CE_Survey.getUserDetail(logchk.getPKUser());
	boolean isConsulting = true;
	isConsulting = Org.isConsulting(UserDetail[10]); // check whether organisation is a consulting company 
	if (isConsulting){
		Vector vOrg = logchk.getOrgList(logchk.getCompany());

		for(int i=0; i<vOrg.size(); i++)
		{
			votblOrganization vo = (votblOrganization)vOrg.elementAt(i);
			int PKOrg = vo.getPKOrganization();
			String OrgName = vo.getOrganizationName();

			if(logchk.getOrg() == PKOrg)
			{ 
    out.write(_jsp_string23, 0, _jsp_string23.length);
    out.print((PKOrg));
    out.write(_jsp_string24, 0, _jsp_string24.length);
    out.print((OrgName));
    out.write(_jsp_string25, 0, _jsp_string25.length);
     } else { 
    out.write(_jsp_string23, 0, _jsp_string23.length);
    out.print((PKOrg));
    out.write('>');
    out.print((OrgName));
    out.write(_jsp_string25, 0, _jsp_string25.length);
    	}	
		} 
	} else { 
    out.write(_jsp_string26, 0, _jsp_string26.length);
    out.print((logchk.getSelfOrg()));
    out.write('>');
    out.print((UserDetail[10]));
    out.write(_jsp_string27, 0, _jsp_string27.length);
     } // End of isConsulting 
    out.write(_jsp_string28, 0, _jsp_string28.length);
    
/*------------------------------------------------------------end: LOgin modification 2------------------------------------------*/

    out.write(_jsp_string29, 0, _jsp_string29.length);
    out.print(( trans.tslt("Job Position") ));
    out.write(_jsp_string30, 0, _jsp_string30.length);
    
   /********************
	* Edited by James 17 Oct 2007
	************************/
	 Vector v = JP.getAllJobPositions(logchk.getOrg());
	 
	 for(int i=0; i<v.size(); i++) {
	  
		votblJobPosition vo = (votblJobPosition)v.elementAt(i);
     
            	
	  	int JobPosition_ID =  vo.getJobPositionID();
		String JobPosition_Desc = vo.getJobPosition();
	  

    out.write(_jsp_string31, 0, _jsp_string31.length);
    out.print((JobPosition_ID));
    out.write(_jsp_string32, 0, _jsp_string32.length);
    out.print((JobPosition_Desc));
    out.write(_jsp_string33, 0, _jsp_string33.length);
    out.print((JobPosition_Desc));
    out.write(_jsp_string34, 0, _jsp_string34.length);
    	}	
    out.write(_jsp_string35, 0, _jsp_string35.length);
    out.print(( trans.tslt("Job Position") ));
    out.write(_jsp_string36, 0, _jsp_string36.length);
    out.print(( trans.tslt("Add") ));
    out.write(_jsp_string37, 0, _jsp_string37.length);
    out.print(( trans.tslt("Edit") ));
    out.write(_jsp_string38, 0, _jsp_string38.length);
    out.print(( trans.tslt("Delete") ));
    out.write(_jsp_string39, 0, _jsp_string39.length);
    	}	
    out.write(_jsp_string40, 0, _jsp_string40.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string41, 0, _jsp_string41.length);
    
	Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);

    out.write(_jsp_string42, 0, _jsp_string42.length);
     // Denise 05/01/2010 update new email address 
    out.write(_jsp_string43, 0, _jsp_string43.length);
    out.print((year));
    out.write(_jsp_string44, 0, _jsp_string44.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("JobPosition.jsp"), -5974251081856227357L, false);
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

  private final static char []_jsp_string22;
  private final static char []_jsp_string37;
  private final static char []_jsp_string29;
  private final static char []_jsp_string13;
  private final static char []_jsp_string8;
  private final static char []_jsp_string28;
  private final static char []_jsp_string25;
  private final static char []_jsp_string36;
  private final static char []_jsp_string7;
  private final static char []_jsp_string16;
  private final static char []_jsp_string21;
  private final static char []_jsp_string27;
  private final static char []_jsp_string32;
  private final static char []_jsp_string17;
  private final static char []_jsp_string23;
  private final static char []_jsp_string24;
  private final static char []_jsp_string40;
  private final static char []_jsp_string3;
  private final static char []_jsp_string26;
  private final static char []_jsp_string11;
  private final static char []_jsp_string0;
  private final static char []_jsp_string43;
  private final static char []_jsp_string15;
  private final static char []_jsp_string9;
  private final static char []_jsp_string33;
  private final static char []_jsp_string42;
  private final static char []_jsp_string14;
  private final static char []_jsp_string6;
  private final static char []_jsp_string4;
  private final static char []_jsp_string30;
  private final static char []_jsp_string44;
  private final static char []_jsp_string5;
  private final static char []_jsp_string38;
  private final static char []_jsp_string2;
  private final static char []_jsp_string1;
  private final static char []_jsp_string10;
  private final static char []_jsp_string35;
  private final static char []_jsp_string39;
  private final static char []_jsp_string19;
  private final static char []_jsp_string34;
  private final static char []_jsp_string12;
  private final static char []_jsp_string31;
  private final static char []_jsp_string20;
  private final static char []_jsp_string18;
  private final static char []_jsp_string41;
  static {
    _jsp_string22 = ":</font></b></td>\r\n		<td width=\"17%\"><select size=\"1\" name=\"selOrg\" onchange=\"proceed(this.form,this.form.selOrg)\">\r\n".toCharArray();
    _jsp_string37 = "\" name=\"btnAdd\" onclick=\"Add(this.form, this.form.txtJobPosition)\"></td>\r\n				<td align=\"center\">\r\n				<input type=\"button\" value=\"".toCharArray();
    _jsp_string29 = "\r\n<table border=\"0\" width=\"99%\" cellspacing=\"0\" cellpadding=\"0\" height=\"79\">\r\n	<tr>\r\n		<td>\r\n<table border=\"0\" width=\"68%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td width=\"289\">\r\n		<div style='width:232px; height:136px; z-index:1; overflow:auto'>\r\n<table border=\"1\" width=\"210\" height=\"12\" bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\">\r\n	<tr>\r\n		<td colspan=\"2\" bgcolor=\"Navy\" height=\"27\">\r\n		<p align=\"center\">\r\n		<b><font face=\"Arial\" color=\"#FFFFFF\" size=\"2\">".toCharArray();
    _jsp_string13 = "		\r\n		<script>\r\n		alert(\"Record exists\");\r\n		</script>\r\n".toCharArray();
    _jsp_string8 = "?\"))\r\n		{\r\n				form.action=\"JobPosition.jsp?Add=1\";\r\n				form.method=\"post\";\r\n				form.submit();\r\n		}\r\n	}\r\n	else\r\n	{\r\n		alert(\"".toCharArray();
    _jsp_string28 = "\r\n</select></td>\r\n		<td width=\"64%\">\r\n<table border=\"0\" width=\"96%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<font size=\"2\">\r\n   \r\n		\r\n	</tr>\r\n</table>\r\n\r\n\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"19%\">&nbsp;</td>\r\n		<td width=\"17%\">&nbsp;</td>\r\n		<td width=\"64%\">&nbsp;</td>\r\n	</tr>\r\n</table>\r\n\r\n".toCharArray();
    _jsp_string25 = "</option>\r\n			".toCharArray();
    _jsp_string36 = ":</b>&nbsp; </font>\r\n		<input type=\"text\" name=\"txtJobPosition\" size=\"21\"><table border=\"0\" width=\"55%\" cellspacing=\"0\" cellpadding=\"0\">\r\n			<tr>\r\n				<td align=\"center\">&nbsp;</td>\r\n				<td align=\"center\">&nbsp;</td>\r\n			</tr>\r\n			<tr>\r\n				<td align=\"center\"><input type=\"button\" value=\"".toCharArray();
    _jsp_string7 = "\");\r\n		}\r\n	}\r\n\r\n}\r\n//void function Add(form, field)\r\nfunction Add(form, field)\r\n{\r\n	if(field.value != \"\")\r\n	{\r\n		if(confirm(\"".toCharArray();
    _jsp_string16 = "\r\n		<script>\r\n		alert(\"Added successfully\");\r\n		</script>\r\n".toCharArray();
    _jsp_string21 = ".\r\n	</font></li>\r\n</ul>\r\n		</td>\r\n		</tr>\r\n	<tr>\r\n		<td colspan=\"3\">&nbsp;</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"19%\"><b><font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string27 = "</option>\r\n	".toCharArray();
    _jsp_string32 = " onclick=\"show(JobPosition_ID,txtJobPosition,data)\"></span><font size=\"2\">\r\n		</font></font>\r\n		<td width=\"57%\" align=\"left\">\r\n		<font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string17 = "\r\n\r\n<form name =\"JobPosition\" method=\"post\" action=\"JobPosition.jsp\">\r\n".toCharArray();
    _jsp_string23 = "\r\n				<option value=".toCharArray();
    _jsp_string24 = " selected>".toCharArray();
    _jsp_string40 = "\r\n<p></p>\r\n\r\n\r\n".toCharArray();
    _jsp_string3 = "\r\n\r\n\r\n\r\n<html>\r\n<head>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n\r\n".toCharArray();
    _jsp_string26 = "\r\n		<option value=".toCharArray();
    _jsp_string11 = "\r\n		<script>\r\n		alert(\"Deleted successfully\");\r\n		</script>\r\n".toCharArray();
    _jsp_string0 = "  \r\n                 \r\n".toCharArray();
    _jsp_string43 = "\r\n		<td align=\"center\" height=\"5\" valign=\"top\"><font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"Login.jsp\">Home</a>&nbsp;| <a color=\"navy\" face=\"Arial\">&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"mailto:3SixtyAdmin@pcc.com.sg?subject=Regarding:\">Contact \r\n		Us</a><a color=\"navy\" face=\"Arial\" href=\"termofuse.jsp\" target=\"_blank\"><span style=\"color: #000080; text-decoration: none\"> | Terms of Use </span></a>| <span style=\"color: #000080; text-decoration: none\"><a style=\"TEXT-DECORATION: none; color:navy;\" href=\"http://www.pcc.com.sg/\" target=\"_blank\">PCC Website</a></span></font></td></tr><tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\">\r\n   \r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		<font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;Copyright &copy; ".toCharArray();
    _jsp_string15 = "\r\n		<script>\r\n		alert(\"Record exists\");\r\n		</script>\r\n".toCharArray();
    _jsp_string9 = "\");\r\n	}\r\n}\r\n/*------------------------------------------------------------start: LOgin modification 1------------------------------------------*/\r\n/*	choosing organization*/\r\n\r\nfunction proceed(form,field)\r\n{\r\n	form.action=\"JobPosition.jsp?proceed=\"+field.value;\r\n	form.method=\"post\";\r\n	form.submit();\r\n}	\r\n\r\nfunction show(field1,field2,field3)\r\n{\r\n	if(field1 != null)\r\n	{\r\n		for (i = 0; i < field1.length; i++) \r\n		{\r\n			if(field1[i].checked)\r\n			{\r\n				field2.value = field3[i].value;\r\n			}\r\n		}\r\n		if(field1.checked)\r\n		{	\r\n			field2.value = field3.value;\r\n		}\r\n	}\r\n}\r\n\r\n</script>\r\n<body style=\"text-align: left\">\r\n".toCharArray();
    _jsp_string33 = "<input type=hidden value=\"".toCharArray();
    _jsp_string42 = "\r\n\r\n<table border=\"0\" width=\"610\" height=\"26\" id=\"table1\">\r\n\r\n	<tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\">\r\n		".toCharArray();
    _jsp_string14 = "\r\n		<script>\r\n		alert(\"Edited successfully\");\r\n		</script>\r\n".toCharArray();
    _jsp_string6 = "?\"))\r\n			{\r\n					form.action=\"JobPosition.jsp?Edit=1\";\r\n					form.method=\"post\";\r\n					form.submit();\r\n			}\r\n		}\r\n		else\r\n		{\r\n			alert(\"".toCharArray();
    _jsp_string4 = "\r\n</head>\r\n<SCRIPT LANGUAGE=JAVASCRIPT>\r\n\r\nfunction check(field)\r\n{\r\n	var isValid = 0;\r\n	var clickedValue = 0;\r\n	//check whether any checkbox selected\r\n	if( field == null ) {\r\n		isValid = 2;\r\n	\r\n	} else {\r\n\r\n		if(isNaN(field.length) == false) {\r\n			for (i = 0; i < field.length; i++)\r\n				if(field[i].checked) {\r\n					clickedValue = field[i].value;\r\n					isValid = 1;\r\n				}\r\n		}else {		\r\n			if(field.checked) {\r\n				clickedValue = field.value;\r\n				isValid = 1;\r\n			}\r\n				\r\n		}\r\n	}\r\n	\r\n	if(isValid == 1)\r\n		return clickedValue;\r\n	else if(isValid == 0)\r\n		alert(\"No record selected\");\r\n	else if(isValid == 2)\r\n		alert(\"No record available\");\r\n	\r\n	isValid = 0;\r\n\r\n}\r\n\r\n//Edited by Xuehai 02 Jun 2011. Remove 'void'. Enable to run on different browers like Chrome&Firefox.\r\n//void function Delete(form, field)\r\nfunction Delete(form, field)\r\n{\r\n	if(check(field))\r\n	{\r\n		if(confirm(\"".toCharArray();
    _jsp_string30 = "</font></b></td>\r\n	</tr>\r\n\r\n".toCharArray();
    _jsp_string44 = " Pacific Century Consulting Pte Ltd. All Rights Reserved.\r\n		</font>\r\n		</td>\r\n		\r\n	</tr>\r\n		\r\n</table>\r\n\r\n</body>\r\n</html>\r\n".toCharArray();
    _jsp_string5 = "?\"))\r\n		{\r\n			form.action=\"JobPosition.jsp?Delete=1\";\r\n			form.method=\"post\";\r\n			form.submit();\r\n		}\r\n	}\r\n}\r\n//void function Edit(form, field1, field2)\r\nfunction Edit(form, field1, field2)\r\n{\r\n	if(check(field1))\r\n	{\r\n		if(field2.value != \"\")\r\n		{\r\n			if(confirm(\"".toCharArray();
    _jsp_string38 = "\" name=\"btnEdit\" onclick=\"Edit(this.form, this.form.JobPosition_ID,this.form.txtJobPosition)\"></td>\r\n			</tr>\r\n		</table>\r\n		</td>\r\n	</tr>\r\n</table>\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"66%\">&nbsp;\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"73%\">\r\n		<table border=\"0\" width=\"31%\" cellspacing=\"0\" cellpadding=\"0\">\r\n			<tr>\r\n				<td width=\"99\">&nbsp;</td>\r\n				<td>\r\n				<input type=\"button\" value=\"".toCharArray();
    _jsp_string2 = "\r\n".toCharArray();
    _jsp_string1 = "                    \r\n".toCharArray();
    _jsp_string10 = " <font size=\"2\">\r\n   \r\n	<script>\r\n	parent.location.href = \"index.jsp\";\r\n	</script>\r\n".toCharArray();
    _jsp_string35 = "		\r\n	\r\n</table>\r\n</div>\r\n		</td>\r\n		<td width=\"374\" align=\"center\">\r\n		<font face=\"Arial\" size=\"2\"><b>".toCharArray();
    _jsp_string39 = "\" name=\"btnDel\" style=\"float: right\" onclick=\"Delete(this.form, this.form.JobPosition_ID)\"></td>\r\n			</tr>\r\n		</table>\r\n		</td>\r\n	</tr>\r\n</table>\r\n</form>\r\n".toCharArray();
    _jsp_string19 = "</font></b></td>\r\n		</tr>\r\n	<tr>\r\n		<td colspan=\"3\">\r\n<font size=\"2\">\r\n   \r\n<ul>\r\n	<li><font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string34 = "\" name=\"data\"></font></td>\r\n</tr>\r\n".toCharArray();
    _jsp_string12 = "\r\n		<script>\r\n		alert(\"Job Position can not be removed. It has been assigned with one or more surveys\");\r\n		</script>\r\n".toCharArray();
    _jsp_string31 = "\r\n  <tr onMouseOver = \"this.bgColor = '#99ccff'\"\r\n    	onMouseOut = \"this.bgColor = '#FFFFcc'\">\r\n\r\n		<td width=\"12%\" align=\"center\"><font face=\"Arial\">\r\n		<span style=\"font-size: 11pt\"><input type=\"radio\" name=\"JobPosition_ID\" value=".toCharArray();
    _jsp_string20 = ".\r\n	</font></li>\r\n	<li><font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string18 = "\r\n<table border=\"0\" width=\"99%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td colspan=\"3\">\r\n		<b>\r\n		<font face=\"Arial\" color=\"#000080\" size=\"2\">".toCharArray();
    _jsp_string41 = "\r\n\r\n".toCharArray();
  }
}
