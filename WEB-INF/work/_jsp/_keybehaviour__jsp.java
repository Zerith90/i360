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
import CP_Classes.Competency;
import CP_Classes.KeyBehaviour;
import CP_Classes.vo.votblOrganization;
import CP_Classes.vo.voCompetency;
import CP_Classes.vo.voKeyBehaviour;

public class _keybehaviour__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.Competency Comp;
    synchronized (pageContext.getSession()) {
      Comp = (CP_Classes.Competency) pageContext.getSession().getAttribute("Comp");
      if (Comp == null) {
        Comp = new CP_Classes.Competency();
        pageContext.getSession().setAttribute("Comp", Comp);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.KeyBehaviour KB;
    synchronized (pageContext.getSession()) {
      KB = (CP_Classes.KeyBehaviour) pageContext.getSession().getAttribute("KB");
      if (KB == null) {
        KB = new CP_Classes.KeyBehaviour();
        pageContext.getSession().setAttribute("KB", KB);
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
    out.write(_jsp_string3, 0, _jsp_string3.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string4, 0, _jsp_string4.length);
    out.print((trans.tslt("Delete Key Behaviour")));
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
String compDef = " ";

if(request.getParameter("org") != null)
{ 
	int PKOrg = new Integer(request.getParameter("org")).intValue();
 	logchk.setOrg(PKOrg);
	
	int comp = Integer.parseInt(request.getParameter("comp"));	
	int kb = Integer.parseInt(request.getParameter("kb"));
	
	if(comp != 0)
		compDef = Comp.CompetencyDefinition(comp);
	
	KB.setKBLevel(kb);
	Comp.setPKComp(comp);
}

/*-------------------------------------------------------------------end login modification 1--------------------------------------*/

/************************************************** ADDING TOGGLE FOR SORTING PURPOSE *************************************************/

	int type = KB.getSortType(); //1=name, 2=origin	
	int toggle = KB.getToggle();	//0=asc, 1=desc		
			
	if(request.getParameter("name") != null)
	{	 
		if(toggle == 0)
			toggle = 1;
		else
			toggle = 0;
		
		KB.setToggle(toggle);
		
		type = Integer.parseInt(request.getParameter("name"));			 
		KB.setSortType(type);									
	} 
  
	
/************************************************** ADDING TOGGLE FOR SORTING PURPOSE *************************************************/
	


    out.write(_jsp_string7, 0, _jsp_string7.length);
    out.print(( trans.tslt("Key Behaviour") ));
    out.write(_jsp_string8, 0, _jsp_string8.length);
    out.print(( trans.tslt("To Add, click on the Add button to create English version, then use Edit function to fill in information for other languages")));
    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print(( trans.tslt("To Edit, click on the relevant radio button and click on the Edit button") ));
    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print(( trans.tslt("To Delete, click on the relevant radio button and click on the Delete button") ));
    out.write(_jsp_string10, 0, _jsp_string10.length);
    out.print(( trans.tslt("Organisation") ));
    out.write(_jsp_string11, 0, _jsp_string11.length);
    
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
    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print((PKOrg));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((OrgName));
    out.write(_jsp_string14, 0, _jsp_string14.length);
     } else { 
    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print((PKOrg));
    out.write('>');
    out.print((OrgName));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    	}	
		} 
	} else { 
    out.write(_jsp_string15, 0, _jsp_string15.length);
    out.print((logchk.getSelfOrg()));
    out.write('>');
    out.print((UserDetail[10]));
    out.write(_jsp_string16, 0, _jsp_string16.length);
     } // End of isConsulting 
    out.write(_jsp_string17, 0, _jsp_string17.length);
    
	int DisplayNo, KBLevel;
	String KeyB, compName, Compname = "", origin="";		
	int pkComp, pkKB;
	DisplayNo = 1;
	KBLevel = 1;
   
	int PostKBLevel = KB.getKBLevel();
	
	int CompID = 0;	
	CompID = Comp.getPKComp();
	int compID = logchk.getCompany();
	int orgID = logchk.getOrg();
	int pkUser = logchk.getPKUser();	
	if(CompID != 0)
		compDef = Comp.CompetencyDefinition(CompID);	

	Vector CompResult = Comp.FilterRecord(compID, orgID);
	Vector KBResult = null;

	KBResult = KB.FilterKBList(CompID, PostKBLevel, compID, orgID);
		
	int t =0;

    out.write(_jsp_string18, 0, _jsp_string18.length);
    out.print(( trans.tslt("Competency Level") ));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print((t));
    out.write('>');
    out.print((trans.tslt("All")));
    out.write(_jsp_string20, 0, _jsp_string20.length);
    
			for(KBLevel = 1; KBLevel <= 10; KBLevel++) {
				if(PostKBLevel != 0 && PostKBLevel == KBLevel) {
		
    out.write(_jsp_string21, 0, _jsp_string21.length);
    out.print((KBLevel));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((KBLevel));
    out.write(_jsp_string20, 0, _jsp_string20.length);
     } else {
		
    out.write(_jsp_string21, 0, _jsp_string21.length);
    out.print((KBLevel));
    out.write('>');
    out.print((KBLevel));
    out.write(_jsp_string20, 0, _jsp_string20.length);
    
			}				
		}       
		
    out.write(_jsp_string22, 0, _jsp_string22.length);
    out.print(( trans.tslt("Competency") ));
    out.write(_jsp_string23, 0, _jsp_string23.length);
    out.print((t));
    out.write('>');
    out.print((trans.tslt("All")));
    out.write(_jsp_string24, 0, _jsp_string24.length);
    
		   /********************
			* Edited by James 30 Oct 2007
			************************/
			
			//while(CompResult.next()) {
			for(int i=0; i<CompResult.size(); i++) {
				voCompetency voC = (voCompetency)CompResult.elementAt(i);
				pkComp = voC.getPKCompetency();
				compName = voC.getCompetencyName();
				//origin = aResult.getString("Description");
				if(pkComp == CompID) {
		
    out.write(_jsp_string25, 0, _jsp_string25.length);
    out.print((pkComp));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((compName));
    out.write(_jsp_string24, 0, _jsp_string24.length);
     } else {
		
    out.write(_jsp_string25, 0, _jsp_string25.length);
    out.print((pkComp));
    out.write('>');
    out.print((compName));
    out.write(_jsp_string24, 0, _jsp_string24.length);
    
			}
				
			}
			
		
    out.write(_jsp_string26, 0, _jsp_string26.length);
    out.print(( trans.tslt("Show") ));
    out.write(_jsp_string27, 0, _jsp_string27.length);
    out.print(( trans.tslt("Definition") ));
    out.write(_jsp_string28, 0, _jsp_string28.length);
    out.print((compDef));
    out.write(_jsp_string29, 0, _jsp_string29.length);
    out.print(( trans.tslt("Key Behaviour") ));
    out.write(_jsp_string30, 0, _jsp_string30.length);
    out.print(( trans.tslt("Origin") ));
    out.write(_jsp_string31, 0, _jsp_string31.length);
     	
			/********************
			* Edited by James 30 Oct 2007
			************************/
	//while(KBResult.next()) {		
		
	for(int i=0; i<KBResult.size(); i++) {
		voKeyBehaviour voKB = (voKeyBehaviour)KBResult.elementAt(i);
		pkKB = voKB.getPKKeyBehaviour();		
		KeyB =  voKB.getKeyBehaviour();
		origin =  voKB.getDescription();

    out.write(_jsp_string32, 0, _jsp_string32.length);
    out.print((pkKB));
    out.write(_jsp_string33, 0, _jsp_string33.length);
     out.print(DisplayNo);
    out.write(_jsp_string34, 0, _jsp_string34.length);
     out.print(KeyB); 
    out.write(_jsp_string35, 0, _jsp_string35.length);
     out.print(origin); 
    out.write(_jsp_string36, 0, _jsp_string36.length);
     	DisplayNo++;
	} 
	

    out.write(_jsp_string37, 0, _jsp_string37.length);
    out.print(( trans.tslt("Add") ));
    out.write(_jsp_string38, 0, _jsp_string38.length);
    out.print(( trans.tslt("Edit") ));
    out.write(_jsp_string39, 0, _jsp_string39.length);
    out.print(( trans.tslt("Delete") ));
    out.write(_jsp_string40, 0, _jsp_string40.length);
    
	if(request.getParameter("delete") != null) {
		int pkKB1 = Integer.parseInt(request.getParameter("delete"));
		int check = KB.CheckSysLibKB(pkKB1);

		int totalKB = KB.totalKB(pkKB1);
		int userType = logchk.getUserType();	// 1= super admin
		if((userType == 1) || (check == 0)) {
		if(totalKB > 3) {
			
				boolean delete = KB.deleteRecord(pkKB1, pkUser);
				
			if(delete) {
			

    out.write(_jsp_string41, 0, _jsp_string41.length);
    
			}else{
			
			
    out.write(_jsp_string42, 0, _jsp_string42.length);
    out.print((trans.tslt("Deletion cannot be performed. Data is being used")));
    out.write(_jsp_string43, 0, _jsp_string43.length);
    		
		}
		} else {

    out.write(_jsp_string44, 0, _jsp_string44.length);
    out.print((trans.tslt("The total number of Key Behaviour in each Competency cannot be less than 3")));
    out.write(_jsp_string45, 0, _jsp_string45.length);
    		
		}
		} else if((userType != 1) && check == 1){

    out.write(_jsp_string44, 0, _jsp_string44.length);
    out.print((trans.tslt("You are not allowed to delete System Generated Libraries")));
    out.write(_jsp_string46, 0, _jsp_string46.length);
    	
	}
	
		
		
	}
	}

    out.write(_jsp_string47, 0, _jsp_string47.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string48, 0, _jsp_string48.length);
    
	Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);

    out.write(_jsp_string49, 0, _jsp_string49.length);
     // Denise 05/01/2010 update new email address 
    out.write(_jsp_string50, 0, _jsp_string50.length);
    out.print((year));
    out.write(_jsp_string51, 0, _jsp_string51.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("KeyBehaviour.jsp"), 1588466698389874668L, false);
    _caucho_depends.add(depend);
    depend = new com.caucho.vfs.Depend(appDir.lookup("Footer.jsp"), -9159279428429745474L, false);
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

  private final static char []_jsp_string26;
  private final static char []_jsp_string28;
  private final static char []_jsp_string22;
  private final static char []_jsp_string27;
  private final static char []_jsp_string41;
  private final static char []_jsp_string4;
  private final static char []_jsp_string36;
  private final static char []_jsp_string29;
  private final static char []_jsp_string14;
  private final static char []_jsp_string38;
  private final static char []_jsp_string24;
  private final static char []_jsp_string3;
  private final static char []_jsp_string25;
  private final static char []_jsp_string17;
  private final static char []_jsp_string33;
  private final static char []_jsp_string42;
  private final static char []_jsp_string18;
  private final static char []_jsp_string11;
  private final static char []_jsp_string35;
  private final static char []_jsp_string16;
  private final static char []_jsp_string23;
  private final static char []_jsp_string21;
  private final static char []_jsp_string12;
  private final static char []_jsp_string34;
  private final static char []_jsp_string13;
  private final static char []_jsp_string46;
  private final static char []_jsp_string15;
  private final static char []_jsp_string50;
  private final static char []_jsp_string7;
  private final static char []_jsp_string8;
  private final static char []_jsp_string19;
  private final static char []_jsp_string37;
  private final static char []_jsp_string32;
  private final static char []_jsp_string9;
  private final static char []_jsp_string0;
  private final static char []_jsp_string44;
  private final static char []_jsp_string20;
  private final static char []_jsp_string10;
  private final static char []_jsp_string49;
  private final static char []_jsp_string2;
  private final static char []_jsp_string47;
  private final static char []_jsp_string43;
  private final static char []_jsp_string45;
  private final static char []_jsp_string31;
  private final static char []_jsp_string5;
  private final static char []_jsp_string39;
  private final static char []_jsp_string1;
  private final static char []_jsp_string6;
  private final static char []_jsp_string30;
  private final static char []_jsp_string51;
  private final static char []_jsp_string40;
  private final static char []_jsp_string48;
  static {
    _jsp_string26 = "\r\n            </select></td>\r\n       <td align=\"left\"><input type=\"button\" value=\"".toCharArray();
    _jsp_string28 = "</td>\r\n      <td>&nbsp;</td>\r\n      <td colspan=\"2\"><textarea name=\"compDef\" style='font-size:10.0pt;font-family:Arial' cols=\"44\" rows=\"5\" readonly>".toCharArray();
    _jsp_string22 = "\r\n        </select></td>\r\n      <td>&nbsp;</td>\r\n    </tr>\r\n	 <tr>\r\n      <td>&nbsp;</td>\r\n      <td>&nbsp;</td>\r\n      <td>&nbsp;</td>\r\n      <td>&nbsp;</td>\r\n    </tr>\r\n	 <tr>\r\n       <td>".toCharArray();
    _jsp_string27 = "\" name=\"btnShow\" onclick=\"proceed(this.form,this.form.selOrg, this.form.Competency, this.form.KBLevel)\"></td>\r\n    </tr>\r\n	 <tr>\r\n	   <td>&nbsp;</td>\r\n	   <td>&nbsp;</td>\r\n	   <td>&nbsp;</td>\r\n	   <td>&nbsp;</td>\r\n    </tr>\r\n	 <tr>\r\n      <td>".toCharArray();
    _jsp_string41 = "\r\n		<script>\r\n			alert(\"Deleted successfully\");\r\n			window.location.href = \"KeyBehaviour.jsp\";\r\n		</script>\r\n".toCharArray();
    _jsp_string4 = "\r\n\r\n</head>\r\n\r\n<body>\r\n<p>\r\n  <SCRIPT LANGUAGE=\"JavaScript\">\r\n<!-- Begin\r\n\r\nvar x = parseInt(window.screen.width) / 2 - 225;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up\r\nvar y = parseInt(window.screen.height) / 2 - 125; \r\n\r\nfunction check(field)\r\n{\r\n	var isValid = 0;\r\n	var clickedValue = 0;\r\n	//check whether any checkbox selected\r\n	if( field == null ) {\r\n		isValid = 2;\r\n	\r\n	} else {\r\n\r\n		if(isNaN(field.length) == false) {\r\n			for (i = 0; i < field.length; i++)\r\n				if(field[i].checked) {\r\n					clickedValue = field[i].value;\r\n					isValid = 1;\r\n				}\r\n		}else {		\r\n			if(field.checked) {\r\n				clickedValue = field.value;\r\n				isValid = 1;\r\n			}\r\n				\r\n		}\r\n	}\r\n	\r\n	if(isValid == 1)\r\n		return clickedValue;\r\n	else if(isValid == 0)\r\n		alert(\"No record selected\");\r\n	else if(isValid == 2)\r\n		alert(\"No record available\");\r\n	\r\n	isValid = 0;\r\n\r\n}\r\n\r\n//Edited by Xuehai 02 Jun 2011. Remove 'void'. Enable to run on different browers like Chrome&Firefox.\r\n//void function confirmAdd(form) {\r\nfunction confirmAdd(form) {\r\n	form.action = \"KeyBehaviour.jsp\";\r\n	\r\n	var myWindow=window.open('AddKB.jsp','windowRef','scrollbars=no, width=450, height=250');\r\n	myWindow.moveTo(x,y);\r\n    myWindow.location.href = 'AddKB.jsp';\r\n}\r\n\r\n//void function confirmEdit(form, field) {	\r\nfunction confirmEdit(form, field) {	\r\n	var value = check(field);\r\n	\r\n	if(value) {\r\n		var myWindow=window.open('EditKB.jsp','windowRef','scrollbars=no, width=500, height=750');\r\n		var query = \"EditKB.jsp?clicked=\"+value;\r\n		myWindow.moveTo(x,y);\r\n    	myWindow.location.href = query;\r\n	}\r\n}\r\n\r\nfunction confirmDelete(form, field) {\r\n	var value = check(field);\r\n	\r\n	if(value)\r\n	{	\r\n		if(confirm('".toCharArray();
    _jsp_string36 = "&nbsp;\r\n	   </td>\r\n   </tr>\r\n".toCharArray();
    _jsp_string29 = "</textarea></td>\r\n    </tr>\r\n  </table>\r\n&nbsp;\r\n\r\n<div style='width:610px; height:259px; z-index:1; overflow:auto'>\r\n<table width=\"593\" border=\"1\" style='font-size:10.0pt;font-family:Arial' bordercolor=\"#3399FF\" bgcolor=\"#FFFFCC\">\r\n<th bgcolor=\"navy\" width=\"10\" bordercolor=\"#3399FF\">&nbsp;\r\n</th>\r\n<th bgcolor=\"navy\" width=\"10\" bordercolor=\"#3399FF\"><b><font span style='font-size:10.0pt;font-family:Arial;color:white'>\r\nNo</font></b></th>\r\n<th bgcolor=\"navy\" width=\"480\" bordercolor=\"#3399FF\"><a href=\"KeyBehaviour.jsp?name=1\"><b><font span style='font-size:10.0pt;font-family:Arial;color:white'><u>".toCharArray();
    _jsp_string14 = "</option>\r\n			".toCharArray();
    _jsp_string38 = "\" onclick=\"confirmAdd(this.form)\">\r\n<input type=\"button\" name=\"btnEdit\" value=\"".toCharArray();
    _jsp_string24 = "\r\n           ".toCharArray();
    _jsp_string3 = "\r\n\r\n\r\n\r\n\r\n<title>Key Behaviour</title>\r\n\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n\r\n".toCharArray();
    _jsp_string25 = "\r\n           <option value = ".toCharArray();
    _jsp_string17 = "\r\n        </select></td>\r\n		<td width=\"65\">&nbsp;</td>\r\n	</tr>\r\n\r\n".toCharArray();
    _jsp_string33 = ">\r\n	   </td>\r\n	   	<td align=\"center\" bordercolor=\"#3399FF\">\r\n	   		\r\n   		  ".toCharArray();
    _jsp_string42 = "	\r\n<script>\r\n		alert(\"".toCharArray();
    _jsp_string18 = "\r\n    <tr>\r\n      <td>&nbsp;</td>\r\n      <td>&nbsp;</td>\r\n      <td>&nbsp;</td>\r\n      <td>&nbsp;</td>\r\n    </tr>   \r\n    <tr>\r\n      <td>".toCharArray();
    _jsp_string11 = "</td>\r\n		<td width=\"12\">&nbsp;</td>\r\n		<td width=\"363\"><select size=\"1\" name=\"selOrg\" onChange=\"proceed1(this.form,this.form.selOrg, this.form.Competency,this.form.KBLevel)\">\r\n".toCharArray();
    _jsp_string35 = "&nbsp;\r\n	   </td>\r\n	   <td align=\"center\" bordercolor=\"#3399FF\">\r\n		".toCharArray();
    _jsp_string16 = "</option>\r\n	".toCharArray();
    _jsp_string23 = "</td>\r\n       <td>&nbsp;</td>\r\n       <td><select name=\"Competency\">\r\n           <option value=".toCharArray();
    _jsp_string21 = "\r\n          <option value=".toCharArray();
    _jsp_string12 = "\r\n				<option value=".toCharArray();
    _jsp_string34 = "&nbsp;</td>\r\n	   <td bordercolor=\"#3399FF\">\r\n		".toCharArray();
    _jsp_string13 = " selected>".toCharArray();
    _jsp_string46 = "!\");\r\n			//window.location.href = \"Competency.jsp\";\r\n		</script>\r\n".toCharArray();
    _jsp_string15 = "\r\n		<option value=".toCharArray();
    _jsp_string50 = "\r\n		<td align=\"center\" height=\"5\" valign=\"top\"><font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"Login.jsp\">Home</a>&nbsp;| <a color=\"navy\" face=\"Arial\">&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"mailto:3SixtyAdmin@pcc.com.sg?subject=Regarding:\">Contact \r\n		Us</a><a color=\"navy\" face=\"Arial\" href=\"termofuse.jsp\" target=\"_blank\"><span style=\"color: #000080; text-decoration: none\"> | Terms of Use </span></a>| <span style=\"color: #000080; text-decoration: none\"><a style=\"TEXT-DECORATION: none; color:navy;\" href=\"http://www.pcc.com.sg/\" target=\"_blank\">PCC Website</a></span></font></td></tr><tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\">\r\n   \r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		<font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;Copyright &copy; ".toCharArray();
    _jsp_string7 = "\r\n\r\n<form name=\"KBList\" method=\"post\" action=\"KeyBehaviour.jsp\">\r\n<table border=\"0\" width=\"555\" cellspacing=\"0\" cellpadding=\"0\" span style='font-size:10.0pt;font-family:Arial;'>\r\n	<tr>\r\n	  <td colspan=\"3\"><b><font color=\"#000080\" size=\"2\" face=\"Arial\">".toCharArray();
    _jsp_string8 = " </font></b></td>\r\n	  <td>&nbsp;</td>\r\n    </tr>\r\n	<tr>\r\n	  <td colspan=\"3\"><ul>\r\n	      <li><font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string19 = "</td>\r\n      <td>&nbsp;</td>\r\n      <td>\r\n        <select name=\"KBLevel\">\r\n          <option value=".toCharArray();
    _jsp_string37 = "\r\n</table>\r\n</div>\r\n\r\n<p></p>\r\n<font face=\"Arial\">\r\n<input type=\"button\" name=\"Add\" value=\"".toCharArray();
    _jsp_string32 = "\r\n   <tr onMouseOver = \"this.bgColor = '#99ccff'\"\r\n    	onMouseOut = \"this.bgColor = '#FFFFCC'\">\r\n       <td bordercolor=\"#3399FF\">\r\n	   		<input type=\"radio\" name=\"radioKB\" value=".toCharArray();
    _jsp_string9 = ".</font></li>\r\n          <li><font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string0 = "\r\n\r\n\r\n\r\n\r\n\r\n<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n\r\n<html>\r\n<head>\r\n".toCharArray();
    _jsp_string44 = "\r\n		<script>\r\n			alert(\"".toCharArray();
    _jsp_string20 = "\r\n          ".toCharArray();
    _jsp_string10 = ".</font></li>\r\n	      </ul></td>\r\n	  <td>&nbsp;</td>\r\n    </tr>\r\n	<tr>\r\n		<td width=\"115\">".toCharArray();
    _jsp_string49 = "\r\n\r\n<table border=\"0\" width=\"610\" height=\"26\" id=\"table1\">\r\n\r\n	<tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\"/>\r\n		".toCharArray();
    _jsp_string2 = "\r\n".toCharArray();
    _jsp_string47 = "\r\n\r\n<p></p>\r\n\r\n\r\n".toCharArray();
    _jsp_string43 = "!\");\r\n</script>\r\n".toCharArray();
    _jsp_string45 = "!\");\r\n			//window.location.href = \"KeyBehaviour.jsp\";\r\n		</script>		\r\n".toCharArray();
    _jsp_string31 = "</u></font></b></a></th>\r\n\r\n".toCharArray();
    _jsp_string5 = "?')) {\r\n			form.action = \"KeyBehaviour.jsp?delete=\"+value;\r\n			form.method = \"post\";\r\n			form.submit();\r\n			return true;\r\n		} else\r\n			return false;\r\n	}\r\n\r\n}\r\n\r\n/*------------------------------------------------------------start: LOgin modification 1------------------------------------------*/\r\n/*	choosing organization*/\r\n\r\nfunction proceed(form, org, comp, kb)\r\n{  \r\n	form.action=\"KeyBehaviour.jsp?org=\"+org.value + \"&comp=\" + comp.value + \"&kb=\" + kb.value;\r\n	form.method=\"post\";\r\n	form.submit();	\r\n}	\r\n\r\n/* Function added by Ha on 15/05/08 to reload the page when changing organisation*/\r\nfunction proceed1(form, org, comp, kb)\r\n{\r\n    comp.value = 0;\r\n    kb.value = 0;\r\n    form.action=\"KeyBehaviour.jsp?org=\"+org.value + \"&comp=\" + comp.value + \"&kb=\" + kb.value;\r\n	form.method=\"post\";\r\n	form.submit();\r\n}\r\n</script>\r\n  \r\n\r\n".toCharArray();
    _jsp_string39 = "\"  onclick = \"confirmEdit(this.form, this.form.radioKB)\">\r\n<input type=\"button\" name=\"Submit\" value=\"".toCharArray();
    _jsp_string1 = "   \r\n".toCharArray();
    _jsp_string6 = " <font size=\"2\">\r\n   \r\n    	    	<script>\r\n	parent.location.href = \"index.jsp\";\r\n</script>\r\n".toCharArray();
    _jsp_string30 = "</u></font></b></a></th>\r\n<th bgcolor=\"navy\" width=\"90\" bordercolor=\"#3399FF\"><a href=\"KeyBehaviour.jsp?name=2\"><b><font style='font-size:10.0pt;font-family:Arial;color:white'><u>".toCharArray();
    _jsp_string51 = " Pacific Century Consulting Pte Ltd. All Rights Reserved.\r\n		</font>\r\n		</td>\r\n		\r\n	</tr>\r\n		\r\n</table>\r\n\r\n\r\n</body>\r\n</html>".toCharArray();
    _jsp_string40 = "\"  onclick = \"return confirmDelete(this.form, this.form.radioKB)\">\r\n</font>\r\n</form>\r\n".toCharArray();
    _jsp_string48 = "\r\n\r\n".toCharArray();
  }
}