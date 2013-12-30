<%// Author: Dai Yong in June 2013%>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.lang.*" %>
<%@ page import = "CP_Classes.vo.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>EditDateGroup</title>
<%@ page pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html">
<style type="text/css">
<!--
body {
	background-color: #eaebf4;
}
-->
</style></head>

<body style="background-color: #DEE3EF">
<jsp:useBean id="Database" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>    
<jsp:useBean id="CoachDateGroup" class="Coach.CoachDateGroup"scope="session" />
<jsp:useBean id="LoginStatus" class="Coach.LoginStatus" scope="session" />
<script language = "javascript">
function confirmEdit(form)
{
	if(form.Name.value != "") {
		if(confirm("Edit Coaching Period?")) {
			form.action = "EditDateGroup.jsp?edit=1";
			form.method = "post";
			form.submit();
			return true;
		}else
			return false;
	} else {
		if(form.Name.value == "") {
			alert("Please Enter Coaching Period Name");
			form.Name.focus();
		}
		return false;
	}
	return true;
}

function cancelEdit()
{
	window.close();
}
</script>

<%
String username=(String)session.getAttribute("username");
int   PK_CoachDateGroup = LoginStatus.getSelectedDateGroup();
  if (!logchk.isUsable(username)) 
  {
%> <font size="2">
   
<script>
parent.location.href = "../index.jsp";
</script>
<%
	} 
  else 
  { 
	if(request.getParameter("edit") != null) {
		if(request.getParameter("Name") != null)	{
  			String name = request.getParameter("Name");
  			String des="";
  			if(request.getParameter("Description") != null){
  				des=request.getParameter("Description");
  			}
  			

			// check whether DateGroup name already exists in database
		    Boolean Exist = false;
		    
				Vector v = CoachDateGroup.getAllDateGroup();
				for (int i = 0; i < v.size(); i++) {
					voCoachDateGroup vo = (voCoachDateGroup) v.elementAt(i);

					String DateGroupName = vo.getName();

					if (name.trim().equalsIgnoreCase((DateGroupName.trim()))) {
						Exist = true;
					}

				}
				
				if (!Exist) {
					try {
						boolean editsuc = CoachDateGroup.updateDateGroup(PK_CoachDateGroup, name, des);
						
						if (editsuc) {
%>
						<script>
						alert("Coaching period edited successfully");
						opener.location.href = 'DateGroup.jsp';
						window.close();
						</script>
						<% 
					}
					else{
						
						
						
					}
				}catch(SQLException SE) {
                     System.out.println(SE);
				}
			} else {			
%>
	<script>
  		alert("Coaching Period Name Exists");
		window.location.href='EditDateGroup.jsp';
	</script>
<%			
			}

	}
	}
%>	

<form name="EditDateGroup" method="post">
<p>	
	<br>
		<b><font color="#000080" size="3" face="Arial">Edit Coaching Period</font></b>
	<br>
	</p>
  <table border="0" width="480" style='font-size:10.0pt;font-family:Arial'>
    <tr>
      <td width="70" height="33">Name</td>
      <td width="10" height="33">&nbsp;</td>
      <td width="400" height="33">
    	<input name="Name" type="text"  style='font-size:10.0pt;font-family:Arial' id="Name" value="<%=CoachDateGroup.getSelectedDateGroup(PK_CoachDateGroup).getName()%>"size="50" maxlength="50">
	  </td>
    </tr>
    <tr>
      <td width="70" height="33">Description</td>
      <td width="10" height="33">&nbsp;</td>
      <td width="400" height="33">
    	<input name="Description" type="text"  style='font-size:10.0pt;font-family:Arial' id="Description" value="<%=CoachDateGroup.getSelectedDateGroup(PK_CoachDateGroup).getdescription()%>"size="50" maxlength="200">
	  </td>
    </tr>
    <tr>
      <td width="82" height="12"></td>
      <td width="10" height="12"></td>
      <td width="303" height="12"></td>
    </tr>
   
  </table>
  <blockquote>
    <blockquote>
      <p>
		<font face="Arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</font>		<font face="Arial" span style="font-size: 10.0pt; font-family: Arial">		
	        <input type="button" name="Submit" value="Submit" onClick="return confirmEdit(this.form)"></font><font span style='font-family:Arial'>
		</font>
			<font face="Arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </font>
		<font face="Arial" span style="font-size: 10.0pt; font-family: Arial">
			<input name="Cancel" type="button" id="Cancel" value="Cancel" onClick="cancelEdit()">
			</font> </p>
    </blockquote>
  </blockquote>
</form>
<% } %>
</body>
</html>