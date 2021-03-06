/*
 * JSP generated by Resin Professional 4.0.36 (built Fri, 26 Apr 2013 03:33:09 PDT)
 */

package _jsp;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;
import CP_Classes.vo.votblSurveyCluster;
import CP_Classes.vo.votblSurveyCompetency;
import CP_Classes.vo.votblSurveyBehaviour;
import CP_Classes.vo.voCluster;

public class _survey_0clusterkeybehaviourlist__jsp extends com.caucho.jsp.JavaPage
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
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.Login logchk;
    synchronized (pageContext.getSession()) {
      logchk = (CP_Classes.Login) pageContext.getSession().getAttribute("logchk");
      if (logchk == null) {
        logchk = new CP_Classes.Login();
        pageContext.getSession().setAttribute("logchk", logchk);
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
    out.write(_jsp_string0, 0, _jsp_string0.length);
    CP_Classes.Translate trans;
    synchronized (pageContext.getSession()) {
      trans = (CP_Classes.Translate) pageContext.getSession().getAttribute("trans");
      if (trans == null) {
        trans = new CP_Classes.Translate();
        pageContext.getSession().setAttribute("trans", trans);
      }
    }
    out.write(_jsp_string0, 0, _jsp_string0.length);
    CP_Classes.Cluster ClusterQ;
    synchronized (pageContext.getSession()) {
      ClusterQ = (CP_Classes.Cluster) pageContext.getSession().getAttribute("ClusterQ");
      if (ClusterQ == null) {
        ClusterQ = new CP_Classes.Cluster();
        pageContext.getSession().setAttribute("ClusterQ", ClusterQ);
      }
    }
    out.write(_jsp_string0, 0, _jsp_string0.length);
    CP_Classes.SurveyCompetency SC;
    SC = (CP_Classes.SurveyCompetency) pageContext.getAttribute("SC");
    if (SC == null) {
      SC = new CP_Classes.SurveyCompetency();
      pageContext.setAttribute("SC", SC);
    }
    out.write(_jsp_string0, 0, _jsp_string0.length);
    CP_Classes.SurveyKB SKB;
    SKB = (CP_Classes.SurveyKB) pageContext.getAttribute("SKB");
    if (SKB == null) {
      SKB = new CP_Classes.SurveyKB();
      pageContext.setAttribute("SKB", SKB);
    }
    out.write(_jsp_string3, 0, _jsp_string3.length);
    	
	response.setHeader("Pragma", "no-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("expires", 0);
	
	String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) {
  
  	response.sendRedirect("index.jsp");
  }

    out.write(_jsp_string3, 0, _jsp_string3.length);
     
//Edited by Roger 17 June 2008
//Fix bug when choosing key behaviour  

    out.write(_jsp_string3, 0, _jsp_string3.length);
    
		//capture event when ddl changed
		String comIdStr = request.getParameter("change");
		int paramComId = 0;
		if (comIdStr != null && !"".equals(comIdStr)) {
			paramComId = Integer.parseInt(comIdStr);
		}

    out.write(_jsp_string4, 0, _jsp_string4.length);
    
if ( request.getParameter("save") != null && !"".equals(request.getParameter("save"))) {
		
		int previousComp = 0;
		int SurveyID = CE_Survey.getSurvey_ID();
		int ClusterID = CE_Survey.getClusterID();
		System.out.println("ASDASDASDASDASDASDASDASDADSCluster id is "+ClusterID);
		String [] chkSelect = request.getParameterValues("chkKB");
		
		// Edited by Eric Lu 22/5/08
		// Added new boolean that determines whether key behaviour is added successfully
		// If boolean is true, successful message pops up
		boolean bKeyBehaviourAdded = true;

			if(chkSelect != null)
	    	{ 
	    		try
				{
		    		for(int i=0; i<chkSelect.length; i++)
					{
						System.out.println(">>>>>>>>" + chkSelect[i]);
						if (!CE_Survey.addKeyBehaviour(SurveyID,Integer.parseInt(request.getParameter("comId")),Integer.parseInt(chkSelect[i]),ClusterID))
							bKeyBehaviourAdded = false;
					}
				}
				catch(SQLException sqle)
				{	
					bKeyBehaviourAdded = false;
				}
			
				if (bKeyBehaviourAdded) {
					
    out.write(_jsp_string5, 0, _jsp_string5.length);
    
				} else {
					
    out.write(_jsp_string6, 0, _jsp_string6.length);
    
				}

			}	
			CE_Survey.setJobPos_ID(paramComId);

	}

	if(	request.getParameter("close") != null)
	{

    out.write(_jsp_string7, 0, _jsp_string7.length);
    
}

    out.write(_jsp_string8, 0, _jsp_string8.length);
    out.print((trans.tslt("No record selected")));
    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print((trans.tslt("No record available")));
    out.write(_jsp_string10, 0, _jsp_string10.length);
    out.print((trans.tslt("Cluster")));
    out.write(_jsp_string11, 0, _jsp_string11.length);
    
	voCluster vCluster = ClusterQ.getCluster(CE_Survey.getClusterID());
	String clusterName = vCluster.getClusterName();

    out.write(_jsp_string0, 0, _jsp_string0.length);
    out.print((clusterName));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print((trans.tslt("Competency")));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    

		Vector v = SC.getSurveyClusterCompetency(CE_Survey.getSurvey_ID(), CE_Survey.getClusterID());
		
		for(int i=0; i<v.size(); i++)
		{	
			votblSurveyCompetency vo = (votblSurveyCompetency)v.elementAt(i);
			
			int CompLevel = vo.getCompetencyLevel();
			int CompID = vo.getCompetencyID();
			String Comp = vo.getCompetencyName();
			
			if(paramComId == CompID)
			{
		
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((CompID));
    out.write(_jsp_string15, 0, _jsp_string15.length);
    out.print((Comp+" ("+CompLevel+")"));
    out.write(_jsp_string16, 0, _jsp_string16.length);
    	}
			else
			{
		
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((CompID));
    out.write('>');
    out.print((Comp+" ("+CompLevel+")"));
    out.write(_jsp_string17, 0, _jsp_string17.length);
    	}				
		}
		
    out.write(_jsp_string18, 0, _jsp_string18.length);
    out.print((trans.tslt("Key Behaviour")));
    out.write(_jsp_string19, 0, _jsp_string19.length);
     	

//Edited by Roger 17 June 2008
//Fix problem when listing the wrong key behavior
int behaviourSize = 0;

if (paramComId != 0) {
	int CompLevel = SC.getCompetencyLevel(CE_Survey.getSurvey_ID(), paramComId);
	CE_Survey.set_CompLevel(CompLevel);
		
	Vector vKB = SKB.getKBNotAssigned(CE_Survey.getSurvey_ID(),paramComId,CE_Survey.get_CompLevel());
	behaviourSize = vKB.size();
	for(int i=0; i<vKB.size(); i++)
	{
		votblSurveyBehaviour vo = (votblSurveyBehaviour)vKB.elementAt(i);
		int pkKB = vo.getKeyBehaviourID();
		String KeyBehaviour = vo.getKBName();


    out.write(_jsp_string20, 0, _jsp_string20.length);
    out.print((pkKB));
    out.write(_jsp_string21, 0, _jsp_string21.length);
    out.print(( KeyBehaviour ));
    out.write(_jsp_string22, 0, _jsp_string22.length);
    	}	
}

    out.write(_jsp_string23, 0, _jsp_string23.length);
     if (behaviourSize > 0) { 
    out.write(_jsp_string24, 0, _jsp_string24.length);
    out.print((trans.tslt("Add")));
    out.write(_jsp_string25, 0, _jsp_string25.length);
     } 
    out.write(_jsp_string26, 0, _jsp_string26.length);
    out.print((trans.tslt("Close")));
    out.write(_jsp_string27, 0, _jsp_string27.length);
    out.print((paramComId));
    out.write(_jsp_string28, 0, _jsp_string28.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("Survey_ClusterKeyBehaviourList.jsp"), -2258913449920319546L, false);
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

  private final static char []_jsp_string8;
  private final static char []_jsp_string16;
  private final static char []_jsp_string20;
  private final static char []_jsp_string23;
  private final static char []_jsp_string19;
  private final static char []_jsp_string22;
  private final static char []_jsp_string28;
  private final static char []_jsp_string7;
  private final static char []_jsp_string27;
  private final static char []_jsp_string18;
  private final static char []_jsp_string5;
  private final static char []_jsp_string15;
  private final static char []_jsp_string25;
  private final static char []_jsp_string4;
  private final static char []_jsp_string24;
  private final static char []_jsp_string11;
  private final static char []_jsp_string26;
  private final static char []_jsp_string14;
  private final static char []_jsp_string12;
  private final static char []_jsp_string10;
  private final static char []_jsp_string9;
  private final static char []_jsp_string21;
  private final static char []_jsp_string2;
  private final static char []_jsp_string0;
  private final static char []_jsp_string17;
  private final static char []_jsp_string6;
  private final static char []_jsp_string1;
  private final static char []_jsp_string3;
  private final static char []_jsp_string13;
  static {
    _jsp_string8 = "\r\n<html>\r\n<head>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n\r\n  <SCRIPT LANGUAGE=\"JavaScript\">\r\n<!-- Begin\r\nvar data;\r\nfunction checkData(field)\r\n{\r\n	var flag = false;\r\n	\r\n	\r\n	for (i = 0; i < field.length; i++) \r\n	{\r\n		if(field[i].checked)\r\n			flag = true;\r\n		if(field[i].selected)\r\n		{\r\n			flag = true;\r\n			data = field[i].value;\r\n			\r\n		}\r\n	}\r\n	if(field != null)\r\n		flag = true;\r\n		\r\n	return flag;\r\n		\r\n}\r\n\r\nfunction check(field)\r\n{\r\n	var isValid = 0;\r\n	var clickedValue = 0;\r\n	//check whether any checkbox selected\r\n	if( field == null ) {\r\n		isValid = 2;\r\n	\r\n	} else {\r\n\r\n		if(isNaN(field.length) == false) {\r\n			for (i = 0; i < field.length; i++)\r\n				if(field[i].checked) {\r\n					clickedValue = field[i].value;\r\n					isValid = 1;\r\n				}\r\n		}else {		\r\n			if(field.checked) {\r\n				clickedValue = field.value;\r\n				isValid = 1;\r\n			}\r\n				\r\n		}\r\n	}\r\n	\r\n	if(isValid == 1)\r\n		return clickedValue;\r\n	else if(isValid == 0)\r\n		alert(\"".toCharArray();
    _jsp_string16 = "</option>\r\n		".toCharArray();
    _jsp_string20 = "\r\n   <tr onMouseOver = \"this.bgColor = '#99ccff'\"\r\n    	onMouseOut = \"this.bgColor = '#FFFFcc'\">\r\n       <td width=\"20\">\r\n	   		<font span style='font-size:11.0pt;font-family:Arial'>\r\n	   		<input type=\"checkbox\" name=\"chkKB\" value=".toCharArray();
    _jsp_string23 = "\r\n</table>\r\n</div>\r\n\r\n\r\n<table border=\"0\" width=\"91%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td>\r\n ".toCharArray();
    _jsp_string19 = "</font></b></th>\r\n".toCharArray();
    _jsp_string22 = "</font><font size=\"2\">&nbsp;\r\n	   	</font>\r\n	   </td>\r\n   </tr>\r\n".toCharArray();
    _jsp_string28 = "\"/>\r\n</form>\r\n\r\n\r\n\r\n</body>\r\n</html>\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n".toCharArray();
    _jsp_string7 = "					\r\n<script>\r\nwindow.close()\r\nopener.location.href = 'SurveyKeyBehaviour.jsp';\r\n</script>\r\n".toCharArray();
    _jsp_string27 = "\" onClick=\"closeWindow()\" style=\"float: right\"></td>\r\n	</tr>\r\n	</table>\r\n\r\n<input type=\"hidden\" name=\"comId\" value=\"".toCharArray();
    _jsp_string18 = "\r\n    	</select>\r\n		</td>\r\n    </tr>\r\n    <tr>\r\n      <td>&nbsp;</td>\r\n      <td>&nbsp;</td>\r\n      <td>&nbsp;</td>\r\n    </tr>\r\n</table>\r\n\r\n<div style=\"width:388px; height:237px; z-index:1; overflow:auto\"> \r\n<table border=\"1\" width=\"383\" bordercolor=\"#3399FF\" bgcolor=\"#FFFFCC\">\r\n<th bgcolor=\"navy\" width=\"20\">\r\n	  <font size=\"2\">\r\n	  <input type=\"checkbox\" name=\"checkAll\" onClick=\"checkedAll(this.form, this.form.chkKB,this.form.checkAll)\"></font></th>\r\n<th bgcolor=\"navy\">\r\n	<font size=\"2\">\r\n	\r\n   \r\n    	<b><font span style='font-family:Arial;color:white'>".toCharArray();
    _jsp_string5 = "\r\n						<script>\r\n                                                        // Changed by DeZ, 26/06/08, update survey status to Not Commissioned whenever changes are made to survey\r\n							alert(\"Added successfully, survey status has been changed to Non Commissioned, to re-open survey please go to the Survey Detail page\");\r\n						</script>\r\n					".toCharArray();
    _jsp_string15 = " selected>".toCharArray();
    _jsp_string25 = "\" name=\"btnAdd\" onClick=\"confirmAdd(this.form,this.form.selCompetency, this.form.chkKB)\">\r\n ".toCharArray();
    _jsp_string4 = "\r\n\r\n\r\n".toCharArray();
    _jsp_string24 = "     \r\n        <input type=\"button\" value=\"".toCharArray();
    _jsp_string11 = ":\r\n".toCharArray();
    _jsp_string26 = "  &nbsp;    \r\n        </td>\r\n		<td width=\"448\"> <font size=\"2\">\r\n   \r\n    	<input type=\"button\" name=\"btnClose\" value=\"".toCharArray();
    _jsp_string14 = "\r\n			<option value=".toCharArray();
    _jsp_string12 = "\r\n</font>\r\n</th>\r\n    <tr>\r\n      <td><font span style='font-family:Arial; font-weight:700' size=\"2\">".toCharArray();
    _jsp_string10 = "\");\r\n	\r\n	isValid = 0;\r\n\r\n}\r\n\r\nfunction checkedAll(form, field, checkAll)\r\n{	\r\n	if(checkAll.checked == true) \r\n		for(var i=0; i<field.length; i++)\r\n			field[i].checked = true;\r\n	else \r\n		for(var i=0; i<field.length; i++)\r\n			field[i].checked = false;	\r\n}\r\n\r\nfunction closeWindow(form)\r\n{\r\n	window.close();\r\n 	opener.location.href = 'SurveyKeyBehaviour.jsp';\r\n}\r\nfunction CompChange(form, field) \r\n{\r\n	if (field.selectedIndex == 0) {\r\n		window.location.href = \"Survey_ClusterKeyBehaviourList.jsp\";\r\n	} else {\r\n		checkData(field);\r\n		form.action = \"Survey_ClusterKeyBehaviourList.jsp?change=\"+data\r\n		form.method = \"post\";\r\n		form.submit();\r\n	}\r\n}\r\n\r\n//Edited by Xuehai 02 Jun 2011. Remove 'void'. Enable to run on different browers like Chrome&Firefox.\r\n//void function confirmAdd(form,field, fieldKB) \r\nfunction confirmAdd(form,field, fieldKB) \r\n{\r\n	checkData(field);\r\n	if(check(fieldKB)) {\r\n		form.action = \"Survey_ClusterKeyBehaviourList.jsp?close=1&save=1\"\r\n		form.method = \"post\";\r\n		form.submit();\r\n	} \r\n}\r\n-->\r\n</script>\r\n</head>\r\n\r\n<body bgcolor=\"#DEE3EF\">\r\n\r\n<!-- Populate Drop Down List -->\r\n<form name=\"Survey_ClusterKeyBehaviourList\" method=\"post\" action=\"Survey_ClusterKeyBehaviourList.jsp\">\r\n<table width=\"343\" border=\"0\">\r\n<th colspan=3 align=center><b>\r\n<font span style='font-family:Arial; font-weight:700' size=\"2\">".toCharArray();
    _jsp_string9 = "\");\r\n	else if(isValid == 2)\r\n		alert(\"".toCharArray();
    _jsp_string21 = "></font><font span style='font-family:Arial' size=\"2\">\r\n            </font>\r\n	   </td>\r\n	   <td width=\"347\">\r\n           <font span style='font-family:Arial' size=\"2\">".toCharArray();
    _jsp_string2 = "  \r\n".toCharArray();
    _jsp_string0 = "\r\n".toCharArray();
    _jsp_string17 = "</option>\r\n\r\n		".toCharArray();
    _jsp_string6 = "\r\n						<script>\r\n							alert(\"Added unsuccessfully\");\r\n							window.close();\r\n							opener.location.href = 'SurveyKeyBehaviour.jsp';\r\n						</script>\r\n					".toCharArray();
    _jsp_string1 = "\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n".toCharArray();
    _jsp_string3 = "\r\n\r\n".toCharArray();
    _jsp_string13 = ":</font></td>\r\n      <td>&nbsp;</td>\r\n\r\n      <td>\r\n			<select name=\"selCompetency\" onChange=\"CompChange(this.form,this.form.selCompetency)\">\r\n				<option value=\"\" selected>Please select one</option>\r\n      ".toCharArray();
  }
}
