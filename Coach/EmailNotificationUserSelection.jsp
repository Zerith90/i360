<%@ page import="java.sql.*,
                 java.io.*,
                 javazoom.upload.*,
                 java.lang.String,
                 java.util.Vector,
                 CP_Classes.vo.votblUserRelation,
				 CP_Classes.vo.votblOrganization,
				 CP_Classes.vo.votblSurvey,
				 CP_Classes.AssignTarget_Rater,
				 CP_Classes.vo.voUser,
				 CP_Classes.vo.voCoachSession"%>   
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>                   
<jsp:useBean id="setting" class="CP_Classes.Setting" scope="session"/>
<jsp:useBean id="db" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="email" class="CP_Classes.ReminderCheck" scope="session"/>
<jsp:useBean id="user" class="CP_Classes.User" scope="session"/>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="Org" class="CP_Classes.Organization" scope="session"/>
<jsp:useBean id="user_Jenty" class="CP_Classes.User_Jenty" scope="session"/>
<jsp:useBean id="CoachingEmail" class="Coach.CoachingEmail" scope="session"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
</head>
<script language = "javascript">

	
	function check(field){
		var isValid = 0;
		var clickedValue = 0;
		//check whether any checkbox selected
		if (field == null) {
			isValid = 2;

		} else {

			if (isNaN(field.length) == false) {
				for (i = 0; i < field.length; i++)
					if (field[i].checked) {
						clickedValue = field[i].value;
						isValid = 1;
					}
			} else {
				if (field.checked) {
					clickedValue = field.value;
					isValid = 1;
				}
			}
		}
		if (isValid == 1)
			return clickedValue;
		else if (isValid == 0)
			alert("No record selected");
		else if (isValid == 2)
			alert("No record available");
		isValid = 0;
	}
	function deleteUser(form, field) {
		var value = check(field);
		if (value) {
			
				form.action = "EmailNotificationUserSelection.jsp?deleteUser=" + value;
				form.method = "post";
				form.submit();
		}
	}
	function sendNotification(form) {
				form.action = "EmailNotificationUserSelection.jsp?send=" + 1;
				form.method = "post";
				form.submit();
	}
	function sendReminder(form) {
	
			
				form.action = "EmailNotificationUserSelection.jsp?send=" + 2;
				form.method = "post";
				form.submit();
	}
</script>




		<div align="center">
<body>
	<%
	String username=(String)session.getAttribute("username");
	int errorFlag = 0;
	 if(!logchk.isUsable(username)){
	  %> <font size="2">
	<script>
	parent.location.href = "../index.jsp";
	</script> <%
 	} 
   else 
   {   
   		
   	if(request.getParameter("deleteUser")!= null){
 		int PKuser = Integer.valueOf(request.getParameter("deleteUser"));
 		CoachingEmail.deleteUser(PKuser);
 	}
   	Vector<voUser> selectedUsers=CoachingEmail.getSelectedUsers();
   	
   	if (request.getParameter("action")!=null) {
 		if (request.getParameter("action").equalsIgnoreCase("1")) {
		CoachingEmail.setAction(1);
 			} else {
 		CoachingEmail.setAction(2);
 			}
 		}
 	if (request.getParameter("send")!=null) {
 		if (request.getParameter("send").equalsIgnoreCase("1")) {
 				errorFlag = CoachingEmail.notificationAll(CoachingEmail.getSelectedUsers(),CE_Survey.get_survOrg(),CE_Survey.getSurvey_ID());
			
 			} else {
 				errorFlag = CoachingEmail.reminderAll(CoachingEmail.getSelectedUsers(),CE_Survey.get_survOrg(),CE_Survey.getSurvey_ID());
 			}
 			
 		}
 				if (errorFlag == -1&&CoachingEmail.getAction()==1) {
				%>
 					<script>alert('You need to create a Coaching notification template before sending ')</script>  
				<%			
				}	
				else if (errorFlag == -1 &&CoachingEmail.getAction()==2) {
				%>
 					<script>alert('You need to create a Coaching reminder template before sending ')</script>  
				<%			
				}	
				else if (errorFlag>0) {
				%>
 					<script>alert('Emails Send Successfully')</script>  
				<%			
 					System.out.println("Ending... Send emails to targets");
				}	
 			if (CoachingEmail.getAction()==1) {
 %>				 <br> <b><font color="#000080" size="3" face="Arial">Advanced
					Email Notification</font>
				</b> <%
 			} else {
 %> 		<br> <b><font color="#000080" size="3" face="Arial">Advanced
					Email Reminder</font>
			</b> <%
 			}
 %> <br>
<br>
		<%
		if (errorFlag>0){
			%>
 					<font face="Arial" size="2" color="#000080">Send to <%=errorFlag%> Recipient</font></b></td>
			<%	
		}
 		%>
<form action="EmailNotificationUserSelection.jsp" method="post" >
	<table>
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>&nbsp;</font>
			</b></th>
			<th width="30" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>No.</font>
			</b></th>
			<th width="300" bgcolor="navy" bordercolor="#3399FF" align="center"><b>
					<font style='color: white'>Recipient List:</font>
			</b></th>
			<%
				int DisplayNo = 1;
				
				for (int i = 0; i < selectedUsers.size(); i++) {
					voUser userInTable = new voUser();
					userInTable = (voUser) selectedUsers.elementAt(i);
					int iPKUser = userInTable.getPKUser();
					String iGivenName = userInTable.getGivenName();
					String iFamilyName = userInTable.getFamilyName();
					
			%>
			<tr onMouseOver="this.bgColor = '#99ccff'"
				onMouseOut="this.bgColor = '#FFFFCC'">
				<td style="border-width: 1px"><font size="2"> <input type="radio" name="selUser" value=<%=iPKUser%>></font></td>
				<td align="center"><%=DisplayNo%></td>
				<td align="left"><%=iFamilyName + ", " + iGivenName%></td>
			</tr>
			<%
				DisplayNo++;
				}
			%>
		</table>
		<br>
		<br>
		<input type="button" name="DeleteUser" value="Delete User" onclick="deleteUser(this.form, this.form.selUser)">
		<%
		if(CoachingEmail.getAction()==1){
		%>
		<input type="button" name="DeleteUser" value="Send Notification Emails" onclick="sendNotification(this.form)">
		<%
		}else{
		%>
		</table>
		<input type="button" name="DeleteUser" value="Send Reminder Emails" onclick="sendReminder(this.form)">
		<%
		}
		 %>
</form>
<%	}	%>

</body>
</div>
</html>