<%// Author: Dai Yong in June 2013%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Date"%>
<%@ page import="CP_Classes.vo.*"%>
<%@ page import = "java.text.SimpleDateFormat" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>Coaching Session Name Setup</title>
<%@ page pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html">
<style type="text/css">
<!--
body {
	
}
-->
</style>
</head>

<body>
	<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session" />
	<jsp:useBean id="Database" class="CP_Classes.Database" scope="session" />
	<jsp:useBean id="CoachOrganization" class="Coach.CoachOrganization" scope="session" />
	<jsp:useBean id="SessionSetup" class="Coach.SessionSetup" scope="session" />
	<jsp:useBean id="CoachDateGroup" class="Coach.CoachDateGroup" scope="session" />
	<jsp:useBean id="CoachSlotGroup" class="Coach.CoachSlotGroup" scope="session" />
	<jsp:useBean id="CoachSession" class="Coach.CoachSession" scope="session" />
	<jsp:useBean id="CoachVenue" class="Coach.CoachVenue" scope="session" />
	<link rel="stylesheet" type="text/css" media="all" href="jsDatePick_ltr.min.css" />

	<script type="text/javascript" src="jsDatePick.min.1.3.js"></script>
	<script type="text/javascript">
	window.onload = function(){
		new JsDatePick({
			useMode:2,
			target:"inputField",
			dateFormat:"%d-%M-%Y"
		});
	};
	function confirmAdd(form) {
			if((form.SessionName.value != "")&&(form.MaxSessions.value != "")&&(form.inputField.value != "")) {
				if(confirm("Add Session Name?")) {
					form.action = "SessionName.jsp?add=1";
					form.method = "post";
					form.submit();
					return true;
					
					
				}else
					return false;
			} else {
				if(form.SessionName.value == "") {
					alert("Please enter session name");
					form.SessionName.focus();
				}
				if(form.MaxSessions.value == "") {
					alert("Please enter maximum sessions");
					form.MaxSessions.focus();
				}
				if(form.inputField.value == "") {
					alert("Please enter cut-off date");
					form.MaxSessions.focus();
				}
				return false;
			}
		}
		
	
	</script>

	<!-- select Organization -->


	<%
		String username = (String) session.getAttribute("username");

		if (!logchk.isUsable(username)) {
	%>
	<font size="2"> <script>
		parent.location.href = "../index.jsp";
	</script> <%
 	} else {
 		Vector organizationlist=CoachOrganization.getAllOrganizations();
 		Vector slotGroupList=CoachSlotGroup.getAllSlotGroup();
 		Vector dayGroupList=CoachDateGroup.getAllDateGroup();
  		if (request.getParameter("add") != null) {
  			 Boolean Exist = false;
   		 	String sessionName = request.getParameter("SessionName");
 			String sessionDes = request.getParameter("SessionDescription");
 			int sessionMAX = Integer.parseInt(request.getParameter("MaxSessions"));
 			String dateString = request.getParameter("inputField");
 			Calendar cal = Calendar.getInstance();
 			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
 			cal.setTime(sdf.parse(dateString));// all done
 			Date cutOffDate=cal.getTime();
 			Vector v = SessionSetup.getAllSession();
 			for (int i = 0; i < v.size(); i++) {
 				voCoachSession vo = (voCoachSession) v.elementAt(i);

 				String name = vo.getName();
 				//System.out.println("Existing Schedule Name:"+slotGroupName);
 				if (name.trim().equalsIgnoreCase((sessionName.trim()))) {
 					Exist = true;
 					System.out.println("Same Coaching Session Name");
 				}

 			}

 			if (!Exist) {
 				try {
 					boolean add = SessionSetup.addSession(sessionName, sessionDes, sessionMAX, cutOffDate);
 					int sessionPK = SessionSetup.getSessionPKbyNameAndDes(sessionName,sessionDes);
 					if (add) {
 						//get the added session name PK
 						SessionSetup.setSessionPK(sessionPK);
 						SessionSetup.setSessionName(sessionName);
 						System.out.println("Session PK"
 								+ SessionSetup.getSessionPK());
 %>
 						<script>
 						alert("Coaching Session successfully added");
 						window.location.href = 'CoachAssignment.jsp';
 						</script>
 						<% 
 					}
 					else{
 						
 					}
 				}catch(Exception SE) {
                      System.out.println(SE);
 				}
 			} else {			
 %>
 	<script>
   		alert("Coaching Session Name exists");
 		window.location.href='SessionName.jsp';
 	</script>
 <%			
 			}
 		}
 		
 %>
	<!-- html codes  -->
	<form name="AddSlot" method="post">
		<p>	
				<b><font color="#000080" size="3" face="Arial">New Session Setup</font></b>
				<br>
				<br>
				<br>
			</p>
			<p>
				<b><font color="#000080" size="2" face="Arial">*Session Name:</font></b>
			</p>
			
			<input name="SessionName" type="text"  style='font-size:10.0pt;font-family:Arial' id="SessionName" size="50" maxlength="50">
			
		
			<p>
				<b><font color="#000080" size="2" face="Arial">*Maximum Sessions That One Candidate Can Book:</font></b>
			</p>
			<input name="MaxSessions" type="text"  style='font-size:10.0pt;font-family:Arial' id="SessionDescription" size="50" maxlength="2">
			<p>
				<b><font color="#000080" size="2" face="Arial">*Cut-Off Date:</font></b>
			</p>
			 <input name="inputField" type="text" size="12" id="inputField"  style='font-size:10.0pt;font-family:Arial' id="Date" size="10" maxlength="50"/>
			<p>
				<b><font color="#000080" size="2" face="Arial">Session Description:</font></b>
			</p>
			
			<input name="SessionDescription" type="text"  style='font-size:10.0pt;font-family:Arial' id="SessionDescription" size="50" maxlength="200">
			<br>
			
			<br>
			<p>
				<b><font size="2" face="Arial">* fields are compulsory to fill in</font></b>
			</p>
			<input type="button" name="back" value="back" onClick="javascript: window.location.href = 'SessionManagement.jsp';">
			<input type="button" name="Submit" value="Submit" onClick="confirmAdd(this.form)">
	</form>
<%
 	}
%>
</body>
</html>
</body>
</html>