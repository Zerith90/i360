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
import CP_Classes.vo.votblSurveyRelationSpecific;
import CP_Classes.vo.votblAssignment;
import java.lang.String;
import CP_Classes.SurveyResult;
import CP_Classes.vo.voGroup;
import CP_Classes.vo.voDepartment;
import CP_Classes.vo.voDivision;
import CP_Classes.vo.votblRelationHigh;
import CP_Classes.vo.votblRelationSpecific;

public class _assigntr_0ratermenu_0editrater__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.User user;
    synchronized (pageContext.getSession()) {
      user = (CP_Classes.User) pageContext.getSession().getAttribute("user");
      if (user == null) {
        user = new CP_Classes.User();
        pageContext.getSession().setAttribute("user", user);
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
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.Department Department;
    synchronized (pageContext.getSession()) {
      Department = (CP_Classes.Department) pageContext.getSession().getAttribute("Department");
      if (Department == null) {
        Department = new CP_Classes.Department();
        pageContext.getSession().setAttribute("Department", Department);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.Division Division;
    synchronized (pageContext.getSession()) {
      Division = (CP_Classes.Division) pageContext.getSession().getAttribute("Division");
      if (Division == null) {
        Division = new CP_Classes.Division();
        pageContext.getSession().setAttribute("Division", Division);
      }
    }
    out.write(_jsp_string3, 0, _jsp_string3.length);
    CP_Classes.Group Group;
    synchronized (pageContext.getSession()) {
      Group = (CP_Classes.Group) pageContext.getSession().getAttribute("Group");
      if (Group == null) {
        Group = new CP_Classes.Group();
        pageContext.getSession().setAttribute("Group", Group);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.Organization Orgs;
    synchronized (pageContext.getSession()) {
      Orgs = (CP_Classes.Organization) pageContext.getSession().getAttribute("Orgs");
      if (Orgs == null) {
        Orgs = new CP_Classes.Organization();
        pageContext.getSession().setAttribute("Orgs", Orgs);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.RaterRelation RR;
    synchronized (pageContext.getSession()) {
      RR = (CP_Classes.RaterRelation) pageContext.getSession().getAttribute("RR");
      if (RR == null) {
        RR = new CP_Classes.RaterRelation();
        pageContext.getSession().setAttribute("RR", RR);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.SurveyRelationSpecific SurveyRelationSpecific;
    synchronized (pageContext.getSession()) {
      SurveyRelationSpecific = (CP_Classes.SurveyRelationSpecific) pageContext.getSession().getAttribute("SurveyRelationSpecific");
      if (SurveyRelationSpecific == null) {
        SurveyRelationSpecific = new CP_Classes.SurveyRelationSpecific();
        pageContext.getSession().setAttribute("SurveyRelationSpecific", SurveyRelationSpecific);
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

assignTR.setGroupID(0);


if(request.getParameter("change") != null)
{
	int var2 = new Integer(request.getParameter("change")).intValue();
	assignTR.set_selectedRaterID(var2);
}

if(request.getParameter("edit") != null)
{
	
	boolean isEdited;
	int count = 0;
	
    String code = request.getParameter("edit");
    System.out.println("java" +  code);
    isEdited = assignTR.editAssignment(assignTR.get_selectedAssID(), code, logchk.getPKUser());
    
    if(isEdited){
    	
    
			
	assignTR.set_selectedTargetID(0);
	assignTR.set_selectedAssID(0);

    out.write(_jsp_string7, 0, _jsp_string7.length);
    

    }
    else{
    	assignTR.set_selectedTargetID(0);
    	assignTR.set_selectedAssID(0);
    
    out.write(_jsp_string8, 0, _jsp_string8.length);
    
    }
}

if(request.getParameter("assID") != null)
{	 
	int assignmentid = Integer.parseInt(request.getParameter("assID"));
	assignTR.set_selectedAssID(assignmentid);								
} 

if(request.getParameter("targetID") != null)
{	 
	int targetID = Integer.parseInt(request.getParameter("targetID"));
	assignTR.set_selectedTargetID(targetID);							
} 

if(request.getParameter("close") != null)
{	
	assignTR.set_selectedTargetID(0);
	assignTR.set_selectedAssID(0);
	
    out.write(_jsp_string9, 0, _jsp_string9.length);
    
}

/************************************************** ADDING TOGGLE FOR SORTING PURPOSE *************************************************/

	int toggle = assignTR.getToggle();	//0=asc, 1=desc
	int type = 1; //1=name, 2=origin		
			
	if(request.getParameter("name") != null)
	{	 
		if(toggle == 0)
			toggle = 1;
		else
			toggle = 0;
		
		assignTR.setToggle(toggle);
		
		type = Integer.parseInt(request.getParameter("name"));			 
		assignTR.setSortType(type);									
	} 

/*********************************************************END ADDING TOGGLE FOR SORTING PURPOSE *************************************/


    out.write(_jsp_string10, 0, _jsp_string10.length);
    out.print(( trans.tslt("Edit Rater") ));
    out.write(_jsp_string11, 0, _jsp_string11.length);
    out.print(( trans.tslt("Selected Survey") ));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    	
	
	String SurveyName= CE_Survey.getSurveyName(CE_Survey.getSurvey_ID());
	

    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((SurveyName));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    
	String [] TargetDetail = new String[13];
    int assignmentID = assignTR.get_selectedAssID();
    votblAssignment assignment = assignTR.getAssignment(assignmentID);
	TargetDetail = user.getUserDetail(assignment.getTargetLoginID(),assignTR.get_NameSequence());
	
	
	

    out.write(_jsp_string15, 0, _jsp_string15.length);
    out.print(( trans.tslt("Target Family Name") ));
    out.write(_jsp_string16, 0, _jsp_string16.length);
    out.print((TargetDetail[0]));
    out.write(_jsp_string17, 0, _jsp_string17.length);
    out.print(( trans.tslt("Target Other Name") ));
    out.write(_jsp_string18, 0, _jsp_string18.length);
    out.print((TargetDetail[1]));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print(( trans.tslt("Rater") ));
    out.write(_jsp_string20, 0, _jsp_string20.length);
    out.print(( trans.tslt("Family Name") ));
    out.write(_jsp_string21, 0, _jsp_string21.length);
    out.print(( trans.tslt("Other Name") ));
    out.write(_jsp_string22, 0, _jsp_string22.length);
    out.print(( trans.tslt("Login Name") ));
    out.write(_jsp_string23, 0, _jsp_string23.length);
    out.print(( trans.tslt("Relation") ));
    out.write(_jsp_string24, 0, _jsp_string24.length);
    
	

	String RaterDetail[] = new String[13];
	
    RaterDetail = user.getUserDetail(assignment.getRaterLoginID(), assignTR.get_NameSequence());
    
    int relationSpec = assignment.getRTSpecific();
    int relationHigh = assignment.getRTRelation();
	
    out.write(_jsp_string25, 0, _jsp_string25.length);
    out.print((RaterDetail[0]));
    out.write(_jsp_string26, 0, _jsp_string26.length);
    out.print((RaterDetail[1]));
    out.write(_jsp_string27, 0, _jsp_string27.length);
    out.print((RaterDetail[2]));
    out.write(_jsp_string28, 0, _jsp_string28.length);
    
		Vector vRelHigh = RR.getAllRelationHigh(assignment.getRaterLoginID(), assignTR.get_selectedTargetID());
	
		for(int k=0; k<vRelHigh.size();k++)
		{
			votblRelationHigh vo = (votblRelationHigh)vRelHigh.elementAt(k);
			int RelID = vo.getRelationID();
			
			
				String RelHigh = vo.getRelationHigh(); 
				
				/*
				*Change(s): Set the current relation as selected
				*Reason(s): To show the current rater relation
				*Updated by: Liu Taichen
				*Updated on: 6 June 2012
				*/
				if((relationSpec ==0) && (relationHigh == RelID)){
				
    out.write(_jsp_string29, 0, _jsp_string29.length);
    out.print(("High"+RelID));
    out.write(_jsp_string30, 0, _jsp_string30.length);
    out.print((RelHigh));
    out.write(_jsp_string31, 0, _jsp_string31.length);
    	
				}
				else
				{
					
    out.write(_jsp_string32, 0, _jsp_string32.length);
    out.print(("High"+RelID));
    out.write('>');
    out.print((RelHigh));
    out.write(_jsp_string33, 0, _jsp_string33.length);
    	
					
				}
				/*
				*Change(s): Use class SurveyRelationSpecific to manage relation specifics
				*Reason(s): To associate relation specific to survey instead of organization
				*Updated by: Liu Taichen
				*Updated on: 5 June 2012
				*/
				//Vector vRelSpecific = RR.getAllRelationSpecific(logchk.getOrg(),PKUser, assignTR.get_selectedTargetID());
				Vector vRelSpecific = SurveyRelationSpecific.getRelationSpecific(RelID, CE_Survey.getSurvey_ID());
				for(int i=0; i<vRelSpecific.size();i++)
				{
					votblSurveyRelationSpecific so = (votblSurveyRelationSpecific)vRelSpecific.elementAt(i);
					int SpecID = so.getSpecificID();
					String RelSpec = so.getRelationSpecific();
					
					if(relationSpec == SpecID){
			
    out.write(_jsp_string34, 0, _jsp_string34.length);
    out.print(("Spec"+SpecID));
    out.write(_jsp_string30, 0, _jsp_string30.length);
    out.print((RelSpec));
    out.write(_jsp_string33, 0, _jsp_string33.length);
    		
					}
					else
					{
						
    out.write(_jsp_string35, 0, _jsp_string35.length);
    out.print(("Spec"+SpecID));
    out.write('>');
    out.print((RelSpec));
    out.write(_jsp_string31, 0, _jsp_string31.length);
    		
						
					}
		//System.out.println("RIANTONEW 'High" + RelID + ", " + RelHigh + "'");
		
		}
		
	

		}
	
    out.write(_jsp_string36, 0, _jsp_string36.length);
    	
//}

    out.write(_jsp_string37, 0, _jsp_string37.length);
    out.print(( trans.tslt("Edit") ));
    out.write(_jsp_string38, 0, _jsp_string38.length);
    out.print(( trans.tslt("Cancel") ));
    out.write(_jsp_string39, 0, _jsp_string39.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("AssignTR_RaterMenu_EditRater.jsp"), 6501635496375048926L, false);
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

  private final static char []_jsp_string7;
  private final static char []_jsp_string15;
  private final static char []_jsp_string20;
  private final static char []_jsp_string36;
  private final static char []_jsp_string35;
  private final static char []_jsp_string10;
  private final static char []_jsp_string33;
  private final static char []_jsp_string38;
  private final static char []_jsp_string32;
  private final static char []_jsp_string31;
  private final static char []_jsp_string28;
  private final static char []_jsp_string34;
  private final static char []_jsp_string6;
  private final static char []_jsp_string30;
  private final static char []_jsp_string21;
  private final static char []_jsp_string25;
  private final static char []_jsp_string18;
  private final static char []_jsp_string0;
  private final static char []_jsp_string26;
  private final static char []_jsp_string5;
  private final static char []_jsp_string14;
  private final static char []_jsp_string39;
  private final static char []_jsp_string12;
  private final static char []_jsp_string8;
  private final static char []_jsp_string24;
  private final static char []_jsp_string37;
  private final static char []_jsp_string4;
  private final static char []_jsp_string23;
  private final static char []_jsp_string22;
  private final static char []_jsp_string3;
  private final static char []_jsp_string1;
  private final static char []_jsp_string11;
  private final static char []_jsp_string16;
  private final static char []_jsp_string13;
  private final static char []_jsp_string2;
  private final static char []_jsp_string27;
  private final static char []_jsp_string19;
  private final static char []_jsp_string9;
  private final static char []_jsp_string29;
  private final static char []_jsp_string17;
  static {
    _jsp_string7 = "\r\n	<script>\r\n	    alert(\"Rater Relation successfully edited.\")\r\n		location.href = \"AssignTarget_Rater.jsp\";\r\n	</script>\r\n".toCharArray();
    _jsp_string15 = "	\r\n		<td width=\"174\" height=\"25\" style=\"border-right-style:solid; border-right-width:1px; border-top-style:solid; border-top-width:1px; border-bottom-style:solid; border-bottom-width:1px\" align=\"left\" bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\" colspan=\"2\">\r\n		<font size=\"2\">\r\n   \r\n    	<font face=\"Arial\" style=\"font-weight:700\" size=\"2\">\r\n		&nbsp;".toCharArray();
    _jsp_string20 = "</font></b></td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"28\" align=\"center\" bgcolor=\"#000080\">\r\n          &nbsp		\r\n		</td>\r\n		<td width=\"146\" align=\"center\" bgcolor=\"#000080\">\r\n		<b>\r\n		<a href=\"AssignTR_TargetMenu_AddRater.jsp?name=1\">\r\n		<font style='font-family:Arial;color:white' size=\"2\">\r\n		<u>".toCharArray();
    _jsp_string36 = "\r\n		\r\n		</select>\r\n		</font>\r\n	</td>\r\n</tr>\r\n".toCharArray();
    _jsp_string35 = "\r\n						\r\n						<option value=".toCharArray();
    _jsp_string10 = "\r\n<form name=\"AssignTR_RaterMenu_EditRater\" action=\"AssignTR_RaterMenu_EditRater.jsp\" method=\"post\">\r\n<table border=\"0\" width=\"610\">\r\n	<tr>\r\n		<td><b><font face=\"Arial\" size=\"2\"><font color=\"#000080\">".toCharArray();
    _jsp_string33 = "</option>\r\n			".toCharArray();
    _jsp_string38 = "\" name=\"btnEdit\" style=\"float: right\" onclick=\"edit(this.form, this.form.selRelation )\"></td>\r\n		<td>\r\n		<input type=\"button\" value=\"".toCharArray();
    _jsp_string32 = "		\r\n					<option value=".toCharArray();
    _jsp_string31 = "</option>\r\n				".toCharArray();
    _jsp_string28 = "</font></td>\r\n		<td width=\"165\" align=\"center\">\r\n		<font face=\"Arial\" size=\"2\">\r\n\r\n		<select size=\"1\" name=selRelation>\r\n".toCharArray();
    _jsp_string34 = "\r\n							\r\n					<option value=".toCharArray();
    _jsp_string6 = " <font size=\"2\">\r\n   \r\n    <script>\r\n	parent.location.href = \"index.jsp\";\r\n	</script>\r\n".toCharArray();
    _jsp_string30 = " selected>".toCharArray();
    _jsp_string21 = "</u></font></a></b></td>\r\n		<td width=\"171\" align=\"center\" bgcolor=\"#000080\">\r\n		<b>\r\n		<a href=\"AssignTR_TargetMenu_AddRater.jsp?name=2\"><font style='font-family:Arial;color:white' size=\"2\">\r\n		<u>".toCharArray();
    _jsp_string25 = "	\r\n	<tr onMouseOver = \"this.bgColor = '#99ccff'\"\r\n    	onMouseOut = \"this.bgColor = '#FFFFcc'\">\r\n		<td width=\"28\" align=\"center\">\r\n		&nbsp\r\n		</td>\r\n		<td width=\"146\" align=\"center\">\r\n		<font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string18 = ":</font></td>\r\n		<td style=\"border-left-style:solid; border-left-width:1px; border-top-style:solid; border-top-width:1px; border-bottom-style:solid; border-bottom-width:1px\" bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\" colspan=\"2\" width=\"448\">\r\n		<font face=\"Arial\" size=\"2\">&nbsp;".toCharArray();
    _jsp_string0 = "  \r\n                 \r\n".toCharArray();
    _jsp_string26 = "</font></td>\r\n		<td width=\"171\" align=\"center\">\r\n		<font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string5 = "\r\n</head>\r\n<SCRIPT LANGUAGE=JAVASCRIPT>\r\n\r\nfunction check(field)\r\n{\r\n	var isValid = 0;\r\n	var clickedValue = 0;\r\n	//check whether any checkbox selected\r\n	if( field == null ) {\r\n		isValid = 2;\r\n	\r\n	} else {\r\n\r\n		if(isNaN(field.length) == false) {\r\n			for (i = 0; i < field.length; i++)\r\n				if(field[i].checked) {\r\n					clickedValue = field[i].value;\r\n					isValid = 1;\r\n				}\r\n		}else {		\r\n			if(field.checked) {\r\n				clickedValue = field.value;\r\n				isValid = 1;\r\n			}\r\n				\r\n		}\r\n	}\r\n	\r\n	if(isValid == 1)\r\n		return clickedValue;\r\n	else if(isValid == 0)\r\n		alert(\"No record selected\");\r\n	else if(isValid == 2)\r\n		alert(\"No record available\");\r\n	\r\n	isValid = 0;\r\n		\r\n}\r\n\r\nfunction closeME(form)\r\n{ \r\n	form.action = \"AssignTR_RaterMenu_EditRater.jsp?close=1\";\r\n	form.method=\"post\";\r\n	form.submit();\r\n}\r\nfunction refresh(form, field)\r\n{\r\n	form.action = \"AssignTR_RaterMenu_EditRater.jsp?refresh=\"+field.value;\r\n	form.method=\"post\";\r\n	form.submit();	\r\n}\r\n\r\nfunction edit(form, field)\r\n{\r\n		alert(field.value);\r\n	  if (confirm(\"Edit Rater?\"))\r\n	    {\r\n			form.action=\"AssignTR_RaterMenu_EditRater.jsp?edit=\" +field.value;\r\n			form.method=\"post\";\r\n			form.submit();		\r\n		}\r\n	}\r\n\r\n\r\n\r\nfunction raterChange(form,field)\r\n{\r\n	\r\n	form.action=\"AssignTR_RaterMenu_EditRater.jsp?change=\"+field.value;\r\n	form.method=\"post\";\r\n	form.submit();	\r\n}\r\n\r\nfunction checkedAll(form, field, checkAll)\r\n{	\r\n	if(checkAll.checked == true) \r\n		for(var i=0; i<field.length; i++)\r\n			field[i].checked = true;\r\n	else \r\n		for(var i=0; i<field.length; i++)\r\n			field[i].checked = false;	\r\n}\r\n\r\n\r\n\r\n</SCRIPT>\r\n<body>\r\n".toCharArray();
    _jsp_string14 = "\r\n\r\n\r\n\r\n</font>\r\n\r\n</td>\r\n	</tr>\r\n	<tr>\r\n".toCharArray();
    _jsp_string39 = "\" name=\"btnCancel\" style=\"float: right\" onclick=\"closeME(this.form)\"></td>\r\n	</tr>\r\n	\r\n</table>\r\n</form>\r\n\r\n</body>\r\n</html>".toCharArray();
    _jsp_string12 = ":</font></td>\r\n		<td style=\"border-left-style:solid; border-left-width:1px; border-bottom-style:solid; border-bottom-width:1px; border-top-style:solid; border-top-width:1px\" height=\"22\" bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\" colspan=\"2\" width=\"448\">\r\n<font face=\"Arial\" size=\"2\">\r\n".toCharArray();
    _jsp_string8 = "\r\n    	<script>\r\n    	    alert(\"Rater Relation not edited.\")\r\n    		location.href = \"AssignTarget_Rater.jsp\";\r\n    	</script>\r\n    ".toCharArray();
    _jsp_string24 = "</font></b></td>\r\n	</tr>\r\n".toCharArray();
    _jsp_string37 = "\r\n</table>\r\n<p></p>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"538\">\r\n		<input type=\"button\" value=\"".toCharArray();
    _jsp_string4 = "\r\n\r\n\r\n\r\n\r\n\r\n<html>\r\n<head>\r\n\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n".toCharArray();
    _jsp_string23 = "</u></font></a></b></td>\r\n		<td width=\"165\" align=\"center\" bgcolor=\"#000080\">\r\n		<b><font face=\"Arial\" size=\"2\" color=\"#FFFFFF\">".toCharArray();
    _jsp_string22 = "</u></font></a></b></td>\r\n		<td width=\"165\" align=\"center\" bgcolor=\"#000080\">\r\n		<b>\r\n		<a href=\"AssignTR_TargetMenu_AddRater.jsp?name=3\"><font style='font-family:Arial;color:white' size=\"2\">\r\n		<u>".toCharArray();
    _jsp_string3 = "                  \r\n".toCharArray();
    _jsp_string1 = "\r\n".toCharArray();
    _jsp_string11 = "</font>\r\n		</font></b></td>\r\n	</tr>\r\n	<tr>\r\n		<td>&nbsp;</td>\r\n	</tr>\r\n</table>\r\n<table border=\"1\" width=\"610\" bgcolor=\"#FFFFFF\" bordercolor=\"#FFFFFF\" style=\"border-left-width: 0px; border-right-width: 0px\" cellspacing=\"1\">\r\n	<tr>\r\n		<td width=\"174\" style=\"border-right-style:solid; border-right-width:1px; border-bottom-style:solid; border-bottom-width:1px; border-top-style:solid; border-top-width:1px\" height=\"22\" align=\"left\" bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\" colspan=\"2\">\r\n		<font size=\"2\">\r\n   \r\n    	<font face=\"Arial\" style=\"font-weight:700\" size=\"2\">&nbsp;\r\n    	".toCharArray();
    _jsp_string16 = ":</font></td>\r\n		<td height=\"25\" style=\"border-left-style:solid; border-left-width:1px; border-top-style:solid; border-top-width:1px; border-bottom-style:solid; border-bottom-width:1px\" bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\" colspan=\"2\" width=\"448\">\r\n		<font face=\"Arial\" size=\"2\">&nbsp;".toCharArray();
    _jsp_string13 = "\r\n	&nbsp".toCharArray();
    _jsp_string2 = "   \r\n".toCharArray();
    _jsp_string27 = "</font></td>\r\n		<td width=\"165\" align=\"center\">\r\n		<font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string19 = "</font></td>\r\n	</tr>\r\n</table>\r\n<p></p>\r\n\r\n<table border=\"1\" width=\"610\" bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\">\r\n	<tr>\r\n		<td colspan=\"5\" bgcolor=\"#000080\">\r\n		<p align=\"center\">\r\n		<b><font face=\"Arial\" size=\"2\" color=\"#FFFFFF\">".toCharArray();
    _jsp_string9 = "\r\n	<script>\r\n		location.href ='AssignTarget_Rater.jsp';\r\n	</script>\r\n".toCharArray();
    _jsp_string29 = "		\r\n						<option value=".toCharArray();
    _jsp_string17 = "</font></td>\r\n	</tr>\r\n	<tr>\r\n		<td width=\"174\" style=\"border-right-style:solid; border-right-width:1px; border-top-style:solid; border-top-width:1px; border-bottom-style:solid; border-bottom-width:1px\" align=\"left\" bgcolor=\"#FFFFCC\" bordercolor=\"#3399FF\" colspan=\"2\">\r\n		&nbsp;<font face=\"Arial\" style=\"font-weight:700\" size=\"2\">\r\n		".toCharArray();
  }
}