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
import CP_Classes.vo.voCompetency;
import CP_Classes.vo.voDevelopmentPlan;
import CP_Classes.vo.votblSurvey;

public class _developmentplan__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.DevelopmentPlan DP;
    synchronized (pageContext.getSession()) {
      DP = (CP_Classes.DevelopmentPlan) pageContext.getSession().getAttribute("DP");
      if (DP == null) {
        DP = new CP_Classes.DevelopmentPlan();
        pageContext.getSession().setAttribute("DP", DP);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.DevelopmentPlanReport DPR;
    synchronized (pageContext.getSession()) {
      DPR = (CP_Classes.DevelopmentPlanReport) pageContext.getSession().getAttribute("DPR");
      if (DPR == null) {
        DPR = new CP_Classes.DevelopmentPlanReport();
        pageContext.getSession().setAttribute("DPR", DPR);
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
    CP_Classes.Setting SET;
    synchronized (pageContext.getSession()) {
      SET = (CP_Classes.Setting) pageContext.getSession().getAttribute("SET");
      if (SET == null) {
        SET = new CP_Classes.Setting();
        pageContext.getSession().setAttribute("SET", SET);
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
    out.write(_jsp_string4, 0, _jsp_string4.length);
    out.print((trans.tslt("Are you sure you want to delete this record")));
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

	int raterID = logchk.getPKUser();
	int DisplayNo, ResID, pkComp, fkComp, res;
	String resName [] = new String [6];

	resName[0] = trans.tslt("All");
	resName[1] = trans.tslt("Development Activities");
	resName[2] = trans.tslt("Books");
	resName[3] = trans.tslt("Web Resources");
	resName[4] = trans.tslt("Training Courses");
	resName[5] = trans.tslt("AV Resources"); // Change Resource category from "In-house Resources" to "AV Resources", Desmond 10 May 2011
	
	String CompName, DRARes, origin;
	DisplayNo = 1;
	ResID = 0;
	DRARes = "";
	CompName = "";
	pkComp = 0;
	fkComp = DP.getFKComp();
	res = DP.getOption();

	int orgID = logchk.getOrg();	
	int compID = logchk.getCompany();
	int pkUser = logchk.getPKUser();
	Vector vPlan = new Vector();

	if(request.getParameter("survey") != null)
	{ 
		session.setAttribute("surveyID",request.getParameter("survey"));
		int FKSurvey = new Integer(request.getParameter("survey")).intValue();
		
		DP.setFKSurvey(FKSurvey);
		//vPlan = DP.getDevelopmentPlan(raterID,0,0);
		//Edited by Roger 20 June 2008
		vPlan = DP.getDevelopmentPlanBySurveyId(raterID,0,0, FKSurvey);
		
	}
//***********Edited by Tracy 12 Aug 08*******************
// Display development list on page load
	else if(session.getAttribute("surveyID")!=null) {
		String strSurveyID= (String)session.getAttribute("surveyID");
		int FKSurvey = Integer.parseInt(strSurveyID);
		
		DP.setFKSurvey(FKSurvey);
		vPlan = DP.getDevelopmentPlanBySurveyId(raterID,0,0, FKSurvey);
//*********************************************************
	}
	// out.print(session.getAttribute("surveyID")); commented by Tracy 01 Sept 08	
	if(request.getParameter("generate") != null) {
		int FKSurvey = new Integer(request.getParameter("Survey")).intValue();
		
		java.util.Date timeStamp = new java.util.Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("ddMMyyHHmmss");
		String temp  =  dFormat.format(timeStamp);
		
		String file_name = "IndividualDevelopmentPlan"+temp+".xls";
		String displayed = "IndividualDevelopmentPlan";						
		
		DPR.Report(FKSurvey, raterID, file_name);	
		
		String output = SET.getReport_Path() + "/" + file_name;
		File f = new File (output);
		
		displayed += ".xls";
		
		//set the content type(can be excel/word/powerpoint etc..)
		response.reset();
		response.setContentType ("application/xls");
		//set the header and also the Name by which user will be prompted to save
		response.addHeader ("Content-Disposition", "attachment;filename=\"" + displayed + "\"");
	
		//get the file name
		String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
		//OPen an input stream to the file and post the file contents thru the 
		//servlet output stream to the client m/c
		
		InputStream in = new FileInputStream(f);
		ServletOutputStream outs = response.getOutputStream();
			
		int bit = 256;
		int i = 0;

		try {
			while ((bit) >= 0) {
				bit = in.read();
				outs.write(bit);
			}

		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}

		outs.flush();
		outs.close();
		in.close();	
		
		
	
	}
	
/*-------------------------------------------------------------------end login modification 1--------------------------------------*/


/************************************************** ADDING TOGGLE FOR SORTING PURPOSE *************************************************/

	/*int toggle = DRAResQuery.getToggle();	//0=asc, 1=desc
	int type = 1; //1=name, 2=origin		
			
	if(request.getParameter("name") != null)
	{	 
		if(toggle == 0)
			toggle = 1;
		else
			toggle = 0;
		
		DRAResQuery.setToggle(toggle);
		
		type = Integer.parseInt(request.getParameter("name"));			 
		DRAResQuery.setSortType(type);									
	} */
	
/************************************************** ADDING TOGGLE FOR SORTING PURPOSE *************************************************/
	

    out.write(_jsp_string7, 0, _jsp_string7.length);
    
	// Moved by Tracy 26 Aug 08**************************
	// Commented by Tracy 01 Sept 08********************
	if(request.getParameter("delete") != null) {
		
		ResID = Integer.parseInt(request.getParameter("delete"));
		
		DP.deleteRecord(ResID);
		session.setAttribute("surveyID",request.getParameter("survey"));
		int FKSurvey = new Integer(request.getParameter("survey")).intValue();
		
		DP.setFKSurvey(FKSurvey);
		vPlan = DP.getDevelopmentPlanBySurveyId(raterID,0,0, FKSurvey);

    out.write(_jsp_string8, 0, _jsp_string8.length);
    
	}
	// End move by Tracy 26 Aug 08************************
	// End comment by Tracy 01 Sept 08********************


    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print(( trans.tslt("Individual Development Plan") ));
    out.write(_jsp_string10, 0, _jsp_string10.length);
    out.print(( trans.tslt("To display an existing Development Plan, select the appropriate survey and click on the Show My Plan button") ));
    out.write(_jsp_string11, 0, _jsp_string11.length);
    out.print(( trans.tslt("To create a new Development Plan, click on the Add button") ));
    out.write(_jsp_string11, 0, _jsp_string11.length);
    out.print(( trans.tslt("To edit/delete a Development Plan item, select the appropriate survey, click on the Show My Plan button, select the development item then click in the Edit/Delete button") ));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print(( trans.tslt("Survey") ));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((trans.tslt("Please select one")));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    
			//Edit by Roger 1 July 2008
			//display survey base on target instead
			//Vector vSurvey = DP.getSurveys(pkUser);
			Vector vSurvey = DP.getSurveysByTargetDevCompetency(pkUser);
			for(int i=0; i<vSurvey.size(); i++) {
				votblSurvey vo = (votblSurvey)vSurvey.elementAt(i);
			
				int pkSurvey = vo.getSurveyID();
				String sSurveyName = vo.getSurveyName();
				if(pkSurvey != 0 && pkSurvey == DP.getFKSurvey()) {
		
    out.write(_jsp_string15, 0, _jsp_string15.length);
    out.print((pkSurvey));
    out.write(_jsp_string16, 0, _jsp_string16.length);
    out.print((sSurveyName));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    		

				}else {
		
    out.write(_jsp_string15, 0, _jsp_string15.length);
    out.print((pkSurvey));
    out.write('>');
    out.print((sSurveyName));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    
				}
			}
		
    out.write(_jsp_string17, 0, _jsp_string17.length);
    out.print(( trans.tslt("Show My Plan") ));
    out.write(_jsp_string18, 0, _jsp_string18.length);
    out.print(( trans.tslt("Competency") ));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print(( trans.tslt("Development Activities/Resources") ));
    out.write(_jsp_string20, 0, _jsp_string20.length);
    out.print(( trans.tslt("Proposed Completion Date") ));
    out.write(_jsp_string21, 0, _jsp_string21.length);
    out.print(( trans.tslt("Development Preview Process") ));
    out.write(_jsp_string22, 0, _jsp_string22.length);
    out.print(( trans.tslt("Actual Completion Date") ));
    out.write(_jsp_string23, 0, _jsp_string23.length);
     	
	/*String arr[] = {"Completion of activity or feedback from supervisor and peers", "Book summary or book review discussion with supervisor", "Course completion certificate"};
	String resources[] = {"Identify effective decision makers to 'bounce ideas off' prior to finalizing a decision.",
	 					"Innovation at Work, Katherine E. Holt, Ph.D, Publisher: ASTD, March 2003, ISBN: 1-56286-351-7",
						"Creating and Marketing Innovative Products: From Concept to Commercialisation, www.nus.edu.sg/nex"};
	String competency[] = {"Decision Making (Problem Solving)",
	 					"Innovation",
						"Innovation"};
	String completion []= {"18 Oct 2007",
	 					"&nbsp;",
						"&nbsp;"};
	String timeframe[]= {"18 Oct 2007",
	 					"31 Oct 2007",
						"02 Dec 2007"};*/
	
	
	for(int i=0; i<vPlan.size(); i++) {
		voDevelopmentPlan voPlan = (voDevelopmentPlan)vPlan.elementAt(i);
		
		String arr = voPlan.getProcess();
		String resources = voPlan.getResource();
		String competency = voPlan.getCompetencyName();
		String completion = voPlan.getCompletionDate();
		String timeframe = voPlan.getProposedDate();
		ResID = voPlan.getPKDevPlan();

    out.write(_jsp_string24, 0, _jsp_string24.length);
    out.print((ResID));
    out.write(_jsp_string25, 0, _jsp_string25.length);
     out.print(DisplayNo);
    out.write(_jsp_string26, 0, _jsp_string26.length);
    out.print((competency));
    out.write(_jsp_string27, 0, _jsp_string27.length);
    out.print((resources));
    out.write(_jsp_string28, 0, _jsp_string28.length);
    out.print((timeframe));
    out.write(_jsp_string29, 0, _jsp_string29.length);
    out.print((arr));
    out.write(_jsp_string28, 0, _jsp_string28.length);
    
		if(completion.equals(""))  {
		 	out.print("&nbsp;");
		} else {
			out.print(completion);
		}
		
		
    out.write(_jsp_string30, 0, _jsp_string30.length);
     	DisplayNo++;
	// Edited by Tracy 26 aug 08
	// Commented by Tracy 01 Sep 08
	} }
	// End comment by tracy 01 Sep 08
	

    out.write(_jsp_string31, 0, _jsp_string31.length);
    out.print(( trans.tslt("Add") ));
    out.write(_jsp_string32, 0, _jsp_string32.length);
    out.print(( trans.tslt("Edit") ));
    out.write(_jsp_string33, 0, _jsp_string33.length);
    out.print(( trans.tslt("Delete") ));
    out.write(_jsp_string34, 0, _jsp_string34.length);
    out.print(( trans.tslt("Print Development Plan") ));
    out.write(_jsp_string35, 0, _jsp_string35.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string7, 0, _jsp_string7.length);
    
	Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);

    out.write(_jsp_string36, 0, _jsp_string36.length);
     // Denise 05/01/2010 update new email address 
    out.write(_jsp_string37, 0, _jsp_string37.length);
    out.print((year));
    out.write(_jsp_string38, 0, _jsp_string38.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("DevelopmentPlan.jsp"), 8393414196813562856L, false);
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

  private final static char []_jsp_string9;
  private final static char []_jsp_string27;
  private final static char []_jsp_string29;
  private final static char []_jsp_string10;
  private final static char []_jsp_string32;
  private final static char []_jsp_string18;
  private final static char []_jsp_string22;
  private final static char []_jsp_string12;
  private final static char []_jsp_string1;
  private final static char []_jsp_string31;
  private final static char []_jsp_string13;
  private final static char []_jsp_string14;
  private final static char []_jsp_string8;
  private final static char []_jsp_string15;
  private final static char []_jsp_string34;
  private final static char []_jsp_string30;
  private final static char []_jsp_string11;
  private final static char []_jsp_string20;
  private final static char []_jsp_string33;
  private final static char []_jsp_string16;
  private final static char []_jsp_string37;
  private final static char []_jsp_string24;
  private final static char []_jsp_string28;
  private final static char []_jsp_string0;
  private final static char []_jsp_string25;
  private final static char []_jsp_string23;
  private final static char []_jsp_string26;
  private final static char []_jsp_string35;
  private final static char []_jsp_string5;
  private final static char []_jsp_string36;
  private final static char []_jsp_string19;
  private final static char []_jsp_string2;
  private final static char []_jsp_string17;
  private final static char []_jsp_string38;
  private final static char []_jsp_string3;
  private final static char []_jsp_string6;
  private final static char []_jsp_string4;
  private final static char []_jsp_string21;
  private final static char []_jsp_string7;
  static {
    _jsp_string9 = "\r\n\r\n<form name=\"DRAList\" method=\"post\" action=\"DevelopmentPlan.jsp\">\r\n<table border=\"0\" width=\"579\" cellspacing=\"0\" cellpadding=\"0\" font span style='font-size:10.0pt;font-family:Arial;'>\r\n	<tr>\r\n	  <td colspan=\"4\"><b><font color=\"#000080\" size=\"2\" face=\"Arial\">".toCharArray();
    _jsp_string27 = "\r\n	   </td>\r\n	   <td align=\"left\">\r\n		".toCharArray();
    _jsp_string29 = "\r\n        </td>\r\n	   <td align=\"left\">\r\n		".toCharArray();
    _jsp_string10 = " </font></b></td>\r\n    </tr>\r\n	<tr>\r\n	  <td colspan=\"4\"><ul>\r\n	    <li><font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string32 = "\" onClick=\"confirmAdd(this.form)\">\r\n<input type=\"button\" name=\"btnEdit\" value=\"".toCharArray();
    _jsp_string18 = "\" name=\"btnShow\" onclick=\"proceed(this.form, this.form.Survey)\"></td>\r\n    </tr>	\r\n    <tr>\r\n      <td width=\"131\">&nbsp;</td>\r\n      <td width=\"18\">&nbsp;</td>\r\n      <td width=\"355\">&nbsp;</td>\r\n      <td width=\"75\" align=\"center\">&nbsp;</td>\r\n    </tr>\r\n </table>\r\n  \r\n<p></p>\r\n\r\n\r\n<div style=\"width:720px; height:300px; z-index:1; overflow:auto;\"> \r\n<table width=\"700\" border=\"1\" style='font-size:10.0pt;font-family:Arial' bordercolor=\"#3399FF\" bgcolor=\"#FFFFCC\">\r\n<th width=\"20\" bgcolor=\"navy\">&nbsp;\r\n\r\n</th>\r\n<th width=\"25\" bgcolor=\"navy\"><b><font span style='font-family:Arial;color:white'>No</font></b></th>\r\n<th bgcolor=\"navy\" width=\"100\"><a href=\"DevelopmentPlan.jsp?name=1\"><b>\r\n<font span style='font-family:Arial;color:white'><u>".toCharArray();
    _jsp_string22 = "</u></font></b></a></div></th>\r\n<th bgcolor=\"navy\" align=\"center\" width=\"70\"><a href=\"DevelopmentPlan.jsp?name=2\"><b>\r\n<font style='font-family:Arial;color:white'><u>".toCharArray();
    _jsp_string12 = ".</font></li>\r\n      \r\n	  </ul></td>\r\n	 <tr>\r\n      <td width=\"131\">&nbsp;</td>\r\n      <td width=\"18\">&nbsp;</td>\r\n      <td width=\"355\">&nbsp;</td>\r\n      <td width=\"75\" align=\"center\">&nbsp;</td>\r\n    </tr>\r\n    <tr>\r\n      <td>".toCharArray();
    _jsp_string1 = "\r\n\r\n\r\n</head>\r\n\r\n<body>\r\n\r\n".toCharArray();
    _jsp_string31 = "\r\n</table>\r\n</div>\r\n\r\n<p></p>\r\n<input type=\"button\" name=\"btnAdd\" value=\"".toCharArray();
    _jsp_string13 = "</td>\r\n      <td>:</td>\r\n      <td><select name=\"Survey\" onchange=\"proceed(this.form, this.form.Survey)\">\r\n        <option value=0 selected>".toCharArray();
    _jsp_string14 = "\r\n        ".toCharArray();
    _jsp_string8 = "		\r\n	<script>\r\n	alert(\"Deleted successfully\");\r\n	window.reload();\r\n	</script>\r\n".toCharArray();
    _jsp_string15 = "\r\n        <option value = ".toCharArray();
    _jsp_string34 = "\"  onclick = \"return confirmDelete(this.form, this.form.radioDRARes)\">\r\n<input type=\"button\" name=\"btnGenerate\" value=\"".toCharArray();
    _jsp_string30 = "\r\n        </td>\r\n   </tr>\r\n".toCharArray();
    _jsp_string11 = ".</font></li>\r\n	    <li><font face=\"Arial\" size=\"2\">".toCharArray();
    _jsp_string20 = "</u></font></b></a></th>\r\n<th bgcolor=\"navy\" align=\"center\" width=\"70\"><a href=\"DevelopmentPlan.jsp?name=2\"><b>\r\n<font style='font-family:Arial;color:white'><u>".toCharArray();
    _jsp_string33 = "\"  onclick = \"confirmEdit(this.form, this.form.radioDRARes,this.form.Survey)\">\r\n<input type=\"button\" name=\"btnDelete\" value=\"".toCharArray();
    _jsp_string16 = " selected>".toCharArray();
    _jsp_string37 = "\r\n		<td align=\"center\" height=\"5\" valign=\"top\"><font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"Login.jsp\">Home</a>&nbsp;| <a color=\"navy\" face=\"Arial\">&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"mailto:3SixtyAdmin@pcc.com.sg?subject=Regarding:\">Contact \r\n		Us</a><a color=\"navy\" face=\"Arial\" href=\"termofuse.jsp\" target=\"_blank\"><span style=\"color: #000080; text-decoration: none\"> | Terms of Use </span></a>| <span style=\"color: #000080; text-decoration: none\"><a style=\"TEXT-DECORATION: none; color:navy;\" href=\"http://www.pcc.com.sg/\" target=\"_blank\">PCC Website</a></span></font></td></tr><tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\">\r\n   \r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		<font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;Copyright &copy; ".toCharArray();
    _jsp_string24 = "\r\n   <tr onMouseOver = \"this.bgColor = '#99ccff'\"\r\n    	onMouseOut = \"this.bgColor = '#FFFFCC'\">\r\n       <td align=\"center\">\r\n	   		<input type=\"radio\" name=\"radioDRARes\" value=".toCharArray();
    _jsp_string28 = "\r\n	   </td>\r\n	   <td align=\"center\">\r\n		".toCharArray();
    _jsp_string0 = "\r\n\r\n\r\n\r\n\r\n<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n\r\n<html>\r\n<head>\r\n<title>Development Resources</title>\r\n\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n\r\n".toCharArray();
    _jsp_string25 = "><font size=\"2\">\r\n	   </td>\r\n	   	<td align=\"center\">\r\n   		  ".toCharArray();
    _jsp_string23 = "</u></font></b></a></th>\r\n".toCharArray();
    _jsp_string26 = "</td>\r\n	   <td>\r\n		".toCharArray();
    _jsp_string35 = "\"  onclick = \"return confirmGenerate(this.form)\">&nbsp; </form>\r\n\r\n\r\n<p></p>\r\n<div align=\"center\">\r\n  \r\n\r\n".toCharArray();
    _jsp_string5 = " ?')) {\r\n			// Changed by Tracy 26 Aug 08\r\n			// Commented by Tracy 01 Sep 08\r\n			//Edited by Xuehai 26 May 2011, Changed 'DRAList.value' to 'document.DRAList.value'\r\n			form.action = \"DevelopmentPlan.jsp?delete=\"+value+\"&survey=\"+document.DRAList.Survey.value;\r\n			// *******************************************\r\n			form.method = \"post\";\r\n			form.submit();\r\n			return true;\r\n	\r\n		}\r\n		else\r\n			return false;\r\n	}\r\n}\r\n\r\n/*------------------------------------------------------------start: LOgin modification 1------------------------------------------*/\r\n/*	choosing organization*/\r\n\r\nfunction proceed(form, field)\r\n{\r\n	form.action=\"DevelopmentPlan.jsp?survey=\"+field.value;\r\n	form.method=\"post\";\r\n	form.submit();\r\n}\r\n\r\nfunction confirmGenerate(form)\r\n{\r\n	if(form.Survey.value == 0) {\r\n		alert(\"Please select Survey\");\r\n	} else {\r\n		form.action=\"DevelopmentPlan.jsp?generate=1\";\r\n		form.method=\"post\";\r\n		form.submit();\r\n	}\r\n}\r\n</script>\r\n  \r\n\r\n".toCharArray();
    _jsp_string36 = "\r\n\r\n<table border=\"0\" width=\"610\" height=\"26\" id=\"table1\">\r\n\r\n	<tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\"/>\r\n		".toCharArray();
    _jsp_string19 = "</u></font></b></a></th>\r\n<th bgcolor=\"navy\" align=\"center\" width=\"240\"><a href=\"DevelopmentPlan.jsp?name=2\"><b>\r\n<font style='font-family:Arial;color:white'><u>".toCharArray();
    _jsp_string2 = "\r\n".toCharArray();
    _jsp_string17 = "\r\n        </select></td>\r\n      <td align=\"left\"><input type=\"button\" value=\"".toCharArray();
    _jsp_string38 = " Pacific Century Consulting Pte Ltd. All Rights Reserved.\r\n		</font>\r\n		</td>\r\n		\r\n	</tr>\r\n		\r\n</table>\r\n\r\n</div>\r\n</body>\r\n</html>".toCharArray();
    _jsp_string3 = "     \r\n".toCharArray();
    _jsp_string6 = " <font size=\"2\">\r\n   \r\n    	    	<script>\r\n	parent.location.href = \"index.jsp\";\r\n</script>\r\n".toCharArray();
    _jsp_string4 = "\r\n\r\n\r\n\r\n<SCRIPT LANGUAGE=\"JavaScript\">\r\n<!-- Begin\r\n\r\nvar x = parseInt(window.screen.width) / 2 - 400;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up\r\nvar y = parseInt(window.screen.height) / 2 - 350; \r\n\r\n\r\nfunction check(field, option)\r\n{\r\n	var isValid = 0;\r\n	var clickedValue = 0;\r\n	//check whether any checkbox selected\r\n	\r\n	if( field == null ) {\r\n		isValid = 2;\r\n	\r\n	} else {\r\n		for (i = 0; i < field.length; i++) \r\n			if(field[i].checked) {		\r\n				clickedValue = field[i].value;\r\n				//field[i].checked = false;\r\n				isValid = 1;\r\n			}\r\n    \r\n		if(isValid == 0 && field != null)  {\r\n			if(field.checked) {\r\n				clickedValue = field.value;\r\n				isValid = 1;\r\n			}\r\n		}\r\n    }\r\n	\r\n	if(option == 1) {\r\n		if(isValid == 1)\r\n			return clickedValue;\r\n		else if(isValid == 0)\r\n			alert(\"Please select a resource type\");\r\n		else if(isValid == 2)\r\n			alert(\"No record available\");\r\n	} else {\r\n	\r\n		if(isValid == 1)\r\n			return clickedValue;\r\n		else if(isValid == 0)\r\n			alert(\"No record selected\");\r\n		else if(isValid == 2)\r\n			alert(\"No record available\");\r\n	\r\n	}\r\n	\r\n	isValid = 0;	\r\n	\r\n}\r\n//Edited by Xuehai 25 May 2011. Removed 'void' to enable running on Chrome&Firefox\r\n//void function confirmAdd(form) {\r\nfunction confirmAdd(form) {\r\n//**************Edited by Tracy 12 Aug 08\r\n//Add parameter \"survey\" to url link\r\n	//Edited by Xuehai 26 May 2011, Changed 'DRAList.value' to 'document.DRAList.value'\r\n	var myWindow=window.open('AddDevelopmentPlan.jsp?survey='+document.DRAList.Survey.value,'windowRef','scrollbars=yes, width=800, height=700');\r\n//****************************************\r\n	myWindow.moveTo(x,y);\r\n\r\n}\r\n\r\n//void function confirmEdit(form, field,survey) {	\r\nfunction confirmEdit(form, field,survey) {	\r\n	var value = check(field);\r\n	//Edited by Xuehai 26 May 2011, Changed 'DRAList.value' to 'document.DRAList.value'\r\n	if(value) {\r\n		var myWindow=window.open('EditDevelopmentPlan.jsp?edit='+value+'&survey='+document.DRAList.Survey.value,'windowRef','scrollbars=yes, width=650, height=500');\r\n		myWindow.moveTo(x,y);\r\n	}\r\n}\r\n\r\nfunction confirmDelete(form, field) {\r\n	var value = check(field);\r\n	\r\n	if(value) {\r\n		if(confirm('".toCharArray();
    _jsp_string21 = "</u></font></b></a></th>\r\n<th bgcolor=\"navy\" align=\"center\" width=\"129\"><div align=\"center\"><a href=\"DevelopmentPlan.jsp?name=2\"><b>\r\n  <font style='font-family:Arial;color:white'><u>".toCharArray();
    _jsp_string7 = "\r\n\r\n".toCharArray();
  }
}