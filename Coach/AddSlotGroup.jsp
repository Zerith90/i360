<%// Author: Dai Yong in June 2013%>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "CP_Classes.vo.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>Add Time Slot</title>
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
<jsp:useBean id="CoachSlotGroup" class="Coach.CoachSlotGroup"scope="session" />
<jsp:useBean id="LoginStatus" class="Coach.LoginStatus" scope="session" />
<script language = "javascript">
function confirmAdd(form)
{
	if(form.Name.value != "") {
		if(confirm("Add Time Slot?")) {
			form.action = "AddSlotGroup.jsp?add=1";
			form.method = "post";
			form.submit();
			return true;
		}else
			return false;
	} else {
		if(form.Name.value == "") {
			alert("Please Enter Time Slot Name");
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
	
	if(request.getParameter("add") != null) {
		if(request.getParameter("Name") != null)	{
  			String name = request.getParameter("Name");
  			

			// check whether SlotGroup name already exists in database
  			 Boolean Exist = false;
 		    
			//System.out.println("New Schedule Name:"+name);
 			Vector v = CoachSlotGroup.getAllSlotGroup();
 			for(int i = 0; i < v.size(); i++){
 				voCoachSlotGroup vo = (voCoachSlotGroup)v.elementAt(i);
 				
 				String slotGroupName = vo.getSlotGroupName();
 				//System.out.println("Existing Schedule Name:"+slotGroupName);
 				if(name.trim().equalsIgnoreCase((slotGroupName.trim()))){
 					Exist = true;
 					System.out.println("Same Time Slot Name");
 				}

 			}

			
			if(!Exist) {						
				try{					
					boolean add =CoachSlotGroup.addSlotGroup(name);
					
					if(add){
						
						%>
						<script>
						alert("Time Slot successfully added");
						opener.location.href = 'SlotGroup.jsp';
						window.close();
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
  		alert("TIme Slot Name Exists");
		window.location.href='AddSlotGroup.jsp';
	</script>
<%			
			}

	}
	}
%>	
	<p>
		<b><font color="#000080" size="3" face="Arial">Add Time Slot</font></b>
	</p>
<form name="AddSlotGroup" method="post">
  <table border="0" width="480" height="141" font span style='font-size:10.0pt;font-family:Arial'>
    <tr>
      <td width="150" height="33">Time Slot Name:</td>
      <td width="10" height="33">&nbsp;</td>
      <td width="400" height="33">
    	<input name="Name" type="text"  style='font-size:10.0pt;font-family:Arial' id="Name" size="30" maxlength="50">
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