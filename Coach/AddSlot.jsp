<%// Author: Dai Yong in June 2013%>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "CP_Classes.vo.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>Add Timing</title>
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
<jsp:useBean id="CoachSlot" class="Coach.CoachSlot"scope="session" />
<jsp:useBean id="LoginStatus" class="Coach.LoginStatus" scope="session" />
<script language = "javascript">
function confirmAdd(form)
{
	
	if(form.StartingTime.value != ""&&form.EndingTime.value != "") {
		if(confirm("Add timing?")) {
			form.action = "AddSlot.jsp?add=1";
			form.method = "post";
			form.submit();
			return true;
		}else
			return false;
	} else {
		if(form.StartingTime.value == "") {
			alert("Please enter starting time");
			form.Name.focus();
		}
		if(form.EndingTime.value == "") {
			alert("Please enter ending time");
			form.Name.focus();
		}
		return false;
	}
	return true;
}

function cancelAdd()
{
	window.close();
}
</script>

<%
String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {
%> <font size="2">
   
<script>
	parent.location.href = "../index.jsp";
</script>
<%  } 
  else 
  { 
	//System.out.println("Starting Time: "+request.getParameter("StartingTime"));
	//System.out.println("Ending Time: "+request.getParameter("EndingTime"));
	if(request.getParameter("add") != null) {
		if(request.getParameter("StartingTime") != null&&request.getParameter("EndingTime") != null)	{
  			int StartingTime =Integer.valueOf(request.getParameter("StartingTime")); 
  			int EndingTime =Integer.valueOf(request.getParameter("EndingTime")); 
  		
  			
				try{		
					
					int FKCoachSlotGroup=LoginStatus.getSelectedSlotGroup();
					//System.out.println("FKCoachSlotGroup"+FKCoachSlotGroup);
							
					boolean add =CoachSlot.addSlot(FKCoachSlotGroup, StartingTime, EndingTime);
					
					if(add){
						LoginStatus.setSelectedSlotGroup(FKCoachSlotGroup);
						%>
						<script>
						alert("Coaching Timing added");
						opener.location.href = "SlotGroup.jsp";
						window.close();
						</script>
						<% 
					}
					else{
						
					}
				}catch(Exception SE) {
                     System.out.println(SE);
				}
			 

	}
	}
%>	
	<p>
		<b><font color="#000080" size="3" face="Arial">Add Coaching Timing</font></b>
	</p>
<form name="AddSlot" method="post">
  <table border="0" width="480" style='font-size:10.0pt;font-family:Arial'>
  <tr><font color="#000080" size="2" face="Arial">Notes: Please Use 4 digits time. E.g. 1300</font></tr>
  <tr><font color="#000080" size="2" face="Arial"></font></tr>
    <tr>
      <td width="60" height="23">Starting Time:</td>
      <td width="200" height="23">
    	<input name="StartingTime" type="text"  style='font-size:10.0pt;font-family:Arial' id="StartingTime" size="10" maxlength="4">
	  </td>
	  </tr>
	  <tr>
	   <td width="60" height="23">Ending Time:</td>
	  <td width="200" height="23">
    	<input name="EndingTime" type="text"  style='font-size:10.0pt;font-family:Arial' id="EndingTime" size="10" maxlength="4">
	  </td>
    </tr>
  </table>
  <blockquote>
    <blockquote>
      <p>
		<font face="Arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</font>		<font face="Arial" span style="font-size: 10.0pt; font-family: Arial">		
	        <input class="btn btn-primary" type="button" name="Submit" value="Submit" onClick="return confirmAdd(this.form)"></font><font span style='font-family:Arial'>
		</font>
			<font face="Arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </font>
		<font face="Arial" span style="font-size: 10.0pt; font-family: Arial">
			<input name="Cancel" class="btn btn-primary" type="button" id="Cancel" value="Cancel" onClick="cancelAdd()">
			</font> </p>
    </blockquote>
  </blockquote>
</form>
<% } %>
</body>
</html>