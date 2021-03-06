/*
 * JSP generated by Resin Professional 4.0.36 (built Fri, 26 Apr 2013 03:33:09 PDT)
 */

package _jsp._iphone;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import CP_Classes.vo.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class _coachbookingsignup__jsp extends com.caucho.jsp.JavaPage
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
    CP_Classes.User User;
    synchronized (pageContext.getSession()) {
      User = (CP_Classes.User) pageContext.getSession().getAttribute("User");
      if (User == null) {
        User = new CP_Classes.User();
        pageContext.getSession().setAttribute("User", User);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    
		SessionSetup.setUserPK(logchk.getPKUser());
		String username = (String) session.getAttribute("username");
		Vector userAssignments=new Vector();
		int userPK=logchk.getPKUser();

		if (!logchk.isUsable(username)) {
	
    out.write(_jsp_string3, 0, _jsp_string3.length);
    
 	} else {
		Vector sessionlist=SessionSetup.getSessionAllNamesUsedbyCandidate(logchk.getOrg());
		userAssignments=SessionSetup.getUserAssignmentBYCandidate();
 		if (request.getParameter("setSession") != null) {
 			//set up the org
 				int sessionPK=Integer.parseInt(request.getParameter("selSession"));
 				SessionSetup.setSelectedSession(sessionPK);
 				System.out.println("Selected Session:"+sessionPK);
 				userAssignments=SessionSetup.getUserAssignmentBYCandidate();
 		}
 		if (request.getParameter("setdate") != null) {
 			//set up the org
 				int datePK=Integer.parseInt(request.getParameter("selDate"));
 				System.out.println("set date PK:"+datePK);
 				SessionSetup.setSelectedDate(datePK);
 				//0 : all dates
 				if(SessionSetup.getSelectedDate()==0){
 					userAssignments=SessionSetup.getUserAssignmentBYCandidate();
 				}else{
 					userAssignments=SessionSetup.getUserAssignmentBYCandidate(datePK);
 				}
 		}
 		if (request.getParameter("signUp") != null) {
 			int selAssignmentPK=Integer.parseInt(request.getParameter("selAssignment"));
 			voCoachUserAssignment selectedUserAssignment=SessionSetup.getSelectedvoUserAssignment(selAssignmentPK);
 			if(SessionSetup.reachMax(userPK)){
 				
    out.write(_jsp_string4, 0, _jsp_string4.length);
     
 				
 			}else if(selectedUserAssignment.getUserFK()!=0){
 				
    out.write(_jsp_string5, 0, _jsp_string5.length);
     
 			}else{
 				SessionSetup.candidateSignUp(selAssignmentPK, userPK);
 				if(SessionSetup.getSelectedDate()==0){
 				userAssignments=SessionSetup.getUserAssignmentBYCandidate();
 				}else{
 				userAssignments=SessionSetup.getUserAssignmentBYCandidate(SessionSetup.getSelectedDate());
 				}
 				
    out.write(_jsp_string6, 0, _jsp_string6.length);
     
 			}
 			
 		}
 		if (request.getParameter("unSignUp") != null) {
 			int selAssignmentPK=Integer.parseInt(request.getParameter("selAssignment"));
 			//validate whether the user has sign up already and only sign one
 			if(!SessionSetup.checkUserHasSigned(selAssignmentPK, userPK)){
 				
    out.write(_jsp_string7, 0, _jsp_string7.length);
     
 				
 			}else{
 				SessionSetup.candidateUnSign(selAssignmentPK, userPK);
 				if(SessionSetup.getSelectedDate()==0){
 				userAssignments=SessionSetup.getUserAssignmentBYCandidate();
 				}else{
 				userAssignments=SessionSetup.getUserAssignmentBYCandidate(SessionSetup.getSelectedDate());
 				}
 				
    out.write(_jsp_string8, 0, _jsp_string8.length);
     
 			}
 		}
 		System.out.println("User login: "+userPK);
 		if(!SessionSetup.validateUser(logchk.getOrg(), userPK)){
 			userAssignments=new Vector();
 			System.out.println("User not authorized");
 			
    out.write(_jsp_string9, 0, _jsp_string9.length);
     
 		}
 		/************************************************** ADDING TOGGLE FOR SORTING PURPOSE *************************************************/

	int toggle = SessionSetup.getToggle();	//0=asc, 1=desc
	int type = 1; //1=date, 2=starting time 3= coach 4= status
			
	if(request.getParameter("name") != null)
	{	 
		if(toggle == 0)
			toggle = 1;
		else
			toggle = 0;
		
		SessionSetup.setToggle(toggle);
		
		type = Integer.parseInt(request.getParameter("name"));			 
		SessionSetup.setSortType(type);									
	} 
	
/************************************************** ADDING TOGGLE FOR SORTING PURPOSE *************************************************/
 		
 
    out.write(_jsp_string10, 0, _jsp_string10.length);
    
								for (int i = 0; i < sessionlist.size(); i++) {

									voCoachSession sessionDistic = (voCoachSession) sessionlist.elementAt(i);
										int sessionPK = sessionDistic.getPK();
										String sessionName = sessionDistic.getName();
										String sessionCode = sessionDistic.getDescription();

										if (SessionSetup.getSelectedSession() == sessionPK) {
							
    out.write(_jsp_string11, 0, _jsp_string11.length);
    out.print((sessionPK));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print((sessionName));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    
									} else {
								
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((sessionPK));
    out.write('>');
    out.print((sessionName));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    
									}
										}
								
    out.write(_jsp_string15, 0, _jsp_string15.length);
    
								for (int i = 0; i < SessionSetup.getCoachDates().size(); i++) {

									voCoachDate date = (voCoachDate)SessionSetup.getCoachDates().elementAt(i);
										int datePK = date.getPK();
										String Date=date.getDate().substring(0, 10);
										Date dateString = new SimpleDateFormat("yyyy-MM-dd").parse(Date);
										SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yyyy");
										String finalDate=sdf.format(dateString);
										if (SessionSetup.getSelectedDate() == datePK) {
							
    out.write(_jsp_string11, 0, _jsp_string11.length);
    out.print((datePK));
    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print((finalDate));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    
									} else {
								
    out.write(_jsp_string14, 0, _jsp_string14.length);
    out.print((datePK));
    out.write('>');
    out.print((finalDate));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    
									}
										}
								
    out.write(_jsp_string16, 0, _jsp_string16.length);
    
				int DisplayNo = 1;
				
				for (int i = 0; i < userAssignments.size(); i++) {
					voCoachUserAssignment userAssignment = new voCoachUserAssignment();
					userAssignment = (voCoachUserAssignment) userAssignments.elementAt(i);

					int AssignmentPK = userAssignment.getAssignmentPK();
					int StartingTime =userAssignment.getStartingTime();
					int EndingTime = userAssignment.getEndingTime();
					String CoachName=userAssignment.getCoachName();
					String sessionVenue=userAssignment.getSessionVenue();
					String Date=userAssignment.getDate().substring(0, 10);
					Date date = new SimpleDateFormat("yyyy-MM-dd").parse(Date);
					SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yyyy");
					String finalDate=sdf.format(date);
					String OrganizationName=userAssignment.getOrganizationName();
					int Status=userAssignment.getStatus();
					int UserFK=userAssignment.getUserFK();
					int venueFK=userAssignment.getVenueFK();
					String userNameInAssignment=SessionSetup.getUserNamebyID(UserFK);
					String fullName=User.getUserInfo(UserFK).getFamilyName()+" "+User.getUserInfo(UserFK).getGivenName();
					String link=userAssignment.getCoachLink();
					String fileName=userAssignment.getCoachFileName();
					String startingTime4Digits;
					String endingTime4Digits;
				if (StartingTime < 1000) {
					startingTime4Digits="0"+StartingTime;
				} else {
					startingTime4Digits=""+StartingTime;
				}
				if (EndingTime < 1000) {
					endingTime4Digits="0"+EndingTime;
				} else {
					endingTime4Digits=""+EndingTime;
				}
			
    out.write(_jsp_string17, 0, _jsp_string17.length);
    out.print((AssignmentPK));
    out.write(_jsp_string18, 0, _jsp_string18.length);
    out.print((DisplayNo));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print((finalDate));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print((startingTime4Digits));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print((endingTime4Digits));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print((CoachName));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    out.print((fullName));
    out.write(_jsp_string20, 0, _jsp_string20.length);
    
				DisplayNo++;
				}
			
    out.write(_jsp_string21, 0, _jsp_string21.length);
    
 	}
 
    out.write(_jsp_string22, 0, _jsp_string22.length);
    
	Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);

    out.write(_jsp_string23, 0, _jsp_string23.length);
     // Denise 05/01/2010 update new email address 
    out.write(_jsp_string24, 0, _jsp_string24.length);
    out.print((year));
    out.write(_jsp_string25, 0, _jsp_string25.length);
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
    depend = new com.caucho.vfs.Depend(appDir.lookup("iphone/CoachBookingSignUp.jsp"), 210316445690736390L, false);
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
  private final static char []_jsp_string21;
  private final static char []_jsp_string18;
  private final static char []_jsp_string9;
  private final static char []_jsp_string0;
  private final static char []_jsp_string10;
  private final static char []_jsp_string19;
  private final static char []_jsp_string7;
  private final static char []_jsp_string8;
  private final static char []_jsp_string16;
  private final static char []_jsp_string6;
  private final static char []_jsp_string5;
  private final static char []_jsp_string4;
  private final static char []_jsp_string12;
  private final static char []_jsp_string24;
  private final static char []_jsp_string2;
  private final static char []_jsp_string23;
  private final static char []_jsp_string13;
  private final static char []_jsp_string20;
  private final static char []_jsp_string15;
  private final static char []_jsp_string25;
  private final static char []_jsp_string3;
  private final static char []_jsp_string11;
  private final static char []_jsp_string14;
  private final static char []_jsp_string22;
  private final static char []_jsp_string17;
  static {
    _jsp_string1 = "\r\n	".toCharArray();
    _jsp_string21 = "\r\n		</table>\r\n		<br>\r\n			\r\n			<!-- select coach -->\r\n			<div align=\"center\">\r\n			<input type=\"button\" name=\"sign\" value=\"Book\" onclick=\"signUp(this.form,this.form.selAssignment)\" style=\"height: 45px; width: 130px\">\r\n			&nbsp;&nbsp;&nbsp;\r\n			<input type=\"button\" name=\"unsign\" value=\"Delete Booking\" onclick=\"unSignUp(this.form,this.form.selAssignment)\" style=\"height: 45px; width: 130px\">\r\n			</div>\r\n		</form> ".toCharArray();
    _jsp_string18 = ">\r\n					</font>\r\n					</td>\r\n					<td align=\"center\">".toCharArray();
    _jsp_string9 = "\r\n				<script type=\"text/javascript\">\r\n					alert(\"Sorry, you have no coaching session to book!\");\r\n				</script>\r\n				".toCharArray();
    _jsp_string0 = "\r\n\r\n\r\n\r\n\r\n\r\n\r\n<html>\r\n<head>\r\n\r\n<title>Coach Booking System</title>\r\n\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\"/> \r\n<style type=\"text/css\">\r\n</style>\r\n".toCharArray();
    _jsp_string10 = "\r\n\r\n		<form method=\"post\">\r\n			<p  align=\"center\" >\r\n			<b><font color=\"#000080\" size=\"4\" face=\"Arial\">Welcome to Coaching Booking System</font></b>\r\n			</p>\r\n			<table>\r\n				<tr>\r\n					<td>\r\n					<b><font color=\"#000080\" size=\"2\" face=\"Arial\">Session Name:</font></b>\r\n					</td>\r\n					<td><select size=\"1\" name=\"selSession\" onChange=\"setSessionName(this.form)\">\r\n						<option value=0>Select a Session Name</option>\r\n							".toCharArray();
    _jsp_string19 = "</td>\r\n					<td align=\"center\">".toCharArray();
    _jsp_string7 = "\r\n 				<script type=\"text/javascript\">\r\n 					alert(\"Sorry, you did not book the slot\");\r\n 				</script>\r\n 				".toCharArray();
    _jsp_string8 = "\r\n 				<script type=\"text/javascript\">\r\n 					alert(\"Delete booking successfully\");\r\n 				</script>\r\n 				".toCharArray();
    _jsp_string16 = "\r\n							\r\n					</select></td>\r\n				</tr>\r\n			</table>\r\n			<table>\r\n			<br>\r\n			<th width=\"30\" bgcolor=\"navy\" bordercolor=\"#3399FF\" align=\"center\"><b>\r\n					<font style='color: white'>&nbsp;</font>\r\n			</b></th>\r\n			<th width=\"30\" bgcolor=\"navy\" bordercolor=\"#3399FF\" align=\"center\"><b>\r\n					<font style='color: white'>No</font>\r\n			</b></th>\r\n			<th width=\"200\" bgcolor=\"navy\" bordercolor=\"#3399FF\" align=\"center\"><a href=\"CoachBookingSignUp.jsp?name=1\"><b>\r\n					<font style='color: white'>Date</font>\r\n			</b></th>\r\n			<th width=\"70\" bgcolor=\"navy\" bordercolor=\"#3399FF\" align=\"center\"><a href=\"CoachBookingSignUp.jsp?name=2\"><b>\r\n					<font style='color: white'>Starting</font>\r\n			</b></th>\r\n			<th width=\"70\" bgcolor=\"navy\" bordercolor=\"#3399FF\" align=\"center\"><a href=\"CoachBookingSignUp.jsp?name=5\"><b>\r\n					<font style='color: white'>Ending</font>\r\n			</b></th>\r\n			<th width=\"150\" bgcolor=\"navy\" bordercolor=\"#3399FF\" align=\"center\"><a href=\"CoachBookingSignUp.jsp?name=3\"><b>\r\n					<font style='color: white'>Coach</font>\r\n			</b></th>\r\n			<th width=\"200\" bgcolor=\"navy\" bordercolor=\"#3399FF\" align=\"center\"><b>\r\n					<font style='color: white'>Booked By</font>\r\n			</b></th>\r\n			\r\n			\r\n\r\n			".toCharArray();
    _jsp_string6 = "\r\n 				<script type=\"text/javascript\">\r\n 					alert(\"Book successfully\");\r\n 				</script>\r\n 				".toCharArray();
    _jsp_string5 = "\r\n 				<script type=\"text/javascript\">\r\n 					alert(\"Sorry, this session has been booked already!\");\r\n 				</script>\r\n 				".toCharArray();
    _jsp_string4 = "\r\n 				<script type=\"text/javascript\">\r\n 					alert(\"Sorry, you have already booked all your coaching slots!\");\r\n 				</script>\r\n 				".toCharArray();
    _jsp_string12 = " selected>".toCharArray();
    _jsp_string24 = "\r\n		<td align=\"center\" height=\"5\" valign=\"top\"><font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"Login.jsp\">Home</a>&nbsp;| <a color=\"navy\" face=\"Arial\">&nbsp;<a style=\"TEXT-DECORATION: none; color:navy;\" href=\"mailto:3SixtyAdmin@pcc.com.sg?subject=Regarding:\">Contact \r\n		Us</a><a color=\"navy\" face=\"Arial\" href=\"termofuse.jsp\" target=\"_blank\"><span style=\"color: #000080; text-decoration: none\"> | Terms of Use </span></a>| <span style=\"color: #000080; text-decoration: none\"><a style=\"TEXT-DECORATION: none; color:navy;\" href=\"http://www.pcc.com.sg/\" target=\"_blank\">PCC Website</a></span></font></td></tr><tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\">\r\n   \r\n		<td align=\"center\" height=\"5\" valign=\"top\">\r\n		<font size=\"1\" color=\"navy\" face=\"Arial\">&nbsp;Copyright &copy; ".toCharArray();
    _jsp_string2 = "\r\n	<script type=\"text/javascript\">\r\n	var x = parseInt(window.screen.width) / 2 - 240;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up\r\n	var y = parseInt(window.screen.height) / 2 - 115;  // the number 125 is the exact half of the height of the pop-up and so should be changed according to the size of the pop-up\r\n\r\n		function proceed(form) {\r\n			\r\n		}\r\n		function check(field)\r\n		{\r\n			var isValid = 0;\r\n			var clickedValue = 0;\r\n			if( field == null ) {\r\n				isValid = 2;\r\n			\r\n			} else {\r\n\r\n				if(isNaN(field.length) == false) {\r\n					for (i = 0; i < field.length; i++)\r\n						if(field[i].checked) {\r\n							clickedValue = field[i].value;\r\n							isValid = 1;\r\n						}\r\n				}else {		\r\n					if(field.checked) {\r\n						clickedValue = field.value;\r\n						isValid = 1;\r\n					}\r\n						\r\n				}\r\n			}\r\n			\r\n			if(isValid == 1)\r\n				return clickedValue;\r\n			else if(isValid == 0)\r\n				alert(\"No record selected\");\r\n			else if(isValid == 2)\r\n				alert(\"No record available\");\r\n			\r\n			isValid = 0;\r\n\r\n		}\r\n		function signUp(form,field) {\r\n			var value = check(field);\r\n			if (value) {\r\n					form.action = \"CoachBookingSignUp.jsp?signUp=\" + value;\r\n					form.method = \"post\";\r\n					form.submit();\r\n			}\r\n			\r\n		}\r\n		function unSignUp(form,field) {\r\n			var value = check(field);\r\n			if (value) {\r\n					form.action = \"CoachBookingSignUp.jsp?unSignUp=\" + value;\r\n					form.method = \"post\";\r\n					form.submit();\r\n			}\r\n			\r\n		}\r\n		\r\n		function setSessionName(form) {\r\n			\r\n			if (form.selSession.value == \"0\") {\r\n				alert(\"Please Select Session\");\r\n			}else{\r\n				form.action = \"CoachBookingSignUp.jsp?setSession=1\";\r\n				form.method = \"post\";\r\n				form.submit();\r\n			}\r\n			\r\n		}\r\n		function setSessionDate(form) {\r\n		\r\n				form.action = \"CoachBookingSignUp.jsp?setdate=1\";\r\n				form.method = \"post\";\r\n				form.submit();\r\n			\r\n		}\r\n		\r\n	</script>\r\n</head>\r\n\r\n<body>\r\n	\r\n	<!-- select Session -->\r\n\r\n\r\n	".toCharArray();
    _jsp_string23 = "\r\n\r\n<table border=\"0\" align=\"center\" height=\"26\" id=\"table1\">\r\n\r\n	<tr>\r\n		<font size=\"2\" face=\"Arial\" style=\"font-size: 14pt\" color=\"#000080\">\r\n		".toCharArray();
    _jsp_string13 = "\r\n								".toCharArray();
    _jsp_string20 = "</td>\r\n				</tr>\r\n				".toCharArray();
    _jsp_string15 = "\r\n							\r\n					</select></td>\r\n				</tr>\r\n				<tr>\r\n				<td height=\"20\">\r\n				</td>\r\n				</tr>\r\n				<tr>\r\n					<td>\r\n					<b><font color=\"#000080\" size=\"2\" face=\"Arial\">Session Date:</font></b>\r\n					</td>\r\n					<td ><select size=\"1\"\r\n						name=\"selDate\" onChange=\"setSessionDate(this.form)\">\r\n						<option value=0>ALL</option>\r\n							".toCharArray();
    _jsp_string25 = " Pacific Century Consulting Pte Ltd. All Rights Reserved.\r\n		</font>\r\n		</td>\r\n		\r\n	</tr>\r\n		\r\n</table>\r\n</body>\r\n</html>".toCharArray();
    _jsp_string3 = "\r\n	<font size=\"2\"> <script>\r\n		parent.location.href = \"../iphone/index.jsp\";\r\n	</script> ".toCharArray();
    _jsp_string11 = "\r\n							<option value=".toCharArray();
    _jsp_string14 = "\r\n							\r\n							<option value=".toCharArray();
    _jsp_string22 = "\r\n	<p>&nbsp;</p>\r\n	".toCharArray();
    _jsp_string17 = "\r\n				<tr onMouseOver=\"this.bgColor = '#99ccff'\"\r\n					onMouseOut=\"this.bgColor = '#FFFFCC'\">\r\n					<td style=\"border-width: 1px\"><font size=\"2\"> <input\r\n							type=\"radio\" name=\"selAssignment\" value=".toCharArray();
  }
}
