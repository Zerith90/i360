/*
 * JSP generated by Resin Professional 4.0.36 (built Fri, 26 Apr 2013 03:33:09 PDT)
 */

package _jsp;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.sql.*;
import java.io.*;
import CP_Classes.SurveyResult;

public class _assigntr_0ratermenu__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.Database db;
    synchronized (pageContext.getSession()) {
      db = (CP_Classes.Database) pageContext.getSession().getAttribute("db");
      if (db == null) {
        db = new CP_Classes.Database();
        pageContext.getSession().setAttribute("db", db);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.AssignTarget_Rater assignTR;
    synchronized (pageContext.getSession()) {
      assignTR = (CP_Classes.AssignTarget_Rater) pageContext.getSession().getAttribute("assignTR");
      if (assignTR == null) {
        assignTR = new CP_Classes.AssignTarget_Rater();
        pageContext.getSession().setAttribute("assignTR", assignTR);
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
    out.print((trans.tslt("Delete Rater?")));
    out.write(_jsp_string5, 0, _jsp_string5.length);
    

//response.setHeader("Pragma", "no-cache");
//response.setHeader("Cache-Control", "no-cache");
//response.setDateHeader("expires", 0);

String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {
    out.write(_jsp_string6, 0, _jsp_string6.length);
      } 
  else 
  { 
	  
if(request.getParameter("del") != null)
{
		//Changed by Ha 19/06/08 to automatically calculated again when a rater is removed
		SurveyResult SR = new SurveyResult();
		int targetID = SR.TargetID(assignTR.get_selectedAssID());
		boolean bIsDeleted = assignTR.delRater(assignTR.get_selectedAssID(), logchk.getPKUser());
	

    out.write(_jsp_string7, 0, _jsp_string7.length);
    
	
		SR.updateCalculationStatus(targetID, CE_Survey.getSurvey_ID(),0);
		if (!SR.isAllRaterRated(CE_Survey.getSurvey_ID(),assignTR.getGroupID(), targetID)) 
			SR.CalculateStatus(targetID,CE_Survey.getSurvey_ID(), assignTR.getDivID(), assignTR.getDeptID(), assignTR.getGroupID(), 1);	
		else    	
		    SR.CalculateStatus(targetID,CE_Survey.getSurvey_ID(), assignTR.getDivID(), assignTR.getDeptID(), assignTR.getGroupID(), 0);	
		//End of change made by Ha 19/06/08
		assignTR.set_selectedTargetID(0);
		assignTR.set_selectedAssID(0);
}

    out.write(_jsp_string8, 0, _jsp_string8.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("AssignTR_RaterMenu.jsp"), 4182138435703038003L, false);
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

  private final static char []_jsp_string4;
  private final static char []_jsp_string7;
  private final static char []_jsp_string9;
  private final static char []_jsp_string5;
  private final static char []_jsp_string0;
  private final static char []_jsp_string3;
  private final static char []_jsp_string6;
  private final static char []_jsp_string2;
  private final static char []_jsp_string1;
  private final static char []_jsp_string8;
  static {
    _jsp_string4 = "\r\n</head>\r\n<script language=\"JavaScript\">\r\n<!--\r\nfunction FP_swapImg() {//v1.0\r\n var doc=document,args=arguments,elm,n; doc.$imgSwaps=new Array(); for(n=2; n<args.length;\r\n n+=2) { elm=FP_getObjectByID(args[n]); if(elm) { doc.$imgSwaps[doc.$imgSwaps.length]=elm;\r\n elm.$src=elm.src; elm.src=args[n+1]; } }\r\n}\r\n\r\nfunction FP_preloadImgs() {//v1.0\r\n var d=document,a=arguments; if(!d.FP_imgs) d.FP_imgs=new Array();\r\n for(var i=0; i<a.length; i++) { d.FP_imgs[i]=new Image; d.FP_imgs[i].src=a[i]; }\r\n}\r\n\r\nfunction FP_getObjectByID(id,o) {//v1.0\r\n var c,el,els,f,m,n; if(!o)o=document; if(o.getElementById) el=o.getElementById(id);\r\n else if(o.layers) c=o.layers; else if(o.all) el=o.all[id]; if(el) return el;\r\n if(o.id==id || o.name==id) return o; if(o.childNodes) c=o.childNodes; if(c)\r\n for(n=0; n<c.length; n++) { el=FP_getObjectByID(id,c[n]); if(el) return el; }\r\n f=o.forms; if(f) for(n=0; n<f.length; n++) { els=f[n].elements;\r\n for(m=0; m<els.length; m++){ el=FP_getObjectByID(id,els[n]); if(el) return el; } }\r\n return null;\r\n}\r\n// -->\r\n\r\n\r\nfunction del() \r\n{\r\n	if(confirm(\"".toCharArray();
    _jsp_string7 = "	\r\n		<script>\r\n		alert(\"Deleted successfully\");\r\n		window.close();\r\n		opener.location.href ='AssignTarget_Rater.jsp';\r\n		</script>\r\n".toCharArray();
    _jsp_string9 = "\r\n</body>\r\n</html>\r\n".toCharArray();
    _jsp_string5 = "\"))\r\n	{\r\n		//var myWindow=window.open('AssignTR_RaterMenu.jsp?del=1','windowRef','scrollbars=no, width=350, height=550');\r\n		var myWindow=window.open('AssignTR_RaterMenu.jsp?del=1','windowRef','scrollbars=no, width=150, height=50');\r\n    	//myWindow.location.href = 'AssignTR_RaterMenu.jsp?del=1';\r\n	}\r\n	else\r\n	{\r\n		window.close();\r\n		opener.location.href ='AssignTarget_Rater.jsp';\r\n	}\r\n}\r\n\r\nfunction edit()\r\n{\r\n	window.close();\r\n	opener.location.href ='AssignTR_RaterMenu_EditRater.jsp';\r\n	}\r\n</script>\r\n<body onload=\"FP_preloadImgs(/*url*/'images/button2B.jpg',/*url*/'images/button2C.jpg')\" bgcolor=\"#FFFFCC\">\r\n".toCharArray();
    _jsp_string0 = "  \r\n                 \r\n".toCharArray();
    _jsp_string3 = "\r\n<html>\r\n<head>\r\n\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n".toCharArray();
    _jsp_string6 = " <font size=\"2\">\r\n   \r\n    	    	    	<script>\r\n	parent.location.href = \"index.jsp\";\r\n</script>\r\n".toCharArray();
    _jsp_string2 = "  \r\n".toCharArray();
    _jsp_string1 = "\r\n".toCharArray();
    _jsp_string8 = "\r\n<form name=\"AssignTR_RaterMenu\" action=\"AssignTR_RaterMenu.jsp\" method=\"post\">\r\n<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\r\n	<tr>\r\n		<td>\r\n		<a onclick=\"del()\">\r\n		<img border=\"0\" id=\"img1\" src=\"images/button2A.jpg\" height=\"20\" width=\"100\" alt=\"Delete Rater\" onmouseover=\"FP_swapImg(1,0,/*id*/'img1',/*url*/'images/button2B.jpg')\" onmouseout=\"FP_swapImg(0,0,/*id*/'img1',/*url*/'images/button2A.jpg')\" onmousedown=\"FP_swapImg(1,0,/*id*/'img1',/*url*/'images/button2C.jpg')\" onmouseup=\"FP_swapImg(0,0,/*id*/'img1',/*url*/'images/button2B.jpg')\" fp-style=\"fp-btn: Border Left 1\" fp-title=\"Delete Rater\"></td>\r\n		</a>\r\n	</tr>\r\n	<tr>\r\n		<td>&nbsp;</td>\r\n	</tr>\r\n	<tr>\r\n		<td>\r\n		<p align=\"left\">\r\n		\r\n		<a onclick=\"edit()\">\r\n		<img border=\"0\" id=\"img2\" src=\"images\\button4A.jpg\" height=\"20\" width=\"100\" alt=\"Edit Rater\" fp-style=\"fp-btn: Border Left 1\" fp-title=\"Edit Rater\" onmouseover=\"FP_swapImg(1,0,/*id*/'img2',/*url*/'images/button4B.jpg')\" onmouseout=\"FP_swapImg(0,0,/*id*/'img2',/*url*/'images/button4A.jpg')\" onmousedown=\"FP_swapImg(1,0,/*id*/'img2',/*url*/'images/button4C.jpg')\" onmouseup=\"FP_swapImg(0,0,/*id*/'img2',/*url*/'images/button4B.jpg')\"></a></td>\r\n	</tr>\r\n</table>\r\n</form>\r\n".toCharArray();
  }
}