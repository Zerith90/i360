/*
 * JSP generated by Resin Professional 4.0.36 (built Fri, 26 Apr 2013 03:33:09 PDT)
 */

package _jsp;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.sql.*;
import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.Date;
import java.text.*;
import java.lang.String;
import CP_Classes.vo.voCompetency;

public class _report_0developmentguide_0competencylist__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.Database Database;
    synchronized (pageContext.getSession()) {
      Database = (CP_Classes.Database) pageContext.getSession().getAttribute("Database");
      if (Database == null) {
        Database = new CP_Classes.Database();
        pageContext.getSession().setAttribute("Database", Database);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.DevelopmentGuide Rpt9;
    synchronized (pageContext.getSession()) {
      Rpt9 = (CP_Classes.DevelopmentGuide) pageContext.getSession().getAttribute("Rpt9");
      if (Rpt9 == null) {
        Rpt9 = new CP_Classes.DevelopmentGuide();
        pageContext.getSession().setAttribute("Rpt9", Rpt9);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Setting setting;
    synchronized (pageContext.getSession()) {
      setting = (CP_Classes.Setting) pageContext.getSession().getAttribute("setting");
      if (setting == null) {
        setting = new CP_Classes.Setting();
        pageContext.getSession().setAttribute("setting", setting);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
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
    out.write(_jsp_string3, 0, _jsp_string3.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string4, 0, _jsp_string4.length);
    out.print((trans.tslt("Save Development Guide")));
    out.write(_jsp_string5, 0, _jsp_string5.length);
    	
Date timeStamp = new java.util.Date();
	SimpleDateFormat dFormat = new SimpleDateFormat("ddMMyyHHmmss");
	String temp  =  dFormat.format(timeStamp);
	String file_name = "Development Guide " + temp + ".xls";

	if(request.getParameter("add") != null)
	{		
			int SurveyID = CE_Survey.getSurvey_ID();
			// Changed by Ha 26/05/08: continue the method only if chkComp !=null
			if (request.getParameterValues("chkComp")!=null)
			{
	 	   		String [] chkSelect = request.getParameterValues("chkComp");
	 	    
				Rpt9.SelComp(chkSelect,logchk.getPKUser(),file_name);
					
				String output = setting.getReport_Path() + "\\"+file_name;
				File f = new File (output);
					
				//set the content type(can be excel/word/powerpoint etc..)
				response.reset();
				response.setContentType ("application/xls");
				//set the header and also the Name by which user will be prompted to save
				response.addHeader ("Content-Disposition", "attachment;filename="+file_name+"");
							
				//get the file name
				String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
				//OPen an input stream to the file and post the file contents thru the 
				//servlet output stream to the client m/c
			
				InputStream in = new FileInputStream(f);
				ServletOutputStream outs = response.getOutputStream();
							
				int bit = 256;
				int i = 0;									
				try {
					   while ((bit) >= 0) 
					   {
					       bit = in.read();
					       outs.write(bit);
					   }
					   //System.out.println("" +bit);
					
			 	} 
				catch (IOException ioe) 
				{
					   ioe.printStackTrace(System.out);
				}

				outs.flush();
           		outs.close();
          		in.close();	
			}
	}	
	int DisplayNo;
	String pkCompetency; 
	String name, definition;
	DisplayNo = 1;

	

    out.write(_jsp_string6, 0, _jsp_string6.length);
    out.print((trans.tslt("Name")));
    out.write(_jsp_string7, 0, _jsp_string7.length);
    out.print((trans.tslt("Definition")));
    out.write(_jsp_string8, 0, _jsp_string8.length);
     	
	/*
	*changed by clement
	*23-jan-2008
	*/
	
	// System.out.println("OrgID = "+logchk.getOrg());
	// String query1 ="SELECT * FROM Competency WHERE FKOrganizationID = "+logchk.getOrg();
	Vector v = CompetencyQuery.getCompetencyByOrg(logchk.getOrg());
	
	for(int i=0; i<v.size(); i++){
		voCompetency vo = (voCompetency) v.get(i);

		pkCompetency = Integer.toString(vo.getPKCompetency());
		
		name = vo.getCompetencyName();
		definition = vo.getCompetencyDefinition();
		
	
    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print((pkCompetency));
    out.write(_jsp_string10, 0, _jsp_string10.length);
     out.print(name);
    out.write(_jsp_string11, 0, _jsp_string11.length);
     out.print(definition);
    out.write(_jsp_string12, 0, _jsp_string12.length);
     	DisplayNo++;
	} 

    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((trans.tslt("Save")));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((trans.tslt("Close")));
    out.write(_jsp_string15, 0, _jsp_string15.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("Report_DevelopmentGuide_CompetencyList.jsp"), -2611507907322019397L, false);
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

  private final static char []_jsp_string0;
  private final static char []_jsp_string4;
  private final static char []_jsp_string14;
  private final static char []_jsp_string8;
  private final static char []_jsp_string7;
  private final static char []_jsp_string15;
  private final static char []_jsp_string6;
  private final static char []_jsp_string11;
  private final static char []_jsp_string13;
  private final static char []_jsp_string9;
  private final static char []_jsp_string2;
  private final static char []_jsp_string12;
  private final static char []_jsp_string1;
  private final static char []_jsp_string10;
  private final static char []_jsp_string3;
  private final static char []_jsp_string5;
  static {
    _jsp_string0 = "   \r\n\r\n".toCharArray();
    _jsp_string4 = "\r\n</head>\r\n\r\n\r\n<SCRIPT LANGUAGE=\"JavaScript\">\r\n<!-- Begin\r\n\r\n\r\nfunction check(field)\r\n{\r\n	var isValid = 0;\r\n	var clickedValue = 0;\r\n	//check whether any checkbox selected\r\n	\r\n	if( field == null ) {\r\n		isValid = 2;\r\n	\r\n	} else {\r\n		for (i = 0; i < field.length; i++) \r\n			if(field[i].checked) {		\r\n				clickedValue = field[i].value;\r\n				//field[i].checked = false;\r\n				isValid = 1;\r\n			}\r\n    \r\n		if(isValid == 0 && field != null)  {\r\n			if(field.checked) {\r\n				clickedValue = field.value;\r\n				isValid = 1;\r\n			}\r\n		}\r\n    }\r\n	\r\n	if(isValid == 1)\r\n		return clickedValue;\r\n	else if(isValid == 0)\r\n		alert(\"No record selected\");\r\n	else if(isValid == 2)\r\n		alert(\"No record available\");\r\n	\r\n	isValid = 0;	\r\n	\r\n}\r\n\r\n//Edited by Xuehai 02 Jun 2011. Remove 'void'. Enable to run on different browers like Chrome&Firefox.\r\n//void function ConfirmAdd(form, field)\r\nfunction ConfirmAdd(form, field)\r\n{\r\n	if(check(field))\r\n	{\r\n	//\\\\ Changed message from print to save by Ha 26/05/08\r\n		if(confirm(\"".toCharArray();
    _jsp_string14 = "\" onclick=\"ConfirmAdd(this.form,this.form.chkComp)\"></td>\r\n		<td><input type=\"button\" value=\"".toCharArray();
    _jsp_string8 = "</font></b></th>\r\n".toCharArray();
    _jsp_string7 = "</font></b></th>\r\n<th bgcolor=\"navy\"><b><font style='font-family:Arial;color:white' size=\"2\">".toCharArray();
    _jsp_string15 = "\" name=\"btnClose\" onclick=\"closeWindow()\"></td>\r\n	</tr>\r\n</table>\r\n&nbsp;&nbsp;&nbsp;\r\n\r\n</form>\r\n\r\n</body>\r\n</html>".toCharArray();
    _jsp_string6 = "\r\n<form name=\"Report_DevelopmentGuide_CompetencyList\" method=\"post\" action=\"Report_DevelopmentGuide_CompetencyList.jsp\">\r\n\r\n<table border=\"1\">\r\n<tr><td>\r\n<div style='width:900px; height:500px; z-index:1; overflow:auto;'>  \r\n<table border=\"1\" bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\">\r\n<th bgcolor=\"navy\"><input type=\"checkbox\" name=\"chkAll\" onclick='checkAll(this, this.form.chkComp)'></th>\r\n<th bgcolor=\"navy\"><b><font style='font-family:Arial;color:white' size=\"2\">".toCharArray();
    _jsp_string11 = "</font><font size=\"2\" face=\"Arial\">\r\n			</font>\r\n       </td>\r\n	   <td>\r\n           <font style='font-family:Arial' size=\"2\">".toCharArray();
    _jsp_string13 = "\r\n</table>\r\n</div>\r\n</td></tr>\r\n</table>\r\n<p></p>\r\n<table border=\"0\" width=\"55%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td width=\"210\">\r\n\r\n<input type=\"button\" name=\"Add\" value=\"".toCharArray();
    _jsp_string9 = "\r\n   <tr onMouseOver = \"this.bgColor = '#99ccff'\"\r\n    	onMouseOut = \"this.bgColor = '#FFFFcc'\">\r\n       <td>\r\n	   		<font face=\"Arial\" style=\"font-size: 11.0pt; font-family: Arial\">\r\n	   		<input type=\"checkbox\" name=\"chkComp\" value=".toCharArray();
    _jsp_string2 = "\r\n".toCharArray();
    _jsp_string12 = "</font><font size=\"2\" face=\"Arial\">\r\n			</font>\r\n       </td>\r\n   </tr>\r\n".toCharArray();
    _jsp_string1 = "   \r\n".toCharArray();
    _jsp_string10 = "></font><font style='font-family:Arial' size=\"2\">\r\n            </font>\r\n	   </td>\r\n	   <td>\r\n           <font style='font-family:Arial' size=\"2\">".toCharArray();
    _jsp_string3 = "\r\n\r\n<html>\r\n<head>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n\r\n".toCharArray();
    _jsp_string5 = "?\"))\r\n		{\r\n			form.action=\"Report_DevelopmentGuide_CompetencyList.jsp?add=1\";\r\n			form.method=\"post\";\r\n			form.submit();\r\n		}\r\n		\r\n	}\r\n	\r\n} \r\n\r\nfunction closeWindow()\r\n{\r\n	window.close();\r\n}\r\n/* Edited by Xuehai. Check or Uncheck all */\r\nfunction checkAll(chkAll, chkComp){\r\n	if(chkComp && chkComp.length>0){\r\n		var checked=chkAll.checked;\r\n		var i;\r\n		for(i=0;i<chkComp.length;i++){\r\n			chkComp[i].checked=checked;\r\n		}\r\n	}\r\n}\r\n</script>\r\n\r\n<body>\r\n".toCharArray();
  }
}
