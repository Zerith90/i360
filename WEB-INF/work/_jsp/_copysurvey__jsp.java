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
import java.text.*;
import java.lang.String;
import CP_Classes.vo.votblSurvey;
import CP_Classes.vo.votblOrganization;

public class _copysurvey__jsp extends com.caucho.jsp.JavaPage
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
     // by Lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.Create_Edit_Survey CE_Survey;
    synchronized (pageContext.getSession()) {
      CE_Survey = (CP_Classes.Create_Edit_Survey) pageContext.getSession().getAttribute("CE_Survey");
      if (CE_Survey == null) {
        CE_Survey = new CP_Classes.Create_Edit_Survey();
        pageContext.getSession().setAttribute("CE_Survey", CE_Survey);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Login logchk;
    synchronized (pageContext.getSession()) {
      logchk = (CP_Classes.Login) pageContext.getSession().getAttribute("logchk");
      if (logchk == null) {
        logchk = new CP_Classes.Login();
        pageContext.getSession().setAttribute("logchk", logchk);
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
    out.print((trans.tslt("Please select Organisation")));
    out.write(_jsp_string4, 0, _jsp_string4.length);
    out.print((trans.tslt("Please enter new Survey name")));
    out.write(_jsp_string5, 0, _jsp_string5.length);
    


String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {
    out.write(_jsp_string6, 0, _jsp_string6.length);
      } 
  else 
  { 
  
	if(request.getParameter("add") != null)
	{
		String NewSurveyName = request.getParameter("NewSurveyName");
		int OrgID = new Integer(request.getParameter("selOrg")).intValue();
		int status = new Integer(request.getParameter("selStatus")).intValue();
		//Edit by Roger 21 July 2008
		//Survey that belongs to different organization can have the same name. include org id check 
		//int iSurveyID = CE_Survey.getSurveyID(NewSurveyName);
		
		boolean valid = CE_Survey.checkCopySurvey(NewSurveyName, OrgID);
		
		if(valid)
		{
			//try
			//{
				CE_Survey.CopySurvey(CE_Survey.getSurvey_ID(),NewSurveyName, OrgID, status, logchk.getPKUser());
				
			//*** Edited by Tracy 14 Aug 08, get new survey ID based on new survey name and Organization ID
				int newlyCopiedSurveyId = CE_Survey.getSurveyOrgID(NewSurveyName, OrgID);
			//********************************************
				CE_Survey.setSurvey_ID(newlyCopiedSurveyId); 
			
    out.write(_jsp_string7, 0, _jsp_string7.length);
    		/*}
			catch(Exception E)
			{	
    out.write(_jsp_string8, 0, _jsp_string8.length);
    	}	*/
		}
		else
		{
			
    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print((trans.tslt("Survey name exists")));
    out.write(_jsp_string10, 0, _jsp_string10.length);
    	}	

}

    out.write(_jsp_string11, 0, _jsp_string11.length);
    out.print(( trans.tslt("Copy Survey") ));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print(( trans.tslt("Selected Survey") ));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    	
	String SurveyName=" ";
	int db_SurveyStatus=0;
	int FKOrg = 0;
	/************************
	*Edited By James 02 Nov 02
	************************/
	
	votblSurvey voSurvey = CE_Survey.getSurveyDetail(CE_Survey.getSurvey_ID());
	
	if(voSurvey != null) {
	
	SurveyName = voSurvey.getSurveyName();
	db_SurveyStatus = voSurvey.getSurveyStatus();
	FKOrg = voSurvey.getFKOrganization();
	}
	

    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((SurveyName));
    out.write(_jsp_string15, 0, _jsp_string15.length);
    out.print(( trans.tslt("New Survey Name") ));
    out.write(_jsp_string16, 0, _jsp_string16.length);
    
	//Remove by Roger 3 July 2008
	//Organization is always default to previously specified, no need to choose again 

    out.write(_jsp_string17, 0, _jsp_string17.length);
    out.print(( trans.tslt("Organisation") ));
    out.write(_jsp_string18, 0, _jsp_string18.length);
    
	Vector vOrg = logchk.getOrgList(logchk.getCompany());
	
	for(int i=0; i<vOrg.size(); i++)
	{
		votblOrganization vo = (votblOrganization)vOrg.elementAt(i);
		int PKOrg = vo.getPKOrganization();
		String OrgName = vo.getOrganizationName();
	
		if(logchk.getOrg() == PKOrg)
		{

    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print((PKOrg));
    out.write(_jsp_string20, 0, _jsp_string20.length);
    out.print((OrgName));
    out.write(_jsp_string21, 0, _jsp_string21.length);
    		}
		else	
		{

    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print((PKOrg));
    out.write('>');
    out.print((OrgName));
    out.write(_jsp_string22, 0, _jsp_string22.length);
    		}	
	}

    out.write(_jsp_string23, 0, _jsp_string23.length);
    out.print(( trans.tslt("Status") ));
    out.write(_jsp_string24, 0, _jsp_string24.length);
    out.print(( trans.tslt("Open") ));
    out.write(_jsp_string25, 0, _jsp_string25.length);
    out.print(( trans.tslt("Closed") ));
    out.write(_jsp_string26, 0, _jsp_string26.length);
    out.print(( trans.tslt("Not Commissioned") ));
    out.write(_jsp_string27, 0, _jsp_string27.length);
    	
	//Added by Roger 3 July 2008 (Revision 1.2)
	//Use Hidden field to default the value

    out.write(_jsp_string28, 0, _jsp_string28.length);
    out.print((logchk.getOrg()));
    out.write(_jsp_string29, 0, _jsp_string29.length);
    out.print(( trans.tslt("Add") ));
    out.write(_jsp_string30, 0, _jsp_string30.length);
    out.print(( trans.tslt("Cancel") ));
    out.write(_jsp_string31, 0, _jsp_string31.length);
    	}	
    out.write(_jsp_string32, 0, _jsp_string32.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("CopySurvey.jsp"), -1229418712110739504L, false);
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

  private final static char []_jsp_string14;
  private final static char []_jsp_string28;
  private final static char []_jsp_string23;
  private final static char []_jsp_string11;
  private final static char []_jsp_string24;
  private final static char []_jsp_string13;
  private final static char []_jsp_string30;
  private final static char []_jsp_string4;
  private final static char []_jsp_string21;
  private final static char []_jsp_string12;
  private final static char []_jsp_string32;
  private final static char []_jsp_string6;
  private final static char []_jsp_string29;
  private final static char []_jsp_string1;
  private final static char []_jsp_string31;
  private final static char []_jsp_string20;
  private final static char []_jsp_string26;
  private final static char []_jsp_string15;
  private final static char []_jsp_string17;
  private final static char []_jsp_string8;
  private final static char []_jsp_string0;
  private final static char []_jsp_string5;
  private final static char []_jsp_string16;
  private final static char []_jsp_string25;
  private final static char []_jsp_string19;
  private final static char []_jsp_string9;
  private final static char []_jsp_string7;
  private final static char []_jsp_string2;
  private final static char []_jsp_string22;
  private final static char []_jsp_string27;
  private final static char []_jsp_string3;
  private final static char []_jsp_string10;
  private final static char []_jsp_string18;
  static {
    _jsp_string14 = "\r\n	".toCharArray();
    _jsp_string28 = "\r\n<input type=\"hidden\" name=\"selStatus\" value=\"3\"/>\r\n<input type=\"hidden\" name=\"selOrg\" value=\"".toCharArray();
    _jsp_string23 = "\r\n</select></span></font></td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"100\" height=\"21\" style=\"border-style: none; border-width: medium\">&nbsp;</td>\r\n		<td width=\"665\" height=\"21\" style=\"border-style: none; border-width: medium\" align=\"left\">&nbsp;\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"100\" style=\"border-style: none; border-width: medium\">\r\n		<b>\r\n		<font face=\"Arial\" size=\"2\" color=\"#000080\">".toCharArray();
    _jsp_string11 = "	\r\n<form name=\"CopySurvey\" method=\"post\">\r\n	<table border=\"0\" width=\"98%\" cellspacing=\"0\" cellpadding=\"8\" bordercolorlight=\"#C0C0C0\" bordercolordark=\"#808080\" bordercolor=\"#808080\" style=\"border-width: 0px\">\r\n	<tr>\r\n	\r\n	<td width=\"100\" style=\"border-style: none; border-width: medium; \" height=\"22\" align=\"left\">\r\n	<b>\r\n	<font face=\"Arial\" size=\"3\" color=\"black\">\r\n	".toCharArray();
    _jsp_string24 = ":</font></b></td>\r\n		<td width=\"665\" style=\"border-style: none; border-width: medium\" align=\"left\">\r\n		<p>\r\n		<select size=\"1\" name=\"selStatus\">\r\n			\r\n				<option value=\"1\">".toCharArray();
    _jsp_string13 = ":</font></b></td>\r\n		<td style=\"border-style: none; border-width: medium; \" height=\"22\" align=\"left\">\r\n<font face=\"Arial\" size=\"2\" color=\"#000080\">\r\n<b>\r\n".toCharArray();
    _jsp_string30 = "\" name=\"btnAdd\" style=\"float: left\" onClick=\"add(this.form,this.form.NewSurveyName,this.form.selOrg)\"/>\r\n	\r\n		<input type=\"button\" value=\"".toCharArray();
    _jsp_string4 = "\");\r\n		}\r\n	}	\r\n	else\r\n	{\r\n		alert(\"".toCharArray();
    _jsp_string21 = "</option>\r\n\r\n".toCharArray();
    _jsp_string12 = "\r\n	</font>\r\n	</b>\r\n	</td>\r\n	\r\n	<td>\r\n	</td>\r\n	\r\n	</tr>\r\n	\r\n		<tr>\r\n		<td style=\"border-style: none; border-width: medium\">&nbsp;</td>\r\n		<td style=\"border-style: none; border-width: medium\" align=\"left\">&nbsp;</td>\r\n	</tr>\r\n	\r\n	<tr>\r\n	<td width=\"100\" style=\"border-style: none; border-width: medium; \" height=\"22\" align=\"left\">\r\n	<b>\r\n	<font face=\"Arial\" size=\"2\" color=\"#000080\">\r\n	".toCharArray();
    _jsp_string32 = "\r\n</body>\r\n</html>".toCharArray();
    _jsp_string6 = " <font size=\"2\">\r\n   \r\n	 <script>\r\n	parent.location.href = \"index.jsp\";\r\n	</script>\r\n".toCharArray();
    _jsp_string29 = "\"/>\r\n<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td width=\"400\">&nbsp;</td>\r\n		<td>&nbsp;</td>\r\n	</tr>\r\n	<tr>\r\n	<td width=\"400\"></td>\r\n\r\n		<td width=\"200\">\r\n		<input type=\"button\" value=\"".toCharArray();
    _jsp_string1 = "\r\n                 \r\n                 \r\n".toCharArray();
    _jsp_string31 = "\" name=\"btnCancel\" style=\"float: left\" onClick=\"window.close()\"/></td>\r\n			</tr>\r\n</table>\r\n</form>\r\n".toCharArray();
    _jsp_string20 = " selected>".toCharArray();
    _jsp_string26 = "</option>\r\n				<option value=\"3\" selected>".toCharArray();
    _jsp_string15 = "\r\n\r\n</b>\r\n\r\n</font>\r\n\r\n</td>	</tr>\r\n	<tr>\r\n		<td style=\"border-style: none; border-width: medium\">&nbsp;</td>\r\n		<td style=\"border-style: none; border-width: medium\" align=\"left\">&nbsp;</td>\r\n	</tr>\r\n	<tr>\r\n		<td style=\"border-style: none; border-width: medium\">\r\n		<b>\r\n		<font face=\"Arial\" size=\"2\" color=\"#000080\">".toCharArray();
    _jsp_string17 = "	 \r\n\r\n<!--  \r\n	<tr>\r\n		<td width=\"100\" style=\"border-style: none; border-width: medium\">\r\n		<b>\r\n		<font face=\"Arial\" size=\"2\" color=\"#000080\">".toCharArray();
    _jsp_string8 = "\r\n				<script>\r\n					window.close();\r\n				</script>\r\n		".toCharArray();
    _jsp_string0 = "  \r\n                 \r\n".toCharArray();
    _jsp_string5 = "\");\r\n	}\r\n}\r\n\r\n</script>\r\n<head>\r\n<title>\r\nCopy Survey\r\n</title>\r\n</head>\r\n<body bgcolor=\"#FFFFCC\">\r\n".toCharArray();
    _jsp_string16 = ":</font></b></td>\r\n		<td style=\"border-style: none; border-width: medium\" align=\"left\">\r\n		<p align=\"center\"><font face=\"Arial\"><span style=\"font-size: 11pt\">\r\n		<input name=\"NewSurveyName\" size=\"20\" style=\"float: left\"></span></font></td>\r\n	</tr>\r\n	<tr>\r\n		<td style=\"border-style: none; border-width: medium\">&nbsp;</td>\r\n		<td style=\"border-style: none; border-width: medium\" align=\"left\">&nbsp;</td>\r\n	</tr>\r\n    \r\n".toCharArray();
    _jsp_string25 = "</option>\r\n				<option value=\"2\">".toCharArray();
    _jsp_string19 = "\r\n	<option value=".toCharArray();
    _jsp_string9 = "		\r\n			<script>\r\n			alert(\"".toCharArray();
    _jsp_string7 = "\r\n				<script>\r\n				window.close();\r\n				opener.opener.location.href = \"SurveyDetail.jsp\";\r\n				opener.close();\r\n				</script>\r\n\r\n	".toCharArray();
    _jsp_string2 = "\r\n".toCharArray();
    _jsp_string22 = "</option>\r\n".toCharArray();
    _jsp_string27 = "</option>\r\n\r\n\r\n															\r\n				</select><font size=\"2\"> </font>\r\n		</td>\r\n	</tr>\r\n\r\n-->\r\n\r\n</table>\r\n".toCharArray();
    _jsp_string3 = "\r\n \r\n  \r\n<html>\r\n<SCRIPT LANGUAGE=JAVASCRIPT>\r\nfunction add(form, field, field2)\r\n{\r\n	var valid = true;\r\n	if(field.value == \"\") {\r\n		valid = false;\r\n	} \r\n	\r\n	if(valid)\r\n	{\r\n		if(field2.value != \"0\") {\r\n			form.action=\"CopySurvey.jsp?add=1\"\r\n			form.method=\"post\";\r\n			form.submit();\r\n		} else {\r\n			alert(\"".toCharArray();
    _jsp_string10 = "\");\r\n			</script>\r\n	".toCharArray();
    _jsp_string18 = ":</font></b></td>\r\n		<td width=\"665\" style=\"border-style: none; border-width: medium\" align=\"left\">\r\n		<p><font face=\"Arial\"><span style=\"font-size: 11pt\">\r\n<select size=\"1\" name=\"selOrg\">\r\n<option value=\"0\">&nbsp</option>\r\n".toCharArray();
  }
}
