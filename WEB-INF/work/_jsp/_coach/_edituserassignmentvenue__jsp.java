/*
 * JSP generated by Resin Professional 4.0.36 (built Fri, 26 Apr 2013 03:33:09 PDT)
 */

package _jsp._coach;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import CP_Classes.vo.*;

public class _edituserassignmentvenue__jsp extends com.caucho.jsp.JavaPage
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

    // Author: Dai Yong in June 2013
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
    Coach.SessionSetup SessionSetup;
    synchronized (pageContext.getSession()) {
      SessionSetup = (Coach.SessionSetup) pageContext.getSession().getAttribute("SessionSetup");
      if (SessionSetup == null) {
        SessionSetup = new Coach.SessionSetup();
        pageContext.getSession().setAttribute("SessionSetup", SessionSetup);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    Coach.CoachDateGroup CoachDateGroup;
    synchronized (pageContext.getSession()) {
      CoachDateGroup = (Coach.CoachDateGroup) pageContext.getSession().getAttribute("CoachDateGroup");
      if (CoachDateGroup == null) {
        CoachDateGroup = new Coach.CoachDateGroup();
        pageContext.getSession().setAttribute("CoachDateGroup", CoachDateGroup);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    Coach.CoachDate CoachDate;
    synchronized (pageContext.getSession()) {
      CoachDate = (Coach.CoachDate) pageContext.getSession().getAttribute("CoachDate");
      if (CoachDate == null) {
        CoachDate = new Coach.CoachDate();
        pageContext.getSession().setAttribute("CoachDate", CoachDate);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    Coach.CoachSlotGroup CoachSlotGroup;
    synchronized (pageContext.getSession()) {
      CoachSlotGroup = (Coach.CoachSlotGroup) pageContext.getSession().getAttribute("CoachSlotGroup");
      if (CoachSlotGroup == null) {
        CoachSlotGroup = new Coach.CoachSlotGroup();
        pageContext.getSession().setAttribute("CoachSlotGroup", CoachSlotGroup);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    Coach.CoachVenue CoachVenue;
    synchronized (pageContext.getSession()) {
      CoachVenue = (Coach.CoachVenue) pageContext.getSession().getAttribute("CoachVenue");
      if (CoachVenue == null) {
        CoachVenue = new Coach.CoachVenue();
        pageContext.getSession().setAttribute("CoachVenue", CoachVenue);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    	
	Vector venues=CoachVenue.getAllCoachVenue();
	int userAssignmentPK = 0;
	int venuePK;
	if (request.getParameter("UserAssignment") != null) {
		userAssignmentPK = Integer.parseInt(request.getParameter("UserAssignment"));
		SessionSetup.setSelectedUserAssignment(userAssignmentPK);
	} else {
		userAssignmentPK = SessionSetup.getSelectedUserAssignment();
	}
	if (request.getParameter("setVenue") != null) {
		venuePK = Integer.parseInt(request.getParameter("selVenue"));
		SessionSetup.setSelectedVenue(venuePK);
		System.out.println("venue set to: "+venuePK);
	}else {
		venuePK = SessionSetup.getSelectedVenue();
	}
	if (request.getParameter("save") != null) {	
			SessionSetup.updateUserAssignmentVenue(userAssignmentPK, SessionSetup.getSelectedVenue());
			System.out.println("update session venue");
		
		
    out.write(_jsp_string3, 0, _jsp_string3.length);
    
	}
	
	
    out.write(_jsp_string4, 0, _jsp_string4.length);
    
							for (int i = 0; i < venues.size(); i++) {
								voCoachVenue venue = (voCoachVenue) venues.elementAt(i);
								int currentVenuePK = venue.getVenuePK();
								String venueAddress1 = venue.getVenue1();
								if (SessionSetup.getSelectedVenue() == currentVenuePK) {
						
    out.write(_jsp_string5, 0, _jsp_string5.length);
    out.print((currentVenuePK));
    out.write(_jsp_string6, 0, _jsp_string6.length);
    out.print((venueAddress1));
    out.write(_jsp_string7, 0, _jsp_string7.length);
    
								} else {
							
    out.write(_jsp_string8, 0, _jsp_string8.length);
    out.print((currentVenuePK));
    out.write('>');
    out.print((venueAddress1));
    out.write(_jsp_string7, 0, _jsp_string7.length);
    
								}
							}
							
    out.write(_jsp_string9, 0, _jsp_string9.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("Coach/EditUserAssignmentVenue.jsp"), 5383380802249393064L, false);
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

  private final static char []_jsp_string1;
  private final static char []_jsp_string8;
  private final static char []_jsp_string4;
  private final static char []_jsp_string7;
  private final static char []_jsp_string5;
  private final static char []_jsp_string6;
  private final static char []_jsp_string0;
  private final static char []_jsp_string3;
  private final static char []_jsp_string2;
  private final static char []_jsp_string9;
  static {
    _jsp_string1 = "\r\n	".toCharArray();
    _jsp_string8 = "\r\n						\r\n						<option value=".toCharArray();
    _jsp_string4 = "\r\n	\r\n	<div align=\"center\">\r\n	<form>\r\n		<table width=\"300\">\r\n		<p>\r\n				<b><font color=\"#000080\" size=\"2\" face=\"Arial\">User Assignment Venue Management</font></b>\r\n				</p>\r\n				<tr>\r\n					<td width=\"500\" colspan=\"2\"><select size=\"1\"\r\n						name=\"selVenue\" onChange=\"setVenue(this.form)\">\r\n						<option value=0>Select a Coaching Venue</option>\r\n						".toCharArray();
    _jsp_string7 = "\r\n							".toCharArray();
    _jsp_string5 = "\r\n						<option value=".toCharArray();
    _jsp_string6 = " selected>".toCharArray();
    _jsp_string0 = "\r\n\r\n\r\n\r\n\r\n\r\n<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n<html>\r\n<head>\r\n\r\n<title>Change User Assignment Venue</title>\r\n\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody {\r\n	\r\n}\r\n-->\r\n</style>\r\n	".toCharArray();
    _jsp_string3 = "\r\n		<script type=\"text/javascript\">\r\n			opener.location.href = \"UserAssignment.jsp\";\r\n			window.close();\r\n		</script>\r\n		".toCharArray();
    _jsp_string2 = "\r\n	<script type=\"text/javascript\">\r\n	var x = parseInt(window.screen.width) / 2 - 240;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up\r\n	var y = parseInt(window.screen.height) / 2 - 115;  // the number 125 is the exact half of the height of the pop-up and so should be changed according to the size of the pop-up\r\n	\r\n		function proceed(){\r\n			opener.location.href='SelectCoach.jsp';\r\n			opener.location.reload(true);\r\n			window.close();\r\n		}\r\n		function setVenue(form){\r\n			form.action = \"EditUserAssignmentVenue.jsp?setVenue=1\";\r\n			form.method = \"post\";\r\n			form.submit();\r\n		}\r\n		function saveVenue(form){\r\n			form.action = \"EditUserAssignmentVenue.jsp?save=1\";\r\n			form.method = \"post\";\r\n			form.submit();\r\n		}\r\n		function cancelAdd()\r\n		{	\r\n			opener.location.href=\"UserAssignment.jsp\";\r\n			opener.location.reload(true);\r\n			window.close();\r\n		}	\r\n		function viewVenueDetail(form){\r\n			var value=form.selVenue.value;\r\n				if(value==\"0\"){\r\n				alert(\"Please select a venue\");\r\n				}else{\r\n				var myWindow=window.open('ViewUserAssignmentVenue.jsp?ViewDayGroup='+ value,'windowRef','scrollbars=yes, width=480, height=250');\r\n				var query = \"ViewUserAssignmentVenue.jsp?ViewDayGroup=\" + value;\r\n				myWindow.moveTo(x,y);\r\n	    		myWindow.location.href = query;\r\n				}\r\n		}\r\n	</script>\r\n</head>\r\n<body>\r\n\r\n\r\n\r\n".toCharArray();
    _jsp_string9 = "\r\n						\r\n				</select></td>\r\n					<td><input  type=\"button\" name=\"viewVenueDetails\" value=\"View Venue Details\" onclick=\"viewVenueDetail(this.form)\"></td>\r\n				</tr>\r\n			</table>\r\n		\r\n		<br>\r\n		<br>\r\n			<input  name=\"save\" type=\"button\" id=\"Save\" value=\"Save\" onClick=\"saveVenue(this.form)\">\r\n			<input name=\"Cancel\" type=\"button\" id=\"Cancel\" value=\"Close\" onClick=\"cancelAdd()\">		\r\n		</form>\r\n</div>\r\n</body>\r\n</html>".toCharArray();
  }
}
