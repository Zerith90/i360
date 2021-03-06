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
import CP_Classes.vo.votblSurveyDemos;

public class _survey_0demoslist__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.Competency CompetencyQuery;
    synchronized (pageContext.getSession()) {
      CompetencyQuery = (CP_Classes.Competency) pageContext.getSession().getAttribute("CompetencyQuery");
      if (CompetencyQuery == null) {
        CompetencyQuery = new CP_Classes.Competency();
        pageContext.getSession().setAttribute("CompetencyQuery", CompetencyQuery);
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
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.SurveyDemo SD;
    synchronized (pageContext.getSession()) {
      SD = (CP_Classes.SurveyDemo) pageContext.getSession().getAttribute("SD");
      if (SD == null) {
        SD = new CP_Classes.SurveyDemo();
        pageContext.getSession().setAttribute("SD", SD);
      }
    }
    out.write(_jsp_string3, 0, _jsp_string3.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string4, 0, _jsp_string4.length);
    	
	response.setHeader("Pragma", "no-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("expires", 0);

String username=(String)session.getAttribute("username");
  if (!logchk.isUsable(username)) 
  {
    out.write(_jsp_string5, 0, _jsp_string5.length);
      } 
  else 
  {
  	
	if(request.getParameter("add") != null)
	{
		String [] chkSelect = new String[10];
		int SurveyID = CE_Survey.getSurvey_ID();
 	    chkSelect = request.getParameterValues("chkDemo");
 	    
 	    // Edited by Eric Lu 22/5/08
 	    // Added boolean to determine whether demographics are added successfully
 	    // If successful, confirm box with successful message pops up
 	    // Else, confirm box with unsuccessful message pops up
 	    boolean bDemosAdded = true;
 	    
		try
		{
			if (!CE_Survey.addDemos(SurveyID,chkSelect))
				bDemosAdded = false;	
		}
		catch(SQLException sqle)
		{
			bDemosAdded = false;
		}
 
 		if (bDemosAdded) {
 			
    out.write(_jsp_string6, 0, _jsp_string6.length);
    
 		} else {
 			
    out.write(_jsp_string7, 0, _jsp_string7.length);
    
 		}


    out.write(_jsp_string8, 0, _jsp_string8.length);
    
	}

    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print((trans.tslt("Demographic Option")));
    out.write(_jsp_string10, 0, _jsp_string10.length);
    
	Vector v = SD.getDemoNotAssigned(CE_Survey.getSurvey_ID());
	
	for(int i=0; i<v.size(); i++)
	{
		votblSurveyDemos vo = (votblSurveyDemos)v.elementAt(i);
		int DemoID = vo.getDemographicID();
		String DemoName = vo.getDemographicName();
				

    out.write(_jsp_string11, 0, _jsp_string11.length);
    out.print((DemoID));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print(( DemoName ));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    	}	
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((trans.tslt("Add")));
    out.write(_jsp_string15, 0, _jsp_string15.length);
    out.print((trans.tslt("Close")));
    out.write(_jsp_string16, 0, _jsp_string16.length);
    	}	
    out.write(_jsp_string17, 0, _jsp_string17.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("Survey_DemosList.jsp"), 6668306571898218949L, false);
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

  private final static char []_jsp_string3;
  private final static char []_jsp_string10;
  private final static char []_jsp_string13;
  private final static char []_jsp_string11;
  private final static char []_jsp_string17;
  private final static char []_jsp_string14;
  private final static char []_jsp_string12;
  private final static char []_jsp_string6;
  private final static char []_jsp_string9;
  private final static char []_jsp_string7;
  private final static char []_jsp_string15;
  private final static char []_jsp_string0;
  private final static char []_jsp_string8;
  private final static char []_jsp_string2;
  private final static char []_jsp_string1;
  private final static char []_jsp_string5;
  private final static char []_jsp_string4;
  private final static char []_jsp_string16;
  static {
    _jsp_string3 = "\r\n\r\n\r\n<html>\r\n<head>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n\r\n".toCharArray();
    _jsp_string10 = " </span></th>\r\n".toCharArray();
    _jsp_string13 = "</font><font size=\"2\" face=\"Arial\">&nbsp;\r\n	   	</font>\r\n	   </td>\r\n   </tr>\r\n".toCharArray();
    _jsp_string11 = "		\r\n   <tr onMouseOver = \"this.bgColor = '#99ccff'\"\r\n    	onMouseOut = \"this.bgColor = '#FFFFFF'\">\r\n       <td bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\" width=\"20\">\r\n	   		<font face=\"Arial\" span style=\"font-size: 11.0pt; font-family: Arial\">\r\n	   		<input type=\"checkbox\" name=\"chkDemo\" value=".toCharArray();
    _jsp_string17 = "\r\n</body>\r\n</html>\r\n".toCharArray();
    _jsp_string14 = "\r\n</table>\r\n</div>\r\n<table border=\"0\" width=\"75%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td width=\"210\">\r\n<input type=\"button\" name=\"Add\" value=\"".toCharArray();
    _jsp_string12 = "></font><font span style='font-family:Arial' size=\"2\">\r\n            </font>\r\n	   </td>\r\n	   <td bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\" width=\"124\">\r\n           <font span style='font-family:Arial' size=\"2\">".toCharArray();
    _jsp_string6 = "\r\n 				<script>\r\n 					alert(\"Added successfully\");\r\n 				</script>\r\n 			".toCharArray();
    _jsp_string9 = "\r\n<form name=\"Survey_DemosList\" method=\"post\" action=\"Survey_DemosList.jsp\">\r\n<div style=\"width:169px; height:272px; z-index:1; overflow:auto\"> \r\n<table border=\"1\" bordercolor=\"#3399FF\" width=\"160\">\r\n\r\n<th bgcolor=\"navy\" colspan=\"2\">\r\n	<font size=\"2\">\r\n   \r\n    	<span style=\"font-family: Arial; color: #FFFFFF\">".toCharArray();
    _jsp_string7 = "\r\n 				<script>\r\n 					alert(\"Added unsuccessfully\");\r\n 				</script>\r\n 			".toCharArray();
    _jsp_string15 = "\" onclick=\"ConfirmAdd(this.form,this.form.chkDemo)\"></td>\r\n		<td>\r\n		<p align=\"center\">\r\n		<input type=\"button\" value=\"".toCharArray();
    _jsp_string0 = "\r\n\r\n\r\n\r\n\r\n".toCharArray();
    _jsp_string8 = "					\r\n<script>window.close()\r\n 		opener.location.href = 'SurveyDemos.jsp';</script>\r\n".toCharArray();
    _jsp_string2 = "\r\n".toCharArray();
    _jsp_string1 = "  \r\n".toCharArray();
    _jsp_string5 = " <font size=\"2\">\r\n   \r\n	<script>\r\n	parent.location.href = \"index.jsp\";\r\n	</script>\r\n".toCharArray();
    _jsp_string4 = "\r\n<title>Survey - Demographics</title>\r\n</head>\r\n  <SCRIPT LANGUAGE=\"JavaScript\">\r\nfunction check(field)\r\n{\r\n	var isValid = 0;\r\n	var clickedValue = 0;\r\n	//check whether any checkbox selected\r\n	\r\n	if( field == null ) {\r\n		isValid = 2;\r\n	\r\n	} else {\r\n		for (i = 0; i < field.length; i++) \r\n			if(field[i].checked) {		\r\n				clickedValue = field[i].value;\r\n				//field[i].checked = false;\r\n				isValid = 1;\r\n			}\r\n    \r\n		if(isValid == 0 && field != null)  {\r\n			if(field.checked) {\r\n				clickedValue = field.value;\r\n				isValid = 1;\r\n			}\r\n		}\r\n    }\r\n	\r\n	if(isValid == 1)\r\n		return clickedValue;\r\n	else if(isValid == 0)\r\n		alert(\"No record selected\");\r\n	else if(isValid == 2)\r\n		alert(\"No record available\");\r\n	\r\n	isValid = 0;	\r\n	\r\n}\r\n\r\nfunction closeWindow()\r\n{\r\n	window.close();\r\n}\r\nfunction ConfirmAdd(form,field) \r\n{\r\n	if(check(field))\r\n	{\r\n		// Edited by Eric Lu 22/5/08\r\n		// Displays confirm box when adding demographics\r\n		if (confirm(\"Add Demographics?\")) {\r\n			form.action = \"Survey_DemosList.jsp?add=1\"\r\n			form.method = \"post\";\r\n			form.submit();\r\n		}\r\n	}\r\n	\r\n}\r\n\r\n\r\n</script>\r\n<body bgcolor=\"#DEE3EF\">\r\n".toCharArray();
    _jsp_string16 = "\" name=\"btnClose\" onclick=\"closeWindow()\" style=\"float: right\"></td>\r\n	</tr>\r\n</table>\r\n</form>\r\n".toCharArray();
  }
}
