<%// Author: Dai Yong in June 2013%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="CP_Classes.vo.*"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>Change User Assignment</title>
<%@ page pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html">
<style type="text/css">
<!--
body {
	
}
-->
</style>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session" />
	<jsp:useBean id="Database" class="CP_Classes.Database" scope="session" />
	<jsp:useBean id="User" class="CP_Classes.User" scope="session" />
	<jsp:useBean id="CoachOrganization" class="Coach.CoachOrganization" scope="session" />
	<jsp:useBean id="SessionSetup" class="Coach.SessionSetup" scope="session" />
	<jsp:useBean id="CoachDateGroup" class="Coach.CoachDateGroup" scope="session" />
	<jsp:useBean id="CoachSlotGroup" class="Coach.CoachSlotGroup" scope="session" />
	<script type="text/javascript">
	
	function setUserNamee(form){
		
		if(form.selUser.value==0){
			alert("Please Select a user");
		}
		else{
			form.action = "AddUser.jsp?add="+form.selUser.value;
			form.method = "post";
			form.submit();
		}
		
	}
	
	function saveUserAssignment(form){
		form.action = "AddUser.jsp?save=1";
		form.method = "post";
		form.submit();
	}
	
	</script>
</head>

<body>
	
	<!-- select Session -->


	<%
		String username = (String) session.getAttribute("username");
		Vector userAssignments=new Vector();

		if (!logchk.isUsable(username)) {
	%>
	<font size="2"> <script>
		parent.location.href = "../index.jsp";
	</script> <%
 	} else {
		//Vector users=SessionSetup.getUsersbyOrganizationID();
		Vector users=SessionSetup.getUsersbyOrganizationIDandSurveyID();
		
		
		if(request.getParameter("UserAssignment")!=null){
			SessionSetup.setSelectedUserAssignment(Integer.parseInt(request.getParameter("UserAssignment")));
		}
		
		if(request.getParameter("add")!=null){
			SessionSetup.setSelectUser(Integer.parseInt(request.getParameter("add")));
			System.out.println("Select user: "+Integer.parseInt(request.getParameter("add")));
		}
		if(request.getParameter("save")!=null){
			SessionSetup.updateUser();

			%>
			<script>
			alert("User successfully Changed");
			opener.location.href = 'UserAssignment.jsp';
			window.close();
			</script>
			<% 
		}
 		
 		
 %>
		<div align="center">
		<form method="post">
			<p>
				<b><font color="#000080" size="2" face="Arial">Candidate List:</font></b>
			</p>
			<table align="center">
				<tr>
					<td><select size="1"
						name="selUser" onChange="setUserNamee(this.form)">
						<option value=0>Select Candidate</option>
					
							<%
								for (int i = 0; i < users.size(); i++) {

									voUser user = (voUser) users.elementAt(i);
										int userPK =user.getPKUser();
										String UserName = user.getLoginName();
										String fullName=user.getFamilyName()+" "+user.getGivenName();
										//System.out.println("family Name:"+user.getFamilyName());

										if (SessionSetup.getSelectUser() == userPK) {
							%>
							<option value=<%=userPK%> selected><%=fullName%>
								<%
									} else {
								%>
							
							<option value=<%=userPK%>><%=fullName%>
								<%
									}
										}
								%>							
					</select></td>
				</tr>
			</table>
			<br>
			<!-- select coach -->
			<input class="btn btn-primary" type="button" name="save" value="Save and Close" onclick="saveUserAssignment(this.form)">
		
		</form> 
		</div><%
 	}
 %>
</body>
</html>
</body>
</html>