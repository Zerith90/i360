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
import CP_Classes.vo.votblOrganization;

public class _edit_0organization__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.Create_Edit_Survey CE_Survey;
    synchronized (pageContext.getSession()) {
      CE_Survey = (CP_Classes.Create_Edit_Survey) pageContext.getSession().getAttribute("CE_Survey");
      if (CE_Survey == null) {
        CE_Survey = new CP_Classes.Create_Edit_Survey();
        pageContext.getSession().setAttribute("CE_Survey", CE_Survey);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Organization org;
    synchronized (pageContext.getSession()) {
      org = (CP_Classes.Organization) pageContext.getSession().getAttribute("org");
      if (org == null) {
        org = new CP_Classes.Organization();
        pageContext.getSession().setAttribute("org", org);
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
    out.print((trans.tslt("Enter Organisation Name")));
    out.write(_jsp_string5, 0, _jsp_string5.length);
    out.print((trans.tslt("Enter Organisation Code")));
    out.write(_jsp_string6, 0, _jsp_string6.length);
    


String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {
    out.write(_jsp_string7, 0, _jsp_string7.length);
      } 
  else 
  { 	
  
if(request.getParameter("btnEdit") != null)
{
	String OrgName = request.getParameter("txtOrgName");
	String OrgCode = request.getParameter("txtOrgCode");
	int NameSeq = new Integer(request.getParameter("selNameSeq")).intValue();
        
        // Added by DeZ, 18/06/08, to add function to enable/disable Nominate Rater
        String nomRater = request.getParameter("selNomRater");
        
        // Changed by DeZ, 18/06/08, to add function to enable/disable Nominate Rater
	boolean bIsEdited = org.editRecord(logchk.getOrg(), OrgCode, OrgName, logchk.getCompany(), NameSeq, logchk.getPKUser(), nomRater);
	
	if(bIsEdited) {
	
    out.write(_jsp_string8, 0, _jsp_string8.length);
    
	}
}


    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print(( trans.tslt("Edit Organisation") ));
    out.write(_jsp_string10, 0, _jsp_string10.length);
    
	int PKOrg=0;
	int NameSeq=0;
	String NameSequence = "";
	String OrgCode="";
	String OrgName="";
        String nomRater="";
        
	/*****************************
	* Edit By James 15 - Nov 2007
	***********************************/
	
	votblOrganization vo_Org=org.getOrganization(logchk.getOrg());
	String logoName = "";
	
        
	if(vo_Org!=null)
	{
		PKOrg = vo_Org.getPKOrganization();
		OrgCode = vo_Org.getOrganizationCode();		
		OrgName = vo_Org.getOrganizationName();
		logoName = vo_Org.getOrganizationLogo();
		NameSeq = vo_Org.getNameSequence();
                
		if(NameSeq == 0)
			NameSequence = "FamilyName, OtherName";
		else
			NameSequence = "OtherName, FamilyName";
	}
	
    out.write(_jsp_string11, 0, _jsp_string11.length);
    out.print(( trans.tslt("Organisation Name") ));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    
	System.out.println(setting.getIsStandalone());
	if(setting.getIsStandalone() == false || (setting.getIsStandalone() && logchk.getUserType() == 1))
	{	
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((OrgName));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    	}	
	else
	{	
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((OrgName));
    out.write(_jsp_string15, 0, _jsp_string15.length);
    	}	
    out.write(_jsp_string16, 0, _jsp_string16.length);
    out.print(("Logo/" + logoName));
    out.write(_jsp_string17, 0, _jsp_string17.length);
    out.print(( trans.tslt("Organisation Code") ));
    out.write(_jsp_string18, 0, _jsp_string18.length);
    
	if(setting.getIsStandalone() == false || (setting.getIsStandalone() && logchk.getUserType() == 1))
	{	
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print((OrgCode));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    	}	
	else
	{	
    out.write(_jsp_string20, 0, _jsp_string20.length);
    out.print((OrgCode));
    out.write(_jsp_string15, 0, _jsp_string15.length);
    	}	
    out.write(_jsp_string21, 0, _jsp_string21.length);
    out.print(( trans.tslt("Name Sequence") ));
    out.write(_jsp_string22, 0, _jsp_string22.length);
    out.print(( trans.tslt("Family Name") ));
    out.write(_jsp_string23, 0, _jsp_string23.length);
    out.print(( trans.tslt("Other Name") ));
    out.write(_jsp_string24, 0, _jsp_string24.length);
    out.print(( trans.tslt("Other Name") ));
    out.write(_jsp_string23, 0, _jsp_string23.length);
    out.print(( trans.tslt("Family Name") ));
    out.write(_jsp_string25, 0, _jsp_string25.length);
    out.print((NameSeq));
    out.write(_jsp_string26, 0, _jsp_string26.length);
     // Added by DeZ, 18/06/08, to add function to enable/disable Nominate Rater 
    out.write(_jsp_string27, 0, _jsp_string27.length);
    out.print(( trans.tslt("Nominate Raters") ));
    out.write(_jsp_string28, 0, _jsp_string28.length);
    out.print(( trans.tslt("Yes")));
    out.write(_jsp_string29, 0, _jsp_string29.length);
    out.print(( trans.tslt("No") ));
    out.write(_jsp_string30, 0, _jsp_string30.length);
    
                int nomRaterCurr = 1;
                if(vo_Org.getNomRater()) { nomRaterCurr = 0; }
                
    out.write(_jsp_string31, 0, _jsp_string31.length);
    out.print((nomRaterCurr));
    out.write(_jsp_string32, 0, _jsp_string32.length);
    out.print(( trans.tslt("Save Changes") ));
    out.write(_jsp_string33, 0, _jsp_string33.length);
    out.print(( trans.tslt("Close") ));
    out.write(_jsp_string34, 0, _jsp_string34.length);
    	}	
    out.write(_jsp_string35, 0, _jsp_string35.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("Edit_Organization.jsp"), -1059571710438927744L, false);
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

  private final static char []_jsp_string12;
  private final static char []_jsp_string17;
  private final static char []_jsp_string22;
  private final static char []_jsp_string21;
  private final static char []_jsp_string31;
  private final static char []_jsp_string28;
  private final static char []_jsp_string4;
  private final static char []_jsp_string35;
  private final static char []_jsp_string26;
  private final static char []_jsp_string5;
  private final static char []_jsp_string25;
  private final static char []_jsp_string29;
  private final static char []_jsp_string30;
  private final static char []_jsp_string6;
  private final static char []_jsp_string24;
  private final static char []_jsp_string14;
  private final static char []_jsp_string13;
  private final static char []_jsp_string19;
  private final static char []_jsp_string27;
  private final static char []_jsp_string34;
  private final static char []_jsp_string0;
  private final static char []_jsp_string8;
  private final static char []_jsp_string9;
  private final static char []_jsp_string16;
  private final static char []_jsp_string23;
  private final static char []_jsp_string33;
  private final static char []_jsp_string32;
  private final static char []_jsp_string2;
  private final static char []_jsp_string1;
  private final static char []_jsp_string18;
  private final static char []_jsp_string7;
  private final static char []_jsp_string10;
  private final static char []_jsp_string15;
  private final static char []_jsp_string20;
  private final static char []_jsp_string3;
  private final static char []_jsp_string11;
  static {
    _jsp_string12 = ":</font></td>\r\n		<td width=\"498\">\r\n".toCharArray();
    _jsp_string17 = "\" width=\"160\" height=\"100\"></td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"181\">&nbsp;</td>\r\n		<td>&nbsp;</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"181\"><font face=\"Arial\" size=\"2\">\r\n		".toCharArray();
    _jsp_string22 = ":</font></td>\r\n		<td><select size=\"1\" name=\"selNameSeq\">\r\n		<option value=\"0\">".toCharArray();
    _jsp_string21 = "		\r\n		</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"181\">&nbsp;</td>\r\n		<td>&nbsp;</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"181\"><font face=\"Arial\" size=\"2\">\r\n		".toCharArray();
    _jsp_string31 = "\r\n                window.document.Edit_Organization.selNomRater.selectedIndex=".toCharArray();
    _jsp_string28 = ":</font></td>\r\n        <td>\r\n            <select size=\"1\" name=\"selNomRater\">\r\n                <option value=\"True\">".toCharArray();
    _jsp_string4 = "\r\n<title>Edit Organisation</title>\r\n</head>\r\n<SCRIPT LANGUAGE=\"JavaScript\">\r\nvar x = parseInt(window.screen.width) / 2 - 200;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up\r\nvar y = parseInt(window.screen.height) / 2 - 100;  // the number 125 is the exact half of the height of the pop-up and so should be changed according to the size of the pop-up\r\n\r\n\r\nfunction validate()\r\n{\r\n    x = document.Edit_Organization\r\n    if (x.txtOrgName.value == \"\")\r\n    {\r\n	alert(\"".toCharArray();
    _jsp_string35 = "\r\n</body>\r\n</html>".toCharArray();
    _jsp_string26 = ";\r\n		</script>\r\n		</select><font size=\"2\"> </font>\r\n		\r\n		</td>\r\n	</tr>\r\n    <tr>\r\n		<td width=\"181\">&nbsp;</td>\r\n		<td>&nbsp;</td>\r\n	</tr>\r\n    ".toCharArray();
    _jsp_string5 = "\");\r\n	return false \r\n	}\r\n	if (x.txtOrgCode.value == \"\")\r\n    {\r\n	alert(\"".toCharArray();
    _jsp_string25 = "</option>\r\n		<script>\r\n			window.document.Edit_Organization.selNameSeq.selectedIndex=".toCharArray();
    _jsp_string29 = "</option>\r\n                <option value=\"False\">".toCharArray();
    _jsp_string30 = "</option>\r\n            </select>\r\n            <script>\r\n                ".toCharArray();
    _jsp_string6 = "\");\r\n	return false \r\n	}\r\n	\r\n	// Edited Eric Lu 15-May-08\r\n	// Added confirmation box for editing organization\r\n	if (confirm(\"Edit Organisation?\")) {\r\n		return true;\r\n	} else {\r\n		return false;\r\n	}\r\n}\r\n</SCRIPT>\r\n<body bgcolor=\"#FFFFCC\">\r\n".toCharArray();
    _jsp_string24 = "</option>\r\n		<option value=\"1\">".toCharArray();
    _jsp_string14 = "\">\r\n".toCharArray();
    _jsp_string13 = "\r\n			<input type=\"text\" name=\"txtOrgName\" size=\"50\" value=\"".toCharArray();
    _jsp_string19 = "		\r\n		<input type=\"text\" name=\"txtOrgCode\" size=\"10\" value=\"".toCharArray();
    _jsp_string27 = "\r\n    <tr>\r\n        <td width=\"181\"><font face=\"Arial\" size=\"2\">\r\n        ".toCharArray();
    _jsp_string34 = "\" name=\"btnClose\" style=\"float: right\" onClick=\"window.close()\"></td>\r\n	</tr>\r\n</table>\r\n</form>\r\n".toCharArray();
    _jsp_string0 = "  \r\n				 \r\n".toCharArray();
    _jsp_string8 = "\r\n	<script>\r\n		alert(\"Edited successfully\");\r\n		window.close();\r\n		//Edited by Xuehai, 06 Jun 2011. Changing location.href() to location.href='';\r\n		//opener.location.href('OrganizationList.jsp');\r\n		opener.location.href='OrganizationList.jsp';\r\n	</script>\r\n	".toCharArray();
    _jsp_string9 = "\r\n  <form name=\"Edit_Organization\" action=\"Edit_Organization.jsp\" method=\"post\" onSubmit=\"return validate()\">\r\n<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td><b><font face=\"Arial\" color=\"#000080\" size=\"2\">".toCharArray();
    _jsp_string16 = "			\r\n		\r\n		</td>\r\n\r\n		<td width=\"304\" rowspan=\"5\" align=\"center\" valign=\"top\"><img src=\"".toCharArray();
    _jsp_string23 = ", ".toCharArray();
    _jsp_string33 = "\" name=\"btnEdit\" style=\"float: left\"></td>\r\n		<td align=\"right\" width=\"15%\">\r\n		<font size=\"2\">\r\n   \r\n		<input type=\"button\" value=\"".toCharArray();
    _jsp_string32 = ";\r\n            </script>\r\n        </td>\r\n    </tr>\r\n</table>\r\n<table border=\"0\" width=\"96%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td width=\"29%\">&nbsp;</td>\r\n		<td colspan=\"2\">&nbsp;</td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"29%\" align=\"right\">&nbsp;</td>\r\n		<td align=\"right\" colspan=\"2\">&nbsp;</td>\r\n	</tr>\r\n	<tr>		\r\n		<td align=\"left\" width=\"56%\"><input type=\"submit\" value=\"".toCharArray();
    _jsp_string2 = "\r\n".toCharArray();
    _jsp_string1 = "  \r\n".toCharArray();
    _jsp_string18 = ":</font></td>\r\n		<td>\r\n".toCharArray();
    _jsp_string7 = " <font size=\"2\">\r\n   \r\n	<script>\r\n	parent.location.href = \"index.jsp\";\r\n	</script>\r\n".toCharArray();
    _jsp_string10 = "</font></b></td>\r\n	</tr>\r\n	<tr>\r\n		<td>&nbsp;</td>\r\n	</tr>\r\n	<tr>\r\n		<td>&nbsp;</td>\r\n	</tr>\r\n</table>\r\n<table border=\"0\" width=\"98%\" cellspacing=\"0\" cellpadding=\"0\">\r\n".toCharArray();
    _jsp_string15 = "\" readonly>\r\n".toCharArray();
    _jsp_string20 = "\r\n		<input type=\"text\" name=\"txtOrgCode\" size=\"10\" value=\"".toCharArray();
    _jsp_string3 = "\r\n\r\n<html>\r\n<head>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n\r\n".toCharArray();
    _jsp_string11 = "\r\n\r\n	<tr>\r\n		<td width=\"181\"><font face=\"Arial\" size=\"2\">\r\n		".toCharArray();
  }
}