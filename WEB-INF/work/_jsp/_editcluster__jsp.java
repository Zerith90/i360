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
import java.lang.*;
import CP_Classes.vo.*;

public class _editcluster__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.Database Database;
    synchronized (pageContext.getSession()) {
      Database = (CP_Classes.Database) pageContext.getSession().getAttribute("Database");
      if (Database == null) {
        Database = new CP_Classes.Database();
        pageContext.getSession().setAttribute("Database", Database);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Competency Comp;
    synchronized (pageContext.getSession()) {
      Comp = (CP_Classes.Competency) pageContext.getSession().getAttribute("Comp");
      if (Comp == null) {
        Comp = new CP_Classes.Competency();
        pageContext.getSession().setAttribute("Comp", Comp);
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
    out.write(_jsp_string3, 0, _jsp_string3.length);
    CP_Classes.Translate trans;
    synchronized (pageContext.getSession()) {
      trans = (CP_Classes.Translate) pageContext.getSession().getAttribute("trans");
      if (trans == null) {
        trans = new CP_Classes.Translate();
        pageContext.getSession().setAttribute("trans", trans);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Cluster Cluster;
    synchronized (pageContext.getSession()) {
      Cluster = (CP_Classes.Cluster) pageContext.getSession().getAttribute("Cluster");
      if (Cluster == null) {
        Cluster = new CP_Classes.Cluster();
        pageContext.getSession().setAttribute("Cluster", Cluster);
      }
    }
    out.write(_jsp_string4, 0, _jsp_string4.length);
    out.print((trans.tslt("Edit Cluster")));
    out.write(_jsp_string5, 0, _jsp_string5.length);
    out.print((trans.tslt("Please enter Cluster Name")));
    out.write(_jsp_string6, 0, _jsp_string6.length);
    
String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {

    out.write(_jsp_string7, 0, _jsp_string7.length);
      } 
  else 
  { 

/*-------------------------------------------------------------------end login modification 1--------------------------------------*/


    out.write(_jsp_string8, 0, _jsp_string8.length);
    
	int orgID = logchk.getOrg();	
	int compID = logchk.getCompany();
	int pkUser = logchk.getPKUser();
	int userType = logchk.getUserType();	// 1= super admin
	
	int compExist = 0;
	
	if(request.getParameter("edit") != null) {
		if(request.getParameter("Name") != null)	{
  			String name = request.getParameter("Name");
  			

			// check whether Cluster name already exists in database
		    Boolean Exist = false;
		    
			Vector v = Cluster.getOrganizationCluster(logchk.getOrg());
			for(int i = 0; i < v.size(); i++){
				voCluster vo = (voCluster)v.elementAt(i);
				
				String ClusterName = vo.getClusterName();
				
				if(name.equals(ClusterName)){
					Exist = true;
				}

			}
			
			if(!Exist) {						
				try{					
					boolean edit = Cluster.updateCluster(logchk.getClusterID(),name, pkUser);
					
					if(edit){
						
    out.write(_jsp_string9, 0, _jsp_string9.length);
     
					}
					else{
						
					}
				}catch(SQLException SE) {
                     System.out.println(SE);
				}
			} else {			

    out.write(_jsp_string10, 0, _jsp_string10.length);
    out.print((trans.tslt("Cluster Name exists")));
    out.write(_jsp_string11, 0, _jsp_string11.length);
    			
			}

	}
	}

    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print(( trans.tslt("Cluster Name") ));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((Cluster.getCluster(logchk.getClusterID()).getClusterName()));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print(( trans.tslt("Submit") ));
    out.write(_jsp_string15, 0, _jsp_string15.length);
    out.print(( trans.tslt("Cancel") ));
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("EditCluster.jsp"), 988693172469462499L, false);
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

  private final static char []_jsp_string5;
  private final static char []_jsp_string4;
  private final static char []_jsp_string10;
  private final static char []_jsp_string1;
  private final static char []_jsp_string17;
  private final static char []_jsp_string16;
  private final static char []_jsp_string9;
  private final static char []_jsp_string0;
  private final static char []_jsp_string7;
  private final static char []_jsp_string12;
  private final static char []_jsp_string6;
  private final static char []_jsp_string14;
  private final static char []_jsp_string3;
  private final static char []_jsp_string2;
  private final static char []_jsp_string13;
  private final static char []_jsp_string15;
  private final static char []_jsp_string11;
  private final static char []_jsp_string8;
  static {
    _jsp_string5 = "?\")) {\r\n			form.action = \"EditCluster.jsp?edit=1\";\r\n			form.method = \"post\";\r\n			form.submit();\r\n			return true;\r\n		}else\r\n			return false;\r\n	} else {\r\n		if(form.Name.value == \"\") {\r\n			alert(\"".toCharArray();
    _jsp_string4 = "\r\n<script language = \"javascript\">\r\nfunction confirmEdit(form)\r\n{\r\n	if(form.Name.value != \"\") {\r\n		if(confirm(\"".toCharArray();
    _jsp_string10 = "\r\n	<script>\r\n  		alert(\"".toCharArray();
    _jsp_string1 = "\r\n<style type=\"text/css\">\r\n<!--\r\nbody {\r\n	background-color: #eaebf4;\r\n}\r\n-->\r\n</style></head>\r\n\r\n<body style=\"background-color: #DEE3EF\">\r\n".toCharArray();
    _jsp_string17 = "\r\n</body>\r\n</html>".toCharArray();
    _jsp_string16 = "\" onClick=\"cancelEdit()\">\r\n			</font> </p>\r\n    </blockquote>\r\n  </blockquote>\r\n</form>\r\n".toCharArray();
    _jsp_string9 = "\r\n						<script>\r\n						alert(\"Cluster edited successfully\");\r\n						opener.location.href = 'Cluster.jsp';\r\n						window.close();\r\n						</script>\r\n						".toCharArray();
    _jsp_string0 = "\r\n\r\n\r\n\r\n\r\n\r\n<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n<html>\r\n<head>\r\n\r\n<title>EditCluster</title>\r\n\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n".toCharArray();
    _jsp_string7 = " <font size=\"2\">\r\n   \r\n<script>\r\n	parent.location.href = \"index.jsp\";\r\n</script>\r\n".toCharArray();
    _jsp_string12 = "	\r\n\r\n\r\n<form name=\"EditCluster\" method=\"post\">\r\n  <table border=\"0\" width=\"480\" height=\"141\" font span style='font-size:10.0pt;font-family:Arial'>\r\n    <tr>\r\n      <td width=\"70\" height=\"33\">".toCharArray();
    _jsp_string6 = "\");\r\n			form.Name.focus();\r\n		}\r\n		return false;\r\n	}\r\n	return true;\r\n}\r\n//Edited by Xuehai 02 Jun 2011. Remove 'void'. Enable to run on different browers like Chrome&Firefox.\r\n//void function cancelAdd()\r\nfunction cancelEdit()\r\n{\r\n	window.close();\r\n	//opener.location.href = 'Competency.jsp';\r\n}\r\n</script>\r\n\r\n".toCharArray();
    _jsp_string14 = "\"size=\"50\" maxlength=\"50\">\r\n	  </td>\r\n    </tr>\r\n    <tr>\r\n      <td width=\"82\" height=\"12\"></td>\r\n      <td width=\"10\" height=\"12\"></td>\r\n      <td width=\"303\" height=\"12\"></td>\r\n    </tr>\r\n   \r\n  </table>\r\n  <blockquote>\r\n    <blockquote>\r\n      <p>\r\n		<font face=\"Arial\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n		</font>		<font face=\"Arial\" span style=\"font-size: 10.0pt; font-family: Arial\">		\r\n	        <input type=\"button\" name=\"Submit\" value=\"".toCharArray();
    _jsp_string3 = "    \r\n".toCharArray();
    _jsp_string2 = "\r\n".toCharArray();
    _jsp_string13 = "</td>\r\n      <td width=\"10\" height=\"33\">&nbsp;</td>\r\n      <td width=\"400\" height=\"33\">\r\n    	<input name=\"Name\" type=\"text\"  style='font-size:10.0pt;font-family:Arial' id=\"Name\" value=\"".toCharArray();
    _jsp_string15 = "\" onClick=\"return confirmEdit(this.form)\"></font><font span style='font-family:Arial'>\r\n		</font>\r\n			<font face=\"Arial\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n        </font>\r\n		<font face=\"Arial\" span style=\"font-size: 10.0pt; font-family: Arial\">\r\n			<input name=\"Cancel\" type=\"button\" id=\"Cancel\" value=\"".toCharArray();
    _jsp_string11 = "\");\r\n		window.location.href='EditCluster.jsp';\r\n	</script>\r\n".toCharArray();
    _jsp_string8 = "\r\n\r\n".toCharArray();
  }
}
